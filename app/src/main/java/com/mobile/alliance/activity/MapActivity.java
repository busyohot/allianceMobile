package com.mobile.alliance.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobile.alliance.R;
import com.mobile.alliance.api.CommonHandler;
import com.mobile.alliance.api.PersistentCookieStore;
import com.mobile.alliance.api.RetrofitClient;
import com.mobile.alliance.api.ServiceApi;
import com.mobile.alliance.entity.map.MapVO;
import com.mobile.alliance.fragment.DeliveryListFragment;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import lombok.SneakyThrows;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity {

    private ServiceApi serviceKaKaoMap; //kakaoMap용
    public  static OkHttpClient client;

    MapView mapView;

    //마커 표시하기
    MapPoint MARKER_POINT;
    MapPOIItem marker;

    TextView mapListAddr1;

    //공통
    CommonHandler commonHandler;
    int no;

    Thread t;
    Thread[] u;

    @SneakyThrows @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        commonHandler = new CommonHandler(this);


        //api 이용시 쿠키전달
        PersistentCookieStore cookieStore = new PersistentCookieStore(this);
        //CookieManager cookieManager  = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        client = new OkHttpClient
                .Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .addInterceptor(commonHandler.httpLoggingInterceptor())
                .build();
        serviceKaKaoMap = RetrofitClient.getKaKaoMap(client).create(ServiceApi.class);  //알림톡용


        String listAddr1 = getIntent().getStringExtra("listAddr1");
        no = Integer.parseInt(getIntent().getStringExtra("no"));

        mapListAddr1 = (TextView) findViewById(R.id.mapListAddr1);
        mapListAddr1.setText(listAddr1);

        mapView = new MapView(this);

        marker = new MapPOIItem();

        mapView.setZoomLevel(7, true);// 줌 레벨 변경
        mapView.zoomIn(true);// 줌 인
        mapView.zoomOut(true);// 줌 아웃

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        u = new Thread[DeliveryListFragment.addr.size()];

        mFindPositionAddrRed(listAddr1, "red",     0);
    }

    //반복문으로 넘오온 주소들에 대한 좌표를 찾기
    private void addrToGeo(){
        for ( int i= 0; i < DeliveryListFragment.addr.size(); i++) {
            if( i != no){
                int finalI = i;
                u[i] = new Thread() {
                    public void run() {
                        try {
                            mFindPositionAddr(DeliveryListFragment.addr.get(finalI).toString(), "gray",     finalI +1);

                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                try {
                    u[i].start();
                    u[i].join();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void mFindPositionAddrRed(String searchAddr,String color,int k ){    //요청 VO

        new AsyncTask<Void, Void, MapVO>() {

            @Override
            protected MapVO doInBackground(Void... params){
                Call<MapVO> call = serviceKaKaoMap.mFindPositionAddr(searchAddr);
                try{
                    return  call.execute().body();
                }catch(IOException e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(MapVO mapVO){
                super.onPostExecute(mapVO);
                if(mapVO == null){
                    return;
                }
                if(mapVO.meta.getTotalCount() == 0){
                    if(color.equals("red")){
                        commonHandler.showFinishAlertDialog("주소찾기",
                                searchAddr + "\n\n주소로 지도 찾기에 실패하였습니다.", "Y");
                        return;
                    }
                    return;
                }

                MARKER_POINT = null;
                MARKER_POINT = MapPoint.mapPointWithGeoCoord(
                        Double.parseDouble(mapVO.documents.get(0).y),
                        Double.parseDouble(mapVO.documents.get(0).x));
                marker = new MapPOIItem();
                marker.setItemName(searchAddr);
                //마커가 찍힐 위치 지정
                marker.setMapPoint(MARKER_POINT);
                marker.setTag(k);
                marker.setAlpha(0.9f);
                if(color.equals("red")){
                    marker.setMarkerType(MapPOIItem.MarkerType.RedPin); // 기본으로 제공하는 BluePin 마커 모양.
                    marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                    // 중심점 변경
                    mapView.setMapCenterPoint(marker.getMapPoint(), true);
                    mapView.addPOIItem(marker);
                }else if(color.equals("gray")){
                    marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                    marker.setSelectedMarkerType(MapPOIItem.MarkerType.BluePin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                    mapView.addPOIItem(marker);
                }

                if(k == 0){
                    addrToGeo();
                }
            }
        }.execute();
    }

    private void mFindPositionAddr(String searchAddr,String color,int k ){    //요청 VO
    /******************************
        new AsyncTask<Void, Void, MapVO>() {

             @Override
             protected MapVO doInBackground(Void... params){
                 Call<MapVO> call = serviceKaKaoMap.mFindPositionAddr(searchAddr);
                 try{
                     return  call.execute().body();
                 }catch(IOException e){
                     e.printStackTrace();
                 }
                 return null;
             }

             @Override
             protected void onPostExecute(MapVO mapVO){
                 super.onPostExecute(mapVO);
                 if(mapVO == null){
                     return;
                 }
                 if(mapVO.meta.getTotalCount() == 0){
                     if(color.equals("red")){
                         commonHandler.showFinishAlertDialog("주소찾기",
                                 searchAddr + "\n\n주소로 지도 찾기에 실패하였습니다.", "Y");
                         return;
                     }
                     return;
                 }

                 MARKER_POINT = null;
                 MARKER_POINT = MapPoint.mapPointWithGeoCoord(
                         Double.parseDouble(mapVO.documents.get(0).y),
                         Double.parseDouble(mapVO.documents.get(0).x));
                 marker = new MapPOIItem();
                 marker.setItemName(searchAddr);
                 //마커가 찍힐 위치 지정
                 marker.setMapPoint(MARKER_POINT);
                 marker.setTag(k);
                 //marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 기본으로 제공하는 BluePin 마커 모양.
                 //marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                 //marker.setCustomImageAnchor(0.5f, 1.0f);
                 marker.setAlpha(0.4f);
                 if(color.equals("red")){
                     marker.setMarkerType(MapPOIItem.MarkerType.RedPin); // 기본으로 제공하는 BluePin 마커 모양.
                     marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

                     //marker.setCustomImageResourceId(R.drawable.marker_return_others);
                     //marker.setCustomSelectedImageResourceId(R.drawable.marker_return_others);
                     // 중심점 변경
                     mapView.setMapCenterPoint(marker.getMapPoint(), true);
                     mapView.addPOIItem(marker);
                     //mapView.addPOIItem(marker);
                 }else if(color.equals("gray")){
                     //marker.setCustomImageResourceId(R.drawable.marker_completed_others);
                     //marker.setCustomSelectedImageResourceId(R.drawable.marker_completed_others);
                     marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                     marker.setSelectedMarkerType(MapPOIItem.MarkerType.BluePin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                     mapView.addPOIItem(marker);
                     //mapView.addPOIItem(marker);
                 }

                //if(k == 0){
                //
                //    u = new Thread() {
                //        public void run() {
                //            try {
                //                addrToGeo();
                //
                //            }catch (Exception e) {
                //                e.printStackTrace();
                //            }
                //        }
                //    };



             }

         }.execute();
        *****************************/

        serviceKaKaoMap.mFindPositionAddr(searchAddr).enqueue(new Callback<MapVO>() {    //앞 요청VO, CallBack 응답 VO
            @Override
            public void onResponse(Call<MapVO> call, Response<MapVO> response) {  //둘다 응답 VO
                marker = new MapPOIItem();
                if(response.isSuccessful()){

                    MapVO mapVO = response.body();

                    if(mapVO.meta.getTotalCount() == 0){
                        if(color.equals("red")){
                            commonHandler.showFinishAlertDialog("주소찾기",searchAddr +"\n\n주소로 지도 찾기에 실패하였습니다.","Y");
                            return;
                        }
                        return;
                    }
                    MARKER_POINT=null;

                    MARKER_POINT = MapPoint.mapPointWithGeoCoord(
                            Double.parseDouble(mapVO.documents.get(0).y) ,
                            Double.parseDouble(mapVO.documents.get(0).x) );
                    marker = new MapPOIItem();
                    marker.setItemName(searchAddr);
                    //마커가 찍힐 위치 지정
                    marker.setMapPoint(MARKER_POINT);
                    marker.setTag(k);
                    if(color.equals("red")) {
                        marker.setMarkerType(MapPOIItem.MarkerType.RedPin); // 기본으로 제공하는 BluePin 마커 모양.
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                        marker.setAlpha(0.9f);

                        // 중심점 변경
                        mapView.setMapCenterPoint(marker.getMapPoint(), true);
                        mapView.addPOIItem(marker);
                    }
                    else if(color.equals("gray")){

                        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.BluePin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                        marker.setAlpha(0.6f);
                        mapView.addPOIItem(marker);
                    }
                }
            }
            @Override
            public void onFailure(Call<MapVO> call, Throwable t) {
            }
        });
        return;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


       for(int a=u.length;a>0;a--)
        {
            try{
                u[a].interrupt();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        finish();
    }
}