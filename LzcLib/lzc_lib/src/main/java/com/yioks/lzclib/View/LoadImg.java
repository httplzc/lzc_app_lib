package com.yioks.lzclib.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.yioks.lzclib.R;


/**
 * Created by Administrator on 2016/8/5 0005.
 */
public class LoadImg extends FrameLayout {
    private int res = R.drawable.load_anim;
    private View animView;

    public LoadImg(Context context) {
        super(context);

    }

    private void initDate(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadImg);
        res = typedArray.getResourceId(R.styleable.LoadImg_res, R.drawable.load_anim);
        typedArray.recycle();
        animView = new View(context);
        this.addView(animView);
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
    public void setVisibility(int visibility) {
//        if (visibility != VISIBLE) {
//            this.clearAnimation();
//            this.setAnimation(null);
//        }
        super.setVisibility(visibility);
    }

    @Override
    protected void onAttachedToWindow() {

        animView.setBackgroundResource(R.drawable.loading_img);
        animView.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.loading_anim);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        animation.setFillAfter(false);
        animView.setAnimation(animation);
        animView.startAnimation(animation);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        Animation animation = animView.getAnimation();
        if (animation != null) {
            animation.cancel();
        }
        animView.clearAnimation();
        super.onDetachedFromWindow();
    }

}
