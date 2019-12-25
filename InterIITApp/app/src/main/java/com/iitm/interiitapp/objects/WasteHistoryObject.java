package com.iitm.interiitapp.objects;

import com.google.gson.annotations.SerializedName;

public class WasteHistoryObject {
    @SerializedName("id")
    public int id;
    @SerializedName("waste_type")
    public int wasteType;
    @SerializedName("created_at")
    public String createdAt;
    @SerializedName("collected_at")
    public String collectedAt;
    @SerializedName("collected_by")
    public String collectedBy;
    @SerializedName("comments")
    public String comments;
}
