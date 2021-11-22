package com.mobile.alliance.entity;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.reflect.Array;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommonVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("errorNo")  private String	errorNo;
    @SerializedName("errorMsg")  private String	errorMsg;
    @SerializedName("rtnYn")  private String	rtnYn;	//리턴값 Y 성공, N실패
    @SerializedName("rtnMsg")  private String	rtnMsg;	//리턴 메세지
    @SerializedName("saveUser")  private	String	saveUser;
    @SerializedName("saveTime")  private	String	saveTime;
    @SerializedName("updtUser")  private	String	updtUser;
    @SerializedName("updtTime")  private	String	updtTime;

    @SerializedName("commCd")   private	String	commCd;		//마스터 공통 코드
    @SerializedName("commNm")  private	String	commNm;		//마스터 공통 명칭
    @SerializedName("commDesc")  private	String	commDesc;	//마스터 공통 상세설명
    @SerializedName("comdCd")  private	String	comdCd;		//상세 공통 코드
    @SerializedName("comdNm")  private	String	comdNm;		//상세 공통 명칭
    @SerializedName("comdDesc")  private	String	comdDesc;	//상세 공통 상세설명
    @SerializedName("tblCommMId")  private	String	tblCommMId;			//공통코드 고유아이디

    @SerializedName("cmpyCd")  private	String	cmpyCd				;	//회사코드
    @SerializedName("cmpyNm")  private	String	cmpyNm				;	//회사명칭

    @SerializedName("useYn")  private	String	useYn				;	//사용여부

    @SerializedName("windowId")  private	String	windowId			;	//팝업창 아이디
    @SerializedName("saveGubn")  private	String	saveGubn			;	//저장인지 수정인지 구별
    @SerializedName("rowId")  private	String	rowId			;	//로우아이디

    @SerializedName("fileName")  private	String	fileName	;




    @SerializedName("columnIdList")  private	String[]	columnIdList	;
    @SerializedName("headerList")  private	String[]	headerList	;

    @SerializedName("jsessionId")  private	String	jsessionId	;
    @SerializedName("loginTime")  private	String	loginTime	;
    @SerializedName("interval")  private	int	interval	;   //로그인 제한시간. 단위는 초



    @SerializedName("comboType")    private String	comboType	;       //공통코드 불러오는 콤보타입
    @SerializedName("comboCd")    private String	comboCd	;       //공통코드 불러오는 콤보코드
    @SerializedName("comboNm")    private String	comboNm	;       //공통코드 불러오는 콤보명칭
}
