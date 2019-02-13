package pers.lizechao.android_lib.support.log;

import android.content.Context;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Hashtable;

import pers.lizechao.android_lib.common.CrashHandle;
import pers.lizechao.android_lib.data.ApplicationData;
import pers.lizechao.android_lib.net.utils.NetWatchdog;
import pers.lizechao.android_lib.storage.db.Storage;
import pers.lizechao.android_lib.utils.DeviceUtil;

/**
 * Created by Lzc on 2018/6/27 0027.
 */
public class LogRecorder {
    private volatile static LogRecorder logRecorder = null;

    private final Hashtable<String, LogData.Builder> operationHistory = new Hashtable<>();

    public static LogRecorder getInstance() {
        return logRecorder;
    }

    public static void init(CrashHandle crashHandle) {
        if (logRecorder == null) {
            synchronized (LogRecorder.class) {
                if (logRecorder == null)
                    logRecorder = new LogRecorder(crashHandle);

            }
        }
    }

    private LogRecorder(CrashHandle crashHandle) {
        crashHandle.registerCrashObserver((t, ex) -> {
            StringWriter stringWriter = new StringWriter();
            ex.printStackTrace(new PrintWriter(stringWriter));
            writeCrashData(stringWriter.toString());
        });
        if (!Storage.getDBInstance().load(Boolean.class, "haveWriteUserMsgLog", false)) {
            writeUserMsg();
            Storage.getDBInstance().store(true, "haveWriteUserMsgLog");
        }
    }

    public void addOperationHistory(Object object, String actionName) {
        operationHistory.put(object.toString(), getCommentBuilder(LogType.Operation)
                .addData("时间：" + LogUtil.formatData(new Date()) + "   名称：" + object.getClass().getName() + "   " + actionName));
    }

    public void endOperationHistory(Object object) {
        LogData.Builder builder = operationHistory.get(object.toString());
        if (builder != null) {
            LogManager.getInstance().writeLog(builder.build());
        }
        operationHistory.remove(object.toString());
    }


    public void writeNetError(String typeStr, String params, String requestUrl, String backData, Exception e) {
        String exceptStr = null;
        if (e != null)
            exceptStr = handleException(e);
        LogData logData = getCommentBuilder(LogType.NetError)
                .addData("错误类型：" + typeStr)
                .addData("请求网址：" + requestUrl)
                .addData("请求参数：" + params)
                .addData("接口返回值：" + backData)
                .addData("堆栈信息：" + exceptStr)
                .build();
        LogManager.getInstance().writeLog(logData);
    }


    public void writeNormalException(Exception e) {
        LogManager.getInstance().writeLog(getCommentBuilder(LogType.NormalException).addData("堆栈信息：" + handleException(e)).build());
    }

    public void writeNormalLog(String log) {
        String stringBuilder = LogUtil.BR +
                "日志信息：" + log;
        LogManager.getInstance().writeLog(getCommentBuilder(LogType.Normal).addData("日志信息：" + log).build());
    }

    private void writeCrashData(String msg) {
        LogData crashLog = getCommentBuilder(LogType.CrashError)
                .addData("错误堆栈: " + msg)
                .build();
        LogManager.getInstance().writeLog(crashLog);
    }


    private void writeUserMsg() {
        String str = DeviceUtil.getPhoneMessage(ApplicationData.applicationContext);
        String[] temp = str.split("\n");
        LogData.Builder builder = getCommentBuilder(LogType.UserMsg);
        for (String s : temp) {
            builder.addData(s);
        }
        LogManager.getInstance().writeLog(builder.build());
    }

    private String handleException(Throwable ex) {
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    private LogData.Builder getCommentBuilder(LogType logType) {
        Context context = ApplicationData.applicationContext;
        String connectType = "none";
        if (NetWatchdog.isMobileConnect(context)) {
            connectType = "Mobile";
        } else if (NetWatchdog.isWifiConnect(context)) {
            connectType = "Wifi";
        }
        return new LogData.Builder(logType)
                .addData("当前时间：" + LogUtil.formatData(new Date()))
                .addData("当前网络状态：" + connectType);
    }


}
