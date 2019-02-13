package pers.lizechao.android_lib.ui.widget;

import android.annotation.SuppressLint;
import android.view.View;

import com.annimon.stream.Stream;

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
 * Time: 14:14
 * 方格布局
 */
public class LabUnfitGridLayoutManager extends LabelView.LabelLayoutManager {
    private int lineCount = 3;
    private int widthSpan=0;
    //每行最高值
    private Integer lineHeights[];

    private int lines;


    public LabUnfitGridLayoutManager(int lineCount) {
        this.lineCount = lineCount;

    }

    @Override
    protected void onDataSetChange(LabelViewAdapter adapter) {

    }

    @SuppressLint("DrawAllocation")
    @Override
    void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (labelView.getChildCount() % lineCount == 0) {
            lines = labelView.getChildCount() / lineCount;
        } else {
            lines = labelView.getChildCount() / lineCount + 1;
        }
        if (lineHeights==null||lines != lineHeights.length)
            lineHeights = new Integer[lines];
        int size = View.MeasureSpec.getSize(widthMeasureSpec);
        int mode = View.MeasureSpec.getMode(widthMeasureSpec);
        int realWidth;
        if (mode == View.MeasureSpec.AT_MOST || mode == View.MeasureSpec.EXACTLY) {
            realWidth = size;
        } else {
            throw new IllegalStateException("放入没有宽度的容器没有意义！");
        }

        int currentLineIndex = 0;
        int currentMaxHeight = 0;
        int childWidth=0;
        for (int i = 0; i < labelView.getChildCount(); i++) {
            View child = labelView.getChildAt(i);
            child.measure(View.MeasureSpec.makeMeasureSpec(realWidth, View.MeasureSpec.AT_MOST),
              View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            childWidth=Math.max(child.getMeasuredWidth(),childWidth);
            currentMaxHeight = Math.max(currentMaxHeight, child.getMeasuredHeight());
            if ((i + 1) % lineCount == 0 || i == labelView.getChildCount() - 1) {
                lineHeights[currentLineIndex] = currentMaxHeight;
                currentLineIndex++;
                currentMaxHeight = 0;
            }
        }

        int realHeight = Stream.of(lineHeights).reduce((s1, s2) -> s1 + s2).orElse(0)
          + (lines - 1) * labelView.getPadding_v()
          + labelView.getPaddingTop()
          + labelView.getPaddingBottom();
        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(realWidth, View.MeasureSpec.EXACTLY);
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(realHeight, View.MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        widthSpan=(realWidth-childWidth*lineCount-labelView.getPaddingLeft()-labelView.getPaddingRight()
        -(lineCount-1)*labelView.getPadding_h())/(lineCount-1);
    }

    @Override
    void onLayout(boolean changed, int l, int t, int r, int b) {
        if (lineHeights == null || lineHeights.length == 0) {
            return;
        }
        int width = labelView.getWidth();
        int height = labelView.getHeight();
        int currentLeft = labelView.getPaddingLeft();
        int currentTop = labelView.getPaddingTop();
        int lineIndex = 0;
        for (int i = 0; i < labelView.getChildCount(); i++) {
            View child = labelView.getChildAt(i);
            //超出最大宽度
            if (child.getMeasuredWidth() > width - labelView.getPaddingLeft() - labelView.getPaddingRight()) {
                child.layout(0, 0, 0, 0);
                continue;
            }
            if (i != 0 && i % lineCount == 0) {
                currentLeft = labelView.getPaddingLeft();
                currentTop += labelView.getPadding_v() + lineHeights[lineIndex];
                lineIndex++;
            }
            int left = currentLeft;
            int top = currentTop;
            int right = left + child.getMeasuredWidth();
            int bottom = top + lineHeights[lineIndex];
            child.layout(left, top, right, bottom);
            currentLeft = right + widthSpan;
        }
    }

    /**
     *
     * @return 每行几个
     */
    public int getLineCount() {
        return lineCount;
    }

    //总共几行
    public int getLines() {
        return lines;
    }

    public Integer[] getLineHeights() {
        return lineHeights;
    }
}
