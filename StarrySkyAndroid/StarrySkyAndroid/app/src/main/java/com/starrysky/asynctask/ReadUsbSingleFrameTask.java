package com.starrysky.asynctask;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.os.AsyncTask;
import android.util.Log;

import com.starrysky.dto.CameraInfo;
import com.starrysky.helper.CameraDeviceHelper;
import com.starrysky.helper.EventEmitter;

import static android.os.SystemClock.sleep;


public class ReadUsbSingleFrameTask extends AsyncTask<Void,Integer , ReadUsbSingleFrameTask.Result>{
    private static final String TAG = "ReadUsbSingleFrameTask";
    private UsbDeviceConnection usbDeviceConnection;
    private UsbEndpoint usbEndpoint;
    private int l;
    private byte[] data=new byte[4096];
    private int k=0;
    private int start_position=0;

    public ReadUsbSingleFrameTask(UsbDeviceConnection usbDeviceConnection,UsbEndpoint usbEndpoint) {
        this.usbDeviceConnection = usbDeviceConnection;
        this.usbEndpoint = usbEndpoint;
    }

    protected ReadUsbSingleFrameTask.Result doInBackground(Void... voids) {
        ReadUsbSingleFrameTask.Result result = new ReadUsbSingleFrameTask.Result();
        byte[] imgDataBytes = null;
        CameraInfo cm= new CameraInfo();
        int pre_usedDDR=0;
        int data_recevied_ddr=0;

        CameraDeviceHelper.readCameraInfo(usbDeviceConnection,cm);

        Log.d(TAG,"#### QHYCCD | used ddr step0:" + String.valueOf(cm.usedDDR));

        while(cm.usedDDR==0){

            CameraDeviceHelper.readCameraInfo(usbDeviceConnection,cm);
            sleep(100);
            Log.d(TAG ,"#### QHYCCD | usedDDR is zero,wait data coming");
        }

        pre_usedDDR=0;

        while(cm.usedDDR!=pre_usedDDR){

            pre_usedDDR = cm.usedDDR;
            Log.d(TAG, "#### QHYCCD | pre used DDR:" + String.valueOf(pre_usedDDR));
            CameraDeviceHelper.readCameraInfo(usbDeviceConnection,cm);
            Log.d(TAG, "#### QHYCCD | used DDR:" + String.valueOf(cm.usedDDR));
            sleep(100);
        }

        data_recevied_ddr=cm.usedDDR;

        for (int i=0; i < (data_recevied_ddr/2-4); i++) {
            l = usbDeviceConnection.bulkTransfer(usbEndpoint,data,4096,1500);

            if (l==0) {
                Log.d(TAG , "#### QHYCCD | one timeout appears");
                i=10000;
            }else if (l==4){
                start_position=k;
                Log.d(TAG,"####QHYCCD | size 4 packet at:"+String.valueOf(i)+"x4K" + String.valueOf(data[0]) + " "+ String.valueOf(data[1])+" "+String.valueOf(data[2])+" "+String.valueOf(data[3]));
            }
            else if (l<4 && l>0) {
                Log.d(TAG, "#### QHYCCD | appear size of "+String.valueOf(l)+"packet");
            }
            else if (l<4096 && l>4) {
                Log.v(TAG, "#### QHYCCD | appear size of " + String.valueOf(l) + "packet");
            }
            k=k+l;

            System.arraycopy(data, 0, imgDataBytes, k, l);

        }

        CameraDeviceHelper.readCameraInfo(usbDeviceConnection,cm);
        Log.d(TAG , "####QHYCCD | final used DDR after readout:" + String.valueOf(cm.usedDDR));

        result.setImgData(imgDataBytes);
        result.setStartPosition(start_position);
        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(ReadUsbSingleFrameTask.Result result) {
        super.onPostExecute(result);

        //showImageOnImageBox(imgDataBytes,4096+start_position,iv,6056,2000,16);
        EventEmitter.emitReadUsbSingleFrameDoneMessageEvent(result);
        Log.d(TAG, "#### QHYCCD | END" + " usb async reading thread end");
    }


    public static class Result{
        private byte[] imgData;
        private int startPosition;

        public byte[] getImgData() {
            return imgData;
        }

        public void setImgData(byte[] imgData) {
            this.imgData = imgData;
        }

        public int getStartPosition() {
            return startPosition;
        }

        public void setStartPosition(int startPosition) {
            this.startPosition = startPosition;
        }
    }
};