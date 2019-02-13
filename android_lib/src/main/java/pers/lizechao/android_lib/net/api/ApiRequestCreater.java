package pers.lizechao.android_lib.net.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.Optional;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Objects;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import pers.lizechao.android_lib.common.SerializerFactory;
import pers.lizechao.android_lib.net.base.HttpMethod;
import pers.lizechao.android_lib.net.base.NetClient;
import pers.lizechao.android_lib.net.base.RequestParams;
import pers.lizechao.android_lib.net.params.BaseParams;
import pers.lizechao.android_lib.net.params.FormParams;
import pers.lizechao.android_lib.net.params.MultipartFormParams;
import pers.lizechao.android_lib.net.params.MultipleData;
import pers.lizechao.android_lib.net.params.Params;
import pers.lizechao.android_lib.net.params.ParamsType;
import pers.lizechao.android_lib.net.params.RawParams;
import pers.lizechao.android_lib.utils.JavaUtils;

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
 * Date: 2018-08-12
 * Time: 17:16
 * 用于生成请求本事  对应接口返回值支持
 * ApiRequest<T>
 * Completable
 * Single<T>
 */
public class ApiRequestCreater {
    @Nullable
    private final ComParamsBuilderFactory paramsBuilderFactory;
    @NonNull
    private final ApiHelper apiHelper;

    private ApiRequestCreater(@Nullable ComParamsBuilderFactory paramsBuilderFactory, @NonNull ApiHelper apiHelper) {
        this.paramsBuilderFactory = paramsBuilderFactory;
        this.apiHelper = apiHelper;
    }

