package com.mobile.alliance.entity.deliveryDelay;

import com.google.gson.annotations.SerializedName;
import com.mobile.alliance.entity.CommonVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper=true)
public class DeliveryDelaySaveVO extends CommonVO
{

    @SerializedName("instMobileMId")    private	String	instMobileMId	;   //모바일 고유 아이디
    @SerializedName("tblSoMId")         private	String	tblSoMId	    ;   //주문 고유 ID
    @SerializedName("capaId")           private	String	capaId	        ;   //캐파 고유 ID
    @SerializedName("orgnDlvyDt")       private	String	orgnDlvyDt	    ;   //원래배송일자
    @SerializedName("postPoneDt")       private	String	postPoneDt	    ;   //변경한배송일자
    @SerializedName("postPoneMemo")     private	String	postPoneMemo	;   //변경사유


    public DeliveryDelaySaveVO(     String  instMobileMId
                                ,   String  tblSoMId
                                ,   String  capaId
                                ,   String  orgnDlvyDt
                                ,   String  postPoneDt
                                ,   String  postPoneMemo
                                ,   String  saveUser
    )
    {
        this.instMobileMId = instMobileMId;
        this.tblSoMId = tblSoMId;
        this.capaId = capaId;
        this.orgnDlvyDt = orgnDlvyDt;
        this.postPoneDt = postPoneDt;
        this.postPoneMemo = postPoneMemo;

        this.setSaveUser(saveUser);
    }

}
