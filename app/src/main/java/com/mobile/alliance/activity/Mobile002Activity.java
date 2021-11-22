package com.mobile.alliance.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobile.alliance.adapter.Mobile002PagerAdapter;
import com.mobile.alliance.api.BackPressCloseHandler;
import com.mobile.alliance.R;
import com.mobile.alliance.api.LogoutHandler;
import com.mobile.alliance.api.PersistentCookieStore;
import com.mobile.alliance.api.RetrofitClient;
import com.mobile.alliance.api.ServiceApi;
import com.mobile.alliance.api.CommonHandler;

import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;


public class Mobile002Activity extends AppCompatActivity {

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
    CommonHandler commonHandler = new CommonHandler(this);

    //뷰페이져
    private ViewPager mViewPager;
    //탭아답터
    private Mobile002PagerAdapter mobile002PagerAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobile_002);

        //내부에 데이터 저장하는것
        sharePref = getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        editor = sharePref.edit();



        //로그인을 유지해야하는가 여부가 N 일경우 로그아웃 시킴
        if(commonHandler.calTime(sharePref.getString("loginTime",""), sharePref.getInt("interval",360)).equals("N")){
            commonHandler.showLogoutDialog("로그아웃","로그인 유지가 종료되었습니다.\n다시 로그인 하시기 바랍니다.");
            ;
        }

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





        //진행바
        mProgressView = (ProgressBar) findViewById(R.id.fListProgress);

        //http통신
        service = RetrofitClient.getClient(client).create(ServiceApi.class);



        //로그아웃버튼
        ImageView logoutImg = (ImageView)findViewById(R.id.logoutImg) ;
        logoutImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* Intent activity_first = new Intent(Mobile002Activity.this, FirstActivity.class);    //지금액티비티 , 옮길 액티비티
                startActivity(activity_first);  //다음 액티비티를 열고
                Mobile002Activity.this.finish();     //이 액티비티를 닫음*/

                logoutHandler.onLogout();
            }
        });

        //화면의 컨텐츠 정의
        //물류센터 & 사람이름
        TextView dcTextView = (TextView)findViewById(R.id.confirmText);
        dcTextView.setText("["+sharePref.getString("dcNm","") + "] "+ sharePref.getString("userName",""));

        //뒤로가기
        ImageView backImg = (ImageView)findViewById(R.id.backImg);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mobile_001 = new Intent(Mobile002Activity.this, Mobile001Activity.class);
                startActivity(mobile_001);  //다음 액티비티를 열고
                Mobile002Activity.this.finish();     //이 액티비티를 닫음
            }
        });
        //탭 레이아웃

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);


        mViewPager = (ViewPager) findViewById(R.id.pager_content);

        mobile002PagerAdapter = new Mobile002PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        mViewPager.setAdapter(mobile002PagerAdapter);

        mViewPager.addOnPageChangeListener( new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mViewPager.setCurrentItem(1);   //여기에 mViewPager.setCurrentItem(1) 써주면 1번을 선택되어진다 (0, 1) 순서
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
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

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        //로그인을 유지해야하는가 여부가 N 일경우 로그아웃 시킴
        if(commonHandler.calTime(sharePref.getString("loginTime",""), sharePref.getInt("interval",360)).equals("N")){
            commonHandler.showLogoutDialog("로그아웃 onRestart","로그인 유지가 종료되었습니다.\n다시 로그인 하시기 바랍니다.");
        }
    }
}