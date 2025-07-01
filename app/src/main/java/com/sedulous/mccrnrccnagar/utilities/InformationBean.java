package com.sedulous.mccrnrccnagar.utilities;

import org.json.JSONArray;

public class InformationBean {

    private JSONArray questionJsonArray;

    private String loadNumber;

    private String image1;

    private String image2;

    private String coachNo;

    private String coachName;


    public InformationBean(JSONArray questionJsonArray, String loadNumber, String image1, String image2, String coachNo, String coachName){


        this.questionJsonArray = questionJsonArray;

        this.loadNumber = loadNumber;

        this.image1 = image1;

        this.image2 = image2;

        this.coachName = coachName;

        this.coachNo = coachNo;
    }

    public JSONArray getQuestionJsonArray() {
        return questionJsonArray;
    }

    public String getLoadNumber() {
        return loadNumber;
    }

    public String getImage1() {
        return image1;
    }

    public String getImage2() {
        return image2;
    }

    public String getCoachNo() {
        return coachNo;
    }

    public String getCoachName() {
        return coachName;
    }
}
