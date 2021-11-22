package com.mobile.alliance.activity;




import android.content.Intent;

import android.content.SharedPreferences;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.mobile.alliance.api.BackPressCloseHandler;
import com.mobile.alliance.R;
import com.mobile.alliance.api.PersistentCookieStore;
import com.mobile.alliance.api.RetrofitClient;
import com.mobile.alliance.api.ServiceApi;

import com.mobile.alliance.api.CommonHandler;
import com.mobile.alliance.entity.LoginVO;


import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FirstActivity extends AppCompatActivity {

    public static OkHttpClient client;

    //뒤로가기 버튼 2번 누르면 취소 하는것
    private BackPressCloseHandler backPressCloseHandler;

    //진행바 뷰
    private ProgressBar mProgressView;
    private ServiceApi service;

    //api 이용시 쿠키전달
    private PersistentCookieStore persistentCookieStore;


    //공통
    CommonHandler commonHandler = new CommonHandler(this);


    //내부에 데이터 저장하는것
    static private String SHARE_NAME = "SHARE_PREF";
    static SharedPreferences sharePref = null;
    static SharedPreferences.Editor editor = null;

    //자동로그인 저장
    static private String LOGIN_SHARE = "LOGIN_PREF";
    static SharedPreferences loginPref = null;
    static SharedPreferences.Editor loginEditor = null;


    //0505
    static SharedPreferences cookiePrefs = null;
    private static final String COOKIE_PREFS = "CookiePrefsFile";



    EditText userId;
    EditText userPw;
    Button login_button;
    CheckBox loginAuto;

    TextView changPw;
    TextView mode2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        backPressCloseHandler = new BackPressCloseHandler(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_first);
        //내부에 데이터 저장하는것
        sharePref = getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        editor = sharePref.edit();

        //0505
        cookiePrefs = getSharedPreferences(COOKIE_PREFS, 0);
        //Log.d("cookiePrefs",cookiePrefs.getString("Cookie",""));

        //자동로그인 데이터 저장하는것
        loginPref = getSharedPreferences(LOGIN_SHARE, MODE_PRIVATE);
        loginEditor = loginPref.edit();

        //내부저장소에 값 삭제

       try {
           editor.remove("rtnYn");
           editor.remove("cmpyCd");
           editor.remove("cmpyNm");
           editor.remove("userId");
           editor.remove("userName");
           editor.remove("userGrntCd");
           editor.remove("userGrntNm");
           editor.remove("userGrdCd");
           editor.remove("userGrdNm");
           editor.remove("dcCd");
           editor.remove("dcNm");
           editor.remove("mobileGrntCd");
           editor.remove("mobileGrntNm");

           editor.commit();
       }
       catch (Exception e){}


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
        service = RetrofitClient.getClient(client).create(ServiceApi.class);

        mProgressView = (ProgressBar) findViewById(R.id.fListProgress);


        userId = (EditText)findViewById(R.id.userId);
        userId.setText(sharePref.getString("PhoneNum",""));


        userPw = (EditText)findViewById(R.id.userPw);
        login_button = findViewById(R.id.login_button);

        loginAuto         = (CheckBox)findViewById(R.id.loginAuto);    //자동로그인 체크

        changPw = (TextView) findViewById(R.id.changPw);

        SpannableString content = new SpannableString(changPw.getText().toString());

        content.setSpan(new UnderlineSpan(), 0, content.length(),0);
        changPw.setText(content);

        //비밀번호를 잊으셨습니까 를 눌렀을때
        changPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneCheck = new Intent(FirstActivity.this, PhoneCheckActivity.class);
                phoneCheck.putExtra("loginId",userId.getText().toString());
                startActivity(phoneCheck);  //다음 액티비티를 열고
                FirstActivity.this.finish();     //이 액티비티를 닫음

            }
        });

        userId.setText(loginPref.getString("loginUserId",sharePref.getString("PhoneNum","")));
        userPw.setText(loginPref.getString("loginUserPw",""));



        if(loginPref.getBoolean("loginAuto",false)) //자동로그인을 체크해서 저장된값이 있다면
        {
            userId.setText(loginPref.getString("loginUserId",sharePref.getString("PhoneNum","")));
            userPw.setText(loginPref.getString("loginUserPw",""));
            loginAuto.setChecked(true);
            startLogin(new LoginVO("A", userId.getText().toString(), userPw.getText().toString() , sharePref.getString("cnntIp","")));
            showProgress(true);

        }
        else
        {
            userId.setText(loginPref.getString("loginUserId",sharePref.getString("PhoneNum","")));
            userPw.setText(loginPref.getString("loginUserPw",""));
            loginAuto.setChecked(false);
            loginAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                    if(isChecked)   //체크 했나?
                    {
                        loginEditor.putString("loginUserId",userId.getText().toString());
                        loginEditor.putString("loginUserPw",userPw.getText().toString());
                        loginEditor.putBoolean("loginAuto",true);
                        loginEditor.apply();
                    }
                }
            });
        }

        mProgressView = (ProgressBar) findViewById(R.id.fListProgress);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(loginPref.getBoolean("loginAuto",false)) //자동로그인을 체크체크가 되어있다면
                {

                    loginEditor.putString("loginUserId",userId.getText().toString());
                    loginEditor.putString("loginUserPw",userPw.getText().toString());
                    loginEditor.putBoolean("loginAuto",true);
                    loginEditor.apply();
                }

                startLogin(new LoginVO("A", userId.getText().toString(), userPw.getText().toString() , sharePref.getString("cnntIp","")));
                showProgress(true);
            }
        });
        mode2 = (TextView) findViewById(R.id.mode2);
        mode2.setText(sharePref.getString("mode","")+"\n"+sharePref.getString("VersionCode","")+" [" + sharePref.getString("VersionName","")+"]");
    }
    @Override public void onBackPressed()
    {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }


    private void startLogin(LoginVO loginVO) {
         service.mLogin(loginVO).enqueue(new Callback<LoginVO>() {
            @Override
            public void onResponse(Call<LoginVO> call, Response<LoginVO> response) {
                 if(response.isSuccessful()) //응답값이 없다
                {

                    LoginVO result = response.body();

                    if(result.getRtnYn().equals("Y")) {


                        //내부저장소에 값 넣기
                        editor.putBoolean("isShare", true); //저장할때 true // 수정할때 flase

                        editor.putString("cmpyCd", result.getCmpyCd());
                        editor.putString("cmpyNm", result.getCmpyNm());
                        editor.putString("userId", result.getUserId());
                        editor.putString("userName", result.getUserName());
                        editor.putString("userGrntCd", result.getUserGrntCd());
                        editor.putString("userGrntNm", result.getUserGrntNm());
                        editor.putString("userGrdCd", result.getUserGrdCd());
                        editor.putString("userGrdNm", result.getUserGrdNm());
                        editor.putString("dcCd", result.getDcCd());
                        editor.putString("dcNm", result.getDcNm());
                        editor.putString("tblUserMId", result.getId());
                        editor.putString("jsessionId", result.getJsessionId());
                        editor.putString("loginTime", result.getLoginTime());
                        editor.putString("mobileGrntCd", result.getMobileGrntCd());
                        editor.putString("mobileGrntNm", result.getMobileGrntNm());

                        editor.putInt("interval", 360);  //단위는 분
                        
                        editor.apply();     //저장종료

                        if (result.getRtnYn().equals("Y")) {
                            Intent mobile_001 = new Intent(FirstActivity.this, Mobile001Activity.class);
                            startActivity(mobile_001);  //다음 액티비티를 열고
                            FirstActivity.this.finish();     //이 액티비티를 닫음
                        }

                    }
                    else
                    {
                        //showConfirmDialog("로그인 실패", result.getRtnMsg());

                        View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                        AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);
                        builder.setView(dialogView);

                        AlertDialog alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();

                        Button ok_btn = dialogView.findViewById(R.id.successBtn);
                        TextView ok_txt = dialogView.findViewById(R.id.successText);
                        ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START); //글자를 왼쪽 정렬
                        ok_txt.setText("로그인에 실패하였습니다.\n아이디 또는 암호를 확인하세요.");
                        ok_btn.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                alertDialog.dismiss();

                            }
                        });
                    }
                }
                else{
                    //showConfirmDialog("로그인 실패","로그인 응답결과가 없습니다.");

                    View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                    AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);
                    builder.setView(dialogView);

                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                    Button ok_btn = dialogView.findViewById(R.id.successBtn);
                    TextView ok_txt = dialogView.findViewById(R.id.successText);
                    ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START); //글자를 왼쪽 정렬
                    ok_txt.setText("로그인에 실패하였습니다.\n로그인 응답결과가 없습니다.");
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
            public void onFailure(Call<LoginVO> call, Throwable t) {
                //showConfirmDialog("로그인 실패","접속실패\n"+t.getMessage());

                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);
                builder.setView(dialogView);

                AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();

                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                TextView ok_txt = dialogView.findViewById(R.id.successText);
                ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START); //글자를 왼쪽 정렬
                ok_txt.setText("로그인에 실패하였습니다.\n접속실패\n"+t.getMessage());
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

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private HttpLoggingInterceptor httpLoggingInterceptor(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                android.util.Log.e("httpLog :", message + "");
            }
        });

        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    
}