package com.starrysky.helper;

import android.graphics.Bitmap;
import android.util.Log;

import org.jcodec.api.android.FrameGrab;

import java.io.File;


public class VideoHelper {
    private static final String TAG = "VideoHelper";
    public static Bitmap getFirstFrame(File mp4File,int frameNumber){
        try {
            if( frameNumber < 0 ){
                frameNumber = 0 ;
            }
            Bitmap bitmap = FrameGrab.getFrame(mp4File, frameNumber);

            return bitmap;
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
            return null;
        }
    }
}
