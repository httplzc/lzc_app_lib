package pers.lizechao.android_lib.support.img.pick;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;

import pers.lizechao.android_lib.support.aop.manager.ActivityResultHelper;
import pers.lizechao.android_lib.support.aop.manager.PermissionHelper;

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
 * 选取图片控制器
 */
public class PickImageManager {
    private PickImageCallBack pickImageCallBack;
    static final int Pick_Image = 5011;
    private static final int Pick_Image_CODE = 5011;
    private final int limitCount;

    public PickImageManager(int limitCount) {
        this.limitCount = limitCount;
    }

    public interface PickImageCallBack {
        void onSucceed(Uri[] uris);

        void onFail();
    }

    private void handleResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != Pick_Image || pickImageCallBack == null)
            return;
        if (data != null) {
            Parcelable[] parcelables = data.getParcelableArrayExtra("uris");
            Uri uris[] = new Uri[parcelables.length];
            for (int i = 0; i < parcelables.length; i++) {
                uris[i] = (Uri) parcelables[i];
            }
            pickImageCallBack.onSucceed(uris);
        } else {
            pickImageCallBack.onFail();
        }
    }

    public void startPickImage(AppCompatActivity context) {
        PermissionHelper.getInstance().request(context, Pick_Image_CODE,
          new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
          , new PermissionHelper.CallBack() {
              @Override
              public void succeed() {
                  pickImageFromAlbum(context, limitCount);
              }

              @Override
              public void fail(String[] permissions) {
                  if (pickImageCallBack != null)
                      pickImageCallBack.onFail();
              }
          });
    }

    /**
     * 选择照相
     */
    private void pickImageFromAlbum(AppCompatActivity activity, int limitSize) {
        ActivityResultHelper.getInstance().startActivityForResult(activity, Pick_Image,
          PicImageActivity.createIntent(activity, limitSize), this::handleResult);
    }


    public void setPickImageCallBack(PickImageCallBack pickImageCallBack) {
        this.pickImageCallBack = pickImageCallBack;
    }
}
