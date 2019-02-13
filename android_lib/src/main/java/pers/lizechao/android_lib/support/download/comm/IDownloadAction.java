package pers.lizechao.android_lib.support.download.comm;

/**
 * Created by Lzc on 2018/6/13 0013.
 * 请求service接口
 */
public interface IDownloadAction {
    //开始下载
    void startDownload(String uuid, DownLoadMsg downLoadMsg);
    //暂停下载
    void pauseDownload(String uuid);
    //删除下载
    void deleteDownLoad(String uuid);
}
