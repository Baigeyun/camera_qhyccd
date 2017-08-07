package com.starrysky.contract;

import com.starrysky.BasePresenter;
import com.starrysky.BaseView;
import com.starrysky.dto.CameraDevice;


public class CameraViewportContract {
    public interface Presenter extends BasePresenter<CameraViewportContract.View> {
        void clean();

        void getDevice(Integer vendorId, Integer productId);

        void openCamera(CameraDevice cameraDevice);

        void loadLatestGallery(int count);

        void saveVideo();
    }

    public interface View extends BaseView {
    }
}
