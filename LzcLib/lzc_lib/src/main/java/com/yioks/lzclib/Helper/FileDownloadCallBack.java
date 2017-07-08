package com.yioks.lzclib.Helper;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yioks.lzclib.Untils.StringManagerUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by ${User} on 2017/3/11 0011.
 */

public abstract class FileDownloadCallBack implements Callback {
    private File file;
    private Handler handler;
    private Context context;


    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public FileDownloadCallBack(File file, Context context) {
        this.file = file;
        this.context = context;
        handler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    onSuccess((File) msg.obj);
                } else if (msg.what == 1) {
                    onFailure((Integer) msg.obj, FileDownloadCallBack.this.file);
                } else {
                    onProgress((Integer) msg.obj);
                }
                super.handleMessage(msg);
            }
        };

    }

    @Override
    public void onFailure(Call call, IOException e) {
        Message message = handler.obtainMessage();
        message.what = 1;
        message.obj = -1;
        handler.sendMessage(message);
    }

    @Override
    public void onResponse(Call call, Response response) {
        Log.i("lzc","onResponse"+response.code());
        if (response.isSuccessful()) {
            InputStream inputStream = response.body().byteStream();
            long totalLength = 0;
            long current = 0;
            if (response.code() == 206) {

                String range = response.header("Content-Range");
                Log.i("lzc", "range" + range);
                String data[] = range.replace("bytes", "").trim().split("/");
                if (data.length < 2 || data[0].split("-").length < 2) {
                    sendFailMessage(response.code());
                    return;
                }
                String currentMsg[] = data[0].split("-");
                Log.i("lzc", "currentMsg" + data[0] + "---" + "--" + currentMsg[0] + "---" + currentMsg[1]);
                totalLength = StringManagerUtil.VerifyNumber(currentMsg[1]) ? Integer.valueOf(currentMsg[1]) : 0;
                current = StringManagerUtil.VerifyNumber(currentMsg[0]) ? Integer.valueOf(currentMsg[0]) : 0;
            } else {
                totalLength = response.body().contentLength();
            }


            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file,true);
                byte[] data = new byte[2048];
                int realRead;
                while ((realRead = inputStream.read(data, 0, 2048)) != -1) {
                    outputStream.write(data, 0, realRead);
                    current += realRead;
                    //发送进度
                    if (totalLength != -1) {
                        Message message = handler.obtainMessage();
                        message.what = 2;
                          Log.i("lzc", "current" + current + "---" + totalLength);
                        message.obj = (int) ((float) current / (float) totalLength * 100f);
                        handler.sendMessage(message);
                    }
                }
                inputStream.close();
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                if (inputStream != null)
                    try {
                        inputStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                if (outputStream != null)
                    try {
                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                sendFailMessage(response.code());
                return;
            }
            Message message = handler.obtainMessage();
            message.what = 0;
            message.obj = file;
            handler.sendMessage(message);
        } else {
            sendFailMessage(response.code());
        }

    }

    private void sendFailMessage(int code) {
        Message message = handler.obtainMessage();
        message.what = 1;
        message.obj = code;
        handler.sendMessage(message);
    }

    public abstract void onFailure(int statusCode, File file);

    public abstract void onSuccess(File file);

    public abstract void onProgress(int progress);

    public void cancelAllRequest() {
        if (handler != null)
            handler.removeCallbacksAndMessages(null);
    }

}
