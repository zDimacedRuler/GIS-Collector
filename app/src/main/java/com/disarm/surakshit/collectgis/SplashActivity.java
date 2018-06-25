package com.disarm.surakshit.collectgis;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.disarm.surakshit.collectgis.Util.Constants;

import java.io.File;

public class SplashActivity extends AppCompatActivity {

    public File cmsFolder = Environment.getExternalStoragePublicDirectory(Constants.CMS_DIRECTORY);
    public File workingFolder = Environment.getExternalStoragePublicDirectory(Constants.CMS_WORKING);
    public File tempKMLFolder = Environment.getExternalStoragePublicDirectory(Constants.CMS_TEMP_KML);

    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,};

    public static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

    EditText phoneEdit;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        creatingFolder();
        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }
        if (isNumberPresent()) {
            callMainActivity();
        } else {
            setContentView(R.layout.activity_splash);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            phoneEdit = findViewById(R.id.splash_phone_text);
            submitButton = findViewById(R.id.splash_submit_button);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number = phoneEdit.getText().toString();
                    if (number.length() == 10 && number.matches("^[789]\\d{9}$")) {
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(Constants.PHONE_NO, number).apply();
                        callMainActivity();
                    } else {
                        phoneEdit.setError("Enter Valid Number");
                    }
                }
            });
        }

    }

    private void callMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void creatingFolder() {
        if (!cmsFolder.exists())
            cmsFolder.mkdir();
        if (!workingFolder.exists())
            workingFolder.mkdir();
        if (!tempKMLFolder.exists())
            tempKMLFolder.mkdir();
    }

    private boolean isNumberPresent() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String phoneNumber = prefs.getString(Constants.PHONE_NO, "");
        if (phoneNumber.equals(""))
            return false;
        return true;
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != REQUEST_CODE_REQUIRED_PERMISSIONS)
            return;
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Cannot start without permissions", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }
        recreate();
    }

}
