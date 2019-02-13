package pers.lizechao.android_lib.support.protocol.bus;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import io.reactivex.Scheduler;
import pers.lizechao.android_lib.common.DestroyListener;
import pers.lizechao.android_lib.support.protocol.base.ThreadReceiver;

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
 * Date: 2018-09-11
 * Time: 9:40
 */
public class BusStubReceiver<T> extends ThreadReceiver<T> {
    private final String tag;

    public BusStubReceiver(Class<T> interfaceClass, String tag, @Nullable LifecycleOwner lifecycleOwner) {
        super(interfaceClass);
        this.tag = tag;
        init(lifecycleOwner);
    }

    public BusStubReceiver(Class<T> interfaceClass, T listener, String tag, @Nullable LifecycleOwner lifecycleOwner) {
        super(interfaceClass, listener);
        this.tag = tag;
        init(lifecycleOwner);
    }

    public BusStubReceiver(Class<T> interfaceClass, @Nullable Scheduler scheduler, String tag, @Nullable LifecycleOwner lifecycleOwner) {
        super(interfaceClass, scheduler);
        this.tag = tag;
        init(lifecycleOwner);
    }

    public BusStubReceiver(Class<T> interfaceClass, T listener, @Nullable Scheduler scheduler, String tag, @Nullable LifecycleOwner lifecycleOwner) {
        super(interfaceClass, listener, scheduler);
        this.tag = tag;
        init(lifecycleOwner);
    }

    private final Observer<StubData> observer = this::callBackStubData;

    private void init(@Nullable LifecycleOwner lifecycleOwner) {
        LiveDataBus.Observable<StubData> observable = LiveDataBus.get().with(BusUtils.getTagName(tag, interfaceClass),StubData.class);
        if (lifecycleOwner != null) {
            observable.observe(lifecycleOwner, observer);
            lifecycleOwner.getLifecycle().addObserver(new DestroyListener(this::unRegister));
        } else {
            observable.observeForever(observer);
        }

    }

    public void unRegister() {
        unReceive();
        LiveDataBus.get().with(BusUtils.getTagName(tag, interfaceClass), StubData.class).removeObserver(observer);
    }
}
