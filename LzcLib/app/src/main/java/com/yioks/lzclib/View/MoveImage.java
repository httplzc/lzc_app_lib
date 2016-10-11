package com.yioks.lzclib.View;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.yioks.lzclib.Activity.PicCultActivity;
import com.yioks.lzclib.Data.ScreenData;


/**
 * Created by Yioks on 2016/7/1.
 */
public class MoveImage extends ImageView{
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
    private float offX;
    private float offY;
    private float dx;
    private float dy;


    private float maxleft;
    private float maxright;
    private float maxtop;
    private float maxbottom;
    private float lastscale=1;
    private float upscale=1;
    private boolean fromZoom=false;
    private Context context;
    private Matrix lastMatrix=new Matrix();



    public MoveImage(Context context) {
        super(context);
    }


    public MoveImage(Context context, AttributeSet paramAttributeSet) {
        super(context, paramAttributeSet);
        this.context=context;
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
                if (mode == DRAG) {
                    dx = event.getX() - startPoint.x;
                    dy = event.getY() - startPoint.y;

                    matrix = new Matrix(currentMaritx);


                    Matrix matrixteamp=new Matrix(currentMaritx);
                    matrixteamp.postTranslate(dx, dy);
                    RectF rectF=new RectF();
                    PicCultActivity picCultActivity= (PicCultActivity) context;
                    rectF.right=picCultActivity.getBitmapwidth();
                    rectF.bottom=picCultActivity.getBitmapheight();
                    matrixteamp.mapRect(rectF);
                    float left=rectF.left;
                    float right=rectF.right;
                    float bottom=rectF.bottom;
                    float top=rectF.top;

                    RectF currentRect=new RectF();
                    currentRect.right=picCultActivity.getBitmapwidth();
                    currentRect.bottom=picCultActivity.getBitmapheight();
                    currentMaritx.mapRect(currentRect);



                    float litmatHeight=(PicCultActivity.PicRealHeight-PicCultActivity.backHeight)/2f;
                    if (20* ScreenData.density>left&&litmatHeight>top&&right>(ScreenData.widthPX-20*ScreenData.density)&&bottom>(PicCultActivity.PicRealHeight-litmatHeight))
                    {

                        matrix=matrixteamp;
                    }
                    else
                    {


                        float realdx=dx;
                        float realdy=dy;

                        if(!(20*ScreenData.density>left))
                        {
                            realdx=20*ScreenData.density-currentRect.left;
                            realdx=realdx+0.1f*realdx/Math.abs(realdx);
                        }

                        if(!(litmatHeight>top))
                        {
                            realdy=litmatHeight-currentRect.top;
                            realdy=realdy+0.1f*realdy/Math.abs(realdy);
                        }
                        if(!(right>(ScreenData.widthPX-20*ScreenData.density)))
                        {
                            realdx=-(currentRect.right-(ScreenData.widthPX-20*ScreenData.density));
                            realdx=realdx+0.1f*realdx/Math.abs(realdx);
                        }
                        if(!(bottom>(PicCultActivity.PicRealHeight-litmatHeight)))
                        {
                            realdy=-(currentRect.bottom-(PicCultActivity.PicRealHeight-litmatHeight));
                            realdy=realdy+0.1f*realdy/Math.abs(realdy);
                        }
                        matrix.postTranslate(realdx, realdy);
                    }




                    //放大
                } else if (mode == ZOOM) {
                    float endDis = distance(event);
                    if (endDis > 10f) {
                        boolean flag=true;
                        float scale = endDis / startDis;
                        while(flag)
                        {

                            Matrix matrixteamp=moveLenlimit(scale);

                            if (matrixteamp!=null)
                            {
                                matrix=matrixteamp;
                                break;
                            }
                            else
                            {
                                scale+=0.001;

                                if(scale>=1)
                                {
                                    break;
                                }
                            }
                        }

                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mode = 0;
                if(!fromZoom)
                {
                    offX+=dx;
                    offY+=dy;
                }
                else
                {
                    upscale=lastscale;
                }
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
        if (matrix != null)
            this.setImageMatrix(matrix);
        lastMatrix.set(matrix);
        return true;
    }


    private Matrix moveLenlimit(float scale)
    {
        matrix = new Matrix();
        matrix.set(currentMaritx);
        float litmatHeight=(PicCultActivity.PicRealHeight-PicCultActivity.backHeight)/2f;
        Matrix matrixteamp=new Matrix(currentMaritx);
        matrixteamp.postScale(scale, scale,midPoint.x,midPoint.y);
        RectF rectF=new RectF();
        PicCultActivity picCultActivity= (PicCultActivity) context;
        rectF.right=picCultActivity.getBitmapwidth();
        rectF.bottom=picCultActivity.getBitmapheight();
        matrixteamp.mapRect(rectF);
        float left=rectF.left;
        float right=rectF.right;
        float bottom=rectF.bottom;
        float top=rectF.top;
        if(!(20*ScreenData.density>left&&litmatHeight>top&&right>(ScreenData.widthPX-20*ScreenData.density)&&bottom>(PicCultActivity.PicRealHeight-litmatHeight)))
        {
            return null;
        }
        else
        {
            return matrixteamp;
        }
    }

    private  float distance(MotionEvent event) {
        //两根线的距离
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }


    private  PointF mid(MotionEvent event) {
        float midx = event.getX(1) + event.getX(0);
        float midy = event.getY(1) + event.getY(0);
        return new PointF(ScreenData.widthPX / 2f, ScreenData.heightPX/ 2f);
       // return new PointF(1500,1500);
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
}
