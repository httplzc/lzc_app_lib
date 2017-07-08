package com.yioks.lzclib.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ${User} on 2017/4/15 0015.
 */

public abstract class PagerGroupAdapter {
    protected Context context;

    public PagerGroupAdapter(Context context) {
        this.context = context;
    }

    //返回总数量
    public abstract int getCount();


    //实例化view
    public abstract Object instantiateItem(ViewGroup viewGroup, int position);

    //销毁view
    public abstract void  destroyItem(ViewGroup viewGroup, View view);


    //获取对应数据项
    public abstract Object getItem(int position);


    //获取数据对应position
    public abstract int getPosition(Object object);

    //能前进
    public abstract boolean canNext(int position);

    //能后退
    public abstract boolean canLast(int position);
}
