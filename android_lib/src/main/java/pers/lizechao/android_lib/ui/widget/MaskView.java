package pers.lizechao.android_lib.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

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
 * Time: 13:43
 * 用于显示一个扣空中间的覆盖层
 */
public class MaskView extends View {
    private boolean is_circle = false;
    private int borderlineColor = Color.WHITE;
    private float borderline = 2f;
    private Paint paint;
    private PorterDuffXfermode xFermode;
    //横向边距
    private float maskPadding;
    //比例 w/h
    private float ratio = 1;
    private final RectF rectF = new RectF();

    public MaskView(Context context) {
        super(context);
        init(context);
    }

    public void init(Context context) {
        setLayerType(LAYER_TYPE_HARDWARE, null);
        paint = new Paint();
        paint.setAntiAlias(true);
        xFermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

    }

    public MaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initAttr(attrs);
    }


    public MaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initAttr(attrs);
    }

    public void initAttr(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MaskView);
        maskPadding = typedArray.getDimension(R.styleable.MaskView_mask_padding, maskPadding);
        ratio = typedArray.getFloat(R.styleable.MaskView_mask_ratio, ratio);
        borderline = typedArray.getDimension(R.styleable.MaskView_border_line_width, 0);
        is_circle = typedArray.getBoolean(R.styleable.MaskView_isCircle, is_circle);
        borderlineColor = typedArray.getColor(R.styleable.MaskView_border_line_color, borderlineColor);
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setXfermode(xFermode);
        paint.setStyle(Paint.Style.FILL);
        float heightPadding = (getHeight() - ((getWidth() - maskPadding * 2) / ratio)) / 2;
        rectF.set(maskPadding, heightPadding, getWidth() - maskPadding, getHeight() - heightPadding);
        if (is_circle) {
            canvas.drawCircle((rectF.left + rectF.right) / 2f, (rectF.top + rectF.bottom) / 2f, rectF.width() / 2f, paint);
        } else {
            canvas.drawRect(rectF, paint);
        }
        paint.setXfermode(null);
        if (borderline == 0) {
            return;
        }
        //绘制内边框
        paint.setColor(borderlineColor);
        paint.setStrokeWidth(borderline);
        paint.setStyle(Paint.Style.STROKE);
        if (is_circle) {
            canvas.drawCircle((rectF.left + rectF.right) / 2f, (rectF.top + rectF.bottom) / 2f, rectF.width() / 2f, paint);
        } else {
            canvas.drawRect(rectF.left - borderline / 2f, rectF.top - borderline / 2f,
              rectF.right + borderline / 2f, rectF.bottom + borderline / 2f, paint);
        }

    }


    /**
     * @param maskPadding 横向边距
     * @param ratio       w/h
     */
    public void setMaskPadding(float maskPadding, float ratio) {
        this.maskPadding = maskPadding;
        this.ratio = ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public void setIs_circle(boolean is_circle) {
        this.is_circle = is_circle;
    }

    public void setBorderline(float borderline) {
        this.borderline = borderline;
    }

    public void setBorderlineColor(int borderlineColor) {
        this.borderlineColor = borderlineColor;
    }


}
