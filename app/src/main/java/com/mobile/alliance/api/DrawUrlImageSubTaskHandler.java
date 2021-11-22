package com.mobile.alliance.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.IOException;
import java.io.InputStream;


public class DrawUrlImageSubTaskHandler extends AsyncTask<String, Void, Bitmap> {

    SubsamplingScaleImageView sivSample;
    public DrawUrlImageSubTaskHandler(SubsamplingScaleImageView sivSample) {
        this.sivSample = sivSample;
    }


    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap bitmap = null;
        InputStream in = null;

        try {
            in = new java.net.URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        }
        catch (Exception e) {
            in = null;
            bitmap = null;
            e.printStackTrace();
        }
        finally {
            try {
                if(in != null){
                    in.close();
                }
            }
            catch (IOException e) {
                in = null;
                bitmap = null;
                e.printStackTrace();
            }
        }

        return bitmap;
    }

    protected void onPostExecute(Bitmap bitmap){
        if(bitmap != null){
            sivSample.setImage(ImageSource.bitmap(bitmap));
        }

    }
}
