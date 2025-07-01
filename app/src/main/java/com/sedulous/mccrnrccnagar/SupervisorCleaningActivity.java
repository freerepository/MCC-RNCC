package com.sedulous.mccrnrccnagar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sedulous.mccrnrccnagar.Model.PageData;
import com.sedulous.mccrnrccnagar.Model.QanswerData;
import com.sedulous.mccrnrccnagar.resonses.ResponseClass;
import com.sedulous.mccrnrccnagar.resonses.TaskQuestion;
import com.sedulous.mccrnrccnagar.resonses.TaskType;
import com.sedulous.mccrnrccnagar.resonses.TrainCoach;
import com.sedulous.mccrnrccnagar.resonses.TrainData;
import com.sedulous.mccrnrccnagar.resonses.TrainTypeModel;
import com.sedulous.mccrnrccnagar.resonses.UserData;
import com.sedulous.mccrnrccnagar.services.Web;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.sedulous.mccrnrccnagar.utilities.DB;
import com.sedulous.mccrnrccnagar.utilities.O;
import com.sedulous.mccrnrccnagar.utilities.SP;
import com.sedulous.mccrnrccnagar.utilities.VolleySingleton;
import com.whygraphics.multilineradiogroup.MultiLineRadioGroup;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class SupervisorCleaningActivity extends Activity {

    Button btnNext;
    public TextView toolbarTitle, tvTotalCoaches, tvTodayLoad, tvCompleted;
    EditText etCoachNo;
    SwipeRefreshLayout srl;
    RecyclerView recyclerView;
    TextInputLayout input;
    LinearLayout coachNameLayout, loadLayout, totalCoachLoadCompleteLayout;
    RelativeLayout shift_name_layout, header;
    Spinner spShift, spTrainNo, spTrainType, spCoachName;;

    private ArrayList<TrainCoach> coachList;
    private ArrayList<TaskQuestion> questionList = new ArrayList<>();
    private ArrayList<TrainData> trainList = new ArrayList<>();
    private ArrayList<String> trainNoList = new ArrayList<>();
    private ArrayList<String> trainTypeList = new ArrayList<>();
    private ArrayList<String> coachNameList = new ArrayList<>();

    private ArrayAdapter<String> trainNoAdapter;
    private ArrayAdapter<String> trainTypeAdapter;
    private ArrayAdapter<String> coachNameAdapter;
    QanswerData qanswerData=new QanswerData();

    public TaskType taskType;
    public static int load=0;
    int typing = 0;
    public static int selectedText = 1, maxCoach, n_load_page=0;
    private static final int NEXT_REQ = 1000;
    String dep , shiftname = "";
    public String selectedTrain, selectedTrainType = "", selectedCoachNo,
            selectedCoachName, totalload, taskId;
    TrainData selectedTrainData=null;
    String shift[] = {"Select Shift", "A", "B", "C"};
    ProgressDialog mProgressDialog;
    SupRatingAdapter adapter;
    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sup_cleaning);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        db=new DB(SupervisorCleaningActivity.this);
        shift_name_layout = findViewById(R.id.shift_name_layout);
        header = findViewById(R.id.header);
        spTrainNo = (Spinner) findViewById(R.id.sp_trainNo);
        spTrainType = (Spinner) findViewById(R.id.sp_trainType);
        spCoachName = (Spinner) findViewById(R.id.sp_coachName);
        spShift = findViewById(R.id.shifttype);
        etCoachNo = (EditText) findViewById(R.id.et_coachNo);
        toolbarTitle = (TextView) findViewById(R.id.tv_title);
        tvTotalCoaches = (TextView) findViewById(R.id.tv_total_coach);
        tvTodayLoad = (TextView) findViewById(R.id.tv_today_load);
        tvCompleted = (TextView) findViewById(R.id.tv_completed);
        recyclerView = (RecyclerView) findViewById(R.id.rv_rating);
        srl=findViewById(R.id.srl);
        loadLayout = (LinearLayout) findViewById(R.id.load_layout);
        coachNameLayout = (LinearLayout) findViewById(R.id.coach_name_layout);
        btnNext = (Button) findViewById(R.id.btn_next);
        totalCoachLoadCompleteLayout = (LinearLayout) findViewById(R.id.v_coach_load_detail_layout);
        totalCoachLoadCompleteLayout.setVisibility(View.GONE);
        taskType = (TaskType) getIntent().getSerializableExtra("data");
        UserData userData = (UserData) (new Gson()).fromJson(SP.getUserData(SupervisorCleaningActivity.this), UserData.class);
        qanswerData.taskType=taskType;
        dep = userData.getDepot_name();
        toolbarTitle.setText(taskType.getTask_type());
        taskId = taskType.getId();

        if (taskId.equalsIgnoreCase("11") || taskId.equalsIgnoreCase("13") || taskId.equalsIgnoreCase("15")) {
            shift_name_layout.setVisibility(View.GONE);
        }else if (taskId.equalsIgnoreCase("12") || taskId.equalsIgnoreCase("14")) {
            shift_name_layout.setVisibility(View.VISIBLE);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, shift);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            spShift.setAdapter(spinnerArrayAdapter);
            spShift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int myPosition, long myID) {
                    if(myPosition>0) {
                        shiftname=shift[myPosition];
                    } else {
                        shiftname="";
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) { }
            });
        }
        if ( taskId.equalsIgnoreCase("14")) {
            header.setVisibility(View.GONE);
        }

        trainNoList.add(0, "Train No.");
        trainNoAdapter = new ArrayAdapter<>(SupervisorCleaningActivity.this, android.R.layout.simple_spinner_item, android.R.id.text1, trainNoList);
        trainNoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTrainNo.setAdapter(trainNoAdapter);

        trainTypeList.add(0, "Train Type.");
        trainTypeAdapter = new ArrayAdapter<String>(SupervisorCleaningActivity.this, android.R.layout.simple_spinner_item, android.R.id.text1, trainTypeList);
        trainTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTrainType.setAdapter(trainTypeAdapter);

        coachNameList.add(0, "Select Coach.");
        coachNameAdapter = new ArrayAdapter<String>(SupervisorCleaningActivity.this, android.R.layout.simple_spinner_item, android.R.id.text1, coachNameList);
        coachNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spCoachName.setAdapter(coachNameAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new SupRatingAdapter(SupervisorCleaningActivity.this, questionList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        spTrainNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedTrain = spTrainNo.getSelectedItem().toString();
                    coachNameList.clear();
                    coachNameList.add(0, "Select Coach.");
                    coachNameAdapter.notifyDataSetChanged();
                    getCoachTypes(selectedTrain, trainList.get(position-1).getId());
                    for (int i = 0; i < trainNoList.size(); i++) {
                        try {
                            if (trainList.get(i).getTrain_no().equalsIgnoreCase(selectedTrain)) {
                                selectedTrainData = trainList.get(i);
                                maxCoach = Integer.parseInt(trainList.get(i).getTot_coach());
                                if (!taskId.equalsIgnoreCase("14"))
                                openPopUp();

                                break;
                            }
                        }catch (ArrayIndexOutOfBoundsException e){
                            e.printStackTrace();
                        }
                    }
                } else {
                    selectedTrain = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        spTrainType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    selectedTrainType = spTrainType.getSelectedItem().toString();
                } else {
                    selectedTrainType ="";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spCoachName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    selectedCoachName = coachNameList.get(i);
                }else{
                    selectedCoachName ="";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedCoachNo = etCoachNo.getText().toString().trim();
                validateRating();

            }
        });

        if (taskId.equalsIgnoreCase("11") || taskId.equalsIgnoreCase("12") ||
                taskId.equalsIgnoreCase("13") || taskId.equalsIgnoreCase("15")) {
            if (O.checkNetwork(SupervisorCleaningActivity.this)) {
                getTrainList(dep, taskId);
                getTrainTypes();
            } else {
                loadOfflineTrains("trainlist_"+dep+"_"+taskId);
                loadOfflineTrainTypes();
            }
        }else{
            qanswerData.pageData_list.clear();
            qanswerData.pageData_list.add(new PageData());

            if (O.checkNetwork(SupervisorCleaningActivity.this)) {
                getQuestionList(dep, taskId);
            } else {
                loadOfflineQTaskList("qtask_"+dep+"_"+taskId);
            }
        }
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                    if (taskId.equalsIgnoreCase("11") || taskId.equalsIgnoreCase("12") ||
                            taskId.equalsIgnoreCase("13") || taskId.equalsIgnoreCase("15")) {
                        if(trainTypeList.size()==0 || trainNoList.size()==0 || coachNameList.size()==0){
                            if (O.checkNetwork(SupervisorCleaningActivity.this)) {
                                getTrainList(dep, taskId);
                                getTrainTypes();
                            } else {
                                srl.setRefreshing(false);
                                loadOfflineTrains("trainlist_"+dep+"_"+taskId);
                                loadOfflineTrainTypes();
                            }
                        }else if(load>0 && qanswerData.questions_list.isEmpty()){
                            if (O.checkNetwork(SupervisorCleaningActivity.this)) {
                                getQuestionList(dep, taskId);
                            } else {
                                srl.setRefreshing(false);
                                loadOfflineQTaskList("qtask_"+dep+"_"+taskId);
                            }
                        }else{
                            srl.setRefreshing(false);
                        }
                    }else{
                        if (O.checkNetwork(SupervisorCleaningActivity.this)) {
                            getQuestionList(dep, taskId);
                        } else {
                            srl.setRefreshing(false);
                            loadOfflineQTaskList("qtask_"+dep+"_"+taskId);
                        }
                    }
            }
        });
    }

    private void openPopUp() {

        final Dialog dialog = new Dialog(SupervisorCleaningActivity.this);
        dialog.setContentView(R.layout.input_popup);
        RelativeLayout delete = (RelativeLayout) dialog.findViewById(R.id.delete_btn);
        TextView dialogTextview = (TextView) dialog.findViewById(R.id.popup_text);
        input = (TextInputLayout) dialog.findViewById(R.id.til_today_load);
        input.getEditText().addTextChangedListener(filterTextWatcher);
        ImageButton btnDelete = (ImageButton) dialog.findViewById(R.id.img_btn);
        dialogTextview.setText("Enter Today's Load for Train no. " + selectedTrain);
        Button btnPackage = (Button) dialog.findViewById(R.id.btn_package);
        btnPackage.setText("GO");
        dialog.setCancelable(false);
        btnPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!input.getEditText().getText().toString().isEmpty()) {
                    load = Integer.parseInt(input.getEditText().getText().toString().trim());
                    totalload = input.getEditText().getText().toString().trim();
                    if (load <= maxCoach) {
                        totalCoachLoadCompleteLayout.setVisibility(View.VISIBLE);
                        tvTodayLoad.setText(input.getEditText().getText().toString().trim());
                        tvTotalCoaches.setText(""+maxCoach);
                        //akm
                        if (O.checkNetwork(SupervisorCleaningActivity.this)) {
                            getQuestionList(dep, taskId);
                        } else {
                            loadOfflineQTaskList("qtask_"+dep+"_"+taskId);
                        }
                        qanswerData.pageData_list.clear();
                        for(int n=0; n<load; n++){
                            qanswerData.pageData_list.add(new PageData());
                        }
                        dialog.dismiss();
                    } else {
                        input.setErrorEnabled(true);
                        input.setError("Load should be less than " + maxCoach);
                    }
                } else {
                    input.setErrorEnabled(true);
                    input.setError((Html.fromHtml("<p>&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;Can't be empty!</p>")));
                }
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("IntentData", "" + data);
        try {
            if (resultCode == RESULT_OK && requestCode == NEXT_REQ) {
                try{
                   if(data!=null && data.getSerializableExtra("result")!=null){
                       qanswerData=(QanswerData) data.getSerializableExtra("result");
                   }
                }catch (Exception e){
                    e.printStackTrace();
                }
                n_load_page++;
                etCoachNo.setText("");
                tvCompleted.setText(""+(n_load_page));
                adapter.notifyDataSetChanged();
                recyclerView.getLayoutManager().scrollToPosition(0);
            }
        } catch (Exception e) {
            Log.e("Result_NPExc", "" + e);
        }
    }

    private void validateRating() {
        try {
            if (taskId.equalsIgnoreCase("11") ||
                taskId.equalsIgnoreCase("12") ||
                taskId.equalsIgnoreCase("13") ||
                taskId.equalsIgnoreCase("15")) {

                if (qanswerData.pageData_list.get(n_load_page).answerData.size()!=questionList.size()) {
                    Toast.makeText(SupervisorCleaningActivity.this, "Please rate all questions.!", Toast.LENGTH_SHORT).show();
                } else if (spTrainNo.getSelectedItemPosition() == 0 ) {
                    Toast.makeText(this, "Please select Train no.!", Toast.LENGTH_SHORT).show();
                } else if (spTrainType.getSelectedItemPosition() == 0) {
                    Toast.makeText(this, "Please select Train Type!", Toast.LENGTH_SHORT).show();
                } else if (spCoachName.getSelectedItemPosition() == 0) {
                    Toast.makeText(this, "Please select Coach!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etCoachNo.getText().toString())) {
                    Toast.makeText(this, "Please Enter Coach No. !", Toast.LENGTH_SHORT).show();
                } else if (existCoach(etCoachNo.getText().toString()) ) {
                    Toast.makeText(this, "This coach no. already exist.!", Toast.LENGTH_SHORT).show();
                } else if (taskId.equalsIgnoreCase("12") && spShift.getSelectedItemPosition() == 0) {
                    Toast.makeText(this, "Please Select Shift", Toast.LENGTH_SHORT).show();
                }else {
                        try{
                            spTrainNo.setEnabled(false);
                            spTrainType.setEnabled(false);
                            spShift.setEnabled(false);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        qanswerData.pageData_list.get(n_load_page).coachNo=etCoachNo.getText().toString();
                        qanswerData.pageData_list.get(n_load_page).coachName= selectedCoachName;
                        qanswerData.trainType = selectedTrainType;
                        qanswerData.trainData = selectedTrainData;
                        qanswerData.shift=shiftname;
                        Intent intent = new Intent(SupervisorCleaningActivity.this, TakePhotoActivity.class);
                        intent.putExtra("qdata", qanswerData);
                        intent.putExtra("nload", n_load_page);
                        startActivityForResult(intent, NEXT_REQ);
                }
            } else if (taskId.equalsIgnoreCase("14")) {
                if(qanswerData.pageData_list.get(n_load_page).answerData.size()!=questionList.size()) {
                    Toast.makeText(this, "Please rate all questions.!", Toast.LENGTH_SHORT).show();
                } else if (spShift.getSelectedItemPosition() == 0) {
                    Toast.makeText(this, "Please Select Shift", Toast.LENGTH_SHORT).show();
                }else {
                    qanswerData.shift=shiftname;
                    Intent intent = new Intent(SupervisorCleaningActivity.this, TakePhotoActivity.class);
                    intent.putExtra("qdata", qanswerData);
                    intent.putExtra("nload", n_load_page);
                    startActivity(intent);
              }
            }

        } catch (Exception e) {

        }
    }

    public void getTrainList(final String dep, final String taskId){
        srl.setRefreshing(true);
        final JSONObject requestObj = new JSONObject();
        try {
            requestObj.put("task_type", taskId);
            requestObj.put("depot", dep);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, Web.TRAIN_NUMBER_URL,
                requestObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                        srl.setRefreshing(false);
                        Log.w("TRAIN_DETAILS", "" + response);
                        Gson gson=new Gson();
                        ResponseClass responseClass = gson.fromJson(response.toString(), ResponseClass.class);
                        if (responseClass.getSuccess() == 1 && responseClass.getTrains()!=null &&
                                responseClass.getTrains().size()>0) {
                            db.insertToResponse("trainlist_"+dep+"_"+taskId, response.toString());
                            trainList=responseClass.getTrains();
                            trainNoList.clear();
                            trainNoList.add(0, "Train No.");
                            for (int i = 0; i < responseClass.getTrains().size(); i++) {
                                try {
                                    if (!responseClass.getTrains().get(i).getTrain_no().isEmpty()) {
                                        trainNoList.add(responseClass.getTrains().get(i).getTrain_no());
                                    }
                                } catch (Exception e) {
                                    Log.e("Error", "" + e);
                                }
                            }
                            trainNoAdapter.notifyDataSetChanged();
                        }else{
                            loadOfflineTrains("trainlist_"+dep+"_"+taskId);
                        }
                } catch (Exception e) {
                    Log.e("err trainlist", ""+e);
                    loadOfflineTrains("trainlist_"+dep+"_"+taskId);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",""+error);
                srl.setRefreshing(false);
                loadOfflineTrains("trainlist_"+dep+"_"+taskId);
            }
        });
        VolleySingleton.getInstance(SupervisorCleaningActivity.this).addToRequestQueue(stringRequest);
    }

    public void loadOfflineTrains(String key){
        try {
            srl.setRefreshing(false);
            Gson gson=new Gson();
            ResponseClass responseClass = gson.fromJson(db.getResponseData(key).response, ResponseClass.class);
            if (responseClass.getSuccess() == 1 && responseClass.getTrains()!=null &&
                    responseClass.getTrains().size()>0) {
                trainList=responseClass.getTrains();
                trainNoList.clear();
                trainNoList.add(0, "Train No.");
                for (int i = 0; i < responseClass.getTrains().size(); i++) {
                    try {
                        if (!responseClass.getTrains().get(i).getTrain_no().isEmpty()) {
                            trainNoList.add(responseClass.getTrains().get(i).getTrain_no());
                        }
                    } catch (Exception e) {
                        Log.e("Error", "" + e);
                    }
                }
                trainNoAdapter.notifyDataSetChanged();
            }else{

            }
        } catch (Exception e) {
            Toast.makeText(SupervisorCleaningActivity.this,
                    "There is no train in this cleaning type.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getCoachTypes(final String trainno, final String trainid){
        srl.setRefreshing(true);
        final JSONObject requestObj = new JSONObject();
        try {
            requestObj.put("train_no", trainno);
            requestObj.put("train_id", trainid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, Web.TRAIN_COACH_URL,
                requestObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    srl.setRefreshing(false);
                    Gson gson = new Gson();
                    ResponseClass responseClass = gson.fromJson(response.toString(), ResponseClass.class);
                    if (responseClass.getSuccess() == 1) {
                        db.insertToResponse("coachtype_"+trainno+"_"+trainid, response.toString());
                        coachList = responseClass.getTrainCoaches();
                        coachNameList.clear();
                        coachNameList.add("Select Coach.");
                        for (int i = 0; i < coachList.size(); i++) {
                            coachNameList.add(coachList.get(i).getCoach_name());
                        }
                        coachNameAdapter.notifyDataSetChanged();
                    } else {
                        loadOfflineCoachTypes("coachtype_"+trainno+"_"+trainid);
                    }
                } catch (Exception e) {
                    loadOfflineCoachTypes("coachtype_"+trainno+"_"+trainid);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",""+error);
                srl.setRefreshing(false);
                loadOfflineCoachTypes("coachtype_"+trainno+"_"+trainid);
            }
        });
        VolleySingleton.getInstance(SupervisorCleaningActivity.this).addToRequestQueue(stringRequest);
    }
    public void loadOfflineCoachTypes(String key){
        try {
            srl.setRefreshing(false);
            Gson gson = new Gson();
            ResponseClass responseClass = gson.fromJson(db.getResponseData(key).
                    response.toString(), ResponseClass.class);
            if (responseClass.getSuccess() == 1) {

                coachList = responseClass.getTrainCoaches();
                coachNameList.clear();
                coachNameList.add("Select Coach.");
                for (int i = 0; i < coachList.size(); i++) {
                    coachNameList.add(coachList.get(i).getCoach_name());
                }
                coachNameAdapter.notifyDataSetChanged();
            } else {

            }
        } catch (Exception e) {

        }
    }

    public void getTrainTypes(){
        srl.setRefreshing(true);
        Log.e("res train type", "rech");
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, Web.TRAIN_TYPES_URL,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.e("res train type", ""+response);
                    srl.setRefreshing(false);
                    Gson gson = new Gson();
                    TrainTypeModel trainTypeModel = gson.fromJson(response.toString(), TrainTypeModel.class);
                    if (trainTypeModel.success == 1 && trainTypeModel.types!=null && trainTypeModel.types.size()>0) {
                        db.insertToResponse("traintypes", response.toString());
                        trainTypeList.clear();
                        trainTypeList.add("Train Type.");
                        for (int i = 0; i < trainTypeModel.types.size(); i++) {
                            trainTypeList.add(trainTypeModel.types.get(i).train_type);
                        }
                        trainTypeAdapter.notifyDataSetChanged();
                    }
                    else {
                        loadOfflineTrainTypes();
                    }
                } catch (Exception e) {
                    loadOfflineTrainTypes();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",""+error);
                srl.setRefreshing(false);
                loadOfflineTrainTypes();
            }
        });
        VolleySingleton.getInstance(SupervisorCleaningActivity.this).addToRequestQueue(stringRequest);
    }

    public void loadOfflineTrainTypes(){
        try {
            srl.setRefreshing(false);
            Gson gson = new Gson();
            TrainTypeModel trainTypeModel = gson.fromJson(db.getResponseData("traintypes").response, TrainTypeModel.class);
            if (trainTypeModel.success == 1 && trainTypeModel.types!=null && trainTypeModel.types.size()>0) {
                trainTypeList.clear();
                trainTypeList.add("Train Type.");
                for (int i = 0; i < trainTypeModel.types.size(); i++) {
                    trainTypeList.add(trainTypeModel.types.get(i).train_type);
                }
                trainTypeAdapter.notifyDataSetChanged();
            }
            else {

            }
        } catch (Exception e) {

        }
    }

    public void getQuestionList(final String dep, final String taskId){
        srl.setRefreshing(true);
        final JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("depot", dep);
            jsonObject.put("task_type", taskId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, Web.QTASK_URL,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                srl.setRefreshing(false);
                try {
                    Log.e("TrainTaskRes", "" + response);
                    Gson gson = new Gson();
                    ResponseClass responce = gson.fromJson(response.toString(), ResponseClass.class);
                    if (responce.getSuccess() == 1) {
                        db.insertToResponse("qtask_"+dep+"_"+taskId, response.toString());
                        btnNext.setVisibility(View.VISIBLE);
                        questionList = responce.getTaskQuestionArrayList();
                        qanswerData.questions_list=questionList;
                        adapter.questionArrayList=questionList;
                        adapter.notifyDataSetChanged();
                    } else {
                        loadOfflineQTaskList("qtask_"+dep+"_"+taskId);
                    }
                } catch (Exception e) {
                    loadOfflineQTaskList("qtask_"+dep+"_"+taskId);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",""+error);
                srl.setRefreshing(false);
                loadOfflineQTaskList("qtask_"+dep+"_"+taskId);
            }
        });
        VolleySingleton.getInstance(SupervisorCleaningActivity.this).addToRequestQueue(stringRequest);
    }
    public void loadOfflineQTaskList(String key){
        srl.setRefreshing(false);
        try {
            Gson gson = new Gson();
            ResponseClass responce = gson.fromJson(db.getResponseData(key).response, ResponseClass.class);
            if (responce.getSuccess() == 1) {

                btnNext.setVisibility(View.VISIBLE);
                questionList = responce.getTaskQuestionArrayList();
                qanswerData.questions_list=questionList;
                adapter.questionArrayList=questionList;
                adapter.notifyDataSetChanged();
            } else {

            }
        } catch (Exception e) {

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(SupervisorCleaningActivity.this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        backAlert();
    }

    private void backAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setCancelable(false);
        builder.setMessage("Leaving this page before completion will discard all the progress" +
                "\n Do you want to exit?")
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {

                        Intent i = new Intent(SupervisorCleaningActivity.this, HomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public TextWatcher filterTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (count > 0) {
                typing = 1;
            }
            Log.w("watchText", "" + s + " " + start + " " + before + " " + count);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (input.isErrorEnabled()) {
                input.setErrorEnabled(false);
                input.getEditText().setBackgroundResource(R.drawable.shape_outline);
                input.getEditText().setPadding(0, 10, 0, 10);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean existCoach(String coach_no){
        for (int a=0; a< qanswerData.pageData_list.size(); a++) {
            if(a<n_load_page) {
                if (qanswerData.pageData_list.get(a).coachNo.equalsIgnoreCase(coach_no))
                    return true;
            }
        }
        return false;
    }

    public class SupRatingAdapter extends RecyclerView.Adapter<SupRatingAdapter.MyViewHolder> {

        private Context mcontext;
        public ArrayList<TaskQuestion> questionArrayList;

        public SupRatingAdapter(SupervisorCleaningActivity trainDetails, ArrayList<TaskQuestion> questionList) {
            this.mcontext=trainDetails;
            this.questionArrayList =questionList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suprating_q,parent,false);
            MyViewHolder vh=new MyViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(final SupRatingAdapter.MyViewHolder holder,  int pos) {
            final int position=pos;
            holder.setIsRecyclable(false);
            holder.text.setText(questionArrayList.get(position).getQuest());

            if(questionArrayList.get(position).getType().equalsIgnoreCase("2")) {
                holder.et_shortfall.setVisibility(View.VISIBLE);
                holder.radioGroup.setVisibility(View.GONE);
            }else if(questionArrayList.get(position).getType().equalsIgnoreCase("1")) {
                holder.et_shortfall.setVisibility(View.GONE);
                holder.radioGroup.setVisibility(View.VISIBLE);
            }

            holder.et_shortfall.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                @Override
                public void afterTextChanged(Editable s) {
                    holder.et_shortfall.requestFocus(holder.et_shortfall.getText().length());
                    try {
                        if (!holder.et_shortfall.getText().toString().trim().isEmpty()) {
                            qanswerData.pageData_list.get(n_load_page).answerData.put(questionArrayList.get(position).getId(),holder.et_shortfall.getText().toString().trim() );
                        } else {
                            qanswerData.pageData_list.get(n_load_page).answerData.remove(questionArrayList.get(position).getId() );
                        }
                    }catch (Exception e){ }
                }
            });

            holder.radioGroup.setSelected(false);

            try {
                if (!questionArrayList.get(position).getScore1().trim().isEmpty()) {
                    holder.radioGroup.addButtons(questionArrayList.get(position).getScore1().trim());
                }
                if (!questionArrayList.get(position).getScore2().trim().isEmpty()) {
                    holder.radioGroup.addButtons(questionArrayList.get(position).getScore2().trim());
                }
                if (!questionArrayList.get(position).getScore3().trim().isEmpty()) {
                    holder.radioGroup.addButtons(questionArrayList.get(position).getScore3().trim());
                }
                if (!questionArrayList.get(position).getScore4().trim().isEmpty()) {
                    holder.radioGroup.addButtons(questionArrayList.get(position).getScore4().trim());
                }
                if (!questionArrayList.get(position).getScore5().trim().isEmpty()) {
                    holder.radioGroup.addButtons(questionArrayList.get(position).getScore5().trim());
                }

            }catch (Exception e){
                Log.e("NP_Exception_adapter",""+e);
            }

            String rb_hvalue=qanswerData.pageData_list.get(n_load_page).answerData.get(questionArrayList.get(position).getId());
            try {
                if(!TextUtils.isEmpty(rb_hvalue)){
                    holder.et_shortfall.setText(rb_hvalue);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            try {
                holder.radioGroup.check(rb_hvalue);
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.radioGroup.setOnCheckedChangeListener(new MultiLineRadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(ViewGroup group, RadioButton button) {
                    if (button==null) {
                        Toast.makeText(mcontext, "Please select answer", Toast.LENGTH_LONG).show();
                    } else {
                        String selection = (String) button.getText();
                        try {

                            qanswerData.pageData_list.get(n_load_page).answerData.
                                    put(questionArrayList.get(position).getId(), selection);
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                        }
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return questionArrayList.size();
        }
        @Override
        public int getItemViewType(int position) {
            return position;
        }
        public class MyViewHolder extends RecyclerView.ViewHolder  {
            TextView text;
            MultiLineRadioGroup radioGroup;
            EditText et_shortfall;

            public MyViewHolder(View itemView) {
                super(itemView);
                text=(TextView)itemView.findViewById(R.id.tv_question);
                radioGroup= (MultiLineRadioGroup) itemView.findViewById(R.id.rg_options);
                et_shortfall=(EditText)itemView.findViewById(R.id.et_shortfall);
            }
        }
    }
    protected void showLoading(@NonNull String message0) {
        mProgressDialog = new ProgressDialog(SupervisorCleaningActivity.this);
        mProgressDialog.setMessage(message0);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }
    protected void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
