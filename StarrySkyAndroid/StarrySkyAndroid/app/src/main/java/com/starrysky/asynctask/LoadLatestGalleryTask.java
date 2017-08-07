package com.starrysky.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.starrysky.helper.Constants;
import com.starrysky.helper.EventEmitter;
import com.starrysky.helper.PicHelper;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class LoadLatestGalleryTask extends AsyncTask<Integer,Integer,List<String>> {
    private int queryCount;
    private Context context ;

    public LoadLatestGalleryTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPostExecute(List<String> mediaFileUrlList) {
        EventEmitter.emitLoadLatestGalleryDoneMessageEvent(mediaFileUrlList);
    }

    @Override
    protected List<String> doInBackground(Integer... integers) {
        if( integers != null && integers.length > 0 ){
            queryCount = integers[0];
        }
        File savePathFile = PicHelper.getSavePath(context);

        File files[] = savePathFile.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                if( name != null && (name.endsWith(Constants.FILE_SUFFIX_JPEG) || name.endsWith(Constants.FILE_SUFFIX_MP4)) ){
                    return true;
                }
                return false;
            }
        });


        if( files != null && files.length > 0 ){
            Arrays.sort(files, new Comparator<File>(){
                public int compare(File o1, File o2) {
                    if ( o1.lastModified() > o2.lastModified()) {
                        return -1;
                    } else if (((File)o1).lastModified() < ((File)o2).lastModified()) {
                        return +1;
                    } else {
                        return 0;
                    }
                }
            });

            List<String> mediaFileUrlList = new ArrayList<String>();
            int cnt = 0;
            if( queryCount < files.length ){
                for( int i = 0 ; i < queryCount ; i++ ){
                    File curFile = files[i];
                    mediaFileUrlList.add(curFile.getAbsolutePath());
                }
            }else{
                for( File file : files){
                    mediaFileUrlList.add(file.getAbsolutePath());
                }
            }
            return mediaFileUrlList;
        }
        return null;
    }
}
