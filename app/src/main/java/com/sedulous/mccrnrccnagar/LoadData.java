package com.sedulous.mccrnrccnagar;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sedulous.mccrnrccnagar.Model.PenaltyQModel;
import com.sedulous.mccrnrccnagar.resonses.ResponseClass;
import com.sedulous.mccrnrccnagar.resonses.TaskType;
import com.sedulous.mccrnrccnagar.resonses.TrainData;
import com.sedulous.mccrnrccnagar.resonses.TrainTypeModel;
import com.sedulous.mccrnrccnagar.resonses.UserData;
import com.sedulous.mccrnrccnagar.services.Web;
import com.sedulous.mccrnrccnagar.utilities.DB;
import com.sedulous.mccrnrccnagar.utilities.SP;
import com.sedulous.mccrnrccnagar.utilities.VolleySingleton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import static com.sedulous.mccrnrccnagar.OfficerActivity.DESIGNATION_API;
import static com.sedulous.mccrnrccnagar.OfficerActivity.GRADE_API;
import static com.sedulous.mccrnrccnagar.PenalityActivity.getpenaltyques;

public class LoadData {

    Context context;
    ArrayList<TaskType> taskTypeArrayList=new ArrayList<>();
    DB db;
    UserData userData;
    String dep;
    ArrayList<TrainData> train_arrays=new ArrayList<>();

