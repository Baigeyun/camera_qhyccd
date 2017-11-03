package com.starrysky.helper;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

/**
 * Created by sbin on 2017-10-29.
 */

public class PicHelperExt  {


    private static final String TAG = "PicHelperExt";
    private static PicHelperExt instance;
    private PicHelperExt()
    {
    }
    public static synchronized PicHelperExt getInstance()
    {
        if(instance == null)
        {
            return new PicHelperExt();
        }
        return instance;
    }
//    private  int brightness(int rgb) {
//        return rgb & 0x0000FF;
//    }


    public  void colorImageIfNeeded(Integer sensorType,Bitmap bmp)
    {
        if( sensorType != null && sensorType.equals(Constants.SENSOR_TYPE_RGB) ) {
            long startTime = System.currentTimeMillis();
            int r = 0, g = 0, b = 0;
            int wVal = bmp.getWidth() -2;
            int hVal = bmp.getHeight() -2;
            int tmpVal  = 0;
            int col = 0;
            int row = 0;
            for (int x = 2; x < wVal; x++) {
                for (int y = 2; y < hVal; y++) {
                     col = x % 2;
                     row = y % 2;
                     tmpVal = col + row ;
                    //0,0 B    1,1 R    0,1 1,0 G
                    if (tmpVal == 0) {
                        // B
                        b = brightnessfun1(brightnessADD (brightness(bmp.getPixel(x, y - 1)) , brightness(bmp.getPixel(x, y + 1))));
                        g = brightness(bmp.getPixel(x, y));
                        r = brightnessfun1 (brightnessADD (brightness(bmp.getPixel(x - 1, y)) ,brightness(bmp.getPixel(x + 1, y))));

                    } else if (tmpVal  == 1) {
                        // G
                        if (row == 0) {
                            r = brightness(bmp.getPixel(x, y));
                            g = brightnessfun2 (brightnessADD4(brightness(bmp.getPixel(x - 1, y)), brightness(bmp.getPixel(x + 1, y)) ,brightness(bmp.getPixel(x, y - 1)) ,brightness(bmp.getPixel(x, y + 1))));
                            b = brightnessfun2 (brightnessADD4(brightness(bmp.getPixel(x + 1, y - 1)) ,brightness(bmp.getPixel(x + 1, y + 1)) , brightness(bmp.getPixel(x - 1, y - 1)) , brightness(bmp.getPixel(x - 1, y + 1))));
                        } else {
                            r = brightnessfun2(brightnessADD4(brightness(bmp.getPixel(x + 1, y - 1)) , brightness(bmp.getPixel(x + 1, y + 1)) , brightness(bmp.getPixel(x - 1, y - 1)) , brightness(bmp.getPixel(x - 1, y + 1))));
                            g = brightnessfun2(brightnessADD4(brightness(bmp.getPixel(x - 1, y)) ,brightness(bmp.getPixel(x + 1, y)) , brightness(bmp.getPixel(x, y - 1)) , brightness(bmp.getPixel(x, y + 1))));
                            b = brightness(bmp.getPixel(x, y));
                        }
                    } else {
                        // R
                        r = brightnessfun1(brightnessADD (brightness(bmp.getPixel(x, y - 1)) , brightness(bmp.getPixel(x, y + 1))));
                        g = brightness(bmp.getPixel(x, y));
                        b = brightnessfun1 (brightnessADD(brightness(bmp.getPixel(x - 1, y)) , brightness(bmp.getPixel(x + 1, y))));

                    }
                    bmp.setPixel(x, y, Color.rgb(r, g, b));
                }
            }
            long end = System.currentTimeMillis() - startTime;
            Log.v(TAG ,"time = " + end + stringFromJNI());
        }
    }

    public native String stringFromJNI();

    public native int brightness(int rgb);

    public native int brightnessADD(int a,int b);

    public native int brightnessADD4(int a,int b,int c,int d);

    public native int brightnessfun1(int a);

    public native int brightnessfun2(int a);


    // used to load the 'native-lib' library on this function
    static {
        System.loadLibrary("native-lib");
    }
}
