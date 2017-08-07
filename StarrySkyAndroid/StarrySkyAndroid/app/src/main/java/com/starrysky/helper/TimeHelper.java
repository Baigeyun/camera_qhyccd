package com.starrysky.helper;

public class TimeHelper {
    public static long toUs(String exposureTime) {
        String numStr = null;
        if( exposureTime.endsWith("us") ){
            numStr = exposureTime.substring(0,exposureTime.indexOf("us"));
            return Long.parseLong(numStr);
        }else if( exposureTime.endsWith("ms") ){
            numStr = exposureTime.substring(0,exposureTime.indexOf("ms"));
            return Long.parseLong(numStr) * 1000L;
        }else if( exposureTime.endsWith("s") ){
            numStr = exposureTime.substring(0,exposureTime.indexOf("s"));
            return Long.parseLong(numStr) * 1000L * 1000L;
        }
        return 0L;

    }


}
