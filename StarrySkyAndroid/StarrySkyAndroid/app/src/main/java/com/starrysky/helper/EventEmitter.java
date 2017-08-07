package com.starrysky.helper;

import android.graphics.Bitmap;

import com.starrysky.asynctask.ReadUsbSingleFrameTask;
import com.starrysky.dto.CameraDevice;
import com.starrysky.dto.CameraHandler;
import com.starrysky.event.CameraViewportMessageEvent;
import com.starrysky.event.MessageEvent;
import com.starrysky.event.ScanResultMessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Eden on 2017/5/14.
 */

public class EventEmitter {

    public static void emitScanResultMessageEvent(String data) {
        ScanResultMessageEvent<String> event = new ScanResultMessageEvent<>();
        event.setType(MessageEvent.TYPE_SCAN_RESULT_PROGRESS_UPDATE);
        event.setData(data);
        EventBus.getDefault().post(event);
    }

    public static void emitLoadCameraDeviceDoneMessageEvent(List<CameraDevice> cameraDeviceList) {
        ScanResultMessageEvent<List<CameraDevice>> event = new ScanResultMessageEvent<>();
        event.setType(ScanResultMessageEvent.TYPE_LOAD_CAMERA_DEVICE_DONE);
        event.setData(cameraDeviceList);
        EventBus.getDefault().post(event);
    }

    public static void emitDownloadFirmwareDoneMessageEvent(List<CameraDevice> downloadedCameraDeviceList) {
        ScanResultMessageEvent<List<CameraDevice>> event = new ScanResultMessageEvent<>();
        event.setType(ScanResultMessageEvent.TYPE_DOWNLOAD_FIRMWARE_DONE);
        event.setData(downloadedCameraDeviceList);
        EventBus.getDefault().post(event);
    }

    public static void emitCameraOpenMessageEvent(CameraHandler cameraHandler) {
        ScanResultMessageEvent<CameraHandler> event = new ScanResultMessageEvent<>();
        event.setType(MessageEvent.TYPE_CAMERA_OPEN_DONE);
        event.setData(cameraHandler);
        EventBus.getDefault().post(event);
    }

    public static void emitReadUsbSingleFrameDoneMessageEvent(ReadUsbSingleFrameTask.Result result) {
        CameraViewportMessageEvent<ReadUsbSingleFrameTask.Result> event = new CameraViewportMessageEvent<>();
        event.setType(MessageEvent.TYPE_READ_USB_SINGLE_FRAME_DONE);
        event.setData(result);
        EventBus.getDefault().post(event);
    }

    public static void emitGetDeviceByVendorIdAndProductIdDoneMessageEvent(CameraDevice cameraDevice) {
        CameraViewportMessageEvent<CameraDevice> event = new CameraViewportMessageEvent<>();
        event.setType(MessageEvent.TYPE_GET_DEVICE_BY_VENDORID_AND_PRODUCTID_DONE);
        event.setData(cameraDevice);
        EventBus.getDefault().post(event);
    }

    public static void emitLoadLatestGalleryDoneMessageEvent(List<String> imageFileUrlList) {
        CameraViewportMessageEvent<List<String>> event = new CameraViewportMessageEvent<>();
        event.setType(MessageEvent.TYPE_LOAD_LATEST_GALLERY_DONE);
        event.setData(imageFileUrlList);
        EventBus.getDefault().post(event);
    }

    public static void emitSaveVideoDoneMessageEvent(String videoFilePath) {
        CameraViewportMessageEvent<String> event = new CameraViewportMessageEvent<>();
        event.setType(MessageEvent.TYPE_SAVE_VIDEO_DONE);
        event.setData(videoFilePath);
        EventBus.getDefault().post(event);
    }

    public static void emitShowHistMessageEvent(Bitmap bitmap) {
        CameraViewportMessageEvent<Bitmap> event = new CameraViewportMessageEvent<>();
        event.setType(MessageEvent.TYPE_SHOW_HIST_DONE);
        event.setData(bitmap);
        EventBus.getDefault().post(event);
    }
}
