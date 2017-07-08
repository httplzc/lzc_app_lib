package com.yioks.lzclib.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.yioks.lzclib.Adapter.ImgListAdapter;
import com.yioks.lzclib.Data.ScreenData;
import com.yioks.lzclib.R;

/**
 * Created by ${User} on 2016/10/25 0025.
 */
public class ImgListView extends ViewGroup {
    private Context context;
    private int padding = 0;
    private int padding_t = 0;
    private onImgClickListener onImgClickListener;
    private ImgListAdapter adapter;
    private final static int lineCount = 3;

    public ImgListView(Context context) {
        super(context);
        this.context = context;
    }

    public ImgListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImgListView);
        padding = (int) typedArray.getDimension(R.styleable.ImgListView_img_list_padding_landscape, (int) (10 * ScreenData.density));
        padding_t = (int) typedArray.getDimension(R.styleable.ImgListView_img_list_padding_vertical, (int) (12 * ScreenData.density));
        typedArray.recycle();
    }

    public ImgListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttrs(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (adapter == null || adapter.getCount() == 0) {
            return;
        }

        int width = getWidth();
        int height = getHeight();
        int currentWidth = getPaddingLeft();
        int currentHeight = getPaddingTop();
        int line = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getMeasuredWidth() > width - getPaddingLeft() - getPaddingRight()) {
                child.layout(0, 0, 0, 0);
                continue;
            }
            int left;
            int top;
            if (i == 0) {
                left = 0;
                top = 0;

            } else {
                if (i % (adapter.getCount() == 4 ? 2 : 3) != 0) {
                    left = currentWidth;
                    top = currentHeight;
                } else {
                    line++;
                    currentWidth = getPaddingLeft();
                    left = currentWidth;
                    currentHeight += child.getMeasuredHeight() + padding_t;
                    top = currentHeight;
                }
            }
            currentWidth += child.getMeasuredWidth() + padding;


            int right = left + child.getMeasuredWidth();
            int bottom = top + child.getMeasuredHeight();
            child.layout(left, top, right, bottom);
        }
    }

    private int getLineHeight(int width) {
        int currentWidth = getPaddingLeft();
        int currentHeight = getPaddingTop();
        int line = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getMeasuredWidth() > width - getPaddingLeft() - getPaddingRight()) {
                continue;
            }
            int left;
            int top;
            if (child.getMeasuredWidth() + currentWidth + getPaddingRight() <= width) {
                left = currentWidth;
                top = currentHeight;
                currentWidth += child.getMeasuredWidth() + padding;

            } else {
                line++;
                currentWidth = getPaddingLeft();
                left = currentWidth;
                top = currentHeight;
                currentHeight += child.getMeasuredHeight() + padding_t;
                currentWidth += child.getMeasuredWidth() + padding;
            }
        }
        if (getChildCount() != 0) {
            return (line + 1) * getChildAt(0).getMeasuredHeight() + line * padding_t + getPaddingBottom() + getPaddingTop();
        } else {
            return 0;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (adapter == null || adapter.getCount() == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int realWidth = GetWidthRealSize(widthMeasureSpec) + getPaddingLeft() + getPaddingRight();
        int childWidth = calcChildWidth(realWidth - getPaddingLeft() - getPaddingRight());
        for (int i = 0; i < getChildCount(); i++) {
            measureChild(getChildAt(i), MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY));
        }
        int line = 0;
        if (adapter.getCount() == 1)
            line = 1;
        else {
            line = (adapter.getCount() - 1) / lineCount + 1;
        }
        int realHeight = 0;
        realHeight = line == 1 ? childWidth : line * childWidth + (line - 1) * padding_t;
        realHeight += getPaddingTop() + getPaddingBottom();
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(realWidth, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(realHeight, MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    private int calcChildWidth(int realWidth) {
        return (realWidth - (lineCount - 1) * padding) / 3;
    }

    private int GetWidthRealSize(int widthMeasureSpec) {
        int realSize = 0;
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int Mode = MeasureSpec.getMode(widthMeasureSpec);
        switch (Mode) {
            case MeasureSpec.AT_MOST:
                realSize = ScreenData.widthPX;
                break;
            case MeasureSpec.EXACTLY:
                realSize = size;
                break;
            case MeasureSpec.UNSPECIFIED:
                realSize = ScreenData.widthPX;
                break;
        }
        return realSize;
    }


    /**
     * 为标签赋值必须在adapter之后调用
     *
     * @param
     */
    private void initData() {
        if (adapter == null)
            return;
        //   this.tabDataList = tabDataList;
        this.removeAllViews();
        for (int i = 0; i < adapter.getCount(); i++) {
            View view = adapter.bindData(i, this);
            this.addView(view);
            final int finalI = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onImgClickListener != null)
                        onImgClickListener.onItemClick(v, finalI);
                }
            });
        }
    }


    public interface onImgClickListener {
        void onItemClick(View view, int position);
    }


    public onImgClickListener getOnImgClickListener() {
        return onImgClickListener;
    }

    public void setOnImgClickListener(onImgClickListener onImgClickListener) {
        this.onImgClickListener = onImgClickListener;
    }


    public ImgListAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(ImgListAdapter adapter) {
        this.adapter = adapter;
        initData();
    }


    public int getVerticalPadding() {
        return padding_t;
    }

    //设置上下间距
    public void setVerticalPadding(int padding_t) {
        this.padding_t = padding_t;
    }

    public int getHorizontalPadding() {
        return padding;
    }

    //设置左右间距
    public void setHorizontalPadding(int padding) {
        this.padding = padding;
    }


    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public int getPadding_t() {
        return padding_t;
    }

    public void setPadding_t(int padding_t) {
        this.padding_t = padding_t;
    }
}
