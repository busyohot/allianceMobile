package com.mobile.alliance.entity.map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString


public class MapMetaVO {


    @SerializedName("is_end")
    @Expose
    public Boolean isEnd;
    @SerializedName("pageable_count")
    @Expose
    public Integer pageableCount;
    @SerializedName("total_count")
    @Expose
    public Integer totalCount;



}
