package com.starrysky.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.starrysky.R;
import com.starrysky.dto.GalleryData;
import com.starrysky.helper.Constants;
import com.starrysky.helper.PicHelper;
import com.starrysky.helper.VideoHelper;
import com.starrysky.listener.OnImageClickListener;
import com.starrysky.listener.OnLongPressListener;
import com.starrysky.listener.OnLongTouchReleaseListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SettingBarView extends ConstraintLayout {
    private static final String TAG = "SettingBarView";
    public final static String ORIENTATION_HORIZONTAL = "horizontal";
    public final static String ORIENTATION_VERTICAL = "vertical";

    @BindView(R.id.captureBtn) ImageView captureBtn;
    @BindView(R.id.galleryPreviewImageView1) ImageView galleryPreviewImageView1;
    @BindView(R.id.galleryPreviewImageView2) ImageView galleryPreviewImageView2;
    @BindView(R.id.fpsTextView)TextView mFpsTextView;
    @BindView(R.id.temperatureTextView)TextView mTemperatureTextView;
    @BindView(R.id.settingBtn) ImageView settingBtn;

    boolean isUp = true;
    boolean isLongPress = false;
    private int mFps;
    private int mTemperature;
    private int mBackgroundColor = Color.BLACK;
    private int mFontColor = Color.WHITE;

    private String mOrientation;

    private boolean isHistDrawing;
    /*private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;
*/

    /* listener */
    private OnClickListener onCaptureBtnClick;
    private OnLongPressListener onCaptureBtnLongPress;
    private OnLongTouchReleaseListener onCaptureBtnLongTouchRelease;


    private OnImageClickListener onGalleryPreviewImageView1ClickListener;
    private OnImageClickListener onGalleryPreviewImageView2ClickListener;
    private OnClickListener onSettingBtnClick;

    /* data */
    private List<GalleryData> latestGalleryDataList;

    public SettingBarView(Context context) {
        super(context);
        init(context,null, 0);
    }

    public SettingBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs, 0);
    }

    public SettingBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context,attrs, defStyle);
    }

    private void init(Context context,AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.SettingBarView, defStyle, 0);

        mFps = a.getInt(R.styleable.SettingBarView_fps,0);
        mTemperature = a.getInt(R.styleable.SettingBarView_temperature,0);
        mBackgroundColor = a.getColor(R.styleable.SettingBarView_backgroundColor,mBackgroundColor);
        mFontColor = a.getColor(R.styleable.SettingBarView_fontColor,mFontColor);
        mOrientation = a.getString(R.styleable.SettingBarView_orientation);

        a.recycle();

        initView(context);
        /*if (a.hasValue(R.styleable.SettingBarView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.SettingBarView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }*/



        // Set up a default TextPaint object
        /*mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);*/

        // Update TextPaint and text measurements from attributes
        //invalidateTextPaintAndMeasurements();

    }

    private void initView(final Context context) {
        View rootView = null;
        rootView = LayoutInflater.from(context).inflate(R.layout.view_setting_bar, this, true);
        ButterKnife.bind(this, rootView);


        mTemperatureTextView.setTextColor(mFontColor);
        mFpsTextView.setTextColor(mFontColor);

        rootView.setBackgroundColor(mBackgroundColor);

        galleryPreviewImageView1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if( onGalleryPreviewImageView1ClickListener != null ){
                    String filePath = latestGalleryDataList.get(0).getSourceFilePath();
                    onGalleryPreviewImageView1ClickListener.onClick(filePath);
                }
            }
        });

        galleryPreviewImageView2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if( onGalleryPreviewImageView1ClickListener != null ){
                    String filePath = latestGalleryDataList.get(1).getSourceFilePath();
                    onGalleryPreviewImageView1ClickListener.onClick(filePath);
                }
            }
        });



        final GestureDetectorCompat gd = new GestureDetectorCompat(context , new GestureDetector.OnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

                return true;
            }

            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {
            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {
                isLongPress = true;
                if( isUp == false ){
                    if( onCaptureBtnLongPress != null ){
                        onCaptureBtnLongPress.onLongPress();
                    }


                }
            }
        });
        captureBtn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if( motionEvent.getAction() == motionEvent.ACTION_UP ){
                    isUp = true;

                    if( isLongPress == true ){
                        if( onCaptureBtnLongTouchRelease != null ){
                            onCaptureBtnLongTouchRelease.onTouchRelease();
                        }
                    }else{
                        if( onCaptureBtnClick != null ){
                            onCaptureBtnClick.onClick(view);
                        }
                    }
                }else if( motionEvent.getAction() == motionEvent.ACTION_DOWN ){
                    isUp = false;
                    isLongPress = false;
                }

                gd.onTouchEvent(motionEvent);
                return true;
            }
        });

        settingBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if( onSettingBtnClick != null ){
                    onSettingBtnClick.onClick(v);
                }
            }
        });
    }

    public void setOnCaptureBtnClick(OnClickListener onCaptureBtnClick) {
        this.onCaptureBtnClick = onCaptureBtnClick;
    }

    public void setLatestGalleryData(List<String> latestGalleryList) {
        if( latestGalleryList != null ){
            latestGalleryDataList = new ArrayList<>();
            for(String sourceFilePath : latestGalleryList){
                GalleryData galleryData = new GalleryData();

                galleryData.setSourceFilePath(sourceFilePath);
                if(sourceFilePath.endsWith(Constants.FILE_SUFFIX_JPEG)){
                    galleryData.setType(GalleryData.TYPE_IMAGE);
                    galleryData.setPreviewImageBytes(null);
                }else if( sourceFilePath.endsWith(Constants.FILE_SUFFIX_MP4) ){
                    galleryData.setType(GalleryData.TYPE_VIDEO);
                    galleryData.setPreviewImageBytes( PicHelper.readJpegBytes( VideoHelper.getFirstFrame(new File(sourceFilePath),0) ));
                }

                latestGalleryDataList.add(galleryData);
            }
        }

        reloadLatestGalleryImageView();
    }

    private void reloadLatestGalleryImageView() {
        if( latestGalleryDataList != null && latestGalleryDataList.size() > 0 ){
            int galleryListSize = latestGalleryDataList.size();
            if( galleryListSize == 1 ){
                GalleryData galleryData = latestGalleryDataList.get(0);

                if( galleryData.getType().equals(GalleryData.TYPE_IMAGE) ){
                    File file = new File( galleryData.getSourceFilePath() );
                    Uri imageUri = Uri.fromFile(file);
                    Glide.with(this).load(imageUri).into(galleryPreviewImageView1);
                }else if( galleryData.getType().equals(GalleryData.TYPE_VIDEO) ){
                    Glide.with(this).load(galleryData.getPreviewImageBytes()).into(galleryPreviewImageView1);
                }
                galleryPreviewImageView1.setVisibility(View.VISIBLE);
                galleryPreviewImageView2.setVisibility(View.GONE);
            }else{
                GalleryData galleryData1 = latestGalleryDataList.get(0);
                GalleryData galleryData2 = latestGalleryDataList.get(1);

                File file = null;
                Uri imageUri = null;
                if( galleryData1.getType().equals(GalleryData.TYPE_IMAGE) ){
                    file = new File(galleryData1.getSourceFilePath());
                    imageUri = Uri.fromFile(file);
                    Glide.with(this).load(imageUri).into(galleryPreviewImageView1);
                }else {
                    Glide.with(this).load(galleryData1.getPreviewImageBytes()).into(galleryPreviewImageView1);
                }


                if( galleryData2.getType().equals( GalleryData.TYPE_IMAGE ) ){
                    file = new File(galleryData2.getSourceFilePath());
                    imageUri = Uri.fromFile(file);
                    Glide.with(this).load(imageUri).into(galleryPreviewImageView2 );
                }else {
                    Glide.with(this).load(galleryData2.getPreviewImageBytes()).into(galleryPreviewImageView2);
                }

                galleryPreviewImageView1.setVisibility(View.VISIBLE);
                galleryPreviewImageView2.setVisibility(View.VISIBLE);
            }
        }else{
            galleryPreviewImageView1.setVisibility(View.GONE);
            galleryPreviewImageView2.setVisibility(View.GONE);

        }
    }

    public void setOnGalleryPreviewImageView1ClickListener(OnImageClickListener onGalleryPreviewImageView1ClickListener) {
        this.onGalleryPreviewImageView1ClickListener = onGalleryPreviewImageView1ClickListener;
    }

    public void setOnGalleryPreviewImageView2ClickListener(OnImageClickListener onGalleryPreviewImageView2ClickListener) {
        this.onGalleryPreviewImageView2ClickListener = onGalleryPreviewImageView2ClickListener;
    }

    public void setOnCaptureBtnLongPress(OnLongPressListener onCaptureBtnLongPress) {
        this.onCaptureBtnLongPress = onCaptureBtnLongPress;
    }

    public void setOnCaptureBtnLongTouchRelease(OnLongTouchReleaseListener onCaptureBtnLongTouchRelease) {
        this.onCaptureBtnLongTouchRelease = onCaptureBtnLongTouchRelease;
    }
    public void setFps(Integer fps){
        this.mFps = fps;
        if( this.mFpsTextView != null ){
            this.mFpsTextView.setText(String.valueOf(mFps) + "fps");
        }
    }

    public void setTemperature(int temperature){
        this.mTemperature = temperature;
        if( this.mTemperatureTextView != null ){
            this.mTemperatureTextView.setText(String.valueOf(temperature) + "℃");
        }
    }

    public OnClickListener getOnSettingBtnClick() {
        return onSettingBtnClick;
    }

    public void setOnSettingBtnClick(OnClickListener onSettingBtnClick) {
        this.onSettingBtnClick = onSettingBtnClick;
    }
}
