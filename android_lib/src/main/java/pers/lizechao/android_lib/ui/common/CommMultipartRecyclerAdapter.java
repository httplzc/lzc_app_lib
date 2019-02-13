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
public abstract class CommMultipartRecyclerAdapter extends RecyclerView.Adapter<CommMultipartRecyclerAdapter.ViewHolder> {
    private List<?> dataList = new ArrayList<>();
    private final Context context;

    public CommMultipartRecyclerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public abstract int getItemViewType(int position);

    public abstract int getLayoutId(int type);

    public abstract int getBR(int type);

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), getLayoutId(viewType), parent, false);
        return new ViewHolder(viewDataBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommMultipartRecyclerAdapter.ViewHolder holder, int position) {
        Object data = dataList.get(position);
        holder.viewDataBinding.setVariable(getBR(getItemViewType(position)), data);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    protected class ViewHolder extends RecyclerView.ViewHolder {
        protected final ViewDataBinding viewDataBinding;

        ViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            this.viewDataBinding = viewDataBinding;
        }

    }

    public void setDataList(List<?> dataList) {
        this.dataList = dataList;
    }

    public List<?> getDataList() {
        return dataList;
    }

}
