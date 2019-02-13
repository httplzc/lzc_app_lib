package pers.lizechao.android_lib.support.protocol.messenger;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;

import pers.lizechao.android_lib.support.protocol.base.ThreadReceiver;

import io.reactivex.Scheduler;

/**
 * Created by Lzc on 2018/6/27 0027.
 */
public class MessengerReceiver<T> extends ThreadReceiver<T> {
    public MessengerReceiver(Class<T> interfaceClass) {
        super(interfaceClass);
    }

    public MessengerReceiver(Class<T> interfaceClass, T listener) {
        super(interfaceClass, listener);
    }

    public MessengerReceiver(Class<T> interfaceClass, T listener, @Nullable Scheduler scheduler) {
        super(interfaceClass, listener, scheduler);
    }

    /**
     * 生成binder 并接收回调  接收端调用
     **/
    public IBinder getSendBinder() {
        return new Messenger(new CallbackHandler()).getBinder();
    }

    @SuppressLint("HandlerLeak")
    private class CallbackHandler extends Handler {
        CallbackHandler() {
            super(Looper.getMainLooper());
        }


        @Override
        public void handleMessage(Message msg) {
            callBackStubData(new StubData(msg.getData()));
            super.handleMessage(msg);
        }
    }


}
