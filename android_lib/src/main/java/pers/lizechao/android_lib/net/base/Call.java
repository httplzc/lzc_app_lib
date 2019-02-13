package pers.lizechao.android_lib.net.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * Created by Lzc on 2018/6/14 0014.
 */
public abstract class Call {
    private final RequestData requestData;

    public Call(RequestData requestData) {
        this.requestData = requestData;
    }
    
    @NonNull
    abstract public CallObservable execute();

    @Nullable
    abstract public NetResult executeSync() throws IOException;

    @NonNull
    abstract public Call executeAsync(NetCallBack netCallBack);

    abstract public boolean isExecuted();

    abstract public void cancel();

    abstract public boolean isCanceled();

    public RequestData getRequestData() {
        return requestData;
    }
}
