package com.starrysky.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;

public class GalleryHelper {
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
}
