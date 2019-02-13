package pers.lizechao.android_lib.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import pers.lizechao.android_lib.R;
import pers.lizechao.android_lib.common.LoopTask;

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
 * Date: 2018-07-16
 * Time: 14:34
 * 轮播图View
 */
public class AutoLoopViewPager extends ViewPager {
    @Nullable
    private LoopTask loopTask;
    private boolean autoScroll = true;
    private int autoScrollTime = 4000;
    private int mSlop;
    private ScrollConflictHandle conflictHandle;
    private boolean handleTouch = true;


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public AutoLoopViewPager(@NonNull Context context) {
        super(context);
        initView();
    }

    public AutoLoopViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initView();
    }


    private void initView() {
        mSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        if (autoScroll) {
            loopTask = new LoopTask(autoScrollTime, Looper.getMainLooper());
            loopTask.setRunnable(() -> {
                if (getAdapter() != null) {
                    if (getCurrentItem() != getAdapter().getCount() - 1)
                        setCurrentItem(getCurrentItem() + 1, true);
                    else {
                        setCurrentItem(0, false);
                    }
                }
            });
            loopTask.setAutoStart(true);
        }

        initTouch();

    }

    private void initTouch() {
        this.setOnTouchListener(new View.OnTouchListener() {
            private PointF downPointF = new PointF();
            private PointF currentPointF = new PointF();
            private boolean isClick = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!handleTouch)
                    return false;
                currentPointF.x = event.getX();
                currentPointF.y = event.getY();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downPointF.x = currentPointF.x;
                    downPointF.y = currentPointF.y;
                    isClick = true;
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    float dy = currentPointF.y - downPointF.y;
                    float dx = currentPointF.x - downPointF.x;
                    if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > mSlop) {
                        isClick = false;
                        if (conflictHandle != null)
                            conflictHandle.enableOutView(false);
                        onTouchEvent(event);
                        return true;
                    } else if (Math.abs(dx) < Math.abs(dy) && Math.abs(dy) > mSlop) {
                        isClick = false;
                        if (conflictHandle != null)
                            conflictHandle.enableOutView(true);
                    }
                } else {
                    if (conflictHandle != null)
                        conflictHandle.enableOutView(true);
                }
                if (event.getAction() == MotionEvent.ACTION_UP && isClick) {
                    performClick();
                }
                return false;
            }
        });
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AutoLoopViewPager);
        autoScroll = typedArray.getBoolean(R.styleable.AutoLoopViewPager_autoScroll, true);
        autoScrollTime = typedArray.getInteger(R.styleable.AutoLoopViewPager_intervalTime, autoScrollTime);
        typedArray.recycle();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (loopTask != null)
                loopTask.setAutoStart(false);
        } else if (ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_UP) {
            if (loopTask != null)
                loopTask.setAutoStart(true);
        }

        return super.onTouchEvent(ev);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (loopTask != null)
            loopTask.setAutoStart(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (loopTask != null)
            loopTask.setAutoStart(false);
    }

    public interface ScrollConflictHandle {
        //让外部view可以处理触摸事件
        void enableOutView(boolean enable);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setScrollConflictHandle(ScrollConflictHandle scrollConflictHandle) {
        this.conflictHandle = scrollConflictHandle;
    }
}
