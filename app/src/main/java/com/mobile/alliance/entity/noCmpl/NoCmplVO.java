package com.mobile.alliance.entity.noCmpl;

import com.google.gson.annotations.SerializedName;
import com.mobile.alliance.entity.CommonVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper=true)
public class NoCmplVO extends CommonVO
{


    @SerializedName("instMobileMId")            private	String	instMobileMId	;
    @SerializedName("tblSoMId")                 private	String	tblSoMId		;
    @SerializedName("mobileXCmplType")          private	String	mobileXCmplType		;
    @SerializedName("mobileXCmplTypeNm")        private	String	mobileXCmplTypeNm		;
    @SerializedName("mobileXCmplTxt")           private	String	mobileXCmplTxt		;
    @SerializedName("memo")                     private	String	memo			;
    @SerializedName("signImg")                  private	String	signImg			;
    @SerializedName("img1")                     private	String	img1			;
    @SerializedName("img2")                     private	String	img2			;
    @SerializedName("img3")                     private	String	img3			;
    @SerializedName("img4")                     private	String	img4			;
    @SerializedName("img5")                     private	String	img5			;
    @SerializedName("img6")                     private	String	img6			;
    @SerializedName("img7")                     private	String	img7			;
    @SerializedName("img8")                     private	String	img8			;
    @SerializedName("img9")                     private	String	img9			;
    @SerializedName("img10")                    private	String	img10			;
    @SerializedName("mobileXCmpleteUseYn")      private	String	mobileXCmpleteUseYn			;
    @SerializedName("bigo")                     private	String	bigo			;
    @SerializedName("sendProc")                 private	String	sendProc		;
    @SerializedName("alrmTalkUserid")           private	String	alrmTalkUserid			;
    @SerializedName("messageType")              private	String	messageType		;
    @SerializedName("phn")                      private	String	phn				;
    @SerializedName("profile")                  private	String	profile			;
    @SerializedName("alrmTalkTmp")              private	String	alrmTalkTmp		;
    @SerializedName("tmplid")                   private	String	tmplid			;
    @SerializedName("tmplnm")                   private	String	tmplnm			;
    @SerializedName("smskind")                  private	String	smskind			;
    @SerializedName("smssender")                private	String	smssender		;
    @SerializedName("smslmstit")                private	String	smslmstit		;
    @SerializedName("smsonly")                  private	String	smsonly			;
    @SerializedName("alrmTalkUseYn")            private	String	alrmTalkUseYn			;

    @SerializedName("agntSendYn")     private String agntSendYn;           //화주사 알림톡 수신여부
    @SerializedName("agntSendHp1")    private String agntSendHp1;           //화주사 알림톡 관리자 전화번호
    public NoCmplVO(String  cmpyCd, String  comboType	, String  commCd		, String comdCd		,String useYn		)
    {
        this.setCmpyCd(cmpyCd);
        this.setComboType(comboType);
        this.setCommCd(commCd);
        this.setComdCd(comdCd);
        this.setUseYn(useYn);
    }

}
