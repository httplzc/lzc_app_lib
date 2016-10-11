package com.yioks.lzclib.View;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yioks.lzclib.R;


/**
 * Created by Administrator on 2016/8/12 0012.
 */
public class FitRelativeLayout extends RelativeLayout {
    private float ratio=1;
    private Context context;
    private boolean enable=true;
    private ImageView imageView;
    private TextView textView1;
    private TextView textView2;
    public FitRelativeLayout(Context context) {
        super(context);
        this.context=context;
    }
    public FitRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        initData(attrs);
    }

    public FitRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        initData(attrs);
    }

    private void initData(AttributeSet attrs) {
        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.FitRelativeLayout);
        ratio=typedArray.getFloat(R.styleable.FitRelativeLayout_r_ratio, 1f);
        enable=typedArray.getBoolean(R.styleable.FitRelativeLayout_enable,true);
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
        ChangeEnable(enable);
    }

    private void initView() {
        imageView= (ImageView) getChildAt(0);
        LinearLayout linearLayout= (LinearLayout) getChildAt(1);
        if(linearLayout.getChildAt(0) instanceof ViewGroup)
        {
            textView1 = (TextView)((ViewGroup)(linearLayout.getChildAt(0))).getChildAt(0);
        }
        else
        {
            textView1 = (TextView) linearLayout.getChildAt(0);
        }

        textView2= (TextView) linearLayout.getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width=MeasureSpec.getSize(widthMeasureSpec);
        int mode=MeasureSpec.EXACTLY;
        heightMeasureSpec=MeasureSpec.makeMeasureSpec((int) (width*(1f/ratio)),mode);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        ChangeEnable(enable);
        this.enable = enable;
    }

    private void ChangeEnable(boolean state)
    {
        super.setEnabled(state);
        if(state)
        {
            Drawable drawable= DrawableCompat.wrap(imageView.getDrawable());
            DrawableCompat.setTintList(drawable, null);
            imageView.setImageDrawable(drawable);
            imageView.invalidateDrawable(drawable);
            textView1.setTextColor(ContextCompat.getColor(context, R.color.gray_text_deep));
        }
        else
        {
            Drawable drawable= DrawableCompat.wrap(imageView.getDrawable());
            DrawableCompat.setTintList(drawable, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray)));
            imageView.setImageDrawable(drawable);
            imageView.invalidateDrawable(drawable);
            textView1.setTextColor(ContextCompat.getColor(context, R.color.gray_text));
        }

    }

}
