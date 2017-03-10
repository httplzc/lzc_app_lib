package com.yioks.lzclib.View;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

import com.yioks.lzclib.Data.ScreenData;
import com.yioks.lzclib.R;

import java.lang.reflect.Field;

/**
 * Created by ${User} on 2016/9/1 0001.
 */
public class OverScrollScrollView extends ScrollView {
    private float lastY;
    private float lastX;
    private View headview;
    private View footview;
    private Context context;
    private float downY;
    protected Scroller scroller;
    private ValueAnimator valueAnimator;
    private ValueAnimator valueAnimator2;
    private ValueAnimator overscrollAnimator;
    private boolean flingOverScrollEnable = true;
    private boolean dragOverScrollEnable = true;
    private boolean dragOverScrollHeadEnable = true;
    private boolean dragOverScrollFootEnable = true;
    private final static float radio = 0.45f;
    private final static long back_time = 400;
    //第一次电机的X坐标
    protected float firstX;
    //第一次电机的Y坐标
    protected float firstY;

    private VelocityTracker velocityTracker;
    private int headColor= Color.TRANSPARENT;
    private int footColor=Color.TRANSPARENT;
    private int bottomHeight = 0;

    public OverScrollScrollView(Context context) {
        super(context);
        this.context = context;
        initUntil();

    }

    private void initUntil() {
        scroller = new Scroller(context, new OvershootInterpolator());
        this.setOverScrollMode(OVER_SCROLL_NEVER);
        this.setVerticalScrollBarEnabled(false);
        if (velocityTracker == null) {
            initVelocity();
        }
    }

    private void initVelocity() {
        velocityTracker = VelocityTracker.obtain();
    }


