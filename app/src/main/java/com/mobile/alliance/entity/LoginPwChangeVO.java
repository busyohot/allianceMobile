package com.mobile.alliance.entity;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginPwChangeVO extends CommonVO
{


    @SerializedName("loginId")
    private String  loginId;

    @SerializedName("phoneNo")
    private String  phoneNo;

    @SerializedName("password")
    private String  password;







    public LoginPwChangeVO(String  cmpyCd, String  loginId, String  phoneNo,String password)
    {
        this.setCmpyCd(cmpyCd);
        this.loginId =   loginId;
        this.phoneNo =   phoneNo;
        this.password =   password;
    }
}
