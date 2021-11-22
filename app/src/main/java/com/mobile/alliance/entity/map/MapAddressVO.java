package com.mobile.alliance.entity.map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MapAddressVO {




        @SerializedName("address_name")
        @Expose
        public String addressName;
        @SerializedName("b_code")
        @Expose
        public String bCode;
        @SerializedName("h_code")
        @Expose
        public String hCode;
        @SerializedName("main_address_no")
        @Expose
        public String mainAddressNo;
        @SerializedName("mountain_yn")
        @Expose
        public String mountainYn;
        @SerializedName("region_1depth_name")
        @Expose
        public String region1depthName;
        @SerializedName("region_2depth_name")
        @Expose
        public String region2depthName;
        @SerializedName("region_3depth_h_name")
        @Expose
        public String region3depthHName;
        @SerializedName("region_3depth_name")
        @Expose
        public String region3depthName;
        @SerializedName("sub_address_no")
        @Expose
        public String subAddressNo;
        @SerializedName("x")
        @Expose
        public String x;
        @SerializedName("y")
        @Expose
        public String y;

}
