package com.mobile.alliance.service;

import com.mobile.alliance.api.TextValueHandler;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

//앱 다운로드를 위한 API 서비스
public class ApiService {
    TextValueHandler textValueHandler = new TextValueHandler();
    public Api api;
    //private String API_URL = "Your_URL";

    private ApiService() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(textValueHandler.API_URL_PATH)
                .build();

        api = retrofit.create(Api.class);

    }

    public static ApiService getInstance() {
        return Holder.apiService;
    }

    private static class Holder {
        public static final ApiService apiService = new ApiService();

    }

    interface Api {

        @GET
        @Streaming
        Call<ResponseBody> downloadFile(@Url String url);
    }

}
