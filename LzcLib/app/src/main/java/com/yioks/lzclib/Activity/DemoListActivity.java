package com.yioks.lzclib.Activity;

import android.os.Bundle;

import com.yioks.lzclib.Adapter.ListAdapter;
import com.yioks.lzclib.Adapter.MylistAdapter;
import com.yioks.lzclib.Data.Bean;
import com.yioks.lzclib.Data.DomeBean;
import com.yioks.lzclib.Helper.ParamsBuilder;
import com.yioks.lzclib.Helper.ResolveDataHelper;
import com.yioks.lzclib.Helper.ResolveDateHelperImp;
import com.yioks.lzclib.Helper.onResolveDataFinish;

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
        ResolveDataHelper resolveDataHelper=new ResolveDateHelperImp(context,new DomeBean(), new ParamsBuilder(context).build());
        resolveDataHelper.setOnResolveDataFinish(new onResolveDataFinish() {
            @Override
            public void resolveFinish(Object data) {
                onSuccessDo(data);
            }

            @Override
            public void onFail(String code) {
                onFailDeal();
            }
        });
        resolveDataHelper.StartGetData();
    }

    @Override
    public ListAdapter getAdapter() {
        return mylistAdapter;
    }

    @Override
    public void onClick(int id) {

    }
}
