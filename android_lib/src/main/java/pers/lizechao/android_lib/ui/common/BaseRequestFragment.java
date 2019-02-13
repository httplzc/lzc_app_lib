package pers.lizechao.android_lib.ui.common;

import android.arch.lifecycle.Observer;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.view.View;

import com.annimon.stream.function.Supplier;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import pers.lizechao.android_lib.ui.manager.DataBindRequestManager;

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
 * Date: 2018-09-05
 * Time: 10:56
 * 用于进入后需要请求数据的页面。支持多个请求
 */
public abstract class BaseRequestFragment<T extends ViewDataBinding> extends BaseFragment<T> {
    protected DataBindRequestManager dataBindRequestManager;


    @Override
    protected void initExtraView(View root) {
        super.initExtraView(root);
        dataBindRequestManager = new DataBindRequestManager(viewBind, this);
    }

    @Override
    protected void requestData(boolean useCache) {
        Completable request = dataBindRequestManager.getRequest(useCache);
        if (request != null) {
            request.subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(Disposable d) {
                    addDisposable(d);
                }

                @Override
                public void onComplete() {
                    requestSucceed();
                }

                @Override
                public void onError(Throwable e) {
                    requestError(e);
                }
            });
        } else
            requestSucceed();

    }


    protected <D> void registerDataRequest(@NonNull Supplier<Single<D>> supplier, int BR_ID) {
        dataBindRequestManager.registerDataRequest(supplier, BR_ID);
    }

    protected <D> void registerDataRequest(@NonNull Supplier<Single<D>> supplier, Observer<D> observer) {
        dataBindRequestManager.registerDataRequest(supplier, observer);
    }


    protected abstract void requestError(Throwable e);

    protected abstract void requestSucceed();

    protected abstract void requestLoading();

}
