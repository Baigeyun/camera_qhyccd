package com.starrysky.presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.AsyncTask;

import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.ImageProcessor;
import com.starrysky.asynctask.GetDeviceByVendorIdAndProductIdTask;
import com.starrysky.asynctask.LoadLatestGalleryTask;
import com.starrysky.asynctask.OpenCameraTask;
import com.starrysky.asynctask.SaveVideoTask;
import com.starrysky.contract.CameraViewportContract;
import com.starrysky.dto.CameraDevice;
import com.starrysky.helper.EventEmitter;
import com.starrysky.helper.ImageHistogramHelper;

import butterknife.ButterKnife;

public class CameraViewportPresenter implements CameraViewportContract.Presenter {
    private CameraViewportContract.View view;

    /* task */
    private GetDeviceByVendorIdAndProductIdTask getDeviceByVendorIdAndProductIdTask;
    private OpenCameraTask openCameraTask;
    private LoadLatestGalleryTask loadLatestGalleryTask;
    private SaveVideoTask saveVideoTask;

    public CameraViewportPresenter(CameraViewportContract.View view) {
        this.view = view;
        ButterKnife.bind(this, (Activity)view);
    }

    @Override
    public void setView(CameraViewportContract.View view) {
        this.view = view;
    }

    @Override
    public void clean() {
        if( getDeviceByVendorIdAndProductIdTask != null ){
            getDeviceByVendorIdAndProductIdTask.cancel(true);
        }
        if( openCameraTask != null ){
            openCameraTask.cancel(true);
        }

        if( loadLatestGalleryTask != null ){
            loadLatestGalleryTask.cancel(true);
        }

        if( saveVideoTask != null ){
            saveVideoTask.cancel(true);
        }


    }

    @Override
    public void getDevice(Integer vendorId, Integer productId) {
        getDeviceByVendorIdAndProductIdTask = new GetDeviceByVendorIdAndProductIdTask((Context)view);
        getDeviceByVendorIdAndProductIdTask.execute(vendorId,productId);
    }

    @Override
    public void loadLatestGallery(int count) {
        if( count <= 0 ){
            count = 2;  //default 2
        }
        loadLatestGalleryTask = new LoadLatestGalleryTask((Context)view);
        loadLatestGalleryTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,count);
    }

    public void openCamera(CameraDevice cameraDevice) {
        openCameraTask = new OpenCameraTask((Context)view);
        openCameraTask.execute(cameraDevice);
    }

    @Override
    public void saveVideo() {
        saveVideoTask = new SaveVideoTask((Context)view);
        saveVideoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void showHist(final Bitmap scaledBmp, final int width , final int height) {
        new AsyncTask<Void,Integer,Bitmap>(){


            @Override
            protected Bitmap doInBackground(Void... params) {
                CV4JImage cv4jImage0 = new CV4JImage(scaledBmp);
                ImageProcessor imageProcessor = cv4jImage0.convert2Gray().getProcessor();
                Bitmap bitmap = ImageHistogramHelper.getInstance().drawHist(imageProcessor,new Paint(), width, height);

                EventEmitter.emitShowHistMessageEvent(bitmap);

                scaledBmp.recycle();
                return bitmap;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
}
