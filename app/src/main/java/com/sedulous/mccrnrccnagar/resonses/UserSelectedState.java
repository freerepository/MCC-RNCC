package com.sedulous.mccrnrccnagar.resonses;

import java.util.ArrayList;

public class UserSelectedState {
    String coathNo;
    ArrayList<TaskQuestion> arrayList;
    String imagePath1;
    String imagePath2;

    public String getCoathNo() {
        return coathNo;
    }

    public void setCoathNo(String coathNo) {
        this.coathNo = coathNo;
    }

    public ArrayList<TaskQuestion> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<TaskQuestion> arrayList) {
        this.arrayList = arrayList;
    }

    public String getImagePath1() {
        return imagePath1;
    }

    public void setImagePath1(String imagePath1) {
        this.imagePath1 = imagePath1;
    }

    public String getImagePath2() {
        return imagePath2;
    }

    public void setImagePath2(String imagePath2) {
        this.imagePath2 = imagePath2;
    }
}
