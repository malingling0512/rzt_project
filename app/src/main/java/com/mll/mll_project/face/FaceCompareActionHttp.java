package com.mll.mll_project.face;


import android.util.Log;

import com.google.gson.Gson;
import com.mll.mll_project.face.Action;
import com.mll.mll_project.recog.ServerRequestReadCardState;

import java.util.Map;

public class FaceCompareActionHttp implements Action {

    private RecordRequest recordRequest;

    @Override
    public String doGet(Map<String, String> parms) {
        return null;
    }

    @Override
    public String doPost(String body) {
//        ServerModeDataModel.setWitnessCallback(this);
//        Log.i("FaceCompare", "FaceCompareActionHttp callback:" + callback);
//
//        ServerRequestReadCardState.ActionCallbackWait actionWait;
//        actionWait = new ServerRequestReadCardState.ActionCallbackWait(this);
//
//        this.recordRequest = null;
//
//        if (ConfigManager.getServerMode() && callback != null) {
//            callback.startCommondRead(RecogSession.MODE_FACE_COMPARE, actionWait);
//            // 这里等待30秒，如果还没有读卡就退出
//            actionWait.waitRead(60000 * 3);
//        }
//
//        Log.i("FaceCompare", "doPost");
//        if (recordRequest == null) {
//            actionWait.notifyTimeout();
//            RecordRequest request = ServerModeDataModel.getKongRecordRequest();
//            recordRequest = request;
//        }
//
//        Gson gson = new Gson();
//        return gson.toJson(recordRequest);
        return null;
    }

    private static CommondCallback callback;

    public interface CommondCallback {
        void startCommondRead(int mode, ServerRequestReadCardState.ActionCallbackWait cb);
    }

    public static void setCommondCallback(CommondCallback commondCallback) {
        callback = commondCallback;
    }


}
