package com.yioks.lzclib.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

/**
 * 需要刷新的页面
 * Created by ${} on 2016/9/14 0014.
 */
public abstract class ReceiverTitleBaseActivity extends TitleBaseActivity {
    protected BroadcastReceiver broadcastReceiver;
    private  String RECEIVER_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBroadCaseReceiver();
    }

    private void initBroadCaseReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                reFreshBackGround();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        RECEIVER_NAME = this.getClass().getName()+"refresh_action";
        intentFilter.addAction(RECEIVER_NAME);
        registerReceiver(broadcastReceiver, intentFilter);
    }
    public static void CallReFresh(Context context,Class class_name)
    {
        Intent intent=new Intent();
        intent.setAction(class_name.getName()+"refresh_action");
        context.sendBroadcast(intent);
    }

    protected abstract void reFreshBackGround();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }
}
