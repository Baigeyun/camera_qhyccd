package com.starrysky.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.starrysky.R;


public class CameraTopBarView extends ConstraintLayout {
    private MenuPopup.OnSettingSetListener onSettingSetListener;

    public CameraTopBarView(Context context) {
        super(context);
        init(context,null, 0);
    }

    public CameraTopBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs, 0);
    }

    public CameraTopBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context,attrs, defStyle);
    }

    private void init(Context context,AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CameraTopBarView, defStyle, 0);

        a.recycle();
        initView(context);
    }

    private void initView(final Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_camera_topbar, this, true);

        ImageView settingImageView = (ImageView)findViewById(R.id.settingImageView);

        settingImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuPopup popupMenu = new MenuPopup(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                if( onSettingSetListener != null ){
                    popupMenu.setOnSettingSetListener(onSettingSetListener);
                }

            }
        });
    }

    public void setOnSettingSetListener(MenuPopup.OnSettingSetListener onSettingSetListener) {
        this.onSettingSetListener = onSettingSetListener;
    }
}
