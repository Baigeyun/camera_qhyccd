package com.starrysky.asynctask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.starrysky.activity.CameraViewportActivity;
import com.starrysky.helper.BitmapHelper;
import com.starrysky.helper.Constants;
import com.starrysky.helper.EventEmitter;
import com.starrysky.helper.PicHelper;
import com.starrysky.helper.ProgressDialogHelper;

import org.jcodec.api.android.SequenceEncoder;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class SaveVideoTask extends AsyncTask<Void,Integer,String> {
    private static final String TAG = "SaveVideoTask";
    private Context context ;

    private SequenceEncoder encoder;
    private File videoFile;
    private List<int[]> bmpByteAryList;
    public SaveVideoTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        File videoTmpPath = PicHelper.getVideoTmpPath(context);
        Toast.makeText(context,"### video images:" + videoTmpPath.list().length,Toast.LENGTH_LONG).show();

        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG,"### SaveVideoTask onPost");
        Toast.makeText(context,"视频已生成",Toast.LENGTH_SHORT).show();

        // clear frame temp directory
        File videoTmpPathFile = PicHelper.getVideoTmpPath(context);
        if( videoTmpPathFile.exists() ){
            for(File file : videoTmpPathFile.listFiles()){
                file.delete();
            }
        }

    }

    @Override
    protected String doInBackground(Void... param) {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Log.e(TAG,e.getMessage());
        }

        File videoTmpPath = PicHelper.getVideoTmpPath(context);
        String prefix = PicHelper.PREFIX_VIDEO_FRAME;

        final File videoFile = new File(PicHelper.getSavePath(context) ,PicHelper.generateVideoFileName());

        File[] frameImageFileAry = videoTmpPath.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String filename) {
                if( filename.endsWith(Constants.FILE_SUFFIX_JPEG) ){
                    return true;
                }
                return false;
            }
        });
        if( frameImageFileAry != null && frameImageFileAry.length > 0 ){
            try{

                SequenceEncoder enc = new SequenceEncoder(videoFile);
                for( File frameFile : frameImageFileAry ){
                    Bitmap bitmap = BitmapHelper.getFitSampleBitmap(frameFile.getAbsolutePath(),800,600);
                    encoder.encodeImage(bitmap);

                    bitmap.recycle();
                }

                encoder.finish();

            }catch(Exception e){
                Log.e(TAG, e.getMessage());
                return null;
            } finally{
                // clear frame temp directory
                File videoTmpPathFile = PicHelper.getVideoTmpPath(context);
                if( videoTmpPathFile.exists() ){
                    for(File file : videoTmpPathFile.listFiles()){
                        file.delete();
                    }
                }
            }
        }


        return "success";
    }

    private void deleteTmpFile() {
        // delete old video tmp file
        File videoTmpPath = PicHelper.getVideoTmpPath(context);
        if( videoTmpPath.exists() ){
            File[] files = videoTmpPath.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String filename) {
                    if( filename.startsWith(PicHelper.PREFIX_VIDEO_FRAME) && filename.endsWith(Constants.FILE_SUFFIX_JPEG) ){
                        return true;
                    }
                    return false;
                }
            });

            if( files != null && files.length > 0 ){
                for(File file : files ){
                    boolean success = file.delete();

                    if( success == true ){
                        Log.d(TAG, "### delete old video tmp file success:" + file.getAbsolutePath());
                    }else{
                        Log.d(TAG, "### delete old video tmp file failed:" + file.getAbsolutePath());
                    }
                }
            }

        }
    }


}
