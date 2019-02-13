package pers.lizechao.android_lib.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.support.annotation.RequiresPermission;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.UUID;

import pers.lizechao.android_lib.BuildConfig;
import pers.lizechao.android_lib.storage.db.SaveData;

/**
 * 获取设备信息工具类
 */
public class DeviceUtil {
    private static SaveData<String> devInfo;

    private DeviceUtil() {

    }

    public static String getAppName(Context context) {
        try {
            PackageInfo pkg = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pkg.applicationInfo.loadLabel(context.getPackageManager()).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPackageName(Context context) {
        try {
            PackageInfo pkg = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            return pkg.applicationInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
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


    /**
     * 获取当前版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /*
     * 获取当前程序的版本名
     */
    public static String getVersionName(Context context) {

        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        return packInfo.versionName;
    }

    /**
     * 获取手机制造商、手机版本 、android系统定制商
     *
     * @return
     */
    public static String getPhoneMessage(Context context) {
        return ("产品品牌：" + Build.BRAND) +
                "\n" +
                "产品型号：" + Build.PRODUCT +
                "\n" +
                "版本：" + Build.MODEL +
                "\n" +
                "硬件制造商：" + Build.MANUFACTURER +
                "\n" +
                "cpu：" + getCpuMsg() +
                "\n" +
                "raw：" + getRawMsg() +
                "\n" +
                "rom：" + getOutRomSize() +
                "\n" +
                "显示属性：" + context.getResources().getDisplayMetrics().toString() +
                "\n" +
                "主板：" + Build.BOARD +
                "\n" +
                "   cpu指令集：" + Build.CPU_ABI +
                "\n" +
                "   cpu指令集2:" + Build.CPU_ABI2 +
                "\n" +
                "   设置参数：" + Build.DEVICE +
                "\n" +
                "   显示屏参数：" + Build.DISPLAY +
                "\n" +
                "   硬件识别码：" + Build.FINGERPRINT +
                "\n" +
                "   硬件名称：" + Build.HARDWARE +
                "\n" +
                "   HOST:" + Build.HOST +
                "\n" +
                "   修订版本列表：" + Build.ID +
                "\n" +
                "   硬件序列号：" + Build.SERIAL +
                "\n" +
                "   手机制造商：" + Build.PRODUCT +
                "\n" +
                "   描述Build的标签：" + Build.TAGS +
                "\n" +
                "   TIME:" + Build.TIME +
                "\n" +
                "   builder类型：" + Build.TYPE +
                "\n" +
                "   USER:" + Build.USER +
                "\n";
    }

    /**
     * 判断存储卡是否可用
     *
     * @return
     */
    public static boolean isSdCardExist() {

        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取设备UUID
     *
     * @param context
     * @return
     */
    public static String getDeviceUUID(Context context) {
        if (!StrUtils.CheckNull(getDevInfo(context))) {
            return getDevInfo(context);
        }
        String deviceSerial = Build.SERIAL;
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (StrUtils.CheckNull(deviceSerial) && StrUtils.CheckNull(androidId)) {
            setDevInfo(context, UUID.randomUUID().toString());
        } else
            setDevInfo(context, HashUtils.MD5(StrUtils.CheckEmpty(deviceSerial) + StrUtils.CheckEmpty(androidId)));
        return getDevInfo(context);
    }

    /**
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    @RequiresPermission(android.Manifest.permission.READ_PHONE_STATE)
    public static String getIMEI(Activity context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    /**
     * 获得状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    private static String getDevInfo(Context context) {
        if (devInfo == null) {
            return null;
        }
        return devInfo.get();
    }

    private static void setDevInfo(Context context, String devInfoStr) {
        if (!StrUtils.CheckNull(devInfoStr)) {
            if (devInfo == null) {
                devInfo = new SaveData<>("devInfo");
            }
            devInfo.set(devInfoStr);
        }
    }


    public static void parseSignature(byte[] signature) {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
            String pubKey = cert.getPublicKey().toString();
            String signNumber = cert.getSerialNumber().toString();
            Log.i(BuildConfig.LibTAG, "signName:" + cert.getSigAlgName());
            Log.i(BuildConfig.LibTAG, "pubKey:" + HashUtils.MD5(pubKey));
            Log.i(BuildConfig.LibTAG, "signNumber:" + signNumber);
            Log.i(BuildConfig.LibTAG, "subjectDN:" + cert.getSubjectDN().toString());
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }

    public static Signature getApplicationSign(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        if (packageInfo == null)
            return null;
        return packageInfo.signatures[0];
    }

    public static String getCpuMsg() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader localBufferedReader =null;
        try {
            FileReader fr = new FileReader(str1);
            localBufferedReader = new BufferedReader(fr, 8192);
            while ((str2 = localBufferedReader.readLine()) != null) {
                stringBuilder.append(str2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(localBufferedReader!=null) {
                try {
                    localBufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }


    public static long getOutRomSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return totalBlocks * blockSize;
    }

    public static String getRawMsg() {
        String str1 = "/proc/meminfo";
        String str2 = "";
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            while ((str2 = localBufferedReader.readLine()) != null) {
                stringBuilder.append(str2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    public static int getAppIco(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.applicationInfo.labelRes;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
