package com.mobile.alliance.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
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
import com.mobile.alliance.entity.noCmpl.NoCmplOnCreateVO;
import com.mobile.alliance.entity.noCmpl.NoCmplSaveStatVO;
import com.mobile.alliance.entity.noCmpl.NoCmplSaveTalkVO;
import com.mobile.alliance.entity.noCmpl.NoCmplVO;
import com.mobile.alliance.entity.sendTalk.SendTalkVO;
import com.mobile.alliance.entity.sendTalk.TalkVO;
import com.mobile.alliance.fragment.DeliveryListFragment;
import com.mobile.alliance.util.ImageResizeUtils;

import org.w3c.dom.Text;

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

public class NoCmplActivity extends AppCompatActivity {
    public static Toast mToast;
    String completeMsg="";
    final int PICTURE_REQUEST_CODE = 100;


    private static TextValueHandler textValueHandler = new TextValueHandler();
    private String TAG = "NoCmplActivity";
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





    ImageView picAddImg01;
    ImageView picAddImg02;
    ImageView picAddImg03;
    ImageView picAddImg04;
    ImageView picAddImg05;
    ImageView picAddImg06;

    TextView picAddUri01;
    TextView picAddUri02;
    TextView picAddUri03;
    TextView picAddUri04;
    TextView picAddUri05;
    TextView picAddUri06;

    TextView picFileName01;
    TextView picFileName02;
    TextView picFileName03;
    TextView picFileName04;
    TextView picFileName05;
    TextView picFileName06;

    View picAddView;
    ImageView noCmplBack;

    Button  sendFileFtp;


    RadioGroup noCmplReasonGroup;

    TableLayout noCmplTable;
    Button noCmplBtn;



    RadioButton noCmplRadio[];
    TextView    noCmplReasonCode[];
    EditText    noCmplReasonEtc[];

    EditText noCmplMemo;
    SignaturePad noSignaturePad;
    Button noSaveButton, noClearButton;
    TextView noSignUri,    noSignFileName;
    //(최대 6장)
    TextView max6;

    public static Integer[]     LOCK = {0,0,0,0,0,0};       //0 해제 , 1 이미지 선택완료 , 2 파일서버 전송 완료
    public static Integer       SELECT_NO = 0;
    public static ImageView[]   picAddImg;
    public static TextView[]    picAddUri;
    public static TextView[]    picFileName;

    /**
     *  변수설정
     */

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;
    private Boolean isCamera = false;
    private File tempFile;
    String instMobileMIdValue;
    String soNoValue;




    String finalNoCmplSelectCode;
    String finalNoCmplSelectName;
    String finalNoCmplSelectEtc ;
    String finalNoCmplSelectMemo;

