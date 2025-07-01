package com.sedulous.mccrnrccnagar.resonses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TrainData implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("task_type")
    @Expose
    private String task_type;

    @SerializedName("task_name")
    @Expose
    private String task_name;

    @SerializedName("train_no")
    @Expose
    private String train_no;

    @SerializedName("depot_id")
    @Expose
    private String depot_id;

    @SerializedName("depot_name")
    @Expose
    private String depot_name;

    @SerializedName("tot_coach")
    @Expose
    private String tot_coach;

    @SerializedName("janitor")
    @Expose
    private String janitor;

    @SerializedName("time_val")
    @Expose
    private String time_val;

    @SerializedName("mode")
    @Expose
    private String mode;

    @SerializedName("coach")
    @Expose
    private String coach;

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

    public String getTask_type() {
        return task_type;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getTrain_no() {
        return train_no;
    }

    public void setTrain_no(String train_no) {
        this.train_no = train_no;
    }

    public String getDepot_id() {
        return depot_id;
    }

    public void setDepot_id(String depot_id) {
        this.depot_id = depot_id;
    }

    public String getDepot_name() {
        return depot_name;
    }

    public void setDepot_name(String depot_name) {
        this.depot_name = depot_name;
    }

    public String getTot_coach() {
        return tot_coach;
    }

    public void setTot_coach(String tot_coach) {
        this.tot_coach = tot_coach;
    }

    public String getJanitor() {
        return janitor;
    }

    public void setJanitor(String janitor) {
        this.janitor = janitor;
    }

    public String getTime_val() {
        return time_val;
    }

    public void setTime_val(String time_val) {
        this.time_val = time_val;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getCoach() {
        return coach;
    }

    public void setCoach(String coach) {
        this.coach = coach;
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
