package com.yioks.lzclib.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.yioks.lzclib.R;


/**
 * Created by ${User} on 2016/9/27 0027.
 */
public class ViewPagerIndicator extends View {
    private Paint paint;
    private int circle_background_color;
    private int circle_foreground_color;
    private Context context;
    //白点数
    private int count = 3;
    //实际白点偏移量
    private int offX;
    private int position;
    //当前偏移量
    private float offset;

    public ViewPagerIndicator(Context context) {
        super(context);
        this.context = context;
        initPaint();
    }

    public ViewPagerIndicator(Context context, int count) {
        this(context);
        this.count = count;
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttr(attrs);
        initPaint();
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        count = typedArray.getInteger(R.styleable.ViewPagerIndicator_count, 3);
        typedArray.recycle();
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttr(attrs);
        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        circle_background_color = ContextCompat.getColor(context, R.color.yindao_circle_b);
        circle_foreground_color = ContextCompat.getColor(context, R.color.yindao_circle_f);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth() / 7;
        int height = (int) (getHeight() * 4.8f / 5f);
        int unit = width / (count * 2 - 1);
        int first = (getWidth() - width) / 2;
        paint.setColor(circle_background_color);
        for (int i = 0; i < count; i++) {
            canvas.drawCircle(first + (i * 2 * unit) + unit / 2, height, unit / 2, paint);
        }
        paint.setColor(circle_foreground_color);
        calcOffX(unit);
        canvas.drawCircle(first + offX + unit / 2, height, unit / 2, paint);
        super.onDraw(canvas);
    }

    private void calcOffX(int unit) {
        offX = (int) (position * unit * 2 + offset * 2 * unit);
    }

    public int getOffX() {
        return offX;
    }

    public void setOffX(int position, float offset) {
        this.position = position;
        this.offset = offset;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
