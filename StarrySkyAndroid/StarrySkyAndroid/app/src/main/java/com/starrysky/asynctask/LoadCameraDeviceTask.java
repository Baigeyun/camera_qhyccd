package com.starrysky.asynctask;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.util.Log;

import com.starrysky.R;
import com.starrysky.dto.CameraDevice;
import com.starrysky.dto.CameraList;
import com.starrysky.helper.CameraDeviceHelper;
import com.starrysky.helper.EventEmitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class LoadCameraDeviceTask extends AsyncTask<Void, Integer, List<CameraDevice>> {
    private static final String TAG = "LoadCameraDeviceTask";
    private Context context;
    public LoadCameraDeviceTask(Context context) {
        this.context = context;
    }

    @Override
    protected List<CameraDevice> doInBackground(Void... voids) {
        List<CameraDevice> cameraDeviceList = new ArrayList<CameraDevice>();
        CameraList cameraList = CameraDeviceHelper.scanCamera(context);

        // 封装返回对象
        if( cameraList != null && cameraList.totalQHYCamera > 0 ){
            for(int i = 0 ; i < cameraList.totalQHYCamera ; i++){
                CameraDevice cameraDevice = new CameraDevice();
                String cameraName = cameraList.cameraName[i];
                Integer vid = cameraList.vid[i];
                Integer pid = cameraList.pid[i];
                UsbDevice device = cameraList.device[i];
                boolean hasPermission = cameraList.hasPermission[i];

                cameraDevice.setDevice(device);
                cameraDevice.setHasPermission(hasPermission);
                cameraDevice.setCameraName(cameraName);
                cameraDevice.setVid(vid);
                cameraDevice.setPid(pid);
                cameraDeviceList.add(cameraDevice);
            }
        }
        return cameraDeviceList;
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    @Override
    protected void onPostExecute(List<CameraDevice> cameraDeviceList) {
        EventEmitter.emitLoadCameraDeviceDoneMessageEvent(cameraDeviceList);
    }

    CameraList scanCamera() {
        CameraList cameraList = new CameraList();
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

        cameraList.totalUSBDevice = deviceList.size();

        Log.v(TAG, String.valueOf(cameraList.totalUSBDevice));

        for (int i = 0; i < 10; i++) {
            cameraList.cameraName[i] = "";   //clear camera list name table
        }
        cameraList.totalQHYCamera = 0;

        if (cameraList.totalUSBDevice > 0) {
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

            int index = 0;
            int vid, pid;

            //check all device and put the QHYCCD device into the CameraList (cl)
            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();

                vid = device.getVendorId();
                pid = device.getProductId();

                if (vid == 0x1618 && pid == 0x0920) {
                    cameraList.cameraName[index] = "QHY5II-FW";
                    cameraList.vid[index] = vid;
                    cameraList.pid[index] = pid;
                    cameraList.totalQHYCamera++;
                    cameraList.device[index] = device;
                    cameraList.hasPermission[index] = usbManager.hasPermission(device);

                    index++;
                } else if (vid == 0x1618 && pid == 0x0921) {

                    cameraList.cameraName[index] = "QHY5II-IO";
                    cameraList.totalQHYCamera++;
                    cameraList.vid[index] = vid;
                    cameraList.pid[index] = pid;
                    cameraList.device[index] = device;
                    cameraList.hasPermission[index] = usbManager.hasPermission(device);

                    index++;
                } else if (vid == 0x1618 && pid == 656) {

                    cameraList.cameraName[index] = "QHY5III290-FW";
                    cameraList.totalQHYCamera++;
                    cameraList.vid[index] = vid;
                    cameraList.pid[index] = pid;
                    cameraList.device[index] = device;
                    cameraList.hasPermission[index] = usbManager.hasPermission(device);

                    index++;
                } else if (vid == 1204 && pid == 243) {

                    cameraList.cameraName[index] = "FX3_EEPROM_EMPTY";
                    cameraList.totalQHYCamera++;
                    cameraList.vid[index] = vid;
                    cameraList.pid[index] = pid;
                    cameraList.device[index] = device;
                    cameraList.hasPermission[index] = usbManager.hasPermission(device);

                    index++;
                } else if (vid == 0x1618 && pid == 0xc129) {

                    cameraList.cameraName[index] = "QHY128-IO";
                    cameraList.totalQHYCamera++;
                    cameraList.vid[index] = vid;
                    cameraList.pid[index] = pid;
                    cameraList.device[index] = device;
                    cameraList.hasPermission[index] = usbManager.hasPermission(device);

                    index++;
                } else if(vid == 0x1618 && pid == 0x0940)
                {
                    cameraList.cameraName[index] = "PoleMaster-IO";
                    cameraList.totalQHYCamera++;
                    cameraList.vid[index] = vid;
                    cameraList.pid[index] = pid;
                    cameraList.device[index] = device;
                    cameraList.hasPermission[index] = usbManager.hasPermission(device);

                } else {

                    Log.i(TAG, "found unknown device" + "vid:pid=" + vid + ":" + pid);
                    Log.i(TAG, "class:" + device.getDeviceClass() + " subclass:" + device.getDeviceSubclass() + " protocol:" + device.getDeviceProtocol());
                }
            }
        }
        return cameraList;
    }

}
