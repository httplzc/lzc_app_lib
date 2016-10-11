package com.yioks.lzclib.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.yioks.lzclib.R;


/**
 * Created by Administrator on 2016/8/5 0005.
 */
public class LoadImg extends View {
    private int res= R.drawable.load_anim;
    public LoadImg(Context context) {
        super(context);

    }

    private void initDate( Context context,AttributeSet attrs) {
        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.LoadImg);
        res=typedArray.getResourceId(R.styleable.LoadImg_res,R.drawable.load_anim);
        typedArray.recycle();
    }

    public LoadImg(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDate(context, attrs);
    }

    public LoadImg(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDate(context, attrs);
    }


    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        if(visibility==VISIBLE)
        {
            this.setBackgroundResource(res);
            AnimationDrawable animationDrawable= (AnimationDrawable) getBackground();
            animationDrawable.start();
        }
        super.onWindowVisibilityChanged(visibility);
    }
}
