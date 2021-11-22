package com.mobile.alliance.entity.sendTalk;

import com.google.gson.annotations.SerializedName;
import com.mobile.alliance.entity.CommonVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper=true)
public class SaveTalkVO extends CommonVO
{
    @SerializedName("instMobileMId")	private String instMobileMId;
    @SerializedName("dlvyDt")			private String dlvyDt;
    @SerializedName("fromDlvyTime")		private String fromDlvyTime;
    @SerializedName("toDlvyTime")		private String toDlvyTime;
    @SerializedName("msg")			    private String msg;
    @SerializedName("sendComplete")		private String sendComplete;
    @SerializedName("userid")			private String userid;
    @SerializedName("messageType")		private String messageType;
    @SerializedName("phn")			    private String phn;
    @SerializedName("profile")			private String profile;
    @SerializedName("reservedt")		private String reservedt;
    @SerializedName("alrmTlkMsg")		private String alrmTlkMsg;
    @SerializedName("title")			private String title;
    @SerializedName("tmplId")			private String tmplId;
    @SerializedName("message")          private String message;                 //리턴값




    public SaveTalkVO(
            String instMobileMId
        ,   String dlvyDt
        ,   String fromDlvyTime
        ,   String toDlvyTime
        ,   String msg
        ,   String sendComplete
        ,   String userid
        ,   String messageType
        ,   String phn
        ,   String profile
        ,   String reservedt
        ,   String alrmTlkMsg
        ,   String title
        ,   String tmplId
            ,String message
            ,   String saveUser

    )
    {
        this.instMobileMId	=	instMobileMId	;
        this.dlvyDt			=	dlvyDt			;
        this.fromDlvyTime	=	fromDlvyTime	;
        this.toDlvyTime		=	toDlvyTime		;
        this.msg			=	msg				;
        this.sendComplete	=	sendComplete	;
        this.userid			=	userid			;
        this.messageType	=	messageType		;
        this.phn			=	phn				;
        this.profile		=	profile			;
        this.reservedt		=	reservedt		;
        this.alrmTlkMsg	=	alrmTlkMsg		;
        this.title			=	title				;
        this.tmplId		=	tmplId			;
        this.message		=	message			;
        this.setSaveUser(saveUser);
    }

}
