package pers.lizechao.android_lib.net.base;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import pers.lizechao.android_lib.common.SerializerFactory;
import pers.lizechao.android_lib.net.data.HttpCodeError;
import pers.lizechao.android_lib.net.data.NetError;
import pers.lizechao.android_lib.net.data.Progress;
import pers.lizechao.android_lib.net.utils.NetUtils;
import pers.lizechao.android_lib.storage.file.FileStoreUtil;

/**
 * Created by Lzc on 2018/6/19 0019.
 */
public class CallObservable extends Single<NetResult> {
    private final Call call;

    public CallObservable(Call call) {
        this.call = call;
    }

    @Override
    protected void subscribeActual(SingleObserver<? super NetResult> observer) {
        CallCallback callback = new CallCallback(call, observer);
        observer.onSubscribe(callback);
        call.executeAsync(callback);
        Log.i("net_data", call.getRequestData().toString());
    }


    //保存流为文件并回调进度
    private void dealFileStream(File file, NetResult netResult, Observer<? super Progress> observer) {
        Progress progress;
        if (netResult.responseCode() == 206) {
            String range = netResult.getHead("Content-Range");
            progress = NetUtils.calcInitProgress(range);

        } else {
            progress = new Progress(netResult.contentLength(), 0);
        }
        try {
            long finalCurrent = progress.current;
            FileStoreUtil.saveInputStream(file, netResult.getStream(), true, aLong -> {
                progress.current = finalCurrent + aLong;
                observer.onNext(progress);
            });
            observer.onComplete();
        } catch (IOException e) {
            e.printStackTrace();
            observer.onError(e);
        }
    }

    public Observable<Progress> mapFile(File file) {
        return this.toObservable().flatMap((Function<NetResult, ObservableSource<Progress>>) netResult -> {
            if (!netResult.isSuccessful()) {
                throw new HttpCodeError(netResult.responseCode());
            }
            return new Observable<Progress>() {
                @Override
                protected void subscribeActual(Observer<? super Progress> observer) {
                    dealFileStream(file, netResult, observer);
                }
            };
        });
    }

    public Single<String> mapString() {
        return this.map(netResult -> {
            if (!netResult.isSuccessful())
                throw new HttpCodeError(netResult.responseCode());
            return netResult.getString();
        });
    }

    public Single<JSONObject> mapJson() {
        return this.mapString().map(JSONObject::new);
    }

    public <T> Single<T> mapToBean(Class<T> tClass, Type... type) {
        return this.mapString().map(s -> SerializerFactory.newInstance().createJsonSerializer().unSerial(s, tClass, type));
    }

    public Single<byte[]> mapBytes() {
        return this.map(netResult -> {
            if (!netResult.isSuccessful())
                throw new HttpCodeError(netResult.responseCode());
            return netResult.getBytes();
        });
    }


    public Observable<Progress> mapFileUi(File file) {
        return mapFile(file).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

    }

    public Single<String> mapStringToUi() {
        return mapString().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<byte[]> mapBytesToUi() {
        return mapBytes().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<JSONObject> mapJsonToUi() {
        return mapJson().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public <T> Single<T> mapBeanToUi(Class<T> tClass, Type... type) {
        return mapToBean(tClass, type).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    private static final class CallCallback implements Disposable, NetCallBack {
        private final Call call;
        private final SingleObserver<? super NetResult> observer;
        boolean terminated = false;

        CallCallback(Call call, SingleObserver<? super NetResult> observer) {
            this.call = call;
            this.observer = observer;
        }

        @Override
        public void succeed(Call call, NetResult netResult) {
            if (call.isCanceled()) return;
            try {
                observer.onSuccess(netResult);
            } catch (Throwable t) {
                try {
                    observer.onError(t);
                } catch (Throwable inner) {
                    inner.printStackTrace();
                }
            }
        }

        @Override
        public void error(@Nullable Call call, Throwable throwable) {
            if (call == null) {
                observer.onError(throwable);
                return;
            }
            if (call.isCanceled()) return;
            try {
                observer.onError(new NetError(throwable));
            } catch (Throwable inner) {
                inner.printStackTrace();
            }
        }


        @Override
        public void dispose() {
            call.cancel();
        }

        @Override
        public boolean isDisposed() {
            return call.isCanceled();
        }


    }

}
