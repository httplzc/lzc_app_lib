/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2015 cpoopc
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package pers.lizechao.android_lib.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;

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
 * Date: 2018-07-06
 * Time: 16:02
 * 可以添加头部的ViewPager
 */
public class HeadViewPagerParentView extends LinearLayout {

    private float mDownX;
    private float mDownY;
    private float mLastY;

    //最大头部滑动距离
    private final int minY = 0;
    //最小滑动距离
    private int maxY = 0;
    //设置头部高度差值
    private int maxYDiff = 0;
    private int mHeadHeight;
    private int mExpandHeight;
    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    // 方向
    private DIRECTION mDirection;
    private int mCurY;
    private int mLastScrollerY;
    private boolean needCheckUpdown;
    private boolean updown;
    private boolean mDisallowIntercept;
    private boolean isClickHead;
    private boolean isClickHeadExpand;

    //头部View
    private View mHeadView;
    //底部ViewPager
    private ViewPager childViewPager;
    //滑动帮助
    private Scroller mScroller;
    //滑动速率监测
    private VelocityTracker mVelocityTracker;
    //辅助判断
    private HeadViewPagerScrollHelper mHelper;
    //滑动监听回调
    private OnScrollListener onScrollListener;

    /**
     * 滑动方向 *
     */
    enum DIRECTION {
        UP,// 向上划
        DOWN// 向下划
    }

    public interface OnScrollListener {

        void onScroll(int currentY, int maxY);

    }


