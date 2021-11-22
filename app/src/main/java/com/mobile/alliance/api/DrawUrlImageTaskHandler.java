package com.mobile.alliance.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.IOException;
import java.io.InputStream;





public class DrawUrlImageTaskHandler extends AsyncTask<String, Void, Bitmap> {
    ImageView ivSample;

    public DrawUrlImageTaskHandler(ImageView ivSample) {
        this.ivSample = ivSample;
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

            e.printStackTrace();
            in = null;
            bitmap = null;

        }
        finally {
            try {
                if(in != null)
                {
                    in.close();
                }

            }
            catch (IOException e) {

                e.printStackTrace();
                in = null;
                bitmap = null;
            }
        }

        return bitmap;
    }

    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap != null){
            ivSample.setImageBitmap(bitmap);
        }
        else
        {
            ivSample.setEnabled(false);
        }
    }
}


