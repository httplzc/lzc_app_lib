package pers.lizechao.android_lib.ui.widget;

import android.view.View;

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
 * Date: 2018-09-14
 * Time: 9:38
 */
public class LabLinearLayoutManager extends LabelView.LabelLayoutManager {
    private LabGridLayoutManager labGridLayoutManager;

    public LabLinearLayoutManager() {
        labGridLayoutManager = new LabGridLayoutManager(1);
    }

    @Override
    void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        labGridLayoutManager.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    void onLayout(boolean changed, int l, int t, int r, int b) {
        labGridLayoutManager.onLayout(changed, l, t, r, b);
    }

    @Override
    void setLabelView(LabelView labelView) {
        super.setLabelView(labelView);
        labGridLayoutManager.setLabelView(labelView);
    }

    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
        labGridLayoutManager.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    @Override
    protected void setMeasuredDimension(int widthMeasureSpec, int heightMeasureSpec) {
        super.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        labGridLayoutManager.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDataSetChange(LabelViewAdapter adapter) {
        super.onDataSetChange(adapter);
        labGridLayoutManager.onDataSetChange(adapter);
    }
}
