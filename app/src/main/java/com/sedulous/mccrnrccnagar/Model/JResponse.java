package com.sedulous.mccrnrccnagar.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class JResponse implements Serializable {

    @SerializedName("success")
    public int mStatus;
    @SerializedName("message")
    public String message;
}
