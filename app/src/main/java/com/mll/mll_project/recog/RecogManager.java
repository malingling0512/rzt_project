package com.mll.mll_project.recog;

import android.util.Log;

import com.wave.rztcore.AdapterState;
import com.wave.rztcore.CardReaderState;
import com.wave.rztcore.ComponentsInitor;
import com.wave.rztilib.IStateController;

public class RecogManager {

    public static final String TAG = "Wr:RecogManager";
    private static final RecogManager mIns = new RecogManager();

    // 状态控制管理器
    private IStateController mController;
    private ServerRequestReadCardState mServerState;
    private CardReaderState mCardReaderState;
    private RecogCallback mRecogCb;
//    private WebDataManager mWebDataManager;
    // 测试用，用来模拟读卡状态
//    private TestReadCardState mTestReadCard = null;
    private boolean mConnected = true;

    private RecogManager() {
        mController = ComponentsInitor.getStateController();
    }

    public static RecogManager getInstance() {
        return mIns;
    }       //获取实例

//    public WebDataManager getWebDataManager() {
//        return mWebDataManager;
//    }

//    public void setHttpActionCallback(ActionCallback cb) {
//
//    }

//    public void updateNetState() {
//        if (null != mRecogCb) {
//            mRecogCb.updateRecogState(RecogSession.IS_NetConntected);
//        }
//    }

    public void setNetConnected(boolean bConnected) {
        mConnected = bConnected;
    }

    public boolean isNetConnected() {
        return mConnected;
    }

    public static String deUnicode(String content){//将16进制数转换为汉字
        String enUnicode=null;
        String deUnicode=null;
        for(int i=0;i<content.length();i++){
            if(enUnicode==null){
                enUnicode=String.valueOf(content.charAt(i));
            }else{
                enUnicode=enUnicode+content.charAt(i);
            }
            if(i%4==3){
                if(enUnicode!=null){
                    if(deUnicode==null){
                        deUnicode=String.valueOf((char)Integer.valueOf(enUnicode, 16).intValue());
                    }else{
                        deUnicode=deUnicode+String.valueOf((char)Integer.valueOf(enUnicode, 16).intValue());
                    }
                }
                enUnicode=null;
            }

        }
        return deUnicode;
    }

