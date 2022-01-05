package com.mobile.alliance.entity.deliveryCancel;

import com.google.gson.annotations.SerializedName;
import com.mobile.alliance.entity.CommonVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper=true)
public class DeliveryCancelSaveVO extends CommonVO
{
    @SerializedName("instMobileMId")   private	String	instMobileMId	;   //시공모바일 고유ID
    @SerializedName("tblSoMId")        private	String	tblSoMId	    ;   //주문 고유 ID
    @SerializedName("dlvyCnclResn")    private	String	dlvyCnclResn	;   //배송 취소 이유 코드
    @SerializedName("cnclMemo")        private	String	cnclMemo	    ;   //취소 메모


    public DeliveryCancelSaveVO(
                String  instMobileMId
            ,   String  tblSoMId
            ,   String  dlvyCnclResn
            ,   String  cnclMemo
            ,   String  tblUserMId
    )
    {
        this.instMobileMId  =   instMobileMId;
        this.tblSoMId       =   tblSoMId;
        this.dlvyCnclResn   =   dlvyCnclResn;
        this.cnclMemo       =   cnclMemo;
        this.setSaveUser(tblUserMId);
    }

}
