package com.mobile.alliance.entity.sendTalk;

import com.google.gson.annotations.SerializedName;
import com.mobile.alliance.entity.CommonVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



//상태값변경
//4000 배송준비 에서 5000 해피콜 완료 상태로 변경
@Getter
@Setter
@ToString(callSuper=true)
public class SaveStatVO extends CommonVO
{
    @SerializedName("instMobileMId")	private String instMobileMId;
    @SerializedName("dlvyDt")			private String dlvyDt;
    @SerializedName("fromDlvyTime")		private String fromDlvyTime;
    @SerializedName("toDlvyTime")		private String toDlvyTime;
    @SerializedName("msg")			    private String msg;
  





    public SaveStatVO(
            String instMobileMId
        ,   String dlvyDt
        ,   String fromDlvyTime
        ,   String toDlvyTime
        ,   String msg
        ,   String saveUser

    )
    {
        this.instMobileMId	=	instMobileMId	;
        this.dlvyDt			=	dlvyDt			;
        this.fromDlvyTime	=	fromDlvyTime	;
        this.toDlvyTime		=	toDlvyTime		;
        this.msg			=	msg				;
        this.setSaveUser(saveUser);
    }

}
