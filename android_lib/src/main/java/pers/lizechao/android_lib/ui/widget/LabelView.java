package pers.lizechao.android_lib.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import io.reactivex.android.schedulers.AndroidSchedulers;
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
 * Date: 2018-07-12
 * Time: 13:47
 * 标签View
 */
public class LabelView extends ViewGroup {
    private LabelViewAdapter adapter;
    private LabelLayoutManager labelLayoutManager;
    private int padding_h = 0;
    private int padding_v = 0;
    private OnImgClickListener onImgClickListener;
    private LabelViewAdapter.AdapterDataObserver dataObserver;
    private Drawable mDivider;
    private int mDividerPadding;

    public LabelView(@NonNull Context context) {
        super(context);
        init();
    }

    public LabelView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttrs(context, attrs);
    }

    private void init() {
        setWillNotDraw(false);
        dataObserver = new LabelViewAdapter.AdapterDataObserver() {
            @Override
            public void onChanged() {
                long start=System.currentTimeMillis();
                removeAllViews();
                for (int i = 0; i < adapter.getCount(); i++) {
                    View view = adapter.bindData(i, LabelView.this);
                    initItemView(view);
                    addView(view);
                }
                if (labelLayoutManager == null)
                    throw new IllegalStateException("labelLayoutManager必须在Adapter前赋值");
                labelLayoutManager.onDataSetChange(adapter);
                requestLayout();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                for (int i = 0; i < itemCount; i++) {
                    int childIndex = positionStart + i;
                    View view = adapter.bindData(childIndex, LabelView.this);
                    initItemView(view);
                    removeViewAt(childIndex);
                    addView(view, childIndex);
                }
                requestLayout();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                for (int i = 0; i < itemCount; i++) {
                    int childIndex = positionStart + i - itemCount;
                    View view = adapter.bindData(childIndex, LabelView.this);
                    initItemView(view);
                    addView(view, childIndex);
                }
                requestLayout();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                removeViews(positionStart, itemCount);
                requestLayout();
            }

        };
    }

    private void initItemView(View view) {
        view.setOnClickListener(v -> {
            if (onImgClickListener != null)
                onImgClickListener.onItemClick(v, indexOfChild(v));
        });
    }

    public interface OnImgClickListener {
        void onItemClick(View view, int position);
    }


    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LabelView);
        padding_h = (int) typedArray.getDimension(R.styleable.LabelView_label_padding_horizontal, padding_h);
        padding_v = (int) typedArray.getDimension(R.styleable.LabelView_label_padding_vertical, padding_v);
        mDivider = typedArray.getDrawable(R.styleable.LabelView_label_divider);
        mDividerPadding = (int) typedArray.getDimension(R.styleable.LabelView_label_divider_padding, 0);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (adapter == null || adapter.getCount() == 0) {
            super.onMeasure(0, 0);
            return;
        }
        labelLayoutManager.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (adapter == null || adapter.getCount() == 0) {
            return;
        }
        labelLayoutManager.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDivider == null || labelLayoutManager == null) {
            return;
        }
        if (labelLayoutManager instanceof LabGridLayoutManager && ((LabGridLayoutManager) labelLayoutManager).getLineCount() == 1) {
            drawDividersVertical(canvas, (LabGridLayoutManager) labelLayoutManager);
        }
    }


    void drawDividersVertical(Canvas canvas, LabGridLayoutManager labGridLayoutManager) {
        final Integer lineHeights[] = labGridLayoutManager.getLineHeights();
        int currentHeight = getPaddingTop();
        for (int i = 0; i < lineHeights.length - 1; i++) {
            currentHeight += lineHeights[i];
            mDivider.setBounds(getPaddingLeft() + mDividerPadding, currentHeight,
                    getWidth() - getPaddingRight() - mDividerPadding, currentHeight + padding_v);
            mDivider.draw(canvas);
        }
    }

    public OnImgClickListener getOnImgClickListener() {
        return onImgClickListener;
    }

    public void setOnItemClickListener(OnImgClickListener onImgClickListener) {
        this.onImgClickListener = onImgClickListener;
    }

    public int getPadding_h() {
        return padding_h;
    }

    public int getPadding_v() {
        return padding_v;
    }

    public LabelViewAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(LabelViewAdapter adapter) {
        if (this.adapter != null) {
            this.adapter.unregisterAdapterDataObserver(dataObserver);
        }
        this.adapter = adapter;
        if (adapter != null) {
            this.adapter.registerAdapterDataObserver(dataObserver);
            AndroidSchedulers.mainThread().scheduleDirect(adapter::notifyChanged);
        }
    }


    public void setPadding_h(int padding_h) {
        this.padding_h = padding_h;
    }

    public void setPadding_v(int padding_v) {
        this.padding_v = padding_v;
    }

    public void setLabelLayoutManager(LabelLayoutManager labelLayoutManager) {
        this.labelLayoutManager = labelLayoutManager;
        labelLayoutManager.setLabelView(this);
    }


    public static abstract class LabelLayoutManager {
        protected LabelView labelView;

        abstract void onMeasure(int widthMeasureSpec, int heightMeasureSpec);

        abstract void onLayout(boolean changed, int l, int t, int r, int b);

        protected void measureChildWithMargins(View child,
                                               int parentWidthMeasureSpec, int widthUsed,
                                               int parentHeightMeasureSpec, int heightUsed) {
            labelView.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
        }


        protected void setMeasuredDimension(int widthMeasureSpec, int heightMeasureSpec) {
            labelView.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        }

        void setLabelView(LabelView labelView) {
            this.labelView = labelView;
        }

        protected void onDataSetChange(LabelViewAdapter adapter) {

        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }


}
