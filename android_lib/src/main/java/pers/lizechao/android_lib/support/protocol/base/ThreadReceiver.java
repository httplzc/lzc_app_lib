package pers.lizechao.android_lib.support.protocol.base;

import android.support.annotation.Nullable;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created with
 * ********************************************************************************
 * #         ___                     ________                ________             *
 * #       |\  \                   |\_____  \              |\   ____\             *
 * #       \ \  \                   \|___/  /|             \ \  \___|             *
 * #        \ \  \                      /  / /              \ \  \                *
 * #         \ \  \____                /  /_/__              \ \  \____           *
 * #          \ \_______\             |\________\             \ \_______\         *
 * #           \|_______|              \|_______|              \|_______|         *
 * #                                                                              *
 * ********************************************************************************
 * Date: 2018-06-30
 * Time: 15:59
 * 支持线程切换的Receiver
 */
public class ThreadReceiver<T> extends StubReceiver<T> {
    @Nullable
    private Scheduler scheduler;

    public ThreadReceiver(Class<T> interfaceClass) {
        super(interfaceClass);
        initScheduler(interfaceClass);
    }

    public ThreadReceiver(Class<T> interfaceClass, T listener) {
        super(interfaceClass, listener);
        initScheduler(interfaceClass);
    }

    public ThreadReceiver(Class<T> interfaceClass, @Nullable Scheduler scheduler) {
        super(interfaceClass);
        this.scheduler = scheduler;
    }

    public ThreadReceiver(Class<T> interfaceClass, T listener, @Nullable Scheduler scheduler) {
        super(interfaceClass, listener);
        this.scheduler = scheduler;
    }

    private void initScheduler(Class<T> interfaceClass) {
        ThreadReceiverTarget threadReceiverTarget = interfaceClass.getAnnotation(ThreadReceiverTarget.class);
        if (threadReceiverTarget != null) {
            switch (threadReceiverTarget.value()) {
                case AndroidMain:
                    scheduler = AndroidSchedulers.mainThread();
                    break;
                case Io:
                    scheduler = Schedulers.io();
                    break;
                case Computation:
                    scheduler = Schedulers.computation();
                    break;
                case NewThread:
                    scheduler = Schedulers.newThread();
                    break;
            }
        }
    }

    @Override
    public void scheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }


    @Override
    public void callBackStubData(StubData stubData) {
        Runnable runnable = () -> super.callBackStubData(stubData);
        if (scheduler != null) {
            scheduler.scheduleDirect(runnable);
        } else
            runnable.run();

    }
}
