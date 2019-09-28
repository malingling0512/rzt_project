package com.mll.mll_project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.mll.mll_project.face.FaceCompareActionHttp;
import com.mll.mll_project.recog.RecogCallback;
import com.mll.mll_project.recog.RecogManager;
import com.mll.mll_project.recog.RecogSession;
import com.mll.mll_project.recog.ServerRequestReadCardState;
import com.wave.idcardreader.IdCardInfo;
import com.wave.rztcamera.WsCamera;
import com.wave.rztcamera.device.WsCameraLight;
import com.wave.rztcore.ComponentsInitor;
import com.wave.rztilib.ICardInfo;
import com.wave.rztilib.IVisitorInfo;
import com.wave.idcardreader.IdCardReader;

import java.util.List;

public class RecongniseActivity extends AppCompatActivity implements RecogCallback {

    private static final String TAG = "RecogniseActivity";

    //1、声明对象
    private BroadcastManager broadcastManager;
    private ViceDisplay mViceDisplay;

    //2、声明组件
    private TextureView imageViewShot;
    private TextureView mCameraPreview;
    private TextClock text_clock;
    private TextClock text_clock_2;
    private ImageView photo_fu;
    private TextView text_net_state;
    private TextView text_recongnise_state;
    private Button manager_btn;
    private LinearLayout card_info; //信息栏布局组件
    private ViewGroup screen_height;

    byte[] head;
    Bitmap bitmapImage;
    private ImageView default_image;

    private CountDownTimerUtils countDownTimerUtils;

    // 页面跳转停止更新视频流
    public static final String ReadCardSuccess = "读卡成功";
    public static final String Recongnising = "比对中...";
    public static final String RecongniseSuccess = "验证成功";
    public static final String RecongniseFail = "比对失败";
    public static final String RecongniseInvalid = "证件过期!";
    private static final int STOP_COUNTDOWN_TIME = 10001;

    private RecogManager recogManager = RecogManager.getInstance();     //识别管理器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recongnise);

        ComponentsInitor.init(this, "");

        //初始化组件
        initView();

        IdCardReader idCardReader = new IdCardReader();
        idCardReader.init("");
        IdCardInfo idCardInfo= (IdCardInfo) idCardReader.read();
        showInfo(idCardInfo);

        broadcastManager = new BroadcastManager(this);   //定义广播
        broadcastManager.registerBroadcast();       //注册刷卡时的广播

        mViceDisplay = new ViceDisplay(this, broadcastManager);
        mViceDisplay.Show();        //显示副屏

        //计时
        countDownTimerUtils = CountDownTimerUtils.getCountDownTimerUtilsInstance(RecongniseActivity.this, mViceDisplay.mPresentation);


        FaceCompareActionHttp.setCommondCallback((FaceCompareActionHttp.CommondCallback) this);   //面部比较操作的回调

//        this.recogManager.initRecogManager(mViceDisplay.getWebDataManager(), this);       //记录管理器

