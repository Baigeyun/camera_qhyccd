package com.starrysky.fragment;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.starrysky.R;
import com.starrysky.helper.Constants;
import com.starrysky.listener.OnSettingDialogOkBtnClick;

import java.io.File;

public class GalleryDialogFragment extends DialogFragment {

    private ImageView imageView;
    private VideoView videoView;

    private OnSettingDialogOkBtnClick onSettingDialogOkBtnClick;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_gallery, null);

        imageView = (ImageView) view.findViewById(R.id.imageView);
        videoView = (VideoView) view.findViewById(R.id.videoView);

        builder.setView(view);
                // Add action buttons
                /*.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if( onSettingDialogOkBtnClick != null ){
                            String resolution = resolutionSpinner.getSelectedItem().toString();
                            String frequency = frequencySpinner.getSelectedItem().toString();
                            String traffice = trafficeSpinner.getSelectedItem().toString();
                            String speed = speedSpinner.getSelectedItem().toString();
                            String exposureTime = exposureTimeSpinner.getSelectedItem().toString();

                            onSettingDialogOkBtnClick.onClick(resolution,frequency,traffice,speed,exposureTime);
                        }
                    }
                });*/
                /*.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LoginDialogFragment.this.getDialog().cancel();
                    }
                })*/


        return builder.create();

    }

    public void setOnSettingDialogOkBtnClick(OnSettingDialogOkBtnClick onSettingDialogOkBtnClick) {
        this.onSettingDialogOkBtnClick = onSettingDialogOkBtnClick;
    }

    public void loadMedia(String filePath){
        imageView.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);


        if( filePath.endsWith(Constants.FILE_SUFFIX_JPEG) ){
            imageView.setVisibility(View.VISIBLE);
            showImage(filePath);
        }else if( filePath.endsWith(Constants.FILE_SUFFIX_MP4) ){
            videoView.setVisibility(View.VISIBLE);
            showVideo(filePath);
        }
    }

    private void showVideo(String filePath) {
        Uri uri = Uri.fromFile(new File(filePath));
        videoView.setMediaController(new MediaController(getActivity()));
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
    }

    private void showImage(String filePath) {
        File file = new File(filePath);
        Uri imageUri = Uri.fromFile(file);
        Glide.with(this).load(imageUri).into(imageView);
        /*if( file.listFiles().length > 0  ){
            File f = file.listFiles()[file.listFiles().length-1];

            textView.setText(f.getAbsolutePath());

        }*/
    }
}
