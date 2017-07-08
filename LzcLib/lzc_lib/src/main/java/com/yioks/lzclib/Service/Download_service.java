package com.yioks.lzclib.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.yioks.lzclib.Helper.FileDownloadCallBack;
import com.yioks.lzclib.R;
import com.yioks.lzclib.Untils.DialogUtil;
import com.yioks.lzclib.Untils.FunUntil;
import com.yioks.lzclib.Untils.HttpUtil;
import com.yioks.lzclib.Untils.StringManagerUtil;
import com.yioks.lzclib.View.MyDialog;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


//下载文件服务
public class Download_service extends Service {

    //通知管理器
    public NotificationManager notificationManager;

    private BroadcastReceiver broadcastReceiver;
    private WifiStateListener wifiStateListener;
    private static final String cancelAction = "com.yioks.lzc_lib_down_service_cancel";
    public static final String finishAction = "com.yioks.lzc_lib_down_service_finish";
    public static final String failureAction = "com.yioks.lzc_lib_down_service_failure";
    private List<DownLoad> downLoadList = new ArrayList<>();
    private Handler handler;
    private boolean onlyWifi = true;
    private boolean openApk = false;
    private FinishDownLoadDo finishDownLoadDo;

    private class DownLoad {
        private RemoteViews remoteViews;
        private android.support.v7.app.NotificationCompat.Builder builder;
        private boolean showNotification;
        private int notific_id = 8024;
        private Notification notification;
        private String RealName;
        // 0进行中 1完成 2错误
        private int state = 0;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                stop();
            }
        };
        wifiStateListener = new WifiStateListener();
        IntentFilter wifiStateFilter = new IntentFilter();
        wifiStateFilter.addAction("android.NET.conn.CONNECTIVITY_CHANGE");
        wifiStateFilter.addAction("android.Net.wifi.WIFI_STATE_CHANGED");
        wifiStateFilter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(wifiStateListener, wifiStateFilter);
        handler = new Handler(getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                boolean downLoadFinish = true;
                for (DownLoad downLoad : downLoadList) {
                    if (downLoad.state == 0) {
                        downLoadFinish = false;
                        break;
                    }
                }
                if (downLoadFinish) {
                    notificationManager.cancelAll();
                    stop();
                }
                return true;
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(cancelAction);
        this.registerReceiver(broadcastReceiver, intentFilter);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public static void startDownLoad(final Context context, final String path, final String downLoadTo, final boolean showNotification) {
        if (StringManagerUtil.CheckNull(path) || StringManagerUtil.CheckNull(downLoadTo)) {
            DialogUtil.ShowToast(context, "参数错误！");
            return;
        }
        if (FunUntil.isNetwork3G(context)) {
            if (!showNotification)
                return;
            MyDialog myDialog = new MyDialog.Builder(context).message("你现在处于3G/4G模式下，你确定要进行下载吗？").canBackCancel(true).canTouchCancel(true).build();
            myDialog.setOk_button_click_listener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendDownMessage(context, path, downLoadTo, showNotification);
                }
            });
            myDialog.showDialog();
        } else {
            sendDownMessage(context, path, downLoadTo, showNotification);
        }


    }


    public static void startDownLoad(final Context context, final String path, @Nullable final String downLoadTo, final boolean showNotification,
                                     boolean onlyWifi, @Nullable FinishDownLoadDo finishDownLoadDo) {
        if (FunUntil.isNetwork3G(context))
            return;
        sendDownMessage(context, path, downLoadTo, showNotification, onlyWifi, finishDownLoadDo);

    }

    private static void sendDownMessage(final Context context, final String path, final String downLoadTo, final boolean showNotification,
                                        boolean isOnlyWifi) {
        Intent intent = new Intent();
        intent.setClass(context, Download_service.class);
        intent.putExtra("path", path);
        intent.putExtra("downLoadTo", downLoadTo);
        intent.putExtra("showNotification", showNotification);
        intent.putExtra("isOnlyWifi", isOnlyWifi);
        context.startService(intent);
    }

    private static void sendDownMessage(final Context context, final String path, final String downLoadTo, final boolean showNotification,
                                        boolean isOnlyWifi, FinishDownLoadDo finishDownLoadDo) {
        Intent intent = new Intent();
        intent.setClass(context, Download_service.class);
        intent.putExtra("path", path);
        intent.putExtra("downLoadTo", downLoadTo);
        intent.putExtra("showNotification", showNotification);
        intent.putExtra("isOnlyWifi", isOnlyWifi);
        intent.putExtra("finishDownLoadDo", (Serializable) finishDownLoadDo);
        context.startService(intent);
    }

    private static void sendDownMessage(final Context context, final String path, final String downLoadTo, final boolean showNotification) {
        Intent intent = new Intent();
        intent.setClass(context, Download_service.class);
        intent.putExtra("path", path);
        intent.putExtra("downLoadTo", downLoadTo);
        intent.putExtra("showNotification", showNotification);
        context.startService(intent);
    }

    public static void startDownLoad(final Context context, final String path, final boolean showNotification) {
        if (StringManagerUtil.CheckNull(path))
            return;
        if (FunUntil.isNetwork3G(context)) {
            if (!showNotification)
                return;
            MyDialog myDialog = new MyDialog.Builder(context).message("你现在处于3G/4G模式下，你确定要进行下载吗？").canBackCancel(true).canTouchCancel(true).build();
            myDialog.setOk_button_click_listener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendDownMessage(context, path, null, showNotification);
                }
            });
            myDialog.showDialog();
        } else {
            sendDownMessage(context, path, null, showNotification);
        }
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        final DownLoad downLoad = new DownLoad();
        downLoadList.add(downLoad);
        downLoad.notific_id += downLoadList.size();
        String path = intent.getStringExtra("path");
        String downLoadTo = intent.getStringExtra("downLoadTo");
        finishDownLoadDo = (FinishDownLoadDo) intent.getSerializableExtra("finishDownLoadDo");
        onlyWifi = intent.getBooleanExtra("isOnlyWifi", true);
        downLoad.showNotification = intent.getBooleanExtra("showNotification", true);
        if (downLoad.showNotification) {
            initNotification(this, downLoad);
        }
        try {
            downLoad.RealName = path.substring(path.lastIndexOf("/") + 1);
        } catch (Exception e) {
            e.printStackTrace();
            downLoad.RealName = "未知";
        }
        final File result = createFile(downLoadTo, path);
        if (result == null) {
            stop();
            return flags;
        }

        HttpUtil.download(path, new FileDownloadCallBack(result, this) {

            private int lastProgress;

            @Override
            public void onFailure(int i, File file) {
                Intent intent1 = new Intent();
                intent1.setAction(failureAction);
                sendBroadcast(intent);
                if (!downLoad.showNotification) {
                    stop();
                    return;
                }

                downLoad.remoteViews = new RemoteViews(getPackageName(), R.layout.download_layout);
                downLoad.remoteViews.setTextViewText(R.id.filename, "下载失败~~~");
                downLoad.builder.setOngoing(false).setAutoCancel(true).setContent(downLoad.remoteViews);
                notificationManager.notify(downLoad.notific_id, downLoad.builder.build());
                downLoad.state = 2;
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onSuccess(File file) {
                if (!downLoad.showNotification) {
                    FinishDownLoad(file);
                    stop();
                    return;
                }
                downLoad.remoteViews = new RemoteViews(getPackageName(), R.layout.download_layout);
                downLoad.remoteViews.setTextViewText(R.id.filename, "下载成功");
                downLoad.builder.setOngoing(false).setAutoCancel(true).setContent(downLoad.remoteViews);
                notificationManager.notify(downLoad.notific_id, downLoad.builder.build());
                FinishDownLoad(file);
                downLoad.state = 1;
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onProgress(int progress) {
                Log.i("lzc", "下载进度" + progress);
                if (!downLoad.showNotification)
                    return;
                if (progress - lastProgress >= new Random().nextInt(2) + 1) {
                    downLoad.remoteViews = new RemoteViews(getPackageName(), R.layout.download_layout);
                    downLoad.remoteViews.setProgressBar(R.id.download_progess, (int) 100, (int) progress, false);
                    downLoad.remoteViews.setTextViewText(R.id.filename, downLoad.RealName + "    " + progress + "%");
                    downLoad.builder.setContent(downLoad.remoteViews);
                    notificationManager.notify(downLoad.notific_id, downLoad.builder.build());
                    lastProgress = progress;
                }

            }
        });
        return Service.START_REDELIVER_INTENT;
    }

    private File createFile(String downLoadTo, String path) {
        File file = null;
        if (downLoadTo == null) {
            File dir = getExternalFilesDir(null);
            if (dir == null) {
                return null;
            }
            File dirDown = new File(dir.getPath() + "/downLoad");
            if (!dirDown.exists()) {
                dirDown.mkdirs();
            }

            String s[] = path.split("\\.");
            file = new File(dirDown + "/" + UUID.randomUUID() + ((s.length != 0) ? ("." + s[s.length - 1]) : ""));
            Log.i("lzc", "file_getname" + file.getName() + "---" + s.length + "path" + path);
            if (file.exists())
                file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            file = new File(downLoadTo);
            if (file.exists()) {
                file.delete();
            } else {
                File parent = new File(file.getParent());
                if (!parent.exists())
                    parent.mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
        return file;
    }


    //避免被销毁
    @Override
    public void onDestroy() {
        stopForeground(true);
        if (broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
        if (wifiStateListener != null)
            unregisterReceiver(wifiStateListener);
        super.onDestroy();
    }


    //运行安装程序
    protected void FinishDownLoad(File file) {
//        if (file.getName().endsWith(".apk")) {
//            Intent intent = new Intent();
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setAction(Intent.ACTION_VIEW);
//            intent.setDataAndType(Uri.fromFile(file),
//                    "application/vnd.android.package-archive");
//            startActivity(intent);
//        }
        Intent intent = new Intent();
        intent.setAction(finishAction);
        sendBroadcast(intent);
        if (finishDownLoadDo != null)
            finishDownLoadDo.finishDownLoad(this, file);
    }


    //初始化通知栏
    public void initNotification(Context context, DownLoad downLoad) {
        downLoad.remoteViews = new RemoteViews(getPackageName(), R.layout.download_layout);
        downLoad.remoteViews.setTextViewText(R.id.filename, "下载进度：");
        downLoad.remoteViews.setProgressBar(R.id.download_progess, 100, 0, false);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent();
        PendingIntent pendingIntent = null;
        pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Intent cancelIntent = new Intent(cancelAction);
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(context, 0, cancelIntent, 0);
        downLoad.builder = new android.support.v7.app.NotificationCompat.Builder(context);
        downLoad.builder
                .setContent(downLoad.remoteViews)
                .setTicker("正在下载中")
                .setContentIntent(pendingIntent)
                .setDeleteIntent(pendingIntentCancel)
                .setSmallIcon(R.drawable.down_load)
                .setAutoCancel(false)
                .setOngoing(true)
                .setWhen(System.currentTimeMillis());
        downLoad.notification = downLoad.builder.build();
        notificationManager.notify(downLoad.notific_id, downLoad.notification);
        startForeground(downLoad.notific_id, downLoad.notification);
    }


    public class WifiStateListener extends BroadcastReceiver {

        public WifiStateListener() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (FunUntil.isNetwork3G(context) && Download_service.this.onlyWifi) {
                stop();
            }

        }
    }

    private void stop() {
        Download_service.this.stopSelf();
        Process.killProcess(Process.myPid());
    }

    public FinishDownLoadDo getFinishDownLoadDo() {
        return finishDownLoadDo;
    }

    public void setFinishDownLoadDo(FinishDownLoadDo finishDownLoadDo) {
        this.finishDownLoadDo = finishDownLoadDo;
    }

    public interface FinishDownLoadDo {
        void finishDownLoad(Context context, File file);
    }

}
