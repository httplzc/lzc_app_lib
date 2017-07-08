package com.yioks.lzclib.Untils;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 错误信息存入本地
 *
 * Created by Administrator on 2016/8/12 0012.
 */
public abstract class MyCrashhandle implements Thread.UncaughtExceptionHandler {

    private Context context;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    /**
     * 初始化
     */
    public void init(Context context) {
        this.context = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        Log.e("lzc", "", ex);
        handleException(thread, ex);
    }

    private boolean handleException(final Thread thread, final Throwable ex) {
        if (ex == null) {
            return false;
        }


        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        sendErrorMsg(stringWriter.toString(),"");
        return true;
    }

    /**
     * 错误信息发送至后台
     *
     * @param s
     */
    private void sendErrorMsg(String s,String app_name) {
        File dir = new File(context.getExternalFilesDir(null).getPath() + "/error_file");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            File file = new File(dir.getPath() + "/error.txt");
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.newLine();
            bufferedWriter.write("app名称" + app_name + "\n");
            bufferedWriter.write("当前时间" + StringManagerUtil.getCurrentTime() + "\n");
            bufferedWriter.write("手机型号" + DeviceUtil.getPhoneMessage() + "\n");
            bufferedWriter.write("android 版本" + Build.VERSION.SDK_INT + "\n");
            bufferedWriter.write("app版本" + DeviceUtil.getVersionName(context) + "\n");
            bufferedWriter.write("错误堆栈信息");
            bufferedWriter.newLine();
            bufferedWriter.write(s);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            restartApp(context);
        }

    }

    /**
     * 重启APP
     *
     * @param context
     */
    public abstract  void restartApp(Context context);
//        Intent intent = new Intent(context, LunchActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
//        android.os.Process.killProcess(android.os.Process.myPid());


}
