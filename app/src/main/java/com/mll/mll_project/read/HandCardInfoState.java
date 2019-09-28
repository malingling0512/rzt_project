package com.mll.mll_project.read;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.mll.mll_project.BitmapUtils;
import com.mll.mll_project.recog.ConfigManager;
import com.mll.mll_project.recog.RecogCallback;
import com.mll.mll_project.recog.RecogSession;
import com.mll.mll_project.utils.PathUtils;
import com.mll.mll_project.visitor.DataObjAdapter;
import com.mll.mll_project.visitor.Visitor;
import com.wave.idcardreader.IdCardInfo;
import com.wave.rztcore.ComponentState;
import com.wave.rztilib.DataUtil;
import com.wave.rztilib.ICardInfo;
import com.wave.rztilib.IDataObj;
import com.wave.rztilib.IDatabase;
import com.wave.rztilib.IStateContext;
import com.wave.rztilib.Names;
import com.wave.rztilib.RZTGlobal;

import java.io.File;
import java.util.Date;

/**
 * 读卡成功对ICardInfo进行预处理，其处理流程如下：
 * 1. 如果主界面不在前台，提示刷卡无效
 * 2. 如果读取的不是身份证，直接返回
 * 3. 生成证件头像（Bitmap），存储本地识别时间等初始化操作
 */
public class HandCardInfoState extends ComponentState {

    private final static String TAG = "Wr:HandCardInfoState";

    private String mLastCardNo = "";
    private long mLastRecogTime = 0;
    private long mRepeatTimes = 0;
    private WitnessCallBack mWitnessCb;

    public HandCardInfoState(WitnessCallBack cb) {
        super("");
        this.mWitnessCb = cb;
    }

    private RecogCallback getRecogCb(IStateContext ctx) {
        return (RecogCallback)ctx.getValue(ConstantDef.RECOG_CALLBACK);
    }

    @Override
    protected String getPluginName() {
        return null;
    }


    //检查两次刷卡是否是同一个用户
    private boolean checkIsSamePerson(IStateContext ctx, ICardInfo cardInfo) {
        long lTime = System.currentTimeMillis() - mLastRecogTime;

        // 不需要检查是否重复刷卡逻辑
        if (ConfigManager.getRepeatReadCardTip()) {
            mLastCardNo = "";
            return false;
        }

        // 同一个用户刷卡成功后需要间隔30秒才能刷
        if (mLastCardNo.equals(cardInfo.getCardId()) && lTime < ConstantDef.READ_INTERNAL) {
            setInterrupt(true);
            ++mRepeatTimes;

            if ((mRepeatTimes % 2) == 0) {
                //AudioPlayer.play(AudioPlayer.wavWarn);
                getRecogCb(ctx).updateRecogState(RecogSession.IS_RepeatReadCard);
            }
            waitMill(ConstantDef.SAMEPERSON_WAIT_TIME);
            return true;
        }
        mRepeatTimes = 0;
        mLastRecogTime = System.currentTimeMillis();
        return false;
    }

    //刷身份证流程
    @Override
    public boolean execute(IStateContext ctx, IDataObj dataObj){
        setResult(dataObj);
        int mode = (int)ctx.getValue(ConstantDef.RECOG_MODE);       // 识别模式，主要服务模式下使用（mode:模式）

        // 如果不在主UI界面，提示刷卡无效
        if (!((boolean)ctx.getValue(ConstantDef.IS_INMAINUI))) {
            waitMill(ConstantDef.NOTINMAINUI_WAIT_TIME);
            return false;
        }

        // 目前仅仅支持身份证，所以这里需要判断下
        if (!dataObj.getType().equals(IdCardInfo.ID_TYPE)) {
            Log.w(TAG, "请刷身份证！");
            return false;
        }

        // 等于0表示等待客户端连接
        if (mode == RecogSession.MODE_WAIT_SERVERMODE || ctx.isPaused()) {
            waitMill(1000);
            return false;
        }

        if (dataObj instanceof ICardInfo){
            IdCardInfo cardInfo = (IdCardInfo)dataObj;

            // 检查两次刷卡是否是同一个用户
            if (checkIsSamePerson(ctx, cardInfo)) {
                return true;
            }

            ctx.setValue(ConstantDef.RECOG_RESULT, 0);  //识别结果

            setInterrupt(false);

            // 如果是服务器模式下，获取识别模式，后面作为UI更新显示用【获取识别模式】
            int recogMode = DataUtil.getInteger(dataObj, ConstantDef.RECOG_MODE, RecogSession.MODE_FACE_COMPARE);//识别、人脸对比

            Date date = new Date(System.currentTimeMillis());//系统时间

            Bitmap headBmp = getHeadBitmap(dataObj);        //得到证件照的Bitmap

            if (null == headBmp) {
                Log.e(TAG, "份证头像为空！");
                getRecogCb(ctx).updateRecogState(RecogSession.IS_CloseUIShowing);   //回调
                return false;
            }

            cardInfo.setValue(ConstantDef.HEAD_BITMAP, headBmp);  //public void setValue(String name, Object value) { }//设置身份证信息的证件照

            // 读卡成功，卡的信息更新到界面显示
            getRecogCb(ctx).updateRecogState(RecogSession.IS_ReadCardSuccess);  //更新识别状态
            getRecogCb(ctx).readCardInfo(cardInfo, mode);       //读卡

            // 这里转换dataObj到Visitor
            final Visitor visitor = DataObjAdapter.convert(cardInfo);   //创建visitor
//            visitor.setTimestamp(date);

            // 把转换后的Visitor对象、证件头像和开始识别时间保存到
            // 状态上下文，后面的状态可以取出来使用，这个识别时间在这里设置
            // 其它状态不能设置，因为此时间用来生成现场照片的名称用

            ctx.setValue(ConstantDef.VISITOR_OBJECT, visitor);  //识别现场的照片
            ctx.setValue(ConstantDef.HEAD_BITMAP, headBmp);     //身份证头像

            ctx.setValue(ConstantDef.RECOG_DATE, date);     // 记录当前的记录识别时间

            savePhoto(headBmp, visitor);    // 保存证件头像到本地

            // 更新界面显示
            mWitnessCb.updateVisitorDataToUI(visitor, headBmp, recogMode);

            return true;
        } else {
            return false;
        }
    }

    //保存获取到的证件照
    private void savePhoto(Bitmap headBmp, Visitor visitor) {
        String photoFile = PathUtils.getPhotoFile(RZTGlobal.getContext(), visitor.getCode());
        File pfile = new File(photoFile);

        if (!pfile.exists()) {
            try {
                pfile.createNewFile();
                boolean savePhoto2Loacl = BitmapUtils.savePhoto2Loacl(pfile, headBmp);
                if (!savePhoto2Loacl) {
                    pfile.delete();
                }
                visitor.setPhoto(photoFile);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "call savePhoto exception:" + photoFile);
            }
        } else {
            visitor.setPhoto(photoFile);
        }
    }

    //证件照转换为Bitmap
    private Bitmap getHeadBitmap(IDataObj dataObj) {
        Bitmap headBmp = null;
        Object obj = dataObj.getValue(Names.HEADIMAGE_N);

        if (null != obj && obj instanceof byte[]) {
            // 人脸头像转换为Bitmap
            byte[] bytes = (byte[]) obj;
            headBmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return headBmp;
    }
}
