package pers.lizechao.android_lib.support.pay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.animation.Animation;

import java.io.Serializable;

import pers.lizechao.android_lib.function.ActivityCallBack;
import pers.lizechao.android_lib.support.protocol.messenger.MessengerReceiver;
import pers.lizechao.android_lib.support.protocol.messenger.MessengerSender;

/**
 * Created by Lzc on 2017/9/16 0016.
 */

public class PayActivity extends AppCompatActivity {

    public PayCallBack payCallBack;
    private ActivityCallBack activityCallBack;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        activityCallBack.onNewIntent(intent);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getBundleExtra("bundle");
        if (bundle != null) {
            payCallBack = new MessengerSender<>(PayCallBack.class, bundle.getBinder("binder")).asInterface();
        }
        if (payCallBack == null) {
            finish();
            return;
        }
        PayTarget payTarget = (PayTarget) getIntent().getSerializableExtra("payTarget");
        Object payData = getIntent().getSerializableExtra("payData");
        IPayStrategy strategy = selectStrategyByType(payTarget);
        activityCallBack = strategy;
        activityCallBack.onCreate(this);
        strategy.pay(this, payData, new PayCallBack() {
            @Override
            public void onSucceed() {
                if (payCallBack != null)
                    payCallBack.onSucceed();
                finish();
                overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
                payCallBack = null;
            }

            @Override
            public void onFail(String msg) {
                if (payCallBack != null)
                    payCallBack.onFail(msg);
                finish();
                overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
                payCallBack = null;
            }

            @Override
            public void onCancel() {
                if (payCallBack != null) {
                    payCallBack.onCancel();
                }
                finish();
                overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
                payCallBack = null;
            }
        });
    }


    protected static void pay(Context context, PayTarget payTarget, Serializable payData, PayCallBack payCallBack) {
        IBinder binder = new MessengerReceiver<>(PayCallBack.class, payCallBack).getSendBinder();
        Bundle bundle = new Bundle();
        bundle.putBinder("binder", binder);
        Intent intent = new Intent();
        intent.putExtra("payTarget", payTarget);
        intent.putExtra("payData", payData);
        intent.putExtra("bundle", bundle);
        intent.setClass(context, PayActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityCallBack.onDestroy(this);
        payCallBack = null;
    }

    @NonNull
    public IPayStrategy selectStrategyByType(PayTarget payTarget) {
        IPayStrategy iPayStrategy = null;
        switch (payTarget) {
            case Ali:
                iPayStrategy = new PayStrategyAli();
                break;
            case WX:
                iPayStrategy = new PayStrategyWx();
                break;
        }
        return iPayStrategy;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        activityCallBack.onActivityResult(requestCode, resultCode, data);
    }

}
