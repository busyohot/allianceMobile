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
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
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
import com.mobile.alliance.api.PersistentCookieStore;
import com.mobile.alliance.api.RetrofitClient;
import com.mobile.alliance.api.ServiceApi;
import com.mobile.alliance.api.TextValueHandler;
import com.mobile.alliance.entity.noCmpl.NoCmplDelVO;
import com.mobile.alliance.entity.yesCmpl.YesCmplDelVO;
import com.mobile.alliance.entity.yesCmpl.YesCmplOnCreateVO;
import com.mobile.alliance.entity.yesCmpl.YesCmplVO;
import com.mobile.alliance.fragment.DeliveryListFragment;

import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

import java.util.List;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;

import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import lombok.SneakyThrows;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YesCmplCancelActivity extends AppCompatActivity {
    private static TextValueHandler textValueHandler = new TextValueHandler();
    private String TAG = "YesCmplCancelActivity";
    public static OkHttpClient client;

    //뒤로가기 버튼 2번 누르면 취소 하는것
    private BackPressCloseHandler backPressCloseHandler;

    //진행바 뷰
    private ProgressBar mProgressView;
    private ServiceApi serviceTalk; //알림톡용
    private ServiceApi service; //시스템용

    //api 이용시 쿠키전달
    private PersistentCookieStore persistentCookieStore;

    //공통
    CommonHandler commonHandler = new CommonHandler(this);

    //내부에 데이터 저장하는것
    static private String SHARE_NAME = "SHARE_PREF";
    static SharedPreferences sharePref = null;
    static SharedPreferences.Editor editor = null;

    /******** FTP UPLOAD *************/
    Handler handler = new Handler();



    ImageView yesCancelPicAddImg01;
    ImageView yesCancelPicAddImg02;
    ImageView yesCancelPicAddImg03;
    ImageView yesCancelPicAddImg04;
    ImageView yesCancelPicAddImg05;
    ImageView yesCancelPicAddImg06;

    TextView yesCancelPicAddUri01;
    TextView yesCancelPicAddUri02;
    TextView yesCancelPicAddUri03;
    TextView yesCancelPicAddUri04;
    TextView yesCancelPicAddUri05;
    TextView yesCancelPicAddUri06;

    TextView yesCancelPicFileName01;
    TextView yesCancelPicFileName02;
    TextView yesCancelPicFileName03;
    TextView yesCancelPicFileName04;
    TextView yesCancelPicFileName05;
    TextView yesCancelPicFileName06;

    View yesCancelPicAddView;
    ImageView yesCancelCmplBack;



    RadioGroup yesCancelCmplReasonGroup;

    TableLayout yesCancelCmplTable;
    Button yesCancelCmplBtn;



    //뷰 배열

    //착불비
    RadioButton yesCmplCancelRcptCostRadio[];
    TextView    yesCmplCancelRcptCostCode[];
    TextView    yesCmplCancelRcptCostEtc[];


    //양중비
    RadioButton yesCmplCancelYangJungRadio[];
    TextView    yesCmplCancelYangJungCode[];
    TextView    yesCmplCancelYangJungEtc[] ;

    //내림서비스
    RadioButton yesCmplCancelService02Radio[];
    TextView    yesCmplCancelService02Code[];
    TextView    yesCmplCancelService02Etc[] ;


    //벽고정->기타
    RadioButton yesCmplCancelService03Radio[];
    TextView    yesCmplCancelService03Code[];
    TextView    yesCmplCancelService03Etc[] ;






    TextView yesCancelCmplMemo;

    ImageView signatureCancelPad;


    TextView signCancelUri,    signCancelFileName;

    public static ImageView[]   yesCancelPicAddImg;
    public static TextView[]    yesCancelPicAddUri;
    public static TextView[]    yesCancelPicFileName;

    /**
     *  변수설정
     */

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;
    private Boolean isCamera = false;
    private File tempFile;
    String instMobileMIdValue;
    String soNoValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yes_cmpl_cancel);





        signatureCancelPad = (ImageView) findViewById(R.id.signatureCancelPad);

        signCancelUri = (TextView)    findViewById(R.id.signCancelUri);
        signCancelFileName = (TextView)   findViewById(R.id.signCancelFileName) ;



        instMobileMIdValue   = getIntent().getStringExtra("instMobileMId");
        soNoValue            = getIntent().getStringExtra("soNo");
        //내부에 데이터 저장하는것
        sharePref = getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        editor = sharePref.edit();

        //진행바
        mProgressView = (ProgressBar) findViewById(R.id.yesCancelCmplProgress);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //키보드 뜰때 입력창 가리지 않고 화면을 위로 올리기

        //api 이용시 쿠키전달
        PersistentCookieStore cookieStore = new PersistentCookieStore(this);
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        client = new OkHttpClient

                .Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .addInterceptor(commonHandler.httpLoggingInterceptor())
                .build();
        service = RetrofitClient.getClient(client).create(ServiceApi.class);    //시스템용

        //화면 뜨자마자 SP 수행시켜서 배송취소사유 불러옴
        mYesCmplReasonCombo(new YesCmplVO(
                sharePref.getString("cmpyCd","")
                ,"COMM"
                ,"DLVY_COST_CLCT"
                ,""
                ,"Y"

        ));
        showProgress(true);




        yesCancelCmplTable = (TableLayout)findViewById(R.id.yesCancelCmplTable);

        yesCancelCmplBack = (ImageView)findViewById(R.id.yesCancelCmplBack);

        yesCancelCmplBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                yesCmplCancelCancel();
            }
        });


        yesCancelCmplReasonGroup = (RadioGroup)findViewById(R.id.yesCancelCmplReasonGroup);


        //배송취소 버튼
        yesCancelCmplBtn = (Button) findViewById(R.id.yesCancelCmplBtn);


        yesCancelCmplMemo = (TextView)findViewById(R.id.yesCancelCmplMemo);



        yesCancelPicAddView = (View)findViewById(R.id.yesCancelPicAddView);
        yesCancelPicAddUri01 = (TextView)findViewById(R.id.yesCancelPicAddUri01);
        yesCancelPicAddUri02 = (TextView)findViewById(R.id.yesCancelPicAddUri02);
        yesCancelPicAddUri03 = (TextView)findViewById(R.id.yesCancelPicAddUri03);
        yesCancelPicAddUri04 = (TextView)findViewById(R.id.yesCancelPicAddUri04);
        yesCancelPicAddUri05 = (TextView)findViewById(R.id.yesCancelPicAddUri05);
        yesCancelPicAddUri06 = (TextView)findViewById(R.id.yesCancelPicAddUri06);

        yesCancelPicFileName01 = (TextView)findViewById(R.id.yesCancelPicFileName01);
        yesCancelPicFileName02 = (TextView)findViewById(R.id.yesCancelPicFileName02);
        yesCancelPicFileName03 = (TextView)findViewById(R.id.yesCancelPicFileName03);
        yesCancelPicFileName04 = (TextView)findViewById(R.id.yesCancelPicFileName04);
        yesCancelPicFileName05 = (TextView)findViewById(R.id.yesCancelPicFileName05);
        yesCancelPicFileName06 = (TextView)findViewById(R.id.yesCancelPicFileName06);


        yesCancelPicAddImg01 = (ImageView)findViewById(R.id.yesCancelPicAddImg01);
        yesCancelPicAddImg02 = (ImageView)findViewById(R.id.yesCancelPicAddImg02);
        yesCancelPicAddImg03 = (ImageView)findViewById(R.id.yesCancelPicAddImg03);
        yesCancelPicAddImg04 = (ImageView)findViewById(R.id.yesCancelPicAddImg04);
        yesCancelPicAddImg05 = (ImageView)findViewById(R.id.yesCancelPicAddImg05);
        yesCancelPicAddImg06 = (ImageView)findViewById(R.id.yesCancelPicAddImg06);

        yesCancelPicAddImg = new ImageView[]{yesCancelPicAddImg01, yesCancelPicAddImg02, yesCancelPicAddImg03, yesCancelPicAddImg04, yesCancelPicAddImg05,
                yesCancelPicAddImg06};
        yesCancelPicAddUri = new TextView[]{yesCancelPicAddUri01, yesCancelPicAddUri02, yesCancelPicAddUri03, yesCancelPicAddUri04, yesCancelPicAddUri05,
                yesCancelPicAddUri06};
        yesCancelPicFileName = new TextView[]{yesCancelPicFileName01, yesCancelPicFileName02, yesCancelPicFileName03, yesCancelPicFileName04, yesCancelPicFileName05,
                yesCancelPicFileName06};
        for(int i=0;i<6;i++){

            int finalI = i;
            yesCancelPicAddImg[i].setOnClickListener(new ImageView.OnClickListener() {
                @Override
                public void onClick(View v) {


                        if(yesCancelPicAddUri[finalI].getText() != null && !yesCancelPicAddUri[finalI].getText().equals(""))
                        {

                            Intent imageActivity = new Intent(YesCmplCancelActivity.this, ImageActivity.class);
                            imageActivity.putExtra("imageUri",yesCancelPicAddUri[finalI].getText());
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

        //배송취소 처리 버튼을 눌렀을때
        yesCancelCmplBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag") @Override
            public void onClick(View v){


                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(YesCmplCancelActivity.this);
                builder.setView(dialogView);

                AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();

                Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
                TextView ok_txt = dialogView.findViewById(R.id.confirmText);
                ok_txt.setTextSize(20);
                ok_txt.setTypeface(null, Typeface.BOLD);
                ok_txt.setText("배송취소를 하시겠습니까?");
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






    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        yesCmplCancelCancel();
    }

    int sendFileCnt = 0;    //전송할 파일 갯수
    int sendProcCnt = 0;    //전송 수행한 파일갯수
    int sendCmplCnt = 0;    //전송 완료한 파일갯수

    //안드로이드 최근 버전에서는 네크워크 통신시에 반드시 스레드를 요구한다.
    class NThread extends Thread{
        public NThread() {
        }
        @SneakyThrows @Override

        public void run() {
            //반복 돌면서 파일 경로가 있는 사진은 ftp 서버에서 지운다
            for(int i=0;i<yesCancelPicFileName.length;i++) {  //사진은 최대 6장
                //파일경로가 있다면
                if(yesCancelPicFileName[i] != null && !yesCancelPicFileName[i].getText().toString().equals("")){
                    //Log.d(TAG,"NThread_delete_image_fileName(삭제할 파일) : " + yesCancelPicFileName[i].getText().toString());
                    delete(yesCancelPicFileName[i].getText().toString());

                }
            }
            //싸인 지우기
            if(signCancelFileName != null && !signCancelFileName.getText().toString().equals("")){
                //Log.d(TAG,"NThread_delete_sign_fileName(삭제할 파일) : " + signCancelFileName.getText().toString());
                delete(signCancelFileName.getText().toString());

            }
            //미마감 처리 데이터 저장 SP
            mYesCmplDel(new YesCmplDelVO(
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

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    Integer noNum;  //착불비 N 인것의 순번 저장용
    Integer yangJungNo;  //양중비 N 인것의 순번 저장용
    Integer service02;  //내림서비스 N 인것의 순번 저장용
    Integer service03;  //벽고정->기타 N 인것의 순번 저장용



    private void mYesCmplReasonCombo(YesCmplVO yesCmplCancelVO) {
        service.mYesCmplReasonCombo(yesCmplCancelVO).enqueue(new Callback<List<YesCmplVO>>() {

            @Override
            public void onResponse(Call<List<YesCmplVO>> call, Response<List<YesCmplVO>> response) {

                if(response.isSuccessful()) //응답값이 있다
                {


                    //화면 뜨자 마자 내용 부름
                    mYesCmplOnCreate(new YesCmplOnCreateVO(   instMobileMIdValue  ));
                    showProgress(true);




                    List<YesCmplVO> result = response.body();

                    if(result.size()+1 > 1)
                    {

                        //착불비
                        yesCmplCancelRcptCostRadio       = new RadioButton[result.size()+1];        //사유선택
                        yesCmplCancelRcptCostCode     = new TextView[result.size()+1];        //사유 코드


                        yesCmplCancelRcptCostEtc       = new TextView[result.size()+1];       //내용입력

                        TextView yesCmplCancelRcptCost = new TextView(getApplicationContext());
                        yesCmplCancelRcptCost.setText("착불비");
                        //TableRow tRowRcptCost = new TableRow(getApplicationContext());     //착불비 제목 보이는 부분

                        yesCancelCmplTable.addView(yesCmplCancelRcptCost, new TableLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));


                        //착불비의 사유 2개 고객입금,현장수금 과 없음 을 불러와서 배치함
                        for(int i = 0; i<result.size()+1; i++) {


                            TableRow tRow = new TableRow(getApplicationContext());     // 테이블 ROW 생성

                            yesCmplCancelRcptCostRadio[i] = new RadioButton(getApplicationContext());
                            yesCmplCancelRcptCostCode[i] = new TextView(getApplicationContext());
                            yesCmplCancelRcptCostCode[i].setVisibility(View.GONE);
                            yesCmplCancelRcptCostEtc[i] = new TextView(getApplicationContext());


                            if(i != result.size()){
                                yesCmplCancelRcptCostRadio[i].setText(result.get(i).getComboNm());
                                yesCmplCancelRcptCostCode[i].setText(result.get(i).getComboCd());
                                yesCmplCancelRcptCostEtc[i].setVisibility(View.GONE);
                            }
                            else
                            {
                                yesCmplCancelRcptCostRadio[i].setText("없음");
                                yesCmplCancelRcptCostCode[i].setText("N");
                                yesCmplCancelRcptCostEtc[i].setVisibility(View.GONE);
                            }

                            if(yesCmplCancelRcptCostCode[i].getText().equals("N"))
                            {
                                noNum = i;

                            }



                            int finalI = i;


                            tRow.addView(yesCmplCancelRcptCostRadio[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            tRow.addView(yesCmplCancelRcptCostCode[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            tRow.addView(yesCmplCancelRcptCostEtc[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                            yesCancelCmplTable.addView(tRow, new TableLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                        }




                        //양중비 제목 보이는 부분
                        TextView yesYangJungCost = new TextView(getApplicationContext());
                        yesYangJungCost.setText("\n양중비");
                        //TableRow tRowYangJungCost = new TableRow(getApplicationContext());     //양중비비 제목 보이는 부분

                        yesCancelCmplTable.addView(yesYangJungCost, new TableLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));

                        yesCmplCancelYangJungRadio       = new RadioButton[2];        //사유선택
                        yesCmplCancelYangJungCode     = new TextView[2];        //사유 코드
                        yesCmplCancelYangJungEtc       = new TextView[2];       //내용입력

                        for(int i = 0; i<2; i++) { //있음 없음 2개뿐
                            TableRow tRow = new TableRow(getApplicationContext());     // 테이블 ROW 생성

                            yesCmplCancelYangJungRadio[i] = new RadioButton(getApplicationContext());
                            yesCmplCancelYangJungCode[i] = new TextView(getApplicationContext());
                            yesCmplCancelYangJungCode[i].setVisibility(View.GONE);
                            yesCmplCancelYangJungEtc[i] = new TextView(getApplicationContext());

                            if(i==0) //있음
                            {
                                yesCmplCancelYangJungRadio[i].setText("있음");
                                yesCmplCancelYangJungCode[i].setText("Y");
                                yesCmplCancelYangJungEtc[i].setVisibility(View.GONE);
                            }
                            else if(i==1)
                            {
                                yesCmplCancelYangJungRadio[i].setText("없음");
                                yesCmplCancelYangJungCode[i].setText("N");
                                yesCmplCancelYangJungEtc[i].setVisibility(View.GONE);
                            }


                            if(yesCmplCancelYangJungCode[i].getText().equals("Y"))
                            {
                                yangJungNo = i;
                            }

                            int finalI = i;


                            tRow.addView(yesCmplCancelYangJungRadio[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            tRow.addView(yesCmplCancelYangJungCode[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            tRow.addView(yesCmplCancelYangJungEtc[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                            yesCancelCmplTable.addView(tRow, new TableLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                        }


                        //내림서비스 제목 보이는 부분
                        TextView yesCmplCancelService02Cost = new TextView(getApplicationContext());
                        yesCmplCancelService02Cost.setText("\n내림서비스");
                        //TableRow tRowService02Cost = new TableRow(getApplicationContext());

                        yesCancelCmplTable.addView(yesCmplCancelService02Cost, new TableLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));

                        yesCmplCancelService02Radio       = new RadioButton[2];        //사유선택
                        yesCmplCancelService02Code     = new TextView[2];        //사유 코드
                        yesCmplCancelService02Etc       = new TextView[2];       //내용입력

                        for(int i = 0; i<2; i++) { //있음 없음 2개뿐
                            TableRow tRow = new TableRow(getApplicationContext());     // 테이블 ROW 생성

                            yesCmplCancelService02Radio[i] = new RadioButton(getApplicationContext());
                            yesCmplCancelService02Code[i] = new TextView(getApplicationContext());
                            yesCmplCancelService02Code[i].setVisibility(View.GONE);
                            yesCmplCancelService02Etc[i] = new TextView(getApplicationContext());

                            if(i==0) //있음
                            {
                                yesCmplCancelService02Radio[i].setText("있음");
                                yesCmplCancelService02Code[i].setText("Y");
                                yesCmplCancelService02Etc[i].setVisibility(View.GONE);
                            }
                            else if(i==1)
                            {
                                yesCmplCancelService02Radio[i].setText("없음");
                                yesCmplCancelService02Code[i].setText("N");
                                yesCmplCancelService02Etc[i].setVisibility(View.GONE);
                            }


                            if(yesCmplCancelService02Code[i].getText().equals("Y"))
                            {
                                service02 = i;
                            }

                            int finalI = i;


                            tRow.addView(yesCmplCancelService02Radio[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            tRow.addView(yesCmplCancelService02Code[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            tRow.addView(yesCmplCancelService02Etc[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                            yesCancelCmplTable.addView(tRow, new TableLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                        }


                        //벽고정->기타 제목 보이는 부분
                        TextView yesCmplCancelService03Cost = new TextView(getApplicationContext());
                        yesCmplCancelService03Cost.setText("\n기타");
                        //TableRow tRowService03Cost = new TableRow(getApplicationContext());

                        yesCancelCmplTable.addView(yesCmplCancelService03Cost, new TableLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));

                        yesCmplCancelService03Radio       = new RadioButton[2];        //사유선택
                        yesCmplCancelService03Code     = new TextView[2];        //사유 코드
                        yesCmplCancelService03Etc       = new TextView[2];       //내용입력

                        for(int i = 0; i<2; i++) { //있음 없음 2개뿐
                            TableRow tRow = new TableRow(getApplicationContext());     // 테이블 ROW 생성

                            yesCmplCancelService03Radio[i] = new RadioButton(getApplicationContext());
                            yesCmplCancelService03Code[i] = new TextView(getApplicationContext());
                            yesCmplCancelService03Code[i].setVisibility(View.GONE);
                            yesCmplCancelService03Etc[i] = new TextView(getApplicationContext());

                            if(i==0) //있음
                            {
                                yesCmplCancelService03Radio[i].setText("있음");
                                yesCmplCancelService03Code[i].setText("Y");
                                yesCmplCancelService03Etc[i].setVisibility(View.GONE);
                            }
                            else if(i==1)
                            {
                                yesCmplCancelService03Radio[i].setText("없음");
                                yesCmplCancelService03Code[i].setText("N");
                                yesCmplCancelService03Etc[i].setVisibility(View.GONE);
                            }


                            if(yesCmplCancelService03Code[i].getText().equals("Y"))
                            {
                                service03 = i;
                            }

                            int finalI = i;


                            tRow.addView(yesCmplCancelService03Radio[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            tRow.addView(yesCmplCancelService03Code[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            tRow.addView(yesCmplCancelService03Etc[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                            yesCancelCmplTable.addView(tRow, new TableLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                        }
                    }
                    else
                    {
                        commonHandler.showAlertDialog("배송취소 특이사항 조회 실패", "조회 0건");
                    }
                    showProgress(false);
                }
                else{
                    commonHandler.showAlertDialog("배송취소 특이사항 조회 실패","응답결과가 없습니다.");
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<List<YesCmplVO>> call, Throwable t) {
                commonHandler.showAlertDialog("배송취소 특이사항 조회 실패","접속실패\n"+t.getMessage());
                showProgress(false);
            }
        });
    }





    YesCmplVO onCreateData;  //화면 열리자마자 불러온데이터 여기에 넣기(여기 넣었다가 나중에 배송취소 처리 완료 버튼 누를때 알림톡 발송 보냄)
    //화면 열리자 마자 불러오기
    private void mYesCmplOnCreate(YesCmplOnCreateVO yesCmplCancelOnCreateVO) {
        service.mYesCmplOnCreate(yesCmplCancelOnCreateVO).enqueue(new Callback<YesCmplVO>() {

            @SuppressLint("LongLogTag") @Override
            public void onResponse(Call<YesCmplVO> call, Response<YesCmplVO> response) {

                if(response.isSuccessful()) //응답값이 있다
                {
                    YesCmplVO result = response.body();
                    onCreateData = result;

                    if(result.getImg1() != null  && !result.getImg1().equals(""))
                    {
                        try{
                            new DrawUrlImageTaskHandler(
                                    (ImageView) findViewById(R.id.yesCancelPicAddImg01))
                                    .execute(textValueHandler.HTTP_HOST + result.getImg1());
                            yesCancelPicAddUri01
                                    .setText(textValueHandler.HTTP_HOST + result.getImg1());
                            yesCancelPicFileName01.setText(result.getImg1());


                        }catch(Exception e){
                            e.printStackTrace();
                            //yesCancelPicAddImg01.setVisibility(View.INVISIBLE);
                            yesCancelPicAddImg01.setImageBitmap(null);
                        }
                    }
                    else
                    {
                        //yesCancelPicAddImg01.setVisibility(View.INVISIBLE);
                        yesCancelPicAddImg01.setImageBitmap(null);
                    }

                    if(result.getImg2() != null  && !result.getImg2().equals(""))
                    {
                        try{
                            new DrawUrlImageTaskHandler(
                                    (ImageView) findViewById(R.id.yesCancelPicAddImg02))
                                    .execute(textValueHandler.HTTP_HOST + result.getImg2());
                            yesCancelPicAddUri02
                                    .setText(textValueHandler.HTTP_HOST + result.getImg2());
                            yesCancelPicFileName02.setText(result.getImg2());
                        }
                        catch(Exception e){
                            e.printStackTrace();
                            //yesCancelPicAddImg02.setVisibility(View.INVISIBLE);
                            yesCancelPicAddImg02.setImageBitmap(null);
                        }
                    }
                    else
                    {
                        //yesCancelPicAddImg02.setVisibility(View.INVISIBLE);
                        yesCancelPicAddImg02.setImageBitmap(null);
                    }

                    if(result.getImg3() != null  && !result.getImg3().equals(""))
                    {
                        try{
                            new DrawUrlImageTaskHandler(
                                    (ImageView) findViewById(R.id.yesCancelPicAddImg03))
                                    .execute(textValueHandler.HTTP_HOST + result.getImg3());
                            yesCancelPicAddUri03
                                    .setText(textValueHandler.HTTP_HOST + result.getImg3());
                            yesCancelPicFileName03.setText(result.getImg3());

                        }
                        catch(Exception e){
                            e.printStackTrace();
                            //yesCancelPicAddImg03.setVisibility(View.INVISIBLE);
                            yesCancelPicAddImg03.setImageBitmap(null);
                        }
                    }
                    else
                    {
                        //yesCancelPicAddImg03.setVisibility(View.INVISIBLE);
                        yesCancelPicAddImg03.setImageBitmap(null);
                    }





                    if(result.getImg4() != null  && !result.getImg4().equals(""))
                    {
                        try{
                            new DrawUrlImageTaskHandler(
                                    (ImageView) findViewById(R.id.yesCancelPicAddImg04))
                                    .execute(textValueHandler.HTTP_HOST + result.getImg4());
                            yesCancelPicAddUri04
                                    .setText(textValueHandler.HTTP_HOST + result.getImg4());
                            yesCancelPicFileName04.setText(result.getImg4());
                        }  catch(Exception e){
                            e.printStackTrace();
                            //yesCancelPicAddImg04.setVisibility(View.INVISIBLE);
                            yesCancelPicAddImg04.setImageBitmap(null);
                        }
                    }else{
                        //yesCancelPicAddImg04.setVisibility(View.INVISIBLE);
                        yesCancelPicAddImg04.setImageBitmap(null);
                    }

                    if(result.getImg5() != null  && !result.getImg5().equals("")){
                        try{
                            new DrawUrlImageTaskHandler(
                                    (ImageView) findViewById(R.id.yesCancelPicAddImg05))
                                    .execute(textValueHandler.HTTP_HOST + result.getImg5());
                            yesCancelPicAddUri05
                                    .setText(textValueHandler.HTTP_HOST + result.getImg5());
                            yesCancelPicFileName05.setText(result.getImg5());
                        }
                        catch(Exception e){
                            e.printStackTrace();
                            //yesCancelPicAddImg05.setVisibility(View.INVISIBLE);
                            yesCancelPicAddImg05.setImageBitmap(null);
                        }
                    }else{
                        //yesCancelPicAddImg05.setVisibility(View.INVISIBLE);
                        yesCancelPicAddImg05.setImageBitmap(null);
                    }


                    if(result.getImg6() != null  && !result.getImg6().equals("")){
                        try{
                            new DrawUrlImageTaskHandler(
                                    (ImageView) findViewById(R.id.yesCancelPicAddImg06))
                                    .execute(textValueHandler.HTTP_HOST + result.getImg6());
                            yesCancelPicAddUri06
                                    .setText(textValueHandler.HTTP_HOST + result.getImg6());
                            yesCancelPicFileName06.setText(result.getImg6());
                        }  catch(Exception e){
                            e.printStackTrace();
                            //yesCancelPicAddImg06.setVisibility(View.INVISIBLE);
                            yesCancelPicAddImg06.setImageBitmap(null);
                        }
                    }else{
                        //yesCancelPicAddImg06.setVisibility(View.INVISIBLE);
                        yesCancelPicAddImg06.setImageBitmap(null);

                    }

                    if(result.getSignImg() != null  && !result.getSignImg().equals("")){
                        try{
                            new DrawUrlImageTaskHandler(
                                    (ImageView) findViewById(R.id.signatureCancelPad))
                                    .execute(textValueHandler.HTTP_HOST + result.getSignImg());
                            signCancelUri.setText(textValueHandler.HTTP_HOST + result.getSignImg());
                            signCancelFileName.setText(result.getSignImg());




                        }  catch(Exception e){
                            e.printStackTrace();
                            signatureCancelPad.setVisibility(View.INVISIBLE);
                            Log.d(TAG,"getSignImg catch : " + textValueHandler.HTTP_HOST + result.getSignImg());
                        }
                    }else{signatureCancelPad.setVisibility(View.INVISIBLE);
                        Log.d(TAG,"getSignImg else : " + textValueHandler.HTTP_HOST + result.getSignImg());}



                    //착불비
                    for(int q=0;q<yesCmplCancelRcptCostRadio.length;q++)
                    {
                        yesCmplCancelRcptCostRadio[q].setEnabled(false); //체크버튼을 비활성화 함
                        if(yesCmplCancelRcptCostCode[q].getText().toString().equals(result.getDlvyCostClct())){
                            yesCmplCancelRcptCostRadio[q].setChecked(true);  //체크버튼을 체크처리함
                            yesCmplCancelRcptCostRadio[q].setTypeface(null, Typeface.BOLD);
                            yesCmplCancelRcptCostRadio[q].setEnabled(true); //체크버튼을 활성화 함
                            //불러들인 코드가 N이아니고 고객입금이나 현장수금을 선택했을때 입력하는 란을 활성화 시킴
                            //코드       고객입금 - CUST, 현장수금 - CASH

Log.d("getDlvyCostClct",result.getDlvyCostClct());
                            if(result.getDlvyCostClct().equals("CUST")){
                                yesCmplCancelRcptCostEtc[q].setText(result.getDlvyCostTxt());   // 고객입금 - CUST, 현장수금 - CASH 일 경우에 입력한 값 넣기
                                yesCmplCancelRcptCostEtc[q].setTextSize(18);
                                yesCmplCancelRcptCostEtc[q].setVisibility(View.VISIBLE);           //입력한 값 보여주기
                                yesCmplCancelRcptCostEtc[q].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_gray_detail_white_btn));

                            }
                            else if(result.getDlvyCostClct().equals("CASH")){
                                yesCmplCancelRcptCostEtc[q].setText(result.getDlvyCostTxt());   // 고객입금 - CUST, 현장수금 - CASH 일 경우에 입력한 값 넣기
                                yesCmplCancelRcptCostEtc[q].setTextSize(18);
                                yesCmplCancelRcptCostEtc[q].setVisibility(View.VISIBLE);           //입력한 값 보여주기
                                yesCmplCancelRcptCostEtc[q].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_gray_detail_white_btn));

                            }
                        }
                    }
                    //양중비
                    for(int q=0;q<yesCmplCancelYangJungRadio.length;q++)
                    {
                        yesCmplCancelYangJungRadio[q].setEnabled(false); //체크버튼을 비활성화 함
                        if(yesCmplCancelYangJungCode[q].getText().toString().equals(result.getLiftCostYn())){
                            yesCmplCancelYangJungRadio[q].setChecked(true);  //체크버튼을 체크처리함
                            yesCmplCancelYangJungRadio[q].setTypeface(null, Typeface.BOLD);
                            yesCmplCancelYangJungRadio[q].setEnabled(true); //체크버튼을 활성화 함
                            //불러들인 코드가 Y이라면 글자도 보여줘야함
                            if(result.getLiftCostYn().equals("Y")){
                                yesCmplCancelYangJungEtc[q].setText(result.getListCostTxt());   //Y일 경우에 입력한 값 넣기
                                yesCmplCancelYangJungEtc[q].setTextSize(18);
                                yesCmplCancelYangJungEtc[q].setVisibility(View.VISIBLE);           //입력한 값 보여주기
                                yesCmplCancelYangJungEtc[q].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_gray_detail_white_btn));
                            }
                        }
                    }
                    //내림서비스
                    for(int q=0;q<yesCmplCancelService02Radio.length;q++)
                    {
                        yesCmplCancelService02Radio[q].setEnabled(false); //체크버튼을 비활성화 함
                        if(yesCmplCancelService02Code[q].getText().toString().equals(result.getDownSrvcYn())){
                            yesCmplCancelService02Radio[q].setChecked(true);  //체크버튼을 체크처리함
                            yesCmplCancelService02Radio[q].setTypeface(null, Typeface.BOLD);
                            yesCmplCancelService02Radio[q].setEnabled(true); //체크버튼을 활성화 함
                            //불러들인 코드가 Y이라면 글자도 보여줘야함
                            if(result.getDownSrvcYn().equals("Y")){
                                yesCmplCancelService02Etc[q].setText(result.getDownSrvcTxt());   //Y일 경우에 입력한 값 넣기
                                yesCmplCancelService02Etc[q].setTextSize(18);
                                yesCmplCancelService02Etc[q].setVisibility(View.VISIBLE);           //입력한 값 보여주기
                                yesCmplCancelService02Etc[q].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_gray_detail_white_btn));
                            }
                        }
                    }

                    //벽고정->기타
                    for(int q=0;q<yesCmplCancelService03Radio.length;q++)
                    {
                        yesCmplCancelService03Radio[q].setEnabled(false); //체크버튼을 비활성화 함
                        if(yesCmplCancelService03Code[q].getText().toString().equals(result.getWallFstnYn())){
                            yesCmplCancelService03Radio[q].setChecked(true);  //체크버튼을 체크처리함
                            yesCmplCancelService03Radio[q].setTypeface(null, Typeface.BOLD);
                            yesCmplCancelService03Radio[q].setEnabled(true); //체크버튼을 활성화 함
                            //불러들인 코드가 Y이라면 글자도 보여줘야함
                            if(result.getWallFstnYn().equals("Y")){
                                yesCmplCancelService03Etc[q].setText(result.getWallFstnTxt());   //Y일 경우에 입력한 값 넣기
                                yesCmplCancelService03Etc[q].setTextSize(18);
                                yesCmplCancelService03Etc[q].setVisibility(View.VISIBLE);           //입력한 값 보여주기
                                yesCmplCancelService03Etc[q].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_gray_detail_white_btn));
                            }
                        }
                    }
                    //메모
                    yesCancelCmplMemo.setText(result.getMemo());
                    yesCancelCmplMemo.setTextSize(18);











                }
                else{
                    //commonHandler.showToast("배송취소 onCreate 데이터 조회 실패\n응답결과가 없음",0,17,17);
                    commonHandler.showFinishAlertDialog("배송취소 onCreate 데이터 조회 실패","응답결과가 없거나 여러개 입니다.\n[instMobileMId : "+instMobileMIdValue+"]\n이전화면으로 이동합니다","Y");
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<YesCmplVO> call, Throwable t) {
                commonHandler.showToast("배송취소 onCreate 데이터 조회 실패\n접속실패\n"+t.getMessage(),0,17,17);
                commonHandler.showFinishAlertDialog("배송취소 onCreate 데이터 조회 실패","접속실패\n"+t.getMessage(),"Y");
                showProgress(false);
            }
        });
    }



    //배송취소 처리 버튼 눌렀을떄 (SAVE)
    private void mYesCmplDel(YesCmplDelVO yesCmplDelVO) {
        service.mYesCmplDel(yesCmplDelVO).enqueue(new Callback<YesCmplVO>() {

            @Override
            public void onResponse(Call<YesCmplVO> call, Response<YesCmplVO> response) {

                if(response.isSuccessful()) //응답값이 있다
                {
                    YesCmplVO result = response.body();
                    if(result.getRtnYn().equals("Y"))
                    {
                        View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                        AlertDialog.Builder builder = new AlertDialog.Builder(YesCmplCancelActivity.this);
                        builder.setView(dialogView);

                        AlertDialog alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();

                        Button ok_btn = dialogView.findViewById(R.id.successBtn);
                        TextView ok_txt = dialogView.findViewById(R.id.successText);
                        ok_txt.setText("배송완료 취소 성공");
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
                        commonHandler.showAlertDialog("배송취소 처리 실패",""+(result.getRtnMsg() == null ? "처리 실패 " : result.getRtnMsg()));
                    }
                    showProgress(false);

                }
                else{
                    commonHandler.showAlertDialog("배송취소 처리 저장 실패","응답결과가 없음");
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<YesCmplVO> call, Throwable t) {
                commonHandler.showAlertDialog("배송취소 처리 저장 실패","접속실패\n"+t.getMessage());
                showProgress(false);
            }
        });
    }




    //배송취소 화면 나가기
    private void yesCmplCancelCancel(){

        View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(YesCmplCancelActivity.this);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
        TextView ok_txt = dialogView.findViewById(R.id.confirmText);
        ok_txt.setTextSize(20);
        ok_txt.setTypeface(null, Typeface.BOLD);
        ok_txt.setText("배송완료 화면을 나가시겠습니까?");
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