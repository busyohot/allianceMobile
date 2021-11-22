package com.mobile.alliance.entity.delivery;

import com.google.gson.annotations.SerializedName;
import com.mobile.alliance.entity.CommonVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper=true)
public class DeliveryDetailSrchVO extends CommonVO
{
    @SerializedName("instMobileMId")        private	String	instMobileMId	;   //시공모바일 고유ID
    @SerializedName("tblUserMId")        private	String	tblUserMId	;           //사용자 ID의 고유ID
    @SerializedName("mobileGrntCd")        private	String	mobileGrntCd	;   //모바일 권한 코드


    public DeliveryDetailSrchVO(String  instMobileMId, String  tblUserMId, String  mobileGrntCd)
    {
        this.instMobileMId = instMobileMId;
        this.tblUserMId = tblUserMId;
        this.mobileGrntCd = mobileGrntCd;

    }
}