    public byte chartoint(byte c) {
        switch (c) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            case 'A':
            case 'a':
                return 10;
            case 'B':
            case 'b':
                return 11;
            case 'C':
            case 'c':
                return 12;
            case 'D':
            case 'd':
                return 13;
            case 'E':
            case 'e':
                return 14;
            case 'F':
            case 'f':
                return 15;
            default:
                break;
        }
        return 0;
    }

    public byte[] strToHex(String indata, int len) {
        byte[] result = new byte[len];
        byte[] buf = indata.getBytes();
        for (int i = 0; i < len; i++) {
            result[i] = (byte) ((chartoint(buf[i * 2]) << 4) + chartoint(buf[i * 2 + 1]));
        }

        return result;
    }

    public void test(){
        String strExpireDate = "";
        String strEffectDate = "";

        String strHex = "205fc55f6f82200020002000200020002000200020002000200020002000310030003100310039003600320030003500320030007f5e1c4e0177f16d3357025e5753715c3a535753715c275953903300380038003800f75320002000200020002000200020002000200020002000200020002000200020002000340033003000310030003400310039003600320030003500320030003400300031003400f16d3357025e6c51895b405c5753715c0652405c20002000200020002000320030003000380030003800310038007f951f67200020002000200020002000200020002000200020002000200020002000200020002000200020002000200020002000574c66007e00320000ff851d5151513e710dd564f377a09724cdbcbbf4b1f0595b2285c39fd4c7c54b89455ed03e1eef274e048e24aff493443e30904aa017d44db1768d0fbdddd9095d611172dad3a124025f0169df49eab811aed65251515a3e80f0c800c86bbd99ef7f05767e866417c8f12557d3d290f87eefb122e2568691cfd47dc20e3ec1b3f9c0a1e932dc45306087e84de856858f8d450fb39e662000e2f2ef97b5e9ea3988427a85891c2897eec97345dbe01019066c3c0fa2eb961568d2cc881168a8f6bb0f91145d70a1a0a24cb2b7b8284ace1f71e254d0ed60ccb92321447a8af5f1632885c6414ed850d962f65d3c4d7ee47d12b87a3d39158788e8cde1c7441cdd47ac30d96ea129342b92e3ce91665323cbd42f57fea92d765f19c6e32b3bed6772f34047686abab6a4c1edc93b7ed8a5c900c8049d2416b378e27f2f6934f1c1b9b0a809383f604d5a4e43bf1d17fc1431420c2234199905e85ec5a9cb1d2928f601331227fad9b064e3d944d8764c20fbb64c7391931dc461185766be32443e469a5a1e7fd5fd342529230aacd089456a186eceeef9ab9ac23ef7f1152990ed9068e3e4b368712a23de8f83877f496b109a622fe3c4077922400f67c8ea40b1bc5204f93ec7fe7421f04e9a134949f7d64d014fdcb526d480736fdc248460f71b01046f1a7ec7701f863d17ed3ffeac56561f37364ef2c45b677744b1cb5675499078d01420e208d06b0a58874a42b08bc9b68c8fe7e8f38ae8e0ee4ce7b8cbd9cb1fcf5212c6ca746d3c90dcd18f1e08962a4e600ed62ccdb925e1d025caa0d88e7abae1c2cf5c09fd1768e2451b3b63daac4feebcb4f21a83009dc565d8f0b81def11f25c3d2e288ee23213f6211252546ce506c3112852be0afba1076f247c479c3ea90e2648e3b74dc0011578e2809777779a3bbc4a27c5717b93017a305dc092981231778935d69adffe279ccc0620af66a8f77868a0623d1f25e93a7ec7f99334d9e13f3deeeb0b594f832445142b3e9048656ec7511af314146324442474394de6d0d23ccbdf011dcde0d637bf27f3ac96ff4d87f90ea75b4e6c4eb933eebb20966b0d54187a39e7d66bf1fe405cf3b9ebf4bfa04af6b95d7323cbd6c3b8b020d7eb2a28cbc1935d595a3eb3361304156405544fcea13181808f32ea92c1dc9a7ae223e82d275cda3c406475511660d1fc11730f141602475a3e0939269b7f4422bf86ac491d432430ca124453faf9487b3dac412ee448285500b7dcc65e6daa2d74b97da641647e8ada2b80dd0cb246ae511f2cf3e06cf462442977468d42214c719a0a892487dac95a3e80f1a06b94739f61d85633274eab51cafddf336cb0f2063df3798ebe2b2cfc57201aa9683e3ac0da0d992a5fd48f4dde998dc06f17905f0f222f6a75b9ece3960a9a5f5cae51419a";
        byte[] receive;
        receive = strToHex(strHex, strHex.length() / 2);

        byte[] EffectDate = new byte[16];
        byte[] EffectDateasc = new byte[8];
        byte[] EffectDateout = null;
        System.arraycopy(receive, 188, EffectDate, 0, EffectDate.length);
        char a0 = '0';

        if (EffectDate[0] >= a0 && EffectDate[0] <= '9') {
            for (int i = 0; i < EffectDateasc.length; i++) {
                EffectDateasc[i] = EffectDate[2 * i];
            }

            strEffectDate = new String(EffectDateasc);
            Log.d("AdapterState", "strEffectDate:" + strEffectDate);
        }

        byte[] ExpireDate = new byte[16];
        byte[] ExpireDateasc = new byte[8];
        byte[] ExpireDateout = null;
        System.arraycopy(receive, 204, ExpireDate, 0, ExpireDate.length);
        byte bHead = ExpireDate[0];
        // 数字，直接转换
        if (bHead >= '0' && bHead <= '9') {
            for (int i = 0; i < ExpireDateasc.length; i++) {
                ExpireDateasc[i] = ExpireDate[2 * i];
            }
            strExpireDate = new String(ExpireDateasc);
        }
        else
        {
            // 可能是长期转为汉字
            for (int i = 0; i < ExpireDate.length; i+=2) {
                byte bTmp = ExpireDate[i];
                ExpireDate[i] = ExpireDate[i + 1];
                ExpireDate[i + 1] = bTmp;
            }

            try {
                //strExpireDate = System.Encoding.GetEncoding("gb2312").GetString(ExpireDateasc);
                strExpireDate = new String(ExpireDate, "Unicode");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            Log.d("AdapterState", "strExpireDate:" + strExpireDate);
        }
    }

//    public boolean initRecogManager(WebDataManager wcb, RecogCallback cb) {
//
//        mWebDataManager = wcb;
//        mRecogCb = cb;
//
//        test();
//
//        // 数据适配的状态处理器
//        AdapterState adapterState = new AdapterState("");
//        mCardReaderState = new CardReaderState();
//
//        adapterState.addComponent(new HandleVisitor(wcb));
//
//        // 把界面更新接口设置到IStateContext中，这样每个状态处理器都可以
//        // 访问此接口来更新界面UI
//        mController.getStateContext().setValue(ConstantDef.RECOG_CALLBACK, cb);
//        mController.getStateContext().setValue(ConstantDef.RECOG_MODE, RecogSession.MODE_NOTIN_SERVERMODE);
//
//        // 1. 身份证读取
//        mController.add(mCardReaderState);
//        // 2. 对身份证信息预处理
//        mController.add(new HandCardInfoState(wcb));
//        // 3. 人脸比对
//        mController.add(new CompareFaceState());
//        // 4. 访客信息输入
//        mController.add(new VisitorInputState());
//        // 5. 数据适配，负责入库和提交数据到后台服务器
//        mController.add(adapterState);
//
//        // 状态执行完后需要回访通知之前已经执行过的状态
//        mController.setFeedback(true);
//
//        return true;
//    }

    /**
     * 测试模式，使用模拟的读卡状态对象替换真实的读卡状态器
     * @param num 循环的次数
     * @param isTestMode 是否处于测试模式下
     */
//    public void enterTestMode(int num, boolean isTestMode) {
//        if (isTestMode) {
//            if (null == mTestReadCard) {
////                mTestReadCard = new TestReadCardState(num);
////                mController.setStateExecutor(0, mTestReadCard);
//            }
//        } else {
//            // 退出测试模式，用真实的读卡状态器替换测试的
//            mController.setStateExecutor(0, mCardReaderState);
//            mTestReadCard = null;
//        }
//    }

    /**
     * 这个方法是根据服务器模式切换动态挂接专门处理服务器模式的状态执行器
     * @param mode 执行模式，暂时不用
     * @param cb 等待接口，证件信息读取后通知另外线程获取信息
     */
    public synchronized void enterReadCardMode(int mode, ServerRequestReadCardState.ActionCallbackWait cb) {

        Log.i(TAG, "进入ServerMode，mode：" + mode);

        pauseRecog();

        if (null != cb || mode == RecogSession.MODE_WAIT_SERVERMODE) {
            if (null == mServerState) {
                mServerState = new ServerRequestReadCardState(cb);
            } else {
                mServerState.setActionCallbackWait(cb);
            }

            if (cb != null) {
                cb.setStateContext(mController.getStateContext());
            }

            // 如果状态没有插入，则插入
            if (mController.contains(mServerState) < 0) {
                // 由于前面两个状态固定，所以这里写死插入第三个位置上
                mController.insert(2, mServerState);
            }
        }

//        mController.getStateContext().setValue(ConstantDef.RECOG_MODE, mode);
//        if (!ConfigManager.getServerMode() || mode != RecogSession.MODE_WAIT_SERVERMODE) {
//            startRecog();
//        }
    }

    public synchronized void leaveReadCardMode() {

        if (null != mServerState) {
            // 退出服务器模式，移除此状态
            mController.remove(mServerState);
            mServerState.notifyWait();
        }

//        mController.getStateContext().setValue(ConstantDef.RECOG_MODE, RecogSession.MODE_NOTIN_SERVERMODE);

        // 一般读卡模式，启动读卡
        startRecog();
    }

    public boolean isCanPauseInServerMode() {
        if (null == mController) {
            return true;
        }
        int mode = (int)mController.getStateContext().getValue(ConstantDef.RECOG_MODE);
        return (mode == RecogSession.MODE_WAIT_SERVERMODE);
    }

    public void pauseRecog() {
        if (null != mController) {
            mController.pause();
        }
    }

    public void startRecog() {
        if (null != mController) {
            mController.start();
        }
    }

    public void stopRecog() {
        if (null != mController) {
            mController.stop();
        }
    }

    public void setMainUIFlag(boolean flag) {
        if (null != mController) {
//            mController.getStateContext().setValue(ConstantDef.IS_INMAINUI, flag);
        }
    }

}
