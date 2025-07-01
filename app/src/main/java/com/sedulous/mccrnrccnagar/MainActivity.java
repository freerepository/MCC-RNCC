package com.sedulous.mccrnrccnagar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import com.sedulous.mccrnrccnagar.resonses.UserData;
import com.google.gson.Gson;
import com.sedulous.mccrnrccnagar.utilities.O;
import com.sedulous.mccrnrccnagar.utilities.SP;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            O.deleteRecursive(new File(getExternalFilesDir(null).getAbsolutePath(), O.FOLDER_TEMP));
        }catch (Exception e){
            e.printStackTrace();
        }
//        try{
//            O.deleteRecursive(new File(getExternalFilesDir(null).getAbsolutePath(), "Camera"));
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        Thread thread=new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    if (SP.getUserData(MainActivity.this).isEmpty()) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }else {
                        UserData userData=(UserData)(new Gson()).fromJson(SP.getUserData(MainActivity.this),UserData.class);
                        if (userData.getUser_type().equalsIgnoreCase("Supervisor")) {
                            Intent i = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else if (userData.getUser_type().equalsIgnoreCase("Officer")) {
                            Intent i = new Intent(MainActivity.this, OfficerActivity.class);
                            startActivity(i);
                            finish();
                        }
                    /*    Intent intent = new Intent( MainActivity.this, HomeActivity.class);
                        startActivity(intent);*/
                    }
                }
            }
        };thread.start();

    }
}
