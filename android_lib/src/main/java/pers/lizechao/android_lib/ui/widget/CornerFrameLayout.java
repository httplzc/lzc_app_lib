package pers.lizechao.android_lib.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

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
 * Time: 11:41
 * 用于绘制圆角的容器
 */
public class CornerFrameLayout extends FrameLayout {
    private Paint paint;
    private PorterDuffXfermode xfermode;
    private float topLeftRadius = 0f;
    private float topRightRadius = 0f;
    private float bottomLeftRadius = 0f;
    private float bottomRightRadius = 0f;
    private final RectF rect = new RectF();

    public CornerFrameLayout(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
    }

    public CornerFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttr(attrs);
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CornerFrameLayout);
        topLeftRadius = typedArray.getDimension(R.styleable.CornerFrameLayout_left_top_radius, 0);
        topRightRadius = typedArray.getDimension(R.styleable.CornerFrameLayout_right_top_radius, 0);
        bottomLeftRadius = typedArray.getDimension(R.styleable.CornerFrameLayout_left_bottom_radius, 0);
        bottomRightRadius = typedArray.getDimension(R.styleable.CornerFrameLayout_right_bottom_radius, 0);
        float radius = typedArray.getDimension(R.styleable.CornerFrameLayout_corner_radius, 0);
        if (radius != 0) {
            topLeftRadius = topRightRadius = bottomRightRadius = bottomLeftRadius = radius;
        }
        typedArray.recycle();
    }

    public CornerFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttr(attrs);
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        int saveCount = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
        paint.setXfermode(null);
        super.dispatchDraw(canvas);
        paint.setXfermode(xfermode);
        drawTopLeft(canvas);
        drawTopRight(canvas);
        drawBottomLeft(canvas);
        drawBottomRight(canvas);
        canvas.restoreToCount(saveCount);
    }

    private void drawTopLeft(Canvas canvas) {
        if (topLeftRadius > 0) {
            Path path = new Path();
            path.moveTo(0, topLeftRadius);
            path.lineTo(0, 0);
            path.lineTo(topLeftRadius, 0);
            rect.set(0, 0, topLeftRadius * 2, topLeftRadius * 2);
            path.arcTo(rect, -90, -90);
            path.close();
            canvas.drawPath(path, paint);
        }
    }

    private void drawTopRight(Canvas canvas) {
        if (topRightRadius > 0) {
            int width = getWidth();
            Path path = new Path();
            path.moveTo(width - topRightRadius, 0);
            path.lineTo(width, 0);
            path.lineTo(width, topRightRadius);
            rect.set(width - 2 * topRightRadius, 0, width, topRightRadius * 2);
            path.arcTo(rect, 0, -90);
            path.close();
            canvas.drawPath(path, paint);
        }
    }

    private void drawBottomLeft(Canvas canvas) {
        if (bottomLeftRadius > 0) {
            int height = getHeight();
            Path path = new Path();
            path.moveTo(0, height - bottomLeftRadius);
            path.lineTo(0, height);
            path.lineTo(bottomLeftRadius, height);
            rect.set(0, height - 2 * bottomLeftRadius, bottomLeftRadius * 2, height);
            path.arcTo(rect, 90, 90);
            path.close();
            canvas.drawPath(path, paint);
        }
    }

    private void drawBottomRight(Canvas canvas) {
        if (bottomRightRadius > 0) {
            int height = getHeight();
            int width = getWidth();
            Path path = new Path();
            path.moveTo(width - bottomRightRadius, height);
            path.lineTo(width, height);
            path.lineTo(width, height - bottomRightRadius);
            rect.set(width - 2 * bottomRightRadius, height - 2 * bottomRightRadius, width, height);
            path.arcTo(rect, 0, 90);
            path.close();
            canvas.drawPath(path, paint);
        }
    }
}
