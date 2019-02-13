package pers.lizechao.android_lib.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;

import pers.lizechao.android_lib.R;

/**
 * Created with
 * ********************************************************************************
 * #         ___                     ________                ________             *
 * #       |\  \                   |\_____  \              |\   ____\             *
 * #       \ \  \                   \|___/  /|             \ \  \___|             *
 * #        \ \  \                      /  / /              \ \  \                *
 * #         \ \  \____                /  /_/__              \ \  \____           *
 * #          \ \_______\             |\________\             \ \_______\         *
 * #           \|_______|              \|_______|              \|_______|         *
 * #                                                                              *
 * ********************************************************************************
 * Date: 2018-07-04
 * Time: 15:08
 * 圆形的可显示文字的view
 */
public class CornerTextView extends android.support.v7.widget.AppCompatTextView {
    private int fullColor = android.graphics.Color.TRANSPARENT;
    private Paint paint;
    private float borderWidth;
    private boolean isCircle = false;
    private float corner_radius = 0;
    private int borderColor = android.graphics.Color.WHITE;
    private GradientDrawable drawable;

    public CornerTextView(Context context) {
        super(context);
        initPaint();
    }

    private void initPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);

    }

    public CornerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initPaint();
        createBackGround();
    }

    private void createBackGround() {
        if (drawable == null) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setCornerRadius(corner_radius);
            gradientDrawable.setStroke((int) borderWidth, borderColor);
            gradientDrawable.setColor(fullColor);
            this.setBackground(gradientDrawable);
            drawable = gradientDrawable;
        }
        if (isCircle)
            drawable.setCornerRadius(getHeight() / 2);

    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CornerTextView);
        fullColor = typedArray.getColor(R.styleable.CornerTextView_corner_text_full_color, android.graphics.Color.WHITE);
        borderWidth = typedArray.getDimension(R.styleable.CornerTextView_corner_text_line_width, 0);
        borderColor = typedArray.getColor(R.styleable.CornerTextView_corner_text_line_color, android.graphics.Color.WHITE);
        corner_radius = typedArray.getDimension(R.styleable.CornerFrameLayout_corner_radius, 0);
        isCircle = typedArray.getBoolean(R.styleable.CornerTextView_corner_text_isCircle, false);
        typedArray.recycle();
    }


    public void setFullColor(int color) {
        fullColor = color;
        drawable = null;
        createBackGround();
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        drawable = null;
        createBackGround();

    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
        drawable = null;
        createBackGround();

    }

    public void setCircle(boolean circle) {
        isCircle = circle;
    }

    public void setCorner_radius(float corner_radius) {
        this.corner_radius = corner_radius;
        drawable = null;
        createBackGround();
    }

    public CornerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if(isCircle)
            drawable.setCornerRadius((getHeight()-borderWidth*2)/2);
        int with = getWidth();
        int height = getHeight();
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(fullColor);
//        canvas.drawCircle(with / 2, height / 2, with / 2 - 10, paint);
//        if (Math.abs(borderWidth) > 0.01) {
//            paint.setStyle(Paint.Style.STROKE);
//            paint.setStrokeWidth(borderWidth);
//            paint.setColor(borderColor);
//            canvas.drawCircle(with / 2, height / 2, with / 2 - 10, paint);
//        }
        super.onDraw(canvas);
    }
}
