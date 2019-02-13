package pers.lizechao.android_lib.storage.file;

/**
 * Created by Lzc on 2018/6/2 0002.
 */
public interface AsyncCallBack<T,E> {
    void onResult(T data);

    void onError(E error);
}
