package com.yioks.lzclib.Data;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by ${User} on 2017/3/11 0011.
 */

public class OkHttpInstance {
    private static OkHttpClient client = null;
    private static OkHttpInstance OkHttpInstance = null;
    private static final int cacheSize = 20 * 1024 * 1024;
    private static final int connectTime = 10000;
    private static final int writeTime = 30000;
    private static final int readTime = 60000;

    private OkHttpInstance(Context context) {
        client = new OkHttpClient.Builder().connectTimeout(connectTime, TimeUnit.MILLISECONDS).readTimeout(writeTime, TimeUnit.MILLISECONDS)
                .writeTimeout(readTime, TimeUnit.MILLISECONDS).cache(new Cache(context.getCacheDir(), cacheSize)).build();
    }

    private OkHttpInstance(Context context, long connectTime, long writeTime, long readTime) {
        client = new OkHttpClient.Builder().connectTimeout(connectTime, TimeUnit.MILLISECONDS).readTimeout(writeTime, TimeUnit.MILLISECONDS)
                .writeTimeout(readTime, TimeUnit.MILLISECONDS).cache(new Cache(context.getCacheDir(), cacheSize)).build();
    }

    public static OkHttpInstance InitOkHttpClient(Context context) {
        if (OkHttpInstance == null)
            OkHttpInstance = new OkHttpInstance(context);
        return OkHttpInstance;
    }

    public static OkHttpInstance InitOkHttpClient(Context context, long connectTime, long writeTime, long readTime) {
        if (OkHttpInstance == null)
            OkHttpInstance = new OkHttpInstance(context, connectTime, writeTime, readTime);
        return OkHttpInstance;
    }

    public static OkHttpClient getClient()
    {
        return client;
    }


}
