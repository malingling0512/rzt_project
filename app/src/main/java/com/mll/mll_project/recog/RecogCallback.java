package com.mll.mll_project.recog;

import android.graphics.Bitmap;

import com.wave.rztilib.ICardInfo;
import com.wave.rztilib.IVisitorInfo;

public interface RecogCallback {

    Bitmap getCameraShot();
    void updateRecogState(final int state);
    void readCardInfo(final ICardInfo visitorInfo, int mode);
    void recognizedVisitor(final IVisitorInfo visitorInfo, int mode);
    void finishedVisitor(final IVisitorInfo visitorInfo, int mode);

}
