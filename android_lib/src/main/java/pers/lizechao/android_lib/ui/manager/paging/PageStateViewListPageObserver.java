package pers.lizechao.android_lib.ui.manager.paging;

import android.support.annotation.NonNull;

import java.util.List;

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
 * Date: 2018-09-07
 * Time: 11:21
 */
public class PageStateViewListPageObserver<T> implements ListPageObserver<T> {
    @NonNull
    protected final PageStateView pageStateView;


    public PageStateViewListPageObserver(@NonNull PageStateView pageStateView) {
        this.pageStateView = pageStateView;
    }

    @Override
    public void onRefreshSucceed(List<T> dataList, boolean finish) {
        if (dataList.size() == 0) {
            pageStateView.setState(PageStateView.State.Null);
        } else {
            pageStateView.setState(PageStateView.State.Normal);
        }
    }

    @Override
    public void onLoadMoreSucceed(List<T> list, boolean finish) {

    }

    @Override
    public void onRefreshError(Throwable e) {
        if (pageStateView.getState() == PageStateView.State.Loading) {
            pageStateView.setState(PageStateView.State.Error);
        }
    }

    @Override
    public void onLoadMoreError(Throwable e) {

    }

}
