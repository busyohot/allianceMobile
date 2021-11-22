package com.mobile.alliance.entity;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginRequestVO extends CommonVO
{


    @SerializedName("userId")
    private String  userId;

    @SerializedName("userPw")
    private String  userPw;







    public LoginRequestVO(String  cmpyCd, String  userId, String  userPw)
    {
        this.setCmpyCd(cmpyCd);
        this.userId =   userId;
        this.userPw =   userPw;
    }
}
