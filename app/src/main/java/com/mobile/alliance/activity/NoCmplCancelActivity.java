package com.mobile.alliance.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

    //뒤로가기 버튼 2번 누르면 취소 하는것
    private BackPressCloseHandler backPressCloseHandler;
    //로그아웃
    private LogoutHandler logoutHandler;
    //진행바 뷰
    private ProgressBar mProgressView;
    private ServiceApi serviceTalk; //알림톡용
    private ServiceApi service; //시스템용

    //api 이용시 쿠키전달
    private PersistentCookieStore persistentCookieStore;

    //공통
    CommonHandler commonHandler = new CommonHandler(this);

     DrawUrlImageTaskHandler drawUrlImageTaskHandler;
    //내부에 데이터 저장하는것
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

    //public static Integer[]     LOCK = {0,0,0,0,0,0};       //0 해제 , 1 이미지 선택완료 , 2 파일서버 전송 완료
    public Integer       SELECT_NO = 0;
    public ImageView[]   picAddImgCancel;
    public TextView[]    picAddUriCancel;
    public TextView[]   picFileNameCancel;
    String instMobileMIdValue;

    /**
     *  변수설정
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
        //내부에 데이터 저장하는것
        sharePref = getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        editor = sharePref.edit();

        //진행바
        mProgressView = (ProgressBar) findViewById(R.id.noCmplProgressCancel);
        logoutHandler = new LogoutHandler(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //키보드 뜰때 입력창 가리지 않고 화면을 위로 올리기

        //api 이용시 쿠키전달
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
        service = RetrofitClient.getClient(client).create(ServiceApi.class);    //시스템용

        //화면 뜨자마자 SP 수행시켜서 미마감사유 불러옴
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
                        //확대화면 보는 Activity를 오픈함
                        Intent imageActivity = new Intent(NoCmplCancelActivity.this, ImageActivity.class);
                        imageActivity.putExtra("imageUri",picAddUriCancel[finalI].getText());
                        imageActivity.putExtra("imageNo",finalI+"");
                        imageActivity.putExtra("imageType","download");

                        startActivity(imageActivity);  //다음 액티비티를 열고
                    }
                    else
                    {
                        commonHandler.showAlertDialog("이미지 열기 실패","이미지 경로가 존재하지 않습니다.");
                    }
                }
            });
        }

        //미마감 취소 버튼을 누름
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
                ok_txt.setText("미마감 취소를 하시겠습니까?");
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

    //안드로이드 최근 버전에서는 네크워크 통신시에 반드시 스레드를 요구한다.
    class NThread extends Thread{
        public NThread() {
        }
        @SneakyThrows
        @Override

        public void run() {

            //반복 돌면서 파일 경로가 있는 사진은 ftp 서버에서 지운다
            for(int i=0;i<picFileNameCancel.length;i++) {  //사진은 최대 6장
                //파일경로가 있다면
                if(picFileNameCancel[i] != null && !picFileNameCancel[i].getText().toString().equals("")){

                    delete(picFileNameCancel[i].getText().toString());

                }
            }

            //싸인 지우기
            if(noSignCancelFileName != null && !noSignCancelFileName.getText().toString().equals("")){
                //Log.d(TAG,"NThread_delete_sign_fileName(삭제할 파일) : " + noSignCancelFileName.getText().toString());
                delete(noSignCancelFileName.getText().toString());

            }


            //미마감 처리 데이터 저장 SP
            mNoCmplDel(new NoCmplDelVO(
                    instMobileMIdValue

                    , sharePref.getString("tblUserMId","")


            ));

        }

        public void delete(String deleteFile) throws FTPException, IOException, FTPIllegalReplyException{

                FTPClient client = new FTPClient();
            try{
                client.connect(textValueHandler.FTP_HOST, textValueHandler.FTP_PORT);//ftp 서버와 연결, 호스트와 포트를 기입
                client.login(textValueHandler.FTP_USER, textValueHandler.FTP_PASS);//로그인을 위해 아이디와 패스워드 기입
                client.setType(FTPClient.TYPE_BINARY);//2진으로 변경
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
                        //commonHandler.showToast("서버 이미지 파일 삭제 실패",0,17,17);
                        //Log.d(TAG,"FTP FILE DELETE FAIL, 서버 이미지 파일 삭제 실패");
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
    Integer etcNo;  //ETC 인것의 순번 저장용

    List<NoCmplVO> resultCheck;


    //미마감 사유 목록 불러와서 표시 하는것
    private void mNoCmplReasonCombo(NoCmplVO noCmplVO) {


        service.mNoCmplReasonCombo(noCmplVO).enqueue(new Callback<List<NoCmplVO>>() {


            @Override
            public void onResponse(Call<List<NoCmplVO>> call, Response<List<NoCmplVO>> response) {

                if(response.isSuccessful()) //응답값이 없다
                {
                    //화면 뜨자 마자 내용 부름
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
                            TableRow tRow = new TableRow(getApplicationContext());     // 테이블 ROW 생성

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
                        commonHandler.showAlertDialog("미마감 사유 조회 실패", "조회 0건");
                    }
                }
                else{
                    commonHandler.showAlertDialog("미마감 사유 조회 실패","응답결과가 없습니다.");
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<List<NoCmplVO>> call, Throwable t) {

                commonHandler.showAlertDialog("미마감 사유 조회 실패","접속실패\n"+t.getMessage());
                showProgress(false);
            }
        });
    }




///////////////05050505
    NoCmplVO onCreateData;  //화면 열리자마자 불러온데이터 여기에 넣기(여기 넣었다가 나중에 미마감 처리 완료 버튼 누를때 알림톡 발송 보냄)
    //화면 열리자 마자 불러오기
    private void mNoCmplOnCreate(NoCmplOnCreateVO noCmplOnCreateVO) {
        service.mNoCmplOnCreate(noCmplOnCreateVO).enqueue(new Callback<NoCmplVO>() {

            @Override
            public void onResponse(Call<NoCmplVO> call, Response<NoCmplVO> response) {

                if(response.isSuccessful()) //응답값이 있다
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
                    }else{noSignatureCancelPad.setVisibility(View.INVISIBLE);
                        Log.d(TAG,"getSignImg else : " + textValueHandler.HTTP_HOST + result.getSignImg());}


                    //미마감 사유
                    for(int q=0;q<resultCheck.size();q++)
                    {
                        noCmplRadioCancel[q].setEnabled(false); //체크버튼을 비활성화 함
                       //미마감사유 코드가  불러들인 코드와 같으면 그걸 체크 한다

                        if(noCmplReasonCodeCancel[q].getText().toString().equals(result.getMobileXCmplType())){
                            noCmplRadioCancel[q].setChecked(true);  //체크버튼을 체크처리함
                            noCmplRadioCancel[q].setEnabled(true); //체크버튼을 활성화 함
                            //불러들인 코드가 ETC 라면 글자도 보여줘야함
                            if(result.getMobileXCmplType().equals("ETC")){
                                noCmplReasonEtcCancel[q].setText(result.getMobileXCmplTxt());   //기타일 경우에 입력한 값 넣기
                                noCmplReasonEtcCancel[q].setVisibility(View.VISIBLE);           //입력한 값 보여주기
                                //noCmplReasonEtcCancel[q].setHeight(60);
                                noCmplReasonEtcCancel[q].setTextSize(16);   //기타일 경우에 입력한 값 넣기
                                noCmplReasonEtcCancel[q].setPadding(5,5,5,5);
                                noCmplReasonEtcCancel[q].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_gray_detail_white_btn));
                            }
                        }
                    }
                    //메모
                    noCmplMemoCancel.setText(result.getMemo());

                }
                else{
                    commonHandler.showToast("미마감 onCreate 데이터 조회 실패\n응답결과가 없음",0,17,17);
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<NoCmplVO> call, Throwable t) {
                commonHandler.showToast("미마감 onCreate 데이터 조회 실패\n접속실패\n+t.getMessage()",0,17,17);
                showProgress(false);
            }
        });
    }



    //미마감 취소 버튼 눌렀을떄 (DEL)
    private void mNoCmplDel(NoCmplDelVO noCmplDelVO) {

        service.mNoCmplDel(noCmplDelVO).enqueue(new Callback<NoCmplVO>() {

            @Override
            public void onResponse(Call<NoCmplVO> call, Response<NoCmplVO> response) {

                if(response.isSuccessful()) //응답값이 있다
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
                        ok_txt.setText("미마감 취소 완료");
                        ok_btn.setOnClickListener(new View.OnClickListener() {

                            @SneakyThrows @Override
                            public void onClick(View v) {

                                alertDialog.dismiss();

                                //배송목록 갱신하기
                                DeliveryListFragment deliveryListFragment = (DeliveryListFragment)DeliveryListFragment._DeliveryListFragment;
                                deliveryListFragment.changeDate();

                                //상세정보보기 다시 정보 읽기

                                DeliveryListDetail deliveryListDetail = (DeliveryListDetail)DeliveryListDetail._DeliveryListDetail;
                                //deliveryListDetail.finish();
                                deliveryListDetail.deliveryDetailSrch();

                                //알림톡발송(현재화면) Activity 닫기
                                finish();
                            }
                        });
                    }
                    else
                    {
                        commonHandler.showAlertDialog("미마감 취소 처리 실패","" + (result.getRtnMsg() == null ? "취소 처리 실패 " : result.getRtnMsg()));
                    }
                }
                else{
                    commonHandler.showAlertDialog("미마감 처리 취소 실패","응답결과가 없음");
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<NoCmplVO> call, Throwable t) {
                commonHandler.showAlertDialog("미마감 처리 취소 실패","접속실패\n"+t.getMessage());
                showProgress(false);
            }
        });
    }



    //텍스트 입력란 활성화 비활성화 넣기
    private void setUseableEditText(EditText et, boolean useable) {
        et.setClickable(useable);
        et.setEnabled(useable);
        et.setFocusable(useable);
        et.setFocusableInTouchMode(useable);
    }

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
        ok_txt.setText("미마감 취소 화면을 나가시겠습니까?");
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