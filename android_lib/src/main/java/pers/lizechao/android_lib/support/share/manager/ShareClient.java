package pers.lizechao.android_lib.support.share.manager;

import android.app.Activity;

import pers.lizechao.android_lib.support.share.data.ShareContent;
import pers.lizechao.android_lib.support.share.data.ShareTarget;
import pers.lizechao.android_lib.support.share.media.ShareMediaImg;
import pers.lizechao.android_lib.support.share.media.ShareMediaVideo;
import pers.lizechao.android_lib.support.share.media.ShareMediaWebPage;

/**
 * Created by Lzc on 2018/6/22 0022.
 */
public class ShareClient {
    private ShareCallBack shareCallBack;
    private ShareContent shareContent = new ShareContent();
    private final Activity activity;


    private ShareClient(Activity activity) {
        this.activity = activity;
    }


    public static ShareClient create(Activity activity) {
        return new ShareClient(activity);
    }


    public ShareClient withContext(ShareContent shareContent) {
        this.shareContent = shareContent;
        return this;
    }

    public ShareClient withText(String text) {
        shareContent.mText = text;
        return this;
    }

    public ShareClient withCallback(ShareCallBack callback) {
        shareCallBack = callback;
        return this;
    }

    public ShareClient withSubject(String text) {
        shareContent.mSubject = text;
        return this;
    }

    public ShareClient withMedia(ShareMediaImg shareMediaImg) {
        shareContent.mMedia = shareMediaImg;
        return this;
    }

    public ShareClient withMedia(ShareMediaVideo shareMediaVideo) {
        shareContent.mMedia = shareMediaVideo;
        return this;
    }

    public ShareClient withMedia(ShareMediaWebPage shareMediaWebPage) {
        shareContent.mMedia = shareMediaWebPage;
        return this;
    }


    public void share(ShareTarget shareTarget) {
        ShareActivity.share(activity, shareTarget, shareContent, shareCallBack);
    }

    public static void share(Activity activity, ShareTarget shareTarget, ShareContent shareContent, ShareCallBack shareCallBack) {
        ShareActivity.share(activity, shareTarget, shareContent, shareCallBack);
    }

}
