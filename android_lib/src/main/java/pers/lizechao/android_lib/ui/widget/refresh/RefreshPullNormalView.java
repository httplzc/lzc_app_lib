package pers.lizechao.android_lib.ui.widget.refresh;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import pers.lizechao.android_lib.ui.manager.ScreenManager;
import pers.lizechao.android_lib.ui.widget.RefreshParent;

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
 * Date: 2018-07-30
 * Time: 13:06
 */
public abstract class RefreshPullNormalView extends RefreshParent.PullView {
    //动画速度阻尼
    private float animTimeRatio = 0.8f;
    private boolean anim;

    protected final ScreenManager screenManager;

    protected ValueAnimator lastValueAnimator;


    //当前刷新状态
    public enum PullStatus {
        //正在刷新  正常 释放可刷新 释放为取消
        ON_REFRESH, REFRESH_SUCCEED, NORMAL, PULL_REFRESH, PULL_CANCEL
    }

    //当前滑动状态
    protected PullStatus pullStatus = PullStatus.NORMAL;

    public RefreshPullNormalView(@NonNull Context context, boolean isTop) {
        super(context, isTop);
        screenManager = new ScreenManager(context);
    }

    protected abstract View onCreateView(Context context, ViewGroup viewGroup);


    @Override
    protected void onSucceed() {
        if (pullStatus == PullStatus.ON_REFRESH) {
            onPullStateChange(PullStatus.REFRESH_SUCCEED);
            animTo(0);
        }
    }

    @Override
    protected void onFail() {
        if (pullStatus == PullStatus.ON_REFRESH) {
            animTo(0);
        }
    }


    @Override
    protected void onRefresh(boolean anim) {
        if (anim)
            animTo(getRefreshHeight(), () -> onPullStateChange(PullStatus.ON_REFRESH));
        else {
            callGetData();
        }
    }

    @Override
    public void addPullHeight(int pullHeightAdd) {
        super.addPullHeight(pullHeightAdd);
        onPullHeightChange();
    }

    @Override
    public void setPullHeight(int hideHeight) {
        super.setPullHeight(hideHeight);
        onPullHeightChange();
    }


    protected void onPullHeightChange() {
        //大于取消高度
        if (getPullHeight() > getCancelHeight()) {
            onPullStateChange(PullStatus.PULL_REFRESH);
        }
        //小于取消高度
        else if (getPullHeight() > 0) {
            if (pullStatus != PullStatus.PULL_CANCEL) {
                onPullStateChange(PullStatus.PULL_CANCEL);
            }
        }
        //完全收回
        else if (getPullHeight() == 0) {
            onPullStateChange(PullStatus.NORMAL);
        }
    }

    @Override
    protected void onUp() {
        if (pullStatus == PullStatus.PULL_REFRESH) {
            //回退到刷新
            animTo(getRefreshHeight(), () -> onPullStateChange(PullStatus.ON_REFRESH));
        } else {
            //回退
            animTo(0);
        }
    }


    private void animTo(int height) {
        animTo(height, null);
    }

    private void animTo(int height, Runnable runnable) {
        if (anim && lastValueAnimator != null) {
            lastValueAnimator.cancel();
        }
        anim = true;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(getPullHeight(), height);
        lastValueAnimator = valueAnimator;
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration(getAnimTime(Math.abs(getPullHeight() - height)));
        valueAnimator.addUpdateListener(animation -> setPullHeight(((Float) animation.getAnimatedValue()).intValue()));
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (runnable != null)
                    runnable.run();
                anim = false;
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

    protected long getAnimTime(float distance) {
        long time = (long) (distance / animTimeRatio);
        if (time > 1000) {
            return 1000L;
        }
        return time;
    }


    @Override
    protected boolean handlePull() {
        return !anim && pullStatus != PullStatus.ON_REFRESH && pullStatus != PullStatus.REFRESH_SUCCEED;
    }

    public void setAnimTimeRatio(float animTimeRatio) {
        this.animTimeRatio = animTimeRatio;
    }

    //获取取消高度
    public int getCancelHeight() {
        return screenManager.DpToPx(45);

    }

    //获取刷新时底部高度
    public int getRefreshHeight() {
        return screenManager.DpToPx(45);
    }

    //当拖动状态改变
    public void onPullStateChange(PullStatus newStatus) {
        if (pullStatus == newStatus)
            return;
        this.pullStatus = newStatus;
        onViewStateChange(pullStatus);
        if (newStatus == PullStatus.ON_REFRESH) {
            callGetData();
        }
    }

    protected abstract void onViewStateChange(PullStatus newStatus);
}