    String noCmplSelectCode ;       //선택한 미마감사유의 코드
    String noCmplSelectName ;//선택한 미마감사유의 명칭
    String noCmplSelectEtc  ;        //선택이 ETC 였을때 입력한 문구
    String noCmplSelectMemo ;       //입력한 메모
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_cmpl);

        LOCK = new Integer[]{0, 0, 0, 0, 0, 0};
        SELECT_NO = 0;



































        noSignaturePad = (SignaturePad) findViewById(R.id.noSignaturePad);
        noSaveButton = (Button)findViewById(R.id.noSaveButton);
        noClearButton = (Button)findViewById(R.id.noClearButton);
        noSignUri = (TextView)    findViewById(R.id.noSignUri);
        noSignFileName = (TextView)   findViewById(R.id.noSignFleName) ;



        noSaveButton.setVisibility(View.INVISIBLE);
        noClearButton.setVisibility(View.INVISIBLE);
        noSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {

            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched
            }

            @Override
            public void onSigned() {
                //Event triggered when the pad is signed
                //noSaveButton.setEnabled(true);
                //noClearButton.setEnabled(true);
                noSaveButton.setVisibility(View.VISIBLE);
                noClearButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onClear() {
                //Event triggered when the pad is cleared
                //noSaveButton.setEnabled(false);
                //noClearButton.setEnabled(false);
                noSaveButton.setVisibility(View.INVISIBLE);
                noClearButton.setVisibility(View.INVISIBLE);
            }
        });

        noSaveButton.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows @Override
            public void onClick(View v) {
                //write code for saving the signature here
                //Toast.makeText(YesCmplActivity.this, "Signature Saved", Toast.LENGTH_SHORT).show();

                //signSetImage();
                noSaveButton.setVisibility(View.INVISIBLE);

                File signFile =   saveBitmapToPng(noSignaturePad.getSignatureBitmap());

                String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());

                noSignaturePad.setEnabled(false);
                noSignUri.setText(signFile.getAbsolutePath());
                noSignFileName.setText(timeStamp +"/"+signFile.getName());

                showProgress(true);
                //이미지 뷰에 이미지 넣은다음 바로 서버로 전송한다
                NoCmplActivity.SignNThread signNThread = new NoCmplActivity.SignNThread();
                signNThread.start();
            }
        });

        noClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noSignaturePad.clear();
                noSignaturePad.setEnabled(true);
            }
        });






























        instMobileMIdValue   = getIntent().getStringExtra("instMobileMId");
        soNoValue            = getIntent().getStringExtra("soNo");
        //내부에 데이터 저장하는것
        sharePref = getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        editor = sharePref.edit();

        //진행바
        mProgressView = (ProgressBar) findViewById(R.id.noCmplProgress);
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

        serviceTalk = RetrofitClient.getTalk(client).create(ServiceApi.class);  //알림톡용
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


        //화면 뜨자 마자 내용 부름
        mNoCmplOnCreate(new NoCmplOnCreateVO(   instMobileMIdValue  ));
        showProgress(true);





        noCmplTable = (TableLayout)findViewById(R.id.noCmplTable);

        noCmplBack = (ImageView)findViewById(R.id.noCmplBack);
        noCmplBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                noCmplCancel();
            }
        });


       /* sendFileFtp = (Button) findViewById(R.id.sendFileFtp);
        //사진전송버튼을 눌렀을때
        sendFileFtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NThread nThread = new NThread();
                nThread.start();
            }
        });*/



        noCmplReasonGroup = (RadioGroup)findViewById(R.id.noCmplReasonGroup);
        noCmplBtn = (Button) findViewById(R.id.noCmplBtn);
        noCmplBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_gray_detail_btn));
        noCmplBtn.setTextColor(getResources().getColorStateList(R.color.gray_808080));
        noCmplBtn.setEnabled(false);

        noCmplMemo = (EditText)findViewById(R.id.noCmplMemo);

        max6 = (TextView) findViewById(R.id.max6);  //화면에 (최대 6장) 부분

        picAddView = (View)findViewById(R.id.picAddView);
        picAddUri01 = (TextView)findViewById(R.id.picAddUri01);
        picAddUri02 = (TextView)findViewById(R.id.picAddUri02);
        picAddUri03 = (TextView)findViewById(R.id.picAddUri03);
        picAddUri04 = (TextView)findViewById(R.id.picAddUri04);
        picAddUri05 = (TextView)findViewById(R.id.picAddUri05);
        picAddUri06 = (TextView)findViewById(R.id.picAddUri06);

        picFileName01 = (TextView)findViewById(R.id.picFileName01);
        picFileName02 = (TextView)findViewById(R.id.picFileName02);
        picFileName03 = (TextView)findViewById(R.id.picFileName03);
        picFileName04 = (TextView)findViewById(R.id.picFileName04);
        picFileName05 = (TextView)findViewById(R.id.picFileName05);
        picFileName06 = (TextView)findViewById(R.id.picFileName06);


        picAddImg01 = (ImageView)findViewById(R.id.picAddImg01);
        picAddImg02 = (ImageView)findViewById(R.id.picAddImg02);
        picAddImg03 = (ImageView)findViewById(R.id.picAddImg03);
        picAddImg04 = (ImageView)findViewById(R.id.picAddImg04);
        picAddImg05 = (ImageView)findViewById(R.id.picAddImg05);
        picAddImg06 = (ImageView)findViewById(R.id.picAddImg06);

        picAddImg = new ImageView[]{picAddImg01, picAddImg02, picAddImg03, picAddImg04, picAddImg05,
                picAddImg06};
        picAddUri = new TextView[]{picAddUri01, picAddUri02, picAddUri03, picAddUri04, picAddUri05,
                picAddUri06};
        picFileName = new TextView[]{picFileName01, picFileName02, picFileName03, picFileName04, picFileName05,
                picFileName06};
        for(int i=0;i<6;i++){

            int finalI = i;
            picAddImg[i].setOnClickListener(new ImageView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(LOCK[finalI] == 0)   //해제
                    {
                        try{
                            mToast.cancel();
                        }
                        catch(Exception e)
                        {}
                        //컨펌(버튼 두개중 하나 선택)

                        View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(NoCmplActivity.this);
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
                                SELECT_NO= finalI;
                                takePhoto();

                            }
                        });

                        Button no_btn = dialogView.findViewById(R.id.confirmBtnNo);
                        no_btn.setText("갤러리");
                        no_btn.setOnClickListener(new View.OnClickListener() {
                            @SneakyThrows @Override
                            public void onClick(View v) {

                                alertDialog.dismiss();
                                SELECT_NO= finalI;
                                goToAlbum();
                            }
                        });

                    }
                    else        //1 잠금. -> 이미지가 교체되었다 라는거다. 이때 이 이미지를 터치하면 새로운 Activity 에 이미지,삭제버튼,다운로드, 핀치 투 줌 넣기
                    {
                        if(picAddUri[finalI].getText() != null && !picAddUri[finalI].getText().equals(""))
                        {
                            //Log.d("imageActivity imageUri",picAddUri[finalI].getText().toString());
                            //Log.d("imageActivity imageNo",finalI+"");
                            Intent imageActivity = new Intent(NoCmplActivity.this, ImageActivity.class);
                            imageActivity.putExtra("imageUri",picAddUri[finalI].getText());
                            imageActivity.putExtra("imageNo",finalI+"");
                            imageActivity.putExtra("imageType","delete");
                            imageActivity.putExtra("imageGubn","no");  //미마감

                            startActivity(imageActivity);  //다음 액티비티를 열고
                        }
                        else
                        {
                            //알림 (알림닫는 확인버튼)

                            View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                            AlertDialog.Builder builder = new AlertDialog.Builder(NoCmplActivity.this);
                            builder.setView(dialogView);

                            AlertDialog alertDialog = builder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.show();

                            Button ok_btn = dialogView.findViewById(R.id.successBtn);
                            TextView ok_txt = dialogView.findViewById(R.id.successText);
                            ok_txt.setText("이미지 열기 실패\n\n이미지 경로가 존재하지 않습니다.");
                            ok_btn.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {

                                    alertDialog.dismiss();

                                }
                            });
                        }
                    }
                }
            });
        }



