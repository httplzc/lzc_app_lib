package pers.lizechao.android_lib.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

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
 * Date: 2018-07-05
 * Time: 10:10
 * 基础RecycleView 增强实现了点击事件
 */
public class BaseRecycleView extends RecyclerView {
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    @Override
    public void onChildAttachedToWindow(View child) {
        super.onChildAttachedToWindow(child);
        child.setOnClickListener(v -> {
            int position = BaseRecycleView.this.getChildAdapterPosition(v);
            if (position != -1 && onItemClickListener != null) {
                onItemClickListener.onItemClick(v, position);
            }
        });

        child.setOnLongClickListener(v -> {
            int position = BaseRecycleView.this.getChildAdapterPosition(v);
            if (position != -1 && onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(v, position);
                return true;
            }
            return false;
        });
    }

    public BaseRecycleView(Context context) {
        super(context);
    }

    public BaseRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }



    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }
}
