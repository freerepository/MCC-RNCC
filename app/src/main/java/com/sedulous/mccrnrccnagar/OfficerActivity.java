package com.sedulous.mccrnrccnagar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sedulous.mccrnrccnagar.Model.ApkModel;
import com.sedulous.mccrnrccnagar.Model.JResponse;
import com.sedulous.mccrnrccnagar.resonses.UserData;
import com.google.gson.Gson;
import com.sedulous.mccrnrccnagar.utilities.DB;
import com.sedulous.mccrnrccnagar.utilities.O;
import com.sedulous.mccrnrccnagar.utilities.SP;
import com.sedulous.mccrnrccnagar.utilities.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.sedulous.mccrnrccnagar.HomeActivity.APK_API;

public class OfficerActivity extends Activity implements RadioGroup.OnCheckedChangeListener {
    public final static String submitofficer = "https://mccrncc.projectrailway.in/api/save_officer_report";
    public final static String DESIGNATION_API = "https://mccrncc.projectrailway.in/api/officerDesignation";
    public final static String GRADE_API = "https://mccrncc.projectrailway.in/api/officerGrade";
    Spinner sp_designation, sp_grade;
    EditText et_train, et_load;
    RadioGroup radioGroup;
    RadioButton r1, r2, r3, r4;
    EditText remark, amount;
    Button submit;
    ArrayList<String> designation_list = new ArrayList<>();
    ArrayList<String> grade_list = new ArrayList<>();
    ArrayAdapter<String> adapter_designation, adapter_grade;

