package pers.lizechao.android_lib.support.img.compression.manager;

/**
 * Created by Lzc on 2018/6/11 0011.
 */
public interface PressCallBack {
    void onFinish(String uuid, String[] paths);

    void onError(String uuid, int index, String error);
}
