package com.mll.mll_project;

public class DevelopTestActivity {

    public interface CameraPreviewObjectCallback {
        void setRotate(int rotation);
    }

    private static CameraPreviewObjectCallback cameraPreviewObjectCallback;
    public static void setCameraPreviewObjectCallback(CameraPreviewObjectCallback callback){
        cameraPreviewObjectCallback = callback;
    }

}
