package com.mobile.alliance.activity;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.icu.util.Calendar;
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
import android.widget.Toast;

import com.mobile.alliance.R;
import com.mobile.alliance.api.BackPressCloseHandler;
import com.mobile.alliance.api.CommonHandler;
import com.mobile.alliance.api.LogoutHandler;
import com.mobile.alliance.api.PersistentCookieStore;
import com.mobile.alliance.api.RetrofitClient;
import com.mobile.alliance.api.ServiceApi;
import com.mobile.alliance.api.TextValueHandler;
import com.mobile.alliance.entity.deliveryCancel.DeliveryCancelVO;
import com.mobile.alliance.entity.deliveryDelay.DeliveryDelaySaveVO;
import com.mobile.alliance.entity.deliveryDelay.DeliveryDelayVO;
import com.mobile.alliance.fragment.DeliveryListFragment;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;
import lombok.SneakyThrows;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryDelay extends AppCompatActivity {

    String  TAG = "DeliveryDelay";
    String instMobileMId;
    String tblSoMId;
    String instDt;

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

    ImageView   deliveryDelayBack       ;    //왼쪽 위 뒤로가기 화살표 이미지
    TextView    deliveryDelayOriInstDt  ; //원래배송일자
    TextView    deliveryDelaySelectDt   ;   //선택한 배송일자(연기한 배송일자)
    TextView    selectDelayDate;    //시스템에 전송할 연기날짜
    TextView    selectDelayCapaId;  //시스템에 전송할 캐파아이디
    MaterialCalendarView materialCalendarView ;
    Button      deliveryDelayBtn3 ;         //배송연기 버튼



    EditText deliveryDelayMemo;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_delay);

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
        try
        {
            instDt         =   getIntent().getStringExtra("instDt");
        }
        catch(Exception e)
        {
            instDt = "";
        }
        Log.d(TAG,"instMobileMId : " + instMobileMId);
        Log.d(TAG,"tblSoMId : " + tblSoMId);
        Log.d(TAG,"instDt : " + instDt);

        //내부에 데이터 저장하는것
        sharePref = getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        editor = sharePref.edit();
        //진행바
        mProgressView = (ProgressBar) findViewById(R.id.deliveryDelayProgress);

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


        deliveryDelayBack       =   (ImageView)findViewById(R.id.deliveryDelayBack);
        //화면의 왼쪽 위 뒤로가기 이미지를 클릭했을때
        deliveryDelayBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deliveryDelyBack();
            }
        });

        deliveryDelayOriInstDt  =   (TextView)findViewById(R.id.deliveryDelayOriInstDt);
        deliveryDelayOriInstDt.setText("(취소 배송일자 "+instDt.substring(0,4)+"년 " + instDt.substring(4,6)+"월 "+ instDt.substring(6,8)+"일)");



        deliveryDelaySelectDt   =   (TextView)findViewById(R.id.deliveryDelaySelectDt);
        selectDelayDate         =   (TextView)findViewById(R.id.selectDelayDate);
        selectDelayCapaId       =   (TextView)findViewById(R.id.selectDelayCapaId) ;
        materialCalendarView  = (MaterialCalendarView ) findViewById(R.id.materialCalendarView );


        deliveryDelayBtn3   =   (Button)findViewById(R.id.deliveryDelayBtn3);   //배송완료 버튼

        deliveryDelayMemo   =   (EditText)findViewById(R.id.deliveryDelayMemo);

        //화면 뜨자마자 SP 수행시켜서 배송연기 날짜 리스트를 불러온다
        deliveryDelayList(new DeliveryDelayVO(
                sharePref.getString("cmpyCd","A")
                ,tblSoMId

        ));
        showProgress(true);



        deliveryDelayBtn3.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v){
                //1.원래배송일자를 체크
                if(instDt.equals("") || instDt == null)
                {
                    commonHandler.showAlertDialog("","원래 배송일자가 없습니다.");
                    return;
                }
                //2. 선택한 연기할 배송일자를 체크
                if(selectDelayDate.getText().equals("") || selectDelayDate.getText() == null)
                {
                    commonHandler.showAlertDialog("","선택한 연기 배송일자가 없습니다.");
                    return;
                }
                //3. 선택한 연기할 배송일자에 해당하는 capaId 를 체크
                if(selectDelayCapaId.getText().equals("") || selectDelayCapaId.getText() == null)
                {
                    commonHandler.showAlertDialog("","선택한 연기 배송일자의 캐파가 없습니다.");
                    return;
                }
                //4. 배송 연기 사유는 필수
                if(deliveryDelayMemo.getText().toString().trim().length() == 0)
                {
                    commonHandler.showAlertDialog("","배송연기사유는 [필수] 입니다.");
                    return;
                }



                //컨펌(버튼 두개중 하나 선택)
                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DeliveryDelay.this);
                builder.setView(dialogView);

                android.app.AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();

                Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
                TextView ok_txt = dialogView.findViewById(R.id.confirmText);
                ok_txt.setTextSize(14);
                ok_txt.setTypeface(null, Typeface.BOLD);
                ok_txt.setText( "원래 배송일자 : " + resultValue.get(0).getDlvyCnclDt()+"\n"+
                                deliveryDelaySelectDt.getText() +"\n\n"+
                                "배송일을 변경하시겠습니까?"
                );
                ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                ok_btn.setOnClickListener(new View.OnClickListener() {
                    @SneakyThrows @Override
                    public void onClick(View v) {

                        alertDialog.dismiss();
                        //연기한 배송일을 저장하기
                       deliveryDelaySave(new DeliveryDelaySaveVO(
                                 instMobileMId
                                ,tblSoMId
                                ,selectDelayCapaId.getText().toString().trim()
                                ,resultValue.get(0).getDlvyCnclDt2()
                                ,selectDelayDate.getText().toString().trim()
                                ,deliveryDelayMemo.getText().toString().trim()
                                ,sharePref.getString("tblUserMId","")

                        ));
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






        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)

                //.setCalendarDisplayMode(CalendarMode.MONTHS)
                .setCalendarDisplayMode(CalendarMode.MONTHS)

                .commit();

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int Year = date.getYear();
                int Month = date.getMonth() + 1;
                int Day = date.getDay();

                String yy = String.valueOf(Year);
                String mm = String.valueOf(Month);
                String dd = String.valueOf(Day);
                if(mm.length() == 1){
                    mm='0'+mm;
                } if(dd.length() == 1){
                    dd='0'+dd;
                }

                selectDelayDate.setText(yy+""+mm+""+dd);
                deliveryDelaySelectDt.setText("연기 배송일자 : " + yy + "년 " + mm +"월 " + dd +"일");

                for(int i=0;i<resultValue.size();i++)
                {
                    if(resultValue.get(i).getInstDt().equals(yy+""+mm+""+dd))
                    {
                        selectDelayCapaId.setText(resultValue.get(i).getCapaId());
                    }
                }
            }
        });



    }   //onCreate

    List<DeliveryDelayVO> resultValue;
    //화면 뜨자마자 SP 수행시켜서 가능 달력 리스트 불러오기
    private void deliveryDelayList(DeliveryDelayVO deliveryDelayVO) {
        service.mDeliveryDelayList(deliveryDelayVO).enqueue(new Callback<List<DeliveryDelayVO>>() {

            @Override
            public void onResponse(Call<List<DeliveryDelayVO>> call, Response<List<DeliveryDelayVO>> response) {

                if(response.isSuccessful()) //응답값이 있다
                {
                    List<DeliveryDelayVO> result = response.body();
                    resultValue = result;
                    if(result.size() > 0){
                        //commonHandler.showAlertDialog("배송 연기 날짜 목록 조회 성공", "조회 "+result.size()+"건");
                        //commonHandler.showAlertDialog(result.get(0).getInstDt(), "조회 "+result.size()+"건");


                        materialCalendarView.state().edit()
                                .setMinimumDate(CalendarDay.from(       Integer.parseInt(result.get(0).getInstDt().substring(0,4)),
                                                                  Integer.parseInt(result.get(0).getInstDt().substring(4,6))-1,
                                                                         Integer.parseInt(result.get(0).getInstDt().substring(6,8))     )    )


                                .setMaximumDate(CalendarDay.from(       Integer.parseInt(result.get(result.size()-1).getInstDt().substring(0,4)),
                                        Integer.parseInt(result.get(result.size()-1).getInstDt().substring(4,6))-1,
                                        Integer.parseInt(result.get(result.size()-1).getInstDt().substring(6,8))     )    )

                                .commit();



                        ArrayList<CalendarDay> disabledDates = new ArrayList<>();


                        for(int i=0;i<result.size();i++)
                        {
                            if(!result.get(i).getAbleYn().equals("Y")){
                                disabledDates.add(CalendarDay.from(Integer
                                                .parseInt(result.get(i).getInstDt().substring(0, 4)),
                                        Integer.parseInt(
                                                result.get(i).getInstDt().substring(4, 6)) - 1,
                                        Integer.parseInt(
                                                result.get(i).getInstDt().substring(6, 8))));
                            }
                        }

                        materialCalendarView.addDecorator(new com.mobile.alliance.api.DayDisableDecorator(disabledDates));


                        ArrayList<CalendarDay> enabledDates = new ArrayList<>();


                        for(int i=0;i<result.size();i++)
                        {
                            if(result.get(i).getAbleYn().equals("Y")){
                                enabledDates.add(CalendarDay.from(Integer
                                                .parseInt(result.get(i).getInstDt().substring(0, 4)),
                                        Integer.parseInt(
                                                result.get(i).getInstDt().substring(4, 6)) - 1,
                                        Integer.parseInt(
                                                result.get(i).getInstDt().substring(6, 8))));
                            }
                        }

                        materialCalendarView.addDecorator(new com.mobile.alliance.api.DayEnableDecorator(enabledDates));


                        //for(int i = 0 ;)
                    }
                    else
                    {
                        commonHandler.showAlertDialog("배송 연기 날짜 목록 조회 실패", "조회 0건");
                    }
                    showProgress(false);
                }
                else{
                    commonHandler.showAlertDialog("배송 연기 날짜 목록 조회 실패","응답결과가 없습니다.");
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<List<DeliveryDelayVO>> call, Throwable t) {
                commonHandler.showAlertDialog("배송 연기 날짜 목록 조회 실패","접속실패\n"+t.getMessage());
                showProgress(false);
            }
        });
    }





    //선택한 연기한 배송일자를 저장하기
    private void deliveryDelaySave(DeliveryDelaySaveVO deliveryDelaySaveVO) {
        service.mDeliveryDelaySave(deliveryDelaySaveVO).enqueue(new Callback<DeliveryDelayVO>() {

            @Override
            public void onResponse(Call<DeliveryDelayVO> call, Response<DeliveryDelayVO> response) {

                if(response.isSuccessful()) //응답값이 있다
                {
                    DeliveryDelayVO resultSave = response.body();

                    if(resultSave.getRtnYn().equals("Y"))
                    {
                        //commonHandler.showAlertDialog("배송 연기 성공", "배송일 연기에 성공하였습니다.");
                        View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                        AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryDelay.this);
                        builder.setView(dialogView);

                        AlertDialog alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();

                        Button ok_btn = dialogView.findViewById(R.id.successBtn);
                        TextView ok_txt = dialogView.findViewById(R.id.successText);
                        ok_txt.setText("배송 연기 성공");
                        ok_btn.setOnClickListener(new View.OnClickListener() {
                            @SneakyThrows @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                //배송목록 갱신하기
                                DeliveryListFragment deliveryListFragment = (DeliveryListFragment)DeliveryListFragment._DeliveryListFragment;
                                deliveryListFragment.changeDate();


                                //상세정보보기 다시 정보 읽기

                                DeliveryListDetail deliveryListDetail = (DeliveryListDetail)DeliveryListDetail._DeliveryListDetail;

                                //deliveryListDetail.deliveryDetailSrch();
                                deliveryListDetail.finish();
                                finish();
                            }
                        });

                    }
                    else
                    {
                        commonHandler.showAlertDialog("배송 연기 실패", resultSave.getRtnMsg());
                    }
                    showProgress(false);
                }
                else{
                    commonHandler.showAlertDialog("배송 연기 실패","응답결과가 없습니다.");
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<DeliveryDelayVO> call, Throwable t) {
                commonHandler.showAlertDialog("배송 연기 실패","접속실패\n"+t.getMessage());
                showProgress(false);
            }
        });
    }


    //로딩바
    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        deliveryDelyBack();
    }
    private void deliveryDelyBack(){

        View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryDelay.this);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
        TextView ok_txt = dialogView.findViewById(R.id.confirmText);
        ok_txt.setTextSize(20);
        ok_txt.setTypeface(null, Typeface.BOLD);
        ok_txt.setText("배송연기 화면을 나가시겠습니까?");
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