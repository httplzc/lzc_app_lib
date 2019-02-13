package pers.lizechao.android_lib.support.download.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import java.util.HashMap;

import pers.lizechao.android_lib.R;

/**
 * Created by Lzc on 2017/7/13 0013.
 */

public class DownLoadNotificationManager {
    private final NotificationManager notificationManager;
    private final HashMap<String, NotificationData> notificationDataHashMap = new HashMap<>();
    private int count = 9000;
    private final Context context;
    private static final int maxFileNameLength=20;

    public DownLoadNotificationManager(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    //初始化通知栏
    public void createNotification(Context context, Intent cancelIntent, String uuid, String fileName) {
        if (fileName.length() > maxFileNameLength) {
            fileName = fileName.substring(0, fileName.length() > maxFileNameLength ? maxFileNameLength : fileName.length());
        }
        NotificationData notificationData = new NotificationData(uuid);
        notificationDataHashMap.put(uuid, notificationData);
        notificationData.remoteViews = new RemoteViews(context.getPackageName(), R.layout.download_layout);
        notificationData.remoteViews.setTextViewText(R.id.filename, fileName + " 下载进度：");
        notificationData.remoteViews.setProgressBar(R.id.download_progress, 100, 0, false);
        NotificationCompat notificationCompat;
        Intent intent = new Intent();
        PendingIntent pendingIntent = null;
        pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(context, 0, cancelIntent, 0);
        notificationData.builder = new Notification.Builder(context);
        notificationData.builder
                .setContent(notificationData.remoteViews)
                .setTicker("正在下载中")
                .setContentIntent(pendingIntent)
                .setDeleteIntent(pendingIntentCancel)
                .setSmallIcon(R.drawable.ic_file_download)
                .setAutoCancel(false)
                .setOngoing(true)
                .setWhen(System.currentTimeMillis());
        Notification notification = notificationData.builder.build();
        notificationManager.notify(notificationData.notificationId, notification);
        //    context.startForeground(notificationData.notificationId, notification);
    }


    public void updateNotificationBySucceed(String uuid) {
        NotificationData notificationData = notificationDataHashMap.get(uuid);
        if (notificationData == null)
            return;
        notificationData.remoteViews = new RemoteViews(context.getPackageName(), R.layout.download_layout);
        notificationData.remoteViews.setTextViewText(R.id.filename, "下载成功");
        notificationData.builder.setOngoing(false).setAutoCancel(true).setContent(notificationData.remoteViews);
        notificationManager.notify(notificationData.notificationId, notificationData.builder.build());
    }


    public void updateNotificationByFailure(String uuid) {
        NotificationData notificationData = notificationDataHashMap.get(uuid);
        if (notificationData == null)
            return;
        notificationData.remoteViews = new RemoteViews(context.getPackageName(), R.layout.download_layout);
        notificationData.remoteViews.setTextViewText(R.id.filename, "下载失败~~~");
        notificationData.builder.setOngoing(false).setAutoCancel(true).setContent(notificationData.remoteViews);
        notificationManager.notify(notificationData.notificationId, notificationData.builder.build());
    }

    public void updateNotificationByProgress(String uuid, int progress, String filename) {
        if (filename.length() > maxFileNameLength) {
            filename = filename.substring(0, maxFileNameLength);
        }
        NotificationData notificationData = notificationDataHashMap.get(uuid);
        if (notificationData == null)
            return;
        notificationData.remoteViews = new RemoteViews(context.getPackageName(), R.layout.download_layout);
        notificationData.remoteViews.setProgressBar(R.id.download_progress, 100, progress, false);
        notificationData.remoteViews.setTextViewText(R.id.filename, filename + "    " + progress + "%");
        notificationData.builder.setContent(notificationData.remoteViews);
        notificationManager.notify(notificationData.notificationId, notificationData.builder.build());

    }

    private class NotificationData {
        Notification.Builder builder;
        RemoteViews remoteViews;
        final String uuid;
        final int notificationId;

        public NotificationData(String uuid) {
            notificationId = ++count;
            this.uuid = uuid;
        }
    }
}
