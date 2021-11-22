package com.mobile.alliance.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginResponseVO extends CommonVO
{


    @SerializedName("userId")
    private String  userId;

    @SerializedName("userPw")
    private String  userPw;

    @SerializedName("userName")   private	String	userName	;

    @SerializedName("userGrntCd")   private	String	userGrntCd	;	//사용자권한코드
    @SerializedName("userGrntNm")   private	String	userGrntNm	;	//사용자권한명칭
    @SerializedName("userGrdCd")   private	String	userGrdCd	;	//사용자 등급
    @SerializedName("userGrdNm")   private	String	userGrdNm	;	//사용자 등급

    @SerializedName("mobileGrntCd")   private	String	mobileGrntCd	;	//모바일사용자권한코드
    @SerializedName("mobileGrntNm")   private	String	mobileGrntNm	;	//모바일사용자권한명칭


}
