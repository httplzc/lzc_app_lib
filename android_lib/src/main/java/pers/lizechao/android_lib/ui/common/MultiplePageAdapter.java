package pers.lizechao.android_lib.ui.common;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with
 * ********************************************************************************
 * #         ___                     ________                ________             *
 * #       |\  \                   |\_____  \              |\   ____\             *
 * #       \ \  \                   \|___/  /|             \ \  \___|             *
 * #        \ \  \                      /  / /              \ \  \                *
 * #         \ \  \____                /  /_/__              \ \  \____           *
 * #          \ \_______\             |\________\             \ \_______\         *
 * #           \|_______|              \|_______|              \|_______|         *
 * #                                                                              *
 * ********************************************************************************
 * Date: 2018-08-04
 * Time: 10:51
 * 多种View混合的PagerAdapter
 */
public class MultiplePageAdapter extends PagerAdapter {
    private List<MultipleViewData> dataList = new ArrayList<>();
    private final Context context;

    public MultiplePageAdapter(Context context) {
        this.context = context;
    }

    public class MultipleViewData {
        public int layoutId;
        public int BR_ID;
        public Object objectData;
        ViewDataBinding viewDataBinding;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        MultipleViewData multipleViewData = dataList.get(position);
        if (multipleViewData.viewDataBinding == null) {
            multipleViewData.viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), multipleViewData.layoutId, container, false);
            multipleViewData.viewDataBinding.setVariable(multipleViewData.BR_ID, multipleViewData.objectData);
        }
        return dataList.get(position).viewDataBinding.getRoot();
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(dataList.get(position).viewDataBinding.getRoot());
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    public void setDataList(List<MultipleViewData> dataList) {
        this.dataList = dataList;
    }
}
