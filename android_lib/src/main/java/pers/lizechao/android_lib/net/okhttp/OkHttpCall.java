package pers.lizechao.android_lib.net.okhttp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.Response;
import pers.lizechao.android_lib.net.base.Call;
import pers.lizechao.android_lib.net.base.CallObservable;
import pers.lizechao.android_lib.net.base.NetCallBack;
import pers.lizechao.android_lib.net.base.NetResult;
import pers.lizechao.android_lib.net.base.RequestData;

/**
 * Created by Lzc on 2018/6/14 0014.
 */
public class OkHttpCall extends Call {
    @Nullable
    private final okhttp3.Call realCall;


    OkHttpCall(@Nullable okhttp3.Call realCall, RequestData requestData) {
        super(requestData);
        this.realCall = realCall;
    }

    @NonNull
    @Override
    public CallObservable execute() {
        return new CallObservable(this);
    }


    @Override
    @Nullable
    public NetResult executeSync() throws IOException {
        if(realCall==null)
            return null;
        Response response = realCall.execute();
        if (response == null)
            return null;
        return new OkHttpNetResult(response);
    }

    @NonNull
    @Override
    public Call executeAsync(NetCallBack netCallBack) {
        if (realCall == null) {
            netCallBack.error(null, new IllegalArgumentException("创建请求失败"));
            return this;
        }
        realCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                netCallBack.error(OkHttpCall.this, e);
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                netCallBack.succeed(OkHttpCall.this, new OkHttpNetResult(response));
            }
        });
        return this;
    }

    @Override
    public boolean isExecuted() {
        return realCall != null && realCall.isExecuted();
    }

    @Override
    public void cancel() {
        if (realCall != null)
            realCall.cancel();
    }

    @Override
    public boolean isCanceled() {
        return realCall != null && realCall.isCanceled();
    }

}
