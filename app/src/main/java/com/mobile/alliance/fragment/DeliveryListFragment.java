package com.mobile.alliance.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobile.alliance.R;

import com.mobile.alliance.activity.DeliveryListDetail;
import com.mobile.alliance.adapter.DeliveryListViewAdapter;
import com.mobile.alliance.api.BackPressCloseHandler;
import com.mobile.alliance.api.CommonHandler;
import com.mobile.alliance.api.PersistentCookieStore;
import com.mobile.alliance.api.RetrofitClient;
import com.mobile.alliance.api.ServiceApi;
import com.mobile.alliance.entity.delivery.DeliveryVO;


import java.lang.reflect.Array;
import java.net.CookieManager;
import java.net.CookiePolicy;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lombok.SneakyThrows;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeliveryListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeliveryListFragment extends Fragment {
    public static ArrayMap addr = null    ;
    public static Fragment _DeliveryListFragment; //다른액티비티에서 이 액티비티를 제어하기위해

    //배송목록 조회한 결과를 담을 곳
    List<DeliveryVO> deliveryResult;


    ListView deliverylistview ; //배송목록 리스트뷰
    ImageView noResultImg;    //결과없음 이미지
    TextView noResultText;  //결과없음 텍스트



    DeliveryListViewAdapter adapter;

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
    CommonHandler commonHandler = new CommonHandler(getActivity());


    int[] position = {0,0,0,0,0};   //리스트 위치이동

    int topBtnPosition=1;   //맨 위 버튼 5개 전체, 배송중, 미완료, 미마감, 배송완료 1,2,3,4,5


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    TextView selDate;
    Button dBtn01;  //전체
    Button dBtn02;  //배송중
    Button dBtn03;  //미완료
    Button dBtn04;  //미마감
    Button dBtn05;  //배송완료


    public DeliveryListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeliveryListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeliveryListFragment newInstance(String param1, String param2) {
        DeliveryListFragment fragment = new DeliveryListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }






    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREAN);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }



    private String getTime() throws ParseException {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        String t = getCalDay(mFormat.format(mDate) ,0);
         return t;
    }
    private String getCalDay(String selDateS ,int d) throws ParseException {
        selDateS = selDateS.substring(0,10);
        String from = selDateS.replace(".","-");
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN);


        Date to = transFormat.parse(from);

        Calendar cal = Calendar.getInstance();
        cal.setTime(to);
        cal.add(Calendar.DATE , d);
        int dayNum = cal.get(Calendar.DAY_OF_WEEK);
        String day = "";
        switch (dayNum) {
            case 1: day = "일"; break;
            case 2: day = "월"; break;
            case 3: day = "화"; break;
            case 4: day = "수"; break;
            case 5: day = "목"; break;
            case 6: day = "금"; break;
            case 7: day = "토"; break;
        }
        return (mFormat.format(cal.getTime()) + " "+day);
    }

    @SneakyThrows
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        _DeliveryListFragment = DeliveryListFragment.this;//다른액티비티에서 이 액티비티를 제어하기위해


        adapter = new DeliveryListViewAdapter();

        //내부에 데이터 저장하는것
        sharePref = getContext().getSharedPreferences(SHARE_NAME, getContext().MODE_PRIVATE);
        editor = sharePref.edit();

        backPressCloseHandler = new BackPressCloseHandler(getActivity());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //키보드 뜰때 입력창 가리지 않고 화면을 위로 올리기

        //api 이용시 쿠키전달
        PersistentCookieStore cookieStore = new PersistentCookieStore(getContext());
        //CookieManager cookieManager  = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        client = new OkHttpClient
                .Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                //.cookieJar(new JavaNetCookieJar(CookieManager()))
                .addInterceptor(commonHandler.httpLoggingInterceptor())
                .build();


        View v = inflater.inflate(R.layout.fragment_delivery_list, container, false);

        //진행바
        mProgressView = (ProgressBar)v.findViewById(R.id.fListProgress);
        //http통신
        service = RetrofitClient.getClient(client).create(ServiceApi.class);

        // Inflate the layout for this fragment
        selDate =(TextView)v.findViewById(R.id.dlvyDtText) ;
        selDate.setText(getTime());

        //05050 테스트용         selDate.setText("2021.08.02");


        changeDate();

        selDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDate();
            }
        });

        TextView downText = (TextView)v.findViewById(R.id.downText) ;
        downText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDate();
            }
        });

        TextView leftText = (TextView)v.findViewById(R.id.leftText) ;
        leftText.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows
            @Override
            public void onClick(View view) {
                selDate.setText(getCalDay(selDate.getText().toString(),-1));
                changeDate();
            }
        });
        TextView rightText = (TextView)v.findViewById(R.id.rightText) ;
        rightText.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows
            @Override
            public void onClick(View view) {
                selDate.setText(getCalDay(selDate.getText().toString(),1));
                changeDate();
            }
        });

        dBtn01 = (Button)v.findViewById(R.id.dBtn01) ;
        dBtn02 = (Button)v.findViewById(R.id.dBtn02) ;
        dBtn03 = (Button)v.findViewById(R.id.dBtn03) ;
        dBtn04 = (Button)v.findViewById(R.id.dBtn04) ;
        dBtn05 = (Button)v.findViewById(R.id.dBtn05) ;
        dBtnSelect(dBtn01,"deSel");dBtnSelect(dBtn02,"deSel");dBtnSelect(dBtn03,"deSel");dBtnSelect(dBtn04,"deSel");dBtnSelect(dBtn05,"deSel");
        dBtnSelect(dBtn01,"sel");

        dBtn01.setOnClickListener(new View.OnClickListener() {
             @SuppressLint("LongLogTag")
             @Override
            public void onClick(View view) {
                 topBtnPosition =1;
                 dBtnSelect(dBtn01, "deSel");
                 dBtnSelect(dBtn02, "deSel");
                 dBtnSelect(dBtn03, "deSel");
                 dBtnSelect(dBtn04, "deSel");
                 dBtnSelect(dBtn05, "deSel");

                 dBtnSelect(dBtn01, "sel");


                 if (deliveryResult != null && deliveryResult.size() > 0) {
                     addr = new ArrayMap();
                     adapter = new DeliveryListViewAdapter();
                     for (int i = 0; i < deliveryResult.size(); i++) {

                             //리스트 아답터에 넣는부분
                             int stateColor = 0;
                            /*
                            회색 4000	배송모바일 확정
                            배송중 회색 5000	해피콜 완료
                            배송중 초록 6999	부분상차(완료)
                            배송중 초록 7000	상차완료
                            빨강 8000	배송완료(미마감)
                            파랑 9999	배송완료

                             */
                             if (deliveryResult.get(i).getDlvyStatCd().equals("4000") || deliveryResult.get(i).getDlvyStatCd().equals("5000")) {

                                 stateColor = Color.parseColor("#696969");
                             } else if (deliveryResult.get(i).getDlvyStatCd().equals("6999") || deliveryResult.get(i).getDlvyStatCd().equals("7000")) {
                                 stateColor = Color.parseColor("#20B2AA");
                             } else if (deliveryResult.get(i).getDlvyStatCd().equals("8000")) {
                                 stateColor = Color.parseColor("#FF0000");
                             } else if (deliveryResult.get(i).getDlvyStatCd().equals("9999")) {
                                 stateColor = Color.parseColor("#0000FF");
                             }


                             //INST_CTGR 시공 카테고리 : 가구 / 가전/ 서비스  1000가구/2000가전/3000서비스 (오더속성창의 제일 처음에 보여지는 부분)
                             Drawable instCtrgImg = null;
                             if (deliveryResult.get(i).getInstCtgr().equals("1000")) {
                                 instCtrgImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_2);
                             } else if (deliveryResult.get(i).getInstCtgr().equals("2000")) {
                                 instCtrgImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_3);
                             } else if (deliveryResult.get(i).getInstCtgr().equals("3000")) {
                                 instCtrgImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_1);
                             }


                             //INST_TYPE       시공유형 - 일반 / 익일   (table : NORMAL 정상배송 / NEXTDAY 익일배송 )(오더속성창의 두번째에보여지는부분) 일반=정상배송, 익일=익일배송
                             Drawable instTypeImg = null;
                             if (deliveryResult.get(i).getInstType().equals("NORMAL")) {
                                 instTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_4);
                             } else if (deliveryResult.get(i).getInstType().equals("NEXTDAY")) {
                                 instTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_5);
                             }


                             //SO_TYPE 주문유형 : 일반      //반품        //교환
                            /*
                            1000	일반오더
                            2000	반품오더
                            2500	교환오더
                            4000	AS오더
                              */
                             Drawable soTypeImg = null;
                             String soTypeNm = null;
                             if (deliveryResult.get(i).getSoType().equals("1000")) {
                                 soTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_2_2);
                             } else if (deliveryResult.get(i).getSoType().equals("2000")) {
                                 soTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_2_3);
                             }

                             /*
                             else if (deliveryResult.get(i).getSoType().equals("2500")) {
                                 soTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_2_4);
                             } else if (deliveryResult.get(i).getSoType().equals("4000")) {
                                 soTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_2_1);
                             }
                            */
                             else{
                                 soTypeNm =" "+deliveryResult.get(i).getSoTypeNm()+" ";
                             }


                             //SEAT_TYPE       시공좌석유형  1인 / 2인
                             Drawable seatTypeImg = null;
                             if (deliveryResult.get(i).getSeatType().equals("1111")) {
                                 seatTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_3_1);
                             } else if (deliveryResult.get(i).getSeatType().equals("2222")) {
                                 seatTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_3_2);
                             }

                         addr.put(i,deliveryResult.get(i).getAddr1());
                             adapter.addItem(

                                     stateColor
                                     , deliveryResult.get(i).getSeq().toString()
                                     , instCtrgImg
                                     , instTypeImg
                                     , soTypeImg
                                     , soTypeNm
                                     , seatTypeImg
                                     , deliveryResult.get(i).getDlvyStatNm()
                                     , deliveryResult.get(i).getSoNo().toString()
                                     , deliveryResult.get(i).getAddr1().toString()
                                     , deliveryResult.get(i).getAddr2()
                                     , deliveryResult.get(i).getAcptEr().toString()
                                     , deliveryResult.get(i).getAcptTel2().toString()
                                     ,  deliveryResult.get(i).getTblUserMId().toString()
                                     //,sharePref.getString("tblUserMId","")
                                     //, "1"            //0505 테스트용도
                                     , deliveryResult.get(i).getInstMobileMId().toString()
                                     , deliveryResult.get(i).getTelCnt().toString()
                                     ,i
                             );


                     } //end of FOR
                     position[topBtnPosition-1] = deliverylistview.getFirstVisiblePosition(); //위치값 저장



                     deliverylistview.setAdapter(adapter);
                     //Log.d("btn01 Arrays.toString(position)",Arrays.toString(position));
                     //Log.d("btn01 position["+(topBtnPosition-1)+"]",position[topBtnPosition-1]+"");
                     //Log.d("btn01 topBtnPosition",topBtnPosition+"");


                     deliverylistview.setSelection(position[topBtnPosition-1]);   //해당위치로 이동
                 }
             }
        });
        //배송중 버튼을 눌렀을때
        dBtn02.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                topBtnPosition =2;
                dBtnSelect(dBtn01,"deSel");dBtnSelect(dBtn02,"deSel");dBtnSelect(dBtn03,"deSel");dBtnSelect(dBtn04,"deSel");dBtnSelect(dBtn05,"deSel");
                dBtnSelect(dBtn02,"sel");


                if(deliveryResult != null && deliveryResult.size() > 0) {
                    addr = new ArrayMap();
                    adapter = new DeliveryListViewAdapter();
                    int g=0;
                    for (int i = 0; i < deliveryResult.size(); i++)
                    {

                         if(        deliveryResult.get(i).getDlvyStatCd().equals("5000")
                                 || deliveryResult.get(i).getDlvyStatCd().equals("6999")
                                 || deliveryResult.get(i).getDlvyStatCd().equals("7000")
                         )
                         {

                                 //리스트 아답터에 넣는부분
                                 int stateColor = 0;
                                /*
                                회색 4000	배송모바일 확정
                                배송중 회색 5000	해피콜 완료
                                배송중 초록 6999	부분상차(완료)
                                배송중 초록 7000	상차완료
                                빨강 8000	배송완료(미마감)
                                파랑 9999	배송완료

                                 */
                                 if (deliveryResult.get(i).getDlvyStatCd().equals("4000") || deliveryResult.get(i).getDlvyStatCd().equals("5000")) {

                                     stateColor = Color.parseColor("#696969");
                                 } else if (deliveryResult.get(i).getDlvyStatCd().equals("6999") || deliveryResult.get(i).getDlvyStatCd().equals("7000")) {
                                     stateColor = Color.parseColor("#20B2AA");
                                 } else if (deliveryResult.get(i).getDlvyStatCd().equals("8000")) {
                                     stateColor = Color.parseColor("#FF0000");
                                 } else if (deliveryResult.get(i).getDlvyStatCd().equals("9999")) {
                                     stateColor = Color.parseColor("#0000FF");
                                 }


                                 //INST_CTGR 시공 카테고리 : 가구 / 가전/ 서비스  1000가구/2000가전/3000서비스 (오더속성창의 제일 처음에 보여지는 부분)
                                 Drawable instCtrgImg = null;
                                 if (deliveryResult.get(i).getInstCtgr().equals("1000")) {
                                     instCtrgImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_2);
                                 } else if (deliveryResult.get(i).getInstCtgr().equals("2000")) {
                                     instCtrgImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_3);
                                 } else if (deliveryResult.get(i).getInstCtgr().equals("3000")) {
                                     instCtrgImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_1);
                                 }


                                 //INST_TYPE       시공유형 - 일반 / 익일   (table : NORMAL 정상배송 / NEXTDAY 익일배송 )(오더속성창의 두번째에보여지는부분) 일반=정상배송, 익일=익일배송
                                 Drawable instTypeImg = null;
                                 if (deliveryResult.get(i).getInstType().equals("NORMAL")) {
                                     instTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_4);
                                 } else if (deliveryResult.get(i).getInstType().equals("NEXTDAY")) {
                                     instTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_5);
                                 }


                                         //SO_TYPE 주문유형 : 일반      //반품        //교환
                                /*
                                1000	일반오더
                                2000	반품오더
                                2500	교환오더
                                4000	AS오더
                                  */
                                 Drawable soTypeImg = null;
                                 String soTypeNm=null;
                                 if (deliveryResult.get(i).getSoType().equals("1000")) {
                                     soTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_2_2);
                                 } else if (deliveryResult.get(i).getSoType().equals("2000")) {
                                     soTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_2_3);
                                 }

                                /* else if (deliveryResult.get(i).getSoType().equals("2500")) {
                                     soTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_2_4);
                                 } else if (deliveryResult.get(i).getSoType().equals("4000")) {
                                     soTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_2_1);
                                 }*/

                                 else{
                                     soTypeNm =" "+deliveryResult.get(i).getSoTypeNm()+" ";
                                 }


                                 //SEAT_TYPE       시공좌석유형  1인 / 2인
                                 Drawable seatTypeImg = null;
                                 if (deliveryResult.get(i).getSeatType().equals("1111")) {
                                     seatTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_3_1);
                                 } else if (deliveryResult.get(i).getSeatType().equals("2222")) {
                                     seatTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_3_2);
                                 }

                             addr.put(g,deliveryResult.get(i).getAddr1());
                                 adapter.addItem(

                                         stateColor
                                         , deliveryResult.get(i).getSeq().toString()
                                         , instCtrgImg
                                         , instTypeImg
                                         , soTypeImg
                                         , soTypeNm
                                         , seatTypeImg
                                         , deliveryResult.get(i).getDlvyStatNm()
                                         , deliveryResult.get(i).getSoNo().toString()
                                         , deliveryResult.get(i).getAddr1().toString()
                                         , deliveryResult.get(i).getAddr2()
                                         , deliveryResult.get(i).getAcptEr().toString()
                                         , deliveryResult.get(i).getAcptTel2().toString()
                                         ,  deliveryResult.get(i).getTblUserMId().toString()
                                         //,sharePref.getString("tblUserMId","")
                                         //, "1"            //0505 테스트용도
                                         , deliveryResult.get(i).getInstMobileMId().toString()
                                         , deliveryResult.get(i).getTelCnt().toString()
                                         ,g
                                 );
                                 g++;
                         }  //end of IF

                    } //end of FOR



                //deliverylistview.setAdapter(adapter);
                    position[topBtnPosition-1] = deliverylistview.getFirstVisiblePosition(); //위치값 저장

                    deliverylistview.setAdapter(adapter);
                    //Log.d("btn02 Arrays.toString(position)",Arrays.toString(position));
                    //Log.d("btn02 position["+(topBtnPosition-1)+"]",position[topBtnPosition-1]+"");
                    //Log.d("btn02 topBtnPosition",topBtnPosition+"");

                    deliverylistview.setSelection(position[topBtnPosition-1]);   //해당위치로 이동
                }
            }
        });
        dBtn03.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                topBtnPosition =3;
                dBtnSelect(dBtn01,"deSel");dBtnSelect(dBtn02,"deSel");dBtnSelect(dBtn03,"deSel");dBtnSelect(dBtn04,"deSel");dBtnSelect(dBtn05,"deSel");
                dBtnSelect(dBtn03,"sel");
                //미완료
                 if(deliveryResult != null && deliveryResult.size() > 0) {
                     addr = new ArrayMap();
                adapter = new DeliveryListViewAdapter();
                     int g=0;
                for (int i = 0; i < deliveryResult.size(); i++) {
                     if(        deliveryResult.get(i).getDlvyStatCd().equals("4000") ) {

                         //리스트 아답터에 넣는부분
                             int stateColor = 0;
                            /*
                            회색 4000	배송모바일 확정
                            배송중 회색 5000	해피콜 완료
                            배송중 초록 6999	부분상차(완료)
                            배송중 초록 7000	상차완료
                            빨강 8000	배송완료(미마감)
                            파랑 9999	배송완료

                             */
                             if (deliveryResult.get(i).getDlvyStatCd().equals("4000") || deliveryResult.get(i).getDlvyStatCd().equals("5000")) {

                                 stateColor = Color.parseColor("#696969");
                             } else if (deliveryResult.get(i).getDlvyStatCd().equals("6999") || deliveryResult.get(i).getDlvyStatCd().equals("7000")) {
                                 stateColor = Color.parseColor("#20B2AA");
                             } else if (deliveryResult.get(i).getDlvyStatCd().equals("8000")) {
                                 stateColor = Color.parseColor("#FF0000");
                             } else if (deliveryResult.get(i).getDlvyStatCd().equals("9999")) {
                                 stateColor = Color.parseColor("#0000FF");
                             }


                             //INST_CTGR 시공 카테고리 : 가구 / 가전/ 서비스  1000가구/2000가전/3000서비스 (오더속성창의 제일 처음에 보여지는 부분)
                             Drawable instCtrgImg = null;
                             if (deliveryResult.get(i).getInstCtgr().equals("1000")) {
                                 instCtrgImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_2);
                             } else if (deliveryResult.get(i).getInstCtgr().equals("2000")) {
                                 instCtrgImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_3);
                             } else if (deliveryResult.get(i).getInstCtgr().equals("3000")) {
                                 instCtrgImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_1);
                             }


                             //INST_TYPE       시공유형 - 일반 / 익일   (table : NORMAL 정상배송 / NEXTDAY 익일배송 )(오더속성창의 두번째에보여지는부분) 일반=정상배송, 익일=익일배송
                             Drawable instTypeImg = null;
                             if (deliveryResult.get(i).getInstType().equals("NORMAL")) {
                                 instTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_4);
                             } else if (deliveryResult.get(i).getInstType().equals("NEXTDAY")) {
                                 instTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_5);
                             }


                                     //SO_TYPE 주문유형 : 일반      //반품        //교환
                            /*
                            1000	일반오더
                            2000	반품오더
                            2500	교환오더
                            4000	AS오더
                              */
                             Drawable soTypeImg = null;
                             String soTypeNm=null;
                             if (deliveryResult.get(i).getSoType().equals("1000")) {
                                 soTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_2_2);
                             } else if (deliveryResult.get(i).getSoType().equals("2000")) {
                                 soTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_2_3);
                             }

                            /* else if (deliveryResult.get(i).getSoType().equals("2500")) {
                                 soTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_2_4);
                             } else if (deliveryResult.get(i).getSoType().equals("4000")) {
                                 soTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_2_1);
                             }*/
                             else{
                                 soTypeNm =" "+deliveryResult.get(i).getSoTypeNm()+" ";
                             }

                             //SEAT_TYPE       시공좌석유형  1인 / 2인
                             Drawable seatTypeImg = null;
                             if (deliveryResult.get(i).getSeatType().equals("1111")) {
                                 seatTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_3_1);
                             } else if (deliveryResult.get(i).getSeatType().equals("2222")) {
                                 seatTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_3_2);
                             }

                         addr.put(g,deliveryResult.get(i).getAddr1());
                             adapter.addItem(

                                     stateColor
                                     , deliveryResult.get(i).getSeq().toString()
                                     , instCtrgImg
                                     , instTypeImg
                                     , soTypeImg
                                     ,soTypeNm
                                     , seatTypeImg
                                     , deliveryResult.get(i).getDlvyStatNm()
                                     , deliveryResult.get(i).getSoNo().toString()
                                     , deliveryResult.get(i).getAddr1().toString()
                                     , deliveryResult.get(i).getAddr2()
                                     , deliveryResult.get(i).getAcptEr().toString()
                                     , deliveryResult.get(i).getAcptTel2().toString()
                                     ,  deliveryResult.get(i).getTblUserMId().toString()
                                     //,sharePref.getString("tblUserMId","")
                                     //, "1"            //0505 테스트용도
                                     , deliveryResult.get(i).getInstMobileMId().toString()
                                     , deliveryResult.get(i).getTelCnt().toString()
                                     ,g
                             );
                             g++;
                     }  //end of IF

                 } //end of FOR



                 //deliverylistview.setAdapter(adapter);
                     position[topBtnPosition-1] = deliverylistview.getFirstVisiblePosition(); //위치값 저장

                     deliverylistview.setAdapter(adapter);
                     //Log.d("btn03 Arrays.toString(position)",Arrays.toString(position));
                     //Log.d("btn03 position["+(topBtnPosition-1)+"]",position[topBtnPosition-1]+"");
                     //Log.d("btn03 topBtnPosition",topBtnPosition+"");

                     deliverylistview.setSelection(position[topBtnPosition-1]);   //해당위치로 이동

                }
            }

        });
        dBtn04.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                topBtnPosition =4;
                dBtnSelect(dBtn01,"deSel");dBtnSelect(dBtn02,"deSel");dBtnSelect(dBtn03,"deSel");dBtnSelect(dBtn04,"deSel");dBtnSelect(dBtn05,"deSel");
                dBtnSelect(dBtn04,"sel");
                //미마감
                 if(deliveryResult != null && deliveryResult.size() > 0) {
                     addr = new ArrayMap();
                adapter = new DeliveryListViewAdapter();
                     int g=0;
                for (int i = 0; i < deliveryResult.size(); i++) {
                     if(        deliveryResult.get(i).getDlvyStatCd().equals("8000") ) {

                         //리스트 아답터에 넣는부분
                             int stateColor = 0;
                            /*
                            회색 4000	배송모바일 확정
                            배송중 회색 5000	해피콜 완료
                            배송중 초록 6999	부분상차(완료)
                            배송중 초록 7000	상차완료
                            빨강 8000	배송완료(미마감)
                            파랑 9999	배송완료

                             */
                             if (deliveryResult.get(i).getDlvyStatCd().equals("4000") || deliveryResult.get(i).getDlvyStatCd().equals("5000")) {

                                 stateColor = Color.parseColor("#696969");
                             } else if (deliveryResult.get(i).getDlvyStatCd().equals("6999") || deliveryResult.get(i).getDlvyStatCd().equals("7000")) {
                                 stateColor = Color.parseColor("#20B2AA");
                             } else if (deliveryResult.get(i).getDlvyStatCd().equals("8000")) {
                                 stateColor = Color.parseColor("#FF0000");
                             } else if (deliveryResult.get(i).getDlvyStatCd().equals("9999")) {
                                 stateColor = Color.parseColor("#0000FF");
                             }


                             //INST_CTGR 시공 카테고리 : 가구 / 가전/ 서비스  1000가구/2000가전/3000서비스 (오더속성창의 제일 처음에 보여지는 부분)
                             Drawable instCtrgImg = null;
                             if (deliveryResult.get(i).getInstCtgr().equals("1000")) {
                                 instCtrgImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_2);
                             } else if (deliveryResult.get(i).getInstCtgr().equals("2000")) {
                                 instCtrgImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_3);
                             } else if (deliveryResult.get(i).getInstCtgr().equals("3000")) {
                                 instCtrgImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_1);
                             }


                             //INST_TYPE       시공유형 - 일반 / 익일   (table : NORMAL 정상배송 / NEXTDAY 익일배송 )(오더속성창의 두번째에보여지는부분) 일반=정상배송, 익일=익일배송
                             Drawable instTypeImg = null;
                             if (deliveryResult.get(i).getInstType().equals("NORMAL")) {
                                 instTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_4);
                             } else if (deliveryResult.get(i).getInstType().equals("NEXTDAY")) {
                                 instTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_5);
                             }


                                     //SO_TYPE 주문유형 : 일반      //반품        //교환
                            /*
                            1000	일반오더
                            2000	반품오더
                            2500	교환오더
                            4000	AS오더
                              */
                             Drawable soTypeImg = null;
                             String soTypeNm=null;
                             if (deliveryResult.get(i).getSoType().equals("1000")) {
                                 soTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_2_2);
                             } else if (deliveryResult.get(i).getSoType().equals("2000")) {
                                 soTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_2_3);
                             }

                            /* else if (deliveryResult.get(i).getSoType().equals("2500")) {
                                 soTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_2_4);
                             } else if (deliveryResult.get(i).getSoType().equals("4000")) {
                                 soTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_2_1);
                             }*/
                             else{
                                 soTypeNm =" "+deliveryResult.get(i).getSoTypeNm()+" ";
                             }

                             //SEAT_TYPE       시공좌석유형  1인 / 2인
                             Drawable seatTypeImg = null;
                             if (deliveryResult.get(i).getSeatType().equals("1111")) {
                                 seatTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_3_1);
                             } else if (deliveryResult.get(i).getSeatType().equals("2222")) {
                                 seatTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_3_2);
                             }

                         addr.put(g,deliveryResult.get(i).getAddr1());
                             adapter.addItem(

                                     stateColor
                                     , deliveryResult.get(i).getSeq().toString()
                                     , instCtrgImg
                                     , instTypeImg
                                     , soTypeImg
                                     ,soTypeNm
                                     , seatTypeImg
                                     , deliveryResult.get(i).getDlvyStatNm()
                                     , deliveryResult.get(i).getSoNo().toString()
                                     , deliveryResult.get(i).getAddr1().toString()
                                     , deliveryResult.get(i).getAddr2()
                                     , deliveryResult.get(i).getAcptEr().toString()
                                     , deliveryResult.get(i).getAcptTel2().toString()
                                     ,  deliveryResult.get(i).getTblUserMId().toString()
                                     //,sharePref.getString("tblUserMId","")
                                     //, "1"            //0505 테스트용도
                                     , deliveryResult.get(i).getInstMobileMId().toString()
                                     , deliveryResult.get(i).getTelCnt().toString()
                                     ,g
                             );
                             g++;
                     }  //end of IF

                 } //end of FOR



                 //deliverylistview.setAdapter(adapter);
                     position[topBtnPosition-1] = deliverylistview.getFirstVisiblePosition(); //위치값 저장

                     deliverylistview.setAdapter(adapter);
                     Log.d("btn04 Arrays.toString(position)",Arrays.toString(position));
                     Log.d("btn04 position["+(topBtnPosition-1)+"]",position[topBtnPosition-1]+"");
                     Log.d("btn04 topBtnPosition",topBtnPosition+"");

                     deliverylistview.setSelection(position[topBtnPosition-1]);   //해당위치로 이동

                }
            }

        });
        dBtn05.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                topBtnPosition =5;
                dBtnSelect(dBtn01,"deSel");dBtnSelect(dBtn02,"deSel");dBtnSelect(dBtn03,"deSel");dBtnSelect(dBtn04,"deSel");dBtnSelect(dBtn05,"deSel");
                dBtnSelect(dBtn05,"sel");

                //배송완료
                 if(deliveryResult != null && deliveryResult.size() > 0) {
                     addr = new ArrayMap();
                adapter = new DeliveryListViewAdapter();
                     int g=0;
                for (int i = 0; i < deliveryResult.size(); i++) {
                     if(        deliveryResult.get(i).getDlvyStatCd().equals("9999") ) {

                             //리스트 아답터에 넣는부분
                             int stateColor = 0;
                            /*
                            회색 4000	배송모바일 확정
                            배송중 회색 5000	해피콜 완료
                            배송중 초록 6999	부분상차(완료)
                            배송중 초록 7000	상차완료
                            빨강 8000	배송완료(미마감)
                            파랑 9999	배송완료

                             */
                             if (deliveryResult.get(i).getDlvyStatCd().equals("4000") || deliveryResult.get(i).getDlvyStatCd().equals("5000")) {

                                 stateColor = Color.parseColor("#696969");
                             } else if (deliveryResult.get(i).getDlvyStatCd().equals("6999") || deliveryResult.get(i).getDlvyStatCd().equals("7000")) {
                                 stateColor = Color.parseColor("#20B2AA");
                             } else if (deliveryResult.get(i).getDlvyStatCd().equals("8000")) {
                                 stateColor = Color.parseColor("#FF0000");
                             } else if (deliveryResult.get(i).getDlvyStatCd().equals("9999")) {
                                 stateColor = Color.parseColor("#0000FF");
                             }


                             //INST_CTGR 시공 카테고리 : 가구 / 가전/ 서비스  1000가구/2000가전/3000서비스 (오더속성창의 제일 처음에 보여지는 부분)
                             Drawable instCtrgImg = null;
                             if (deliveryResult.get(i).getInstCtgr().equals("1000")) {
                                 instCtrgImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_2);
                             } else if (deliveryResult.get(i).getInstCtgr().equals("2000")) {
                                 instCtrgImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_3);
                             } else if (deliveryResult.get(i).getInstCtgr().equals("3000")) {
                                 instCtrgImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_1);
                             }


                             //INST_TYPE       시공유형 - 일반 / 익일   (table : NORMAL 정상배송 / NEXTDAY 익일배송 )(오더속성창의 두번째에보여지는부분) 일반=정상배송, 익일=익일배송
                             Drawable instTypeImg = null;
                             if (deliveryResult.get(i).getInstType().equals("NORMAL")) {
                                 instTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_4);
                             } else if (deliveryResult.get(i).getInstType().equals("NEXTDAY")) {
                                 instTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_1_5);
                             }


                                     //SO_TYPE 주문유형 : 일반      //반품        //교환
                            /*
                            1000	일반오더
                            2000	반품오더
                            2500	교환오더
                            4000	AS오더
                              */
                             Drawable soTypeImg = null;
                             String soTypeNm=null;
                             if (deliveryResult.get(i).getSoType().equals("1000")) {
                                 soTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_2_2);
                             } else if (deliveryResult.get(i).getSoType().equals("2000")) {
                                 soTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_2_3);
                             }
                             /*else if (deliveryResult.get(i).getSoType().equals("2500")) {
                                 soTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_2_4);
                             } else if (deliveryResult.get(i).getSoType().equals("4000")) {
                                 soTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_2_1);
                             }*/
                             else{
                                 soTypeNm =" "+deliveryResult.get(i).getSoTypeNm()+" ";
                             }

                             //SEAT_TYPE       시공좌석유형  1인 / 2인
                             Drawable seatTypeImg = null;
                             if (deliveryResult.get(i).getSeatType().equals("1111")) {
                                 seatTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_3_1);
                             } else if (deliveryResult.get(i).getSeatType().equals("2222")) {
                                 seatTypeImg = ContextCompat.getDrawable(getContext(), R.drawable.badge_3_2);
                             }

                         addr.put(g,deliveryResult.get(i).getAddr1());
                             adapter.addItem(

                                     stateColor
                                     , deliveryResult.get(i).getSeq().toString()
                                     , instCtrgImg
                                     , instTypeImg
                                     , soTypeImg
                                     ,soTypeNm
                                     , seatTypeImg
                                     , deliveryResult.get(i).getDlvyStatNm()
                                     , deliveryResult.get(i).getSoNo().toString()
                                     , deliveryResult.get(i).getAddr1().toString()
                                     , deliveryResult.get(i).getAddr2()
                                     , deliveryResult.get(i).getAcptEr().toString()
                                     , deliveryResult.get(i).getAcptTel2().toString()
                                     ,  deliveryResult.get(i).getTblUserMId().toString()
                                     //,sharePref.getString("tblUserMId","")
                                     //, "1"            //0505 테스트용도
                                     , deliveryResult.get(i).getInstMobileMId().toString()
                                     , deliveryResult.get(i).getTelCnt().toString()
                                     ,g
                             );
                             g++;
                     }  //end of IF

                 } //end of FOR



                 //deliverylistview.setAdapter(adapter);
                     position[topBtnPosition-1] = deliverylistview.getFirstVisiblePosition(); //위치값 저장

                     deliverylistview.setAdapter(adapter);
                     Log.d("btn05 Arrays.toString(position)",Arrays.toString(position));
                     Log.d("btn05 position["+(topBtnPosition-1)+"]",position[topBtnPosition-1]+"");
                     Log.d("btn05 topBtnPosition",topBtnPosition+"");


                     deliverylistview.setSelection(position[topBtnPosition-1]);   //해당위치로 이동

                }
            }

        });





        deliverylistview = (ListView)v.findViewById(R.id.deliverylistview);

        //editor.putInt("listPosition",deliverylistview.getFirstVisiblePosition());
        //editor.apply();


        noResultImg =   (ImageView)v.findViewById(R.id.noResultImg);
        noResultText = (TextView)v.findViewById(R.id.noResultText);
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DeliveryListViewItem listViewItem = (DeliveryListViewItem)adapterView.getItemAtPosition(i).;
                Toast.makeText(getActivity(),"["+listViewItem.getSoNo()+"]\n"+listViewItem.getName()+"님을 선택하였습니다.",Toast.LENGTH_SHORT).show();
            }
        });*/
        return v;
    }

    //버튼들 적용시키기 되돌리기 - 미마감을 제외한 나머지들
    private void dBtnSelect(Button btn, String type) {
        if(btn.equals(dBtn01) || btn.equals(dBtn02) || btn.equals(dBtn03) || btn.equals(dBtn05))  {
            if(type.equals("deSel")) {
                btn.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.rounded_delivery_btn01_01));
                btn.setTextColor(getResources().getColorStateList(R.color.black));
            }
            else if(type.equals("sel")) {
                btn.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.rounded_delivery_btn01_02));
                btn.setTextColor(getResources().getColorStateList(R.color.white));
            }
        }
        else if(btn.equals(dBtn04)){    //미마감
            if(type.equals("deSel")) {
                btn.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.rounded_delivery_btn04_01));
                btn.setTextColor(Color.parseColor("#D50000"));
            }
            else if(type.equals("sel")) {
                btn.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.rounded_delivery_btn04_02));
                btn.setTextColor(getResources().getColorStateList(R.color.white));
            }
        }
    }
    int y=0, m=0, d=0, h=0, mi=0;
    void showDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this.getContext(), new DatePickerDialog.OnDateSetListener() {
            @SneakyThrows
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                y = year;
                m = month+1;
                d = dayOfMonth;
                String selY = Integer.toString(y);
                String selM = Integer.toString(m);
                if(selM.length() == 1){
                    selM = "0"+selM;
                }
                String selD = Integer.toString(d);
                if(selD.length() == 1){
                    selD = "0"+selD;
                }
                String t = getCalDay(selY+"."+selM+"."+selD,0);

                selDate.setText(t);
                    changeDate();
            }
        }   ,   Integer.parseInt(selDate.getText().toString().substring(0,4))
            ,   Integer.parseInt(selDate.getText().toString().substring(5,7))-1
            ,   Integer.parseInt(selDate.getText().toString().substring(8,10)
        ));

        //datePickerDialog.setMessage("날짜선택");
        datePickerDialog.show();
    }


    public void changeDate() throws ParseException {
        adapter = new DeliveryListViewAdapter();
        //배송 목록 조회 SP 수행 부분

        deliveryList(new DeliveryVO(   sharePref.getString("cmpyCd","")
                                    ,  selDate.getText().toString().substring(0,10).replace(".","")
                                    ,  sharePref.getString("tblUserMId","")     //로그인한 사람것의 목록을 보여줌    //   "1"  //0505 테스트
                                    ,  sharePref.getString("mobileGrntCd","")          //      ""
                                    )
                    );
        showProgress(true);
    }

    //SP 타는곳 //화면 열리자 마자 조회
    @SuppressLint("LongLogTag")
    private void deliveryList(DeliveryVO deliveryVO) {
        adapter = new DeliveryListViewAdapter();

        service
                .mDeliveryList(deliveryVO)
                .enqueue(new Callback<List<DeliveryVO>>() {

            @SneakyThrows
            @Override
            public void onResponse(Call<List<DeliveryVO>> call, Response<List<DeliveryVO>> response) {

                if(response.isSuccessful()) //응답값이 있다
                {
                    List<DeliveryVO> result = response.body();
                    adapter = new DeliveryListViewAdapter();
                    if(result.size() > 0) {

                        deliveryResult = result;
                        addr = new ArrayMap();
                        int dBtn01Cnt = 0;  //전체
                        int dBtn02Cnt = 0;  //배송중
                        int dBtn03Cnt = 0;  //미완료
                        int dBtn04Cnt = 0;  //미마감
                        int dBtn05Cnt = 0;  //배송완료

                        for(int i= 0 ; i < result.size() ; i++){
                            //리스트 아답터에 넣는부분
                            int stateColor = 0;
                            /*
                            회색 4000	배송모바일 확정
                            회색 5000	해피콜 완료
                            초록 6999	부분상차(완료)
                            초록 7000	상차완료
                            빨강 8000	배송완료(미마감)
                            파랑 9999	배송완료

                             */
                            if(result.get(i).getDlvyStatCd().equals("4000") || result.get(i).getDlvyStatCd().equals("5000"))
                            {

                                stateColor = Color.parseColor("#696969");
                            }else if(result.get(i).getDlvyStatCd().equals("6999") || result.get(i).getDlvyStatCd().equals("7000"))
                            {
                                stateColor = Color.parseColor("#20B2AA");
                            }
                            else if(result.get(i).getDlvyStatCd().equals("8000") )
                            {
                                stateColor = Color.parseColor("#FF0000");
                            }
                            else if(result.get(i).getDlvyStatCd().equals("9999") )
                            {
                                stateColor = Color.parseColor("#0000FF");
                            }




                            //INST_CTGR 시공 카테고리 : 가구 / 가전/ 서비스  1000가구/2000가전/3000서비스 (오더속성창의 제일 처음에 보여지는 부분)
                            Drawable instCtrgImg = null;  //Log.d("instCtrg : " + i,deliveryResult.get(i).getInstCtgr());
                            if(result.get(i).getInstCtgr().equals("1000")){
                                instCtrgImg=ContextCompat.getDrawable(getContext(),R.drawable.badge_1_2);
                            }
                            else if(result.get(i).getInstCtgr().equals("2000")){
                                instCtrgImg=ContextCompat.getDrawable(getContext(),R.drawable.badge_1_3);
                            }
                            else if(result.get(i).getInstCtgr().equals("3000")){
                                instCtrgImg=ContextCompat.getDrawable(getContext(),R.drawable.badge_1_1);
                            }




                            //INST_TYPE       시공유형 - 일반 / 익일   (table : NORMAL 정상배송 / NEXTDAY 익일배송 )(오더속성창의 두번째에보여지는부분) 일반=정상배송, 익일=익일배송
                            Drawable instTypeImg = null;

                            if(result.get(i).getInstType().equals("NORMAL")){
                                //Log.d("result.get("+i+").getInstType()", result.get(i).getInstType());
                                instTypeImg=ContextCompat.getDrawable(getContext(), R.drawable.badge_1_4);
                            }
                            else if(result.get(i).getInstType().equals("NEXTDAY")){
                                //Log.d("result.get("+i+").getInstType()", result.get(i).getInstType());
                                instTypeImg=ContextCompat.getDrawable(getContext(), R.drawable.badge_1_5);
                            }
                            //Log.d("instTypeImg "+i, instTypeImg+"");

                            //SO_TYPE 주문유형 : 일반      //반품        //교환
                            /*
                            1000	일반오더
                            2000	반품오더
                            2500	교환오더
                            4000	AS오더
                              */
                            Drawable soTypeImg = null;
                            String soTypeNm=null;
                            if(result.get(i).getSoType().equals("1000")){
                                soTypeImg=ContextCompat.getDrawable(getContext(), R.drawable.badge_2_2);
                            }
                            else if(result.get(i).getSoType().equals("2000")){
                                soTypeImg=ContextCompat.getDrawable(getContext(), R.drawable.badge_2_3);
                            }
                            /*else if(result.get(i).getSoType().equals("2500")){
                                soTypeImg=ContextCompat.getDrawable(getContext(), R.drawable.badge_2_4);
                            }
                            else if(result.get(i).getSoType().equals("4000")){
                                soTypeImg=ContextCompat.getDrawable(getContext(), R.drawable.badge_2_1);
                            }*/

                            else{
                                soTypeNm =" "+deliveryResult.get(i).getSoTypeNm()+" ";
                            }




                            //SEAT_TYPE       시공좌석유형  1인 / 2인
                            Drawable seatTypeImg = null;
                            if(result.get(i).getSeatType().equals("1111")){
                                seatTypeImg=ContextCompat.getDrawable(getContext(), R.drawable.badge_3_1);
                            }
                            else if(result.get(i).getSeatType().equals("2222")){
                                seatTypeImg=ContextCompat.getDrawable(getContext(), R.drawable.badge_3_2);
                            }

                            addr.put(i,deliveryResult.get(i).getAddr1());


                            adapter.addItem(

                                    stateColor
                                    , result.get(i).getSeq().toString()
                                    , instCtrgImg
                                    , instTypeImg
                                    , soTypeImg
                                    ,soTypeNm
                                    , seatTypeImg
                                    ,  result.get(i).getDlvyStatNm()
                                    ,  result.get(i).getSoNo().toString()
                                    ,  result.get(i).getAddr1().toString()
                                    ,  result.get(i).getAddr2()
                                    ,  result.get(i).getAcptEr().toString()
                                    ,  result.get(i).getAcptTel2().toString()
                                    ,  result.get(i).getTblUserMId().toString()
                                    //,   sharePref.getString("tblUserMId","")
                                    //,"1"                      //0505테스트
                                    ,  result.get(i).getInstMobileMId().toString()
                                    ,  result.get(i).getTelCnt().toString()
                                    ,i
                            );

                            //전체 수량 체크
                            dBtn01Cnt++;
                            //배송중 수량 체크
                            if (    deliveryResult.get(i).getDlvyStatCd().equals("5000")
                                 || deliveryResult.get(i).getDlvyStatCd().equals("6999")
                                 || deliveryResult.get(i).getDlvyStatCd().equals("7000")
                            )
                            {       dBtn02Cnt++;     }
                            //미완료 수량 체크
                            if (    deliveryResult.get(i).getDlvyStatCd().equals("4000"))
                            {       dBtn03Cnt++;     }
                            //미마감 수량 체크
                            if (    deliveryResult.get(i).getDlvyStatCd().equals("8000"))
                            {       dBtn04Cnt++;     }
                            //배송완료 수량 체크
                            if (    deliveryResult.get(i).getDlvyStatCd().equals("9999"))
                            {       dBtn05Cnt++;     }

                        }
                        deliverylistview.setVisibility(View.VISIBLE);
                        noResultImg.setVisibility(View.GONE);
                        noResultText.setVisibility(View.GONE);


                        if(topBtnPosition == 1) { position[topBtnPosition - 1] = 0; dBtn01.callOnClick();}
                        else if(topBtnPosition == 2) { position[topBtnPosition - 1] = 0; dBtn02.callOnClick();}
                        else if(topBtnPosition == 3) { position[topBtnPosition - 1] = 0; dBtn03.callOnClick();}
                        else if(topBtnPosition == 4) { position[topBtnPosition - 1] = 0; dBtn04.callOnClick();}
                        else if(topBtnPosition == 5) { position[topBtnPosition - 1] = 0; dBtn05.callOnClick();}
                        else {
                            position[topBtnPosition - 1] = deliverylistview.getFirstVisiblePosition(); //위치값 저장
                            deliverylistview.setAdapter(adapter);

                            deliverylistview.setSelection(position[topBtnPosition - 1]);   //해당위치로 이동
                        }

                        dBtn01.setText("전체 "    + dBtn01Cnt+"");
                        dBtn02.setText("배송중 "  + dBtn02Cnt+"");
                        dBtn03.setText("미완료 "  + dBtn03Cnt+"");
                        dBtn04.setText("미마감 "  + dBtn04Cnt+"");
                        dBtn05.setText("배송완료 "  + dBtn05Cnt+"");


                    }
                    else
                    {

                        dBtn01.setText("전체");
                        dBtn02.setText("배송중");
                        dBtn03.setText("미완료");
                        dBtn04.setText("미마감");
                        dBtn05.setText("배송완료");
                        deliverylistview.setVisibility(View.GONE);
                        noResultImg.setVisibility(View.VISIBLE);
                        noResultText.setVisibility(View.VISIBLE);


                        if(topBtnPosition == 1) { dBtn01.callOnClick();}
                        else if(topBtnPosition == 2) { dBtn02.callOnClick();}
                        else if(topBtnPosition == 3) { dBtn03.callOnClick();}
                        else if(topBtnPosition == 4) { dBtn04.callOnClick();}
                        else if(topBtnPosition == 5) { dBtn05.callOnClick();}
                        else {
                            position[topBtnPosition - 1] = deliverylistview.getFirstVisiblePosition(); //위치값 저장
                            deliverylistview.setAdapter(adapter);


                            deliverylistview.setSelection(position[topBtnPosition - 1]);   //해당위치로 이동
                        }
                    }
                }
                else{
                    dBtn01.setText("전체");
                    dBtn02.setText("배송중");
                    dBtn03.setText("미완료");
                    dBtn04.setText("미마감");
                    dBtn05.setText("배송완료");
                    deliverylistview.setVisibility(View.GONE);
                    noResultImg.setVisibility(View.VISIBLE);
                    noResultText.setVisibility(View.VISIBLE);

                    if(topBtnPosition == 1) { dBtn01.callOnClick();}
                    else if(topBtnPosition == 2) { dBtn02.callOnClick();}
                    else if(topBtnPosition == 3) { dBtn03.callOnClick();}
                    else if(topBtnPosition == 4) { dBtn04.callOnClick();}
                    else if(topBtnPosition == 5) { dBtn05.callOnClick();}
                    else {
                        position[topBtnPosition - 1] = deliverylistview.getFirstVisiblePosition(); //위치값 저장
                        deliverylistview.setAdapter(adapter);

                        deliverylistview.setSelection(position[topBtnPosition - 1]);   //해당위치로 이동
                    }

                    commonHandler.showAlertDialog("조회 실패","조회 실패\n"+response.code() +"\n"+ response.message());
                }
                showProgress(false);
            }




            @SneakyThrows
            @Override
            public void onFailure(Call<List<DeliveryVO>> call, Throwable t) {
                dBtn01.setText("전체");
                dBtn02.setText("배송중");
                dBtn03.setText("미완료");
                dBtn04.setText("미마감");
                dBtn05.setText("배송완료");
                adapter = new DeliveryListViewAdapter();
                deliverylistview.setVisibility(View.GONE);
                noResultImg.setVisibility(View.VISIBLE);
                noResultText.setVisibility(View.VISIBLE);

                if(topBtnPosition == 1) { dBtn01.callOnClick();}
                else if(topBtnPosition == 2) { dBtn02.callOnClick();}
                else if(topBtnPosition == 3) { dBtn03.callOnClick();}
                else if(topBtnPosition == 4) { dBtn04.callOnClick();}
                else if(topBtnPosition == 5) { dBtn05.callOnClick();}
                else {
                    position[topBtnPosition - 1] = deliverylistview.getFirstVisiblePosition(); //위치값 저장
                    deliverylistview.setAdapter(adapter);

                    deliverylistview.setSelection(position[topBtnPosition - 1]);   //해당위치로 이동
                }
                commonHandler.showAlertDialog("배송목록 조회 실패","배송목록 조회 실패\n접속실패\n"+t.getMessage());
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