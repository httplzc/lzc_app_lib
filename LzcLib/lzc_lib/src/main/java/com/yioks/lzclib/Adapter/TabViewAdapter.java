package com.yioks.lzclib.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by ${User} on 2017/5/5 0005.
 */

public abstract class TabViewAdapter  {
    protected Context context;

    public TabViewAdapter(Context context) {
        this.context = context;
    }

    public abstract View bindData(int position, ViewGroup viewGroup);
    public abstract boolean setChoice(int position, boolean choice, View view, List<Integer>choiceList);
    public abstract int  getCount();
}
