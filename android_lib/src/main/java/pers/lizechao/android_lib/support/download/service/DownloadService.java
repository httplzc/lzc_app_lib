package pers.lizechao.android_lib.support.download.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import pers.lizechao.android_lib.net.base.NetClient;
import pers.lizechao.android_lib.net.base.NetResult;
import pers.lizechao.android_lib.net.data.HttpCodeError;
import pers.lizechao.android_lib.net.data.Progress;
import pers.lizechao.android_lib.net.okhttp.OkHttpInstance;
import pers.lizechao.android_lib.net.utils.NetUtils;
import pers.lizechao.android_lib.net.utils.NetWatchdog;
import pers.lizechao.android_lib.storage.file.FileStoreUtil;
import pers.lizechao.android_lib.support.download.comm.DownLoadMsg;
import pers.lizechao.android_lib.support.download.comm.IDownloadAction;
import pers.lizechao.android_lib.support.download.comm.IDownloadCallback;
import pers.lizechao.android_lib.support.download.comm.TaskMsg;
import pers.lizechao.android_lib.support.download.comm.WifiStateChangeException;
import pers.lizechao.android_lib.support.protocol.messenger.MessengerReceiver;
import pers.lizechao.android_lib.support.protocol.messenger.MessengerSender;


//下载文件服务
public class DownloadService extends Service implements IDownloadAction {
    //下载队列
    private final HashMap<String, DownLoadServiceMsg> downLoadMsgHashMap = new HashMap<>();
    private static final String TAG = "Download";
    private IDownloadCallback downloadCallback;
    private NetClient netClient = null;


    //开始下载
    @Override
    public void startDownload(String uuid, DownLoadMsg originMsg) {
        Log.i(TAG, "startDownload " + uuid);
        if (originMsg == null)
            return;
        //检查是否为wifi
        if ((originMsg.downLoadConfig.onlyWifi && NetWatchdog.isMobileConnect(this))) {
            downloadCallback.callBackStop(uuid, new WifiStateChangeException());
            return;
        }
        //文件是否存在
        File file = new File(originMsg.targetFilePath);
        if (!file.exists()) {
            downloadCallback.callBackStop(uuid, new FileNotFoundException());
            return;
        }
        //创建下载任务
        DownLoadServiceMsg downLoadMsg = new DownLoadServiceMsg(originMsg);
        downLoadMsgHashMap.put(downLoadMsg.uuid, downLoadMsg);
        netClient.newDownloadCall(downLoadMsg.url, new File(downLoadMsg.targetFilePath))
          .execute()
          .toObservable()
          .flatMap((Function<NetResult, ObservableSource<Progress>>) netResult -> {
              if (!netResult.isSuccessful()) {
                  if (netResult.responseCode() == 416) {
                      file.delete();
                  }
                  throw new HttpCodeError(netResult.responseCode());
              }
              Progress progress;
              if (netResult.responseCode() == 206) {
                  String range = netResult.getHead("Content-Range");
                  progress = NetUtils.calcInitProgress(range);

              } else {
                  progress = new Progress(netResult.contentLength(), 0);
              }
              //回调下载开始
              TaskMsg taskMsg = new TaskMsg(downLoadMsg.uuid);
              taskMsg.totalLength = (int) progress.total;
              taskMsg.contentType = netResult.getHead("Content-Type");
              downloadCallback.callBackStart(taskMsg);
              long finalCurrent = progress.current;
              return new Observable<Progress>() {
                  @Override
                  protected void subscribeActual(Observer<? super Progress> observer) {
                      try {
                          FileStoreUtil.saveInputStream(file, netResult.getStream(), true, aLong -> {
                              progress.current = finalCurrent + aLong;
                              observer.onNext(progress);
                          });
                      } catch (IOException e) {
                          e.printStackTrace();
                          observer.onError(e);
                      }
                      observer.onComplete();
                  }
              };
          })
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Observer<Progress>() {
              int lastProgress = -1;

              @Override
              public void onSubscribe(Disposable d) {
                  downLoadMsg.disposable = d;

              }

              @Override
              public void onNext(Progress progress) {
                  int progressInt = (int) (((float) progress.current / (float) progress.total) * 100f);
                  if (lastProgress != progressInt) {
                      lastProgress = progressInt;
                      downloadCallback.callBackProgress(downLoadMsg.uuid, lastProgress);
                  }
              }

              @Override
              public void onError(Throwable e) {
                  Log.i(TAG, "任务失败 " + downLoadMsg.uuid);
                  downloadCallback.callBackStop(downLoadMsg.uuid, e);
                  downLoadMsgHashMap.remove(downLoadMsg.uuid);
                  checkStopSelf();
              }

              @Override
              public void onComplete() {
                  Log.i(TAG, "结束任务 " + file.getPath());
                  downloadCallback.callBackEnd(downLoadMsg.uuid, file.getPath());
                  downLoadMsgHashMap.remove(downLoadMsg.uuid);
                  checkStopSelf();
              }
          });
        Log.i(TAG, "开始任务 " + downLoadMsg.url);
    }

    @Override
    public void pauseDownload(String uuid) {
        DownLoadServiceMsg downLoadMsg = downLoadMsgHashMap.get(uuid);
        if (downLoadMsg == null)
            return;
        downloadCallback.callBackStop(uuid, null);
        downLoadMsg.disposable.dispose();
        downLoadMsgHashMap.remove(uuid);
        checkStopSelf();
    }

    @Override
    public void deleteDownLoad(String uuid) {
        DownLoadServiceMsg downLoadMsg = downLoadMsgHashMap.get(uuid);
        if (downLoadMsg == null)
            return;
        downLoadMsg.disposable.dispose();
        File file = new File(downLoadMsg.targetFilePath);
        file.delete();
        downLoadMsgHashMap.remove(uuid);
        checkStopSelf();
    }


    private void checkStopSelf() {
        if (downLoadMsgHashMap.size() == 0) {
            downloadCallback.onFinishAllTask();
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        netClient = new NetClient.Builder().okHttpClient(OkHttpInstance.getClient()).build();
        initNetWatch();

    }

    private final NetWatchdog.NetChangeListener listener = new NetWatchdog.NetChangeListener() {
        private void onChange() {
            if (NetWatchdog.isMobileConnect(DownloadService.this)) {
                for (Map.Entry<String, DownLoadServiceMsg> downLoadMsgEntry : downLoadMsgHashMap.entrySet()) {
                    DownLoadServiceMsg downLoadMsg = downLoadMsgEntry.getValue();
                    if (downLoadMsg.isOnlyWifi)
                        pauseDownload(downLoadMsg.uuid);
                }
            }
        }

        @Override
        public void onWifiToMobile() {
            onChange();
        }

        @Override
        public void onMobileToWifi() {

        }

        @Override
        public void onNetDisconnected() {
            onChange();
        }

        @Override
        public void onNetConnect(NetWatchdog.ConnectState connectState) {

        }
    };

    private void initNetWatch() {
        NetWatchdog.getInstance().registerNetChangeListener(listener);
    }


    @Override
    public IBinder onBind(Intent intent) {
        downloadCallback = new MessengerSender<>(IDownloadCallback.class, intent.getBundleExtra("data").getBinder("iBinder")).asInterface();
        return new MessengerReceiver<>(IDownloadAction.class, this).getSendBinder();
    }


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        return Service.START_REDELIVER_INTENT;
    }


    //避免被销毁
    @Override
    public void onDestroy() {
        stopForeground(true);
        NetWatchdog.getInstance().unRegisterNetChangeListener(listener);
        super.onDestroy();
    }


}
