package com.yioks.lzclib.View;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Scroller;

import com.yioks.lzclib.Adapter.PagerGroupAdapter;
import com.yioks.lzclib.Data.ScreenData;
import com.yioks.lzclib.R;

/**
 * Created by ${User} on 2017/4/14 0014.
 */

public class PagingGroup extends ViewGroup {
    private View topView;
    public View currentView;
    private View bottomView;
    private Scroller scroller;
    private PagerGroupAdapter adapter;
    private int currentPosition;
    private Object data;
    private PagerScrollEvent pagerScrollEvent;
    private PagingGroup.onLoadingPageListener onLoadingPageListener;
    private final int itemHeight = ScreenData.heightPX;


    private enum AnimSate {ToNext, ToLast, None}

    private AnimSate animState = AnimSate.None;

    public PagingGroup(Context context) {
        super(context);
        init(context);
    }

    public PagerGroupAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(PagerGroupAdapter adapter) {
        this.adapter = adapter;
        initView();
    }

    public PagingGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    public void init(Context context) {
        scroller = new Scroller(context, new AccelerateInterpolator());
    }

    public PagingGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        return animState != AnimSate.None;
    }

    private void initView() {
        if (adapter == null)
            return;
        if (adapter.getCount() != 0) {
            setCurrentView((View) adapter.instantiateItem(this, 0), 0);
            this.addView(currentView);
            requestLayout();
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        Log.i("lzc", "onlayout" + l + "---" + t + "---" + r + "---" + b);
        if (currentView != null) {
            currentView.layout(l, 0, r, itemHeight);
        }

        if (topView != null) {
            topView.layout(l, -itemHeight, r, 0);
        }
        if (bottomView != null) {
            bottomView.layout(l, itemHeight, r, 2 * itemHeight);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int height = MeasureSpec.getSize(heightMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (currentView == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        currentView.measure(widthMeasureSpec, heightMeasureSpec);
        if (topView != null)
            topView.measure(widthMeasureSpec, heightMeasureSpec);
        if (bottomView != null)
            bottomView.measure(widthMeasureSpec, heightMeasureSpec);
        int width = currentView.getMeasuredWidth();
        int height = currentView.getMeasuredHeight() + (topView != null ? topView.getMeasuredHeight() : 0) + (bottomView != null ? bottomView.getMeasuredHeight() : 0);
        setMeasuredDimension(width, height);
    }

    //滑动到下一章节
    public void scrollToNext() {
        if (adapter == null)
            return;
        if (getCurrentPosition() == adapter.getCount() - 1) {
            return;
        }
        View newView = (ViewGroup) adapter.instantiateItem(this, getCurrentPosition() + 1);
        if (topView != null) {
            adapter.destroyItem(this, topView);
            topView = null;
        }
        if (bottomView != null) {
            adapter.destroyItem(this, bottomView);
        }
        bottomView = newView;
        this.addView(bottomView);
        requestLayout();

        this.post(new Runnable() {
            @Override
            public void run() {
                animState = AnimSate.ToNext;
                scroller.startScroll(0, getScrollY(), 0, itemHeight, 300);
//                Log.i("lzc", "startScroll");
                invalidate();
            }
        });

    }

    public void notifyDataSetChanged() {
        if (currentView == null) {
            initView();
            return;
        }
        int nextPosition = getAdapter().getPosition(data);
        currentPosition = nextPosition;
        enablePagerScroll();
    }

    public void scrollToLast() {
//        Log.i("lzc", "getscrollY" + getScrollY());
        if (adapter == null)
            return;
        if (getCurrentPosition() == 0) {
            return;
        }
        View newView = (ViewGroup) adapter.instantiateItem(this, getCurrentPosition() - 1);
        if (bottomView != null) {
            adapter.destroyItem(this, bottomView);
            bottomView = null;
        }
        if (topView != null) {
            adapter.destroyItem(this, topView);
        }
        topView = newView;
        this.addView(topView);
        requestLayout();
        this.post(new Runnable() {
            @Override
            public void run() {
//                Log.i("lzc", "topView" + (topView.getVisibility() == VISIBLE) + "---" + topView.getHeight());
                animState = AnimSate.ToLast;
                scroller.startScroll(0, getScrollY(), 0, -itemHeight, 300);
                invalidate();
            }
        });
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            this.scrollTo(scroller.getCurrX(), scroller.getCurrY());
//            Log.i("lzc", "computeScrollOffset" + scroller.getCurrY());
            invalidate();
        } else {
            scroller.abortAnimation();
            endScroll();

        }
    }

    private void endScroll() {
        if (animState == AnimSate.ToNext) {
            topView = currentView;
            setCurrentView(bottomView, getCurrentPosition() + 1);
            bottomView = null;
            post(new Runnable() {
                @Override
                public void run() {
                    requestLayout();
                    scrollTo(0, 0);
                }
            });
        } else if (animState == AnimSate.ToLast) {
            bottomView = currentView;
            setCurrentView(topView, getCurrentPosition() - 1);
            topView = null;
            post(new Runnable() {
                @Override
                public void run() {
                    scrollTo(0, 0);
                    requestLayout();
                    Log.i("lzc", "scrollTo" + getScrollY() + "---" + currentView.getLeft() + "----" + currentView.getTop() + "----" + currentView.getRight()
                            + "----" + currentView.getBottom() + "---" + bottomView + "---" + (currentView == topView));

                }
            });
//            Log.i("lzc", "endScroll" + getScrollY() + "---" + topView.getLeft() + "----" + topView.getTop() + "----" + topView.getRight() + "----" + topView.getBottom());
        }

        animState = AnimSate.None;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }


    public View getCurrentView() {
        return currentView;
    }


    public interface PagerScrollEvent {
        void setCanLoadNext(boolean canLoadNext);

        void setCanLoadLast(boolean canLoadLast);

        void completeLoadCurrent(boolean succeed, boolean isHead);

        void setOnLoadingPageListener(PagingGroup.onLoadingPageListener onLoadingPageListener);
    }

    //设置当前的滑动
    private void setCurrentView(View currentView, int position) {
        this.currentView = currentView;
        pagerScrollEvent = (PagerScrollEvent) currentView.findViewById(R.id.pagerScrollView);
        pagerScrollEvent.setOnLoadingPageListener(onLoadingPageListener);
        currentPosition = position;
        data = getAdapter().getItem(currentPosition);
        enablePagerScroll();

    }

    //设置滑动到底部和顶部禁止滑动
    private void enablePagerScroll() {
        pagerScrollEvent.setCanLoadNext(adapter.canNext(currentPosition));
        pagerScrollEvent.setCanLoadLast(adapter.canLast(currentPosition));
    }

    //完成加载
    public void completeLoad(boolean isHead, boolean isSucceed) {
        if (pagerScrollEvent != null) {
            pagerScrollEvent.completeLoadCurrent(isSucceed, isHead);
        }
    }

    public PagingGroup.onLoadingPageListener getOnLoadingPageListener() {
        return onLoadingPageListener;
    }

    public void setOnLoadingPageListener(PagingGroup.onLoadingPageListener onLoadingPageListener) {
        this.onLoadingPageListener = onLoadingPageListener;
    }

    public interface onLoadingPageListener {
        void loadingNext();

        void loadingLast();
    }


}
