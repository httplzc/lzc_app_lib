package pers.lizechao.android_lib.net.api;

import io.reactivex.Completable;
import io.reactivex.Single;

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
 * Time: 15:34
 */
public interface CacheInterceptor {

    public <T> Single<T> getCache(ApiRequest<T> apiRequest);

    public <T> Completable saveCache(ApiRequest<T> apiRequest, T data);
}