    public LoadData(Context c){
        this.context=c; db=new DB(context);
        userData = (UserData) (new Gson()).fromJson(SP.getUserData(context), UserData.class);
        dep = userData.getDepot_name();

    }
    public void startLoading(){
        if (userData.getUser_type().equalsIgnoreCase("Supervisor")) {
            getTrainTypes();
            getTaskType();
        }
        else if (userData.getUser_type().equalsIgnoreCase("Officer")) {
            getDesignation();
            getGrades();
        }
    }
    public void getTaskType(){
        final ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Loading Task Types...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, Web.TASK_TYPE_LIST_URL,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    Log.e("GetTaskTypeRes", "" + response);
                    if ( !response.toString().isEmpty()) {
                        taskTypeArrayList.clear();
                        ResponseClass responseClass=(new Gson()).fromJson(response.toString(), ResponseClass.class);
                        if (responseClass.getSuccess()==1) {
                            db.insertToResponse("tasktype", response.toString());
                            taskTypeArrayList.addAll(responseClass.getTaskType());
                            getTrainList(taskTypeArrayList,0);
                            getQuestionList(taskTypeArrayList,0);
                            getPenalityQuestions(taskTypeArrayList,0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",""+error);
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void getTrainList(final ArrayList<TaskType> list, final int a){
        final ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Loading trains "+a+"...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        final JSONObject requestObj = new JSONObject();
        try {
            requestObj.put("task_type", list.get(a).getId());
            requestObj.put("depot", dep);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, Web.TRAIN_NUMBER_URL,
                requestObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    Log.w("TRAIN_DETAILS", "" + response);
                    Gson gson=new Gson();
                    ResponseClass responseClass = gson.fromJson(response.toString(), ResponseClass.class);
                    if (responseClass.getSuccess() == 1 && responseClass.getTrains()!=null &&
                            responseClass.getTrains().size()>0) {
                        db.insertToResponse("trainlist_"+dep+"_"+list.get(a).getId(), response.toString());
                        train_arrays.addAll(responseClass.getTrains());
                    }
                } catch (Exception e) {

                }
                if(a<list.size()-1){
                    getTrainList(list, a+1);
                }else{
                    getCoachTypes(train_arrays,0);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(a<list.size()-1){
                    getTrainList(list, a+1);
                }else{
                    getCoachTypes(train_arrays,0);
                }
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void getCoachTypes(final ArrayList<TrainData> trains, final int a){
        final ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Loading coaches of train"+trains.get(a).getTrain_no()+"...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        final JSONObject requestObj = new JSONObject();
        try {
            requestObj.put("train_no", trains.get(a).getTrain_no());
            requestObj.put("train_id", trains.get(a).getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, Web.TRAIN_COACH_URL,
                requestObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    Gson gson = new Gson();
                    ResponseClass responseClass = gson.fromJson(response.toString(), ResponseClass.class);
                    if (responseClass.getSuccess() == 1) {
                        db.insertToResponse("coachtype_"+trains.get(a).getTrain_no()+"_"+trains.get(a).getId(),
                                response.toString());
                    }
                } catch (Exception e) {

                }
                if(a<trains.size()-1){
                    getCoachTypes(trains, a+1);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",""+error);
                if(a<trains.size()-1){
                    getCoachTypes(trains, a+1);
                }
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void getQuestionList( final ArrayList<TaskType> list, final int a){
        final ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Loading questions "+a+"...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        final JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("depot", dep);
            jsonObject.put("task_type", list.get(a).getId());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, Web.QTASK_URL,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    Log.e("TrainTaskRes", "" + response);
                    Gson gson = new Gson();
                    ResponseClass responce = gson.fromJson(response.toString(), ResponseClass.class);
                    if (responce.getSuccess() == 1) {
                        db.insertToResponse("qtask_"+dep+"_"+list.get(a).getId(), response.toString());
                    }
                } catch (Exception e) {
                }
                if(a<list.size()-1){
                    getQuestionList(list, a+1);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(a<list.size()-1){
                    getQuestionList(list, a+1);
                }
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void getPenalityQuestions(final ArrayList<TaskType> list, final int a) {

        final ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Loading Penalties."+a+"..");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        final JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("penalty_id", list.get(a).getId());
            jsonObject.put("depot", dep);
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.e("akm_req_penalty",""+jsonObject);
        final JsonObjectRequest ObjectRequest = new JsonObjectRequest(Request.Method.POST,
                getpenaltyques, jsonObject, new Response.Listener<JSONObject >() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("akm_res_penalty",""+response);
                try {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    PenaltyQModel penaltyQModel=null;
                    if(response.getInt("success")==1) {
                        penaltyQModel = new Gson().fromJson(response.toString(), PenaltyQModel.class);
                        db.insertToResponse("qpenalty_"+dep+"_"+list.get(a).getId(), response.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(a<list.size()-1){
                    getPenalityQuestions(list, a+1);
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError error) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                if(a<list.size()-1){
                    getPenalityQuestions(list, a+1);
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(ObjectRequest);
    }

    public void getTrainTypes(){
        final ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Train types...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        Log.e("res train type", "rech");
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, Web.TRAIN_TYPES_URL,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    Gson gson = new Gson();
                    TrainTypeModel trainTypeModel = gson.fromJson(response.toString(), TrainTypeModel.class);
                    if (trainTypeModel.success == 1 && trainTypeModel.types!=null && trainTypeModel.types.size()>0) {
                        db.insertToResponse("traintypes", response.toString());
                    }

                } catch (Exception e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",""+error);
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void getGrades() {
        final ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Loading Grades...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, GRADE_API, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (mProgressDialog != null) {
                                mProgressDialog.dismiss();
                            }
                            //  Toast.makeText(SuperVisorFeedback.this, "success", Toast.LENGTH_SHORT).show();
                            Log.e("response", response.toString());
                            JSONArray jsonArray=response.getJSONArray("GetGrade");
                            db.insertToResponse("officer_grades", response.toString());
                        } catch (JSONException e) {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error", ""+error.toString());
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                        }
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(objectRequest);
    }

    private void getDesignation() {
        final ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Loading Designation...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, DESIGNATION_API, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (mProgressDialog != null) {
                                mProgressDialog.dismiss();
                            }
                            //Toast.makeText(SuperVisorFeedback.this, "success", Toast.LENGTH_SHORT).show();
                            Log.e("response", response.toString());
                            JSONArray jsonArray=response.getJSONArray("GetDesignation");
                            db.insertToResponse("officer_designation", response.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error", ""+error.toString());
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                        }
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(objectRequest);
    }


}


