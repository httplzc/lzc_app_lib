package com.yioks.lzclib.Adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import com.yioks.lzclib.Data.Bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${User} on 2016/10/11 0011.
 */
public abstract class ListAdapter <T extends Bean> extends BaseAdapter {
    public List<T> list =new ArrayList<>();
    public Context context;

    public ListAdapter(Context context) {
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
