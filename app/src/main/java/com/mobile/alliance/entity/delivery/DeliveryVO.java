package com.mobile.alliance.entity.delivery;

import com.google.gson.annotations.SerializedName;
import com.mobile.alliance.entity.CommonVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper=true)
public class DeliveryVO extends CommonVO
{
    @SerializedName("instDt")               private String  instDt;    //시공일
    @SerializedName("tblUserMId")           private String  tblUserMId;    //user 의 고유ID
    @SerializedName("userGrntCd")           private	String	userGrntCd	;	//사용자권한코드



    @SerializedName("userGrntNm")           private	String	userGrntNm	;	//사용자권한명칭

    @SerializedName("mobileGrntCd")   private	String	mobileGrntCd	;	//모바일 사용자권한코드
    @SerializedName("mobileGrntNm")   private	String	mobileGrntNm	;	//모바일 사용자권한명칭


    @SerializedName("userGrdCd")            private	String	userGrdCd	;	//사용자 등급
    @SerializedName("userGrdNm")            private	String	userGrdNm	;	//사용자 등급
    @SerializedName("dcCd")                 private	String	dcCd	;       //물류센터코드
    @SerializedName("dcNm")                 private	String	dcNm	;       //물류센터명칭

    @SerializedName("instMobileMId")        private	String	instMobileMId	;   //시공모바일 고유ID
    @SerializedName("totCnt")               private	String	totCnt			;   //전체수량
    @SerializedName("ingCnt")               private	String	ingCnt			;   //배송중수량
    @SerializedName("ncmplCn")              private	String	ncmplCn			;   //미마감수량
    @SerializedName("cmplCnt")              private	String	cmplCnt			;      //배송완료 수량
    @SerializedName("paltNoxx")             private	String	paltNoxx		;      //팔렛트넘버

    @SerializedName("seq")                  private	String	seq				;       //리스트 맨 앞에 나오는 순번
    @SerializedName("instCtgr")             private	String	instCtgr		;       //시공카테고리코드-가구/가전/서비스
    @SerializedName("instCtgrNm")           private	String	instCtgrNm		;       //시공카테고리명칭
    @SerializedName("instType")             private	String	instType		;       //시공유형코드    -일반 / 익일/ 새벽
    @SerializedName("instTypeNm")           private	String	instTypeNm		;       //시공유형명칭
    @SerializedName("seatType")             private	String	seatType		;       //시공좌석유형코드  -1인 / 2인
    @SerializedName("seatTypeNm")           private	String	seatTypeNm		;       //시공좌석유형명칭

    @SerializedName("dlvyStatCd")           private	String	dlvyStatCd		;       //배송상태코드
    @SerializedName("dlvyStatNm")           private	String	dlvyStatNm		;       //배송상태명칭
    @SerializedName("soType")               private	String	soType			;       //주문유형코드
    @SerializedName("soTypeNm")             private	String	soTypeNm		;       //주문유형명칭
    @SerializedName("soNo")                 private	String	soNo			;       //주문번호(오더번호)
    @SerializedName("acptEr")               private	String	acptEr			;       //수취인
    @SerializedName("postCd")               private	String	postCd			;       //우편번호

    @SerializedName("addr1")                private	String	addr1			;       //주소1
    @SerializedName("addr2")                private	String	addr2			;       //주소2(상세주소)
    @SerializedName("acptTel1")             private	String	acptTel1		;       //수취인 일반번호
    @SerializedName("acptTel2")             private	String	acptTel2		;       //수취인 휴대전화

    @SerializedName("telCnt")               private	String	telCnt			;       //전화한 횟수
    @SerializedName("zoneType")             private	String	zoneType		;       //권역유형코드
    @SerializedName("zoneTypeNm")           private	String	zoneTypeNm		;       //권역유형명칭
    @SerializedName("zoneCd")               private	String	zoneCd			;       //권역코드
    @SerializedName("zoneNm")               private	String	zoneNm			;       //권역명칭
    @SerializedName("instStatCd")           private	String	instStatCd		;       //시공상태코드
    @SerializedName("instCost")             private	String	instCost		;       //시공비
    @SerializedName("dlvyCostType")         private	String	dlvyCostType	;       //운임구분코드(착불이냐 선불이냐)
    @SerializedName("rcptCost")             private	String	rcptCost		;       //착불비
    @SerializedName("passCost")             private	String	passCost		;       //통행료
    @SerializedName("dlvyCost")             private	String	dlvyCost		;       //배송비
    @SerializedName("agntHp")               private	String	agntHp			;       //영업자 핸드폰
    @SerializedName("agntCd")               private	String	agntCd			;       //영업자 코드
    @SerializedName("dlvyTypeCd")           private	String	dlvyTypeCd		;       //배송유형코드
    @SerializedName("instPlanId")           private	String	instPlanId		;       //시공계획고유ID
    @SerializedName("tblSoMId")             private	String	tblSoMId		;       //주문 고유ID
    @SerializedName("ordrEr")               private	String	ordrEr			;       //주문자
    @SerializedName("dlvyRqstMsg")          private	String	dlvyRqstMsg		;       //배송요청사항
    @SerializedName("custSpclTxt")          private	String	custSpclTxt		;       //고객특이사항
    @SerializedName("cbm")                  private	String	cbm				;       //CBM
    @SerializedName("liftCmplYn")           private	String	liftCmplYn		;       //상차완료여부
    @SerializedName("hpclCmplYn")           private	String	hpclCmplYn		;       //해피콜여부
    @SerializedName("dlvyCmplYn")           private	String	dlvyCmplYn		;       //배송완료여부
    @SerializedName("dlvyClclYn")           private	String	dlvyClclYn		;       //배송취소여부
    @SerializedName("dlvyPstpYn")           private	String	dlvyPstpYn		;       //배송연기유무
    @SerializedName("dlvyNcmplYn")          private	String	dlvyNcmplYn		;       //배송미완료유무

    @SerializedName("instMobileDId")        private	String	instMobileDId		;       //모바일 상세 아이디


    @SerializedName("prodCd")        private	String	prodCd			;		//품목코드
    @SerializedName("prodNm")        private	String	prodNm			;		//품목명칭
    @SerializedName("qty")        private	String	qty				;		//품목수량





    @SerializedName("instUrl")        private	String	instUrl			;		//품목 설명 URL
    @SerializedName("instImgPath")        private	String	instImgPath		;		//품목설명 이미지
    @SerializedName("liftQty")        private	String	liftQty			;		//품목상차수량
    @SerializedName("prodStatCd")         private	String	prodStatCd		;		//품목상태코드
    @SerializedName("prodStatNm")        private	String	prodStatNm		;		//품목상태명칭
    @SerializedName("prodLiftCmplYn")        private	String	prodLiftCmplYn	;		//품목상차완료여부


    @SerializedName("editYn")        private	String	editYn	;		//입력 가능한지, 배송일+1일의 10시가 지나서 입력이 불가능 한지를 표시. Y 입력-상차, 미마감,배송완료 등 가능 N 불가능

    @SerializedName("mtoYn")        private	String	mtoYn	;           //mto여부

    public DeliveryVO(String  cmpyCd, String  instDt, String  tblUserMId, String mobileGrntCd)
    {
        this.setCmpyCd(cmpyCd);
        this.instDt =   instDt;
        this.tblUserMId =   tblUserMId;
        this.mobileGrntCd =   mobileGrntCd;
    }

}
