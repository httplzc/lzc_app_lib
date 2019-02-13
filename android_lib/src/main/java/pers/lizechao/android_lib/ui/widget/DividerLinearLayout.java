package pers.lizechao.android_lib.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import pers.lizechao.android_lib.R;

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
 * Date: 2018-09-17
 * Time: 11:23
 */
public class DividerLinearLayout extends LinearLayout {
    private Drawable mDivider;
    private int mDividerWidth;
    private int mDividerHeight;
    private int mDividerPaddingLeft;
    private int mDividerPaddingRight;
    private int mShowDividers = SHOW_DIVIDER_MIDDLE;

    public DividerLinearLayout(Context context) {
        super(context);
    }

    public DividerLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(
          attrs, R.styleable.DividerLinearLayout, 0, 0);
        mDivider = a.getDrawable(R.styleable.DividerLinearLayout_dl_divide);
        mDividerWidth = (int) a.getDimension(R.styleable.DividerLinearLayout_dl_divide_width, 0);
        mDividerHeight = (int) a.getDimension(R.styleable.DividerLinearLayout_dl_divide_height, 0);
        mDividerPaddingLeft = (int) a.getDimension(R.styleable.DividerLinearLayout_dl_divide_padding_left, 0);
        mDividerPaddingRight = (int) a.getDimension(R.styleable.DividerLinearLayout_dl_divide_padding_right, 0);
        a.recycle();

    }

    public DividerLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDivider == null) {
            return;
        }

        if (getOrientation() == VERTICAL) {
            drawDividersVertical(canvas);
        } else {
            drawDividersHorizontal(canvas);
        }
    }


    void drawDividersHorizontal(Canvas canvas) {
        final int count = getVirtualChildCount();
        final boolean isLayoutRtl = false;
        for (int i = 0; i < count; i++) {
            final View child = getVirtualChildAt(i);
            if (child != null && child.getVisibility() != GONE) {
                if (hasDividerBeforeChildAt(i)) {
                    final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    final int position;
                    if (isLayoutRtl) {
                        position = child.getRight() + lp.rightMargin;
                    } else {
                        position = child.getLeft() - lp.leftMargin - mDividerWidth;
                    }
                    drawVerticalDivider(canvas, position);
                }
            }
        }

        if (hasDividerBeforeChildAt(count)) {
            final View child = getLastNonGoneChild();
            int position;
            if (child == null) {
                if (isLayoutRtl) {
                    position = getPaddingLeft();
                } else {
                    position = getWidth() - getPaddingRight() - mDividerWidth;
                }
            } else {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (isLayoutRtl) {
                    position = child.getLeft() - lp.leftMargin - mDividerWidth;
                } else {
                    position = child.getRight() + lp.rightMargin;
                }
            }
            drawVerticalDivider(canvas, position);
        }
    }

    protected void drawDividersVertical(Canvas canvas) {
        final int count = getVirtualChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getVirtualChildAt(i);
            if (child != null && child.getVisibility() != GONE) {
                if (hasDividerBeforeChildAt(i)) {
                    final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    final int top = child.getTop() - lp.topMargin - mDividerHeight;
                    drawHorizontalDivider(canvas, top);
                }
            }
        }

        if (hasDividerBeforeChildAt(count)) {
            final View child = getLastNonGoneChild();
            int bottom = 0;
            if (child == null) {
                bottom = getHeight() - getPaddingBottom() - mDividerHeight;
            } else {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                bottom = child.getBottom() + lp.bottomMargin;
            }
            drawHorizontalDivider(canvas, bottom);
        }
    }

    protected boolean hasDividerBeforeChildAt(int childIndex) {
        if (childIndex == getVirtualChildCount()) {
            // Check whether the end divider should draw.
            return (mShowDividers & SHOW_DIVIDER_END) != 0;
        }
        boolean allViewsAreGoneBefore = allViewsAreGoneBefore(childIndex);
        if (allViewsAreGoneBefore) {
            // This is the first view that's not gone, check if beginning divider is enabled.
            return (mShowDividers & SHOW_DIVIDER_BEGINNING) != 0;
        } else {
            return (mShowDividers & SHOW_DIVIDER_MIDDLE) != 0;
        }
    }

    /**
     * Checks whether all (virtual) child views before the given index are gone.
     */
    private boolean allViewsAreGoneBefore(int childIndex) {
        for (int i = childIndex - 1; i >= 0; i--) {
            final View child = getVirtualChildAt(i);
            if (child != null && child.getVisibility() != GONE) {
                return false;
            }
        }
        return true;
    }


    void drawHorizontalDivider(Canvas canvas, int top) {
        mDivider.setBounds(getPaddingLeft() + mDividerPaddingLeft, top,
          getWidth() - getPaddingRight() - mDividerPaddingRight, top + mDividerHeight);
        mDivider.draw(canvas);
    }

    void drawVerticalDivider(Canvas canvas, int left) {
        mDivider.setBounds(left, getPaddingTop() + mDividerPaddingLeft,
          left + mDividerWidth, getHeight() - getPaddingBottom() - mDividerPaddingRight);
        mDivider.draw(canvas);
    }

    /**
     * Finds the last child that is not gone. The last child will be used as the reference for
     * where the end divider should be drawn.
     */
    private View getLastNonGoneChild() {
        for (int i = getVirtualChildCount() - 1; i >= 0; i--) {
            final View child = getVirtualChildAt(i);
            if (child != null && child.getVisibility() != GONE) {
                return child;
            }
        }
        return null;
    }

    int getVirtualChildCount() {
        return getChildCount();
    }

    @Nullable
    View getVirtualChildAt(int index) {
        return getChildAt(index);
    }

}
