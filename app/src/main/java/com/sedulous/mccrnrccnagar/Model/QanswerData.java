package com.sedulous.mccrnrccnagar.Model;

import com.sedulous.mccrnrccnagar.resonses.TaskQuestion;
import com.sedulous.mccrnrccnagar.resonses.TaskType;
import com.sedulous.mccrnrccnagar.resonses.TrainData;

import java.io.Serializable;
import java.util.ArrayList;

public class QanswerData implements Serializable {
    public TaskType taskType=null;
    public TrainData trainData=null;
    public String shift="", trainType="";
    public ArrayList<PageData> pageData_list=new ArrayList<>();
    public ArrayList<TaskQuestion> questions_list=new ArrayList<>();

}
