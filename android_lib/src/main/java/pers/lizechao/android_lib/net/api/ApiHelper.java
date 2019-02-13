package pers.lizechao.android_lib.net.api;

import android.support.annotation.Nullable;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pers.lizechao.android_lib.net.base.NetClient;
import pers.lizechao.android_lib.net.base.RequestData;

/**
 * Created with
 * ********************************************************************************
 * #         ___                     ________                ________             *
 * #       |\  \                   |\_____  \              |\   ____\             *
 * #       \ \  \                   \|___/  /|             \ \  \___|             *
 * #        \ \  \                      /  / /              \ \  \                *
 * #         \ \  \____                /  /_/__              \ \  \____           *
 * #          \ \_______\             |\________\             \ \_______\         *
 * #           \|_______|              \|_______|              \|_______|         *
 * #                                                                              *
 * ********************************************************************************
 * Date: 2018-08-20
 * Time: 17:16
 * 用于创建网络请求
 */
class ApiHelper {
    private final NetClient netClient;
    private final ApiDataConversion apiDataConversion;
    @Nullable
    private final CacheInterceptor cacheInterceptor;

    public ApiHelper(NetClient netClient, ApiDataConversion apiDataConversion) {
        this.netClient = netClient;
        this.apiDataConversion = apiDataConversion;
        cacheInterceptor = null;
    }

    public ApiHelper(NetClient netClient, ApiDataConversion apiDataConversion, @Nullable CacheInterceptor cacheInterceptor) {
        this.netClient = netClient;
        this.apiDataConversion = apiDataConversion;
        this.cacheInterceptor = cacheInterceptor;
    }

    private Single<String> requestJsonStr(ApiRequest apiRequest) {
        return getNetClient().newCall(new RequestData.Builder()
                .method(apiRequest.getHttpMethod())
                .params(apiRequest.getBaseParams())
                .urlPath(apiRequest.getPath())
                .build())
                .execute()
                .mapString();
    }

    public Completable isSucceed(ApiRequest apiRequest) {
        return requestJsonStr(apiRequest).
                flatMapCompletable(s -> cs -> {
                    try {
                        if (getApiDataConversion().isSucceed(s)) {
                            cs.onComplete();
                        } else
                            cs.onError(null);
                    } catch (Exception e) {
                        cs.onError(e);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private class CacheAbleNetSingle<T> extends Single<T> {
        CompositeDisposable disposable = new CompositeDisposable();
        boolean haveProcessed = false;
        int errorCount = 0;
        private Single<T> netSingle;
        private Single<T> cacheSingle;
        private ApiRequest<T> apiRequest;
        private Throwable netThrowable;

        public CacheAbleNetSingle(Single<T> netSingle, Single<T> cacheSingle, ApiRequest<T> apiRequest) {
            this.netSingle = netSingle;
            this.cacheSingle = cacheSingle;
            this.apiRequest = apiRequest;
        }

        @Override
        protected void subscribeActual(SingleObserver<? super T> observer) {
            observer.onSubscribe(new Disposable() {
                @Override
                public void dispose() {
                    if (disposable != null)
                        disposable.dispose();
                }

                @Override
                public boolean isDisposed() {
                    if (disposable != null)
                        return disposable.isDisposed();
                    return false;
                }
            });
            Single<T> netRequest = netSingle;
            Single<T> cacheRequest = cacheSingle;
            cacheRequest.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<T>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable.add(d);
                        }

                        @Override
                        public void onSuccess(T t) {
                            if (haveProcessed)
                                return;
                            haveProcessed = true;
                            observer.onSuccess(t);
                        }

                        @Override
                        public void onError(Throwable e) {
                            errorCount++;
                            if (errorCount == 2) {
                                observer.onError(netThrowable);
                            }
                        }
                    });
            netRequest.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<T>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable.add(d);
                        }

                        @Override
                        public void onSuccess(T t) {
                            if (cacheInterceptor != null) {
                                cacheInterceptor.saveCache(apiRequest, t)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new CompletableObserver() {
                                            @Override
                                            public void onSubscribe(Disposable d) {

                                            }

                                            @Override
                                            public void onComplete() {
                                                if (haveProcessed)
                                                    return;
                                                haveProcessed = true;
                                                observer.onSuccess(t);
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                observer.onError(e);
                                            }
                                        });
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            errorCount++;
                            netThrowable = e;
                            if (errorCount == 2) {
                                observer.onError(e);
                            }
                        }
                    });
        }
    }

    public <T> Single<T> getBean(ApiRequest<T> apiRequest) {
        if (cacheInterceptor != null && apiRequest.isCacheEnable() && apiRequest.isUseCache()) {
            return new CacheAbleNetSingle<>(getNetBeanSingle(apiRequest), cacheInterceptor.getCache(apiRequest), apiRequest);
        } else {
            return getNetBeanSingle(apiRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    }

    private <T> Single<T> getNetBeanSingle(ApiRequest<T> apiRequest) {
        return requestJsonStr(apiRequest)
                .map(s -> (T) getApiDataConversion().getBean(s, apiRequest.getType()));
    }


    private ApiDataConversion getApiDataConversion() {
        return apiDataConversion;
    }

    private NetClient getNetClient() {
        return netClient;
    }
}
