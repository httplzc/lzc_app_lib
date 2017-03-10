package com.yioks.lzclib.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;

import com.yioks.lzclib.Adapter.ShowBigImgViewPagerAdapter;
import com.yioks.lzclib.Data.BigImgShowData;
import com.yioks.lzclib.R;
import com.yioks.lzclib.View.ViewPagerIndicator;

public class ShowBigImgActivity extends AppCompatActivity {
    private View backGround;
    private ViewPager viewPager;
    private ViewPagerIndicator viewPagerIndicator;
    private ShowBigImgViewPagerAdapter showBigImgViewPagerAdapter;
    private BigImgShowData bigImgShowData;
    protected BroadcastReceiver broadcastReceiver;
    public static final String RECEIVER_NAME = "com.yioks.lzclib.showBigImg.alpha.callBack";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_big_img_dialog_layout);
        bigImgShowData = (BigImgShowData) getIntent().getParcelableExtra("data");
        initView();
        initBroadCaseReceiver();
    }

    private void initBroadCaseReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getBooleanExtra("shutdown", false)) {
                    finish();
                    overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
                } else {
                    setActivityAlpha(intent.getFloatExtra("alpha", 1));
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVER_NAME);
        registerReceiver(broadcastReceiver, intentFilter);
    }


    private void initView() {
        backGround = findViewById(R.id.background);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPagerIndicator = (ViewPagerIndicator) findViewById(R.id.view_pager_indicator);
        viewPagerIndicator.setCount(bigImgShowData.getCount());
        showBigImgViewPagerAdapter = new ShowBigImgViewPagerAdapter(this, bigImgShowData);
        viewPager.setAdapter(showBigImgViewPagerAdapter);
        viewPager.addOnPageChangeListener(showBigImgViewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                viewPagerIndicator.setOffX(position, positionOffset);
                viewPagerIndicator.invalidate();
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                if (state == ViewPager.SCROLL_STATE_IDLE) {
//                    Picasso.with(ShowBigImgActivity.this).resumeTag("showBigImg");
//
//                } else {
//                    Picasso.with(ShowBigImgActivity.this).pauseTag("showBigImg");
//                }
            }
        });
    }

    public static void showBigImg(Context context, BigImgShowData bigImgShowData) {
        Intent intent = new Intent(context, ShowBigImgActivity.class);
        intent.putExtra("data", bigImgShowData);
        context.startActivity(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    public void setActivityAlpha(float alpha) {
        //  ScreenData.DownScreenColor(this, alpha);
        if (alpha < 0)
            alpha = 0.15f;
        if (alpha > 1)
            alpha = 1;
        backGround.setAlpha(alpha);
    }
}
