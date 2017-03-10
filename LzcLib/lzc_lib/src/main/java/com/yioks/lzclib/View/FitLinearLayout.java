package com.yioks.lzclib.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.yioks.lzclib.R;


/**
 * Created by yioks on 2016/5/5.
 * 正方形 linearlayout
 */
public class FitLinearLayout extends LinearLayout {
    private float ratio=1;
    private Context context;

    public FitLinearLayout(Context context) {
        super(context);
        this.context=context;
    }

    public FitLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        initData(attrs);
    }



    public FitLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        initData(attrs);
    }

    private void initData(AttributeSet attrs) {
        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.FitLinearLayout);
        ratio=typedArray.getFloat(R.styleable.FitLinearLayout_l_ratio,1f);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width=MeasureSpec.getSize(widthMeasureSpec);
        int mode=MeasureSpec.EXACTLY;
        heightMeasureSpec=MeasureSpec.makeMeasureSpec((int) (width*(1f/ratio)),mode);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
