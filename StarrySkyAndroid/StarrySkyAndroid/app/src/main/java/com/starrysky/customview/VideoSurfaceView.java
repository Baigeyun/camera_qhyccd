package com.starrysky.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.starrysky.R;


public class VideoSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private Thread videoThread;
    private boolean isPlaying ;

    public VideoSurfaceView(Context context) {
        super(context);
        init(null, 0);
    }

    public VideoSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public VideoSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.VideoSurfaceView, defStyle, 0);


        a.recycle();

        holder = this.getHolder();
        holder.addCallback(this);

    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void play() {
        if( this.videoThread != null ){
            this.videoThread.start();
            isPlaying = true;
        }
    }

    public void stop() {
        if( this.videoThread != null ){
            this.videoThread.interrupt();

            isPlaying = false;
        }
    }


    public void setThread(Thread thread){
        this.videoThread = thread;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

}
