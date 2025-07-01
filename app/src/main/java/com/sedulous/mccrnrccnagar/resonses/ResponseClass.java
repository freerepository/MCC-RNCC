package com.sedulous.mccrnrccnagar.resonses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResponseClass {

    @SerializedName("success")
    @Expose
    private int success;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("user_data")
    @Expose
    private ArrayList<UserData> userData;

    @SerializedName("task_type")
    @Expose
    private ArrayList<TaskType> taskType;

//    @SerializedName("train_type")
//    @Expose
//    private ArrayList<TrainType> trainTypes;

    @SerializedName("train_number")
    @Expose
    private ArrayList<TrainData> trainData;

    @SerializedName("train_coach")
    @Expose
    private ArrayList<TrainCoach> trainCoaches;

    @SerializedName("task_question")
    @Expose
    private ArrayList<TaskQuestion> taskQuestionArrayList;



    public ArrayList<TaskQuestion> getTaskQuestionArrayList() {
        return taskQuestionArrayList;
    }

    public void setTaskQuestionArrayList(ArrayList<TaskQuestion> taskQuestionArrayList) {
        this.taskQuestionArrayList = taskQuestionArrayList;
    }

    public ArrayList<TrainCoach> getTrainCoaches() {
        return trainCoaches;
    }

    public void setTrainCoaches(ArrayList<TrainCoach> trainCoaches) {
        this.trainCoaches = trainCoaches;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public ArrayList<UserData> getUserData() {
        return userData;
    }

    public void setUserData(ArrayList<UserData> userData) {
        this.userData = userData;
    }

    public ArrayList<TaskType> getTaskType() {
        return taskType;
    }

    public void setTaskType(ArrayList<TaskType> taskType) {
        this.taskType = taskType;
    }

    public ArrayList<TrainData> getTrains() {
        return trainData;
    }

    public void setTrainData(ArrayList<TrainData> trainData) {
        this.trainData = trainData;
    }

}
