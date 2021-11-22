package com.mobile.alliance.entity.yesCmpl;

import com.google.gson.annotations.SerializedName;
import com.mobile.alliance.entity.CommonVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper=true)
public class YesCmplDelVO extends CommonVO
{
    @SerializedName("instMobileMId")        private	String	instMobileMId	;   //시공모바일 고유ID




    public YesCmplDelVO(
            String instMobileMId

        ,	String saveUser

    )
    {
        this.instMobileMId      =   instMobileMId       ;

        this.setSaveUser(saveUser);

    }
}