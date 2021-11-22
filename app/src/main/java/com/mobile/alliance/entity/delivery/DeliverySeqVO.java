package com.mobile.alliance.entity.delivery;

import com.google.gson.annotations.SerializedName;
import com.mobile.alliance.entity.CommonVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper=true)
public class DeliverySeqVO extends CommonVO
{
    @SerializedName("instMobileMId")        private	String	instMobileMId	;   //시공모바일 고유ID
    @SerializedName("seq")                  private	String	seq				;       //리스트 맨 앞에 나오는 순번

    public DeliverySeqVO(String  instMobileMId, String  seq, String  saveUser)
    {
        this.instMobileMId = instMobileMId;
        this.seq =   seq;
        this.setSaveUser(saveUser);
    }
}
