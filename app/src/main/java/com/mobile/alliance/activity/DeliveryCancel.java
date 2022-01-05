package com.mobile.alliance.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mobile.alliance.R;
import com.mobile.alliance.api.BackPressCloseHandler;
import com.mobile.alliance.api.CommonHandler;
import com.mobile.alliance.api.LogoutHandler;
import com.mobile.alliance.api.PersistentCookieStore;
import com.mobile.alliance.api.RetrofitClient;
import com.mobile.alliance.api.ServiceApi;
import com.mobile.alliance.api.TextValueHandler;
import com.mobile.alliance.entity.deliveryCancel.DeliveryCancelSaveVO;
import com.mobile.alliance.entity.deliveryCancel.DeliveryCancelVO;


import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;

import lombok.SneakyThrows;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryCancel extends AppCompatActivity {

    String  TAG = "DeliveryCancel";
    String instMobileMId;
    String tblSoMId;

    //로그아웃
    private LogoutHandler logoutHandler;
    private static TextValueHandler textValueHandler = new TextValueHandler();
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

    RadioButton deliveryCancelRadio[];
    TextView deliveryCancelCode[];
    TableLayout deliveryCancelTable;

    EditText deliveryCancelMemo;

    ImageView deliveryCancelBack;


    Button deliveryCancelBtn;

    String deliveryCancelCodeValue="";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_cancel);

        try
        {
            instMobileMId = getIntent().getStringExtra("instMobileMId");
        }
        catch(Exception e)
        {
            instMobileMId = "";
        }
        try
        {
            tblSoMId         =   getIntent().getStringExtra("tblSoMId");
        }
        catch(Exception e)
        {
            tblSoMId = "";
        }

        Log.d(TAG,"instMobileMId : " + instMobileMId);
        Log.d(TAG,"tblSoMId : " + tblSoMId);

        //내부에 데이터 저장하는것
        sharePref = getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        editor = sharePref.edit();
        //진행바
        mProgressView = (ProgressBar) findViewById(R.id.deliveryCancelProgress);

        logoutHandler = new LogoutHandler(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //키보드 뜰때 입력창 가리지 않고 화면을 위로 올리기

        //api 이용시 쿠키전달
        PersistentCookieStore cookieStore = new PersistentCookieStore(this);
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
        client = new OkHttpClient

                .Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .addInterceptor(commonHandler.httpLoggingInterceptor())
                .build();
        serviceTalk = RetrofitClient.getTalk(client).create(ServiceApi.class);  //알림톡용
        service = RetrofitClient.getClient(client).create(ServiceApi.class);    //시스템용




        deliveryCancelMemo = (EditText)findViewById(R.id.deliveryDelayMemo);
        deliveryCancelMemo.setVisibility(View.INVISIBLE);
        deliveryCancelTable = (TableLayout)findViewById(R.id.deliveryCancelTable);
        //화면의 왼쪽 위 뒤로가기 이미지
        deliveryCancelBack = (ImageView)findViewById(R.id.deliveryCancelBack);
        //화면의 왼쪽 위 뒤로가기 이미지를 클릭했을때
        deliveryCancelBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deliveryCancelBack();
            }
        });
        //배송취소 버튼
        deliveryCancelBtn = (Button) findViewById(R.id.deliveryCancelBtn);

        //배송취소 버튼을 눌렀을때
        deliveryCancelBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag") @Override
            public void onClick(View v){

                // 1. 3개의 라디오 버튼중 1개는 체크를 해야함
                // 2. 내용(메모) 입력되어야함--필수아님

                //목록 중 체크한것
                Integer deliveryCancelCnt=0;

                for(int q=0;q<deliveryCancelRadio.length;q++)
                {
                    //체크 한것 대상
                    if(deliveryCancelRadio[q].isChecked()){
                        deliveryCancelCnt++;
                        deliveryCancelCodeValue = deliveryCancelCode[q].getText().toString();

                    }
                }

                Integer deliveryCancelMemoLength=0;
                deliveryCancelMemoLength = deliveryCancelMemo.getText().toString().trim().length();

                if(deliveryCancelCnt == 0)  //목록중 체크를 한것이 없음
                {
                    commonHandler.showAlertDialog("선택하세요","배송 취소 사유중 1개를 선택하세요!");
                    return;
                }

                else if(deliveryCancelCodeValue.equals("ETC") && deliveryCancelMemoLength == 0)   //기타 일경우 메모는 필수
                {
                    commonHandler.showAlertDialog("기타 메모 입력","기타를 선택하였다면 메모를 입력하세요!");
                    return;
                }

                //컨펌(버튼 두개중 하나 선택)
                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DeliveryCancel.this);
                builder.setView(dialogView);

                android.app.AlertDialog alertDialog = builder.create();
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
                        //1. 배송완료 처리 저장하기

                        showProgress(true);
                        DeliveryCancelSaveDb();

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

        //화면 뜨자마자 SP 수행시켜서 배송취소 사유 선택용 목록을 불러옴
        deliveryCancelCombo(new DeliveryCancelVO(
                sharePref.getString("cmpyCd","A")
                ,"COMM"
                ,"DLVY_CNCL_RQST_RESN"
                ,""
                ,"Y"

        ));
        showProgress(true);

    }   //onCreate





    //화면 뜨자마자 SP 수행시켜서 배송취소 사유 선택용 목록을 불러오는 메쏘드
    private void deliveryCancelCombo(DeliveryCancelVO deliveryCancelVO) {
        service.deliveryCancelCombo(deliveryCancelVO).enqueue(new Callback<List<DeliveryCancelVO>>() {

            @Override
            public void onResponse(Call<List<DeliveryCancelVO>> call, Response<List<DeliveryCancelVO>> response) {

                if(response.isSuccessful()) //응답값이 있다
                {
                    List<DeliveryCancelVO> result = response.body();

                    if(result.size() > 0){


                        //배송취소 라디오 버튼
                        deliveryCancelRadio       = new RadioButton[result.size()];        //사유선택
                        deliveryCancelCode     = new TextView[result.size()];        //사유 코드

                        //배송취소 목록 을 반복문 돌려서 화면에 라디오 버튼으로 표시
                        for(int i = 0; i<result.size(); i++) {

                            TableRow tRow = new TableRow(getApplicationContext());     // 테이블 ROW 생성

                            deliveryCancelRadio[i] = new RadioButton(getApplicationContext());
                            deliveryCancelRadio[i].setButtonTintList(ColorStateList
                                    .valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue_1e6ce3)));
                            deliveryCancelRadio[i].setChecked(true);
                            deliveryCancelRadio[i].setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
                            deliveryCancelRadio[i].setChecked(false);


                            deliveryCancelCode[i] = new TextView(getApplicationContext());
                            deliveryCancelCode[i].setVisibility(View.GONE);

                            deliveryCancelRadio[i].setText(result.get(i).getComboNm());
                            deliveryCancelRadio[i].setTextSize(16);
                            deliveryCancelCode[i].setText(result.get(i).getComboCd());


                            int finalI = i;
                            deliveryCancelRadio[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v){
                                    deliveryCancelRadio[finalI].setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.blue_1e6ce3)));

                                    for (int j = 0; j < result.size(); j++) {

                                        if(finalI != j){
                                            deliveryCancelRadio[j].setChecked(false);
                                            deliveryCancelRadio[j].setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));

                                        }

                                    }
                                    if(deliveryCancelCode[finalI].getText().equals("ETC"))
                                    {
                                        deliveryCancelMemo.setVisibility(View.VISIBLE);
                                    }
                                    else{
                                        deliveryCancelMemo.setVisibility(View.INVISIBLE);
                                        deliveryCancelMemo.setText("");
                                    }



                                }
                            });
                            tRow.addView(deliveryCancelRadio[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            tRow.addView(deliveryCancelCode[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));


                            deliveryCancelTable.addView(tRow, new TableLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                        }

                    }
                    else
                    {
                        commonHandler.showAlertDialog("배송 취소 사유 목록 조회 실패", "조회 0건");
                    }
                    showProgress(false);
                }
                else{
                    commonHandler.showAlertDialog("배송 취소 사유 목록 조회 실패","응답결과가 없습니다.");
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<List<DeliveryCancelVO>> call, Throwable t) {
                commonHandler.showAlertDialog("배송 취소 사유 목록 조회 실패","접속실패\n"+t.getMessage());
                showProgress(false);
            }
        });
    }



    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        deliveryCancelBack();
    }
    private void deliveryCancelBack(){

        View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryCancel.this);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
        TextView ok_txt = dialogView.findViewById(R.id.confirmText);
        ok_txt.setTextSize(20);
        ok_txt.setTypeface(null, Typeface.BOLD);
        ok_txt.setText("배송취소 화면을 나가시겠습니까?");
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

    //배송취소 결과를 DB에 저장
    public void DeliveryCancelSaveDb()
    {
        //배송취소 결과를 DB에 저장 SP
        DeliveryCancelSave(new DeliveryCancelSaveVO(
                instMobileMId
                ,   tblSoMId
                ,   deliveryCancelCodeValue
                ,   deliveryCancelMemo.getText().toString().trim()
                ,   sharePref.getString("tblUserMId","")

        ));
    }


    //배송취소 저장하기
    private void DeliveryCancelSave(DeliveryCancelSaveVO deliveryCancelSaveVO) {
        service.mDeliveryCancelSave(deliveryCancelSaveVO).enqueue(new Callback<DeliveryCancelVO>() {

            @Override
            public void onResponse(Call<DeliveryCancelVO> call, Response<DeliveryCancelVO> response) {

                if(response.isSuccessful()) //응답값이 있다
                {
                    DeliveryCancelVO result = response.body();
                    if(result.getRtnYn().equals("Y"))
                    {
                        View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                        AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryCancel.this);
                        builder.setView(dialogView);

                        AlertDialog alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();

                        Button ok_btn = dialogView.findViewById(R.id.successBtn);
                        TextView ok_txt = dialogView.findViewById(R.id.successText);
                        ok_txt.setText("배송취소 완료");
                        ok_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                finish();
                            }
                        });
                    }
                    else
                    {
                        View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                        AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryCancel.this);
                        builder.setView(dialogView);

                        AlertDialog alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();

                        Button ok_btn = dialogView.findViewById(R.id.successBtn);
                        TextView ok_txt = dialogView.findViewById(R.id.successText);
                        ok_txt.setText("배송취소 처리 실패\n\n"+result.getRtnMsg() == null ? "처리 실패 " : result.getRtnMsg());
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryCancel.this);
                    builder.setView(dialogView);

                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                    Button ok_btn = dialogView.findViewById(R.id.successBtn);
                    TextView ok_txt = dialogView.findViewById(R.id.successText);
                    ok_txt.setText("배송취소 처리 실패\n\n응답결과가 없음");
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
            public void onFailure(Call<DeliveryCancelVO> call, Throwable t) {

                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryCancel.this);
                builder.setView(dialogView);

                AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();

                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                TextView ok_txt = dialogView.findViewById(R.id.successText);
                ok_txt.setText("배송취소 처리 실패\n\n접속실패\n"+t.getMessage());
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




    //로딩바
    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }



}