package com.yioks.lzclib.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.yioks.lzclib.Adapter.TabViewAdapter;
import com.yioks.lzclib.Data.ScreenData;
import com.yioks.lzclib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${User} on 2016/10/25 0025.
 */
public class TabView extends ViewGroup {
    private Context context;
    private int padding = 0;
    private int padding_t = 0;
    private onTabClickListener onTabClickListener;
    private boolean isMultiSelect = false;
    private List<Integer> choiceList = new ArrayList<>();
    private TabViewAdapter adapter;
    private boolean canChoice = true;
    private boolean canCancelAll = false;

    public TabView(Context context) {
        super(context);
        this.context = context;
    }

    public TabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabView);
        padding = (int) typedArray.getDimension(R.styleable.TabView_padding_landscape, (int) (10 * ScreenData.density));
        padding_t = (int) typedArray.getDimension(R.styleable.TabView_padding_vertical, (int) (12 * ScreenData.density));
        typedArray.recycle();
    }

    public TabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttrs(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
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
            if (child.getMeasuredWidth() + currentWidth + getPaddingRight() <= width) {
                left = currentWidth;
                top = currentHeight;
                currentWidth += child.getMeasuredWidth() + padding;

            } else {
                line++;
                currentWidth = getPaddingLeft();
                left = currentWidth;
                currentHeight += child.getMeasuredHeight() + padding_t;
                top = currentHeight;
                currentWidth += child.getMeasuredWidth() + padding;
            }


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
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            measureChild(view, widthMeasureSpec, heightMeasureSpec);
        }
        int realWidth = GetRealSize(widthMeasureSpec, true);
        int realHeight = GetRealSize(heightMeasureSpec, false);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(realWidth, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(realHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int GetRealSize(int widthMeasureSpec, boolean isWidth) {
        int realSize = 0;
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int Mode = MeasureSpec.getMode(widthMeasureSpec);
        switch (Mode) {
            case MeasureSpec.AT_MOST:
                realSize = getRealSize(isWidth);
                break;
            case MeasureSpec.EXACTLY:
                realSize = size;
                break;
            case MeasureSpec.UNSPECIFIED:
                realSize = getRealSize(isWidth);
                break;
        }
        return realSize;
    }

    private int getRealSize(boolean isWidth) {
        if (isWidth)
            return ScreenData.widthPX;
        else {
            int height = getLineHeight(ScreenData.widthPX);
//            if (height > 300 * ScreenData.density) {
//                height = (int) (300 * ScreenData.density);
//            }
            return height;
        }

    }

    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
    }

    /**
     * 为标签赋值必须在adapter之后调用
     *
     * @param
     */
    public void initData() {
        if (adapter == null)
            return;
        //   this.tabDataList = tabDataList;
        this.removeAllViews();
        for (int i = 0; i < adapter.getCount(); i++) {
            View view = adapter.bindData(i, this);
            final int finalI = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (canChoice) {
                        if (choiceList.contains(Integer.valueOf(finalI))) {
                            if (canCancelAll&&choiceList.size() != 1) {
                                adapter.setChoice(finalI, false, v, choiceList);
                                choiceList.remove(Integer.valueOf(finalI));
                            }
                        } else {
                            if (!isMultiSelect) {
                                for (Integer integer : choiceList) {
                                    adapter.setChoice(integer, false, getChildAt(integer), choiceList);
                                }
                                choiceList.clear();
                            }
                            adapter.setChoice(finalI, true, v, choiceList);
                            choiceList.add(finalI);
                        }
                    }
                    if (onTabClickListener != null)
                        onTabClickListener.onItemClick(v, finalI);
                }
            });
            this.addView(view);
        }
    }


    public interface onTabClickListener {
        void onItemClick(View view, int position);
    }


    public onTabClickListener getOnTabClickListener() {
        return onTabClickListener;
    }

    public void setOnTabClickListener(onTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
    }

    public void setChoice(int position, boolean choice) {
        if (adapter == null || position < 0 || position > getChildCount() - 1)
            return;
        adapter.setChoice(position, choice, getChildAt(position), choiceList);
        if (choice)
            choiceList.add(new Integer(position));
        else
            choiceList.remove(new Integer(position));
    }


    public boolean isMultiSelect() {
        return isMultiSelect;
    }

    public void setIsMultiSelect(boolean isMultiSelect) {
        this.isMultiSelect = isMultiSelect;
    }

    public TabViewAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(TabViewAdapter adapter) {
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

    public boolean isCanChoice() {
        return canChoice;
    }

    public void setCanChoice(boolean canChoice) {
        this.canChoice = canChoice;
    }

    public void setMultiSelect(boolean multiSelect) {
        isMultiSelect = multiSelect;
    }

    public List<Integer> getChoicePositions() {
        return (List<Integer>) ((ArrayList<Integer>) choiceList).clone();
    }

    public int getChoiceCount() {
        return choiceList.size();
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

    public boolean isCanCancelAll() {
        return canCancelAll;
    }

    public void setCanCancelAll(boolean canCancelAll) {
        this.canCancelAll = canCancelAll;
    }
}
