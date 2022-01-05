package com.mobile.alliance.entity.deliveryDelay;

import com.google.gson.annotations.SerializedName;
import com.mobile.alliance.entity.CommonVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper=true)
public class DeliveryDelayVO extends CommonVO
{

    @SerializedName("tblSoMId")         private	String	tblSoMId	    ;   //주문 고유 ID
    @SerializedName("capaId")           private	String	capaId	        ;   //캐파 고유 ID
    @SerializedName("capaType")         private	String	capaType	    ;   //캐파 유형
    @SerializedName("zoneType")         private	String	zoneType	    ;   //권역유형
    @SerializedName("zoneCd")           private	String	zoneCd	        ;   //권역코드
    @SerializedName("capaSeatType")     private	String	capaSeatType	;   //캐파 좌석 유형 1111 1인시공 2222 2인시공
    @SerializedName("instDt")           private	String	instDt	        ;   //시공일
    @SerializedName("weekCd")           private	String	weekCd	        ;   //요일 코드
    @SerializedName("totCapa")          private	String	totCapa	        ;   //총캐파
    @SerializedName("useCapa")          private	String	useCapa	        ;   //사용캐파
    @SerializedName("remnCapa")         private	String	remnCapa	    ;   //남은캐파
    @SerializedName("statCd")           private	String	statCd	        ;   //배송상태코드
    @SerializedName("ableYn")           private	String	ableYn	        ;   //사용가능여부
    @SerializedName("dlvyCnclDt")       private	String	dlvyCnclDt	    ;   //배송일(원래배송일) 년월일
    @SerializedName("dlvyCnclDt2")      private	String	dlvyCnclDt2	    ;   //배송일(원래배송일)
    @SerializedName("memo")             private	String	memo	        ;   //메모





    public DeliveryDelayVO(String  cmpyCd, String  tblSoMId		)
    {
        this.setCmpyCd(cmpyCd);
        this.tblSoMId = tblSoMId;
    }

}
