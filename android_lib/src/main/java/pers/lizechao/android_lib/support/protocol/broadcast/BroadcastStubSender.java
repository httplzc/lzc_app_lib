package pers.lizechao.android_lib.support.protocol.broadcast;

import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.text.TextUtils;

import pers.lizechao.android_lib.support.protocol.base.StubSender;

/**
 * Created by Lzc on 2018/6/27 0027.
 */
public class BroadcastStubSender<T> extends StubSender<T> {
    private final Context context;
    private String actionName;
    private String permissionName;
    private boolean expert = false;

    public BroadcastStubSender(Context context, Class<T> interfaceClass) {
        super(interfaceClass);
        this.context = context;
        BroadcastMsg broadcastMsg = interfaceClass.getAnnotation(BroadcastMsg.class);
        if (broadcastMsg != null) {
            this.actionName = broadcastMsg.action();
            this.permissionName = broadcastMsg.permission();
            this.expert = broadcastMsg.expert();
            if (TextUtils.isEmpty(this.permissionName))
                this.permissionName = null;
        }
    }

    @Override
    protected void sendStubData(StubData stubData) {
        Intent intent = new Intent(actionName)
                .putExtra(BroadcastConstant.PROCESS_ID_TAG, Process.myPid())
                .putExtra(BroadcastConstant.USER_ID_TAG, Process.myUid())
                .putExtra(BroadcastConstant.DATA_TAG, stubData.getBundle());
        if (!expert)
            intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent, permissionName);
    }
}
