package pers.lizechao.android_lib.support.img.upload;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

import pers.lizechao.android_lib.storage.file.FileStoreManager;
import pers.lizechao.android_lib.storage.file.FileStoreOption;
import pers.lizechao.android_lib.storage.file.Path;
import pers.lizechao.android_lib.storage.file.StoreMedium;
import pers.lizechao.android_lib.support.aop.manager.ActivityResultHelper;
import pers.lizechao.android_lib.support.aop.manager.PermissionHelper;

import static android.app.Activity.RESULT_OK;

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
 * Time: 14:08
 * 拍照控制器
 */
public class TakePhotoManager {
    private TakePhotoCallBack takePhotoCallBack;
    private static final int Choice_Camera = 3011;
    private static final int Choice_Camera_CODE = 3011;
    private Uri myUri;
    private Path path;

    public TakePhotoManager(Path path) {
        this.path = path;
    }

    public interface TakePhotoCallBack {
        void onSucceed(Uri uri);

        void onFail();
    }

    private void handleResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != Choice_Camera || takePhotoCallBack == null)
            return;
        if (resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                takePhotoCallBack.onSucceed(data.getData());
            } else {
                takePhotoCallBack.onSucceed(myUri);
            }
        } else {
            takePhotoCallBack.onFail();
        }
    }

    public void startTakePhoto(AppCompatActivity context) {
        PermissionHelper.getInstance().request(context, Choice_Camera_CODE,
          new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}
          , new PermissionHelper.CallBack() {
              @Override
              public void succeed() {
                  getImageFromCamera(context);
              }

              @Override
              public void fail(String[] permissions) {
                  if (takePhotoCallBack != null)
                      takePhotoCallBack.onFail();
              }
          });
    }

    /**
     * 选择照相
     */
    private void getImageFromCamera(AppCompatActivity activity) {
        File newFile = FileStoreManager.getFileStore(StoreMedium.SDCard).createFile("photo.jpg",path, FileStoreOption.CreateNew);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, newFile.getAbsolutePath());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        myUri = activity.getContentResolver().insert(
          MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
        getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, myUri);
        ActivityResultHelper.getInstance().startActivityForResult(activity, Choice_Camera, getImageByCamera, this::handleResult);
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void setTakePhotoCallBack(TakePhotoCallBack takePhotoCallBack) {
        this.takePhotoCallBack = takePhotoCallBack;
    }
}
