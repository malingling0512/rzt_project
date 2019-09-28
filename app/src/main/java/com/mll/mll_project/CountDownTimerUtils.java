package com.mll.mll_project;

import android.content.Context;
import android.os.CountDownTimer;
import com.mll.mll_project.ViceDisplay.ViceDisplayPresentation;

public class CountDownTimerUtils {

    private Context context;
    private ViceDisplay.ViceDisplayPresentation presentation;

    private static CountDownTimerUtils cDownTimerUtils;

    public CountDownTimerUtils(Context context, ViceDisplay.ViceDisplayPresentation presentation) {
        this.context = context;
        this.presentation = presentation;
    }

    public static CountDownTimerUtils getCountDownTimerUtilsInstance(Context context, ViceDisplayPresentation presentation) {
        if (cDownTimerUtils == null) {
            cDownTimerUtils = new CountDownTimerUtils(context, presentation);
        }
        return cDownTimerUtils;
    }

}
