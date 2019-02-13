package pers.lizechao.android_lib.support.img.upload;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

import pers.lizechao.android_lib.common.DestroyListener;
import pers.lizechao.android_lib.storage.file.Path;
import pers.lizechao.android_lib.support.img.compression.manager.ImgPressTask;
import pers.lizechao.android_lib.support.img.compression.manager.PressCallBack;
import pers.lizechao.android_lib.support.img.cult.ImageCultManager;
import pers.lizechao.android_lib.support.img.pick.PickImageManager;
import pers.lizechao.android_lib.utils.DialogUtil;

/**
 * Created with
 * ********************************************************************************
 * #         ___                     ________                ________             *
 * #       |\  \                   |\_____  \              |\   ____\             *
 * #       \ \  \                   \|___/  /|             \ \  \___|             *
 * #        \ \  \                      /  / /              \ \  \                *
 * #         \ \  \____                /  /_/__              \ \  \____           *
 * #          \ \_______\             |\________\             \ \_______\         *
 * #           \|_______|              \|_______|              \|_______|         *
 * #                                                                              *
 * ********************************************************************************
 * Date: 2018-08-17
 * Time: 14:26
 * 上传图片控制器
 */
public class UploadImageManager {
    private final TakePhotoManager takePhotoManager;
    private final ImageCultManager imageCultManager;
    private final PickImageManager pickImageManager;
    private final ImgPressTask task;
    private final AppCompatActivity activity;
    private final UploadConfig uploadConfig;


    private UploadImageListener uploadImageListener;

    public UploadImageManager(AppCompatActivity activity, Path photoPath) {
        this(activity, photoPath, null);
    }



    public UploadImageManager(AppCompatActivity activity, Path photoPath, UploadConfig uploadConfig) {
        if (uploadConfig == null)
            this.uploadConfig = new UploadConfig.Builder().build();
        else
            this.uploadConfig = uploadConfig;
        this.activity = activity;
        takePhotoManager = new TakePhotoManager(photoPath);
        imageCultManager = new ImageCultManager();
        pickImageManager = new PickImageManager(this.uploadConfig.getPickCount());
        task = new ImgPressTask();
        if (this.uploadConfig.getPressConfig() != null)
            task.setConfig(this.uploadConfig.getPressConfig());
        initListener();
    }

    private void initListener() {
        pickImageManager.setPickImageCallBack(new PickImageManager.PickImageCallBack() {
            @Override
            public void onSucceed(Uri[] uris) {
                if (uris == null || uris.length == 0) {
                    onFail();
                    return;
                }
                if (uploadConfig.isNeedCult() && uris.length == 1) {
                    imageCultManager.startCultImg(activity, uris[0], uploadConfig.getCultRatio(), uploadConfig.isCultIsCircle());
                    return;
                }
                if (uploadConfig.isNeedPress()) {
                    task.setUris(uris);
                    task.startPress();
                    DialogUtil.showDialog(activity, "正在压缩图片！");
                    return;
                }
                if (uploadImageListener != null)
                    uploadImageListener.onFinish(uris);
            }

            @Override
            public void onFail() {
                if (uploadImageListener != null)
                    uploadImageListener.onFail();
            }
        });
        takePhotoManager.setTakePhotoCallBack(new TakePhotoManager.TakePhotoCallBack() {
            @Override
            public void onSucceed(Uri uri) {
                if (uploadConfig.isNeedCult()) {
                    imageCultManager.startCultImg(activity, uri, uploadConfig.getCultRatio(), uploadConfig.isCultIsCircle());
                    return;
                }
                if (uploadConfig.isNeedPress()) {
                    task.setUri(uri);
                    task.startPress();
                    DialogUtil.showDialog(activity, "正在压缩图片！");
                    return;
                }
                if (uploadImageListener != null)
                    uploadImageListener.onFinish(new Uri[]{uri});
            }

            @Override
            public void onFail() {
                if (uploadImageListener != null)
                    uploadImageListener.onFail();
            }
        });
        imageCultManager.setCultCallBack(new ImageCultManager.CultCallBack() {
            @Override
            public void onSucceed(File file) {
                if (uploadConfig.isNeedPress()) {
                    task.setUri(Uri.fromFile(file));
                    task.startPress();
                    DialogUtil.showDialog(activity, "正在压缩图片！");
                    return;
                }
                if (uploadImageListener != null)
                    uploadImageListener.onFinish(new Uri[]{Uri.fromFile(file)});
            }

            @Override
            public void onFail() {
                if (uploadImageListener != null)
                    uploadImageListener.onFail();
            }
        });

        task.setCallback(activity, new PressCallBack() {
            @Override
            public void onFinish(String uuid, String[] paths) {
                DialogUtil.dismissDialog();
                Uri uris[] = new Uri[paths.length];
                for (int i = 0; i < uris.length; i++) {
                    uris[i] = Uri.fromFile(new File(paths[i]));
                }
                if (uploadImageListener != null)
                    uploadImageListener.onFinish(uris);
            }

            @Override
            public void onError(String uuid, int index, String error) {
                DialogUtil.dismissDialog();
                if (uploadImageListener != null)
                    uploadImageListener.onFail();
            }
        });

        activity.getLifecycle().addObserver(new DestroyListener(this::unRegisterListener));
    }

//    public void onResultActivity(int requestCode, int resultCode, Intent data) {
//        takePhotoManager.handleResult(requestCode, resultCode, data);
//        imageCultManager.handleResult(requestCode, resultCode, data);
//        pickImageManager.handleResult(requestCode, resultCode, data);
//    }

    public interface UploadImageListener {
        void onFinish(Uri uris[]);

        void onFail();
    }

    public void pickImage() {
        pickImageManager.startPickImage(activity);
    }

    public void takePhoto() {
        takePhotoManager.startTakePhoto(activity);
    }

    public void setUploadImageListener(UploadImageListener uploadImageListener) {
        this.uploadImageListener = uploadImageListener;
    }

    public void unRegisterListener() {
        pickImageManager.setPickImageCallBack(null);
        takePhotoManager.setTakePhotoCallBack(null);
        task.unregisterCallBack();
    }

}
