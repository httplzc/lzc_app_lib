package pers.lizechao.android_lib.support.protocol.messenger;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import pers.lizechao.android_lib.support.protocol.base.StubSender;

/**
 * Created by Lzc on 2018/6/27 0027.
 */
public class MessengerSender<T> extends StubSender<T> {
    private final Messenger sender;

    public MessengerSender(Class<T> interfaceClass, IBinder iBinder) {
        super(interfaceClass);
        this.sender = new Messenger(iBinder);

    }

    @Override
    protected void sendStubData(StubData stubData) {
        Message message = Message.obtain();
        Bundle bundle = stubData.getBundle();
        message.setData(bundle);
        message.replyTo = null;
        try {
            sender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
