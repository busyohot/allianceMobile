package com.mobile.alliance.entity.deliveryCancel;

import com.google.gson.annotations.SerializedName;
import com.mobile.alliance.entity.CommonVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper=true)
public class DeliveryCancelVO extends CommonVO
{





    public DeliveryCancelVO(String  cmpyCd, String  comboType	, String  commCd		, String comdCd		, String useYn		)
    {
        this.setCmpyCd(cmpyCd);
        this.setComboType(comboType);
        this.setCommCd(commCd);
        this.setComdCd(comdCd);
        this.setUseYn(useYn);
    }

}
