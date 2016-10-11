package com.yioks.lzclib.Untils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.yioks.lzclib.Data.GlobalVariable;

import java.util.UUID;

/**
 * 获取设备信息工具类
 * Created by Yioks-ZhangMengzhen on 2016/4/13.
 */
public class DeviceUtil {
    private DeviceUtil() {

    }

    /**
     * 获取屏幕密度
     *
     * @param active
     * @return
     */
    public static float getDensity(Activity active) {
        DisplayMetrics dm = active.getResources().getDisplayMetrics();
        return dm.density;
    }

    /**
     * 屏幕像素密度
     *
     * @param active
     * @return
     */
    public static int getDensityDPI(Activity active) {
        DisplayMetrics dm = active.getResources().getDisplayMetrics();
        return dm.densityDpi;
    }

    /**
     * 屏幕的宽
     *
     * @param active
     * @return
     */
    public static int getScreenWidth(Activity active) {
        DisplayMetrics dm = active.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 屏幕的高
     *
     * @param active
     * @return
     */
    public static int getScreenHeight(Activity active) {
        DisplayMetrics dm = active.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /*
     * 获取当前程序的版本号
	 */
    public static String getVersionName(Context context){
        // 获取packagemanager的实例
        if( GlobalVariable.APP_VERSION==null)
        {
            PackageManager packageManager = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = null;
            try {
                packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return "";
            }
            GlobalVariable.APP_VERSION=packInfo.versionName;
        }
        return  GlobalVariable.APP_VERSION;
    }

    /**
     * 获取手机制造商、手机版本 、android系统定制商
     * @return
     */
    public static String getPhoneMessage()
    {
        return Build.PRODUCT+"-----"+Build.MODEL+"-----"+Build.BRAND;
    }

    /**
     * 判断存储卡是否可用
     *
     * @return
     */
    public static boolean isSdCardExist() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// 判断是否已经挂载
            return true;
        }
        return false;
    }

    /**
     * 获取设备UUID
     *
     * @param context
     * @return
     */
    public static String getDeviceUUID(Context context) {
        if(GlobalVariable.PhoneUUID!=null)
        {

            return  StringManagerUtil.md5(GlobalVariable.PhoneUUID);
        }
        final String tmDevice, tmSerial, androidId;
        TelephonyManager mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        tmDevice = "" + mTm.getDeviceId();
        tmSerial = "" + mTm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        GlobalVariable.PhoneUUID=deviceUuid.toString();
        return   StringManagerUtil.md5(GlobalVariable.PhoneUUID);
    }

    /**
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        String imei;
        imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        return imei;
    }

}
