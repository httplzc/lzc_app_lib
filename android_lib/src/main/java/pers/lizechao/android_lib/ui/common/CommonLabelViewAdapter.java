package pers.lizechao.android_lib.ui.common;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import pers.lizechao.android_lib.ui.widget.LabelViewAdapter;

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
 * Date: 2018-08-12
 * Time: 14:03
 */
public class CommonLabelViewAdapter<T, BD extends ViewDataBinding> extends LabelViewAdapter {
    private final int layoutId;
    private final int BR_ID;
    private List<T> dataList=new ArrayList<>();
    private final Context context;

    public CommonLabelViewAdapter(Context context, int layoutId, int BR_ID, List<T> dataList) {
        this.layoutId = layoutId;
        this.BR_ID = BR_ID;
        this.dataList = dataList;
        this.context = context;
    }

    public CommonLabelViewAdapter(Context context, int layoutId, int BR_ID) {
        this.layoutId = layoutId;
        this.BR_ID = BR_ID;
        this.context = context;
    }

    @Override
    public View bindData(int position, ViewGroup viewGroup) {
        BD viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, viewGroup, false);
        viewDataBinding.setVariable(BR_ID, dataList.get(position));
        return viewDataBinding.getRoot();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public List<T> getDataList() {
        return dataList;
    }
}
