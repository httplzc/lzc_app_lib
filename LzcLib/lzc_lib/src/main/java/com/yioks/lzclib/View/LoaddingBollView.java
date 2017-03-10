package com.yioks.lzclib.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.PropertyValuesHolder;
import com.nineoldandroids.animation.ValueAnimator;
import com.yioks.lzclib.R;

/**
 * Created by ${User} on 2016/9/6 0006.
 */
public class LoaddingBollView extends View {
    private int width;
    private int height;
    private int first_bollX;
    private int first_bollY;
    private int first_scale;
    private int sec_scale;
    private int sec_bollX;
    private int sec_bollY;
    private Paint paint;
    private int first_boll_color;
    private int sec_boll_color;
    private int maxScale;
    private Context context;
    private static final int time=1600;
    private com.nineoldandroids.animation.ValueAnimator first_boll_animer;
   // private com.nineoldandroids.animation.ValueAnimator sec_boll_animer;

    public LoaddingBollView(Context context) {
        super(context);
        initPaint();
        this.context = context;
    }

    private void initPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
      //  first_boll_color = ContextCompat.getColor(context, R.color.blue);
        first_boll_color= Color.parseColor("#009ACD");
        sec_boll_color = ContextCompat.getColor(context, R.color.orange);

    }

    public LoaddingBollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initPaint();
    }

    public LoaddingBollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (first_scale < sec_scale) {
            drawFirst(canvas);
            drawSec(canvas);
        } else {
            drawSec(canvas);
            drawFirst(canvas);
        }
        super.onDraw(canvas);
    }

    private void drawFirst(Canvas canvas) {
        paint.setColor(first_boll_color);
        canvas.drawCircle(first_bollX, first_bollY, first_scale, paint);
    }

    private void drawSec(Canvas canvas) {
        paint.setColor(sec_boll_color);
        canvas.drawCircle(sec_bollX, sec_bollY, sec_scale, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            maxScale = (int) (getWidth() / 3f);
            first_bollY = getHeight() / 2;
            first_scale = (int) (maxScale * 2f / 3f);
            sec_bollY = getHeight() / 2;
            sec_scale = (int) (maxScale * 2f / 3f);
            calcLocation();
        }
    };


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.EXACTLY;
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (width*0.5f), mode);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);


    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.postDelayed(runnable, 150);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (first_boll_animer != null) {
            first_boll_animer.removeAllUpdateListeners();
            first_boll_animer = null;
        }
//        if (sec_boll_animer != null) {
//            sec_boll_animer.removeAllUpdateListeners();
//            sec_boll_animer = null;
//        }
    }

    private void calcLocation() {
        int maxRight = (int) (getWidth() - maxScale / 2f);
        int maxleft = (int) (maxScale / 2f);
        PropertyValuesHolder first_propertyValuesHolderTrans = PropertyValuesHolder.ofInt("first_X", maxleft, maxRight, maxleft);
        PropertyValuesHolder first_propertyValuesHolderScale = PropertyValuesHolder.ofFloat("first_Scale", first_scale * 2f / 3f, first_scale / 3f, first_scale * 2f / 3f, first_scale, first_scale * 2f / 3f);

        PropertyValuesHolder sec_propertyValuesHolderTrans = PropertyValuesHolder.ofInt("sec_X", maxRight, maxleft, maxRight);
        PropertyValuesHolder sec_propertyValuesHolderScale = PropertyValuesHolder.ofFloat("sec_Scale", first_scale * 2f / 3f, first_scale, first_scale * 2f / 3f, first_scale / 3f, first_scale * 2f / 3f);

        if (first_boll_animer != null) {
            first_boll_animer.removeAllUpdateListeners();
        }
//        if (sec_boll_animer != null) {
//            sec_boll_animer.removeAllUpdateListeners();
//        }
        first_boll_animer = ValueAnimator.ofPropertyValuesHolder(first_propertyValuesHolderScale, first_propertyValuesHolderTrans, sec_propertyValuesHolderScale, sec_propertyValuesHolderTrans);
        //  sec_boll_animer = ValueAnimator.ofPropertyValuesHolder(sec_propertyValuesHolderTrans, sec_propertyValuesHolderScale);

        first_boll_animer.setDuration(time);
        //     sec_boll_animer.setDuration(2000);

        first_boll_animer.setRepeatMode(ValueAnimator.RESTART);
        first_boll_animer.setRepeatCount(ValueAnimator.INFINITE);
//
//        sec_boll_animer.setRepeatMode(ValueAnimator.RESTART);
//        sec_boll_animer.setRepeatCount(ValueAnimator.INFINITE);

        first_boll_animer.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                first_bollX = (int) animation.getAnimatedValue("first_X");
                float first_scale_f = (float) animation.getAnimatedValue("first_Scale");
                first_scale = (int) first_scale_f;

                sec_bollX = (int) animation.getAnimatedValue("sec_X");
                float sec_scale_f = (float) animation.getAnimatedValue("sec_Scale");
                sec_scale = (int) sec_scale_f;
                invalidate();
            }
        });
        first_boll_animer.setInterpolator(new LinearInterpolator());
        first_boll_animer.start();


    }


}