    public void setCurrentScrollable(HeadViewPagerScrollHelper.ScrollableParent scrollableParent) {
        mHelper.setCurrentScrollable(scrollableParent);
    }


    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }


    public HeadViewPagerParentView(Context context) {
        super(context);
        init(context);
    }

    public HeadViewPagerParentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public HeadViewPagerParentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HeadViewPagerParentView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mHelper = new HeadViewPagerScrollHelper();
        mScroller = new Scroller(context);
        this.setOrientation(VERTICAL);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    public boolean isSticked() {
        return mCurY == maxY;
    }

    /**
     * 扩大头部点击滑动范围
     *
     * @param expandHeight
     */
    public void setClickHeadExpand(int expandHeight) {
        mExpandHeight = expandHeight;
    }

    public int getMaxY() {
        return maxY;
    }

    public boolean isHeadTop() {
        return mCurY == minY;
    }

    public boolean canPtr() {
        return updown && mCurY == minY && mHelper.isTop();
    }

    public void requestScrollableLayoutDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
        mDisallowIntercept = disallowIntercept;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float currentX = ev.getX();
        float currentY = ev.getY();
        float deltaY;
        int shiftX = (int) Math.abs(currentX - mDownX);
        int shiftY = (int) Math.abs(currentY - mDownY);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDisallowIntercept = false;
                needCheckUpdown = true;
                updown = true;
                mDownX = currentX;
                mDownY = currentY;
                mLastY = currentY;
                checkIsClickHead((int) currentY, mHeadHeight, getScrollY());
                checkIsClickHeadExpand((int) currentY, mHeadHeight, getScrollY());
                initOrResetVelocityTracker();
                mVelocityTracker.addMovement(ev);
                mScroller.forceFinished(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mDisallowIntercept) {
                    break;
                }
                initVelocityTrackerIfNotExists();
                mVelocityTracker.addMovement(ev);
                deltaY = mLastY - currentY;
                if (needCheckUpdown) {
                    if (shiftX > mTouchSlop && shiftX > shiftY) {
                        needCheckUpdown = false;
                        updown = false;
                    } else if (shiftY > mTouchSlop && shiftY > shiftX) {
                        needCheckUpdown = false;
                        updown = true;
                    }
                }

                //滑动头部  判断是否点击头部 或者可滑动控件在顶部 则滑动本身 否则分发事件
                if (updown && shiftY > mTouchSlop && shiftY > shiftX &&
                        (!isSticked() || mHelper.isTop() || isClickHeadExpand)) {

                    if (childViewPager != null) {
                        childViewPager.requestDisallowInterceptTouchEvent(true);
                    }
                    scrollBy(0, (int) (deltaY));
                }
                mLastY = currentY;
                break;
            case MotionEvent.ACTION_UP:
                //处理开始惯性滑动
                if (updown && shiftY > shiftX && shiftY > mTouchSlop) {
                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    float yVelocity = -mVelocityTracker.getYVelocity();

                    boolean dislowChild = false;
                    Log.i("lzc", "mMinimumVelocity " + yVelocity);
                    if (Math.abs(yVelocity) > mMinimumVelocity) {
                        mDirection = yVelocity > 0 ? DIRECTION.UP : DIRECTION.DOWN;
                        if ((mDirection == DIRECTION.UP && isSticked()) || (!isSticked() && getScrollY() == 0 && mDirection == DIRECTION.DOWN)) {
                            dislowChild = true;
                        } else {
                            mScroller.fling(0, getScrollY(), 0, (int) yVelocity, 0, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
                            mScroller.computeScrollOffset();
                            mLastScrollerY = getScrollY();
                            invalidate();
                        }
                    }
                    Log.i("lzc", "dislowChild " + dislowChild + "  isSticked()  " + isSticked());
                    if (!dislowChild && (isClickHead || !isSticked())) {
                        int action = ev.getAction();
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        boolean dispathResult = super.dispatchTouchEvent(ev);
                        ev.setAction(action);
                        return dispathResult;
                    }
                }
                break;
            default:
                break;
        }
        super.dispatchTouchEvent(ev);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private int getScrollerVelocity(int distance, int duration) {
        if (mScroller == null) {
            return 0;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return (int) mScroller.getCurrVelocity();
        } else {
            return distance / duration;
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            final int currY = mScroller.getCurrY();
            if (mDirection == DIRECTION.UP) {
                // 手势向上划
                if (isSticked()) {
                    int distance = mScroller.getFinalY() - currY;
                    int duration = calcDuration(mScroller.getDuration(), mScroller.timePassed());
                    mHelper.smoothScrollBy(getScrollerVelocity(distance, duration), distance, duration);
                    Log.i("lzc", "computeScroll mDirection == DIRECTION.UP   " + distance);
                    mScroller.forceFinished(true);
                    return;
                } else {
                    scrollTo(0, currY);
                    Log.i("lzc", "computeScroll !isSticked" + currY);
                    invalidate();
                }
            } else {
                // 手势向下划
                if (mHelper.isTop() || isClickHeadExpand) {
                    int deltaY = (currY - mLastScrollerY);
                    int toY = getScrollY() + deltaY;
                    scrollTo(0, toY);
                    if (mCurY <= minY) {
                        mScroller.forceFinished(true);
                        return;
                    }
                }
                invalidate();
            }
            mLastScrollerY = currY;
        }
    }

    @Override
    public void scrollBy(int x, int y) {
        int scrollY = getScrollY();
        int toY = scrollY + y;
        if (toY >= maxY) {
            toY = maxY;
        } else if (toY <= minY) {
            toY = minY;
        }
        y = toY - scrollY;
        super.scrollBy(x, y);
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y >= maxY) {
            y = maxY;
        } else if (y <= minY) {
            y = minY;
        }
        mCurY = y;
        if (onScrollListener != null) {
            onScrollListener.onScroll(y, maxY);
        }
        super.scrollTo(x, y);
    }

    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void checkIsClickHead(int downY, int headHeight, int scrollY) {
        isClickHead = downY + scrollY <= headHeight;
    }

    private void checkIsClickHeadExpand(int downY, int headHeight, int scrollY) {
        if (mExpandHeight <= 0) {
            isClickHeadExpand = false;
        }
        isClickHeadExpand = downY + scrollY <= headHeight + mExpandHeight;
    }

    private int calcDuration(int duration, int timepass) {
        return duration - timepass;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mHeadView = getChildAt(0);
        measureChildWithMargins(mHeadView, widthMeasureSpec, 0, MeasureSpec.UNSPECIFIED, 0);
        maxY = mHeadView.getMeasuredHeight();
        maxY -= maxYDiff;
        mHeadHeight = mHeadView.getMeasuredHeight();
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec) + maxY, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onFinishInflate() {
        if (mHeadView != null && !mHeadView.isClickable()) {
            mHeadView.setClickable(true);
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt != null && childAt instanceof ViewPager) {
                childViewPager = (ViewPager) childAt;
            }
        }
        super.onFinishInflate();
    }

    public int getMaxYDiff() {
        return maxYDiff;
    }

    public void setMaxYDiff(int maxYDiff) {
        this.maxYDiff = maxYDiff;
    }
}
