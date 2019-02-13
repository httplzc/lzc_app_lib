package pers.lizechao.android_lib.support.protocol.handler;

import android.os.Handler;
import android.os.Looper;

import pers.lizechao.android_lib.support.protocol.base.HandlerScheduler;
import pers.lizechao.android_lib.support.protocol.base.IReceiver;
import pers.lizechao.android_lib.support.protocol.base.ISender;
import pers.lizechao.android_lib.support.protocol.base.ThreadReceiver;

import io.reactivex.Scheduler;

/**
 * Created by Lzc on 2018/6/27 0027.
 */
public class ThreadStub<T> implements ISender<T>, IReceiver<T> {
    private final DefaultThreadSender<T> defaultThreadSender;
    private final ThreadReceiver<T> tThreadReceiver;

    public ThreadStub(Class<T> tClass) {
        tThreadReceiver = new ThreadReceiver<>(tClass);
        defaultThreadSender = new DefaultThreadSender<>(tThreadReceiver, tClass);
    }


    @Override
    public T asInterface() {
        return defaultThreadSender.asInterface();
    }

    @Override
    public void receive(T listener) {
        tThreadReceiver.receive(listener);
    }

    @Override
    public void unReceive() {
        tThreadReceiver.unReceive();
    }

    @Override
    public void scheduler(Scheduler scheduler) {
        tThreadReceiver.scheduler(scheduler);
    }

    public static <T> T createInterface(Class<T> interfaceClass, Looper looper, T listener) {
        ThreadStub<T> threadStub = new ThreadStub<>(interfaceClass);
        threadStub.receive(listener);
        threadStub.scheduler(new HandlerScheduler(new Handler(looper)));
        return threadStub.asInterface();
    }

    public static <T> T createInterface(Class<T> interfaceClass, Scheduler scheduler, T listener) {
        ThreadStub<T> threadStub = new ThreadStub<>(interfaceClass);
        threadStub.receive(listener);
        threadStub.scheduler(scheduler);
        return threadStub.asInterface();
    }


    public static <T> T createInterface(Class<T> interfaceClass, T listener) {
        ThreadStub<T> threadStub = new ThreadStub<>(interfaceClass);
        threadStub.receive(listener);
        return threadStub.asInterface();
    }

}
