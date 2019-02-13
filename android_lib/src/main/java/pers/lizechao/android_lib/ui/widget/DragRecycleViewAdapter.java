package pers.lizechao.android_lib.ui.widget;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import static pers.lizechao.android_lib.ui.widget.DragRecycleView.FLAG_DOWN;
import static pers.lizechao.android_lib.ui.widget.DragRecycleView.FLAG_LEFT;
import static pers.lizechao.android_lib.ui.widget.DragRecycleView.FLAG_RIGHT;
import static pers.lizechao.android_lib.ui.widget.DragRecycleView.FLAG_UP;

/**
 * Created by Lzc on 2017/4/5 0005.
 */

public abstract class DragRecycleViewAdapter extends RecyclerView.Adapter {
    private int invisiblePosition;


    public abstract void moveData(int fromPosition, int toPosition);

    public  int getScrollOrientationFlag()
    {
        return  FLAG_UP | FLAG_DOWN | FLAG_LEFT | FLAG_RIGHT;
    }


    public abstract void bindHolderData(@NonNull final RecyclerView.ViewHolder holder, int position);

    public void setInvisiblePosition(int invisiblePosition) {
        this.invisiblePosition = invisiblePosition;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        bindHolderData(holder, position);
        if (invisiblePosition == position) {
            holder.itemView.setVisibility(View.INVISIBLE);
            holder.itemView.setClickable(false);
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setClickable(true);
        }
    }


}

