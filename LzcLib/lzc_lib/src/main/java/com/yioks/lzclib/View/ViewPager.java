package com.yioks.lzclib.View;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by ${User} on 2017/5/17 0017.
 */

public class ViewPager extends android.support.v4.view.ViewPager {
    private PointF downPointF = new PointF();
    private PointF currentPointF = new PointF();

    public ViewPager(Context context) {
        super(context);
    }

    public ViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downPointF.set(ev.getX(), ev.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                currentPointF.set(ev.getX(), ev.getY());
                float dx = Math.abs(currentPointF.x - downPointF.x);
                float dy = Math.abs(currentPointF.y - downPointF.y);
                if (dx > dy && dx > 20)
                    return true;
                break;

        }
        return false;
    }

}
