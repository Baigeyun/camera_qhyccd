package com.starrysky;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.tencent.bugly.crashreport.CrashReport;


public class BaseApplication extends Application {
    private static final String TAG = "BaseApplication";

    @Override
    public void onCreate() {
        super.onCreate();


        CrashReport.initCrashReport(getApplicationContext(), "192e7535ff", true /* prod 设置为 false*/);
    }


}
