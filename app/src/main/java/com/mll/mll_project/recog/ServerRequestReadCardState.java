package com.mll.mll_project.recog;

import android.graphics.Bitmap;
import android.util.Log;

import com.wave.rztcore.ComponentState;
import com.wave.rztilib.IDataObj;
import com.wave.rztilib.IStateContext;
import com.wave.rztilib.RZTGlobal;

import java.util.Date;

public class ServerRequestReadCardState extends ComponentState {

    public static final String TAG = "Wr:ServerRReadCardState";
    public static final Object mLocker = new Object();

    private ActionCallbackWait mActionCbWaint;

    public static class ActionCallbackWait {
//        ActionCallback cb;
        IStateContext ctx;
        final Object locker = ServerRequestReadCardState.mLocker;

//        public ActionCallbackWait(ActionCallback cb) {
//            this.cb = cb;
//        }

        public void setStateContext(IStateContext ctx) {
            this.ctx = ctx;
        }

        public boolean waitRead(int timeout) {
            synchronized (locker) {
                try {
                    locker.wait(timeout);
                } catch (Exception e) {
                    innerNotifyTimeout();
                    return false;
                }
                return true;
            }
        }

        public void notifyTimeout() {
            synchronized (locker) {
                innerNotifyTimeout();
            }
        }

        private void innerNotifyTimeout() {
//            Log.i(TAG, "enter waitRead timeout");
//            if (null != ctx && ConfigManager.getServerMode()) {
//                AudioPlayer.play(AudioPlayer.readcardTimeout);
//                Log.i(TAG, "handle waitRead timeout");
//                ctx.setValue(ConstantDef.RECOG_MODE, RecogSession.MODE_WAIT_SERVERMODE);
//                ctx.pause();
//                ctx = null;
//            }
        }

        public void notifyRead() {
            synchronized (locker) {
                try {
                    locker.notify();
                } catch (Exception e) {

                }
            }
        }
    }

//    private RecogCallback getRecogCb(IStateContext ctx) {
//        return (RecogCallback)ctx.getValue(ConstantDef.RECOG_CALLBACK);
//    }

    public ServerRequestReadCardState(ActionCallbackWait cb) {
        super("");
        mActionCbWaint = cb;
        setInterrupt(true);
    }

    public void setActionCallbackWait(ActionCallbackWait cb) {
        synchronized (mLocker) {
            if (null != mActionCbWaint) {
                mActionCbWaint.notifyRead();
            }
            mActionCbWaint = cb;
        }
    }

    public void notifyWait() {
        synchronized (mLocker) {
            if (null != mActionCbWaint) {
                mActionCbWaint.notifyRead();
            }
        }
    }

    protected String getPluginName() {
        return "";
    }

//    @Override
//    public boolean execute(IStateContext ctx, IDataObj dataObj) {
//        setResult(dataObj);
//        int mode = (int)ctx.getValue(ConstantDef.RECOG_MODE);
//        Bitmap headBmp = (Bitmap) ctx.getValue(ConstantDef.HEAD_BITMAP);
//        Visitor Visitor = (Visitor)ctx.getValue(ConstantDef.VISITOR_OBJECT);

//        // 服务器模式，直接保存数据库，返回
//        if (null != Visitor && null != mActionCbWaint) {
//            // 仅读卡模式，不作比对
//            if (RecogSession.MODE_READ_IDCARD == mode) {
//                setInterrupt(true);
//                commitVistorToServer(ctx, Visitor);
//                Log.i(TAG, "commitVistorToServer，mode：" + mode);
//                synchronized (mLocker) {
//                    if (null != mActionCbWaint) {
//                        mActionCbWaint.cb.onVisitorData(Visitor, headBmp, RecogSession.MODE_READ_DISCARD);
//                    }
//                }
//                getRecogCb(ctx).updateRecogState(RecogSession.IS_CloseUIShowing);
//            } else {
//                setInterrupt(false);
//                Log.i(TAG, "commitVistorToServer，mode：" + mode);
//            }
//            return true;
//        } else {
//            getRecogCb(ctx).updateRecogState(RecogSession.IS_CloseUIShowing);
//            return false;
//        }
//    }

//    @Override
//    public void feedback(IStateContext ctx,boolean failed) {
//        int mode = (int)ctx.getValue(ConstantDef.RECOG_MODE);
//
//        if (ctx.getCurrentSate() != this || mode == RecogSession.MODE_READ_IDCARD) {
//            notifyWait();
//            // 暂停读卡流程执行，等待下一个客户连接请求到来会启动
//            if (ConfigManager.getServerMode()) {
//                ctx.setValue(ConstantDef.RECOG_MODE, RecogSession.MODE_WAIT_SERVERMODE);
//                ctx.pause();
//                Log.i(TAG, "call feedback, pause execute，mode：" + mode);
//            }
//        }
//    }

//    private boolean commitVistorToServer(IStateContext ctx, Visitor Visitor) {
//        Date date = (Date)ctx.getValue(ConstantDef.RECOG_DATE);
//        Bitmap bmp = getRecogCb(ctx).getCameraShot();
//
//        // 存储刷卡时现场照片
//        DataObjAdapter.saveSnapShot(bmp, date);
//
//        VisitorManager visitorManager = new VisitorManager(RZTGlobal.getContext());
//        visitorManager.createOrUpdate(Visitor);
//
//        PassRecordManager passRecordManager = new PassRecordManager(RZTGlobal.getContext());
//        passRecordManager.create(Visitor.getCode(), 1, date, RecogSession.Upload_ServerMode);
//
//        return true;
//    }

}
