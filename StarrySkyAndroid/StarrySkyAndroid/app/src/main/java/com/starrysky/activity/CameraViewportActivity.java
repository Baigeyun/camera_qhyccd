package com.starrysky.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbRequest;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.starrysky.BaseActivity;
import com.starrysky.R;
import com.starrysky.asynctask.ReadUsbSingleFrameTask;
import com.starrysky.contract.CameraViewportContract;
import com.starrysky.customview.MenuPopup;
import com.starrysky.customview.SettingBarView;
import com.starrysky.customview.VideoSurfaceView;
import com.starrysky.dto.CameraDevice;
import com.starrysky.dto.CameraHandler;
import com.starrysky.dto.CameraInfo;
import com.starrysky.dto.Category;
import com.starrysky.dto.SettingMenuDetailItem;
import com.starrysky.dto.SettingMenuItem;
import com.starrysky.dto.SubCategory;
import com.starrysky.event.CameraViewportMessageEvent;
import com.starrysky.event.MessageEvent;
import com.starrysky.helper.AlertHelper;
import com.starrysky.helper.BitmapHelper;
import com.starrysky.helper.CameraDeviceHelper;
import com.starrysky.helper.Constants;
import com.starrysky.helper.DateHelper;
import com.starrysky.helper.FileHelper;
import com.starrysky.helper.GalleryHelper;
import com.starrysky.helper.PicHelper;
import com.starrysky.helper.ProgressDialogHelper;
import com.starrysky.helper.SharedPreferencesHelper;
import com.starrysky.helper.TimeHelper;
import com.starrysky.listener.OnImageClickListener;
import com.starrysky.listener.OnLongPressListener;
import com.starrysky.listener.OnLongTouchReleaseListener;
import com.starrysky.presenter.CameraViewportPresenter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CameraViewportActivity extends BaseActivity implements CameraViewportContract.View{
    private static final String TAG = "CameraViewportActivity";

    /* view */
    @BindView(R.id.settingBarView) SettingBarView mSettingBarView;
    @BindView(R.id.videoSurfaceView) VideoSurfaceView videoSurfaceView;
    @BindView(R.id.histogramImageView)ImageView histogramImageView;

    /* data */
    private CameraDevice cameraDevice;
    private CameraHandler cameraHandler;
    private Integer vendorId;
    private Integer productId;
    private ReadUsbAsync readUsbAsync ;

    int memsize = 100;

    private int BITMAP_WIDTH = 1280;
    private int BITMAP_HEIGHT = 960;

    private byte[][] xbufferdata = new byte[memsize][16384];
    private ByteBuffer[] xbuffer = new ByteBuffer[memsize];
    private UsbRequest[] request = new UsbRequest[memsize];
    private int onStreaming = 0;
    private int pixelPosition = 0;
    private int pre_pixelposition = 0;
    private int frame_count_period10 = 0;
    private int frame_count_bad_period10 = 0;
    private int frame_count_bad_result = 0;
    private int[] log_request_size_a = new int[memsize];
    private int[] log_request_size_b = new int[memsize];
    private int log_request_count = 0;
    private int log_request_received_total = 0;
    private boolean flag_ab = true;
    private boolean flag_frameGood_a = false;  //used for indentity if one frame good or bad
    private boolean flag_frameGood_b = false;
    private boolean flag_isNew_a = false;      //used for identity if one frame is new or old
    private boolean flag_isNew_b = false;
    private volatile int frame_count = 0;
    private volatile int frame_rate = 0;

    private volatile int refresh_count = 0;
    private volatile int refresh_rate = 0;

    private byte[] ImgDataA;
    private byte[] ImgDataB;
    //private byte[] ImgData;
    private boolean onDisplay = false;
    private byte[] ImgDataX = new byte[BITMAP_WIDTH * BITMAP_HEIGHT];//buffer for copy image from the buffer of usb reading thread
    private int[] bmpdata = new int[BITMAP_WIDTH * BITMAP_HEIGHT];  //used for bitmap image's

    private int display_actual_fps_counter = 0;
    private int display_actual_fps = 0;

    private Bitmap bmp1 = null;
    private boolean isRun;
    private boolean isRecording = false;
    private boolean isCapturing = false;
    //private List<int[]> bmpDataList;

    private FpsTask fpsTask;

    int videoTmpFileIndex = 0;

    /* present */
    private CameraViewportPresenter cameraViewportPresenter;

    private MediaRecorder mediaRecorder;

    private boolean isHistDrawing = false;

    class FpsTask extends AsyncTask<Void,Integer,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            while( isRun ){
                readFps();

                try{
                    Thread.sleep(1000);
                }catch (Exception e){
                    return null;
                }
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_viewport);

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                loadFFMpegBinary();
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        cameraViewportPresenter = new CameraViewportPresenter(this);

        getIntentData();

        initView();


        cameraViewportPresenter.loadLatestGallery(2);
    }

    private void initFields(int bitmapWidth, int bitmapHeight) {
        //全局变量
        BITMAP_WIDTH = bitmapWidth;
        BITMAP_HEIGHT = bitmapHeight;

        //初始化双BUFFER
        ImgDataA = new byte[bitmapWidth*bitmapHeight];
        ImgDataB=  new byte[bitmapWidth*bitmapHeight];

        //初始化显示BITMAP
        bmp1 = Bitmap.createBitmap(bitmapWidth,bitmapHeight, Bitmap.Config.ARGB_8888);

        //将ByteBuffer映射到xbuffer上
        for(int i=0;i<memsize;i++) xbuffer[i]= ByteBuffer.wrap(xbufferdata[i]);

        ImgDataX = new byte[bitmapWidth * bitmapHeight];//buffer for copy image from the buffer of usb reading thread
        bmpdata = new int[bitmapWidth * bitmapHeight];  //used for bitmap image's
    }

    private void getIntentData() {
        vendorId = getIntent().getIntExtra(Constants.EXTRA_KEY_VENDOR_ID,0);
        productId = getIntent().getIntExtra(Constants.EXTRA_KEY_PRODUCT_ID,0);
    }

    // delay
    private void delay_ms(int d)
    {
        long starTime=System.currentTimeMillis();
        while(true)
        {
            if(System.currentTimeMillis() - starTime >d) {
                break;
            }
        }
    }

    private void initView() {
        ButterKnife.bind(this);

        mSettingBarView.setOnCaptureBtnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureScreen();

            }
        });

        mSettingBarView.setOnCaptureBtnLongPress(new OnLongPressListener() {
            @Override
            public void onLongPress() {
                startRecord();
            }
        });

        mSettingBarView.setOnCaptureBtnLongTouchRelease(new OnLongTouchReleaseListener() {
            @Override
            public void onTouchRelease() {

                recordDone();
            }
        });

        mSettingBarView.setOnGalleryPreviewImageView1ClickListener(new OnImageClickListener() {
            @Override
            public void onClick(String filePath) {
                /*Intent intent = new Intent(CameraViewportActivity.this, GalleryPreviewActivity.class);
                intent.putExtra("filePath",filePath);
                startActivity(intent);*/

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(filePath)),"image/*");
                startActivity(intent);
            }
        });

        mSettingBarView.setOnGalleryPreviewImageView1ClickListener(new OnImageClickListener() {
            @Override
            public void onClick(String filePath) {
                /*Intent intent = new Intent(CameraViewportActivity.this, GalleryPreviewActivity.class);
                intent.putExtra("filePath",filePath);
                startActivity(intent);*/

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(filePath)),"image/*");
                startActivity(intent);
            }
        });

        mSettingBarView.setOnSettingBtnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuPopup popupMenu = new MenuPopup(CameraViewportActivity.this);

                popupMenu.setOnSettingSetListener(new MenuPopup.OnSettingSetListener() {
                    @Override
                    public void onSet(SettingMenuItem item, SettingMenuDetailItem detailItem) {
                        sendCommand(item,detailItem);
                    }
                });

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                popupMenu.show(inflater.inflate(R.layout.activity_camera_viewport, null));
            }
        });
        /********************/

    }


    private void startRecord() {
        Toast.makeText(CameraViewportActivity.this,"Starting recording...",Toast.LENGTH_SHORT).show();
        isRecording = true;
        videoTmpFileIndex = 0 ;
    }

    private void recordDone() {
        Toast.makeText(CameraViewportActivity.this,"结束录制，正在保存视频...",Toast.LENGTH_SHORT).show();
        isRecording = false;
        saveVideo();
    }

    private void saveVideo() {
        File videoTmpPath = PicHelper.getVideoTmpPath(getApplicationContext());
        File videoFile = new File(PicHelper.getSavePath(getApplicationContext()) , PicHelper.generateVideoFileName());

        //String cmd = "-f image2 -framerate 12 -i " + videoTmpPath.getAbsolutePath() + "/p%d.jpeg " + videoFile.getAbsolutePath();
        final String[] cmdAry = new String[]{
                "-f", "image2","-framerate","12","-i",
                videoTmpPath.getAbsolutePath()+"/p%d.jpeg", videoFile.getAbsolutePath()
        };

        execFFmpegBinary(cmdAry,new ExecuteBinaryResponseHandler() {
            @Override
            public void onFailure(String s) {
                Log.i(TAG,"### FAILED with output :" + s );
            }

            @Override
            public void onSuccess(String s) {
                Log.i(TAG,"### SUCCESS with output : "+s );
            }

            @Override
            public void onProgress(String s) {
                Log.i(TAG,"### progress : "+s);
            }

            @Override
            public void onStart() {
                Log.i(TAG,"### start ");
                ProgressDialogHelper.getInstance().showProgressDialog(CameraViewportActivity.this,"正在处理视频...");

            }

            @Override
            public void onFinish() {
                Log.i(TAG,"### finish ");
                new Thread(){
                    @Override
                    public void run() {
                        File videoTmpPath = PicHelper.getVideoTmpPath(getApplicationContext());
                        FileHelper.deleteAllFile(videoTmpPath);
                    }
                }.start();

                cameraViewportPresenter.loadLatestGallery(2);
                ProgressDialogHelper.getInstance().hideProcessDialog();
            }
        });
    }

    private void captureScreen() {
        if(  videoSurfaceView.isPlaying()  && bmp1 != null ){
           isCapturing =  true;

            try{
                SaveCaptureImageTask saveCaptureImageTask = new SaveCaptureImageTask(bmp1);
                saveCaptureImageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }catch (Exception e){
                Log.e(TAG,e.getMessage());
            }
        }
    }

    class SaveCaptureImageTask extends AsyncTask<String,Void,String>{
        private Bitmap bitmap;

        public SaveCaptureImageTask(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            Toast.makeText(getApplicationContext(),"正在保存截图..." ,Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(String s) {
            isCapturing =  false;
            Toast.makeText(getApplicationContext(),"截图已保存",Toast.LENGTH_SHORT).show();

            cameraViewportPresenter.loadLatestGallery(2);
        }

        @Override
        protected String doInBackground(String... strings) {
            File savePath = PicHelper.getSavePath(getApplicationContext());
            final File jpegFile = new File(savePath.getAbsolutePath(), PicHelper.generatePicFileName() );

            PicHelper.saveJpegToFile(jpegFile.getAbsolutePath(),bitmap);

            // link image to gallery
            GalleryHelper.linkToGallery(getApplicationContext(),jpegFile);
            return "success";
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        new AsyncTask<Void, Void , Void >(){
            @Override
            protected Void doInBackground(Void... params) {
                init();
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }

    private void init() {
        try {
            onStreaming = 1;
            isRun = true;
            initCamera();

            initFields(BITMAP_WIDTH,BITMAP_HEIGHT);
            CameraDeviceHelper.CCC_A2(cameraHandler.getUsbDeviceConnection(),(byte)0, BITMAP_WIDTH ,0,BITMAP_HEIGHT,0);
            delay_ms(100);

            fpsTask = new FpsTask();
            fpsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertHelper.showOKDialog(CameraViewportActivity.this, "Error", "Camera initialization failed!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                }
            });

            //todo 这里要判断空。如果 是空，就不应该再调用。
            if(null != cameraHandler  && null != cameraHandler.getUsbDeviceConnection())
            {
                cameraHandler.getUsbDeviceConnection().close();
            }

        }
    }

    private void initCamera()throws Exception {

        try {
            cameraDevice = CameraDeviceHelper.getDevice(this , vendorId,productId);

            if( cameraDevice == null ){
                throw new Exception("Can not find device.");
            }
            cameraHandler = CameraDeviceHelper.openCamera(this,cameraDevice);

            CameraDeviceHelper.initCamera(this,cameraDevice.getCameraName(),cameraHandler);
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }

        readUsbAsync = new ReadUsbAsync();
        readUsbAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        videoSurfaceView.setThread(new VideoThread(videoSurfaceView.getHolder()));
        videoSurfaceView.play();
    }


    private void logCameraInfo() {
        CameraInfo cm = new CameraInfo();
        CameraDeviceHelper.readCameraInfo(cameraHandler.getUsbDeviceConnection(),cm);   //use low level protocol 0XD2 command to get the camera status


        Log.d(TAG,"#### cmosfreq:" + String.valueOf(cm.cmosfreq) + "\n");
        Log.d(TAG,"#### expTimeToEnd:" + String.valueOf(cm.exptime_toend) + "\n");
        Log.d(TAG, "#### expTime:" + String.valueOf(cm.exptime) + "\n1");

        Log.d(TAG,"#### firmware version Year:" + String.valueOf(cm.firmware_year) + "\n");
        Log.d(TAG,"#### firmware version Month:" + String.valueOf(cm.firmware_month + "\n"));
        Log.d(TAG,"#### firmware version Day:" + String.valueOf(cm.firmware_day) + "\n");


        Log.d(TAG,"#### imageX:" + String.valueOf(cm.imageX) + "\n");
        Log.d(TAG,"#### imageY:" + String.valueOf(cm.imageY) + "\n");

        for (int i = 0; i < 16; i++) {
            Log.d(TAG,"#### Guid:" + String.valueOf(cm.guid[i]) + " ");
        }

        for (int i = 0; i < 8; i++) {
            Log.d(TAG,"#### uart:" + String.valueOf(cm.guid[i]) + " ");
        }

        Log.d(TAG,"#### usbspeed:" + String.valueOf(cm.usbspeed) + "\n");
        Log.d(TAG,"#### is16bit:" + String.valueOf(cm.is16bit) + "\n");
        Log.d(TAG,"#### used ddr:" + String.valueOf(cm.usedDDR));
    }

    @Override
    protected void onStop() {
        super.onStop();

        clean();
    }

    private void clean() {
        onStreaming = 0;
        isRun = false;
        if( readUsbAsync != null && !readUsbAsync.isCancelled() ){
            readUsbAsync.cancel(true);
        }
        if( fpsTask != null && !fpsTask.isCancelled() ){
            fpsTask.cancel(true);
        }

        cameraHandler.getUsbDeviceConnection().close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRun = false;

        cameraViewportPresenter.clean();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CameraViewportMessageEvent event) {
        if( event != null ) {
            String type = event.getType();
            if (type.equals(MessageEvent.TYPE_READ_USB_SINGLE_FRAME_DONE)) {
                ReadUsbSingleFrameTask.Result result = (event.getData() == null) ? null : (ReadUsbSingleFrameTask.Result) event.getData();

                //showImageOnImageBox(result.getImgData(), 4096 + result.getStartPosition(), imageView, 6056, 2000, 16);
            }else if( type.equals(MessageEvent.TYPE_LOAD_LATEST_GALLERY_DONE ) ){
                List<String> imageFileUrlList = (event.getData() == null) ? null : (List<String>) event.getData();

                onLoadLatestGalleryDone(imageFileUrlList);

            }else if( type.equals(MessageEvent.TYPE_SAVE_VIDEO_DONE ) ){
                String videoFilePath = (event.getData() == null) ? null : (String) event.getData();

                onSaveVideoDoneDone(videoFilePath);

            }else if( type.equals(MessageEvent.TYPE_SHOW_HIST_DONE) ){
                Bitmap histBitmap = (event.getData() == null) ? null : (Bitmap) event.getData();

                if( histBitmap != null ){
                    isHistDrawing = false;

                    histogramImageView.setImageBitmap(histBitmap);
                    histogramImageView.setVisibility(View.VISIBLE);
                }
            }



        }
    }

    private void onSaveVideoDoneDone(String videoFilePath) {
        ProgressDialogHelper.getInstance().hideProcessDialog();
        Toast.makeText(this,"Video saved:" + videoFilePath, Toast.LENGTH_SHORT ).show();
        cameraViewportPresenter.loadLatestGallery(2);
    }

    private void onLoadLatestGalleryDone(List<String> galleryBitmapList) {
        for(String imageFileUrl: galleryBitmapList ){
            Log.i(TAG,"### loaded image file url:" + imageFileUrl );
        }

        mSettingBarView.setLatestGalleryData(galleryBitmapList);
    }

    private void onCameraOpenDone(CameraHandler cameraHandler) {
        this.cameraHandler = cameraHandler;

        UsbDeviceConnection usbDeviceConnection = cameraHandler.getUsbDeviceConnection();
        String cameraName = cameraHandler.getCameraName();
        if( cameraName.equals("QHY128-IO") ) {

            CameraDeviceHelper.CCC_A0(usbDeviceConnection,1, 1, 1);
            CameraDeviceHelper.CCC_A6(usbDeviceConnection,(byte) 0);
            logCameraInfo();

            UsbEndpoint usbEndpoint = cameraHandler.getUsbEndpointList()==null? null : cameraHandler.getUsbEndpointList().get(0);
            new ReadUsbSingleFrameTask(cameraHandler.getUsbDeviceConnection(),usbEndpoint).execute();
        }

        else if ( cameraName.equals("QHY5II-IO") ){


            CameraDeviceHelper.CCC_A0(usbDeviceConnection,0,1,1);
            logCameraInfo();
            CameraDeviceHelper.CCC_A6(usbDeviceConnection,(byte)0);

        }
        else if(cameraName.equals("PoleMaster-IO"))
        {
            CameraDeviceHelper.CCC_A0(usbDeviceConnection,0,1,1);
            logCameraInfo();
            CameraDeviceHelper.CCC_A6(usbDeviceConnection,(byte)0);
        }
    }

    private void onGetDeviceDone(CameraDevice cameraDevice) {
        Toast.makeText(this,"获取设备成功" + cameraDevice, Toast.LENGTH_SHORT).show();
    }

    void showImageOnImageBox(byte[] ImgData, int shift, ImageView imgview, int x, int y, int bpp){
        Bitmap bmp = null;
        bmp = Bitmap.createBitmap(x,y, Bitmap.Config.ARGB_8888);


        int[] bmpdata = new int[x*y];

        byte pixel;
        int k=shift;
        int s=0;

        if(bpp==8) {

            for (int j = 0; j < y; j++) {
                for (int i = 0; i < x; i++) {
                    pixel = ImgData[k];
                    bmpdata[s] = 0xff000000 + pixel + (pixel << 8) + (pixel << 16);
                    k++;
                    s++;
                }
            }
        }

        else{
            for (int j = 0; j < y; j++) {
                for (int i = 0; i < x; i++) {
                    pixel = ImgData[k];
                    bmpdata[s] = 0xff000000 + pixel + (pixel << 8) + (pixel << 16);
                    k=k+2;
                    s=s+1;
                }
            }

        }

        bmp.setPixels(bmpdata, 0, x, 0, 0, x, y);


        imgview.setImageBitmap(bmp);

    }






    public class ReadUsbAsync extends AsyncTask {
        int rx_byte=0;
        protected void onHead(){
            //identity the bad/good frame
            if (log_request_received_total != (BITMAP_HEIGHT * BITMAP_WIDTH)+5){
                Log.v("Bad frames", "receive byte=" + String.valueOf(rx_byte) + "pixelPosition=" + String.valueOf(pixelPosition) + "packageTotal=" + log_request_received_total);
                if(flag_ab==true) flag_frameGood_a=false;
                else               flag_frameGood_b=false;
                frame_count_bad_period10++;
            }else{
                if(flag_ab==true) flag_frameGood_a=true;
                else               flag_frameGood_b=true;
            }

            //renew the flag
            if(flag_ab==true)  flag_isNew_a=true;
            else               flag_isNew_b=true;

            pre_pixelposition=pixelPosition; //max pixel position of previous image
            pixelPosition=0;
            flag_ab= !flag_ab;        //switch double buffer flag
            frame_count++;
            frame_count_period10++;  //this counter is used for static the bad frames in each 10 frames.
            //static the bad frame ration in last 10frames.
            if (frame_count_period10>9) {
                frame_count_period10=0;
                frame_count_bad_result=frame_count_bad_period10;
                frame_count_bad_period10=0;
            }

            log_request_count=0;
            log_request_received_total =0;
            if (flag_ab==true ) log_request_size_a[log_request_count] = rx_byte;
            else                 log_request_size_b[log_request_count] = rx_byte;



        }

        protected Object doInBackground(Object[] params) {
            Log.i(TAG,"### READ ###");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.e(TAG,e.getMessage());
            }
            onStreaming=1;
            pixelPosition=0;
            frame_count=0;


            for(int i=0;i<memsize;i++) request[i]=new UsbRequest();
            for(int i=0;i<memsize;i++) request[i].initialize(cameraHandler.getUsbDeviceConnection(),cameraHandler.getUsbEndpointList().get(0));
            for(int i=0;i<memsize;i++) request[i].queue(xbuffer[i], 16384);

            int x=0;


            while(onStreaming==1){
                //Log.d(TAG,"#### Loading stream...");
                cameraHandler.getUsbDeviceConnection().requestWait();
                rx_byte=xbuffer[x].position();

                //there is some conditions of the return pakcage size; for example:
                //5 bytes.   16384 bytes   and   the bytes between  5 to 16384. We will do different work based on this number
                //andorid usb class can only handle maxium 16384 byte transfer.

                if(rx_byte== 5 ){

                    log_request_received_total=log_request_received_total+5;

                    if ((xbuffer[x].get(0)==-86) && (xbuffer[x].get(1)==17) && (xbuffer[x].get(2)==-52) && (xbuffer[x].get(3)==-18))
                    {
                        //frame head possible condition #1: received a package only has five byte inside and it is just the 0xaa 11 cc ee xx
                        onHead();
                    }

                    else{

                        if(pixelPosition<BITMAP_WIDTH*BITMAP_HEIGHT-5) {
                            if (flag_ab==true) System.arraycopy(xbufferdata[x], 0, ImgDataA, pixelPosition, 5);
                            else                System.arraycopy(xbufferdata[x], 0, ImgDataB, pixelPosition, 5);
                            pixelPosition = pixelPosition + 5;

                            if(log_request_count < memsize -2)
                            {
                                log_request_count++;
                            }
                            else
                            {

                            }

                            if (flag_ab==true ) log_request_size_a[log_request_count] = rx_byte;
                            else                 log_request_size_b[log_request_count] = rx_byte;
                        }
                    }
                }

                else if(rx_byte==0){
                }
                else if(rx_byte>5){

                    log_request_received_total=log_request_received_total+rx_byte;
                    if(log_request_count < memsize -2)
                    {
                        log_request_count++;
                    }
                    else
                    {}

                    if (flag_ab == true) log_request_size_a[log_request_count] = rx_byte;
                    else                  log_request_size_b[log_request_count] = rx_byte;

                    if(pixelPosition<=BITMAP_WIDTH*BITMAP_HEIGHT-rx_byte) {
                        /*final int finalX = x;
                        int length = 0;
                        if( ImgDataA.length - pixelPosition > 16384 ){
                            length = 16384;
                        }else{
                            length = ImgDataA.length - pixelPosition ;
                        }*/
                        try{

                            if (flag_ab == true) System.arraycopy(xbufferdata[x], 0, ImgDataA, pixelPosition, rx_byte);
                            else                  System.arraycopy(xbufferdata[x], 0, ImgDataB, pixelPosition, rx_byte);
                            pixelPosition = pixelPosition + rx_byte;

                        }catch(Exception e){
                        }

                    }

                    //frame head possible condition #2: the five head byte is in the end of one package of return data.
                    if ((xbuffer[x].get(rx_byte-5)==-86) && (xbuffer[x].get(rx_byte-4)==17) && (xbuffer[x].get(rx_byte-3)==-52) && (xbuffer[x].get(rx_byte-2)==-18))
                    {
                        onHead();
                        Log.v(TAG,"head on end" );
                    }


                }


                xbuffer[x].clear();                          //generic a new request
                request[x].queue(xbuffer[x], 16384);


                x++;
                if (x>memsize-1) x=0;
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            frame_count=0;
            frame_count_period10=0;
            flag_frameGood_a=false;
            flag_frameGood_b=false;
            flag_isNew_a=false;
            flag_isNew_b=false;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }

    //// TODO: 2017/6/12 这里有一个问题，显示 的时候，是多线程，赋值的时候是另外一个线，导致显示 的时候，出来2484度，1774fps这样的数据 出来。
    ////需要处理多线程带来的问题
    private void readFps(){
        final int _frame_rate=frame_count-frame_rate;
        final int _refresh_rate = refresh_count - refresh_rate;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if( mSettingBarView != null ){
                    mSettingBarView.setFps(_frame_rate);
//                    mSettingBarView.setTemperature(frame_count_bad_result);
                    mSettingBarView.setTemperature(_refresh_rate);
                }
            }
        });

        frame_rate=frame_count;
        refresh_rate = refresh_count;
    }


    class VideoThread extends Thread {
        private SurfaceHolder holder;

        Bitmap bmp ;

        public VideoThread(SurfaceHolder holder) {
            this.holder = holder;
            isRun = true;
        }

        @Override
        public void run() {
            int count = 0;
            while (isRun) {
                Canvas c = null;
                try {
                    synchronized (holder) {
                        c = holder.lockCanvas();//锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了。
                        Paint p = new Paint(); //创建画笔

                        bmp = getCachedBitmap();

                        if( c != null && bmp != null ){
                            final Bitmap scaledBmp = BitmapHelper.scaleDown(bmp,500,true);
                            c.drawBitmap(scaledBmp,null,holder.getSurfaceFrame(),p);


                            histogramImageView.post(new Runnable() {
                                @Override
                                public void run() {
                                    showHist(scaledBmp , histogramImageView.getMeasuredWidth(), histogramImageView.getMeasuredHeight());

                                }
                            });



                            if( isRecording ){
                                final String frameFileName = "p" + videoTmpFileIndex + ".jpeg";
                                Log.i(TAG,frameFileName);
                                videoTmpFileIndex ++;

                                new Thread(){
                                    @Override
                                    public void run() {
                                        if( bmp != null ){
                                            File videoTmpFile = new File(PicHelper.getVideoTmpPath(getApplicationContext()), frameFileName );
                                            PicHelper.saveJpegToFile(videoTmpFile.getAbsolutePath(), bmp);
                                        }
                                    }
                                }.start();
                            }
                        }

                        //readFps();
                        refresh_count++;
                        Thread.sleep(10);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (c != null) {
                        holder.unlockCanvasAndPost(c);//结束锁定画图，并提交改变。
                    }
                }
            }
        }
    }

    private void showHist(Bitmap scaledBmp, int width ,int height) {
        if( isHistDrawing == true ){
            return ;
        }else{
            isHistDrawing = true;
            cameraViewportPresenter.showHist(scaledBmp,width,height);
        }

    }

    private Bitmap getCachedBitmap() {
        if(onDisplay==true) return null;
        onDisplay=true;

        //添加一个判断，如果 有双通道刷新 ，则刷新 ，如果没有刷新 ，则不去计算。

        boolean isRefresh = false;
        //copy the image from double image buffer to the image. and ignore the bad frame
        if(flag_ab==true) {
            if (flag_frameGood_b == true && flag_isNew_b ==true) {
                System.arraycopy(ImgDataB, 0, ImgDataX, 0, BITMAP_WIDTH * BITMAP_HEIGHT);
                flag_isNew_b=false;
                display_actual_fps_counter++;
                isRefresh  = true;
            }
        }
        else {
            if (flag_frameGood_a == true && flag_isNew_a ==true) {
                System.arraycopy(ImgDataA, 0, ImgDataX, 0, BITMAP_WIDTH* BITMAP_HEIGHT);
                flag_isNew_a=false;
                display_actual_fps_counter++;
                isRefresh = true;
            }
        }
        if(isRefresh)
        {
            byte pixel;
            int k=0;
            for(int j=0;j<BITMAP_HEIGHT;j++){
                for(int i=0;i<BITMAP_WIDTH;i++){
                    pixel=ImgDataX[k];
                    bmpdata[k]=0xff000000+pixel+(pixel<<8)+(pixel<<16);
                    k++;
                }
            }

            if( !isCapturing ){
                bmp1.setPixels(bmpdata, 0, BITMAP_WIDTH, 0, 0, BITMAP_WIDTH, BITMAP_HEIGHT);
            }
        }
        onDisplay=false;
        return bmp1;
    }

    private void loadFFMpegBinary() {
        try {
            FFmpeg ffmpeg = FFmpeg.getInstance(getApplicationContext());
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    showUnsupportedExceptionDialog();
                }

                @Override
                public void onSuccess() {
                    //textView.append("读取FFmpeg library 成功 \n");
                    Log.i(TAG,"读取FFmpeg library 成功");
                    Toast.makeText(getApplicationContext(),"读取FFmpeg library 成功",Toast.LENGTH_SHORT).show();
                }
            });
        } catch (FFmpegNotSupportedException e) {
            showUnsupportedExceptionDialog();
        }
    }

    private void showUnsupportedExceptionDialog() {
        new AlertDialog.Builder(CameraViewportActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("不被支持的设备")
                .setMessage("不支持ffmpeg")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CameraViewportActivity.this.finish();
                    }
                })
                .create()
                .show();
    }

    private void execFFmpegBinary(final String[] command,final ExecuteBinaryResponseHandler handler) {
        try {

            FFmpeg.getInstance(getApplicationContext()).execute(command, handler);
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
            //textView.append(e.getMessage() + "\n");
            Log.e(TAG,"### error :" + e.getMessage());
        }

    }

    private void sendCommand(SettingMenuItem item, SettingMenuDetailItem detailItem) {
        final UsbDeviceConnection usbDeviceConnection = cameraHandler.getUsbDeviceConnection();

        String resolutionCateName = getResources().getString(R.string.resolution);
        String trafficeCateName = getResources().getString(R.string.traffice);
        String analogGainCateName = getResources().getString(R.string.analogGain);
        String digitalGainCateName = getResources().getString(R.string.digitalGain);
        String speedCateName = getResources().getString(R.string.speed);
        String exposureTimeCateName = getResources().getString(R.string.exposureTime);

        final String subCategoryName =  detailItem.getName();
        if( resolutionCateName.equals(item.getName()) ){

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Integer width = Integer.parseInt(subCategoryName.substring(0,subCategoryName.indexOf("*")).trim());
                    Integer height = Integer.parseInt(subCategoryName.substring(subCategoryName.indexOf("*")+1).trim());

                    BITMAP_WIDTH = width;
                    BITMAP_HEIGHT = height;
                    bmp1.setWidth(width);
                    bmp1.setHeight(height);
                    CameraDeviceHelper.CCC_A2(cameraHandler.getUsbDeviceConnection(),(byte)0,width,0,height,0);
                }
            },100);

        }else if( trafficeCateName.equals(item.getName()) ){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    CameraDeviceHelper.CCC_A5(usbDeviceConnection,Byte.parseByte(subCategoryName));
                }
            },100);
        }else if( analogGainCateName.equals(item.getName()) ){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    CameraDeviceHelper.CCC_A4(usbDeviceConnection, Integer.decode(subCategoryName),Integer.decode(subCategoryName),Integer.decode(subCategoryName),Integer.parseInt("30"),Integer.parseInt("30"),Integer.parseInt("30"));
                }
            },100);
        }else if( digitalGainCateName.equals(item.getName()) ){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    CameraDeviceHelper.CCC_A4(usbDeviceConnection,Integer.decode("2"),Integer.decode("2"),Integer.decode("2"),Integer.parseInt(subCategoryName),Integer.parseInt(subCategoryName),Integer.parseInt(subCategoryName));
                }
            },100);
        }else if( speedCateName.equals(item.getName()) ){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    SharedPreferencesHelper.put(CameraViewportActivity.this, SharedPreferencesHelper.KEY_SETTING_SPEED , subCategoryName);
                    CameraDeviceHelper.CCC_A1(usbDeviceConnection,Byte.parseByte(subCategoryName));
                }
            },100);
        }else if( exposureTimeCateName.equals(item.getName()) ){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    CameraDeviceHelper.CCC_A3(usbDeviceConnection, TimeHelper.toUs(subCategoryName) );
                }
            },100);
        }
    }

    private void saveSetting(Category category, SubCategory subCategory) {
        if( category != null && subCategory != null ){
            SharedPreferencesHelper.put(getApplicationContext(), SharedPreferencesHelper.KEY_SETTING_CATEGORY_PRIFIX + category.getIndex(), subCategory.getIndex() );

        }
    }
}
