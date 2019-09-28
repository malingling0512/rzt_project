package com.mll.mll_project.read;

import android.graphics.Bitmap;

import com.wave.rztilib.IVisitorInfo;

public interface WitnessCallBack {

    void updateVisitorDataToUI(Object obj, Bitmap bitmap, int mode);
//    void onRecongniseCompleted(TaskState taskState, IVisitorInfo visitorInfo);

}
