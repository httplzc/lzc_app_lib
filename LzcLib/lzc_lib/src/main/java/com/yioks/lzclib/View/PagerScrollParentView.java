package com.yioks.lzclib.View;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yioks.lzclib.Data.ScreenData;
import com.yioks.lzclib.R;

import java.lang.reflect.Field;

import static com.yioks.lzclib.R.id.loadding;
import static com.yioks.lzclib.R.id.loadding_effect;


/**
 * Created by ${User} on 2016/9/1 0001.
 */
public class PagerScrollParentView extends FrameLayout implements PagingGroup.PagerScrollEvent {
    protected LinearLayout content;
    //上一次点击的X坐标
    protected float lastX;
    //上一次点击的Y坐标
    protected float lastY;
    //第一次电机的X坐标
    protected float firstX;
    //第一次电机的Y坐标
    protected float firstY;
    protected android.widget.ScrollView scrollView;
    protected PagerView head;
    protected PagerView foot;
    private boolean dispath = true;
    protected int headColor = Color.WHITE;
    protected int footColor = Color.WHITE;
    protected ValueAnimator valueAnimator;

    //是否可以下拉刷新
    protected boolean canRefrish = true;

    protected boolean canLoadNext = true;
    protected boolean canLoadLast = true;
    //当前状态
    protected ReFreshSatus reFreshSatus = ReFreshSatus.NORMAL;
    private PagingGroup.onLoadingPageListener onLoadingPageListener;

    private static final int headAndFootHeight = 60;

    public enum ReFreshSatus {
        ONREFRESH, NORMAL, PULL, PULLCANREFRESH
    }

    private onPullListener onPullListener;


    public PagerScrollParentView(Context context) {
        super(context);
    }