/*********************************
        max6.setOnClickListener(new View.OnClickListener() {
                                         @SuppressLint("LongLogTag,IntentReset") @Override
                                         public void onClick(View v){

                                             Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                                             //사진을 여러개 선택할수 있도록 한다
                                             intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                             intent.setType("image/*");
                                             //startActivityForResult(Intent.createChooser(intent, "Select Picture"),  100);  //PICTURE_REQUEST_CODE  100
                                             startActivityForResult(intent,  PICTURE_REQUEST_CODE);


                                         }



                                     });

************************/
        //미마감 처리 버튼을 눌렀을때
        noCmplBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag") @Override
            public void onClick(View v){

                // 1. 사진 최소 1장 있는지 체크.  LOCK 가 2 인게 1개 이상있어야함

                // 2. 미마감사유가 1개 있어야 함. 그 1개가 ETC 라면 내용입력에 있어야함

                // 3. 내용(메모) 입력되어야함 --> 필수 아님

                //4. 싸인패드에 사인받기
                Integer lock=0;
                for(int q=0;q<LOCK.length;q++)
                {
                    if(LOCK[q] == 2)
                    {
                        lock++;
                    }
                }
                Integer rCheck=0;

                for(int q=0;q<resultCheck.size();q++)
                {
                    //체크 한것 대상
                    if(noCmplRadio[q].isChecked()){
                        //ETC 라면 글까지 써야 체크한것으로 인식
                        if(noCmplReasonCode[q].getText().equals("ETC")){
                            if(noCmplReasonEtc[q].getText().toString().trim().length() > 0){
                                rCheck++;
                            }
                            //ETC 가 아니라면
                        } else {
                            rCheck++;
                        }
                    }
                }
                Integer memoLength=0;
                memoLength = noCmplMemo.getText().toString().trim().length();









                // 7. 싸인패드는 필수

                Integer noSignPad = noSignUri.getText().length();










                if(lock == 0)   //사진을 서버에 전송한게 없으면
                {
                    View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(NoCmplActivity.this);
                    builder.setView(dialogView);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    Button ok_btn = dialogView.findViewById(R.id.successBtn);
                    TextView ok_txt = dialogView.findViewById(R.id.successText);
                    ok_txt.setText("현장사진이 한장도 없습니다.");
                    ok_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    return;
                }
                else if(rCheck == 0){   //미마감 사유 가 1개도 없으면
                    View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(NoCmplActivity.this);
                    builder.setView(dialogView);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    Button ok_btn = dialogView.findViewById(R.id.successBtn);
                    TextView ok_txt = dialogView.findViewById(R.id.successText);
                    ok_txt.setText("미마감 사유를 선택하세요.\n[기타]를 선택했다면 내용을 입력하세요.");
                    ok_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    return;
                }
                //메모는 필수가 아님
                /*else if(memoLength == 0){   //메모를 안썼다면
                    View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(NoCmplActivity.this);
                    builder.setView(dialogView);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    Button ok_btn = dialogView.findViewById(R.id.successBtn);
                    TextView ok_txt = dialogView.findViewById(R.id.successText);
                    ok_txt.setText("메모를 입력하세요");
                    ok_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    return;
                }*/


                else{
                    //서버에 넣을 데이터를 모은다
                    //1 사진은 불러오는 순간 서버에 올리니깐 제외한다
                    //2 미마감 유형중에서 선택한것 고르기
                    for(int q=0;q<resultCheck.size();q++)
                    {
                        //체크 한것 대상
                        if(noCmplRadio[q].isChecked()){
                            noCmplSelectCode = noCmplReasonCode[q].getText().toString();
                            //ETC 라면 글까지 써야 체크한것으로 인식
                            if(noCmplReasonCode[q].getText().equals("ETC")){
                                if(noCmplReasonEtc[q].getText().toString().trim().length() > 0){
                                    noCmplSelectEtc = noCmplReasonEtc[q].getText().toString();
                                }
                            }
                        }
                    }
                    //3 미마감 메모 구하기
                    noCmplSelectMemo = noCmplMemo.getText().toString().trim();


                    finalNoCmplSelectCode = noCmplSelectCode;

                    //반복돌면서 미마감 사유의 코드에 해당하는 명칭을 구한다
                    for(int i=0;i<resultCheck.size();i++){

                        //미마감 사유 코드가 같으면
                        if(resultCheck.get(i).getComboCd().equals(noCmplSelectCode))
                        {
                            noCmplSelectName = resultCheck.get(i).getComboNm();
                        }
                    }
                    finalNoCmplSelectName = noCmplSelectName;
                    finalNoCmplSelectEtc = noCmplSelectEtc;
                    finalNoCmplSelectMemo = noCmplSelectMemo;

                    //미마감 처리를 하시겠습니까 다이얼로그 띄우기 아니오/예

                    //컨펌(버튼 두개중 하나 선택)

                    View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(NoCmplActivity.this);
                    builder.setView(dialogView);

                    android.app.AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                    Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
                    TextView ok_txt = dialogView.findViewById(R.id.confirmText);
                    ok_txt.setTextSize(20);
                    ok_txt.setTypeface(null, Typeface.BOLD);
                    ok_txt.setText("미마감 처리를 하시겠습니까?");
                    ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    ok_btn.setOnClickListener(new View.OnClickListener() {
                        @SneakyThrows @Override
                        public void onClick(View v) {



                            alertDialog.dismiss();
                            //1. 미마감 처리 저장하기
                            //성공    1-1. 미마감 알림톡 발송 1-1-1 알림톡발송성공 : 알림톡 결과저장
                            //                            1-1-2 알림톡발송실패 : 알림톡 결과저장
                            //2.미마감 처리 저장 실패


                            //1. 미마감 처리 저장하기
                            showProgress(true);
                            XCmplSaveStatDb();

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

        if( requestCode== PICTURE_REQUEST_CODE)     //이미지 다중선택
        {
            //Log.d("requestCode",requestCode+"");
            if (resultCode == RESULT_OK)
            {

                //기존 이미지 지우기
                picAddImg01.setImageResource(0);
                picAddImg02.setImageResource(0);
                picAddImg03.setImageResource(0);
                picAddImg04.setImageResource(0);
                picAddImg05.setImageResource(0);
                picAddImg06.setImageResource(0);

                //ClipData 또는 Uri를 가져온다
                Uri uri = data.getData();
                ClipData clipData = data.getClipData();

                //이미지 URI 를 이용하여 이미지뷰에 순서대로 세팅한다.
                if(clipData!=null)
                {

                    for(int i = 0; i < 6; i++)
                    {
                        if(i<clipData.getItemCount()){
                            Uri urione =  clipData.getItemAt(i).getUri();
                            switch (i){
                                case 0:
                                    picAddImg01.setImageURI(urione);
                                    break;
                                case 1:
                                    picAddImg02.setImageURI(urione);
                                    break;
                                case 2:
                                    picAddImg03.setImageURI(urione);
                                    break;
                                case 3:
                                    picAddImg04.setImageURI(urione);
                                    break;
                                case 4:
                                    picAddImg05.setImageURI(urione);
                                    break;
                                case 5:
                                    picAddImg06.setImageURI(urione);
                                    break;
                            }
                        }
                    }
                }
                else if(uri != null)
                {
                    picAddImg01.setImageURI(uri);
                }
            }



        }

        else if (requestCode == PICK_FROM_ALBUM) {

            Uri photoUri = data.getData();

            Cursor cursor = null;
            try {


                 //  Uri 스키마를
                 //  content:/// 에서 file:/// 로  변경한다.

                String[] proj = { MediaStore.Images.Media.DATA};

                assert photoUri != null;
                cursor = getContentResolver().query(photoUri, null, null, null, null);

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






/*************



            Uri uri = data.getData();
            ClipData clipData = data.getClipData();

            //이미지 URI 를 이용하여 이미지뷰에 순서대로 세팅한다.

            if(uri != null)
            {
                //Log.d("agentpath",getPathFromUri(uri));
                //tempFile = new File(clipData.getItemAt(0).getUri().getPath());
            }
            setImage();

 ***********/

        }
        else if (requestCode == PICK_FROM_CAMERA) {

            setImage();
        }
    }


    public String getPathFromUri(Uri uri){

        Cursor cursor = getContentResolver().query(uri, null, null, null, null );

        cursor.moveToNext();

        String path = cursor.getString( cursor.getColumnIndex( "_data" ) );

        cursor.close();


        return path;



        /*Uri photoUri = data.getData();

        Cursor cursor = null;
        try {


            //  Uri 스키마를
            //  content:/// 에서 file:/// 로  변경한다.

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
        }*/
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
        String imageFileName = "Alliance" +"_" +soNoValue + "_"+ SELECT_NO+"_" + timeStamp+"_";


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

            //String imageFileName = header + soNoValue + "_"+ SELECT_NO+"_" + timeStamp + "_";
            String imageFileName = header + soNoValue + "_"+ SELECT_NO+"_" + timeStamp;
            File storageDir = new File(Environment.getExternalStorageDirectory() + "/.alliance/");
            if (!storageDir.exists()) storageDir.mkdirs();

            File tempFile3 = tempFile;

            //File tempFile = new File(storageDir+"/"+imageFileName+tempFile3.getName());
            File tempFile = new File(storageDir+"/"+imageFileName+".jpg");
            ImageResizeUtils.resizeFile(tempFile3, tempFile, 1280, true);
            oriTempFile = tempFile;


         /*
        첫 번째 파라미터에 변형시킬 tempFile 을 넣었습니다.
        두 번째 파라미터에는 변형시킨 파일을 다시 tempFile에 저장해 줍니다.
        세 번째 파라미터는 이미지의 긴 부분을 1280 사이즈로 리사이징 하라는 의미입니다.
        네 번째 파라미터를 통해 카메라에서 가져온 이미지인 경우 카메라의 회전각도를 적용해 줍니다.(앨범에서 가져온 경우에는 회전각도를 적용 시킬 필요가 없겠죠?)
        */

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(oriTempFile.getAbsolutePath(), options);


        picAddImg[SELECT_NO].setImageBitmap(originalBm);
        picAddUri[SELECT_NO].setText(oriTempFile.getAbsolutePath());

        LOCK[SELECT_NO] = 1;  //1 은 사진을 첨부했다 (갤러리 또는 카메라로 찍어서 앱의 이미지뷰에 넣었다 라는것)
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
        //finish();
        noCmplCancel();
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
            if(picAddUri[SELECT_NO] != null && !picAddUri[SELECT_NO].getText().toString().equals(""))
            {

                sendFileCnt++;
                File file = new File(picAddUri[SELECT_NO].getText().toString());

                upload(file);
            }

        }

        public void upload(File uploadFile){

            /********** Pick file from memory *******/
            //장치로부터 메모리 주소를 얻어낸 뒤, 파일명을 가지고 찾는다.
            //현재 이것은 내장메모리 루트폴더에 있는 것.

            File f = uploadFile;
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

            if(                noSignUri != null && noSignUri.getText() != null && !noSignUri.getText().toString().equals(""))
            {

                File file = new File(noSignUri.getText().toString());
                Log.d("SignNThread",file.getAbsolutePath());
                uploadSign(file);
            }
            else
            {
                showProgress(false);
                noSaveButton.setVisibility(View.INVISIBLE);
                noSignaturePad.setEnabled(true);

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


            if(SELECT_NO == 0) {picFileName01.setText(yyyyMMddFolder +"/"+fileName.getName());};
            if(SELECT_NO == 1) {picFileName02.setText(yyyyMMddFolder +"/"+fileName.getName());};
            if(SELECT_NO == 2) {picFileName03.setText(yyyyMMddFolder +"/"+fileName.getName());};
            if(SELECT_NO == 3) {picFileName04.setText(yyyyMMddFolder +"/"+fileName.getName());};
            if(SELECT_NO == 4) {picFileName05.setText(yyyyMMddFolder +"/"+fileName.getName());};
            if(SELECT_NO == 5) {picFileName06.setText(yyyyMMddFolder +"/"+fileName.getName());};

            handler.post(new Runnable() {
                @Override
                public void run() {
                    showProgress(false);
                    mToast.makeText(getApplicationContext(),"이미지 업로드 성공",Toast.LENGTH_SHORT).show();
                    LOCK[SELECT_NO] = 2;   //2는 파일업로드 완료 상태
                    sendCmplCnt++;

                }
            });


        } catch (Exception e) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    showProgress(false);

                    View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(NoCmplActivity.this);
                    builder.setView(dialogView);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    Button ok_btn = dialogView.findViewById(R.id.successBtn);
                    TextView ok_txt = dialogView.findViewById(R.id.successText);
                    ok_txt.setText("이미지 업로드 실패\n\n"+e.getMessage());
                    ok_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    //이미지세팅 초기화
                    LOCK[SELECT_NO] = 0;
                    picAddImg[SELECT_NO].setImageDrawable(getDrawable(R.mipmap.ic_camera));
                    picAddUri[SELECT_NO].setText("");
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
            client.upload(signFile, new NoCmplActivity.MyTransferListener());//업로드 시작


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
                    noSignaturePad.setEnabled(true);
                    noSaveButton.setVisibility(View.INVISIBLE);

                    noSignFileName.setText("");


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

        public void started() {

            handler.post(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        public void transferred(int length) {

            handler.post(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        public void completed() {

            handler.post(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        public void aborted() {

            handler.post(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        public void failed() {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    //sendFileFtp.setVisibility(View.VISIBLE);
                    // Transfer failed
                    System.out.println(" failed ..." );
                }
            });
        }

    }


    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    Integer etcNo;  //ETC 인것의 순번 저장용

    List<NoCmplVO> resultCheck;

    private void mNoCmplReasonCombo(NoCmplVO noCmplVO) {
        service.mNoCmplReasonCombo(noCmplVO).enqueue(new Callback<List<NoCmplVO>>() {

            @Override
            public void onResponse(Call<List<NoCmplVO>> call, Response<List<NoCmplVO>> response) {

                if(response.isSuccessful()) //응답값이 없다
                {
                    List<NoCmplVO> result = response.body();
                    resultCheck = result;
                    if(result.size() > 0)
                    {
                        noCmplRadio       = new RadioButton[result.size()];
                        noCmplReasonCode     = new TextView[result.size()];
                        noCmplReasonEtc       = new EditText[result.size()];
                        for(int i = 0; i<result.size(); i++)
                        {
                            TableRow tRow = new TableRow(getApplicationContext());     // 테이블 ROW 생성

                            noCmplRadio[i] =  new RadioButton(getApplicationContext());
                            noCmplRadio[i].setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue_1e6ce3)));
                            noCmplRadio[i].setChecked(true);
                            noCmplRadio[i].setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                            noCmplRadio[i].setChecked(false);


                            noCmplReasonCode[i] =  new TextView(getApplicationContext());
                            noCmplReasonEtc[i] =  new EditText(getApplicationContext());
                            noCmplReasonEtc[i].setOnKeyListener(new View.OnKeyListener() {

                                @Override
                                public boolean onKey(View v, int keyCode, KeyEvent event) {
                                    if (keyCode == event.KEYCODE_ENTER)
                                        return true;
                                    return false;
                                }
                            });

                            noCmplRadio[i].setText(result.get(i).getComboNm());
                            noCmplReasonCode[i].setText(result.get(i).getComboCd());
                            if(result.get(i).getComboCd().equals("ETC")){
                                etcNo = i;
                            }
                            noCmplReasonCode[i].setVisibility(View.GONE);
                            noCmplReasonEtc[i].setVisibility(View.INVISIBLE);


                            int finalI = i;
                            noCmplRadio[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    noCmplBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_blue_button));
                                    noCmplBtn.setTextColor(getResources().getColorStateList(R.color.white));
                                    noCmplBtn.setEnabled(true);

                                    noCmplRadio[finalI].setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue_1e6ce3)));
                                    for(int j=0;j<result.size();j++)
                                    {
                                        if(finalI != j)
                                        {
                                            noCmplRadio[j].setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                                            noCmplRadio[j].setChecked(false);
                                        }
                                    }

                                    //기타를 선택을 하면
                                    if(noCmplReasonCode[finalI].getText().equals("ETC"))
                                    {

                                        Integer maxLength = 10;
                                        noCmplReasonEtc[finalI].setText("");
                                        noCmplReasonEtc[finalI].setVisibility(View.VISIBLE);
                                        //입력란 활성화
                                        setUseableEditText(noCmplReasonEtc[finalI],true);

                                        noCmplReasonEtc[finalI].setHint("내용입력(최대10자)");
                                        noCmplReasonEtc[finalI].setMaxLines(1);

                                        noCmplReasonEtc[finalI].setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
                                        noCmplReasonEtc[finalI].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_gray_detail_white_btn));
                                    }
                                    else
                                    {
                                        //ETC 입력란 안보이게 가림
                                        noCmplReasonEtc[etcNo].setText("");
                                        //입력란 비활성화
                                        setUseableEditText(noCmplReasonEtc[etcNo],false);
                                        noCmplReasonEtc[etcNo].setVisibility(View.INVISIBLE);
                                    }
                                }
                            });

                            tRow.addView(noCmplRadio[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            tRow.addView(noCmplReasonCode[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            tRow.addView(noCmplReasonEtc[i], new TableRow.LayoutParams(360, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                            noCmplTable.addView(tRow, new TableLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT)
                            );
                        }
                    }
                    else
                    {
                        View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(NoCmplActivity.this);
                        builder.setView(dialogView);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                        Button ok_btn = dialogView.findViewById(R.id.successBtn);
                        TextView ok_txt = dialogView.findViewById(R.id.successText);
                        ok_txt.setText("미마감 사유 조회 실패 - 조회 0건");
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(NoCmplActivity.this);
                    builder.setView(dialogView);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    Button ok_btn = dialogView.findViewById(R.id.successBtn);
                    TextView ok_txt = dialogView.findViewById(R.id.successText);
                    ok_txt.setText("미마감 사유 조회 실패\n\n조회 응답결과가 없습니다");
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
            public void onFailure(Call<List<NoCmplVO>> call, Throwable t) {
                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(NoCmplActivity.this);
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                TextView ok_txt = dialogView.findViewById(R.id.successText);
                ok_txt.setText("미마감 사유 조회 실패\n\n접속실패\n"+t.getMessage());
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
                }
                else{
                    showProgress(false);
                    View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(NoCmplActivity.this);
                    builder.setView(dialogView);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    Button ok_btn = dialogView.findViewById(R.id.successBtn);
                    TextView ok_txt = dialogView.findViewById(R.id.successText);
                    ok_txt.setText("미마감 onCreate 데이터 조회 실패\n\n응답결과가 없거나 여러개 입니다.\n[instMobileMId : "+instMobileMIdValue+"]\n\n이전화면으로 이동합니다");
                    ok_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<NoCmplVO> call, Throwable t) {

                showProgress(false);
                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(NoCmplActivity.this);
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                TextView ok_txt = dialogView.findViewById(R.id.successText);
                ok_txt.setText("미마감 onCreate 데이터 조회 실패\n\n접속실패\n"+t.getMessage());
                ok_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        finish();
                    }
                });
            }
        });
    }
    //미마감 처리 버튼 눌렀을떄 (SAVE)
    private void mNoCmplSaveStat(NoCmplSaveStatVO noCmplSaveVO) {
        service.mNoCmplSaveStat(noCmplSaveVO).enqueue(new Callback<NoCmplVO>() {

            @Override
            public void onResponse(Call<NoCmplVO> call, Response<NoCmplVO> response) {

                if(response.isSuccessful()) //응답값이 있다
                {
                    NoCmplVO result = response.body();
                    if(result.getRtnYn().equals("Y"))
                    {
                        //미마감 처리 저장 결과가 Y 일경우 미마감 알림톡을 발송함
                        XCmplSendTalk();    //알림톡 발송하기
                    }
                    else
                    {
                        View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                        AlertDialog.Builder builder = new AlertDialog.Builder(NoCmplActivity.this);
                        builder.setView(dialogView);

                        AlertDialog alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();

                        Button ok_btn = dialogView.findViewById(R.id.successBtn);
                        TextView ok_txt = dialogView.findViewById(R.id.successText);
                        ok_txt.setText("미마감 처리 실패\n"+result.getRtnMsg() == null ? "처리 실패 " : result.getRtnMsg());
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(NoCmplActivity.this);
                    builder.setView(dialogView);

                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                    Button ok_btn = dialogView.findViewById(R.id.successBtn);
                    TextView ok_txt = dialogView.findViewById(R.id.successText);
                    ok_txt.setText("미마감 처리 실패\n\n응답결과가 없음");
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
            public void onFailure(Call<NoCmplVO> call, Throwable t) {

                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(NoCmplActivity.this);
                builder.setView(dialogView);

                AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();

                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                TextView ok_txt = dialogView.findViewById(R.id.successText);
                ok_txt.setText("미마감 처리 실패\n\n접속실패\n"+t.getMessage());
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



    //미마감 알림톡 발송 결과를 저장하기. 그리고 저장이 되던 안되던 다이얼로그는 됐다라고 나옴

    private void mNoCmplSaveTalk(NoCmplSaveTalkVO noCmplSaveTalkVO){
        service.mNoCmplSaveTalk(noCmplSaveTalkVO).enqueue(new Callback<NoCmplVO>() {

            @Override public void onResponse(Call<NoCmplVO> call, Response<NoCmplVO> response){
                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);
                android.app.AlertDialog.Builder builder =
                        new android.app.AlertDialog.Builder(NoCmplActivity.this);
                builder.setView(dialogView);
                android.app.AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                TextView ok_txt = dialogView.findViewById(R.id.successText);
                ok_txt.setText("미마감 완료");
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

            @Override public void onFailure(Call<NoCmplVO> call, Throwable t){
                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);
                android.app.AlertDialog.Builder builder =
                        new android.app.AlertDialog.Builder(NoCmplActivity.this);
                builder.setView(dialogView);
                android.app.AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                TextView ok_txt = dialogView.findViewById(R.id.successText);
                ok_txt.setText("미마감 완료");
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

    //텍스트 입력란 활성화 비활성화 넣기
    private void setUseableEditText(EditText et, boolean useable) {
        et.setClickable(useable);
        et.setEnabled(useable);
        et.setFocusable(useable);
        et.setFocusableInTouchMode(useable);
    }

    private void noCmplCancel(){

        //컨펌(버튼 두개중 하나 선택)
        View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(NoCmplActivity.this);
        builder.setView(dialogView);
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
        TextView ok_txt = dialogView.findViewById(R.id.confirmText);
        ok_txt.setTextSize(20);
        ok_txt.setTypeface(null, Typeface.BOLD);
        ok_txt.setText("미마감 처리 화면을 나가시겠습니까?");
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

    //미마감알림톡 결과를 DB에 저장
    public void XCmplTalkDb(String sendComplete, String message)
    {
        mNoCmplSaveTalk(new NoCmplSaveTalkVO(
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
    //미마감 결과를 DB에 저장
    public void XCmplSaveStatDb()
    {
        //미마감 처리 데이터 저장 SP - 미마감 저장은 미마감 저장대로 따로 돌림
        mNoCmplSaveStat(new NoCmplSaveStatVO(
                  instMobileMIdValue
                , finalNoCmplSelectCode    //미마감유형
                , finalNoCmplSelectEtc     //미마감유형이 ETC일때의 텍스트
                , finalNoCmplSelectMemo    //메모
                , noSignFileName.getText().toString()          //사인은 없음
                , picFileName01.getText().toString()   //이미지1
                , picFileName02.getText().toString()   //이미지2
                , picFileName03.getText().toString()   //이미지3
                , picFileName04.getText().toString()   //이미지4
                , picFileName05.getText().toString()   //이미지5
                , picFileName06.getText().toString()   //이미지6
                , ""
                , ""
                , ""
                , ""
                , sharePref.getString("tblUserMId","")
        ));
    }
    //미마감 알림톡 보내기
    public void XCmplSendTalk(){
        completeMsg =onCreateData.getAlrmTalkTmp().toString().replace("#{미마감 사유}", finalNoCmplSelectCode.equals("ETC") ? finalNoCmplSelectName +"("+finalNoCmplSelectEtc+")" : finalNoCmplSelectName);

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
                , null
        ));
        if(onCreateData.getAgntSendYn().equals("Y")){   //화주사 알림톡 수신 여부
            send.add(new SendTalkVO(
                    onCreateData.getMessageType()                   //알림톡 발송유형
                    //0505hphphphp
                    , sharePref.getString("PhoneNum", "")   //onCreateData.getAgntSendHp1()   //화주사 관리자 전화번호
                    , onCreateData.getProfile()                        //알림톡 프로필 아이디
                    , "00000000000000"             //발송시간 0 14개 : 즉시발송
                    , onCreateData.getTmplid()         //알림톡 템플릿아이디
                    , completeMsg      //알림톡 보낼 메세지
                    , onCreateData.getSmskind()         //알림톡 실패시 문자 발송 종류
                    , completeMsg          //알림톡 실패시 문자로 보낼 메세지
                    , onCreateData.getSmssender()         //알림톡 실패시 문자 발송할때 발송자 전화번호
                    , onCreateData.getSmslmstit()            //알림톡 실패시 문자발송할때 LMS 의 제목
                    , onCreateData.getSmsonly()        //알림톡 실패시 문자를 보낼것인가 여부
                    , null
            ));
        }


        Log.d("sendsend",send.toString());
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
                    for(int i=0;i<resultSendTalk.size();i++){
                        XCmplTalkDb(resultSendTalk.get(i).getCode(), resultSendTalk.get(i).getMessage());
                    }
                    //XCmplTalkDb(resultSendTalk.get(0).getCode(), resultSendTalk.get(0).getMessage());
                }
                else{
                    XCmplTalkDb("N","onResponse_noResult");
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<ArrayList<TalkVO>> call, Throwable t) {

                XCmplTalkDb("N","onFailure");

                showProgress(false);
            }
        });
    }
}