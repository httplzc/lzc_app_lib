package com.yioks.lzclib.Helper;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
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
    public void onResponse(Call call, Response response) throws IOException, FileNotFoundException {
        if (response.isSuccessful()) {
            InputStream inputStream = response.body().byteStream();
            long totalLength = response.body().contentLength();
            long current = 0;
            FileOutputStream outputStream = new FileOutputStream(file);
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
            outputStream.close();
            Message message = handler.obtainMessage();
            message.what = 0;
            message.obj = file;
            handler.sendMessage(message);
        } else {
            Message message = handler.obtainMessage();
            message.what = 1;
            message.obj = response.code();
            handler.sendMessage(message);
        }

    }

    public abstract void onFailure(int statusCode, File file);

    public abstract void onSuccess(File file);

    public abstract void onProgress(int progress);

    public void cancelAllRequest() {
        if (handler != null)
            handler.removeCallbacksAndMessages(null);
    }

}
