package com.yioks.lzclib.View;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.PropertyValuesHolder;
import com.nineoldandroids.animation.ValueAnimator;
import com.yioks.lzclib.Adapter.DragRecycleViewAdapter;

/**
 * Created by ${User} on 2017/4/5 0005.
 */

public class DragScrollRecycleView extends FrameLayout {
    private RecyclerView recyclerView;
    private ChoiceWrapper choiceWrapper;

    private enum Mode {normal, drag, up}

    ;
    private Mode mode = Mode.normal;
    private DragRecycleViewAdapter dragRecycleViewAdapter;
    private static final float scale = 1.2f;
    private PointF downPoint = new PointF();
    private PointF currentPoint = new PointF();
    private PointF lastPoint = new PointF();
    private static final int autoScrollRatio = 7;
    private static final int autoScrollTime = 60;
    private Runnable runnable;
    private boolean excludeLastView = true;

    public DragScrollRecycleView(Context context) {
        super(context);
        init(context);
    }

    public DragScrollRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DragScrollRecycleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private static class ChoiceWrapper {
        View dragView;
        int choicePosition = -1;
        View choiceView;
        int moveToPosition = -1;
        View moveToView;
    }

    private void init(Context context) {
        recyclerView = new RecyclerView(context);
        runnable = new Runnable() {
            @Override
            public void run() {
                if (choiceWrapper == null || choiceWrapper.dragView == null)
                    return;
                FrameLayout.LayoutParams lp = (LayoutParams) choiceWrapper.dragView.getLayoutParams();
                if (lp.topMargin < 0) {
                    recyclerView.scrollBy(0, -autoScrollRatio);
                    recyclerView.postDelayed(runnable, autoScrollTime);
                } else if (lp.topMargin > recyclerView.getHeight() - lp.height) {
                    recyclerView.scrollBy(0, autoScrollRatio);
                    recyclerView.postDelayed(runnable, autoScrollTime);

                }
            }
        };
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downPoint.set(ev.getX(), ev.getY());
            lastPoint.set(ev.getX(), ev.getY());
        }
        return mode != Mode.normal;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mode != Mode.drag)
            return false;
        currentPoint.set(event.getX(), event.getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                onMoveDo(currentPoint.x - lastPoint.x, currentPoint.y - lastPoint.y);
                break;
            case MotionEvent.ACTION_UP:
                mode = Mode.up;
                doBackAnim();
                break;
        }
        lastPoint.set(currentPoint.x, currentPoint.y);
        return true;
    }


    private void onMoveDo(float distanceX, float distanceY) {
        if (choiceWrapper.dragView != null && mode == Mode.drag) {
            FrameLayout.LayoutParams lp = (LayoutParams) choiceWrapper.dragView.getLayoutParams();
            lp.leftMargin += distanceX;
            lp.topMargin += distanceY;
            choiceWrapper.dragView.setLayoutParams(lp);
            //   Log.i("lzc", "cuurentPOs" + lp.leftMargin + "---" + lp.topMargin);

            calcChange(new PointF(lp.leftMargin + choiceWrapper.dragView.getWidth() / 2, lp.topMargin + choiceWrapper.dragView.getHeight() / 2));
            if (lp.topMargin < 0 || lp.topMargin > recyclerView.getHeight() - lp.height) {
                recyclerView.postDelayed(runnable, autoScrollTime);
            }
        }
    }

    private void calcChange(PointF pointF) {
        for (int i = 0; i < recyclerView.getAdapter().getItemCount(); i++) {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
            if (viewHolder == null)
                continue;
            View child = viewHolder.itemView;
            if (child == null)
                continue;
            RectF rect = new RectF(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
            if (i == 0 || i == 3 || i == 6 || i == 9) {
               // Log.i("lzc", "text" + i + "---" + rect + "--");
            }
            if (rect.contains(pointF.x, pointF.y)) {
                int currentPosition = i;
                //  Log.i("lzc", "current" + currentPosition + "---" + i + "---" + pointF + "---" + rect);
                if (choiceWrapper.moveToPosition == currentPosition || choiceWrapper.choicePosition == currentPosition ||
                        (!excludeLastView && choiceWrapper.moveToPosition == dragRecycleViewAdapter.getItemCount() - 1)) {
                    return;
                }

                int fromPosition = -1;
                int toPosition = -1;
                View fromView = null;
                View toView = null;

                if (choiceWrapper.moveToView != null) {
                    fromPosition = choiceWrapper.moveToPosition;
                    fromView = choiceWrapper.moveToView;

                } else {
                    fromPosition = choiceWrapper.choicePosition;
                    fromView = choiceWrapper.choiceView;
                }
                toPosition = currentPosition;
                toView = child;

                final View finalFromView = fromView;
                final int finalFromPosition = fromPosition;
                final int finalToPosition = toPosition;
                this.post(new Runnable() {
                    @Override
                    public void run() {
                        if (finalFromView != null) {
                            changeViewVisible(finalFromPosition, VISIBLE);
                        }
                        changeViewVisible(finalToPosition, INVISIBLE);
                    }
                });

                dragRecycleViewAdapter.moveData(fromPosition, toPosition);
                dragRecycleViewAdapter.notifyItemMoved(fromPosition, toPosition);
                if (fromPosition == 0)
                    recyclerView.scrollToPosition(0);
                //  dragRecycleViewAdapter.notifyItemMoved(toPosition, fromPosition);
                Log.i("lzc", "fromPosition" + fromPosition + "---" + toPosition);
                choiceWrapper.moveToPosition = toPosition;
                choiceWrapper.moveToView = toView;
                choiceWrapper.choicePosition = -1;
                choiceWrapper.choiceView = null;

                return;

            }

        }


    }

    private void onBackAnimFinish() {
        DragScrollRecycleView.this.removeView(choiceWrapper.dragView);
        mode = Mode.normal;
        recyclerView.removeCallbacks(runnable);
        if (choiceWrapper.choiceView != null) {
            changeViewVisible(choiceWrapper.choicePosition, VISIBLE);
        }

        if (choiceWrapper.moveToView != null) {
            changeViewVisible(choiceWrapper.moveToPosition, VISIBLE);


        }
    }


    private void changeViewVisible(int position, int visible) {
        if (visible == VISIBLE) {
            dragRecycleViewAdapter.removeInvisiblePosition(position);
        } else {
            dragRecycleViewAdapter.addInvisiblePosition(position);
        }
        if (recyclerView.getAdapter().getItemCount() == recyclerView.getChildCount()) {
            View view =recyclerView.getLayoutManager().findViewByPosition(position);
            view.setVisibility(visible);
            Log.i("lzc","position  "+position+"    "+visible);
        } else {
            dragRecycleViewAdapter.notifyItemChanged(position);
        }

    }


    private void doBackAnim() {
        Log.i("lzc", "backAnim" + choiceWrapper.choicePosition + "---" + choiceWrapper.moveToPosition + "--" + choiceWrapper.choiceView);
        View endView = null;
        if (choiceWrapper.choiceView != null) {
            endView = choiceWrapper.choiceView;
        } else {
            endView = recyclerView.getLayoutManager().findViewByPosition(choiceWrapper.moveToPosition);
        }
        PropertyValuesHolder propertyValuesHolder1 = PropertyValuesHolder.ofFloat("marginLeft", choiceWrapper.dragView.getLeft(), endView.getLeft());
        PropertyValuesHolder propertyValuesHolder2 = PropertyValuesHolder.ofFloat("marginTop", choiceWrapper.dragView.getTop(), endView.getTop());
        PropertyValuesHolder propertyValuesHolder3 = PropertyValuesHolder.ofFloat("scale", scale, 1);
        ValueAnimator valueAnimator = ValueAnimator.ofPropertyValuesHolder(propertyValuesHolder1, propertyValuesHolder2, propertyValuesHolder3);
        valueAnimator.setDuration(200);
        final View finalEndView = endView;
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float marginLeft = (float) animation.getAnimatedValue("marginLeft");
                float marginTop = (float) animation.getAnimatedValue("marginTop");
                float scale = (float) animation.getAnimatedValue("scale");
                FrameLayout.LayoutParams layoutParams = (LayoutParams) choiceWrapper.dragView.getLayoutParams();
                layoutParams.width = (int) (finalEndView.getWidth() * scale);
                layoutParams.height = (int) (finalEndView.getHeight() * scale);
                layoutParams.leftMargin = (int) marginLeft;
                layoutParams.topMargin = (int) marginTop;
                choiceWrapper.dragView.setLayoutParams(layoutParams);
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                onBackAnimFinish();

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

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        recyclerView.addItemDecoration(itemDecoration);
    }

    public void initDragRecycleView(final RecyclerView.LayoutManager layoutManager, DragRecycleViewAdapter dragRecycleViewAdapterTemp) {
        this.dragRecycleViewAdapter = dragRecycleViewAdapterTemp;
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(dragRecycleViewAdapter);
        recyclerView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        dragRecycleViewAdapter.addOnClickItemListener(new DragRecycleViewAdapter.onClickItemListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                RecyclerView.ViewHolder viewHolder = dragRecycleViewAdapter.createViewHolder(DragScrollRecycleView.this, 0);
                dragRecycleViewAdapter.bindViewHolder(viewHolder, position);
                mode = Mode.drag;
                choiceWrapper = new ChoiceWrapper();
                choiceWrapper.dragView = viewHolder.itemView;
                Log.i("lzc", " choiceWrapper.dragView == null;" + (choiceWrapper.dragView == null));
                choiceWrapper.choicePosition = position;
                choiceWrapper.choiceView = view;
                FrameLayout.LayoutParams lp = (LayoutParams) choiceWrapper.dragView.getLayoutParams();
                lp.width = (int) (view.getWidth() * scale);
                lp.height = (int) (view.getHeight() * scale);
                lp.leftMargin = (int) (view.getLeft() - view.getWidth() * (scale - 1) / 2f);
                lp.topMargin = (int) (view.getTop() - view.getHeight() * (scale - 1) / 2f);
                choiceWrapper.dragView.setLayoutParams(lp);
                DragScrollRecycleView.this.addView(choiceWrapper.dragView);
                //隐藏原来
                changeViewVisible(position, INVISIBLE);
                Log.i("lzc", "onclickPosition" + position);
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.addView(recyclerView);
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

}