    private void initdate(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.OverScrollScrollView);
        flingOverScrollEnable = typedArray.getBoolean(R.styleable.OverScrollScrollView_flingOverScrollEnable_sv, true);
        dragOverScrollEnable = typedArray.getBoolean(R.styleable.OverScrollScrollView_dragOverScrollEnable_sv, true);
        dragOverScrollHeadEnable = typedArray.getBoolean(R.styleable.OverScrollScrollView_dragOverScrollHeadEnable_sv, true);
        dragOverScrollFootEnable = typedArray.getBoolean(R.styleable.OverScrollScrollView_dragOverScrollFootEnable_sv, true);
        headColor = typedArray.getColor(R.styleable.OverScrollScrollView_OverScrollHeadColor, Color.TRANSPARENT);
        footColor = typedArray.getColor(R.styleable.OverScrollScrollView_OverScrollFootColor, Color.TRANSPARENT);
        typedArray.recycle();
    }

    @Override
    protected void onDetachedFromWindow() {
        try {
            velocityTracker.recycle();
            velocityTracker = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDetachedFromWindow();
    }

    public OverScrollScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initUntil();
        initdate(attrs);
    }

    public OverScrollScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initUntil();
        initdate(attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        float eventX = ev.getX();
        float eventY = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = eventX;
                lastY = eventY;
                firstX = eventX;
                firstY = eventY;
                downY = eventY;
                if (isLessData()) {
                    lessDataDo((ViewGroup) this.getChildAt(0));
                }
                return false;
            case MotionEvent.ACTION_MOVE:

                //区分滑动与点击事件
                int dy = Math.abs((int) (firstY - ev.getY()));
                if (ViewConfiguration.get(getContext()).getScaledTouchSlop() < dy) {
                    lastX = eventX;
                    lastY = eventY;
                    return true;
                } else {
                    return false;
                }

            case MotionEvent.ACTION_UP:
                return false;
        }
        return false;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        headview = new View(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        footview = new View(context);
        LinearLayout.LayoutParams layoutParamsfoot = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        ViewGroup viewGroup = (ViewGroup) this.getChildAt(0);
        viewGroup.addView(headview, 0);
        viewGroup.addView(footview);
        headview.setLayoutParams(layoutParams);
        footview.setLayoutParams(layoutParamsfoot);
        headview.setBackgroundColor(headColor);
        footview.setBackgroundColor(footColor);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //   Log.i("lzc", "scrollView");
        if (velocityTracker == null) {
            initVelocity();
        }


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
        LinearLayout.LayoutParams layoutParamsTop = (LinearLayout.LayoutParams) headview.getLayoutParams();
        LinearLayout.LayoutParams layoutParamsFoot = (LinearLayout.LayoutParams) footview.getLayoutParams();
        if (valueAnimator != null) {
            valueAnimator.removeAllUpdateListeners();
        }
        if (valueAnimator2 != null) {
            valueAnimator2.removeAllUpdateListeners();
        }
        try {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downY = eventY;
                    lastY = eventY;
                    velocityTracker.clear();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //  Log.i("lzc", "move");
                    velocityTracker.addMovement(ev);
                    if (isFirstItemVisible() || layoutParamsTop.height != 0) {
                        if (lastY != downY) {
                            //     Log.i("lzc", "eventY - lastY" + (eventY - lastY));
                            if (eventY - lastY > 0 || layoutParamsTop.height != 0) {

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
                                    while (!aClass.getName().equals(ScrollView.class.getName())) {
                                        aClass = aClass.getSuperclass();
                                    }
                                    try {
                                        final int activePointerIndex = ev.findPointerIndex(ev.getPointerId(0));
                                        final int y = (int) ev.getY(activePointerIndex);
                                        Field field = aClass.getDeclaredField("mLastMotionY");
                                        field.setAccessible(true);
                                        field.set(this, y);
                                    } catch (NoSuchFieldException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                    lastY = eventY;
                                    return true;
                                }
                                lastY = eventY;
                                return super.onTouchEvent(ev);

                            }
                        }
                    }
                    if (isLastItemVisible() || layoutParamsFoot.height != 0) {
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
                                this.fullScroll(ScrollView.FOCUS_DOWN);
                                return super.onTouchEvent(ev);
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    velocityTracker.addMovement(ev);
                    back();
                    break;

            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        lastY = eventY;
        return super.onTouchEvent(ev);
    }


    public void back() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) headview.getLayoutParams();
        if (layoutParams.height != 0) {
            valueAnimator = ValueAnimator.ofInt(layoutParams.height, 0);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) headview.getLayoutParams();
                    layoutParams.height = (int) animation.getAnimatedValue();
                    headview.setLayoutParams(layoutParams);
                    headview.invalidate();
                }
            });
            valueAnimator.setDuration(back_time);
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.start();
        }
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) footview.getLayoutParams();
        if (layoutParams2.height != 0) {
            valueAnimator2 = ValueAnimator.ofInt(layoutParams2.height, 0);
            valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) footview.getLayoutParams();
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
        return this.getScrollY() == 0;
    }


    private boolean isLessData() {
        ViewGroup scrollViewChild = (ViewGroup) this.getChildAt(0);
        if (scrollViewChild.getChildCount() != 0) {
            if (scrollViewChild.getHeight() < getHeight()) {
                return true;
            }
        }
        return false;
    }

    private void lessDataDo(ViewGroup scrollViewChild) {
        LinearLayout.LayoutParams layoutParamsFoot = (LinearLayout.LayoutParams) footview.getLayoutParams();
        layoutParamsFoot.height = getHeight() - scrollViewChild.getHeight() - scrollViewChild.getPaddingTop();
        bottomHeight = getHeight() - scrollViewChild.getChildAt(scrollViewChild.getChildCount() - 1).getBottom() - scrollViewChild.getPaddingTop();
    }

    private boolean isLastItemVisible() {
        ViewGroup scrollViewChild = (ViewGroup) this.getChildAt(0);
        if (isLessData()) {
            return true;
        }
        if (null != scrollViewChild) {
            return this.getScrollY() >= (scrollViewChild.getHeight() - getHeight());
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
        velocityTracker.computeCurrentVelocity(1000);
        int lastSpeed = (int) velocityTracker.getYVelocity();
        int newScrollY = scrollY + deltaY;
        final int bottom = maxOverScrollY + scrollRangeY;
        final int top = -maxOverScrollY;

        if (newScrollY > bottom) {
            //   Log.i("lzc","isTouchEvent"+isTouchEvent+(valueAnimator == null)+"------"+(overscrollAnimator == null));
            if (!isTouchEvent && (valueAnimator == null || !valueAnimator.isRunning()) && (valueAnimator2 == null || !valueAnimator2.isRunning())) {
                if (overscrollAnimator == null || !overscrollAnimator.isRunning()) {
                    // Log.i("lzc","isTouchEvent"+isTouchEvent+"---"+lastSpeed);
                    overScrollBottom((int) (lastSpeed / 50f));
                }
            }
        } else if (newScrollY < top) {
            if (!isTouchEvent && (valueAnimator == null || !valueAnimator.isRunning()) && (valueAnimator2 == null || !valueAnimator2.isRunning())) {
                if (overscrollAnimator == null || !overscrollAnimator.isRunning()) {
                    overScrollTop((int) (lastSpeed / 50f));
                }
            }
        }

        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }


    protected void overScrollBottom(int deltaY) {
        deltaY = Math.abs(deltaY);
        if (deltaY == 0) {
            deltaY = (int) (30 * ScreenData.density);
        }
        //  overscrollAnimator = ValueAnimator.ofInt(this.getScrollY(), scrollRangeY, this.getScrollY());
        overscrollAnimator = ValueAnimator.ofInt(0, deltaY, 0);
        overscrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //     Log.i("lzc", "scroll_bottom_animation" + animation.getAnimatedValue() + "----" + footview.getHeight());
                LinearLayout.LayoutParams layoutParamsFoot = (LinearLayout.LayoutParams) footview.getLayoutParams();
                //     Log.i("lzc", "" + "layoutParamsFoot.height" + layoutParamsFoot.height);
                layoutParamsFoot.height = (int) animation.getAnimatedValue();
                footview.setLayoutParams(layoutParamsFoot);
                footview.invalidate();
                OverScrollScrollView.this.fullScroll(ScrollView.FOCUS_DOWN);
//                Log.i("lzc", "scroll_saddddd"+animation.getAnimatedValue());
//                OverScrollScrollView.this.scrollTo(0, (int) (animation.getAnimatedValue()));
            }
        });
        overscrollAnimator.setDuration(500);
        overscrollAnimator.setInterpolator(new DecelerateInterpolator());
        overscrollAnimator.start();
    }

    protected void overScrollTop(int deltaY) {
        Log.i("lzc", "deltaY" + deltaY);
        deltaY = Math.abs(deltaY);
        if (deltaY == 0) {
            deltaY = (int) (30 * ScreenData.density);
        }
        overscrollAnimator = ValueAnimator.ofInt(0, deltaY, 0);
        overscrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LinearLayout.LayoutParams layoutParamsTop = (LinearLayout.LayoutParams) headview.getLayoutParams();
                layoutParamsTop.height = (int) animation.getAnimatedValue();
                headview.setLayoutParams(layoutParamsTop);
                headview.invalidate();
            }
        });
        overscrollAnimator.setDuration(700);
        overscrollAnimator.setInterpolator(new DecelerateInterpolator());
        overscrollAnimator.start();

    }
}
