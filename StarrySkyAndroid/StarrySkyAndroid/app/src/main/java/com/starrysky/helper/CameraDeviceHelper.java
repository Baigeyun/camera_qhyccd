package com.starrysky.helper;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import com.starrysky.activity.CameraViewportActivity;
import com.starrysky.asynctask.ReadUsbSingleFrameTask;
import com.starrysky.dto.CameraDevice;
import com.starrysky.dto.CameraHandler;
import com.starrysky.dto.CameraInfo;
import com.starrysky.dto.CameraList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Eden on 2017/5/16.
 */

public class CameraDeviceHelper {
    private static final String TAG = "CameraDeviceHelper";

    public static long fourByteToLong(byte[] buffer, int position){
        long result;

        long[] Bit= new long[4];

        for(int i=0;i<4;i++) Bit[i]=buffer[position+i];
        for(int i=0;i<4;i++){
            if (Bit[i]<0) Bit[i]=256+Bit[i];
        }
        result=Bit[0]+256*Bit[1]+256*256*Bit[2]+256*256*256*Bit[3];
        return result;
    }

    public static long twoByteToLong(byte msb, byte lsb){
        long result;
        long msb1,lsb1;

        if(msb<0) msb1=msb+256;
        else     msb1=msb;

        if(lsb<0) lsb1=lsb+256;
        else      lsb1=lsb;

        result = msb1*256+lsb1;
        return result;
    }

    public static long fourByteToLong2(byte msb3,byte msb2,byte msb1,byte msb0){
        long result;
        long b3,b2,b1,b0;

        if(msb0<0)  b0=msb0+256;
        else        b0=msb0;

        if(msb1<0) b1=msb1+256;
        else      b1=msb1;

        if(msb2<0) b2=msb2+256;
        else      b2=msb2;

        if(msb3<0)  b3=msb3+256;
        else        b3=msb3;

        result = b3*256*256*256+b2*256*256+b1*256+b0;
        return result;
    }


    public static byte ascii2byte(byte asc1,byte asc2){

        int a1,a2,a3;
        byte b3;

        a1=0;
        a2=0;

        if(asc1<=57 && asc1>=48)     a1=asc1-48;
        if(asc1<=70 && asc1>=65)     a1=asc1-55;

        if(asc2<=57 && asc2>=48)     a2=asc2-48;
        if(asc2<=70 && asc2>=65)     a2=asc2-55;

        //Log.v("QHYCCD asc1",String.valueOf(a1));
        //Log.v("QHYCCD asc2",String.valueOf(a2));


        a3=a1*16+a2;

        b3=(byte)a3;

        return b3;

    }

    public static void fx2_reset(UsbDeviceConnection usbDeviceConnection, int i){
        byte[] buf= new byte[16];
        buf[0]=(byte)i;
        vend_tx(usbDeviceConnection,0xa0,0x00,0xE600,1,buf);
    }

    public static void vend_tx(UsbDeviceConnection usbDeviceConnection,int vendreq, int index, int value, int length, byte[] buf) {
        int requestType = 0x40;
        int vendRequest = vendreq;
        int offset = 0;
        int timeout = 100;
        usbDeviceConnection.controlTransfer(requestType, vendRequest, value, index, buf, length, timeout);
    }

    public static void readCameraInfo(UsbDeviceConnection usbDeviceConnection,CameraInfo cm){
        byte[] data=new byte[64];
        vend_rx(usbDeviceConnection,0xd2,0x00,0x00,64,data);

        cm.cmosfreq=data[0];

        cm.exptime_toend=(int)fourByteToLong2(data[1],data[2],data[3],data[4]);
        cm.exptime=(int)fourByteToLong2(data[5],data[6],data[7],data[8]);

        cm.firmware_year  =data[9];
        cm.firmware_month=data[10];
        cm.firmware_day  =data[11];


        cm.usedDDR= (int)fourByteToLong2((byte)0x00,data[19],data[20],data[21]);

        cm.imageX=(int)twoByteToLong(data[28],data[29]);
        cm.imageY=(int)twoByteToLong(data[30],data[31]);

        cm.is16bit=data[32];
        cm.usbspeed=data[33];

        for (int i=0;i<8;i++) cm.uart[i]=data[38+i];

        cm.submodel=data[46];
        cm.sensorType=data[47];

        for (int i=0;i<16;i++) cm.guid[i]=data[48+i];
    }

