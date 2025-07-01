package com.sedulous.mccrnrccnagar.Model;

public class PendingData {

    public String id, taskid, activity,trainno, requests, method, url,
            pics, upload_status, insert_time, access_time;
    public PendingData(){

    }
    public void setID(String id){
        this.id=id;
    }
    public void setTaskID(String taskid){
        this.taskid=taskid;
    }
    public void setActivity(String activity){
        this.activity=activity;
    }
    public void setTrainNo(String trainNo){
        this.trainno=trainNo;
    }
    public void setRequest(String requests){
        this.requests=requests;
    }
    public void setMethod(String method){
        this.method=method;
    }
    public void setUrl(String url){
        this.url=url;
    }
    public void setPics(String pics){
        this.pics=pics;
    }
    public void setUploadStatus(String upload_status){
        this.upload_status=upload_status;
    }
    public void setInsertTime(String insert_time){
        this.insert_time=insert_time;
    }
    public void setAccessTime(String access_time){
        this.access_time=access_time;
    }
}
