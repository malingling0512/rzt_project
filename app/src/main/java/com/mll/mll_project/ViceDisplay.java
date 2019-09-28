package com.mll.mll_project;

import android.app.Activity;
import android.app.Presentation;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.mll.mll_project.recog.RecogSession;
import com.wave.idcardreader.IdCardInfo;
import com.wave.idcardreader.IdCardReader;
import com.wave.rztcamera.WsCamera;
import com.wave.rztcore.ComponentsInitor;
import com.wave.rztilib.ICardInfo;
import com.wave.rztilib.IVisitorInfo;


public class ViceDisplay {
    private DisplayManager mDisplayManager;
    public ViceDisplayPresentation mPresentation;
    private Display[] displays;

    public  ViceDisplay(Activity context, BroadcastManager broadcastManager){
        mDisplayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        displays = mDisplayManager.getDisplays();//DisplayManager.DISPLAY_CATEGORY_PRESENTATION);

        Log.i("ViceDisplay", "双屏异显,display presentation count: " + displays.length);

        if (displays.length < 2) {
            // 副屏
            mPresentation = new ViceDisplayPresentation(context, displays[0], broadcastManager);
        } else {
            // 主屏
            mPresentation = new ViceDisplayPresentation(context, displays[1], broadcastManager);
        }

        mPresentation.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mPresentation.setOnDismissListener(mOnDismissListener);
    }

    //展示识别状态
    public void showRecongniseState(int state) {
        mPresentation.showRecongniseState(state);
    }

    public void Show() {
        try {
            mPresentation.show();
            Log.i("ViceDisplay", "双屏异显");
        } catch (Exception ex) {
            mPresentation = null;
            Log.i("ViceDisplay", "双屏异显出现异常");
            ex.printStackTrace();
        }
    }

