package com.starrysky.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.starrysky.BaseActivity;
import com.starrysky.R;
import com.starrysky.helper.AlertHelper;
import com.starrysky.helper.PicHelper;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                Dexter.withActivity(SplashActivity.this)
                        .withPermissions(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        ).withListener(new MultiplePermissionsListener() {

                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if( report.areAllPermissionsGranted() == false ){
                            AlertHelper.showOKDialog(SplashActivity.this, "No Permission", "You have denied some permissions, please open all the permissions you need in your settings", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SplashActivity.this.finish();
                                }
                            });
                        }else{
                            File savePath = PicHelper.getSavePath(SplashActivity.this);

                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(SplashActivity.this, ScanResultActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            },5000);

                        }


                    }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



    }



}
