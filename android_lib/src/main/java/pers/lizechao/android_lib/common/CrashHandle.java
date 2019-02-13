package pers.lizechao.android_lib.common;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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
 * Date: 2018/11/21 0021
 * Time: 13:43
 */
public class CrashHandle implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private List<CrashObserver> crashObservers = new ArrayList<>();
    private static final CrashHandle CRASH_HANDLE = new CrashHandle();

    public static CrashHandle getInstance() {
        return CRASH_HANDLE;
    }

    public void registerCrashObserver(CrashObserver crashObserver) {
        crashObservers.add(crashObserver);
    }

    public void unRegisterCrashObserver(CrashObserver crashObserver) {
        crashObservers.remove(crashObserver);
    }

    @Override
    public void uncaughtException(Thread t, Throwable ex) {
        ex.printStackTrace();
        Log.e("lzc", "", ex);
        for (CrashObserver crashObserver : crashObservers) {
            crashObserver.onCrash(t, ex);
        }
    }


    public interface CrashObserver {
        void onCrash(Thread t, Throwable ex);
    }

    /**
     * 初始化
     */
    public void init() {
        Thread.setDefaultUncaughtExceptionHandler(this);
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }
}
