package pers.lizechao.android_lib.support.protocol.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.util.ObjectsCompat;
import android.text.TextUtils;

import io.reactivex.Scheduler;
import pers.lizechao.android_lib.support.protocol.base.ThreadReceiver;

/**
 * Created by Lzc on 2018/6/27 0027.
 */
public class BroadcastStubReceiver<T> extends ThreadReceiver<T> {

    private String actionName;
    private String permissionName;
    private BroadcastReceiver receiver;

    public BroadcastStubReceiver(Context context, Class<T> interfaceClass) {
        super(interfaceClass);
        init(context, interfaceClass);
    }

    public BroadcastStubReceiver(Context context, Class<T> interfaceClass, T listener) {
        super(interfaceClass, listener);
        init(context, interfaceClass);
    }

    public BroadcastStubReceiver(Class<T> interfaceClass, T listener, @Nullable Scheduler scheduler) {
        super(interfaceClass, listener, scheduler);
    }

    private void init(Context context, Class<T> interfaceClass) {
        BroadcastMsg broadcastMsg = interfaceClass.getAnnotation(BroadcastMsg.class);
        if (broadcastMsg != null) {
            this.actionName = broadcastMsg.action();
            this.permissionName = broadcastMsg.permission();
            if(TextUtils.isEmpty(this.permissionName))
                this.permissionName=null;
        }
        initReceiver(context);
    }

    private void initReceiver(Context context) {
        if (actionName == null)
            return;
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String processId = intent.getStringExtra(BroadcastConstant.PROCESS_ID_TAG);
                String userId = intent.getStringExtra(BroadcastConstant.USER_ID_TAG);
                if (ObjectsCompat.equals(Process.myPid(), processId))
                    return;
                if (!ObjectsCompat.equals(Process.myUid(), userId))
                    return;
                callBackStubData(new StubData(intent.getBundleExtra(BroadcastConstant.DATA_TAG)));
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(actionName);
        context.registerReceiver(receiver, filter, permissionName, null);
    }


    public void unregister(Context context) {
        if (receiver != null)
            context.unregisterReceiver(receiver);
    }
}
