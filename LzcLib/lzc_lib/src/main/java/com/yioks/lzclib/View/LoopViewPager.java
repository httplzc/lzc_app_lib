package com.yioks.lzclib.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.yioks.lzclib.Data.ScreenData;
import com.yioks.lzclib.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ${User} on 2017/4/10 0010.
 */

public class LoopViewPager extends FrameLayout {
    private InnerViewPager viewPager;
    private ViewPagerIndicator pagerIndicator;
    private PagerAdapter adapter;
    private TimerTask timerTask;
    private Timer timer;
    private auto auto;
    private boolean autoScroll=true;
    private int indicatorSize;

    public LoopViewPager(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.loop_viewpager_layout, this, true);
        viewPager = (InnerViewPager) view.findViewById(R.id.view_pager);
        pagerIndicator = (ViewPagerIndicator) view.findViewById(R.id.view_pager_indicator);
    }

    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LoopViewPager);
        autoScroll = typedArray.getBoolean(R.styleable.LoopViewPager_enable_auto_scroll, true);
        indicatorSize = (int) typedArray.getDimension(R.styleable.LoopViewPager_indicator_size, 7 * ScreenData.density);
        typedArray.recycle();
        pagerIndicator.setRatio(indicatorSize);
    }


    public LoopViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initAttrs(attrs);
    }

    public void invalidateData() {
        pagerIndicator.setCount(adapter.getCount());
        adapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0, false);
    }


    public void initData(final PagerAdapter pagerAdapter) {
        adapter = pagerAdapter;
        viewPager.setAdapter(pagerAdapter);
        pagerIndicator.setCount(pagerAdapter.getCount());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //  Log.i("lzc", "position" + position + "---" + positionOffset);
                pagerIndicator.setOffX(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (pagerAdapter.getCount() != 0 && autoScroll) {
            startAuto();
        }

    }

    public void cancelAutoEvent() {
        if (auto != null)
            viewPager.removeCallbacks(auto);
        cancelAuto();
    }

    public void startAutoEvent() {
        if (!autoScroll)
            return;
        auto = new auto();
        viewPager.postDelayed(auto, 2000);
    }

    public class auto implements Runnable {

        @Override
        public void run() {
            startAuto();
        }
    }

    public void startAuto() {
        if (!autoScroll)
            return;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                viewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        if (viewPager.getCurrentItem() != adapter.getCount() - 1)
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                        else {
                            viewPager.setCurrentItem(0, true);
                        }
                    }
                });
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 2000, 2000);
    }

    public void cancelAuto() {
        if (timer != null && timerTask != null) {
            timerTask.cancel();
            timer.cancel();
        }

    }

    public boolean isAutoScroll() {
        return autoScroll;
    }

    public void setAutoScroll(boolean autoScroll) {
        if (autoScroll != this.autoScroll) {
            this.autoScroll = autoScroll;
            if (!autoScroll)
                cancelAuto();
            else
                startAuto();
        }

    }
}
