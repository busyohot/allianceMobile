package com.mobile.alliance.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.mobile.alliance.R;
import com.mobile.alliance.api.CommonHandler;
import com.mobile.alliance.api.DrawUrlImageSubTaskHandler;
import com.mobile.alliance.api.LogoutHandler;
import com.mobile.alliance.api.TextValueHandler;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import lombok.SneakyThrows;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageActivity extends AppCompatActivity {
    private static TextValueHandler textValueHandler = new TextValueHandler();

    String delFile="";

    CommonHandler commonHandler = new CommonHandler(this);
    Context context;
    Button delBtn;
    Button downBtn;
    SubsamplingScaleImageView centerImage;

    //๋ก๊ทธ์์
    private LogoutHandler logoutHandler;

    private File file, dir;
    private String savePath = "AllianceImage";
    private String FileName = null;

    Handler handler = new Handler();

    @SuppressLint("ResourceType") @Override
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

        //๋ค์ด๋ก๋๋ฐ์ ๋๋?ํ?๋ฆฌ ์์ฑํ๊ธฐ
        MakePhtoDir();
        centerImage = (SubsamplingScaleImageView) findViewById(R.id.centerImage);

        delBtn = (Button) findViewById(R.id.delBtn);
        downBtn = (Button) findViewById(R.id.downBtn);
        if(imageType.equals("delete")){
            downBtn.setVisibility(View.GONE);    //์ด๋ฏธ์ง ๋ณด๊ธฐ ์ข๋ฃ๊ฐ ์ญ์?๋ผ๋ฉด ๋ค์ด๋ก๋๋ฒํผ์ ๊ฐ๋ฆฐ๋ค
            centerImage.setImage(ImageSource.uri(imageUri));
        }
        else if(imageType.equals("download")){
            delBtn.setVisibility(View.GONE);    //์ด๋ฏธ์ง ๋ณด๊ธฐ ์ข๋ฃ๊ฐ ๋ค์ด์ด๋ผ๋ฉด ์ญ์?๋ฒํผ์ ๊ฐ๋ฆฐ๋ค
            try{
                new DrawUrlImageSubTaskHandler(
                        (SubsamplingScaleImageView) findViewById(R.id.centerImage))
                        .execute(imageUri);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            if(!centerImage.isEnabled()){
                downBtn.setVisibility(View.GONE);
                commonHandler.showFinishAlertDialog("์ด๋ฏธ์ง ๋ก๋ฉ ์คํจ","์ด๋ฏธ์ง ๊ฒฝ๋ก๊ฐ ์๋ชป๋์๊ฑฐ๋,์๋ฒ์ ์ด๋ฏธ์ง๊ฐ ์์ต๋๋ค.","Y");
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
                ok_txt.setText("์ด๋ฏธ์ง๋ฅผ ์ญ์?ํ์๊ฒ?์ต๋๊น?");
                ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                ok_btn.setOnClickListener(new View.OnClickListener() {
                    @SneakyThrows @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        if(imageGubn.equals("no")){     //๋ฏธ๋ง๊ฐ ๋ฑ๋กํ๋ฉด์์ ํ์ฌ(์ด๋ฏธ์ง๋ณด๊ธฐ)๋ฅผ ์คํํ๋ค๋ฉด
                            //20220123 ์?์ฐํธ ์ถ๊ฐ. ์ญ์? ํ๋ฉด ํ์ผ์๋ฒ์์๋ ์ญ์?ํ๊ธฐ
                            delFile = NoCmplActivity.picFileName[imageNo].getText().toString();
                            DeleteFileNThread deleteFileNThread = new DeleteFileNThread();
                            deleteFileNThread.start();

                            NoCmplActivity.LOCK[imageNo] = 0;
                            NoCmplActivity.picAddImg[imageNo]
                                    .setImageDrawable(getDrawable(R.mipmap.ic_camera));
                            NoCmplActivity.picAddUri[imageNo].setText("");
                            NoCmplActivity.picFileName[imageNo].setText("");

                            finish();
                        }
                        if(imageGubn.equals("yes")){     //๋ฐฐ์ก์๋ฃ ๋ฑ๋กํ๋ฉด์์ ํ์ฌ(์ด๋ฏธ์ง๋ณด๊ธฐ)๋ฅผ ์คํํ๋ค๋ฉด
                            //20220123 ์?์ฐํธ ์ถ๊ฐ. ์ญ์? ํ๋ฉด ํ์ผ์๋ฒ์์๋ ์ญ์?ํ๊ธฐ
                            delFile = YesCmplActivity.yesPicFileName[imageNo].getText().toString();
                            DeleteFileNThread deleteFileNThread = new DeleteFileNThread();
                            deleteFileNThread.start();

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
                ok_txt.setText("๋ค์ด๋ก๋ ํ์๊ฒ?์ต๋๊น?");
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
     - ์ด๋ฏธ์ง ๋ก๋ ๋ฐฉ๋ฒ (์ด๋ฏธ์ง ๋ฆฌ์์ค, assetsํด๋, URI)

     SubsamplingScaleImageView imageView =

     (SubsamplingScaleImageView)findViewById(id.imageView);
     imageView.setImage(ImageSource.resource(R.drawable.monkey));
     // ... or ...
     imageView.setImage(ImageSource.asset("map.png"))
     // ... or ...
     imageView.setImage(ImageSource.uri("/sdcard/DCIM/DSCM00123.JPG"));

     - ์คํ์ ๊ฐ๋ฅํ๋ ์๋ฌ๋ฅผ ๊ฐ์?ธ์ฌ ์ ์๋ ๋ฐฉ๋ฒ

     imageView.setImage(ImageSource.bitmap(bitmap));

     -> ๊ธฐ์กด์ ๋ฐฉ๋ฒ์ธ Bitmapํด๋์ค๋ฅผ ์ด์ฉํ์ฌ ๊ณ?ํ์ง ์ด๋ฏธ์ง๋ฅผ ๋ก๋ํ๋ฉดOutOfMemory ๋ฉ์์ง๊ฐ ๋์ฌ ์ ์๋ค.
     * ๋ผ์ด์?์ค ๋ํ Apache License, Version 2.0์ด๋ค. Apache License, Version 2.0 ํ์์ ๋ง๊ฒ ์ถ์ํ๋ค๋ฉด ์์์?์ด์ฉ๋ ์์?๋กญ๋ค.

     ์ด์๊ฐ์ด ์ฝ๋ ๋จ ๋ช ์ค๋ก ImageView์ ๊ณ?ํ์ง ์ด๋ฏธ์ง ํ๋,์ถ์,์ด๋ ํ์น ์ค (Pinch Zoom) ๊ธฐ๋ฅ์ ํ ๋ฒ์ ์ฌ์ฉ ๊ฐ๋ฅํ SubsamplingScaleImageView๋ฅผ ์ถ์ฒํ๋ค.
     ***/

    //์ด๋ฏธ์ง ๋ค์ด๋ก๋ ๋ฐ์ ๋๋?ํ?๋ฆฌ ๋ง๋ค๊ธฐ๊ธฐ
    private void MakePhtoDir(){
        //savePath = "/Android/data/" + getPackageName();
        //dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), savePath);
        dir = new File(Environment.getExternalStorageDirectory(), savePath);
        if(!dir.exists())
            dir.mkdirs(); // make dir
    }

    /*
    private String getDefaultDownloadDirectory() {
        return this.getExternalFilesDir(null) + "/download/";
    }
    */

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
        Date date_now = new Date(System.currentTimeMillis()); // ํ์ฌ์๊ฐ์ ๊ฐ์?ธ์ Dateํ์ผ๋ก ์?์ฅํ๋ค
        // ๋์์ผ์๋ถ์ด 14์๋ฆฌ ํฌ๋ฉง
        SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return date_format.format(date_now)+".JPG"; // 14์๋ฆฌ ํฌ๋ฉง์ผ๋ก ์ถ๋?ฅํ๋ค
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
                    "ํ์ผ๋ค์ด๋ก๋ ์คํจ. ๋คํธ์ํฌ ์ฐ๊ฒฐ์ํ๋ฅผ ํ์ธํ์ธ์."
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
                openToastOnUiThread("ํ์ผ๋ค์ด๋ก๋ ์คํจ. ๋๋ฐ์ด์ค์ ๊ถํ์ ํ์ธํ์ธ์.");
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
            openToastOnUiThread("๋ค์ด๋ก๋ ์ฑ๊ณต.๊ฐค๋ฌ๋ฆฌ๋ฅผ ํ์ธํ์ธ์.");
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
    //์๋๋ก์ด๋ ์ต๊ทผ ๋ฒ์?์์๋ ๋คํฌ์ํฌ ํต์?์์ ๋ฐ๋์ ์ค๋?๋๋ฅผ ์๊ตฌํ๋ค.
    class DeleteFileNThread extends Thread{
        public DeleteFileNThread() {
        }
        @SneakyThrows
        @Override

        public void run() {
            delete(delFile);
        }

        public void delete(String deleteFile) throws FTPException, IOException, FTPIllegalReplyException{

            FTPClient client = new FTPClient();
            try{
                client.connect(textValueHandler.FTP_HOST, textValueHandler.FTP_PORT);//ftp ์๋ฒ์ ์ฐ๊ฒฐ, ํธ์คํธ์ ํฌํธ๋ฅผ ๊ธฐ์
                client.login(textValueHandler.FTP_USER, textValueHandler.FTP_PASS);//๋ก๊ทธ์ธ์ ์ํด ์์ด๋์ ํจ์ค์๋ ๊ธฐ์
                client.setType(FTPClient.TYPE_BINARY);//2์ง์ผ๋ก ๋ณ๊ฒฝ
                client.setPassive(true);
                String[] arr = deleteFile.split("\\/");
                client.changeDirectory(textValueHandler.FTP_DIR);

                for (int i = 0; i < arr.length - 1; i++) {
                    client.changeDirectory(arr[i]+"/");
                }
                //Log.d(TAG,"FTP FILE DELETE : "+arr[(arr.length - 1)]);
                client.deleteFile(arr[(arr.length - 1)]);
            }
            catch(Exception e)
            {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //commonHandler.showToast("์๋ฒ ์ด๋ฏธ์ง ํ์ผ ์ญ์? ์คํจ",0,17,17);
                        //Log.d(TAG,"FTP FILE DELETE FAIL, ์๋ฒ ์ด๋ฏธ์ง ํ์ผ ์ญ์? ์คํจ");
                    }
                });
                e.printStackTrace();
                try {
                    if(client != null){
                        client.disconnect(true);
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
}