package com.sedulous.mccrnrccnagar.resonses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class TrainTypeModel implements Serializable {

    @SerializedName("success")
    @Expose
    public int success;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("train_type")
    @Expose
    public ArrayList<TrainType> types;

    public class TrainType implements Serializable{
        @SerializedName("id")
        public String id;
        @SerializedName("train_type")
        public String train_type;
        @SerializedName("act_status")
        public String act_status;
        @SerializedName("del_status")
        public String del_status;
        @SerializedName("created_by")
        public String created_by;
        @SerializedName("created_date")
        public String created_date;
        @SerializedName("updated_by")
        public String updated_by;
        @SerializedName("updated_date")
        public String updated_date;
    }



}
