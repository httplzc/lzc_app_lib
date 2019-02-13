package pers.lizechao.android_lib.support.share.manager;

/**
 * Created by Lzc on 2018/6/22 0022.
 */
public interface ShareCallBack {
    void shareSucceed();

    void shareFail(String errorCode);

    void shareCancel();
}
