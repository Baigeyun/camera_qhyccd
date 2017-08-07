package com.starrysky.activity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.starrysky.BaseActivity;
import com.starrysky.R;
import com.starrysky.adapter.ScanResultListAdapter;
import com.starrysky.contract.ScanResultContract;
import com.starrysky.dto.CameraDevice;
import com.starrysky.dto.CameraHandler;
import com.starrysky.event.MessageEvent;
import com.starrysky.event.ScanResultMessageEvent;
import com.starrysky.helper.Constants;
import com.starrysky.helper.SimpleDividerItemDecoration;
import com.starrysky.listener.OnScanResultItemClickListener;
import com.starrysky.presenter.ScanResultPresenter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.starrysky.helper.Constants.EXTRA_KEY_PRODUCT_ID;
import static com.starrysky.helper.Constants.EXTRA_KEY_VENDOR_ID;


public class ScanResultActivity extends BaseActivity implements ScanResultContract.View{
    public static final String TAG = "ScanResultActivity";
    private static final String ACTION_USB_ATTACHED  = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    private static final String ACTION_USB_DETACHED  = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    private static final String ACTION_USB_PERMISSION  = "com.starrysky.USB_CAMERA_PERMISSION";
    private static final String EXTRA_KEY_CAMERA_DEVICE = "cameraDevice";


    /**/
    private boolean doubleBackToExitPressedOnce = false;
    private CameraHandler cameraHandler;
    private CameraDevice cameraDevice;
    private boolean isScanning = false;
    private boolean hasDownloadFirmware = false;

