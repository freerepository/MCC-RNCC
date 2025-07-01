package com.sedulous.mccrnrccnagar.resonses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RatingQuestionModel {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("questions")
    @Expose
    private String questions;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
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
}
