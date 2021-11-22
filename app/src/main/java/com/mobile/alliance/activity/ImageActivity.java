package com.mobile.alliance.activity;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.mobile.alliance.R;
import com.mobile.alliance.api.CommonHandler;
import com.mobile.alliance.api.DrawUrlImageSubTaskHandler;
import com.mobile.alliance.api.DrawUrlImageTaskHandler;
import com.mobile.alliance.api.LogoutHandler;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import lombok.SneakyThrows;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageActivity extends AppCompatActivity {



    CommonHandler commonHandler = new CommonHandler(this);
    Context context;
    Button delBtn;
    Button downBtn;
    SubsamplingScaleImageView centerImage;

    //로그아웃
    private LogoutHandler logoutHandler;

    private File file, dir;
    private String savePath = "AllianceImage";
    private String FileName = null;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        context = this.getBaseContext();
        String imageUri = getIntent().getStringExtra("imageUri");
        Integer imageNo = Integer.parseInt(getIntent().getStringExtra("imageNo"));


        logoutHandler = new LogoutHandler(this);
String imageType = getIntent().getStringExtra("imageType");
//Log.d("ImageActivity",imageType);
String imageGubn = getIntent().getStringExtra("imageGubn");

//다운로드받을 디렉토리 생성하기
       MakePhtoDir();



        centerImage = (SubsamplingScaleImageView) findViewById(R.id.centerImage);
        //centerImage.setImage(ImageSource.resource(R.drawable.img_companylogo_alliance));
        //centerImage.setImage(ImageSource.uri("file:///storage/emulated/0/DCIM/Camera/20211013_221840.jpg"));








        delBtn = (Button) findViewById(R.id.delBtn);
        downBtn = (Button) findViewById(R.id.downBtn);
        if(imageType.equals("delete"))
        {
            downBtn.setVisibility(View.GONE);    //이미지 보기 종료가 삭제라면 다운로드버튼을 가린다
            centerImage.setImage(ImageSource.uri(imageUri));
        }
        else if(imageType.equals("download"))
        {
            delBtn.setVisibility(View.GONE);    //이미지 보기 종료가 다운이라면 삭제버튼을 가린다
            try{
                new DrawUrlImageSubTaskHandler(
                        (SubsamplingScaleImageView) findViewById(R.id.centerImage))
                        .execute(imageUri);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            if(!centerImage.isEnabled())
            {
                downBtn.setVisibility(View.GONE);
                commonHandler.showFinishAlertDialog("이미지 로딩 실패","이미지 경로가 잘못되었거나,서버에 이미지가 없습니다.","Y");
            }


        }




        delBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v){
                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(ImageActivity.this);
                builder.setView(dialogView);

                AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();

                Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
                TextView ok_txt = dialogView.findViewById(R.id.confirmText);
                ok_txt.setTextSize(20);
                ok_txt.setTypeface(null, Typeface.BOLD);
                ok_txt.setText("이미지를 삭제하시겠습니까?");
                ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                ok_btn.setOnClickListener(new View.OnClickListener() {
                    @SneakyThrows @Override
                    public void onClick(View v) {


                        alertDialog.dismiss();

                        if(imageGubn.equals("no")){     //미마감 등록화면에서 현재(이미지보기)를 오픈했다면
                            NoCmplActivity.LOCK[imageNo] = 0;
                            NoCmplActivity.picAddImg[imageNo]
                                    .setImageDrawable(getDrawable(R.mipmap.ic_camera));
                            NoCmplActivity.picAddUri[imageNo].setText("");
                            NoCmplActivity.picFileName[imageNo].setText("");
                            finish();
                        }
                        if(imageGubn.equals("yes")){     //배송완료 등록화면에서 현재(이미지보기)를 오픈했다면
                            YesCmplActivity.YES_LOCK[imageNo] = 0;
                            YesCmplActivity.yesPicAddImg[imageNo]
                                    .setImageDrawable(getDrawable(R.mipmap.ic_camera));
                            YesCmplActivity.yesPicAddUri[imageNo].setText("");
                            YesCmplActivity.yesPicFileName[imageNo].setText("");
                            finish();
                        }



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





        downBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v){
                View dialogView = getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(ImageActivity.this);
                builder.setView(dialogView);

                AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();

                Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
                TextView ok_txt = dialogView.findViewById(R.id.confirmText);
                ok_txt.setTextSize(20);
                ok_txt.setTypeface(null, Typeface.BOLD);
                ok_txt.setText("다운로드 하시겠습니까?");
                ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                ok_btn.setOnClickListener(new View.OnClickListener() {
                    @SneakyThrows @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        requestFileDownload(imageUri);
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


    /****
     - 이미지 로드 방법 (이미지 리소스, assets폴더, URI)

     SubsamplingScaleImageView imageView =

     (SubsamplingScaleImageView)findViewById(id.imageView);
     imageView.setImage(ImageSource.resource(R.drawable.monkey));
     // ... or ...
     imageView.setImage(ImageSource.asset("map.png"))
     // ... or ...
     imageView.setImage(ImageSource.uri("/sdcard/DCIM/DSCM00123.JPG"));

     - 실행은 가능하나 에러를 가져올 수 있는 방법

     imageView.setImage(ImageSource.bitmap(bitmap));

     -> 기존의 방법인 Bitmap클래스를 이용하여 고화질 이미지를 로드하면OutOfMemory 메시지가 나올 수 있다.
     * 라이선스 또한 Apache License, Version 2.0이다. Apache License, Version 2.0 형식에 맞게 출시한다면 상업적이용도 자유롭다.

     이와같이 코드 단 몇 줄로 ImageView에 고화질 이미지 확대,축소,이동 핀치 줌 (Pinch Zoom) 기능을 한 번에 사용 가능한 SubsamplingScaleImageView를 추천한다.
     ***/

    //이미지 다운로드 받을 디렉토리 만들기기
    private void MakePhtoDir(){
        //savePath = "/Android/data/" + getPackageName();
        //dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), savePath);
        dir = new File(Environment.getExternalStorageDirectory(), savePath);
        if(!dir.exists())
            dir.mkdirs(); // make dir
    }

    private String getDefaultDownloadDirectory() {
        return this.getExternalFilesDir(null) + "/download/";
    }

    /**
     * Requests a server to download a file.
     * @param fileUrl File adderess of a server. It is supposed that HTTP protocol is used.
     */
    @SuppressLint("LongLogTag")
    private void requestFileDownload(String fileUrl) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(fileUrl)
                .build();
        CallbackToDownloadFile cbToDownloadFile = new CallbackToDownloadFile(
                dir,
                getFileNameFrom(fileUrl)
        );

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String extension = mimeTypeMap.getFileExtensionFromUrl(fileUrl);
        String mimeType = mimeTypeMap.getMimeTypeFromExtension(extension);
        client.newCall(request).enqueue(cbToDownloadFile);
    }
    private String getFileNameFrom(String url) {
        Date date_now = new Date(System.currentTimeMillis()); // 현재시간을 가져와 Date형으로 저장한다
        // 년월일시분초 14자리 포멧
        SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return date_format.format(date_now)+".JPG"; // 14자리 포멧으로 출력한다
    }

    /**
     * Callback class to handle downloading a file after the download request.
     * If there is already the same file, it is overwritten.
     */
    private class CallbackToDownloadFile implements Callback {

        private File directory;
        private File fileToBeDownloaded;

        @SuppressLint("LongLogTag") public CallbackToDownloadFile(File directory, String fileName) {
            this.directory = directory;
            this.fileToBeDownloaded = new File(this.directory.getAbsolutePath() + "/" + fileName);
        }

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            openToastOnUiThread(
                    //"Cannot download the file. Check if your device is connected to the internet."
                    "파일다운로드 실패. 네트워크 연결상태를 확인하세요."
            );
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            if (!this.directory.exists()) {
                this.directory.mkdirs();
            }

            if (this.fileToBeDownloaded.exists()) {
                this.fileToBeDownloaded.delete();
            }

            try {
                this.fileToBeDownloaded.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                //openToastOnUiThread("Cannot create the download file. Check the write permission.");
                openToastOnUiThread("파일다운로드 실패. 디바이스의 권한을 확인하세요.");
                return;
            }

            InputStream is = response.body().byteStream();
            OutputStream os = new FileOutputStream(this.fileToBeDownloaded);

            final int BUFFER_SIZE = 2048;
            byte[] data = new byte[BUFFER_SIZE];

            int count;
            long total = 0;

            while ((count = is.read(data)) != -1) {
                total += count;
                os.write(data, 0, count);
            }

            os.flush();
            os.close();
            is.close();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(this.fileToBeDownloaded)));
            openToastOnUiThread("다운로드 성공.갤러리를 확인하세요.");
        }

        private void openToastOnUiThread(final String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            ImageActivity.this,
                            message,
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        }
    }
}