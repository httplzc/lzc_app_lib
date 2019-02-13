package pers.lizechao.android_lib.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

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
 * Date: 2018-07-02
 * Time: 17:33
 * 可以拖动的列表
 */
public class DragRecycleView extends FrameLayout {
    //基础RecycleView
    private BaseRecycleView recyclerView;
    //记录状态
    private final ChoiceState choiceState = new ChoiceState();
    //数据适配器
    private DragRecycleViewAdapter adapter;
    public float AnimScale = 1.2f;
    private final PointF downPoint = new PointF();
    private final PointF currentPoint = new PointF();
    private final PointF lastPoint = new PointF();
    private static final int autoScrollRatio = 7;
    private static final int autoScrollTime = 60;
    //滑动recyclerView的事件
    private Runnable scrollRecyclerViewRunnable;

    private long lastSwitchTime;
    private int scrollOrientationFlag;

    enum Mode {
        normal, drag, up
    }

    public static final int FLAG_UP = 1;
    public static final int FLAG_DOWN = 1 << 1;
    public static final int FLAG_LEFT = 1 << 2;
    public static final int FLAG_RIGHT = 1 << 3;

    public DragRecycleView(Context context) {
        super(context);
        init(context);
    }

    public DragRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DragRecycleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private class ChoiceState {
        //一般状态 正在拖拽 已经放手播放动画中

        //当前拖动的真实View
        View dragView;
        //当前选中的序列号
        int currentPosition;
        //当前状态
        Mode mode = Mode.normal;

        void setNormal() {
            mode = Mode.normal;
        }

        void setDrag() {
            mode = Mode.drag;
        }

        void setUp() {
            mode = Mode.up;
        }

        boolean isNormal() {
            return mode == Mode.normal;
        }

        boolean isDrag() {
            return mode == Mode.drag;
        }

        //当前拖动View中心
        PointF getDragViewCenter() {
            return new PointF(dragView.getTranslationX() + dragView.getWidth() / 2, dragView.getTranslationY() + dragView.getHeight() / 2);
        }

        //越过到顶部
        boolean isDragViewCrossTop() {
            return dragView.getTranslationY() < 0;
        }

        //越过底部
        boolean isDragViewCrossBottom() {
            return dragView.getTranslationY() > recyclerView.getHeight() - dragView.getHeight();
        }

        //越过左边
        boolean isDragViewCrossLeft() {
            return dragView.getTranslationX() < 0;
        }

        //越过右边
        boolean isDragViewCrossRight() {
            return dragView.getTranslationX() > recyclerView.getWidth() - dragView.getWidth();
        }

        //进行偏移
        void callDragViewTrans(float distanceX, float distanceY) {
            if ((scrollOrientationFlag & FLAG_LEFT) == 0 && distanceX < 0) {
                distanceX = 0;
            }
            if ((scrollOrientationFlag & FLAG_RIGHT) == 0 && distanceX > 0) {
                distanceX = 0;
            }
            if ((scrollOrientationFlag & FLAG_UP) == 0 && distanceY < 0) {
                distanceY = 0;
            }
            if ((scrollOrientationFlag & FLAG_DOWN) == 0 && distanceY > 0) {
                distanceY = 0;
            }
            dragView.setTranslationX(dragView.getTranslationX() + distanceX);
            dragView.setTranslationY(dragView.getTranslationY() + distanceY);
        }

    }