    public static void vend_rx(UsbDeviceConnection usbDeviceConnection,int vendreq, int index, int value, int length, byte[] buf) {
        int requestType = 0xC0;
        int vendRequest = vendreq;
        int offset = 0;
        int timeout = 100;
        usbDeviceConnection.controlTransfer(requestType, vendRequest, value, index, buf, length, timeout);
    }
























    /* ***************************************END QHYYCCD Low Level (1) USB protocol ************************** */

    //vend request command
    /*public static void vend_rx(int vendreq, int index, int value, int length, byte[] buf) {
        int requestType = 0xC0;
        int vendRequest = vendreq;
        int offset = 0;
        int timeout = 100;
        cameraHandler.getUsbDeviceConnection().controlTransfer(requestType, vendRequest, value, index, buf, length, timeout);
    }

    public static void vend_tx(int vendreq, int index, int value, int length, byte[] buf) {
        int requestType = 0x40;
        int vendRequest = vendreq;
        int offset = 0;
        int timeout = 100;
        cameraHandler.getUsbDeviceConnection().controlTransfer(requestType, vendRequest, value, index, buf, length, timeout);
    }



    public static void fx2_reset(UsbDeviceConnection usbDeviceConnection,int i){


        byte[] buf= new byte[16];
        buf[0]=(byte)i;
        vend_tx(usbDeviceConnection,0xa0,0x00,0xE600,1,buf);

        // r = libusb_control_transfer(h, 0x40, 0xA0, FX2_CPUCS_ADDR, 0x00, &reset, 0x01, VENDORCMD_TIMEOUT);
    }*/


    public static byte MSB(int i) {
        byte j;
        j = (byte) ((i & ~0x00ff) >> 8);
        return j;
    }

    public static byte LSB(int i) {
        byte j;
        j = (byte) (i & ~0xff00);
        return j;
    }

    public static byte MSB3(long i){
        byte j;
        j = (byte) ((i & ~0x00FFFFFF) >> 24);
        return j;
    }

    public static byte MSB2(long i){
        byte j;
        j = (byte) ((i & ~0xFF00FFFF) >> 16);
        return j;
    }

    public static byte MSB1(long i){
        byte j;
        j = (byte) ((i & ~0xFFFF00FF) >> 8);
        return j;
    }

    public static byte MSB0(long i){
        byte j;
        j = (byte) ((i & ~0xFFFFFF00));
        return j;
    }




    /* ***************************************QHYYCCD Low Level (1) USB protocol ************************** */
    public static void CCC_A0(UsbDeviceConnection usbDeviceConnection,int mode, int binx, int biny) {
        byte[] data = new byte[16];
        data[0] = (byte) 0xa0;
        data[1] = LSB(mode);
        data[2] = MSB(binx);
        data[3] = LSB(binx);
        data[4] = MSB(biny);
        data[5] = LSB(biny);
        vend_tx(usbDeviceConnection,0xd1, 0x00, 0x00, 16, data);
    }


    public static void CCC_A1(UsbDeviceConnection usbDeviceConnection,byte speed) {
        byte[] data = new byte[16];
        data[0] = (byte) 0xa1;
        data[1] = speed;
        vend_tx(usbDeviceConnection,0xd1, 0x00, 0x00, 16, data);
    }


    public static void CCC_A2(UsbDeviceConnection usbDeviceConnection,byte mode, int XSIZE, int XSTART, int YSIZE, int YSTART) {
        byte[] data = new byte[16];
        data[0] = (byte) 0xa2;
        data[1] = mode;
        data[2] = MSB(XSIZE);
        data[3] = LSB(XSIZE);
        data[4] = MSB(XSTART);
        data[5] = LSB(XSTART);
        data[6] = MSB(YSIZE);
        data[7] = LSB(YSIZE);
        data[8] = MSB(YSTART);
        data[9] = LSB(YSTART);
        vend_tx(usbDeviceConnection,0xd1, 0x00, 0x00, 16, data);
    }

    public static void CCC_A3(UsbDeviceConnection usbDeviceConnection,long exposureTime) {
        byte[] data = new byte[16];
        data[0] = (byte) 0xa3;
        data[1] = (byte) MSB3(exposureTime);
        data[2] = (byte) MSB2(exposureTime);
        data[3] = (byte) MSB1(exposureTime);
        data[4] = (byte) MSB0(exposureTime);
        vend_tx(usbDeviceConnection,0xd1, 0x00, 0x00, 16, data);
    }

