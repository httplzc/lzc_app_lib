package com.yioks.lzclib.Adapter;

import android.content.Context;

import com.yioks.lzclib.Data.Bean;
import com.yioks.lzclib.View.RecycleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${User} on 2017/4/7 0007.
 */

public abstract class RecycleListAdapter<T extends Bean> extends RecycleView.Adapter {
    public List<T> list =new ArrayList<>();
    public Context context;

    public RecycleListAdapter(Context context) {
        this.context = context;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
