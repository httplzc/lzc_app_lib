package pers.lizechao.android_lib.support.img.pick;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import pers.lizechao.android_lib.R;


/**
 * Created by Lzc on 2016/9/29 0029.
 */
public class NumberCheckView extends View {
    private int numberCheckNumber = 1;
    private boolean numberCheckCheck = false;
    private Paint paint;
    private int noChoiceColor = 0;
    private int haveChoiceColor = 0;
    private final int lineColor = Color.WHITE;
    private final Context context;

    public NumberCheckView(Context context) {
        super(context);
        this.context = context;
        initPaint();
    }

    public NumberCheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initPaint();
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.NumberCheckView);
        setNumberCheckNumber(typedArray.getIndex(R.styleable.NumberCheckView_numberCheckNumber));
        setNumberCheckCheck(typedArray.getBoolean(R.styleable.NumberCheckView_numberCheckCheck,false));
        typedArray.recycle();
    }


    private void initPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);
        noChoiceColor = ContextCompat.getColor(context, R.color.pic_number_check_no_choice_color);
        haveChoiceColor = ContextCompat.getColor(context, R.color.pic_number_check_choice_color);
        this.setPadding(0, 0, 0, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        if (isNumberCheckCheck()) {
            int line = (int) (getWidth() / 20f);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(line);
            paint.setColor(lineColor);
            canvas.drawCircle(width / 2, height / 2, width / 2 - line / 2, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(haveChoiceColor);
            canvas.drawCircle(width / 2, height / 2, width / 2 - line, paint);
            String text = "" + numberCheckNumber;
            paint.setTextSize(width - line * 4);
            int textWidth = (int) paint.measureText(text);
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            paint.setColor(lineColor);
            canvas.drawText(text, (width - textWidth) / 2f, (height - fontMetrics.bottom + fontMetrics.top) / 2f - fontMetrics.top - 2, paint);


        } else {
            int line = (int) (getWidth() / 20f);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(lineColor);
            paint.setStrokeWidth(line);
            paint.setAlpha((int) (0.4f * 255));
            canvas.drawCircle(width / 2, height / 2, width / 2 - line / 2, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(noChoiceColor);
            paint.setStrokeWidth(0);
            paint.setAlpha((int) (0.4f * 255));
            canvas.drawCircle(width / 2, height / 2, width / 2 - line, paint);

        }
    }

    public int getNumberCheckNumber() {
        return numberCheckNumber;
    }

    public void setNumberCheckNumber(int numberCheckNumber) {
        this.numberCheckNumber = numberCheckNumber;
        invalidate();
    }

    public boolean isNumberCheckCheck() {
        return numberCheckCheck;
    }

    public void setNumberCheckCheck(boolean numberCheckCheck) {
        this.numberCheckCheck = numberCheckCheck;
        invalidate();
    }

    @Override
    public void setAlpha(float alpha) {
        if (Math.abs(alpha - getAlpha()) <= 0.1)
            return;
        super.setAlpha(alpha);
    }
}
