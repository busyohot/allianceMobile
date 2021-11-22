package com.mobile.alliance.api;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static TextValueHandler textValueHandler = new TextValueHandler();
    //api 이용시 쿠키전달
    private PersistentCookieStore persistentCookieStore;



    private static Retrofit retrofit = null;
    private static Retrofit talkfit = null;
    private static Retrofit kakaoMapfit = null;
    public static Retrofit getClient(OkHttpClient client) {


      /*  Gson gson = new GsonBuilder()
                .setLenient()
                .create();*/




        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(textValueHandler.BASE_URL)
                    //.client(FirstActivity.client)
                    .client(client)

                    .addConverterFactory(GsonConverterFactory.create())

                    .build();
        }

        return retrofit;
    }


    public static Retrofit getTalk(OkHttpClient client) {
/*Gson gson = new GsonBuilder()
                .setLenient()
                .create();*/

        if (talkfit == null) {
            talkfit = new Retrofit.Builder()
                    .baseUrl(textValueHandler.TALK_URL)

                    //.client(FirstActivity.client)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())

                    .build();
        }

        return talkfit;
    }





    public static Retrofit getKaKaoMap(OkHttpClient client) {
/*Gson gson = new GsonBuilder()
                .setLenient()
                .create();*/

        if (kakaoMapfit == null) {
            kakaoMapfit = new Retrofit.Builder()
                    .baseUrl(textValueHandler.KAKAO_MAP_URL)

                    //.client(FirstActivity.client)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())

                    .build();
        }

        return kakaoMapfit;
    }




}

