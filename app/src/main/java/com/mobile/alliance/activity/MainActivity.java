package com.mobile.alliance.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;

import android.content.Context;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.mobile.alliance.BuildConfig;
import com.mobile.alliance.R;
import com.mobile.alliance.api.CommonHandler;
import com.mobile.alliance.api.TextValueHandler;
import com.mobile.alliance.entity.VersionVO;
import com.mobile.alliance.service.DownloadNotificationService;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    public static Activity _MainActivity; //다른액티비티에서 이 액티비티를 제어하기위해
    TextValueHandler textValueHandler = new TextValueHandler();





    VersionVO versionVO = new VersionVO();

    static final int PERMISSIONS_REQUEST = 0x0000001;
    private String PhoneNum;
    private String  SimOper;


    //내부에 데이터 저장하는것
    static private String SHARE_NAME = "SHARE_PREF";
    static SharedPreferences sharePref = null;
    static SharedPreferences.Editor editor = null;

    //공통
    CommonHandler commonHandler = new CommonHandler(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String mode = "alliance";

        textValueHandler.type(mode);

        try{
            setDirEmpty(Environment.getExternalStorageDirectory() + "/.alliance/");
        }
        catch(Exception e){};



        getHashKey();   //카카오 맵에 설정하려고 해시키 가져오기
        //내부에 데이터 저장하는것
        sharePref = getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        editor = sharePref.edit();
    //체크해야할것
        //1.    버젼
        //2.    권한

        //폰넘버 읽기 권한이 있다면
        if (
                /*(
                    (       Build.VERSION.SDK_INT >= Build.VERSION_CODES.R    //11 이상인경우
                      &&    (       ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)              == PackageManager.PERMISSION_GRANTED
                                &&  ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS)    == PackageManager.PERMISSION_GRANTED
                            )
                    )
                    ||
                    (       Build.VERSION.SDK_INT < Build.VERSION_CODES.R     // 11 미만인 경우
                      &&    (       ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)      == PackageManager.PERMISSION_GRANTED
                                &&  ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)               == PackageManager.PERMISSION_GRANTED
                          )
                    )
                )*/
             ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)      == PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)               == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED  //위치 찾기
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED    //카메라
                &&ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED    //파일 쓰기 28까지
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED    //파일 읽기 28까지

                      /*  (
                                ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q    //10 이상인경우
                                        &&   ActivityCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED    //파일 관리
                                )
                                ||
                                ( Build.VERSION.SDK_INT < Build.VERSION_CODES.Q     // 10 미만인 경우
                                        &&  ( ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED    //파일 쓰기 28까지
                                                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED    //파일 읽기 28까지
                                        )
                                )
                        )*/



        )
        {
            //Log.d("grantResults 권한체크 if","grantResults 권한체크 if");
            TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            PhoneNum = telManager.getLine1Number(); //전화번호 추출
            SimOper = telManager.getSimOperator();//통신사확인


  /*******************************************


Toast.makeText(this,"유심상태 : " + telManager.getSimState() + "," + telManager.PHONE_TYPE_NONE ,Toast.LENGTH_SHORT);


            //Log.d("main", "음성통화 상태 : [ getCallState ] >>> " + telManager.getCallState());
            //Log.d("main", "데이터통신 상태 : [ getDataState ] >>> " + telManager.getDataState());
            //Log.d("main", "전화번호 : [ getLine1Number ] >>> " + telManager.getLine1Number());
            //Log.d("main", "통신사 ISO 국가코드 : [ getNetworkCountryIso ] >>> "+telManager.getNetworkCountryIso());
            //Log.d("main", "통신사 ISO 국가코드 : [ getNetworkCountryIso ] >>> "+telManager.getNetworkCountryIso());
            //Log.d("main", "통신사 ISO 국가코드 : [ getSimCountryIso ] >>> "+telManager.getSimCountryIso());
            //Log.d("main", "망사업자 MCC+MNC : [ getNetworkOperator ] >>> "+telManager.getNetworkOperator());
            //Log.d("main", "망사업자 MCC+MNC : [ getSimOperator ] >>> "+telManager.getSimOperator());
            //Log.d("main", "망사업자명 : [ getNetworkOperatorName ] >>> "+telManager.getNetworkOperatorName());
            //Log.d("main", "망사업자명 : [ getSimOperatorName ] >>> "+telManager.getSimOperatorName());
            //Log.d("main", "SIM 카드 상태 : [ getSimState ] >>> "+telManager.getSimState());
            //Log.d("main", "USSD_ERROR_SERVICE_UNAVAIL : [ USSD_ERROR_SERVICE_UNAVAIL ] >>> "+telManager.USSD_ERROR_SERVICE_UNAVAIL);



            if(     telManager.getSimState() ==  TelephonyManager.SIM_STATE_ABSENT  //유심없음 1
                ||  telManager.getSimState() == TelephonyManager.SIM_STATE_UNKNOWN  //유심상태를 알수없음 0
                ||  telManager.getSimState() == TelephonyManager.SIM_STATE_PERM_DISABLED    //유심이 있지만 사용중지  7
                ||  telManager.getSimState() == TelephonyManager.SIM_STATE_CARD_IO_ERROR    //유심이 있지만 오류상태 8
                ||  telManager.getSimState() == TelephonyManager.SIM_STATE_CARD_RESTRICTED  //유심이 있지만 통신사가 제한 9
                ||  telManager.getSimState() == TelephonyManager.SIM_STATE_NETWORK_LOCKED  //유심이 있지만 네트워크 잠김 4



            )
            {



                PhoneNum = "";

                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(dialogView);

                AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();

                Button ok_btn = dialogView.findViewById(R.id.successBtn);
                TextView ok_txt = dialogView.findViewById(R.id.successText);
                ok_txt.setText("유심이 없거나, 사용중지, 오류가 발생하여 확인할수 없습니다.");
                ok_btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        alertDialog.dismiss();
                        return;
                    }
                });



                return;
            }
            else {

                //PhoneNum = "+821026410505";
                if(PhoneNum == null){
                    //PhoneNum = "+821026410505";
                }

                if(PhoneNum != null && PhoneNum.startsWith("+82")){
                    PhoneNum = PhoneNum.replace("+82", "0");
                }

                Toast.makeText(getApplicationContext(),"PhoneNum : " + PhoneNum,Toast.LENGTH_SHORT).show();
            }

******************************/

            //PhoneNum = "+821026410505";
            if(PhoneNum == null){
                //PhoneNum = "+821026410505";
            }

            if(PhoneNum != null && PhoneNum.startsWith("+82")){
                PhoneNum = PhoneNum.replace("+82", "0");
            }



            editor.putBoolean("isShare", true); //저장할때 true // 수정할때 flase
            editor.putString("PhoneNum", PhoneNum == null ? "" : PhoneNum);
            editor.putString("VersionCode", String.valueOf(BuildConfig.VERSION_CODE));
            editor.putString("VersionName", BuildConfig.VERSION_NAME);
            editor.putString("cnntIp", commonHandler.getLocalIpAddress());
            editor.putString("mode",mode);
            editor.apply();     //저장종료


            Toast toast = Toast.makeText(this, "PhoneNum : " + sharePref.getString("PhoneNum","") + "\nversionCode : " + sharePref.getString("VersionCode","") + "\nversionName : " + sharePref.getString("VersionName","")+"\ndeviceIp : " + sharePref.getString("cnntIp",""), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER|Gravity.BOTTOM, 0, 0);
            //toast.show();



            //2초 뒤 액티비티 이동
            //앱 버젼 체크 하는곳으로 이동

        }
        //폰넘버 읽기 권한이 없다면
        else {
            //권한체크로 넘어감
            //Log.d("grantResults 권한체크 else","grantResults 권한체크 else");
            OnCheckPermission();
        }






