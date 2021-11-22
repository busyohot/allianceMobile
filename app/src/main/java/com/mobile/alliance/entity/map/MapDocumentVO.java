package com.mobile.alliance.entity.map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MapDocumentVO {

    @SerializedName("address")
    @Expose
    public MapAddressVO address;
    @SerializedName("address_name")
    @Expose
    public String addressName;
    @SerializedName("address_type")
    @Expose
    public String addressType;
    @SerializedName("road_address")
    @Expose
    public MapRoadAddressVO roadAddress;
    @SerializedName("x")
    @Expose
    public String x;
    @SerializedName("y")
    @Expose
    public String y;

}