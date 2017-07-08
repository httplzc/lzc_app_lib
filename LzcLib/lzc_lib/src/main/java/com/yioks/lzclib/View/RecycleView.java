package com.yioks.lzclib.View;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.yioks.lzclib.Adapter.RecycleViewHeadAdapter;
import com.yioks.lzclib.Data.ScreenData;

/**
 * Created by ${User} on 2017/4/7 0007.
 */

public class RecycleView extends RecyclerView {
    private PointF downPointF = new PointF();
    private PointF currentPointF = new PointF();
    private RecycleViewHeadAdapter recycleViewHeadAdapter = new RecycleViewHeadAdapter();
    ;

    public RecycleView(Context context) {
        super(context);
    }

    public RecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setItemClickListener(RecycleViewHeadAdapter.onItemClickListener onItemClickListener) {
        recycleViewHeadAdapter.setItemClickListener(onItemClickListener);
    }

    public void addHeadView(View view,RecycleViewHeadAdapter.WrapperViewHolder wrapperViewHolder) {
        if (recycleViewHeadAdapter.headContain(view))
            return;
        recycleViewHeadAdapter.addHeadView(view,wrapperViewHolder);
        recycleViewHeadAdapter.notifyItemInserted(recycleViewHeadAdapter.getHeadCount());
    }

    public void addHeadView(View view, int position,RecycleViewHeadAdapter.WrapperViewHolder wrapperViewHolder) {
        if (recycleViewHeadAdapter.headContain(view))
            return;
        recycleViewHeadAdapter.addHeadView(view, position,wrapperViewHolder);
        recycleViewHeadAdapter.notifyDataSetChanged();
    }

    public int getHeadCount() {
        return recycleViewHeadAdapter == null ? 0 : recycleViewHeadAdapter.getHeadCount();
    }

    public int getFootCount() {
        return recycleViewHeadAdapter == null ? 0 : recycleViewHeadAdapter.getFootCount();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        adapter = recycleViewHeadAdapter.setAdapter(adapter);
        super.setAdapter(adapter);
    }

    public RecycleViewHeadAdapter getWrapperAdapter() {
        return recycleViewHeadAdapter;
    }

    public void removeHeadView(View view) {
        recycleViewHeadAdapter.removeHeadView(view);
        recycleViewHeadAdapter.notifyDataSetChanged();
    }


    public void addFootViewNoNotify(View view,RecycleViewHeadAdapter.WrapperViewHolder wrapperViewHolder) {
        recycleViewHeadAdapter.addFootView(view,wrapperViewHolder);
    }


    public void addFootView(View view,RecycleViewHeadAdapter.WrapperViewHolder wrapperViewHolder) {
        if (recycleViewHeadAdapter.footContain(view))
            return;

        recycleViewHeadAdapter.addFootView(view,wrapperViewHolder);
        //   recycleViewHeadAdapter.notifyDataSetChanged();

        this.post(new Runnable() {
            @Override
            public void run() {
                recycleViewHeadAdapter.notifyItemInserted(recycleViewHeadAdapter.getItemCount());
            }
        });
    }

//    public void addFootView(View view, int position) {
//        if (recycleViewHeadAdapter.getHeadViewList().contains(view))
//            return;
//        recycleViewHeadAdapter.addFootView(view);
//        this.post(new Runnable() {
//            @Override
//            public void run() {
//                recycleViewHeadAdapter.notifyItemInserted(recycleViewHeadAdapter.getItemCount());
//            }
//        });
//
//    }

    public void removeFootView(View view) {
        if (!recycleViewHeadAdapter.footContain(view))
            return;
        int removePosition = getHeadCount() + getWrapperAdapter().getAdapter().getItemCount() + getWrapperAdapter().getIndexOfFoot(view);
        recycleViewHeadAdapter.removeFootView(view);
        //    recycleViewHeadAdapter.notifyDataSetChanged();

        recycleViewHeadAdapter.notifyItemRemoved(removePosition);
     //   recycleViewHeadAdapter.notifyItemRangeChanged(removePosition, recycleViewHeadAdapter.getItemCount());
        // recycleViewHeadAdapter.notifyDataSetChanged();
    }

    public void removeFootViewNoNotify(View view) {
        if (!recycleViewHeadAdapter.footContain(view))
            return;
        recycleViewHeadAdapter.removeFootView(view);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        currentPointF.x = ev.getX();
        currentPointF.y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downPointF.x = currentPointF.x;
                downPointF.y = currentPointF.y;
                //   Log.i("lzc", "down");
                return false;
            case MotionEvent.ACTION_MOVE:
                float dy = currentPointF.y - downPointF.y;
                float dx = currentPointF.x - downPointF.x;

//                Log.i("lzc", "MOVE_true" + Math.abs(dy) + "---" + Math.abs(dx));
                if (Math.abs(dy) > Math.abs(dx) && Math.abs(dy) > 10 * ScreenData.density) {
                    //  Log.i("lzc", "MOVE");
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                return false;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return super.onTouchEvent(e);
    }
}
