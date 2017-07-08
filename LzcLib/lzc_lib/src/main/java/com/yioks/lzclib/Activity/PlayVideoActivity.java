package com.yioks.lzclib.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;

import com.yioks.lzclib.R;
import com.yioks.lzclib.Untils.DialogUtil;
import com.yioks.lzclib.View.VideoPlayerFull;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

import static fm.jiecao.jcvideoplayer_lib.JCVideoPlayer.NORMAL_ORIENTATION;

public class PlayVideoActivity extends AppCompatActivity {

    private VideoPlayerFull videoPlayer;
    private SensorManager sensorManager;
    private VideoPlayerFull.JCAutoFullscreenListener sensorEventListener;
    public final static int Change = 0;
    public final static int Orientation = 1;
    public final static int Vertical = -1;
    public final static String ChangeOrientation = "ChangeOrientation";
    private int backUp1;
    private int backUp2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorEventListener = new VideoPlayerFull.JCAutoFullscreenListener();
        int canChangeOrientation = getIntent().getIntExtra(ChangeOrientation, Change);
        videoPlayer = (VideoPlayerFull) findViewById(com.yioks.lzclib.R.id.video_player);
        videoPlayer.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
            }
        });

        sensorEventListener.setCanChangeOrientation(canChangeOrientation == Change);

        videoPlayer.setUp(getIntent().getStringExtra("url"), JCVideoPlayer.SCREEN_WINDOW_FULLSCREEN, "");
        backUp1 = JCVideoPlayer.FULLSCREEN_ORIENTATION;
        backUp2 = NORMAL_ORIENTATION;
        if (canChangeOrientation == Orientation) {
            JCVideoPlayer.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            videoPlayer.startWindowFullscreen();

        } else {
            JCVideoPlayer.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
            NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
        JCVideoPlayer.lastAutoFullscreenTime = 0;
        videoPlayer.startButton.performClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JCVideoPlayer.FULLSCREEN_ORIENTATION = backUp1;
        NORMAL_ORIENTATION = backUp2;
    }

    /**
     * @param context
     * @param url
     * @param orientation PlayVideoActivity.Orientation ,PlayVideoActivity.Vertical,PlayVideoActivity.Vertical
     */
    public static void playVideo(Context context, String url, int orientation) {
        if (url == null || url.equals("")) {
            DialogUtil.showTopSnack(context, "该视频不能播放!");
        }
        Intent intent = new Intent();
        intent.setClass(context, PlayVideoActivity.class);
        intent.putExtra(PlayVideoActivity.ChangeOrientation, orientation);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
        JCVideoPlayerStandard.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

}
