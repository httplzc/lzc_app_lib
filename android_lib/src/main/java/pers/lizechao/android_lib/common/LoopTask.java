package pers.lizechao.android_lib.common;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.TimeUnit;


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
 * Date: 2018-08-18
 * Time: 11:46
 */
public class LoopTask {
    private final long timeSpan;
    private boolean isRun = false;
    private final Handler handler;
    private Runnable runnable;
    private final Runnable scheduleTask = new Runnable() {
        @Override
        public void run() {
            if (isRun) {
                if (runnable != null)
                    runnable.run();
                handler.postDelayed(this, timeSpan);
            }
        }
    };

    public LoopTask(long timeSpan, Handler handler) {
        this.timeSpan = timeSpan;
        this.handler = handler;
    }

    public LoopTask(long timeSpan, Looper looper) {
        this.timeSpan = timeSpan;
        this.handler = new Handler(looper);
    }

    public LoopTask(long timeSpan, TimeUnit timeUnit, Looper looper) {
        this.timeSpan = timeUnit.toMillis(timeSpan);
        this.handler = new Handler(looper);
    }

    public void setAutoStart(boolean start) {
        if (start == isRun)
            return;
        if (start) {
            handler.postDelayed(scheduleTask, timeSpan);
            isRun = true;
        } else {
            handler.removeCallbacksAndMessages(scheduleTask);
            isRun = false;
        }
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }


}
