package com.yioks.lzclib.Activity;

import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

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
    private int startPosition;
    private View anim;
    private static final int aninTime = 300;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_big_img_dialog_layout);
        bigImgShowData = (BigImgShowData) getIntent().getParcelableExtra("data");
        startPosition = getIntent().getIntExtra("startPosition", 0);
        initView();
        initBroadCaseReceiver();
        viewPager.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                viewPager.getViewTreeObserver().removeOnPreDrawListener(this);
                showStartAnim();
                return true;
            }
        });
    }

    private void showStartAnim() {
        showBigImgViewPagerAdapter.setIsanim(true);
        AnimationSet animationSet = new AnimationSet(true);
        BigImgShowData.MessageUri messageUri = bigImgShowData.getMessageUri(startPosition);
        if (messageUri == null)
            return;
        Animation animation = new ScaleAnimation(messageUri.getWidth() / anim.getWidth(), 1
                , messageUri.getHeight() / anim.getHeight(), 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        Animation animationTrans = new TranslateAnimation(messageUri.getCenterX() - messageUri.getWidth() / 2 - anim.getWidth() / 2, 0,
                messageUri.getCenterY() - messageUri.getHeight() / 2 - anim.getHeight() / 2, 0);
        animationSet.addAnimation(animation);
        animationSet.addAnimation(animationTrans);
        animationSet.setDuration(aninTime);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setAnimation(animationSet);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showBigImgViewPagerAdapter.setIsanim(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAlphaAnim();
        animationSet.start();
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
        anim = findViewById(R.id.anim);
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
        viewPager.setCurrentItem(startPosition, false);
    }

    public static void showBigImg(Context context, BigImgShowData bigImgShowData) {
        Intent intent = new Intent(context, ShowBigImgActivity.class);
        intent.putExtra("data", bigImgShowData);
        context.startActivity(intent);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (showBigImgViewPagerAdapter != null) {
                boolean complic = showBigImgViewPagerAdapter.back();
                if (!complic) {
                    finish();
                    overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
                }


            } else {
                finish();
                overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    public void startAlphaAnim() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setActivityAlpha((Float) animation.getAnimatedValue());
            }
        });
        valueAnimator.setDuration(aninTime);
        valueAnimator.start();
    }

    public void setActivityAlpha(float alpha) {
        //  ScreenData.DownScreenColor(this, alpha);
        if (alpha < 0)
            alpha = 0.15f;
        if (alpha > 1)
            alpha = 1;
        backGround.setBackgroundColor(Color.argb((int) (alpha * 255f), 0, 0, 0));
    }
}
