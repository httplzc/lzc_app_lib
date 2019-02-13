package pers.lizechao.android_lib.net.okhttp;

import android.content.Context;

import java.io.InputStream;

import okhttp3.OkHttpClient;
import pers.lizechao.android_lib.ProjectConfig;

/**
 * Created by Lzc on 2017/3/11 0011.
 */

public class OkHttpInstance {
    private static OkHttpClient client;
    private static OkHttpFactory okHttpFactory;


    public static void initOkHttpClient(Context context, InputStream... stream) {
        if (okHttpFactory == null)
            okHttpFactory = OkHttpFactory.newInstance();
        try {
            OkHttpInstance.client = okHttpFactory.createOkHttpClient(context, stream);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("初始化Okhttp失败！");
        }
    }


    public static void setOkHttpFactory(OkHttpFactory factory) {
        okHttpFactory = factory;
    }

    public static abstract class OkHttpFactory {
        abstract protected OkHttpClient createOkHttpClient(Context context, InputStream... stream) throws Exception;

        public static OkHttpFactory newInstance() {
            try {
                return ProjectConfig.getInstance().getOkHttpFactory().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public static OkHttpClient getClient() {
        return client;
    }
}
