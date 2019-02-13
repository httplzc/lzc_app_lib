package pers.lizechao.android_lib.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import pers.lizechao.android_lib.R;


/**
 * Created by Lzc on 2016/8/5 0005.
 */
public class ImgRotateView extends View {
    private int res = R.drawable.loading_img;
    private boolean isAnim = false;

    public ImgRotateView(Context context) {
        super(context);

    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    private void initDate(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImgRotateView);
        res = typedArray.getResourceId(R.styleable.ImgRotateView_rotate_res, R.drawable.loading_img);
        typedArray.recycle();
    }

    public ImgRotateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDate(context, attrs);
    }

    public ImgRotateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDate(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getVisibility() == VISIBLE)
            setMeasuredDimension(MeasureSpec.makeMeasureSpec(getBackground().getMinimumWidth(), MeasureSpec.AT_MOST),
              MeasureSpec.makeMeasureSpec(getBackground().getMinimumHeight(), MeasureSpec.AT_MOST));
        else
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE)
            startAnim();
        else
            endAnim();
    }

    private void startAnim() {
        if (isAnim)
            return;
        isAnim = true;
        this.setBackgroundResource(res);
        this.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.loading_anim);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        animation.setFillAfter(false);
        this.setAnimation(animation);
        this.startAnimation(animation);
    }

    private void endAnim() {
        isAnim = false;
        Animation animation = this.getAnimation();
        if (animation != null) {
            animation.cancel();
        }
        this.clearAnimation();
    }

    @Override
    protected void onAttachedToWindow() {
        startAnim();
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        endAnim();
        super.onDetachedFromWindow();
    }

}
