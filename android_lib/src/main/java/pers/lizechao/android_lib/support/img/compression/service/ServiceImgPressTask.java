package pers.lizechao.android_lib.support.img.compression.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;

import pers.lizechao.android_lib.data.ApplicationData;
import pers.lizechao.android_lib.storage.file.StoreMedium;
import pers.lizechao.android_lib.storage.file.FileStoreManager;
import pers.lizechao.android_lib.storage.file.Path;
import pers.lizechao.android_lib.support.img.compression.comm.PressConfig;
import pers.lizechao.android_lib.utils.FileUntil;

/**
 * Created by Lzc on 2018/6/11 0011.
 */
public class ServiceImgPressTask {
    final File originFile;
    final PressConfig option;
    final String uuid;
    File newFile;
    PressState currentStatus;
    //使用内存
    long userMemory = 0;
    //序列
    int index = 0;



    public static ServiceImgPressTask createTask(Uri uri, PressConfig option, String uuid, int index) {
        if (uri == null)
            return null;
        File file = FileUntil.UriToFile(uri);
        if (file == null)
            return null;
        ServiceImgPressTask pressMsg = new ServiceImgPressTask(ApplicationData.applicationContext, file, option, uuid, index);
        pressMsg.newFile = FileStoreManager.getFileStore(StoreMedium.External).createFile("press_img_key_" + uuid,
          Path.parse("/cache/press_img/press_img_" + uuid + "_" + index + "."
            + Bitmap.CompressFormat.values()[option.compressFormat].name().toLowerCase()));
        if (pressMsg.newFile == null) {
            return null;
        }
        return pressMsg;
    }

    private ServiceImgPressTask(Context context, File originFile, PressConfig option, String uuid, int index) {
        this.option = (PressConfig) option.clone();
        this.originFile = originFile;
        this.uuid = uuid;
        currentStatus = PressState.Prepare;
        PressImgUntil.longImgOption(context, Uri.fromFile(originFile), this.option);
        userMemory = PressImgUntil.calcPressTaskMemoryUse(context, this);
        this.index = index;
    }

    public enum PressState {
        //压缩中
        Pressing,
        //错误
        Error,
        //完成
        Finish,
        //准备中
        Prepare

    }
}
