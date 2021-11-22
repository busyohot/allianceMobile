package com.mobile.alliance.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.DecimalFormat;

import android.net.Uri;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.SpannableString;

import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.alliance.R;

import com.mobile.alliance.api.CommonHandler;
import com.mobile.alliance.api.LogoutHandler;
import com.mobile.alliance.api.PersistentCookieStore;
import com.mobile.alliance.api.RetrofitClient;
import com.mobile.alliance.api.ServiceApi;
import com.mobile.alliance.entity.delivery.DeliveryDetailSrchVO;
import com.mobile.alliance.entity.delivery.DeliveryLiftCancelVO;
import com.mobile.alliance.entity.delivery.DeliveryLiftSaveVO;
import com.mobile.alliance.entity.delivery.DeliveryTelVO;
import com.mobile.alliance.entity.delivery.DeliveryVO;
import com.mobile.alliance.fragment.DeliveryListFragment;


import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;


import lombok.SneakyThrows;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryListDetail extends AppCompatActivity {
    public static OkHttpClient client;
    public static Activity _DeliveryListDetail; //다른액티비티에서 이 액티비티를 제어하기위해
    //뒤로가기 버튼 2번 누르면 취소 하는것
    //private BackPressCloseHandler backPressCloseHandler;
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


    NestedScrollView nestedScrollView;
    Button deliveryDetailLiftBtn;
    ImageView deliveryDetailBack;
    View dekiveryDetailTitleBack;
    TextView deliveryDetailState;
    TextView deliveryDetailName;
    TextView deliveryDetailHp;
    TextView deliveryDetailAddr1;
    TextView deliveryDetailAddr2;
    TextView deliveryDetailMsg;
    ImageView deliveryDetailImg01;
    ImageView deliveryDetailImg02;
    ImageView deliveryDetailImg03;
    ImageView deliveryDetailImg04;
    TextView deliveryDetailSoNo;
    TextView deliveryDetailInstMobileMId;
    //TextView deliveryDetailInstMobileDId;
    TextView deliveryDetailRcptCost;

    Button deliveryArmBtn;  //알림톡 뉴버튼


    View sendTalkView;
    View sendSmsView;

    ConstraintLayout con;

    TableLayout detailTablelayout;


    TextView detailTelCnt;   //통화횟수 카운트

        //오른쪽 상차 버튼
    Button deliverylRightLiftBtn;
    //오른쪽 상차 닫기버튼
    Button deliverylRightLiftClose;

    //상차 취소 버튼
    Button deliveryDetailLiftCancelBtn;

    View deliveryBtnArea;
    Button deliveryNoCmpllBtn;  //미마감 버튼

    Button  deliveryCmplBtn;    //배송완료 버튼

    String instMobileMIdValue;
    String no;
    Button deliveryCancleBtn,deliveryDelayBtn;

    
    TextView deliveryDetailInstDt;          //배송일(시공일) 표시해주기
    ImageView detailMap;
    String editYn;              //입력 가능한지, 배송일+1일의 10시가 지나서 입력이 불가능 한지를 표시. Y 입력-상차, 미마감,배송완료 등 가능 N 불가능

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _DeliveryListDetail = DeliveryListDetail.this;//다른액티비티에서 이 액티비티를 제어하기위해


        setContentView(R.layout.activity_delivery_list_detail);

        logoutHandler = new LogoutHandler(this);
        //backPressCloseHandler = new BackPressCloseHandler(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //키보드 뜰때 입력창 가리지 않고 화면을 위로 올리기

        //api 이용시 쿠키전달
        PersistentCookieStore cookieStore = new PersistentCookieStore(this);
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);

        client = new OkHttpClient
                .Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .addInterceptor(commonHandler.httpLoggingInterceptor())
                .build();


        //내부에 데이터 저장하는것
        sharePref = getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        editor = sharePref.edit();
        //진행바
        mProgressView = (ProgressBar) findViewById(R.id.activity_delivery_list_detail_progress);

        //http통신
        service = RetrofitClient.getClient(client).create(ServiceApi.class);

        instMobileMIdValue = getIntent().getStringExtra("instMobileMId");

        no =  getIntent().getStringExtra("no");

        mDeliveryDetailSrch(new DeliveryDetailSrchVO(
                    instMobileMIdValue
                ,   sharePref.getString("tblUserMId","")
                ,   sharePref.getString("mobileGrntCd","")
                )
        );
        showProgress(true);


        //뷰 설정
        //탑 색상
        deliveryDetailBack = (ImageView) findViewById(R.id.deliveryDetailBack);

        deliveryDetailBack.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows @Override
            public void onClick(View view) {
                //배송목록 갱신하기
                DeliveryListFragment deliveryListFragment = (DeliveryListFragment)DeliveryListFragment._DeliveryListFragment;
                deliveryListFragment.changeDate();
                finish();
            }
        });
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        deliveryDetailLiftBtn = (Button) findViewById(R.id.deliveryDetailLiftBtn);      //상차완료버튼
        deliveryDetailLiftBtn.setVisibility(View.GONE); //상차버튼 없애기
        deliveryDetailLiftBtn.setEnabled(false);    //상차버튼 비활성화 하기(상차버튼이 안보였다가 보여지면 비활성화 상태 이어야 하며 체크를 1개라도 하면 활성으로 바뀐다)

        dekiveryDetailTitleBack = (View) findViewById(R.id.dekiveryDetailTitleBack);
        deliveryDetailState = (TextView) findViewById(R.id.deliveryDetailState);
        deliveryDetailName = (TextView) findViewById(R.id.deliveryDetailName);
        deliveryDetailHp = (TextView) findViewById(R.id.deliveryDetailHp);
        deliveryDetailAddr1 = (TextView) findViewById(R.id.deliveryDetailAddr1);
        //deliveryDetailAddr1.setSelected(true);
        deliveryDetailAddr2 = (TextView) findViewById(R.id.deliveryDetailAddr2);
        //deliveryDetailAddr2.setSelected(true);
        deliveryDetailMsg = (TextView) findViewById(R.id.deliveryDetailMsg);
        //deliveryDetailMsg.setSelected(true);
        deliveryDetailImg01 = (ImageView) findViewById(R.id.deliveryDetailImg01);
        deliveryDetailImg02 = (ImageView) findViewById(R.id.deliveryDetailImg02);
        deliveryDetailImg03 = (ImageView) findViewById(R.id.deliveryDetailImg03);
        deliveryDetailImg04 = (ImageView) findViewById(R.id.deliveryDetailImg04);
        deliveryDetailSoNo = (TextView) findViewById(R.id.deliveryDetailSoNo);
        deliveryDetailInstMobileMId = (TextView) findViewById(R.id.deliveryDetailInstMobileMId);
        //deliveryDetailInstMobileDId = (TextView) findViewById(R.id.deliveryDetailInstMobileDId);
        deliveryDetailRcptCost = (TextView) findViewById(R.id.deliveryDetailRcptCost);

        con = (ConstraintLayout) findViewById(R.id.con);

        detailTablelayout = (TableLayout) findViewById(R.id.detailTablelayout);


        sendTalkView = (View) findViewById(R.id.sendTalkView);
        sendSmsView = (View) findViewById(R.id.sendSmsView);


        deliveryArmBtn = (Button) findViewById((R.id.deliveryArmBtn));   //알림톡 뉴 버튼

        detailTelCnt = (TextView) findViewById(R.id.detailTelCnt);

        deliverylRightLiftBtn = (Button) findViewById(R.id.deliverylRightLiftBtn);  //오른쪽 상차버튼
        deliverylRightLiftBtn.setVisibility(View.GONE); //오른쪽 상차 버튼 가리기

        deliverylRightLiftClose = (Button) findViewById(R.id.deliverylRightLiftClose);  //오른쪽 상차닫기버튼
        deliverylRightLiftClose.setVisibility(View.GONE);

        deliveryDetailLiftCancelBtn = (Button) findViewById(R.id.deliveryDetailLiftCancelBtn);  //상차취소버튼
        deliveryDetailLiftCancelBtn.setVisibility(View.GONE);   //상차취소버튼 안보이기

        deliveryBtnArea = (View)findViewById(R.id.deliveryBtnArea);

        deliveryNoCmpllBtn = (Button)findViewById(R.id.deliveryNoCmpllBtn);     //미마감 버튼

        deliveryCmplBtn = (Button) findViewById(R.id.deliveryCmplBtn);  //배송완료 버튼

        deliveryDetailInstDt = (TextView) findViewById(R.id.deliveryDetailInstDt);  //배송일


        deliveryCancleBtn  = (Button) findViewById(R.id.deliveryCancleBtn);  //배송취소버튼
        deliveryCancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast toast = Toast.makeText(getApplicationContext(), "배송 취소\n개발예정입니다. 조금만 기다려주세요.", Toast.LENGTH_SHORT);   //0 short , 1 long
                toast.setGravity(Gravity.CENTER|Gravity.BOTTOM,0, 20);
                toast.show();

            }

        });

        deliveryDelayBtn = (Button) findViewById(R.id.deliveryDelayBtn);  //배송연기버튼
        deliveryDelayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast toast = Toast.makeText(getApplicationContext(), "배송 연기\n개발예정입니다. 조금만 기다려주세요.", Toast.LENGTH_SHORT);   //0 short , 1 long
                toast.setGravity(Gravity.CENTER|Gravity.BOTTOM,0, 20);
                toast.show();

            }

        });




        detailMap = (ImageView) findViewById(R.id.detailMap);



    }

    @SneakyThrows @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //배송목록 갱신하기
        DeliveryListFragment deliveryListFragment = (DeliveryListFragment)DeliveryListFragment._DeliveryListFragment;
        deliveryListFragment.changeDate();
        finish();
        //backPressCloseHandler.onBackPressed();
    }

    //화면 열자마자 조회하는 부분
    private void mDeliveryDetailSrch(DeliveryDetailSrchVO dliveryDetailSrchVO) {
        service.mDeliveryDetailSrch(dliveryDetailSrchVO).enqueue(new Callback<List<DeliveryVO>>() {

            @SuppressLint({"NewApi", "ResourceAsColor", "LongLogTag"})
            @Override
            public void onResponse(Call<List<DeliveryVO>> call, Response<List<DeliveryVO>> response) {

                if (response.isSuccessful() && response.body().size() > 0) //응답값이 있다 && 조회한게 1개 이상
                {
                    List<DeliveryVO> result = response.body();

                    editYn = result.get(0).getEditYn();         //입력 가능한지, 배송일+1일의 10시가 지나서 입력이 불가능 한지를 표시. Y 입력-상차, 미마감,배송완료 등 가능 N 불가능

                    detailTelCnt.setText(result.get(0).getTelCnt());
                    detailMap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            startActivity(new Intent (getApplicationContext(), MapActivity.class)
                                    .putExtra("instMobileMId",result.get(0).getInstMobileMId())
                                    .putExtra("listAddr1",result.get(0).getAddr1())
                                    .putExtra("no",no)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));  //다음 액티비티를 열고


                        }
                    });


                    //탑의 배송상태 색상과 글자 변경
                    //리스트 아답터에 넣는부분

                    deliveryDetailInstMobileMId.setText(result.get(0).getInstMobileMId());

                    //deliveryDetailInstMobileDId.setText(result.get(0).getInstMobileDId());
                    int stateColor = 0;
                            /*
                            회색 4000	배송모바일 확정
                            배송중 회색 5000	해피콜 완료
                            배송중 초록 6999	부분상차(완료)
                            배송중 초록 7000	상차완료
                            빨강 8000	배송완료(미마감)
                            파랑 9999	배송완료

                             */
                    if (result.get(0).getDlvyStatCd().equals("4000")) {

                        stateColor = Color.parseColor("#696969");


                        deliveryCmplBtn.setClickable(false);
                        deliveryCmplBtn.setBackground(getApplicationContext().getDrawable(
                                R.drawable.rounded_gray_button));
                        deliveryNoCmpllBtn.setText("미마감");

                        deliveryNoCmpllBtn.setClickable(false);
                        deliveryNoCmpllBtn.setBackground(getApplicationContext().getDrawable(
                                R.drawable.rounded_gray_button));
                        deliveryCmplBtn.setText("배송완료");

                    }
                    if (result.get(0).getDlvyStatCd().equals("5000")) {

                        stateColor = Color.parseColor("#696969");


                        deliveryCmplBtn.setClickable(true);
                        deliveryCmplBtn.setBackground(getApplicationContext().getDrawable(
                                R.drawable.rounded_blue_button));
                        deliveryNoCmpllBtn.setText("미마감");

                        deliveryNoCmpllBtn.setClickable(true);
                        deliveryNoCmpllBtn.setBackground(getApplicationContext().getDrawable(
                                R.drawable.rounded_blue_button));
                        deliveryCmplBtn.setText("배송완료");

                    }






                    else if (result.get(0).getDlvyStatCd().equals("6999") || result.get(0).getDlvyStatCd().equals("7000")) {
                        stateColor = Color.parseColor("#20B2AA");


                        deliveryCmplBtn.setClickable(true);
                        deliveryCmplBtn.setBackground(getApplicationContext().getDrawable(
                                R.drawable.rounded_blue_button));
                        deliveryNoCmpllBtn.setText("미마감");

                        deliveryNoCmpllBtn.setClickable(true);
                        deliveryNoCmpllBtn.setBackground(getApplicationContext().getDrawable(
                                R.drawable.rounded_blue_button));
                        deliveryCmplBtn.setText("배송완료");

                    } else if (result.get(0).getDlvyStatCd().equals("8000")) {
                        stateColor = Color.parseColor("#FF0000");
                        deliveryCmplBtn.setClickable(false);

                        deliveryCmplBtn.setBackground(getApplicationContext().getDrawable(R.drawable.rounded_gray_button));
                        deliveryNoCmpllBtn.setText("미마감 취소");

                    } else if (result.get(0).getDlvyStatCd().equals("9999")) {
                        stateColor = Color.parseColor("#0000FF");
                        deliveryNoCmpllBtn.setClickable(false);

                        deliveryNoCmpllBtn.setBackground(getApplicationContext().getDrawable(
                                R.drawable.rounded_gray_button));
                        deliveryCmplBtn.setText("배송완료 취소");
                       

                    }



                   dekiveryDetailTitleBack.setBackgroundColor(stateColor);
                    deliveryDetailState.setText(result.get(0).getDlvyStatNm());
                    deliveryDetailName.setText(result.get(0).getAcptEr());
                    deliveryDetailHp.setText(result.get(0).getAcptTel2());

                    //핸드폰 번호를 터치했을때 - SP수행해야함
                    deliveryDetailHp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            detailTelCnt.setText(String.valueOf(Integer.parseInt(detailTelCnt.getText().toString()) + 1));

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_DIAL);
                            Uri tel = Uri.parse("tel:" + deliveryDetailHp.getText());
                            intent.setData(tel);
                            //intent.setData(Uri.parse("tel:114"));
                            startActivity(intent);


                            mDeliveryListDetailTelUpdate(new DeliveryTelVO(

                                    deliveryDetailInstMobileMId.getText().toString()

                                    , sharePref.getString("tblUserMId", "")
                                    , sharePref.getString("mobileGrntCd", "")

                            ));
                            showProgress(true);


                        }
                    });


                    deliveryDetailAddr1.setText(result.get(0).getAddr1());
                    deliveryDetailAddr2.setText(result.get(0).getAddr2());

                    deliveryDetailAddr1.setOnClickListener(new TextView.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DeliveryListDetail.this);
                            builder.setView(dialogView);

                            android.app.AlertDialog alertDialog = builder.create();
                            //alertDialog.setCancelable(false);
                            alertDialog.show();

                            ImageView ok_img = dialogView.findViewById(R.id.successImg);
                            ok_img.setVisibility(View.GONE);



                            TextView ok_title = dialogView.findViewById(R.id.successTitle);
                            ok_title.setVisibility(View.VISIBLE);
                            ok_title.setTextSize(18);
                            ok_title.setTypeface(null, Typeface.BOLD);
                            ok_title.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);   //왼쪽정렬
                            ok_title.setText("주소 (3초후 닫힘)");

                            TextView ok_txt = dialogView.findViewById(R.id.successText);
                            ok_txt.setTextSize(18);
                            ok_txt.setTypeface(null, Typeface.NORMAL);
                            ok_txt.setText(result.get(0).getAddr1() + "\n" + (result.get(0).getAddr2() == null ? "" : result.get(0).getAddr2())          );
                            ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);   //왼쪽정렬

                            Button ok_btn = dialogView.findViewById(R.id.successBtn);
                            ok_btn.setText("복사 후 닫기");
                            ok_btn.setOnClickListener(new View.OnClickListener() {
                                @SneakyThrows
                                @Override
                                public void onClick(View v) {

                                    //alertDialog.dismiss();
                                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("주소", result.get(0).getAddr1() + "\n" + (result.get(0).getAddr2() == null ? "" : result.get(0).getAddr2()));
                                    clipboard.setPrimaryClip(clip);
                                    alertDialog.dismiss();
                                    Toast.makeText(DeliveryListDetail.this,"복사완료",Toast.LENGTH_SHORT).show();
                                }
                            });
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    alertDialog.dismiss();
                                }
                            },3000);
                        }

                    });

                    deliveryDetailMsg.setText(result.get(0).getDlvyRqstMsg());



                    //알람톡, 문자 보내기 부분

                    //알림톡 뉴 버튼
                    deliveryArmBtn.setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(editYn.equals("N"))
                            {
                                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryListDetail.this);
                                builder.setView(dialogView);

                                AlertDialog alertDialog = builder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();

                                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                                TextView ok_txt = dialogView.findViewById(R.id.successText);
                                ok_txt.setText("배송일("+deliveryDetailInstDt.getText()+")의 다음날 오전10시가 지나 입력할 수 없습니다.");
                                ok_btn.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {

                                        alertDialog.dismiss();
                                        return;
                                    }
                                });
                                return;
                            }

                            Intent sendTalk = new Intent(DeliveryListDetail.this, SendTalkActivity.class);


                            sendTalk.putExtra("instMobileMId", result.get(0).getInstMobileMId());
                            sendTalk.putExtra("dlvyStatCd", result.get(0).getDlvyStatCd());

                            //2021-11-22 정연호 추가. 시공일 전송
                            sendTalk.putExtra("instDt", result.get(0).getInstDt());
                            
                            startActivity(sendTalk);

                        }

                    });


                    //미마감 버튼 클릭했을떄
                    deliveryNoCmpllBtn.setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            if(editYn.equals("N"))
                            {
                                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryListDetail.this);
                                builder.setView(dialogView);

                                AlertDialog alertDialog = builder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();

                                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                                TextView ok_txt = dialogView.findViewById(R.id.successText);
                                ok_txt.setText("배송일("+deliveryDetailInstDt.getText()+")의 다음날 오전10시가 지나 입력할 수 없습니다.");
                                ok_btn.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {

                                        alertDialog.dismiss();
                                        return;
                                    }
                                });
                                return;
                            }

                            else if(result.get(0).getDlvyStatCd().equals("9999")){   //배송완료 완료 상태에서 상차하기를 안하고 미마감 하려고 할때


                                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryListDetail.this);
                                builder.setView(dialogView);

                                AlertDialog alertDialog = builder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();

                                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                                TextView ok_txt = dialogView.findViewById(R.id.successText);
                                ok_txt.setText("배송완료이므로 미마감 처리를 할수 없습니다.");
                                ok_btn.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {

                                        alertDialog.dismiss();
                                        return;
                                    }
                                });



                                return;
                            }


                           else if(result.get(0).getDlvyStatCd().equals("4000")){   //배송준비 완료상태에서 해피콜을 안하고 미마감 하려고 할때

                                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
                                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryListDetail.this);
                                builder.setView(dialogView);

                                AlertDialog alertDialog = builder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();

                                Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
                                TextView ok_txt = dialogView.findViewById(R.id.confirmText);
                                ok_txt.setTextSize(20);
                                ok_txt.setTypeface(null, Typeface.BOLD);
                                ok_txt.setText("해피콜, 상차 완료 후 진행하세요");
                                ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                ok_btn.setOnClickListener(new View.OnClickListener() {
                                    @SneakyThrows @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });

                                Button no_btn = dialogView.findViewById(R.id.confirmBtnNo);
                                no_btn.setVisibility(View.GONE);

                            }
                             else   if(result.get(0).getDlvyStatCd().equals("5000")){   //해피콜 완료 상태에서 상차하기를 안하고 미마감 하려고 할때

                                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
                                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryListDetail.this);
                                builder.setView(dialogView);

                                AlertDialog alertDialog = builder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();

                                Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
                                TextView ok_txt = dialogView.findViewById(R.id.confirmText);
                                ok_txt.setTextSize(20);
                                ok_txt.setTypeface(null, Typeface.BOLD);
                                ok_txt.setText("상차처리를 하지 않고 미마감 처리를 하시겠습니까?\n품목은 모두 미상차 됩니다.");
                                ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                ok_btn.setOnClickListener(new View.OnClickListener() {
                                    @SneakyThrows @Override
                                    public void onClick(View v) {


                                        alertDialog.dismiss();
                                        //미마감 처리 하기
                                        Intent noCmpl =
                                                new Intent(DeliveryListDetail.this, NoCmplActivity.class);
                                        noCmpl.putExtra("instMobileMId",
                                                result.get(0).getInstMobileMId()); //모바일 헤더 아이디
                                        noCmpl.putExtra("soNo", result.get(0).getSoNo());
                                        startActivity(noCmpl);

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
                            else
                            {
                                //상태가 미마감 상태면 미마감 취소 화면을 연다
                                if(result.get(0).getDlvyStatCd().equals("8000"))    //8000 미마감
                                {
                                    Intent noCmplCancel = new Intent(DeliveryListDetail.this, NoCmplCancelActivity.class);
                                    noCmplCancel.putExtra("instMobileMId", result.get(0).getInstMobileMId()); //모바일 헤더 아이디
                                    noCmplCancel.putExtra("soNo", result.get(0).getSoNo());
                                    startActivity(noCmplCancel);
                                }
                                else {
                                    Intent noCmpl =  new Intent(DeliveryListDetail.this, NoCmplActivity.class);
                                    noCmpl.putExtra("instMobileMId", result.get(0).getInstMobileMId()); //모바일 헤더 아이디
                                    noCmpl.putExtra("soNo", result.get(0).getSoNo());
                                    startActivity(noCmpl);
                                }
                            }
                        }
                    });
                    //배송완료 버튼 클릭했을떄
                    deliveryCmplBtn.setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(editYn.equals("N"))
                            {
                                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryListDetail.this);
                                builder.setView(dialogView);

                                AlertDialog alertDialog = builder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();

                                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                                TextView ok_txt = dialogView.findViewById(R.id.successText);
                                ok_txt.setText("배송일("+deliveryDetailInstDt.getText()+")의 다음날 오전10시가 지나 입력할 수 없습니다.");
                                ok_btn.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {

                                        alertDialog.dismiss();
                                        return;
                                    }
                                });
                                return;
                            }

                            else if(result.get(0).getDlvyStatCd().equals("8000")){   // 미마감

                                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryListDetail.this);
                                builder.setView(dialogView);

                                AlertDialog alertDialog = builder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();

                                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                                TextView ok_txt = dialogView.findViewById(R.id.successText);
                                ok_txt.setText("미마감이므로 배송완료 처리를 할 수 없습니다.");
                                ok_btn.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                        return;
                                    }
                                });
                                return;
                            }

                            else if(result.get(0).getDlvyStatCd().equals("4000")){   //배송준비 완료상태에서 해피콜을 안하고 미마감 하려고 할때

                                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
                                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryListDetail.this);
                                builder.setView(dialogView);

                                AlertDialog alertDialog = builder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();

                                Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
                                TextView ok_txt = dialogView.findViewById(R.id.confirmText);
                                ok_txt.setTextSize(20);
                                ok_txt.setTypeface(null, Typeface.BOLD);
                                ok_txt.setText("해피콜, 상차 완료 후 진행하세요");
                                ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                ok_btn.setOnClickListener(new View.OnClickListener() {
                                    @SneakyThrows @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });
                                Button no_btn = dialogView.findViewById(R.id.confirmBtnNo);
                                no_btn.setVisibility(View.GONE);

                            }

                            else if(result.get(0).getDlvyStatCd().equals("5000")){   //해피콜 완료 상태에서 상차하기를 안하고 미마감 하려고 할때


                                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
                                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryListDetail.this);
                                builder.setView(dialogView);

                                AlertDialog alertDialog = builder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();

                                Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
                                TextView ok_txt = dialogView.findViewById(R.id.confirmText);
                                ok_txt.setTextSize(20);
                                ok_txt.setTypeface(null, Typeface.BOLD);
                                ok_txt.setText("상차처리를 하지 않고 배송완료 처리를 하시겠습니까?\n품목은 모두 미상차 됩니다.");
                                ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                ok_btn.setOnClickListener(new View.OnClickListener() {
                                    @SneakyThrows @Override
                                    public void onClick(View v) {


                                        alertDialog.dismiss();
                                        //배송완료 처리 하기
                                        Intent yesCmpl =
                                                new Intent(DeliveryListDetail.this, YesCmplActivity.class);
                                        yesCmpl.putExtra("instMobileMId",
                                                result.get(0).getInstMobileMId()); //모바일 헤더 아이디
                                        yesCmpl.putExtra("soNo", result.get(0).getSoNo());
                                        startActivity(yesCmpl);

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
                            else
                            {



                                //상태가 배송완료 상태면 배송완료 취소 화면을 연다
                                if(result.get(0).getDlvyStatCd().equals("9999"))    //9999 배송완료
                                {
                                   Intent yesCmplCancel = new Intent(DeliveryListDetail.this, YesCmplCancelActivity.class);
                                    yesCmplCancel.putExtra("instMobileMId", result.get(0).getInstMobileMId()); //모바일 헤더 아이디
                                    yesCmplCancel.putExtra("soNo", result.get(0).getSoNo());
                                    startActivity(yesCmplCancel);
                                }
                                else {

                                    Intent yesCmpl =
                                            new Intent(DeliveryListDetail.this, YesCmplActivity.class);
                                    yesCmpl.putExtra("instMobileMId",
                                            result.get(0).getInstMobileMId()); //모바일 헤더 아이디
                                    yesCmpl.putExtra("soNo", result.get(0).getSoNo());
                                    startActivity(yesCmpl);
                                }

                            }
                        }

                    });




                    //오더내용부분 시작

                    //INST_CTGR 시공 카테고리 : 가구 / 가전/ 서비스  1000가구/2000가전/3000서비스 (오더속성창의 제일 처음에 보여지는 부분)
                    if (result.get(0).getInstCtgr().equals("1000")) {
                        deliveryDetailImg01.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.badge_1_2));
                    } else if (result.get(0).getInstCtgr().equals("2000")) {
                        deliveryDetailImg01.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.badge_1_3));
                    } else if (result.get(0).getInstCtgr().equals("3000")) {
                        deliveryDetailImg01.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.badge_1_1));
                    }

                    //INST_TYPE       시공유형 - 일반 / 익일   (table : NORMAL 정상배송 / NEXTDAY 익일배송 )(오더속성창의 두번째에보여지는부분) 일반=정상배송, 익일=익일배송
                    if (result.get(0).getInstType().equals("NORMAL")) {
                        deliveryDetailImg02.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.badge_1_4));
                    } else if (result.get(0).getInstType().equals("NEXTDAY")) {
                        deliveryDetailImg02.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.badge_1_5));
                    }
                    //SO_TYPE 주문유형 : 일반      //반품        //교환
                    /*      1000	일반오더,   2000	반품오더,   2500	교환오더,   4000	AS오더   */

                    if (result.get(0).getSoType().equals("1000")) {
                        deliveryDetailImg03.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.badge_2_2));
                    } else if (result.get(0).getSoType().equals("2000")) {
                        deliveryDetailImg03.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.badge_2_3));
                    } else if (result.get(0).getSoType().equals("2500")) {
                        deliveryDetailImg03.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.badge_2_4));
                    } else if (result.get(0).getSoType().equals("4000")) {
                        deliveryDetailImg03.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.badge_2_1));
                    }
                    //SEAT_TYPE       시공좌석유형  1인 / 2인

                    if (result.get(0).getSeatType().equals("1111")) {
                        deliveryDetailImg04.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.badge_3_1));
                    } else if (result.get(0).getSeatType().equals("2222")) {
                        deliveryDetailImg04.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.badge_3_2));
                    }
                    deliveryDetailInstDt.setText(result.get(0).getInstDt().substring(0,4)+"년 " + result.get(0).getInstDt().substring(4,6)+"월 "+ result.get(0).getInstDt().substring(6,8)+"일");    //배송일(시공일)
                    deliveryDetailSoNo.setText(result.get(0).getSoNo());        //오더번호



                    // 숫자 셋째자리에 , 찍기
                    DecimalFormat formatter = new DecimalFormat("###,###");


                    deliveryDetailRcptCost.setText(formatter.format(Integer.parseInt(result.get(0).getRcptCost() == null ? "0" : result.get(0).getRcptCost()))+ " 원");

                    List<DeliveryVO> deliveryVO = new ArrayList<>();

                    CheckBox checkBox[] = new CheckBox[result.size()];

                    TextView prodNm[] = new TextView[result.size()];


                    ImageView playo[] = new ImageView[result.size()];
                    ImageView imageViews[] = new ImageView[result.size()];

                    TextView qty[] = new TextView[result.size()];
                    TextView uploadQty[] = new TextView[result.size()];

                    TextView instMobileDId[] = new TextView[result.size()];

                    TextView line[] = new TextView[result.size()];
                    detailTablelayout.removeAllViews();

                    for (int i = 0; i < result.size(); i++) {
                        TableRow tRow = new TableRow(getApplicationContext());     // 테이블 ROW 생성


                        ViewGroup.LayoutParams pp = new ViewGroup.LayoutParams(720, LinearLayout.LayoutParams.WRAP_CONTENT);
                        checkBox[i] = new CheckBox(getApplicationContext());




                        SpannableString spannableCheckBoxString = new SpannableString(result.get(i).getProdCd()+" "+ result.get(i).getProdNm().toString());
                        int start = 0;
                        int end = spannableCheckBoxString.toString().indexOf(" ")+0;
                        spannableCheckBoxString.setSpan(new ForegroundColorSpan(Color.parseColor("#228B22")),start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //특정위치만 색 변경
                        spannableCheckBoxString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //특정위치만 폰트 스타일 변경
                        spannableCheckBoxString.setSpan(new RelativeSizeSpan(1.0f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //특정위치만 크기변경
                        spannableCheckBoxString.setSpan(new UnderlineSpan(), start,end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        checkBox[i].append(spannableCheckBoxString);

                        checkBox[i].setLayoutParams(pp);
                        checkBox[i].setPadding(0, 20, 0, 20);

                        ViewGroup.LayoutParams ppCd = new ViewGroup.LayoutParams(720, LinearLayout.LayoutParams.WRAP_CONTENT);

                        prodNm[i] = new TextView(getApplicationContext());

                        //회색 4000	배송모바일 확정
                        //배송중 회색 5000	해피콜 완료
                        //이면서 미상차 인것은 미상차라고 표시
                        if (   (!result.get(0).getDlvyStatCd().equals("4000") && !result.get(0).getDlvyStatCd().equals("5000"))
                                &&
                                !result.get(i).getProdLiftCmplYn().equals("Y")      //품목이 상차안한것 - 미상차 인것
                        ){
                            prodNm[i].setText("[미상차] "+ result.get(i).getProdCd()+" "+ result.get(i).getProdNm().toString());

                            prodNm[i].setTextColor(Color.GRAY);

                            prodNm[i].setTypeface(null,Typeface.NORMAL);

                            SpannableString spannableString = new SpannableString(prodNm[i].getText());
                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6702")),0,5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //특정위치만 색 변경
                            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //특정위치만 폰트 스타일 변경
                            spannableString.setSpan(new RelativeSizeSpan(1.3f), 0, 0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //특정위치만 크기변경
                            prodNm[i].setText(spannableString);
                        }
                        else if (  (!result.get(0).getDlvyStatCd().equals("4000") && !result.get(0).getDlvyStatCd().equals("5000"))
                                &&
                                result.get(i).getProdLiftCmplYn().equals("Y")      //품목이 상차한것 - 상차 인것
                         )
                        {
                            prodNm[i].setText("[상차완료] "+result.get(i).getProdCd()+" "+ result.get(i).getProdNm().toString());
                            prodNm[i].setTextColor(Color.BLACK);
                            prodNm[i].setTypeface(null,Typeface.BOLD);
                        }
                        else
                        {


                            SpannableString spannableProdNmString = new SpannableString(result.get(i).getProdCd()+" "+ result.get(i).getProdNm().toString());
                            int startProdNm = 0;
                            int endProdNm = spannableProdNmString.toString().indexOf(" ")+0;
                            spannableProdNmString.setSpan(new ForegroundColorSpan(Color.parseColor("#228B22")),startProdNm,endProdNm, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //특정위치만 색 변경
                            spannableProdNmString.setSpan(new StyleSpan(Typeface.BOLD), startProdNm, endProdNm, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //특정위치만 폰트 스타일 변경
                            spannableProdNmString.setSpan(new RelativeSizeSpan(1.0f), startProdNm, endProdNm, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //특정위치만 크기변경
                            spannableProdNmString.setSpan(new UnderlineSpan(), startProdNm,endProdNm,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            prodNm[i].append(spannableCheckBoxString);



                        }

                        prodNm[i].setLayoutParams(ppCd);
                        prodNm[i].setPadding(0, 20, 0, 20);

                        //플레이 아이콘 이미지
                        playo[i] = new ImageView(getApplicationContext());
                        playo[i].setForegroundGravity(Gravity.CENTER | Gravity.CENTER);
                        playo[i].setPadding(10, 20, 10, 0);
                        playo[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_playo));

                        imageViews[i] = new ImageView(getApplicationContext());
                        imageViews[i].setForegroundGravity(Gravity.CENTER | Gravity.CENTER);
                        imageViews[i].setPadding(10, 20, 10, 0);
                        imageViews[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_infoblue));
                        //imageViews[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_info));

                         String prodUrl = (result.get(i).getInstImgPath() == null || result.get(i).getInstImgPath().trim() == "" )  ? "" : result.get(i).getInstUrl();

                         if(result.get(i).getInstImgPath() == null || result.get(i).getInstImgPath().trim() == "")
                         {
                             imageViews[i].setVisibility(View.INVISIBLE);
                         }



                        int finalI = i;
                        imageViews[i].setOnClickListener(new ImageView.OnClickListener() {
                            @Override
                            public void onClick(View v) {




                                if(URLUtil.isValidUrl(prodUrl))
                                {
                                    if(prodUrl != null || !prodUrl.toString().equals(""))
                                    {
                                        //확대화면 보는 Activity를 오픈함
                                        Intent imageActivity = new Intent(DeliveryListDetail.this, ImageActivity.class);
                                        imageActivity.putExtra("imageUri",prodUrl);
                                        imageActivity.putExtra("imageNo", finalI +"");
                                        imageActivity.putExtra("imageType","download");

                                        startActivity(imageActivity);  //다음 액티비티를 열고
                                    }
                                    else
                                    {
                                        commonHandler.showAlertDialog("이미지 열기 실패","이미지 경로가 존재하지 않습니다.");
                                    }
                                }
                                else
                                {
                                    commonHandler.showAlertDialog("웹 주소오류","올바른 웹 주소 형식이 아닙니다.");
                                }




                            }
                        });


                        String instUrl = (result.get(i).getInstUrl() == null || result.get(i).getInstUrl().trim() == "" )  ? "" : result.get(i).getInstUrl();

                        if(result.get(i).getInstUrl() == null || result.get(i).getInstUrl().trim() == "" )
                        {
                            playo[i].setVisibility(View.INVISIBLE);
                        }
                        playo[i].setOnClickListener(new ImageView.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                /*
                                프로토콜이 없으면 붙이기
                                URLUtil.guessUrl("url")
                                올바른 url 형식인지 체크
                                URLUtil.isValidUrl("url")
                                프로토콜을 붙이지 않은 상태에서 url 인지 체크 (검색엔진에 사용할지 분기처리할 때 사용)
                                Patterns.WEB_URL.matcher(url).matches()
                                */

                                if(URLUtil.isValidUrl(instUrl))
                                {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(instUrl));
                                    startActivity(browserIntent);
                                }
                                else
                                {
                                    commonHandler.showAlertDialog("웹 주소오류","올바른 웹 주소 형식이 아닙니다.");
                                }
                            }

                        });



                        if( result.get(i).getMtoYn().equals("Y"))
                        {
                            qty[i] = new TextView(getApplicationContext());
                            qty[i].setText(result.get(i).getQty().toString() + " 개"+"\nMTO");
                            qty[i].setPadding(0, 20, 0, 20);
                            qty[i].setGravity(Gravity.CENTER | Gravity.RIGHT);
                            qty[i].setTypeface(null, Typeface.BOLD);

                            SpannableString spannableQtyString = new SpannableString(qty[i].getText());
                            int startQty = spannableQtyString.toString().indexOf("\n")+1;
                            int endQty = spannableQtyString.length();


                             spannableQtyString.setSpan(new BackgroundColorSpan(Color.parseColor("#FFAF0A")),startQty,endQty, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //특정위치만 배경색 변경

                            spannableQtyString.setSpan(new StyleSpan(Typeface.BOLD), startQty, endQty, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //특정위치만 폰트 스타일 변경
                            spannableQtyString.setSpan(new RelativeSizeSpan(1.0f), startQty, endQty, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //특정위치만 크기변경
                            //spannableQtyString.setSpan(new UnderlineSpan(), startQty,endQty,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            qty[i].setText(spannableQtyString);


                        }
                        else {
                            qty[i] = new TextView(getApplicationContext());
                            qty[i].setText(result.get(i).getQty().toString() + " 개"+"\n");
                            qty[i].setPadding(0, 20, 0, 20);
                            qty[i].setGravity(Gravity.CENTER | Gravity.RIGHT);
                            qty[i].setTypeface(null, Typeface.BOLD);
                        }

                        uploadQty[i] = new TextView(getApplicationContext());
                        uploadQty[i].setText(result.get(i).getQty().toString());

                        instMobileDId[i] = new TextView(getApplicationContext());
                        instMobileDId[i].setText(result.get(i).getInstMobileDId());
                        instMobileDId[i].setVisibility(View.GONE);


                        checkBox[i].setOnClickListener(new CheckBox.OnClickListener() {
                            @Override

                            public void onClick(View v) {


                                //체크한 갯수를 체크해서 상차완료 버튼을 활성화 할지 말지를 결정함
                                int chkCnt = 0;

                                for (int t = 0; t < result.size(); t++) {
                                    if (checkBox[t].isChecked()) {
                                        chkCnt++;
                                    }
                                }
                                if (chkCnt > 0) {
                                    deliveryDetailLiftBtn.setEnabled(true);
                                    deliveryDetailLiftBtn.setBackground(getApplicationContext().getDrawable(R.drawable.rounded_delivery_lift_01_01));
                                } else {
                                    deliveryDetailLiftBtn.setEnabled(false);
                                    deliveryDetailLiftBtn.setBackground(getApplicationContext().getDrawable(R.drawable.rounded_delivery_lift_01_02));
                                }
                                deliveryDetailLiftBtn.getResources().getColorStateList(R.color.white);
                            }
                        });

                        line[i] = new TextView(getApplicationContext());
                        line[i].setHeight(2);
                        line[i].setBackgroundColor(R.color.ebebeb);

                        tRow.addView(instMobileDId[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT, 1));



                        //5000 알림톡 완료상태일때만 목록에서 체크를 할수있음

                        deliveryDetailLiftBtn.setVisibility(View.GONE); //상차완료 버튼 안보이게 함
                        deliveryBtnArea.setVisibility(View.GONE);
                        checkBox[i].setVisibility(View.GONE);
                        //mto[i].setVisibility(View.GONE);

                        tRow.addView(checkBox[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                        tRow.addView(prodNm[i], new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                        tRow.addView(imageViews[i], new TableRow.LayoutParams(320, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                        tRow.addView(playo[i], new TableRow.LayoutParams(320,  LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                        tRow.addView(qty[i], new TableRow.LayoutParams(320,  LinearLayout.LayoutParams.WRAP_CONTENT, 1));



                        detailTablelayout.addView(tRow, new TableLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT));

                        detailTablelayout.addView(line[i], new TableLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));




                    }   //end of for (int i = 0; i < result.size(); i++)

                    //오른쪽 상차버튼, 오른쪽 상차닫기버튼 은 해피콜 완료 상태일때만 보여줌 5000
                    if (result.get(0).getDlvyStatCd().equals("5000")) {
                        deliverylRightLiftBtn.setVisibility(View.VISIBLE);
                        deliverylRightLiftClose.setVisibility(View.GONE);

                    }
                    else
                    {
                        deliverylRightLiftBtn.setVisibility(View.GONE);
                        deliverylRightLiftClose.setVisibility(View.GONE);
                    }


                    //오른쪽 상차버튼을 누르면 원래 가리고있던 체크를 보여주고 prodNm 은 가림
                    deliverylRightLiftBtn.setOnClickListener(new Button.OnClickListener() {
                        @Override

                        public void onClick(View v) {

                            if(editYn.equals("N"))
                            {
                                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryListDetail.this);
                                builder.setView(dialogView);

                                AlertDialog alertDialog = builder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();

                                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                                TextView ok_txt = dialogView.findViewById(R.id.successText);
                                ok_txt.setText("배송일("+deliveryDetailInstDt.getText()+")의 다음날 오전10시가 지나 입력할 수 없습니다.");
                                ok_btn.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {

                                        alertDialog.dismiss();
                                        return;
                                    }
                                });
                                return;
                            }

                            for(int o=0;o<result.size();o++)
                            {
                                prodNm[o].setVisibility(View.GONE);
                                //mto2[o].setVisibility(View.GONE);
                                checkBox[o].setVisibility(View.VISIBLE);
                                //mto[o].setVisibility(View.VISIBLE);
                            }
                            deliveryDetailLiftBtn.setVisibility(View.VISIBLE); //상차완료 버튼 버튼 보이게 함
                            deliveryBtnArea.setVisibility((View.VISIBLE));      //상차완료 버튼 의 뷰
                            deliverylRightLiftClose.setVisibility(View.VISIBLE);    //오른쪽 상차 닫기 버튼 보이게 함
                            deliverylRightLiftBtn.setVisibility(View.GONE);     //오른쪽 상차 버튼 닫기
                        }
                    });
                    //오른쪽 상차 닫기 버튼을 누르면
                    deliverylRightLiftClose.setOnClickListener(new Button.OnClickListener() {
                        @Override

                        public void onClick(View v) {
                            for(int o=0;o<result.size();o++)
                            {
                                prodNm[o].setVisibility(View.VISIBLE);
                                //mto2[o].setVisibility(View.VISIBLE);
                                checkBox[o].setVisibility(View.GONE);
                                ///mto[o].setVisibility(View.GONE);
                            }
                            deliveryDetailLiftBtn.setVisibility(View.GONE); //상차완료 버튼 버튼 보이게 함
                            deliveryBtnArea.setVisibility(View.GONE);
                            deliverylRightLiftClose.setVisibility(View.GONE);
                            deliverylRightLiftBtn.setVisibility(View.VISIBLE);     //오른쪽 상차 버튼 보이기
                        }
                    });


                    //6999 부분상차, 7000 상차
                    //이면 상차취소 버튼을 보여줌

                    //상차취소 버튼
                    if (   result.get(0).getDlvyStatCd().equals("6999") || result.get(0).getDlvyStatCd().equals("7000")) {
                        deliveryDetailLiftCancelBtn.setVisibility(View.VISIBLE);
                        deliveryBtnArea.setVisibility(View.VISIBLE);
                        //상차취소버튼을 눌렀을때
                        deliveryDetailLiftCancelBtn.setOnClickListener(new Button.OnClickListener() {
                            @Override

                            public void onClick(View v) {
                                if(editYn.equals("N"))
                                {
                                    View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryListDetail.this);
                                    builder.setView(dialogView);

                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.setCancelable(false);
                                    alertDialog.show();

                                    Button ok_btn = dialogView.findViewById(R.id.successBtn);
                                    TextView ok_txt = dialogView.findViewById(R.id.successText);
                                    ok_txt.setText("배송일("+deliveryDetailInstDt.getText()+")의 다음날 오전10시가 지나 입력할 수 없습니다.");
                                    ok_btn.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View v) {

                                            alertDialog.dismiss();
                                            return;
                                        }
                                    });
                                    return;
                                }
                                //여러개 중에 상차 한것만 상차취소를 한다
                                //6999	부분상차, 7000 상차

                                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
                                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryListDetail.this);
                                builder.setView(dialogView);

                                AlertDialog alertDialog = builder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();

                                Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
                                TextView ok_txt = dialogView.findViewById(R.id.confirmText);
                                ok_txt.setTextSize(20);
                                ok_txt.setTypeface(null, Typeface.BOLD);
                                ok_txt.setText("상차취소 처리를 하시겠습니까?");
                                ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                ok_btn.setOnClickListener(new View.OnClickListener() {
                                    @SneakyThrows @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                        showProgress(true);

                                        mDeliveryLiftCancel(new DeliveryLiftCancelVO(
                                                        "DEL"
                                                        , result.get(0).getInstMobileMId()
                                                        , "0"
                                                        , "0"
                                                        , sharePref.getString("tblUserMId", "")
                                                )
                                        );    // 상차취소
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
                    else{
                        deliveryDetailLiftCancelBtn.setVisibility(View.GONE);   //상차취소버튼
                        deliveryBtnArea.setVisibility(View.GONE);       //상차버튼
                    }

                    //상차완료 버튼 //상차처리 버튼
                    //상차 완료 버튼을 눌렀을때
                    deliveryDetailLiftBtn.setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if(editYn.equals("N"))
                            {
                                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryListDetail.this);
                                builder.setView(dialogView);

                                AlertDialog alertDialog = builder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();

                                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                                TextView ok_txt = dialogView.findViewById(R.id.successText);
                                ok_txt.setText("배송일("+deliveryDetailInstDt.getText()+")의 다음날 오전10시가 지나 입력할 수 없습니다.");
                                ok_btn.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {

                                        alertDialog.dismiss();
                                        return;
                                    }
                                });
                                return;
                            }


                            View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
                            AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryListDetail.this);
                            builder.setView(dialogView);

                            AlertDialog alertDialog = builder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.show();

                            Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
                            TextView ok_txt = dialogView.findViewById(R.id.confirmText);
                            ok_txt.setTextSize(20);
                            ok_txt.setTypeface(null, Typeface.BOLD);
                            ok_txt.setText("상차처리 처리를 하시겠습니까?");
                            ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            ok_btn.setOnClickListener(new View.OnClickListener() {
                                @SneakyThrows @Override
                                public void onClick(View v) {

                                    alertDialog.dismiss();

                                    //전체 갯수와 체크한 갯수를 구한다
                                    deliveryVO.clear();
                                    for (int t = 0; t < result.size(); t++) {
                                        if (checkBox[t].isChecked()) {
                                            deliveryVO.add(result.get(t));
                                        }
                                    }
                                    showProgress(true);

                                    //전체갯수 = 체크갯수 - 상차완료 7000
                                    //전체갯수 > 체크갯수 - 부분상차(완료) 6999
                                    //이건 SP에서 체크해서 넣는다.

                                    ArrayList<DeliveryLiftSaveVO> liftSave = new ArrayList<DeliveryLiftSaveVO>();
                                    for (int k = 0; k < deliveryVO.size(); k++) {
                                        liftSave.add(new DeliveryLiftSaveVO(
                                                "INS"
                                                , deliveryVO.get(k).getInstMobileMId()
                                                , deliveryVO.get(k).getInstMobileDId()

                                                , deliveryVO.get(k).getQty()

                                                , sharePref.getString("tblUserMId", "")
                                        ));
                                    }
                                    mDeliveryLiftSave(liftSave);    // 상차처리
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
                } else {
                    nestedScrollView.setVisibility(View.GONE);
                    deliveryDetailLiftBtn.setVisibility(View.GONE);
                    deliveryBtnArea.setVisibility(View.GONE);
                    commonHandler.showFinishAlertDialog("배송 상세조회 실패", "배송 상세조회 결과가 없습니다.", "Y");
                }
                showProgress(false);
            }



            @Override
            public void onFailure(Call<List<DeliveryVO>> call, Throwable t) {
                commonHandler.showAlertDialog("배송 상세조회 실패", "접속실패\n" + t.getMessage());
                showProgress(false);
            }
        });
    }

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    //상차완료 하기
    private void mDeliveryLiftSave(ArrayList<DeliveryLiftSaveVO> deliveryLiftSaveVO) {    //요청 VO

        service.mDeliveryLiftSave(deliveryLiftSaveVO).enqueue(new Callback<DeliveryLiftSaveVO>() {    //앞 요청VO, CallBack 응답 VO

            @Override
            public void onResponse(Call<DeliveryLiftSaveVO> call, Response<DeliveryLiftSaveVO> response) {  //둘다 응답 VO

                if (response.isSuccessful()) //응답값이 있다
                {
                    DeliveryLiftSaveVO result = response.body();
                    if (result.getRtnYn().equals("Y")) {

                        View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                        AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryListDetail.this);
                        builder.setView(dialogView);

                        AlertDialog alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();

                        Button ok_btn = dialogView.findViewById(R.id.successBtn);
                        TextView ok_txt = dialogView.findViewById(R.id.successText);
                        ok_txt.setText("상차 완료 성공");
                        ok_btn.setOnClickListener(new View.OnClickListener() {

                            @SneakyThrows @Override
                            public void onClick(View v) {

                                alertDialog.dismiss();
                                //배송목록 갱신하기
                                DeliveryListFragment deliveryListFragment = (DeliveryListFragment)DeliveryListFragment._DeliveryListFragment;
                                deliveryListFragment.changeDate();
                                deliveryDetailSrch();
                            }
                        });

                    }
                    else
                    {
                        commonHandler.showAlertDialog("상차 완료 실패", "응답메세지\n" + result.getRtnMsg());
                    }
                    showProgress(false);

                } else {
                    commonHandler.showAlertDialog("상차 완료 실패", "응답결과가 없습니다.");


                }
                showProgress(false);
            }


            @Override
            public void onFailure(Call<DeliveryLiftSaveVO> call, Throwable t) {

                commonHandler.showAlertDialog("상차 완료 실패", "접속실패\n" + t.getMessage());

                showProgress(false);
            }


        });
    }



    //상차취소 하기
    private void mDeliveryLiftCancel(DeliveryLiftCancelVO deliveryLiftCancelVO) {    //요청 VO

        service.mDeliveryLiftCancel(deliveryLiftCancelVO).enqueue(new Callback<DeliveryLiftCancelVO>() {    //앞 요청VO, CallBack 응답 VO

            @Override
            public void onResponse(Call<DeliveryLiftCancelVO> call, Response<DeliveryLiftCancelVO> response) {  //둘다 응답 VO

                if (response.isSuccessful()) //응답값이 있다
                {
                    DeliveryLiftCancelVO result = response.body();
                    if (result.getRtnYn().equals("Y")) {

                        View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                        AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryListDetail.this);
                        builder.setView(dialogView);

                        AlertDialog alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();

                        Button ok_btn = dialogView.findViewById(R.id.successBtn);
                        TextView ok_txt = dialogView.findViewById(R.id.successText);
                        ok_txt.setText("상차 취소 성공");
                        ok_btn.setOnClickListener(new View.OnClickListener() {

                            @SneakyThrows @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                //배송목록 갱신하기
                                DeliveryListFragment deliveryListFragment = (DeliveryListFragment)DeliveryListFragment._DeliveryListFragment;
                                deliveryListFragment.changeDate();
                                deliveryDetailSrch();
                            }
                        });
                    } else {
                        commonHandler.showAlertDialog("상차 취소 실패", "응답메세지\n" + result.getRtnMsg());

                    }
                    showProgress(false);

                } else {
                    commonHandler.showAlertDialog("상차 취소 실패", "응답결과가 없습니다.");
                    showProgress(false);
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<DeliveryLiftCancelVO> call, Throwable t) {

                commonHandler.showAlertDialog("상차 취소 실패", "접속실패\n" + t.getMessage());
                showProgress(false);
            }
        });
    }

    private void mDeliveryListDetailTelUpdate(DeliveryTelVO deliveryTelVO) {    //요청 VO
        service.mDeliveryListDetailTelUpdate(deliveryTelVO).enqueue(new Callback<DeliveryVO>() {    //앞 요청VO, CallBack 응답 VO

            @Override
            public void onResponse(Call<DeliveryVO> call, Response<DeliveryVO> response) {  //둘다 응답 VO
                if (response.isSuccessful()) //응답값이 없다
                {
                    DeliveryVO result = response.body();
                    if (result.getRtnYn().equals("Y")) {
                        //내부저장소에 있는걸 불러오기
                    } else {
                        commonHandler.showAlertDialog("통화 카운트 저장 실패", ""+result.getRtnMsg());
                    }
                    showProgress(false);
                } else {
                    commonHandler.showAlertDialog("통화 카운트 저장 실패", ""+"응답결과가 없습니다.");
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<DeliveryVO> call, Throwable t) {

                commonHandler.showAlertDialog("통화 카운트 저장 접속 실패", ""+"접속실패\n" + t.getMessage());
                showProgress(false);
            }
        });
    }

    protected void deliveryDetailSrch(){
        String instMobileMIdValue = getIntent().getStringExtra("instMobileMId");


        mDeliveryDetailSrch(new DeliveryDetailSrchVO(
                        instMobileMIdValue
                        ,   sharePref.getString("tblUserMId","")
                        ,   sharePref.getString("mobileGrntCd","")
                )
        );


        showProgress(true);
    }
}