package com.mobile.alliance.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.mobile.alliance.R;
import com.mobile.alliance.api.BackPressCloseHandler;
import com.mobile.alliance.api.CommonHandler;
import com.mobile.alliance.api.PersistentCookieStore;
import com.mobile.alliance.api.RetrofitClient;
import com.mobile.alliance.api.ServiceApi;
import com.mobile.alliance.api.TextValueHandler;
import com.mobile.alliance.entity.noCmpl.NoCmplSaveStatVO;
import com.mobile.alliance.entity.noCmpl.NoCmplSaveTalkVO;
import com.mobile.alliance.entity.noCmpl.NoCmplVO;
import com.mobile.alliance.entity.sendTalk.SendTalkVO;
import com.mobile.alliance.entity.sendTalk.TalkVO;
import com.mobile.alliance.entity.yesCmpl.YesCmplOnCreateVO;
import com.mobile.alliance.entity.yesCmpl.YesCmplSaveStatVO;
import com.mobile.alliance.entity.yesCmpl.YesCmplSaveTalkVO;
import com.mobile.alliance.entity.yesCmpl.YesCmplVO;
import com.mobile.alliance.fragment.DeliveryListFragment;
import com.mobile.alliance.util.ImageResizeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
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

public class YesCmplActivity extends AppCompatActivity {


    public static Toast mToast;


    String completeMsg = "";
    private static TextValueHandler textValueHandler = new TextValueHandler();
    private String TAG = "YesCmplActivity";
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




    ImageView yesPicAddImg01;
    ImageView yesPicAddImg02;
    ImageView yesPicAddImg03;
    ImageView yesPicAddImg04;
    ImageView yesPicAddImg05;
    ImageView yesPicAddImg06;

    TextView yesPicAddUri01;
    TextView yesPicAddUri02;
    TextView yesPicAddUri03;
    TextView yesPicAddUri04;
    TextView yesPicAddUri05;
    TextView yesPicAddUri06;

    TextView yesPicFileName01;
    TextView yesPicFileName02;
    TextView yesPicFileName03;
    TextView yesPicFileName04;
    TextView yesPicFileName05;
    TextView yesPicFileName06;

    View yesPicAddView;
    ImageView yesCmplBack;



    RadioGroup yesCmplReasonGroup;

    TableLayout yesCmplTable;
    Button yesCmplBtn;



    //뷰 배열

    //착불비
    RadioButton yesCmplRcptCostRadio[];
    TextView    yesCmplRcptCostCode[];
    EditText    yesCmplRcptCostEtc[];


    //양중비
    RadioButton yesCmplYangJungRadio[];
    TextView    yesCmplYangJungCode[];
    EditText    yesCmplYangJungEtc[] ;

    //내림서비스
    RadioButton yesCmplService02Radio[];
    TextView    yesCmplService02Code[];
    EditText    yesCmplService02Etc[] ;


    //벽고정->기타
    RadioButton yesCmplService03Radio[];
    TextView    yesCmplService03Code[];
    EditText    yesCmplService03Etc[] ;






    EditText yesCmplMemo;

    SignaturePad signaturePad;
    Button saveButton, clearButton;

    TextView signUri,    signFileName;
    public static Integer[]     YES_LOCK = {0,0,0,0,0,0};       //0 해제 , 1 이미지 선택완료 , 2 파일서버 전송 완료
    public static Integer       YES_SELECT_NO = 0;
    public static ImageView[]   yesPicAddImg;
    public static TextView[]    yesPicAddUri;
    public static TextView[]    yesPicFileName;

    /**
     *  변수설정
     */

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;
    private Boolean isCamera = false;
    private File tempFile;
    String instMobileMIdValue;
    String soNoValue;




