package com.yioks.lzclib.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.yioks.lzclib.Data.ScreenData;
import com.yioks.lzclib.R;

/**
 * Created by ${User} on 2017/4/17 0017.
 */

public class ArcImgView extends ImageView {
    private static final float maxBottomHeightRatio = 0.125f;
    private Path path;
    private Paint paint;
    private float arcRatio = 0f;
    private float maxScrollHeight = 0;
    private float minScrollHeight = 0;
    private Matrix originMatrix;
    private float imgRatio = 1f;
    private static final float maxImgScale = 1.3f;

    public ArcImgView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        path = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);

    }

    public ArcImgView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initAttrs(context, attrs);
    }

    public ArcImgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcImgView);
        maxScrollHeight = typedArray.getDimension(R.styleable.ArcImgView_maxScrollHeight, 300 * ScreenData.density);
        minScrollHeight = typedArray.getDimension(R.styleable.ArcImgView_minScrollHeight, 300 * ScreenData.density);
        arcRatio = typedArray.getFloat(R.styleable.ArcImgView_arc_ratio, 0f);
        imgRatio = typedArray.getFloat(R.styleable.ArcImgView_img_ratio, 1f);
        typedArray.recycle();
    }


    public float getMaxScrollHeight() {
        return maxScrollHeight;
    }

    public void setMaxScrollHeight(float maxScrollHeight) {
        this.maxScrollHeight = maxScrollHeight;
    }

    public float getMinScrollHeight() {
        return minScrollHeight;
    }

    public void setMinScrollHeight(float minScrollHeight) {
        this.minScrollHeight = minScrollHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec((int) (maxScrollHeight - (maxScrollHeight - minScrollHeight) * arcRatio), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (originMatrix == null) {
            // Log.i("lzc", "setOrigin");
            originMatrix = new Matrix(getImageMatrix());
            this.setScaleType(ScaleType.MATRIX);
        }
        int width = getWidth();
        int height = getHeight();
        float maxBottomHeight = height * maxBottomHeightRatio;
        path.reset();
        path.moveTo(0, height - maxBottomHeight);
        path.lineTo(0, height + 1);
        path.lineTo(width, height + 1);
        path.lineTo(width, height - maxBottomHeight);
        //  RectF rectOrl=new RectF(-width,-height,2f*width,height-maxBottomHeight/3.5f);
        path.quadTo(width / 2, height + maxBottomHeight * calcArcHeight(), 0, height - maxBottomHeight);
        // path.arcTo(rectOrl, 0, 180, false);
        path.close();
        canvas.drawPath(path, paint);
        this.setImageMatrix(calcMatrix());
    }

    //计算图片大小
    private Matrix calcMatrix() {
        Matrix matrix = new Matrix(originMatrix);
        float scale = (1 - arcRatio) * (maxImgScale - 1) + 1;
        // Log.i("lzc", "scale" + scale);
        matrix.postScale(scale * imgRatio, scale * imgRatio, getWidth() / 2, getHeight() / 2);
        return matrix;
    }

    private float calcArcHeight() {
        return 0.5f - (1.5f * arcRatio);
    }

    public float getArcRatio() {
        return arcRatio;
    }

    public void setArcRatio(float arcRatio) {
        this.arcRatio = arcRatio;
        invalidate();
        requestLayout();
    }

    public float getImgRatio() {
        return imgRatio;
    }

    public void setImgRatio(float imgRatio) {
        if (Math.abs(this.imgRatio - imgRatio) > 0.001) {
            this.imgRatio = imgRatio;
            invalidate();
        }

    }
}
