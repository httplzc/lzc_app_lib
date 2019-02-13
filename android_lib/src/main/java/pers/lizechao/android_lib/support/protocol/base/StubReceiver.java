package pers.lizechao.android_lib.support.protocol.base;

import android.support.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Lzc on 2018/6/27 0027.
 */
public abstract class StubReceiver<T> extends Stub<T> implements IReceiver<T> {
    //监听接口
    private T listener;

    public StubReceiver(Class<T> interfaceClass) {
        super(interfaceClass);

    }

    public StubReceiver(Class<T> interfaceClass, T listener) {
        super(interfaceClass);
        receive(listener);
    }


    @Override
    public void receive(T listener) {
        this.listener = listener;
    }


    @Override
    public void unReceive() {
        this.listener = null;
    }

    //收到函数调用，执行回调
    protected void callBackStubData(StubData stubData) {
        if(listener==null)
            return;
        Method method = methodList.get(stubData.indexMethod);
        if (method != null) {
            try {
                method.invoke(listener, stubData.args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }


    @Nullable
    public T getListener() {
        return listener;
    }
}
