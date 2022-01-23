package com.mobile.alliance.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mobile.alliance.R;
import com.mobile.alliance.activity.BarCodeScanActivity;

import com.mobile.alliance.api.BackPressCloseHandler;
import com.mobile.alliance.api.CommonHandler;
import com.mobile.alliance.api.PersistentCookieStore;
import com.mobile.alliance.api.RetrofitClient;
import com.mobile.alliance.api.ServiceApi;
import com.mobile.alliance.entity.barCode.BarCodeLiftChangeVO;
import com.mobile.alliance.entity.barCode.BarCodeVO;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URLDecoder;

import lombok.SneakyThrows;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangeTruckFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangeTruckFragment extends Fragment {
    public static Fragment _ChangeTruckFragment; //다른액티비티에서 이 액티비티를 제어하기위해
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
    CommonHandler commonHandler;
    private Context mContext;
    private Activity mActivity;
    @Override
    public void onAttach(Context context) {
        mContext = context;
        if (context instanceof Activity) {
            mActivity = (Activity)context;
        }
        commonHandler = new CommonHandler(mActivity);
        super.onAttach(context);
    }

    // detach에서는 변수 clearing 해주기 leak방지
    @Override
    public void onDetach() {
        mActivity = null;
        mContext = null;
        super.onDetach();
    }

    Button barBtn,soNoInputBtn;
    EditText soNoScan;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChangeTruckFragment(){
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChangeTruckFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChangeTruckFragment newInstance(String param1, String param2){
        ChangeTruckFragment fragment = new ChangeTruckFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    public void onResume(){
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        _ChangeTruckFragment = ChangeTruckFragment.this;//다른액티비티에서 이 액티비티를 제어하기위해

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

        View v = inflater.inflate(R.layout.fragment_change_truck, container, false);

        //진행바
        mProgressView = (ProgressBar)v.findViewById(R.id.barCodeProgress);
        //http통신
        service = RetrofitClient.getClient(client).create(ServiceApi.class);

        //바코드 스캔 버튼을 눌렀을때 바코드 스캔하는 새 액티비티를 오픈
        barBtn = (Button) v.findViewById(R.id.barBtn);   //바코드
        barBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                IntentIntegrator.forSupportFragment(ChangeTruckFragment.this).setCaptureActivity(BarCodeScanActivity.class).initiateScan();
            }
        });

        soNoScan = (EditText) v.findViewById(R.id.soNoScan);
        soNoInputBtn = (Button) v.findViewById(R.id.soNoInputBtn);
        soNoInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                mBarCodeScan(new BarCodeVO(
                    sharePref.getString("cmpyCd","")
                ,   soNoScan.getText().toString()
                ));
                showProgress(true);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(resultCode == Activity.RESULT_OK){
            IntentResult scanResult =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            String re = scanResult.getContents();
            String message = re;
            soNoScan.setText(re);
            mBarCodeScan(new BarCodeVO(
                    sharePref.getString("cmpyCd", "")
                ,   re
            ));
            showProgress(true);
        }
    }

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    //판매오더번호 조회
    private void mBarCodeScan(BarCodeVO barCodeVO) {
        service.mBarCodeScan(barCodeVO).enqueue(new Callback<BarCodeVO>() {
            @SneakyThrows @Override
            public void onResponse(Call<BarCodeVO> call, Response<BarCodeVO> response) {
                if(response.isSuccessful()) { //응답값이 있다
                    BarCodeVO result = response.body();
                    if(result.getInstMobileMId() != null && !result.getInstMobileMId().trim().equals(""))
                    {
                        //시공계획과 모바일데이터가 생성되어있지 않음
                        if(result.getDlvyStatCd() == null || result.getDlvyStatCd().trim().equals("") || Integer.parseInt(result.getDlvyStatCd().trim()) < 4000) {
                            View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setView(dialogView);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.show();
                            Button ok_btn = dialogView.findViewById(R.id.successBtn);
                            TextView ok_txt = dialogView.findViewById(R.id.successText);
                            ok_txt.setText("시공계획 & 모바일데이터가 생성되지 않은 주문번호입니다. 사무실에 문의하세요.");
                            ok_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                }
                            });
                        }
                        //userMId 가 같다면 (상차를 본인이한거면)
                        else if (result.getExecUserMId().equals(sharePref.getString("tblUserMId","")))
                        {
                            View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setView(dialogView);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.show();
                            Button ok_btn = dialogView.findViewById(R.id.successBtn);
                            TextView ok_txt = dialogView.findViewById(R.id.successText);
                            ok_txt.setText("이미 상차할당이 되어 주문입니다.");
                            ok_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                }
                            });
                        }
                        //상차변경하기
                        else {
                            View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setView(dialogView);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.show();
                            Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
                            TextView ok_txt = dialogView.findViewById(R.id.confirmText);
                            ok_txt.setTextSize(20);
                            ok_txt.setTypeface(null, Typeface.BOLD);
                            ok_txt.setText("주문번호 "+ result.getSoNo() +" 는 ["+result.getUserNm()+"](이)가 배송예정입니다. 변경하시겠습니까?");
                            SpannableString spannableString = new SpannableString(ok_txt.getText());
                            int start = ok_txt.getText().toString().indexOf("는 [")+3;
                            int end = ok_txt.getText().toString().indexOf("](이)가")+0;
                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#0000FF")),start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //특정위치만 색 변경
                            //spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);    //특정위치만 폰트 스타일 변경
                            spannableString.setSpan(new RelativeSizeSpan(1.0f), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);    //특정위치만 크기변경
                            ok_txt.setText(spannableString);

                            ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            ok_btn.setOnClickListener(new View.OnClickListener() {
                                @SneakyThrows @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                    mBarCodeLiftChange(new BarCodeLiftChangeVO(
                                            result.getInstMobileMId()
                                        ,   sharePref.getString("tblUserMId", "")
                                    ));
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
                    else {
                        View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setView(dialogView);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                        Button ok_btn = dialogView.findViewById(R.id.successBtn);
                        TextView ok_txt = dialogView.findViewById(R.id.successText);
                        ok_txt.setText("판매오더번호 조회 실패\n조회 결과가 없습니다.");
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                TextView ok_txt = dialogView.findViewById(R.id.successText);

                //ok_txt.setText("판매오더번호 조회 실패\n조회오류가 발생하였습니다.");
                //20220120 정연호 수정. was에서 [500 internal server error] excpition발생시 오류추적번호 나오게 변경
                ok_txt.setText("판매오더번호 조회 실패\n"+response.code() +"\n"+ response.message()+"\n\n"+
                URLDecoder.decode(response.errorBody().string(),"UTF-8"));

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
            public void onFailure(Call<BarCodeVO> call, Throwable t) {
                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                TextView ok_txt = dialogView.findViewById(R.id.successText);
                ok_txt.setText("판매오더번호 조회 실패\n접속실패\n"+t.getMessage());
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

    //판매오더번호로 상차변경
    private void mBarCodeLiftChange(BarCodeLiftChangeVO barCodeLiftChangeVO) {
        service.mBarCodeLiftChange(barCodeLiftChangeVO).enqueue(new Callback<BarCodeVO>() {
            @SneakyThrows @Override
            public void onResponse(Call<BarCodeVO> call, Response<BarCodeVO> response) {
                if(response.isSuccessful()) { //응답값이 있다
                    BarCodeVO result = response.body();
                    if(result.getRtnYn().equals("Y")) {
                        View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setView(dialogView);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                        Button ok_btn = dialogView.findViewById(R.id.successBtn);
                        TextView ok_txt = dialogView.findViewById(R.id.successText);
                        ok_txt.setText("변경 성공");
                        ok_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                soNoScan.setText("");   //판매오더번호 입력 란 비우기
                                //다시 바코드 띄우기
                                //IntentIntegrator.forSupportFragment(ChangeTruckFragment.this).setCaptureActivity(BarCodeScanActivity.class).initiateScan();
                            }
                        });
                    }
                    else {
                        View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setView(dialogView);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                        Button ok_btn = dialogView.findViewById(R.id.successBtn);
                        TextView ok_txt = dialogView.findViewById(R.id.successText);
                        ok_txt.setText("변경 실패");
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setView(dialogView);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    Button ok_btn = dialogView.findViewById(R.id.successBtn);
                    TextView ok_txt = dialogView.findViewById(R.id.successText);

                    //20220120 정연호 수정. was에서 [500 internal server error] excpition발생시 오류추적번호 나오게 변경
                    //ok_txt.setText("변경실패\n조회오류가 발생하였습니다.");
                    ok_txt.setText("변경실패\n\n"+response.code() +"\n"+ response.message()+"\n\n"+
                    URLDecoder.decode(response.errorBody().string(),"UTF-8"));

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
            public void onFailure(Call<BarCodeVO> call, Throwable t) {
                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                TextView ok_txt = dialogView.findViewById(R.id.successText);
                ok_txt.setText("변경실패\n접속실패\n"+t.getMessage());
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
}