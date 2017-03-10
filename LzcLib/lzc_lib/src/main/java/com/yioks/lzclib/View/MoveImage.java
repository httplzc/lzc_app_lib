package com.yioks.lzclib.View;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;


/**
 * Created by Yioks on 2016/7/1.
 */
public class MoveImage extends ImageView {
    private PointF startPoint = new PointF();
    //定义矩阵
    private Matrix matrix;
    //实例化矩阵
    private Matrix currentMaritx = new Matrix();
    //用于标记模式
    private int mode = 0;
    //拖动
    private static final int DRAG = 1;
    //放大
    private static final int ZOOM = 2;
    private float startDis = 0;
    //中心点
    private PointF midPoint;
    private float dx;
    private float dy;


    private float maxleft;
    private float maxright;
    private float maxtop;
    private float maxbottom;
    private float bitmapWidth;
    private float bitmapHeight;
    private Context context;


    public MoveImage(Context context) {
        super(context);
    }


    public MoveImage(Context context, AttributeSet paramAttributeSet) {
        super(context, paramAttributeSet);
        this.context = context;
    }

    //拖动执行
    private void dragDo(MotionEvent event) {
        dx = event.getX() - startPoint.x;
        dy = event.getY() - startPoint.y;

        matrix = new Matrix(currentMaritx);


        Matrix matrixteamp = new Matrix(currentMaritx);
        matrixteamp.postTranslate(dx, dy);
        RectF rectF = new RectF();
        rectF.right = bitmapWidth;
        rectF.bottom = bitmapHeight;
        matrixteamp.mapRect(rectF);
        float left = rectF.left;
        float right = rectF.right;
        float bottom = rectF.bottom;
        float top = rectF.top;

        //未变化的图片
        RectF currentRect = new RectF();
        currentRect.right = bitmapWidth;
        currentRect.bottom = bitmapHeight;
        currentMaritx.mapRect(currentRect);


        if (maxleft > left && maxtop > top && right > maxright && bottom > maxbottom) {
            matrix = matrixteamp;
        } else {
            transLateDragToRight(left, right, bottom, top, matrix,currentRect);
        }
    }

    //矫正拖动位置
    private void transLateDragToRight(float left, float right, float bottom, float top, Matrix matrix, RectF currentRect) {
        float realdx = dx;
        float realdy = dy;

        if (maxleft < left) {
            realdx = maxleft-currentRect.left;
        }

        if (maxtop < top) {
            realdy = maxtop - currentRect.top;
        }
        if (right < maxright) {
            realdx = maxright-currentRect.right;
        }
        if (bottom < maxbottom) {
            realdy = maxbottom - currentRect.bottom;
        }
        matrix.postTranslate(realdx, realdy);

    }


    //矫正放大位置
    private void transLateToRight(float left, float right, float bottom, float top, Matrix matrix) {
        float realdx = dx;
        float realdy = dy;

        if (maxleft < left) {
            realdx = maxleft - left;
        }

        if (maxtop < top) {
            realdy = maxtop - top;
        }
        if (right < maxright) {
            realdx = -right + maxright;
        }
        if (bottom < maxbottom) {
            realdy = maxbottom - bottom;
        }
        matrix.postTranslate(realdx, realdy);

    }

    public boolean onTouchEvent(MotionEvent event) {

        this.setScaleType(ScaleType.MATRIX);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //拖动
                mode = DRAG;
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
//                if (!fromZoom) {
////                    offX += dx;
////                    offY += dy;
//                } else {
//                    upscale = lastscale;
//                }
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
        if (matrix != null) {
            this.setImageMatrix(matrix);
        }
        return true;
    }

    //放大执行
    private void zoomDo(MotionEvent event) {
        float endDis = distance(event);
        if (endDis > 10f) {
            boolean flag = true;
            float scale = endDis / startDis;
            midPoint = mid(event);
            while (flag) {

                Matrix matrixteamp = moveLenlimit(scale);

                if (matrixteamp != null) {
                    matrix = matrixteamp;
                    break;
                } else {
                    scale += 0.001;
                    if (scale >= 1) {
                        break;
                    }
                }
            }

        }
    }


    //手势放大判断越界

    private Matrix moveLenlimit(float scale) {
        matrix = new Matrix();
        matrix.set(currentMaritx);
        Matrix matrixteamp = new Matrix(currentMaritx);
        // setZoomCenter();
        Log.i("lzc", "scale" + scale);
        matrixteamp.postScale(scale, scale, midPoint.x, midPoint.y);
        RectF rectF = new RectF();
        rectF.right = bitmapWidth;
        rectF.bottom = bitmapHeight;
        matrixteamp.mapRect(rectF);
        float left = rectF.left;
        float right = rectF.right;
        float bottom = rectF.bottom;
        float top = rectF.top;
        if (!(maxleft > left && maxtop > top && right > maxright && bottom > maxbottom)) {
            if (right - left < maxright - maxleft || bottom - top < maxbottom - maxtop)
                return null;
            else {
                transLateToRight(left, right, bottom, top, matrixteamp);
                return matrixteamp;
            }
        } else {
            return matrixteamp;
        }
    }

//    private void setZoomCenter() {
//        RectF rectF = new RectF();
//        float litmatHeight = (PicCultActivity.PicRealHeight - PicCultActivity.backHeight) / 2f;
//        PicCultActivity picCultActivity = (PicCultActivity) context;
//        rectF.right = picCultActivity.getBitmapwidth();
//        rectF.bottom = picCultActivity.getBitmapheight();
//        currentMaritx.mapRect(rectF);
//        float left = rectF.left;
//        float right = rectF.right;
//        float bottom = rectF.bottom;
//        float top = rectF.top;
//
//        if (cult_padding < left) {
//            midPoint.x = cult_padding;
//        }
//
//        if (litmatHeight < top) {
//            midPoint.y = litmatHeight;
//        }
//
//        if (right < (ScreenData.widthPX - cult_padding)) {
//            midPoint.x = ScreenData.widthPX - cult_padding;
//        }
//
//        if (PicCultActivity.PicRealHeight - litmatHeight > bottom) {
//            midPoint.y = PicCultActivity.PicRealHeight - litmatHeight;
//        }
//
//    }

    private float distance(MotionEvent event) {
        //两根线的距离
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    //获取手势中点
    private PointF mid(MotionEvent event) {
        float midx = event.getX(1) + event.getX(0);
        float midy = event.getY(1) + event.getY(0);
        //   return new PointF(20f*ScreenData.density,(PicCultActivity.PicRealHeight-PicCultActivity.backHeight) / 2f);
        return new PointF(midx / 2f, midy / 2f);
    }


    public void setMaxleft(float maxleft) {
        this.maxleft = maxleft;
    }

    public void setMaxright(float maxright) {
        this.maxright = maxright;
    }

    public void setMaxtop(float maxtop) {
        this.maxtop = maxtop;
    }

    public void setMaxbottom(float maxbottom) {
        this.maxbottom = maxbottom;
    }

    public float getBitmapWidth() {
        return bitmapWidth;
    }

    public void setBitmapWidth(float bitmapWidth) {
        this.bitmapWidth = bitmapWidth;
    }

    public float getBitmapHeight() {
        return bitmapHeight;
    }

    public void setBitmapHeight(float bitmapHeight) {
        this.bitmapHeight = bitmapHeight;
    }

    public float getMaxbottom() {
        return maxbottom;
    }

    public float getMaxtop() {
        return maxtop;
    }

    public float getMaxright() {
        return maxright;
    }

    public float getMaxleft() {
        return maxleft;
    }
}
