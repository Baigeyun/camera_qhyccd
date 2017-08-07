package com.starrysky.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.starrysky.R;
import com.starrysky.helper.Constants;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryPreviewActivity extends AppCompatActivity {

    @BindView(R.id.imageView)ImageView imageView;
    @BindView(R.id.videoView)VideoView videoView;

    private String filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_preview);



        filePath = getIntent().getStringExtra("filePath");
        if( filePath == null ){
            this.finish();
        }

        ButterKnife.bind(this);

        imageView.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);


        if( filePath.endsWith(Constants.FILE_SUFFIX_JPEG) ){
            imageView.setVisibility(View.VISIBLE);
            showImage();
        }else if( filePath.endsWith(".mp4") ){
            videoView.setVisibility(View.VISIBLE);
            showVideo();
        }
    }

    private void showVideo() {
        Toast.makeText(getApplicationContext(),"获取视频：" + filePath,Toast.LENGTH_SHORT).show();
        Uri uri = Uri.fromFile(new File(filePath));
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
    }

    private void showImage() {
        File file = new File(filePath);
        Uri imageUri = Uri.fromFile(file);
        Glide.with(this).load(imageUri).into(imageView);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
