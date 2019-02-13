package pers.lizechao.android_lib.support.download.comm;

import java.io.Serializable;

/**
 * Created by Lzc on 2018/5/24 0024.
 */
public class DownLoadConfig implements Serializable {
    //只有wifi状态下载
    public boolean onlyWifi;

    //是否显示通知栏提示
    public boolean showByNotification;

    public DownLoadConfig(boolean onlyWifi, boolean showByNotification) {
        this.onlyWifi = onlyWifi;
        this.showByNotification = showByNotification;
    }

    public DownLoadConfig() {
    }
}
