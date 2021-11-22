package com.mobile.alliance.entity.phoneCheck;

import com.google.gson.annotations.SerializedName;
import com.mobile.alliance.entity.CommonVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper=true)
public class PhoneCheckSendVO extends CommonVO
{
    @SerializedName("message_type")     private String  message_type;   //알림톡 발송 메세지 타입
    @SerializedName("phn")              private String  phn;            //알림톡 받을 사람 전화번호
    @SerializedName("profile")          private	String	profile;	    //알림톡에서 준 고유아이디
    @SerializedName("tmplId")           private	String	tmplId;	        //템플릿아이디
    @SerializedName("msg")              private	String	msg;	        //알림톡용 메세지(템플릿)
    @SerializedName("smsKind")          private	String	smsKind;	    //알림톡 실패시 발송할 문자의 종류 문자메시지 전환발송시 SMS/LMS구분 (S:SMS, L:LMS,M:MMS, N:발송하지 않음)
    @SerializedName("msgSms")           private	String	msgSms;         //알림톡 실패시 문자로 발송할 내용(알림톡 내용이랑 같은거 씀)
    @SerializedName("smsSender")        private	String	smsSender;      //알림톡 실패시 문자로 발송할때 발송번호(알림톡 업체에 등록되어있는 번호여야함)
    @SerializedName("smsLmsTit")        private	String	smsLmsTit;      //알림톡 실패시 문자로 발송할때 LMS 일경우 제목
    @SerializedName("smsOnly")          private	String	smsOnly;        //Y 면 알림톡으로 안보내고 그냥 문자로만 보냄, N 알림톡으로 발송하고 실패하면 문자로 보냄
    @SerializedName("reservedt")          private	String	reservedt;      //알림톡 발송시간




    public PhoneCheckSendVO(
                String  message_type
            ,   String  phn
            ,   String  profile
            ,   String reservedt
            ,   String  tmplId
            ,   String  msg
            ,   String  smsKind
            ,   String  msgSms
            ,   String  smsSender
            ,   String  smsLmsTit
            ,   String  smsOnly


    )
    {
        this.message_type   =   message_type;
        this.phn=phn;
        this.profile=profile;
        this.reservedt = reservedt;
        this.tmplId=tmplId;
        this.msg   =msg;
        this.smsKind   =smsKind;
        this.msgSms   =msgSms;
        this.smsSender   =smsSender;
        this.smsLmsTit   =smsLmsTit;
        this.smsOnly   =smsOnly;


    }

}
