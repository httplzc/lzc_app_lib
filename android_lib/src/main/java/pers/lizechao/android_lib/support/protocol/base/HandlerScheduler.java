package pers.lizechao.android_lib.support.protocol.base;

import android.os.Handler;
import android.os.Message;

import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;

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
 * Time: 14:36
 */
public class HandlerScheduler extends Scheduler {

    private final Handler handler;

   public HandlerScheduler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public Disposable scheduleDirect(Runnable run, long delay, TimeUnit unit) {
        if (run == null) throw new NullPointerException("run == null");
        if (unit == null) throw new NullPointerException("unit == null");

        ScheduledRunnable scheduled = new ScheduledRunnable(handler, run);
        handler.postDelayed(scheduled, Math.max(0L, unit.toMillis(delay)));
        return scheduled;
    }

    @Override
    public Worker createWorker() {
        return new HandlerWorker(handler);
    }

    private static final class HandlerWorker extends Worker {
        private final Handler handler;

        private volatile boolean disposed;

        HandlerWorker(Handler handler) {
            this.handler = handler;
        }

        @Override
        public Disposable schedule(Runnable run, long delay, TimeUnit unit) {
            if (run == null) throw new NullPointerException("run == null");
            if (unit == null) throw new NullPointerException("unit == null");

            if (disposed) {
                return Disposables.disposed();
            }


            ScheduledRunnable scheduled = new ScheduledRunnable(handler, run);

            Message message = Message.obtain(handler, scheduled);
            message.obj = this;

            handler.sendMessageDelayed(message, Math.max(0L, unit.toMillis(delay)));

            if (disposed) {
                handler.removeCallbacks(scheduled);
                return Disposables.disposed();
            }

            return scheduled;
        }

        @Override
        public void dispose() {
            disposed = true;
            handler.removeCallbacksAndMessages(this /* token */);
        }

        @Override
        public boolean isDisposed() {
            return disposed;
        }
    }

    private static final class ScheduledRunnable implements Runnable, Disposable {
        private final Handler handler;
        private final Runnable delegate;

        private volatile boolean disposed;

        ScheduledRunnable(Handler handler, Runnable delegate) {
            this.handler = handler;
            this.delegate = delegate;
        }

        @Override
        public void run() {
            try {
                delegate.run();
            } catch (Throwable t) {
                IllegalStateException ie =
                  new IllegalStateException("Fatal Exception thrown on ThreadTarget.", t);
                Thread thread = Thread.currentThread();
                thread.getUncaughtExceptionHandler().uncaughtException(thread, ie);
            }
        }

        @Override
        public void dispose() {
            disposed = true;
            handler.removeCallbacks(this);
        }

        @Override
        public boolean isDisposed() {
            return disposed;
        }
    }
}