        Log.i(TAG, "oncreate");

    }

    //初始化控件
    private void initView () {
        text_recongnise_state = (TextView) findViewById(R.id.text_recongnise_state);
        text_net_state = (TextView) findViewById(R.id.text_net_state);
        card_info = (LinearLayout) findViewById(R.id.card_info);
        screen_height = (ViewGroup) findViewById(R.id.screen_height);

        manager_btn = (Button) findViewById(R.id.manager_btn);
        text_clock = (TextClock) findViewById(R.id.text_clock_1);
        text_clock_2 = (TextClock) findViewById(R.id.text_clock_2);
        photo_fu = (ImageView) findViewById(R.id.photo_fu);
        text_clock.setFormat24Hour("HH:mm");
        text_clock.setFormat12Hour("HH:mm");
        text_clock_2.setFormat24Hour("yyyy-MM-dd  EEEE");
        text_clock_2.setFormat12Hour("yyyy-MM-dd  EEEE");

        default_image = (ImageView) findViewById(R.id.default_image);

        imageViewShot = (TextureView) findViewById(R.id.preview_flow);
        imageViewShot.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                CommonComp.getWsCamera().addSurface(imageViewShot, width, height, "", "");
                CommonComp.getWsCamera().setFullView(imageViewShot, WsCamera.FULLVIEW);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                CommonComp.getWsCamera().updateSurface(mCameraPreview, width, height);
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                CommonComp.getWsCamera().removeSurface(mCameraPreview);
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    //识别信息展示
    private void showInfo(final ICardInfo cardInfo){
        if (cardInfo != null){
            head = (byte[])cardInfo.getHeadImage();
            bitmapImage = getBitmapFromByte(head);    //bitmap类代表位图
        }else {
            head = null;
            bitmapImage=null;
        }
        photo_fu.setImageBitmap(bitmapImage);      //证件照
    }

    //二维数组转bitmap图片
    private Bitmap getBitmapFromByte(byte[] temp) {
        if (temp != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(temp,0,temp.length);
            return bitmap;
        }else {
            return null;
        }
    }

    public Bitmap getCameraShot() {
        return CommonComp.getWsCamera().getBitmap();
    }   //获取照相机拍照


    /**
     * 读取完身份证，显示身份证信息，马上进入识别流程
     * @param cardInfo 访客身份证信息
     */
    public void readCardInfo(final ICardInfo cardInfo, final int mode){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mode == RecogSession.MODE_READ_DISCARD){
                    mViceDisplay.showRecongniseState(RecogSession.IS_ReadCardSuccess);
                }
                hideCameraShow(false);  //展示相机
                card_info.setVisibility(View.GONE);

                showInfo(cardInfo);     //// 更新主屏(小屏)访客基础信息
                mViceDisplay.mPresentation.showCardInfo(cardInfo);   // 更新副屏(大屏)访客基础信息
            }
        });
    }       //读卡操作

    @Override
    public void updateRecogState(int state) {
        if (state == RecogSession.IS_NetConntected) {
//            showNetState("网络正常");
        } else {
            mViceDisplay.showRecongniseState(state);
        }
    }   //更新识别状态

    @Override
    public void recognizedVisitor(IVisitorInfo visitorInfo, int mode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 这里是主屏（小屏幕）
                card_info.setVisibility(View.VISIBLE);
                //重置
//                mUnitView.setContent("");
//                mFloorView.setContent("");
//                visitorreason.setText("");
//                visitorphone.setText("");

                //刷新列表
//                initValidFloors();
//                mFloorView.setFloors(mValidFloor);

                // 通知副屏切换到输入界面
                // 把访客的身份证信息显示到输入界面
                // 同时从数据库调出来访事由显示在界面让访客选择
//                mViceDisplay.switchToInputMode(true);
//                mViceDisplay.updateVisitorInfo(visitorInfo);
                // 这里需要关闭计时器，不需要自动关闭访客显示信息
                // 等到点击界面的取消或确定按钮后再次启动定时器，关闭
                mViceDisplay.showRecongniseState(RecogSession.IS_InputVisitor_Mode);

                imageViewShot.setVisibility(View.INVISIBLE);
                CommonComp.getWsCamera().pauseSurface(imageViewShot);
            }
        });
    }   //身份证已经验证成功，切换到输入模式

    @Override
    public void finishedVisitor(IVisitorInfo visitorInfo, int mode) {

    }      //已经处理完访客信息




    @Override
    public void onBackPressed() {
        Toast.makeText(this, "主界面不能执行返回操作", Toast.LENGTH_LONG).show();
    }       //主界面不能执行返回操作

    public void showRecongniseState(int state, String info){        //识别状态
        if (RecogSession.IS_RecongniseFail == state) {      //识别失败，关闭来访者信息
//            cancelVisitorInfo();
        }
        if (RecogSession.IS_Recongnising == state ||  RecogSession.IS_ReadCardSuccess == state) {    //正在识别，设置识别状态字为白色
            text_recongnise_state.setTextColor(Color.WHITE);
        } else if (RecogSession.IS_RecongniseSuccess == state) {        //  识别成功，设置识别状态字可见，并为0xFFFFF000色
//            setNetStateVisible(View.VISIBLE);
            text_recongnise_state.setTextColor(0xFFFFF000);
        } else {
            text_recongnise_state.setTextColor(Color.RED);
//            setNetStateVisible(View.INVISIBLE);
        }
        text_recongnise_state.setText(info);
    }       //展示识别状态

    public void startCommondRead(int mode, ServerRequestReadCardState.ActionCallbackWait cb) {
        boolean mainActivity = isMainActivity();
        if (!mainActivity) {
            return;
        }
//        AudioPlayer.play(AudioPlayer.readcard);
        recogManager.enterReadCardMode(mode, cb);
//        handler.sendEmptyMessage(STOP_COUNTDOWN_TIME);
    }

    private boolean isMainActivity() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = null;
        String[] split = null;
        String topActivityName = null;
        runningTasks = manager.getRunningTasks(1);
        if (runningTasks.size() <= 0) return false;
        split = runningTasks.get(0).topActivity.getClassName().split("\\.");
        topActivityName = split[split.length - 1];
        if (!"RecongniseActivity".equals(topActivityName)) {
            return false;
        }
        return true;
    }     //判断




    private void hideCameraShow(boolean bHidden) {
        if (!bHidden) {
            imageViewShot.setVisibility(View.VISIBLE);
            CommonComp.getWsCamera().resumeSurface(imageViewShot);
        } else {
            imageViewShot.setVisibility(View.INVISIBLE);
            CommonComp.getWsCamera().pauseSurface(imageViewShot);
        }
    }     //隐藏摄像机显示，

