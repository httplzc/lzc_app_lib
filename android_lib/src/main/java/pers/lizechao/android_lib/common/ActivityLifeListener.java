package pers.lizechao.android_lib.common;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

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
 * Date: 2018-09-13
 * Time: 9:27
 */
public class ActivityLifeListener implements LifecycleObserver {
    private final Runnable runnable;
    private Lifecycle.Event event;

    public ActivityLifeListener(Lifecycle.Event event, Runnable runnable) {
        this.runnable = runnable;
        this.event = event;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy() {
        if (event != Lifecycle.Event.ON_DESTROY)
            return;
        if (runnable != null)
            runnable.run();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    void onAny() {
        if (event != Lifecycle.Event.ON_ANY)
            return;
        if (runnable != null)
            runnable.run();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate() {
        if (event != Lifecycle.Event.ON_CREATE)
            return;
        if (runnable != null)
            runnable.run();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause() {
        if (event != Lifecycle.Event.ON_PAUSE)
            return;
        if (runnable != null)
            runnable.run();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume() {
        if (event != Lifecycle.Event.ON_RESUME)
            return;
        if (runnable != null)
            runnable.run();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart() {
        if (event != Lifecycle.Event.ON_START)
            return;
        if (runnable != null)
            runnable.run();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop() {
        if (event != Lifecycle.Event.ON_STOP)
            return;
        if (runnable != null)
            runnable.run();
    }


}
