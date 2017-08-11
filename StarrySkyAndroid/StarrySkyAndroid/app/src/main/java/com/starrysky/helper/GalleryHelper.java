package com.starrysky.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

public class GalleryHelper {
    private static final String TAG = "GalleryHelper";

    /**
     * Save image to gallery
     * @param context
     * @param bitmap
     * @param title
     * @param description
     * @return
     */
    public static String saveToGallery(Context context, Bitmap bitmap,String title,String description){
        String savedImageURL = MediaStore.Images.Media.insertImage(
                context.getContentResolver(),
                bitmap,
                title,
                description
        );

        return savedImageURL;
    }

    /**
     * notify gallery to scan new media in app directory
     * @param context
     * @param mediaPath
     */
    public static void linkToGallery(Context context , File mediaPath){
        try{
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mediaPath)));
        }catch(Exception e){
            Log.e(TAG,"Notify gallery scan new media fail");
        }
    }
}
