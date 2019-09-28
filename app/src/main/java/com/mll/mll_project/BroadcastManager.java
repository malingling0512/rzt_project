package com.mll.mll_project;

import android.content.Context;

public class BroadcastManager {

    private Context mcontext;
    protected static final String TAG = "BroadcastManager";
    public BroadcastManager(Context context) {
        this.mcontext = context;
    }

    /**
     * 注册刷卡时候的广播
     */
    public void registerBroadcast() {
        //IntentFilter filter = new IntentFilter();
        //filter.addAction(MyApplication.ACTION_ID_CARD);
    }

}
