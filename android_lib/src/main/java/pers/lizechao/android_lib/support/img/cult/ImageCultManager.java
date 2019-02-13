package pers.lizechao.android_lib.support.img.cult;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

import pers.lizechao.android_lib.support.aop.manager.ActivityResultHelper;

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
 * Time: 12:55
 */
public class ImageCultManager {
    private CultCallBack cultCallBack;


    public interface CultCallBack {
        void onSucceed(File file);

        void onFail();
    }

    public void startCultImg(AppCompatActivity context, Uri uri, float ratio, boolean is_circle) {
        ActivityResultHelper.getInstance().startActivityForResult(context, ImageCultActivity.CULT_PIC,
          ImageCultActivity.startCultImgIntent(context, uri, ratio, is_circle), this::handleResult);
    }

    public void setCultCallBack(CultCallBack cultCallBack) {
        this.cultCallBack = cultCallBack;
    }

    private void handleResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != ImageCultActivity.CULT_PIC || cultCallBack == null)
            return;
        if (data == null) {
            cultCallBack.onFail();
            return;
        }
        String path = data.getStringExtra(ImageCultActivity.ResultKey);
        if (path == null)
            cultCallBack.onFail();
        else
            cultCallBack.onSucceed(new File(path));

    }

}
