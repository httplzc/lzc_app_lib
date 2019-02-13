package pers.lizechao.android_lib.support.download.comm;

/**
 * Created by Lzc on 2018/6/13 0013.
 * service回调接口
 */
public interface IDownloadCallback {
    //回调开始
    void callBackStart(TaskMsg taskMsg);

    //回调停止
    void callBackStop(String uuid, Throwable throwable);

    //回调结束
    void callBackEnd(String uuid, String filePath);

    //回调进度
    void callBackProgress(String uuid, int progress);

    //回调结束
    void onFinishAllTask();
}
