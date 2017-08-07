package com.starrysky.helper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.hist.CalcHistogram;


public class ImageHistogramHelper {

    private ImageHistogramHelper(){}

    static class Holder{
        public final static ImageHistogramHelper INSTANCE = new ImageHistogramHelper();
    }

    public static ImageHistogramHelper getInstance(){
        return Holder.INSTANCE;
    }

    public Bitmap drawHist(ImageProcessor imageProcessor, Paint paint, int width, int height) {

        CalcHistogram calcHistogram = new CalcHistogram();
        int bins = 127;
        int[][] hist = new int[imageProcessor.getChannels()][bins];
        calcHistogram.calcHSVHist(imageProcessor,bins,hist,true);
        Bitmap bm = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);

        float step = width/127;
        int xoffset;
        int yoffset;
        int channels = imageProcessor.getChannels();

        int h = height;
        int[] colors = new int[]{Color.argb(77,255,0,0),Color.argb(77,0,255,0),Color.argb(77,0,0,255)};
        for (int i=0;i<channels;i++) {

            paint.setColor(colors[i]);
            for (int j=0;j<bins;j++) {

                xoffset = (int)(j*step);
                yoffset = hist[i][j]*h/255;
                canvas.drawRect(xoffset,h-yoffset,xoffset+step,h,paint);
            }
        }

        return bm;
    }
}
