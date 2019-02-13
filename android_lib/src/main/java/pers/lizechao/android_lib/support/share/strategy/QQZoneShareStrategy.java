package pers.lizechao.android_lib.support.share.strategy;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;

import pers.lizechao.android_lib.support.share.data.ShareContent;
import pers.lizechao.android_lib.support.share.data.ShareExtraData;
import pers.lizechao.android_lib.support.share.media.ShareMediaImg;
import pers.lizechao.android_lib.support.share.media.ShareMediaWebPage;
import pers.lizechao.android_lib.utils.FileUntil;

/**
 * Created by Lzc on 2018/6/22 0022.
 */
public class QQZoneShareStrategy extends BaseShareStrategy implements IUiListener {

    @Override
    public void onComplete(Object o) {
        shareSucceed();
    }

    @Override
    public void onError(UiError uiError) {
        shareFail(uiError.errorMessage);
    }

    @Override
    public void onCancel() {
        shareCancel();
    }

    @Override
    void sendVideo(ShareContent shareContent) {
        shareFail("qq空间不支持该种类型");
    }

    @Override
    void sendWebPage(ShareContent shareContent) {
        Tencent mTencent = Tencent.createInstance(ShareExtraData.QQId, activity);
        Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareContent.mText);//必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareContent.mSubject);//选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareContent.mMedia.toUrl());//必填
        ArrayList<String> imgs = new ArrayList<>();
        if (shareContent.mMedia instanceof ShareMediaImg) {
            ShareMediaImg shareMediaImg = (ShareMediaImg) shareContent.mMedia;
            if (shareMediaImg.getImgUris() != null) {
                for (Uri uri : shareMediaImg.getImgUris()) {
                    if (FileUntil.isFile(uri))
                        imgs.add(FileUntil.UriToFile(uri).getPath());
                    else
                        imgs.add(uri.toString());
                }
            } else {
                if (shareContent.mMedia.isUrl())
                    imgs.add(shareContent.mMedia.toUrl());
                else
                    imgs.add(shareContent.mMedia.toLocalPath());
            }

        } else if (shareContent.mMedia instanceof ShareMediaWebPage) {
            imgs.add(shareContent.mMedia.getThumbPath());
        }
        if (imgs.size() != 0)
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgs);
        mTencent.shareToQzone(activity, params, this);
    }

    @Override
    void sendImg(ShareContent shareContent) {
        Tencent mTencent = Tencent.createInstance(ShareExtraData.QQId, activity);
        Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareContent.mText);//必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareContent.mSubject);//选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareContent.mMedia.getThumbPath());//必填
        ArrayList<String> imgs = new ArrayList<>();
        ShareMediaImg shareMediaImg = (ShareMediaImg) shareContent.mMedia;
        if (shareMediaImg.getImgUris() != null) {
            for (Uri uri : shareMediaImg.getImgUris()) {
                if (FileUntil.isFile(uri))
                    imgs.add(FileUntil.UriToFile(uri).getPath());
                else
                    imgs.add(uri.toString());
            }
        } else {
            imgs.add(shareContent.mMedia.toDataPath());
        }
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgs);
        mTencent.shareToQzone(activity, params, this);
    }

    @Override
    void sendText(ShareContent shareContent) {
        shareFail("qq空间不支持该种类型");
    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, this);
    }

    @Override
    public void onCreate(Activity activity) {

    }
}
