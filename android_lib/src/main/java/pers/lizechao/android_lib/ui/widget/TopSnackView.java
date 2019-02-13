package pers.lizechao.android_lib.ui.widget;

import android.app.Activity;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import pers.lizechao.android_lib.R;
import pers.lizechao.android_lib.utils.DeviceUtil;

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
 * Date: 2018-07-02
 * Time: 15:54
 */
public class TopSnackView {
    private String message;
    private View mView;
    public static final int TIME_SHORT = 3000;
    private int time = TIME_SHORT;
    public static final int TIME_LONG = 5000;
    private boolean isShow;
    private TextView text = null;
    private ViewGroup parentView;
    private boolean newTime;
    private boolean isAnimBack;
    private final Activity context;



    public TopSnackView(Activity context) {
        this.context = context;
        initRealView();
    }


    private final Runnable cancelRunnable = new Runnable() {
        @Override
        public void run() {
            if (newTime) {
                newTime = false;
                mView.postDelayed(this, time);
            } else {
                removeView();
            }
        }
    };


    private void initRealView() {
        mView = LayoutInflater.from(context).inflate(
          R.layout.top_snack_layout, parentView, false);
        View status_bar = mView.findViewById(R.id.status_bar);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) status_bar.getLayoutParams();
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        layoutParams.height = DeviceUtil.getStatusBarHeight(context);
        status_bar.setLayoutParams(layoutParams);
        text = mView.findViewById(R.id.text);
        parentView = context.findViewById(android.R.id.content);
    }

    public void removeViewCurrent() {
        mView.clearAnimation();
        isShow = false;
        parentView.removeView(mView);
        mView.removeCallbacks(cancelRunnable);
    }

    public void removeView() {
        mView.setTranslationY(0);
        mView.removeCallbacks(cancelRunnable);
        isAnimBack = true;
        ViewCompat.animate(mView)
          .translationY(-mView.getHeight())
          .setInterpolator(new FastOutLinearInInterpolator())
          .setDuration(300)
          .setListener(new ViewPropertyAnimatorListenerAdapter() {
              @Override
              public void onAnimationStart(View view) {

              }

              @Override
              public void onAnimationEnd(View view) {
                  isAnimBack = false;
                  if (parentView != null) {
                      parentView.removeView(mView);
                  }
                  isShow = false;
              }

              @Override
              public void onAnimationCancel(View view) {
                  super.onAnimationCancel(view);
                  isAnimBack = false;
              }
          }).start();
    }

    private void startAnim() {
        mView.clearAnimation();
        ViewCompat.animate(mView)
          .translationY(0f)
          .setInterpolator(new FastOutLinearInInterpolator())
          .setDuration(300)
          .setListener(new ViewPropertyAnimatorListenerAdapter() {
              @Override
              public void onAnimationStart(View view) {

              }

              @Override
              public void onAnimationEnd(View view) {
                  mView.postDelayed(cancelRunnable, time);

              }
          }).start();
    }


    public void show() {
        if (!isShow) {
            isShow = true;
            parentView.addView(mView);
            ViewGroup.LayoutParams lp = mView.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mView.setLayoutParams(lp);
            mView.measure(0, 0);
            mView.setTranslationY(-mView.getMeasuredHeight());
            startAnim();
        } else {
            //若已经显示
            if (isAnimBack) {
                //若正在结束动画
                startAnim();
                this.newTime = false;
            } else
                this.newTime = true;
        }


    }


    public static TopSnackView make(Activity context, String string) {
        TopSnackView topSnackView = new TopSnackView(context);
        topSnackView.setMessage(string);
        return topSnackView;
    }

    public static TopSnackView make(Activity context, String string, int time) {
        TopSnackView topSnackView = new TopSnackView(context);
        topSnackView.setMessage(string);
        topSnackView.time = time;
        return topSnackView;
    }


    public String getMessage() {
        return message;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setMessage(String message) {
        this.message = message;
        if (text != null)
            text.setText(message);
    }

    public boolean isShow() {
        return isShow;
    }


}
