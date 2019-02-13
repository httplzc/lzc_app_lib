package pers.lizechao.android_lib.support.img.compression.service;

import pers.lizechao.android_lib.support.img.compression.manager.ImgPressTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lzc on 2018/6/11 0011.
 * 记录任务执行状态
 */
public class ServiceImgPressTaskRecord {
    private String uuid;
    private final List<ServiceImgPressTask> preparePressTasks = new ArrayList<>();
    private final List<ServiceImgPressTask> finishPressTasks = new ArrayList<>();

    ServiceImgPressTaskRecord(ImgPressTask imgPressTask) {
        if (imgPressTask.getUris() == null)
            return;
        uuid = imgPressTask.getUuid();
        for (int i = 0; i < imgPressTask.getUris().length; i++) {
            ServiceImgPressTask task = ServiceImgPressTask.createTask(imgPressTask.getUris()[i], imgPressTask.getConfig(),
              imgPressTask.getUuid(), i);
            if (task != null) {
                preparePressTasks.add(task);
            }
        }
    }

    public String[] getPaths() {
        String pathsArray[] = new String[finishPressTasks.size()];
        for (ServiceImgPressTask finishPressTask : finishPressTasks) {
            if (finishPressTask.currentStatus == ServiceImgPressTask.PressState.Finish)
                pathsArray[finishPressTask.index] = finishPressTask.newFile.getPath();
            else
                pathsArray[finishPressTask.index] = null;
        }
        return pathsArray;
    }

    public void finish(ServiceImgPressTask serviceImgPressTask) {
        if (!uuid.equals(serviceImgPressTask.uuid))
            return;
        serviceImgPressTask.currentStatus = ServiceImgPressTask.PressState.Finish;
        finishPressTasks.add(serviceImgPressTask);
    }

    public void error(ServiceImgPressTask serviceImgPressTask) {
        if (!uuid.equals(serviceImgPressTask.uuid))
            return;
        serviceImgPressTask.currentStatus = ServiceImgPressTask.PressState.Error;
        finishPressTasks.add(serviceImgPressTask);
    }

    public boolean haveFinish() {
        return finishPressTasks.size() == preparePressTasks.size();
    }

    public List<ServiceImgPressTask> getPreparePressTasks() {
        return preparePressTasks;
    }

    public String getUuid() {
        return uuid;
    }
}
