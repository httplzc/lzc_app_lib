package com.yioks.lzclib.Fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yioks.lzclib.Adapter.ListAdapter;
import com.yioks.lzclib.Data.Bean;
import com.yioks.lzclib.R;
import com.yioks.lzclib.View.ParentView;
import com.yioks.lzclib.View.ReFreshListViewParentView;
import com.yioks.lzclib.View.RefreshScrollParentViewBase;

import java.util.List;

public abstract class RefreshListFragment<T extends Bean> extends Fragment implements ReFreshListViewParentView.LoaddingMoreListener, RefreshScrollParentViewBase.onCenterReFreshListener
        , ParentView.ReFreshDataListener {
    public ReFreshListViewParentView parentView;
    public ListView listView;
    public ListAdapter listAdapter;
    public boolean isMore = false;
    public int REQUEST_COUNT = 15;

    public void initView(View view) {
        parentView = (ReFreshListViewParentView) view.findViewById(R.id.parent_view);
        parentView.setOnCenterReFreshListener(this);
        parentView.setReFreshDataListener(this);
        parentView.setLoaddingMoreListener(this);
        listAdapter = getAdapter();
        listView = (ListView) view.findViewById(R.id.listview);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onClick((int) id);
            }
        });
    }

    public int GetPagerNumber() {
        int startPageNumber = 0;
        if (isMore) {
            startPageNumber = listAdapter.getCount() / REQUEST_COUNT;
        } else {
            startPageNumber = 0;
        }
        return startPageNumber;
    }

    public abstract void GetData();

    public abstract ListAdapter getAdapter();

    public abstract void onClick(int id);


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
            listAdapter.getList().clear();
            listAdapter.getList().addAll(teamList);
            parentView.completeLoad(true);
            parentView.setHaveFinishLoadMore(teamList.size() < REQUEST_COUNT);
        } else {
            listAdapter.getList().addAll(teamList);
            parentView.loaddingMoreComplete(teamList.size() < REQUEST_COUNT,true);
        }
        parentView.setstaus(ParentView.Staus.Normal);
        listAdapter.notifyDataSetChanged();
    }

    //数据请求失败调用
    public void onFailDeal() {
        if (isMore) {
            parentView.loaddingMoreComplete(false,false);
        } else {
            parentView.completeLoad(false);
        }

        if (listAdapter.getCount() == 0) {
            parentView.setstaus(ParentView.Staus.Error);
        }
    }
}