    public PagerScrollParentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PagerScrollParentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addExternView();
    }

    private class PagerView {
        View pull;
        View loading;
        View loading_effect;
        View refresh_succeed;

        TextView refreshText;
        ImageView loadingRefresh;
        View rootView;

        public PagerView(View rootView) {
            this.rootView = rootView;
            pull = rootView.findViewById(R.id.pull);
            loading = rootView.findViewById(loadding);
            loading_effect = rootView.findViewById(loadding_effect);
            refresh_succeed = rootView.findViewById(R.id.refresh_succeed);
            refreshText = (TextView) rootView.findViewById(R.id.refresh_text);
            loadingRefresh = (ImageView) rootView.findViewById(R.id.refresh_img);
        }

        public int getPaddingValue() {
            return this == head ? head.rootView.getPaddingTop() : foot.rootView.getPaddingBottom();
        }

        public void setPaddingValue(int padding) {
            if (this == head) {
                rootView.setPadding(0, padding, 0, 0);
                if (onPullListener != null)
                    onPullListener.onHeadPull(padding);
            } else {
                rootView.setPadding(0, 0, 0, padding);
                if (onPullListener != null)
                    onPullListener.onFootPull(padding);
            }
        }
    }


    protected void addExternView() {
        scrollView = (android.widget.ScrollView) getChildAt(0);
        content = (LinearLayout) scrollView.getChildAt(0);
        head = new PagerView(LayoutInflater.from(getContext()).inflate(R.layout.change_pager_view_top, content, false));
        foot = new PagerView(LayoutInflater.from(getContext()).inflate(R.layout.change_pager_view_bottom, content, false));

        head.rootView.setBackgroundColor(headColor);
        foot.rootView.setBackgroundColor(footColor);
        content.addView(head.rootView, 0);
        content.addView(foot.rootView);
        content.setPadding(head.rootView.getPaddingLeft(), (int) (-headAndFootHeight * ScreenData.density), head.rootView.getPaddingRight(), (int) (-headAndFootHeight * ScreenData.density));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void changeLessDataHeight() {
        int contentHeight = content.getHeight();
        int scrollViewHeight = scrollView.getHeight();
        View child = foot.rootView;
        if (contentHeight < scrollViewHeight + headAndFootHeight * ScreenData.density) {
            int calcHeight = (int) (scrollViewHeight + headAndFootHeight * ScreenData.density - contentHeight);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) child.getLayoutParams();
            lp.height = child.getHeight() + (int) (calcHeight + headAndFootHeight * ScreenData.density);
            child.setLayoutParams(lp);
        }

    }


    /**
     * 是否到达顶部
     *
     * @return
     */
    public boolean isReadyForPullStart() {
        return scrollView.getScrollY() == 0;
    }

    /**
     * 是否到达底部
     *
     * @return
     */
    public boolean isReadyForPullEnd() {
        if (null != content) {
            return scrollView.getScrollY() >= (content.getHeight() - getHeight()) || content.getHeight() <= scrollView.getHeight();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //根据状态判断是否拦截点击事件
        return super.onInterceptTouchEvent(ev);
    }

    public void fixScrollView(MotionEvent ev) {
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
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float eventX = ev.getX();
        float eventY = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = eventX;
                lastY = eventY;
                firstX = eventX;
                firstY = eventY;
                if (dispath)
                    onTouchDo(ev, false);
                break;
            case MotionEvent.ACTION_MOVE:
                if (reFreshSatus != ReFreshSatus.ONREFRESH && dispath) {
                    //头部下拉
                    //  Log.i("lzc", "foot.getPaddingValue()" + foot.getPaddingValue() + "---" + head.getPaddingValue() + "---" + isReadyForPullStart() + "---" + isReadyForPullEnd());
                    if (foot.getPaddingValue() == 0 && (head.getPaddingValue() != 0 || (isReadyForPullStart() && eventY - lastY > 0))) {
                        if (canLoadLast) {
                            onTouchDo(ev, true);
                            //       Log.i("lzc","head.getPaddingValue()"+head.getPaddingValue()+"----"+( eventY - lastY)+"---"+eventY+"---"+lastY);
                            if (head.getPaddingValue() != 0 && eventY - lastY < 0) {
                                //   Log.i("lzc","head_back");
                                fixScrollView(ev);
                                lastX = eventX;
                                lastY = eventY;
                                return true;
                            }
                        }

                    }
                    //底部下拉
                    else if (head.getPaddingValue() == 0 && (foot.getPaddingValue() != 0 || (isReadyForPullEnd() && eventY - lastY < 0))) {
                        if (canLoadNext) {
                            onTouchDo(ev, false);
                            if (foot.getPaddingValue() != 0 && eventY - lastY > 0) {
                                fixScrollView(ev);
                                lastX = eventX;
                                lastY = eventY;
                                return true;
                            }
                        }
                    }
                }

                lastX = eventX;
                lastY = eventY;
                break;
            case MotionEvent.ACTION_UP:
                if (dispath)
                    onTouchDo(ev, head.getPaddingValue() != 0);
                lastX = eventX;
                lastY = eventY;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    protected void move(float dy, PagerView pagerView) {

        if (pagerView == foot) {
            dy = -dy;
            //  changeLessDataHeight();
        }

        int newPadding = (int) (dy * 0.35f) + pagerView.getPaddingValue();
        //大于边线距离

        //移动距离大于释放刷新临界值
        if (newPadding >= headAndFootHeight * ScreenData.density) {
            if (reFreshSatus != ReFreshSatus.PULLCANREFRESH) {
                // reFreshImg.setImageResource(R.drawable.default_ptr_rotate);
//                int dx = (int) (ScreenData.density * 7);
//                reFreshImg.setPadding(dx, dx, dx, dx);
                pagerView.loadingRefresh.setRotation(pagerView == head ? 180 : 0);
            }
            reFreshSatus = ReFreshSatus.PULLCANREFRESH;
            pagerView.refreshText.setText("释放跳转");

        } else if (newPadding > 0) {
            //移动距离小于释放刷新临界值


            if (reFreshSatus != ReFreshSatus.PULL) {
                // reFreshImg.setImageResource(R.drawable.indicator_arrow);
//                int dx = (int) (ScreenData.density * 5);
//                reFreshImg.setPadding(dx, dx, dx, dx);
                pagerView.loadingRefresh.setRotation(pagerView == head ? 0 : 180);
            }
            reFreshSatus = ReFreshSatus.PULL;
            pagerView.refreshText.setText(((pagerView == head) ? "下拉加载上一页" : "上拉加载下一页"));
        } else {
            newPadding = 0;
            reFreshSatus = ReFreshSatus.NORMAL;
            //onBackToNormal();
        }
        if (pagerView == head) {
            pagerView.setPaddingValue(newPadding);
        } else {
            pagerView.setPaddingValue(newPadding);
            Log.i("lzc", "pagerView.rootView.setPadding   " + pagerView.rootView.getPaddingBottom() + "  ----  " + newPadding);
            Log.i("lzc", "" + content.getHeight());
        }


        //   Log.i("lzc","reFreshView"+reFreshView.getPaddingValue()+"---"+((LinearLayout.LayoutParams)reFreshView.getLayoutParams()).topMargin);
        //reFreshView.setLayoutParams(layoutParams);
//        } else {
//            //留下边线距离
//            reFreshSatus = ReFreshSatus.NORMAL;
//            back();
//        }

    }


    protected void Up(boolean isHead) {

        //根据状态进行返回
        if (reFreshSatus == ReFreshSatus.PULL) {
            back(isHead ? head : foot);
        } else if (reFreshSatus == ReFreshSatus.PULLCANREFRESH) {
            refresh(isHead ? head : foot);
        }
    }

    public boolean onTouchDo(MotionEvent event, boolean isHead) {
        if (!canRefrish) {
            return false;
        }
        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                reFreshSatus = ReFreshSatus.NORMAL;
                return true;
            case MotionEvent.ACTION_MOVE:
                //根据手指移动修改刷新布局上padding
                float dy = eventY - lastY;
                if (Math.abs(eventY - firstY) >= getResources().getDimension(R.dimen.min_pull_distance))
                    move(dy, isHead ? head : foot);
                return true;
            case MotionEvent.ACTION_UP:
                //点击事件
                //  performClick();
                Up(isHead);
                return true;

        }
        return true;
    }

    /**
     * 调用到刷新状态动画
     */
    protected void refresh(final PagerView pagerView) {
        canRefrish = false;
        valueAnimator = ValueAnimator.ofInt(pagerView.getPaddingValue(), (int) (45 * ScreenData.density));
        valueAnimator.setDuration((long) (Math.abs((pagerView.getPaddingValue() - 45 * ScreenData.density)) / 0.8f));
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (pagerView == head)
                    pagerView.setPaddingValue((int) animation.getAnimatedValue());
                else
                    pagerView.setPaddingValue((int) animation.getAnimatedValue());
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //    Log.i("lzc", " pagerView.rootView.getPaddingBottom()" + pagerView.rootView.getPaddingBottom());
                pagerView.loading.setVisibility(VISIBLE);
                pagerView.pull.setVisibility(INVISIBLE);
                reFreshSatus = ReFreshSatus.ONREFRESH;
                if (onLoadingPageListener != null) {
                    if (pagerView == head)
                        onLoadingPageListener.loadingLast();
                    else
                        onLoadingPageListener.loadingNext();
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

    protected void backCurrent(boolean isHead) {
        if (isHead) {
            head.setPaddingValue(0);
        } else {
            foot.setPaddingValue(0);
        }
        PagerView pagerView = isHead ? head : foot;
        pagerView.loading.setVisibility(INVISIBLE);
        pagerView.pull.setVisibility(VISIBLE);
        pagerView.loadingRefresh.setImageResource(R.drawable.indicator_arrow);
        int dx = (int) (ScreenData.density * 5);
        pagerView.loadingRefresh.setPadding(dx, dx, dx, dx);
        canRefrish = true;
        reFreshSatus = ReFreshSatus.NORMAL;
        pagerView.loading_effect.setVisibility(VISIBLE);
        pagerView.refresh_succeed.setVisibility(INVISIBLE);

    }

    /**
     * 返回初始状态
     */
    protected void back(final PagerView pagerView) {
        Log.i("lzc", "backbackback");
        if (pagerView.getPaddingValue() < 0) {
            return;
        }
        canRefrish = false;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(pagerView.getPaddingValue(), 0);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration((long) (pagerView.getPaddingValue() / 0.8f));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pagerView.setPaddingValue((int) animation.getAnimatedValue());

                if ((int) animation.getAnimatedValue() == 0) {
                    pagerView.loading.setVisibility(INVISIBLE);
                    pagerView.pull.setVisibility(VISIBLE);
                    pagerView.loadingRefresh.setImageResource(R.drawable.indicator_arrow);
                    int dx = (int) (ScreenData.density * 5);
                    pagerView.loadingRefresh.setPadding(dx, dx, dx, dx);
                    canRefrish = true;
                    reFreshSatus = ReFreshSatus.NORMAL;
                    pagerView.loading_effect.setVisibility(VISIBLE);
                    pagerView.refresh_succeed.setVisibility(INVISIBLE);
                }
            }
        });
        valueAnimator.start();
    }

    /**
     * 完成加载，恢复状态
     */
    public void completeLoad(boolean succeed, boolean isHead) {
        final PagerView pagerView = isHead ? head : foot;
        if (reFreshSatus == ReFreshSatus.ONREFRESH) {
            if (succeed) {
                pagerView.refresh_succeed.setVisibility(VISIBLE);
                pagerView.loading_effect.setVisibility(INVISIBLE);
                this.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        valueAnimator.removeAllUpdateListeners();
                        back(pagerView);
                    }
                }, 800);
            } else {
                valueAnimator.removeAllUpdateListeners();
                back(pagerView);
            }

        }
    }

    /**
     * 完成加载，恢复状态
     */
    public void completeLoadCurrent(boolean succeed, boolean isHead) {
        final PagerView pagerView = isHead ? head : foot;
        if (reFreshSatus == ReFreshSatus.ONREFRESH) {
            valueAnimator.removeAllUpdateListeners();
            if (succeed)
                backCurrent(pagerView == head);
            else
                back(isHead ? head : foot);
        }
    }

    /**
     * 调用自动加载动画
     */
    public void startLoad(final boolean isHead) {
        final PagerView pagerView = isHead ? head : foot;
        if (reFreshSatus != ReFreshSatus.NORMAL) {
            return;
        }
        canRefrish = false;
        valueAnimator = ValueAnimator.ofFloat(0, 45 * ScreenData.density);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration(800);
        pagerView.loading.setVisibility(VISIBLE);
        pagerView.pull.setVisibility(INVISIBLE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float aFloat = (Float) animation.getAnimatedValue();
                if (pagerView == head) {
                    pagerView.setPaddingValue((int) aFloat.floatValue());
                } else {
                    pagerView.setPaddingValue((int) aFloat.floatValue());
                }

                if (ScreenData.density * 45f - aFloat < 0.01) {
                    if (onLoadingPageListener != null) {
                        if (isHead)
                            onLoadingPageListener.loadingLast();
                        else
                            onLoadingPageListener.loadingNext();
                    }
                    reFreshSatus = ReFreshSatus.ONREFRESH;
                }
            }
        });
        valueAnimator.start();
    }


    public PagingGroup.onLoadingPageListener getOnLoadingPageListener() {
        return onLoadingPageListener;
    }

    public void setOnLoadingPageListener(PagingGroup.onLoadingPageListener onLoadingPageListener) {
        this.onLoadingPageListener = onLoadingPageListener;
    }

    public boolean isCanLoadNext() {
        return canLoadNext;
    }

    public void setCanLoadNext(boolean canLoadNext) {
        this.canLoadNext = canLoadNext;
    }

    public boolean isCanLoadLast() {
        return canLoadLast;
    }

    public void setCanLoadLast(boolean canLoadLast) {
        this.canLoadLast = canLoadLast;
    }


    private int getPullHeight(boolean isHeight) {
        return isHeight ? head.getPaddingValue() : foot.getPaddingValue();
    }


    public interface onPullListener {
        void onHeadPull(int height);

        void onFootPull(int height);
    }

    public PagerScrollParentView.onPullListener getOnPullListener() {
        return onPullListener;
    }

    public void setOnPullListener(PagerScrollParentView.onPullListener onPullListener) {
        this.onPullListener = onPullListener;
    }
}
