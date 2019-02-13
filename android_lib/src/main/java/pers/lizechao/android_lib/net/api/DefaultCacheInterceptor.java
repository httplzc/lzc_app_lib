package pers.lizechao.android_lib.net.api;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import pers.lizechao.android_lib.storage.db.Storage;

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
 * Date: 2018/11/7 0007
 * Time: 17:03
 */
public abstract class DefaultCacheInterceptor implements CacheInterceptor {
    @Override
    public <T> Single<T> getCache(ApiRequest<T> apiRequest) {
        return new Single<T>() {
            @Override
            protected void subscribeActual(SingleObserver<? super T> observer) {
                T data = Storage.getDBInstance().load(apiRequest.getType(), getUniqueKey(apiRequest));
                if (data != null)
                    observer.onSuccess(data);
                else
                    observer.onError(new NoCacheException());
            }
        };
    }

    private class NoCacheException extends Exception {

    }

    abstract protected <T> String getUniqueKey(ApiRequest<T> apiRequest);


    @Override
    public <T> Completable saveCache(ApiRequest<T> apiRequest, T data) {
        if (data != null) {
            return Storage.getDBInstance().store(data, getUniqueKey(apiRequest));
        }
        return new Completable() {
            @Override
            protected void subscribeActual(CompletableObserver s) {
                s.onComplete();
            }
        };
    }

    public void clearAllCache(String cacheHead)
    {

    }

}
