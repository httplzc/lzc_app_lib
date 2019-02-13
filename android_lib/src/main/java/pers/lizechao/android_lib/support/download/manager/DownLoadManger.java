package pers.lizechao.android_lib.support.download.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import pers.lizechao.android_lib.data.ApplicationData;
import pers.lizechao.android_lib.storage.file.Path;
import pers.lizechao.android_lib.support.download.comm.DownLoadMsg;
import pers.lizechao.android_lib.support.download.comm.IDownloadAction;
import pers.lizechao.android_lib.support.download.comm.IDownloadCallback;
import pers.lizechao.android_lib.support.download.comm.TaskMsg;
import pers.lizechao.android_lib.support.download.service.DownloadService;
import pers.lizechao.android_lib.support.protocol.messenger.MessengerReceiver;
import pers.lizechao.android_lib.support.protocol.messenger.MessengerSender;

/**
 * Created by Lzc on 2017/7/10 0010.
 * 下载管理器
 */

class DownLoadManger {
    private static DownLoadManger downLoadManger;
    private final HashMap<String, DownloadMsgCallBackList> msgCallBackHashMap = new HashMap<>();
    private final HashMap<String, DownloadTask> downloadTasks = new HashMap<>();
    //由于服务器还没有连接等待中的队列
    private final Queue<DownloadTask> prepareDownloadTask = new LinkedList<>();
    private final DownLoadServiceConnection downLoadServiceConnection;
    //当前与服务器的连接状态
    private ConnectState connectState = ConnectState.None;
    private final DownLoadNotificationManager downLoadNotificationManager;
    private IDownloadAction downloadAction;

    //当前与service的连接状态
    private enum ConnectState {
        None, Connecting, Connected
    }


    private DownLoadManger() {
        downLoadServiceConnection = new DownLoadServiceConnection();
        downLoadNotificationManager = new DownLoadNotificationManager(ApplicationData.applicationContext);
    }

    private static class DownloadMsgCallBackList extends ArrayList<DownloadMsgCallBack> implements DownloadMsgCallBack {

        @Override
        public void onStart(TaskMsg taskMsg) {
            for (DownloadMsgCallBack downloadMsgCallBack : this) {
                downloadMsgCallBack.onStart(taskMsg);
            }
        }

        @Override
        public void onStop(Throwable throwable) {
            for (DownloadMsgCallBack downloadMsgCallBack : this) {
                downloadMsgCallBack.onStop(throwable);
            }
        }

        @Override
        public void onFinish(File file) {
            for (DownloadMsgCallBack downloadMsgCallBack : this) {
                downloadMsgCallBack.onFinish(file);
            }
        }

        @Override
        public void onProgress(int progress) {
            for (DownloadMsgCallBack downloadMsgCallBack : this) {
                downloadMsgCallBack.onProgress(progress);
            }
        }
    }


    public static DownLoadManger getInstance() {
        if (downLoadManger == null) {
            synchronized (DownLoadManger.class) {
                if (downLoadManger == null)
                    downLoadManger = new DownLoadManger();
            }
        }
        return downLoadManger;
    }


    private class DownloadCallBack implements IDownloadCallback {
        @Override
        public void callBackStart(TaskMsg taskMsg) {
            DownloadTask downloadTask = getDataByDBOrMemory(taskMsg.uuid);
            if (downloadTask == null) {
                return;
            }
            downloadTask.setStart();
            String path = downloadTask.getDownLoadMsg().targetFilePath;
            if (downloadTask.getDownLoadMsg().downLoadConfig.showByNotification)
                downLoadNotificationManager.createNotification(ApplicationData.applicationContext, new Intent(), taskMsg.uuid, Path.parse(path).getFileName());
            DownLoadServiceDBHelper.saveDataToDB(ApplicationData.applicationContext, downloadTask.getDownLoadMsg());
            DownloadMsgCallBack downloadMsgCallBack = msgCallBackHashMap.get(taskMsg.uuid);
            if (downloadMsgCallBack != null) {
                downloadMsgCallBack.onStart(taskMsg);
                downloadMsgCallBack.onProgress(downloadTask.getDownLoadMsg().progress);
            }
        }

        @Override
        public void callBackStop(String uuid, Throwable throwable) {
            DownloadTask downloadTask = getDataByDBOrMemory(uuid);
            DownloadMsgCallBack downloadMsgCallBack = msgCallBackHashMap.get(uuid);
            if (downloadTask == null) {
                return;
            }
            downloadTask.setStop();
            if (downloadTask.getDownLoadMsg().downLoadConfig.showByNotification)
                downLoadNotificationManager.updateNotificationByFailure(uuid);
            DownLoadServiceDBHelper.saveDataToDB(ApplicationData.applicationContext, downloadTask.getDownLoadMsg());
            if (downloadMsgCallBack != null)
                downloadMsgCallBack.onStop(throwable);
            msgCallBackHashMap.remove(uuid);
        }


        @Override
        public void callBackEnd(String uuid, String filePath) {
            DownloadTask downloadTask = getDataByDBOrMemory(uuid);
            DownloadMsgCallBack downloadMsgCallBack = msgCallBackHashMap.get(uuid);
            if (downloadTask == null) {
                return;
            }
            downloadTask.setFinish();
            if (downloadTask.getDownLoadMsg().downLoadConfig.showByNotification)
                downLoadNotificationManager.updateNotificationBySucceed(uuid);
            DownLoadServiceDBHelper.saveDataToDB(ApplicationData.applicationContext, downloadTask.getDownLoadMsg());
            if (downloadMsgCallBack != null)
                downloadMsgCallBack.onFinish(new File(filePath));
            msgCallBackHashMap.remove(uuid);
        }


