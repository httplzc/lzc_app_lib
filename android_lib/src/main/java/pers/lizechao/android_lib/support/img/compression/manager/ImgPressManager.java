package pers.lizechao.android_lib.support.img.compression.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import pers.lizechao.android_lib.data.ApplicationData;
import pers.lizechao.android_lib.support.img.compression.comm.IImgPressAction;
import pers.lizechao.android_lib.support.img.compression.comm.IImgPressCallBack;
import pers.lizechao.android_lib.support.img.compression.service.PressImgService;
import pers.lizechao.android_lib.support.protocol.messenger.MessengerReceiver;
import pers.lizechao.android_lib.support.protocol.messenger.MessengerSender;

/**
 * Created by Lzc on 2017/7/24 0024.
 */

public class ImgPressManager {
    //连接类
    private final PressServiceConnection pressServiceConnection;
    //回调
    private final HashMap<String, PressCallBack> pressCallBackHashMap = new HashMap<>();
    //任务列表
    private final HashMap<String, ImgPressTask> taskHashMap = new HashMap<>();

    //由于服务器还没有连接等待中的队列
    private final Queue<ImgPressTask> preparePressTask = new LinkedList<>();

    //当前与服务器的连接状态
    private ConnectState connectState = ConnectState.None;

    //当前与service的连接状态
    private enum ConnectState {
        None, Connecting, Connected
    }

    private final static ImgPressManager IMG_PRESS_MANAGER = new ImgPressManager();

    private IImgPressAction iImgPressAction;

    public static ImgPressManager getInstance() {
        return IMG_PRESS_MANAGER;
    }

    private ImgPressManager() {
        pressServiceConnection = new PressServiceConnection();
    }

    private class ImgPressCallBack implements IImgPressCallBack {

        @Override
        public void error(String uuid, int index, String error) {
            PressCallBack pressCallBack = pressCallBackHashMap.get(uuid);
            if (pressCallBack != null) {
                pressCallBack.onError(uuid, index, error);
            }
        }

        @Override
        public void finish(String uuid, String[] paths) {
            PressCallBack pressCallBack = pressCallBackHashMap.get(uuid);
            if (pressCallBack != null) {
                pressCallBack.onFinish(uuid, paths);
            }
        }

        @Override
        public void finishAllTask() {
            callDestroy();
        }
    }


    //获取通信类
    private class PressServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iImgPressAction = new MessengerSender<>(IImgPressAction.class, service).asInterface();
            connectState = ConnectState.Connected;
            for (ImgPressTask imgPressTask : preparePressTask) {
                startPress(imgPressTask);
            }
            preparePressTask.clear();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iImgPressAction = null;
            connectState = ConnectState.None;

        }
    }

    public void addPressTask(ImgPressTask pressTask) {

    }


    //开始压缩
    public void startPress(ImgPressTask pressTask) {

        if (connectState == ConnectState.Connected) {
            taskHashMap.put(pressTask.getUuid(), pressTask);
            iImgPressAction.startPress(pressTask);
        } else {
            preparePressTask.add(pressTask);
            if (connectState == ConnectState.None)
                connectService();
        }

    }


    public void stopPress(String uuid) {
        iImgPressAction.stopPress(uuid);

    }


    //准备
    public void connectService() {
        if (connectState == ConnectState.None) {
            connectState = ConnectState.Connecting;
            Intent intent = new Intent(ApplicationData.applicationContext, PressImgService.class);
            Bundle bundle = new Bundle();
            bundle.putBinder("iBinder", new MessengerReceiver<>(IImgPressCallBack.class, new ImgPressCallBack()).getSendBinder());
            intent.putExtra("data", bundle);
            ApplicationData.applicationContext.bindService(intent, pressServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }


    //注册回调
    public void registerCallBack(String uuid, PressCallBack pressCallBack) {
        pressCallBackHashMap.put(uuid, pressCallBack);
    }

    //移除回调
    public void unregisterCallBack(String uuid) {
        pressCallBackHashMap.remove(uuid);
    }

    //关闭服务
    public void callDestroy() {
        callDestroyMsg();
        try {
            if (pressServiceConnection != null && connectState != ConnectState.None) {
                connectState = ConnectState.None;
                ApplicationData.applicationContext.unbindService(pressServiceConnection);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        iImgPressAction = null;
    }

    private void callDestroyMsg() {
        iImgPressAction.stopSelf();
    }

    public interface Callback {
        void callback();
    }
}
