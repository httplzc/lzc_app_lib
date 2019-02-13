package pers.lizechao.android_lib.net.base;

/**
 * Created by Lzc on 2018/6/15 0015.
 */
public interface NetCallBack {
    void succeed(Call call, NetResult netResult);

    void error(Call call, Throwable throwable);
}
