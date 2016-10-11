package com.yioks.lzclib.View;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by ${User} on 2016/8/23 0023.
 */
public class ProgressTextView extends TextView {
    private int max_progress=100;
    private int current_progress=0;
    private Context context;
    private ValueAnimator valueAnimator;
    private static final int animTime=1000;
    public ProgressTextView(Context context) {
        super(context);
        this.context=context;
        init_progress();
    }

    private void init_progress() {
        this.setText("0%");
    }

    public ProgressTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init_progress();
    }

    public ProgressTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init_progress();
    }

    public int getMax_progress() {
        return max_progress;
    }

    public void setMax_progress(int max_progress) {
        if(max_progress<0||max_progress<current_progress)
        {
            return;
        }
        this.max_progress = max_progress;
    }

    public int getCurrent_progress() {
        return current_progress;
    }

    public void setCurrentProgressByAnim(int current_progress) {
        if(current_progress<0||current_progress>max_progress)
        {
            return;
        }
        if(valueAnimator!=null)
        {
            valueAnimator.removeAllUpdateListeners();
            valueAnimator.cancel();
        }
        valueAnimator=ValueAnimator.ofInt(this.current_progress,current_progress);
        valueAnimator.setDuration(animTime);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ProgressTextView.this.setText(animation.getAnimatedValue() + "%");
                ProgressTextView.this.current_progress = (int) animation.getAnimatedValue();
            }
        });
        valueAnimator.start();
    }


    public void setCurrentProgressByAnim(int current_progress,int time) {
        if(current_progress<0||current_progress>max_progress)
        {
            return;
        }
        if(valueAnimator!=null)
        {
            valueAnimator.removeAllUpdateListeners();
            valueAnimator.cancel();
        }
        valueAnimator=ValueAnimator.ofInt(this.current_progress,current_progress);
        valueAnimator.setDuration(time);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ProgressTextView.this.setText(animation.getAnimatedValue() + "%");
                ProgressTextView.this.current_progress = (int) animation.getAnimatedValue();
            }
        });
        valueAnimator.start();
    }

    public void setCurrentProgress(int current_progress) {
        if(current_progress<0||current_progress>max_progress)
        {
            return;
        }
        if(valueAnimator!=null)
        {
            valueAnimator.removeAllUpdateListeners();
            valueAnimator.cancel();
        }
        ProgressTextView.super.setText(current_progress + "%");
        ProgressTextView.this.current_progress = current_progress;
    }

    public void reset()
    {
        max_progress=100;
        current_progress=0;
        this.setText("0%");
    }

}
