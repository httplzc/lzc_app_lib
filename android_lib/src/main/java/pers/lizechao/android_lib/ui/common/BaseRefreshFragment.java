package pers.lizechao.android_lib.ui.common;

import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.view.View;

import pers.lizechao.android_lib.R;
import pers.lizechao.android_lib.ui.widget.PageStateView;
import pers.lizechao.android_lib.ui.widget.RefreshParent;

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
 * Date: 2018-08-23
 * Time: 15:50
 * 需要 RefreshParent 与 PageStateView
 */
public abstract class BaseRefreshFragment<T extends ViewDataBinding> extends BaseRequestFragment<T> implements RefreshParent.RefreshCallBack {
    @Nullable
    protected RefreshParent refreshParent;
    @Nullable
    protected PageStateView pageStateView;


    @Override
    protected void initExtraView(View root) {
        super.initExtraView(root);
        initMainView(root);
    }

    protected void initMainView(View root) {
        refreshParent = root.findViewById(R.id.refreshParent);
        pageStateView = root.findViewById(R.id.pageStateView);
        if (refreshParent != null) {
            refreshParent.setEnableFoot(false);
            refreshParent.setRefreshCallBack(this);
        }

        if (pageStateView != null) {
            pageStateView.setRefreshNotify(this::onRefresh);
            pageStateView.setState(PageStateView.State.Loading);
        }
    }

    @Override
    public void onRefresh() {
        if (pageStateView != null && pageStateView.getState() != PageStateView.State.Normal)
            pageStateView.setState(PageStateView.State.Loading);
        requestData(false);
    }

    @Override
    public void onLoadingMore() {

    }


    @Override
    protected void requestError(Throwable e) {
        if (pageStateView != null && pageStateView.getState() != PageStateView.State.Normal)
            pageStateView.setState(PageStateView.State.Error);
        if (refreshParent != null)
            refreshParent.refreshFinish(false);
    }

    @Override
    protected void requestSucceed() {
        if (pageStateView != null)
            pageStateView.setState(PageStateView.State.Normal);
        if (refreshParent != null)
            refreshParent.refreshFinish(true);
    }

    @Override
    protected void requestLoading() {
        if (pageStateView != null)
            pageStateView.setState(PageStateView.State.Loading);
    }
}
