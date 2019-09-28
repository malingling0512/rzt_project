package com.mll.mll_project;

public class ConstantDef {

    // 识别时间
    public static final String RECOG_DATE = "recog_date";
    // 识别模式，主要服务模式下使用
    public static final String RECOG_MODE = "recog_mode";
    // 身份证头像
    public static final String HEAD_BITMAP = "head_bitmap";
    // 识别现场照片
    public static final String RECOG_BITMAP = "recog_bitmap";
    public static final String VISITOR_OBJECT = "visitor_obj";
    public static final String RECOG_CALLBACK = "recog_callback";
    // 是否在主页面标志
    public static final String IS_INMAINUI = "is_inmainui";
    // 识别结果（1：成功；其它失败）
    public static final String RECOG_RESULT = "recog_result";

    // 刷卡时证件过期等待时间（毫秒）
    public static final int VALIDDATE_WAIT_TIME = 1000;
    // 同一个人连续刷卡时等待时间（毫秒）
    public static final int SAMEPERSON_WAIT_TIME = 1000;
    // 刷卡时不在主页面时等待时间（毫秒）
    public static final int NOTINMAINUI_WAIT_TIME = 1000;

    // 同一个人刷卡成功时再次刷卡的间隔时间（毫秒）
    public final static int READ_INTERNAL = 1000 * 10;

}
