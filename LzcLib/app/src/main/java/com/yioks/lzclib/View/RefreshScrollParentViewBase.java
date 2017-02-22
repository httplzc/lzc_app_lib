package com.yioks.lzclib.View;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yioks.lzclib.Data.ScreenData;
import com.yioks.lzclib.R;

import java.lang.reflect.Field;

/**
 * 可以下拉刷新的控件
 */
public abstract class RefreshScrollParentViewBase<T extends View> extends ParentView implements ViewParent {
    //刷新头部view
    protected View reFreshView;
    //刷新文字控件
    protected TextView refreshText;
    //刷新图片
    protected ImageView reFreshImg;
    //下拉刷新状态视图
    protected LinearLayout pull;
    //正在加载状态视图
    protected FrameLayout loadding;
    //中间刷新监听器
    protected onCenterReFreshListener onCenterReFreshListener;
    protected ValueAnimator valueAnimator;
    protected T scrollView;
    protected View reFreshMoreView;

    protected LinearLayout refresh_succeed;

    protected View loadding_effect;

    protected boolean haveFinishLoadMore = false;
    protected boolean isLoaddingMore = false;
    protected int refresh_position = 1;
    private boolean dispath = true;

    protected int refreshColor;
    protected int footColor;

    //刷新的状态  正在刷新 正常 下拉中  释放可以刷新
    public enum ReFreshSatus {
        ONREFRESH, NORMAL, PULL, PULLCANREFRESH
    }

    ;
    //是否可以下拉刷新
    protected boolean canRefrish = true;
    //当前状态
    protected ReFreshSatus reFreshSatus = ReFreshSatus.NORMAL;

    public RefreshScrollParentViewBase(Context context) {
        super(context);
    }

