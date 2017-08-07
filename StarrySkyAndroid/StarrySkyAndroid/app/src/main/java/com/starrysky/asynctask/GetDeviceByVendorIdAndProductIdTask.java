package com.starrysky.asynctask;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;

import com.starrysky.dto.CameraDevice;
import com.starrysky.helper.CameraDeviceHelper;
import com.starrysky.helper.EventEmitter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GetDeviceByVendorIdAndProductIdTask extends AsyncTask<Integer, Integer, CameraDevice> {
    private Context context;

    public GetDeviceByVendorIdAndProductIdTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPostExecute(CameraDevice cameraDevice) {
        EventEmitter.emitGetDeviceByVendorIdAndProductIdDoneMessageEvent(cameraDevice);
    }

    @Override
    protected CameraDevice doInBackground(Integer... params) {
        Integer vendorId = params[0];
        Integer productId = params[1];

        return CameraDeviceHelper.getDevice(context , vendorId,productId);
    }
}
