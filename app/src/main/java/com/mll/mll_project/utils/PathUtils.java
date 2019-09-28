package com.mll.mll_project.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;

public class PathUtils {

    private static final String TAG = "PathUtils";
    /**
     * 获得证件照的路径
     *
     * @param context
     * @return
     */
    public static String getPhotoFilePath(Context context) {
        String absolutePath = context.getExternalFilesDir("witness_photo").getAbsolutePath();
        Log.i(TAG, "absolutePath" + absolutePath);
        String photoPath = absolutePath + File.separator;
        return photoPath;
    }

    /**
     * 获得证件照的文件
     *
     * @param context
     * @param cardNo
     * @return
     */
    public static String getPhotoFile(Context context, String cardNo) {
        String photoFilePath = getPhotoFilePath(context);
        File file = new File(photoFilePath);

        if (!file.exists()) {
            file.mkdirs();
        }

        StringBuilder builder = new StringBuilder(photoFilePath);
        builder.append(cardNo + "_photo.jpg");
        //Log.i(TAG, "存储的证件头像的路径：" + builder.toString());

        return builder.toString();
    }
}
