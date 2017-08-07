package com.starrysky.presenter;

import android.app.Activity;
import android.content.Context;

import com.starrysky.R;
import com.starrysky.asynctask.DownloadFirmwareTask;
import com.starrysky.asynctask.LoadCameraDeviceTask;
import com.starrysky.asynctask.OpenCameraTask;
import com.starrysky.contract.ScanResultContract;
import com.starrysky.dto.CameraDevice;
import com.starrysky.helper.EventEmitter;

import java.util.List;

import butterknife.BindString;
import butterknife.ButterKnife;

public class ScanResultPresenter implements ScanResultContract.Presenter {
    public static final String TAG = "ScanResultPresenter";

    private ScanResultContract.View view;

    /* resources */
    @BindString(R.string.scanning_usb_device) String scanningUsbDeviceStr;
    @BindString(R.string.downloading_firmware) String downloadingFirmwareStr;

    /* Task */
    private LoadCameraDeviceTask loadCameraDeviceTask;
    private DownloadFirmwareTask downloadFirmwareTask;
    private OpenCameraTask openCameraTask;

    public ScanResultPresenter(ScanResultContract.View view) {
        this.setView(view);
        ButterKnife.bind(this, (Activity)view);
    }

    @Override
    public void setView(ScanResultContract.View view) {
        this.view = view;
    }

    @Override
    public void loadCameraDevice() {
        loadCameraDeviceTask = new LoadCameraDeviceTask((Context)view);

        EventEmitter.emitScanResultMessageEvent(scanningUsbDeviceStr);
        loadCameraDeviceTask.execute();
    }

    @Override
    public void downloadFirmware(List<CameraDevice> cameraDevicesList) {
        downloadFirmwareTask = new DownloadFirmwareTask((Context)view);

        EventEmitter.emitScanResultMessageEvent(downloadingFirmwareStr);
        downloadFirmwareTask.execute(cameraDevicesList);
    }

    @Override
    public void openCamera(CameraDevice cameraDevice) {
        openCameraTask = new OpenCameraTask((Context)view);
        openCameraTask.execute(cameraDevice);
    }

    @Override
    public void clean() {
        this.setView(null);
        if(loadCameraDeviceTask != null && !loadCameraDeviceTask.isCancelled() ){
            loadCameraDeviceTask.cancel(true);
        }

        if(downloadFirmwareTask != null && !downloadFirmwareTask.isCancelled() ){
            downloadFirmwareTask.cancel(true);
        }
    }


}
