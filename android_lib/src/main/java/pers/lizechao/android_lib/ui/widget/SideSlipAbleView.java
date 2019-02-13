package pers.lizechao.android_lib.ui.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

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
 * Date: 2018-07-07
 * Time: 12:46
 */
class SideSlipAbleView extends ViewGroup {
    //主View
    private View contentView;
    //侧滑外的View
    @Nullable
    private View extraView;
    private final GestureDetector gestureDetector;
    private final int mTouchSlop;
    //当前滑动偏移量  一定是正数
    private int currentOffset;
    //当前状态
    private ScrollState state = ScrollState.Normal;
    //最大滑动距离
    private int maxScrollWidth = 0;

    private enum ScrollState {Normal, CanToBack, CanToEnd}

    private boolean isAnim = false;

    private ScrollListener scrollListener;

    //滑动取消比例
    private static final float needCancelRatio = 0.5f;

    private OpenListener openListener;

    //是否由父类处理事件
    private boolean needHandDownUp = false;


    private final PointF downPoint = new PointF();
    private final PointF lastPoint = new PointF();
    private final PointF currentPoint = new PointF();

    public SideSlipAbleView(Context context) {
        super(context);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        gestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                needHandDownUp = false;
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                needHandDownUp = true;
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                scroll(distanceX);
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(velocityX) > Math.abs(velocityY)) {
                    removeCallbacks(upAction);
                    doScrollAnim(velocityX < 0);
                }

                return true;
            }


        });
    }

    public void resetState(boolean isOpen) {
        //根据当前状态，修改初始偏移量
        if (isOpen)
            currentOffset = maxScrollWidth;
        else
            currentOffset = 0;
        requestLayout();
    }


    private final Runnable upAction = () -> {
        if (state == ScrollState.CanToBack) {
            doScrollAnim(false);
        } else if (state == ScrollState.CanToEnd) {
            doScrollAnim(true);
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (extraView == null)
            return false;
        currentPoint.set(ev.getX(), ev.getY());
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downPoint.set(ev.getX(), ev.getY());
            lastPoint.set(ev.getX(), ev.getY());
            state = ScrollState.Normal;
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = downPoint.x - currentPoint.x;
            float dy = downPoint.y - currentPoint.y;
            lastPoint.set(ev.getX(), ev.getY());
            return Math.abs(dx) > Math.abs(dy) && dx < 0 && Math.abs(dx) > mTouchSlop;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            post(upAction);
        }
        boolean result = gestureDetector.onTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_UP && !needHandDownUp) {
            ev.setAction(MotionEvent.ACTION_CANCEL);
        }
        super.onTouchEvent(ev);
        return result;
    }

    public SideSlipAbleView(Context context, View contentView, View extraView) {
        this(context);
        setView(contentView, extraView);
    }

    public void setView(View contentView, View extraView) {
        if (getChildCount() >= 2)
            throw new IllegalStateException("只能有两个View");
        this.contentView = contentView;
        this.extraView = extraView;
        this.addView(contentView);
        if (extraView != null)
            this.addView(extraView);
    }

    private void changeCurrentScroll(float currentOffset) {
        if (currentOffset < 0)
            currentOffset = 0;
        if (currentOffset > maxScrollWidth)
            currentOffset = maxScrollWidth;
        this.currentOffset = (int) currentOffset;
        if (currentOffset == 0) {
            state = ScrollState.Normal;
        } else if (currentOffset < needCancelRatio * maxScrollWidth) {
            state = ScrollState.CanToBack;
        } else {
            state = ScrollState.CanToEnd;
        }
        if (openListener != null) {
            if (currentOffset == 0)
                openListener.close();
            else if (currentOffset == maxScrollWidth)
                openListener.open();
        }
        if (scrollListener != null)
            scrollListener.scroll(currentOffset, maxScrollWidth);
        requestLayout();
    }


    //滑动
    private void scroll(float scroll) {
        if (isAnim)
            return;
        changeCurrentScroll((int) (currentOffset + scroll));
    }


    //播放开闭动画
    public void doScrollAnim(boolean open) {
        if (isAnim || extraView == null)
            return;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(currentOffset, open ? maxScrollWidth : 0);
        valueAnimator.addUpdateListener(animation -> changeCurrentScroll((Float) animation.getAnimatedValue()));
        valueAnimator.setDuration(100);
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnim = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        isAnim = true;
        valueAnimator.start();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildWithMargins(contentView, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
          0, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.UNSPECIFIED), 0);
        if (extraView != null)
            //测量侧滑外的部分
            measureChild(extraView, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
              , MeasureSpec.makeMeasureSpec(contentView.getMeasuredHeight(), MeasureSpec.AT_MOST));
        //最终宽度
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), contentView.getMeasuredHeight());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        MarginLayoutParams layoutParamsContent = (MarginLayoutParams) contentView.getLayoutParams();
        MarginLayoutParams layoutParamsSide = null;
        if (extraView != null) {
            layoutParamsSide = (MarginLayoutParams) extraView.getLayoutParams();
            maxScrollWidth = extraView.getMeasuredWidth() + layoutParamsSide.leftMargin + layoutParamsSide.rightMargin;
        }


        //根据偏移量对其进行布局
        contentView.layout(getPaddingLeft() + layoutParamsContent.leftMargin - currentOffset,
          getPaddingTop() + layoutParamsContent.topMargin, getMeasuredWidth() - currentOffset - getPaddingRight() - layoutParamsContent.rightMargin,
          getMeasuredHeight() - getPaddingBottom() - layoutParamsContent.bottomMargin);

        //将布局放在正常布局右边
        if (extraView != null && layoutParamsSide != null) {
            extraView.layout(getMeasuredWidth() - currentOffset + layoutParamsSide.leftMargin,
              contentView.getTop(),
              getMeasuredWidth() + extraView.getMeasuredWidth() + layoutParamsSide.leftMargin + layoutParamsSide.rightMargin - currentOffset,
              contentView.getBottom());

        }

    }

    public void setOpenListener(OpenListener openListener) {
        this.openListener = openListener;
    }

    public void setScrollListener(ScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    interface ScrollListener {
        void scroll(float currentScroll, float maxScroll);
    }

    interface OpenListener {
        void open();

        void close();
    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    public int getMaxScrollWidth() {
        return maxScrollWidth;
    }
}
