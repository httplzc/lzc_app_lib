package pers.lizechao.android_lib.ui.common;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import java.util.HashMap;

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
 * Date: 2019/5/11 0011
 * Time: 15:48
 */
public abstract class CommMultiRecyclerAdapter<T> extends CommRecyclerAdapter<T, ViewDataBinding> {
    private HashMap<Integer, CommRecyclerAdapter<T, ? extends ViewDataBinding>> commRecyclerAdapters;


    public CommMultiRecyclerAdapter() {
        super(0, 0, null);
    }

    public CommRecyclerAdapter<T, ? extends ViewDataBinding> getCacheAdapter(int type) {
        CommRecyclerAdapter<T, ? extends ViewDataBinding> commRecyclerAdapter = commRecyclerAdapters.get(type);
        if (commRecyclerAdapter == null) {
            commRecyclerAdapter = createAdapterByType(type);
            commRecyclerAdapters.put(type, commRecyclerAdapter);
        }
        return commRecyclerAdapter;
    }

    //选用那个Adapter
    public abstract CommRecyclerAdapter<T, ? extends ViewDataBinding> createAdapterByType(int type);

    @Override
    public abstract int getItemViewType(int position);

    @NonNull
    @Override
    public CommRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CommRecyclerAdapter<T, ? extends ViewDataBinding> commRecyclerAdapter = getCacheAdapter(viewType);
        return commRecyclerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull CommRecyclerAdapter.ViewHolder holder, int position) {
        T data = dataList.get(position);
        CommRecyclerAdapter commRecyclerAdapter = commRecyclerAdapters.get(getItemViewType(position));
        commRecyclerAdapter.setExtraData(holder.viewDataBinding, data);
    }

}
