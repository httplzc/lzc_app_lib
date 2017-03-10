package com.yioks.lzclib.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.yioks.lzclib.Data.Bean;

/**
 * Created by ${User} on 2016/10/11 0011.
 */
public class MylistAdapter extends ListAdapter<Bean> {
    public MylistAdapter(Context context) {
        super(context);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
