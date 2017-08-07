package com.starrysky.dto;


public class CameraInfo {
    public int firmware_year;//平台左端的位置
    public int firmware_month;
    public int firmware_day;
    public int cmosfreq;
    public int exptime_toend;
    public int exptime;
    public int submodel;
    public int sensorType;
    public int imageX;
    public int imageY;
    public int is16bit;
    public int usbspeed;

    public int usedDDR;

    public byte[] guid= new byte[16];
    public byte[] uart= new byte[8];
}
