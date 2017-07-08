package com.yioks.lzclib.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ${User} on 2017/4/5 0005.
 */

public abstract class DragRecycleViewAdapter extends RecyclerView.Adapter {
    private List<onClickItemListener> onClickItemListeners = new ArrayList<>();
    private Set<Integer> invisiblePositions = new HashSet<>();

    @Override
    public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    public abstract void moveData(int fromPosition, int toPosition);

    public void addInvisiblePosition(Integer integer) {
        invisiblePositions.add(integer);
    }

    public void removeInvisiblePosition(Integer integer) {
        invisiblePositions.remove(integer);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        // holder.itemView.setVisibility(View.VISIBLE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (onClickItemListener onClickItemListener : onClickItemListeners) {
                    onClickItemListener.onItemClick(v, holder.getAdapterPosition());
                }

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                for (onClickItemListener onClickItemListener : onClickItemListeners) {
                    onClickItemListener.onLongClick(v, holder.getAdapterPosition());
                }
                return true;
            }
        });
        if (invisiblePositions.contains(Integer.valueOf(position))) {
            holder.itemView.setVisibility(View.INVISIBLE);
            holder.itemView.setClickable(false);
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setClickable(true);
        }
    }

    @Override
    abstract public int getItemCount();

    public interface onClickItemListener {
        void onItemClick(View view, int position);

        void onLongClick(View view, int position);
    }


    public void removeOnClickItemListener(DragRecycleViewAdapter.onClickItemListener onClickItemListener) {
        onClickItemListeners.remove(onClickItemListener);
    }

    public void addOnClickItemListener(DragRecycleViewAdapter.onClickItemListener onClickItemListener) {
        onClickItemListeners.add(onClickItemListener);
    }
}