    /* component */
    @BindView(R.id.contentView) ConstraintLayout contentView;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.notFoundTextView) TextView notFoundTextView;
    @BindView(R.id.waitView) View waitView;
    @BindView(R.id.waitText) TextView waitText;

    /* resource */
    @BindString(R.string.pleaseWait) String pleaseWaitStr;
    @BindString(R.string.error) String errorStr;
    @BindString(R.string.canNotOpenCamera) String canNotOpenCameraStr;


    /* adapter */
    ScanResultListAdapter adapter;

    /* present */
    private ScanResultPresenter scanResultPresenter;


    /* broadcast receiver */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    Integer vendorId = intent.getIntExtra(EXTRA_KEY_VENDOR_ID,0);
                    Integer productId = intent.getIntExtra(EXTRA_KEY_PRODUCT_ID,0);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {

                        scanDevice();

                    } else {
                        Toast.makeText(ScanResultActivity.this,"无法获取权限",Toast.LENGTH_SHORT).show();
                    }
                }
            } else if( ACTION_USB_DETACHED.equals(action) ){
                scanDevice();
            } else if( ACTION_USB_ATTACHED.equals(action) ){
                scanDevice();
            }
            else
            {
                Toast.makeText(ScanResultActivity.this,"没有搜索到设备",Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void onUsbCameraPermissionGranted(CameraDevice cameraDevice) {
        if( cameraDevice != null ){
            if( hasDownloadFirmware == false ){
                List<CameraDevice> cameraDevicesList = new ArrayList<>();
                cameraDevicesList.add(cameraDevice);
                scanResultPresenter.downloadFirmware(cameraDevicesList);


            }else{
                Intent intent = new Intent(ScanResultActivity.this, CameraViewportActivity.class);
                intent.putExtra(Constants.EXTRA_KEY_VENDOR_ID ,cameraDevice.getVid());
                intent.putExtra(EXTRA_KEY_PRODUCT_ID ,cameraDevice.getPid());

                startActivity(intent);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();

        scanResultPresenter = new ScanResultPresenter(ScanResultActivity.this);

        if ( ACTION_USB_ATTACHED.equalsIgnoreCase(getIntent().getAction())) {
        }



    }

    private void initView() {
        ButterKnife.bind(this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        adapter = new ScanResultListAdapter(ScanResultActivity.this,null);
        adapter.setOnScanResultItemClickListener(new OnScanResultItemClickListener() {


            @Override
            public void onClick(CameraDevice cameraDevice) {
                boolean hasGrantPermission = checkUsbDevicePermission(cameraDevice);

                if( hasGrantPermission == true ){
                    ScanResultActivity.this.cameraDevice = cameraDevice;
                    onUsbCameraPermissionGranted(cameraDevice);
                }

            }
        });
        recyclerView.setAdapter(adapter);
    }

    private boolean checkUsbDevicePermission(CameraDevice cameraDevice) {
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);

        String deviceName = cameraDevice.getCameraName();
        UsbDevice device = cameraDevice.getDevice();
        if (!manager.hasPermission(device)) {
            Intent intent = new Intent(ACTION_USB_PERMISSION);
            intent.putExtra(EXTRA_KEY_VENDOR_ID , cameraDevice.getVid());
            intent.putExtra(EXTRA_KEY_PRODUCT_ID , cameraDevice.getPid());

            PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            manager.requestPermission(device, mPermissionIntent);
            return false;
        } else {
            return true;
        }
    }


    private void setStateWait() {
        waitView.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.GONE);
        notFoundTextView.setVisibility(View.GONE);
    }

    private void setStateFoundDevices() {
        contentView.setVisibility(View.VISIBLE);
        waitView.setVisibility(View.GONE);
        notFoundTextView.setVisibility(View.GONE);
    }

    private void setStateNotFoundDevices() {
        notFoundTextView.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.GONE);
        waitView.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        scanDevice();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_ATTACHED);
        filter.addAction(ACTION_USB_DETACHED);
        filter.addAction(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver,filter);
    }

    private void scanDevice() {
        if( isScanning == false ){
            isScanning = true;
            setStateWait();
            scanResultPresenter.loadCameraDevice();
        }
        else
        {

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(mUsbReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        scanResultPresenter.clean();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ScanResultMessageEvent event) {
        Log.i(TAG,"### 获取事件 ScanResultMessageEvent,Type:" + event.getType());
        if( event != null ){
            String type = event.getType();
            if( type.equals(MessageEvent.TYPE_LOAD_CAMERA_DEVICE_DONE) ){
                //加载 相机设备完成
                List<CameraDevice> cameraDevicesList = (event.getData() == null)? null : (List<CameraDevice>)event.getData();

                onLoadCameraDeviceDone(cameraDevicesList);

            }else if( type.equals(MessageEvent.TYPE_DOWNLOAD_FIRMWARE_DONE) ){
                //下载固件完成事件
                List<CameraDevice> downloadedCameraDevicesList = (event.getData() == null)? null : (List<CameraDevice>)event.getData();

                onDownloadFirmwareDone(downloadedCameraDevicesList);
            }else if( type.equals(MessageEvent.TYPE_SCAN_RESULT_PROGRESS_UPDATE) ){
                //扫描结果 上报事件
                String message = (event.getData() == null)? null : (String)event.getData();

                onProgressUpdate(message);
            }
        }
    }

    private void onProgressUpdate(String message) {
        waitText.setText(message);
    }

    public void onLoadCameraDeviceDone(List<CameraDevice> cameraDevicesList) {
        Log.i(TAG,"### Scan Camera Devices Done,found count:" + cameraDevicesList.size());
        if( cameraDevicesList == null ||  cameraDevicesList.size() == 0 ){
            setStateNotFoundDevices();
        }else{
            if( adapter != null ){
                adapter.setData(cameraDevicesList);
                setStateFoundDevices();
            }
        }
        isScanning = false;
    }

    public void onDownloadFirmwareDone(List<CameraDevice> downloadedCameraDevicesList) {
        if( hasDownloadFirmware == false ){
            hasDownloadFirmware = true;

            if( downloadedCameraDevicesList != null && downloadedCameraDevicesList.size() > 0 ){
                onLoadCameraDeviceDone(downloadedCameraDevicesList);
                this.cameraDevice = downloadedCameraDevicesList.get(0);
            }
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if ( ACTION_USB_ATTACHED.equalsIgnoreCase(getIntent().getAction())) {
        }
    }
}
