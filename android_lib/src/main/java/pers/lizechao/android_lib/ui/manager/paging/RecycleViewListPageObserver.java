package pers.lizechao.android_lib.ui.manager.paging;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;

import java.util.List;

import pers.lizechao.android_lib.ui.common.CommRecyclerAdapter;
import pers.lizechao.android_lib.ui.widget.HeadFootRecycleView;
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
 * Date: 2018-09-07
 * Time: 10:18
 */
public class RecycleViewListPageObserver<T> implements ListPageObserver<T> {
    @NonNull
    protected final RefreshParent refreshParent;
    @NonNull
    protected final HeadFootRecycleView headFootRecycleView;
    @NonNull
    protected final CommRecyclerAdapter<T, ? extends ViewDataBinding> recyclerAdapter;


    public RecycleViewListPageObserver(@NonNull RefreshParent refreshParent,
                                       @NonNull HeadFootRecycleView headFootRecycleView,
                                       @NonNull CommRecyclerAdapter<T, ? extends ViewDataBinding> recyclerAdapter) {
        this.refreshParent = refreshParent;
        this.headFootRecycleView = headFootRecycleView;
        this.recyclerAdapter = recyclerAdapter;
    }

    @Override
    public void onRefreshSucceed(List<T> dataList,boolean finish) {
        recyclerAdapter.setDataList(dataList);
        recyclerAdapter.notifyDataSetChanged();
        refreshParent.refreshFinish(true);
        refreshParent.setNoMoreData(finish);
    }

    @Override
    public void onLoadMoreSucceed(List<T> dataList,boolean finish) {
        recyclerAdapter.getDataList().addAll(dataList);
        recyclerAdapter.notifyItemRangeInserted(recyclerAdapter.getItemCount() - dataList.size(), dataList.size());
        refreshParent.loadingMoreFinish(true, finish);
    }

    @Override
    public void onRefreshError(Throwable e) {
        refreshParent.refreshFinish(false);
    }

    @Override
    public void onLoadMoreError(Throwable e) {
        refreshParent.loadingMoreFinish(false, false);
    }
}
