package com.yioks.lzclib.Data;

import android.content.Context;

import java.io.Serializable;

/**
 * Created by ${User} on 2017/3/6 0006.
 */

public class WebViewJavascriptInterface implements Serializable {
    protected Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
