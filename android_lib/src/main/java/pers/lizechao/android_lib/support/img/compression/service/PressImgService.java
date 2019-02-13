package pers.lizechao.android_lib.support.img.compression.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pers.lizechao.android_lib.storage.file.FileStoreUtil;
import pers.lizechao.android_lib.support.img.compression.comm.IImgPressAction;
import pers.lizechao.android_lib.support.img.compression.comm.IImgPressCallBack;
import pers.lizechao.android_lib.support.img.compression.manager.ImgPressTask;
import pers.lizechao.android_lib.support.img.utils.ImageUtils;
import pers.lizechao.android_lib.support.protocol.base.ThreadReceiverTarget;
import pers.lizechao.android_lib.support.protocol.handler.ThreadStub;
import pers.lizechao.android_lib.support.protocol.messenger.MessengerReceiver;
import pers.lizechao.android_lib.support.protocol.messenger.MessengerSender;
import pers.lizechao.android_lib.utils.FunUntil;

/**
 * <p>
 * 压缩图片
 * helper methods.
 */
public class PressImgService extends Service {
    //准备压缩连表
    private final List<ServiceImgPressTask> prepareTaskList = new ArrayList<>();
    //实际任务队列
    private final Map<String, ServiceImgPressTaskRecord> pressGroups = new HashMap<>();
    //线程池
    private ThreadPoolExecutor executorService;
    //当前可用内存
    private long availableMemory = 0;
    //最大可用内存
    private long maxMemory = 0;
    //压缩线程回调
    private IPressThreadCallBack pressThreadCallBack;
    //回调给另一个进程Manager
    private IImgPressCallBack iImgPressCallBack;


    public PressImgService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        prepareTaskList.clear();
        pressGroups.clear();
        initThreadPool();
        initPressCallback();
        FunUntil.MemoryMessage memoryMessage = FunUntil.getMemoryMsg(this);
        availableMemory = (memoryMessage.maxMemory - memoryMessage.totalMemory + memoryMessage.freeMemory) * 2 / 3;
        maxMemory = availableMemory;
    }

    private void initPressCallback() {
        pressThreadCallBack = ThreadStub.createInterface(IPressThreadCallBack.class, new IPressThreadCallBack() {
            @Override
            public void onSucceed(ServiceImgPressTask pressMsg) {
                Log.i("lzc", "压缩任务成功！" + pressMsg.newFile.getPath());
                callTaskFinish(pressMsg);
                availableMemory += pressMsg.userMemory;
                dispatchTask();
            }

            @Override
            public void onFail(ServiceImgPressTask pressMsg) {
                Log.i("lzc", "压缩任务失败！" + pressMsg.newFile.getPath());
                callTaskError(pressMsg, "圧缩过程失败！");
                availableMemory += pressMsg.userMemory;
                dispatchTask();
            }
        });
    }


    @ThreadReceiverTarget(ThreadReceiverTarget.ThreadTarget.AndroidMain)
    private interface IPressThreadCallBack {
        void onSucceed(ServiceImgPressTask pressMsg);

        void onFail(ServiceImgPressTask pressMsg);
    }

    /**
     * 初始化线程池参数
     */
    private void initThreadPool() {
        executorService = new ThreadPoolExecutor(4, Integer.MAX_VALUE,
          5, TimeUnit.SECONDS,
          new LinkedBlockingQueue<>());
    }


    //接收另一进程manager控制
    private class ImgPressAction implements IImgPressAction {

        @Override
        public void startPress(ImgPressTask imgPressTask) {
            if (imgPressTask == null || imgPressTask.getUris() == null || imgPressTask.getUris().length == 0)
                return;
            if (executorService.isShutdown())
                initThreadPool();
            ServiceImgPressTaskRecord imgPressTaskGroup = new ServiceImgPressTaskRecord(imgPressTask);
            pressGroups.put(imgPressTaskGroup.getUuid(), imgPressTaskGroup);
            prepareTaskList.addAll(imgPressTaskGroup.getPreparePressTasks());
            dispatchTask();
        }

        @Override
        public void stopPress(String uuid) {
            prepareTaskList.removeAll(pressGroups.get(uuid).getPreparePressTasks());
            pressGroups.remove(uuid);
            if (pressGroups.size() == 0)
                iImgPressCallBack.finishAllTask();
        }

        @Override
        public void stopSelf() {
            PressImgService.this.stopSelf();
        }
    }


    //压缩线程
    private class PressRunnable implements Runnable {
        private final ServiceImgPressTask pressMsg;

        PressRunnable(ServiceImgPressTask pressMsg) {
            this.pressMsg = pressMsg;
        }

        @Override
        public void run() {
            try {
                Bitmap bitmap = ImageUtils.loadBitmapAndCompress(pressMsg.originFile, pressMsg.option.maxWidth, pressMsg.option.maxHeight);
                int maxSize = (int) (pressMsg.option.pressRadio * bitmap.getWidth() * bitmap.getHeight());
                byte data[] = ImageUtils.compressImgSize(bitmap, maxSize, PressImgUntil.ignoreSize);
                FileStoreUtil.saveByteData(pressMsg.newFile, data, false);
                ImageUtils.setFilePictureDegree(pressMsg.newFile, ImageUtils.readPictureDegree(pressMsg.originFile.getPath()));
                pressThreadCallBack.onSucceed(pressMsg);
            } catch (IOException e) {
                e.printStackTrace();
                pressThreadCallBack.onFail(pressMsg);
            }
        }
    }

    private void callTaskError(ServiceImgPressTask task, String error) {
        iImgPressCallBack.error(task.uuid, task.index, error);
        ServiceImgPressTaskRecord group = pressGroups.get(task.uuid);
        group.error(task);
        checkFinish(group);

    }

    private void callTaskFinish(ServiceImgPressTask task) {
        ServiceImgPressTaskRecord group = pressGroups.get(task.uuid);
        group.finish(task);
        checkFinish(group);
    }

    //检查某个组是否完成
    private void checkFinish(ServiceImgPressTaskRecord group) {
        if (group.haveFinish()) {
            iImgPressCallBack.finish(group.getUuid(), group.getPaths());
            pressGroups.remove(group.getUuid());
            if (pressGroups.size() == 0) {
                iImgPressCallBack.finishAllTask();
            }
        }
    }

    //分发任务
    private void dispatchTask() {
        if (prepareTaskList.size() == 0)
            return;
        //排序
        Collections.sort(prepareTaskList, (o1, o2) -> Double.compare(o1.userMemory, o2.userMemory));
        //按顺序取，直到最大内存
        int current = 0;
        while (current <= prepareTaskList.size() - 1) {
            if (prepareTaskList.get(current).userMemory > availableMemory)
                return;
            else {
                ServiceImgPressTask addTask = prepareTaskList.remove(current);
                try {
                    addTask.currentStatus = ServiceImgPressTask.PressState.Pressing;
                    Log.i("lzc", "开始执行压缩任务！" + addTask.newFile.getPath());
                    executorService.execute(new PressRunnable(addTask));

                } catch (Exception e) {
                    e.printStackTrace();
                    callTaskError(addTask, "线程启动异常！");
                    return;
                }
                availableMemory -= addTask.userMemory;
            }
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        iImgPressCallBack = new MessengerSender<>(IImgPressCallBack.class, intent.getBundleExtra("data").getBinder("iBinder")).asInterface();
        return new MessengerReceiver<>(IImgPressAction.class, new ImgPressAction()).getSendBinder();
    }

}