    public static void CCC_A4(UsbDeviceConnection usbDeviceConnection,int againR, int againG, int againB, int dgainR, int dgainG, int dgainB) {
        byte[] data = new byte[16];
        data[0] = (byte) 0xa4;
        data[1] = MSB(againR);
        data[2] = LSB(againR);
        data[3] = MSB(dgainR);
        data[4] = LSB(dgainR);

        data[5] = MSB(againG);
        data[6] = LSB(againG);
        data[7] = MSB(dgainG);
        data[8] = LSB(dgainG);

        data[9] = MSB(againB);
        data[10] = LSB(againB);
        data[12] = LSB(dgainB);
        data[11] = MSB(dgainB);

        vend_tx(usbDeviceConnection,0xd1, 0x00, 0x00, 16, data);

    }

    public static void CCC_A5(UsbDeviceConnection usbDeviceConnection,byte traffic) {
        byte[] data = new byte[16];
        data[0] = (byte) 0xa5;
        data[1] = traffic;
        vend_tx(usbDeviceConnection,0xd1, 0x00, 0x00, 16, data);
    }


    public static void CCC_A6(UsbDeviceConnection usbDeviceConnection,byte command) {
        byte[] data = new byte[16];
        data[0] = (byte) 0xa6;
        data[1] = command;
        vend_tx(usbDeviceConnection,0xd1, 0x00, 0x00, 16, data);
    }

    public static void CCC_A7(UsbDeviceConnection usbDeviceConnection,byte value) {
        byte[] data = new byte[16];
        data[0] = (byte) 0xa7;
        data[1] = value;
        vend_tx(usbDeviceConnection,0xd1, 0x00, 0x00, 16, data);
    }

    public static void CCC_A8(UsbDeviceConnection usbDeviceConnection,int offset1R, int offset1G, int offset1B) {
        byte[] data = new byte[16];
        data[0] = (byte) 0xa8;
        data[1] = MSB(offset1R);
        data[2] = LSB(offset1R);
        data[3] = MSB(offset1G);
        data[4] = LSB(offset1G);
        data[5] = MSB(offset1B);
        data[6] = LSB(offset1B);
        vend_tx(usbDeviceConnection,0xd1, 0x00, 0x00, 16, data);
    }

    public static void CCC_A9(UsbDeviceConnection usbDeviceConnection,byte value) {
//ENABLE DDR
        byte[] data = new byte[16];
        data[0] = (byte) 0xa9;
        vend_tx(usbDeviceConnection,0xd1, 0x00, 0x00, 16, data);
    }

    public static void CCC_AA(UsbDeviceConnection usbDeviceConnection,byte length, byte[] buf) {
        byte[] data = new byte[16];
        data[0] = (byte) 0xaa;
        data[1] = length;
        for (int i = 0; i < length; i++) {
            data[2 + i] = buf[i];
        }

        vend_tx(usbDeviceConnection,0xd1, 0x00, 0x00, 16, data);
    }

    public static void CCC_AB(UsbDeviceConnection usbDeviceConnection,int val) {
        byte[] data = new byte[16];
        data[0] = (byte) 0xab;
        data[1] = MSB(val);
        data[2] = LSB(val);

        vend_tx(usbDeviceConnection,0xd1, 0x00, 0x00, 16, data);
    }


    public static String getDeviceName(int vendorId,int productId){

        if (vendorId == 0x1618 && productId == 0x0920) {
            return "QHY5II-FW";
        } else if (vendorId == 0x1618 && productId == 0x0921) {
            return "QHY5II-IO";
        } else if (vendorId == 0x1618 && productId == 656) {
            return "QHY5III290-FW";
        } else if (vendorId == 1204 && productId == 243) {
            return  "FX3_EEPROM_EMPTY";
        } else if (vendorId == 0x1618 && productId == 0xc129) {
            return "QHY128-IO";
        } else if(vendorId == 0x1618 && productId == 0x0940)
        {
            return "PoleMaster-IO";
        }
        else {
            return "Unknown Device";
        }
    }

