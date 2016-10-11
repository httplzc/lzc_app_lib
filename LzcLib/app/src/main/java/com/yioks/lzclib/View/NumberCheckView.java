package com.yioks.lzclib.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.yioks.lzclib.R;


/**
 * Created by ${User} on 2016/9/29 0029.
 */
public class NumberCheckView extends CheckBox {
    private int number = 1;
    private Paint paint;
    private int noChoiceColor = 0;
    private int haveChoiceColor = 0;
    private int lineColor = Color.WHITE;
    private Context context;

    public NumberCheckView(Context context) {
        super(context);
        this.context = context;
        initPaint();
    }

    public NumberCheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initPaint();
    }

    public NumberCheckView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initPaint();
    }

    private void initPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);
        noChoiceColor = ContextCompat.getColor(context, R.color.choice_pic_color);
        haveChoiceColor = ContextCompat.getColor(context, R.color.colorPrimary);
        this.setPadding(0,0,0,0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        if (isChecked()) {
            int line = (int) (getWidth() / 20f);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(line);
            paint.setColor(lineColor);
            canvas.drawCircle(width / 2, height / 2, width / 2 - line/2, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(haveChoiceColor);
            canvas.drawCircle(width / 2, height / 2, width / 2 - line, paint);
            String text = ""+number;
            paint.setTextSize(width - line * 4);
           // Log.i("lzc","paint.setTextSiz"+paint.getTextSize());
            int textWidth = (int) paint.measureText(text);
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            paint.setColor(lineColor);
           // Log.i("lzc", "sdsa" + fontMetrics.descent + "--" + fontMetrics.bottom + "--" + fontMetrics.top);
            canvas.drawText(text, (width - textWidth) / 2f, (height-fontMetrics.bottom+fontMetrics.top)/ 2f - fontMetrics.top-2, paint);


        } else {
            int line = (int) (getWidth() / 20f);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(lineColor);
            paint.setStrokeWidth(line);
            paint.setAlpha((int) (0.4f*255));
            canvas.drawCircle(width / 2, height / 2, width / 2 - line/2, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(noChoiceColor);
            paint.setStrokeWidth(0);
            paint.setAlpha((int) (0.4f*255));
            canvas.drawCircle(width / 2, height / 2, width / 2 - line, paint);

        }
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public void setAlpha(float alpha) {
        if (Math.abs(alpha - getAlpha()) <= 0.1)
            return;
        super.setAlpha(alpha);
    }
}
