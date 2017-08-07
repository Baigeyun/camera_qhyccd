package com.starrysky.event;

public class MessageEvent<T> {
    public final static String TYPE_LOAD_CAMERA_DEVICE_DONE = "LOAD_CAMERA_DEVICE_DONE";
    public final static String TYPE_DOWNLOAD_FIRMWARE_DONE = "DOWNLOAD_FIRMWARE_DONE";
    public static final String TYPE_CAMERA_OPEN_DONE = "CAMERA_OPEN_DONE";

    public static final String TYPE_READ_USB_SINGLE_FRAME_DONE = "READ_USB_SINGLE_FRAME_DONE";
    public static final String TYPE_LOAD_LATEST_GALLERY_DONE = "LOAD_LATEST_GALLERY_DONE";
    // scan result
    public final static String TYPE_SCAN_RESULT_PROGRESS_UPDATE = "TYPE_SCAN_RESULT_PROGRESS_UPDATE";
    public static final String TYPE_GET_DEVICE_BY_VENDORID_AND_PRODUCTID_DONE = "GET_DEVICE_DONE";
    public static final String TYPE_SAVE_VIDEO_DONE = "SAVE_VIDEO_DONE";
    public static final String TYPE_SHOW_HIST_DONE = "TYPE_SHOW_HIST_DONE";


    private String type;
    private T data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
