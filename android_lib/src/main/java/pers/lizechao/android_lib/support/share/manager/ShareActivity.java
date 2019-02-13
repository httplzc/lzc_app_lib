package pers.lizechao.android_lib.support.share.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.animation.Animation;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import pers.lizechao.android_lib.function.ActivityCallBack;
import pers.lizechao.android_lib.support.protocol.messenger.MessengerReceiver;
import pers.lizechao.android_lib.support.protocol.messenger.MessengerSender;
import pers.lizechao.android_lib.support.share.data.ShareContent;
import pers.lizechao.android_lib.support.share.data.ShareTarget;
import pers.lizechao.android_lib.support.share.strategy.IShareStrategy;
import pers.lizechao.android_lib.support.share.strategy.StrategyFactory;

/**
 * Created by Lzc on 2017/9/16 0016.
 */

public class ShareActivity extends AppCompatActivity {

    private ActivityCallBack activityCallBack;
    private Disposable disposable;
    public  ShareCallBack shareCallBack;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        activityCallBack.onNewIntent(intent);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return  super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shareCallBack=new MessengerSender<>(ShareCallBack.class, getIntent().getBundleExtra("bundle").getBinder("binder")).asInterface();
        ShareTarget shareTarget = (ShareTarget) getIntent().getSerializableExtra("shareType");
        ShareContent shareContent = getIntent().getParcelableExtra("shareContent");
        IShareStrategy strategy = StrategyFactory.selectStrategyByType(shareTarget);
        activityCallBack = strategy;
        activityCallBack.onCreate(this);
        strategy.share(this, shareContent, shareContent.getShareType())
          .subscribeOn(AndroidSchedulers.mainThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new SingleObserver<Boolean>() {
              @Override
              public void onSubscribe(Disposable d) {
                  disposable = d;
              }

              @Override
              public void onSuccess(Boolean aBoolean) {
                  if (shareCallBack != null)
                      if (aBoolean)
                          shareCallBack.shareSucceed();
                      else
                          shareCallBack.shareCancel();
                  shareCallBack = null;
                  finish();
                  overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
              }

              @Override
              public void onError(Throwable e) {
                  if (shareCallBack != null)
                      shareCallBack.shareFail(e.getMessage());
                  shareCallBack = null;
                  finish();
                  overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
              }
          });
    }


    public static void share(Activity context, ShareTarget shareTarget, ShareContent shareContent, ShareCallBack shareCallBack) {
        IBinder binder = new MessengerReceiver<>(ShareCallBack.class, shareCallBack).getSendBinder();
        Bundle bundle = new Bundle();
        bundle.putBinder("binder", binder);
        Intent intent = new Intent();
        intent.putExtra("shareType", shareTarget);
        intent.putExtra("shareContent", shareContent);
        intent.putExtra("bundle", bundle);
        intent.setClass(context, ShareActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        activityCallBack.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        shareCallBack = null;
        if (disposable != null)
            disposable.dispose();
        activityCallBack.onDestroy(this);
    }


}
