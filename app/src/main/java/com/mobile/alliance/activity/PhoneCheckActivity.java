package com.mobile.alliance.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.mobile.alliance.R;
import com.mobile.alliance.api.BackPressCloseHandler;
import com.mobile.alliance.api.CommonHandler;
import com.mobile.alliance.api.LogoutHandler;
import com.mobile.alliance.api.PersistentCookieStore;
import com.mobile.alliance.api.RetrofitClient;
import com.mobile.alliance.api.ServiceApi;
import com.mobile.alliance.entity.phoneCheck.PhoneCheckSendVO;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.Random;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneCheckActivity extends AppCompatActivity {
    public static OkHttpClient client;

    //뒤로가기 버튼 2번 누르면 취소 하는것
    private BackPressCloseHandler backPressCloseHandler;
    //로그아웃
    private LogoutHandler logoutHandler;
    //진행바 뷰
    private ProgressBar mProgressView;
    private ServiceApi serviceTalk; //알림톡용

    //api 이용시 쿠키전달
    private PersistentCookieStore persistentCookieStore;

    //내부에 데이터 저장하는것
    static private String SHARE_NAME = "SHARE_PREF";
    static SharedPreferences sharePref = null;
    static SharedPreferences.Editor editor = null;

    //공통
    CommonHandler commonHandler;

    Intent intent;

    ImageView backPhn;

    EditText phnNoChk,countAuthNo;
    TextView count_view,authNo,count_send,count_notice;
    Button authBtn,authChkBtn;
    int randomNum; //인증번호 6자리숫자

    CountDownTimer countDownTimer;

    String loginIdValue="";

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_check);

        int max_num_value = 999999;
        int min_num_value = 000000;

        Random random = new Random();

        loginIdValue = getIntent().getStringExtra("loginId");


        authNo =(TextView) findViewById(R.id.authNo);

        count_send = (TextView)findViewById(R.id.count_send);   //인증번호를 발송하였습니다.
        count_send.setVisibility(View.GONE);
        count_notice = (TextView) findViewById(R.id.count_notice);  //3분내 입력하세요............
        count_notice.setVisibility(View.GONE);

        commonHandler = new CommonHandler(this);

        backPressCloseHandler = new BackPressCloseHandler(this);
        logoutHandler = new LogoutHandler(this);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //키보드 뜰때 입력창 가리지 않고 화면을 위로 올리기

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

        intent = getIntent();

        //내부에 데이터 저장하는것
        sharePref = getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        editor = sharePref.edit();

        // 화면에 보일 TextView
        count_view = (TextView) findViewById(R.id.count_view);

        // TODO : 타이머 돌릴 총시간
        String conversionTime = "000300";

        backPhn = (ImageView) findViewById(R.id.backPhn);

        phnNoChk = (EditText) findViewById(R.id.phnNoChk);
        phnNoChk.setText(sharePref.getString("PhoneNum", ""));
        phnNoChk.setEnabled(false);

        //인증번호 발송 버튼
        authBtn = (Button) findViewById(R.id.authBtn);

        //인증번호 확인 버튼
        authChkBtn = (Button) findViewById(R.id.authChkBtn);
        authChkBtn.setClickable(false);
        //문자로 받은 인증번호를 입력하는 란
        countAuthNo = (EditText)findViewById(R.id.countAuthNo);
        countAuthNo.setVisibility(View.GONE);
        countAuthNo.setEnabled(false);
        countAuthNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    authChkBtn.setClickable(true);
                    authChkBtn.setBackground(getApplicationContext().getDrawable(R.drawable.rounded_blue_button));
                } else {
                    authChkBtn.setClickable(false);
                    authChkBtn.setBackground(getApplicationContext().getDrawable(R.drawable.rounded_gray_button));
                }
            }
        });

        //화면 아래 확인버튼
        authChkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //입력해야할 인증번호와 실제 입력한 인증번호가 같으면
                if(authNo.getText().toString().equals(countAuthNo.getText().toString()))
                {
                    countDownTimer.cancel();

                    Intent changePass = new Intent(PhoneCheckActivity.this, ChangePassActivity.class);
                    changePass.putExtra("loginId",loginIdValue);
                    startActivity(changePass);  //다음 액티비티를 열고
                    PhoneCheckActivity.this.finish();     //이 액티비티를 닫음
                }
                else
                {
                    View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                    AlertDialog.Builder builder = new AlertDialog.Builder(PhoneCheckActivity.this);
                    builder.setView(dialogView);

                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                    Button ok_btn = dialogView.findViewById(R.id.successBtn);
                    TextView ok_txt = dialogView.findViewById(R.id.successText);
                    ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START); //글자를 왼쪽 정렬
                    ok_txt.setText("도착한 인증번호 문자메세지를 다시 확인하시고 입력하세요.");
                    ok_btn.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                }
            }
        });

        //화면 왼쪽 위에있는 뒤로가는 화살표 이미지
        backPhn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                countDownTimer.cancel();
                Intent first = new Intent(PhoneCheckActivity.this, FirstActivity.class);
                startActivity(first);  //다음 액티비티를 열고
                PhoneCheckActivity.this.finish();     //이 액티비티를 닫음

            }
        });

        //인증요청 발송버튼을 눌렀을때
        authBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v){
                //가렸던 부분들을 보여줌
                count_send.setVisibility(View.VISIBLE);
                count_notice.setVisibility(View.VISIBLE);
                countAuthNo.setVisibility(View.VISIBLE);

                randomNum = random.nextInt(max_num_value - min_num_value + 1) + min_num_value;
                authNo.setText(randomNum+"");

                //20220122 정연호 추가
                Log.d("PhoneCheckActivity",randomNum+"");

                ArrayList<PhoneCheckSendVO> send = new ArrayList<PhoneCheckSendVO>();
                send.add(new PhoneCheckSendVO(
                        "AT"                    //알림톡 발송유형

                        //알림톡 발송 문자문자 인증번호 받기
                        , sharePref.getString("PhoneNum","")    //알림톡 받는사람 전화번호

                        , "a9edf1ad8d55c0b88b089bc48404cfbfb7912037"
                        //알림톡 프로필 아이디
                        , "00000000000000"              //발송시간
                        , "ALLIANCE_CNFM_NO"
                        , "얼라이언스 인증번호 안내\n"+randomNum+""+"\n타인에게 노출하지 마세요."      //알림톡 보낼 메세지
                        , "S"            //알림톡 실패시 문자 발송 종류
                        , "얼라이언스 인증번호 안내\n"+randomNum+""+"\n타인에게 노출하지 마세요."          //알림톡 실패시 문자로 보낼 메세지
                        , "0315269846"         //알림톡 실패시 문자 발송할때 발송자 전화번호
                        , "인증번호"          //알림톡 실패시 문자발송할때 LMS 의 제목
                        , "N"            //알림톡 실패시 문자를 보낼것인가 여부

                ));

                mPhoneCheckSend(send);    // 알림톡 발송하는 부분
                // 카운트 다운 시작
                countDown(conversionTime);
                countAuthNo.setEnabled(true);
                authChkBtn.setClickable(true);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        countDownTimer.cancel();
        Intent first = new Intent(PhoneCheckActivity.this, FirstActivity.class);
        startActivity(first);  //다음 액티비티를 열고
        PhoneCheckActivity.this.finish();     //이 액티비티를 닫음
    }

    //알림톡을 발송하는 부분
    private void mPhoneCheckSend(ArrayList< PhoneCheckSendVO > phoneCheckSendVO) {    //요청 VO
        serviceTalk.mPhoneCheckSend(phoneCheckSendVO).enqueue(new Callback<ArrayList<PhoneCheckSendVO>>() {    //앞 요청VO, CallBack 응답 VO
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call<ArrayList<PhoneCheckSendVO>> call, Response<ArrayList<PhoneCheckSendVO>> response) {  //둘다 응답 VO

            }
            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<ArrayList<PhoneCheckSendVO>> call, Throwable t) {

            }
        });
    }

    public void countDown(String time) {
        long conversionTime = 0;

        // 1000 단위가 1초
        // 60000 단위가 1분
        // 60000 * 3600 = 1시간

        String getHour = time.substring(0, 2);
        String getMin = time.substring(2, 4);
        String getSecond = time.substring(4, 6);

        // "00"이 아니고, 첫번째 자리가 0 이면 제거
        if (getHour.substring(0, 1) == "0") {
            getHour = getHour.substring(1, 2);
        }

        if (getMin.substring(0, 1) == "0") {
            getMin = getMin.substring(1, 2);
        }

        if (getSecond.substring(0, 1) == "0") {
            getSecond = getSecond.substring(1, 2);
        }

        // 변환시간
        conversionTime = Long.valueOf(getHour) * 1000 * 3600 + Long.valueOf(getMin) * 60 * 1000 + Long.valueOf(getSecond) * 1000;

        // 첫번쨰 인자 : 원하는 시간 (예를들어 30초면 30 x 1000(주기))
        // 두번쨰 인자 : 주기( 1000 = 1초)
        countDownTimer =

        new CountDownTimer(conversionTime, 1000) {

            // 특정 시간마다 뷰 변경
            public void onTick(long millisUntilFinished) {

                // 시간단위
                String hour = String.valueOf(millisUntilFinished / (60 * 60 * 1000));

                // 분단위
                long getMin = millisUntilFinished - (millisUntilFinished / (60 * 60 * 1000)) ;
                String min = String.valueOf(getMin / (60 * 1000)); // 몫

                // 초단위
                String second = String.valueOf((getMin % (60 * 1000)) / 1000); // 나머지

                // 밀리세컨드 단위
                String millis = String.valueOf((getMin % (60 * 1000)) % 1000); // 몫

                // 시간이 한자리면 0을 붙인다
                if (hour.length() == 1) {
                    hour = "0" + hour;
                }

                // 분이 한자리면 0을 붙인다
                if (min.length() == 1) {
                    min = "0" + min;
                }

                // 초가 한자리면 0을 붙인다
                if (second.length() == 1) {
                    second = "0" + second;
                }
                //count_view.setText(hour + ":" + min + ":" + second);
                count_view.setText(min + ":" + second);
            }

            // 제한시간 종료시
            public void onFinish() {
                // 변경 후
                //count_view.setText("촬영종료!");
                count_view.setText("00:00");
                count_view.setTextColor(getResources().getColorStateList(R.color.gray_808080));

                // TODO : 타이머가 모두 종료될때 어떤 이벤트를 진행할지
                //확인할 인증번호 란을 없애버려서 뭘 넣어도 인증이 안되게 한다
                commonHandler.showAlertDialog("입력 가능시간 경과","입력 가능한 3분이 경과하였습니다.\n인증 요청 버튼을 눌러 다시 인증번호를 수신하십시오.");
                authNo.setText("");
                countAuthNo.setText("");
                countAuthNo.setEnabled(false);
                authChkBtn.setClickable(false);

                count_send.setVisibility(View.GONE);
                count_notice.setVisibility(View.GONE);
                countAuthNo.setVisibility(View.GONE);

                authChkBtn.setBackground(getApplicationContext().getDrawable(R.drawable.rounded_gray_button));
            }
        };
        countDownTimer.start();
    }
}