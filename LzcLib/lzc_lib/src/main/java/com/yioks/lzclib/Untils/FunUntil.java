package com.yioks.lzclib.Untils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.List;

/**
 * Created by ${User} on 2016/9/20 0020.
 */
public class FunUntil {

    /**
     * 呼叫电话
     *
     * @param context
     * @param phone
     */
    public static void CallPhone(Context context, String phone) {
        if (phone == null || phone.equals("") || phone.equals("未知")) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        context.startActivity(intent);
    }

    /**
     * 检查某个app是否安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean CheckAppHaveInstall(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : installedPackages) {
            if (packageInfo.packageName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 隐藏键盘
     *
     * @param editText
     * @param context
     */
    public static void hideSoftInput(EditText editText, Context context) {
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    /**
     * 显示键盘
     *
     * @param editText
     * @param context
     */
    public static void showSoftInput(EditText editText, Context context) {
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }

    /**
     * 显示推送
     *
     * @param content
     */
    public static void showNotification(Context context, String title, String content, String ticker, int smallIco, int largeIco) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder builder = new android.support.v7.app.NotificationCompat.Builder(context);
        builder
                .setAutoCancel(false)
                .setTicker(ticker)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setSmallIcon(smallIco)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIco))
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());
        Notification notification = builder.build();
        notificationManager.notify(8024, notification);
    }


    public static String getProcessName(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps != null && !runningApps.isEmpty()) {
            for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
                if (procInfo.pid == pid) {
                    return procInfo.processName;
                }
            }
        }
        return null;
    }


}
