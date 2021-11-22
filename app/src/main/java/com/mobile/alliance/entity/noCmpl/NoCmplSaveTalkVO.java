package com.mobile.alliance.entity.noCmpl;

import com.google.gson.annotations.SerializedName;
import com.mobile.alliance.entity.CommonVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


//미마감 알림톡 발송 결과를 저장하기위한 VO
@Getter
@Setter
@ToString(callSuper=true)
public class NoCmplSaveTalkVO extends CommonVO
{
    @SerializedName("instMobileMId")        private	String	instMobileMId	;   //시공모바일 고유ID
    @SerializedName("sendComplete")         private	String	sendComplete	;
    @SerializedName("alrmTalkUserid")               private	String	alrmTalkUserid			;
    @SerializedName("messageType")          private	String	messageType		;
    @SerializedName("phn")                  private	String	phn				;
    @SerializedName("profile")              private	String	profile			;
    @SerializedName("reservedt")            private	String	reservedt		;
    @SerializedName("alrmTlkMsg")           private	String	alrmTlkMsg		;
    @SerializedName("title")                private	String	title			;
    @SerializedName("tmplid")               private	String	tmplid			;
    @SerializedName("message")               private	String	message			;



    public NoCmplSaveTalkVO(
            String instMobileMId
        ,	String sendComplete
        ,	String alrmTalkUserid
        ,	String messageType
        ,	String phn
        ,	String profile
        ,	String reservedt
        ,	String alrmTlkMsg
        ,	String title
        ,	String tmplid
        ,	String message
        ,	String saveUser
    )
    {
        this.instMobileMId      =   instMobileMId       ;
        this.sendComplete		=	sendComplete		;
        this.alrmTalkUserid		=	alrmTalkUserid		;
        this.messageType		=	messageType			;
        this.phn				=	phn					;
        this.profile			=	profile				;
        this.reservedt			=	reservedt			;
        this.alrmTlkMsg			=	alrmTlkMsg			;
        this.title				=	title				;
        this.tmplid				=	tmplid				;
        this.message			=	message				;
        this.setSaveUser(saveUser);
    }
}