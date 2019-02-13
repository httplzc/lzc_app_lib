package pers.lizechao.android_lib.ui.manager;


import android.app.Activity;
import android.app.ProgressDialog;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


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
 * Date: 2018-07-02
 * Time: 16:18
 */
public class WaitViewManager {
    private ProgressDialog progressDialog;
    private static final WaitViewManager instance = new WaitViewManager();

    public static WaitViewManager getInstance() {
        return instance;
    }


    /**
     * 加载提示框
     */
    public ProgressDialog showDialog(Activity context, String content, @Nullable Runnable cancelRun) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        progressDialog = new ProgressDialog(context);
        progressDialog.setOnDismissListener(dialog -> {
            if (cancelRun != null)
                cancelRun.run();
        });
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(content);
        progressDialog.show();
        return progressDialog;
    }


    /**
     * 取消对话框显示
     */
    public void dismissDialog() {
        try {
            if (progressDialog != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static <T> Observer<T> wrapperSubscriber(Activity activity, String msg, Observer<T> observer) {
        WeakReference<Activity> weakReference = new WeakReference<>(activity);
        return new Observer<T>() {
            @Override
            public void onSubscribe(Disposable d) {
                WaitViewManager.getInstance().showDialog(weakReference.get(), msg, d::dispose);
                observer.onSubscribe(d);
            }

            @Override
            public void onNext(T t) {
                observer.onNext(t);
            }

            @Override
            public void onError(Throwable e) {
                WaitViewManager.getInstance().dismissDialog();
                observer.onError(e);

            }

            @Override
            public void onComplete() {
                WaitViewManager.getInstance().dismissDialog();
                observer.onComplete();
            }
        };
    }

    public boolean isShow() {
        return progressDialog != null && progressDialog.isShowing();
    }

}
