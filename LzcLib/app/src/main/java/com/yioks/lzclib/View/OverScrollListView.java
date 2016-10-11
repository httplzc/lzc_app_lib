package com.yioks.lzclib.View;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;

import com.yioks.lzclib.R;

import java.lang.reflect.Field;

/**
 * Created by ${User} on 2016/9/1 0001.
 */
public class OverScrollListView extends ListView {
    private float lastY;
    private View headview;
    private View footview;
    private Context context;
    private float downY;
    protected Scroller scroller;
    private ValueAnimator valueAnimator;
    private ValueAnimator valueAnimator2;
    private ValueAnimator overscrollAnimator;
    private VelocityTracker velocityTracker;
    private boolean flingOverScrollEnable = true;
    private boolean dragOverScrollEnable = true;
    private boolean dragOverScrollHeadEnable = true;
    private boolean dragOverScrollFootEnable = true;

    private int bottomHeight = 0;
    private final static float radio = 0.45f;
    private final static long back_time = 500;

    public OverScrollListView(Context context) {
        super(context);
        this.context = context;
        initData();

    }

    private void initData() {
        scroller = new Scroller(context, new OvershootInterpolator());
        this.setOverScrollMode(OVER_SCROLL_NEVER);
        this.setVerticalScrollBarEnabled(false);
        initVelocity();
    }

    private void initVelocity() {
        velocityTracker = VelocityTracker.obtain();
    }


