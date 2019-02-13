package pers.lizechao.android_lib.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.annimon.stream.Objects;

import java.io.File;
import java.util.List;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

/**
 * Created by Lzc on 2016/9/20 0020.
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


    public static boolean isIntentAvailable(Context context, Intent intent) {
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        return componentName != null;
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
     * 显示简单通知
     *
     * @param content
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void showNotification26(Context context, String title, String content, String ticker, int smallIco, int largeIco) {
        // 传入参数：通道ID，通道名字，通道优先级（类似曾经的 builder.setPriority()）
        NotificationChannel channel =
                new NotificationChannel("showNotification26", "通知", NotificationManager.IMPORTANCE_HIGH);

        // 配置通知渠道的属性
        channel.setDescription("通知");
        // 设置通知出现时声音，默认通知是有声音的
        // 设置通知出现时的闪灯（如果 android 设备支持的话）
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        // 设置通知出现时的震动（如果 android 设备支持的话）
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //最后在 notificationManager 中创建该通知渠道
        notificationManager.createNotificationChannel(channel);
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "showNotification26");
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

    public static void showNotification(Context context, String title, String content, String ticker, int smallIco, int largeIco) {
        if (Build.VERSION.SDK_INT > 26) {
            showNotification26(context, title, content, ticker, smallIco, largeIco);
        } else {
            showNotificationBlow(context, title, content, ticker, smallIco, largeIco);
        }
    }

    /**
     * 显示简单通知
     *
     * @param content
     */
    public static void showNotificationBlow(Context context, String title, String content, String ticker, int smallIco, int largeIco) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
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


    /**
     * 根据进程id获取进程名称
     *
     * @param context
     * @param pid
     * @return
     */
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

    /**
     * 根据进程名获取进程id
     *
     * @param context
     * @param name
     * @return -1为没有找到该进程
     */
    public static int getProcessIdByProcessName(Context context, String name) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps != null && !runningApps.isEmpty()) {
            for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
                if (procInfo.processName.equals(name)) {
                    return procInfo.pid;
                }
            }
        }
        return -1;
    }


    /**
     * 打开apk
     *
     * @param context
     * @param uri
     */
    public static void openApk(Context context, Uri uri) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(uri,
                "application/vnd.android.package-archive");
        intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);

    }

    /**
     * 打开apk
     *
     * @param context
     * @param file
     */
    public static void openApk(Context context, File file) {
        openApk(context, Uri.fromFile(file));

    }

    /**
     * 判断服务是否在运行
     *
     * @param mContext
     * @param serviceName
     * @return
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (myAM == null)
            return false;
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    /**
     * 判断app是否在前台
     *
     * @param context
     * @return
     */
    public static boolean appIsForeground(Context context) {
        ActivityManager mActivityManager;
        mActivityManager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        if (mActivityManager == null)
            return false;
        ComponentName topActivity = mActivityManager.
                getRunningTasks(1).get(0).topActivity;
        if (topActivity == null)
            return false;
        String packageName = topActivity.getPackageName();
        return Objects.equals(packageName, context.getPackageName());
    }

    //判断进程是否在运行
    public static boolean isProcessesWork(Context mContext, String packageName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (myAM == null)
            return false;
        List<ActivityManager.RunningAppProcessInfo> myList = myAM.getRunningAppProcesses();
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).processName;
            if (mName.equals(packageName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }


    public static MemoryMessage getMemoryMsg(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int memClass = activityManager.getMemoryClass();//64，以m为单位
        int largeClass = activityManager.getLargeMemoryClass();//64，以m为单位
        long freeMemory = Runtime.getRuntime().freeMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();
        return new MemoryMessage(memClass, freeMemory, totalMemory, maxMemory, largeClass);
    }

    //
    public static class MemoryMessage {
        public int memClass;
        public long freeMemory;
        public long totalMemory;
        public long maxMemory;
        public int largeClass;

        public MemoryMessage() {
        }

        public MemoryMessage(int memClass, long freeMemory, long totalMemory, long maxMemory, int largeClass) {
            this.memClass = memClass;
            this.freeMemory = freeMemory;
            this.totalMemory = totalMemory;
            this.maxMemory = maxMemory;
            this.largeClass = largeClass;
        }

        @Override
        public String toString() {
            return "最大activity内存:" + memClass + "m   " + "大内存activity:" + largeClass + "m" + "   空闲内存" + freeMemory / (1024 * 1024) + "m" + "   使用内存：" + totalMemory / (1024 * 1024) + "m"
                    + "   最大可用内存:" + maxMemory / (1024 * 1024) + "m";
        }
    }


    //计算文字中心坐标
    public static float calcCenterTextY(Paint paint, int groupHeight) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (groupHeight - fontMetrics.bottom + fontMetrics.top) / 2f - fontMetrics.top;
    }

    public static void copyStringToClip(Context context, String text) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。ClipboardManager
        ClipData clipData = ClipData.newPlainText("", text);
        cm.setPrimaryClip(clipData);
    }


    public static void restartApp(Context context) {
        if (checkFront(context)) {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            if (intent == null)
                return;
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        Process.killProcess(Process.myPid());
    }


    public static boolean checkFront(Context context) {
        ActivityManager mActivityManager;
        mActivityManager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        if (mActivityManager == null)
            return false;
        ComponentName topActivity = mActivityManager.
                getRunningTasks(1).get(0).topActivity;
        if (topActivity == null)
            return false;
        String packageName = topActivity.getPackageName();
        return Objects.equals(packageName, context.getPackageName());
    }


}
