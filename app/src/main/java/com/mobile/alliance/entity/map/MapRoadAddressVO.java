package com.mobile.alliance.entity.map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString


public class MapRoadAddressVO {


    @SerializedName("address_name")
    @Expose
    public String addressName;
    @SerializedName("building_name")
    @Expose
    public String buildingName;
    @SerializedName("main_building_no")
    @Expose
    public String mainBuildingNo;
    @SerializedName("region_1depth_name")
    @Expose
    public String region1depthName;
    @SerializedName("region_2depth_name")
    @Expose
    public String region2depthName;
    @SerializedName("region_3depth_name")
    @Expose
    public String region3depthName;
    @SerializedName("road_name")
    @Expose
    public String roadName;
    @SerializedName("sub_building_no")
    @Expose
    public String subBuildingNo;
    @SerializedName("underground_yn")
    @Expose
    public String undergroundYn;
    @SerializedName("x")
    @Expose
    public String x;
    @SerializedName("y")
    @Expose
    public String y;
    @SerializedName("zone_no")
    @Expose
    public String zoneNo;
}
