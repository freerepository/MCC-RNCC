package com.sedulous.mccrnrccnagar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sedulous.mccrnrccnagar.resonses.ResponseClass;
import com.sedulous.mccrnrccnagar.resonses.UserData;
import com.sedulous.mccrnrccnagar.services.Web;
import com.google.gson.Gson;
import com.sedulous.mccrnrccnagar.utilities.O;
import com.sedulous.mccrnrccnagar.utilities.SP;
import com.sedulous.mccrnrccnagar.utilities.VolleySingleton;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    private EditText etPhone,etPassword;
    private Button btnSubmit;
    ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindView();
    }

    public void bindView() {

        etPhone = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnSubmit = (Button) findViewById(R.id.Button_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etPhone.getText().toString()) || TextUtils.isEmpty(etPassword.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "Please enter mobile number and password",
                            Toast.LENGTH_SHORT).show();
                } else if (etPhone.getText().toString().length() != 10 ) {
                    Toast.makeText(LoginActivity.this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                }else if (etPassword.getText().toString().length()<5 ) {
                    Toast.makeText(LoginActivity.this, "Please enter a valid password more than 5 char", Toast.LENGTH_SHORT).show();
                }else{
                    if (O.checkNetwork(LoginActivity.this)) {
                        login(etPhone.getText().toString(), etPassword.getText().toString());
                    } else {
                        Toast.makeText(LoginActivity.this, R.string.internet_connection_error, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    public void login(final String uid, final String password){
        btnSubmit.setEnabled(false);

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone_no", uid );
            jsonObject.put("password", password);
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.e("request_login",jsonObject.toString());
        showLoading("Please wait...");
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, Web.LOGIN_URL,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("response_login", response.toString());
                hideLoading();
                try {

                        ResponseClass responseClass=(new Gson()).fromJson(response.toString(),ResponseClass.class);
                        if (responseClass.getSuccess()==1) {
                            SP.setUserData(LoginActivity.this, (new Gson()).toJson(responseClass.getUserData().get(0)));
                            UserData userData=(UserData)(new Gson()).fromJson(SP.getUserData(LoginActivity.this),UserData.class);
                            if (userData.getUser_type().equalsIgnoreCase("Supervisor")) {
                                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                                i.putExtra("from","login");
                                startActivity(i);
                                finish();
                            }
                            else if (userData.getUser_type().equalsIgnoreCase("Officer")) {
                                Intent i = new Intent(LoginActivity.this, OfficerActivity.class);
                                i.putExtra("from","login");
                                startActivity(i);
                                finish();
                            }
                            btnSubmit.setBackgroundResource(R.drawable.button_orange_bg);
                        } else {
                            btnSubmit.setEnabled(true);
                            Toast.makeText(LoginActivity.this, "Please try again!", Toast.LENGTH_SHORT).show();
                        }
                } catch (Exception e) {
                    e.printStackTrace();
                    btnSubmit.setEnabled(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",""+error);
                hideLoading();
                Toast.makeText(LoginActivity.this, "Login failled...",
                        Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
        super.onBackPressed();
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
