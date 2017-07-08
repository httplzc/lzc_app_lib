package com.yioks.lzclib.Service;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yioks.lzclib.Helper.ChoicePhotoManager;
import com.yioks.lzclib.Untils.FileUntil;
import com.yioks.lzclib.Untils.FunUntil;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * 压缩图片
 * helper methods.
 */
public class PressImgService extends Service {
    private static final String ACTION_FOO = "com.yioks.lzclib.Service.action.pressImg";

    private static final String EXTRA_PARAM1 = "com.yioks.lzclib.Service.extra.pressImgUri";
    private static final String EXTRA_PARAM2 = "com.yioks.lzclib.Service.extra.pressImgOption";
    private static final String EXTRA_PARAM3 = "com.yioks.lzclib.Service.extra.pressImgCount";
    private static final String EXTRA_PARAM4 = "com.yioks.lzclib.Service.extra.pressImgPosition";
    private static final String EXTRA_PARAM5 = "com.yioks.lzclib.Service.extra.tag";
    private int count = 0;
    private volatile int realCount;
    private static final String FileName = "pressPic";
    public static final String callbackReceiver = "com.yioks.lzclib.Service.pressImg_receiver";
    //    public Vector<File> fileList = new Vector<>();
    private Hashtable<Integer, File> fileHashtable = new Hashtable<>();
    private ExecutorService executorService;
    private String tag="";


    public PressImgService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fileHashtable.clear();
        executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                9, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }


    private class PressRunnable implements Runnable {
        private Intent intent;

        public PressRunnable(Intent intent) {
            this.intent = intent;
        }

        @Override
        public void run() {
            onHandleIntent(intent);
        }
    }


    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionPress(Context context, Uri uri, ChoicePhotoManager.Option option, int realCount, int position) {
        Intent intent = new Intent(context, PressImgService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, uri);
        intent.putExtra(EXTRA_PARAM2, option);
        intent.putExtra(EXTRA_PARAM3, realCount);
        intent.putExtra(EXTRA_PARAM4, position);
        intent.putExtra(EXTRA_PARAM5, context.hashCode());
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        executorService.execute(new PressRunnable(intent));
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                tag = intent.getIntExtra(EXTRA_PARAM5,0)+"";
                final Uri uri = intent.getParcelableExtra(EXTRA_PARAM1);
                final ChoicePhotoManager.Option option = (ChoicePhotoManager.Option) intent.getSerializableExtra(EXTRA_PARAM2);
                realCount = intent.getIntExtra(EXTRA_PARAM3, 1);
                int position = intent.getIntExtra(EXTRA_PARAM4, 0);
                handleActionFoo(uri, option, position);
            }
        }
    }

    private void callFinish() {
        Intent intent = new Intent();
        intent.setAction(callbackReceiver+tag);
        Log.i("lzc", "action  " + callbackReceiver+tag);
        Uri[] uris = new Uri[fileHashtable.keySet().size()];
        for (Map.Entry<Integer, File> integerFileEntry : fileHashtable.entrySet()) {
            uris[integerFileEntry.getKey()] = Uri.fromFile(integerFileEntry.getValue());
        }
        intent.putExtra("data", uris);
        sendBroadcast(intent);
        fileHashtable.clear();
        count = 0;
        stopSelf();
    }


    public static void StopProcess(Context context) {
        int pid = FunUntil.getProcessIdByProcessName(context, context.getPackageName() + ":pressImg");
        if (pid != -1)
            Process.killProcess(pid);
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(Uri uri, ChoicePhotoManager.Option option, int position) {
        File file = new File(FileUntil.UriToFile(uri, this));
        if (!file.exists())
            return;
        File newFile = FileUntil.createTempFile(FileName + UUID.randomUUID() + ".jpg");
        FileUntil.compressImg(this, file, newFile, option.pressRadio, option.maxWidth, option.maxHeight, option.longImgRatio);
        FileUntil.setFilePictureDegree(newFile, FileUntil.readPictureDegree(file.getPath()));
        Log.i("lzc", "position" + position);
        fileHashtable.put(position, newFile);
        synchronized (PressImgService.class) {
            count++;
            if (realCount == count) {
                callFinish();
            }
        }
    }


}
