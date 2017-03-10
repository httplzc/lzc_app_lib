package com.yioks.lzclib.View;

import android.content.ContextWrapper;
import android.util.Log;

import com.unity3d.player.UnityPlayer;

/**
 * Created by asus-pc on 2016/8/6.
 */
public class MyUnity extends UnityPlayer {
    public MyUnity(ContextWrapper contextWrapper) {
        super(contextWrapper);
    }

    @Override
    protected void kill() {
        Log.i("lzc","kill");
        super.kill();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
