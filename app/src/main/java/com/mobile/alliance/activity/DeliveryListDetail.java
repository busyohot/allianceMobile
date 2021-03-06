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
import java.net.URLDecoder;
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
    public static Activity _DeliveryListDetail; //???????????????????????? ??? ??????????????? ??????????????????
    //???????????? ?????? 2??? ????????? ?????? ?????????
    //private BackPressCloseHandler backPressCloseHandler;
    //????????????
    //private LogoutHandler logoutHandler;
    //????????? ???
    private ProgressBar mProgressView;
    private ServiceApi service;

    //api ????????? ????????????
    private PersistentCookieStore persistentCookieStore;

    //????????? ????????? ???????????????
    static private String SHARE_NAME = "SHARE_PREF";
    static SharedPreferences sharePref = null;
    static SharedPreferences.Editor editor = null;

    //??????
    //private CommonHandler commonHandler;
    //??????
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

    Button deliveryArmBtn;  //????????? ?????????


    View sendTalkView;
    View sendSmsView;

    ConstraintLayout con;

    TableLayout detailTablelayout;


    TextView detailTelCnt;   //???????????? ?????????

        //????????? ?????? ??????
    Button deliverylRightLiftBtn;
    //????????? ?????? ????????????
    Button deliverylRightLiftClose;

    //?????? ?????? ??????
    Button deliveryDetailLiftCancelBtn;

    View deliveryBtnArea;
    Button deliveryNoCmpllBtn;  //????????? ??????

    Button  deliveryCmplBtn;    //???????????? ??????

    String instMobileMIdValue = "";
    String no;
    Button deliveryCancleBtn,deliveryDelayBtn;

    
    TextView deliveryDetailInstDt;          //?????????(?????????) ???????????????
    ImageView detailMap;

    //20211205 ??????
    TextView deliveryDetailText03;  //????????????

    String editYn;              //?????? ????????????, ?????????+1?????? 10?????? ????????? ????????? ????????? ????????? ??????. Y ??????-??????, ?????????,???????????? ??? ?????? N ?????????

    //20211231 ????????? ??????. ???????????? ???????????? ?????? ???????????? ?????? ?????????????????? ??????
    String  tblSoMIdValue = "";
    //20211231 ????????? ??????. ???????????? ????????? ???????????? ???????????? ?????? ????????? ?????????
    String  instDtValue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _DeliveryListDetail = DeliveryListDetail.this;//???????????????????????? ??? ??????????????? ??????????????????


        setContentView(R.layout.activity_delivery_list_detail);

        //logoutHandler = new LogoutHandler(this);
        //backPressCloseHandler = new BackPressCloseHandler(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //????????? ?????? ????????? ????????? ?????? ????????? ?????? ?????????

        //api ????????? ????????????
        PersistentCookieStore cookieStore = new PersistentCookieStore(this);
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);

        client = new OkHttpClient
                .Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .addInterceptor(commonHandler.httpLoggingInterceptor())
                .build();


        //????????? ????????? ???????????????
        sharePref = getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        editor = sharePref.edit();
        //?????????
        mProgressView = (ProgressBar) findViewById(R.id.activity_delivery_list_detail_progress);

        //http??????
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


        //??? ??????
        //??? ??????
        deliveryDetailBack = (ImageView) findViewById(R.id.deliveryDetailBack);

        deliveryDetailBack.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows @Override
            public void onClick(View view) {
                //???????????? ????????????
                DeliveryListFragment deliveryListFragment = (DeliveryListFragment)DeliveryListFragment._DeliveryListFragment;
                deliveryListFragment.changeDate();
                finish();
            }
        });
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        deliveryDetailLiftBtn = (Button) findViewById(R.id.deliveryDetailLiftBtn);      //??????????????????
        deliveryDetailLiftBtn.setVisibility(View.GONE); //???????????? ?????????
        deliveryDetailLiftBtn.setEnabled(false);    //???????????? ???????????? ??????(??????????????? ??????????????? ???????????? ???????????? ?????? ????????? ?????? ????????? 1????????? ?????? ???????????? ?????????)

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

        //20211205 ??????
        deliveryDetailText03 = (TextView) findViewById(R.id.deliveryDetailText03);

        deliveryDetailImg04 = (ImageView) findViewById(R.id.deliveryDetailImg04);
        deliveryDetailSoNo = (TextView) findViewById(R.id.deliveryDetailSoNo);
        deliveryDetailInstMobileMId = (TextView) findViewById(R.id.deliveryDetailInstMobileMId);
        //deliveryDetailInstMobileDId = (TextView) findViewById(R.id.deliveryDetailInstMobileDId);
        deliveryDetailRcptCost = (TextView) findViewById(R.id.deliveryDetailRcptCost);

        con = (ConstraintLayout) findViewById(R.id.con);

        detailTablelayout = (TableLayout) findViewById(R.id.detailTablelayout);


        sendTalkView = (View) findViewById(R.id.sendTalkView);
        sendSmsView = (View) findViewById(R.id.sendSmsView);


        deliveryArmBtn = (Button) findViewById((R.id.deliveryArmBtn));   //????????? ??? ??????

        detailTelCnt = (TextView) findViewById(R.id.detailTelCnt);

        deliverylRightLiftBtn = (Button) findViewById(R.id.deliverylRightLiftBtn);  //????????? ????????????
        deliverylRightLiftBtn.setVisibility(View.GONE); //????????? ?????? ?????? ?????????

        deliverylRightLiftClose = (Button) findViewById(R.id.deliverylRightLiftClose);  //????????? ??????????????????
        deliverylRightLiftClose.setVisibility(View.GONE);

        deliveryDetailLiftCancelBtn = (Button) findViewById(R.id.deliveryDetailLiftCancelBtn);  //??????????????????
        deliveryDetailLiftCancelBtn.setVisibility(View.GONE);   //?????????????????? ????????????

        deliveryBtnArea = (View)findViewById(R.id.deliveryBtnArea);

        deliveryNoCmpllBtn = (Button)findViewById(R.id.deliveryNoCmpllBtn);     //????????? ??????

        deliveryCmplBtn = (Button) findViewById(R.id.deliveryCmplBtn);  //???????????? ??????

        deliveryDetailInstDt = (TextView) findViewById(R.id.deliveryDetailInstDt);  //?????????


        deliveryCancleBtn  = (Button) findViewById(R.id.deliveryCancleBtn);  //??????????????????
        deliveryCancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //20211231 ????????? ????????????.
                /*
                Toast toast = Toast.makeText(getApplicationContext(), "?????? ??????\n?????????????????????. ????????? ??????????????????.", Toast.LENGTH_SHORT);   //0 short , 1 long
                toast.setGravity(Gravity.CENTER|Gravity.BOTTOM,0, 20);
                toast.show();
                */
                //20211231 ????????? ??????. ??????????????? ???????????? ?????????????????? ???????????? ???????????? ??????
                Intent deliveryCancelIntent = new Intent(DeliveryListDetail.this, DeliveryCancel.class);
                deliveryCancelIntent.putExtra("instMobileMId", instMobileMIdValue);
                deliveryCancelIntent.putExtra("tblSoMId", tblSoMIdValue);
                startActivity(deliveryCancelIntent);

            }

        });

        deliveryDelayBtn = (Button) findViewById(R.id.deliveryDelayBtn);  //??????????????????
        deliveryDelayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //20211231 ????????? ????????????.
                /*
                Toast toast = Toast.makeText(getApplicationContext(), "?????? ??????\n?????????????????????. ????????? ??????????????????.", Toast.LENGTH_SHORT);   //0 short , 1 long
                toast.setGravity(Gravity.CENTER|Gravity.BOTTOM,0, 20);
                toast.show();
                */
                //20211231 ????????? ??????. ??????????????? ???????????? ?????????????????? ???????????? ???????????? ??????
                Intent deliveryDelayIntent = new Intent(DeliveryListDetail.this, DeliveryDelay.class);
                deliveryDelayIntent.putExtra("instMobileMId", instMobileMIdValue);
                deliveryDelayIntent.putExtra("tblSoMId", tblSoMIdValue);
                deliveryDelayIntent.putExtra("instDt", instDtValue);
                startActivity(deliveryDelayIntent);
            }

        });




        detailMap = (ImageView) findViewById(R.id.detailMap);



    }

    @SneakyThrows @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //???????????? ????????????
        DeliveryListFragment deliveryListFragment = (DeliveryListFragment)DeliveryListFragment._DeliveryListFragment;
        deliveryListFragment.changeDate();
        finish();
        //backPressCloseHandler.onBackPressed();
    }




    //?????? ???????????? ???????????? ??????
    private void mDeliveryDetailSrch(DeliveryDetailSrchVO dliveryDetailSrchVO) {
        service.mDeliveryDetailSrch(dliveryDetailSrchVO).enqueue(new Callback<List<DeliveryVO>>() {

            @SneakyThrows @SuppressLint({"NewApi", "ResourceAsColor", "LongLogTag"})
            @Override
            public void onResponse(Call<List<DeliveryVO>> call, Response<List<DeliveryVO>> response) {

                if (response.isSuccessful() && response.body().size() > 0) //???????????? ?????? && ???????????? 1??? ??????
                {
                    List<DeliveryVO> result = response.body();

                    //20211231 ????????? ??????. ????????????ID
                    tblSoMIdValue = result.get(0).getTblSoMId();
                    //20211231 ????????? ??????. ????????? ??????
                    instDtValue = result.get(0).getInstDt();

                    editYn = result.get(0).getEditYn();         //?????? ????????????, ?????????+1?????? 10?????? ????????? ????????? ????????? ????????? ??????. Y ??????-??????, ?????????,???????????? ??? ?????? N ?????????

                    detailTelCnt.setText(result.get(0).getTelCnt());
                    detailMap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            startActivity(new Intent (getApplicationContext(), MapActivity.class)
                                    .putExtra("instMobileMId",result.get(0).getInstMobileMId())
                                    .putExtra("listAddr1",result.get(0).getAddr1())
                                    .putExtra("no",no)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));  //?????? ??????????????? ??????


                        }
                    });


                    //?????? ???????????? ????????? ?????? ??????
                    //????????? ???????????? ????????????

                    deliveryDetailInstMobileMId.setText(result.get(0).getInstMobileMId());

                    //deliveryDetailInstMobileDId.setText(result.get(0).getInstMobileDId());
                    int stateColor = 0;
                            /*
                            ?????? 4000	??????????????? ??????
                            ????????? ?????? 5000	????????? ??????
                            ????????? ?????? 6999	????????????(??????)
                            ????????? ?????? 7000	????????????
                            ?????? 8000	????????????(?????????) 8060 ????????????
                            ?????? 9999	????????????

                             */
                    if (result.get(0).getDlvyStatCd().equals("4000")) {

                        stateColor = Color.parseColor("#696969");


                        deliveryCmplBtn.setClickable(false);
                        deliveryCmplBtn.setBackground(getApplicationContext().getDrawable(
                                R.drawable.rounded_gray_button));
                        deliveryNoCmpllBtn.setText("?????????");

                        deliveryNoCmpllBtn.setClickable(false);
                        deliveryNoCmpllBtn.setBackground(getApplicationContext().getDrawable(
                                R.drawable.rounded_gray_button));
                        deliveryCmplBtn.setText("????????????");

                       //20211231 ????????? ??????. ????????? ????????? ????????? ????????? ????????? ?????????, ??????????????? ????????? ??????
                       if(sharePref.getString("mobileGrntCd", "").equals("9999"))
                       {
                           deliveryCmplBtn.setClickable(true);
                           deliveryCmplBtn.setBackground(getApplicationContext().getDrawable(
                                   R.drawable.rounded_blue_button));
                           deliveryNoCmpllBtn.setText("?????????");
                            deliveryNoCmpllBtn.setClickable(true);
                           deliveryNoCmpllBtn.setBackground(getApplicationContext().getDrawable(
                                   R.drawable.rounded_blue_button));
                           deliveryCmplBtn.setText("????????????");

                       }

                    }
                    if (result.get(0).getDlvyStatCd().equals("5000")) {

                        stateColor = Color.parseColor("#696969");


                        deliveryCmplBtn.setClickable(true);
                        deliveryCmplBtn.setBackground(getApplicationContext().getDrawable(
                                R.drawable.rounded_blue_button));
                        deliveryNoCmpllBtn.setText("?????????");

                        deliveryNoCmpllBtn.setClickable(true);
                        deliveryNoCmpllBtn.setBackground(getApplicationContext().getDrawable(
                                R.drawable.rounded_blue_button));
                        deliveryCmplBtn.setText("????????????");

                    }






                    else if (result.get(0).getDlvyStatCd().equals("6999") || result.get(0).getDlvyStatCd().equals("7000")) {
                        stateColor = Color.parseColor("#20B2AA");


                        deliveryCmplBtn.setClickable(true);
                        deliveryCmplBtn.setBackground(getApplicationContext().getDrawable(
                                R.drawable.rounded_blue_button));
                        deliveryNoCmpllBtn.setText("?????????");

                        deliveryNoCmpllBtn.setClickable(true);
                        deliveryNoCmpllBtn.setBackground(getApplicationContext().getDrawable(
                                R.drawable.rounded_blue_button));
                        deliveryCmplBtn.setText("????????????");

                    } else if (result.get(0).getDlvyStatCd().equals("8000") || result.get(0).getDlvyStatCd().equals("8060")) { //20220106 ????????? 8060 ???????????? ??????
                        stateColor = Color.parseColor("#FF0000");
                        deliveryCmplBtn.setClickable(false);

                        deliveryCmplBtn.setBackground(getApplicationContext().getDrawable(R.drawable.rounded_gray_button));
                        deliveryNoCmpllBtn.setText("????????? ??????");

                    } else if (result.get(0).getDlvyStatCd().equals("9999")) {
                        stateColor = Color.parseColor("#0000FF");
                        deliveryNoCmpllBtn.setClickable(false);

                        deliveryNoCmpllBtn.setBackground(getApplicationContext().getDrawable(
                                R.drawable.rounded_gray_button));
                        deliveryCmplBtn.setText("???????????? ??????");
                       

                    }



                   dekiveryDetailTitleBack.setBackgroundColor(stateColor);
                    deliveryDetailState.setText(result.get(0).getDlvyStatNm());
                    deliveryDetailName.setText(result.get(0).getAcptEr());
                    deliveryDetailHp.setText(result.get(0).getAcptTel2());

                    //????????? ????????? ??????????????? - SP???????????????
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
                            ok_title.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);   //????????????
                            ok_title.setText("?????? (3?????? ??????)");

                            TextView ok_txt = dialogView.findViewById(R.id.successText);
                            ok_txt.setTextSize(18);
                            ok_txt.setTypeface(null, Typeface.NORMAL);
                            ok_txt.setText(result.get(0).getAddr1() + "\n" + (result.get(0).getAddr2() == null ? "" : result.get(0).getAddr2())          );
                            ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);   //????????????

                            Button ok_btn = dialogView.findViewById(R.id.successBtn);
                            ok_btn.setText("?????? ??? ??????");
                            ok_btn.setOnClickListener(new View.OnClickListener() {
                                @SneakyThrows
                                @Override
                                public void onClick(View v) {

                                    //alertDialog.dismiss();
                                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("??????", result.get(0).getAddr1() + "\n" + (result.get(0).getAddr2() == null ? "" : result.get(0).getAddr2()));
                                    clipboard.setPrimaryClip(clip);
                                    alertDialog.dismiss();
                                    Toast.makeText(DeliveryListDetail.this,"????????????",Toast.LENGTH_SHORT).show();
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



                    //?????????, ?????? ????????? ??????

                    //????????? ??? ??????
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
                                ok_txt.setText("?????????("+deliveryDetailInstDt.getText()+")??? ????????? ??????10?????? ?????? ????????? ??? ????????????.");
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

                            //2021-11-22 ????????? ??????. ????????? ??????
                            sendTalk.putExtra("instDt", result.get(0).getInstDt());
                            
                            startActivity(sendTalk);

                        }

                    });


                    //????????? ?????? ???????????????
                    deliveryNoCmpllBtn.setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            if(     editYn.equals("N")
                                    &&  !sharePref.getString("mobileGrntCd", "").equals("9999") //20211231 ?????????. ????????? ????????? ????????????????????? ?????????
                            )
                            {
                                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryListDetail.this);
                                builder.setView(dialogView);

                                AlertDialog alertDialog = builder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();

                                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                                TextView ok_txt = dialogView.findViewById(R.id.successText);
                                ok_txt.setText("?????????("+deliveryDetailInstDt.getText()+")??? ????????? ??????10?????? ?????? ????????? ??? ????????????.");
                                ok_btn.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {

                                        alertDialog.dismiss();
                                        return;
                                    }
                                });
                                return;
                            }

                            else if(result.get(0).getDlvyStatCd().equals("9999")){   //???????????? ?????? ???????????? ??????????????? ????????? ????????? ????????? ??????


                                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryListDetail.this);
                                builder.setView(dialogView);

                                AlertDialog alertDialog = builder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();

                                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                                TextView ok_txt = dialogView.findViewById(R.id.successText);
                                ok_txt.setText("????????????????????? ????????? ????????? ?????? ????????????.");
                                ok_btn.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {

                                        alertDialog.dismiss();
                                        return;
                                    }
                                });



                                return;
                            }


                           else if(     result.get(0).getDlvyStatCd().equals("4000")     //???????????? ?????????????????? ???????????? ????????? ????????? ????????? ??????
                                    &&  !sharePref.getString("mobileGrntCd", "").equals("9999") //20211231 ?????????. ????????? ????????? ????????????????????? ?????????
                           ){  

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
                                ok_txt.setText("?????????, ?????? ?????? ??? ???????????????");
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
                             else if(   result.get(0).getDlvyStatCd().equals("5000")
                                    &&  !sharePref.getString("mobileGrntCd", "").equals("9999") //20211231 ?????????. ????????? ????????? ????????????????????? ?????????

                             ){   //????????? ?????? ???????????? ??????????????? ????????? ????????? ????????? ??????

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
                                ok_txt.setText("??????????????? ?????? ?????? ????????? ????????? ???????????????????\n????????? ?????? ????????? ?????????.");
                                ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                ok_btn.setOnClickListener(new View.OnClickListener() {
                                    @SneakyThrows @Override
                                    public void onClick(View v) {


                                        alertDialog.dismiss();
                                        //????????? ?????? ??????
                                        Intent noCmpl =
                                                new Intent(DeliveryListDetail.this, NoCmplActivity.class);
                                        noCmpl.putExtra("instMobileMId",
                                                result.get(0).getInstMobileMId()); //????????? ?????? ?????????
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
                                //????????? ????????? 8000 ????????? ????????? ?????? ????????? ??????
                                if(result.get(0).getDlvyStatCd().equals("8000") || result.get(0).getDlvyStatCd().equals("8060")) //20220106 ????????? 8060 ???????????? ??????
                                {
                                    Intent noCmplCancel = new Intent(DeliveryListDetail.this, NoCmplCancelActivity.class);
                                    noCmplCancel.putExtra("instMobileMId", result.get(0).getInstMobileMId()); //????????? ?????? ?????????
                                    noCmplCancel.putExtra("soNo", result.get(0).getSoNo());
                                    startActivity(noCmplCancel);
                                }
                                else {
                                    Intent noCmpl =  new Intent(DeliveryListDetail.this, NoCmplActivity.class);
                                    noCmpl.putExtra("instMobileMId", result.get(0).getInstMobileMId()); //????????? ?????? ?????????
                                    noCmpl.putExtra("soNo", result.get(0).getSoNo());
                                    startActivity(noCmpl);
                                }
                            }
                        }
                    });
                    //???????????? ?????? ???????????????
                    deliveryCmplBtn.setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(editYn.equals("N")
                                    &&  !sharePref.getString("mobileGrntCd", "").equals("9999") //20211231 ?????????. ????????? ????????? ????????????????????? ?????????
                            )
                            {
                                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryListDetail.this);
                                builder.setView(dialogView);

                                AlertDialog alertDialog = builder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();

                                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                                TextView ok_txt = dialogView.findViewById(R.id.successText);
                                ok_txt.setText("?????????("+deliveryDetailInstDt.getText()+")??? ????????? ??????10?????? ?????? ????????? ??? ????????????.");
                                ok_btn.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {

                                        alertDialog.dismiss();
                                        return;
                                    }
                                });
                                return;
                            }

                            else if(    (result.get(0).getDlvyStatCd().equals("8000")  || result.get(0).getDlvyStatCd().equals("8060")  )  //20220106 ????????? 8060 ???????????? ??????    //?????????
                                    &&  !sharePref.getString("mobileGrntCd", "").equals("9999") //20211231 ?????????. ????????? ????????? ????????????????????? ?????????
                            ){   // ?????????

                                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryListDetail.this);
                                builder.setView(dialogView);

                                AlertDialog alertDialog = builder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();

                                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                                TextView ok_txt = dialogView.findViewById(R.id.successText);
                                ok_txt.setText("?????????????????? ???????????? ????????? ??? ??? ????????????.");
                                ok_btn.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                        return;
                                    }
                                });
                                return;
                            }

                            else if(result.get(0).getDlvyStatCd().equals("4000")
                                    &&  !sharePref.getString("mobileGrntCd", "").equals("9999") //20211231 ?????????. ????????? ????????? ????????????????????? ?????????
                            ){   //???????????? ?????????????????? ???????????? ????????? ????????? ????????? ??????

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
                                ok_txt.setText("?????????, ?????? ?????? ??? ???????????????");
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

                            else if(result.get(0).getDlvyStatCd().equals("5000")
                                    &&  !sharePref.getString("mobileGrntCd", "").equals("9999") //20211231 ?????????. ????????? ????????? ????????????????????? ?????????
                            ){   //????????? ?????? ???????????? ??????????????? ????????? ????????? ????????? ??????


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
                                ok_txt.setText("??????????????? ?????? ?????? ???????????? ????????? ???????????????????\n????????? ?????? ????????? ?????????.");
                                ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                ok_btn.setOnClickListener(new View.OnClickListener() {
                                    @SneakyThrows @Override
                                    public void onClick(View v) {


                                        alertDialog.dismiss();
                                        //???????????? ?????? ??????
                                        Intent yesCmpl =
                                                new Intent(DeliveryListDetail.this, YesCmplActivity.class);
                                        yesCmpl.putExtra("instMobileMId",
                                                result.get(0).getInstMobileMId()); //????????? ?????? ?????????
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
                                //????????? ???????????? ????????? ???????????? ?????? ????????? ??????
                                if(result.get(0).getDlvyStatCd().equals("9999"))    //9999 ????????????
                                {
                                   Intent yesCmplCancel = new Intent(DeliveryListDetail.this, YesCmplCancelActivity.class);
                                    yesCmplCancel.putExtra("instMobileMId", result.get(0).getInstMobileMId()); //????????? ?????? ?????????
                                    yesCmplCancel.putExtra("soNo", result.get(0).getSoNo());
                                    startActivity(yesCmplCancel);
                                }
                                else {

                                    Intent yesCmpl =
                                            new Intent(DeliveryListDetail.this, YesCmplActivity.class);
                                    yesCmpl.putExtra("instMobileMId",
                                            result.get(0).getInstMobileMId()); //????????? ?????? ?????????
                                    yesCmpl.putExtra("soNo", result.get(0).getSoNo());
                                    startActivity(yesCmpl);
                                }

                            }
                        }

                    });




                    //?????????????????? ??????

                    //INST_CTGR ?????? ???????????? : ?????? / ??????/ ?????????  1000??????/2000??????/3000????????? (?????????????????? ?????? ????????? ???????????? ??????)
                    if (result.get(0).getInstCtgr().equals("1000")) {
                        deliveryDetailImg01.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.badge_1_2));
                    } else if (result.get(0).getInstCtgr().equals("2000")) {
                        deliveryDetailImg01.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.badge_1_3));
                    } else if (result.get(0).getInstCtgr().equals("3000")) {
                        deliveryDetailImg01.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.badge_1_1));
                    }

                    //INST_TYPE       ???????????? - ?????? / ??????   (table : NORMAL ???????????? / NEXTDAY ???????????? )(?????????????????? ??????????????????????????????) ??????=????????????, ??????=????????????
                    if (result.get(0).getInstType().equals("NORMAL")) {
                        deliveryDetailImg02.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.badge_1_4));
                    } else if (result.get(0).getInstType().equals("NEXTDAY")) {
                        deliveryDetailImg02.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.badge_1_5));
                    }
                    //SO_TYPE ???????????? : ??????      //??????        //??????
                    /*      1000	????????????,   2000	????????????,   2500	????????????,   4000	AS??????   */

                    if (result.get(0).getSoType().equals("1000")) {
                        deliveryDetailImg03.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.badge_2_2));
                    } else if (result.get(0).getSoType().equals("2000")) {
                        deliveryDetailImg03.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.badge_2_3));
                    }
                    /*
                    else if (result.get(0).getSoType().equals("2500")) {
                        deliveryDetailImg03.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.badge_2_4));
                    } else if (result.get(0).getSoType().equals("4000")) {
                        deliveryDetailImg03.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.badge_2_1));
                    }
                    */
                    else{
                        deliveryDetailText03.setText(" "+result.get(0).getSoTypeNm()+" ");
                    }


                    //SEAT_TYPE       ??????????????????  1??? / 2???

                    if (result.get(0).getSeatType().equals("1111")) {
                        deliveryDetailImg04.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.badge_3_1));
                    } else if (result.get(0).getSeatType().equals("2222")) {
                        deliveryDetailImg04.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.badge_3_2));
                    }
                    deliveryDetailInstDt.setText(result.get(0).getInstDt().substring(0,4)+"??? " + result.get(0).getInstDt().substring(4,6)+"??? "+ result.get(0).getInstDt().substring(6,8)+"???");    //?????????(?????????)
                    deliveryDetailSoNo.setText(result.get(0).getSoNo());        //????????????



                    // ?????? ??????????????? , ??????
                    DecimalFormat formatter = new DecimalFormat("###,###");


                    deliveryDetailRcptCost.setText(formatter.format(Integer.parseInt(result.get(0).getRcptCost() == null ? "0" : result.get(0).getRcptCost()))+ " ???");

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
                        TableRow tRow = new TableRow(getApplicationContext());     // ????????? ROW ??????


                        ViewGroup.LayoutParams pp = new ViewGroup.LayoutParams(720, LinearLayout.LayoutParams.WRAP_CONTENT);
                        checkBox[i] = new CheckBox(getApplicationContext());




                        SpannableString spannableCheckBoxString = new SpannableString(result.get(i).getProdCd()+" "+ result.get(i).getProdNm().toString());
                        int start = 0;
                        int end = spannableCheckBoxString.toString().indexOf(" ")+0;
                        spannableCheckBoxString.setSpan(new ForegroundColorSpan(Color.parseColor("#228B22")),start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //??????????????? ??? ??????
                        spannableCheckBoxString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //??????????????? ?????? ????????? ??????
                        spannableCheckBoxString.setSpan(new RelativeSizeSpan(1.0f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //??????????????? ????????????
                        spannableCheckBoxString.setSpan(new UnderlineSpan(), start,end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        checkBox[i].append(spannableCheckBoxString);

                        checkBox[i].setLayoutParams(pp);
                        checkBox[i].setPadding(0, 20, 0, 20);

                        ViewGroup.LayoutParams ppCd = new ViewGroup.LayoutParams(720, LinearLayout.LayoutParams.WRAP_CONTENT);

                        prodNm[i] = new TextView(getApplicationContext());

                        //?????? 4000	??????????????? ??????
                        //????????? ?????? 5000	????????? ??????
                        //????????? ????????? ????????? ??????????????? ??????
                        if (   (!result.get(0).getDlvyStatCd().equals("4000") && !result.get(0).getDlvyStatCd().equals("5000"))
                                &&
                                !result.get(i).getProdLiftCmplYn().equals("Y")      //????????? ??????????????? - ????????? ??????
                        ){
                            prodNm[i].setText("[?????????] "+ result.get(i).getProdCd()+" "+ result.get(i).getProdNm().toString());

                            prodNm[i].setTextColor(Color.GRAY);

                            prodNm[i].setTypeface(null,Typeface.NORMAL);

                            SpannableString spannableString = new SpannableString(prodNm[i].getText());
                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6702")),0,5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //??????????????? ??? ??????
                            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //??????????????? ?????? ????????? ??????
                            spannableString.setSpan(new RelativeSizeSpan(1.3f), 0, 0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //??????????????? ????????????
                            prodNm[i].setText(spannableString);
                        }
                        else if (  (!result.get(0).getDlvyStatCd().equals("4000") && !result.get(0).getDlvyStatCd().equals("5000"))
                                &&
                                result.get(i).getProdLiftCmplYn().equals("Y")      //????????? ???????????? - ?????? ??????
                         )
                        {
                            prodNm[i].setText("[????????????] "+result.get(i).getProdCd()+" "+ result.get(i).getProdNm().toString());
                            prodNm[i].setTextColor(Color.BLACK);
                            prodNm[i].setTypeface(null,Typeface.BOLD);
                        }
                        else
                        {


                            SpannableString spannableProdNmString = new SpannableString(result.get(i).getProdCd()+" "+ result.get(i).getProdNm().toString());
                            int startProdNm = 0;
                            int endProdNm = spannableProdNmString.toString().indexOf(" ")+0;
                            spannableProdNmString.setSpan(new ForegroundColorSpan(Color.parseColor("#228B22")),startProdNm,endProdNm, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //??????????????? ??? ??????
                            spannableProdNmString.setSpan(new StyleSpan(Typeface.BOLD), startProdNm, endProdNm, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //??????????????? ?????? ????????? ??????
                            spannableProdNmString.setSpan(new RelativeSizeSpan(1.0f), startProdNm, endProdNm, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //??????????????? ????????????
                            spannableProdNmString.setSpan(new UnderlineSpan(), startProdNm,endProdNm,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            prodNm[i].append(spannableCheckBoxString);



                        }

                        prodNm[i].setLayoutParams(ppCd);
                        prodNm[i].setPadding(0, 20, 0, 20);

                        //????????? ????????? ?????????
                        playo[i] = new ImageView(getApplicationContext());
                        playo[i].setForegroundGravity(Gravity.CENTER | Gravity.CENTER);
                        playo[i].setPadding(10, 20, 10, 0);
                        playo[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_playo));

                        imageViews[i] = new ImageView(getApplicationContext());
                        imageViews[i].setForegroundGravity(Gravity.CENTER | Gravity.CENTER);
                        imageViews[i].setPadding(10, 20, 10, 0);
                        imageViews[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_infoblue));
                        //imageViews[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_info));

                         String prodUrl = (result.get(i).getInstImgPath() == null || result.get(i).getInstImgPath().trim().equals("") )  ? "" : result.get(i).getInstUrl();

                         if(result.get(i).getInstImgPath() == null || result.get(i).getInstImgPath().trim().equals(""))
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
                                        //???????????? ?????? Activity??? ?????????
                                        Intent imageActivity = new Intent(DeliveryListDetail.this, ImageActivity.class);
                                        imageActivity.putExtra("imageUri",prodUrl);
                                        imageActivity.putExtra("imageNo", finalI +"");
                                        imageActivity.putExtra("imageType","download");

                                        startActivity(imageActivity);  //?????? ??????????????? ??????
                                    }
                                    else
                                    {
                                        commonHandler.showAlertDialog("????????? ?????? ??????","????????? ????????? ???????????? ????????????.");
                                    }
                                }
                                else
                                {
                                    commonHandler.showAlertDialog("??? ????????????","????????? ??? ?????? ????????? ????????????.");
                                }




                            }
                        });


                        String instUrl = (result.get(i).getInstUrl() == null || result.get(i).getInstUrl().trim().equals(""))  ? "" : result.get(i).getInstUrl();

                        if(result.get(i).getInstUrl() == null || result.get(i).getInstUrl().trim().equals(""))
                        {
                            playo[i].setVisibility(View.INVISIBLE);
                        }
                        playo[i].setOnClickListener(new ImageView.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                /*
                                ??????????????? ????????? ?????????
                                URLUtil.guessUrl("url")
                                ????????? url ???????????? ??????
                                URLUtil.isValidUrl("url")
                                ??????????????? ????????? ?????? ???????????? url ?????? ?????? (??????????????? ???????????? ??????????????? ??? ??????)
                                Patterns.WEB_URL.matcher(url).matches()
                                */

                                if(URLUtil.isValidUrl(instUrl))
                                {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(instUrl));
                                    startActivity(browserIntent);
                                }
                                else
                                {
                                    commonHandler.showAlertDialog("??? ????????????","????????? ??? ?????? ????????? ????????????.");
                                }
                            }

                        });



                        if( result.get(i).getMtoYn().equals("Y"))
                        {
                            qty[i] = new TextView(getApplicationContext());
                            qty[i].setText(result.get(i).getQty().toString() + " ???"+"\nMTO");
                            qty[i].setPadding(0, 20, 0, 20);
                            qty[i].setGravity(Gravity.CENTER | Gravity.RIGHT);
                            qty[i].setTypeface(null, Typeface.BOLD);

                            SpannableString spannableQtyString = new SpannableString(qty[i].getText());
                            int startQty = spannableQtyString.toString().indexOf("\n")+1;
                            int endQty = spannableQtyString.length();


                             spannableQtyString.setSpan(new BackgroundColorSpan(Color.parseColor("#FFAF0A")),startQty,endQty, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //??????????????? ????????? ??????

                            spannableQtyString.setSpan(new StyleSpan(Typeface.BOLD), startQty, endQty, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //??????????????? ?????? ????????? ??????
                            spannableQtyString.setSpan(new RelativeSizeSpan(1.0f), startQty, endQty, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //??????????????? ????????????
                            //spannableQtyString.setSpan(new UnderlineSpan(), startQty,endQty,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            qty[i].setText(spannableQtyString);


                        }
                        else {
                            qty[i] = new TextView(getApplicationContext());
                            qty[i].setText(result.get(i).getQty().toString() + " ???"+"\n");
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


                                //????????? ????????? ???????????? ???????????? ????????? ????????? ?????? ????????? ?????????
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



                        //5000 ????????? ????????????????????? ???????????? ????????? ????????????

                        deliveryDetailLiftBtn.setVisibility(View.GONE); //???????????? ?????? ???????????? ???
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

                    //????????? ????????????, ????????? ?????????????????? ??? ????????? ?????? ??????????????? ????????? 5000
                    if (result.get(0).getDlvyStatCd().equals("5000")) {
                        deliverylRightLiftBtn.setVisibility(View.VISIBLE);
                        deliverylRightLiftClose.setVisibility(View.GONE);

                    }
                    else
                    {
                        deliverylRightLiftBtn.setVisibility(View.GONE);
                        deliverylRightLiftClose.setVisibility(View.GONE);
                    }


                    //????????? ??????????????? ????????? ?????? ??????????????? ????????? ???????????? prodNm ??? ??????
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
                                ok_txt.setText("?????????("+deliveryDetailInstDt.getText()+")??? ????????? ??????10?????? ?????? ????????? ??? ????????????.");
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
                            deliveryDetailLiftBtn.setVisibility(View.VISIBLE); //???????????? ?????? ?????? ????????? ???
                            deliveryBtnArea.setVisibility((View.VISIBLE));      //???????????? ?????? ??? ???
                            deliverylRightLiftClose.setVisibility(View.VISIBLE);    //????????? ?????? ?????? ?????? ????????? ???
                            deliverylRightLiftBtn.setVisibility(View.GONE);     //????????? ?????? ?????? ??????
                        }
                    });
                    //????????? ?????? ?????? ????????? ?????????
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
                            deliveryDetailLiftBtn.setVisibility(View.GONE); //???????????? ?????? ?????? ????????? ???
                            deliveryBtnArea.setVisibility(View.GONE);
                            deliverylRightLiftClose.setVisibility(View.GONE);
                            deliverylRightLiftBtn.setVisibility(View.VISIBLE);     //????????? ?????? ?????? ?????????
                        }
                    });


                    //6999 ????????????, 7000 ??????
                    //?????? ???????????? ????????? ?????????

                    //???????????? ??????
                    if (   result.get(0).getDlvyStatCd().equals("6999") || result.get(0).getDlvyStatCd().equals("7000")) {
                        deliveryDetailLiftCancelBtn.setVisibility(View.VISIBLE);
                        deliveryBtnArea.setVisibility(View.VISIBLE);
                        //????????????????????? ????????????
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
                                    ok_txt.setText("?????????("+deliveryDetailInstDt.getText()+")??? ????????? ??????10?????? ?????? ????????? ??? ????????????.");
                                    ok_btn.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View v) {

                                            alertDialog.dismiss();
                                            return;
                                        }
                                    });
                                    return;
                                }
                                //????????? ?????? ?????? ????????? ??????????????? ??????
                                //6999	????????????, 7000 ??????

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
                                ok_txt.setText("???????????? ????????? ???????????????????");
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
                                        );    // ????????????
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
                        deliveryDetailLiftCancelBtn.setVisibility(View.GONE);   //??????????????????
                        deliveryBtnArea.setVisibility(View.GONE);       //????????????
                    }

                    //???????????? ?????? //???????????? ??????
                    //?????? ?????? ????????? ????????????
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
                                ok_txt.setText("?????????("+deliveryDetailInstDt.getText()+")??? ????????? ??????10?????? ?????? ????????? ??? ????????????.");
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
                            ok_txt.setText("???????????? ????????? ???????????????????");
                            ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            ok_btn.setOnClickListener(new View.OnClickListener() {
                                @SneakyThrows @Override
                                public void onClick(View v) {

                                    alertDialog.dismiss();

                                    //?????? ????????? ????????? ????????? ?????????
                                    deliveryVO.clear();
                                    for (int t = 0; t < result.size(); t++) {
                                        if (checkBox[t].isChecked()) {
                                            deliveryVO.add(result.get(t));
                                        }
                                    }
                                    showProgress(true);

                                    //???????????? = ???????????? - ???????????? 7000
                                    //???????????? > ???????????? - ????????????(??????) 6999
                                    //?????? SP?????? ???????????? ?????????.

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
                                    mDeliveryLiftSave(liftSave);    // ????????????
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

                    //20220120 ????????? ??????. was?????? [500 internal server error] excpition????????? ?????????????????? ????????? ??????
                    //commonHandler.showFinishAlertDialog("?????? ???????????? ??????", "?????? ???????????? ????????? ????????????.", "Y");
                    commonHandler.showFinishAlertDialog("?????? ???????????? ??????",
                                                        response.code() +"\n"+ response.message()+"\n\n"+
                                                                URLDecoder.decode(response.errorBody().string(),"UTF-8"),
                                                        "Y");
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<List<DeliveryVO>> call, Throwable t) {
                commonHandler.showAlertDialog("?????? ???????????? ??????", "????????????\n" + t.getMessage());
                showProgress(false);
            }
        });
    }

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    //???????????? ??????
    private void mDeliveryLiftSave(ArrayList<DeliveryLiftSaveVO> deliveryLiftSaveVO) {    //?????? VO

        service.mDeliveryLiftSave(deliveryLiftSaveVO).enqueue(new Callback<DeliveryLiftSaveVO>() {    //??? ??????VO, CallBack ?????? VO

            @SneakyThrows @Override
            public void onResponse(Call<DeliveryLiftSaveVO> call, Response<DeliveryLiftSaveVO> response) {  //?????? ?????? VO

                if (response.isSuccessful()) //???????????? ??????
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
                        ok_txt.setText("?????? ?????? ??????");
                        ok_btn.setOnClickListener(new View.OnClickListener() {

                            @SneakyThrows @Override
                            public void onClick(View v) {

                                alertDialog.dismiss();
                                //???????????? ????????????
                                DeliveryListFragment deliveryListFragment = (DeliveryListFragment)DeliveryListFragment._DeliveryListFragment;
                                deliveryListFragment.changeDate();
                                deliveryDetailSrch();
                            }
                        });

                    }
                    else
                    {
                        commonHandler.showAlertDialog("?????? ?????? ??????", "???????????????\n" + result.getRtnMsg());
                    }
                    showProgress(false);

                } else {

                    //20220120 ????????? ??????. was?????? [500 internal server error] excpition????????? ?????????????????? ????????? ??????
                    //commonHandler.showAlertDialog("?????? ?????? ??????", "??????????????? ????????????.");
                    commonHandler.showAlertDialog(  "?????? ?????? ??????",
                                                    response.code() +"\n"+ response.message()+"\n\n"+
                                                        URLDecoder.decode(response.errorBody().string(),"UTF-8"));


                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<DeliveryLiftSaveVO> call, Throwable t) {

                commonHandler.showAlertDialog("?????? ?????? ??????", "????????????\n" + t.getMessage());

                showProgress(false);
            }
        });
    }



    //???????????? ??????
    private void mDeliveryLiftCancel(DeliveryLiftCancelVO deliveryLiftCancelVO) {    //?????? VO

        service.mDeliveryLiftCancel(deliveryLiftCancelVO).enqueue(new Callback<DeliveryLiftCancelVO>() {    //??? ??????VO, CallBack ?????? VO

            @SneakyThrows @Override
            public void onResponse(Call<DeliveryLiftCancelVO> call, Response<DeliveryLiftCancelVO> response) {  //?????? ?????? VO

                if (response.isSuccessful()) //???????????? ??????
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
                        ok_txt.setText("?????? ?????? ??????");
                        ok_btn.setOnClickListener(new View.OnClickListener() {

                            @SneakyThrows @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                //???????????? ????????????
                                DeliveryListFragment deliveryListFragment = (DeliveryListFragment)DeliveryListFragment._DeliveryListFragment;
                                deliveryListFragment.changeDate();
                                deliveryDetailSrch();
                            }
                        });
                    } else {
                        commonHandler.showAlertDialog("?????? ?????? ??????", "???????????????\n" + result.getRtnMsg());

                    }
                    showProgress(false);

                } else {

                    //20220120 ????????? ??????. was?????? [500 internal server error] excpition????????? ?????????????????? ????????? ??????
                    //commonHandler.showAlertDialog("?????? ?????? ??????", "??????????????? ????????????.");
                    commonHandler.showAlertDialog("?????? ?????? ??????", response.code() +"\n"+ response.message()+"\n\n"+
                            URLDecoder.decode(response.errorBody().string(),"UTF-8"));

                    showProgress(false);
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<DeliveryLiftCancelVO> call, Throwable t) {

                commonHandler.showAlertDialog("?????? ?????? ??????", "????????????\n" + t.getMessage());
                showProgress(false);
            }
        });
    }

    private void mDeliveryListDetailTelUpdate(DeliveryTelVO deliveryTelVO) {    //?????? VO
        service.mDeliveryListDetailTelUpdate(deliveryTelVO).enqueue(new Callback<DeliveryVO>() {    //??? ??????VO, CallBack ?????? VO

            @SneakyThrows @Override
            public void onResponse(Call<DeliveryVO> call, Response<DeliveryVO> response) {  //?????? ?????? VO
                if (response.isSuccessful()) //???????????? ??????
                {
                    DeliveryVO result = response.body();
                    if (result.getRtnYn().equals("Y")) {
                        //?????????????????? ????????? ????????????
                    } else {
                        commonHandler.showAlertDialog("?????? ????????? ?????? ??????", ""+result.getRtnMsg());
                    }
                    showProgress(false);
                } else {

                    //20220120 ????????? ??????. was?????? [500 internal server error] excpition????????? ?????????????????? ????????? ??????
                    //commonHandler.showAlertDialog("?????? ????????? ?????? ??????", ""+"??????????????? ????????????.");
                    commonHandler.showAlertDialog("?????? ????????? ?????? ??????", response.code() +"\n"+ response.message()+"\n\n"+
                            URLDecoder.decode(response.errorBody().string(),"UTF-8"));

                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<DeliveryVO> call, Throwable t) {

                commonHandler.showAlertDialog("?????? ????????? ?????? ?????? ??????", "????????????\n" + t.getMessage());
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