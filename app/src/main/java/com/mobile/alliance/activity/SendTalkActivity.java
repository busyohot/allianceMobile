package com.mobile.alliance.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mobile.alliance.R;
import com.mobile.alliance.api.BackPressCloseHandler;
import com.mobile.alliance.api.CommonHandler;
import com.mobile.alliance.api.LogoutHandler;
import com.mobile.alliance.api.PersistentCookieStore;
import com.mobile.alliance.api.RetrofitClient;
import com.mobile.alliance.api.ServiceApi;
import com.mobile.alliance.entity.sendTalk.SaveStatVO;
import com.mobile.alliance.entity.sendTalk.SaveTalkVO;
import com.mobile.alliance.entity.sendTalk.SendTalkVO;
import com.mobile.alliance.entity.sendTalk.SrchTalkVO;
import com.mobile.alliance.entity.sendTalk.TalkVO;
import com.mobile.alliance.fragment.DeliveryListFragment;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import lombok.SneakyThrows;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SendTalkActivity extends AppCompatActivity {
String completeMsg="";
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


    //내부에 데이터 저장하는것
    static private String SHARE_NAME = "SHARE_PREF";
    static SharedPreferences sharePref = null;
    static SharedPreferences.Editor editor = null;


    //화면의 뷰 정의
    ImageView sendTalkBack;
    Button sendTalkBtn;     //알림톡발송버튼
    TextView sendTalkMsg;
    TextView    sendTalkMsg2;
    TextView dlvyDtText;
    ImageView dlvyDtImg;
    TextView fromDlvyTime;  //출발시작시간
    TextView    toDlvyTime; //도착시간
    Button sendSmsBtn;  //문자발송 버튼

    TextView talkUserId;
    TextView talkMessageType;
    TextView talkPhn;
    TextView talkProfile;
    TextView talkReservedt;
    TextView talkAlrmTlkMsg;
    TextView talkTitle;
    TextView talkTmplid;
    TextView talkTmplnm;
    TextView talkSmskind;
    TextView talkSmssender;
    TextView talkSmslmstit;
    TextView talkSmsonly;
    TextView    talkInstMobileMId;
    TextView    talkTblSoMId;

    EditText editTextTextMultiLine;





    String instMobileMIdValue;
    String dlvyStatCdValue;






    long mNow;
    Date mDate;

    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREAN);
    SimpleDateFormat mTimeFormamt = new SimpleDateFormat("HH", Locale.KOREAN);



    private String getDate() throws ParseException {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        String d = getCalDay(mFormat.format(mDate) ,0);
        return d;
    }
    private String getTime() throws ParseException {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        String t = mTimeFormamt.format(mDate);
        return t;
    }
    private String getCalDay(String selDateS ,int d) throws ParseException {
        selDateS = selDateS.substring(0,10);
        String from = selDateS.replace(".","-");
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN);


        Date to = transFormat.parse(from);

        Calendar cal = Calendar.getInstance();
        cal.setTime(to);
        cal.add(Calendar.DATE , d);
        int dayNum = cal.get(Calendar.DAY_OF_WEEK);
        String day = "";
        switch (dayNum) {
            case 1: day = "일"; break;
            case 2: day = "월"; break;
            case 3: day = "화"; break;
            case 4: day = "수"; break;
            case 5: day = "목"; break;
            case 6: day = "금"; break;
            case 7: day = "토"; break;
        }
        return (mFormat.format(cal.getTime()) + " "+day);
    }

    int y=0, m=0, d=0, h=0, mi=0;
    void showDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @SneakyThrows
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                y = year;
                m = month+1;
                d = dayOfMonth;
                String selY = Integer.toString(y);
                String selM = Integer.toString(m);
                if(selM.length() == 1){
                    selM = "0"+selM;
                }
                String selD = Integer.toString(d);
                if(selD.length() == 1){
                    selD = "0"+selD;
                }
                String t = getCalDay(selY+"."+selM+"."+selD,0);

                dlvyDtText.setText(t);

            }
        }   ,   Integer.parseInt(dlvyDtText.getText().toString().substring(0,4))
                ,   Integer.parseInt(dlvyDtText.getText().toString().substring(5,7))-1
                ,   Integer.parseInt(dlvyDtText.getText().toString().substring(8,10)
        ));

        //datePickerDialog.setMessage("날짜선택");
        datePickerDialog.show();


    }


    void showTime(TextView timeGubn) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        h = hourOfDay;
                        mi = minute;

                        String selH = Integer.toString(h);
                        if(selH.length() == 1){
                            selH = "0"+selH;
                        }
                        String selMi = Integer.toString(mi);
                        if(selMi.length() == 1){
                            selMi = "0"+selMi;
                        }


                        timeGubn.setText(selH+" 시");

                    }
                }   ,
                Integer.parseInt(timeGubn.getText().toString().substring(0,2))
                ,   00,true
        );

        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_talk);





        //commonHandler.showToast("토스터 테스트",0,17,17);

        //내부에 데이터 저장하는것
        sharePref = getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        editor = sharePref.edit();


        //진행바
        mProgressView = (ProgressBar) findViewById(R.id.sendTalkProgress);
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
        serviceTalk = RetrofitClient.getTalk(client).create(ServiceApi.class);  //알림톡용
        service = RetrofitClient.getClient(client).create(ServiceApi.class);    //시스템용

        instMobileMIdValue =   getIntent().getStringExtra("instMobileMId");
        dlvyStatCdValue =   getIntent().getStringExtra("dlvyStatCd");

        //화면 열리자마자 조회 SP
        //Log.d("instMobileMIdValue",instMobileMIdValue);
        mSrchTalk(new SrchTalkVO(instMobileMIdValue)       );
        showProgress(true);





        sendTalkBack = (ImageView) findViewById(R.id.sendTalkBack);
        sendTalkBtn = (Button) findViewById(R.id.sendTalkBtn);
        sendTalkMsg = (TextView) findViewById(R.id.sendTalkMsg);    //배송요청사항
        sendTalkMsg2 = (TextView) findViewById(R.id.cnslTxt);    //CS상담내용
        dlvyDtText = (TextView)findViewById(R.id.dlvyDtText);   //배송예정일 의 날짜
        dlvyDtImg = (ImageView)findViewById(R.id.dlvyDtImg);    //달력이미지
        fromDlvyTime = (TextView)findViewById(R.id.fromDlvyTime);   //배송 시작시간
        toDlvyTime  =   (TextView)findViewById(R.id.toDlvyTime);    //배송 종료시간
        sendSmsBtn = (Button) findViewById(R.id.sendSmsBtn);

        talkUserId          =   (TextView)findViewById(R.id.talkUserId);
        talkMessageType     =   (TextView)findViewById(R.id.talkMessageType);
        talkPhn             =   (TextView)findViewById(R.id.talkPhn);
        talkProfile         =   (TextView)findViewById(R.id.talkProfile);
        talkReservedt       =   (TextView)findViewById(R.id.talkReservedt);
        talkAlrmTlkMsg      =   (TextView)findViewById(R.id.talkAlrmTlkMsg);
        talkTitle           =   (TextView)findViewById(R.id.talkTitle);
        talkTmplid          =   (TextView)findViewById(R.id.talkTmplid);
        talkTmplnm          =   (TextView)findViewById(R.id.talkTmplnm);
        talkSmskind         =   (TextView)findViewById(R.id.talkSmskind);
        talkSmssender       =   (TextView)findViewById(R.id.talkSmssender);
        talkSmslmstit       =   (TextView)findViewById(R.id.talkSmslmstit);
        talkSmsonly         =   (TextView)findViewById(R.id.talkSmsonly);

        talkInstMobileMId   =   (TextView)findViewById(R.id.talkInstMobileMId);
        talkTblSoMId        =   (TextView)findViewById(R.id.talkTblSoMId);

        editTextTextMultiLine = (EditText) findViewById(R.id.editTextTextMultiLine);



        sendTalkBack.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View v) {
                //commonHandler.showFinishAlertDialog2("이전화면","일림톡 화면을 닫으시겠습니까?","Y");
                //컨펌(버튼 두개중 하나 선택)

                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(SendTalkActivity.this);
                builder.setView(dialogView);

                AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();

                Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
                TextView ok_txt = dialogView.findViewById(R.id.confirmText);
                ok_txt.setTextSize(20);
                ok_txt.setTypeface(null, Typeface.BOLD);
                ok_txt.setText("이전화면으로 이동하시겠습니까?");
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
        });












        //문자발송 누르기
        sendSmsBtn.setOnClickListener(new Button.OnClickListener() {

            @SneakyThrows
            @Override
            public void onClick(View v) {
                String phno = talkPhn.getText().toString();

                Uri smsUri = Uri.parse("tel:" + phno);
                Intent intent = new Intent(Intent.ACTION_VIEW, smsUri); // 보내는 화면이 팝업됨

                intent.putExtra("address", phno); // 받는 번호
                //intent.putExtra("sms_body", "문자메세지보내기"); // 보낼 문자내용
                intent.setType("vnd.android-dir/mms-sms");
                startActivity(intent);

            }
        });


        //알림톡 발송버튼을 눌렀을때
        sendTalkBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(SendTalkActivity.this);
                builder.setView(dialogView);

                AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();

                Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
                TextView ok_txt = dialogView.findViewById(R.id.confirmText);
                ok_txt.setTextSize(20);
                ok_txt.setTypeface(null, Typeface.BOLD);
                ok_txt.setText("알림톡을 발송 하시겠습니까?");
                ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                ok_btn.setOnClickListener(new View.OnClickListener() {
                    @SneakyThrows @Override
                    public void onClick(View v) {

                        SaveStatDb();   //4000 배송준비 단계에서 5000해피콜 상태로 변경한다
                        alertDialog.dismiss();

                        completeMsg = talkAlrmTlkMsg.getText().toString().replace("#{배송예정일}",dlvyDtText.getText().toString()+"요일");
                        completeMsg = completeMsg.replace("#{배송시간}",fromDlvyTime.getText().toString()+"~"+toDlvyTime.getText().toString());
                        Log.e("completeMsg","completeMsg : " + completeMsg);
                                    //알림톡 발송 알림톡 발송
                        ArrayList<SendTalkVO> send = new ArrayList<SendTalkVO>();

                        SendTalkVO.Button1 button1Data = new SendTalkVO.Button1();
                        button1Data.setName("상담사원에게 메세지 보내기");
                        button1Data.setType("WL");
                        button1Data.setUrl_mobile("http://pf.kakao.com/_ExbPtK/chat");
                        button1Data.setUrl_pc("http://pf.kakao.com/_ExbPtK/chat");


                        send.add(new SendTalkVO(
                                talkMessageType.getText().toString()                    //알림톡 발송유형
                                //0505hphphphp
                                ,   sharePref.getString("PhoneNum","")   //talkPhn.getText().toString()    //알림톡 받는사람 전화번호
                                ,   talkProfile.getText().toString()                        //알림톡 프로필 아이디
                                ,   talkReservedt.getText().toString()              //발송시간
                                ,   talkTmplid.getText().toString()            //알림톡 템플릿아이디
                                ,   completeMsg      //알림톡 보낼 메세지
                                ,   talkSmskind.getText().toString()            //알림톡 실패시 문자 발송 종류
                                ,   completeMsg          //알림톡 실패시 문자로 보낼 메세지
                                ,   talkSmssender.getText().toString()          //알림톡 실패시 문자 발송할때 발송자 전화번호
                                ,   talkSmslmstit.getText().toString()          //알림톡 실패시 문자발송할때 LMS 의 제목
                                ,   talkSmsonly.getText().toString()            //알림톡 실패시 문자를 보낼것인가 여부
                                ,   button1Data

                        ));

                        mSendTalk(send);    // 알림톡 발송하는 부분
                        showProgress(true);
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



                    ArrayList<TalkVO> result = new ArrayList<TalkVO>();
    //알림톡 발송
    private void mSendTalk(ArrayList<SendTalkVO> sendTalkVO) {    //요청 VO

        serviceTalk.mSendTalk(sendTalkVO).enqueue(new Callback<ArrayList<TalkVO>>() {    //앞 요청VO, CallBack 응답 VO

            @Override
            public void onResponse(Call<ArrayList<TalkVO>> call, Response<ArrayList<TalkVO>> response) {  //둘다 응답 VO

                if(response.isSuccessful() ) //응답값이 있다
                {
                    //알림톡 결과를 받은후에
                    result  = response.body();
                    //// 2. 상태값과 상관없이   알림톡을 발송했고 그 결과가 success 일때 (발송 성공일때) 알림톡 발송 내역을 저장(결과코드를 포함하여저장)
                    SaveTalkDb(result.get(0).getCode(), result.get(0).getMessage());

                }
                else{
                    SaveTalkDb("N","onResponse_noResult");
                }
                showProgress(false);
            }



            @Override
            public void onFailure(Call<ArrayList<TalkVO>> call, Throwable t) {

                SaveTalkDb("N","onFailure");

                showProgress(false);
            }



        });


    }
    //상태코드 상관없이 그냥 알림톡 발송의 성공여부만 저장함
    public void SaveTalkDb(String sendComplete, String message)
    {

        mSaveTalk(new SaveTalkVO(
                talkInstMobileMId.getText().toString()
                , dlvyDtText.getText().toString().substring(0, 10).replace(".", "")
                , fromDlvyTime.getText().toString().substring(0, 2)
                , toDlvyTime.getText().toString().substring(0, 2)
                , completeMsg
                , sendComplete
                , talkUserId.getText().toString()
                , talkMessageType.getText().toString()
                , talkPhn.getText().toString()
                , talkProfile.getText().toString()
                , talkReservedt.getText().toString()
                , talkAlrmTlkMsg.getText().toString()
                , talkTitle.getText().toString()
                , talkTmplid.getText().toString()
                , message
                , sharePref.getString("tblUserMId", "")
        ));


    }
    //4000 배송모바일 확정 상태일때만 알림톡 보낼때만 상태를 5000 해피콜 완료 로 바꾼다
    public void SaveStatDb()
    {
        if(dlvyStatCdValue.equals("4000")){ //4000 배송모바일 확정 상태일때만 알림톡 보낼때만 상태를 5000 해피콜 완료 로 바꾼다
            mSaveStat(new SaveStatVO(
                    talkInstMobileMId.getText().toString()
                    , dlvyDtText.getText().toString().substring(0, 10).replace(".", "")
                    , fromDlvyTime.getText().toString().substring(0, 2)
                    , toDlvyTime.getText().toString().substring(0, 2)
                    , editTextTextMultiLine.getText().toString()
                    , sharePref.getString("tblUserMId", "")
            ));
        }
    }

    //알림톡 발송결과와 상관없이 알림톡 했다는걸 DB에 저장함
    private void mSaveTalk(SaveTalkVO saveTalkVO) {    //요청 VO

        service.mSaveTalk(saveTalkVO).enqueue(new Callback<TalkVO>() {    //앞 요청VO, CallBack 응답 VO
            @Override
            public void onResponse(Call<TalkVO> call, Response<TalkVO> response) {  //둘다 응답 VO
                showProgress(false);
                if(!dlvyStatCdValue.equals("4000")) //4000 배송준비 상태 가 아닐때만 알림톡 발송했다고 보냄
                {
                    //알림톡 발송결과 저장이 성공하던 실패하던 알림톡 발송완료를 표시한다
                    View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                    AlertDialog.Builder builder = new AlertDialog.Builder(SendTalkActivity.this);
                    builder.setView(dialogView);

                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                    Button ok_btn = dialogView.findViewById(R.id.successBtn);
                    TextView ok_txt = dialogView.findViewById(R.id.successText);
                    ok_txt.setText("알림톡 발송 완료");
                    ok_btn.setOnClickListener(new View.OnClickListener() {

                        @SneakyThrows @Override
                        public void onClick(View v) {


                            alertDialog.dismiss();
                            //배송목록 갱신하기
                            DeliveryListFragment deliveryListFragment = (DeliveryListFragment)DeliveryListFragment._DeliveryListFragment;
                            deliveryListFragment.changeDate();


                            //상세정보보기 다시 정보 읽기

                            DeliveryListDetail deliveryListDetail = (DeliveryListDetail)DeliveryListDetail._DeliveryListDetail;

                            deliveryListDetail.deliveryDetailSrch();

                            //알림톡발송(현재화면) Activity 닫기
                            finish();

                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<TalkVO> call, Throwable t) {
                showProgress(false);
                if(!dlvyStatCdValue.equals("4000")) //4000 배송준비 상태 가 아닐때만 알림톡 발송했다고 보냄
                {
                    //알림톡 발송결과 저장이 성공하던 실패하던 알림톡 발송완료를 표시한다
                    View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                    AlertDialog.Builder builder = new AlertDialog.Builder(SendTalkActivity.this);
                    builder.setView(dialogView);

                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                    Button ok_btn = dialogView.findViewById(R.id.successBtn);
                    TextView ok_txt = dialogView.findViewById(R.id.successText);
                    ok_txt.setText("알림톡 발송 실패");
                    ok_btn.setOnClickListener(new View.OnClickListener() {

                        @SneakyThrows @Override
                        public void onClick(View v) {


                            alertDialog.dismiss();
                            //배송목록 갱신하기
                            DeliveryListFragment deliveryListFragment = (DeliveryListFragment)DeliveryListFragment._DeliveryListFragment;
                            deliveryListFragment.changeDate();


                            //상세정보보기 다시 정보 읽기

                            DeliveryListDetail deliveryListDetail = (DeliveryListDetail)DeliveryListDetail._DeliveryListDetail;

                            deliveryListDetail.deliveryDetailSrch();



                        }
                    });
                }
            }

        });
    }

    //4000dl 5000으로 바꿈 - 해피콜 완료 상태 5000
    private void mSaveStat(SaveStatVO saveStatVO) {    //요청 VO

        service.mSaveStat(saveStatVO).enqueue(new Callback<TalkVO>() {    //앞 요청VO, CallBack 응답 VO
            @Override
            public void onResponse(Call<TalkVO> call, Response<TalkVO> response) {  //둘다 응답 VO
                showProgress(false);
                //결과값이 있을떄
                if(response.isSuccessful()){
                    if(response.body().getRtnYn().equals("Y"))  //리턴이 성공일때
                    {
                        //알림톡 발송완료 라고 다이얼로그
                        //알림톡 발송결과 저장이 성공하던 실패하던 알림톡 발송완료를 표시한다
                        View dialogView =
                                getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(SendTalkActivity.this);
                        builder.setView(dialogView);

                        AlertDialog alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();

                        Button ok_btn = dialogView.findViewById(R.id.successBtn);
                        TextView ok_txt = dialogView.findViewById(R.id.successText);
                        ok_txt.setText("해피콜 완료");
                        ok_btn.setOnClickListener(new View.OnClickListener() {

                            @SneakyThrows @Override
                            public void onClick(View v){


                                alertDialog.dismiss();
                                //배송목록 갱신하기
                                DeliveryListFragment deliveryListFragment =
                                        (DeliveryListFragment) DeliveryListFragment._DeliveryListFragment;
                                deliveryListFragment.changeDate();


                                //상세정보보기 다시 정보 읽기

                                DeliveryListDetail deliveryListDetail =
                                        (DeliveryListDetail) DeliveryListDetail._DeliveryListDetail;

                                deliveryListDetail.deliveryDetailSrch();

                                //알림톡발송(현재화면) Activity 닫기
                                finish();

                            }

                        });
                    }
                    else
                    {
                        //알림톡 발송완료 라고 다이얼로그
                        //알림톡 발송결과 저장이 성공하던 실패하던 알림톡 발송완료를 표시한다
                        View dialogView =
                                getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(SendTalkActivity.this);
                        builder.setView(dialogView);

                        AlertDialog alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();

                        Button ok_btn = dialogView.findViewById(R.id.successBtn);
                        TextView ok_txt = dialogView.findViewById(R.id.successText);
                        ok_txt.setText("해피콜 결과 저장 실패.");
                        ok_btn.setOnClickListener(new View.OnClickListener() {

                            @SneakyThrows @Override
                            public void onClick(View v){


                                alertDialog.dismiss();
                                //배송목록 갱신하기
                                DeliveryListFragment deliveryListFragment =
                                        (DeliveryListFragment) DeliveryListFragment._DeliveryListFragment;
                                deliveryListFragment.changeDate();

                                //상세정보보기 다시 정보 읽기

                                DeliveryListDetail deliveryListDetail =
                                        (DeliveryListDetail) DeliveryListDetail._DeliveryListDetail;

                                deliveryListDetail.deliveryDetailSrch();


                            }

                        });
                    }
                }
                else
                {
                    View dialogView =
                            getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(SendTalkActivity.this);
                    builder.setView(dialogView);

                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                    Button ok_btn = dialogView.findViewById(R.id.successBtn);
                    TextView ok_txt = dialogView.findViewById(R.id.successText);
                    ok_txt.setText("해피콜 결과 저장 실패..");
                    ok_btn.setOnClickListener(new View.OnClickListener() {

                        @SneakyThrows @Override
                        public void onClick(View v){


                            alertDialog.dismiss();
                            //배송목록 갱신하기
                            DeliveryListFragment deliveryListFragment =
                                    (DeliveryListFragment) DeliveryListFragment._DeliveryListFragment;
                            deliveryListFragment.changeDate();

                            //상세정보보기 다시 정보 읽기

                            DeliveryListDetail deliveryListDetail =
                                    (DeliveryListDetail) DeliveryListDetail._DeliveryListDetail;

                            deliveryListDetail.deliveryDetailSrch();


                        }

                    });
                }
            }
            @Override
            public void onFailure(Call<TalkVO> call, Throwable t) {

                showProgress(false);
                //알림톡 발송결과 저장이 성공하던 실패하던 알림톡 발송완료를 표시한다
                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(SendTalkActivity.this);
                builder.setView(dialogView);

                AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();

                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                TextView ok_txt = dialogView.findViewById(R.id.successText);
                ok_txt.setText("해피콜 결과 저장 실패...");
                ok_btn.setOnClickListener(new View.OnClickListener() {

                    @SneakyThrows @Override
                    public void onClick(View v) {


                        alertDialog.dismiss();
                        //배송목록 갱신하기
                        DeliveryListFragment deliveryListFragment = (DeliveryListFragment)DeliveryListFragment._DeliveryListFragment;
                        deliveryListFragment.changeDate();


                        //상세정보보기 다시 정보 읽기

                        DeliveryListDetail deliveryListDetail = (DeliveryListDetail)DeliveryListDetail._DeliveryListDetail;

                        deliveryListDetail.deliveryDetailSrch();



                    }
                });
            }

        });
    }



    //화면이 열리자 마자 조회를 함
    private void mSrchTalk(SrchTalkVO srchTalkVO) {
        service.mSrchTalk(srchTalkVO).enqueue(new Callback<TalkVO>() {


            @SneakyThrows
            @Override
            public void onResponse(Call<TalkVO> call, Response<TalkVO> response) {

                if (response.isSuccessful() ) //응답값이 있다
                {
                    TalkVO result = response.body();

                    sendTalkMsg.setText(result.getDlvyRqstMsg());




                    //배송예정일 넣기

                    dlvyDtText.setText(getDate());
                    dlvyDtText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showDate();
                        }
                    });

                    dlvyDtImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showDate();
                        }
                    });


                    fromDlvyTime.setText("08" + " 시");
                    //fromDlvyTime.setText(getTime() + " 시");   //원본
                    fromDlvyTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showTime(fromDlvyTime);
                        }
                    });

                    String hhhh = (Integer.parseInt(getTime())+1)+"";
                    if(hhhh.length() == 1)
                    {
                        hhhh ="0"+hhhh;
                    }
                    //toDlvyTime.setText( hhhh+ " 시");  //원본
                    toDlvyTime.setText( "10"    + " 시");
                    toDlvyTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showTime(toDlvyTime);
                        }
                    });

                    /////////////////알림톡 메세지 보낼 부분


                    talkUserId.setText(result.getUserid());
                    talkMessageType.setText(result.getMessageType());
                    talkPhn.setText(result.getPhn());
                    talkProfile.setText(result.getProfile());
                    talkReservedt.setText("00000000000000");    //숫자14자리 0 즉시, yyyyMMddHHmmss
                    talkAlrmTlkMsg.setText(result.getAlrmTalkTmp());
                    talkTitle.setText(result.getSmsLmsTit());
                    talkTmplid.setText(result.getTmplId());
                    talkTmplnm.setText(result.getTmplNm());
                    talkSmskind.setText(result.getSmsKind());
                    talkSmssender.setText(result.getSmsSender());
                    talkSmslmstit.setText(result.getSmsLmsTit());
                    talkSmsonly.setText(result.getSmsOnly());


                    talkInstMobileMId.setText(result.getInstMobileMId());
                    talkTblSoMId.setText(result.getTblSoMId());



                } else {
                    commonHandler.showFinishAlertDialog("내용조회 실패","내용조회 결과가 없습니다.","Y");
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<TalkVO> call, Throwable t) {
                commonHandler.showAlertDialog("내용조회 실패", "접속실패\n" + t.getMessage());
                showProgress(false);
            }

        });
    }







    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    @Override public void onBackPressed()
    {
        //commonHandler.showFinishAlertDialog2("이전화면","일림톡 화면을 닫으시겠습니까?","Y");

        //컨펌(버튼 두개중 하나 선택)

        View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(SendTalkActivity.this);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
        TextView ok_txt = dialogView.findViewById(R.id.confirmText);
        ok_txt.setTextSize(20);
        ok_txt.setTypeface(null, Typeface.BOLD);
        ok_txt.setText("이전화면으로 이동하시겠습니까?");
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