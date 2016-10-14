package com.yioks.lzclib.Activity;

import android.os.Bundle;

import com.yioks.lzclib.Adapter.ListAdapter;
import com.yioks.lzclib.Adapter.MylistAdapter;
import com.yioks.lzclib.Data.Bean;

/**
 * Created by ${User} on 2016/10/11 0011.
 */
public class DemoListActivity extends RefreshListActivity<Bean> {
    private MylistAdapter mylistAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleState();
        bindTitle(true, "", -1);
        mylistAdapter=new MylistAdapter(this);
        initView();
    }

    @Override
    public void GetData() {
        
    }

    @Override
    public ListAdapter getAdapter() {
        return mylistAdapter;
    }

    @Override
    public void onClick(int id) {

    }
}
