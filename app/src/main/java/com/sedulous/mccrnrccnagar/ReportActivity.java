package com.sedulous.mccrnrccnagar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sedulous.mccrnrccnagar.Model.QanswerData;
import com.sedulous.mccrnrccnagar.resonses.UserData;
import com.sedulous.mccrnrccnagar.utilities.DB;
import com.sedulous.mccrnrccnagar.utilities.O;
import com.sedulous.mccrnrccnagar.utilities.SP;
import com.google.gson.Gson;
import com.sedulous.mccrnrccnagar.utilities.VolleyMultipartRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReportActivity extends Activity {
    public final static String STORING_Image_sign = "https://mccrncc.projectrailway.in/api/upload_signature";
    public final static String STORING_Image_pics = "https://mccrncc.projectrailway.in/api/upload_image";
    public final static String STORING_DATA_API = "https://mccrncc.projectrailway.in/api/save_task_quest";
    public final static String STORING_DATA_Associate = "https://mccrncc.projectrailway.in/api/save_associate_area";
    Button btnNext;
    LinearLayout signaturelayout;
    ImageView signclick1, signclick2, iv_sign1, iv_sign2;
    public String strSignatureFilePath1 ="", strSignatureFilePath2="",
            penaltySignatureFilePath1 ="", penaltySignatureFilePath2="", dep, taskId="",
            requestBody, signatureresponse1,signatureresponse2;
    public static final int SIGNATURE_ACTIVITY = 1,SIGNATURE_ACTIVITY2 = 2;
    RequestQueue rQueue;
    ProgressDialog mProgressDialog;
    RecyclerView rv_out;
    OuterAdapter outerAdapter;
    ArrayList<RowData> outer_list =new ArrayList<>();
    QanswerData qanswerData=null;
    int w=0;
    boolean isSubmited=false;
    DB db;
    
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_akm);
        db=new DB(ReportActivity.this);
        UserData userData = (UserData) (new Gson()).fromJson(SP.getUserData(ReportActivity.this), UserData.class);
        qanswerData=(QanswerData)getIntent().getSerializableExtra("qdata");
        taskId = qanswerData.taskType.getId();
        dep = userData.getDepot_name();
        rv_out=findViewById(R.id.rv_report);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        signaturelayout=findViewById(R.id.signature_layout);
        btnNext = (Button) findViewById(R.id.btn_next);
        signclick1=findViewById(R.id.click1);
        signclick2=findViewById(R.id.click2);
        iv_sign1=findViewById(R.id.img_sign1);
        iv_sign2=findViewById(R.id.img_sign2);

        signclick1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReportActivity.this, CaptureSignatureActivity.class);
                startActivityForResult(intent,SIGNATURE_ACTIVITY);
            }
        });

        signclick2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReportActivity.this, CaptureSignatureActivity.class);
                startActivityForResult(intent,SIGNATURE_ACTIVITY2);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                if (strSignatureFilePath1.isEmpty() || strSignatureFilePath2.isEmpty()) {
                    Toast.makeText(ReportActivity.this, "Please take both signature.", Toast.LENGTH_SHORT).show();
                } else if(!isSubmited){
                    btnNext.setEnabled(false);
                    makeRequestJson();
                }else if(isSubmited){
                    Intent i = new Intent(ReportActivity.this, PenalityActivity.class);
                    i.putExtra("qdata", qanswerData);
                    i.putExtra("sign1", penaltySignatureFilePath1);
                    i.putExtra("sign2", penaltySignatureFilePath2);
                    startActivity(i);
                }
            }
        });

        setData();
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(ReportActivity.this,
                LinearLayoutManager.VERTICAL, false);
        rv_out.setLayoutManager(layoutManager1);
        outerAdapter = new OuterAdapter(ReportActivity.this, outer_list);
        rv_out.setAdapter(outerAdapter);
        rv_out.setNestedScrollingEnabled(false);
        rv_out.setItemViewCacheSize(20);
        rv_out.setDrawingCacheEnabled(true);
        rv_out.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

    }

    public void makeRequestJson(){
        try {
            if (taskId.equalsIgnoreCase("11") || taskId.equalsIgnoreCase("12") ||
                    taskId.equalsIgnoreCase("13") || taskId.equalsIgnoreCase("15")) {
                UserData userData = (UserData) (new Gson()).fromJson(SP.getUserData(ReportActivity.this), UserData.class);
                String dep = userData.getDepot_name();

                JSONObject jObject = new JSONObject();
                JSONArray jarray = new JSONArray();
                try {
                    jObject.put("task_type_id", qanswerData.taskType.getId());
                    jObject.put("supervisor_name", userData.getName());
                    jObject.put("depot", dep);
                    jObject.put("shift", qanswerData.shift);
                    jObject.put("total_janiator", qanswerData.trainData.getJanitor());
                    jObject.put("train_type", qanswerData.trainType);
                    jObject.put("train_assign_id", qanswerData.trainData.getId());
                    jObject.put("total_maintenance_hr", qanswerData.trainData.getTime_val());
                    jObject.put("total_load", qanswerData.pageData_list.size());
                    jObject.put("train_no", qanswerData.trainData.getTrain_no());
                    jObject.put("task_type", qanswerData.taskType.getTask_type());
                    jObject.put("signature1", signatureresponse1);
                    jObject.put("signature2", signatureresponse2);
                    jObject.put("task_date", O.getDateTime("yyyy-MM-dd"));
                    jObject.put("loads", jarray);
                    for(int i=0; i<qanswerData.pageData_list.size(); i++){
                        JSONObject jsonObject=new JSONObject();
                        jsonObject.put("load_no", (i+1));
                        jsonObject.put("coach_no", qanswerData.pageData_list.get(i).coachNo);
                        jsonObject.put("coach_name", qanswerData.pageData_list.get(i).coachName);
                        jsonObject.put("image1", qanswerData.pageData_list.get(i).response_img1);
                        jsonObject.put("image2", qanswerData.pageData_list.get(i).response_img2);
                        JSONArray jinnerArray=new JSONArray();
                        for(int j=0; j<qanswerData.questions_list.size(); j++) {
                            JSONObject jo=new JSONObject();
                            jo.put("quest",qanswerData.questions_list.get(j).getQuest());
                            jo.put("answer",qanswerData.pageData_list.get(i).answerData.
                                    get(qanswerData.questions_list.get(j).getId()));
                            jinnerArray.put(jo);
                        }
                        jsonObject.put("question", jinnerArray);
                        jarray.put(jsonObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                requestBody = jObject.toString();
                if(O.checkNetwork(ReportActivity.this)){
                    uploadSign(strSignatureFilePath1, 1);
                }else{
                    String pics="";
                    for(int a=0; a<qanswerData.pageData_list.size(); a++){
                        db.insertToPic(qanswerData.pageData_list.get(a).response_img1,
                                qanswerData.pageData_list.get(a).response_img1, STORING_Image_pics, "N");
                        db.insertToPic(qanswerData.pageData_list.get(a).response_img2,
                                qanswerData.pageData_list.get(a).response_img2, STORING_Image_pics, "N");
                        pics=pics+","+qanswerData.pageData_list.get(a).response_img1;
                        pics=pics+","+qanswerData.pageData_list.get(a).response_img2;
                    }
                    pics=pics.substring(1);
                    if(db.insertToPending(taskId,DB.REPORT, qanswerData.trainData.getTrain_no(),
                            requestBody, "POST", STORING_DATA_API,pics,"N","")>0){
                        showPendingInserted();
                        btnNext.setBackgroundResource(R.drawable.button_orange_bg);
                        isSubmited=true;
                        btnNext.setEnabled(true);
                        btnNext.setText("Go to Penalty Page");
                    }
                }
            } else if (taskId.equalsIgnoreCase("14")) {
                JSONObject jObject = new JSONObject();
                UserData userData = (UserData) (new Gson()).fromJson(SP.getUserData(ReportActivity.this), UserData.class);
                String dep = userData.getDepot_name();
                try {
                    jObject.put("task_type_id", qanswerData.taskType.getId());
                    jObject.put("supervisor_name", userData.getName());
                    jObject.put("depot", dep);
                    jObject.put("shift", qanswerData.shift);
                    jObject.put("task_type", qanswerData.taskType.getTask_type());
                    jObject.put("signature1", signatureresponse1);
                    jObject.put("signature2", signatureresponse2);
                    jObject.put("image1", qanswerData.pageData_list.get(0).response_img1);
                    jObject.put("image2", qanswerData.pageData_list.get(0).response_img1);
                    jObject.put("task_date", O.getDateTime("yyyy-MM-dd"));
                    jObject.put("task_month", O.getDateTime("MMM-yyyy"));
                    JSONArray jinnerArray=new JSONArray();
                    for(int j=0; j<qanswerData.questions_list.size(); j++) {
                        JSONObject jo=new JSONObject();
                        jo.put("quest",qanswerData.questions_list.get(j).getQuest());
                        jo.put("answer",qanswerData.pageData_list.get(0).answerData.
                                get(qanswerData.questions_list.get(j).getId()));
                        jinnerArray.put(jo);
                    }
                    jObject.put("question", jinnerArray);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                requestBody = jObject.toString();
                if(O.checkNetwork(ReportActivity.this)){
                    uploadSign(strSignatureFilePath1, 1);
                }else{

                    String pics="";
                    for(int a=0; a<qanswerData.pageData_list.size(); a++){
                        db.insertToPic(qanswerData.pageData_list.get(a).response_img1,
                                qanswerData.pageData_list.get(a).response_img1, STORING_Image_pics, "N");
                        db.insertToPic(qanswerData.pageData_list.get(a).response_img2,
                                qanswerData.pageData_list.get(a).response_img2, STORING_Image_pics, "N");
                        pics=pics+","+qanswerData.pageData_list.get(a).response_img1;
                        pics=pics+","+qanswerData.pageData_list.get(a).response_img2;
                    }
                    pics=pics.substring(1);
                    if(db.insertToPending(taskId,DB.REPORT, "",
                            requestBody, "POST", STORING_DATA_API,pics,"N","")>0){
                        showPendingInserted();
                        btnNext.setBackgroundResource(R.drawable.button_orange_bg);
                        isSubmited=true;
                        btnNext.setEnabled(true);
                        btnNext.setText("Go to Penalty Page");
                    }
                }
            }else{
                btnNext.setEnabled(true);
                hideLoading();
            }
        }
        catch (Exception e){
            btnNext.setEnabled(true);
            hideLoading();
            Log.v("akkkkkkkkkk",e.toString());
        }
    }

    public void uploadFinal(String taskId){
        String API_URL="";
        if(taskId.equalsIgnoreCase("14")) {
            API_URL=STORING_DATA_Associate;
        }else{
            API_URL=STORING_DATA_API;
        }
        Log.e("request body",""+requestBody);
        showLoading("Uploading final...");
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,
                API_URL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoading();
                Log.e("response",response);
                Toast.makeText(ReportActivity.this, ""+response, Toast.LENGTH_LONG).show();
                btnNext.setBackgroundResource(R.drawable.button_orange_bg);
                isSubmited=true;
                btnNext.setEnabled(true);
                btnNext.setText("Go to Penalty Page");

                try{
                    File file1=new File(strSignatureFilePath1);
                    if(file1.exists()) file1.delete();
                }catch (Exception e){
                    e.printStackTrace();
                }try{
                    File file2=new File(strSignatureFilePath2);
                    if(file2.exists()) file2.delete();
                }catch (Exception e){
                    e.printStackTrace();
                }

                db.deletePics(strSignatureFilePath1);
                db.deletePics(strSignatureFilePath2);

                for(int a=0; a<qanswerData.pageData_list.size(); a++){
                    try{
                        File file=new File(qanswerData.pageData_list.get(a).response_img1);
                        if(file.exists()) file.delete();
                    }catch (Exception e){
                        e.printStackTrace();
                    }try{
                        File file=new File(qanswerData.pageData_list.get(a).response_img2);
                        if(file.exists()) file.delete();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError error) {
                btnNext.setEnabled(true);
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
                    return null;
                }
            }
            @Override
            public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "multipart/form-data");
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ReportActivity.this);
        requestQueue.add(stringRequest);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String status = bundle.getString("status");
            switch(requestCode) {
                case SIGNATURE_ACTIVITY:
                    if (status.equalsIgnoreCase("done")) {
                        strSignatureFilePath1 = bundle.getString("signature_image_url");
                        iv_sign1.setImageBitmap(BitmapFactory.decodeFile(strSignatureFilePath1));
                        signatureresponse1=strSignatureFilePath1;
                        db.insertToPic(strSignatureFilePath1,strSignatureFilePath1,STORING_Image_sign, "N");
                        penaltySignatureFilePath1 = O.savefile(ReportActivity.this, O.FOLDER_SIGN,
                                BitmapFactory.decodeFile(strSignatureFilePath1),100);
                        db.insertToPic(penaltySignatureFilePath1,penaltySignatureFilePath1,STORING_Image_sign, "N");

                    }
                    break;
                case SIGNATURE_ACTIVITY2:
                    if (status.equalsIgnoreCase("done")) {
                        strSignatureFilePath2 = bundle.getString("signature_image_url");
                        iv_sign2.setImageBitmap(BitmapFactory.decodeFile(strSignatureFilePath2));
                        signatureresponse2=strSignatureFilePath2;
                        db.insertToPic(strSignatureFilePath2,strSignatureFilePath2,STORING_Image_sign, "N");
                        penaltySignatureFilePath2 = O.savefile(ReportActivity.this, O.FOLDER_SIGN,
                                BitmapFactory.decodeFile(strSignatureFilePath2),100);
                        db.insertToPic(penaltySignatureFilePath2,penaltySignatureFilePath2,STORING_Image_sign, "N");

                    }
                    break;

            }

        }
    }

    private void uploadSign(final String path, final int img_number) {

        showLoading("Uploading Signature...");
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, STORING_Image_sign,
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoading();
                        btnNext.setEnabled(true);
                        if(img_number==1) {
                            signatureresponse1 = new String(response.data);
                            requestBody=requestBody.replace(strSignatureFilePath1.replace("/","\\/"),signatureresponse1);
                            uploadSign(strSignatureFilePath2,2);
                        }else if(img_number==2){
                            signatureresponse2 = new String(response.data);
                            requestBody=requestBody.replace(strSignatureFilePath2.replace("/","\\/"),signatureresponse2);
                            uploadBitmap((qanswerData.pageData_list.size()*2)-1, 0);
                        }
                        Log.e("signatureresponse1",new String(response.data));
                        rQueue.getCache().clear();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        hideLoading();
                        btnNext.setEnabled(true);
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }
            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("sign", new DataPart(imagename + ".png", O.getBytes(path)));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(ReportActivity.this);
        rQueue.add(volleyMultipartRequest);
    }

    private void uploadBitmap(final int twiceOfMaxPage, final int currentItem) {
        final int currentPage=currentItem/2;
        int nimage=(currentItem%2)+1;
        final String filepath;
        if(nimage==1) filepath=qanswerData.pageData_list.get(currentPage).response_img1;
        else filepath=qanswerData.pageData_list.get(currentPage).response_img2;
        if(filepath.contains(".jpg") || filepath.contains(".png")) {
            showLoading("Uploading Image" + currentItem + ", Please wait...");
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, STORING_Image_pics,
                    new com.android.volley.Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            hideLoading();
                            btnNext.setEnabled(true);
                            String imageresponse = new String(response.data);
                            Log.e("page no", "c " + currentItem + "  tw " + twiceOfMaxPage +
                                    "  file  " + filepath + "  reqbody "
                                    + requestBody);

                            requestBody = requestBody.replace(filepath.replace("/", "\\/"), imageresponse);
                            if (currentItem >= twiceOfMaxPage) {
                                uploadFinal(taskId);
                            } else {
                                uploadBitmap(twiceOfMaxPage, currentItem + 1);
                            }
                            rQueue.getCache().clear();
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(com.android.volley.VolleyError error) {
                            hideLoading();
                            btnNext.setEnabled(true);
                            error.printStackTrace();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    long imagename = System.currentTimeMillis();
                    params.put("image", new DataPart(imagename + ".jpg", O.getBytes(filepath)));
                    return params;
                }
            };
            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rQueue = Volley.newRequestQueue(getApplicationContext());
            rQueue.add(volleyMultipartRequest);
            //Volley.newRequestQueue(this).add(volleyMultipartRequest);
        }else{
            requestBody=requestBody.replace(filepath.replace("/","\\/"), "");
            if (currentItem>=twiceOfMaxPage) {
                uploadFinal(taskId);
            }else{
                uploadBitmap(twiceOfMaxPage, currentItem+1);
            }
        }
    }
    public class OuterAdapter extends RecyclerView.Adapter<OuterAdapter.ViewHolder> {

        private ArrayList<RowData> datalist;
        private LayoutInflater mInflater;
        public OuterAdapter(Context context, ArrayList<RowData> data) {
            this.mInflater = LayoutInflater.from(context);
            this.datalist = data;
        }
        @Override
        public OuterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.item_rv, parent, false);
            return new OuterAdapter.ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(OuterAdapter.ViewHolder h, final int position ) {
            h.setIsRecyclable(false);
            LinearLayoutManager layoutManager1 = new LinearLayoutManager(ReportActivity.this,
                    LinearLayoutManager.HORIZONTAL, false);
            h.rv_inner.setLayoutManager(layoutManager1);
            InnerAdapter inner_adapter = new InnerAdapter(ReportActivity.this,
                    datalist.get(position).item_list );
            h.rv_inner.setAdapter(inner_adapter);
            h.rv_inner.setNestedScrollingEnabled(false);
            h.rv_inner.setItemViewCacheSize(20);
            h.rv_inner.setDrawingCacheEnabled(true);
            h.rv_inner.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        }
        @Override
        public int getItemCount() {
            return datalist.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder  {
            RecyclerView rv_inner;
            ViewHolder(View itemView) {
                super(itemView);
                rv_inner=itemView.findViewById(R.id.rv_inner);
            }
        }
    }
    public class InnerAdapter extends RecyclerView.Adapter<InnerAdapter.ViewHolder> {

        private ArrayList<TextItem> textlist;
        private LayoutInflater mInflater;
        public InnerAdapter(Context context, ArrayList<TextItem> data) {
            this.mInflater = LayoutInflater.from(context);
            this.textlist = data;
        }
        @Override
        public InnerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.item_rv_inner, parent, false);
            return new InnerAdapter.ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(InnerAdapter.ViewHolder h, final int position ) {
            h.setIsRecyclable(false);
            TextItem t=textlist.get(position);
            h.tv.setText(t.text);
            h.tv.setTextColor(t.text_color);
            h.tv.setTypeface(null, t.typeface);
            h.tv.setGravity(t.gravity);
            h.itemView.setBackgroundColor(t.bg_color);
            h.itemView.setLayoutParams(new FrameLayout.
                    LayoutParams(t.width, FrameLayout.LayoutParams.MATCH_PARENT));
        }
        @Override
        public int getItemCount() {
            return textlist.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder  {
            TextView tv;
            ViewHolder(View itemView) {
                super(itemView);
                tv=itemView.findViewById(R.id.tv);
            }
        }
    }
    class RowData{
        ArrayList<TextItem> item_list=new ArrayList<>();
    }
    class TextItem{
        String text="";
        int text_color=Color.BLACK;
        int bg_color=Color.WHITE;
        int typeface=Typeface.NORMAL;
        int width=0;
        int gravity= Gravity.CENTER;
        public TextItem(){ }
        public TextItem setTxt(String text){
            this.text=text;
            return this;
        }
        public TextItem setTxtClr(int txtColor){
            this.text_color=txtColor;
            return this;
        }
        public TextItem setBgClr(int bgColor){
            this.bg_color=bgColor;
            return this;
        }
        public TextItem setTf(int typeFace){
            this.typeface=typeFace;
            return this;
        }
        public TextItem setW(int w){
            this.width=w;
            return this;
        }
        public TextItem setG(int gravity){
            this.gravity=gravity;
            return this;
        }
    }

    public void setData(){
        int nrow=0, ncolumn=0;
        if(taskId.equalsIgnoreCase("14")){
            nrow=qanswerData.questions_list.size()+1;
            ncolumn=qanswerData.pageData_list.size()+2;
            w=O.getW(ReportActivity.this);
            for(int r=0; r<nrow; r++){
                RowData rowData=new RowData();
                if(r==0){
                    for(int c=0; c<ncolumn; c++){
                        TextItem txtItem=new TextItem();
                        if(c==0){
                            txtItem.setTxt("SNo.").setTxtClr(Color.WHITE).setBgClr(Color.BLUE).setW(w/10).setG(Gravity.START);
                        }else if(c==1){
                            txtItem.setTxt("Coach Sr No\nDescription of Works").
                                    setTxtClr(Color.WHITE).setBgClr(Color.BLUE).setW(2*w/3).setG(Gravity.START);
                        }else{
                            txtItem.setTxt("1").setTxtClr(Color.WHITE).
                                    setBgClr(Color.BLUE).setW(w/5).setG(Gravity.CENTER_HORIZONTAL);
                        }
                        rowData.item_list.add(txtItem);
                    }
                }else{
                    for(int c=0; c<ncolumn; c++){
                        TextItem txtItem=new TextItem();
                        if(c==0){
                            txtItem.setTxt(""+r).setTxtClr(Color.BLUE).setBgClr(Color.WHITE).setW(w/10).setG(Gravity.START);
                        }else if(c==1){
                            txtItem.setTxt(qanswerData.questions_list.get(r-1).getQuest()).setTxtClr(Color.BLUE).
                                    setBgClr(Color.WHITE).setW(2*w/3).setG(Gravity.START);
                        }else{
                            txtItem.setTxt(qanswerData.pageData_list.get(c-2).answerData.get(qanswerData.questions_list.get(r-1).getId())).
                                    setTxtClr(Color.BLACK).setBgClr(Color.WHITE).setW(w/5).setG(Gravity.CENTER_HORIZONTAL);
                        }
                        rowData.item_list.add(txtItem);
                    }
                }
                outer_list.add(rowData);
            }
        }else{
            nrow=qanswerData.questions_list.size()+2;
            ncolumn=qanswerData.pageData_list.size()+2;
            w=O.getW(ReportActivity.this);
            for(int r=0; r<nrow; r++){
                RowData rowData=new RowData();
                if(r==0){
                    for(int c=0; c<ncolumn; c++){
                        TextItem txtItem=new TextItem();
                        if(c==0){
                            txtItem.setTxt("SNo.").setTxtClr(Color.WHITE).setBgClr(Color.BLUE).setW(w/10).setG(Gravity.START);
                        }else if(c==1){
                            txtItem.setTxt("Cleanliness Record(50% weightage)\nCoach Interior(Coach No)").
                                    setTxtClr(Color.WHITE).setBgClr(Color.BLUE).setW(2*w/3).setG(Gravity.START);
                        }else{
                            txtItem.setTxt("Coach\n"+qanswerData.pageData_list.get(c-2).coachNo).setTxtClr(Color.WHITE).
                                    setBgClr(Color.BLUE).setW(w/5).setG(Gravity.CENTER_HORIZONTAL);
                        }
                        rowData.item_list.add(txtItem);
                    }
                }else if(r==nrow-1 ){
                    for(int c=0; c<ncolumn; c++){
                        TextItem txtItem=new TextItem();
                        if(c==0){
                            txtItem.setTxt("").setTxtClr(Color.BLACK).setBgClr(Color.WHITE).setW(w/10).
                                    setG(Gravity.START);
                        }else if(c==1){
                            txtItem.setTxt("Average Percentage").
                                    setTxtClr(Color.BLACK).setBgClr(Color.WHITE).setW(2*w/3).setG(Gravity.START).setTf(Typeface.BOLD);
                        }else{
                            int sum=0; int total=54;
                            for(int n=0; n<qanswerData.questions_list.size(); n++){
                                String value=qanswerData.pageData_list.get(c-2).answerData.
                                        get(qanswerData.questions_list.get(n).getId()).trim();
                                int answer=0;
                                int na_factor=0;
                                try{
                                    if(value.equalsIgnoreCase("NA")){
                                        if(qanswerData.questions_list.get(n).getNa()==1)
                                            na_factor=-6;
                                    }else {
                                        answer = Integer.parseInt(value);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                sum+=answer;
                                total+=na_factor;
                            }
                            int avrage=(sum*100)/total;
                            txtItem.setTxt(""+avrage).setTxtClr(Color.BLACK).
                                    setBgClr(Color.WHITE).setW(w/5).setG(Gravity.CENTER_HORIZONTAL).setTf(Typeface.BOLD);
                        }
                        rowData.item_list.add(txtItem);
                    }
                }else{
                    for(int c=0; c<ncolumn; c++){
                        TextItem txtItem=new TextItem();
                        if(c==0){
                            txtItem.setTxt(""+r).setTxtClr(Color.BLUE).setBgClr(Color.WHITE).setW(w/10).setG(Gravity.START);
                        }else if(c==1){
                            txtItem.setTxt(qanswerData.questions_list.get(r-1).getQuest()).setTxtClr(Color.BLUE).
                                    setBgClr(Color.WHITE).setW(2*w/3).setG(Gravity.START);
                        }else{
                            txtItem.setTxt(qanswerData.pageData_list.get(c-2).answerData.get(qanswerData.questions_list.get(r-1).getId())).
                                    setTxtClr(Color.BLACK).setBgClr(Color.WHITE).setW(w/5).setG(Gravity.CENTER_HORIZONTAL);
                        }
                        rowData.item_list.add(txtItem);
                    }
                }
                outer_list.add(rowData);
            }
        }
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to exit page ?\nAll Data will be lost")
                .setCancelable(true)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent i = new Intent(ReportActivity.this, HomeActivity.class);
                        startActivity(i);
                        finishAffinity();
                    }
                }).show();
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
    public void showPendingInserted(){
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
