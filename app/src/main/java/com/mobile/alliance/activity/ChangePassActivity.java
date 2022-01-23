package com.mobile.alliance.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.alliance.R;
import com.mobile.alliance.api.BackPressCloseHandler;
import com.mobile.alliance.api.CommonHandler;
import com.mobile.alliance.api.LogoutHandler;
import com.mobile.alliance.api.PersistentCookieStore;
import com.mobile.alliance.api.RetrofitClient;
import com.mobile.alliance.api.ServiceApi;
import com.mobile.alliance.entity.LoginPwChangeVO;
import com.mobile.alliance.entity.LoginVO;


import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.SneakyThrows;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassActivity extends AppCompatActivity {
    public static OkHttpClient client;

    //뒤로가기 버튼 2번 누르면 취소 하는것
    private BackPressCloseHandler backPressCloseHandler;
    //로그아웃
    private LogoutHandler logoutHandler;
    //진행바 뷰
    private ProgressBar mProgressView;
    private ServiceApi service; //알림톡용

    //api 이용시 쿠키전달
    private PersistentCookieStore persistentCookieStore;

    //내부에 데이터 저장하는것
    static private String SHARE_NAME = "SHARE_PREF";
    static SharedPreferences sharePref = null;
    static SharedPreferences.Editor editor = null;

    //공통
    CommonHandler commonHandler;
    Intent intent;

    EditText passNoChk,passNew,passNewChk,passIdChk;
    ImageView backPhn2;
    Button passChkBtn;

    String loginIdValue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        commonHandler = new CommonHandler(this);
        loginIdValue = getIntent().getStringExtra("loginId");
        Toast.makeText(getApplicationContext(), "ChangePassActivity loginId : " + loginIdValue,Toast.LENGTH_SHORT);
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

        service = RetrofitClient.getClient(client).create(ServiceApi.class);
        intent = getIntent();

        //내부에 데이터 저장하는것
        sharePref = getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        editor = sharePref.edit();

        passNoChk = (EditText) findViewById(R.id.passNoChk);
        passNoChk.setText(sharePref.getString("PhoneNum", ""));
        passNoChk.setEnabled(false);

        passIdChk = (EditText) findViewById(R.id.passIdChk);
        passIdChk.setText(loginIdValue);

        //Toast.makeText(getApplicationContext(), "ChangePassActivity2 loginId : " + passIdChk.getText().toString(),Toast.LENGTH_SHORT);

        backPhn2 = (ImageView) findViewById(R.id.backPhn2);

        passNew     =   (EditText) findViewById(R.id.passNew);  //비밀번호
        passNewChk  =   (EditText) findViewById(R.id.passNewChk);   //비밀번호 확인

        passChkBtn = (Button) findViewById(R.id.passChkBtn);

        mProgressView = (ProgressBar) findViewById(R.id.pwProg);

        //화면 왼쪽 위에있는 뒤로가는 화살표 이미지
        backPhn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent phoneCheck = new Intent(ChangePassActivity.this, PhoneCheckActivity.class);
                phoneCheck.putExtra("loginId",loginIdValue);
                startActivity(phoneCheck);  //다음 액티비티를 열고

                ChangePassActivity.this.finish();     //이 액티비티를 닫음
            }
        });

        //화면 아래 확인 버튼
        passChkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(!passNew.getText().toString().equals(passNewChk.getText().toString()))    //비밀번호와 비밀번호 확인이 같으면 비밀번호를 바꾸는 SP를 수행함
                {
                    commonHandler.showAlertDialog("신규 비밀번호 다름","신규 비밀번호 입력과 신규 비밀번호 확인이 같지 않습니다");
                    return;
                }
                if(textValidate(passNew.getText().toString()))  //비밀번호가 입력규칙에 맞으면
                {
                    if(textValidate(passNewChk.getText().toString()))  //비밀번호확인이 입력규칙에 맞으면
                    {
                        if(passNew.getText().toString().equals(passNewChk.getText().toString()))    //비밀번호와 비밀번호 확인이 같으면 비밀번호를 바꾸는 SP를 수행함
                        {

                            mChangePwSend(new LoginPwChangeVO(  sharePref.getString("cmpyCd","A")
                                                                , loginIdValue
                                                                , sharePref.getString("PhoneNum", "")
                                                                , passNew.getText().toString() ));
                            showProgress(true);
                        }
                        else
                        {
                            commonHandler.showAlertDialog("신규 비밀번호 다름","신규 비밀번호 입력과 신규 비밀번호 확인이 같지 않습니다");
                        }
                    }
                    else
                    {
                        commonHandler.showAlertDialog("신규 비밀번호 확인","비밀번호는 영어,숫자,특수문자의 조합으로 4자리 이상 12자리 이하 입니다");
                    }
                }
                else
                {
                    commonHandler.showAlertDialog("신규 비밀번호 입력","비밀번호는 영어,숫자,특수문자의 조합으로 4자리 이상 12자리 이하 입니다.");
                }
            }
        });

        passNewChk.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    passChkBtn.setClickable(true);
                    passChkBtn.setBackground(getApplicationContext().getDrawable(R.drawable.rounded_blue_button));
                } else {
                    passChkBtn.setClickable(false);
                    passChkBtn.setBackground(getApplicationContext().getDrawable(R.drawable.rounded_gray_button));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent phoneCheck = new Intent(ChangePassActivity.this, PhoneCheckActivity.class);
        phoneCheck.putExtra("loginId",loginIdValue);
        startActivity(phoneCheck);  //다음 액티비티를 열고
        ChangePassActivity.this.finish();     //이 액티비티를 닫음
    }

    public boolean textValidate(String str) {
        String Passwrod_PATTERN = "^((?=.*[a-zA-Z]+)|(?=.*[0-9]+)|(?=.*\\W)).{4,12}$";
        //String Passwrod_PATTERN = "^((?=.*[a-zA-Z]+)|(?=.*[0-9]+)).{4,12}$";
        Pattern pattern = Pattern.compile(Passwrod_PATTERN);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }


    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void mChangePwSend(LoginPwChangeVO loginPwChangeVO){
        service.mChangePwSend(loginPwChangeVO).enqueue(new Callback<LoginVO>() {
            @SneakyThrows @Override
            public void onResponse(Call<LoginVO> call, Response<LoginVO> response){
                if(response.isSuccessful()) //응답값이 있다
                {
                    LoginVO result = response.body();
                    if(
                        result.getRtnYn().equals("Y"))
                        {
                            View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                            AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassActivity.this);
                            builder.setView(dialogView);

                            AlertDialog alertDialog = builder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.show();

                            Button ok_btn = dialogView.findViewById(R.id.successBtn);
                            TextView ok_txt = dialogView.findViewById(R.id.successText);
                            ok_txt.setText("비밀번호 성공");
                            ok_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                    Intent first = new Intent(ChangePassActivity.this, FirstActivity.class);
                                    startActivity(first);  //다음 액티비티를 열고
                                    ChangePassActivity.this.finish();     //이 액티비티를 닫음
                                }
                            });
                        }
                        else
                        {
                            commonHandler.showAlertDialog("비밀번호 변경실패", result.getRtnMsg());
                        }
                    }
                    else
                    {
                        //20220120 정연호 수정. was에서 [500 internal server error] excpition발생시 오류추적번호 나오게 변경
                        //commonHandler.showAlertDialog("비밀번호 변경실패", "비밀번호 변경 응답결과가 없습니다.");
                        commonHandler.showAlertDialog("비밀번호 변경실패", response.code() +"\n"+ response.message()+"\n\n"+
                                URLDecoder.decode(response.errorBody().string(),"UTF-8"));
                    }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<LoginVO> call, Throwable t){
                commonHandler.showAlertDialog("비밀번호 변경실패", "접속실패\n" + t.getMessage());
                showProgress(false);
            }
        });
    }
}