package com.sedulous.mccrnrccnagar.resonses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserData {

    @SerializedName("mcc_user_id")
    @Expose
    private String mcc_user_id;

    @SerializedName("user_type")
    @Expose
    private String user_type;

    @SerializedName("depot_id")
    @Expose
    private String depot_id;

    @SerializedName("depot_name")
    @Expose
    private String depot_name;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("ph_no")
    @Expose
    private String ph_no;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("aadhar_no")
    @Expose
    private String aadhar_no;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("act_status")
    @Expose
    private String act_status;

    @SerializedName("del_status")
    @Expose
    private String del_status;

    @SerializedName("emp_id")
    @Expose
    private String emp_id;

    @SerializedName("created_by")
    @Expose
    private String created_by;

    @SerializedName("created_date")
    @Expose
    private String created_date;

    @SerializedName("updated_by")
    @Expose
    private String updated_by;

    public String getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(String updated_date) {
        this.updated_date = updated_date;
    }

    @SerializedName("updated-date")
    @Expose
    private String updated_date;


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
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

    public String getMcc_user_id() {
        return mcc_user_id;
    }

    public void setMcc_user_id(String mcc_user_id) {
        this.mcc_user_id = mcc_user_id;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPh_no() {
        return ph_no;
    }

    public void setPh_no(String ph_no) {
        this.ph_no = ph_no;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAadhar_no() {
        return aadhar_no;
    }

    public void setAadhar_no(String aadhar_no) {
        this.aadhar_no = aadhar_no;
    }
}