    public static CameraHandler openCamera(Context context,CameraDevice cameraDevice) {
        CameraHandler cameraHandler = null;

        if( cameraDevice != null /*&& cameraDevice.isHasPermission() == true */) {
            cameraHandler = new CameraHandler();
            Log.d(TAG,"### QHYCCD | Select Camera Name:" + cameraDevice.getCameraName());
            cameraHandler.setCameraName(cameraDevice.getCameraName());

            int totalInterface;
            int totalEndPoint;

            totalInterface = cameraDevice.getDevice().getInterfaceCount();
            Log.d(TAG,"### total interface" + String.valueOf(totalInterface));

            UsbInterface qinterface = cameraDevice.getDevice().getInterface(0);
            cameraHandler.setUsbInterface(qinterface);
            totalEndPoint = qinterface.getEndpointCount();

            Log.d(TAG, "### total endpoint" + String.valueOf(totalEndPoint));

            UsbDeviceConnection qconnection = null;
            UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
            qconnection = usbManager.openDevice( cameraDevice.getDevice() );
            cameraHandler.setUsbDeviceConnection(qconnection);

           /* if (qconnection == null ) {
                Log.d(TAG,"### device not opened");
                EventEmitter.emitCameraOpenMessageEvent(null);
                throw new RuntimeException("device not opened");
            }else{
                Log.d(TAG,"### device opened");
                EventEmitter.emitCameraOpenMessageEvent(cameraHandler);

            }*/

            //list all the endpoint to the global endPoint (qEP[])
            if(totalEndPoint>0) {
                List<UsbEndpoint> usbEndpointList = cameraHandler.getUsbEndpointList();
                if( usbEndpointList == null ){
                    usbEndpointList = new ArrayList<>();
                }

                UsbEndpoint usbEndpoint = null;
                for(int i=0 ; i<totalEndPoint ; i++) {
                    usbEndpoint = qinterface.getEndpoint(i);
                    if( usbEndpoint != null ){
                        usbEndpointList.add(usbEndpoint);
                    }
                    Log.d(TAG, "total endpoint" + String.valueOf(usbEndpoint.getMaxPacketSize())+"address:"+String.valueOf(usbEndpoint.getAddress()));
                }

                cameraHandler.setUsbEndpointList(usbEndpointList);
            }else{
                throw new RuntimeException("Can not get end point");
            }


        }else {
            Log.d(TAG ,"### QHYCCD | Open camera error");
            throw new RuntimeException("Has Permission:" + cameraDevice.isHasPermission());
        }

        return cameraHandler;
    }

