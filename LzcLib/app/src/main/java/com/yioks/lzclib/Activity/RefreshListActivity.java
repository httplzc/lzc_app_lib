package com.yioks.lzclib.Activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.yioks.lzclib.R;
import com.yioks.lzclib.View.ParentView;
import com.yioks.lzclib.View.ReFreshListViewParentView;
import com.yioks.lzclib.View.RefreshScrollParentViewBase;


/**
 * 列表刷新基础类
 * Created by ${User} on 2016/9/14 0014.
 */
public abstract class RefreshListActivity extends ReceiverTitleBaseActivity implements ReFreshListViewParentView.LoaddingMoreListener, RefreshScrollParentViewBase.onCenterReFreshListener
        , ParentView.ReFreshDataListener {
    protected ReFreshListViewParentView parentView;
    protected ListView listView;
    protected BaseAdapter listAdapter;
    protected boolean isMore = false;
    protected  int REQUEST_COUNT = 15;

    protected void initView() {
        parentView = (ReFreshListViewParentView) findViewById(R.id.parent_view);
        parentView.setOnCenterReFreshListener(this);
        parentView.setReFreshDataListener(this);
        parentView.setLoaddingMoreListener(this);
        listAdapter = getAdapter();
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onClick((int) id);
            }
        });
    }

    protected int GetPagerNumber() {
        int startPageNumber = 0;
        if (isMore) {
            startPageNumber = listAdapter.getCount() / 15;
        } else {
            startPageNumber = 0;
        }
        return startPageNumber;
    }

    protected abstract void GetData();

    protected abstract BaseAdapter getAdapter();

    protected abstract void onClick(int id);


    @Override
    protected void reFreshBackGround() {
        parentView.startload();
    }

    @Override
    public void onCenterReFresh() {
        isMore = false;
        GetData();
    }

    @Override
    public void loadMore() {
        isMore = true;
        GetData();
    }

    @Override
    public void refreshData() {
        isMore = false;
        parentView.setstaus(ParentView.Staus.Loading);
        GetData();
    }

    protected void onFailDeal() {
        if (isMore) {
            parentView.loaddingMoreComplete(false);
        } else {
            parentView.completeLoad(false);
        }

        if (listAdapter.getCount() == 0) {
            parentView.setstaus(ParentView.Staus.Error);
        }
    }


}
