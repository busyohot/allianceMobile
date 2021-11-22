package com.mobile.alliance.entity.barCode;

import com.google.gson.annotations.SerializedName;
import com.mobile.alliance.entity.CommonVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper=true)
public class BarCodeVO extends CommonVO
{
    @SerializedName("barcode")             private String  barcode;                 //바코드 (판마오더번호)
    @SerializedName("instMobileMId")       private	String	instMobileMId;         //시공 모바일 헤더 아이디
    @SerializedName("soNo")                private	String	soNo ;                  //판매오더번호
    @SerializedName("execUserMId")         private	String	execUserMId  ;          //시공하는 사람 고유아이디
    @SerializedName("userId")              private	String	userId     ;            //사용자아이디
    @SerializedName("userNm")              private	String	userNm     ;            //이름
    @SerializedName("hp")                  private	String	hp ;                    //핸드폰번호
    @SerializedName("dlvyStatCd")          private	String	dlvyStatCd   ;          //배송상태코드
    @SerializedName("instStatNm")          private	String	instStatNm   ;          //배송상태명칭


    public BarCodeVO(
                String  cmpyCd
            ,   String  barcode

    )
    {
        this.setCmpyCd(cmpyCd);
        this.barcode=barcode;

    }

}
