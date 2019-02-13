package pers.lizechao.android_lib.net.okhttp;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;
import pers.lizechao.android_lib.net.base.NetResult;

/**
 * Created by Lzc on 2018/6/15 0015.
 */
public class OkHttpNetResult extends NetResult {
    private final Response response;

    OkHttpNetResult(@NonNull Response response) {
        this.response = response;
    }

    @Override
    public boolean isSuccessful() {
        return response.isSuccessful();
    }

    @Override
    public byte[] getBytes() {
        if (response.body() != null) {
            try {
                return response.body().bytes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String getString() {
        if (response.body() != null) {
            try {
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public InputStream getStream() {
        if (response.body() != null)
            return response.body().byteStream();
        return null;
    }

    @Override
    public String getHead(String name) {
        return response.header(name);
    }

    @Override
    public long getSentRequestAtMillis() {
        return response.sentRequestAtMillis();
    }

    @Override
    public long getReceivedResponseAtMillis() {
        return response.receivedResponseAtMillis();
    }

    @Override
    public long expendTime() {
        return response.receivedResponseAtMillis() - response.sentRequestAtMillis();
    }

    @Override
    public int responseCode() {
        return response.code();
    }

    @Override
    public long contentLength() {
        if (response.body() == null)
            return 0;
        return response.body().contentLength();
    }
}
