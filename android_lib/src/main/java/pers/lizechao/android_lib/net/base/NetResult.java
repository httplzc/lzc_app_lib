package pers.lizechao.android_lib.net.base;

import android.support.annotation.Nullable;

import java.io.InputStream;

/**
 * Created by Lzc on 2018/6/14 0014.
 */
public abstract class NetResult {

    abstract public boolean isSuccessful();

    abstract public @Nullable
    byte[] getBytes();

    abstract public @Nullable
    String getString();

    abstract public @Nullable
    InputStream getStream();

    abstract public String getHead(String name);

    abstract public long getSentRequestAtMillis();

    abstract public long getReceivedResponseAtMillis();

    abstract public long expendTime();

    abstract public int responseCode();

    abstract public long contentLength();
}
