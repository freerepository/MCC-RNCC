package com.sedulous.mccrnrccnagar.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sedulous.mccrnrccnagar.Model.PendingData;
import com.sedulous.mccrnrccnagar.Model.PicsData;
import com.sedulous.mccrnrccnagar.Model.ResponseData;

import java.util.ArrayList;

public class DB {

    private static final String DATABASE_NAME = "dball";    // Database Name
    private static final int DATABASE_Version = 1;    // Database Version
    public static String ID="id", KEYID ="keyid", RESPONSE="response", REQUEST="request",METHOD="method",
            URL="url", UPDATE_TIME="update_time", ACCESS_TIME="access_time", INSERT_TIME="insert_time",
            ACTIVITY="activity", REPORT="report", PENALTY="penalty",OFFICER="officer",
            TRAINNO="trainno",TASKID="taskid", PICS="pics", FILEPATH="filepath", UPLOAD_STATUS="upload_status", CREATED_TIME="created_time";
    String tag="MyDataBase", TABLE_RESPONSE="table_response",
            TABLE_PICS="table_pics", TABLE_PENDING="table_pending";
    private String CREATE_TABLE_PENDING = String.format(" Create Table IF NOT EXISTS %s (%s integer PRIMARY KEY AUTOINCREMENT,  " +
                    "%s TEXT NOT NULL,%s TEXT NOT NULL, %s TEXT, %s TEXT, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL )",
            TABLE_PENDING, ID, TASKID, ACTIVITY, TRAINNO, REQUEST, METHOD, URL, PICS ,UPLOAD_STATUS ,ACCESS_TIME, INSERT_TIME);
    private String CREATE_TABLE_RESPONSE = String.format(" Create Table IF NOT EXISTS %s (%s integer PRIMARY KEY AUTOINCREMENT,  " +
                    "%s TEXT NOT NULL,%s TEXT NOT NULL,%s TEXT NOT NULL )",
            TABLE_RESPONSE, ID, KEYID, RESPONSE, UPDATE_TIME );
    private String CREATE_TABLE_PICS = String.format(" Create Table IF NOT EXISTS %s (%s integer PRIMARY KEY AUTOINCREMENT,  " +
                    "%s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL )",
            TABLE_PICS, ID, KEYID, FILEPATH, URL, UPLOAD_STATUS, CREATED_TIME );

    SQLiteDatabase db;
    Context context;

    public DB(Context context) {
        this.context = context;
        try {
            db = new MyDbHelper(context).getWritableDatabase();
        } catch (SQLiteCantOpenDatabaseException s) {
            s.printStackTrace();
        }
    }

//    public int countAttendance(String user_id, String MMyy){
//        int n=0;
//        n = db.rawQuery("SELECT id FROM " + TABLE_Attendance + " WHERE "+O.USER_ID +" = ? AND "+O.CREATED_TIME +" like ? ", new String[]{user_id, "%"+MMyy+"%"}).getCount();
//        return  n;
//    }

