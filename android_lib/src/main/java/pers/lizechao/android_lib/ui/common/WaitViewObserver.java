package pers.lizechao.android_lib.ui.common;

import android.app.Activity;

import java.lang.ref.WeakReference;

import io.reactivex.observers.DisposableSingleObserver;
import pers.lizechao.android_lib.R;
import pers.lizechao.android_lib.utils.DialogUtil;

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
 * Date: 2018-08-06
 * Time: 11:07
 */
public abstract class WaitViewObserver<T> extends DisposableSingleObserver<T> {
    private final WeakReference<Activity> activityWeakReference;
    private final String text;

    public WaitViewObserver(Activity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
        text = activity.getResources().getString(R.string.wait_view_default_text);
    }


    public WaitViewObserver(Activity activity, String text) {
        this.activityWeakReference = new WeakReference<>(activity);
        this.text = text;
    }


    @Override
    public void onError(Throwable e) {
        DialogUtil.dismissDialog();
    }

    @Override
    protected void onStart() {
        if (activityWeakReference.get() != null)
            DialogUtil.showDialog(activityWeakReference.get(), text, this::dispose);
    }


    @Override
    public void onSuccess(T t) {
        DialogUtil.dismissDialog();
    }

    public void stop() {
        super.dispose();
        DialogUtil.dismissDialog();
    }
}