    public OverScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initData();
        initattrs(attrs);
    }

    public OverScrollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initData();
        initattrs(attrs);
    }

    private void initattrs(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.OverScrollListView);
        flingOverScrollEnable = typedArray.getBoolean(R.styleable.OverScrollListView_flingOverScrollEnable_lv, true);
        dragOverScrollEnable = typedArray.getBoolean(R.styleable.OverScrollListView_dragOverScrollEnable_lv, true);
        dragOverScrollHeadEnable = typedArray.getBoolean(R.styleable.OverScrollListView_dragOverScrollHeadEnable_lv, true);
        dragOverScrollFootEnable = typedArray.getBoolean(R.styleable.OverScrollListView_dragOverScrollFootEnable_lv, true);
        typedArray.recycle();
    }

    @Override
    protected void onDetachedFromWindow() {
        try {
            if (velocityTracker != null)
                velocityTracker.recycle();
            velocityTracker = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDetachedFromWindow();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        headview = new View(context);
        this.addHeaderView(headview, null, false);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        headview.setLayoutParams(layoutParams);
//        headview.setBackgroundResource(R.color.blue);

        footview = new View(context);
        LayoutParams layoutParamsfoot = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        this.addFooterView(footview, null, false);
        footview.setLayoutParams(layoutParamsfoot);
//        footview.setBackgroundResource(R.color.orange);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (velocityTracker == null) {
            initVelocity();
        }
        velocityTracker.addMovement(ev);
        ViewParent viewParent = getParent();
        if (viewParent instanceof RefreshScrollParentViewBase) {
            RefreshScrollParentViewBase refreshScrollParentViewBase = (RefreshScrollParentViewBase) viewParent;
            if (refreshScrollParentViewBase.reFreshSatus != RefreshScrollParentViewBase.ReFreshSatus.NORMAL) {
                return super.onTouchEvent(ev);
            }
        }
        if (!dragOverScrollEnable) {
            return super.onTouchEvent(ev);
        }
        float eventY = ev.getY();
        ViewGroup.LayoutParams layoutParamsTop = headview.getLayoutParams();
        ViewGroup.LayoutParams layoutParamsFoot = footview.getLayoutParams();
        if (valueAnimator != null) {
            valueAnimator.removeAllUpdateListeners();
        }
        if (valueAnimator2 != null) {
            valueAnimator2.removeAllUpdateListeners();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = eventY;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isFirstItemVisible() || layoutParamsTop.height != 0) {
                    if (lastY != downY) {
                        if (layoutParamsTop.height != 0 || eventY - lastY > 0) {

                            layoutParamsTop.height += (eventY - lastY) * radio;
                            if (layoutParamsTop.height < 0) {
                                layoutParamsTop.height = 0;
                            }
                            if (!dragOverScrollHeadEnable) {
                                layoutParamsTop.height = 0;
                            }
                            headview.setLayoutParams(layoutParamsTop);
                            headview.invalidate();
                            if (eventY - lastY < 0) {

                                Class aClass = this.getClass();
                                while (!aClass.getName().equals(AbsListView.class.getName())) {
                                    aClass = aClass.getSuperclass();
                                }
                                try {

                                    int activePointerIndex = ev.findPointerIndex(ev.getPointerId(0));
                                    int y = (int) ev.getY(activePointerIndex);
                                    Field mMotionYField = aClass.getDeclaredField("mMotionY");
                                    mMotionYField.setAccessible(true);
                                    mMotionYField.set(this, y);
                                    mMotionYField.setAccessible(false);

                                    Field field = aClass.getDeclaredField("mLastY");
                                    field.setAccessible(true);
                                    field.set(this, y);
                                    field.setAccessible(false);
                                } catch (NoSuchFieldException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                                lastY = eventY;
                                return true;
                            }
                            lastY = eventY;
                            boolean temp = super.onTouchEvent(ev);
                            return temp;
                        }
                    }
                }
                if (isLastItemVisible() || layoutParamsFoot.height != 0) {
                    Log.i("lzc", "over_scroll");
                    if (lastY != downY) {
                        if (eventY - lastY < 0 || layoutParamsFoot.height != 0) {
                            layoutParamsFoot.height += (lastY - eventY) * radio;
                            if (layoutParamsFoot.height < 0) {
                                layoutParamsFoot.height = 0;
                            }
                            if (!dragOverScrollFootEnable) {
                                layoutParamsFoot.height = 0;
                            }
                            footview.setLayoutParams(layoutParamsFoot);
                            footview.invalidate();
                            lastY = eventY;
                            this.setSelection(this.getBottom());
                            return super.onTouchEvent(ev);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                back();
                break;

        }
        lastY = eventY;
        return super.onTouchEvent(ev);
    }

    public void back() {
        ViewGroup.LayoutParams layoutParams = headview.getLayoutParams();
        if (layoutParams.height != 0) {
            valueAnimator = ValueAnimator.ofInt(layoutParams.height, 0);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ViewGroup.LayoutParams layoutParams = headview.getLayoutParams();
                    layoutParams.height = (int) animation.getAnimatedValue();
                    headview.setLayoutParams(layoutParams);
                    headview.invalidate();
                }
            });
            valueAnimator.setDuration(back_time);
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.start();
        }
        ViewGroup.LayoutParams layoutParams2 = footview.getLayoutParams();
        if (layoutParams2.height != 0) {
            valueAnimator2 = ValueAnimator.ofInt(layoutParams2.height, 0);
            valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    ViewGroup.LayoutParams layoutParams = footview.getLayoutParams();
                    Log.i("lzc","layoutParams2.height"+layoutParams.height);
                    layoutParams.height = (int) animation.getAnimatedValue();
                    footview.setLayoutParams(layoutParams);
                    footview.invalidate();
                }
            });
            valueAnimator2.setDuration(back_time);
            valueAnimator2.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator2.start();
        }
    }

    private boolean isFirstItemVisible() {
        final Adapter adapter = this.getAdapter();
        if (null == adapter || adapter.isEmpty()) {
            return true;
        } else {
            Log.i("lzc","getchildcont"+this.getCount());
            if (this.getFirstVisiblePosition() <=0) {
                final View firstVisibleChild = this.getChildAt(0);
                if (firstVisibleChild != null) {
                    Log.i("lzc", "isFirstVisable" + firstVisibleChild.getTop());
                    return firstVisibleChild.getTop() == 0;
                }
            }
        }

        return false;
    }

    private boolean isLastItemVisible() {
        final Adapter adapter = this.getAdapter();
        if (null == adapter || adapter.isEmpty()) {
            return true;
        } else {
            if (isLessData()) {
                return true;
            }
            final int lastItemPosition = this.getCount() - 1;
            final int lastVisiblePosition = this.getLastVisiblePosition();
            if (lastVisiblePosition >= lastItemPosition - 1) {
                final int childIndex = lastVisiblePosition - this.getFirstVisiblePosition();
                final View lastVisibleChild = this.getChildAt(childIndex);
                if (lastVisibleChild != null) {
                    if (lastVisibleChild.getBottom() <= this.getHeight()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isLessData() {
        ListView scrollViewChild = this;
        if (scrollViewChild.getChildCount() != 0) {
            if (scrollViewChild.getLastVisiblePosition() == scrollViewChild.getAdapter().getCount() - 1) {
                ViewGroup.LayoutParams layoutParamsFoot = (ViewGroup.LayoutParams) footview.getLayoutParams();
                if (layoutParamsFoot.height == 0) {
                    layoutParamsFoot.height = getHeight() - scrollViewChild.getChildAt(scrollViewChild.getChildCount() - 1).getBottom();
                    if (layoutParamsFoot.height < 0) {
                        layoutParamsFoot.height = 0;
                    }
                }
                bottomHeight = getHeight() - scrollViewChild.getChildAt(scrollViewChild.getChildCount() - 1).getBottom();
                if (bottomHeight < 0) {
                    bottomHeight = 0;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        if (!flingOverScrollEnable) {
            return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
        }
        if (velocityTracker == null) {
            initVelocity();
        }
        velocityTracker.computeCurrentVelocity(1);
        int lastSpeed = (int) velocityTracker.getYVelocity();
        Log.i("lzc", "lastSpeed" + lastSpeed);
        if (!isTouchEvent && (valueAnimator == null || !valueAnimator.isRunning()) && (valueAnimator2 == null || !valueAnimator2.isRunning())) {
            if (overscrollAnimator == null || !overscrollAnimator.isRunning()) {
                if (lastSpeed > 0) {
                    //  Log.i("lzc", "toppppppppppppppp");
                    overScrollTop(lastSpeed);
                } else {
                    //   Log.i("lzc", "bottommmmmmmmmmmmmmm");
                    overScrollBottom(-lastSpeed);
                }
            }
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    protected void overScrollBottom(int deltaY) {
        overscrollAnimator = ValueAnimator.ofInt(this.getScrollY(), this.getScrollY() + deltaY * 7, this.getScrollY());
        overscrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                OverScrollListView.this.scrollTo(0, (int) (animation.getAnimatedValue()));
            }
        });
        overscrollAnimator.setDuration(700);
        overscrollAnimator.setInterpolator(new DecelerateInterpolator());
        overscrollAnimator.start();
    }

    protected void overScrollTop(int deltaY) {
        Log.i("lzc", "overscrollHead");
        overscrollAnimator = ValueAnimator.ofInt(0, deltaY * 7, 0);
        overscrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams layoutParamsTop = headview.getLayoutParams();
                layoutParamsTop.height = (int) animation.getAnimatedValue();
                headview.setLayoutParams(layoutParamsTop);
                headview.invalidate();
            }
        });
        overscrollAnimator.setDuration(700);
        overscrollAnimator.setInterpolator(new DecelerateInterpolator());
        overscrollAnimator.start();

    }

    //
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//
//        return super.onTouchEvent(ev);
//    }
}
