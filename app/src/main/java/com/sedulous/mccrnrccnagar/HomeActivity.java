package com.sedulous.mccrnrccnagar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sedulous.mccrnrccnagar.Model.ApkModel;
import com.sedulous.mccrnrccnagar.resonses.ResponseClass;
import com.sedulous.mccrnrccnagar.resonses.TaskType;
import com.sedulous.mccrnrccnagar.resonses.UserData;
import com.sedulous.mccrnrccnagar.services.Web;
import com.google.gson.Gson;
import com.sedulous.mccrnrccnagar.utilities.DB;
import com.sedulous.mccrnrccnagar.utilities.InputStreamVolleyRequest;
import com.sedulous.mccrnrccnagar.utilities.O;
import com.sedulous.mccrnrccnagar.utilities.SP;
import com.sedulous.mccrnrccnagar.utilities.VolleySingleton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    public static String APK_API="https://mccrncc.projectrailway.in/api/get_apiupdate";
    private RecyclerView recyclerView;
    private TaskTypeAdapter adapter;
    private ArrayList<TaskType> taskTypeArrayList=new ArrayList<>();;
    private final String[] permissions = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int REQUEST_CODE = 12345;
    ProgressDialog mProgressDialog;
    SwipeRefreshLayout srl;
    DB db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        db=new DB(HomeActivity.this);
        srl=findViewById(R.id.srl);
        TextView tvUserType=findViewById(R.id.tvUserType);
        recyclerView=findViewById(R.id.recyclerView);
        //SupervisorCleaningActivity.b=54;
        UserData userData=(UserData)(new Gson()).
                fromJson(SP.getUserData(HomeActivity.this), UserData.class);

        //akm permission added
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, permissions[0]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(HomeActivity.this, permissions[1]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(HomeActivity.this, permissions[2]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(HomeActivity.this, permissions[3]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(HomeActivity.this, permissions[4]) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(HomeActivity.this, permissions, REQUEST_CODE);
        }

        tvUserType.setText(userData.getUser_type());
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new TaskTypeAdapter(this,taskTypeArrayList);
        recyclerView.setAdapter(adapter);
        if (O.checkNetwork(HomeActivity.this)){
            getTaskType();
        }else {
            loadOffline();
        }
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (O.checkNetwork(HomeActivity.this)){
                    getTaskType();
                }else {
                    srl.setRefreshing(false);
                    loadOffline();
                }
            }
        });
        findViewById(R.id.menus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });

        getApk();
        if(getIntent()!=null && getIntent().getStringExtra("from")!=null &&
        getIntent().getStringExtra("from").equalsIgnoreCase("login")){
            LoadData loadData=new LoadData(this);
            loadData.startLoading();
        }
    }

    public void getTaskType(){
        srl.setRefreshing(true);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, Web.TASK_TYPE_LIST_URL,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    srl.setRefreshing(false);
                    Log.e("GetTaskTypeRes", "" + response);
                    if ( !response.toString().isEmpty()) {
                        taskTypeArrayList.clear();
                        ResponseClass responseClass=(new Gson()).fromJson(response.toString(), ResponseClass.class);
                        if (responseClass.getSuccess()==1) {
                            db.insertToResponse("tasktype", response.toString());
                            taskTypeArrayList.addAll(responseClass.getTaskType());
                            if (taskTypeArrayList.size() > 0) {
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(HomeActivity.this, "Please try again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        loadOffline();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",""+error);
                hideLoading();
                loadOffline();
            }
        });
        VolleySingleton.getInstance(HomeActivity.this).addToRequestQueue(stringRequest);
    }

    public void loadOffline(){
        try {
            srl.setRefreshing(false);
            if ( !TextUtils.isEmpty(db.getResponseData("tasktype").response)) {
                taskTypeArrayList.clear();
                ResponseClass responseClass=(new Gson()).fromJson(db.getResponseData("tasktype").response, ResponseClass.class);
                taskTypeArrayList.addAll(responseClass.getTaskType());
                if (taskTypeArrayList.size() > 0) {
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(HomeActivity.this, "Please try again!", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(HomeActivity.this, R.string.internet_connection_error, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class TaskTypeAdapter extends RecyclerView.Adapter<TaskTypeAdapter.TaskTypeHolder> {

        private Context context;
        private ArrayList<TaskType> typeArrayList;

        public TaskTypeAdapter(Context context,ArrayList<TaskType> typeArrayList){
            this.context=context;
            this.typeArrayList=typeArrayList;
        }

        @NonNull
        @Override
        public TaskTypeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_type,null);
            return (new TaskTypeHolder(view));
        }

        @Override
        public void onBindViewHolder(TaskTypeHolder holder,  int pos) {
            final int position=pos;
            holder.setIsRecyclable(false);
            TaskType taskType=typeArrayList.get(position);
            holder.tvTitle.setText(taskType.getTask_type());
            holder.tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                UserData userData=(UserData)(new Gson()).fromJson(SP.getUserData(context),UserData.class);
//                if(userData.getUser_type().equalsIgnoreCase("supervisor")){
//                    //supervisor item reset.
//                    SupervisorCleaningActivity.maxCoach=0;
//                    SupervisorCleaningActivity.n_load_page=0;
//                    Intent intent = new Intent(context, SupervisorCleaningActivity.class);
//                    intent.putExtra("data",(Serializable) typeArrayList.get(position));
//                    context.startActivity(intent);
//
//                }
                    UserData userData=(UserData)(new Gson()).fromJson(SP.getUserData(context),UserData.class);
                    if (typeArrayList.get(position).getTask_type().trim().equals("Mechanized coach cleaning")){
                        SupervisorCleaningActivity.maxCoach=0;
                        SupervisorCleaningActivity.n_load_page=0;
                        Intent intent = new Intent(HomeActivity.this,SupervisorCleaningActivity.class);
                        intent.putExtra("data",(Serializable) typeArrayList.get(position));
                        startActivity(intent);
                    }
                    else if (typeArrayList.get(position).getTask_type().trim().equals("Associate Area Cleaning")) {
                        SupervisorCleaningActivity.maxCoach=0;
                        SupervisorCleaningActivity.n_load_page=0;
                        Intent intent = new Intent(HomeActivity.this,SupervisorCleaningActivity.class);
                        intent.putExtra("data",(Serializable) typeArrayList.get(position));
                        startActivity(intent);
                    }
                else if (typeArrayList.get(position).getTask_type().trim().equals("Mechanized Coach Cleaning Consumables")) {
                    Intent intent = new Intent(HomeActivity.this,MechanizedConsumable.class);
                    intent.putExtra("data",(Serializable) typeArrayList.get(position));
                    startActivity(intent);
                }
                else if (typeArrayList.get(position).getTask_type().trim().equals("Associate Area Cleaning Consumables")) {
                    Intent intent = new Intent(HomeActivity.this, AssociateConsumable.class);
                    intent.putExtra("data",(Serializable) typeArrayList.get(position));
                    startActivity(intent);
                }
                }
            });
        }

        @Override
        public int getItemCount() {
            return typeArrayList.size();
        }

        public class TaskTypeHolder extends RecyclerView.ViewHolder{

            private TextView tvTitle;
            public TaskTypeHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle=itemView.findViewById(R.id.tv_title);
            }
        }
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
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
        super.onBackPressed();
    }
    public void getApk(){

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, APK_API,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("response_apk", response.toString());

                try {
                    ApkModel apkModel=(ApkModel) (new Gson()).fromJson(response.toString(),ApkModel.class);
                    if(apkModel.mStatus==1){
                        if(apkModel.apkDetails.get(0).mApkVersion> BuildConfig.VERSION_CODE){
                            showApkUpdateDialog(apkModel.apkDetails.get(0).mApkLink,apkModel.apkDetails.get(0).mApkVersion);
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
            }
        });
        VolleySingleton.getInstance(HomeActivity.this).addToRequestQueue(stringRequest);
    }
    public void showApkUpdateDialog(final String applink, final int vcode){
        new AlertDialog.Builder(HomeActivity.this)
                .setTitle("APK update")
                .setCancelable(false)
                .setMessage("New Apk found, update required other wise some function may not work properly.\nUpdate?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        downloadApp(applink,vcode);
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
    public void downloadApp(final String applink, final int vcode){
        showLoading("Downloading Mcc update apk...");
        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.POST, applink,
                new com.android.volley.Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        // TODO handle the response
                        hideLoading();
                        try {
                            if (response!=null) {

                                String destination =  Environment.getExternalStorageDirectory().getAbsolutePath();
                                if(!new File(destination).exists()) (new File(destination)).mkdir();
                                String fileName = "Mcc"+vcode+".apk";
                                final File file = new File(destination,fileName);
                                FileOutputStream outputStream = new FileOutputStream(file.getAbsolutePath());
                                outputStream.write(response);
                                outputStream.close();

                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                                startActivity(intent);
                                Toast.makeText(HomeActivity.this, "Download complete.", Toast.LENGTH_LONG).show();
                                finishAffinity();
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
                            e.printStackTrace();
                        }
                    }
                } ,new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO handle the error
                error.printStackTrace();
                hideLoading();
                Log.e("error",""+error);
            }
        }, null);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack());
        mRequestQueue.add(request);
    }
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_setting, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_upload_pending) {
                    Intent intent = new Intent(HomeActivity.this, UploadActivity.class);
                    startActivity(intent);
                }else if (item.getItemId() == R.id.menu_download_offline) {
                    showDownloadOfflineDialog();
                }else if (item.getItemId() == R.id.menu_logout) {
                    showLogoutAlertDialog();
                }
                return false;
            }
        });
        popup.show();
    }
    public void showLogoutAlertDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Logout Confirm");
        builder.setMessage("Do you want to logout ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                SP.setUserData(HomeActivity.this,"");
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
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
        AlertDialog dialog=builder.create();
        dialog.show();
    }
    public void showDownloadOfflineDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Synchronize offline data");
        builder.setMessage("Download & update ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               LoadData loadData=new LoadData(HomeActivity.this);
               loadData.startLoading();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }
}
