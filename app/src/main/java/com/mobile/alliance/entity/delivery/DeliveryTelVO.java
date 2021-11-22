package com.mobile.alliance.entity.delivery;

import com.google.gson.annotations.SerializedName;
import com.mobile.alliance.entity.CommonVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper=true)
public class DeliveryTelVO extends CommonVO
{
    @SerializedName("instMobileMId")        private	String	instMobileMId	;   //시공모바일 고유ID
    @SerializedName("mobileGrntCd")        private	String	mobileGrntCd	;



    public DeliveryTelVO(String  instMobileMId, String  saveUser, String mobileGrntCd)
    {
        this.instMobileMId = instMobileMId;
        this.setSaveUser(saveUser);
        this.mobileGrntCd = mobileGrntCd;
    }

}
