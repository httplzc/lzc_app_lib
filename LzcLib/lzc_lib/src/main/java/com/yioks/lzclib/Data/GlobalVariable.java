package com.yioks.lzclib.Data;

import android.content.Context;

import com.yioks.lzclib.Untils.SharedPreferencesUtil;

/**
 * 全局变量类
 * Created by Yioks-ZhangMengzhen on 2016/5/20.
 */
public class GlobalVariable {
    private static String HTTP = "http://www.yioks.com/app_api.mpl";//请求
    public static String CLIENT_KEY;//设备唯一标识
    public static String APP_VERSION;//APP版本号
    public static String flag = "";
    public static String PhoneUUID="";
    private static Context context;

    public static String getHTTP(Context context) {
        if (HTTP == null || HTTP.equals("")) {
            HTTP = (String) SharedPreferencesUtil.get(context, "http", "", "state");
            com.yioks.lzclib.Data.GlobalVariable.HTTP = HTTP;
        }
        return HTTP;
    }

    public static void setHTTP(String HTTP, Context context) {
        GlobalVariable.HTTP = HTTP;
        com.yioks.lzclib.Data.GlobalVariable.HTTP = HTTP;
        SharedPreferencesUtil.put(context, "http", HTTP, "state");
    }

    public static String getHTTP() {
        return HTTP;
    }

    public static void setHTTP(String HTTP) {
        GlobalVariable.HTTP = HTTP;
    }

    public static void reGetData(Context context) {
        GlobalVariable.context = context;
    }

}
