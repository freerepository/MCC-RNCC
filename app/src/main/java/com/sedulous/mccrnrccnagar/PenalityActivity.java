package com.sedulous.mccrnrccnagar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sedulous.mccrnrccnagar.Model.JResponse;
import com.sedulous.mccrnrccnagar.Model.PenaltyQModel;
import com.sedulous.mccrnrccnagar.Model.QanswerData;
import com.sedulous.mccrnrccnagar.resonses.UserData;
import com.sedulous.mccrnrccnagar.utilities.DB;
import com.sedulous.mccrnrccnagar.utilities.O;
import com.google.gson.Gson;
import com.sedulous.mccrnrccnagar.utilities.SP;
import com.sedulous.mccrnrccnagar.utilities.VolleyMultipartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.sedulous.mccrnrccnagar.ReportActivity.STORING_Image_sign;

public class PenalityActivity extends Activity{
    public final static String getpenaltyques = "https://mccrncc.projectrailway.in/api/get_penalty_quest2";
    public final static String submitpenality = "https://mccrncc.projectrailway.in/api/save_penalty_quest";
    TextView tv_title;
    PenalityAdapter adapter;
    RecyclerView recyclerView;
    Button btn_next_submit;

    String requestBody;
    TextView tv_total_penality_amount;
    ProgressDialog mProgressDialog;
    DB db;
    QanswerData qanswerData=null;
    String taskId="", dep, penaltySignatureFilePath1, penaltySignatureFilePath2;
    PenaltyQModel penaltyQModel=null;
    ArrayList<PenaltyQModel.Question> question_list=new ArrayList<>();
    HashMap<String, QItem> penalty_map=new HashMap<>();
    SwipeRefreshLayout srl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penality);
        qanswerData=(QanswerData)getIntent().getSerializableExtra("qdata");
        taskId = qanswerData.taskType.getId();
        penaltySignatureFilePath1 =getIntent().getStringExtra("sign1");
        penaltySignatureFilePath2 =getIntent().getStringExtra("sign2");
        UserData userData=(UserData)(new Gson()).fromJson(SP.getUserData(PenalityActivity.this),UserData.class);
        dep=userData.getDepot_name();
        db=new DB(PenalityActivity.this);

        srl=findViewById(R.id.srl);
        tv_title=findViewById(R.id.tv_title);
        tv_total_penality_amount=findViewById(R.id.tv_total_penality_amount);
        btn_next_submit=findViewById(R.id.btn_next_submit);
        btn_next_submit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                if(penalty_map.size()==0){
                    Toast.makeText(getApplicationContext(),"Please Select at least one Penality",Toast.LENGTH_SHORT).show();
                }else {
                    makeJSONString();
                    if (O.checkNetwork(PenalityActivity.this)) {
                        uploadSign(penaltySignatureFilePath1, 1);
                    }else{
                        String train_no="";
                        if (!taskId.equalsIgnoreCase("14")){
                            train_no=qanswerData.trainData.getTrain_no();
                        }
                        if( db.insertToPending(taskId,DB.PENALTY, train_no,
                                requestBody, "POST", submitpenality, "","N","")>0) {
                            btn_next_submit.setBackgroundResource(R.drawable.button_orange_bg);
                            showPendingInserted();
                        }
                    }}
            }
        });

        recyclerView=findViewById(R.id.recyclerView);
        tv_title.setText("Penality of"+" "+qanswerData.taskType.getTask_type());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PenalityAdapter(PenalityActivity.this, question_list);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(adapter==null || adapter.pnlty_qlist.isEmpty()){
                    if(O.checkNetwork(PenalityActivity.this)) {
                        getPenalityQuestions();
                    }else{
                        loadOfflinePenaltyQ("qpenalty_"+dep+"_"+taskId);
                    }
                }else{
                    srl.setRefreshing(false);
                }
            }
        });
        if(O.checkNetwork(PenalityActivity.this)) {
            getPenalityQuestions();
        }else{
            loadOfflinePenaltyQ("qpenalty_"+dep+"_"+taskId);
        }
    }
    public String makeJSONString(){
        JSONArray array = new JSONArray();
        for (Map.Entry<String, QItem> mmap : penalty_map.entrySet()) {
            QItem qItem = mmap.getValue();
            try {
                JSONObject object = new JSONObject();
                object.put("quest", mmap.getKey());
                object.put("marked", qItem.isChecked);
                object.put("amount", qItem.penalityAmount);
                object.put("unit", qItem.shortFall);
                array.put(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JSONObject jObject = new JSONObject();
        try {
            jObject.put("task_type_id", taskId);
            if (!taskId.equalsIgnoreCase("14")) {
                jObject.put("train_no", qanswerData.trainData.getTrain_no());
                jObject.put("coach_no", qanswerData.pageData_list.get(0).coachNo);
                jObject.put("coach_name", qanswerData.pageData_list.get(0).coachName);
            }
            jObject.put("task_type", qanswerData.taskType.getTask_type());
            jObject.put("signature1", penaltySignatureFilePath1);
            jObject.put("signature2", penaltySignatureFilePath2);
            jObject.put("task_date", O.getDateTime("dd-MM-yyyy"));
            jObject.put("question", array);
            requestBody = jObject.toString();
            return requestBody;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestBody;
    }
    private void uploadSign(final String path, final int img_number) {

        showLoading("Uploading Signature"+img_number+"...");
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, STORING_Image_sign,
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoading();
                        btn_next_submit.setEnabled(true);
                        String signatureresponse = new String(response.data);
                        if(img_number==1) {
                            requestBody=requestBody.replace(penaltySignatureFilePath1.replace("/","\\/"),signatureresponse);
                            uploadSign(penaltySignatureFilePath2,2);
                        }else if(img_number==2){
                            requestBody=requestBody.replace(penaltySignatureFilePath2.replace("/","\\/"),signatureresponse);
                            uploadFinal();
                        }

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        hideLoading();
                        btn_next_submit.setEnabled(true);
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
        RequestQueue rQueue = Volley.newRequestQueue(PenalityActivity.this);
        rQueue.add(volleyMultipartRequest);
    }
    public void uploadFinal(){
        btn_next_submit.setEnabled(false);
        showLoading("uploading data...");
        Log.e("req_body",requestBody);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, submitpenality, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoading();
                try {
                    JResponse jresponse = new Gson().fromJson(response, JResponse.class);
                    Toast.makeText(PenalityActivity.this, "" + jresponse.message, Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
                btn_next_submit.setBackgroundResource(R.drawable.button_orange_bg);
                db.deletePics(penaltySignatureFilePath1);
                db.deletePics(penaltySignatureFilePath2);
                try{
                    File file1=new File(penaltySignatureFilePath1);
                    if(file1.exists()) file1.delete();
                }catch (Exception e){
                    e.printStackTrace();
                }try{
                    File file2=new File(penaltySignatureFilePath1);
                    if(file2.exists()) file2.delete();
                }catch (Exception e){
                    e.printStackTrace();
                }
                Intent i = new Intent(PenalityActivity.this, HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }

        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError error) {
                btn_next_submit.setEnabled(true);
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(PenalityActivity.this);
        requestQueue.add(stringRequest);
    }
    private void getPenalityQuestions() {
        srl.setRefreshing(true);
        final JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("penalty_id", qanswerData.taskType.getId());
            jsonObject.put("depot", dep);
        }catch (Exception e){
            e.printStackTrace();
        }
        final JsonObjectRequest ObjectRequest = new JsonObjectRequest(Request.Method.POST,
                getpenaltyques, jsonObject, new Response.Listener<JSONObject >() {
                    @Override
                    public void onResponse(JSONObject response) {
                        srl.setRefreshing(false);
                        Log.e("response",""+response);

                        try {
                            penaltyQModel=null;
                            if(response.getInt("success")==1) {
                                penaltyQModel = new Gson().fromJson(response.toString(), PenaltyQModel.class);
                                db.insertToResponse("qpenalty_"+dep+"_"+taskId, response.toString());
                            } else {
                                loadOfflinePenaltyQ("qpenalty_"+dep+"_"+taskId);
                            }
                            adapter.pnlty_qlist=penaltyQModel.mQuestions;
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            loadOfflinePenaltyQ("qpenalty_"+dep+"_"+taskId);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        srl.setRefreshing(false);
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(ObjectRequest);
    }

    public void loadOfflinePenaltyQ(String key){
        srl.setRefreshing(false);
        try {
            penaltyQModel=null;
            penaltyQModel = new Gson().fromJson(db.getResponseData(key).response, PenaltyQModel.class);
            adapter.pnlty_qlist=penaltyQModel.mQuestions;
            adapter.notifyDataSetChanged();
        } catch (Exception e) {

        }
    }
    public class PenalityAdapter extends RecyclerView.Adapter<PenalityAdapter.MyViewHolder> {
        private Context mcontext;
        ArrayList<PenaltyQModel.Question> pnlty_qlist;

        public PenalityAdapter(Context c, ArrayList<PenaltyQModel.Question> pqlist) {
            this.mcontext=c;
            this.pnlty_qlist = pqlist;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.penality_layout,parent,false);
            MyViewHolder vh=new MyViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.setIsRecyclable(false);
            final PenaltyQModel.Question question=pnlty_qlist.get(position);
            int c = position + 1;
            holder.textsno.setText("" + c);
            try {
                holder.textques.setText(question.mQuestion);
                holder.textrate.setText(question.mPrice);
            } catch (Exception e) {

            }
            QItem qItem=null;
            if(penalty_map.containsKey(question.mQId))
            qItem=penalty_map.get(question.mQId);
            try {
                if(qItem!=null){
                    holder.et_shortfall.setText(qItem.shortFall);
                    holder.texttoatal.setText(qItem.total);
                    if(qItem.isChecked.equalsIgnoreCase("Y")){
                        holder.checkBox.setChecked(true);
                    }else{
                        holder.checkBox.setChecked(false);
                    }
                }else{
                    holder.et_shortfall.setText("");
                    holder.texttoatal.setText("");
                    holder.checkBox.setChecked(false);
                }
            } catch (Exception e){
                e.printStackTrace();
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
                            int total=Integer.parseInt(question.mPrice)*Integer.parseInt(holder.et_shortfall.getText().toString().trim());
                            holder.texttoatal.setText(""+total);
                            penalty_map.put(question.mQId, new QItem().
                                    setShortFall(holder.et_shortfall.getText().toString().trim()).
                                    setPenaltyAmount(question.mPrice).setChecked("Y").setTotal(""+total));
                            holder.checkBox.setChecked(true);
                        }else{
                            penalty_map.remove(question.mQId);
                            holder.checkBox.setChecked(false);
                            holder.texttoatal.setText("");
                        }
                        notifyDataSetChanged();
                    }catch (Exception e){
                        Log.v("ExceptionAfter",e.toString());
                    }
                }
            });

            holder.checkBox.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        try {
                            if (!holder.et_shortfall.getText().toString().trim().isEmpty()) {
                                int total=Integer.parseInt(question.mPrice)*Integer.parseInt(holder.et_shortfall.getText().toString().trim());
                                holder.texttoatal.setText(""+total);
                                penalty_map.put(question.mQId, new QItem().
                                        setShortFall(holder.et_shortfall.getText().toString().trim()).
                                        setPenaltyAmount(question.mPrice).setChecked("Y").setTotal(""+total));
                                holder.checkBox.setChecked(true);
                            }else{
                                penalty_map.remove(question.mQId);
                                holder.checkBox.setChecked(false);
                                holder.texttoatal.setText("");
                            }
                            notifyDataSetChanged();
                        }catch (Exception e){
                            Log.v("ExceptionAfter",e.toString());
                        }
                    }else {
                        try {
                            penalty_map.remove(question.mQId);
                            holder.checkBox.setChecked(false);
                            holder.texttoatal.setText("");
                            notifyDataSetChanged();
                        }catch (Exception e){
                            Log.v("ExceptionAfter",e.toString());
                        }
                    }
                }
            });
            int sum=0;
            for(QItem qItem1:penalty_map.values()){
                try {
                    sum = sum + Integer.parseInt(qItem1.total);
                }catch (Exception e){e.printStackTrace();}
            }
            tv_total_penality_amount.setText("Total:"+sum);
        }
        @Override
        public int getItemCount()  {
            return pnlty_qlist.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{

            TextView textsno,textques,textrate,texttoatal;
            EditText et_shortfall;
            CheckBox checkBox;
            public MyViewHolder(View itemView) {
                super(itemView);
                textsno=itemView.findViewById(R.id.tv_index_number);
                textques=itemView.findViewById(R.id.tv_index_text);
                textrate=itemView.findViewById(R.id.et_qty_issued_at_origin);
                texttoatal=itemView.findViewById(R.id.penality);
                et_shortfall =itemView.findViewById(R.id.et_shortfall);
                checkBox=itemView.findViewById(R.id.checkBox);
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
                        Intent i = new Intent(PenalityActivity.this, HomeActivity.class);
                        startActivity(i);
                        finishAffinity();
                    }
                }).show();
        super.onBackPressed();
    }
    class QItem {
        String shortFall="", penalityAmount="", total="", isChecked="";

        public QItem setShortFall(String shortFall){
            this.shortFall=shortFall;
            return this;
        }
        public QItem setPenaltyAmount(String amount){
            this.penalityAmount=amount;
            return this;
        }
        public QItem setTotal(String total){
            this.total=total;
            return this;
        }
        public QItem setChecked(String isChecked){
            this.isChecked=isChecked;
            return this;
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
                        Intent i = new Intent(PenalityActivity.this, HomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
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

}
