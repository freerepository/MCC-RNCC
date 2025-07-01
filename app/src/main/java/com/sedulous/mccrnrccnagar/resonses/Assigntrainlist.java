package com.sedulous.mccrnrccnagar.resonses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Assigntrainlist implements Serializable {

    @SerializedName("Train_no")
    @Expose
    private String trainNo;
    @SerializedName("coaches")
    @Expose
    private String coaches;
    @SerializedName("ac_coach")
    @Expose
    private String acCoach;
    @SerializedName("SL_coach")
    @Expose
    private String sLCoach;
    @SerializedName("GN_coach")
    @Expose
    private String gNCoach;

    @SerializedName("coach_type")
    @Expose
    private String coachType;

    public String getTrainNo() {
        return trainNo;
    }

    public void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }

    public String getCoaches() {
        return coaches;
    }

    public void setCoaches(String coaches) {
        this.coaches = coaches;
    }

    public String getAcCoach() {
        return acCoach;
    }

    public void setAcCoach(String acCoach) {
        this.acCoach = acCoach;
    }

    public String getSLCoach() {
        return sLCoach;
    }

    public void setSLCoach(String sLCoach) {
        this.sLCoach = sLCoach;
    }

    public String getGNCoach() {
        return gNCoach;
    }

    public void setGNCoach(String gNCoach) {
        this.gNCoach = gNCoach;
    }

    public String getCoachType() {
        return coachType;
    }

    public void setCoachType(String coachType) {
        this.coachType = coachType;
    }

}
