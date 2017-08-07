package com.starrysky.asynctask;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.support.constraint.solver.ArrayLinkedVariables;
import android.util.Log;

import com.starrysky.dto.CameraDevice;
import com.starrysky.dto.CameraHandler;
import com.starrysky.helper.CameraDeviceHelper;
import com.starrysky.helper.EventEmitter;
import com.tencent.bugly.Bugly;

import java.util.ArrayList;
import java.util.List;

public class OpenCameraTask extends AsyncTask<CameraDevice , Integer, CameraHandler> {
    private static final String TAG = "OpenCameraTask";
    private Context context;

    public OpenCameraTask(Context context) {
        this.context = context;
    }

    @Override
    protected CameraHandler doInBackground(CameraDevice... cameraDevices) {
        try{
            Thread.sleep(1000);
        }catch (Exception e){}

        CameraHandler cameraHandler = null;
        if( cameraDevices.length > 0 && cameraDevices[0] != null ){
            cameraHandler = CameraDeviceHelper.openCamera(context,cameraDevices[0]);
        }
        return cameraHandler;
    }

    @Override
    protected void onPostExecute(CameraHandler cameraHandler) {
        EventEmitter.emitCameraOpenMessageEvent(cameraHandler);
    }



}
