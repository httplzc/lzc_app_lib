package pers.lizechao.android_lib.support.log;

import android.content.Context;
import android.os.Build;
import android.os.HandlerThread;
import android.os.Looper;

import com.annimon.stream.function.Consumer;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import pers.lizechao.android_lib.data.ApplicationData;
import pers.lizechao.android_lib.storage.file.FileStoreUtil;
import pers.lizechao.android_lib.support.protocol.base.ThreadReceiverTarget;
import pers.lizechao.android_lib.support.protocol.handler.ThreadStub;
import pers.lizechao.android_lib.utils.DeviceUtil;
import pers.lizechao.android_lib.utils.FunUntil;

/**
 * Created by Lzc on 2018/4/2 0002.
 */

public class LogManager {
    private static final LogManager ourInstance = new LogManager();
    private HandlerThread writeThread;
    private Consumer<String> readCallback;
    private ICallHandLog callHandLog;
    private ICallBackLog callBackLog;


    public static LogManager getInstance() {
        return ourInstance;
    }

    private LogManager() {

    }

    public void stopLog() {
        if (writeThread != null)
            writeThread.quitSafely();
    }

    //读取log
    public void readLog(LogType logType, Consumer<String> consumer) {
        checkThread();
        this.readCallback = consumer;
        callHandLog.readLog(logType);
    }

    public void writeLog(LogData logData) {
        checkThread();
        callHandLog.handleLog(logData);
    }


    public void clearLog() {
        checkThread();
        LogType logTypes[] = LogType.values();
        for (LogType logType : logTypes) {
            callHandLog.clearLog(logType);
        }

    }

    public void clearLog(LogType logType) {
        checkThread();
        callHandLog.clearLog(logType);
    }


    private void checkThread() {
        if (writeThread == null || !writeThread.isAlive()) {
            synchronized (ourInstance) {
                if (writeThread == null || !writeThread.isAlive()) {
                    writeThread = new HandlerThread("write_log_thread");
                    writeThread.start();
                    initCaller(writeThread.getLooper());
                }
            }
        }
    }

    private void initCaller(Looper looper) {
        callHandLog = ThreadStub.createInterface(ICallHandLog.class, looper,
          new ICallHandLog() {
              @Override
              public void handleLog(LogData logData) {
                  try {
                      if (logData.getLogFile().length() == 0) {
                          LogData fileHead = getFileHead(ApplicationData.applicationContext, logData.getLogType());
                          FileStoreUtil.saveTextData(fileHead.getLogFile(), fileHead.log, true);
                      }
                      FileStoreUtil.saveTextData(logData.getLogFile(), logData.log, true);
                  } catch (IOException e) {
                      e.printStackTrace();
                  }
                  if (logData.getLogType() == LogType.CrashError) {
                      FunUntil.restartApp(ApplicationData.applicationContext);
                  }
              }

              @Override
              public void readLog(LogType logType) {
                  String log = null;
                  try {
                      log = FileStoreUtil.loadStr(LogUtil.getOriginLogFile(logType));
                  } catch (IOException e) {
                      e.printStackTrace();
                  }
                  callBackLog.callBackLog(log, logType);
              }

              @Override
              public void clearLog(LogType logType) {
                  File file = LogUtil.getOriginLogFile(logType);
                  file.delete();
                  LogUtil.getOriginLogFile(logType);
              }
          });
        callBackLog = ThreadStub.createInterface(ICallBackLog.class, (log, logType) -> readCallback.accept(log));
    }

    private LogData getFileHead(Context context, LogType logType) {
        return new LogData.Builder(logType)
          .addData("日志类型：" + logType)
          .addData("日志创建时间：" + LogUtil.formatData(new Date()))
          .addData("uuid：" + DeviceUtil.getDeviceUUID(context))
          .addData("app名称：" + DeviceUtil.getAppName(context))
          .addData("android 版本：" + Build.VERSION.SDK_INT)
          .addData("app版本：" + DeviceUtil.getVersionName(context))
          .build();
    }


    private interface ICallHandLog {
        void handleLog(LogData logData);

        void readLog(LogType logType);

        void clearLog(LogType logType);
    }

    @ThreadReceiverTarget(ThreadReceiverTarget.ThreadTarget.AndroidMain)
    private interface ICallBackLog {
        void callBackLog(String log, LogType logType);
    }
}
