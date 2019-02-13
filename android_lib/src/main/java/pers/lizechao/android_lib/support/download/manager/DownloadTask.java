package pers.lizechao.android_lib.support.download.manager;

import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.function.Consumer;

import java.io.File;

import pers.lizechao.android_lib.common.DestroyListener;
import pers.lizechao.android_lib.function.Notification;
import pers.lizechao.android_lib.net.utils.NetWatchdog;
import pers.lizechao.android_lib.support.download.comm.DownLoadConfig;
import pers.lizechao.android_lib.support.download.comm.DownLoadMsg;
import pers.lizechao.android_lib.support.download.comm.DownLoadMsg.DownLoadState;
import pers.lizechao.android_lib.support.download.comm.TaskMsg;
import pers.lizechao.android_lib.ui.layout.NormalDialog;

/**
 * Created by Lzc on 2018/5/23 0023.
 * 下载任务属性控制类
 */
public class DownloadTask {
    private final DownLoadMsg downLoadMsg;
    private DownloadMsgCallBack callBack;

    @Nullable
    public static DownloadTask getByUuid(String uuid) {
        return DownLoadManger.getInstance().getDataByDBOrMemory(uuid);
    }

    @NonNull
    public static DownloadTask create() {
        return new DownloadTask(new DownLoadMsg());
    }


    @NonNull
    public static DownloadTask createByUuid(String uuid) {
        return new DownloadTask(new DownLoadMsg(uuid));
    }


    public DownloadTask(DownLoadMsg downLoadMsg) {
        this.downLoadMsg = downLoadMsg;
    }

    private DownloadTask() {
        downLoadMsg = new DownLoadMsg();
    }

    public DownloadTask(String url, String path) {
        this();
        setUrl(url);
        setTargetFilePath(path);
    }

    public DownloadTask(String url, String path, DownLoadConfig downLoadConfig) {
        this();
        setUrl(url);
        setTargetFilePath(path);
        setConfig(downLoadConfig);
    }


    public DownloadTask setUrl(String url) {
        downLoadMsg.url = url;
        return this;
    }


    public void setTargetFilePath(String path) {
        downLoadMsg.targetFilePath = path;
    }

    public void setConfig(DownLoadConfig downLoadConfig) {
        downLoadMsg.downLoadConfig = downLoadConfig;
    }


    public void startDownload(Activity context) {
        startDownload(context, notify -> new NormalDialog.Builder(context)
          .message("你现在处于3G/4G模式下，你确定要进行下载吗？")
          .canBackCancel(true)
          .canTouchCancel(true)
          .confirmListener(v -> notify.notifying())
          .build().showDialog());
    }

    public void startDownload(Activity context, Consumer<Notification> consumer) {
        if (downLoadMsg.downLoadConfig.onlyWifi && NetWatchdog.isMobileConnect(context)) {
            consumer.accept(() -> {
                downLoadMsg.downLoadConfig.onlyWifi = false;
                startDownloadReal();
            });
            return;
        }
        startDownloadReal();
    }

    private void startDownloadReal() {
        DownLoadManger.getInstance().startDownload(this);
    }

    public boolean isDownLoading() {
        return downLoadMsg.currentState == DownLoadState.Downloading;
    }

    public boolean isStop() {
        return downLoadMsg.currentState == DownLoadState.Stop;
    }

    public boolean isUnStart() {
        return downLoadMsg.currentState == DownLoadState.UnStart;
    }

    public boolean downloadFileExits() {
        return getDownloadFile().exists();
    }


    public File getDownloadFile() {
        return new File(downLoadMsg.targetFilePath);
    }


    public boolean checkDownLoadFinish() {
        return downLoadMsg != null && downLoadMsg.currentState == DownLoadMsg.DownLoadState.Finish
          && new File(downLoadMsg.targetFilePath).exists();
    }

    public void pauseDownload() {
        DownLoadManger.getInstance().pauseDownload(downLoadMsg.uuid);
    }

    public void deleteDownLoad() {
        DownLoadManger.getInstance().deleteDownLoad(downLoadMsg.uuid);
    }

    public DownLoadMsg getDownLoadMsg() {
        return downLoadMsg;
    }

    public String getUUID() {
        return downLoadMsg.uuid;
    }

    public void registerMsgCallBack(LifecycleOwner lifecycleOwner, DownloadMsgCallBack downloadMsgCallBack) {
        DownLoadManger.getInstance().registerMsgCallBack(downLoadMsg.uuid, downloadMsgCallBack);
        lifecycleOwner.getLifecycle().addObserver(new DestroyListener(this::unRegisterMsgCallBack));
    }

    public void unRegisterMsgCallBack(DownloadMsgCallBack downloadMsgCallBack) {
        DownLoadManger.getInstance().unRegisterMsgCallBack(downLoadMsg.uuid, downloadMsgCallBack);
    }

    public void unRegisterAllMsgCallBack() {
        DownLoadManger.getInstance().unRegisterMsgCallBack(downLoadMsg.uuid);
    }


    public void unRegisterMsgCallBack() {
        DownLoadManger.getInstance().unRegisterMsgCallBack(downLoadMsg.uuid, callBack);
    }


    public void registerMsgCallBack(LifecycleOwner lifecycleOwner,SimpleDownloadMsgCallBack downloadMsgCallBack) {
        callBack = new DownloadMsgCallBack() {
            @Override
            public void onStart(TaskMsg taskMsg) {

            }

            @Override
            public void onStop(Throwable throwable) {
                downloadMsgCallBack.onStop(throwable);
            }

            @Override
            public void onFinish(File file) {
                downloadMsgCallBack.onFinish(file);
            }

            @Override
            public void onProgress(int progress) {

            }
        };
        DownLoadManger.getInstance().registerMsgCallBack(downLoadMsg.uuid, callBack);
        lifecycleOwner.getLifecycle().addObserver(new DestroyListener(this::unRegisterMsgCallBack));
    }



    //开始状态
    void setStart() {
        downLoadMsg.currentState = DownLoadState.Downloading;
        downLoadMsg.startTime = System.currentTimeMillis();
        downLoadMsg.lastStart = System.currentTimeMillis();
    }

    //结束状态
    void setFinish() {
        downLoadMsg.currentState = DownLoadState.Finish;
        downLoadMsg.endTime = System.currentTimeMillis();
        downLoadMsg.totalTime += System.currentTimeMillis() - downLoadMsg.lastStart;
    }

    //暂停
    void setStop() {
        if (downLoadMsg.currentState == DownLoadState.Finish || downLoadMsg.currentState == DownLoadState.UnStart)
            return;
        downLoadMsg.currentState = DownLoadState.Stop;
        downLoadMsg.totalTime += System.currentTimeMillis() - downLoadMsg.lastStart;
    }

    public String getUrl() {
        return downLoadMsg.url;
    }

}
