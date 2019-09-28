package com.mll.mll_project.recog;

import android.graphics.Bitmap;
import android.util.Log;

import com.mll.mll_project.CommonComp;
import com.wave.rztcamera.device.WsCameraLight;
import com.wave.rztcore.ComponentState;
import com.wave.rztcore.PersonInfo;
import com.wave.rztcore.SoundPlayer;
import com.wave.rztcore.VisitorInfo;
import com.wave.rztilib.DataUtil;
import com.wave.rztilib.ICardInfo;
import com.wave.rztilib.IDataObj;
import com.wave.rztilib.IFaceCompare;
import com.wave.rztilib.IPersonInfo;
import com.wave.rztilib.ISceneGather;
import com.wave.rztilib.IStateContext;
import com.wave.rztilib.Names;

import java.util.Date;

/**
 * 人脸比对，通过摄像头采集的图像和证件头像比对，其处理流程如下：
 * 1. 校验证件头像是否为空
 * 2. 检查证件有效期
 * 3. 开始识别（默认8秒超时时间）
 */
public class CompareFaceState extends ComponentState {

    final static public String TAG = "Wr:CompareFaceState";

    private final SceneGather mSceneGather = new SceneGather();

    static class SceneGather implements ISceneGather {

        RecogCallback cb;
        IStateContext ctx;

        public SceneGather() {
        }

        public void setSceneGather(IStateContext ctx, RecogCallback cb) {
            this.cb = cb;
            this.ctx = ctx;
        }

        public boolean isCancel() {
            return (ctx.isPaused() || ctx.isStopped());
        }

        public Bitmap gatherImage() {
            return cb.getCameraShot();
        }
        public byte[] gatherBytes() {
            return null;
        }
    }

    public CompareFaceState() {
        super("");
    }

    private RecogCallback getRecogCb(IStateContext ctx) {
        return (RecogCallback)ctx.getValue(ConstantDef.RECOG_CALLBACK);
    }

    protected String getPluginName() {
        return "";
    }

    @Override
    public boolean execute(IStateContext ctx, IDataObj dataObj) {
        setResult(dataObj);
        int result = 0;
        Bitmap headBmp = (Bitmap)ctx.getValue(ConstantDef.HEAD_BITMAP);
        Date date = (Date)ctx.getValue(ConstantDef.RECOG_DATE);
        // 到访时间，如果前面状态没有设置，就采用当前时间
        if (null == date) {
            date = new Date(System.currentTimeMillis());
        }
        // 没有人脸头像
        if (null == headBmp) {
            getRecogCb(ctx).updateRecogState(RecogSession.IS_CloseUIShowing);
            return false;
        }
        // 存储刷卡时现场照片
        // DataObjAdapter.saveSnapShot(CommonComp.getWsCamera().getBitmap(), date);
        // 检查卡有效期
        if (isCardExpired(dataObj)) {
            getRecogCb(ctx).updateRecogState(RecogSession.IS_IdInfoExpired);
            waitMill(ConstantDef.VALIDDATE_WAIT_TIME);
            return false;
        }
        try {
            // 开启补光灯
//            SoundPlayer.play(AudioPlayer.wavPrompt);
            WsCameraLight.openLight();

            // 开始人脸比对
            result = faceCompare(headBmp, ctx);
        } catch (Exception e) {
            e.printStackTrace();
            getRecogCb(ctx).updateRecogState(RecogSession.IS_CloseUIShowing);
        } finally {
            // 关闭补光灯
            WsCameraLight.closeLight();
        }
        setRecogResult(dataObj, date, result);
        ctx.setValue(ConstantDef.RECOG_RESULT, result);
        return true;
    }

    /**
     * 此状态可以处理输入类型为ICardInfo和IPersonInfo对象的数据
     * 但最终经过此状态转换输出是标准的IVisitorInfo对象
     * @param dataObj 输入的数据对象
     * @param result 识别结果
     */
    private void setRecogResult(IDataObj dataObj, Date date, int result) {
        VisitorInfo visitor = null;
        if (dataObj instanceof ICardInfo) {
            visitor = new VisitorInfo(new PersonInfo((ICardInfo)dataObj));
        } else if (dataObj instanceof IPersonInfo) {
            visitor = new VisitorInfo((IPersonInfo) dataObj);
        }
        if (null != visitor) {
            visitor.setVisitDate(date.getTime());
            visitor.setPassResult(result);
            setResult(visitor);
        }
    }

    /**
     * 使用现场照片和身份证头像进行比对，如果比对成功返回1
     * 1. 更新界面显示：正在比对中
     * 2. 生成摄像头数据采集接口
     * 3. 识别身份证头像，提取特征码
     * 4. 使用头像特征码和现场照片进行比对，直到超时
     * @param head 身份证头像
     * @param ctx 状态上下文
     * @return 比对成功返回1
     */
    private int faceCompare(Bitmap head, IStateContext ctx) {
        // 识别超时时间，默认8秒
        int recogTimeout = ConfigManager.getRecongniseTimeout();
        int recogResult = RecogSession.Recog_Failed;
        Bitmap bmpShot;
        Long currentTime = System.currentTimeMillis();
        // 开始识别，界面提示
        getRecogCb(ctx).updateRecogState(RecogSession.IS_Recongnising);
        // Log.v(TAG, "开始识别，识别超时时间：" + recogTimeout);
        IFaceCompare faceCompare = CommonComp.getFaceInitor().create();

        try {
            // 设置摄像头采集回调接口
            mSceneGather.setSceneGather(ctx, getRecogCb(ctx));
            // 1. 先识别头像（提取头像的特征码）
            if (0 == faceCompare.recogFace(head)) {
                // 2. 现场照片和头像比对
                bmpShot = faceCompare.faceCompare(mSceneGather, recogTimeout);
                ctx.setValue(ConstantDef.RECOG_BITMAP, bmpShot);
            }
        } catch (Exception e) {
            e.printStackTrace();
            getRecogCb(ctx).updateRecogState(RecogSession.IS_CloseUIShowing);
        } finally {
            // 这里一定要释放算法对象
            faceCompare.release();
        }
        if (faceCompare.getErrorCode() == 0) {
            recogResult = RecogSession.Recog_Sucessfully;
            Log.v(TAG, "识别成功，时间：" + (System.currentTimeMillis() - currentTime));
        } else {
            Log.e(TAG, "识别失败，错误信息：" + faceCompare.errorCodeToString(faceCompare.getErrorCode()));
        }
        return recogResult;
    }

    /**
     * 判断身份证是否过期
     */
    private boolean isCardExpired(IDataObj dataObj) {
        final Date date = new Date(System.currentTimeMillis());
        long from = DataUtil.getLong(dataObj, Names.EFFECTDATE_N, System.currentTimeMillis());
        long to = DataUtil.getLong(dataObj, Names.EXPIREDATE_N, System.currentTimeMillis());
        long time = date.getTime();
        if (time > to || time < from) {
            // 证件过期语音
//            SoundPlayer.play(AudioPlayer.wavExpire);
            return true;
        }
        return false;
    }
}
