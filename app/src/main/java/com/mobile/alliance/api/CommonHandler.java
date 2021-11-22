package com.mobile.alliance.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mobile.alliance.R;
import com.mobile.alliance.activity.FirstActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import lombok.SneakyThrows;
import okhttp3.logging.HttpLoggingInterceptor;

public class CommonHandler {



    private Activity activity;
    public CommonHandler(Activity context)
    {
        this.activity = context;
    }

    //okhttp 로그 남기기

    public HttpLoggingInterceptor httpLoggingInterceptor(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                android.util.Log.e("httpLog :", message + "");
            }
        });

        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    //alertDialog

    public void showAlertDialog(String title,String message) {

        View dialogView = activity.getLayoutInflater().inflate(R.layout.custom_dial_success, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        Button ok_btn = dialogView.findViewById(R.id.successBtn);
        TextView ok_txt = dialogView.findViewById(R.id.successText);
        ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START); //글자를 왼쪽 정렬
        ok_txt.setText(title +"\n"+message);
        ok_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                alertDialog.dismiss();

            }
        });
    }

    public void showFinishAlertDialog(String title,String message, String finisyYn) {




        View dialogView = activity.getLayoutInflater().inflate(R.layout.custom_dial_success, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        Button ok_btn = dialogView.findViewById(R.id.successBtn);
        TextView ok_txt = dialogView.findViewById(R.id.successText);
        ok_txt.setText(title+"\n"+message);
        ok_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                activity.finish();
            }
        });
    }



    public void showLogoutDialog(String title,String message) {

        View dialogView = activity.getLayoutInflater().inflate(R.layout.custom_dial_success, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        Button ok_btn = dialogView.findViewById(R.id.successBtn);
        TextView ok_txt = dialogView.findViewById(R.id.successText);
        ok_txt.setText(title +"\n"+message);
        ok_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                //로그인 액티비티로 이동시키기
                Intent loginIntent = new Intent(activity, FirstActivity.class);
                activity.startActivity(loginIntent);
                activity.finish();

            }
        });

    }



    public static String getLocalIpAddress() {
        try {
            //Device에 있는 모든 네트워크에 대해 뺑뺑이를 돕니다.
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();

                //네트워크 중에서 IP가 할당된 넘들에 대해서 뺑뺑이를 한 번 더 돕니다.
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {

                    InetAddress inetAddress = enumIpAddr.nextElement();

                    //네트워크에는 항상 Localhost 즉, 루프백(LoopBack)주소가 있으며, 우리가 원하는 것이 아닙니다.
                    //IP는 IPv6와 IPv4가 있습니다.
                    //IPv6의 형태 : fe80::64b9::c8dd:7003
                    //IPv4의 형태 : 123.234.123.123
                    //어떻게 나오는지는 찍어보세요.
                    if(inetAddress.isLoopbackAddress()) {
                        Log.i("IPAddress", intf.getDisplayName() + "(loopback) | " + inetAddress.getHostAddress());
                    }
                    else
                    {
                        Log.i("IPAddress", intf.getDisplayName() + " | " + inetAddress.getHostAddress());
                    }

                    //루프백이 아니고, IPv4가 맞다면 리턴~~~
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        //if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @SneakyThrows
    @SuppressLint("LongLogTag")
    public String calTime(String loginTimeS, int intervalS){


        //현재 날짜시간을 문자열로 변경
        long now = System.currentTimeMillis();
        Date nowTime = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        String cur = sdf.format(nowTime);


        //날짜변경 유형
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");

        //로그인한 시간을 Date로 parsing 후 time가져오기
        Date loginTime = dateFormat.parse(loginTimeS);
        long LoginDateTime = loginTime.getTime();


        //현재시간을 요청시간의 형태로 format 후 time 가져오기
        Date curTime = dateFormat.parse(cur);
        long curDateTime = curTime.getTime();

        //분으로 표현
        long minute = (curDateTime - LoginDateTime) / 60000;
        //Log.d("calTime 로그인 시간" , loginTime +" - " +LoginDateTime+"");
        //Log.d("calTime 현재   시간" , curTime +" - " + curDateTime+"");
        //Log.d("calTime 차이",minute+"분 차이");
        String logYn="";
        if(minute > intervalS) {  //로그인유지시간보다 크다면 로그아웃할때
            logYn = "N";    //로그인 안돼
        }
        else
        {
            logYn = "Y"; //로그인 유지시켜
        }
        return logYn;
    }

    public void showToast(String msg,   Integer time, Integer h,Integer y ) { //h horizon 수평, //v vertical 수직

        Toast toast2 = Toast.makeText(activity, msg, time);   //0 short , 1 long
        toast2.setGravity(h|y,0, 0);
        toast2.show();
    }






    public void findPositionAddr(String addr1,String addr2) {

        try
        {






            String host_url = "https://dapi.kakao.com/v2/local/search/address.json?query="+"서울 송파구 백제고분로41길 7-28";
            HttpURLConnection conn = null;
            //Log.d("mapLog" , "host_url :  "+ host_url );
            URL url = new URL(host_url);
            conn = (HttpURLConnection)url.openConnection();

            conn.setRequestMethod("GET");//POST GET
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);



            //서버에서 보낸 응답 데이터 수신 받기
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String returnMsg = in.readLine();


            //Log.d("mapLog" , "returnMsg :  " + returnMsg );



            Gson gson = new Gson();

           // vo = gson.fromJson(returnMsg, new TypeToken<ArrayList<SendAlrmTalkVO>>(){}.getType());



            //HTTP 응답 코드 수신
            int responseCode = conn.getResponseCode();
            if(responseCode == 400) {
                System.out.println("400 : 명령을 실행 오류");
            } else if (responseCode == 500) {
                System.out.println("500 : 서버 에러.");
            } else { //정상 . 200 응답코드 . 기타 응답코드
                System.out.println(responseCode + " : 응답코드임");
            }

        }catch(IOException ie) {
            System.out.println("IOException " + ie.getCause());
            ie.printStackTrace();
        }catch(Exception ee) {
            System.out.println("Exception " + ee.getCause());
            ee.printStackTrace();
        }


        ////Log.d(vo.toString());
        //return vo;
    }
}