    public long insertToPending(String taskid, String act, String trainno, String request,
                                String method,String url, String pics, String upload_status,String accesstime ) {
        long n = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TASKID, taskid);
            contentValues.put(ACTIVITY, act);
            contentValues.put(TRAINNO, trainno);
            contentValues.put(PICS, pics);
            contentValues.put(UPLOAD_STATUS, upload_status);
            contentValues.put(REQUEST, request);
            contentValues.put(METHOD, method);
            contentValues.put(URL, url);
            contentValues.put(ACCESS_TIME, accesstime);
            contentValues.put(INSERT_TIME, O.getDateTime());
            n = db.insert(TABLE_PENDING, null, contentValues);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return n;
    }

    public long updatePendingStatus(String id, String status) {
        long n = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(UPLOAD_STATUS, status);
            n = db.update(TABLE_PENDING, contentValues, ID+" = ?", new String[]{id});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return n;
    }
    public PendingData getPendingData(String id) {
        PendingData pendingData=null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_PENDING + " WHERE "+ID+" = ?", new String[]{ id });
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                pendingData=new PendingData();
                pendingData.setID(String.valueOf(cursor.getInt(cursor.getColumnIndex(ID))));
                pendingData.setTaskID(cursor.getString(cursor.getColumnIndex(TASKID)));
                pendingData.setActivity(cursor.getString(cursor.getColumnIndex(ACTIVITY)));
                pendingData.setTrainNo(cursor.getString(cursor.getColumnIndex(TRAINNO)));
                pendingData.setRequest(cursor.getString(cursor.getColumnIndex(REQUEST)));
                pendingData.setMethod(cursor.getString(cursor.getColumnIndex(METHOD)));
                pendingData.setUrl(cursor.getString(cursor.getColumnIndex(URL)));
                pendingData.setPics(cursor.getString(cursor.getColumnIndex(PICS)));
                pendingData.setUploadStatus(cursor.getString(cursor.getColumnIndex(UPLOAD_STATUS)));
                pendingData.setAccessTime(cursor.getString(cursor.getColumnIndex(ACCESS_TIME)));
                pendingData.setInsertTime(cursor.getString(cursor.getColumnIndex(INSERT_TIME)));

            }
            if(cursor!=null && !cursor.isClosed()) cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pendingData;
    }

    public ArrayList<PendingData> getAllPendingData() {
        ArrayList<PendingData> datalist = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_PENDING, new String[]{});
            if (cursor != null && cursor.moveToLast()) {
                do {
                    PendingData pendingData=new PendingData();
                    pendingData.setID(String.valueOf(cursor.getInt(cursor.getColumnIndex(ID))));
                    pendingData.setTaskID(cursor.getString(cursor.getColumnIndex(TASKID)));
                    pendingData.setActivity(cursor.getString(cursor.getColumnIndex(ACTIVITY)));
                    pendingData.setTrainNo(cursor.getString(cursor.getColumnIndex(TRAINNO)));
                    pendingData.setRequest(cursor.getString(cursor.getColumnIndex(REQUEST)));
                    pendingData.setMethod(cursor.getString(cursor.getColumnIndex(METHOD)));
                    pendingData.setUrl(cursor.getString(cursor.getColumnIndex(URL)));
                    pendingData.setPics(cursor.getString(cursor.getColumnIndex(PICS)));
                    pendingData.setUploadStatus(cursor.getString(cursor.getColumnIndex(UPLOAD_STATUS)));
                    pendingData.setAccessTime(cursor.getString(cursor.getColumnIndex(ACCESS_TIME)));
                    pendingData.setInsertTime(cursor.getString(cursor.getColumnIndex(INSERT_TIME)));
                    datalist.add(pendingData);
                }
                while (cursor.moveToPrevious());
            }
            if(cursor!=null && !cursor.isClosed()) cursor.close();
        } catch (Exception e) {
            Log.e(tag, "" + e);
        }
        return datalist;
    }

    public int deletePendingData(String id) {
        try {
            return db.delete(TABLE_PENDING, ID+ " = ?", new String[]{id});
        } catch (Exception e) {
            Log.e(tag, "" + e);
        }
        return 0;
    }
    ////////////////////////////////////////////////
    public long insertToResponse(String keyid, String response ) {
        long n = 0;
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RESPONSE +" WHERE "+ KEYID + " = ? ", new String[]{keyid});
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEYID, keyid);
            contentValues.put(RESPONSE, response);
            contentValues.put(UPDATE_TIME, O.getDateTime());
            if (cursor.getCount() > 0) {
                n = db.update(TABLE_RESPONSE, contentValues, KEYID +" = ?", new String[]{keyid});
            } else {
                n = db.insert(TABLE_RESPONSE, null, contentValues);
            }
            if(!cursor.isClosed()) cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return n;
    }

    public ResponseData getResponseData(String keyid) {
        ResponseData responseData=null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_RESPONSE + " WHERE "+KEYID+" = ?", new String[]{ keyid });
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                responseData=new ResponseData();
                responseData.setID(String.valueOf(cursor.getInt(cursor.getColumnIndex(ID))));
                responseData.setKEYID(cursor.getString(cursor.getColumnIndex(KEYID)));
                responseData.setResponse(cursor.getString(cursor.getColumnIndex(RESPONSE)));
                responseData.setUpdateTime(cursor.getString(cursor.getColumnIndex(UPDATE_TIME)));

            }
            if(cursor!=null && !cursor.isClosed()) cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseData;
    }

    public ArrayList<ResponseData> getAllResponse() {
        ArrayList<ResponseData> datalist = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_RESPONSE, new String[]{});
            if (cursor != null && cursor.moveToLast()) {
                do {
                    ResponseData responseData=new ResponseData();
                    responseData=new ResponseData();
                    responseData.setID(String.valueOf(cursor.getInt(cursor.getColumnIndex(ID))));
                    responseData.setKEYID(cursor.getString(cursor.getColumnIndex(KEYID)));
                    responseData.setResponse(cursor.getString(cursor.getColumnIndex(RESPONSE)));
                    responseData.setUpdateTime(cursor.getString(cursor.getColumnIndex(UPDATE_TIME)));
                    datalist.add(responseData);
                }
                while (cursor.moveToPrevious());
            }
            if(cursor!=null && !cursor.isClosed()) cursor.close();
        } catch (Exception e) {
            Log.e(tag, "" + e);
        }
        return datalist;
    }

    public int deleteResponse(String keyid) {
        try {
            return db.delete(TABLE_RESPONSE, KEYID+ " = ?", new String[]{keyid});
        } catch (Exception e) {
            Log.e(tag, "" + e);
        }
        return 0;
    }
    ///////////////////////////////////////////////

    public long insertToPic(String keyid, String filepath, String url, String uploadstatus ) {
        long n = 0;
        try {
            //ID, KEYID, FILEPATH, URL, UPLOAD_STATUS, CREATED_TIME
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PICS +" WHERE "+ KEYID + " = ? ", new String[]{keyid});
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEYID, keyid);
            contentValues.put(FILEPATH, filepath);
            contentValues.put(URL, url);
            contentValues.put(UPLOAD_STATUS, uploadstatus);
            contentValues.put(CREATED_TIME, O.getDateTime());
            if (cursor.getCount() > 0) {
                n = db.update(TABLE_PICS, contentValues, KEYID +" = ?", new String[]{keyid});
            } else {
                n = db.insert(TABLE_PICS, null, contentValues);
            }
            if(!cursor.isClosed()) cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return n;
    }

    public PicsData getPicsData(String keyid) {
        PicsData picsData=null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_PICS + " WHERE "+KEYID+" = ?", new String[]{ keyid });
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                picsData=new PicsData();
                picsData.setID(String.valueOf(cursor.getInt(cursor.getColumnIndex(ID))));
                picsData.setKEYID(cursor.getString(cursor.getColumnIndex(KEYID)));
                picsData.setFilePath(cursor.getString(cursor.getColumnIndex(FILEPATH)));
                picsData.setUrl(cursor.getString(cursor.getColumnIndex(URL)));
                picsData.setUploadStatus(cursor.getString(cursor.getColumnIndex(UPLOAD_STATUS)));
                picsData.setCreatedTime(cursor.getString(cursor.getColumnIndex(CREATED_TIME)));

            }
            if(cursor!=null && !cursor.isClosed()) cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return picsData;
    }

    public ArrayList<PicsData> getAllPics() {
        ArrayList<PicsData> datalist = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_PICS, new String[]{});
            if (cursor != null && cursor.moveToLast()) {
                do {
                    PicsData picsData=new PicsData();
                    picsData.setID(String.valueOf(cursor.getInt(cursor.getColumnIndex(ID))));
                    picsData.setKEYID(cursor.getString(cursor.getColumnIndex(KEYID)));
                    picsData.setFilePath(cursor.getString(cursor.getColumnIndex(FILEPATH)));
                    picsData.setUrl(cursor.getString(cursor.getColumnIndex(URL)));
                    picsData.setUploadStatus(cursor.getString(cursor.getColumnIndex(UPLOAD_STATUS)));
                    picsData.setCreatedTime(cursor.getString(cursor.getColumnIndex(CREATED_TIME)));

                }
                while (cursor.moveToPrevious());
            }
            if(cursor!=null && !cursor.isClosed()) cursor.close();
        } catch (Exception e) {
            Log.e(tag, "" + e);
        }
        return datalist;
    }

    public int deletePics(String keyid) {
        try {
            return db.delete(TABLE_PICS, KEYID+ " = ?", new String[]{keyid});
        } catch (Exception e) {
            Log.e(tag, "" + e);
        }
        return 0;
    }

    /////////////////////////////////////////////////////
//    public long updateUser(String user_id, String name, String utype, String login_method,
//                           String password, String phone, String email, String address ) {
//        long n = 0;
//        try {
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(O.USER_NAME, name);
//            contentValues.put(O.USER_TYPE, utype);
//            contentValues.put(O.LOGIN_METHOD, login_method);
//            contentValues.put(O.PASSWORD, password);
//            contentValues.put(O.PHONE, phone);
//            contentValues.put(O.EMAIL, email);
//            contentValues.put(O.ADDRESS, address);
//            n = db.update(TABLE_USER, contentValues, O.USER_ID+" = ?", new String[]{user_id});
//        } catch (Exception e) {
//            Log.e("DB","problem inserting profile " + e);
//        }
//        return n;
//    }

     // SQLiteOpenHelper child class to create database , table


    private class MyDbHelper extends SQLiteOpenHelper {
        private Context context;

        public MyDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context = context;
        }
        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(CREATE_TABLE_RESPONSE);
            db.execSQL(CREATE_TABLE_PENDING);
            db.execSQL(CREATE_TABLE_PICS);

        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
