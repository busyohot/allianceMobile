package com.mobile.alliance.adapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.alliance.R;
import com.mobile.alliance.activity.DeliveryListDetail;
import com.mobile.alliance.activity.DeliveryListViewItem;
import com.mobile.alliance.activity.MapActivity;
import com.mobile.alliance.activity.NoCmplActivity;
import com.mobile.alliance.api.CommonHandler;
import com.mobile.alliance.api.PersistentCookieStore;
import com.mobile.alliance.api.RetrofitClient;
import com.mobile.alliance.api.ServiceApi;
import com.mobile.alliance.entity.delivery.DeliverySeqVO;
import com.mobile.alliance.entity.delivery.DeliveryTelVO;
import com.mobile.alliance.entity.delivery.DeliveryVO;
import com.mobile.alliance.fragment.DeliveryListFragment;

import org.w3c.dom.Text;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryListViewAdapter extends BaseAdapter {

    public static final int TYPE_CLASS_NUMBER = 0x00000002; //EditText 입력창에 숫자만 넣게하기
            
            
    private ArrayList<DeliveryListViewItem> arrayList = new ArrayList<>();


    public static OkHttpClient client;

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
    CommonHandler commonHandler;




    public DeliveryListViewAdapter(){

    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ResourceAsColor") @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Context context = viewGroup.getContext();

         commonHandler = new CommonHandler((Activity) context);

        //내부에 데이터 저장하는것
        sharePref = context.getSharedPreferences(SHARE_NAME, context.MODE_PRIVATE);
        editor = sharePref.edit();


        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.deliverylistviewitem,viewGroup,false);
        }

        TextView    selNo = (TextView) view.findViewById(R.id.selNo) ;

        //tblUserMId
        TextView  tblUserMId = (TextView) view.findViewById(R.id.tblUserMId);

        //instMobileMId
        TextView  instMobileMId = (TextView) view.findViewById(R.id.instMobileMId);


        //listLeftBar왼쪽 색깔 세로 바 - 배송진행상태(listState) 와 연동
        TextView  listleftBar = (TextView) view.findViewById(R.id.listleftBar);

        //listSeqBtn 순번 터치하는 투명 버튼
        Button listSeqBtn = (Button) view.findViewById(R.id.listSeqBtn);

        TextView  listSeq = (TextView) view.findViewById(R.id.listSeq);
        //listSeq 순번 - 변경가능 SEQ

        //listDownImg 순번변경 버튼 이미지
        ImageView listDownImg = (ImageView) view.findViewById(R.id.listDownImg);

        //listImg01 가구/가전/서비스 INST_CTGR 시공카테고리
        ImageView listImg01 = (ImageView) view.findViewById(R.id.deliveryDetailImg01);

        //listImg02  INST_TYPE   시공유형     일반    익일
        ImageView listImg02 = (ImageView) view.findViewById(R.id.deliveryDetailImg02);

        //listImg03  주문유형 SO_TYPE 일반 반품 교환
        ImageView listImg03 = (ImageView) view.findViewById(R.id.deliveryDetailImg03);
        
        //20211205 추가
        TextView listText03 = (TextView) view.findViewById(R.id.deliveryDetailTextView03);
        
        //listImg04 1인 / 2인 SEAT_TYPE   시공좌석유형
        ImageView listImg04 = (ImageView) view.findViewById(R.id.deliveryDetailImg04);

        //listState 배송상태 한글표시 글자 색은 상태코드 에 따라 바뀜
        TextView  listState = (TextView) view.findViewById(R.id.listState);

        //listSoNo 오더번호 (대괄호 꼭 넣어줘야한다) SO_NO
        TextView  listSoNo = (TextView) view.findViewById(R.id.listSoNo);

        listSoNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(context,listSoNo.getText()+" 선택",Toast.LENGTH_SHORT).show();

                context.startActivity(new Intent (context,DeliveryListDetail.class)
                        .putExtra("instMobileMId",instMobileMId.getText().toString())
                        .putExtra("no",selNo.getText().toString())
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));  //다음 액티비티를 열고


            }
        });


        //listName 고객명 ACPT_ER
        TextView  listName = (TextView) view.findViewById(R.id.listName);
        listName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context,listName.getText()+" 선택",Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent (context,DeliveryListDetail.class)
                        .putExtra("instMobileMId",instMobileMId.getText().toString())
                        .putExtra("no",selNo.getText().toString())
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));  //다음 액티비티를 열고

            }
        });
        //listAddr1  고객주소 ADDR1
        TextView  listAddr1 = (TextView) view.findViewById(R.id.listAddr1);

        //listAddr2  상세주소 ADDR2
        TextView  listAddr2 = (TextView) view.findViewById(R.id.listAddr2);


        listAddr1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast toast = Toast.makeText(context.getApplicationContext(), listAddr1.getText()+"\n"+listAddr2.getText() == null ? "" : listAddr1.getText()+"\n"+listAddr2.getText(), Toast.LENGTH_SHORT);   //0 short , 1 long
                ViewGroup group = (ViewGroup)toast.getView();
                TextView msgTextView = (TextView)group.getChildAt(0);
                msgTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
                msgTextView.setTypeface(null,Typeface.BOLD);
                toast.setGravity(Gravity.CENTER|Gravity.CENTER,0, 0);
                toast.show();

            }
        });
        listAddr2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast toast = Toast.makeText(context.getApplicationContext(), listAddr1.getText()+"\n"+listAddr2.getText() == null ? "" : listAddr1.getText()+"\n"+listAddr2.getText(), Toast.LENGTH_SHORT);   //0 short , 1 long
                ViewGroup group = (ViewGroup)toast.getView();
                TextView msgTextView = (TextView)group.getChildAt(0);
                msgTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
                msgTextView.setTypeface(null,Typeface.BOLD);
                toast.setGravity(Gravity.CENTER|Gravity.CENTER,0, 0);
                toast.show();
            }
        });

        TextView listHp = (TextView) view.findViewById(R.id.listHp);


        ImageView listDetail = (ImageView) view.findViewById(R.id.listDetail);
        listDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent (context,DeliveryListDetail.class)
                        .putExtra("instMobileMId",instMobileMId.getText().toString())
                        .putExtra("no",selNo.getText().toString())
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));  //다음 액티비티를 열고


            }
        });








        //listTelCnt
        TextView  listTelCnt = (TextView) view.findViewById(R.id.listTelCnt);

        DeliveryListViewItem deliveryListViewItem = arrayList.get(i);

        listleftBar.setBackgroundColor(deliveryListViewItem.getColorCode());
        listSeq.setText(deliveryListViewItem.getSeq());
        listImg01.setImageDrawable(deliveryListViewItem.getImg01());
        listImg02.setImageDrawable(deliveryListViewItem.getImg02());
        listImg03.setImageDrawable(deliveryListViewItem.getImg03());
        listText03.setText(deliveryListViewItem.getText03());   //20211205 추가
        listImg04.setImageDrawable(deliveryListViewItem.getImg04());
        listState.setText(deliveryListViewItem.getState());
        listState.setTextColor(deliveryListViewItem.getColorCode());
        listSoNo.setText(deliveryListViewItem.getSoNo());
        listName.setText(deliveryListViewItem.getName());
        listAddr1.setText(deliveryListViewItem.getAddr1());
        listAddr2.setText(deliveryListViewItem.getAddr2());
        listHp.setText(deliveryListViewItem.getHp());

        selNo.setText(deliveryListViewItem.getNo()+"");

        ImageView listMap = (ImageView) view.findViewById(R.id.listMap);
        listMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                context.startActivity(new Intent (context, MapActivity.class)
                        .putExtra("instMobileMId",instMobileMId.getText().toString())
                        .putExtra("listAddr1",listAddr1.getText().toString())
                        .putExtra("no",selNo.getText().toString())
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));  //다음 액티비티를 열고
                        //.addFlags(Integer.parseInt(selNo.getText().toString())));  //다음 액티비티를 열고


            }
        });



        tblUserMId.setText(deliveryListViewItem.getTblUserMId());
        instMobileMId.setText(deliveryListViewItem.getInstMobileMId());

        listTelCnt.setText(deliveryListViewItem.getTelCnt());



        //api 이용시 쿠키전달
        PersistentCookieStore cookieStore = new PersistentCookieStore(context);
        //CookieManager cookieManager  = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        client = new OkHttpClient
                .Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                //.cookieJar(new JavaNetCookieJar(CookieManager()))
                .addInterceptor(commonHandler.httpLoggingInterceptor())
                .build();

        mProgressView = (ProgressBar) view.findViewById(R.id.list_progress);
        service = RetrofitClient.getClient(client).create(ServiceApi.class);


        //핸드폰 번호를 터치했을때 - SP수행해야함
        listHp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listTelCnt.setText(String.valueOf(Integer.parseInt(listTelCnt.getText().toString())+1));
                deliveryListViewItem.setTelCnt(listTelCnt.getText().toString());
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                Uri tel = Uri.parse("tel:"+listHp.getText());
                intent.setData(tel);

                context.startActivity(intent);


                mDeliveryTelUpdate(new DeliveryTelVO(
                        instMobileMId.getText().toString()

                        ,sharePref.getString("tblUserMId","")
                        ,sharePref.getString("mobileGrntCd","")

                ));
                showProgress(true);



            }
        });
        //순번을 변경했을때 - SP 수행햐야함
        listSeqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final CharSequence[] oItems = {   "1", "2", "3", "4", "5", "6","7", "8", "9", "10"
                                                , "11", "12", "13", "14", "15", "16","17", "18", "19", "20"
                                                , "21", "22", "23", "24", "25", "26","27", "28", "29", "30"
                                                };       //20220110 정연호 추가 , 5 추가, 11~30 추가

                AlertDialog.Builder msgBuilder = new AlertDialog.Builder(context,R.style.deliverySelectNoStyle)
                    .setTitle("배송 순번을 선택하세요")
                    .setItems(oItems, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            listSeq.setText(oItems[i]);
                            deliveryListViewItem.setSeq(listSeq.getText().toString());

                            mDeliverySeqUpdate(new DeliverySeqVO(instMobileMId.getText().toString()
                                    , listSeq.getText().toString()
                                    ,sharePref.getString("tblUserMId","")
                            ));
                            showProgress(true);
                        }
                    })
                    .setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i){
                        }
                    });

                AlertDialog msgDlg = msgBuilder.create();
                msgDlg.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog){

                        Button negativeButton =
                                ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                        Button positiveButton =
                                ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                        negativeButton.setTextSize(24);
                        positiveButton.setTextSize(24);
                        //버튼 사이 정렬
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT );
                        negativeButton.setLayoutParams(params);
                        positiveButton.setLayoutParams(params);

                        negativeButton.invalidate();
                        positiveButton.invalidate();
                    }
                });
                msgDlg.show();
                msgDlg.setCancelable(false);
            }
        });

        return view;
    }
    //넣는부분
    public void addItem(
                            int colorCode
                        ,   String seq
                        , Drawable image01
                        , Drawable image02
                        , Drawable image03
                            , String soTypeNm
                        , Drawable image04
                        , String state
                        , String soNo
                        , String addr1
                        , String addr2
                        , String name
                        , String hp
            , String tblUserMId
            , String instMobileMId
            , String telCnt
                     ,int i
    ) {
        DeliveryListViewItem deliveryListViewItem = new DeliveryListViewItem();
        deliveryListViewItem.setColorCode(colorCode);
        deliveryListViewItem.setSeq(seq);
        deliveryListViewItem.setImg01(image01);
        deliveryListViewItem.setImg02(image02);
        deliveryListViewItem.setImg03(image03);
        deliveryListViewItem.setText03(soTypeNm);
        deliveryListViewItem.setImg04(image04);
        deliveryListViewItem.setState(state);
        deliveryListViewItem.setSoNo(soNo);
        deliveryListViewItem.setAddr1(addr1);
        deliveryListViewItem.setAddr2(addr2);
        deliveryListViewItem.setName(name);
        deliveryListViewItem.setHp(hp);

        deliveryListViewItem.setTblUserMId(tblUserMId);
        deliveryListViewItem.setInstMobileMId(instMobileMId);
        deliveryListViewItem.setTelCnt(telCnt);
        deliveryListViewItem.setNo(i);

        arrayList.add(deliveryListViewItem);
    }

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void mDeliverySeqUpdate(DeliverySeqVO deliverySeqVO) {    //요청 VO
        service.mDeliverySeqUpdate(deliverySeqVO).enqueue(new Callback<DeliveryVO>() {    //앞 요청VO, CallBack 응답 VO

            @Override
            public void onResponse(Call<DeliveryVO> call, Response<DeliveryVO> response) {  //둘다 응답 VO

                if(response.isSuccessful()) //응답값이 없다
                {
                    DeliveryVO result = response.body();
                    if(result.getRtnYn().equals("Y")) {
                        //내부저장소에 있는걸 불러오기

                    }
                    else
                    {
                        commonHandler.showAlertDialog("순번 변경 실패", result.getRtnMsg());
                    }
                }
                else{
                    commonHandler.showAlertDialog("순번 변경 실패","응답결과가 없습니다.");
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<DeliveryVO> call, Throwable t) {

                commonHandler.showAlertDialog("순번 변경 접속 실패","접속실패\n"+t.getMessage());
                showProgress(false);
            }
        });
    }

    private void mDeliveryTelUpdate(DeliveryTelVO deliveryTelVO) {    //요청 VO
        service.mDeliveryTelUpdate(deliveryTelVO).enqueue(new Callback<DeliveryVO>() {    //앞 요청VO, CallBack 응답 VO

            @Override
            public void onResponse(Call<DeliveryVO> call, Response<DeliveryVO> response) {  //둘다 응답 VO

                if(response.isSuccessful()) //응답값이 없다
                {
                    DeliveryVO result = response.body();
                    if(result.getRtnYn().equals("Y")) {
                        //내부저장소에 있는걸 불러오기

                    }
                    else
                    {
                        commonHandler.showAlertDialog("통화 카운트 저장 실패", result.getRtnMsg());
                    }
                }
                else{
                    commonHandler.showAlertDialog("통화 카운트 저장 실패","응답결과가 없습니다.");
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<DeliveryVO> call, Throwable t) {

                commonHandler.showAlertDialog("통화 카운트 저장 접속 실패","접속실패\n"+t.getMessage());
                showProgress(false);
            }
        });
    }
}
