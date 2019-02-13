package pers.lizechao.android_lib.support.protocol.base;

import java.lang.reflect.Proxy;

/**
 * Created by Lzc on 2018/6/27 0027.
 */
public abstract class StubSender<T> extends Stub<T> implements ISender<T> {
    private final T data;

    public StubSender(Class<T> interfaceClass) {
        super(interfaceClass);
        data = (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, (proxy, method, args) -> {
            sendStubData(new StubData(methodList.indexOf(method), args));
            return null;
        });
    }


    public T asInterface() {
        return data;
    }

    protected abstract void sendStubData(StubData stubData);
}
