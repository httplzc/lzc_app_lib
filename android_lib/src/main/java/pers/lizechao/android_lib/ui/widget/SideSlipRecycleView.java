package pers.lizechao.android_lib.ui.widget;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

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
 * Date: 2018-07-09
 * Time: 12:30
 * 可以侧滑的RecycleView
 */
public class SideSlipRecycleView extends BaseRecycleView {
    private final PointF downPoint = new PointF();
    private final PointF lastPoint = new PointF();
    private final PointF currentPoint = new PointF();
    //是否为侧滑状态
    private boolean isSideSlip = false;

    public SideSlipRecycleView(Context context) {
        super(context);

    }

    public SideSlipRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //根据滑动情况决定是否拦截事件
        currentPoint.set(ev.getX(), ev.getY());
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downPoint.set(ev.getX(), ev.getY());
            lastPoint.set(ev.getX(), ev.getY());
            isSideSlip = false;
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = downPoint.x - currentPoint.x;
            float dy = downPoint.y - currentPoint.y;
            lastPoint.set(ev.getX(), ev.getY());
            int mTouchScroll = 64;
            if (getLayoutManager().canScrollHorizontally() && Math.abs(dy) > Math.abs(dx) && Math.abs(dy) > mTouchScroll) {
                isSideSlip = true;
            } else if (getLayoutManager().canScrollVertically() && Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > mTouchScroll) {
                isSideSlip = true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return !isSideSlip && super.onInterceptTouchEvent(e);
    }


    boolean isOpen(int position) {
        SideSlipRecyclerViewAdapter slipRecyclerViewAdapter = (SideSlipRecyclerViewAdapter) getAdapter();
        return slipRecyclerViewAdapter.isOpen(position);
    }

    public void changeSideViewOpenOrClose(int position, boolean open) {
        SideSlipRecyclerViewAdapter.ViewHolderWrapper wrapper = (SideSlipRecyclerViewAdapter.ViewHolderWrapper) findViewHolderForAdapterPosition(position);
        SideSlipRecyclerViewAdapter slipRecyclerViewAdapter = (SideSlipRecyclerViewAdapter) getAdapter();
        slipRecyclerViewAdapter.setOpenOrClose(position, open);
        if (wrapper != null) {
            wrapper.slipAbleView.doScrollAnim(open);
        }
    }

}
