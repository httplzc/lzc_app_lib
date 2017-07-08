package com.yioks.lzclib.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.PopupWindow;

import com.yioks.lzclib.Helper.ChoicePhotoManager;
import com.yioks.lzclib.Untils.FileUntil;

import java.io.File;

/**
 * 继承他可调用相机
 * Created by lzc on 2016/7/11 0011.
 */
public abstract class TakePhoteBaseActivity extends TitleBaseActivity {


    private ChoicePhotoManager choicePhotoManager;

    protected void initChoicePhotoManager() {
        choicePhotoManager=new ChoicePhotoManager(this);
        choicePhotoManager.setOnChoiceFinishListener(new ChoicePhotoManager.onChoiceFinishListener() {
            @Override
            public void onCutPicFinish(Uri uri) {
                onCutPicfinish(new File(FileUntil.UriToFile(uri,context)));
            }

            @Override
            public void onCutPicFinish(Uri[] uris) {
                onCutPicfinish(uris);
            }

            @Override
            public void cancel() {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //activity 返回值
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        choicePhotoManager.onCultActivityResultDo(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    //裁剪后图片回调
    public abstract void onCutPicfinish(File file);




    public abstract void onCutPicfinish(Uri[] uris);




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        choicePhotoManager.onCultRequestPermissionsResultDo(requestCode, permissions, grantResults);

    }

    /**
     * 显示底部弹窗
     *
     * @param activity
     * @param bili
     * @return
     */
    public PopupWindow showPopwindow(Activity activity, float bili, int limitCount) {
        ChoicePhotoManager.Option option=new ChoicePhotoManager.Option();
        option.bili=bili;
        return choicePhotoManager.showChoiceWindow(this,limitCount,option);
    }


    @Override
    protected void onDestroy() {
        if(choicePhotoManager!=null)
            choicePhotoManager.unRegisterReceiver();
        super.onDestroy();
    }
}
