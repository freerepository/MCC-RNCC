package com.sedulous.mccrnrccnagar.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import static android.content.Context.MODE_PRIVATE;

public class SP {
    public static void setUserData(Context context,String uid){
        SharedPreferences pref=context.getSharedPreferences("app", MODE_PRIVATE);
        pref.edit().putString("userData", uid).apply();
    }

    public static String getUserData(Context context){
        SharedPreferences pref=context.getSharedPreferences("app", MODE_PRIVATE);
        return pref.getString("userData","");
    }
//    public static SharedPreferences getPref(Context context){
//        return context.getSharedPreferences("spdb", MODE_PRIVATE);
//    }
//    public static SharedPreferences.Editor getEditor(Context context){
//        return getPref(context).edit();
//    }
//    public static void saveTaskTypes(Context context, String values){
//        SharedPreferences.Editor edt=getEditor(context).putString("tasktype", values);
//        edt.apply();
//    }
//    public static String getTaskTypes(Context context){
//        return getPref(context).getString( "tasktype", "");
//    }
//      public static void clearPref(Context context){
//          getPref(context).edit().clear().apply();
//      }
}