    public RefreshScrollParentViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        inittype(context, attrs);
    }

    public RefreshScrollParentViewBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inittype(context, attrs);
    }

    protected void inittype(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RefreshScrollParentViewBase);
        refresh_position = typedArray.getInt(R.styleable.RefreshScrollParentViewBase_postion, 1);
        refreshColor=typedArray.getColor(R.styleable.RefreshScrollParentViewBase_headColor, ContextCompat.getColor(context,R.color.line_color));
        footColor=typedArray.getColor(R.styleable.RefreshScrollParentViewBase_bottomColor, ContextCompat.getColor(context,R.color.line_color));
        typedArray.recycle();
    }

    //加载完xml后 添加刷新view
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        scrollView = (T) contentView;
        addExternView();
        //  reFreshView.setPadding(reFreshView.getPaddingLeft(), (int) (-60 * ScreenData.density), reFreshView.getPaddingRight(), reFreshView.getPaddingBottom());
    }

    protected abstract void addExternView();

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //根据状态判断是否拦截点击事件
        if (staus != Staus.Normal) {
            super.onInterceptTouchEvent(ev);
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Log.i("lzc","dispath"+dispath);
        float eventX = ev.getX();
        float eventY = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = eventX;
                lastY = eventY;
                firstX = eventX;
                firstY = eventY;
                if (dispath)
                    onTouchDo(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                if ((isReadyForPullStart() || reFreshView.getPaddingTop() != 0) && reFreshSatus != ReFreshSatus.ONREFRESH && dispath) {
                    if (eventY - lastY < 0 && reFreshView.getPaddingTop() > 0) {
                        onTouchDo(ev);
                        if (scrollView instanceof ScrollView) {
                            Class aClass = scrollView.getClass();
                            while (!aClass.getName().equals(ScrollView.class.getName())) {
                                aClass = aClass.getSuperclass();
                            }

                            try {
                                final int activePointerIndex = ev.findPointerIndex(ev.getPointerId(0));
                                final int y = (int) ev.getY(activePointerIndex);
                                Field field = aClass.getDeclaredField("mLastMotionY");
                                field.setAccessible(true);
                                field.set(scrollView, y);
                                field.setAccessible(false);
                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        } else if (scrollView instanceof ListView) {
                            Class aClass = scrollView.getClass();
                            while (!aClass.getName().equals(AbsListView.class.getName())) {
                                aClass = aClass.getSuperclass();
                            }
                            try {

                                int activePointerIndex = ev.findPointerIndex(ev.getPointerId(0));
                                int y = (int) ev.getY(activePointerIndex);
                                Field mMotionYField = aClass.getDeclaredField("mMotionY");
                                mMotionYField.setAccessible(true);
                                mMotionYField.set(scrollView, y);
                                mMotionYField.setAccessible(false);

                                Field field = aClass.getDeclaredField("mLastY");
                                field.setAccessible(true);
                                field.set(scrollView, y);
                                field.setAccessible(false);
                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        return true;
                    } else {
                        onTouchDo(ev);
                    }
                }

//                if(isReadyForPullEnd())
//                {
//                    if(eventY-lastY<0)
//                    {
//                        Log.i("lzc","isReadyForPullEnd");
//                        onTouchDo(ev);
//                    }
//
//                }
                lastX = eventX;
                lastY = eventY;
                break;
            case MotionEvent.ACTION_UP:
                if (dispath)
                    onTouchDo(ev);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 是否到达顶部
     *
     * @return
     */
    protected abstract boolean isReadyForPullStart();


    /**
     * 是否到达底部
     *
     * @return
     */
    protected abstract boolean isReadyForPullEnd();

    protected void move(float dy) {
        int newPadding = (int) (dy * 0.35f) + reFreshView.getPaddingTop();
        //大于边线距离

        //移动距离大于释放刷新临界值
        if (newPadding >= 60 * ScreenData.density) {
            if (reFreshSatus != ReFreshSatus.PULLCANREFRESH) {
                // reFreshImg.setImageResource(R.drawable.default_ptr_rotate);
//                int dx = (int) (ScreenData.density * 7);
//                reFreshImg.setPadding(dx, dx, dx, dx);
                reFreshImg.setRotation(180);
            }
            reFreshSatus = ReFreshSatus.PULLCANREFRESH;
            refreshText.setText("释放刷新");

        } else if (newPadding > 0) {
            //移动距离小于释放刷新临界值


            if (reFreshSatus != ReFreshSatus.PULL) {
                // reFreshImg.setImageResource(R.drawable.indicator_arrow);
//                int dx = (int) (ScreenData.density * 5);
//                reFreshImg.setPadding(dx, dx, dx, dx);
                reFreshImg.setRotation(0);
            }
            reFreshSatus = ReFreshSatus.PULL;
            refreshText.setText("下拉刷新");
        } else {
            newPadding = 0;
            reFreshSatus = ReFreshSatus.NORMAL;
            //onBackToNormal();
        }

        reFreshView.setPadding(0, newPadding, 0, 0);
        //reFreshView.setLayoutParams(layoutParams);
//        } else {
//            //留下边线距离
//            reFreshSatus = ReFreshSatus.NORMAL;
//            back();
//        }

    }


    protected void Up() {

        //根据状态进行返回
        if (reFreshSatus == ReFreshSatus.PULL) {
            back();
        } else if (reFreshSatus == ReFreshSatus.PULLCANREFRESH) {
            refresh();
        }
    }

    public boolean onTouchDo(MotionEvent event) {
        if (staus != Staus.Normal) {
            return super.onTouchEvent(event);
        }
        if (!canRefrish) {
            return false;
        }
        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = eventX;
                lastY = eventY;
                firstX = eventX;
                firstY = eventY;
                reFreshSatus = ReFreshSatus.NORMAL;
                return true;
            case MotionEvent.ACTION_MOVE:
                //根据手指移动修改刷新布局上padding
                float dy = eventY - lastY;
                move(dy);
                lastX = eventX;
                lastY = eventY;
                return true;
            case MotionEvent.ACTION_UP:
                //点击事件
                //  performClick();
                Up();
                return true;

        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.i("lzc"," touch");
//        if (staus != Staus.Normal) {
//            return super.onTouchEvent(event);
//        }
//        if (!canRefrish) {
//            return false;
//        }
//        float eventX = event.getX();
//        float eventY = event.getY();
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                lastX = eventX;
//                lastY = eventY;
//                firstX = eventX;
//                firstY = eventY;
//                reFreshSatus = ReFreshSatus.NORMAL;
//                return true;
//            case MotionEvent.ACTION_MOVE:
//                //根据手指移动修改刷新布局上padding
//                float dy = eventY - firstY;
//                // scrollBy(0, (int) (-dy * 0.45f));
//                //    LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) reFreshView.getLayoutParams();
//                move(dy);
//                lastX = eventX;
//                lastY = eventY;
//                return true;
//            case MotionEvent.ACTION_UP:
//                //点击事件
//                performClick();
//                Up();
//                return true;
//
//        }
//        return true;
        return super.onTouchEvent(event);
    }

    /**
     * 调用到刷新状态动画
     */
    protected void refresh() {
        canRefrish = false;
        valueAnimator = ValueAnimator.ofInt(reFreshView.getPaddingTop(), (int) (45 * ScreenData.density));
        valueAnimator.setDuration((long) (Math.abs((reFreshView.getPaddingTop() - 45 * ScreenData.density)) / 0.8f));
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                reFreshView.setPadding(0, (int) animation.getAnimatedValue(), 0, 0);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                loadding.setVisibility(VISIBLE);
                pull.setVisibility(INVISIBLE);
                reFreshSatus = ReFreshSatus.ONREFRESH;
                if (isLoaddingMore) {
                    completeLoad(false);
                    return;
                }
                if (onCenterReFreshListener != null) {
                    onCenterReFreshListener.onCenterReFresh();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
    }

    /**
     * 返回初始状态
     */
    protected void back() {
        if (reFreshView.getPaddingTop() < 0) {
            return;
        }
        canRefrish = false;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(reFreshView.getPaddingTop(), 0);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration((long) (reFreshView.getPaddingTop() / 0.8f));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                reFreshView.setPadding(0, (int) animation.getAnimatedValue(), 0, 0);
                if ((int) animation.getAnimatedValue() == 0) {
                    loadding.setVisibility(INVISIBLE);
                    pull.setVisibility(VISIBLE);
                    reFreshImg.setImageResource(R.drawable.indicator_arrow);
                    int dx = (int) (ScreenData.density * 5);
                    reFreshImg.setPadding(dx, dx, dx, dx);
                    canRefrish = true;
                    reFreshSatus = ReFreshSatus.NORMAL;
                    loadding_effect.setVisibility(VISIBLE);
                    refresh_succeed.setVisibility(INVISIBLE);
                }
            }
        });
        valueAnimator.start();
    }

    /**
     * 完成加载，恢复状态
     */
    public void completeLoad(boolean succeed) {
        if (reFreshSatus == ReFreshSatus.ONREFRESH) {
            if (succeed) {
                refresh_succeed.setVisibility(VISIBLE);
                loadding_effect.setVisibility(INVISIBLE);
                this.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        valueAnimator.removeAllUpdateListeners();
                        back();
                    }
                }, 800);
            } else {
                valueAnimator.removeAllUpdateListeners();
                back();
            }

        }
    }

    /**
     * 调用自动加载动画
     */
    public void startload() {
        if (reFreshSatus != ReFreshSatus.NORMAL) {
            return;
        }
        canRefrish = false;
        valueAnimator = ValueAnimator.ofFloat(0, 45 * ScreenData.density);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration(800);
        loadding.setVisibility(VISIBLE);
        pull.setVisibility(INVISIBLE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float aFloat = (Float) animation.getAnimatedValue();
                reFreshView.setPadding(0, (int) aFloat.floatValue(), 0, 0);
                if (ScreenData.density * 45f - aFloat < 0.01) {
                    if (onCenterReFreshListener != null) {
                        onCenterReFreshListener.onCenterReFresh();
                    }
                    reFreshSatus = ReFreshSatus.ONREFRESH;
                }
            }
        });
        valueAnimator.start();
    }

    public interface onCenterReFreshListener {
        void onCenterReFresh();
    }

    public RefreshScrollParentViewBase.onCenterReFreshListener getOnCenterReFreshListener() {
        return onCenterReFreshListener;
    }

    public void setOnCenterReFreshListener(RefreshScrollParentViewBase.onCenterReFreshListener onCenterReFreshListener) {
        this.onCenterReFreshListener = onCenterReFreshListener;
    }

    public ReFreshSatus getReFreshSatus() {
        return reFreshSatus;
    }

    public void setReFreshSatus(ReFreshSatus reFreshSatus) {
        this.reFreshSatus = reFreshSatus;
    }

    public boolean isDispath() {
        return dispath;
    }

    public void setDispath(boolean dispath) {
        this.dispath = dispath;
    }
}
