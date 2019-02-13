package pers.lizechao.android_lib.support.share.strategy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import io.reactivex.Single;
import pers.lizechao.android_lib.support.share.data.ShareContent;
import pers.lizechao.android_lib.support.share.data.ShareDateType;
import pers.lizechao.android_lib.support.share.data.ShareExtraData;
import pers.lizechao.android_lib.support.share.manager.ShareActivity;


/**
 * Created by Lzc on 2018/6/23 0023.
 */
public abstract class WXShareStrategyBase extends BaseShareStrategy {
    private static final String ShareWxCallBackData = "share_wx_callback_data";

    abstract int getScreenByType();

    @Override
    public Single<Boolean> share(Activity context, ShareContent shareContent, ShareDateType shareDateType) {
        Single<Boolean> single = super.share(context, shareContent, shareDateType);
        sendInit();
        return single;
    }

    private void sendInit() {
        IWXAPI api = WXAPIFactory.createWXAPI(activity, ShareExtraData.WXId, true);
        api.registerApp(ShareExtraData.WXId);
    }

    private String buildTransaction(String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private SendMessageToWX.Req getWXRequest(ShareContent shareContent) {
        WXMediaMessage wxMediaMessage = new WXMediaMessage();
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("share_wx");
        req.scene = getScreenByType();
        req.message = wxMediaMessage;
        wxMediaMessage.title = shareContent.mSubject;
        wxMediaMessage.description = shareContent.mText;
        return req;
    }

    private void sendRequest(SendMessageToWX.Req req) {
        IWXAPI api = WXAPIFactory.createWXAPI(activity, ShareExtraData.WXId);
        api.sendReq(req);
    }


    @Override
    void sendVideo(ShareContent shareContent) {
        SendMessageToWX.Req req = getWXRequest(shareContent);
        req.message.thumbData = shareContent.mMedia.getThumbBytes();
        WXVideoObject wxVideoObject = new WXVideoObject();
        wxVideoObject.videoUrl = shareContent.mMedia.toDataPath();
        req.message.mediaObject=wxVideoObject;
        sendRequest(req);
    }

    @Override
    void sendWebPage(ShareContent shareContent) {
        SendMessageToWX.Req req = getWXRequest(shareContent);
        req.message.thumbData = shareContent.mMedia.getThumbBytes();
        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = shareContent.mMedia.toDataPath();
        req.message.mediaObject=webpageObject;
        sendRequest(req);
    }

    @Override
    void sendImg(ShareContent shareContent) {
        SendMessageToWX.Req req = getWXRequest(shareContent);
        req.message.thumbData = shareContent.mMedia.getThumbBytes();
        WXImageObject wxImageObject = new WXImageObject();
        wxImageObject.imagePath = shareContent.mMedia.toDataPath();
        req.message.mediaObject=wxImageObject;
        sendRequest(req);
    }

    @Override
    void sendText(ShareContent shareContent) {
        SendMessageToWX.Req req = getWXRequest(shareContent);
        sendRequest(req);
    }

    public static void onWXCallbackDo(Context context, BaseResp baseResp) {
        if (baseResp == null)
            return;
        Bundle bundle = new Bundle();
        baseResp.toBundle(bundle);
        Intent intent = new Intent();
        intent.setClass(context, ShareActivity.class);
        intent.putExtra(ShareWxCallBackData, bundle);
        context.startActivity(intent);
    }


    private void handIntentByWX(Intent intent) {
        if (intent != null) {
            Bundle bundle = intent.getBundleExtra(ShareWxCallBackData);
            if (bundle != null) {
                ShowMessageFromWX.Resp resp = new ShowMessageFromWX.Resp(bundle);
                switch (resp.errCode) {
                    case BaseResp.ErrCode.ERR_OK:
                        shareSucceed();
                        break;
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        shareCancel();
                        break;
                    default:
                        shareFail(resp.errStr);
                        break;
                }
            }

        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        handIntentByWX(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onCreate(Activity activity) {

    }
}
