package pers.lizechao.android_lib.ui.widget;

import android.annotation.SuppressLint;
import android.view.View;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

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
 * Time: 14:29
 * 流式布局
 */
public class LabFlowLayoutManager extends LabelView.LabelLayoutManager {
    //每行行高
    private final List<Integer> lineHeightList = new ArrayList<>();
    //每个View对应的行
    private int childLine[];

    @SuppressLint("DrawAllocation")
    @Override
    void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        childLine = new int[labelView.getChildCount()];
        lineHeightList.clear();
        int size = View.MeasureSpec.getSize(widthMeasureSpec);
        int mode = View.MeasureSpec.getMode(widthMeasureSpec);
        int realWidth;
        if (mode == View.MeasureSpec.AT_MOST || mode == View.MeasureSpec.EXACTLY) {
            realWidth = size;
        } else {
            throw new IllegalStateException("放入没有宽度的容器没有意义！");
        }
        int currentWidth = labelView.getPaddingLeft();

        int currentMaxHeight = 0;
        for (int i = 0; i < labelView.getChildCount(); i++) {
            View child = labelView.getChildAt(i);
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
            //判断是否需要换行
            if (currentWidth + child.getMeasuredWidth() + labelView.getPaddingRight() > realWidth) {
                currentWidth = labelView.getPaddingLeft();
                lineHeightList.add(currentMaxHeight);
                currentMaxHeight = 0;
            }
            currentWidth += child.getMeasuredWidth() + labelView.getPadding_h();
            childLine[i] = lineHeightList.size();
            currentMaxHeight = Math.max(currentMaxHeight, child.getMeasuredHeight());

        }
        //最后一行
        lineHeightList.add(currentMaxHeight);
        //总高度
        int lineHeight = Stream.of(lineHeightList).reduce((a, b) -> a + b).get()
                + labelView.getPadding_v() * (lineHeightList.size() - 1)
                + labelView.getPaddingTop()
                + labelView.getPaddingBottom();
        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(realWidth, View.MeasureSpec.EXACTLY);
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(lineHeight, View.MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = labelView.getWidth();
        int currentLeft = labelView.getPaddingLeft();
        int currentTop = labelView.getPaddingTop();
        for (int i = 0; i < labelView.getChildCount(); i++) {
            View child = labelView.getChildAt(i);
            int childWith = child.getMeasuredWidth();
            //超出最大宽度
            if (childWith > width - labelView.getPaddingLeft() - labelView.getPaddingRight()) {
                childWith = width - labelView.getPaddingLeft() - labelView.getPaddingRight();
            }
            //是否换行
            if (childLine[i] != 0 && childLine[i] != childLine[i - 1]) {
                currentLeft = labelView.getPaddingLeft();
                currentTop += labelView.getPadding_v() + lineHeightList.get(childLine[i - 1]);
            }
            int left = currentLeft;
            int top = currentTop;
            int right = left + childWith;
            int bottom = top + lineHeightList.get(childLine[i]);
            child.layout(left, top, right, bottom);
            currentLeft = right + labelView.getPadding_h();
        }
    }
}
