package com.sedulous.mccrnrccnagar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sedulous.mccrnrccnagar.Model.PendingData;
import com.sedulous.mccrnrccnagar.Model.PicsData;
import com.sedulous.mccrnrccnagar.utilities.DB;
import com.sedulous.mccrnrccnagar.utilities.O;
import com.sedulous.mccrnrccnagar.utilities.VolleyMultipartRequest;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.sedulous.mccrnrccnagar.OfficerActivity.submitofficer;

public class UploadActivity extends AppCompatActivity {

    ProgressDialog mProgressDialog;
    PendingAdapter pendingAdapter;
    RecyclerView rv;
    ArrayList<PendingData> pendingDataList=new ArrayList<>();
    DB db;
    SwipeRefreshLayout srl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        rv=findViewById(R.id.rv);
        srl=findViewById(R.id.srl);
        db=new DB(UploadActivity.this);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(UploadActivity.this,
                LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManager1);
        pendingAdapter = new PendingAdapter(UploadActivity.this, pendingDataList);
        rv.setAdapter(pendingAdapter);
        rv.setNestedScrollingEnabled(false);
        rv.setItemViewCacheSize(20);
        pendingAdapter.datalist=db.getAllPendingData();
        pendingAdapter.notifyDataSetChanged();
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pendingAdapter.datalist=db.getAllPendingData();
                pendingAdapter.notifyDataSetChanged();
                srl.setRefreshing(false);
            }
        });

    }

    public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.ViewHolder> {

        private ArrayList<PendingData> datalist;
        private LayoutInflater mInflater;
        public PendingAdapter(Context context, ArrayList<PendingData> data) {
            this.mInflater = LayoutInflater.from(context);
            this.datalist = data;
        }
        @Override
        public PendingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.item_upload, parent, false);
            return new PendingAdapter.ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(PendingAdapter.ViewHolder h, final int position ) {
            h.setIsRecyclable(false);
            final PendingData pendingData=datalist.get(position);
            h.tv_task.setText(getTask(pendingData.taskid));
            h.tv_trainno.setText("Train No : "+pendingData.trainno);
            h.tv_act.setText("For : "+pendingData.activity);
            h.tv_date.setText("Date : "+pendingData.insert_time);
            h.tv_uploadstatus.setText("Upload Status : "+pendingData.upload_status);
            h.iv_upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(pendingData.activity.equalsIgnoreCase(DB.PENALTY)){
                        try {
                            if(O.checkNetwork(UploadActivity.this)){
                            JSONObject jo=new JSONObject(pendingData.requests);
                            PicsData picsDataSign1=db.getPicsData(jo.getString("signature1"));
                            PicsData picsDataSign2=db.getPicsData(jo.getString("signature2"));
                            ArrayList<PicsData> pics=new ArrayList<>();
                            for(String str:pendingData.pics.split(",")){
                                pics.add(db.getPicsData(str));
                            }
                            uploadPenaltySign(pendingData.id, pendingData.url, pics ,pendingData.requests,picsDataSign1, picsDataSign2, 1);
                            }else{
                                Toast.makeText(UploadActivity.this,"Internet Connection not available",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }else if(pendingData.activity.equalsIgnoreCase(DB.REPORT)){
                        try {
                            JSONObject jo=new JSONObject(pendingData.requests);
                            PicsData picsDataSign1=db.getPicsData(jo.getString("signature1"));
                            PicsData picsDataSign2=db.getPicsData(jo.getString("signature2"));
                            ArrayList<PicsData> pics=new ArrayList<>();
                            for(String str:pendingData.pics.split(",")){
                                pics.add(db.getPicsData(str));
                            }
                            uploadSign(pendingData.id, pendingData.url, pics ,pendingData.requests,picsDataSign1, picsDataSign2, 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else if(pendingData.activity.equalsIgnoreCase(DB.OFFICER)){
                       uploadOfficer(pendingData.requests,pendingData.id);
                    }
                }
            });
            h.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(UploadActivity.this)
                            .setMessage("This Pending data will be deleted permanently from your device.\n" +
                                    "Do you want to delete?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if(pendingData.activity.equalsIgnoreCase(DB.PENALTY)){
                                        db.deletePendingData(pendingData.id);
                                        pendingAdapter.datalist=db.getAllPendingData();
                                        pendingAdapter.notifyDataSetChanged();
                                    }else if(pendingData.activity.equalsIgnoreCase(DB.REPORT)){
                                        try {
                                            JSONObject jo=new JSONObject(pendingData.requests);
                                            PicsData sign1=db.getPicsData(jo.getString("signature1"));
                                            PicsData sign2=db.getPicsData(jo.getString("signature2"));
                                            try{
                                                File file1=new File(sign1.filepath);
                                                if(file1.exists()) file1.delete();
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }try{
                                                File file2=new File(sign2.filepath);
                                                if(file2.exists()) file2.delete();
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                            db.deletePics(sign1.keyid);
                                            db.deletePics(sign2.keyid);

                                            for(String str:pendingData.pics.split(",")){
                                                PicsData picsData=db.getPicsData(str);
                                                try{
                                                    File file=new File(picsData.filepath);
                                                    if(file.exists()) file.delete();
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                                db.deletePics(picsData.keyid);
                                            }
                                            db.deletePendingData(pendingData.id);
                                            pendingAdapter.datalist=db.getAllPendingData();
                                            pendingAdapter.notifyDataSetChanged();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }else if(pendingData.activity.equalsIgnoreCase(DB.OFFICER)){
                                        db.deletePendingData(pendingData.id);
                                        pendingAdapter.datalist=db.getAllPendingData();
                                        pendingAdapter.notifyDataSetChanged();
                                    }
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }
            });
        }
        @Override
        public int getItemCount() {
            return datalist.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder  {
            TextView tv_task, tv_act, tv_trainno, tv_uploadstatus, tv_date;
            ImageView iv_upload, iv_delete;
            ViewHolder(View itemView) {
                super(itemView);
                tv_task=itemView.findViewById(R.id.tv_task);
                tv_act=itemView.findViewById(R.id.tv_act);
                tv_trainno=itemView.findViewById(R.id.tv_trainno);
                tv_uploadstatus=itemView.findViewById(R.id.tv_upload_status);
                tv_date=itemView.findViewById(R.id.tv_date);
                iv_upload=itemView.findViewById(R.id.iv_upload);
                iv_delete=itemView.findViewById(R.id.iv_delete);

            }
        }
    }

    public void uploadPenalty(final String requestBody, String url, final String id){

        showLoading("uploading data...");
        Log.e("req body origin",""+db.getPendingData(id).requests);
        Log.e("req body",""+requestBody);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,
                url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoading();

                db.updatePendingStatus(id,"Y");
                PendingData pendingData=db.getPendingData(id);
                try {
                    JSONObject jo=new JSONObject(pendingData.requests);
                    PicsData sign1=db.getPicsData(jo.getString("signature1"));
                    PicsData sign2=db.getPicsData(jo.getString("signature2"));
                    try{
                        File file1=new File(sign1.filepath);
                        if(file1.exists()) file1.delete();
                    }catch (Exception e){
                        e.printStackTrace();
                    }try{
                        File file2=new File(sign2.filepath);
                        if(file2.exists()) file2.delete();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    db.deletePics(sign1.keyid);
                    db.deletePics(sign2.keyid);

                    db.updatePendingStatus(id,"Y");
                    db.deletePendingData(id);
                    pendingAdapter.datalist=db.getAllPendingData();
                    pendingAdapter.notifyDataSetChanged();
                    showDialog();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError error) {
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
        RequestQueue requestQueue = Volley.newRequestQueue(UploadActivity.this);
        requestQueue.add(stringRequest);
    }
    private void uploadPenaltySign(final String id, final String final_url, final ArrayList<PicsData> pic_list,
                            final String requestBody, final PicsData picsDataSign1,
                            final PicsData picsDataSign2, final int img_number) {
        final String url; final String img_path;
        if(img_number==1){
            url=picsDataSign1.url;
            img_path=picsDataSign1.filepath;
        }else if(img_number==2){
            url=picsDataSign2.url;
            img_path=picsDataSign2.filepath;
        }else{
            url=""; img_path="";
        }
        showLoading("Uploading Signature"+img_number+"...");
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST,
                url,
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoading();

                        String signatureresponse = new String(response.data);
                        Log.e("sign_path   "+img_path+" response "+img_number,""+signatureresponse);
                        if(img_number==1) {
                            uploadPenaltySign(id, final_url, pic_list, requestBody.replace(img_path.replace("/","\\/"),signatureresponse),
                                    picsDataSign1, picsDataSign2, 2);
                        }else if(img_number==2){
                            uploadPenalty(requestBody.replace(img_path.replace("/","\\/"),signatureresponse),
                                    final_url,id);
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        hideLoading();
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
                params.put("sign", new DataPart(imagename + ".png", O.getBytes(img_path)));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue rQueue = Volley.newRequestQueue(UploadActivity.this);
        rQueue.add(volleyMultipartRequest);
    }
    public void uploadFinal(final String id, final String requestBody, String url){
        Log.e("akm_request_body",""+requestBody);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,
                url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoading();
                db.updatePendingStatus(id,"Y");
                PendingData pendingData=db.getPendingData(id);
                try {
                    JSONObject jo=new JSONObject(pendingData.requests);
                    PicsData sign1=db.getPicsData(jo.getString("signature1"));
                    PicsData sign2=db.getPicsData(jo.getString("signature2"));
                    try{
                        File file1=new File(sign1.filepath);
                        if(file1.exists()) file1.delete();
                    }catch (Exception e){
                        e.printStackTrace();
                    }try{
                        File file2=new File(sign2.filepath);
                        if(file2.exists()) file2.delete();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    db.deletePics(sign1.keyid);
                    db.deletePics(sign2.keyid);

                    for(String str:pendingData.pics.split(",")){
                        PicsData picsData=db.getPicsData(str);
                        try{
                            File file=new File(picsData.filepath);
                            if(file.exists()) file.delete();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        db.deletePics(picsData.keyid);
                    }
                    db.deletePendingData(pendingData.id);
                    pendingAdapter.datalist=db.getAllPendingData();
                    pendingAdapter.notifyDataSetChanged();
                    showDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError error) {
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
        RequestQueue requestQueue = Volley.newRequestQueue(UploadActivity.this);
        requestQueue.add(stringRequest);
    }
    private void uploadSign(final String id, final String final_url, final ArrayList<PicsData> pic_list,
                            final String requestBody, final PicsData picsDataSign1,
                            final PicsData picsDataSign2, final int img_number) {
        final String url; final String img_path;
        if(img_number==1){
            url=picsDataSign1.url;
            img_path=picsDataSign1.filepath;
        }else if(img_number==2){
            url=picsDataSign2.url;
            img_path=picsDataSign2.filepath;
        }else{
            url=""; img_path="";
        }
        showLoading("Uploading Signature"+img_number+"...");
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST,
                url,
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoading();
                        String signatureresponse = new String(response.data);
                        if(img_number==1) {

                            uploadSign(id, final_url, pic_list, requestBody.replace(img_path.replace("/","\\/"),signatureresponse),
                                    picsDataSign1, picsDataSign2, 2);
                        }else if(img_number==2){
                            uploadBitmap(id, final_url, requestBody.replace(img_path.replace("/","\\/"),signatureresponse),
                                    pic_list, 0);
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        hideLoading();
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
                params.put("sign", new DataPart(imagename + ".png", O.getBytes(img_path)));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue rQueue = Volley.newRequestQueue(UploadActivity.this);
        rQueue.add(volleyMultipartRequest);
    }

    private void uploadBitmap(final String id, final String final_url, final String requestbody, final ArrayList<PicsData> pics_list,
                              final int currentImage ) {
        final PicsData picsData=pics_list.get(currentImage);
        if(picsData.filepath.contains(".jpg") || picsData.filepath.contains(".png")) {
        showLoading("Uploading Image"+(currentImage+1)+", Please wait...");
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST,
                picsData.url,
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoading();
                        String imageresponse=new String(response.data);

                        if (currentImage>=pics_list.size()-1) {
                            uploadFinal(id, requestbody.replace(picsData.filepath.replace("/","\\/"), imageresponse), final_url);
                        }else{
                            uploadBitmap(id, final_url, requestbody.replace(picsData.filepath.replace("/","\\/"), imageresponse),
                                    pics_list,currentImage+1);
                        }

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        hideLoading();
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
                params.put("image", new DataPart(imagename + ".jpg", O.getBytes(picsData.filepath)));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue rQueue = Volley.newRequestQueue(getApplicationContext());
        rQueue.add(volleyMultipartRequest);
        //Volley.newRequestQueue(this).add(volleyMultipartRequest);
        }else{
            if (currentImage>=pics_list.size()-1) {
                uploadFinal(id, requestbody.replace(picsData.filepath.replace("/","\\/"), ""), final_url);
            }else{
                uploadBitmap(id, final_url, requestbody.replace(picsData.filepath.replace("/","\\/"), ""),
                        pics_list,currentImage+1);
            }
        }
    }

    public void uploadOfficer(final String requestBody, final String id){
        showLoading("Uploading Officer data");
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, submitofficer, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("res officer",response);
                hideLoading();
                db.updatePendingStatus(id,"Y");
                db.deletePendingData(id);
                pendingAdapter.datalist=db.getAllPendingData();
                pendingAdapter.notifyDataSetChanged();
                showDialog();
            }

        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError error) {

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

        RequestQueue requestQueue = Volley.newRequestQueue(UploadActivity.this);
        requestQueue.add(stringRequest);
    }
    protected void showLoading(@NonNull String message0) {
        mProgressDialog = new ProgressDialog(UploadActivity.this);
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
    public String getTask(String taskid){
        if(taskid.equalsIgnoreCase("11")){
            return "Mechanized coach cleaning";
        }else if(taskid.equalsIgnoreCase("12")){
            return "Dry coach cleaning";
        }else if(taskid.equalsIgnoreCase("13")){
            return "Intensive coach cleaning";
        }else if(taskid.equalsIgnoreCase("14")){
            return "Associate Area cleaning";
        }else if(taskid.equalsIgnoreCase("15")){
            return "Pad Locking";
        }else{
            return taskid;
        }
    }
    public void showDialog(){
        new AlertDialog.Builder(this)
                .setMessage("Your Data uploaded successfully,\nand removed from pending page")
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }
}
