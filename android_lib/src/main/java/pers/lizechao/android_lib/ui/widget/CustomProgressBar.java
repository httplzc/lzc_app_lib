package pers.lizechao.android_lib.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.nineoldandroids.animation.ValueAnimator;

import pers.lizechao.android_lib.R;
import pers.lizechao.android_lib.utils.FunUntil;

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
 * Date: 2018-07-02
 * Time: 17:43
 * 用于显示确定的进度，不确定的进度，圆形的进度条
 */
public class CustomProgressBar extends View {
    private int maxProgress = 100;
    private float progress = 0;
    private boolean isIndeterminate = false;
    private boolean isCircleMode = false;
    private boolean isOnlyText = false;
    private float circleLineWidth;
    private Paint paint;
    private Paint textPaint;
    private Drawable backgroundDrawable;
    private Drawable mainDrawable;
    private Drawable indicatorDrawable;
    private RectF circleRectF;
    private ValueAnimator valueAnimatorRotate;
    private ValueAnimator valueAnimatorProgress;
    private static final int animTime = 1000;
    private float indeterminateWidthRatio = 0.3f;
    private boolean showProgressText;
    private float currentTranslateRatio = 0;
    private float textSize;
    private int textColor;

    private final RectF rect = new RectF();
    private final Rect textRect = new Rect();

    public CustomProgressBar(Context context) {
        super(context);
        initPaint(context);

    }

