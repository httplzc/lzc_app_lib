package com.yioks.lzclib.Helper;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by ${User} on 2017/3/11 0011.
 */

public abstract class HandlerCallBack implements Callback {
    private Context context;
    private Handler handler;

    public HandlerCallBack(Context context) {
        this.context = context;
        handler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    onSuccess((String) msg.obj);
                } else {
                    onFailure((Integer) msg.obj);
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void onFailure(Call call, IOException e) {
        if(call.isCanceled())
            return;
        Message message = handler.obtainMessage();
        message.what = 1;
        message.obj = -1;
        handler.sendMessage(message);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException, FileNotFoundException {
        Message message = handler.obtainMessage();
        if (response.isSuccessful()) {
            message.what = 0;
            message.obj = response.body().string();
        } else {
            message.what = 1;
            message.obj = response.code();
        }
        handler.sendMessage(message);
    }

    public abstract void onFailure(int statusCode);

    public abstract void onSuccess(String string);


    public void cancelAllRequest() {
        if (handler != null)
            handler.removeCallbacksAndMessages(null);
    }


}
