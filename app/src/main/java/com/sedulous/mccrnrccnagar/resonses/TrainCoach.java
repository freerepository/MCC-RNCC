package com.sedulous.mccrnrccnagar.resonses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrainCoach {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("coach_name")
    @Expose
    private String coach_name;

    @SerializedName("act_status")
    @Expose
    private String act_status;

    @SerializedName("del_status")
    @Expose
    private String del_status;

    @SerializedName("created_by")
    @Expose
    private String created_by;

    @SerializedName("created_date")
    @Expose
    private String created_date;

    @SerializedName("updated_by")
    @Expose
    private String updated_by;

    @SerializedName("updated_date")
    @Expose
    private String updated_date;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoach_name() {
        return coach_name;
    }

    public void setCoach_name(String coach_name) {
        this.coach_name = coach_name;
    }

    public String getAct_status() {
        return act_status;
    }

    public void setAct_status(String act_status) {
        this.act_status = act_status;
    }

    public String getDel_status() {
        return del_status;
    }

    public void setDel_status(String del_status) {
        this.del_status = del_status;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public String getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(String updated_date) {
        this.updated_date = updated_date;
    }

}
