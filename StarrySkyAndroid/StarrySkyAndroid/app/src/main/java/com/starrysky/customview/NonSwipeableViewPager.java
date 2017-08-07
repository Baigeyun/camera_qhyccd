package com.starrysky.customview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class NonSwipeableViewPager extends ViewPager {
    private static final String TAG = "NonSwipeableViewPager";
    private float x1,x2;
    private float y1,y2;
    static final int MIN_DISTANCE = 150;

    public NonSwipeableViewPager(Context context) {
        super(context);
    }

    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();
                float deltaX = x2 - x1;
                float deltaY = y2 - y1;

                if (Math.abs(deltaY) > Math.abs(deltaX) && Math.abs(deltaY) > MIN_DISTANCE)
                {
                    return true;
                }
                else
                {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return false;
    }
}
