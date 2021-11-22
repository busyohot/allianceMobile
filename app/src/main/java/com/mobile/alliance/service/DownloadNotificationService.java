package com.mobile.alliance.service;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.mobile.alliance.activity.FirstActivity;
import com.mobile.alliance.activity.MainActivity;
import com.mobile.alliance.api.CommonHandler;
import com.mobile.alliance.api.TextValueHandler;
import com.mobile.alliance.fragment.DeliveryListFragment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class DownloadNotificationService extends IntentService {

    TextValueHandler textValueHandler = new TextValueHandler();


    public static final String PROGRESS_UPDATE = "progress_update";
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    public DownloadNotificationService(){
        super("downloadService");
    }


    //onHandleIntent는 워커스레드
    @Override
    protected void onHandleIntent(@Nullable Intent intent){
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // oreo이상은 notificationChannel을 생성해주어야함.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //MainActivity 설정
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent
                    MainPendingIntent = PendingIntent.getActivity(this , 0, mainIntent, PendingIntent.FLAG_ONE_SHOT);

       /* //YesActivity 설정
        Intent yesIntent = new Intent(this, YesActivity.class);
        yesIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent yesPendingIntent = PendingIntent.getActivity(this, 0, yesIntent, PendingIntent.FLAG_ONE_SHOT);

        //NoActivity 설정
        Intent noIntent = new Intent(this, NoActivity.class);
        noIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent noPendingIntent = PendingIntent.getActivity(this, 0, noIntent, PendingIntent.FLAG_ONE_SHOT);*/
            NotificationChannel notificationChannel = new NotificationChannel("download", "파일 다운로드",
            NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Alliance"); //setting 에서 앱의 알림의 대한 설명
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(false);

            //알림매니저 생성
           // NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            notificationManager.createNotificationChannel(notificationChannel);
        }




        notificationBuilder = new NotificationCompat.Builder(this, "download")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("앱 다운로드")
                .setContentText("다운로드중")
                //.setPriority(NotificationManagerCompat.IMPORTANCE_DEFAULT)
                .setDefaults(0)

                .setAutoCancel(true);


        notificationManager.notify(0, notificationBuilder.build());

        Call<ResponseBody> request = ApiService.getInstance().api.downloadFile(textValueHandler.API_URL);

        try{
            downloadFile(request.execute().body());
        }catch(IOException e){
            //e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadFile(ResponseBody body) throws IOException{
        if(body == null)
        {
            onDownloadComplete(false);
            return;
        }

        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream inputStream = new BufferedInputStream(body.byteStream(), 1024 * 8);

        File storageDir = new File(Environment.getExternalStorageDirectory() + "/AllianceDownload/");
        if (!storageDir.exists()) storageDir.mkdirs();

        File outputFile = new File(storageDir.getPath() + File.separator +
                "alliance.apk");
        //File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + "alliance.apk");



       /* File outputFile = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "alliance.apk");*/

        // 파일이 존재한다면 지우고 다시 받기 위하여
        if(outputFile.exists()){
            outputFile.delete();
        }

        OutputStream outputStream = new FileOutputStream(outputFile);

        long total = 0;
        boolean downloadComplete = false;

        while ((count = inputStream.read(data)) != -1) {

            total += count;
            int progress = (int) ((double) (total * 100) / (double) fileSize);


            //updateNotification(progress);
            outputStream.write(data, 0, count);

            if(progress == 100){
                //updateNotification(progress);
                downloadComplete = true;
            }
        }

        onDownloadComplete(downloadComplete);
        outputStream.flush();
        outputStream.close();
        inputStream.close();

    }


    private void updateNotification(int currentProgress){

        notificationBuilder.setProgress(100, currentProgress, false);
        notificationBuilder.setContentText(currentProgress + "%");
         notificationManager.notify(0, notificationBuilder.build());
    }


    private void sendProgressUpdate(boolean downloadComplete){

        Intent intent = new Intent(PROGRESS_UPDATE);
        intent.putExtra("downloadComplete", downloadComplete);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void onDownloadComplete(boolean downloadComplete){
        sendProgressUpdate(downloadComplete);
        String message;
        if(downloadComplete){
            message = "다운로드가 완료되었습니다.";
        }else {
            message = "다운로드에 실패하였습니다.";
            //Log.d("downloadComplete", false + "");

        }

        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText(message);
        notificationManager.notify(0, notificationBuilder.build());
        notificationManager.cancel(0);


    }

        @Override
        public void onTaskRemoved(Intent rootIntent) {
            notificationManager.cancel(0);
        }



    }