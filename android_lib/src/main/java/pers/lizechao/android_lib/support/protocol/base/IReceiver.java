package pers.lizechao.android_lib.support.protocol.base;

import io.reactivex.Scheduler;

/**
 * Created by Lzc on 2018/6/27 0027.
 */
public interface IReceiver<T> {
    //接收回调
    void receive(T listener);

    //去除回调
    void unReceive();

    //选择接收线程
    void scheduler(Scheduler scheduler);

}
