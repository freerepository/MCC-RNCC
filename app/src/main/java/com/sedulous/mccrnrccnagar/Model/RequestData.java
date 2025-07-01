package com.sedulous.mccrnrccnagar.Model;

public class RequestData {
    public String id, keyid, requests, method, url, insert_time, access_time;
    public RequestData(){

    }
    public void setID(String id){
        this.id=id;
    }
    public void setKEYID(String keyid){
        this.keyid=keyid;
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
    public void setInsertTime(String insert_time){
        this.insert_time=insert_time;
    }
    public void setAccessTime(String access_time){
        this.access_time=access_time;
    }
}
