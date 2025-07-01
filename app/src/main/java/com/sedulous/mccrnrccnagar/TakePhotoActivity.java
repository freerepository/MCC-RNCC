package com.sedulous.mccrnrccnagar;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.sedulous.mccrnrccnagar.Model.QanswerData;
import com.sedulous.mccrnrccnagar.utilities.DB;
import com.sedulous.mccrnrccnagar.utilities.GPSTracker;
import com.sedulous.mccrnrccnagar.utilities.MyLocation;
import com.sedulous.mccrnrccnagar.utilities.O;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TakePhotoActivity extends Activity {
    public final static String STORING_Image_pics = "https://mccrncc.projectrailway.in/api/upload_image";
    private String fileUri;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE2 = 400;
    public TextView textViewTrainNo, textViewCoachNo;
    ImageView imageView1, imageView2;
    LinearLayout cameralayout;
    Button btnNext;
    String file_path1, file_path2;
    GPSTracker gps;
    Boolean image1=false, image2= false;
    RelativeLayout trainDetails_header;
    QanswerData qanswerData=null;
    int current_load=0;
    ProgressDialog mProgressDialog;
    String taskId;
    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        db=new DB(TakePhotoActivity.this);
        qanswerData=(QanswerData)getIntent().getSerializableExtra("qdata");
        taskId=qanswerData.taskType.getId();
        current_load=getIntent().getIntExtra("nload",0);
        trainDetails_header=findViewById(R.id.rl_traindetails_header);
        if(taskId.equalsIgnoreCase("11")|| taskId.equalsIgnoreCase("12")||
                taskId.equalsIgnoreCase("13"))
        {
            trainDetails_header.setVisibility(View.VISIBLE);
        } else if(taskId.equalsIgnoreCase("14")){
            trainDetails_header.setVisibility(View.GONE);
        }else if (taskId.equalsIgnoreCase("15")){

        }
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        cameralayout = (LinearLayout) findViewById(R.id.camera_layout);
        imageView1 = (ImageView) findViewById(R.id.img_train1);
        imageView2 = (ImageView) findViewById(R.id.img_train2);
        btnNext = (Button) findViewById(R.id.btn_takeImgNext);
        textViewCoachNo = (TextView) findViewById(R.id.text_coach_no);
        textViewTrainNo = (TextView) findViewById(R.id.tv_train_no);
        try {
            if(!taskId.equalsIgnoreCase("14")) {
                textViewTrainNo.setText(qanswerData.trainData.getTrain_no());
                textViewCoachNo.setText(qanswerData.pageData_list.get(current_load).coachNo);
            }
        } catch (Exception e) {
            Log.e("NP_Exception_TakePhoto", "" + e);
        }

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image1 = true;
                image2 = false;
                checkPermission(CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image2 = true;
                image1 = false;
                checkPermission(CAMERA_CAPTURE_IMAGE_REQUEST_CODE2);
            }
        });
        if (current_load==qanswerData.pageData_list.size()-1) {
            btnNext.setText("Generate Report");
        }
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(file_path1)) {
                    qanswerData.pageData_list.get(current_load).response_img1=""+Math.random();
                }
                if (TextUtils.isEmpty(file_path2)) {
                    qanswerData.pageData_list.get(current_load).response_img2=""+Math.random();
                }
                if (TextUtils.isEmpty(file_path1) || TextUtils.isEmpty(file_path2)) {
                    Toast.makeText(TakePhotoActivity.this, "Images not taken or Not Uploaded yet.", Toast.LENGTH_SHORT).show();
                }
                {

                    if (taskId.equalsIgnoreCase("11") || taskId.equalsIgnoreCase("12") ||
                            taskId.equalsIgnoreCase("13") || taskId.equalsIgnoreCase("15")) {
                        if (current_load==qanswerData.pageData_list.size()-1) {

                                btnNext.setBackgroundResource(R.drawable.button_orange_bg);
                                Intent i = new Intent(TakePhotoActivity.this, ReportActivity.class);
                                i.putExtra("qdata", qanswerData);
                                startActivity(i);

                        } else {
                            Intent res_intent = new Intent();
                            res_intent.putExtra("result", qanswerData);
                            setResult(Activity.RESULT_OK, res_intent);
                            finish();
                        }
                    } else if (taskId.equalsIgnoreCase("14")) {
                        Toast.makeText(TakePhotoActivity.this, "You have completed all the rating of Shift type, please Click on Generate Report!", Toast.LENGTH_SHORT).show();

                            btnNext.setBackgroundResource(R.drawable.button_orange_bg);
                            Intent i = new Intent(TakePhotoActivity.this, ReportActivity.class);
                            i.putExtra("qdata", qanswerData);
                            startActivity(i);

                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    private void checkPermission(final int REQUEST_CODE){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            fileUri= O.cameraProcess(TakePhotoActivity.this, REQUEST_CODE);
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION },100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("IntentData",""+data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            gps = new GPSTracker(TakePhotoActivity.this);
            if (gps.canGetLocation()) {
                Bitmap bitmap = O.reduceScale(fileUri, 800).copy(Bitmap.Config.ARGB_8888, true);
                try {
                    List<Address> addresses; String cityName="", stateName="", countryName="";
                    try {
                        double latitude = gps.getLocation().getLatitude();
                        double longitude = gps.getLocation().getLongitude();
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        cityName = addresses.get(0).getAddressLine(0);
                        stateName = addresses.get(0).getAddressLine(1);
                        countryName = addresses.get(0).getAddressLine(2);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    //cityname.setText(cityName);
                    Canvas cs = new Canvas(bitmap);
                    Paint paint = new Paint();
                    paint.setTextSize(35);
                    paint.setColor(Color.BLUE);
                    paint.setStyle(Paint.Style.FILL);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy, hh:mm a",Locale.US);
                    String datetime = sdf.format(Calendar.getInstance().getTime());
                    cs.drawText(cityName,10, bitmap.getHeight()-5, paint);
                    cs.drawText(datetime,10, bitmap.getHeight()-35, paint);
                    try {
                        file_path1=O.savefile( TakePhotoActivity.this,O.FOLDER_CAMIMG, bitmap, 60);
                        qanswerData.pageData_list.get(current_load).response_img1=file_path1;
                        if (!TextUtils.isEmpty(file_path1) && !TextUtils.isEmpty(file_path2) &&
                                current_load==qanswerData.pageData_list.size()-1) {
                            btnNext.setText("Generate Report");
                        }

                        imageView1.setImageBitmap(BitmapFactory.decodeFile(file_path1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "First allow Location",
                        Toast.LENGTH_SHORT).show();
                MyLocation.displayPromptForEnablingGPS(TakePhotoActivity.this);
            }

        } else if(requestCode ==CAMERA_CAPTURE_IMAGE_REQUEST_CODE2 && resultCode==RESULT_OK) {
            gps = new GPSTracker(TakePhotoActivity.this);
            if (gps.canGetLocation()) {
                Bitmap bitmap = O.reduceScale(fileUri, 800).copy(Bitmap.Config.ARGB_8888, true);
                try {
                    List<Address> addresses;
                    String cityName = "", stateName = "", countryName = "";
                    try {
                        double latitude = gps.getLocation().getLatitude();
                        double longitude = gps.getLocation().getLongitude();
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        cityName = addresses.get(0).getAddressLine(0);
                        stateName = addresses.get(0).getAddressLine(1);
                        countryName = addresses.get(0).getAddressLine(2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //cityname.setText(cityName);
                    Canvas cs = new Canvas(bitmap);
                    Paint paint = new Paint();
                    paint.setTextSize(32);
                    paint.setColor(Color.BLUE);
                    paint.setStyle(Paint.Style.FILL);
                    float height = paint.measureText("yY");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy, hh:mm a",Locale.US);
                    String datetime = sdf.format(Calendar.getInstance().getTime());
                    cs.drawText(cityName,10, bitmap.getHeight()-5, paint);
                    cs.drawText(datetime,10, bitmap.getHeight()-35, paint);
                    try {
                        file_path2 = O.savefile(TakePhotoActivity.this, O.FOLDER_CAMIMG, bitmap, 60);
                        qanswerData.pageData_list.get(current_load).response_img2=file_path2;
                        if (!TextUtils.isEmpty(file_path1) && !TextUtils.isEmpty(file_path2) &&
                                current_load==qanswerData.pageData_list.size()-1) {
                            btnNext.setText("Generate Report");
                        }
                        imageView2.setImageBitmap(BitmapFactory.decodeFile(file_path2));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }} else {
                Toast.makeText(getApplicationContext(), "First allow Location",
                        Toast.LENGTH_SHORT).show();
                MyLocation.displayPromptForEnablingGPS(TakePhotoActivity.this);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else{
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case 2 : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
        }
    }

}


