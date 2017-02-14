package com.yioks.lzclib.View;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yioks.lzclib.R;


/**
 * Created by ${User} on 2017/2/9 0009.
 */
public class TopSnackView {
    private Context context;
    private String message;
    private View mView;
    public static final int TIME_SHORT = 1000;
    private int time = TIME_SHORT;
    public static final int TIME_LONG = 2000;
    private static boolean isShow;
    private Handler handler;
    private TextView text = null;
    private ViewGroup parentView;


    private TopSnackView(Context context) {
        this.context = context;
        mView = LayoutInflater.from(context).inflate(
                R.layout.top_snack_layout, parentView, false);
        View status_bar = mView.findViewById(R.id.status_bar);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) status_bar.getLayoutParams();
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        layoutParams.height = getStatusBarHeight(context);
        status_bar.setLayoutParams(layoutParams);
        text = (TextView) mView.findViewById(R.id.text);

        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            parentView = (ViewGroup) activity.findViewById(android.R.id.content);
        }
        if (handler == null)
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 0)
                        removeView();
                    super.handleMessage(msg);
                }
            };
    }

    private void removeView() {

        ViewCompat.setTranslationY(mView, 0);
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
                        if (parentView != null) {
                            parentView.removeView(mView);
                            isShow = false;
                        }
                    }
                }).start();
    }

    private void showView() {
        parentView.addView(mView);
        ViewGroup.LayoutParams lp = mView.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mView.setLayoutParams(lp);
        isShow = true;
        mView.measure(0, 0);
        ViewCompat.setTranslationY(mView, -mView.getMeasuredHeight());
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
                        Message message = new Message();
                        message.what = 0;
                        handler.sendMessageDelayed(message, time);
                    }
                }).start();
    }


    /**
     * 获得状态栏高度
     */
    private static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    public static TopSnackView make(Context context, String string) {
        TopSnackView topSnackView = new TopSnackView(context);
        topSnackView.setMessage(string);
        return topSnackView;
    }

    public static TopSnackView make(Context context, String string, int time) {
        TopSnackView topSnackView = new TopSnackView(context);
        topSnackView.setMessage(string);
        topSnackView.time = time;
        return topSnackView;
    }

    public void show() {
        synchronized (TopSnackView.class) {
            showView();
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        if (text != null)
            text.setText(message);
    }
}
