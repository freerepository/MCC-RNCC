package com.sedulous.mccrnrccnagar.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ApkModel implements Serializable {
    @SerializedName("success")
    public int mStatus;
    @SerializedName("message")
    public String message;
    @SerializedName("apkdetails")
    public ArrayList<ApkDetail> apkDetails;
    public static class ApkDetail implements Serializable {

        @SerializedName("id")
        public String mId;
        @SerializedName("apk_link")
        public String mApkLink;
        @SerializedName("apk_ver_code")
        public int mApkVersion;
        @SerializedName("posted")
        public String posted_date;
    }
    }
