package com.yioks.lzclib.Fragment;

import android.support.v4.app.Fragment;
import android.view.View;

import com.yioks.lzclib.Adapter.RecycleListAdapter;
import com.yioks.lzclib.Data.Bean;
import com.yioks.lzclib.R;
import com.yioks.lzclib.View.ParentView;
import com.yioks.lzclib.View.RecycleView;
import com.yioks.lzclib.View.RefreshRecycleViewParentView;
import com.yioks.lzclib.View.RefreshScrollParentViewBase;

import java.util.List;


/**
 * 列表刷新基础类
 * Created by ${User} on 2016/9/14 0014.
 */
public abstract class RefreshRecycleListFragment<T extends Bean> extends Fragment implements RefreshRecycleViewParentView.LoaddingMoreListener, RefreshScrollParentViewBase.onCenterReFreshListener
        , ParentView.ReFreshDataListener {
    public RefreshRecycleViewParentView parentView;
    public RecycleView recyclerView;
    public RecycleListAdapter recycleListAdapter;
    public boolean isMore = false;
    public int REQUEST_COUNT = 10;

    public void initView(View rootView) {
        parentView = (RefreshRecycleViewParentView) rootView.findViewById(R.id.parent_view);
        parentView.setOnCenterReFreshListener(this);
        parentView.setReFreshDataListener(this);
        parentView.setLoaddingMoreListener(this);
        recycleListAdapter = getAdapter();
        recyclerView = (RecycleView) rootView.findViewById(R.id.recycle_view);
        recyclerView.setAdapter(recycleListAdapter);
    }

    public int GetPagerNumber() {
        int startPageNumber = 0;
        if (isMore) {
            startPageNumber = recycleListAdapter.getList().size() / REQUEST_COUNT;
        } else {
            startPageNumber = 0;
        }
        return startPageNumber;
    }

    public abstract void GetData();

    public abstract RecycleListAdapter getAdapter();


    //手动刷新回调
    @Override
    public void onCenterReFresh() {
        isMore = false;
        GetData();
    }

    //加载更多
    @Override
    public void loadMore() {
        isMore = true;
        GetData();
    }

    //加载失败页面的刷新
    @Override
    public void refreshData() {
        isMore = false;
        parentView.setstaus(ParentView.Staus.Loading);
        GetData();
    }

    //数据请求成功
    public void onSuccessDo(Object data) {
        List<T> teamList = (List<T>) data;
        if (!isMore) {
            if (teamList.size() == 0) {
                parentView.completeLoad(true);
                parentView.setstaus(ParentView.Staus.Null);
                return;
            }
            if (recyclerView.getWrapperAdapter().getFootCount() != 0)
                recyclerView.setAdapter(recycleListAdapter);
            recycleListAdapter.getList().clear();
            recycleListAdapter.getList().addAll(teamList);
            parentView.completeLoad(true);
            parentView.setHaveFinishLoadMore(teamList.size() < REQUEST_COUNT);
            recyclerView.getWrapperAdapter().notifyDataSetChanged();
        } else {
            recycleListAdapter.getList().addAll(teamList);
            parentView.loaddingMoreComplete(teamList.size() < REQUEST_COUNT, true);
            recyclerView.getWrapperAdapter().notifyItemRangeInserted(recyclerView.getWrapperAdapter().getItemCount() - teamList.size(), teamList.size());
        }
        parentView.setstaus(ParentView.Staus.Normal);
    }

    //数据请求失败调用
    public void onFailDeal() {
        if (isMore) {
            parentView.loaddingMoreComplete(false, false);
        } else {
            parentView.completeLoad(false);
        }

        if (recycleListAdapter.getList().size() == 0) {
            parentView.setstaus(ParentView.Staus.Error);
        }
    }


}
