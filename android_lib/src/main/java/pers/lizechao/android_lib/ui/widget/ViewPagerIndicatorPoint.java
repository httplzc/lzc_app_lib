package pers.lizechao.android_lib.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import pers.lizechao.android_lib.R;
import pers.lizechao.android_lib.ui.common.CommInfinitePagerAdapter;


/**
 * Created by Lzc on 2016/9/27 0027.
 */
public class ViewPagerIndicatorPoint extends View {
    private Paint paint;
    private Drawable pointBackground;
    private Drawable pointForeground;
    //白点数
    private int count = 0;
    //实际白点偏移量
    private int offX;
    private int position;
    //当前偏移量  0-1
    private float offset;
    private int pointIntervalSize;

    public ViewPagerIndicatorPoint(Context context) {
        super(context);
        initPaint();
        initDefault();
    }

    private void initDefault() {
        if (pointBackground == null)
            pointBackground = ContextCompat.getDrawable(getContext(), R.drawable.viewpager_indicator_point_background);
        if (pointForeground == null)
            pointForeground = ContextCompat.getDrawable(getContext(), R.drawable.viewpager_indicator_point_forground);
        if (pointIntervalSize == -1)
            pointIntervalSize = pointBackground.getMinimumWidth() * 2;
    }


    public ViewPagerIndicatorPoint(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(attrs);
        initPaint();
        initDefault();
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicatorPoint);
        pointBackground = typedArray.getDrawable(R.styleable.ViewPagerIndicatorPoint_point_background);
        pointForeground = typedArray.getDrawable(R.styleable.ViewPagerIndicatorPoint_point_foreground);
        pointIntervalSize = (int) typedArray.getDimension(R.styleable.ViewPagerIndicatorPoint_point_interval_size, -1);
        typedArray.recycle();

    }


    /**
     * 初始化画笔
     */
    private void initPaint() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (count != 0 && count != 1) {
            int height = pointBackground.getMinimumHeight();
            int width = count * pointBackground.getMinimumWidth() + (count - 1) * pointIntervalSize;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        } else {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (count == 0)
            return;
        int startWidth = getPaddingLeft();
        int startHeight = getPaddingTop();
        int unitWidth = pointBackground.getMinimumWidth();
        int unitHeight = pointBackground.getMinimumHeight();
        for (int i = 0; i < count; i++) {
            int left = startWidth + i * (unitWidth + pointIntervalSize);
            int top = startHeight;
            pointBackground.setBounds(left, top, left + unitWidth, top + unitHeight);
            pointBackground.draw(canvas);
        }
        int left = (int) (startWidth + (position + offset) * (unitWidth + pointIntervalSize));
        int top = startHeight;
        pointForeground.setBounds(left, top, left + unitWidth, top + unitHeight);
        pointForeground.draw(canvas);
    }


    public void bindViewPager(ViewPager viewPager, CommInfinitePagerAdapter commInfinitePagerAdapter) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                position = position % commInfinitePagerAdapter.getRealCount();
                setOffX(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setCount(commInfinitePagerAdapter.getRealCount());
        commInfinitePagerAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                setCount(commInfinitePagerAdapter.getRealCount());
            }
        });

    }

    public void bindViewPager(ViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setOffX(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setCount(viewPager.getAdapter().getCount());
        viewPager.getAdapter().registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                setCount(viewPager.getAdapter().getCount());
            }
        });

    }

    public int getOffX() {
        return offX;
    }

    private void setOffX(int position, float offset) {
        this.position = position;
        this.offset = offset;
        this.invalidate();
    }

    public int getCount() {
        return count;
    }

    private void setCount(int count) {
        this.count = count;
        requestLayout();
    }
}
