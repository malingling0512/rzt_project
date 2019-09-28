package com.mll.mll_project;

import com.wave.rztcore.ComponentsInitor;
import com.wave.rztilib.IFaceInitor;
import com.wave.rztilib.IWsCamera;

public class CommonComp {

    private static final String WSCAMERA_CLASS = "com.wave.rztcamera.WsCamera";
    private static final String FACEINITOR_CLASS = "com.wave.facerecog.FaceInitor";

    private static IWsCamera mWsCamera;
    private static IFaceInitor mFaceInitor;

    public static IWsCamera getWsCamera() {
        if (null == mWsCamera) {
            mWsCamera = ComponentsInitor.loadComponent(WSCAMERA_CLASS);
        }
        return mWsCamera;
    }

    public static IFaceInitor getFaceInitor() {
        if (null == mFaceInitor) {
            mFaceInitor = ComponentsInitor.loadComponent(FACEINITOR_CLASS);
        }
        return mFaceInitor;
    }

}
