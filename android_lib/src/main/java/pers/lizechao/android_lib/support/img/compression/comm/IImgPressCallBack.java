package pers.lizechao.android_lib.support.img.compression.comm;

/**
 * Created by Lzc on 2018/6/11 0011.
 */
public interface IImgPressCallBack {
    void error(String uuid, int index, String error);

    void finish(String uuid, String[] path);

    void finishAllTask();
}
