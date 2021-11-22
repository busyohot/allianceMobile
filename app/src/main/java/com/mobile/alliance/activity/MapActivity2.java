package com.mobile.alliance.activity;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobile.alliance.R;
import com.mobile.alliance.api.CommonHandler;
import com.mobile.alliance.api.LogoutHandler;
import com.mobile.alliance.api.PersistentCookieStore;
import com.mobile.alliance.api.RetrofitClient;
import com.mobile.alliance.api.ServiceApi;
import com.mobile.alliance.entity.map.MapVO;
import com.mobile.alliance.fragment.DeliveryListFragment;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;


import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;

import lombok.SneakyThrows;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Call;


public class MapActivity2 extends AppCompatActivity {

    //로그아웃
    private LogoutHandler logoutHandler;
    private ServiceApi serviceKaKaoMap; //kakaoMap용
    public static OkHttpClient client;



    MapView mapView;

    //마커 표시하기
    MapPoint MARKER_POINT;
    MapPOIItem marker;




    TextView mapListAddr1;

    //공통
    CommonHandler commonHandler;
    int no;


    ArrayList<MapVO> listMapVO;



    ArrayList<MapPOIItem> listMarker;

    @SneakyThrows @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        logoutHandler = new LogoutHandler(this);
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


        mapView.setZoomLevel(6, true);// 줌 레벨 변경
        mapView.zoomIn(true);// 줌 인
        mapView.zoomOut(true);// 줌 아웃

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        listMapVO = new ArrayList<MapVO>();
        listMarker = new ArrayList<MapPOIItem>();




