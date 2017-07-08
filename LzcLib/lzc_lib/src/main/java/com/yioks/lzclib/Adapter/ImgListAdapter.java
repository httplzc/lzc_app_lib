package com.yioks.lzclib.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ${User} on 2017/5/5 0005.
 */

public abstract class ImgListAdapter {
    protected Context context;

    public ImgListAdapter(Context context) {
        this.context = context;
    }

    public abstract View bindData(int position, ViewGroup viewGroup);

    public abstract int getCount();
}
