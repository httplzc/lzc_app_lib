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
    private final int layoutId;
    private final int BR_ID;
    private List<T> dataList = new ArrayList<>();
    private final Context context;

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
        T data = dataList.get(position);
        holder.viewDataBinding.setVariable(BR_ID, data);
        setExtraData((BD) holder.viewDataBinding,data);
    }
    public void setExtraData(BD viewBinding,T data)
    {

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

   public class ViewHolder extends RecyclerView.ViewHolder {
       public final BD viewDataBinding;

        ViewHolder(BD viewDataBinding) {
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
