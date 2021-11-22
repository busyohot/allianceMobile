package com.mobile.alliance.entity.barCode;

import com.google.gson.annotations.SerializedName;
import com.mobile.alliance.entity.CommonVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper=true)
public class BarCodeLiftChangeVO extends CommonVO
{

    @SerializedName("instMobileMId")       private	String	instMobileMId;         //시공 모바일 헤더 아이디



    public BarCodeLiftChangeVO(
                String  instMobileMId
            ,   String  saveUser

    )
    {

        this.instMobileMId=instMobileMId;
        this.setSaveUser(saveUser);

    }

}