    public <T> T create(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, this::createNetData);
    }

    private boolean isMultipleParams(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof MultipleData[] || arg instanceof File || arg instanceof File[]) {
                return true;
            }
        }
        return false;
    }

    private Object createNetData(Object proxy, Method method, Object[] args) {
        RequestParams requestParams = method.getAnnotation(RequestParams.class);
        if (requestParams == null)
            return null;
        if (args == null)
            args = new Object[0];
        Annotation annotations[][] = method.getParameterAnnotations();
        if (annotations.length != args.length)
            throw new IllegalArgumentException("有参数注解为空");
        Params params[] = new Params[args.length];
        for (int i = 0; i < params.length; i++) {
            for (Annotation annotation : annotations[i]) {
                if (annotation.annotationType() == Params.class) {
                    params[i] = (Params) annotation;
                    break;
                }
            }
        }
        Type returnType = method.getGenericReturnType();
        return createReturnData(requestParams, returnType, params, args);
    }

    private Object createReturnData(RequestParams requestParams, Type returnType, Params params[], Object[] args) {
        String path = requestParams.path();
        HttpMethod httpMethod = requestParams.method();
        BaseParams baseParams = createBaseParams(args, params, requestParams.paramsType());
        boolean cacheEnable = requestParams.cacheEnable();
        //返回值为Single
        if (Single.class.isAssignableFrom(JavaUtils.getRawType(returnType))) {
            ParameterizedType parameterizedType = (ParameterizedType) returnType;
            Type realType = parameterizedType.getActualTypeArguments()[0];
            return createFinalSingle(new ApiRequest<>(realType, path, baseParams, httpMethod, cacheEnable));
        }
        //返回值为Completable
        else if (Completable.class.isAssignableFrom(JavaUtils.getRawType(returnType))) {
            return createFinalCompletable(new ApiRequest(Object.class, path, baseParams, httpMethod, cacheEnable));
        } else if (ApiRequest.class.isAssignableFrom(JavaUtils.getRawType(returnType))) {
            ParameterizedType parameterizedType = (ParameterizedType) returnType;
            Type realType = parameterizedType.getActualTypeArguments()[0];
            return new ApiRequest(realType, path, baseParams, httpMethod, cacheEnable);
        } else
            throw new IllegalArgumentException("必须返回值为Single 或Completable");
    }

    private BaseParams createBaseParams(Object[] args, Params params[], ParamsType paramsType) {
        BaseParams.Builder builder = createParamsByType(paramsType);
        FormParams comment = Optional.ofNullable(paramsBuilderFactory)
                .map(ComParamsBuilderFactory::createBuilder)
                .orElse(new FormParams.Builder().build());
        if (paramsType != ParamsType.Raw) {
            builder.putParams(comment);
        }
        builder.putBeanByData(args, params, new String[args.length]);
        return builder.build();
    }


    private BaseParams.Builder createParamsByType(ParamsType paramsType) {
        BaseParams.Builder builder = null;
        switch (paramsType) {
            case Form:
                builder = new FormParams.Builder();
                break;
            case MultipartForm:
                builder = new MultipartFormParams.MultipartFormBuilder();
                break;
            case Raw:
                builder = new RawParams.RawBuilder();
                break;
        }
        Objects.requireNonNull(builder);
        return builder;
    }


    public static class Builder {
        @Nullable
        private ComParamsBuilderFactory paramsBuilderFactory;

        private NetClient netClient;

        private ApiDataConversion apiDataConversion;

        private CacheInterceptor cacheInterceptor = null;

        public Builder setParamsBuilderFactory(@Nullable ComParamsBuilderFactory paramsBuilderFactory) {
            this.paramsBuilderFactory = paramsBuilderFactory;
            return this;
        }

        public Builder setNetClient(NetClient netClient) {
            this.netClient = netClient;
            return this;
        }

        public Builder setApiDataConversion(ApiDataConversion apiDataConversion) {
            this.apiDataConversion = apiDataConversion;
            return this;
        }

        public Builder setCacheIntercept(CacheInterceptor cacheInterceptor) {
            this.cacheInterceptor = cacheInterceptor;
            return this;
        }

        public ApiRequestCreater build() {
            if (netClient == null)
                netClient = new NetClient.Builder().build();
            if (apiDataConversion == null) {
                apiDataConversion = new ApiDataConversion() {
                    @Override
                    public boolean isSucceed(String data) {
                        return true;
                    }

                    @Override
                    public Object getBean(String data, Type targetType) {
                        return SerializerFactory.newInstance().createJsonSerializer().unSerial(data, targetType);
                    }
                };
            }
            return new ApiRequestCreater(paramsBuilderFactory, new ApiHelper(netClient, apiDataConversion, cacheInterceptor));
        }
    }


    private <T> ApiSingle<T> createFinalSingle(ApiRequest<T> apiRequest) {
        return new ApiSingle<>(apiRequest);
    }

    private ApiCompletable createFinalCompletable(ApiRequest<?> apiRequest) {
        return new ApiCompletable(apiRequest);
    }


    public class ApiCompletable extends Completable {
        private final ApiRequest<?> apiRequest;

        ApiCompletable(ApiRequest<?> apiRequest) {
            this.apiRequest = apiRequest;
        }

        public ApiCompletable newCompletable() {
            return createFinalCompletable(apiRequest);
        }

        public ApiRequest<?> getApiRequest() {
            return apiRequest;
        }

        @Override
        protected void subscribeActual(CompletableObserver observer) {
            apiHelper.isSucceed(apiRequest).subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(Disposable d) {
                    observer.onSubscribe(d);
                }

                @Override
                public void onComplete() {
                    observer.onComplete();
                }

                @Override
                public void onError(Throwable e) {
                    observer.onError(e);
                }
            });
        }
    }


    public class ApiSingle<T> extends Single<T> {
        private final ApiRequest<T> apiRequest;

        ApiSingle(ApiRequest<T> apiRequest) {
            this.apiRequest = apiRequest;
        }

        @Override
        protected void subscribeActual(SingleObserver<? super T> observer) {
            apiHelper.getBean(apiRequest).subscribe(new SingleObserver<T>() {
                @Override
                public void onSubscribe(Disposable d) {
                    observer.onSubscribe(d);
                }

                @Override
                public void onSuccess(T t) {
                    observer.onSuccess(t);
                }

                @Override
                public void onError(Throwable e) {
                    observer.onError(e);
                }
            });
        }

        public ApiRequest<T> getApiRequest() {
            return apiRequest;
        }

    }
}




