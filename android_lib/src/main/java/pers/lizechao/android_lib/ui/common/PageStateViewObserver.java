package pers.lizechao.android_lib.ui.common;

import io.reactivex.observers.DisposableSingleObserver;
import pers.lizechao.android_lib.ui.widget.PageStateView;

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
public abstract class PageStateViewObserver<T> extends DisposableSingleObserver<T> {
    private final PageStateView pageStateView;

    public PageStateViewObserver(PageStateView pageStateView) {
        this.pageStateView = pageStateView;
    }

    @Override
    public void onError(Throwable e) {
        pageStateView.setState(PageStateView.State.Error);
    }

    @Override
    protected void onStart() {
        pageStateView.setState(PageStateView.State.Loading);
    }


    @Override
    public void onSuccess(T t) {
        pageStateView.setState(PageStateView.State.Normal);
    }

}
