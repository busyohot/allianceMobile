package com.mobile.alliance.entity.sendTalk;

import com.google.gson.annotations.SerializedName;
import com.mobile.alliance.entity.CommonVO;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper=true)
public class TalkVO extends CommonVO {

    @SerializedName("instMobileMId")    private String instMobileMId;           //모바일 고유아이디
    @SerializedName("dlvyRqstMsg")      private String dlvyRqstMsg;             //요청사항
    @SerializedName("cnslTxt")          private String cnslTxt;                 //상담내용
    @SerializedName("dlvyDt")           private String dlvyDt;                  //배송예정일자
    @SerializedName("fromDlvyTime")     private String fromDlvyTime;            //배송예정시간 FROM
    @SerializedName("toDlvyTime")       private String toDlvyTime;              //배송예정시간 TO

    @SerializedName("alrmTalkTmp")      private String alrmTalkTmp;             //메세지(통화내용)
    @SerializedName("dlvyStatCd")       private String dlvyStatCd;              //배송상태코드

    @SerializedName("messageType")      private String messageType;             //알림톡 발송 메세지 타입
    @SerializedName("phn")              private String phn;                     //알림톡 받을 사람 전화번호
    @SerializedName("profile")          private String profile;                 //알림톡에서 준 고유아이디
    @SerializedName("tmplId")           private String tmplId;                  //템플릿아이디
    @SerializedName("tmplNm")           private String tmplNm;                  //템플릿명칭
    @SerializedName("msg")              private String msg;                 //알림톡용 메세지(템플릿)
    @SerializedName("smsKind")          private String smsKind;                 //알림톡 실패시 발송할 문자의 종류 문자메시지 전환발송시 SMS/LMS구분 (S:SMS, L:LMS,M:MMS, N:발송하지 않음)
    @SerializedName("msgSms")           private String msgSms;                  //알림톡 실패시 문자로 발송할 내용(알림톡 내용이랑 같은거 씀)
    @SerializedName("smsSender")        private String smsSender;               //알림톡 실패시 문자로 발송할때 발송번호(알림톡 업체에 등록되어있는 번호여야함)
    @SerializedName("smsLmsTit")        private String smsLmsTit;               //알림톡 실패시 문자로 발송할때 LMS 일경우 제목
    @SerializedName("smsOnly")          private String smsOnly;                 //Y 면 알림톡으로 안보내고 그냥 문자로만 보냄, N 알림톡으로 발송하고 실패하면 문자로 보냄

    @SerializedName("type")             private String type;                    //알림톡 유형
    @SerializedName("tblSoMId")         private String tblSoMId;                //주문오더 고유 아이디

    @SerializedName("sendProc")         private String sendProc;                //알림톡 발송-단계 명칭
    @SerializedName("userid")           private String userid;                  //알림톡 발송-발송자아이디
    @SerializedName("talkReservedt")    private String talkReservedt;           //알림톡 발송-발송할시간

    @SerializedName("code")             private String code;                    //리턴값
    @SerializedName("msgid")            private String msgid;                   //리턴값
    @SerializedName("message")          private String message;                 //리턴값
    @SerializedName("originMessage")    private String originMessage;           //리턴값


    public Data data;
    @Getter
    @Setter
    @ToString(callSuper=true)

    public class Data{
        @SerializedName("phn")          private String phn;     //리턴값
        @SerializedName("msgid")        private String msgid;   //리턴값
        @SerializedName("type")         private String type;    //리턴값
    }
}

