package com.yioks.lzclib.View;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import fm.jiecao.jcvideoplayer_lib.JCUserAction;
import fm.jiecao.jcvideoplayer_lib.JCUtils;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerManager;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

import static fm.jiecao.jcvideoplayer_lib.JCVideoPlayerManager.getCurrentJcvd;

/**
 * Created by ${User} on 2017/4/7 0007.
 */

public class VideoPlayerFull extends JCVideoPlayerStandard {
    private OnClickListener onBackClickListener;

    public VideoPlayerFull(Context context) {
        super(context);
    }

    public VideoPlayerFull(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setUp(String url, int screen, Object... objects) {
        super.setUp(url, screen, objects);
        backButton.setOnClickListener(onBackClickListener);
        fullscreenButton.setVisibility(INVISIBLE);
        fullscreenButton.setEnabled(false);
    }

    public OnClickListener getOnBackClickListener() {
        return onBackClickListener;
    }

    public void setOnBackClickListener(OnClickListener onBackClickListener) {
        this.onBackClickListener = onBackClickListener;
        backButton.setOnClickListener(onBackClickListener);
    }

    public static class JCAutoFullscreenListener implements SensorEventListener {
        private boolean canChangeOrientation = true;

        public boolean isCanChangeOrientation() {
            return canChangeOrientation;
        }

        public void setCanChangeOrientation(boolean canChangeOrientation) {
            this.canChangeOrientation = canChangeOrientation;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {//可以得到传感器实时测量出来的变化值
            if (!canChangeOrientation)
                return;
            final float x = event.values[SensorManager.DATA_X];
            float y = event.values[SensorManager.DATA_Y];
            float z = event.values[SensorManager.DATA_Z];
            //过滤掉用力过猛会有一个反向的大数值
            if (((x > -15 && x < -10) || (x < 15 && x > 10)) && Math.abs(y) < 1.5) {
                if ((System.currentTimeMillis() - lastAutoFullscreenTime) > 2000) {
                    if (getCurrentJcvd() != null) {
                        OnClickListener backListener = ((VideoPlayerFull) JCVideoPlayerManager.getCurrentJcvd()).getOnBackClickListener();
                        getCurrentJcvd().autoFullscreen(x);
                        ((VideoPlayerFull) JCVideoPlayerManager.getCurrentJcvd()).setOnBackClickListener(backListener);
                    }
                    lastAutoFullscreenTime = System.currentTimeMillis();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }


    @Override
    public void autoFullscreen(float x) {
        if (isCurrentJcvd()
                && currentState == CURRENT_STATE_PLAYING
                && currentScreen != SCREEN_WINDOW_TINY) {
            if (x > 0) {
                JCUtils.getAppCompActivity(getContext()).setRequestedOrientation(
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                JCUtils.getAppCompActivity(getContext()).setRequestedOrientation(
                        ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
            startWindowFullscreen();
        }
    }

    @Override
    public void prepareMediaPlayer() {
        super.prepareMediaPlayer();
        initPlayer();
    }

    private void initPlayer()
    {
        this.backButton.setEnabled(true);
        this.backButton.setVisibility(View.VISIBLE);
        ((VideoPlayerFull) JCVideoPlayerManager.getCurrentJcvd()).setOnBackClickListener(onBackClickListener);
    }

    @Override
    public void onAutoCompletion() {
        //加上这句，避免循环播放video的时候，内存不断飙升。
        Runtime.getRuntime().gc();
        Log.i(TAG, "onAutoCompletion " + " [" + this.hashCode() + "] ");
        onEvent(JCUserAction.ON_AUTO_COMPLETE);
        dismissVolumeDialog();
        dismissProgressDialog();
        dismissBrightnessDialog();
        setUiWitStateAndScreen(CURRENT_STATE_AUTO_COMPLETE);
        JCUtils.saveProgress(getContext(), url, 0);

    }

    @Override
    public void clearFullscreenLayout() {

    }

}
