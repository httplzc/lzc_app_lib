package com.yioks.lzclib.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.yioks.lzclib.R;


/**
 * Created by ${User} on 2016/11/11 0011.
 */
public class CircleTextView extends TextView {
    private int Color = android.graphics.Color.TRANSPARENT;
    private Paint paint;
    private float linWidth;
    private int lineColor= android.graphics.Color.WHITE;

    public CircleTextView(Context context) {
        super(context);
        initPaint();
    }

    private void initPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);

    }

    public CircleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initPaint();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleTextView);
        Color = typedArray.getColor(R.styleable.CircleTextView_full_color, android.graphics.Color.WHITE);
        linWidth = typedArray.getDimension(R.styleable.CircleTextView_line_width, 0);
        lineColor = typedArray.getColor(R.styleable.CircleTextView_line_color, android.graphics.Color.WHITE);
        typedArray.recycle();
    }



    public int getColor() {
        return Color;
    }

    public void setColor(int color) {
        Color = color;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public float getLinWidth() {
        return linWidth;
    }

    public void setLinWidth(float linWidth) {
        this.linWidth = linWidth;
    }

    public CircleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = widthMeasureSpec;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int with = getWidth();
        int height = getHeight();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color);
        canvas.drawCircle(with / 2, height / 2, with / 2 - 10, paint);

        if(Math.abs(linWidth)>0.01)
        {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(linWidth);
            paint.setColor(lineColor);
            canvas.drawCircle(with / 2, height / 2, with / 2 - 10, paint);
        }



        super.onDraw(canvas);
    }


}
