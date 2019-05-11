package pers.lizechao.android_lib.ui.common;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
 * Date: 2018-08-03
 * Time: 16:20
 */
public class CommRecyclerAdapter<T, BD extends ViewDataBinding> extends RecyclerView.Adapter<CommRecyclerAdapter.ViewHolder> {
    protected final int layoutId;
    protected final int BR_ID;
    protected List<T> dataList = new ArrayList<>();
    protected Context context;


    public CommRecyclerAdapter(int layoutId, int BR_ID, Context context) {
        this.layoutId = layoutId;
        this.BR_ID = BR_ID;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BD viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, parent, false);
        return new ViewHolder(viewDataBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommRecyclerAdapter.ViewHolder holder, int position) {
        setExtraData((BD) holder.viewDataBinding, dataList.get(position));
    }

    public void setExtraData(BD viewDataBinding, T data) {
        viewDataBinding.setVariable(BR_ID, data);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final BD viewDataBinding;

        public ViewHolder(BD viewDataBinding) {
            super(viewDataBinding.getRoot());
            this.viewDataBinding = viewDataBinding;
        }

    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public List<T> getDataList() {
        return dataList;
    }


}
