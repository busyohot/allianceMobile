package com.mobile.alliance.activity;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Typeface;
import android.os.Handler;

import android.support.v4.content.ContextCompat;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import android.view.WindowManager;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mobile.alliance.R;
import com.mobile.alliance.api.BackPressCloseHandler;
import com.mobile.alliance.api.CommonHandler;
import com.mobile.alliance.api.DrawUrlImageTaskHandler;
import com.mobile.alliance.api.LogoutHandler;
import com.mobile.alliance.api.PersistentCookieStore;
import com.mobile.alliance.api.RetrofitClient;
import com.mobile.alliance.api.ServiceApi;
import com.mobile.alliance.api.TextValueHandler;
import com.mobile.alliance.entity.noCmpl.NoCmplDelVO;
import com.mobile.alliance.entity.noCmpl.NoCmplOnCreateVO;
import com.mobile.alliance.entity.noCmpl.NoCmplVO;
import com.mobile.alliance.fragment.DeliveryListFragment;


import java.io.File;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;


import java.net.URLDecoder;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPClient;

import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import lombok.SneakyThrows;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoCmplCancelActivity extends AppCompatActivity {
    private static TextValueHandler textValueHandler = new TextValueHandler();
    private String TAG = "NoCmplCancelActivity";
    public static OkHttpClient client;

    //???????????? ?????? 2??? ????????? ?????? ?????????
    private BackPressCloseHandler backPressCloseHandler;
    //????????????
    private LogoutHandler logoutHandler;
    //????????? ???
    private ProgressBar mProgressView;
    private ServiceApi serviceTalk; //????????????
    private ServiceApi service; //????????????

    //api ????????? ????????????
    private PersistentCookieStore persistentCookieStore;

    //??????
    CommonHandler commonHandler = new CommonHandler(this);

     DrawUrlImageTaskHandler drawUrlImageTaskHandler;
    //????????? ????????? ???????????????
    static private String SHARE_NAME = "SHARE_PREF";
    static SharedPreferences sharePref = null;
    static SharedPreferences.Editor editor = null;

    /******** FTP UPLOAD *************/
    Handler handler = new Handler();




    ImageView picAddImg01Cancel;
    ImageView picAddImg02Cancel;
    ImageView picAddImg03Cancel;
    ImageView picAddImg04Cancel;
    ImageView picAddImg05Cancel;
    ImageView picAddImg06Cancel;

    TextView picAddUri01Cancel;
    TextView picAddUri02Cancel;
    TextView picAddUri03Cancel;
    TextView picAddUri04Cancel;
    TextView picAddUri05Cancel;
    TextView picAddUri06Cancel;

    TextView picFileName01Cancel;
    TextView picFileName02Cancel;
    TextView picFileName03Cancel;
    TextView picFileName04Cancel;
    TextView picFileName05Cancel;
    TextView picFileName06Cancel;

    View picAddViewCancel;
    ImageView noCmplBackCancel;

    Button  sendFileFtpCancel;


    RadioGroup noCmplReasonGroupCancel;

    TableLayout noCmplTableCancel;
    Button noCmplBtnCancel;



    RadioButton noCmplRadioCancel[];
    TextView    noCmplReasonCodeCancel[];
    TextView    noCmplReasonEtcCancel[];

    TextView noCmplMemoCancel;

    ImageView noSignatureCancelPad;


    TextView noSignCancelUri,    noSignCancelFileName;

    //public static Integer[]     LOCK = {0,0,0,0,0,0};       //0 ?????? , 1 ????????? ???????????? , 2 ???????????? ?????? ??????
    public Integer       SELECT_NO = 0;
    public ImageView[]   picAddImgCancel;
    public TextView[]    picAddUriCancel;
    public TextView[]   picFileNameCancel;
    String instMobileMIdValue;

    /**
     *  ????????????
     */

    private Boolean isCamera = false;
    private File tempFileCancel;
    String instMobileMIdValueCancel;
    String soNoValueCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_cmpl_cancel);


        noSignatureCancelPad = (ImageView) findViewById(R.id.noSignatureCancelPad);

        noSignCancelUri = (TextView)    findViewById(R.id.noSignCancelUri);
        noSignCancelFileName = (TextView)   findViewById(R.id.noSignCancelFileName) ;


        instMobileMIdValue   = getIntent().getStringExtra("instMobileMId");



        instMobileMIdValueCancel   = getIntent().getStringExtra("instMobileMId");
        soNoValueCancel            = getIntent().getStringExtra("soNo");
        //????????? ????????? ???????????????
        sharePref = getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        editor = sharePref.edit();

        //?????????
        mProgressView = (ProgressBar) findViewById(R.id.noCmplProgressCancel);
        logoutHandler = new LogoutHandler(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //????????? ?????? ????????? ????????? ?????? ????????? ?????? ?????????

        //api ????????? ????????????
        PersistentCookieStore cookieStore = new PersistentCookieStore(this);
        //CookieManager cookieManager  = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        client = new OkHttpClient

                .Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))

                //.cookieJar(new JavaNetCookieJar(CookieManager()))
                .addInterceptor(commonHandler.httpLoggingInterceptor())

                .build();
        service = RetrofitClient.getClient(client).create(ServiceApi.class);    //????????????

        //?????? ???????????? SP ??????????????? ??????????????? ?????????
        mNoCmplReasonCombo(new NoCmplVO(
                sharePref.getString("cmpyCd","")
                ,"COMM"
                ,"X_CMPL_TYPE"
                ,""
                ,"Y"

        ));
        showProgress(true);





        noCmplTableCancel = (TableLayout)findViewById(R.id.noCmplTableCancel);

        noCmplBackCancel = (ImageView)findViewById(R.id.noCmplBackCancel);
        noCmplBackCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                noCmplCancel();
            }
        });



        noCmplReasonGroupCancel = (RadioGroup)findViewById(R.id.noCmplReasonGroup);
        noCmplBtnCancel = (Button) findViewById(R.id.noCmplBtnCancel);
        noCmplBtnCancel.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_gray_detail_btn));
        noCmplBtnCancel.setTextColor(getResources().getColorStateList(R.color.gray_808080));
        /////noCmplBtnCancel.setEnabled(false);




        noCmplMemoCancel = (TextView)findViewById(R.id.noCmplMemoCancel);



        picAddViewCancel = (View)findViewById(R.id.picAddViewCancel);
        picAddUri01Cancel = (TextView)findViewById(R.id.picAddUri01Cancel);
        picAddUri02Cancel = (TextView)findViewById(R.id.picAddUri02Cancel);
        picAddUri03Cancel = (TextView)findViewById(R.id.picAddUri03Cancel);
        picAddUri04Cancel = (TextView)findViewById(R.id.picAddUri04Cancel);
        picAddUri05Cancel = (TextView)findViewById(R.id.picAddUri05Cancel);
        picAddUri06Cancel = (TextView)findViewById(R.id.picAddUri06Cancel);

        picFileName01Cancel = (TextView)findViewById(R.id.picFileName01Cancel);
        picFileName02Cancel = (TextView)findViewById(R.id.picFileName02Cancel);
        picFileName03Cancel = (TextView)findViewById(R.id.picFileName03Cancel);
        picFileName04Cancel = (TextView)findViewById(R.id.picFileName04Cancel);
        picFileName05Cancel = (TextView)findViewById(R.id.picFileName05Cancel);
        picFileName06Cancel = (TextView)findViewById(R.id.picFileName06Cancel);


        picAddImg01Cancel = (ImageView)findViewById(R.id.picAddImg01Cancel);
        picAddImg02Cancel = (ImageView)findViewById(R.id.picAddImg02Cancel);
        picAddImg03Cancel = (ImageView)findViewById(R.id.picAddImg03Cancel);
        picAddImg04Cancel = (ImageView)findViewById(R.id.picAddImg04Cancel);
        picAddImg05Cancel = (ImageView)findViewById(R.id.picAddImg05Cancel);
        picAddImg06Cancel = (ImageView)findViewById(R.id.picAddImg06Cancel);

        picAddImgCancel = new ImageView[]{picAddImg01Cancel, picAddImg02Cancel, picAddImg03Cancel, picAddImg04Cancel, picAddImg05Cancel,
                picAddImg06Cancel};
        picAddUriCancel = new TextView[]{picAddUri01Cancel, picAddUri02Cancel, picAddUri03Cancel, picAddUri04Cancel, picAddUri05Cancel,
                picAddUri06Cancel};

        picFileNameCancel = new TextView[]{
                picFileName01Cancel
               , picFileName02Cancel
               , picFileName03Cancel
               , picFileName04Cancel
               , picFileName05Cancel
               , picFileName06Cancel
        };
        for(int i=0;i<6;i++){
            int finalI = i;
            picAddImgCancel[i].setOnClickListener(new ImageView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ////Log.d("picAddUriCancel",picAddUriCancel[finalI].getText().toString());
                    if(picAddUriCancel[finalI].getText().toString() != null || !picAddUriCancel[finalI].getText().toString().equals(""))
                    {
                        //???????????? ?????? Activity??? ?????????
                        Intent imageActivity = new Intent(NoCmplCancelActivity.this, ImageActivity.class);
                        imageActivity.putExtra("imageUri",picAddUriCancel[finalI].getText());
                        imageActivity.putExtra("imageNo",finalI+"");
                        imageActivity.putExtra("imageType","download");

                        startActivity(imageActivity);  //?????? ??????????????? ??????
                    }
                    else
                    {
                        commonHandler.showAlertDialog("????????? ?????? ??????","????????? ????????? ???????????? ????????????.");
                    }
                }
            });
        }

        //????????? ?????? ????????? ??????
        noCmplBtnCancel.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v){

                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(NoCmplCancelActivity.this);
                builder.setView(dialogView);

                AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();

                Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
                TextView ok_txt = dialogView.findViewById(R.id.confirmText);
                ok_txt.setTextSize(20);
                ok_txt.setTypeface(null, Typeface.BOLD);
                ok_txt.setText("????????? ????????? ???????????????????");
                ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                ok_btn.setOnClickListener(new View.OnClickListener() {
                    @SneakyThrows @Override
                    public void onClick(View v) {


                        alertDialog.dismiss();
                        showProgress(true);
                        NThread nThread = new NThread();
                        nThread.start();

                    }
                });

                Button no_btn = dialogView.findViewById(R.id.confirmBtnNo);
                no_btn.setOnClickListener(new View.OnClickListener() {
                    @SneakyThrows @Override
                    public void onClick(View v) {

                        alertDialog.dismiss();

                    }
                });





            }
        });
    }

    //??????????????? ?????? ??????????????? ???????????? ???????????? ????????? ???????????? ????????????.
    class NThread extends Thread{
        public NThread() {
        }
        @SneakyThrows
        @Override

        public void run() {

            //?????? ????????? ?????? ????????? ?????? ????????? ftp ???????????? ?????????
            for(int i=0;i<picFileNameCancel.length;i++) {  //????????? ?????? 6???
                //??????????????? ?????????
                if(picFileNameCancel[i] != null && !picFileNameCancel[i].getText().toString().equals("")){

                    delete(picFileNameCancel[i].getText().toString());

                }
            }

            //?????? ?????????
            if(noSignCancelFileName != null && !noSignCancelFileName.getText().toString().equals("")){
                //Log.d(TAG,"NThread_delete_sign_fileName(????????? ??????) : " + noSignCancelFileName.getText().toString());
                delete(noSignCancelFileName.getText().toString());

            }


            //????????? ?????? ????????? ?????? SP
            mNoCmplDel(new NoCmplDelVO(
                    instMobileMIdValue

                    , sharePref.getString("tblUserMId","")


            ));

        }

        public void delete(String deleteFile) throws FTPException, IOException, FTPIllegalReplyException{

                FTPClient client = new FTPClient();
            try{
                client.connect(textValueHandler.FTP_HOST, textValueHandler.FTP_PORT);//ftp ????????? ??????, ???????????? ????????? ??????
                client.login(textValueHandler.FTP_USER, textValueHandler.FTP_PASS);//???????????? ?????? ???????????? ???????????? ??????
                client.setType(FTPClient.TYPE_BINARY);//2????????? ??????
                client.setPassive(true);
                String[] arr = deleteFile.split("\\/");
                client.changeDirectory(textValueHandler.FTP_DIR);

                for (int i = 0; i < arr.length - 1; i++) {
                    client.changeDirectory(arr[i]+"/");
                }
                //Log.d(TAG,"FTP FILE DELETE : "+arr[(arr.length - 1)]);
                client.deleteFile(arr[(arr.length - 1)]);
            }
            catch(Exception e)
            {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //commonHandler.showToast("?????? ????????? ?????? ?????? ??????",0,17,17);
                        //Log.d(TAG,"FTP FILE DELETE FAIL, ?????? ????????? ?????? ?????? ??????");
                    }
                });
                e.printStackTrace();
                try {
                    if(client != null){
                        client.disconnect(true);
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }




    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        noCmplCancel();
    }

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    Integer etcNo;  //ETC ????????? ?????? ?????????

    List<NoCmplVO> resultCheck;


    //????????? ?????? ?????? ???????????? ?????? ?????????
    private void mNoCmplReasonCombo(NoCmplVO noCmplVO) {
        service.mNoCmplReasonCombo(noCmplVO).enqueue(new Callback<List<NoCmplVO>>() {
            @SneakyThrows
            @Override
            public void onResponse(Call<List<NoCmplVO>> call, Response<List<NoCmplVO>> response) {

                if(response.isSuccessful()) //???????????? ??????
                {
                    //?????? ?????? ?????? ?????? ??????
                    mNoCmplOnCreate(new NoCmplOnCreateVO(   instMobileMIdValueCancel  ));
                    showProgress(true);

                    List<NoCmplVO> result = response.body();
                    resultCheck = result;
                    if(result.size() > 0)
                    {
                        noCmplRadioCancel       = new RadioButton[result.size()];
                        noCmplReasonCodeCancel     = new TextView[result.size()];
                        noCmplReasonEtcCancel       = new TextView[result.size()];
                        for(int i = 0; i<result.size(); i++)
                        {
                            TableRow tRow = new TableRow(getApplicationContext());     // ????????? ROW ??????

                            noCmplRadioCancel[i] =  new RadioButton(getApplicationContext());
                            noCmplReasonCodeCancel[i] =  new TextView(getApplicationContext());
                            noCmplReasonEtcCancel[i] =  new TextView(getApplicationContext());


                            noCmplRadioCancel[i].setText(result.get(i).getComboNm());
                            noCmplReasonCodeCancel[i].setText(result.get(i).getComboCd());
                            if(result.get(i).getComboCd().equals("ETC")){
                                etcNo = i;
                            }
                            noCmplReasonCodeCancel[i].setVisibility(View.GONE);
                            noCmplReasonEtcCancel[i].setVisibility(View.INVISIBLE);

                            tRow.addView(noCmplRadioCancel[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            tRow.addView(noCmplReasonCodeCancel[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            tRow.addView(noCmplReasonEtcCancel[i], new TableRow.LayoutParams(360, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                            noCmplTableCancel.addView(tRow, new TableLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));

                        }
                    }
                    else
                    {
                        commonHandler.showAlertDialog("????????? ?????? ?????? ??????", "?????? 0???");
                    }
                }
                else{

                    //20220120 ????????? ??????. was?????? [500 internal server error] excpition????????? ?????????????????? ????????? ??????
                    //commonHandler.showAlertDialog("????????? ?????? ?????? ??????","??????????????? ????????????.");
                    commonHandler.showAlertDialog("????????? ?????? ?????? ??????",response.code() +"\n"+ response.message()+"\n\n"+
                            URLDecoder.decode(response.errorBody().string(),"UTF-8"));
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<List<NoCmplVO>> call, Throwable t) {
                commonHandler.showAlertDialog("????????? ?????? ?????? ??????","????????????\n"+t.getMessage());
                showProgress(false);
            }
        });
    }

    NoCmplVO onCreateData;  //?????? ??????????????? ?????????????????? ????????? ??????(?????? ???????????? ????????? ????????? ?????? ?????? ?????? ????????? ????????? ?????? ??????)
    //?????? ????????? ?????? ????????????
    private void mNoCmplOnCreate(NoCmplOnCreateVO noCmplOnCreateVO) {
        service.mNoCmplOnCreate(noCmplOnCreateVO).enqueue(new Callback<NoCmplVO>() {

            @SneakyThrows @Override
            public void onResponse(Call<NoCmplVO> call, Response<NoCmplVO> response) {

                if(response.isSuccessful()) //???????????? ??????
                {
                    NoCmplVO result = response.body();
                    onCreateData = result;

                    if(result.getImg1() != null  && !result.getImg1().equals(""))
                    {
                        try{
                            new DrawUrlImageTaskHandler(
                                    (ImageView) findViewById(R.id.picAddImg01Cancel))
                                    .execute(textValueHandler.HTTP_HOST  + result.getImg1());
                            picAddUri01Cancel
                                    .setText(textValueHandler.HTTP_HOST + result.getImg1());
                            picFileName01Cancel.setText(result.getImg1());
                        }catch(Exception e){
                            e.printStackTrace();
                            //picAddImg01Cancel.setVisibility(View.INVISIBLE);
                            picAddImg01Cancel.setImageBitmap(null);
                        }
                        //Log.d("getImg1" , textValueHandler.HTTP_HOST  + result.getImg1());
                    }
                    else
                    {
                        //picAddImg01Cancel.setVisibility(View.INVISIBLE);
                        picAddImg01Cancel.setImageBitmap(null);
                    }

                    if(result.getImg2() != null  && !result.getImg2().equals(""))
                    {
                        try{
                            new DrawUrlImageTaskHandler(
                                    (ImageView) findViewById(R.id.picAddImg02Cancel))
                                    .execute(textValueHandler.HTTP_HOST + result.getImg2());
                            picAddUri02Cancel
                                    .setText(textValueHandler.HTTP_HOST + result.getImg2());
                            picFileName02Cancel.setText(result.getImg2());
                        }
                        catch(Exception e){
                            e.printStackTrace();
                            //picAddImg02Cancel.setVisibility(View.INVISIBLE);
                            picAddImg02Cancel.setImageBitmap(null);
                        }
                    }
                    else
                    {
                        //picAddImg02Cancel.setVisibility(View.INVISIBLE);
                        picAddImg02Cancel.setImageBitmap(null);
                    }

                    if(result.getImg3() != null  && !result.getImg3().equals(""))
                    {
                        try{
                            new DrawUrlImageTaskHandler(
                                    (ImageView) findViewById(R.id.picAddImg03Cancel))
                                    .execute(textValueHandler.HTTP_HOST + result.getImg3());
                            picAddUri03Cancel
                                    .setText(textValueHandler.HTTP_HOST + result.getImg3());
                            picFileName03Cancel.setText(result.getImg3());
                        }
                        catch(Exception e){
                            e.printStackTrace();
                            //picAddImg03Cancel.setVisibility(View.INVISIBLE);
                            picAddImg03Cancel.setImageBitmap(null);
                        }
                    }
                    else
                        {
                            //picAddImg03Cancel.setVisibility(View.INVISIBLE);
                            picAddImg03Cancel.setImageBitmap(null);
                        }
                    if(result.getImg4() != null  && !result.getImg4().equals(""))
                    {
                        try{
                            new DrawUrlImageTaskHandler(
                                    (ImageView) findViewById(R.id.picAddImg04Cancel))
                                    .execute(textValueHandler.HTTP_HOST + result.getImg4());
                            picAddUri04Cancel
                                    .setText(textValueHandler.HTTP_HOST + result.getImg4());
                            picFileName04Cancel.setText(result.getImg4());
                        }  catch(Exception e){
                                e.printStackTrace();
                                //picAddImg04Cancel.setVisibility(View.INVISIBLE);
                                picAddImg04Cancel.setImageBitmap(null);
                            }
                    }else{
                        //picAddImg04Cancel.setVisibility(View.INVISIBLE);
                        picAddImg04Cancel.setImageBitmap(null);
                    }

                    if(result.getImg5() != null  && !result.getImg5().equals("")){
                        try{
                            new DrawUrlImageTaskHandler(
                                    (ImageView) findViewById(R.id.picAddImg05Cancel))
                                    .execute(textValueHandler.HTTP_HOST + result.getImg5());
                            picAddUri05Cancel
                                    .setText(textValueHandler.HTTP_HOST + result.getImg5());
                            picFileName05Cancel.setText(result.getImg5());
                        }
                        catch(Exception e){
                            e.printStackTrace();
                            //picAddImg05Cancel.setVisibility(View.INVISIBLE);
                            picAddImg05Cancel.setImageBitmap(null);
                        }
                    }else{
                        //picAddImg05Cancel.setVisibility(View.INVISIBLE);
                        picAddImg05Cancel.setImageBitmap(null);
                    }


                    if(result.getImg6() != null  && !result.getImg6().equals("")){
                        try{
                            new DrawUrlImageTaskHandler(
                                    (ImageView) findViewById(R.id.picAddImg06Cancel))
                                    .execute(textValueHandler.HTTP_HOST + result.getImg6());
                            picAddUri06Cancel
                                    .setText(textValueHandler.HTTP_HOST + result.getImg6());
                            picFileName06Cancel.setText(result.getImg6());
                        }  catch(Exception e){
                            e.printStackTrace();
                            //picAddImg06Cancel.setVisibility(View.INVISIBLE);
                            picAddImg06Cancel.setImageBitmap(null);
                        }
                    }else{
                        //picAddImg06Cancel.setVisibility(View.INVISIBLE);
                        picAddImg06Cancel.setImageBitmap(null);
                    }
                    if(result.getSignImg() != null  && !result.getSignImg().equals("")){
                        try{
                            new DrawUrlImageTaskHandler(
                                    (ImageView) findViewById(R.id.noSignatureCancelPad))
                                    .execute(textValueHandler.HTTP_HOST + result.getSignImg());
                            noSignCancelUri.setText(textValueHandler.HTTP_HOST + result.getSignImg());
                            noSignCancelFileName.setText(result.getSignImg());
                        }  catch(Exception e){
                            e.printStackTrace();
                            noSignatureCancelPad.setVisibility(View.INVISIBLE);
                            Log.d(TAG,"getSignImg catch : " + textValueHandler.HTTP_HOST + result.getSignImg());
                        }
                    }else{
                        noSignatureCancelPad.setVisibility(View.INVISIBLE);
                        Log.d(TAG,"getSignImg else : " + textValueHandler.HTTP_HOST + result.getSignImg());
                    }

                    //????????? ??????
                    for(int q=0;q<resultCheck.size();q++)
                    {
                        noCmplRadioCancel[q].setEnabled(false); //??????????????? ???????????? ???
                       //??????????????? ?????????  ???????????? ????????? ????????? ?????? ?????? ??????

                        if(noCmplReasonCodeCancel[q].getText().toString().equals(result.getMobileXCmplType())){
                            noCmplRadioCancel[q].setChecked(true);  //??????????????? ???????????????
                            noCmplRadioCancel[q].setEnabled(true); //??????????????? ????????? ???
                            //???????????? ????????? ETC ?????? ????????? ???????????????
                            if(result.getMobileXCmplType().equals("ETC")){
                                noCmplReasonEtcCancel[q].setText(result.getMobileXCmplTxt());   //????????? ????????? ????????? ??? ??????
                                noCmplReasonEtcCancel[q].setVisibility(View.VISIBLE);           //????????? ??? ????????????
                                //noCmplReasonEtcCancel[q].setHeight(60);
                                noCmplReasonEtcCancel[q].setTextSize(16);   //????????? ????????? ????????? ??? ??????
                                noCmplReasonEtcCancel[q].setPadding(5,5,5,5);
                                noCmplReasonEtcCancel[q].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_gray_detail_white_btn));
                            }
                        }
                    }
                    //??????
                    noCmplMemoCancel.setText(result.getMemo());
                }
                else{
                    //20220120 ????????? ??????
                    //commonHandler.showToast("????????? onCreate ????????? ?????? ??????\n??????????????? ??????",0,17,17);
                    //20220120 ????????? ??????. was?????? [500 internal server error] excpition????????? ?????????????????? ????????? ??????
                    //commonHandler.showAlertDialog("????????? ?????? ?????? ??????","??????????????? ??????");
                    commonHandler.showAlertDialog("????????? ?????? ?????? ??????",response.code() +"\n"+ response.message()+"\n\n"+
                            URLDecoder.decode(response.errorBody().string(),"UTF-8"));
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<NoCmplVO> call, Throwable t) {
                //20220120 ????????? ??????
                //commonHandler.showToast("????????? onCreate ????????? ?????? ??????\n????????????\n"+t.getMessage(),0,17,17);
                commonHandler.showAlertDialog("????????? onCreate ????????? ?????? ??????","????????????\n"+t.getMessage());
                showProgress(false);
            }
        });
    }



    //????????? ?????? ?????? ???????????? (DEL)
    private void mNoCmplDel(NoCmplDelVO noCmplDelVO) {

        service.mNoCmplDel(noCmplDelVO).enqueue(new Callback<NoCmplVO>() {

            @SneakyThrows @Override
            public void onResponse(Call<NoCmplVO> call, Response<NoCmplVO> response) {

                if(response.isSuccessful()) //???????????? ??????
                {
                    NoCmplVO result = response.body();
                    if(result.getRtnYn().equals("Y"))
                    {

                        View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                        AlertDialog.Builder builder = new AlertDialog.Builder(NoCmplCancelActivity.this);
                        builder.setView(dialogView);

                        AlertDialog alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();

                        Button ok_btn = dialogView.findViewById(R.id.successBtn);
                        TextView ok_txt = dialogView.findViewById(R.id.successText);
                        ok_txt.setText("????????? ?????? ??????");
                        ok_btn.setOnClickListener(new View.OnClickListener() {

                            @SneakyThrows @Override
                            public void onClick(View v) {

                                alertDialog.dismiss();

                                //???????????? ????????????
                                DeliveryListFragment deliveryListFragment = (DeliveryListFragment)DeliveryListFragment._DeliveryListFragment;
                                deliveryListFragment.changeDate();

                                //?????????????????? ?????? ?????? ??????

                                DeliveryListDetail deliveryListDetail = (DeliveryListDetail)DeliveryListDetail._DeliveryListDetail;
                                //deliveryListDetail.finish();
                                deliveryListDetail.deliveryDetailSrch();

                                //???????????????(????????????) Activity ??????
                                finish();
                            }
                        });
                    }
                    else
                    {
                        commonHandler.showAlertDialog("????????? ?????? ?????? ??????","" + (result.getRtnMsg() == null ? "?????? ?????? ?????? " : result.getRtnMsg()));
                    }
                }
                else{
                    //20220120 ????????? ??????. was?????? [500 internal server error] excpition????????? ?????????????????? ????????? ??????
                    //commonHandler.showAlertDialog("????????? ?????? ?????? ??????","??????????????? ??????");
                    commonHandler.showAlertDialog("????????? ?????? ?????? ??????",response.code() +"\n"+ response.message()+"\n\n"+
                            URLDecoder.decode(response.errorBody().string(),"UTF-8"));
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<NoCmplVO> call, Throwable t) {
                commonHandler.showAlertDialog("????????? ?????? ?????? ??????","????????????\n"+t.getMessage());
                showProgress(false);
            }
        });
    }



    //????????? ????????? ????????? ???????????? ??????
    /*
    private void setUseableEditText(EditText et, boolean useable) {
        et.setClickable(useable);
        et.setEnabled(useable);
        et.setFocusable(useable);
        et.setFocusableInTouchMode(useable);
    }
    */
    private void noCmplCancel(){



        View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(NoCmplCancelActivity.this);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
        TextView ok_txt = dialogView.findViewById(R.id.confirmText);
        ok_txt.setTextSize(20);
        ok_txt.setTypeface(null, Typeface.BOLD);
        ok_txt.setText("????????? ?????? ????????? ??????????????????????");
        ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finish();
            }
        });

        Button no_btn = dialogView.findViewById(R.id.confirmBtnNo);
        no_btn.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
}