    String finalYesCmplSelectMemo           ;
    String finalYesCmplRcptCostSetCode;
    String finalYesCmplRcptCostSetEtc;
    String finalYesCmplYangJungSetCode;
    String finalYesCmplYangJungSetEtc;
    String finalYesCmplService02SetCode;
    String finalYesCmplService02SetEtc;
    String finalYesCmplService03SetCode;
    String finalYesCmplService03SetEtc;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yes_cmpl);

        YES_LOCK = new Integer[]{0, 0, 0, 0, 0, 0};
        YES_SELECT_NO = 0;




        signaturePad = (SignaturePad) findViewById(R.id.yesSignaturePad);
        saveButton = (Button)findViewById(R.id.saveButton);
        clearButton = (Button)findViewById(R.id.clearButton);
        signUri = (TextView)    findViewById(R.id.signUri);
        signFileName = (TextView)   findViewById(R.id.signFleName) ;



        saveButton.setVisibility(View.INVISIBLE);
        clearButton.setVisibility(View.INVISIBLE);
        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {

            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched
            }

            @Override
            public void onSigned() {
                //Event triggered when the pad is signed
                //saveButton.setEnabled(true);
                //clearButton.setEnabled(true);
                saveButton.setVisibility(View.VISIBLE);
                clearButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onClear() {
                //Event triggered when the pad is cleared
                //saveButton.setEnabled(false);
                //clearButton.setEnabled(false);
                saveButton.setVisibility(View.INVISIBLE);
                clearButton.setVisibility(View.INVISIBLE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows @Override
            public void onClick(View v) {
                //write code for saving the signature here
                //Toast.makeText(YesCmplActivity.this, "Signature Saved", Toast.LENGTH_SHORT).show();

                //signSetImage();
                saveButton.setVisibility(View.INVISIBLE);

                File signFile =   saveBitmapToPng(signaturePad.getSignatureBitmap());

                String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());

signaturePad.setEnabled(false);
signUri.setText(signFile.getAbsolutePath());
signFileName.setText(timeStamp +"/"+signFile.getName());

                showProgress(true);
                //이미지 뷰에 이미지 넣은다음 바로 서버로 전송한다
                SignNThread signNThread = new SignNThread();
                signNThread.start();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signaturePad.clear();
                signaturePad.setEnabled(true);
            }
        });
        instMobileMIdValue   = getIntent().getStringExtra("instMobileMId");
        soNoValue            = getIntent().getStringExtra("soNo");
        //내부에 데이터 저장하는것
        sharePref = getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        editor = sharePref.edit();

        //진행바
        mProgressView = (ProgressBar) findViewById(R.id.yesCmplProgress);
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
        serviceTalk = RetrofitClient.getTalk(client).create(ServiceApi.class);  //알림톡용
        service = RetrofitClient.getClient(client).create(ServiceApi.class);    //시스템용

        //화면 뜨자마자 SP 수행시켜서 배송완료사유 불러옴
        mYesCmplReasonCombo(new YesCmplVO(
                sharePref.getString("cmpyCd","")
                ,"COMM"
                ,"DLVY_COST_CLCT"
                ,""
                ,"Y"

        ));
        showProgress(true);


        //화면 뜨자 마자 내용 부름
        mYesCmplOnCreate(new YesCmplOnCreateVO(   instMobileMIdValue  ));
        showProgress(true);

        yesCmplTable = (TableLayout)findViewById(R.id.yesCmplTable);

        yesCmplBack = (ImageView)findViewById(R.id.yesCmplBack);
        yesCmplBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                yesCmplCancel();
            }
        });


        yesCmplReasonGroup = (RadioGroup)findViewById(R.id.yesCmplReasonGroup);


        //배송완료 버튼
        yesCmplBtn = (Button) findViewById(R.id.yesCmplBtn);


        yesCmplMemo = (EditText)findViewById(R.id.yesCmplMemo);



        yesPicAddView = (View)findViewById(R.id.yesPicAddView);
        yesPicAddUri01 = (TextView)findViewById(R.id.yesPicAddUri01);
        yesPicAddUri02 = (TextView)findViewById(R.id.yesPicAddUri02);
        yesPicAddUri03 = (TextView)findViewById(R.id.yesPicAddUri03);
        yesPicAddUri04 = (TextView)findViewById(R.id.yesPicAddUri04);
        yesPicAddUri05 = (TextView)findViewById(R.id.yesPicAddUri05);
        yesPicAddUri06 = (TextView)findViewById(R.id.yesPicAddUri06);

        yesPicFileName01 = (TextView)findViewById(R.id.yesPicFileName01);
        yesPicFileName02 = (TextView)findViewById(R.id.yesPicFileName02);
        yesPicFileName03 = (TextView)findViewById(R.id.yesPicFileName03);
        yesPicFileName04 = (TextView)findViewById(R.id.yesPicFileName04);
        yesPicFileName05 = (TextView)findViewById(R.id.yesPicFileName05);
        yesPicFileName06 = (TextView)findViewById(R.id.yesPicFileName06);


        yesPicAddImg01 = (ImageView)findViewById(R.id.yesPicAddImg01);
        yesPicAddImg02 = (ImageView)findViewById(R.id.yesPicAddImg02);
        yesPicAddImg03 = (ImageView)findViewById(R.id.yesPicAddImg03);
        yesPicAddImg04 = (ImageView)findViewById(R.id.yesPicAddImg04);
        yesPicAddImg05 = (ImageView)findViewById(R.id.yesPicAddImg05);
        yesPicAddImg06 = (ImageView)findViewById(R.id.yesPicAddImg06);

        yesPicAddImg = new ImageView[]{yesPicAddImg01, yesPicAddImg02, yesPicAddImg03, yesPicAddImg04, yesPicAddImg05,
                yesPicAddImg06};
        yesPicAddUri = new TextView[]{yesPicAddUri01, yesPicAddUri02, yesPicAddUri03, yesPicAddUri04, yesPicAddUri05,
                yesPicAddUri06};
        yesPicFileName = new TextView[]{yesPicFileName01, yesPicFileName02, yesPicFileName03, yesPicFileName04, yesPicFileName05,
                yesPicFileName06};
        for(int i=0;i<6;i++){

            int finalI = i;
            yesPicAddImg[i].setOnClickListener(new ImageView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(YES_LOCK[finalI] == 0)   //해제
                    {

                        try{
                            mToast.cancel();
                        }
                        catch(Exception e)
                        {}
                        //컨펌(버튼 두개중 하나 선택)

                        View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(YesCmplActivity.this);
                        builder.setView(dialogView);

                        AlertDialog alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();

                        Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
                        TextView ok_txt = dialogView.findViewById(R.id.confirmText);
                        ok_txt.setVisibility(View.GONE);

                        ok_btn.setText("카메라");
                        ok_btn.setOnClickListener(new View.OnClickListener() {
                            @SneakyThrows @Override
                            public void onClick(View v) {

                                alertDialog.dismiss();
                                YES_SELECT_NO= finalI;
                                takePhoto();

                            }
                        });

                        Button no_btn = dialogView.findViewById(R.id.confirmBtnNo);
                        no_btn.setText("갤러리");
                        no_btn.setOnClickListener(new View.OnClickListener() {
                            @SneakyThrows @Override
                            public void onClick(View v) {

                                alertDialog.dismiss();
                                YES_SELECT_NO= finalI;
                                goToAlbum();
                            }
                        });



                    }
                    else        //1 잠금. -> 이미지가 교체되었다 라는거다. 이때 이 이미지를 터치하면 새로운 Activity 에 이미지,삭제버튼,다운로드, 핀치 투 줌 넣기
                    {
                        if(yesPicAddUri[finalI].getText() != null && !yesPicAddUri[finalI].getText().equals(""))
                        {

                            Intent imageActivity = new Intent(YesCmplActivity.this, ImageActivity.class);
                            imageActivity.putExtra("imageUri",yesPicAddUri[finalI].getText());
                            imageActivity.putExtra("imageNo",finalI+"");
                            imageActivity.putExtra("imageType","delete");
                            imageActivity.putExtra("imageGubn","yes");  //배송완료

                            startActivity(imageActivity);  //다음 액티비티를 열고
                        }
                        else
                        {
                            commonHandler.showAlertDialog("이미지 열기 실패","이미지 경로가 존재하지 않습니다.");
                        }
                    }
                }
            });
        }

        //배송완료 처리 버튼을 눌렀을때
        yesCmplBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag") @Override
            public void onClick(View v){

                // 1. 사진 최소 1장 있는지 체크.  YES_LOCK 가 2 인게 1개 이상있어야함

                // 2. 착불비가 1개 체크 되어있어야함. 그것이 없음 N 이라면 내용입력이 필수임
                // 3. 양중비가 1개 체크 되어있어야함. 그것이 있음 Y 이라면 내용입력이 필수임
                // 4. 내림서비스가 1개 체크 되어있어야함. 그것이 있음 Y 이라면 내용입력이 필수임
                // 5. 벽고정->기타이 1개 체크 되어있어야함. 그것이 있음 Y 이라면 내용입력이 필수임
                // 6. 내용(메모) 입력되어야함--필수아님
                // 7. 싸인패드 싸인은 필수임


                Integer yesLock=0;
                for(int q=0;q<YES_LOCK.length;q++)
                {
                    if(YES_LOCK[q] == 2)
                    {
                        yesLock++;
                    }
                }

                //착불비
                Integer rcptCostCnt=0;
                for(int q=0;q<yesCmplRcptCostRadio.length;q++)
                {
                    //체크 한것 대상
                    if(yesCmplRcptCostRadio[q].isChecked()){
                        //N 라면 글까지 써야 체크한것으로 인식
                        if(yesCmplRcptCostCode[q].getText().equals("CUST")  || yesCmplRcptCostCode[q].getText().equals("CASH") ){
                            if(yesCmplRcptCostEtc[q].getText().toString().trim().length() > 0){
                                rcptCostCnt++;
                            }
                            //ETC 가 아니라면
                        } else {
                            rcptCostCnt++;
                        }
                    }
                }


                //양중비
                Integer yangJungCnt=0;
                for(int q=0;q<yesCmplYangJungRadio.length;q++)
                {
                    //체크 한것 대상
                    if(yesCmplYangJungRadio[q].isChecked()){
                        //Y 라면 글까지 써야 체크한것으로 인식
                        if(yesCmplYangJungCode[q].getText().equals("Y")){
                            if(yesCmplYangJungEtc[q].getText().toString().trim().length() > 0){
                                yangJungCnt++;
                            }
                        //N이라면
                        } else {
                            yangJungCnt++;
                        }
                    }
                }

                //내림서비스
                Integer service02Cnt=0;
                for(int q=0;q<yesCmplService02Radio.length;q++)
                {
                    //체크 한것 대상
                    if(yesCmplService02Radio[q].isChecked()){
                        //Y 라면 글까지 써야 체크한것으로 인식
                        if(yesCmplService02Code[q].getText().equals("Y")){
                            if(yesCmplService02Etc[q].getText().toString().trim().length() > 0){
                                service02Cnt++;
                            }
                            //N이라면
                        } else {
                            service02Cnt++;
                        }
                    }
                }
                //벽고정->기타
                Integer service03Cnt=0;
                for(int q=0;q<yesCmplService03Radio.length;q++)
                {
                    //체크 한것 대상
                    if(yesCmplService03Radio[q].isChecked()){
                        //Y 라면 글까지 써야 체크한것으로 인식
                        if(yesCmplService03Code[q].getText().equals("Y")){
                            if(yesCmplService03Etc[q].getText().toString().trim().length() > 0){
                                service03Cnt++;
                            }
                        //N이라면
                        } else {
                            service03Cnt++;
                        }
                    }
                }




                Integer yesMemoLength=0;
                yesMemoLength = yesCmplMemo.getText().toString().trim().length();



                // 7. 싸인패드는 필수

                Integer yesSignPad = signUri.getText().length();


                //착불비
                String yesCmplRcptCostSetCode = "";     //선택한 사유의 코드
                String yesCmplRcptCostSetEtc = "";      //선택이 N(없음) 이었을때 입력한 문구

                //양중비
                String yesCmplYangJungSetCode = "";     //선택한 사유의 코드
                String yesCmplYangJungSetEtc = "";      //선택이 Y(있음) 이었을때 입력한 문구

                //내림서비스
                String yesCmplService02SetCode = "";     //선택한 사유의 코드
                String yesCmplService02SetEtc = "";      //선택이 Y(있음) 이었을때 입력한 문구

                //내림서비스
                String yesCmplService03SetCode = "";     //선택한 사유의 코드
                String yesCmplService03SetEtc = "";      //선택이 Y(있음) 이었을때 입력한 문구

                String yesCmplSelectMemo = "";       //입력한 메모






                if(yesLock == 0)   //사진을 서버에 전송한게 없으면
                {
                    commonHandler.showAlertDialog("현장사진","현장사진이 한장도 없습니다.");
                    return;
                }
                else if(rcptCostCnt == 0){   //착불비 사유 가 1개도 없으면
                    commonHandler.showAlertDialog("착불비 사유","착불비 사유를 선택하세요.\n[고객입금] 또는 [현장수금]을 선택했다면 내용을 입력하세요.");
                    return;
                }
                else if(yangJungCnt == 0){   //양중비 사유 가 1개도 없으면
                    commonHandler.showAlertDialog("양중비 사유","양중비 사유를 선택하세요.\n[있음]을 선택했다면 내용을 입력하세요.");
                    return;
                }
                else if(service02Cnt == 0){   //내림서비스 사유 가 1개도 없으면
                    commonHandler.showAlertDialog("내림서비스 사유","내림서비스 사유를 선택하세요.\n[있음]을 선택했다면 내용을 입력하세요.");
                    return;
                }
                else if(service03Cnt == 0){   //벽고정->기타 사유 가 1개도 없으면
                    commonHandler.showAlertDialog("기타 사유","기타 사유를 선택하세요.\n[있음]을 선택했다면 내용을 입력하세요.");
                    return;
                }
                //메모 필수아님
               /* else if(yesMemoLength == 0){   //메모를 안썼다면
                    commonHandler.showAlertDialog("메모","메모를 입력하세요");
                    return;
                }*/
                else if (yesSignPad == 0){   //싸인했는지 여부
                    commonHandler.showAlertDialog("서명","서명은 필수 입니다.");
                    return;
                }

                else{
                    //착불비
                    for(int q=0;q<yesCmplRcptCostRadio.length;q++)
                    {
                        //체크 한것 대상
                        if(yesCmplRcptCostRadio[q].isChecked()){
                            yesCmplRcptCostSetCode = yesCmplRcptCostCode[q].getText().toString();
                            //CUST 또는 CASH 라면 글까지 써야 체크한것으로 인식
                            if(yesCmplRcptCostCode[q].getText().equals("CUST")){
                                if(yesCmplRcptCostEtc[q].getText().toString().trim().length() > 0){
                                    yesCmplRcptCostSetEtc = yesCmplRcptCostEtc[q].getText().toString();
                                }
                            }
                            else if(yesCmplRcptCostCode[q].getText().equals("CASH")){
                                if(yesCmplRcptCostEtc[q].getText().toString().trim().length() > 0){
                                    yesCmplRcptCostSetEtc = yesCmplRcptCostEtc[q].getText().toString();
                                }
                            }
                        }
                    }

                    //양중비
                    for(int q=0;q<yesCmplYangJungRadio.length;q++)
                    {
                        //체크 한것 대상
                        if(yesCmplYangJungRadio[q].isChecked()){
                            yesCmplYangJungSetCode = yesCmplYangJungCode[q].getText().toString();
                            //Y 이라면 글까지 써야 체크한것으로 인식
                            if(yesCmplYangJungCode[q].getText().equals("Y")){
                                if(yesCmplYangJungEtc[q].getText().toString().trim().length() > 0){
                                    yesCmplYangJungSetEtc = yesCmplYangJungEtc[q].getText().toString();
                                }
                            }
                        }
                    }


                    //내림서비스
                    for(int q=0;q<yesCmplService02Radio.length;q++)
                    {
                        //체크 한것 대상
                        if(yesCmplService02Radio[q].isChecked()){
                            yesCmplService02SetCode = yesCmplService02Code[q].getText().toString();
                            //Y 이라면 글까지 써야 체크한것으로 인식
                            if(yesCmplService02Code[q].getText().equals("Y")){
                                if(yesCmplService02Etc[q].getText().toString().trim().length() > 0){
                                    yesCmplService02SetEtc = yesCmplService02Etc[q].getText().toString();
                                }
                            }
                        }
                    }


                    //벽고정->기타
                    for(int q=0;q<yesCmplService03Radio.length;q++)
                    {
                        //체크 한것 대상
                        if(yesCmplService03Radio[q].isChecked()){
                            yesCmplService03SetCode = yesCmplService03Code[q].getText().toString();
                            //Y 이라면 글까지 써야 체크한것으로 인식
                            if(yesCmplService03Code[q].getText().equals("Y")){
                                if(yesCmplService03Etc[q].getText().toString().trim().length() > 0){
                                    yesCmplService03SetEtc = yesCmplService03Etc[q].getText().toString();
                                }
                            }
                        }
                    }



                    //6 배송완료 메모 구하기
                    yesCmplSelectMemo = yesCmplMemo.getText().toString().trim();



                     finalYesCmplSelectMemo           = yesCmplSelectMemo;
                     finalYesCmplRcptCostSetCode      = yesCmplRcptCostSetCode;
                     finalYesCmplRcptCostSetEtc       = yesCmplRcptCostSetEtc;
                     finalYesCmplYangJungSetCode      = yesCmplYangJungSetCode;
                     finalYesCmplYangJungSetEtc       = yesCmplYangJungSetEtc;
                     finalYesCmplService02SetCode     = yesCmplService02SetCode;
                     finalYesCmplService02SetEtc      = yesCmplService02SetEtc;
                     finalYesCmplService03SetCode     = yesCmplService03SetCode;
                     finalYesCmplService03SetEtc      = yesCmplService03SetEtc;





                    //컨펌(버튼 두개중 하나 선택)
                    View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(YesCmplActivity.this);
                    builder.setView(dialogView);

                    android.app.AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                    Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
                    TextView ok_txt = dialogView.findViewById(R.id.confirmText);
                    ok_txt.setTextSize(20);
                    ok_txt.setTypeface(null, Typeface.BOLD);
                    ok_txt.setText("배송완료 처리를 하시겠습니까?");
                    ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    ok_btn.setOnClickListener(new View.OnClickListener() {
                        @SneakyThrows @Override
                        public void onClick(View v) {



                            alertDialog.dismiss();
                            //1. 배송완료 처리 저장하기
                            //성공    1-1. 배송완료 알림톡 발송 1-1-1 알림톡발송성공 : 알림톡 결과저장
                            //                              1-1-2 알림톡발송실패 : 알림톡 결과저장
                            //2.배송완료 처리 저장 실패


                            //1. 배송완료 처리 저장하기
                            showProgress(true);
                            YesCmplSaveStatDb();

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
        });
    }
    //6. onActivityResult 분기 처리
    //7. 예외 사항 처리하기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {

            mToast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();

            if(tempFile != null) {
                if(tempFile.exists()) {
                    if(tempFile.delete()) {
                        //Log.d(TAG, tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    } else {
                        //Log.d(TAG, "tempFile 삭제 실패");
                    }

                } else {
                    //Log.d(TAG, "tempFile 존재하지 않음");
                }
            } else {
                //Log.d(TAG, "tempFile is null");
            }
            return;
        }

        if (requestCode == PICK_FROM_ALBUM) {

            Uri photoUri = data.getData();
            //Log.d(TAG, "PICK_FROM_ALBUM photoUri : " + photoUri);
            Cursor cursor = null;
            try {

                /*
                 *  Uri 스키마를
                 *  content:/// 에서 file:/// 로  변경한다.
                 */
                String[] proj = { MediaStore.Images.Media.DATA };

                assert photoUri != null;
                cursor = getContentResolver().query(photoUri, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();
                tempFile = new File(cursor.getString(column_index));

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            setImage();
        }
        else if (requestCode == PICK_FROM_CAMERA) {

            setImage();
        }
    }
    /**
     *  앨범에서 이미지 가져오기
     */
    private void goToAlbum() {
        isCamera = false;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);   //기본 갤러리
        //intent.setType("image/*")  ;    //구글갤러리 접근
        //intent.setAction(Intent.ACTION_GET_CONTENT);    //0505 추가
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    /**
     *  카메라에서 이미지 가져오기
     */
    private void takePhoto(){
        isCamera = true;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try{
            tempFile = createImageFile();
        }catch(IOException e){
            mToast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if(tempFile != null){
            /**
             *  안드로이드 OS 누가 버전 이후부터는 file:// URI 의 노출을 금지로 FileUriExposedException 발생
             *  Uri 를 FileProvider 도 감싸 주어야 합니다.
             *
             *  참고 자료 http://programmar.tistory.com/4 , http://programmar.tistory.com/5
             */

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.mobile.alliance.provider", tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
                //sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(tempFile)));
            } else {
                Uri photoUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
                //sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(tempFile)));
            }


        }
    }

    /**
     *  폴더 및 파일 만들기
     */
    private File createImageFile() throws IOException {

        // 이미지 파일 이름
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String imageFileName = "Alliance" +"_" +soNoValue + "_"+ YES_SELECT_NO+"_" + timeStamp+"_";


        // 이미지가 저장될 폴더 이름
        //File storageDir = new File(Environment.getExternalStorageDirectory() + "/Alliance/");
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/.alliance/");
        if (!storageDir.exists()) storageDir.mkdirs();


        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }

    /**
     *  tempFile 을 bitmap 으로 변환 후 ImageView 에 설정한다.
     */
    @SuppressLint("LongLogTag")
    private void setImage() {       //이미지뷰에 이미지를 넣는부분
        //sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(tempFile)));
        File oriTempFile;
        String header="";
        if (!isCamera)
        {
            header = "alliance_mobile_gallery_";
        }else
        {
            header = "alliance_mobile_camera_";
        }

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

        //String imageFileName = header + soNoValue + "_"+ YES_SELECT_NO+"_" + timeStamp + "_";
        String imageFileName = header + soNoValue + "_"+ YES_SELECT_NO+"_" + timeStamp;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/.alliance/");
        if (!storageDir.exists()) storageDir.mkdirs();

        File tempFile3 = tempFile;

        //File tempFile = new File(storageDir+"/"+imageFileName+tempFile3.getName());
        File tempFile = new File(storageDir+"/"+imageFileName+".jpg");


        ImageResizeUtils.resizeFile(tempFile3, tempFile, 1280, true);
        oriTempFile = tempFile;

        //Log.d("tempFile getAbsolutePath" , oriTempFile.getAbsolutePath());

         /*
        첫 번째 파라미터에 변형시킬 tempFile 을 넣었습니다.
        두 번째 파라미터에는 변형시킨 파일을 다시 tempFile에 저장해 줍니다.
        세 번째 파라미터는 이미지의 긴 부분을 1280 사이즈로 리사이징 하라는 의미입니다.
        네 번째 파라미터를 통해 카메라에서 가져온 이미지인 경우 카메라의 회전각도를 적용해 줍니다.(앨범에서 가져온 경우에는 회전각도를 적용 시킬 필요가 없겠죠?)
        */

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(oriTempFile.getAbsolutePath(), options);
        //Log.d(TAG, "setImage : " + oriTempFile.getAbsolutePath());

        yesPicAddImg[YES_SELECT_NO].setImageBitmap(originalBm);
        yesPicAddUri[YES_SELECT_NO].setText(oriTempFile.getAbsolutePath());

        YES_LOCK[YES_SELECT_NO] = 1;  //1 은 사진을 첨부했다 (갤러리 또는 카메라로 찍어서 앱의 이미지뷰에 넣었다 라는것)
        /**
         *  tempFile 사용 후 null 처리를 해줘야 합니다.
         *  (resultCode != RESULT_OK) 일 때 tempFile 을 삭제하기 때문에
         *  기존에 데이터가 남아 있게 되면 원치 않은 삭제가 이뤄집니다.
         */
        tempFile = null;
        showProgress(true);
        //이미지 뷰에 이미지 넣은다음 바로 서버로 전송한다
        NThread nThread = new NThread();
        nThread.start();

    }
    public File saveBitmapToPng(Bitmap bitmap) {
        /**
         * 캐시 디렉토리에 비트맵을 이미지파일로 저장하는 코드입니다.
         *
         * @version target API 28 ★ API29이상은 테스트 하지않았습니다.★
         * @param Bitmap bitmap - 저장하고자 하는 이미지의 비트맵
         * @param String fileName - 저장하고자 하는 이미지의 비트맵
         *
         * File storage = 저장이 될 저장소 위치
         *
         * return = 저장된 이미지의 경로
         *
         * 비트맵에 사용될 스토리지와 이름을 지정하고 이미지파일을 생성합니다.
         * FileOutputStream으로 이미지파일에 비트맵을 추가해줍니다.
         */
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        File storage =new File(Environment.getExternalStorageDirectory() + "/.alliance/");
        if (!storage.exists()) storage.mkdirs();
        String fileName = "alliance_mobile_sign_" + soNoValue +"_"+timeStamp + ".png";

        File imgFile = new File(storage, fileName);
        try {
            imgFile.createNewFile();
            OutputStream out = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
            out.flush();
            out.close();






        } catch (FileNotFoundException e) {
            Log.e("saveBitmapToPng","FileNotFoundException : " + e.getMessage());
        } catch (IOException e) {
            Log.e("saveBitmapToPng","IOException : " + e.getMessage());
        }
        //Log.d("imgPath" , imgFile.getAbsolutePath());
        return imgFile;
    }








    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        yesCmplCancel();
    }

    int sendFileCnt = 0;    //전송할 파일 갯수
    int sendProcCnt = 0;    //전송 수행한 파일갯수
    int sendCmplCnt = 0;    //전송 완료한 파일갯수

    //안드로이드 최근 버전에서는 네크워크 통신시에 반드시 스레드를 요구한다.
    class NThread extends Thread{
        public NThread() {
        }
        @Override

        public void run() {


            if(yesPicAddUri[YES_SELECT_NO] != null && !yesPicAddUri[YES_SELECT_NO].getText().toString().equals(""))
            {

                sendFileCnt++;
                File file = new File(yesPicAddUri[YES_SELECT_NO].getText().toString());
                //Log.d("NThread_run_file",file.getAbsolutePath());
                upload(file);
            }

        }

        public void upload(File uploadFile){

            /********** Pick file from memory *******/
            //장치로부터 메모리 주소를 얻어낸 뒤, 파일명을 가지고 찾는다.
            //현재 이것은 내장메모리 루트폴더에 있는 것.
            //File f = new File(Environment.getExternalStorageDirectory()+"/logo2.png");
            File f = uploadFile;
            //Log.d("NThread_upload_file",f.getAbsolutePath());
            // Upload file
            uploadFile(f);
        }
    }


    //사인패드 이미지 저장하기
    //안드로이드 최근 버전에서는 네크워크 통신시에 반드시 스레드를 요구한다.
    class SignNThread extends Thread{
        public SignNThread() {
        }
        @Override

        public void run() {

                if(                signUri != null && signUri.getText() != null && !signUri.getText().toString().equals(""))
                {

                    File file = new File(signUri.getText().toString());
                    //Log.d("SignNThread",file.getAbsolutePath());
                    uploadSign(file);
                }
                else
                {
                    showProgress(false);
                    saveButton.setVisibility(View.INVISIBLE);
                    signaturePad.setEnabled(true);

                    commonHandler.showAlertDialog("서명","업로드할 사인 파일의 경로를 찾을수 없습니다.");
                    return;
                }


        }

        public void uploadSign(File uploadSignFile){

            /********** Pick file from memory *******/
            //장치로부터 메모리 주소를 얻어낸 뒤, 파일명을 가지고 찾는다.
            //현재 이것은 내장메모리 루트폴더에 있는 것.
            //File f = new File(Environment.getExternalStorageDirectory()+"/logo2.png");
            File f = uploadSignFile;
            Log.d("SignNThread_upload_file",f.getAbsolutePath());
            // Upload file
            uploadSignFile(f);
        }
        public void upload(File uploadFile){

            /********** Pick file from memory *******/
            //장치로부터 메모리 주소를 얻어낸 뒤, 파일명을 가지고 찾는다.
            //현재 이것은 내장메모리 루트폴더에 있는 것.
            //File f = new File(Environment.getExternalStorageDirectory()+"/logo2.png");
            File f = uploadFile;
            Log.d("NThread_upload_file",f.getAbsolutePath());
            // Upload file
            uploadFile(f);
        }
    }

    public void uploadFile(File fileName){

        Log.d("NThread_uploadFile_file",fileName.getAbsolutePath());
        FTPClient client = new FTPClient();

        //업로드 할 폴더는 해당 폴더 밑에 년월일 예)  web/20211014/

        String yyyyMMddFolder = new SimpleDateFormat("yyyyMMdd").format(new Date());

        try {
            client.connect(textValueHandler.FTP_HOST,textValueHandler.FTP_PORT);//ftp 서버와 연결, 호스트와 포트를 기입
            client.login(textValueHandler.FTP_USER, textValueHandler.FTP_PASS);//로그인을 위해 아이디와 패스워드 기입
            client.setType(FTPClient.TYPE_BINARY);//2진으로 변경
            client.setPassive(true);
            CheckAndMakeDirectory(client,"/"+textValueHandler.FTP_DIR+yyyyMMddFolder+"/");
            client.upload(fileName, new MyTransferListener());//업로드 시작


            Log.d("fileName" , yyyyMMddFolder +"/"+fileName.getName());
            if(YES_SELECT_NO == 0) {yesPicFileName01.setText(yyyyMMddFolder +"/"+fileName.getName());};
            if(YES_SELECT_NO == 1) {yesPicFileName02.setText(yyyyMMddFolder +"/"+fileName.getName());};
            if(YES_SELECT_NO == 2) {yesPicFileName03.setText(yyyyMMddFolder +"/"+fileName.getName());};
            if(YES_SELECT_NO == 3) {yesPicFileName04.setText(yyyyMMddFolder +"/"+fileName.getName());};
            if(YES_SELECT_NO == 4) {yesPicFileName05.setText(yyyyMMddFolder +"/"+fileName.getName());};
            if(YES_SELECT_NO == 5) {yesPicFileName06.setText(yyyyMMddFolder +"/"+fileName.getName());};

            handler.post(new Runnable() {
                @Override
                public void run() {
                    showProgress(false);
                    mToast.makeText(getApplicationContext(),"이미지 업로드 성공",Toast.LENGTH_SHORT).show();
                    YES_LOCK[YES_SELECT_NO] = 2;   //2는 파일업로드 완료 상태
                    sendCmplCnt++;
                }
            });
        } catch (Exception e) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    showProgress(false);

                    commonHandler.showAlertDialog("이미지 업로드 실패","실패사유\n"+e.getMessage());
                    //이미지세팅 초기화
                    YES_LOCK[YES_SELECT_NO] = 0;
                    yesPicAddImg[YES_SELECT_NO].setImageDrawable(getDrawable(R.mipmap.ic_camera));
                    yesPicAddUri[YES_SELECT_NO].setText("");

                }
            });

            e.printStackTrace();
            try {
                client.disconnect(true);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        sendProcCnt++;
    }



    @SuppressLint("LongLogTag")
    public void uploadSignFile(File signFile){

        Log.d("SignNThread_uploadFile_file",signFile.getAbsolutePath());
        FTPClient client = new FTPClient();

        //업로드 할 폴더는 해당 폴더 밑에 년월일 예)  web/20211014/

        String yyyyMMddFolder = new SimpleDateFormat("yyyyMMdd").format(new Date());

        try {
            client.connect(textValueHandler.FTP_HOST,textValueHandler.FTP_PORT);//ftp 서버와 연결, 호스트와 포트를 기입
            client.login(textValueHandler.FTP_USER, textValueHandler.FTP_PASS);//로그인을 위해 아이디와 패스워드 기입
            client.setType(FTPClient.TYPE_BINARY);//2진으로 변경
            client.setPassive(true);
            CheckAndMakeDirectory(client,"/"+textValueHandler.FTP_DIR+yyyyMMddFolder+"/");
            client.upload(signFile, new MyTransferListener());//업로드 시작


            handler.post(new Runnable() {
                @Override
                public void run() {
                    showProgress(false);
                    mToast.makeText(getApplicationContext(),"사인 업로드 성공",Toast.LENGTH_SHORT).show();

                }
            });

        } catch (Exception e) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    showProgress(false);

                    commonHandler.showAlertDialog("이미지 업로드 실패","실패사유\n"+e.getMessage());
                    //이미지세팅 초기화
                    //signaturePad.clear();
                    signaturePad.setEnabled(true);
                    saveButton.setVisibility(View.INVISIBLE);

                    signFileName.setText("");


                }
            });

            e.printStackTrace();
            try {
                client.disconnect(true);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

    }







    //ftp 해당폴더 없으면 폴더 만들어주는 함수

    public void CheckAndMakeDirectory(FTPClient ftp, String path) throws IOException, FTPIllegalReplyException, FTPException{
        try{
            ftp.changeDirectory(path);
        }catch(FTPException e){
            e.printStackTrace();
            // 없으면 폴더 생성
            ftp.createDirectory(path);
            ftp.changeDirectory(path);
        }
    }



    /*******  Used to file upload and show progress  **********/

    public class MyTransferListener implements FTPDataTransferListener {

        public void started() { //파일전송시작
            handler.post(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        public void transferred(int length) {   //파일전송 진행중
            handler.post(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        public void completed() {   //파일전송 완료
            handler.post(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        public void aborted() {     //파일전송 취소
            handler.post(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        public void failed() {      //파일전송실패
            handler.post(new Runnable() {
                @Override
                public void run() {
                }
            });
        }
    }


    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    Integer noNumCust;  //착불비 Cust 인것의 순번 저장용
    Integer noNumCash;  //착불비 Cash 인것의 순번 저장용
    Integer yangJungNo;  //양중비 N 인것의 순번 저장용
    Integer service02;  //내림서비스 N 인것의 순번 저장용
    Integer service03;  //벽고정->기타 N 인것의 순번 저장용



    private void mYesCmplReasonCombo(YesCmplVO yesCmplVO) {
        service.mYesCmplReasonCombo(yesCmplVO).enqueue(new Callback<List<YesCmplVO>>() {

            @Override
            public void onResponse(Call<List<YesCmplVO>> call, Response<List<YesCmplVO>> response) {

                if(response.isSuccessful()) //응답값이 있다
                {
                    List<YesCmplVO> result = response.body();

                    if(result.size()+1 > 1)
                    {

                        //착불비
                        yesCmplRcptCostRadio       = new RadioButton[result.size()+1];        //사유선택
                        yesCmplRcptCostCode     = new TextView[result.size()+1];        //사유 코드


                        yesCmplRcptCostEtc       = new EditText[result.size()+1];       //내용입력

                        TextView yesCmplRcptCost = new TextView(getApplicationContext());
                        yesCmplRcptCost.setText("착불비");
                        //TableRow tRowRcptCost = new TableRow(getApplicationContext());     //착불비 제목 보이는 부분

                        yesCmplTable.addView(yesCmplRcptCost, new TableLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));


                        //착불비의 사유 2개 고객입금,현장수금 과 없음 을 불러와서 배치함
                        for(int i = 0; i<result.size()+1; i++) {


                            TableRow tRow = new TableRow(getApplicationContext());     // 테이블 ROW 생성

                            yesCmplRcptCostRadio[i] = new RadioButton(getApplicationContext());
                            yesCmplRcptCostRadio[i].setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue_1e6ce3)));
                            yesCmplRcptCostRadio[i].setChecked(true);
                            yesCmplRcptCostRadio[i].setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                            yesCmplRcptCostRadio[i].setChecked(false);


                            yesCmplRcptCostCode[i] = new TextView(getApplicationContext());
                            yesCmplRcptCostCode[i].setVisibility(View.GONE);
                            yesCmplRcptCostEtc[i] = new EditText(getApplicationContext());
                            yesCmplRcptCostEtc[i].setOnKeyListener(new View.OnKeyListener() {

                                @Override
                                public boolean onKey(View v, int keyCode, KeyEvent event) {
                                    if (keyCode == event.KEYCODE_ENTER)
                                        return true;
                                    return false;
                                }
                            });


                            if(i != result.size()){
                                yesCmplRcptCostRadio[i].setText(result.get(i).getComboNm());
                                yesCmplRcptCostCode[i].setText(result.get(i).getComboCd());
                                yesCmplRcptCostEtc[i].setVisibility(View.GONE);
                            }
                            else
                            {
                                yesCmplRcptCostRadio[i].setText("없음");
                                yesCmplRcptCostCode[i].setText("N");
                                yesCmplRcptCostEtc[i].setVisibility(View.GONE);
                            }

                            if(yesCmplRcptCostCode[i].getText().equals("CUST"))
                            {
                                noNumCust = i;

                            }
                            if(yesCmplRcptCostCode[i].getText().equals("CASH"))
                            {
                                noNumCash = i;

                            }



                            int finalI = i;



                            yesCmplRcptCostRadio[i].setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v){
                                   yesCmplRcptCostRadio[finalI].setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue_1e6ce3)));

                                   for (int j = 0; j < result.size()+1; j++) {

                                       if(finalI != j){
                                           yesCmplRcptCostRadio[j].setChecked(false);
                                           yesCmplRcptCostRadio[j].setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));

                                       }

                                   }

                                   Log.d("yesCmplRcptCostEtc",yesCmplRcptCostCode[finalI].getText().toString());
                                       //고객입금 - CUST, 또는 현장수금 - CASH 를 선택했을때 입력란을 보여준다다
                                      if(yesCmplRcptCostCode[finalI].getText().equals("CUST"))
                                       {

                                           Integer maxLength = 10;
                                           yesCmplRcptCostEtc[finalI].setText("");
                                           yesCmplRcptCostEtc[finalI].setVisibility(View.VISIBLE);
                                           //입력란 활성화
                                           setUseableEditText(yesCmplRcptCostEtc[finalI],true);

                                           yesCmplRcptCostEtc[finalI].setHint("내용입력(최대10자)");
                                           yesCmplRcptCostEtc[finalI].setMaxLines(1);

                                           yesCmplRcptCostEtc[finalI].setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
                                           yesCmplRcptCostEtc[finalI].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_gray_detail_white_btn));



                                           yesCmplRcptCostEtc[noNumCash].setText("");
                                           setUseableEditText(yesCmplRcptCostEtc[noNumCash],false);
                                           yesCmplRcptCostEtc[noNumCash].setVisibility(View.INVISIBLE);



                                       }
                                       else if(yesCmplRcptCostCode[finalI].getText().equals("CASH"))
                                       {

                                           Integer maxLength = 10;
                                           yesCmplRcptCostEtc[finalI].setText("");
                                           yesCmplRcptCostEtc[finalI].setVisibility(View.VISIBLE);
                                           //입력란 활성화
                                           setUseableEditText(yesCmplRcptCostEtc[finalI],true);

                                           yesCmplRcptCostEtc[finalI].setHint("내용입력(최대10자)");
                                           yesCmplRcptCostEtc[finalI].setMaxLines(1);

                                           yesCmplRcptCostEtc[finalI].setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
                                           yesCmplRcptCostEtc[finalI].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_gray_detail_white_btn));



                                           yesCmplRcptCostEtc[noNumCust].setText("");
                                           setUseableEditText(yesCmplRcptCostEtc[noNumCust],false);
                                           yesCmplRcptCostEtc[noNumCust].setVisibility(View.INVISIBLE);




                                       }
                                       else
                                       {
                                           yesCmplRcptCostEtc[noNumCust].setText("");
                                           setUseableEditText(yesCmplRcptCostEtc[noNumCust],false);
                                           yesCmplRcptCostEtc[noNumCust].setVisibility(View.INVISIBLE);

                                           yesCmplRcptCostEtc[noNumCash].setText("");
                                           setUseableEditText(yesCmplRcptCostEtc[noNumCash],false);
                                           yesCmplRcptCostEtc[noNumCash].setVisibility(View.INVISIBLE);
                                       }

                               }
                            });
                            tRow.addView(yesCmplRcptCostRadio[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            tRow.addView(yesCmplRcptCostCode[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            tRow.addView(yesCmplRcptCostEtc[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                            yesCmplTable.addView(tRow, new TableLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                        }




                        //양중비 제목 보이는 부분
                        TextView yesYangJungCost = new TextView(getApplicationContext());
                        yesYangJungCost.setText("\n양중비");
                        //TableRow tRowYangJungCost = new TableRow(getApplicationContext());     //양중비비 제목 보이는 부분

                        yesCmplTable.addView(yesYangJungCost, new TableLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));

                        yesCmplYangJungRadio       = new RadioButton[2];        //사유선택
                        yesCmplYangJungCode     = new TextView[2];        //사유 코드
                        yesCmplYangJungEtc       = new EditText[2];       //내용입력

                        for(int i = 0; i<2; i++) { //있음 없음 2개뿐
                            TableRow tRow = new TableRow(getApplicationContext());     // 테이블 ROW 생성

                            yesCmplYangJungRadio[i] = new RadioButton(getApplicationContext());

                            yesCmplYangJungRadio[i].setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue_1e6ce3)));
                            yesCmplYangJungRadio[i].setChecked(true);
                            yesCmplYangJungRadio[i].setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                            yesCmplYangJungRadio[i].setChecked(false);

                            yesCmplYangJungCode[i] = new TextView(getApplicationContext());
                            yesCmplYangJungCode[i].setVisibility(View.GONE);
                            yesCmplYangJungEtc[i] = new EditText(getApplicationContext());


                            yesCmplYangJungEtc[i].setOnKeyListener(new View.OnKeyListener() {

                                @Override
                                public boolean onKey(View v, int keyCode, KeyEvent event) {
                                    if (keyCode == event.KEYCODE_ENTER)
                                        return true;
                                    return false;
                                }
                            });




                            if(i==0) //있음
                            {
                                yesCmplYangJungRadio[i].setText("있음");
                                yesCmplYangJungCode[i].setText("Y");
                                yesCmplYangJungEtc[i].setVisibility(View.GONE);
                            }
                            else if(i==1)
                            {
                                yesCmplYangJungRadio[i].setText("없음");
                                yesCmplYangJungCode[i].setText("N");
                                yesCmplYangJungEtc[i].setVisibility(View.GONE);
                            }


                            if(yesCmplYangJungCode[i].getText().equals("Y"))    //있음
                            {
                                yangJungNo = i;
                            }

                            int finalI = i;

                            yesCmplYangJungRadio[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v){
                                    yesCmplYangJungRadio[finalI].setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue_1e6ce3)));
                                    for (int j = 0; j <2; j++) {
                                        if(finalI != j){
                                            yesCmplYangJungRadio[j].setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                                            yesCmplYangJungRadio[j].setChecked(false);
                                        }
                                    }

                                    //있음 Y의 입력란을 선택 하면
                                    if(yesCmplYangJungCode[finalI].getText().equals("Y"))
                                    {
                                        Integer maxLength = 10;
                                        yesCmplYangJungEtc[finalI].setText("");
                                        yesCmplYangJungEtc[finalI].setVisibility(View.VISIBLE);
                                        //입력란 활성화
                                        setUseableEditText(yesCmplYangJungEtc[finalI],true);
                                        yesCmplYangJungEtc[finalI].setHint("내용입력(최대10자)");
                                        yesCmplYangJungEtc[finalI].setMaxLines(1);
                                        yesCmplYangJungEtc[finalI].setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
                                        yesCmplYangJungEtc[finalI].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_gray_detail_white_btn));
                                    }
                                    else
                                    {
                                        //있음 Y의 입력란 안보이게 가림
                                        yesCmplYangJungEtc[yangJungNo].setText("");
                                        //입력란 비활성화
                                        setUseableEditText(yesCmplYangJungEtc[yangJungNo],false);
                                        yesCmplYangJungEtc[yangJungNo].setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                            tRow.addView(yesCmplYangJungRadio[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            tRow.addView(yesCmplYangJungCode[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            tRow.addView(yesCmplYangJungEtc[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                            yesCmplTable.addView(tRow, new TableLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                        }


                        //내림서비스 제목 보이는 부분
                        TextView yesCmplService02Cost = new TextView(getApplicationContext());
                        yesCmplService02Cost.setText("\n내림서비스");
                        //TableRow tRowService02Cost = new TableRow(getApplicationContext());

                        yesCmplTable.addView(yesCmplService02Cost, new TableLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));

                        yesCmplService02Radio       = new RadioButton[2];        //사유선택
                        yesCmplService02Code     = new TextView[2];        //사유 코드
                        yesCmplService02Etc       = new EditText[2];       //내용입력

                        for(int i = 0; i<2; i++) { //있음 없음 2개뿐
                            TableRow tRow = new TableRow(getApplicationContext());     // 테이블 ROW 생성

                            yesCmplService02Radio[i] = new RadioButton(getApplicationContext());

                            yesCmplService02Radio[i].setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue_1e6ce3)));
                            yesCmplService02Radio[i].setChecked(true);
                            yesCmplService02Radio[i].setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                            yesCmplService02Radio[i].setChecked(false);



                            yesCmplService02Code[i] = new TextView(getApplicationContext());
                            yesCmplService02Code[i].setVisibility(View.GONE);
                            yesCmplService02Etc[i] = new EditText(getApplicationContext());


                            yesCmplService02Etc[i].setOnKeyListener(new View.OnKeyListener() {

                                @Override
                                public boolean onKey(View v, int keyCode, KeyEvent event) {
                                    if (keyCode == event.KEYCODE_ENTER)
                                        return true;
                                    return false;
                                }
                            });




                            if(i==0) //있음
                            {
                                yesCmplService02Radio[i].setText("있음");
                                yesCmplService02Code[i].setText("Y");
                                yesCmplService02Etc[i].setVisibility(View.GONE);
                            }
                            else if(i==1)
                            {
                                yesCmplService02Radio[i].setText("없음");
                                yesCmplService02Code[i].setText("N");
                                yesCmplService02Etc[i].setVisibility(View.GONE);
                            }


                            if(yesCmplService02Code[i].getText().equals("Y"))   //있음
                            {
                                service02 = i;
                            }

                            int finalI = i;

                            yesCmplService02Radio[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v){
                                    yesCmplService02Radio[finalI].setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue_1e6ce3)));
                                    for (int j = 0; j < 2; j++) {
                                        if(finalI != j){
                                            yesCmplService02Radio[j].setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                                            yesCmplService02Radio[j].setChecked(false);
                                        }
                                    }

                                    //있음 Y의 입력란을 선택 하면
                                    if(yesCmplService02Code[finalI].getText().equals("Y"))
                                    {
                                        Integer maxLength = 10;
                                        yesCmplService02Etc[finalI].setText("");
                                        yesCmplService02Etc[finalI].setVisibility(View.VISIBLE);
                                        //입력란 활성화
                                        setUseableEditText(yesCmplService02Etc[finalI],true);
                                        yesCmplService02Etc[finalI].setHint("내용입력(최대10자)");
                                        yesCmplService02Etc[finalI].setMaxLines(1);
                                        yesCmplService02Etc[finalI].setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
                                        yesCmplService02Etc[finalI].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_gray_detail_white_btn));
                                    }
                                    else
                                    {
                                        //있음 Y의 입력란 안보이게 가림
                                        yesCmplService02Etc[service02].setText("");
                                        //입력란 비활성화
                                        setUseableEditText(yesCmplService02Etc[service02],false);
                                        yesCmplService02Etc[service02].setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                            tRow.addView(yesCmplService02Radio[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            tRow.addView(yesCmplService02Code[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            tRow.addView(yesCmplService02Etc[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                            yesCmplTable.addView(tRow, new TableLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                        }


                        //벽고정->기타 제목 보이는 부분
                        TextView yesCmplService03Cost = new TextView(getApplicationContext());
                        yesCmplService03Cost.setText("\n기타");
                        //TableRow tRowService03Cost = new TableRow(getApplicationContext());

                        yesCmplTable.addView(yesCmplService03Cost, new TableLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));

                        yesCmplService03Radio       = new RadioButton[2];        //사유선택
                        yesCmplService03Code     = new TextView[2];        //사유 코드
                        yesCmplService03Etc       = new EditText[2];       //내용입력

                        for(int i = 0; i<2; i++) { //있음 없음 2개뿐
                            TableRow tRow = new TableRow(getApplicationContext());     // 테이블 ROW 생성

                            yesCmplService03Radio[i] = new RadioButton(getApplicationContext());

                            yesCmplService03Radio[i].setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue_1e6ce3)));
                            yesCmplService03Radio[i].setChecked(true);
                            yesCmplService03Radio[i].setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                            yesCmplService03Radio[i].setChecked(false);


                            yesCmplService03Code[i] = new TextView(getApplicationContext());
                            yesCmplService03Code[i].setVisibility(View.GONE);
                            yesCmplService03Etc[i] = new EditText(getApplicationContext());


                            yesCmplService03Etc[i].setOnKeyListener(new View.OnKeyListener() {

                                @Override
                                public boolean onKey(View v, int keyCode, KeyEvent event) {
                                    if (keyCode == event.KEYCODE_ENTER)
                                        return true;
                                    return false;
                                }
                            });




                            if(i==0) //있음
                            {
                                yesCmplService03Radio[i].setText("있음");
                                yesCmplService03Code[i].setText("Y");
                                yesCmplService03Etc[i].setVisibility(View.GONE);
                            }
                            else if(i==1)
                            {
                                yesCmplService03Radio[i].setText("없음");
                                yesCmplService03Code[i].setText("N");
                                yesCmplService03Etc[i].setVisibility(View.GONE);
                            }


                            if(yesCmplService03Code[i].getText().equals("Y"))   //있음 Y
                            {
                                service03 = i;
                            }

                            int finalI = i;

                            yesCmplService03Radio[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v){
                                    yesCmplService03Radio[finalI].setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue_1e6ce3)));
                                    for (int j = 0; j < 2; j++) {
                                        if(finalI != j){
                                            yesCmplService03Radio[j].setChecked(false);
                                            yesCmplService03Radio[j].setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                                        }
                                    }

                                    //있음 Y의 입력란을 선택 하면
                                    if(yesCmplService03Code[finalI].getText().equals("Y"))
                                    {

                                        Integer maxLength = 10;
                                        yesCmplService03Etc[finalI].setText("");
                                        yesCmplService03Etc[finalI].setVisibility(View.VISIBLE);
                                        //입력란 활성화
                                        setUseableEditText(yesCmplService03Etc[finalI],true);
                                        yesCmplService03Etc[finalI].setHint("내용입력(최대10자)");
                                        yesCmplService03Etc[finalI].setMaxLines(1);
                                        yesCmplService03Etc[finalI].setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
                                        yesCmplService03Etc[finalI].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_gray_detail_white_btn));
                                    }
                                    else
                                    {
                                        //있음 Y의 입력란 안보이게 가림
                                        yesCmplService03Etc[service03].setText("");
                                        //입력란 비활성화
                                        setUseableEditText(yesCmplService03Etc[service03],false);
                                        yesCmplService03Etc[service03].setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                            tRow.addView(yesCmplService03Radio[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            tRow.addView(yesCmplService03Code[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            tRow.addView(yesCmplService03Etc[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                            yesCmplTable.addView(tRow, new TableLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                        }
                    }
                    else
                    {
                        commonHandler.showAlertDialog("배송완료 특이사항 조회 실패", "조회 0건");
                    }
                    showProgress(false);
                }
                else{
                    commonHandler.showAlertDialog("배송완료 특이사항 조회 실패","응답결과가 없습니다.");
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<List<YesCmplVO>> call, Throwable t) {
                commonHandler.showAlertDialog("배송완료 특이사항 조회 실패","접속실패\n"+t.getMessage());
                showProgress(false);
            }
        });
    }





    YesCmplVO onCreateData;  //화면 열리자마자 불러온데이터 여기에 넣기(여기 넣었다가 나중에 배송완료 처리 완료 버튼 누를때 알림톡 발송 보냄)
    //화면 열리자 마자 불러오기
    private void mYesCmplOnCreate(YesCmplOnCreateVO yesCmplOnCreateVO) {
        service.mYesCmplOnCreate(yesCmplOnCreateVO).enqueue(new Callback<YesCmplVO>() {

            @Override
            public void onResponse(Call<YesCmplVO> call, Response<YesCmplVO> response) {

                if(response.isSuccessful()) //응답값이 있다
                {
                    YesCmplVO result = response.body();
                    onCreateData = result;
                    Log.d("onCreateData",onCreateData.toString());
                }
                else{

                    commonHandler.showFinishAlertDialog("배송완료 onCreate 데이터 조회 실패","응답결과가 없거나 여러개 입니다.\n[instMobileMId : "+instMobileMIdValue+"]\n이전화면으로 이동합니다","Y");
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<YesCmplVO> call, Throwable t) {

                commonHandler.showFinishAlertDialog("배송완료 onCreate 데이터 조회 실패","접속실패\n"+t.getMessage(),"Y");
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

    private void yesCmplCancel(){

        View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(YesCmplActivity.this);
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




    //배송완료알림톡 결과를 DB에 저장
    public void YesCmplTalkDb(String sendComplete, String message)
    {
        mYesCmplSaveTalk(new YesCmplSaveTalkVO(

                onCreateData.getInstMobileMId()
                , sendComplete
                , onCreateData.getAlrmTalkUserid()
                , onCreateData.getMessageType()
                , sharePref.getString("PhoneNum","")    //onCreateData.getPhn() //알림톡 받는사람 전화번호
                , onCreateData.getProfile()
                , "00000000000000"
                , completeMsg
                , onCreateData.getSmslmstit()
                , onCreateData.getTmplid()
                , message
                , sharePref.getString("tblUserMId", "")

        ));
        showProgress(true);
    }
    //배송완료 결과를 DB에 저장
    public void YesCmplSaveStatDb()
    {
        //배송완료 처리 데이터 저장 SP - 배송완료 저장은 배송완료 저장대로 따로 돌림
        mYesCmplSaveStat(new YesCmplSaveStatVO(
                   instMobileMIdValue
                ,   finalYesCmplRcptCostSetCode      //
                ,   finalYesCmplRcptCostSetEtc        //
                ,   finalYesCmplYangJungSetCode        //양중
                ,   finalYesCmplYangJungSetEtc        //
                ,   finalYesCmplService02SetCode        //내림서비스
                ,   finalYesCmplService02SetEtc        //
                ,   finalYesCmplService03SetCode         //벽고정->기타
                ,   finalYesCmplService03SetEtc        //
                ,   finalYesCmplSelectMemo                //
                ,   signFileName.getText().toString()
                ,   yesPicFileName01.getText().toString()
                ,   yesPicFileName02.getText().toString()              //
                ,   yesPicFileName03.getText().toString()             //
                ,   yesPicFileName04.getText().toString()            //
                ,   yesPicFileName05.getText().toString()            //
                ,   yesPicFileName06.getText().toString()           //
                ,   ""
                ,   ""
                ,   ""
                ,   ""
                ,   sharePref.getString("tblUserMId","")

        ));
    }
    //배송완료 알림톡 보내기
    public void YesCmplSendTalk(){
        completeMsg =onCreateData.getAlrmTalkTmp();

        //알림톡 발송 알림톡 발송
        ArrayList<SendTalkVO> send = new ArrayList<SendTalkVO>();
        send.add(new SendTalkVO(
                onCreateData.getMessageType()                   //알림톡 발송유형
                //0505hphphphp
                , sharePref.getString("PhoneNum", "")   //onCreateData.getPhn()   //알림톡 받는사람 전화번호
                , onCreateData.getProfile()                        //알림톡 프로필 아이디
                , "00000000000000"             //발송시간 0 14개 : 즉시발송
                , onCreateData.getTmplid()         //알림톡 템플릿아이디
                , completeMsg      //알림톡 보낼 메세지
                , onCreateData.getSmskind()         //알림톡 실패시 문자 발송 종류
                , completeMsg          //알림톡 실패시 문자로 보낼 메세지
                , onCreateData.getSmssender()         //알림톡 실패시 문자 발송할때 발송자 전화번호
                , onCreateData.getSmslmstit()            //알림톡 실패시 문자발송할때 LMS 의 제목
                , onCreateData.getSmsonly()        //알림톡 실패시 문자를 보낼것인가 여부
                ,   null
        ));
        mSendTalk(send);    // 알림톡 발송하는 부분
    }

    //알림톡 발송
    private void mSendTalk(ArrayList<SendTalkVO> sendTalkVO) {    //요청 VO

        serviceTalk.mSendTalk(sendTalkVO).enqueue(new Callback<ArrayList<TalkVO>>() {    //앞 요청VO, CallBack 응답 VO

            @Override
            public void onResponse(Call<ArrayList<TalkVO>> call, Response<ArrayList<TalkVO>> response) {  //둘다 응답 VO

                if(response.isSuccessful() ) //응답값이 있다
                {
                    //알림톡 결과를 받은후에
                    List<TalkVO> resultSendTalk  = response.body();

                    //// 2. 상태값과 상관없이   알림톡을 발송했고 그 결과가 success 일때 (발송 성공일때) 알림톡 발송 내역을 저장(결과코드를 포함하여저장)
                    YesCmplTalkDb(resultSendTalk.get(0).getCode(), resultSendTalk.get(0).getMessage());
                }
                else{
                    YesCmplTalkDb("N","onResponse_noResult");
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<ArrayList<TalkVO>> call, Throwable t) {

                YesCmplTalkDb("N","onFailure");

                showProgress(false);
            }
        });
    }

    //배송완료 처리 버튼 눌렀을떄 (SAVE)
    private void mYesCmplSaveStat(YesCmplSaveStatVO yesCmplSaveStatVO) {
        service.mYesCmplSaveStat(yesCmplSaveStatVO).enqueue(new Callback<YesCmplVO>() {

            @Override
            public void onResponse(Call<YesCmplVO> call, Response<YesCmplVO> response) {

                if(response.isSuccessful()) //응답값이 있다
                {
                    YesCmplVO result = response.body();
                    if(result.getRtnYn().equals("Y"))
                    {
                        //배송완료 처리 저장 결과가 Y 일경우 배송완료 알림톡을 발송함
                        YesCmplSendTalk();    //배송완료 알림톡 발송하기
                    }
                    else
                    {
                        View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                        AlertDialog.Builder builder = new AlertDialog.Builder(YesCmplActivity.this);
                        builder.setView(dialogView);

                        AlertDialog alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();

                        Button ok_btn = dialogView.findViewById(R.id.successBtn);
                        TextView ok_txt = dialogView.findViewById(R.id.successText);
                        ok_txt.setText("배송완료 처리 실패\n\n"+result.getRtnMsg() == null ? "처리 실패 " : result.getRtnMsg());
                        ok_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });
                    }
                    showProgress(false);
                }
                else{

                    View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                    AlertDialog.Builder builder = new AlertDialog.Builder(YesCmplActivity.this);
                    builder.setView(dialogView);

                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                    Button ok_btn = dialogView.findViewById(R.id.successBtn);
                    TextView ok_txt = dialogView.findViewById(R.id.successText);
                    ok_txt.setText("배송완료 처리 실패\n\n응답결과가 없음");
                    ok_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<YesCmplVO> call, Throwable t) {

                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(YesCmplActivity.this);
                builder.setView(dialogView);

                AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();

                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                TextView ok_txt = dialogView.findViewById(R.id.successText);
                ok_txt.setText("배송완료 처리 실패\n\n접속실패\n"+t.getMessage());
                ok_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                showProgress(false);
            }
        });
    }




    //배송완료 알림톡 발송 결과를 저장하기. 그리고 알림톡 발송 결과 저장이 되던 안되던 다이얼로그는 됐다라고 나옴

    private void mYesCmplSaveTalk(YesCmplSaveTalkVO yesCmplSaveTalkVO){
        service.mYesCmplSaveTalk(yesCmplSaveTalkVO).enqueue(new Callback<YesCmplVO>() {

            @Override public void onResponse(Call<YesCmplVO> call, Response<YesCmplVO> response){
                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);
                android.app.AlertDialog.Builder builder =
                        new android.app.AlertDialog.Builder(YesCmplActivity.this);
                builder.setView(dialogView);
                android.app.AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                TextView ok_txt = dialogView.findViewById(R.id.successText);
                ok_txt.setText("배송완료");
                ok_btn.setOnClickListener(new View.OnClickListener() {
                    @SneakyThrows @Override public void onClick(View v){
                        alertDialog.dismiss();
                        //배송목록 갱신하기
                        DeliveryListFragment deliveryListFragment = (DeliveryListFragment)DeliveryListFragment._DeliveryListFragment;
                        deliveryListFragment.changeDate();


                        //상세정보보기 다시 정보 읽기

                        DeliveryListDetail deliveryListDetail = (DeliveryListDetail)DeliveryListDetail._DeliveryListDetail;

                        deliveryListDetail.deliveryDetailSrch();
                        finish();
                    }
                });
            }

            @Override public void onFailure(Call<YesCmplVO> call, Throwable t){
                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);
                android.app.AlertDialog.Builder builder =
                        new android.app.AlertDialog.Builder(YesCmplActivity.this);
                builder.setView(dialogView);
                android.app.AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                TextView ok_txt = dialogView.findViewById(R.id.successText);
                ok_txt.setText("배송완료");
                ok_btn.setOnClickListener(new View.OnClickListener() {
                    @SneakyThrows @Override public void onClick(View v){
                        alertDialog.dismiss();
                        //배송목록 갱신하기
                        DeliveryListFragment deliveryListFragment = (DeliveryListFragment)DeliveryListFragment._DeliveryListFragment;
                        deliveryListFragment.changeDate();


                        //상세정보보기 다시 정보 읽기

                        DeliveryListDetail deliveryListDetail = (DeliveryListDetail)DeliveryListDetail._DeliveryListDetail;

                        deliveryListDetail.deliveryDetailSrch();
                        finish();
                    }
                });
            }
        });
    };
}