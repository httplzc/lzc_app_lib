package pers.lizechao.android_lib.support.img.compression.comm;


import pers.lizechao.android_lib.support.img.compression.manager.ImgPressTask;

/**
 * Created by Lzc on 2018/6/11 0011.
 */
public interface IImgPressAction {
    void startPress(ImgPressTask imgPressTask);

    void stopPress(String uuid);

    void stopSelf();
}