//        AsyncTask
        VersionCheck versionCheck = new VersionCheck();
        versionCheck.execute();
    }


    //권한체크
    @SuppressLint("LongLogTag")
    public void OnCheckPermission() {
        //Log.d("grantResults OnCheckPermission","grantResults OnCheckPermission");
        //폰넘버 읽기 권한이 없다면
        if   (
              /*  (
                        (   Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                            &&  (
                                        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                                    ||  ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
                                )
                        )
                        ||

                        (   Build.VERSION.SDK_INT < Build.VERSION_CODES.R
                                &&
                            // 10 이하인 경우
                                (       ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                                    ||  ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                                )
                        )
                )*/

        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                ||  ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED

        ||  ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED
        ||  ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)                != PackageManager.PERMISSION_GRANTED    //카메라
        ||  ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED    //파일 쓰기 28까지
        ||  ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED    //파일 읽기 28까지
       // ||  ActivityCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED    //파일 관리
        )
        {
            View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setView(dialogView);

            AlertDialog alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();

            Button ok_btn = dialogView.findViewById(R.id.successBtn);
            TextView ok_txt = dialogView.findViewById(R.id.successText);
            //ok_txt.setGravity(Gravity.LEFT);
            ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START); //글자를 왼쪽 정렬
            ok_txt.setText("앱 사용시 다음 권한이 필요합니다\n"+
                            "\n전화번호     :   ID사용/암호변경" +
                            "\n문자권한     :   문자발송" +
                            "\n위치권한     :   지도사용"+
                            "\n카메라권한 :   사진 촬영"+
                            "\n파일권한     :   이미지 읽기/쓰기"

            );
            ok_btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    alertDialog.dismiss();
                    //폰넘버 읽기 권한 요청 다이얼 띄우기
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_SMS,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSIONS_REQUEST);  //,Manifest.permission.MANAGE_EXTERNAL_STORAGE


                }
            });



        }
        else    //폰권한이 있으면
        {
            //현재 이 액티비디 다시 열기기
            try {
                //TODO 액티비티 화면 재갱신 시키는 코드
                Intent intent = getIntent();
                finish(); //현재 액티비티 종료 실시
                overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                startActivity(intent); //현재 액티비티 재실행 실시
                overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @SuppressLint("LongLogTag")
    @Override
    //권한 결과 보기
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Log.d("grantResults requestCode",requestCode+"");
        switch (requestCode) {

            case PERMISSIONS_REQUEST :
                //Log.d("grantResults PERMISSIONS_REQUEST",grantResults.length+"");
                //얻은 권한이 있으며, 요청한권한과 얻은권한 갯수가 같으면 권한을 다 얻었다고 판단
                if (grantResults.length > 0)
                {

                    for(int i=0; i<grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            //Log.d("grantResults for id : " + i,grantResults[i]+"");
                            //Log.d("grantResults PackageManager.PERMISSION_GRANTED : " + i,PackageManager.PERMISSION_GRANTED+"");
                            //Toast.makeText(this, "앱 실행을 위한 권한이 취소 되었습니다", Toast.LENGTH_LONG).show();
                            Toast toast2 = Toast.makeText(this, "앱 실행을 위한 권한이 취소 되었습니다\n앱을 종료합니다.", Toast.LENGTH_SHORT);
                            toast2.setGravity(Gravity.CENTER|Gravity.BOTTOM,0, 0);
                            toast2.show();

                            //앱을 종료한다
                            finish();
                        }


                    }

                    //현재 이 액티비디 다시 열기기
                    try {
                        //TODO 액티비티 화면 재갱신 시키는 코드
                        Intent intent = getIntent();
                        finish(); //현재 액티비티 종료 실시
                        overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                        startActivity(intent); //현재 액티비티 재실행 실시
                        overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }
                else
                {

                }

            break;
        }
    }


    public void main_btn(View v){
        Intent activity_first = new Intent(this, FirstActivity.class);
        startActivity(activity_first);
    }




    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }

    String AppName = "Alliance";

    Boolean VersionCheckBoolean     = false;
    Boolean AppNameBoolean          = false;
    Boolean VersionCodeBoolean      = false;
    Boolean VersionNameBoolean      = false;


    String VersionCheckTag      ="";
    String XmlAppNameTag           ="";
    String XmlVersionCodeTag       ="";
    String XmlVersionNameTag       ="";
    String[] XmlVersionNameArr     ={};
    String[] AppVersionNameArr    =BuildConfig.VERSION_NAME.split("\\.");
    Boolean VersionUp      = false;


    public class VersionCheck extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings){

            String requestUrl = textValueHandler.API_URL_PATH+"alliance.xml";
            //Log.d("MainActivity_Log","url : " + requestUrl);
            try{

                URL url = new URL(requestUrl);
                InputStream is = url.openStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new InputStreamReader(is, "UTF-8"));

                String tag;
                int eventType = parser.getEventType();
                //Log.d("MainActivity_Log","eventType : " + eventType);
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                        case XmlPullParser.END_DOCUMENT:
                        case XmlPullParser.END_TAG:

                            break;
                        case XmlPullParser.START_TAG:
                            if(parser.getName().equals("VersionCheck"))
                            {
                                VersionCheckBoolean = true;
                            }
                            if(parser.getName().equals("AppName"))
                            {
                                AppNameBoolean = true;
                            }
                            if(parser.getName().equals("VersionCode"))
                            {
                                VersionCodeBoolean = true;
                            }
                            if(parser.getName().equals("VersionName"))
                            {
                                VersionNameBoolean = true;
                            }

                            break;
                        case XmlPullParser.TEXT:
                            if(VersionCheckBoolean) {VersionCheckTag    = parser.getText().trim();}
                            VersionCheckBoolean = false;

                            if(AppNameBoolean)      {XmlAppNameTag         = parser.getText().trim();}
                            AppNameBoolean = false;

                            if(VersionCodeBoolean)  {XmlVersionCodeTag     = parser.getText().trim();}
                            VersionCodeBoolean = false;

                            if(VersionNameBoolean)  {XmlVersionNameTag     = parser.getText().trim();}
                            VersionNameBoolean = false;
                            break;
                    }
                    eventType = parser.next();
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            //Log.d("MainActivity_Log","VersionCheckTag : " + VersionCheckTag);
            //Log.d("MainActivity_Log","XmlAppNameTag      : " + XmlAppNameTag);
            //Log.d("MainActivity_Log","XmlVersionCodeTag  : " + XmlVersionCodeTag);
            //Log.d("MainActivity_Log","XmlVersionNameTag  : " + XmlVersionNameTag);

            XmlVersionNameArr = XmlVersionNameTag.split("\\.");
            //Log.d("MainActivity_Log","Arrays.toString(XmlVersionNameArr)  : " + Arrays.toString(XmlVersionNameArr));



            //  "VersionCode", String.valueOf(BuildConfig.VERSION_CODE)
            //  "VersionName", BuildConfig.VERSION_NAME


            if(VersionCheckTag.equals("VersionCheck"))
            {
                if(XmlAppNameTag.equals("Alliance"))
                {
                    //VersionCode : xml 에서 추출한 코드와 앱 내부의 코드와 비교
                    //xml에서 추출한게 더 크면 신규 앱이 있다는것이다
                    if(Integer.parseInt(XmlVersionCodeTag.trim()) > BuildConfig.VERSION_CODE)
                    {
                        VersionUp = true;

                    }
                    ////VersionName : xml 에서 추출한 네임과 앱 내부의 네임과 비교
                    else
                    {
                        //첫번째 숫자 비교
                        if(Integer.parseInt(XmlVersionNameArr[0]) > Integer.parseInt(AppVersionNameArr[0]))
                        {
                            VersionUp = true;
                        }
                        else
                        {
                            //두번째 숫자 비교
                            if(Integer.parseInt(XmlVersionNameArr[1]) > Integer.parseInt(AppVersionNameArr[1]))
                            {
                                VersionUp = true;
                            }
                            else
                            {
                                //세번째 숫자 비교
                                if(Integer.parseInt(XmlVersionNameArr[2]) > Integer.parseInt(AppVersionNameArr[2]))
                                {
                                    VersionUp = true;
                                }
                            }
                        }
                    }
                }
            }
            //Log.d("MainActivity_Log","VersionUp  : " + VersionUp);

            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //권한이 있다면
            if (        ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)          == PackageManager.PERMISSION_GRANTED
                    &&  ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS)                  == PackageManager.PERMISSION_GRANTED
                    &&  ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)      == PackageManager.PERMISSION_GRANTED  //위치 찾기
                    &&  ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)                    == PackageManager.PERMISSION_GRANTED    //카메라
                    &&  ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)    == PackageManager.PERMISSION_GRANTED    //파일 쓰기 28까지
                    &&  ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE )    == PackageManager.PERMISSION_GRANTED    //파일 읽기 28까지
            ){

                if(VersionUp){

                    View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_success, null);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setView(dialogView);

                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                    Button ok_btn = dialogView.findViewById(R.id.successBtn);
                    TextView ok_txt = dialogView.findViewById(R.id.successText);
                    ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START); //글자를 왼쪽 정렬
                    ok_txt.setText( "앱 업데이트가 필요합니다.\n" +
                                    "현재 : " + BuildConfig.VERSION_CODE + " " +
                                    Arrays.toString(AppVersionNameArr).replace(",", ".") + "\n" +
                                    "신규 : " + XmlVersionCodeTag.trim() + " " +
                                    Arrays.toString(XmlVersionNameArr).replace(",", ".")
                    );
                    ok_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            //registerReceiver();
                            // InstallAPK();
                            registerReceiver();
                            //startFileDownload();
                        }
                    });
                }
                else
                {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            Intent activity_first = new Intent(MainActivity.this, FirstActivity.class);
                            //activity_first.putExtra("PhoneNum",versionVO.getPhoneNum());
                            startActivity(activity_first);  //다음 액티비티를 열고
                            MainActivity.this.finish();     //이 액티비티를 닫음
                        }
                    },500);
                }
            }
            else
            {
                OnCheckPermission();
            }
        }
    }

    public void InstallAPK(){
        File apkFile = new File(textValueHandler.API_URL);

        if (apkFile != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        }
    }

    private BroadcastReceiver onDownloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if(intent.getAction().equals(DownloadNotificationService.PROGRESS_UPDATE)){
                boolean complete=intent.getBooleanExtra("downloadComplete",false);
                if(complete){
                    //Toast.makeText(MainActivity.this,"completed",Toast.LENGTH_SHORT).show();

                    File storageDir = new File(Environment.getExternalStorageDirectory() + "/AllianceDownload/");
                    if (!storageDir.exists()) storageDir.mkdirs();

                    File file = new File(storageDir.getPath() + File.separator +
                            "alliance.apk");
                    //File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + "alliance.apk");
                    Uri apkUri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                       /* apkUri = FileProvider
                                .getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);*/
                        apkUri = FileProvider.getUriForFile(MainActivity.this,
                                "com.mobile.alliance.provider", file);

                    }else{
                        apkUri=Uri.fromFile(file);
                    }
                    Intent openFileIntent = new Intent(Intent.ACTION_VIEW);
                    openFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    openFileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    openFileIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    startActivity(openFileIntent);
                    finish();
                    /*moveTaskToBack(true);
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());*/

                }

            }
        }
    };

    private void registerReceiver(){
        LocalBroadcastManager manager=LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(DownloadNotificationService.PROGRESS_UPDATE);
        manager.registerReceiver(onDownloadReceiver,intentFilter);
        startFileDownload();

    }


    private void startFileDownload(){
        Intent intent=new Intent(this, DownloadNotificationService.class);
        startService(intent);
    }

    // 해당 디렉토리 통째로 비우기
    public void setDirEmpty(String dirName)
    {
        //String path = Environment.getExternalStorageDirectory().toString() + dirName;
        String path = dirName;
        File dir = new File(path);
        File[] childFileList = dir.listFiles();
        if (dir.exists())
        {
            for (File childFile : childFileList)
            {
                if (childFile.isDirectory())
                {
                    setDirEmpty(childFile.getAbsolutePath()); //하위 디렉토리
                }
                else
                    {
                        childFile.delete(); //하위 파일
                    }
            }
            dir.delete();
        }
    }


}