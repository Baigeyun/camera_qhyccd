package com.starrysky.service;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.starrysky.helper.Constants;
import com.starrysky.helper.PicHelper;

import java.io.File;


public class VideoFrameSaveIntentService extends IntentService {

    public VideoFrameSaveIntentService() {
        super("VideoFrameSaveIntentService");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Bitmap frameBitmap = (Bitmap) intent.getParcelableExtra("frameBitmap");
        int index = intent.getIntExtra("index",0);
        if( frameBitmap != null ){
            File videoTmpFile = new File(PicHelper.getVideoTmpPath(getApplicationContext()), PicHelper.generateFileName(PicHelper.PREFIX_VIDEO_FRAME , index , Constants.FILE_SUFFIX_JPEG) );
            PicHelper.saveJpegToFile(videoTmpFile.getAbsolutePath(), frameBitmap);
        }
    }
}
