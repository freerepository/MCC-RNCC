package com.sedulous.mccrnrccnagar.services;

public class Web {


   // public static final String BASE_URL="http://mcc.spjrail.com/api/";
     public static final String BASE_URL="https://mccrncc.projectrailway.in/api/";

    public static final String LOGIN_URL = BASE_URL+"login";
    public static final String TASK_TYPE_LIST_URL = BASE_URL+"get_task_type";

    public static final String TRAIN_COACH_URL = BASE_URL+"get_train_coach/";
    public static final String TRAIN_TYPES_URL = BASE_URL+"get_train_type/";
    public static final String TRAIN_NUMBER_URL = BASE_URL+"get_train_number/";
    public static final String QTASK_URL = BASE_URL+"get_train_task/";

    HttpResponceClass httpResponseClass=new HttpResponceClass();


}
