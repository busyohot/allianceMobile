package com.mobile.alliance.entity.sendTalk;

import com.google.gson.annotations.SerializedName;
import com.mobile.alliance.entity.CommonVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper=true)
public class SrchTalkVO extends CommonVO
{
    @SerializedName("instMobileMId")     private String  instMobileMId;   //모바일 M 아이디
    public SrchTalkVO(String instMobileMId )
    {
        this.instMobileMId   =   instMobileMId;
       }

}
