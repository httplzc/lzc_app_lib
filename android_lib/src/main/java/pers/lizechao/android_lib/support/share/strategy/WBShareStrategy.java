package pers.lizechao.android_lib.support.share.strategy;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MultiImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoSourceObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;

import java.util.ArrayList;
import java.util.Arrays;

import io.reactivex.Single;
import pers.lizechao.android_lib.support.share.data.ShareContent;
import pers.lizechao.android_lib.support.share.data.ShareDateType;
import pers.lizechao.android_lib.support.share.data.ShareExtraData;
import pers.lizechao.android_lib.support.share.media.ShareMediaImg;

/**
 * Created by Lzc on 2018/6/22 0022.
 */
public class WBShareStrategy extends BaseShareStrategy implements WbShareCallback {
    private WbShareHandler wbShareHandler;

    @Override
    public Single<Boolean> share(Activity context, ShareContent shareContent, ShareDateType shareDateType) {
        AuthInfo mAuthInfo = new AuthInfo(context, ShareExtraData.WBId, "https://api.weibo.com/oauth2/default.html", "all");
        WbSdk.install(context, mAuthInfo);
        return super.share(context, shareContent, shareDateType);
    }

    private void send(WeiboMultiMessage weiboMultiMessage) {
        wbShareHandler = new WbShareHandler(activity);
        wbShareHandler.registerApp();
        wbShareHandler.shareMessage(weiboMultiMessage, false);
    }

    @Override
    void sendVideo(ShareContent shareContent) {
        WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
        weiboMultiMessage.videoSourceObject = new VideoSourceObject();
        weiboMultiMessage.videoSourceObject.videoPath = Uri.parse(shareContent.mMedia.toDataPath());
        weiboMultiMessage.videoSourceObject.coverPath = shareContent.mMedia.getThumbUri();
        send(weiboMultiMessage);
    }

    @Override
    void sendWebPage(ShareContent shareContent) {
        shareFail("微博不支持该种类型");
    }

    @Override
    void sendImg(ShareContent shareContent) {
        ShareMediaImg shareMediaImg = (ShareMediaImg) shareContent.mMedia;
        WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
        if (shareMediaImg.getImgUris() != null) {
            MultiImageObject multiImageObject = new MultiImageObject();
            multiImageObject.imageList = new ArrayList<>();
            multiImageObject.imageList.addAll(Arrays.asList(shareMediaImg.getImgUris()));
            weiboMultiMessage.multiImageObject = multiImageObject;

        } else {
            weiboMultiMessage.imageObject = new ImageObject();
            weiboMultiMessage.imageObject.imagePath = shareContent.mMedia.toDataPath();
        }
        weiboMultiMessage.textObject = new TextObject();
        weiboMultiMessage.textObject.text = shareContent.mText;
        send(weiboMultiMessage);
    }

    @Override
    void sendText(ShareContent shareContent) {
        WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
        weiboMultiMessage.textObject = new TextObject();
        weiboMultiMessage.textObject.text = shareContent.mText;
        send(weiboMultiMessage);
    }

    @Override
    public void onNewIntent(Intent intent) {
        wbShareHandler.doResultIntent(intent, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onCreate(Activity activity) {

    }

    @Override
    public void onWbShareSuccess() {
        shareSucceed();
    }

    @Override
    public void onWbShareCancel() {
        shareCancel();
    }

    @Override
    public void onWbShareFail() {
        shareFail("");
    }
}
