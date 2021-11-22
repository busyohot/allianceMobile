package com.mobile.alliance.api;


import com.mobile.alliance.entity.LoginPwChangeVO;
import com.mobile.alliance.entity.map.MapVO;
import com.mobile.alliance.entity.barCode.BarCodeLiftChangeVO;
import com.mobile.alliance.entity.barCode.BarCodeVO;
import com.mobile.alliance.entity.delivery.DeliveryDetailSrchVO;
import com.mobile.alliance.entity.delivery.DeliveryLiftCancelVO;
import com.mobile.alliance.entity.delivery.DeliveryLiftSaveVO;
import com.mobile.alliance.entity.delivery.DeliverySeqVO;
import com.mobile.alliance.entity.delivery.DeliveryTelVO;
import com.mobile.alliance.entity.delivery.DeliveryVO;
import com.mobile.alliance.entity.LoginVO;
import com.mobile.alliance.entity.noCmpl.NoCmplDelVO;
import com.mobile.alliance.entity.noCmpl.NoCmplOnCreateVO;
import com.mobile.alliance.entity.noCmpl.NoCmplSaveTalkVO;
import com.mobile.alliance.entity.noCmpl.NoCmplSaveStatVO;
import com.mobile.alliance.entity.noCmpl.NoCmplVO;
import com.mobile.alliance.entity.phoneCheck.PhoneCheckSendVO;
import com.mobile.alliance.entity.sendTalk.SaveStatVO;
import com.mobile.alliance.entity.sendTalk.SaveTalkVO;
import com.mobile.alliance.entity.sendTalk.SendTalkVO;
import com.mobile.alliance.entity.sendTalk.SrchTalkVO;
import com.mobile.alliance.entity.sendTalk.TalkVO;
import com.mobile.alliance.entity.yesCmpl.YesCmplDelVO;
import com.mobile.alliance.entity.yesCmpl.YesCmplOnCreateVO;
import com.mobile.alliance.entity.yesCmpl.YesCmplSaveStatVO;
import com.mobile.alliance.entity.yesCmpl.YesCmplSaveTalkVO;
import com.mobile.alliance.entity.yesCmpl.YesCmplVO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServiceApi {
    @Headers({"Content-Type: application/json"})
    @POST("mLogin")
    Call<LoginVO> mLogin(@Body LoginVO loginVO);
    //Call<LoginResponseVO> mLogin(@Body LoginRequestVO loginRequestVO);

   /* @POST("/user/join")
    Call<JoinResponse> userJoin(@Body JoinData data);*/


/******************    DeliveryListFragment.java ********************/
    @Headers({"Content-Type: application/json"})
    @POST("mDeliveryList")
    Call<List<DeliveryVO>> mDeliveryList(@Body DeliveryVO deliveryVO);  //call 부분 - 응답 , body 부분 - 요청


    //배송목록에서 순번바꾸기
    @Headers({"Content-Type: application/json"})
    @POST("mDeliverySeqUpdate")
    Call<DeliveryVO> mDeliverySeqUpdate(@Body DeliverySeqVO deliverySeqVO);

    //배송목록에서 통화 한것 카운트하기
    @Headers({"Content-Type: application/json"})
    @POST("mDeliveryTelUpdate")
    Call<DeliveryVO> mDeliveryTelUpdate(@Body DeliveryTelVO deliveryTelVO);



/******************    DeliveryListFragment.java 끝 ********************/


/******************    DeliveryListDetail.java ********************/

    //배송목록 상세 조회
    @Headers({"Content-Type: application/json"})
    @POST("mDeliveryDetailSrch")
    Call<List<DeliveryVO>> mDeliveryDetailSrch(@Body DeliveryDetailSrchVO deliveryDetailSrchVO);  //call 부분 - 응답 , body 부분 - 요청

    //상차
    @Headers({"Content-Type: application/json"})
    @POST("mDeliveryLiftSave")
    Call<DeliveryLiftSaveVO> mDeliveryLiftSave(@Body ArrayList<DeliveryLiftSaveVO> deliveryLiftSaveVO);


    //배송상세보기에서 통화 한것 카운트하기
    @Headers({"Content-Type: application/json"})
    @POST("mDeliveryTelUpdate")
    Call<DeliveryVO> mDeliveryListDetailTelUpdate(@Body DeliveryTelVO deliveryTelVO);

    /******************    DeliveryListDetail.java  끝  ********************/




    /******************    SendTalkActivity.java  ********************/
    //알림톡 발송 화면에서 알림톡 발송
    @Headers({"Content-Type: application/json","userid: alliance"})
    @POST("send")
    Call<ArrayList<TalkVO>> mSendTalk(@Body ArrayList<SendTalkVO> sendTalkVO);


    //알림톡 화면 열때 조회
    //배송목록 상세 조회
    @Headers({"Content-Type: application/json"})
    @POST("mSrchTalk")
    Call<TalkVO> mSrchTalk(@Body SrchTalkVO srchTalkVO);  //call 부분 - 응답 , body 부분 - 요청

    //알림톡 발송하면 배송상태를 4000 배송준비 에서 5000 해피콜 완료 상태로 변경해주는것
    @Headers({"Content-Type: application/json"})
    @POST("mSaveStat")
    Call<TalkVO> mSaveStat(@Body SaveStatVO saveStatVO);

    //알림톡 발송한 내용을 DB에 저장
    @Headers({"Content-Type: application/json"})
    @POST("mSaveTalk")
    Call<TalkVO> mSaveTalk(@Body SaveTalkVO saveTalkVO);


    //상차취소
    @Headers({"Content-Type: application/json"})
    @POST("mDeliveryLiftCancel")
    Call<DeliveryLiftCancelVO> mDeliveryLiftCancel(@Body DeliveryLiftCancelVO deliveryLiftCancelVO);




    /******************    SendTalkActivity.java  끝  ********************/









    //미마감 사유 목록 불러오기
    @Headers({"Content-Type: application/json"})
    @POST("comComboList")
    Call<List<NoCmplVO>> mNoCmplReasonCombo(@Body NoCmplVO noCmplVO);  //call 부분 - 응답 , body 부분 - 요청



    //미마감 등록 화면 열리자 마자 불러오기 //1줄 넣어 1줄받기
    @Headers({"Content-Type: application/json"})
    @POST("mNoCmplOnCreate")
    Call<NoCmplVO> mNoCmplOnCreate(@Body NoCmplOnCreateVO noCmplOnCreateVO);  //call 부분 - 응답 , body 부분 - 요청


    //미마감 등록 화면 미마감 처리 버튼 (SAVE) 1줄넣어 1줄 받기
    @Headers({"Content-Type: application/json"})
    @POST("mNoCmplSaveStat")
    Call<NoCmplVO> mNoCmplSaveStat(@Body NoCmplSaveStatVO noCmplSaveVO);  //call 부분 - 응답 , body 부분 - 요청

    //미마감 등록 화면 미마감 알림톡 발송 결과를 저장
    @Headers({"Content-Type: application/json"})
    @POST("mNoCmplSaveTalk")
    Call<NoCmplVO> mNoCmplSaveTalk(@Body NoCmplSaveTalkVO noCmplSaveTalkVO);  //call 부분 - 응답 , body 부분 - 요청



    //미마감 취소 처리 화면 미마감취소 처리 버튼 (DEL) 1줄넣어 1줄 받기
    @Headers({"Content-Type: application/json"})
    @POST("mNoCmplDel")
    Call<NoCmplVO> mNoCmplDel(@Body NoCmplDelVO noCmplDelVO);  //call 부분 - 응답 , body 부분 - 요청





    //배송완료 사유 목록 불러오기
    @Headers({"Content-Type: application/json"})
    @POST("comComboList")
    Call<List<YesCmplVO>> mYesCmplReasonCombo(@Body YesCmplVO yesCmplVO);  //call 부분 - 응답 , body 부분 - 요청

    //배송완료 등록 화면 열리자 마자 불러오기 //1줄 넣어 1줄받기
    @Headers({"Content-Type: application/json"})
    @POST("mYesCmplOnCreate")
    Call<YesCmplVO> mYesCmplOnCreate(@Body YesCmplOnCreateVO yesCmplOnCreateVO);  //call 부분 - 응답 , body 부분 - 요청


    //배송완료 등록 화면 배송완료 처리 버튼 (SAVE) 1줄넣어 1줄 받기
    @Headers({"Content-Type: application/json"})
    @POST("mYesCmplSaveStat")
    Call<YesCmplVO> mYesCmplSaveStat(@Body YesCmplSaveStatVO yesCmplSaveStatVO);  //call 부분 - 응답 , body 부분 - 요청

    //배송완료 취소 처리 화면 배송완료취소 처리 버튼 (DEL) 1줄넣어 1줄 받기
    @Headers({"Content-Type: application/json"})
    @POST("mYesCmplDel")
    Call<YesCmplVO> mYesCmplDel(@Body YesCmplDelVO yesCmplDelVO);  //call 부분 - 응답 , body 부분 - 요청



    //배송완료 등록 화면 배송완료 처리 버튼 (SAVE) 1줄넣어 1줄 받기
    @Headers({"Content-Type: application/json"})
    @POST("mYesCmplSaveTalk")
    Call<YesCmplVO> mYesCmplSaveTalk(@Body YesCmplSaveTalkVO yesCmplSaveTalkVO);  //call 부분 - 응답 , body 부분 - 요청

    //인증번호 발송
    @Headers({"Content-Type: application/json","userid: alliance"})
    @POST("send")
    Call<ArrayList<PhoneCheckSendVO>> mPhoneCheckSend(@Body ArrayList<PhoneCheckSendVO> phoneCheckSendVO);





    //비밀번호 변경하기
    @Headers({"Content-Type: application/json"})
    @POST("mChangePwSend")
    Call<LoginVO> mChangePwSend(@Body LoginPwChangeVO loginPwChangeVO);  //call 부분 - 응답 , body 부분 - 요청

    //바코드
    //판매오더번호로 조회하기
    @Headers({"Content-Type: application/json"})
    @POST("mBarCodeScan")
    Call<BarCodeVO> mBarCodeScan(@Body BarCodeVO barCodeVO);  //call 부분 - 응답 , body 부분 - 요청

    
    //바코드
    //시공모바일 고유아이디로 상차변경하기
    @Headers({"Content-Type: application/json"})
    @POST("mBarCodeLiftChange")
    Call<BarCodeVO> mBarCodeLiftChange(@Body BarCodeLiftChangeVO barCodeLiftChangeVO);  //call 부분 - 응답 , body 부분 - 요청



    //MapActivity   지도 주소로 좌표구하기
    @Headers({"Content-Type: application/json","Authorization:KakaoAK af74dfdf6d69a1b2e946ef33cb68bdea"})
    @GET("search/address.json")
    Call<MapVO> mFindPositionAddr(@Query("query") String query);  //call 부분 - 응답 , body 부분 - 요청



}