//    private void cancelVisitorInfo() { }        //取消访客信息，卡片信息隐藏、相机隐藏、副屏离开输入模式
//    private void initValidFloors(){ }           //初始化有效楼层
//    private void setViewEvents() { }            //两个按钮事件“保存”,“关闭”
//    private void commitVisitorInfo() { }        //提交访客信息
//    public void setPhoneAndReason(String phone, String reason){ }        //设置来访者电话、来访原因


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        Log.i(TAG, "screen_height：" + screen_height.getWidth());
        if (mViceDisplay.mPresentation != null) {
            Log.i(TAG, "camera_layout：" + mViceDisplay.mPresentation.camera_layout.getHeight());
        }
    }       //动态获取控件宽高
    @Override
    protected void onResume() {     //重新开始
        super.onResume();
        recogManager.setMainUIFlag(true);
//        if (!ConfigManager.getServerMode()) {
//            recogManager.startRecog();
//        } else if (recogManager.isCanPauseInServerMode()) {
//            recogManager.pauseRecog();
//        }
        setNetStateVisible(View.VISIBLE);
    }
    @Override
    protected void onPause() {
        super.onPause();
    }       //暂停进程
    @Override
    protected void onStop() {
        super.onStop();
    }       //停止进程
    @Override
    protected void onDestroy() {
        destroySomething();
        super.onDestroy();

        CommonComp.getWsCamera().closeCamera();
        android.os.Process.killProcess(android.os.Process.myPid());
    }       //销毁进程
    public void destroySomething() {
        WsCameraLight.closeLight();
    }       //销毁正在做的事
    public void onClick (View arg0){
        switch (arg0.getId()) {
            case R.id.photo_fu:
                break;
            default:
                break;
        }
    }       //点击事件

//    public void HideInfo() {
//        photo_fu.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tx_ico));
//        text_recongnise_state.setText("");
//        card_info.setVisibility(View.INVISIBLE);
//        default_image.setVisibility(View.VISIBLE);
//        text_net_state.setText("");
//        showNetState("网络正常");
//    }       //隐藏信息
    private void setNetStateVisible(int visibility) {

    }       //设置网络连接状态可见
//    public void showNetState(final String info) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                text_net_state.setText(info);
//                setNetStateVisible(View.VISIBLE);
//            }
//        });
//    }       //展示网络连接状态
//    public void showDefaultImage(boolean isShow) {      //设置小屏logo图默认可见状态
//        if (isShow) {
//            default_image.setVisibility(View.VISIBLE);
//        } else {
//            default_image.setVisibility(View.INVISIBLE);
//        }
//    }
//    public void showMemoryError(int size) { }       //显示内存错误

}
