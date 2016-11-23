package com.yioks.lzclib.View;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import com.yioks.lzclib.R;

/**
 * Created by ${User} on 2016/8/23 0023.
 */
public class ProgressTextView extends TextView {
    private int max_progress = 100;
    private int current_progress = 0;
    private Context context;
    private ValueAnimator valueAnimator;
    private static final int animTime = 1000;
    private boolean isShowSin = true;


    private Paint mPaint, mCriclePaint, mTextPaint;
    private DrawFilter mDrawFilter;
    private int mTotalHeight, mTotalWidth;
    private int mXoffset = 0;
    private float[] mPointY;
    private float[] mDaymicPointY;
    //波浪线移动速度
    private static final int X_SPEED = 3;
    private int percent = 0;
    private Bitmap bitmap;

    public ProgressTextView(Context context) {
        super(context);
        this.context = context;
        init_progress();

    }

    private void init_progress() {
        this.setText("0%");
        init();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressTextView);
        isShowSin = typedArray.getBoolean(R.styleable.ProgressTextView_isShowSin, true);
        typedArray.recycle();
    }

    public ProgressTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init_progress();
        initAttrs(attrs);
    }

    public ProgressTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init_progress();
        initAttrs(attrs);
    }

    public int getMax_progress() {
        return max_progress;
    }

    public void setMax_progress(int max_progress) {
        if (max_progress < 0 || max_progress < current_progress) {
            return;
        }
        this.max_progress = max_progress;
    }

    public int getCurrent_progress() {
        return current_progress;
    }

    public void setCurrentProgressByAnim(int current_progress) {
        if (current_progress < 0 || current_progress > max_progress) {
            return;
        }
        if (valueAnimator != null) {
            valueAnimator.removeAllUpdateListeners();
            valueAnimator.cancel();
        }
        valueAnimator = ValueAnimator.ofInt(this.current_progress, current_progress);
        valueAnimator.setDuration(animTime);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ProgressTextView.this.current_progress = (int) animation.getAnimatedValue();
            }
        });
        valueAnimator.start();
    }


    public void setCurrentProgressByAnim(int current_progress, int time) {
        if (current_progress < 0 || current_progress > max_progress) {
            return;
        }
        if (valueAnimator != null) {
            valueAnimator.removeAllUpdateListeners();
            valueAnimator.cancel();
        }
        valueAnimator = ValueAnimator.ofInt(this.current_progress, current_progress);
        valueAnimator.setDuration(time);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ProgressTextView.this.current_progress = (int) animation.getAnimatedValue();
            }
        });
        valueAnimator.start();
    }

    public void setCurrentProgress(int current_progress) {
        if (current_progress < 0 || current_progress > max_progress) {
            return;
        }
        if (valueAnimator != null) {
            valueAnimator.removeAllUpdateListeners();
            valueAnimator.cancel();
        }
        ProgressTextView.this.current_progress = current_progress;
    }

    public void reset() {
        max_progress = 100;
        current_progress = 0;
        this.setText("0%");
    }


    private void init() {
        //图片线条（通用）的抗锯齿需要另外设置
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        //实例化一个画笔
        mPaint = new Paint();
        //去除画笔锯齿
        mPaint.setAntiAlias(true);
        //设置画笔风格为实线
        mPaint.setStyle(Paint.Style.FILL);
        //设置画笔颜色
        mPaint.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        //实例化圆的画笔
        mCriclePaint = new Paint(mPaint);
        mCriclePaint.setColor(Color.parseColor("#dddddd"));
        //实例化文字画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        //   bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.load_people);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        this.setText(current_progress + "%");
        if (!isShowSin) {
            super.onDraw(canvas);
            return;
        }
        //去除锯齿
        canvas.setDrawFilter(mDrawFilter);
        runWave();
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int layerId = canvas.saveLayer(0, 0, canvasWidth, canvasHeight, null, Canvas.ALL_SAVE_FLAG);
        canvas.drawCircle(mTotalWidth / 2, mTotalHeight / 2, mTotalWidth / 2, mCriclePaint);
        //canvas.drawBitmap(bitmap,new Rect(0,0,bitmap.getWidth(),bitmap.getHeight()),new Rect(0,0,getWidth(),getHeight()),null);
        //设置颜色混合模式
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //高减去宽除以2使水波纹底部在圆底部，动态改变percent值，在Y轴上变化
        for (int i = 0; i < mTotalWidth; i++) {
            canvas.drawLine(i, mTotalHeight - mDaymicPointY[i] - (mTotalHeight - mTotalWidth) / 2 - current_progress * mTotalWidth / 100, i, mTotalHeight - (mTotalHeight - mTotalWidth) / 2, mPaint);
        }
        //最后将画笔去除Xfermode
        mPaint.setXfermode(null);
        canvas.restoreToCount(layerId);
        mXoffset += X_SPEED;
        if (mXoffset > mTotalWidth) {
            mXoffset = 0;
        }
        postInvalidateDelayed(80);
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalHeight = h;
        mTotalWidth = w;
        //数组的长度为view的宽度
        mPointY = new float[w];
        mDaymicPointY = new float[w];
        //这里我们以view的总宽度为周期，y = a * sin(2π) + b
        for (int i = 0; i < mTotalWidth; i++) {
            mPointY[i] = (float) (h / 25f * (Math.sin(4 * Math.PI * i / w)));
        }
    }

    private void runWave() {
        // 超出屏幕的挪到前面，mXoffset表示第一条水波纹要移动的距离
        int yIntelrval = mPointY.length - mXoffset;
        //使用System.arraycopy方式重新填充第一条波纹的数据
        System.arraycopy(mPointY, 0, mDaymicPointY, mXoffset, yIntelrval);
        System.arraycopy(mPointY, yIntelrval, mDaymicPointY, 0, mXoffset);
    }

    public int getPercent() {
        return percent;
    }

    public boolean isShowSin() {
        return isShowSin;
    }

    public void setIsShowSin(boolean isShowSin) {
        this.isShowSin = isShowSin;
    }

    //    public class refreshLoaddingThread extends Thread {
//        @Override
//        public void run() {
//            while()
//            try {
//                sleep(50);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            synchronized (ProgressTextView.class) {
//                mXoffset += X_SPEED;
//                //如果已经移动到末尾处，则到头重新移动
//                if (mXoffset > mTotalWidth) {
//                    mXoffset = 0;
//                }
//            }
//        }
//    }

}
