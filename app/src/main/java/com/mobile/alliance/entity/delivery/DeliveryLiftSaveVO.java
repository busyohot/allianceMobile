package com.mobile.alliance.entity.delivery;

import com.google.gson.annotations.SerializedName;
import com.mobile.alliance.entity.CommonVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
//배송상세화면에서 상차할때 씀
@Getter
@Setter
@ToString(callSuper=true)
public class DeliveryLiftSaveVO extends CommonVO
{

    @SerializedName("instMobileMId")    private	String	instMobileMId	;   //모바일헤더아이디
    @SerializedName("instMobileDId")    private	String	instMobileDId	;   //모바일상세아이디
    @SerializedName("liftQty")          private	String	liftQty			;      //상차갯수

    public DeliveryLiftSaveVO(
                String  saveGubn
            ,   String  instMobileMId
            ,   String  instMobileDId
            ,   String  liftQty
            ,   String  saveUser


    )
    {
        this.setSaveGubn(saveGubn);
        this.instMobileMId = instMobileMId;
        this.instMobileDId = instMobileDId;
        this.liftQty = liftQty;
        this.setSaveUser(saveUser);
    }

}
