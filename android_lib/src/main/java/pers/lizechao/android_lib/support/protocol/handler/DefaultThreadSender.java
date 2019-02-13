package pers.lizechao.android_lib.support.protocol.handler;

import pers.lizechao.android_lib.support.protocol.base.StubSender;
import pers.lizechao.android_lib.support.protocol.base.ThreadReceiver;

/**
 * Created by Lzc on 2018/6/27 0027.
 */
public class DefaultThreadSender<T> extends StubSender<T> {
    private final ThreadReceiver<T> receiver;

    public DefaultThreadSender(ThreadReceiver<T> receiver, Class<T> interfaceClass) {
        super(interfaceClass);
        this.receiver = receiver;
    }

    @Override
    public void sendStubData(StubData stubData) {
        receiver.callBackStubData(stubData);
    }
}
