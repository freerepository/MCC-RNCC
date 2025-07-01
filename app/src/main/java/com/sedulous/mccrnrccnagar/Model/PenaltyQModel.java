package com.sedulous.mccrnrccnagar.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class PenaltyQModel implements Serializable {

    @SerializedName("success")
    public int mStatus;
    @SerializedName("message")
    public String message;
    @SerializedName("penalty_quest")
    public ArrayList<Question> mQuestions;
    public static class Question implements Serializable{

        @SerializedName("id")
        public String mQId;
        @SerializedName("quest")
        public String mQuestion;
        @SerializedName("penalty_id")
        public String mPenaltyId;
        @SerializedName("penalty_type")
        public String mPenaltyType;
        @SerializedName("price")
        public String mPrice;
        @SerializedName("depot_code")
        public String mDepotCode;
    }

}