        @Override
        public void callBackProgress(String uuid, int progress) {
            DownloadTask downloadTask = getDataByDBOrMemory(uuid);
            DownloadMsgCallBack downloadMsgCallBack = msgCallBackHashMap.get(uuid);
            if (downloadTask == null) {
                return;
            }
            downloadTask.getDownLoadMsg().progress = progress;
            String path = downloadTask.getDownLoadMsg().targetFilePath;
            if (downloadTask.getDownLoadMsg().downLoadConfig.showByNotification)
                downLoadNotificationManager.updateNotificationByProgress(uuid, progress, Path.parse(path).getFileName());
            // DownLoadServiceDBHelper.getInstance(ApplicationData.applicationContext).updateData(downLoadMsg);
            if (downloadMsgCallBack != null)
                downloadMsgCallBack.onProgress(progress);
        }


        @Override
        public void onFinishAllTask() {
            callDestroy();
            msgCallBackHashMap.clear();
        }
    }

    //关闭下载服务
    private void callDestroy() {
        ApplicationData.applicationContext.stopService(new Intent(ApplicationData.applicationContext, DownloadService.class));
        try {
            if (downLoadServiceConnection != null)
                ApplicationData.applicationContext.unbindService(downLoadServiceConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        connectState = ConnectState.None;
        downloadAction = null;
    }


    private void connectService() {
        if (connectState == ConnectState.None) {
            connectState = ConnectState.Connecting;
            Intent intent = new Intent(ApplicationData.applicationContext, DownloadService.class);
            Bundle bundle = new Bundle();
            bundle.putBinder("iBinder", new MessengerReceiver<>(IDownloadCallback.class, new DownloadCallBack()).getSendBinder());
            intent.putExtra("data", bundle);
            ApplicationData.applicationContext.bindService(intent, downLoadServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }


    private class DownLoadServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadAction = new MessengerSender<>(IDownloadAction.class, service).asInterface();
            connectState = ConnectState.Connected;
            for (DownloadTask downloadTask : prepareDownloadTask) {
                startDownload(downloadTask);
            }
            prepareDownloadTask.clear();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            downloadAction = null;
            connectState = ConnectState.None;
        }
    }


    public void startDownload(DownloadTask downloadTask) {
        if (connectState != ConnectState.Connected) {
            prepareDownloadTask.add(downloadTask);
            if (connectState != ConnectState.Connecting)
                connectService();
        } else
            realStart(downloadTask);
    }


    //发送下载消息
    private void realStart(DownloadTask downloadTask) {
        if (downloadTask.isDownLoading())
            return;
        downloadTasks.put(downloadTask.getDownLoadMsg().uuid, downloadTask);
        File file = new File(downloadTask.getDownLoadMsg().targetFilePath);
        if (!file.exists() || !file.isDirectory()) {
            File parentDir = new File(file.getParent());
            parentDir.mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        downloadAction.startDownload(downloadTask.getDownLoadMsg().uuid, downloadTask.getDownLoadMsg());
    }


    /**
     * 暂停下载
     *
     * @param uuid
     */
    void pauseDownload(String uuid) {
        if (downloadAction != null)
            downloadAction.pauseDownload(uuid);
    }

    /**
     * 删除任务
     *
     * @param uuid
     */
    void deleteDownLoad(String uuid) {
        if (downloadAction != null) {
            downloadAction.deleteDownLoad(uuid);
        }
        DownloadTask downloadTask = getDataByDBOrMemory(uuid);
        if (downloadTask == null) {
            return;
        }
        msgCallBackHashMap.remove(uuid);
        if (downloadTask.getDownLoadMsg().downLoadConfig.showByNotification)
            downLoadNotificationManager.updateNotificationByFailure(uuid);
        DownLoadServiceDBHelper.deleteData(ApplicationData.applicationContext, uuid);
    }


    @Nullable
    DownloadTask getDataByDBOrMemory(String uuid) {
        if (uuid == null)
            return null;
        DownloadTask downloadTask = downloadTasks.get(uuid);
        if (downloadTask == null) {
            DownLoadMsg downLoadMsg = DownLoadServiceDBHelper.getDataFromDB(ApplicationData.applicationContext, uuid);
            if (downLoadMsg != null) {
                //若是异常关闭，更正对状态为暂停
                if (downLoadMsg.currentState == DownLoadMsg.DownLoadState.Downloading)
                    downLoadMsg.currentState = DownLoadMsg.DownLoadState.Stop;
                downloadTask = new DownloadTask(downLoadMsg);
                downloadTasks.put(downLoadMsg.uuid, downloadTask);
            }
        }
        return downloadTask;
    }


    void registerMsgCallBack(String uuid, DownloadMsgCallBack downloadMsgCallBack) {
        DownloadMsgCallBackList msgList = msgCallBackHashMap.get(uuid);
        if (msgList == null)
            msgList = new DownloadMsgCallBackList();
        msgList.add(downloadMsgCallBack);
        msgCallBackHashMap.put(uuid, msgList);
    }

    void unRegisterMsgCallBack(String uuid) {
        msgCallBackHashMap.remove(uuid);
    }

    void unRegisterMsgCallBack(String uuid, DownloadMsgCallBack downloadMsgCallBack) {
        List<DownloadMsgCallBack> callBacks = msgCallBackHashMap.get(uuid);
        if (callBacks != null)
            callBacks.remove(downloadMsgCallBack);
    }


}
