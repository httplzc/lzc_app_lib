package pers.lizechao.android_lib.ui.common;

import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import pers.lizechao.android_lib.R;
import pers.lizechao.android_lib.ui.manager.paging.HeadRecyclerViewPageCoverObserver;
import pers.lizechao.android_lib.ui.manager.paging.ListDataRequest;
import pers.lizechao.android_lib.ui.manager.paging.ListPageObserver;
import pers.lizechao.android_lib.ui.manager.paging.PageNumberListDataRequest;
import pers.lizechao.android_lib.ui.manager.paging.PageNumberRequest;
import pers.lizechao.android_lib.ui.manager.paging.PageStateViewListPageObserver;
import pers.lizechao.android_lib.ui.manager.paging.RecycleViewListPageObserver;
import pers.lizechao.android_lib.ui.widget.HeadFootRecycleView;
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
 * Date: 2018-09-10
 * Time: 9:13
 */
public abstract class BaseListActivity<T extends ViewDataBinding, LD> extends BaseRequestActivity<T> implements RefreshParent.RefreshCallBack, ListPageObserver<LD> {
    protected CommRecyclerAdapter<LD, ?> adapter;
    protected ListDataRequest<LD> listDataRequest;
    protected HeadFootRecycleView headFootRecycleView;
    protected RefreshParent refreshParent;
    protected PageStateView pageStateView;
    protected int pageSize = 15;
    protected final int pageNumberStart = 1;
    protected boolean recycleViewHeadCover = true;
    private boolean isFirstLoad=true;


    @Override
    protected void initExtraView() {
        super.initExtraView();
        adapter = createAdapter();
        headFootRecycleView = findViewById(R.id.headFootRecycleView);
        refreshParent = findViewById(R.id.refreshParent);
        pageStateView = findViewById(R.id.pageStateView);
        refreshParent.setEnableHead(true);
        refreshParent.setEnableFoot(true);
        refreshParent.setRefreshCallBack(this);
        initRecycleView(headFootRecycleView);
        listDataRequest = getListRequest();
        initListDataObserver();
    }

    protected void initListDataObserver() {
        listDataRequest.registerManager(this);
        listDataRequest.registerManager(new RecycleViewListPageObserver<>(refreshParent, headFootRecycleView, adapter));
        if (pageStateView != null) {
            pageStateView.setState(PageStateView.State.Loading);
            listDataRequest.registerManager(new PageStateViewListPageObserver<>(pageStateView));
            pageStateView.setRefreshNotify(() -> {
                        pageStateView.setState(PageStateView.State.Loading);
                        requestData(false);
                    }
            );
        }
        if (!recycleViewHeadCover && pageStateView != null) {
            listDataRequest.registerManager(new HeadRecyclerViewPageCoverObserver<>(false,
                    headFootRecycleView, refreshParent, pageStateView));
        }
    }

    @Override
    protected void requestData(boolean useCache) {
        super.requestData(useCache);
        listStartToRequest(useCache);
    }

    protected void listStartToRequest(boolean useCache) {
        listDataRequest.loadRefreshData(useCache);
    }

    protected void initRecycleView(HeadFootRecycleView recycleView) {
        recycleView.setLayoutManager(createLayoutManager());
        recycleView.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration = createItemDecoration();
        if (itemDecoration != null)
            recycleView.addItemDecoration(itemDecoration);
    }

    protected ListDataRequest<LD> getListRequest() {
        return new PageNumberListDataRequest.Builder<LD>()
                .setAdapter(adapter)
                .setPageNumberStart(pageNumberStart)
                .setPageSize(pageSize)
                .setPageNumberRequest(getPageNumberRequest())
                .build();
    }

    protected abstract CommRecyclerAdapter<LD, ?> createAdapter();

    protected abstract PageNumberRequest<LD> getPageNumberRequest();

    //额外请求状态处理
    @Override
    protected void requestError(Throwable e) {
    }

    @Override
    protected void requestSucceed() {
    }

    @Override
    protected void requestLoading() {

    }

    protected RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(this);
    }

    @Nullable
    protected RecyclerView.ItemDecoration createItemDecoration() {
        return null;
    }

    @Override
    public void onRefresh() {
        listDataRequest.loadRefreshData(isFirstLoad);
    }

    @Override
    public void onLoadingMore() {
        listDataRequest.loadMoreData(false);
    }

    public void refreshPage() {
        if (listDataRequest.getDisposable() != null)
            listDataRequest.getDisposable().dispose();
        if (pageStateView.getState() == PageStateView.State.Loading || refreshParent.isRequesting()) {
            onRefresh();
        } else {
            if (pageStateView.getState() != PageStateView.State.Normal)
                pageStateView.setState(PageStateView.State.Loading);
            refreshParent.animToRefresh();
        }
    }

    @Override
    public void onRefreshSucceed(List<LD> list, boolean finish) {
        isFirstLoad=false;
    }

    @Override
    public void onLoadMoreSucceed(List<LD> list, boolean finish) {

    }

    @Override
    public void onRefreshError(Throwable e) {

    }

    @Override
    public void onLoadMoreError(Throwable e) {

    }
}
