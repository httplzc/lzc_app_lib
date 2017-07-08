package com.yioks.lzclib.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import com.yioks.lzclib.Data.ScreenData;


/**
 * Created by Yioks on 2016/7/1.
 */
public class PicCultBackground extends View {
    private float mleft;
    private float mtop;
    private float mright;
    private float mbottom;

    private float realWidth;
    private float realHeight;
    private boolean is_circle = false;

    public PicCultBackground(Context context) {
        super(context);
        init(context);
    }

    public void init(Context context) {
      //  this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public PicCultBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    public PicCultBackground(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float ParentWidth = getWidth();
        float ParentHeight = getHeight();
//        realWidth= PicCultActivity.backWidth;
//        realHeight=PicCultActivity.bili*realWidth;
        mleft = 20 * ScreenData.density + 5;
        mtop = (ParentHeight - realHeight) / 2f + 5;
        mright = mleft + realWidth - 5;
        mbottom = mtop + realHeight - 5;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        if (is_circle) {
            canvas.drawCircle((mleft + mright) / 2f, (mtop + mbottom) / 2f, realWidth / 2f, paint);
        } else {
            canvas.drawRect(mleft, mtop, mright, mbottom, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        paint.setColor(Color.parseColor("#FFFFFF"));
        paint.setStrokeWidth(2f);
        paint.setStrokeWidth(2f);
        paint.setStyle(Paint.Style.STROKE);
        if (is_circle) {
            canvas.drawCircle((mleft + mright) / 2f, (mtop + mbottom) / 2f, realWidth / 2f - 2, paint);
        } else {
            canvas.drawRect(mleft - 2, mtop - 2, mright + 2, mbottom + 2, paint);
        }

    }

    public float getMbottom() {
        return mbottom;
    }

    public void setMbottom(float mbottom) {
        this.mbottom = mbottom;
    }

    public float getMleft() {
        return mleft;
    }

    public void setMleft(float mleft) {
        this.mleft = mleft;
    }

    public float getMright() {
        return mright;
    }

    public void setMright(float mright) {
        this.mright = mright;
    }

    public float getMtop() {
        return mtop;
    }

    public void setMtop(float mtop) {
        this.mtop = mtop;
    }

    public float getMwidth() {
        return mright - mleft;
    }

    public float getMheight() {
        return mbottom - mtop;
    }

    public boolean is_circle() {
        return is_circle;
    }

    public void setIs_circle(boolean is_circle) {
        this.is_circle = is_circle;
    }

    public float getRealWidth() {
        return realWidth;
    }

    public void setRealWidth(float realWidth) {
        this.realWidth = realWidth;
    }

    public float getRealHeight() {
        return realHeight;
    }

    public void setRealHeight(float realHeight) {
        this.realHeight = realHeight;
    }
}