    String designation, grade;
    TextView tvUserType, tvUsername;
    String s_amount, s_remark, requestBody, selection, dep;
    UserData userData;
    ProgressDialog mProgressDialog;
    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer);

        tvUserType = findViewById(R.id.tvUserType);
        tvUsername = findViewById(R.id.tvUserName);
        userData = (UserData) (new Gson()).fromJson(SP.getUserData(OfficerActivity.this), UserData.class);
        dep = userData.getDepot_name();
        db = new DB(OfficerActivity.this);
        sp_designation = findViewById(R.id.designation);
        sp_grade = findViewById(R.id.grade);
        et_train = findViewById(R.id.et_trainNo);
        et_load = findViewById(R.id.et_load);
        radioGroup = findViewById(R.id.Radio_group);
        r1 = findViewById(R.id.rb1);
        r2 = findViewById(R.id.rb2);
        r3 = findViewById(R.id.rb3);
        r4 = findViewById(R.id.rb4);
        tvUserType.setText(userData.getUser_type());
        tvUsername.setText(userData.getName());
        amount = findViewById(R.id.amount);
        remark = findViewById(R.id.remark);
        submit = findViewById(R.id.rating);
        radioGroup.setOnCheckedChangeListener(this);

        designation_list.add("Select Your Designation");
        adapter_designation = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, designation_list);
        adapter_designation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        sp_designation.setAdapter(adapter_designation);
        sp_designation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int myPosition, long myID) {
                if (myPosition > 0) {
                    designation = designation_list.get(myPosition);
                } else {
                    designation = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        grade_list.add("Select Your Grade");
        adapter_grade = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, grade_list);
        adapter_grade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        sp_grade.setAdapter(adapter_grade);
        sp_grade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int myPosition, long myID) {
                if (myPosition > 0) {
                    grade = grade_list.get(myPosition);
                } else {
                    grade = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                final String train_no;
                int load_no = 0;
                train_no = et_train.getText().toString();
                try {
                    load_no = Integer.parseInt(et_load.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    s_amount = amount.getText().toString();
                    s_remark = remark.getText().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (TextUtils.isEmpty(train_no)) {
                    Toast.makeText(OfficerActivity.this, "Enter train No", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(et_load.getText().toString())) {
                    Toast.makeText(OfficerActivity.this, "Enter load No", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(designation)) {
                    Toast.makeText(OfficerActivity.this, "Select Your Designation", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(grade)) {
                    Toast.makeText(OfficerActivity.this, "Select Your Grade", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(s_amount)) {
                    Toast.makeText(OfficerActivity.this, "Enter Amount", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(s_remark)) {
                    Toast.makeText(OfficerActivity.this, "Enter Remark", Toast.LENGTH_SHORT).show();
                } else {
                    submit.setEnabled(false);
                    JSONObject jObject = new JSONObject();
                    try {
                        jObject.put("officer_name", userData.getName());
                        jObject.put("designation", designation);
                        jObject.put("grade", grade);
                        jObject.put("depot", userData.getDepot_name());
                        jObject.put("trainno", train_no);
                        jObject.put("loadno", load_no);
                        jObject.put("taskname", userData.getUser_type());
                        jObject.put("rating", selection);
                        jObject.put("amount", s_amount);
                        jObject.put("remark", s_remark);
                        jObject.put("timestamp", O.getDateTime("dd-MM-yyyy"));

                        requestBody = jObject.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (O.checkNetwork(OfficerActivity.this)) {
                        showLoading("Please wait...");
                        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, submitofficer, new com.android.volley.Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                hideLoading();
                                JResponse jresponse = new Gson().fromJson(response, JResponse.class);
                                O.showDialog(OfficerActivity.this, "" + jresponse.message);
                                submit.setEnabled(true);
                                submit.setBackgroundResource(R.drawable.button_orange_bg);
                            }

                        }, new com.android.volley.Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(com.android.volley.VolleyError error) {
                                submit.setEnabled(true);
                                hideLoading();
                            }
                        }) {
                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8";
                            }

                            @Override
                            public byte[] getBody() throws com.android.volley.AuthFailureError {
                                try {
                                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                                } catch (UnsupportedEncodingException uee) {
                                    //   VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                                    return null;
                                }
                            }

                            @Override
                            public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("Content-Type", "application/json");
                                params.put("Accept", "application/json");
                                return params;
                            }
                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(OfficerActivity.this);
                        requestQueue.add(stringRequest);
                    } else {
                        if (db.insertToPending("Officer", DB.OFFICER, train_no,
                                requestBody, "POST", submitofficer, "", "N", "") > 0) {
                            showPendingInserted();
                            submit.setEnabled(true);
                            submit.setBackgroundResource(R.drawable.button_orange_bg);
                        }
                    }
                }
            }
        });


        findViewById(R.id.menu_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });
        if (O.checkNetwork(OfficerActivity.this)) {
            getGrades();
            getDesignation();
        } else {
            loadOfflineGrades();
            loadOfflineDesignation();
        }

        getApk();
        if (getIntent() != null && getIntent().getStringExtra("from") != null &&
                getIntent().getStringExtra("from").equalsIgnoreCase("login")) {
            LoadData loadData = new LoadData(this);
            loadData.startLoading();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int i) {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getApplicationContext(), "Please select answer", Toast.LENGTH_LONG).show();
        } else {
            int radioButtonId = group.getCheckedRadioButtonId();
            View radio = group.findViewById(radioButtonId);
            int position = group.indexOfChild(radio);
            RadioButton butt = (RadioButton) radioGroup.getChildAt(position);
            selection = (String) butt.getText();
            Log.e("Selected", "" + selection);

        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
        super.onBackPressed();
    }

    private void getGrades() {
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, GRADE_API, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //  Toast.makeText(SuperVisorFeedback.this, "success", Toast.LENGTH_SHORT).show();
                            Log.e("response", response.toString());
                            JSONArray jsonArray = response.getJSONArray("GetGrade");
                            db.insertToResponse("officer_grades", response.toString());
                            grade_list.clear();
                            grade_list.add(0, "Select Your Grade");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                grade_list.add(obj.getString("grade"));
                                // Toast.makeText(SuperVisorFeedback.this, "Array"+obj.getString("shift_name"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            loadOfflineGrades();
                        }
                        adapter_grade.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error", "" + error.toString());
                        loadOfflineGrades();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);
    }

    public void loadOfflineGrades() {
        try {
            JSONObject response = new JSONObject(db.getResponseData("officer_grades").response);
            JSONArray jsonArray = response.getJSONArray("GetGrade");
            grade_list.clear();
            grade_list.add(0, "Select Your Grade");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                grade_list.add(obj.getString("grade"));
                // Toast.makeText(SuperVisorFeedback.this, "Array"+obj.getString("shift_name"), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }
        adapter_grade.notifyDataSetChanged();
    }

    private void getDesignation() {
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, DESIGNATION_API, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Toast.makeText(SuperVisorFeedback.this, "success", Toast.LENGTH_SHORT).show();
                            Log.e("response", response.toString());
                            JSONArray jsonArray = response.getJSONArray("GetDesignation");
                            db.insertToResponse("officer_designation", response.toString());
                            designation_list.clear();
                            designation_list.add(0, "Select Your Designation");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                designation_list.add(obj.getString("designation"));
                                // Toast.makeText(SuperVisorFeedback.this, "Array"+obj.getString("shift_name"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            loadOfflineDesignation();
                        }
                        adapter_designation.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error", "" + error.toString());
                        loadOfflineDesignation();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);
    }

    public void loadOfflineDesignation() {
        try {
            JSONObject response = new JSONObject(db.getResponseData("officer_designation").response);
            JSONArray jsonArray = response.getJSONArray("GetDesignation");
            designation_list.clear();
            designation_list.add(0, "Select Your Designation");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                designation_list.add(obj.getString("designation"));
                // Toast.makeText(SuperVisorFeedback.this, "Array"+obj.getString("shift_name"), Toast.LENGTH_SHORT).show();
            }
            adapter_designation.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getApk() {

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, APK_API,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("response_apk", response.toString());

                try {
                    ApkModel apkModel = (ApkModel) (new Gson()).fromJson(response.toString(), ApkModel.class);
                    if (apkModel.mStatus == 1) {
                        if (apkModel.apkDetails.get(0).mApkVersion > BuildConfig.VERSION_CODE) {
                            showApkUpdateDialog(apkModel.apkDetails.get(0).mApkLink, apkModel.apkDetails.get(0).mApkVersion);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error);
            }
        });
        VolleySingleton.getInstance(OfficerActivity.this).addToRequestQueue(stringRequest);
    }

    public void showApkUpdateDialog(final String applink, final int vcode) {
        new AlertDialog.Builder(OfficerActivity.this)
                .setTitle("APK update")
                .setCancelable(false)
                .setMessage("New Apk found, update required other wise some function may not work properly.\nUpdate?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String destination = getExternalFilesDir(null).getAbsolutePath() + File.separator + "MCCAPP";
                        String fileName = "Mcc" + vcode + ".apk";

                        final File file = new File(destination, fileName);

                        final Uri uri = Uri.fromFile(file);
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(applink));
                        request.setDescription("");
                        request.setTitle(OfficerActivity.this.getString(R.string.app_name));

                        //set destination
                        request.setDestinationUri(uri);

                        // get download service and enqueue file
                        final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        final long downloadId = manager.enqueue(request);

                        //set BroadcastReceiver to install app when .apk is downloaded
                        BroadcastReceiver onComplete = new BroadcastReceiver() {
                            public void onReceive(Context ctxt, Intent intent) {
                                Intent install = new Intent(Intent.ACTION_VIEW);
                                install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                install.setDataAndType(uri,
                                        manager.getMimeTypeForDownloadedFile(downloadId));
                                startActivity(install);
                                unregisterReceiver(this);
                                finishAffinity();
//                                unregisterReceiver(this);
//                                installAPK(file.getAbsolutePath());
                            }
                        };
                        //register receiver for when .apk download is compete
                        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_setting, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_upload_pending) {
                    Intent intent = new Intent(OfficerActivity.this, UploadActivity.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.menu_download_offline) {
                    showDownloadOfflineDialog();
                } else if (item.getItemId() == R.id.menu_logout) {
                    showLogoutAlertDialog();
                }
                return false;
            }
        });
        popup.show();
    }

    public void showLogoutAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout Confirm");
        builder.setMessage("Do you want to logout ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                SP.setUserData(OfficerActivity.this, "");
                Intent intent = new Intent(OfficerActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDownloadOfflineDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Synchronize offline data");
        builder.setMessage("Download & update ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LoadData loadData = new LoadData(OfficerActivity.this);
                loadData.startLoading();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected void showLoading(@NonNull String message0) {
        mProgressDialog = new ProgressDialog(this);
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

    public void showPendingInserted() {
        new AlertDialog.Builder(this)
                .setMessage("Your Data is saved in pending due to unavailable network")
                .setCancelable(true)
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

}
