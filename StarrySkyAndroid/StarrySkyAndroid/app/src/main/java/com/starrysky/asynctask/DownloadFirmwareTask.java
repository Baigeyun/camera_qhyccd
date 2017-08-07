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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *  download firmware
 */

public class DownloadFirmwareTask extends AsyncTask<List<CameraDevice>, Integer, List<CameraDevice>> {
    private static final String TAG = "DownloadFirmwareTask";
    private Context context;

    public DownloadFirmwareTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected List<CameraDevice> doInBackground(List<CameraDevice>... lists) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<CameraDevice> downloadedCameraDeviceList = downlodFirmware(lists[0]);
        return downloadedCameraDeviceList;
    }

    @Override
    protected void onPostExecute(List<CameraDevice> downloadedCameraDeviceList) {
        EventEmitter.emitDownloadFirmwareDoneMessageEvent(downloadedCameraDeviceList);
    }

    private List<CameraDevice> downlodFirmware(List<CameraDevice> cameraDevicesList){
        List<CameraDevice> doanloadedCameraDeviceList = new ArrayList<CameraDevice>();
        if( cameraDevicesList != null && cameraDevicesList.size() > 0 ){
            UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
            UsbDeviceConnection usbDeviceConnection = null;
            for(CameraDevice cameraDevice : cameraDevicesList ){
                usbDeviceConnection = null;
                if( cameraDevice.getCameraName().startsWith("QHY5II") && cameraDevice.isHasPermission() == true){
                    usbDeviceConnection = usbManager.openDevice(cameraDevice.getDevice());
                    if (usbDeviceConnection == null) {
                        Log.i(TAG,"### device not opened");
                    }else {
                        Log.i(TAG,"### device opened");
                        CameraDeviceHelper.downloadFX2(context,usbDeviceConnection, R.raw.qhy5lii_vid0920);

                        doanloadedCameraDeviceList.add(cameraDevice);
                    }
                }

                if( cameraDevice.getCameraName() == "FX3_EEPROM_EMPTY" && cameraDevice.isHasPermission() == true){
                    usbDeviceConnection = usbManager.openDevice( cameraDevice.getDevice() );
                    if ( usbDeviceConnection == null ){
                        Log.i(TAG,"### device not opened");
                    }else {
                        Log.i(TAG,"### device opened");
                        CameraDeviceHelper.downloadFX3(context,usbDeviceConnection,R.raw.qhy128);


                        doanloadedCameraDeviceList.add(cameraDevice);
                    }
                }
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 下载完固件，重新读取usb设备
        CameraList cameraList = CameraDeviceHelper.scanCamera(context);

        // 封装返回对象
        List<CameraDevice> cameraDeviceList = new ArrayList<>();
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

}