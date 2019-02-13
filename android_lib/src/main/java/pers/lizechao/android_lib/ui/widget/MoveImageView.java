package pers.lizechao.android_lib.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import pers.lizechao.android_lib.R;


/**
 * Created by Lzc on 2016/7/1.
 * 可以拖动的ImageView
 */
public class MoveImageView extends android.support.v7.widget.AppCompatImageView {
    private final PointF startPoint = new PointF();
    //实例化矩阵
    private Matrix currentMaritx = new Matrix();
    private final Matrix startMatrix = new Matrix();
    private RectF startRect = new RectF();
    //用于标记模式
    private int mode = 0;
    //拖动
    private static final int DRAG = 1;
    //放大
    private static final int ZOOM = 2;
    private float startDis = 0;
    //中心点
    private PointF midPoint;


    private float imagePadding;
    private float ratio = 1;

    private RectF moveBound;

    private float bitmapWidth;
    private float bitmapHeight;


    public void reset() {
        mode = 0;
        currentMaritx = new Matrix();
    }

    public MoveImageView(Context context) {
        super(context);
    }


    public MoveImageView(Context context, AttributeSet paramAttributeSet) {
        super(context, paramAttributeSet);
        initArray(paramAttributeSet);
    }


    private void initArray(AttributeSet attr) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attr, R.styleable.MoveImageView);
        imagePadding = typedArray.getDimension(R.styleable.MoveImageView_image_padding, imagePadding);
        ratio = typedArray.getFloat(R.styleable.MoveImageView_image_ratio, 1);
        typedArray.recycle();
    }

    //拖动执行
    private void dragDo(MotionEvent event) {
        float dx = event.getX() - startPoint.x;
        float dy = event.getY() - startPoint.y;
        RectF target = new RectF(startRect);
        target.offset(dx, dy);
        if (target.left > moveBound.left) {
            dx = moveBound.left - startRect.left;
        }
        if (target.top > moveBound.top) {
            dy = moveBound.top - startRect.top;
        }
        if (target.right < moveBound.right) {
            dx = moveBound.right - startRect.right;
        }
        if (target.bottom < moveBound.bottom) {
            dy = moveBound.bottom - startRect.bottom;
        }
        currentMaritx = new Matrix(startMatrix);
        currentMaritx.postTranslate(dx, dy);
    }


    //放大执行
    private void zoomDo(MotionEvent event) {
        float endDis = distance(event);
        if (endDis < 10f)
            return;

        float scale = endDis / startDis;
        while (true) {
            midPoint = mid(event);
            Matrix matrixTemp = moveToLimit(scale);
            if (matrixTemp != null) {
                currentMaritx = matrixTemp;
                break;
            } else {
                scale += 0.001;
                if (scale >= 1) {
                    break;
                }
            }
        }
    }

    //手势放大判断越界

    private Matrix moveToLimit(float scale) {
        Matrix matrixTemp = new Matrix(startMatrix);
        matrixTemp.postScale(scale, scale, midPoint.x, midPoint.y);
        RectF rectF = mapRect(matrixTemp);
        if (isInRect(rectF)) {
            return matrixTemp;
        }
        if (rectF.width() < moveBound.width() || rectF.height() < moveBound.height()) {
            return null;
        } else {
            return translateDragToAlign(rectF, matrixTemp);
        }
    }

    //矫正放大位置
    private Matrix translateDragToAlign(RectF target, Matrix matrix) {
        float realDx = 0;
        float realDy = 0;
        if (target.left > moveBound.left) {
            realDx = moveBound.left - target.left;
        }
        if (target.top > moveBound.top) {
            realDy = moveBound.top - target.top;
        }
        if (target.right < moveBound.right) {
            realDx = moveBound.right - target.right;
        }
        if (target.bottom < moveBound.right) {
            realDy = moveBound.bottom - target.bottom;
        }
        matrix.postTranslate(realDx, realDy);
        return matrix;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.getScaleType() != ScaleType.MATRIX)
            this.setScaleType(ScaleType.MATRIX);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //拖动
                mode = DRAG;
                startMatrix.set(this.getImageMatrix());
                startRect = mapRect(startMatrix);
                currentMaritx.set(this.getImageMatrix());
                startPoint.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                //拖动
                if (mode == DRAG) {
                    dragDo(event);
                }
                //放大
                else if (mode == ZOOM) {
                    zoomDo(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                mode = 0;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mode = 0;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                startDis = distance(event);
                if (startDis > 10f) {
                    midPoint = mid(event);
                    currentMaritx.set(this.getImageMatrix());
                }
                break;
        }
        this.setImageMatrix(currentMaritx);
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (moveBound == null) {
            float heightPadding = (getHeight() - ((getWidth() - imagePadding * 2) / ratio)) / 2;
            moveBound = new RectF(imagePadding, heightPadding, getWidth() - imagePadding, getHeight() - heightPadding);
        }
        initImgPosition(bitmapWidth, bitmapHeight);
    }

    private boolean isInRect(RectF target) {
        return target.left <= moveBound.left && target.top <= moveBound.top && target.right >= moveBound.right && target.bottom >= moveBound.bottom;
    }

    //测量Matrix当前坐标
    private RectF mapRect(Matrix matrix) {
        RectF rectF = new RectF();
        rectF.right = bitmapWidth;
        rectF.bottom = bitmapHeight;
        matrix.mapRect(rectF);
        return rectF;
    }

    //两根线的距离
    private float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    //获取手势中点
    private PointF mid(MotionEvent event) {
        float midx = event.getX(1) + event.getX(0);
        float midy = event.getY(1) + event.getY(0);
        return new PointF(midx / 2f, midy / 2f);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        bitmapWidth = bm.getWidth();
        bitmapHeight = bm.getHeight();
    }

    public RectF getMoveBound() {
        return moveBound;
    }

    /**
     * @param imagePadding 横向边距
     * @param ratio        w/h
     */
    public void setImagePadding(float imagePadding, float ratio) {
        moveBound = null;
        this.imagePadding = imagePadding;
        this.ratio = ratio;
        requestLayout();
    }

    public float getImagePadding() {
        return imagePadding;
    }

    private void initImgPosition(float bitmapWidth, float bitmapHeight) {
        if (bitmapWidth / bitmapHeight > getWidth() / moveBound.height()) {
            //宽占满
            float bili = moveBound.height() / bitmapHeight;
            final Matrix matrix = new Matrix();
            matrix.postScale(bili, bili);
            matrix.postTranslate(moveBound.left,moveBound.top);
            setScaleType(ScaleType.MATRIX);
            setImageMatrix(matrix);
        } else if (bitmapWidth / bitmapHeight < moveBound.width() / getHeight()) {
            //高占满
            float bili = moveBound.width() / bitmapWidth;
            final Matrix matrix = new Matrix();
            matrix.postScale(bili, bili);
            matrix.postTranslate(moveBound.left,moveBound.top);
            setScaleType(ScaleType.MATRIX);
            setImageMatrix(matrix);
        }
    }

    public Bitmap getCultImage() {
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bitmap = this.getDrawingCache();
        Bitmap cult = Bitmap.createBitmap(bitmap, (int) moveBound.left, (int) moveBound.top, (int) moveBound.width(), (int) moveBound.height());
        this.setDrawingCacheEnabled(false);
        return cult;
    }

}
