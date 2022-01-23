package com.mobile.alliance.entity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.mobile.alliance.api.BackPressCloseHandler;
import com.mobile.alliance.R;
import com.mobile.alliance.api.CommonHandler;
import com.mobile.alliance.api.PersistentCookieStore;
import com.mobile.alliance.api.RetrofitClient;
import com.mobile.alliance.api.ServiceApi;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URLDecoder;

import lombok.SneakyThrows;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class StandardActivity extends AppCompatActivity {

    public static OkHttpClient client;

    //뒤로가기 버튼 2번 누르면 취소 하는것
    private BackPressCloseHandler backPressCloseHandler;

    //진행바 뷰
    private ProgressBar mProgressView;
    private ServiceApi service;

    //api 이용시 쿠키전달
    private PersistentCookieStore persistentCookieStore;

    //내부에 데이터 저장하는것
    static private String SHARE_NAME = "SHARE_PREF";
    static SharedPreferences sharePref = null;
    static SharedPreferences.Editor editor = null;

//공통
    //private CommonHandler commonHandler;
    //공통
    CommonHandler commonHandler = new CommonHandler(this);

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobile_001);


        //공통
        //CommonHandler commonHandler = new CommonHandler(this);


        backPressCloseHandler = new BackPressCloseHandler(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //키보드 뜰때 입력창 가리지 않고 화면을 위로 올리기

        //api 이용시 쿠키전달
        PersistentCookieStore cookieStore = new PersistentCookieStore(this);
        CookieManager cookieManager  = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);

        client = new OkHttpClient
                .Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .addInterceptor(commonHandler.httpLoggingInterceptor())
                .build();
        intent = getIntent() ;

        //내부에 데이터 저장하는것
        sharePref = getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        editor = sharePref.edit();
        //진행바
        mProgressView = (ProgressBar) findViewById(R.id.fListProgress);

        //http통신
        service = RetrofitClient.getClient(client).create(ServiceApi.class);

        
    }

    @Override public void onBackPressed()
    {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }


    private void startLogin(LoginVO loginVO) {
         service.mLogin(loginVO).enqueue(new Callback<LoginVO>() {

            @SneakyThrows @Override
            public void onResponse(Call<LoginVO> call, Response<LoginVO> response) {

                if(response.isSuccessful()) //응답값이 없다
                {
                    LoginVO result = response.body();
                    if(result.getRtnYn().equals("Y")) {
                        //내부저장소에 있는걸 불러오기
                        sharePref.getString("cnntIp","");
                        commonHandler.showAlertDialog("로그인 성공", result.getUserId() + " / " + result.getUserName()+"\nip : " + sharePref.getString("cnntIp","")+"\nphoneNum : " + sharePref.getString("phoneNum",""));
                    }
                    else
                    {
                        commonHandler.showAlertDialog("로그인 실패", result.getRtnMsg());
                    }
                }
                else{
                    //20220120 정연호 수정. was에서 [500 internal server error] excpition발생시 오류추적번호 나오게 변경
                    //commonHandler.showAlertDialog("로그인 실패","로그인 응답결과가 없습니다.");
                    commonHandler.showAlertDialog("로그인 실패",response.code() +"\n"+ response.message()+"\n\n"+
                            URLDecoder.decode(response.errorBody().string(),"UTF-8"));
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<LoginVO> call, Throwable t) {

                commonHandler.showAlertDialog("로그인 접속 실패","접속실패\n"+t.getMessage());
                showProgress(false);
            }
        });
    }

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}