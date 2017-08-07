package com.starrysky.contract;

import com.starrysky.BasePresenter;
import com.starrysky.BaseView;
import com.starrysky.dto.CameraDevice;

import java.util.List;


public class ScanResultContract {
    public interface Presenter extends BasePresenter<ScanResultContract.View> {
        void loadCameraDevice();

        void downloadFirmware(List<CameraDevice> cameraDevicesList);

        void openCamera(CameraDevice cameraDevice);

        void clean();
    }

    public interface View extends BaseView {
    }
}
