package pers.lizechao.android_lib.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Lzc on 2017/3/25 0025.
 */

public class ScrollView extends android.widget.ScrollView{
    private onScrollListener onScrollListener;
    public ScrollView(Context context) {
        super(context);
    }

    public ScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface onScrollListener
    {
        void onScroll();
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if(onScrollListener!=null)
            onScrollListener.onScroll();
    }



    public ScrollView.onScrollListener getOnScrollListener() {
        return onScrollListener;
    }

    public void setOnScrollListener(ScrollView.onScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }
}
