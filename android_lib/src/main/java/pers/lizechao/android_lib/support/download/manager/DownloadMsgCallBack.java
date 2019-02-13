package pers.lizechao.android_lib.support.download.manager;

import pers.lizechao.android_lib.support.download.comm.TaskMsg;

import java.io.File;

/**
 * Created by Lzc on 2018/5/24 0024.
 */
public interface DownloadMsgCallBack {
    void onStart(TaskMsg taskMsg);

    void onStop(Throwable throwable);

    void onFinish(File file);

    void onProgress(int progress);
}