    private void init(Context context) {
        recyclerView = new BaseRecycleView(context);
        //滑动recyclerView的事件
        scrollRecyclerViewRunnable = new Runnable() {
            @Override
            public void run() {
                if (!choiceState.isDrag())
                    return;
                //若没有滑到头继续滑动
                //横向滑动
                if (recyclerView.getLayoutManager().canScrollHorizontally()) {
                    if (choiceState.isDragViewCrossLeft()) {
                        recyclerView.scrollBy(-autoScrollRatio, 0);
                        recyclerView.postDelayed(this, autoScrollTime);
                    } else if (choiceState.isDragViewCrossRight()) {
                        recyclerView.scrollBy(autoScrollRatio, 0);
                        recyclerView.postDelayed(this, autoScrollTime);
                    }
                } else {
                    //纵向滑动
                    if (choiceState.isDragViewCrossTop()) {
                        recyclerView.scrollBy(0, -autoScrollRatio);
                        recyclerView.postDelayed(this, autoScrollTime);
                    } else if (choiceState.isDragViewCrossBottom()) {
                        recyclerView.scrollBy(0, autoScrollRatio);
                        recyclerView.postDelayed(this, autoScrollTime);
                    }
                }

            }
        };
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downPoint.set(ev.getX(), ev.getY());
            lastPoint.set(ev.getX(), ev.getY());
        }
        return !choiceState.isNormal();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!choiceState.isDrag())
            return false;
        currentPoint.set(event.getX(), event.getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                onMoveDo(currentPoint.x - lastPoint.x, currentPoint.y - lastPoint.y);
                break;
            case MotionEvent.ACTION_UP:
                choiceState.setUp();
                doBackAnim();
                break;
        }
        lastPoint.set(currentPoint.x, currentPoint.y);
        return true;
    }

    //拖动动画真实View
    private void onMoveDo(float distanceX, float distanceY) {
        if (choiceState.isDrag()) {
            choiceState.callDragViewTrans(distanceX, distanceY);
            //计算是否该交换位置
            checkNeedSwitch();
            //若滑动到边界，滚动recycleView
            recyclerView.postDelayed(scrollRecyclerViewRunnable, 0);
        }
    }

    //根据当前动画View的中心判断是否需要移动View
    private void checkNeedSwitch() {
        //避免交换太频繁
        if (System.currentTimeMillis() - lastSwitchTime < 300) {
            return;
        }
        PointF pointF = choiceState.getDragViewCenter();
        View view = recyclerView.findChildViewUnder(pointF.x, pointF.y);
        if (view == null)
            return;
        int needMovePosition = recyclerView.getChildAdapterPosition(view);
        if (needMovePosition == choiceState.currentPosition)
            return;
        lastSwitchTime = System.currentTimeMillis();
        adapter.setInvisiblePosition(needMovePosition);
        adapter.moveData(choiceState.currentPosition, needMovePosition);
        adapter.notifyItemMoved(choiceState.currentPosition, needMovePosition);
        choiceState.currentPosition = needMovePosition;
        //避免连续移动
        recyclerView.scrollToPosition(choiceState.currentPosition != 0 ? choiceState.currentPosition - 1 : 0);
    }


    //松手播放恢复动画
    private void doBackAnim() {
        View endView = recyclerView.getLayoutManager().findViewByPosition(choiceState.currentPosition);
        choiceState.dragView.animate()
          .translationX(endView.getLeft())
          .translationY(endView.getTop())
          .scaleX(1)
          .scaleY(1)
          .setDuration(200)
          .setListener(new android.animation.Animator.AnimatorListener() {
              @Override
              public void onAnimationStart(android.animation.Animator animation) {

              }

              @Override
              public void onAnimationEnd(android.animation.Animator animation) {
                  removeView(choiceState.dragView);
                  choiceState.setNormal();
                  recyclerView.removeCallbacks(scrollRecyclerViewRunnable);
                  adapter.setInvisiblePosition(-1);
                  //直接获得View 避免更新闪烁
                  RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(choiceState.currentPosition);
                  if (holder != null)
                      holder.itemView.setVisibility(VISIBLE);

              }

              @Override
              public void onAnimationCancel(android.animation.Animator animation) {

              }

              @Override
              public void onAnimationRepeat(android.animation.Animator animation) {

              }
          })
          .start();
    }


    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        recyclerView.addItemDecoration(itemDecoration);
    }

    public void initDragRecycleView(RecyclerView.LayoutManager layoutManager, DragRecycleViewAdapter dragAdapter) {
        this.adapter = dragAdapter;
        scrollOrientationFlag = adapter.getScrollOrientationFlag();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.setOnItemLongClickListener(this::onSelectView);
    }

    //选中某个View后
    private void onSelectView(View view, int position) {
        RecyclerView.ViewHolder viewHolder = adapter.createViewHolder(DragRecycleView.this, 0);
        adapter.bindViewHolder(viewHolder, position);
        choiceState.setDrag();
        choiceState.dragView = viewHolder.itemView;
        LayoutParams lp = new LayoutParams(view.getWidth(), view.getHeight());
        choiceState.dragView.setLayoutParams(lp);
        DragRecycleView.this.addView(choiceState.dragView);
        choiceState.currentPosition = position;
        choiceState.dragView.setScaleX(AnimScale);
        choiceState.dragView.setScaleY(AnimScale);
        choiceState.dragView.setTranslationX(view.getLeft());
        choiceState.dragView.setTranslationY(view.getTop());
        adapter.setInvisiblePosition(position);
        adapter.notifyItemChanged(position);
        recyclerView.scrollToPosition(position);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.addView(recyclerView);
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setAnimScale(float animScale) {
        AnimScale = animScale;
    }

}
