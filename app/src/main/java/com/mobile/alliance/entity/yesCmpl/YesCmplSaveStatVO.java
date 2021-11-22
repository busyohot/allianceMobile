package com.mobile.alliance.entity.yesCmpl;

import com.google.gson.annotations.SerializedName;
import com.mobile.alliance.entity.CommonVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


//배송완료 결과를 저장하기위한
@Getter
@Setter
@ToString(callSuper=true)
public class YesCmplSaveStatVO extends CommonVO
{
    @SerializedName("instMobileMId")        private	String	instMobileMId	;   //시공모바일 고유ID
    @SerializedName("dlvyCostClct")         private	String	dlvyCostClct	;
    @SerializedName("dlvyCostTxt")          private	String	dlvyCostTxt		;
    @SerializedName("liftCostYn")           private	String	liftCostYn		;
    @SerializedName("listCostTxt")          private	String	listCostTxt		;
    @SerializedName("downSrvcYn")           private	String	downSrvcYn		;
    @SerializedName("downSrvcTxt")          private	String	downSrvcTxt		;
    @SerializedName("wallFstnYn")           private	String	wallFstnYn		;
    @SerializedName("wallFstnTxt")          private	String	wallFstnTxt		;
    @SerializedName("memo")                 private	String	memo			;
    @SerializedName("signImg")              private	String	signImg			;
    @SerializedName("img1")                 private	String	img1			;
    @SerializedName("img2")                 private	String	img2			;
    @SerializedName("img3")                 private	String	img3			;
    @SerializedName("img4")                 private	String	img4			;
    @SerializedName("img5")                 private	String	img5			;
    @SerializedName("img6")                 private	String	img6			;
    @SerializedName("img7")                 private	String	img7			;
    @SerializedName("img8")                 private	String	img8			;
    @SerializedName("img9")                 private	String	img9			;
    @SerializedName("img10")                private	String	img10			;



    public YesCmplSaveStatVO(
            String	instMobileMId
        ,   String	dlvyCostClct
        ,	String	dlvyCostTxt
        ,	String	liftCostYn
        ,	String	listCostTxt
        ,	String	downSrvcYn
        ,	String	downSrvcTxt
        ,	String	wallFstnYn
        ,	String	wallFstnTxt
        ,	String	memo
        ,	String	signImg
        ,	String	img1
        ,	String	img2
        ,	String	img3
        ,	String	img4
        ,	String	img5
        ,	String	img6
        ,	String	img7
        ,	String	img8
        ,	String	img9
        ,	String	img10
        ,	String	saveUser

    )
    {
        this.instMobileMId	=   instMobileMId	;
        this.dlvyCostClct	=   dlvyCostClct	;
        this.dlvyCostTxt	=   dlvyCostTxt		;
        this.liftCostYn		=   liftCostYn		;
        this.listCostTxt	=   listCostTxt		;
        this.downSrvcYn		=   downSrvcYn		;
        this.downSrvcTxt	=   downSrvcTxt		;
        this.wallFstnYn		=   wallFstnYn		;
        this.wallFstnTxt	=   wallFstnTxt		;
        this.memo			=   memo			;
        this.signImg		=   signImg			;
        this.img1			=   img1			;
        this.img2			=   img2			;
        this.img3			=   img3			;
        this.img4			=   img4			;
        this.img5			=   img5			;
        this.img6			=   img6			;
        this.img7			=   img7			;
        this.img8			=   img8			;
        this.img9			=   img9			;
        this.img10			=   img10			;
        this.setSaveUser(saveUser)      		;

    }
}