package pers.lizechao.android_lib.ui.layout;

import android.databinding.ViewDataBinding;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import pers.lizechao.android_lib.ui.common.CommRecyclerAdapter;
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
 * Date: 2018-08-06
 * Time: 14:17
 * 用于管理列表页面
 * 列表页面需要包含
 * {@link pers.lizechao.android_lib.ui.widget.RefreshParent} 刷新组件
 * {@link pers.lizechao.android_lib.ui.widget.HeadFootRecycleView} 列表组件
 * {@link pers.lizechao.android_lib.ui.widget.PageStateView} 页面状态组件
 */
public class ListPagerManager<T> implements SingleObserver<List<T>> {
    @NonNull
    protected final RefreshParent refreshParent;
    @NonNull
    protected final HeadFootRecycleView headFootRecycleView;
    @Nullable
    protected final PageStateView pageStateView;
    protected final CommRecyclerAdapter<T, ? extends ViewDataBinding> recyclerAdapter;
    private int pagerCount = 15;
    protected GetDataCallBack getDataCallBack;
    protected boolean isMore;
    protected boolean stateShowCoverHead = true;

    protected boolean enableHead = true;
    protected boolean enableFoot = true;

    public ListPagerManager(@NonNull RefreshParent refreshParent, @NonNull HeadFootRecycleView headFootRecycleView, @Nullable PageStateView pageStateView,
                            CommRecyclerAdapter<T, ? extends ViewDataBinding> recyclerAdapter) {
        this.refreshParent = refreshParent;
        this.headFootRecycleView = headFootRecycleView;
        this.pageStateView = pageStateView;
        this.recyclerAdapter = recyclerAdapter;
        initView();
    }

    private void initView() {
        refreshParent.setRefreshCallBack(new RefreshParent.RefreshCallBack() {
            @Override
            public void onRefresh() {
                isMore = false;
                if (getDataCallBack != null)
                    getDataCallBack.refreshData();
            }

            @Override
            public void onLoadingMore() {
                isMore = true;
                if (getDataCallBack != null)
                    getDataCallBack.loadMoreData(getPagerNumber(), pagerCount);
            }
        });
        if (pageStateView != null)
            pageStateView.setRefreshNotify(() -> {
                isMore = false;
                pageStateView.setState(PageStateView.State.Loading);
                if (getDataCallBack != null)
                    getDataCallBack.refreshData();
            });
    }


    public ListPagerManager(@NonNull RefreshParent refreshParent, @NonNull HeadFootRecycleView headFootRecycleView,
                            CommRecyclerAdapter<T, ? extends ViewDataBinding> recyclerAdapter) {
        this(refreshParent, headFootRecycleView, null, recyclerAdapter);
    }


    private int getPagerNumber() {
        return recyclerAdapter.getItemCount() / pagerCount+1;
    }

    public void setPagerCount(int pagerCount) {
        this.pagerCount = pagerCount;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    protected boolean checkNoMoreData(List<T> dataList) {
        return dataList.size() < pagerCount;
    }

    @Override
    public void onSuccess(List<T> dataList) {
        if (pageStateView != null)
            pageStateView.setState(PageStateView.State.Normal);
        if (!isMore) {
            if (dataList.size() == 0) {
                if (pageStateView != null) {
                    pageStateView.setState(PageStateView.State.Null);
                }

                recyclerAdapter.getDataList().clear();
                recyclerAdapter.notifyDataSetChanged();
            } else {
                recyclerAdapter.setDataList(dataList);
                recyclerAdapter.notifyDataSetChanged();
            }
            refreshParent.refreshFinish(true);
            refreshParent.setNoMoreData(checkNoMoreData(dataList));
            changePageStateView();
        } else {
            recyclerAdapter.getDataList().addAll(dataList);
            recyclerAdapter.notifyItemRangeInserted(recyclerAdapter.getItemCount() - dataList.size(), dataList.size());
            refreshParent.loadingMoreFinish(true, checkNoMoreData(dataList));
        }
    }

    private void changePageStateView() {
        if (pageStateView == null)
            return;
        if (!stateShowCoverHead && headFootRecycleView.getHeadCount() > 1 && pageStateView.getState() != PageStateView.State.Normal) {

            refreshParent.setOnHeadPullListener(new RefreshParent.OnPullListener() {
                @Override
                public void onPull(int height) {
                    int headTotal = 0;
                    int width = headFootRecycleView.getWidth();
                    for (View view : headFootRecycleView.getHeadViewList()) {
                        headTotal += view.getHeight();
                        width = Math.max(width, view.getWidth());
                    }
                    Rect rect = pageStateView.getCoverRect();
                    if(rect==null)
                        rect=new Rect(0,headTotal,width,headFootRecycleView.getHeight());
                    rect.top = headTotal;
                    pageStateView.setCoverRect(rect);
                }
            });
//            refreshParent.setEnableHead(false);
//            refreshParent.setEnableFoot(false);
            refreshParent.getOnHeadPullListener().onPull(0);
        } else {
//            refreshParent.setEnableHead(enableHead);
//            refreshParent.setEnableFoot(enableFoot);
            pageStateView.setContentView(headFootRecycleView);
            refreshParent.setOnHeadPullListener(null);
        }
    }

    @Override
    public void onError(Throwable e) {
        if (isMore)
            refreshParent.loadingMoreFinish(false, false);
        else {
            if (recyclerAdapter.getItemCount() == 0 && pageStateView != null) {
                pageStateView.setState(PageStateView.State.Error);
                changePageStateView();
            }
            refreshParent.refreshFinish(false);
        }
    }


    public interface GetDataCallBack {
        void refreshData();

        void loadMoreData(int page, int pageCount);
    }

    public void startLoad() {
        isMore = false;
        if (pageStateView != null && pageStateView.getState() == PageStateView.State.Loading) {
            refreshParent.toRefresh();
        } else
            refreshParent.animToRefresh();
    }

    public void setGetDataCallBack(GetDataCallBack getDataCallBack) {
        this.getDataCallBack = getDataCallBack;
    }

    public int getPagerCount() {
        return pagerCount;
    }

    public void setStateShowCoverHead(boolean stateShowCoverHead) {
        this.stateShowCoverHead = stateShowCoverHead;
    }

    public void setEnableFoot(boolean enableFoot) {
        this.enableFoot = enableFoot;
    }

    public void setEnableHead(boolean enableHead) {
        this.enableHead = enableHead;
    }
}
