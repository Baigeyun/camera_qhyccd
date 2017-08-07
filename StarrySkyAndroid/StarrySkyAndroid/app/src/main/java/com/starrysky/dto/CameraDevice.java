package com.starrysky.dto;

import android.hardware.usb.UsbDevice;
import android.os.Parcel;
import android.os.Parcelable;

public class CameraDevice implements Parcelable {
    private String cameraName;   //用于存储相机名字
    private Integer vid;  //厂商ID
    private Integer pid;  //产品ID
    private UsbDevice device;     //相机设备类
    private boolean hasPermission;  //相机是否具有访问权限

    public CameraDevice() {
    }

    protected CameraDevice(Parcel in) {
        cameraName = in.readString();
        device = in.readParcelable(UsbDevice.class.getClassLoader());
        hasPermission = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cameraName);
        dest.writeParcelable(device, flags);
        dest.writeByte((byte) (hasPermission ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CameraDevice> CREATOR = new Creator<CameraDevice>() {
        @Override
        public CameraDevice createFromParcel(Parcel in) {
            return new CameraDevice(in);
        }

        @Override
        public CameraDevice[] newArray(int size) {
            return new CameraDevice[size];
        }
    };

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public Integer getVid() {
        return vid;
    }

    public void setVid(Integer vid) {
        this.vid = vid;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public UsbDevice getDevice() {
        return device;
    }

    public void setDevice(UsbDevice device) {
        this.device = device;
    }

    public boolean isHasPermission() {
        return hasPermission;
    }

    public void setHasPermission(boolean hasPermission) {
        this.hasPermission = hasPermission;
    }
}
