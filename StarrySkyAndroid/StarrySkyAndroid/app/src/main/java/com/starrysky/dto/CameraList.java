package com.starrysky.dto;

import android.hardware.usb.UsbDevice;


public class CameraList {
    public int totalUSBDevice;   //总的扫描到的USB设备
    public int totalQHYCamera;   //其中扫描到的QHYCCD相机设备数量
    public String[] cameraName = new String[10];   //用于存储相机名字
    public int[] vid = new int[10];  //相机硬件识别号
    public int[] pid = new int[10];  //相机硬件识别号
    public UsbDevice[] device= new UsbDevice[10];     //相机设备类
    public boolean[] hasPermission = new boolean[10];  //相机是否具有访问权限
}
