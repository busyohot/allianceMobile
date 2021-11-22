package com.mobile.alliance.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobile.alliance.api.BackPressCloseHandler;
import com.mobile.alliance.R;
import com.mobile.alliance.api.LogoutHandler;
import com.mobile.alliance.api.PersistentCookieStore;
import com.mobile.alliance.api.RetrofitClient;
import com.mobile.alliance.api.ServiceApi;
import com.mobile.alliance.api.CommonHandler;

import org.w3c.dom.Text;

import java.net.CookieManager;
import java.net.CookiePolicy;

import lombok.SneakyThrows;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;

//로그인 성공후 첫화면. 메인화면.
public class Mobile001Activity extends AppCompatActivity {

    public static OkHttpClient client;

    //뒤로가기 버튼 2번 누르면 취소 하는것
    private BackPressCloseHandler backPressCloseHandler;
    //로그아웃
    private LogoutHandler logoutHandler;
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
    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobile_001);

        //이 화면에는 지금 메뉴가 배송 1개 밖에 없으니깐 그냥 다음화면을 넘어가자
        /*Intent mobile_002 = new Intent(Mobile001Activity.this, Mobile002Activity.class);
        startActivity(mobile_002);  //다음 액티비티를 열고
        Mobile001Activity.this.finish();     //이 액티비티를 닫음*/

        //공통
        //CommonHandler commonHandler = new CommonHandler(this);


        backPressCloseHandler = new BackPressCloseHandler(this);
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
        intent = getIntent() ;

        //내부에 데이터 저장하는것
        sharePref = getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        editor = sharePref.edit();

//로그인을 유지해야하는가 여부가 N 일경우 로그아웃 시킴
        if(commonHandler.calTime(sharePref.getString("loginTime",""), sharePref.getInt("interval",360)).equals("N")){
            commonHandler.showLogoutDialog("로그아웃","로그인 유지가 종료되었습니다.\n다시 로그인 하시기 바랍니다.");

        }




        //진행바
        mProgressView = (ProgressBar) findViewById(R.id.fListProgress);

        //http통신
        service = RetrofitClient.getClient(client).create(ServiceApi.class);

        //로그아웃버튼
        ImageView logoutImg = (ImageView)findViewById(R.id.logoutImg) ;
        logoutImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logoutHandler.onLogout();

            }
        });



        //화면의 컨텐츠 정의
        //물류센터 & 사람이름
        TextView dcTextView = (TextView)findViewById(R.id.confirmText);
        dcTextView.setText("["+sharePref.getString("dcNm","") + "] "+ sharePref.getString("userName",""));

        TextView mode=(TextView) findViewById(R.id.mode);
        mode.setText(sharePref.getString("mode","")+"\n"+sharePref.getString("VersionCode","")+" [" + sharePref.getString("VersionName","")+"]");
        //가운데 버튼 view2
        View view2 = (View)findViewById(R.id.view2);
        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mobile_002 = new Intent(Mobile001Activity.this, Mobile002Activity.class);
                startActivity(mobile_002);  //다음 액티비티를 열고
                Mobile001Activity.this.finish();     //이 액티비티를 닫음
            }
        });


        //맨 아래 사무실 연결하기 이미지
        ImageView callImg = (ImageView)findViewById(R.id.callImg);
        callImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:01024892979"));
                startActivity(intent);
            }
        });


    }

    @Override public void onBackPressed()
    {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }






    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }




}