package com.yioks.lzclib.View;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by ${User} on 2016/9/12 0012.
 */
public class ScrollRecycleView  extends RecyclerView {
    public ScrollRecycleView(Context context) {
        super(context);
    }

    public ScrollRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollRecycleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, expandSpec);
    }
}
