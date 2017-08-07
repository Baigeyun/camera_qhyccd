package com.starrysky.dto;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class CameraHandler implements Parcelable {
    private String cameraName;
    private UsbInterface usbInterface;
    private UsbDeviceConnection usbDeviceConnection;
    private List<UsbEndpoint> usbEndpointList;

    public CameraHandler() {
    }

    protected CameraHandler(Parcel in) {
        cameraName = in.readString();
        usbInterface = in.readParcelable(UsbInterface.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cameraName);
        dest.writeParcelable(usbInterface, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CameraHandler> CREATOR = new Creator<CameraHandler>() {
        @Override
        public CameraHandler createFromParcel(Parcel in) {
            return new CameraHandler(in);
        }

        @Override
        public CameraHandler[] newArray(int size) {
            return new CameraHandler[size];
        }
    };

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public UsbInterface getUsbInterface() {
        return usbInterface;
    }

    public void setUsbInterface(UsbInterface usbInterface) {
        this.usbInterface = usbInterface;
    }

    public UsbDeviceConnection getUsbDeviceConnection() {
        return usbDeviceConnection;
    }

    public void setUsbDeviceConnection(UsbDeviceConnection usbDeviceConnection) {
        this.usbDeviceConnection = usbDeviceConnection;
    }

    public List<UsbEndpoint> getUsbEndpointList() {
        return usbEndpointList;
    }

    public void setUsbEndpointList(List<UsbEndpoint> usbEndpointList) {
        this.usbEndpointList = usbEndpointList;
    }
}
