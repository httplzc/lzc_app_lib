package com.yioks.lzclib.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yioks.lzclib.R;
import com.yioks.lzclib.Untils.FunUntil;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by ${User} on 2017/4/7 0007.
 */

public class VideoPlayer extends JCVideoPlayerStandard {
    private float aspectRatio = -1f;

    public VideoPlayer(Context context) {
        super(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs, context);
    }

    private void initAttrs(AttributeSet attrs, Context context) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VideoPlayer);
        aspectRatio = typedArray.getFloat(R.styleable.VideoPlayer_video_aspect_ratio, -1f);
        typedArray.recycle();
    }

    @Override
    public void setUp(String url, int screen, Object... objects) {
        super.setUp(url, screen, objects);
        if (screen == SCREEN_LAYOUT_NORMAL) {
            backButton.setVisibility(GONE);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthRatio = 0;
        heightRatio = 0;
        if (aspectRatio - 0 > 0.001) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) ((float) MeasureSpec.getSize(widthMeasureSpec) / aspectRatio), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

    }

    @Override
    public int getLayoutId() {
        return R.layout.video_player_layout;
    }

    public void setThumbImg(Uri uri) {
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) thumbImageView;
        simpleDraweeView.setImageURI(uri);
    }

    public void setThumbImg(Uri uri,int width,int height) {
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) thumbImageView;
        FunUntil.loadImg(width,height,simpleDraweeView,uri);
    }

    public void setThumbImgByVideoPath() {

    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }
}
