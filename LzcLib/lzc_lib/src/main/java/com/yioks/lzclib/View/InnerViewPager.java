package com.yioks.lzclib.View;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;

import com.yioks.lzclib.Data.ScreenData;

/**
 * Created by ${User} on 2017/4/10 0010.
 */

public class InnerViewPager extends ViewPager {
    private PointF downPointF = new PointF();
    private PointF currentPointF = new PointF();
    private RefreshScrollParentViewBase refreshScrollParentViewBase;
    RecycleView recycleView = null;
    ;

    public InnerViewPager(Context context) {
        super(context);
    }

    public InnerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            LoopViewPager loopViewPager = (LoopViewPager) getParent().getParent();
            loopViewPager.cancelAutoEvent();
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Log.i("lzc", "onTouchEvent" + ev.getAction());
        super.onTouchEvent(ev);
        currentPointF.x = ev.getX();
        currentPointF.y = ev.getY();
        ViewParent viewParent = getParent();
        LoopViewPager loopViewPager = (LoopViewPager) getParent().getParent();
        if (recycleView == null || refreshScrollParentViewBase == null) {
            while (!(viewParent instanceof RefreshScrollParentViewBase)) {
                if (viewParent instanceof RecycleView)
                    recycleView = (RecycleView) viewParent;
                viewParent = viewParent.getParent();
            }
            refreshScrollParentViewBase = (RefreshScrollParentViewBase) viewParent;
        }


        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downPointF.x = currentPointF.x;
                downPointF.y = currentPointF.y;
               // Log.i("lzc", "downeeeeee");
                changeIntercept(false);
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = currentPointF.y - downPointF.y;
                float dx = currentPointF.x - downPointF.x;
               // Log.i("lzc", "MOVEeeee");
                if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > 10 * ScreenData.density) {
                    refreshScrollParentViewBase.backCurrent();
                    if ((getCurrentItem() == 0 && dx < 0) || (getCurrentItem() == getAdapter().getCount() - 1 && dx > 0)) {
                        return false;
                    }
                    return true;
                } else if (Math.abs(dx) < Math.abs(dy) && Math.abs(dy) > 10 * ScreenData.density) {
                    changeIntercept(true);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                changeIntercept(true);
                loopViewPager.startAutoEvent();
                break;
        }
        return true;
    }


    private void changeIntercept(boolean cancel) {
        recycleView.requestDisallowInterceptTouchEvent(!cancel);
        refreshScrollParentViewBase.requestDisallowInterceptTouchEvent(!cancel);
        refreshScrollParentViewBase.setDispath(cancel);
    }
}
