package com.iitm.interiitapp.objects;

import com.google.gson.annotations.SerializedName;

public class ScrapCollectorObject {
    @SerializedName("name")
    public String name;
    @SerializedName("mobile")
    public String mobile;
    @SerializedName("description")
    public String desc;
    @SerializedName("loc_lat")
    public double lat;
    @SerializedName("loc_long")
    public double lng;
    @SerializedName("email")
    public String email;
}