        Thread t = new Thread() {
            public void run() {
                try {
                    addrToGeo();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int q=0;q<listMapVO.size();q++){
            marker = new MapPOIItem();

            if(listMapVO.get(q).meta.getTotalCount() == 0){
                if(listMapVO.get(q).getMapColor().equals("red")){
                    commonHandler.showFinishAlertDialog("주소찾기",listMapVO.get(q).getAddr1() +"\n\n주소로 지도 찾기에 실패하였습니다.","Y");
                    return;
                }
                return;
            }

            MARKER_POINT=null;
            MARKER_POINT = MapPoint.mapPointWithGeoCoord(
                    Double.parseDouble(listMapVO.get(q).documents.get(0).y) ,
                    Double.parseDouble(listMapVO.get(q).documents.get(0).x) );

            marker.setItemName(listMapVO.get(q).getMapAddr1());
            //마커가 찍힐 위치 지정
            marker.setMapPoint(MARKER_POINT);
            marker.setTag(listMapVO.get(q).getMapNo());
            marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            marker.setCustomImageAnchor(0.5f,1.0f);
            marker.setAlpha(0.5f);
            if(listMapVO.get(q).getMapColor().equals("red")) {
                marker.setCustomImageResourceId(R.drawable.marker_return_others);
                marker.setCustomSelectedImageResourceId(R.drawable.marker_return_others);
                // 중심점 변경
                mapView.setMapCenterPoint(marker.getMapPoint(), true);
            }
            else if(listMapVO.get(q).getMapColor().equals("gray")) {
                marker.setCustomImageResourceId(R.drawable.marker_completed_others);
                marker.setCustomSelectedImageResourceId(R.drawable.marker_completed_others);

            }

            //mapView.addPOIItem(marker);
            listMarker.add(marker);
        }
       mapView.addPOIItems(listMarker.toArray(new MapPOIItem[listMarker.size()]));
    }



    //반복문으로 넘오온 주소들에 대한 좌표를 찾기
    private void addrToGeo(){
            for ( int i= 0; i < DeliveryListFragment.addr.size(); i++) {
             if( i != no)
                {
                    listMapVO.add(mFindPositionAddr(DeliveryListFragment.addr.get(i).toString(), "gray",     i));
                }else {

                 listMapVO.add(mFindPositionAddr(DeliveryListFragment.addr.get(i).toString(), "red",     i));
                }
            }
        }



    private MapVO mFindPositionAddr(String searchAddr,String color,int k ){    //요청 VO
        Call<MapVO> call = serviceKaKaoMap.mFindPositionAddr(searchAddr);
        MapVO ma = null;
        try{
            ma = call.execute().body();
            ma.setMapColor(color);
            ma.setMapNo(k);
            ma.setMapAddr1(searchAddr);

          }
          catch(Exception e)
          {
              e.printStackTrace();
          }
        return ma;
/****************
         new AsyncTask<Void, Void, MapVO>() {

        @Override
        protected MapVO doInBackground(Void... params) {
        Call<MapVO> call = serviceKaKaoMap.mFindPositionAddr(searchAddr);

        try {

        return call.execute().body();
        } catch (IOException e) {
        e.printStackTrace();
        }

        return null;
        }

        @Override
        protected void onPostExecute(MapVO mapVO) {
        super.onPostExecute(mapVO);

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

                if(color.equals("red")) {
                    marker.setItemName(searchAddr);
                    //마커가 찍힐 위치 지정
                    marker.setMapPoint(MARKER_POINT);
                    marker.setTag(k);
                    marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 기본으로 제공하는 BluePin 마커 모양.
                    marker.setCustomImageResourceId(R.drawable.marker_return_others);
                    marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                    marker.setCustomSelectedImageResourceId(R.drawable.marker_return_others);
                    marker.setCustomImageAnchor(0.5f,1.0f);
                    marker.setAlpha(0.3f);

                    // 중심점 변경
                    mapView.setMapCenterPoint(marker.getMapPoint(), true);
                    mapView.addPOIItem(marker);
                    mapView.addPOIItem(marker);

                }
                else if(color.equals("gray")){
                    marker.setItemName(searchAddr);
                    //마커가 찍힐 위치 지정
                    marker.setMapPoint(MARKER_POINT);
                    marker.setTag(k);


                    marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 기본으로 제공하는 BluePin 마커 모양.
                    marker.setCustomImageResourceId(R.drawable.marker_completed_others);
                    marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                    marker.setCustomSelectedImageResourceId(R.drawable.marker_completed_others);


                    marker.setCustomImageAnchor(0.5f,1.0f);
                    marker.setAlpha(0.3f);

                    mapView.addPOIItem(marker);
                    mapView.addPOIItem(marker);

                }

        }


        }.execute();

**********************/

/****************************
        serviceKaKaoMap.mFindPositionAddr(searchAddr).enqueue(new Callback<MapVO>() {    //앞 요청VO, CallBack 응답 VO

            @Override
            public void onResponse(Call<MapVO> call, Response<MapVO> response) {  //둘다 응답 VO
                marker = new MapPOIItem();
                if(response.isSuccessful()){

                    MapVO mapVO = response.body();
                    listMapVO.add(mapVO);
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

                    if(color.equals("red")) {
                        marker.setItemName(searchAddr);
                        //마커가 찍힐 위치 지정
                        marker.setMapPoint(MARKER_POINT);
                        marker.setTag(k);
                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 기본으로 제공하는 BluePin 마커 모양.
                        marker.setCustomImageResourceId(R.drawable.marker_return_others);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                        marker.setCustomSelectedImageResourceId(R.drawable.marker_return_others);
                        marker.setCustomImageAnchor(0.5f,1.0f);
                        marker.setAlpha(0.3f);

                        // 중심점 변경
                        mapView.setMapCenterPoint(marker.getMapPoint(), true);
                        //mapView.addPOIItem(marker);
                        //mapView.addPOIItem(marker);
                        //listMarker.add(marker);
                    }
                    else if(color.equals("gray")){
                        marker.setItemName(searchAddr);
                        //마커가 찍힐 위치 지정
                        marker.setMapPoint(MARKER_POINT);
                        marker.setTag(k);

                        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 기본으로 제공하는 BluePin 마커 모양.
                        marker.setCustomImageResourceId(R.drawable.marker_completed_others);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                        marker.setCustomSelectedImageResourceId(R.drawable.marker_completed_others);


                        marker.setCustomImageAnchor(0.5f,1.0f);
                        marker.setAlpha(0.3f);


                        //mapView.addPOIItem(marker);
                        //mapView.addPOIItem(marker);
                        //listMarker.add(marker);
                    }
                }
                else
                {

                }

            }
            @Override
            public void onFailure(Call<MapVO> call, Throwable t) {
            }
        });
        return;
***********************************/
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}