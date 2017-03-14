package com.yioks.lzclib;

import android.app.Application;

import com.yioks.lzclib.Helper.LzcLibInit;

/**
 * Created by ${User} on 2017/2/24 0024.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LzcLibInit.initApp(this);

    }
}
