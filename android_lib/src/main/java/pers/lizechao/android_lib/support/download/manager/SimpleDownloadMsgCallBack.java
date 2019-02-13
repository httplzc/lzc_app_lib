package pers.lizechao.android_lib.support.download.manager;

import java.io.File;

/**
 * Created by Lzc on 2018/5/24 0024.
 */
public interface SimpleDownloadMsgCallBack {

    void onStop(Throwable throwable);

    void onFinish(File file);

}