    private final DialogInterface.OnDismissListener mOnDismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            if (dialog == mPresentation) {
                mPresentation .dismiss();
                mPresentation = null;
                Log.i("ViceDisplay", "display 消失了");
            }
        }
    };

    public class ViceDisplayPresentation extends Presentation {

        private static final String TAG = "ViceDisplay";

        private static final int widthScreen = 1920;
        private static final int heightScreen = 1080;

        private Activity outerContext;

        private TextView code, name, nation, gender;        //声明右侧需识别显示的内容控件
        private ImageView pImageView;

        private TextView recongnise_status;
        private BroadcastManager broadcastManager;

        private TextureView mCameraPreview;
        private LinearLayout linearLayout;
        public ViewGroup camera_layout;
        private RelativeLayout view_pager_layout;

        private Bitmap default_photo;
        private TextClock text_clock;
        private TextClock text_clock_2;

        private FrameLayout mCameraLay;

        private EditText mVisitorPhone;

        //定义变量
        byte[] head;
        String name2, id, nation2, id_short;
        int sex;
        Bitmap bitmapImage;


        public ViceDisplayPresentation(Activity outerContext, Display display, BroadcastManager broadcastManager) {
            super(outerContext.getApplicationContext(), display);
            this.broadcastManager = broadcastManager;
            this.outerContext = outerContext;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main_display);

            mCameraLay = findViewById(R.id.camera_layout);
            initCameraInfo();       //调用相机

            CommonComp.getWsCamera().startCamera(WsCamera.BACKCAMERA);      //启动相机

            initView();     //初始化右侧显示信息的控件
            IdCardReader idCardReader = new IdCardReader();
            idCardReader.init("");
            IdCardInfo idCardInfo= (IdCardInfo) idCardReader.read();

            showCardInfo(idCardInfo);

        }

        //初始化相机
        private void initCameraInfo() {
            mCameraPreview = (TextureView) findViewById(R.id.camera_preview);
            mCameraPreview.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    CommonComp.getWsCamera().addSurface(mCameraPreview, width, height,
                            CommonComp.getWsCamera().loadString(mCameraPreview.getContext(), ""),
                            CommonComp.getWsCamera().loadString(mCameraPreview.getContext(), ""));
                    CommonComp.getWsCamera().setFullView(mCameraPreview, WsCamera.FULLVIEW);
                }
                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

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

        //初始化右侧显示信息的控件
        private void initView() {
            default_photo = BitmapFactory.decodeResource(outerContext.getResources(), R.drawable.tx_ico);
            code = (TextView) findViewById(R.id.code);          //证件号
            name = (TextView) findViewById(R.id.name);          //姓名
            gender = (TextView) findViewById(R.id.gender);      //性别
            nation = (TextView) findViewById(R.id.nation);      //民族
            pImageView = (ImageView) findViewById(R.id.photo);      //身份证照

            text_clock = (TextClock) findViewById(R.id.text_clock_1);       //时间
            text_clock_2 = (TextClock) findViewById(R.id.text_clock_2);         //日期
            text_clock.setFormat24Hour("HH:mm");
            text_clock.setFormat12Hour("HH:mm");
            text_clock_2.setFormat24Hour("yyyy-MM-dd  EEEE");
            text_clock_2.setFormat12Hour("yyyy-MM-dd  EEEE");

        }

        //设置组件的值
        protected void showCardInfo(final ICardInfo idcardInfo){
            if (idcardInfo != null){
                head = (byte[])idcardInfo.getHeadImage();
                id = idcardInfo.getCardId();       //身份证号
                id_short = id.substring(0, 4)+"****"+id.substring(14,18);
                name2 = idcardInfo.getName();         //名字
                sex = idcardInfo.getGender();       //性别
                nation2 = idcardInfo.getNation();     //民族
                bitmapImage = getBitmapFromByte(head);    //bitmap类代表位图
            }else {
                head = null;
                id = null;
                name2 = null;
                nation2 = null;
                sex = 0;
                bitmapImage=null;
            }
            pImageView.setImageBitmap(bitmapImage);      //证件照
            code.setText(id_short);     //身份证号
            name.setText(name2);     //名字
            nation.setText(nation2);         //民族
            if (sex==1){        //民族
                gender.setText("男");
            }else if (sex==2){
                gender.setText("女");
            }
        }

        //二进制数组转bitmap文件
        private Bitmap getBitmapFromByte(byte[] temp) {
            if (temp != null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(temp,0,temp.length);
                return bitmap;
            }else {
                return null;
            }
        }

        //重置
        public void switchStatusAndIdInfo(boolean showId) {
            if (showId) {
//                mIdInfoLay.setVisibility(View.VISIBLE);
//                //重置状态
//                mMatterView.setContent("");
                mVisitorPhone.setText("");
                recongnise_status.setVisibility(View.GONE);
            } else {
//                mIdInfoLay.setVisibility(View.GONE);
                recongnise_status.setVisibility(View.VISIBLE);
            }
        }

        //tate 0 证件过期，1 比对中，2 比对失败，3 比对成功
        public void showRecongniseState(final int state) {
            outerContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String info = "";

                    if (state == RecogSession.IS_RecongniseInvalid){
                        info = RecongniseActivity.RecongniseInvalid;
                        recongnise_status.setTextColor(Color.RED);
                    }else if (state == RecogSession.IS_Recongnising){
//                        getWebDataManager().cancelCountDown();
                        info = RecongniseActivity.Recongnising;
                    }else if (state == RecogSession.IS_RecongniseFail){
                        info = RecongniseActivity.RecongniseFail;
                        recongnise_status.setTextColor(Color.RED);
                    }else if (state == RecogSession.IS_RecongniseSuccess){
                        info = RecongniseActivity.RecongniseSuccess;
                        recongnise_status.setTextColor(0xFFFFF000);
                    }else if (state == RecogSession.IS_ReadCardSuccess){
                        info = RecongniseActivity.ReadCardSuccess;
//                        getWebDataManager().cancelCountDown();
                        recongnise_status.setTextColor(Color.GREEN);
                    }else if (state == RecogSession.IS_CheckNotInMainUI){
                        info = "刷卡无效";
                        recongnise_status.setTextColor(Color.RED);
                    }else if (state == RecogSession.IS_IdInfoExpired){
                        info = "证件过期";
                        recongnise_status.setTextColor(Color.RED);
                    }else if (state == RecogSession.IS_InputVisitor_Cancel){
                        info = "已取消";
                        recongnise_status.setTextColor(Color.RED);
                    }else if (state == RecogSession.IS_RepeatReadCard){
                        info = "重复刷卡";
                        recongnise_status.setTextColor(Color.GREEN);
                    }else if (state == RecogSession.IS_CloseUIShowing){
//                        getWebDataManager().countDownTimer(outerContext, ViceDisplayPresentation.this);
                        ((RecongniseActivity) outerContext).showRecongniseState(state, "");
                        return;
                    }else if (RecogSession.IS_InputVisitor_Mode == state){
//                        getWebDataManager().cancelCountDown();
                        return;
                    }else if (RecogSession.IS_InputVisitor_Done == state){
                        switchStatusAndIdInfo(true);
//                        getWebDataManager().countDownTimer(outerContext, ViceDisplayPresentation.this);
                        return;
                    }else {
                        return;
                    }

                    if (state == RecogSession.IS_Recongnising){
                        switchStatusAndIdInfo(true);
                    }else {
                        switchStatusAndIdInfo(false);
                    }

                    recongnise_status.setText(info);
                    ((RecongniseActivity) outerContext).showRecongniseState(state, info);

                    //一定时间内，隐藏屏幕信息显示信息
                    if (state == RecogSession.IS_RecongniseInvalid || state == RecogSession.IS_RecongniseFail ||state == RecogSession.IS_RecongniseSuccess ||
                            state == RecogSession.IS_ReadCardSuccess ||state == RecogSession.IS_CheckNotInMainUI ||state == RecogSession.IS_IdInfoExpired ||
                            state == RecogSession.IS_InputVisitor_Cancel ||state == RecogSession.IS_RepeatReadCard) {
//                        getWebDataManager().countDownTimer(outerContext, ViceDisplayPresentation.this);
                    }

                }
            });
        }



    }

}