    public static CameraDevice getDevice(Context context, Integer vendorId, Integer productId) {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

        if ( deviceList.size() > 0) {
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

            //check all device and put the QHYCCD device into the CameraList (cl)
            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();

                int vid = device.getVendorId();
                int pid = device.getProductId();

                if( vendorId == vid && productId == pid ){
                    CameraDevice cameraDevice = new CameraDevice();
                    cameraDevice.setPid(productId);
                    cameraDevice.setHasPermission(usbManager.hasPermission(device));
                    cameraDevice.setDevice( device );
                    cameraDevice.setCameraName(CameraDeviceHelper.getDeviceName(vendorId,productId) );
                    cameraDevice.setVid(vendorId);

                    return cameraDevice;
                }
            }
        }
        return null;

    }

    public static void initCamera(Context context, String cameraName, CameraHandler cameraHandler) {
        UsbDeviceConnection usbDeviceConnection = cameraHandler.getUsbDeviceConnection();
        UsbEndpoint endpoint = cameraHandler.getUsbEndpointList().get(0);

        if(cameraName == "QHY128-IO") {
            Log.i(TAG,"### init camera:QHY128-IO" );
            CCC_A0(usbDeviceConnection,1, 1, 1);
            CCC_A6(usbDeviceConnection,(byte) 0);
            //printCameraInfo();

            new ReadUsbSingleFrameTask(usbDeviceConnection,endpoint).execute();
        }

        else if (cameraName == "QHY5II-IO"){
            Log.i(TAG,"### init camera:QHY5II-IO" );
            CCC_A0(usbDeviceConnection,0,1,1);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            CCC_A6(usbDeviceConnection,(byte)0);

        }
        else if(cameraName == "PoleMaster-IO")
        {
            Log.i(TAG,"### init camera:polemaster-IO" );
            CCC_A0(usbDeviceConnection,0,1,1);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            CCC_A6(usbDeviceConnection,(byte)0);
        }else
        {

        }
    }


    /*public static CameraDevice getDevice(Context context ,Integer vendorId, Integer productId) {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();


        if ( deviceList.size() > 0) {
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

            //check all device and put the QHYCCD device into the CameraList (cl)
            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();

                int vid = device.getVendorId();
                int pid = device.getProductId();

                if( vendorId == vid && productId == pid ){
                    CameraDevice cameraDevice = new CameraDevice();
                    cameraDevice.setPid(productId);
                    cameraDevice.setHasPermission(usbManager.hasPermission(device));
                    cameraDevice.setDevice( device );
                    cameraDevice.setCameraName(CameraDeviceHelper.getDeviceName(vendorId,productId) );
                    cameraDevice.setVid(vendorId);

                    return cameraDevice;
                }
            }
        }
        return null;
    }*/

    public static void downloadFX2(Context context ,UsbDeviceConnection usbDeviceConnection,int id) {
        int length;
        CameraDeviceHelper.fx2_reset(usbDeviceConnection,1);
        try {
            InputStream in = context.getResources().openRawResource(id);
            length = in.available();

            byte[] inbuffer = new byte[length];
            int p;

            in.read(inbuffer);

            //System.arraycopy(inbuffer,0,buffer,0,length);
            Log.d( TAG , "### QHYCCD length" + String.valueOf(length));

            p = 0;
            int line_number = 0;
            while (true) {

                line_number++;
                Log.d(TAG,"### QHYCCD Line#" + String.valueOf(line_number));

                int LineSize = CameraDeviceHelper.ascii2byte(inbuffer[p + 1], inbuffer[p + 2]);
                if (LineSize == 0) {
                    Log.d(TAG,"### QHYCCD Detected End of File," + "last line");
                    break;
                }

                int Address = (int) CameraDeviceHelper.twoByteToLong(CameraDeviceHelper.ascii2byte(inbuffer[p + 3], inbuffer[p + 4]), CameraDeviceHelper.ascii2byte(inbuffer[p + 5], inbuffer[p + 6]));

                Log.d(TAG , "### QHYCCD LineSize" + Integer.toHexString(LineSize));
                Log.d(TAG ,"### QHYCCD Address" + Integer.toHexString(Address));

                int DataType = CameraDeviceHelper.ascii2byte(inbuffer[p + 7], inbuffer[p + 8]);

                Log.d(TAG , "### QHYCCD DataType" + Integer.toHexString(DataType));

                byte[] dbuf = new byte[64];
                int s;

                p = p + 9;

                s = 0;
                for (int i = 0; i < LineSize; i++) {
                    dbuf[s] = CameraDeviceHelper.ascii2byte(inbuffer[p], inbuffer[p + 1]);
                    p = p + 2;
                    s = s + 1;
                    Log.d(TAG, "### QHYCCD DataBuffer " + String.valueOf(i) + " " + Integer.toHexString(dbuf[i]));
                }


                CameraDeviceHelper.vend_tx(usbDeviceConnection,0xa0, 0X00, Address, LineSize, dbuf);

                int LastByte = CameraDeviceHelper.ascii2byte(inbuffer[p], inbuffer[p + 1]);
                Log.d(TAG,"### QHYCCD LastByte " + Integer.toHexString(LastByte));

                p = p + 2;//jump to the
                p = p + 2;//skip the 'd' 'a'
            }


            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        CameraDeviceHelper.fx2_reset(usbDeviceConnection,0);

    }

    public static int downloadFX3(Context context ,UsbDeviceConnection usbDeviceConnection,int id){
        int length;
        byte[] buffer;
        try{
            InputStream in = context.getResources().openRawResource(id);
            length = in.available();
            buffer = new byte[length];
            in.read(buffer);
            in.close();

            Log.d(TAG, "### ImgFile Length"+String.valueOf(length));

            int k=0;  //k is the point to the position of ig=img file

            int sectionCount=0;
            long sectionLength=0;
            long sectionAddress=0;
            int value,index;

            long downloadAddress=0;
            long downloadByteLeft=0;

            int buffersize=2048;
            byte[] bufferToTransfer = new byte[buffersize];

            if ((buffer[k] != 'C') || (buffer[k + 1] != 'Y')) {	/*signature doesn't match */
                Log.d(TAG,"### ImgFile Error,Head is no C Y");
            }

            k=k+4;  //skip 'C' 'Y' xx xx

            sectionCount=0;
            while(true) {
                //get 4 byte length value, sequence is bit0 bit1 bit2 bit3
                sectionLength = CameraDeviceHelper.fourByteToLong(buffer,k);
                k=k+4;
                sectionCount++;
                sectionLength = sectionLength*4;

                if(sectionLength ==0) {
                    Log.d(TAG,"### detect section end"+String.valueOf(sectionLength));
                    Log.d(TAG,"### HEX position at section end"+String.valueOf(k));
                    break;
                }

                //get 4 byte address value ,MSB is first?
                sectionAddress = CameraDeviceHelper.fourByteToLong(buffer,k);
                k=k+4;

                Log.d(TAG,"### new section #"+String.valueOf(sectionCount)+" length:"+String.valueOf(sectionLength)
                        +"section write address:"+String.valueOf(sectionAddress));

                downloadAddress=sectionAddress;
                downloadByteLeft=(int)sectionLength;

                //download serval times of the buffersize
                while (downloadByteLeft >= buffersize) {
                    index     = (int)(downloadAddress >> 16);
                    value     = (int)(downloadAddress-index*256*256) ;

                    Log.v("HEX position", String.valueOf(k));

                    System.arraycopy(buffer, k, bufferToTransfer, 0, buffersize);
                    CameraDeviceHelper.vend_tx(usbDeviceConnection,0xa0, index, value, buffersize, bufferToTransfer);

                    Log.d(TAG,"vend_tx ,index:" + String.valueOf(index) + " value:" + String.valueOf(value) + "buffersize" + String.valueOf(buffersize));

                    k = k + buffersize;
                    downloadByteLeft = downloadByteLeft - buffersize;
                    downloadAddress = downloadAddress + buffersize;
                    // Log.v("downloaded byte", String.valueOf(buffersize));
                }

                //to download the remain byte less than buffersize
                if (downloadByteLeft > 0) {
                    index     = (int)(downloadAddress >> 16);
                    value     = (int)(downloadAddress-index*256*256) ;

                    Log.d(TAG ,"### HEX position," + String.valueOf(k));

                    System.arraycopy(buffer, k, bufferToTransfer, 0, (int) downloadByteLeft);

                    CameraDeviceHelper.vend_tx(usbDeviceConnection,0xa0, index, value, (int) downloadByteLeft, bufferToTransfer);
                    Log.v("vend_tx", "index:" + String.valueOf(index) + " value:" + String.valueOf(value) + "buffersize" + String.valueOf(downloadByteLeft));

                    //Log.v("last download byte", String.valueOf(downloadByteLeft));

                    k = k + (int) downloadByteLeft;
                }

                Log.d(TAG ,"### HEX position," + String.valueOf(k));
            }


            long entryAddress = CameraDeviceHelper.fourByteToLong(buffer,k);
            k=k+4;
            long checksum = CameraDeviceHelper.fourByteToLong(buffer,k);
            k=k+4;

            Log.d(TAG, "### entryAddress:"+String.valueOf(entryAddress));
            Log.d(TAG, "### checkSum:"+String.valueOf(checksum));
            Log.d(TAG, "### end HEX position:"+String.valueOf(k));

            index     = (int)(entryAddress >> 16);
            value     = (int)(entryAddress-index*256*256) ;

            CameraDeviceHelper.vend_tx(usbDeviceConnection,0xa0,index,value,0,null);
            Log.d(TAG,"vend_tx,index:" + String.valueOf(index) + " value:" + String.valueOf(value) + "buffersize" + String.valueOf(0));
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
        return length;
    }

    public static CameraList scanCamera(Context context) {
        CameraList cameraList = new CameraList();
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

        cameraList.totalUSBDevice = deviceList.size();

        //BUTTON1.setText("dev:"+String.valueOf(cl.totalUSBDevice));
        Log.v(TAG, String.valueOf(cameraList.totalUSBDevice));

        for (int i = 0; i < 10; i++) {
            cameraList.cameraName[i] = "";   //clear camera list name table
        }
        cameraList.totalQHYCamera = 0;

        if (cameraList.totalUSBDevice > 0) {
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            // ArrayList<String> USBDeviceList = new ArrayList<String>(); // 存放USB设备的数量

            int index = 0;
            int vid, pid;

            //check all device and put the QHYCCD device into the CameraList (cl)
            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();
                //USBDeviceList.add(String.valueOf(device.getVendorId()));
                //USBDeviceList.add(String.valueOf(device.getProductId()));

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

                    index++;
                }
                else {

                    Log.i(TAG, "found unknown device" + "vid:pid=" + vid + ":" + pid);
                    Log.i(TAG, "class:" + device.getDeviceClass() + " subclass:" + device.getDeviceSubclass() + " protocol:" + device.getDeviceProtocol());
                }
            }
        }
        return cameraList;
    }
}

