package com.yioks.lzclib.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;

import com.yioks.lzclib.R;

/**
 * Created by ${User} on 2016/9/6 0006.
 */
public class DrawableCenterButton extends Button {
    private Drawable[] drawables;
    private float textWidth;
    private float bodyWidth;
    //0 left 1 top 2 right 3 bottom
    private int fangxiang=0;
    public DrawableCenterButton(Context context) {
        super(context);
        init();
    }

    public DrawableCenterButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttrs(context,attrs);
    }

    private void initAttrs(Context context,AttributeSet attrs) {
        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.DrawableCenterButton);
        fangxiang=typedArray.getInteger(R.styleable.DrawableCenterButton_fangxiang,0);
        typedArray.recycle();
    }

    public DrawableCenterButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttrs(context,attrs);
    }
    private void init(){
        drawables = getCompoundDrawables();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        textWidth = getPaint().measureText(getText().toString());
        Drawable drawable = drawables[fangxiang];
        int totalWidth = getWidth();
        if (drawable != null) {
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawablePadding = getCompoundDrawablePadding();
            bodyWidth = textWidth + drawableWidth + drawablePadding;
          //  setPadding(0,0,(int)(totalWidth - bodyWidth),0);
        }
    }

    public void setText(String text){
        if(text.equals(getText().toString()))
            return;
        super.setText(text);
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        if(fangxiang==0)
        {
            canvas.translate((width - bodyWidth) / 2, 0);
        }
        else if(fangxiang==1)
        {

        }
        else if(fangxiang==2)
        {
            canvas.translate(-(width - bodyWidth) / 2, 0);
        }
        else
        {

        }

        super.onDraw(canvas);
    }
}
