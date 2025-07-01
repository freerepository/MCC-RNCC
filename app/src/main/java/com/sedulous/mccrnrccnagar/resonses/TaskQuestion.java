package com.sedulous.mccrnrccnagar.resonses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TaskQuestion implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("task_type")
    @Expose
    private String task_type;

    @SerializedName("task_code")
    @Expose
    private String task_code;

    @SerializedName("weightage")
    @Expose
    private String weightage;

    @SerializedName("quest")
    @Expose
    private String quest;

    @SerializedName("score1")
    @Expose
    private String score1;

    @SerializedName("score2")
    @Expose
    private String score2;

    @SerializedName("score3")
    @Expose
    private String score3;

    @SerializedName("score4")
    @Expose
    private String score4;

    @SerializedName("score5")
    @Expose
    private String score5;

    @SerializedName("is_na")
    @Expose
    private int isna;

    @SerializedName("max_score")
    @Expose
    private String max_score;

    @SerializedName("tstatus")
    @Expose
    private String type;

    private String selectedUserAnswer;


    public String getType() {
        return type;
    }
    public int getNa() {
        return isna;
    }

    public String getMax_score() {
        return max_score;
    }

    public void setMax_score(String max_score) {
        this.max_score = max_score;
    }

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

    public String getTask_code() {
        return task_code;
    }

    public void setTask_code(String task_code) {
        this.task_code = task_code;
    }

    public String getWeightage() {
        return weightage;
    }

    public void setWeightage(String weightage) {
        this.weightage = weightage;
    }

    public String getQuest() {
        return quest;
    }

    public void setQuest(String quest) {
        this.quest = quest;
    }

    public String getScore1() {
        return score1;
    }

    public void setScore1(String score1) {
        this.score1 = score1;
    }

    public String getScore2() {
        return score2;
    }

    public void setScore2(String score2) {
        this.score2 = score2;
    }

    public String getScore3() {
        return score3;
    }

    public void setScore3(String score3) {
        this.score3 = score3;
    }

    public String getScore4() {
        return score4;
    }

    public void setScore4(String score4) {
        this.score4 = score4;
    }

    public String getScore5() {
        return score5;
    }

    public void setScore5(String score5) {
        this.score5 = score5;
    }

    public String getSelectedUserAnswer() {
        return selectedUserAnswer;
    }

    public void setSelectedUserAnswer(String selectedUserAnswer) {
        this.selectedUserAnswer = selectedUserAnswer;
    }
}
