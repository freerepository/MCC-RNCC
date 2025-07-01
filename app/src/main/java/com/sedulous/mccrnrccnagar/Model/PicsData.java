package com.sedulous.mccrnrccnagar.Model;

public class PicsData {
    public String id, keyid, filepath, url, upload_status, created_time;
    public PicsData(){
        
    }
    public void setID(String id){
        this.id=id;
    }
    public void setKEYID(String keyid){
        this.keyid=keyid;
    }
    public void setFilePath(String filepath){
        this.filepath = filepath;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public void setUploadStatus(String uploadStatus){
        this.upload_status=uploadStatus;
    }
    public void setCreatedTime(String created_time){
        this.created_time=created_time;
    }
}