    public CustomProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        initPaint(context);

    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomProgressBar);
        isCircleMode = typedArray.getBoolean(R.styleable.CustomProgressBar_is_circle, false);
        isIndeterminate = typedArray.getBoolean(R.styleable.CustomProgressBar_is_indeterminate, false);
        progress = typedArray.getInteger(R.styleable.CustomProgressBar_progress, 0);
        maxProgress = typedArray.getInteger(R.styleable.CustomProgressBar_max_progress, 100);
        mainDrawable = typedArray.getDrawable(R.styleable.CustomProgressBar_main_res);
        backgroundDrawable = typedArray.getDrawable(R.styleable.CustomProgressBar_background_res);
        circleLineWidth = typedArray.getDimension(R.styleable.CustomProgressBar_circle_width, -1);
        showProgressText = typedArray.getBoolean(R.styleable.CustomProgressBar_show_text, false);
        textSize = typedArray.getDimension(R.styleable.CustomProgressBar_text_size, textSize);
        textColor = typedArray.getColor(R.styleable.CustomProgressBar_text_color, Color.WHITE);
        indicatorDrawable = typedArray.getDrawable(R.styleable.CustomProgressBar_indicator_res);
        isOnlyText = typedArray.getBoolean(R.styleable.CustomProgressBar_only_text, false);
        typedArray.recycle();

    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isIndeterminate && valueAnimatorRotate == null) {
            valueAnimatorRotate = ValueAnimator.ofFloat(0, 1);
            valueAnimatorRotate.addUpdateListener(animation -> {
                currentTranslateRatio = (float) animation.getAnimatedValue();
                invalidate();
            });
            valueAnimatorRotate.setRepeatCount(-1);
            valueAnimatorRotate.setRepeatMode(ValueAnimator.RESTART);
            valueAnimatorRotate.setInterpolator(new FastOutSlowInInterpolator());
            int onceTime = 2000;
            valueAnimatorRotate.setDuration(onceTime);
            valueAnimatorRotate.start();
        }
        if (indicatorDrawable != null && getParent() != null)
            ((ViewGroup) getParent()).setClipChildren(false);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (valueAnimatorRotate != null) {
            valueAnimatorRotate.cancel();
            valueAnimatorRotate = null;
        }

    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public float getProgress() {
        return progress;
    }


    public boolean isIndeterminate() {
        return isIndeterminate;
    }

    public void setIndeterminate(boolean indeterminate) {
        isIndeterminate = indeterminate;
    }


    public float getIndeterminateWidthRatio() {
        return indeterminateWidthRatio;
    }

    public void setIndeterminateWidthRatio(float indeterminateWidthRatio) {
        this.indeterminateWidthRatio = indeterminateWidthRatio;
    }


    private void initPaint(Context context) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        if (backgroundDrawable == null)
            backgroundDrawable = isCircleMode ? ContextCompat.getDrawable(context, R.color.progress_bar_background)
              : ContextCompat.getDrawable(context, R.drawable.custom_progress_corner_background);
        if (mainDrawable == null)
            mainDrawable = isCircleMode ? ContextCompat.getDrawable(context, R.color.progress_bar_main)
              : ContextCompat.getDrawable(context, R.drawable.custom_progress_corner_main);
        if (circleLineWidth == -1)
            circleLineWidth = getResources().getDimension(R.dimen.circle_progress_line);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        circleRectF = new RectF(0, 0, getWidth(), getHeight());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float currentProgress = progress / (float) maxProgress;
        if (!isOnlyText) {
            if (!isCircleMode) {
                paint.setStyle(Paint.Style.FILL);
                backgroundDrawable.setBounds(0, 0, getWidth(), getHeight());
                backgroundDrawable.draw(canvas);
                if (isIndeterminate) {
                    int mainWidth = (int) (getWidth() * indeterminateWidthRatio);
                    int realMax = getWidth() + mainWidth * 2;
                    int currentTrans = (int) (realMax * currentTranslateRatio);
                    rect.set(-mainWidth + currentTrans, 0, currentTrans, getHeight());
                } else {
                    rect.set(0, 0, (getWidth() * currentProgress), getHeight());
                }
                canvas.save();
                canvas.clipRect(rect);
                mainDrawable.setBounds(0, 0, getWidth(), getHeight());
                mainDrawable.draw(canvas);
                canvas.restore();
                if (indicatorDrawable != null) {
                    int indicatorWidth = indicatorDrawable.getMinimumWidth();
                    int indicatorHeight = indicatorDrawable.getMinimumWidth();
                    int indicatorLeft = (int) (getWidth() * currentProgress) - indicatorWidth / 2;
                    int indicatorTop = getHeight() / 2 - indicatorHeight / 2;
                    indicatorDrawable.setBounds(indicatorLeft, indicatorTop, indicatorLeft + indicatorWidth, indicatorTop + indicatorHeight);
                    indicatorDrawable.draw(canvas);
                }


            } else {
                paint.setStrokeWidth(circleLineWidth);
                paint.setStyle(Paint.Style.STROKE);
                ColorDrawable colorDrawableBack = (ColorDrawable) backgroundDrawable;
                ColorDrawable colorDrawableMain = (ColorDrawable) mainDrawable;
                paint.setColor(colorDrawableBack.getColor());
                canvas.drawCircle((float) getWidth() / 2, (float) getHeight() / 2, (getWidth() - circleLineWidth) / (float) 2, paint);
                paint.setColor(colorDrawableMain.getColor());
                circleRectF.set(0, 0, getWidth(), getHeight());
                circleRectF.left += circleLineWidth / 2;
                circleRectF.right -= circleLineWidth / 2;
                circleRectF.top += circleLineWidth / 2;
                circleRectF.bottom -= circleLineWidth / 2;
                canvas.drawArc(circleRectF, -90, currentProgress * 360, false, paint);
            }
        }
        if (showProgressText) {
            textPaint.setColor(textColor);
            textPaint.setTextSize(textSize);
            String value = ((int) progress) + "%";
            textPaint.getTextBounds(value, 0, value.length(), textRect);
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(value, (float) getWidth() / 2, FunUntil.calcCenterTextY(textPaint, getHeight()), textPaint);
        }
    }


    public void setCurrentProgressByAnim(int current_progress, int time) {
        if (current_progress < 0 || current_progress > maxProgress) {
            return;
        }
        if (valueAnimatorProgress != null) {
            valueAnimatorProgress.removeAllUpdateListeners();
            valueAnimatorProgress.cancel();
        }
        valueAnimatorProgress = ValueAnimator.ofFloat(this.progress, current_progress);
        valueAnimatorProgress.setDuration(time);
        valueAnimatorProgress.addUpdateListener(animation -> {
            CustomProgressBar.this.progress = ((Float) animation.getAnimatedValue());
            invalidate();
        });
        valueAnimatorProgress.start();
    }

    public void setProgress(float current_progress) {
        if (current_progress < 0 || current_progress > maxProgress) {
            return;
        }
        if (valueAnimatorProgress != null) {
            valueAnimatorProgress.removeAllUpdateListeners();
            valueAnimatorProgress.cancel();
        }
        CustomProgressBar.this.progress = current_progress;
        invalidate();
    }
}
