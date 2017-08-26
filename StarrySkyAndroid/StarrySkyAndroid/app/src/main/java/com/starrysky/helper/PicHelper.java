package com.starrysky.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import org.beyka.tiffbitmapfactory.CompressionScheme;
import org.beyka.tiffbitmapfactory.Orientation;
import org.beyka.tiffbitmapfactory.TiffSaver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class PicHelper {
    private static final String TAG = "PicHelper";
    public static final String PREFIX_VIDEO_FRAME = "VideoFrame_";
    private static String sdBasePath = getSDCardPath() + "/qhyccd";

    public static File getSavePath(Context context) {
        File path;
       if (hasSDCard()) { // SD card
            path = new File(sdBasePath);
            if( !path.exists() ){
                path.mkdir();
            }
        } else {
            path = new File(Environment.getDataDirectory().getAbsolutePath() + "/data/" + context.getPackageName() );

            if( !path.exists() ){
                path.mkdir();
            }
        }
        return path;
    }

    public static File getVideoTmpPath(Context context) {
        File path;
        if (hasSDCard()) { // SD card

            path = new File(sdBasePath);
            if( !path.exists() ){
                path.mkdir();
            }

            File videoTmpFile = new File(sdBasePath, "VideoTmp");
            if( !videoTmpFile.exists() ){
                videoTmpFile.mkdir();
            }

            return videoTmpFile;
        } else {
            path = new File(Environment.getDataDirectory().getAbsolutePath() + "/data/" + context.getPackageName() );

            if( !path.exists() ){
                path.mkdir();
            }

            return path;
        }

    }
    public static String getCacheFilename(Context context) {
        File f = getSavePath(context);
        return f.getAbsolutePath() + "/cache" + Constants.FILE_SUFFIX_JPEG;
    }

    public static Bitmap loadFromFile(String filename) {
        try {
            File f = new File(filename);
            if (!f.exists()) { return null; }
            Bitmap tmp = BitmapFactory.decodeFile(filename);
            return tmp;
        } catch (Exception e) {
            return null;
        }
    }
    public static Bitmap loadFromCacheFile(Context context) {
        return loadFromFile(getCacheFilename(context));
    }
    public static void saveToCacheFile(Context context,Bitmap bmp) {
        saveJpegToFile(getCacheFilename(context),bmp);
    }
    public static void saveJpegToFile(String filename, Bitmap bmp) {
        try {
            FileOutputStream out = new FileOutputStream(filename);
            bmp.compress(Bitmap.CompressFormat.JPEG, 70, out);
            out.flush();
            out.close();

        } catch(Exception e) {
            Log.e(TAG,e.getMessage());
        }
    }

    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }
    public static String getSDCardPath() {
        File path = Environment.getExternalStorageDirectory();
        return path.getAbsolutePath();
    }

    /*public static String generatePicFileName() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return timestamp + Constants.FILE_SUFFIX_JPEG;
    }*/

    public static String generateVideoFileName() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return timestamp + Constants.FILE_SUFFIX_MP4;
    }

    public static String generateFileName(String prefix,int index,String suffix) {
        String filename = prefix + index + suffix;
        return filename;
    }
    public static String generateUUIDFileName(String suffix){
        UUID uuid = UUID.randomUUID();
        return uuid.toString() + suffix;
    }


   public static byte[] readJpegBytes(Bitmap bitmap){
       ByteArrayOutputStream stream = null;
       byte[] bitmapBytes = null;
       try{
           stream = new ByteArrayOutputStream();
           bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
           bitmapBytes = stream.toByteArray();
       }catch(Exception e){
           Log.e(TAG,e.getMessage());
       }finally{
           try{
               stream.close();
           }catch(Exception e ){
               Log.e(TAG,e.getMessage());
           }
       }

       return bitmapBytes;
   }

    public static String generateJpegFileName() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return timestamp + Constants.FILE_SUFFIX_JPEG;
    }

    public static void savePngToFile(String absolutePath, Bitmap bitmap) {

        try {
            FileOutputStream out = new FileOutputStream(absolutePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 70, out);
            out.flush();
            out.close();

        } catch(Exception e) {
            Log.e(TAG,e.getMessage());
        }
    }

    public static String generatePngFileName() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return timestamp + Constants.FILE_SUFFIX_PNG;
    }

    public static String generateBmpFileName() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return timestamp + Constants.FILE_SUFFIX_BMP;
    }


    public static void saveBmpToFile(String absolutePath, Bitmap bitmap) {
        BmpHelper bmpHelper = new BmpHelper();
        bmpHelper.save(bitmap, absolutePath);
    }

    public static String generateTiffFileName() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return timestamp + Constants.FILE_SUFFIX_TIFF;
    }

    public static void saveTiffToFile(String absolutePath, Bitmap bitmap) {
        TiffSaver.SaveOptions options = new TiffSaver.SaveOptions();
        options.compressionScheme = CompressionScheme.LZW;
        options.orientation = Orientation.LEFT_TOP;
        options.author = "baigeyun";
        options.copyright = "baigeyun qhyccd";

        boolean saved = TiffSaver.saveBitmap(absolutePath, bitmap, options);
    }
}
