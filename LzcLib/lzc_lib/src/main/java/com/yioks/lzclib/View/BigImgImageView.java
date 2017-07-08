package com.yioks.lzclib.View;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.OverScroller;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.yioks.lzclib.Activity.ShowBigImgActivity;
import com.yioks.lzclib.Data.BigImgShowData;
import com.yioks.lzclib.Data.ScreenData;
import com.yioks.lzclib.Untils.FileUntil;
import com.yioks.lzclib.Untils.SpeedUntil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.yioks.lzclib.View.BigImgImageView.Orientation.toBottom;
import static com.yioks.lzclib.View.BigImgImageView.Orientation.toLeft;
import static com.yioks.lzclib.View.BigImgImageView.Orientation.toRight;
import static com.yioks.lzclib.View.BigImgImageView.Orientation.toTop;


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

    private boolean canMove = true;
    private boolean needToLoadRealBigImg = false;
    private boolean needToProLoad = true;
    private final static int minProLoad = 3;
    private BigImgShowData.MessageUri messageUri;
    private static final float maxScale = 1.5f;
    private static final float minScale = 1.3f;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private boolean isScale = false;
    private OverScroller scroller;
    private Uri uri;
    private boolean drawRealBig = false;
    //private Bitmap realBitmap;
    // private RealBitmap realBitmap;
    private RealBitmapWrapper realBitmapWrapper = new RealBitmapWrapper();
    private boolean scrollStart = false;
    private static final float scrollMinRatio = 3f;
    private PointF downPoint = new PointF();
    // private Rect currentBigImgRect;
    private AsyncBigImg asyncBigImg;
    private BitmapRegionDecoder bitmapRegionDecoder = null;
    private RectF currentRequestRect;
    private SpeedUntil speedUntil = new SpeedUntil();


    private int LoadScaleWidth;
    private int LoadScaleHeight;
    private boolean needLoadRealBySize = false;

    private Rect currentScrollRect = new Rect();
    private RectF tempRectF = new RectF();
    private RectF tempRectF2 = new RectF();
    private RectF tempMatrixRect = new RectF();
    private Rect changeRotateRect = new Rect();
    private BigAsyncData bigAsyncData = new BigAsyncData();
    private int imgRotate = 0;

    protected enum Orientation {toLeft, toRight, toTop, toBottom, none}

    private int bigImgRealWidth;
    private int bigImgRealHeight;
    //private Rect currentCallRect;

    public BigImgImageView(Context context) {
        super(context);
        init(context);
    }

    private class RealBitmapWrapper {
        RealBitmap last;
        RealBitmap current;

        private synchronized void add(RealBitmap bitmap) {
            if (current == null)
                current = bitmap;
            else {
                if (last != null)
                    last.recycle();
                last = current;
                current = bitmap;
            }
        }

        public void recycle() {
            if (last != null)
                last.recycle();
            if (current != null)
                current.recycle();
            last = null;
            current = null;
        }

        public boolean contains(Rect rect) {
            if (last == null && current == null)
                return false;
            if (last != null && current != null) {
                tempRectF.set(Math.min(current.targetRect.left, last.targetRect.left),
                        Math.min(current.targetRect.top, last.targetRect.top),
                        Math.max(current.targetRect.right, last.targetRect.right),
                        Math.max(current.targetRect.bottom, last.targetRect.bottom));
                return tempRectF.contains(RectToRectF(rect));
            }
            if (current != null)
                return current.targetRect.contains(RectToRectF(rect));
            else
                return last.targetRect.contains(RectToRectF(rect));
        }

        public Orientation containsGetNext(Rect rect) {
            RectF finial;
            if (last == null && current == null)
                return Orientation.none;
            if (last != null && current != null) {
                tempRectF.set(Math.min(current.targetRect.left, last.targetRect.left),
                        Math.min(current.targetRect.top, last.targetRect.top),
                        Math.max(current.targetRect.right, last.targetRect.right),
                        Math.max(current.targetRect.bottom, last.targetRect.bottom));
                finial = tempRectF;
            } else if (current != null)
                finial = current.targetRect;
            else
                finial = last.targetRect;
//            Log.i("lzc", "last" + last + "   " + current + "    " + rect);
//            Log.i("lzc", "finial_show  " + finial);
            if (finial.left > rect.left)
                return toLeft;
            else if (finial.right < rect.right)
                return toRight;
            else if (finial.top < rect.top)
                return toBottom;
            else
                return toTop;
        }
    }

    private RectF RectToRectF(Rect rect) {
        tempRectF2.set(rect.left, rect.top, rect.right, rect.bottom);
        return tempRectF2;
    }

    private static class RealBitmap {
        Bitmap bitmap;
        Rect rect1;
        RectF targetRect;
        Rect calcRect;

        public RealBitmap(RealBitmap realBitmap) {
            this.bitmap = realBitmap.bitmap;
            this.rect1 = realBitmap.rect1;
            this.targetRect = realBitmap.targetRect;
            this.calcRect = realBitmap.calcRect;
        }

        public RealBitmap() {
        }

        public void recycle() {
            if (bitmap != null)
                bitmap.recycle();
        }

        public boolean isRecycled() {
            return bitmap == null || bitmap.isRecycled();
        }

        @Override
        public String toString() {
            return bitmap.getWidth() + "---" + bitmap.getHeight() + "----" + rect1.toString() + "----" + targetRect.toString();
        }
    }

    //获取清晰的真实图片
    private void getOriginBitmapRect(Uri uri, Orientation preloadFlag) {


        tempMatrixRect.setEmpty();
        tempMatrixRect.right = getDrawable().getIntrinsicWidth();
        tempMatrixRect.bottom = getDrawable().getIntrinsicHeight();
        currentMaritx.mapRect(tempMatrixRect);

        RectF current = tempMatrixRect;
        float ratio = (float) bigImgRealWidth / current.width();
        float ratioHeight = (float) bigImgRealHeight / current.height();
        bigAsyncData.rect = calcBitmapRect(ratio, ratioHeight, bigImgRealWidth, bigImgRealHeight, current
                , preloadFlag, getScrollX(), getScrollY());
        bigAsyncData.target = calcDrawRect(ratio, ratioHeight, bigAsyncData.rect, getScrollX(), getScrollY(), preloadFlag);

        if (bigAsyncData.target.equals(currentRequestRect))
            return;
        currentRequestRect = bigAsyncData.target;

        if (asyncBigImg != null) {
            asyncBigImg.cancel(true);
        }
        Log.i("lzc", "preloadFlag " + preloadFlag);
//        Log.i("lzc", "targetRect " + bigAsyncData.target);
//        Log.i("lzc", "rectCalc  " + bigAsyncData.rect);
//        Log.i("lzc", "origin  " + bitmapRegionDecoder.getWidth() + "   " + bitmapRegionDecoder.getHeight());
        asyncBigImg = new AsyncBigImg();
        asyncBigImg.execute(bigAsyncData);
    }

    private RectF calcDrawRect(float ratio, float ratioHeight, Rect rect, int scrollX, int scrollY, Orientation preloadFlag) {
        float drawWidth = rect.width() / ratio;
        float drawHeight = rect.height() / ratioHeight;
        RectF drawRect = bigAsyncData.target;
        if (preloadFlag == Orientation.none) {
            drawRect = new RectF();
            drawRect.left = scrollX;
            drawRect.top = scrollY;
            drawRect.right = drawRect.left + getWidth();
            drawRect.bottom = drawRect.top + getHeight();
        } else
            drawRect = new RectF(realBitmapWrapper.current.targetRect);
        switch (preloadFlag) {
            case toLeft:
                drawRect.set(drawRect.left - drawWidth, drawRect.top, drawRect.left, drawRect.bottom);
                // drawRect.offset(-drawWidth, 0);
                break;
            case toRight:
                drawRect.set(drawRect.right, drawRect.top, drawRect.right + drawWidth, drawRect.bottom);
//                drawRect.offset(drawWidth, 0);
                break;
            case toTop:
                drawRect.set(drawRect.left, drawRect.top - drawHeight, drawRect.right, drawRect.top);
//                drawRect.offset(0, -drawHeight);
                break;
            case toBottom:
                drawRect.set(drawRect.left, drawRect.bottom, drawRect.right, drawRect.bottom + drawHeight);
//                drawRect.offset(0, drawHeight);
                break;
            case none:
                break;
        }
        return drawRect;
    }


    private void init(final Context context) {
        scroller = new OverScroller(context);
        gestureDetector = new GestureDetector(context, new SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (isAnim)
                    return true;
                if (!isScale) {
                    BigImgImageView.this.setScaleType(ScaleType.MATRIX);
                    scrollTo(0, 0);
                    animToScale();
                } else {
                    scrollTo(0, 0);
                    cancelDrawBigImg();
                    animToMatrix(currentMaritx, originMatrix);
                    destroyBigImg();
                }

                return true;
            }


            @Override
            public boolean onDown(MotionEvent e) {
                downPoint.set(e.getX(), e.getY());
                if (isAnim)
                    return true;
                if (isScale) {
                    scroller.abortAnimation();
                    if (drawRealBig && scrollStart) {
                        scrollStart = false;
                        // onBigImgFlingStop(Orientation.none);\
                        computeScroll();
                        Log.i("lzc", "onBigImgFlingStop44444");
                    }
                }

                return true;
            }


            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isAnim)
                    return true;
                BigImgImageView.this.setScaleType(ScaleType.MATRIX);
                scrollTo(0, 0);
                animToClose(currentMaritx);
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (isAnim)
                    return true;
                if (isScale) {
                    requestIntercept(true);
                    RectF rectf = getMatrixMapRect(currentMaritx);
                    // scroller.startScroll(getScrollX(), getScrollY(), -(int) (velocityX/10), -(int) (velocityY/10), 200);
                    scroller.fling(getScrollX(), getScrollY(), -(int) velocityX, (int) -velocityY,
                            -(int) (rectf.width() / 2 - getWidth() / 2), (int) (rectf.width() / 2 - getWidth() / 2),
                            -(int) (rectf.height() / 2 - getHeight() / 2), (int) (rectf.height() / 2) - getHeight() / 2);
                    speedUntil.bindData(getScrollX(), getScrollY());
                    scrollStart = true;
                    invalidate();
                }
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (isAnim)
                    return true;
                if (isScale) {
                    //  getParent().getParent().requestDisallowInterceptTouchEvent(true);
//                    currentMaritx.postTranslate(-distanceX, -distanceY);
//                    BigImgImageView.this.setImageMatrix(currentMaritx);
                    RectF rectf = getMatrixMapRect(currentMaritx);
                    int maxX = (int) (rectf.width() / 2 - getWidth() / 2);
                    int maxY = (int) (rectf.height() / 2 - getHeight() / 2);
                    int minX = -maxX;
                    int minY = -maxY;
                    boolean cross = false;
                    if (getScrollX() + distanceX > maxX) {
                        distanceX = maxX - getScrollX();
                        cross = true;
                    }


                    if (getScrollX() + distanceX < minX) {
                        cross = true;
                        distanceX = minX - getScrollX();
                    }


                    if (getScrollY() + distanceY > maxY)
                        distanceY = maxY - getScrollY();

                    if (getScrollY() + distanceY < minY)
                        distanceY = minY - getScrollY();

                    requestIntercept(true);
                    //   getParent().getParent().requestDisallowInterceptTouchEvent(true);
                    BigImgImageView.this.scrollBy((int) distanceX, (int) distanceY);

                }
                return true;
            }
        });

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            private float startSpan;
            private Matrix startMatrix;
            private int startScrollX;
            private int startScrollY;

            @Override
            public boolean onScale(ScaleGestureDetector detector) {

                if (isScale && !isAnim && !drawRealBig) {
                    float delta = detector.getScaleFactor();
                    if (detector.getCurrentSpan() < startSpan)
                        return true;
                    currentMaritx.postScale(delta, delta, detector.getFocusX(), detector.getFocusY());
                    return true;
                }
                return false;


            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {

                if (isAnim || !isScale || drawRealBig)
                    return false;
                startMatrix = new Matrix(currentMaritx);
                startSpan = detector.getCurrentSpan();
                startScrollX = getScrollX();
                startScrollY = getScrollY();
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                // scrollTo(0, 0);
                if (isAnim || !isScale || drawRealBig)
                    return;
                animToMatrix(currentMaritx, startMatrix, startScrollX, startScrollY);
            }
        });
    }

    private void cancelDrawBigImg() {
        drawRealBig = false;
        if (asyncBigImg != null)
            asyncBigImg.cancel(true);
        if (realBitmapWrapper.current != null) {
            realBitmapWrapper.current.recycle();
            realBitmapWrapper.current = null;
        }
        if (realBitmapWrapper.last != null) {
            realBitmapWrapper.last.recycle();
            realBitmapWrapper.last = null;
        }
        invalidate();
    }


    //当滑动停止读取大图图片
    private void onBigImgFlingStop() {
        if (!needToLoadRealBigImg)
            return;
        if (!needLoadRealBySize)
            return;
        if (!needToLoad())
            return;
        getOriginBitmapRect(uri, toLoadPre());
    }


    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);

    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }


    public void initSet() {

        scrollTo(0, 0);
        this.setScaleType(ScaleType.FIT_CENTER);
        currentMaritx = null;
        originMatrix = null;
//        this.setScaleType(ScaleType.FIT_CENTER);
        isScale = false;
        isAnim = false;
        if (realBitmapWrapper != null)
            realBitmapWrapper.recycle();
        cancelDrawBigImg();
        mode = NORMAL;
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);

    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);

    }

    @Override
    public void computeScroll() {

        if (needToLoadRealBigImg && isScale && bitmapRegionDecoder != null && !bitmapRegionDecoder.isRecycled()) {
            speedUntil.bindData(getScrollX(), getScrollY());
//            Log.i("lzc", "speed " + speedUntil.getSpeedY() + "   " + speedUntil.getSpeedX());
            // if (Math.max(Math.abs(speedUntil.getSpeedY()), Math.abs(speedUntil.getSpeedX())) < 3f)
            onBigImgFlingStop();
        }
        if (scroller.computeScrollOffset()) {
            //  Log.i("lzc", scroller.getCurrX() + "--" + scroller.getCurrY());
            requestIntercept(true);
            //getParent().getParent().requestDisallowInterceptTouchEvent(true);
            this.scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        } else {
            if (scrollStart) {
                scrollStart = false;
                //  onBigImgFlingStop(calcOrientation(scroller.getStartX(), scroller.getStartY(), scroller.getFinalX(), scroller.getFinalY()));
                //    Log.i("lzc", "onBigImgFlingStop11111");
            }

            scroller.abortAnimation();
        }
    }


    public BigImgImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BigImgImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (originMatrix == null && canMove) {
            originMatrix = new Matrix(getImageMatrix());
            currentMaritx = new Matrix(currentMaritx);
        }
        if (drawRealBig && realBitmapWrapper != null && !isAnim) {
            if (realBitmapWrapper.current != null && !realBitmapWrapper.current.isRecycled())
                canvas.drawBitmap(realBitmapWrapper.current.bitmap, realBitmapWrapper.current.rect1, realBitmapWrapper.current.targetRect, null);
            if (realBitmapWrapper.last != null && !realBitmapWrapper.last.isRecycled())
                canvas.drawBitmap(realBitmapWrapper.last.bitmap, realBitmapWrapper.last.rect1, realBitmapWrapper.last.targetRect, null);
        }

    }

    private boolean needToLoad() {
        if (realBitmapWrapper == null)
            return true;
        currentScrollRect.set(getScrollX(), getScrollY(), getScrollX() + getWidth(), getScrollY() + getHeight());
        return !realBitmapWrapper.contains(currentScrollRect);
    }

    private Orientation toLoadPre() {
        if (realBitmapWrapper == null)
            return Orientation.none;
        return realBitmapWrapper.containsGetNext(currentScrollRect);
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


    public boolean backImageAnim() {
        if (isScale) {
            scrollTo(0, 0);
            animToClose(currentMaritx);
        } else if (originMatrix != null) {
            animToClose(originMatrix);
        } else {
            return false;
        }

        return true;
    }


    ;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //   Log.i("lzc", "eventActiom" + event.getAction());

        if (!canMove)
            return true;
        if (!isAnim) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    //拖动
                    // Log.i("lzc", "ACTION_DOWN");
                    //   originMatrix = new Matrix(getImageMatrix());
                    currentMaritx.set(this.getImageMatrix());
                    startPoint.set(event.getX(), event.getY());
                    mode = NORMAL;
                    break;
                case MotionEvent.ACTION_MOVE:
                    // Log.i("lzc", "ACTION_MOVE");
                    if (isScale) {
                        break;
                    }

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
                    // Log.i("lzc", "ACTION_UP");
                    if (isScale)
                        break;

                    changeMode(false);
//                if (!fromZoom) {
////                    offX += dx;
////                    offY += dy;
//                } else {
//                    upscale = lastscale;
//                }
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    //  Log.i("lzc", "ACTION_POINTER_UP");
                    if (isScale)
                        break;
                    changeMode(false);
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    //  Log.i("lzc", "ACTION_POINTER_DOWN");
                    if (isScale)
                        break;
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
        }


        if (isScale)
            requestIntercept(true);
        //  getParent().getParent().requestDisallowInterceptTouchEvent(true);

        gestureDetector.onTouchEvent(event);
        Orientation orientation = calcOrientation(event.getX(), event.getY(), downPoint.x, downPoint.y);
        if (event.getAction() == MotionEvent.ACTION_UP && drawRealBig && !scrollStart) {
            // onBigImgFlingStop(orientation);
            //  Log.i("lzc", "onBigImgFlingStop2222");

        }
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    /**
     * 计算方向
     *
     * @param
     * @return
     */
    private Orientation calcOrientation(float currentX, float currentY, float lastX, float lastY) {
        float dx = currentX - lastX;
        float dy = currentY - lastY;
        Orientation orientation;
        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0)
                orientation = toLeft;
            else
                orientation = toRight;
        } else if (Math.abs(dx) < Math.abs(dy)) {
            if (dy > 0)
                orientation = toTop;
            else
                orientation = toBottom;
        } else {
            orientation = Orientation.none;
        }

        return orientation;
    }


    private void normalDo(MotionEvent event) {
        float dx = event.getX() - startPoint.x;
        float dy = event.getY() - startPoint.y;
        if (Math.abs(dy) > 15 * ScreenData.density) {
            changeMode(true);
            mode = DRAG;
        }
    }

    private void changeMode(boolean isToMove) {
        if (isToMove) {
            this.setScaleType(ScaleType.MATRIX);
            requestIntercept(true);
            //  getParent().getParent().requestDisallowInterceptTouchEvent(true);
        } else {
            shutdown();
        }

    }


    //放大执行
    private void zoomDo(MotionEvent event) {
        //  Log.i("lzc", "zoomDo");
        float endDis = distance(event);
        if (endDis > 10f) {
            float scale = endDis / startDis;
            midPoint = mid(event);
            currentMaritx.postScale(scale, scale, midPoint.x, midPoint.y);
            startDis = endDis;
            changeAlphaByScale(currentMaritx);
        }
    }

    //根据放大倍率改变透明度
    private void changeAlphaByScale(Matrix matrix) {
        setActivityAlpha(getScaleFormMatrix(matrix) + 0.3f);
    }

    //获取当大倍数

    private float getScaleFormMatrix(Matrix matrix) {
        RectF rectF = new RectF();
        RectF rectFOrigin = new RectF();
        rectF.right = getDrawable().getIntrinsicWidth();
        rectF.bottom = getDrawable().getIntrinsicHeight();
        rectFOrigin.right = rectF.right;
        rectFOrigin.left = rectF.left;
        matrix.mapRect(rectF);
        originMatrix.mapRect(rectFOrigin);
        float currentWidth = rectF.right - rectF.left;
        return currentWidth / (rectFOrigin.right - rectFOrigin.left);
    }

    //根据偏移量改变透明度
    private float changeAlphaByTrans(Matrix matrix) {
        float distance = getCenterDistance(matrix);
//        Log.i("lzc", "distance" + distance);
        float maxDistance = (float) Math.sqrt(Math.pow(getWidth() / 2, 2) + Math.pow(getHeight() / 2, 2));
        setActivityAlpha((maxDistance - distance) / maxDistance + 0.15f);
        return (maxDistance - distance) / maxDistance;
    }


    //拖动执行
    private void dragDo(MotionEvent event) {
        //  Log.i("lzc", "dragDo");
        float dx = event.getX() - lastPoint.x;
        float dy = event.getY() - lastPoint.y;
        if (Math.abs(dx) > 5 || Math.abs(dy) > 5) {
            currentMaritx.postTranslate(dx * Factor, dy * Factor);
            changeAlphaByTrans(currentMaritx);


            float disCur = getCenterDistance(currentMaritx);
            float maxDistance = (float) Math.sqrt(Math.pow(getWidth() / 2, 2) + Math.pow(getHeight() / 2, 2));
            float beilv = (maxDistance - disCur) / maxDistance;
//            Log.i("lzc", "beilv" + beilv + "---" + disCur);
            PointF pointF = getMatrixCenter(currentMaritx);
            currentMaritx = new Matrix(originMatrix);
            currentMaritx.postTranslate(pointF.x - getWidth() / 2, pointF.y - getHeight() / 2);
            currentMaritx.postScale(beilv, beilv, pointF.x, pointF.y);
//            Log.i("lzc", "trans" + (pointF.x - getWidth() / 2) + "---" + (pointF.y - getHeight() / 2));
//            Log.i("lzc", "currentCenter" + getWidth() / 2 + "--" + getHeight() / 2);
//            Log.i("lzc", "currentMaritx" + getMatrixCenter(currentMaritx));

        }


    }

    private RectF getMatrixMapRect(Matrix matrix) {
        RectF rectF = new RectF();
        rectF.right = getDrawable().getIntrinsicWidth();
        rectF.bottom = getDrawable().getIntrinsicHeight();
        matrix.mapRect(rectF);
        return rectF;
    }

    //获取矩阵中心点
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

    //关闭
    private void shutdown() {
        if (currrentAlpha < 0.8f)
            animToClose(currentMaritx);
        else
            animToMatrix(currentMaritx, originMatrix);
    }

    //放大动画
    private void animToScale() {
        RectF rectF = getMatrixMapRect(originMatrix);
        float widthRatio = getWidth() / rectF.width();
        float heightRatio = getHeight() / rectF.height();
        float scaleRatio;
        boolean isWidthMore = widthRatio > heightRatio;
        if (widthRatio <= 1f && heightRatio <= 1f) {
            scaleRatio = maxScale;
        } else {
            scaleRatio = isWidthMore ? widthRatio : heightRatio;
        }
        //  Log.i("lzc", "scaleRatio" + scaleRatio + "---" + widthRatio + "---" + heightRatio);

        if (scaleRatio < maxScale)
            scaleRatio = maxScale;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, scaleRatio);
        valueAnimator.setDuration(150);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentMaritx = new Matrix(originMatrix);
                currentMaritx.postScale((Float) animation.getAnimatedValue(), (Float) animation.getAnimatedValue(), getWidth() / 2, getHeight() / 2);
                BigImgImageView.this.setImageMatrix(currentMaritx);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isScale = true;
                isAnim = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnim = false;
                changeMode(true);
                // Log.i("lzc","End_currentMaritx"+getMatrixMapRect(currentMaritx).width()/getMatrixMapRect(currentMaritx).height());
                RectF realRect = getMatrixMapRect(currentMaritx);
                float radio = Math.max(realRect.width() / getWidth(), realRect.height() / getHeight());
                //    Log.i("lzc", "radioradioradio" + radio);
                if (radio > scrollMinRatio+1 || radio < 1 / scrollMinRatio) {
                    needLoadRealBySize = true;
                    scrollTo(-(int) (realRect.width() / 2 - getWidth() / 2), -(int) (realRect.height() / 2 - getHeight() / 2));
                }
                //   Log.i("lzc", "getScalllY" + BigImgImageView.this.getScrollX() + "---" + BigImgImageView.this.getScrollY() + "--" + realRect);
                if (needToLoadRealBigImg) {
                    initBitmapRegion();
                    onBigImgFlingStop();
                }

                //    Log.i("lzc", "onBigImgFlingStop33333");
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
    }

    private void initBitmapRegion() {
        BinaryResource binaryResource = Fresco.getImagePipelineFactory().getMainFileCache().getResource(new SimpleCacheKey(uri.toString()));
        if (binaryResource instanceof FileBinaryResource) {
            imgRotate = FileUntil.readPictureDegree(((FileBinaryResource) binaryResource).getFile().getPath());
            Log.i("lzc", "imgRotate  " + imgRotate);
        }

        InputStream inputStream = null;
        try {
            if (binaryResource == null) {
                if (uri.getScheme().equals("res")) {
                    inputStream = getResources().openRawResource(Integer.valueOf(uri.getPathSegments().get(uri.getPathSegments().size() - 1)));
                } else {
                    String path = FileUntil.UriToFile(uri, null);
                    if (path == null)
                        return;
                    inputStream = new FileInputStream(new File(path));
                }

            } else {
                inputStream = binaryResource.openStream();
            }
            if (inputStream != null) {
                bitmapRegionDecoder = BitmapRegionDecoder.newInstance(inputStream, false);
                bigImgRealWidth = imgRotate == 0 || imgRotate == 180 ? bitmapRegionDecoder.getWidth() : bitmapRegionDecoder.getHeight();
                bigImgRealHeight = imgRotate == 0 || imgRotate == 180 ? bitmapRegionDecoder.getHeight() : bitmapRegionDecoder.getWidth();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //动画关闭
    private void animToClose(final Matrix currentMatrix) {
        cancelDrawBigImg();
        this.setScaleType(ScaleType.MATRIX);
        float finallyDx = 0;
        float finallyDy = 0;
        float finallyBeilv = 0;
        float finallyAlpha = 0;
        final RectF rect = getMatrixMapRect(currentMatrix);
        if (messageUri == null) {
            finallyAlpha = 0;
            finallyDx = 0;
            finallyDy = 0;
            finallyBeilv = 0;
        } else {
            finallyAlpha = 0;
            finallyDx = messageUri.getCenterX() - getMatrixCenter(currentMatrix).x;
            finallyDy = messageUri.getCenterY() - getMatrixCenter(currentMatrix).y;
            finallyBeilv = Math.min(messageUri.getWidth() / rect.width(), messageUri.getHeight() / rect.height());
        }
        //   Log.i("lzc", "canshu" + finallyDx + "---" + finallyDy + "---" + finallyBeilv);
        PropertyValuesHolder propertyValuesHolderDx = PropertyValuesHolder.ofFloat("dx", 0, finallyDx);
        PropertyValuesHolder propertyValuesHolderDy = PropertyValuesHolder.ofFloat("dy", 0, finallyDy);
        PropertyValuesHolder propertyValuesHolderAlpha = PropertyValuesHolder.ofFloat("alpha", currrentAlpha, finallyAlpha);
        PropertyValuesHolder propertyValuesHolderBeilv = PropertyValuesHolder.ofFloat("beilv", 1, finallyBeilv);
        ValueAnimator valueAnimator = ValueAnimator.ofPropertyValuesHolder(propertyValuesHolderDx, propertyValuesHolderDy, propertyValuesHolderBeilv, propertyValuesHolderAlpha);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float dx = (float) animation.getAnimatedValue("dx");
                float dy = (float) animation.getAnimatedValue("dy");
                float beilv = (float) animation.getAnimatedValue("beilv");
                float alpha = (float) animation.getAnimatedValue("alpha");
                Matrix matrix = new Matrix(currentMatrix);
                matrix.postTranslate(dx, dy);
                PointF pointF1 = getMatrixCenter(matrix);
                matrix.postScale(beilv, beilv, pointF1.x, pointF1.y);
                BigImgImageView.this.setImageMatrix(matrix);
                setActivityAlpha(alpha);
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
                isScale = false;
                canMove = false;
                if (realBitmapWrapper != null)
                    realBitmapWrapper.recycle();
                Intent intent = new Intent();
                intent.setAction(ShowBigImgActivity.RECEIVER_NAME);
                intent.putExtra("shutdown", true);
                getContext().sendBroadcast(intent);
                destroyBigImg();
                requestIntercept(false);
                // getParent().getParent().requestDisallowInterceptTouchEvent(false);
                mode = NORMAL;


            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        long time = (long) (200 * 1.5f);
        valueAnimator.setDuration(time);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.start();
    }

    //动画至目标matrix
    private void animToMatrix(final Matrix currentMatrix, final Matrix targetMatrix, final int ScrollX, final int ScrollY) {
        Animator.AnimatorListener listener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnim = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnim = false;
                currentMaritx = new Matrix(targetMatrix);
                scrollTo(ScrollX, ScrollY);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
        animToMatrix(currentMatrix, targetMatrix, listener);
    }

    private void animToMatrix(final Matrix currentMatrix, final Matrix targetMatrix, Animator.AnimatorListener listener) {
        float finallyDx = 0;
        float finallyDy = 0;
        float finallyBeilv = 0;
        float finallyAlpha = 0;

        RectF rect = getMatrixMapRect(currentMatrix);
        finallyAlpha = 1;
        finallyDx = getMatrixCenter(targetMatrix).x - getMatrixCenter(currentMatrix).x;
        finallyDy = getMatrixCenter(targetMatrix).y - getMatrixCenter(currentMatrix).y;
        finallyBeilv = getMatrixMapRect(targetMatrix).width() / getMatrixMapRect(currentMatrix).width();
        //  Log.i("lzc", "finallyBeilv" + finallyBeilv + "--" + finallyDx + "---" + finallyDy);
        if (Math.abs(finallyBeilv - 1) < 0.01 && finallyDx < 1 && finallyDy < 1)
            return;

        PropertyValuesHolder propertyValuesHolderDx = PropertyValuesHolder.ofFloat("dx", 0, finallyDx);
        PropertyValuesHolder propertyValuesHolderDy = PropertyValuesHolder.ofFloat("dy", 0, finallyDy);
        PropertyValuesHolder propertyValuesHolderAlpha = PropertyValuesHolder.ofFloat("alpha", currrentAlpha, finallyAlpha);
        PropertyValuesHolder propertyValuesHolderBeilv = PropertyValuesHolder.ofFloat("beilv", 1, finallyBeilv);
        ValueAnimator valueAnimator = ValueAnimator.ofPropertyValuesHolder(propertyValuesHolderDx, propertyValuesHolderDy, propertyValuesHolderBeilv, propertyValuesHolderAlpha);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float dx = (float) animation.getAnimatedValue("dx");
                float dy = (float) animation.getAnimatedValue("dy");
                float beilv = (float) animation.getAnimatedValue("beilv");
                float alpha = (float) animation.getAnimatedValue("alpha");
                Matrix matrix = new Matrix(currentMatrix);
                matrix.postTranslate(dx, dy);
                PointF pointF1 = getMatrixCenter(matrix);
                matrix.postScale(beilv, beilv, pointF1.x, pointF1.y);
                BigImgImageView.this.setImageMatrix(matrix);
                setActivityAlpha(alpha);
            }
        });
        valueAnimator.addListener(listener);
        long time = (long) (200);
        valueAnimator.setDuration(time);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.start();
    }

    private void animToMatrix(final Matrix currentMatrix, final Matrix targetMatrix) {
        Animator.AnimatorListener listener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnim = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnim = false;
                currentMaritx = new Matrix(targetMatrix);
                if (!targetMatrix.equals(BigImgImageView.this.originMatrix)) {
                    return;
                }
                if (realBitmapWrapper != null)
                    realBitmapWrapper.recycle();
                isScale = false;
                //  Log.i("lzc", "animEnd");
                requestIntercept(false);
                mode = NORMAL;


            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
        animToMatrix(currentMatrix, targetMatrix, listener);
    }

    private void requestIntercept(boolean intercept) {
        BigImgImageView.this.getParent().getParent().requestDisallowInterceptTouchEvent(intercept);
    }


    public boolean isCanMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public BigImgShowData.MessageUri getMessageUri() {
        return messageUri;
    }

    public void setMessageUri(BigImgShowData.MessageUri messageUri) {
        this.messageUri = messageUri;
    }

    public boolean isAnim() {
        return isAnim;
    }

    public void setAnim(boolean anim) {
        isAnim = anim;
    }

    public boolean isNeedToLoadRealBigImg() {
        return needToLoadRealBigImg;
    }

    public void setNeedToLoadRealBigImg(boolean needToLoadRealBigImg) {
        this.needToLoadRealBigImg = needToLoadRealBigImg;
    }


    /**
     * 计算大图真实坐标
     *
     * @param width
     * @param height
     * @param preloadFlag
     * @param scrollX
     * @param scrollY     @return
     */

    private Rect calcBitmapRect(float ratio, float ratioHeight, int width, int height, RectF current, Orientation preloadFlag, int scrollX, int scrollY) {
        Rect rect = bigAsyncData.rect;
        //放大校正系数
        if (preloadFlag == Orientation.none) {
            rect = new Rect();
            rect.left = (int) ((current.width() / 2f - getWidth() / 2f + scrollX) * ratio);
            rect.top = (int) ((current.height() / 2f - getHeight() / 2f + scrollY) * ratioHeight);
            rect.right = (int) (rect.left + getWidth() * ratio);
            rect.bottom = (int) (rect.top + getHeight() * ratioHeight);
            LoadScaleWidth = rect.width();
            LoadScaleHeight = rect.height();
        }
        if (preloadFlag != Orientation.none)
            rect = new Rect(realBitmapWrapper.current.calcRect);
        switch (preloadFlag) {
//            case toLeft:
//                rect.offset((int) (-LoadScaleWidth), 0);
//                break;
//            case toRight:
//                rect.offset((int) (LoadScaleWidth), 0);
//                break;
//            case toTop:
//                rect.offset(0, (int) (-LoadScaleHeight));
//                break;
//            case toBottom:
//                rect.offset(0, (int) (LoadScaleHeight));
//                break;

            case toLeft:
                rect.set(rect.left - LoadScaleWidth, rect.top, rect.left, rect.bottom);
                break;
            case toRight:
                rect.set(rect.right, rect.top, rect.right + LoadScaleWidth, rect.bottom);
                break;
            case toTop:
                rect.set(rect.left, rect.top - LoadScaleWidth, rect.right, rect.top);
                break;
            case toBottom:
                rect.set(rect.left, rect.bottom, rect.right, rect.bottom + LoadScaleWidth);
                break;
            case none:
                break;
        }
        if (rect.left < 0)
            rect.left = 0;
        if (rect.right > width)
            rect.right = width;
        if (rect.top < 0)
            rect.top = 0;
        if (rect.bottom > height)
            rect.bottom = height;
        if (preloadFlag == toTop || preloadFlag == toLeft) {
            if (rect.width() < LoadScaleWidth) {
                rect.left -= LoadScaleWidth - rect.width();
            }
            if (rect.height() < LoadScaleHeight) {
                rect.top -= LoadScaleHeight - rect.height();
            }
        }
        return rect;
    }


    private class BigAsyncData implements Cloneable {
        Rect rect = new Rect();
        RectF target = new RectF();

        public BigAsyncData() {
        }

        public BigAsyncData(Rect rect, RectF target) {
            this.rect = rect;
            this.target = target;
        }

        @Override
        protected Object clone() {
            return new BigAsyncData(new Rect(rect), new RectF(target));
        }
    }

    public class AsyncBigImg extends AsyncTask<BigAsyncData, Object, RealBitmap> {
        private boolean isCancel = false;

        @Override
        protected RealBitmap doInBackground(BigAsyncData... params) {
            // Log.i("lzc", "startAsyncBigImg");
            BigAsyncData bigAsyncData = params[0];
            if (bitmapRegionDecoder == null)
                return null;

            RealBitmap realBitmapT = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bitmap = null;
            try {
                bitmap = bitmapRegionDecoder.decodeRegion(changRotateRect(imgRotate, bigAsyncData.rect), options);
                if (imgRotate != 0) {
                    Bitmap old = bitmap;
                    bitmap = FileUntil.rotateBitmap(bitmap, imgRotate);
                    old.recycle();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            if (bitmap != null)
                realBitmapT = new RealBitmap();
            else
                return null;
            realBitmapT.bitmap = bitmap;
            realBitmapT.rect1 = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            realBitmapT.targetRect = bigAsyncData.target;
            realBitmapT.calcRect = bigAsyncData.rect;

            Log.i("lzc", "endAsyncBigImg" + bigAsyncData.rect + "    " + realBitmapT.targetRect);
            if (isCancel) {
                realBitmapT.recycle();
                realBitmapT = null;
            }
            return realBitmapT;


        }

        private synchronized Rect changRotateRect(int imgRotate, Rect rect) {
            changeRotateRect.setEmpty();
            if (imgRotate == 0)
                changeRotateRect.set(rect);
            else if (imgRotate == 180) {
                changeRotateRect.set(bigImgRealWidth - rect.left, bigImgRealHeight - rect.top, bigImgRealWidth - rect.right, bigImgRealHeight - rect.bottom);
            } else if (imgRotate == 90) {
                changeRotateRect.set(bigImgRealHeight - rect.top, bigImgRealWidth - rect.left, bigImgRealHeight - rect.bottom, bigImgRealWidth - rect.right);

            } else if (imgRotate == 270) {
                changeRotateRect.set(rect.top, rect.left, rect.bottom, rect.right);
            }
            return changeRotateRect;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            isCancel = true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(RealBitmap realBitmap) {
            if (realBitmap == null)
                return;
            //   Log.i("lzc", "realBitmap22222  " + System.currentTimeMillis());
            realBitmapWrapper.add(realBitmap);
            drawRealBig = true;
            BigImgImageView.this.invalidate();
        }


    }


    public void destroyBigImg() {
        if (bitmapRegionDecoder != null)
            bitmapRegionDecoder.recycle();
    }
}
