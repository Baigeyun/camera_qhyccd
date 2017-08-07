package com.starrysky.helper;

import java.io.File;

/**
 * Created by Eden on 2017/6/25.
 */

public class FileHelper {
    public static void deleteAllFile(File videoTmpPath) {
        // clear frame temp directory
        if( videoTmpPath.exists() ){
            for(File file : videoTmpPath.listFiles()){
                file.delete();
            }
        }
    }
}
