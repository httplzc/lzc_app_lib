package pers.lizechao.android_lib.support.share.strategy;

import android.app.Activity;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import pers.lizechao.android_lib.support.share.data.ShareContent;
import pers.lizechao.android_lib.support.share.data.ShareDateType;
import pers.lizechao.android_lib.support.share.data.ShareException;
import pers.lizechao.android_lib.support.share.manager.ShareCallBack;

/**
 * Created by Lzc on 2018/6/23 0023.
 */
public abstract class BaseShareStrategy implements IShareStrategy,ShareCallBack{
    protected SingleObserver<? super Boolean> observer;
    protected Activity activity;

    @Override
    public Single<Boolean> share(Activity context, ShareContent shareContent, ShareDateType shareDateType) {
        this.activity = context;
        return new Single<Boolean>() {
            @Override
            protected void subscribeActual(SingleObserver<? super Boolean> observer) {
                BaseShareStrategy.this.observer = observer;

                try {
                    switch (shareDateType) {
                        case TEXT:
                            sendText(shareContent);
                            break;
                        case IMG:
                            sendImg(shareContent);
                            break;
                        case WEB_PAGE:
                            sendWebPage(shareContent);
                            break;
                        case VIDEO:
                            sendVideo(shareContent);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    BaseShareStrategy.this.observer.onError(e);
                }

            }
        };

    }

    abstract void sendVideo(ShareContent shareContent) ;

    abstract void sendWebPage(ShareContent shareContent);

    abstract void sendImg(ShareContent shareContent);

    abstract  void sendText(ShareContent shareContent) ;

    @Override
    public void onDestroy(Activity activity) {
        this.activity = null;
        observer = null;
    }

    @Override
    public void shareSucceed() {
        if (observer == null)
            return;
        observer.onSuccess(true);
        observer = null;
    }

    @Override
    public void shareFail(String errorCode) {
        if (observer == null)
            return;
        observer.onError(new ShareException(errorCode));
        observer = null;
    }

    @Override
    public void shareCancel() {
        if (observer == null)
            return;
        observer.onSuccess(false);
        observer = null;
    }
}
