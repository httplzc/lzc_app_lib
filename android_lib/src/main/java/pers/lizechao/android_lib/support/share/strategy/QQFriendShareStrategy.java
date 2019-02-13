package pers.lizechao.android_lib.support.share.strategy;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import pers.lizechao.android_lib.utils.DeviceUtil;
import pers.lizechao.android_lib.utils.StrUtils;
import pers.lizechao.android_lib.support.share.data.ShareContent;
import pers.lizechao.android_lib.support.share.data.ShareExtraData;

/**
 * Created by Lzc on 2018/6/22 0022.
 */
public class QQFriendShareStrategy extends BaseShareStrategy implements IUiListener {

    void sendVideo(ShareContent shareContent) {
        shareFail("qq不支持该种类型");
    }


    void sendWebPage(ShareContent shareContent) {
        Tencent mTencent = Tencent.createInstance(ShareExtraData.QQId, activity);
        Bundle bundle = new Bundle();
        bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, DeviceUtil.getAppName(activity));
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, shareContent.mSubject);
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, StrUtils.CheckEmpty(shareContent.mText));
        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareContent.mMedia.toUrl());
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, shareContent.mMedia.getThumbPath());
        mTencent.shareToQQ(activity, bundle, this);
    }


    void sendImg(ShareContent shareContent) {
        Tencent mTencent = Tencent.createInstance(ShareExtraData.QQId, activity);
        Bundle bundle = new Bundle();
        bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        if (shareContent.mMedia.isPath()) {
            bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, shareContent.mMedia.toLocalPath());
        } else {
            bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareContent.mMedia.toUrl());
        }
        bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, DeviceUtil.getAppName(activity));
        mTencent.shareToQQ(activity, bundle, this);
    }

    void sendText(ShareContent shareContent) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, shareContent.mText);
        intent.putExtra(Intent.EXTRA_TEXT, shareContent.mSubject);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity"));
        activity.startActivity(intent);
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
}
