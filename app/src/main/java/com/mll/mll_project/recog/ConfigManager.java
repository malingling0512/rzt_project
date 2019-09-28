package com.mll.mll_project.recog;

public class ConfigManager {

    public static final int SUPER = 0;
    public static final int MAN = 1;
    public static int root = -1;
    public static boolean isEnableManPWD;
//    public static boolean ygxenable = ConfigManager.getYGXEnable();
//    public static boolean yqenable = ConfigManager.getYQEnable();

    public static boolean isEnableManPWD() {
        return isEnableManPWD;
    }

    public static void setEnableManPWD(boolean enableManPWD) {
        isEnableManPWD = enableManPWD;
    }

    /**
     * 获取识别超时时间
     *
     * @return
     */
    public static int getRecongniseTimeout() {
        return SharePreferenceUtils.getSharedPreferences1().getInt("recongnise_time_out", 8);
    }

    public static boolean getRepeatReadCardTip() {
        return SharePreferenceUtils.getSharedPreferences1().getBoolean("RepeatReadCardTip", false);
    }

}
