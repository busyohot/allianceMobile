package com.mobile.alliance.entity.map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mobile.alliance.entity.CommonVO;
import com.mobile.alliance.entity.sendTalk.TalkVO;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MapVO  extends CommonVO {


    @SerializedName("query")  private String	query;
    @SerializedName("addr1")  private String	addr1;
    @SerializedName("addr2")  private String	addr2;
    @SerializedName("mapColor")  private String	mapColor;
    @SerializedName("mapNo")  private int	mapNo;
    @SerializedName("mapAddr1")  private String	mapAddr1;

    @SerializedName("documents")
    @Expose
    public List<MapDocumentVO> documents = null;
    @SerializedName("meta")
    @Expose
    public MapMetaVO meta;


    public MapVO(String  query)
    {

        this.query =   query;

    }

}
