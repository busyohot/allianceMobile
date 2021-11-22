package com.mobile.alliance.api;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.alliance.R;
import com.mobile.alliance.activity.DeliveryListDetail;
import com.mobile.alliance.activity.FirstActivity;
import com.mobile.alliance.activity.Mobile002Activity;

import lombok.SneakyThrows;

public class LogoutHandler  {
    //자동로그인 저장
    static private String LOGIN_SHARE = "LOGIN_PREF";
    static SharedPreferences loginPref = null;
    static SharedPreferences.Editor loginEditor = null;


    private Activity activity;

    public LogoutHandler(Activity context)
    {
        this.activity = context;

    }

    public void onLogout()
    {

        //자동로그인 데이터 저장하는것
        loginPref = activity.getSharedPreferences(LOGIN_SHARE, activity.MODE_PRIVATE);
        loginEditor = loginPref.edit();

        View dialogView = activity.getLayoutInflater().inflate(R.layout.custom_dial_confirm, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        Button ok_btn = dialogView.findViewById(R.id.confirmBtnYes);
        TextView ok_txt = dialogView.findViewById(R.id.confirmText);
        ok_txt.setTextSize(20);
        ok_txt.setTypeface(null, Typeface.BOLD);
        ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START); //글자를 왼쪽 정렬
        ok_txt.setText(loginPref.getBoolean("loginAuto",false) ? "로그아웃 하시겠습니까?\n자동로그인이 해제 됩니다." : "로그아웃 하시겠습니까?");
        //ok_txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                try {
                    loginEditor.remove("loginAuto");
                    loginEditor.commit();
                }
                catch (Exception e){}
                activity.startActivity(new Intent (activity, FirstActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));  //다음 액티비티를 열고
                activity.finish();
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
