package com.yioks.lzclib.View;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.yioks.lzclib.Activity.ShowBigImgActivity;
import com.yioks.lzclib.Data.ScreenData;

/**
 * Created by ${User} on 2017/2/24 0024.
 */

public class BigImgImageView extends ImageView {
    private int mode = 0;
    private Matrix currentMaritx = new Matrix();
    private PointF midPoint;
    private float startDis = 0;
    private static final int NORMAL = 0;
    //拖动
    private static final int DRAG = 1;
    //放大
    private static final int ZOOM = 2;
    private PointF lastPoint = new PointF();
    private PointF startPoint = new PointF();

    private final static float Factor = 0.75f;

    private float currrentAlpha = 1;

    private Matrix originMatrix;

    private boolean isAnim = false;

    private boolean canMove=true;

    public BigImgImageView(Context context) {
        super(context);
    }

    public BigImgImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BigImgImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initImage();
    }

    public void initImage() {
        if (getDrawable() == null)
            return;
        int width = getDrawable().getIntrinsicWidth();
        int height = getDrawable().getIntrinsicHeight();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!canMove)
            return true;
        if (isAnim)
            return true;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //拖动
                Log.i("lzc","ACTION_DOWN");
                originMatrix = new Matrix(getImageMatrix());
                currentMaritx.set(this.getImageMatrix());
                startPoint.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("lzc","ACTION_MOVE");
                //拖动
                if (mode == DRAG) {
                    dragDo(event);
                }
                //放大
                else if (mode == ZOOM) {
                    zoomDo(event);
                } else {
                    normalDo(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.i("lzc","ACTION_UP");
                changeMode(false);
//                if (!fromZoom) {
////                    offX += dx;
////                    offY += dy;
//                } else {
//                    upscale = lastscale;
//                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                Log.i("lzc","ACTION_POINTER_UP");
                changeMode(false);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                Log.i("lzc","ACTION_POINTER_DOWN");
                if (mode == NORMAL) {
                    changeMode(true);
                    mode = ZOOM;
                    startDis = distance(event);
                    if (startDis > 10f) {
                        midPoint = mid(event);
                    }
                }
                break;
        }
        if (currentMaritx != null) {
            this.setImageMatrix(currentMaritx);
        }
        lastPoint.set(event.getX(), event.getY());
        return true;
    }

    private void normalDo(MotionEvent event) {
        float dx = event.getX() - startPoint.x;
        float dy = event.getY() - startPoint.y;
        if (Math.abs(dy) > 10 * ScreenData.density) {
            changeMode(true);
            mode = DRAG;
        }
    }

    private void changeMode(boolean isToMove) {
        if (isToMove) {
            this.setScaleType(ScaleType.MATRIX);
            getParent().getParent().requestDisallowInterceptTouchEvent(true);
        } else {

            shutdown();
        }

    }


    //放大执行
    private void zoomDo(MotionEvent event) {
        Log.i("lzc","zoomDo");
        float endDis = distance(event);
        if (endDis > 10f) {
            float scale = endDis / startDis;
            midPoint = mid(event);
            currentMaritx.postScale(scale, scale, midPoint.x, midPoint.y);
            startDis = endDis;
            changeAlphaByScale(currentMaritx);
        }
    }

    private void changeAlphaByScale(Matrix matrix) {
        RectF rectF = new RectF();
        rectF.right = getDrawable().getIntrinsicWidth();
        rectF.bottom = getDrawable().getIntrinsicHeight();
        currentMaritx.mapRect(rectF);
        float currentWidth = rectF.right - rectF.left;
        setActivityAlpha(currentWidth / getDrawable().getIntrinsicWidth() + 0.15f);
    }

    private float changeAlphaByTrans(Matrix matrix) {
        float distance = getCenterDistance(matrix);
        Log.i("lzc", "distance" + distance);
        float maxDistance = (float) Math.sqrt(Math.pow(getWidth() / 2, 2) + Math.pow(getHeight() / 2, 2));
        setActivityAlpha((maxDistance - distance) / maxDistance + 0.15f);
        return (maxDistance - distance) / maxDistance;
    }


    //拖动执行
    private void dragDo(MotionEvent event) {
        Log.i("lzc","dragDo");
        float dx = event.getX() - lastPoint.x;
        float dy = event.getY() - lastPoint.y;
        if (Math.abs(dx) > 5 || Math.abs(dy) > 5) {
            currentMaritx.postTranslate(dx * Factor, dy * Factor);
            changeAlphaByTrans(currentMaritx);


            float disCur = getCenterDistance(currentMaritx);
            float maxDistance = (float) Math.sqrt(Math.pow(getWidth() / 2, 2) + Math.pow(getHeight() / 2, 2));
            float beilv = (maxDistance - disCur) / maxDistance;
            Log.i("lzc", "beilv" + beilv + "---" + disCur);
            PointF pointF = getMatrixCenter(currentMaritx);
            currentMaritx = new Matrix(originMatrix);
            currentMaritx.postTranslate(pointF.x - getWidth() / 2, pointF.y - getHeight() / 2);
            currentMaritx.postScale(beilv, beilv, pointF.x, pointF.y);
            Log.i("lzc", "trans" + (pointF.x - getWidth() / 2) + "---" + (pointF.y - getHeight() / 2));
            Log.i("lzc", "currentCenter" + getWidth() / 2 + "--" + getHeight() / 2);
            Log.i("lzc", "currentMaritx" + getMatrixCenter(currentMaritx));

        }


    }

    private RectF getMatrixMapRect(Matrix matrix) {
        RectF rectF = new RectF();
        rectF.right = getDrawable().getIntrinsicWidth();
        rectF.bottom = getDrawable().getIntrinsicHeight();
        matrix.mapRect(rectF);
        return rectF;
    }

    private PointF getMatrixCenter(Matrix matrix) {
        PointF pointF = new PointF();
        RectF rectF = getMatrixMapRect(matrix);
        float currentX = (rectF.right + rectF.left) / 2;
        float currentY = (rectF.bottom + rectF.top) / 2;
        pointF.set(currentX, currentY);
        return pointF;
    }

    private float distanceCenter(float currentX, float currentY) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        return (float) Math.sqrt(Math.pow(currentX - centerX, 2) + Math.pow(currentY - centerY, 2));
    }

    private float getCenterDistance(Matrix matrix) {
        PointF pointF = getMatrixCenter(matrix);
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        return (float) Math.sqrt(Math.pow(pointF.x - centerX, 2) + Math.pow(pointF.y - centerY, 2));
    }


    private float distance(MotionEvent event) {
        try {
            //两根线的距离
            float dx = event.getX(1) - event.getX(0);
            float dy = event.getY(1) - event.getY(0);
            return (float) Math.sqrt(dx * dx + dy * dy);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    //获取手势中点
    private PointF mid(MotionEvent event) {
        try {
            float midx = event.getX(1) + event.getX(0);
            float midy = event.getY(1) + event.getY(0);
            //   return new PointF(20f*ScreenData.density,(PicCultActivity.PicRealHeight-PicCultActivity.backHeight) / 2f);
            return new PointF(midx / 2f, midy / 2f);
        } catch (Exception e) {
            e.printStackTrace();
            return new PointF(getWidth() / 2, getHeight() / 2);
        }
    }

    private void setActivityAlpha(float alpha) {
        Intent intent = new Intent();
        intent.setAction(ShowBigImgActivity.RECEIVER_NAME);
        intent.putExtra("alpha", alpha);
        currrentAlpha = alpha;
        getContext().sendBroadcast(intent);
    }

    private void shutdown() {
        animToOrigin(currentMaritx, currrentAlpha < 0.8f);
    }

    private void animToOrigin(Matrix matrix, final boolean finish) {
        RectF rectFCur = getMatrixMapRect(currentMaritx);
        RectF rectFPro = getMatrixMapRect(originMatrix);
        float beilv = (rectFCur.right - rectFCur.left) / (rectFPro.right - rectFPro.left);
      //  Log.i("lzc", "beilv" + beilv + "----" + (rectFCur.right - rectFCur.left) + "--" + (rectFPro.right - rectFPro.left));

        PointF pointF = getMatrixCenter(matrix);
      //  Log.i("lzc", "animPoint_before" + getMatrixCenter(matrix));
        float dx = pointF.x - getWidth() / 2;
        float dy = pointF.y - getHeight() / 2;
        PropertyValuesHolder propertyValuesHolderDx = PropertyValuesHolder.ofFloat("dx", dx, 0);
        PropertyValuesHolder propertyValuesHolderDy = PropertyValuesHolder.ofFloat("dy", dy, 0);
        PropertyValuesHolder propertyValuesHolderAlpha = PropertyValuesHolder.ofFloat("alpha", currrentAlpha, 1);
        PropertyValuesHolder propertyValuesHolderBeilv = PropertyValuesHolder.ofFloat("beilv", beilv, finish ? 0 : 1);
        ValueAnimator valueAnimator = ValueAnimator.ofPropertyValuesHolder(propertyValuesHolderDx, propertyValuesHolderDy, propertyValuesHolderBeilv, propertyValuesHolderAlpha);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float dx = (float) animation.getAnimatedValue("dx");
                float dy = (float) animation.getAnimatedValue("dy");
                float beilv = (float) animation.getAnimatedValue("beilv");
                float alpha = (float) animation.getAnimatedValue("alpha");
                Matrix matrix = new Matrix(originMatrix);
                matrix.postTranslate(dx, dy);
                PointF pointF1 = getMatrixCenter(matrix);
                matrix.postScale(beilv, beilv, pointF1.x, pointF1.y);
             //   Log.i("lzc", "animPoint" + getMatrixCenter(matrix));
                BigImgImageView.this.setImageMatrix(matrix);
                setActivityAlpha(alpha);
              //  Log.i("lzc", " ---- " + dx + " ---- " + dy + " ---- " + beilv);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnim = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnim = false;
                if (finish) {
                    Intent intent = new Intent();
                    intent.setAction(ShowBigImgActivity.RECEIVER_NAME);
                    intent.putExtra("shutdown", true);
                    getContext().sendBroadcast(intent);
                  //  BigImgImageView.this.setScaleType(ScaleType.FIT_CENTER);
                    getParent().getParent().requestDisallowInterceptTouchEvent(false);
                    mode = NORMAL;
                } else {
                    BigImgImageView.this.setScaleType(ScaleType.FIT_CENTER);
                    BigImgImageView.this.getParent().getParent().requestDisallowInterceptTouchEvent(false);
                    mode = NORMAL;
                    currentMaritx=originMatrix;
                }


            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.setDuration(200);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.start();
    }

    public boolean isCanMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }
}
