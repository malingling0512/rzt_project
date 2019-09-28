package com.mll.mll_project;

import com.wave.rztilib.ICardInfo;

public class StringUtil {

    public static String maskHandle(ICardInfo cardInfo){
        String code = cardInfo.getCardId().trim();
        StringBuilder builder = new StringBuilder();
        String substring = code.substring(0, 6);
        String substring2 = code.substring(14);
        builder.append(substring);
        builder.append("****");
        builder.append(substring2);
        return builder.toString();
    }

    public static String maskHandleString(String code){
        StringBuilder builder = new StringBuilder();
        String substring = code.substring(0, 6);
        String substring2 = code.substring(14);
        builder.append(substring);
        builder.append("****");
        builder.append(substring2);
        return builder.toString();
    }

    public static String appendStr(String number, String source, String pwd){
        StringBuilder builder = new StringBuilder();
        builder.append(number);
        builder.append(source);
        builder.append(pwd);
        return builder.toString();
    }
    public static String appendString(String...values){
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<values.length; i++){
            builder.append(values[i]);
        }

        return builder.toString();
    }

}
