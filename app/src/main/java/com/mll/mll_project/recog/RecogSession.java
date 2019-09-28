package com.mll.mll_project.recog;

public class RecogSession {

    public static final int MODE_NOTIN_SERVERMODE = -1;
    public static final int MODE_WAIT_SERVERMODE = 0;
    public static final int MODE_READ_IDCARD = 1;
    public static final int MODE_FACE_COMPARE = 2;
    public static final int MODE_READ_DISCARD = 10;

    public static final int IS_RecongniseInvalid =  0;
    public static final int IS_Recongnising =  1;
    public static final int IS_RecongniseFail =  2;
    public static final int IS_RecongniseSuccess =  3;
    public static final int IS_ReadCardSuccess =  4;
    public static final int IS_CheckNotInMainUI =  5;
    public static final int IS_CloseUIShowing =  6;
    public static final int IS_IdInfoExpired =  7;
    public static final int IS_RepeatReadCard =  8;
    public static final int IS_NetConntected =  9;
    public static final int IS_InputVisitor_Mode = 10;
    public static final int IS_InputVisitor_Cancel = 11;
    public static final int IS_InputVisitor_Done = 12;

    // 上传标志定义
    public static final int Upload_Waiting =  0;
    public static final int Upload_Sucessfully =  1;
    public static final int Upload_Failed =  -100;
    public static final int Upload_ServerMode =  10;
    public static final int Upload_LocalMode =  20;
    public static final int Upload_RecogFailed =  11;

    public static final int Recog_Sucessfully =  1;
    public static final int Recog_Failed =  2;

}
