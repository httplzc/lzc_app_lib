package pers.lizechao.android_lib.net.params;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

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
 * Time: 16:37
 */
public abstract class BaseFormParams<B extends BaseFormParams.BuilderAb<? extends BaseFormParams, ? extends BaseFormParams.BuilderAb>> extends BaseParams<B> {
    private final Map<String, String> urlParams;

    BaseFormParams(Map<String, String> heads, Map<String, String> urlParams) {
        super(heads);
        this.urlParams = urlParams;
    }

    public String getDataFromUrlParams(String key) {
        return urlParams.get(key);
    }

    //去除url 键值对
    public void removeUrlParams(String key) {
        urlParams.remove(key);
    }

    public Map<String, String> getUrlParams() {
        return urlParams;
    }

    @Override
    abstract public B newBuilder();

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        for (Map.Entry<String, String> entry : urlParams.entrySet()) {
            stringBuilder.append(entry.getKey()).append(" : ").append(entry.getValue());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public abstract static class BuilderAb<P extends BaseFormParams, T extends BaseFormParams.BuilderAb<?, ?>> extends BaseParams.Builder<T, P> {
        protected final Map<String, String> urlParams = new HashMap<>();



        @Override
        public T putParams(@Nullable BaseParams<?> baseParams) {
            if (baseParams != null&&baseParams instanceof BaseFormParams<?>) {
                putAll(((BaseFormParams) baseParams).getUrlParams());
            }
            return super.putParams(baseParams);
        }


        //url 键值对
        public T put(String key, String value) {
            if (key != null && value != null) {
                urlParams.put(key, value);
            }
            return self();
        }

        //url 键值对
        public T putAll(Map<String, String> urlParams) {
            this.urlParams.putAll(urlParams);
            return self();
        }


        @Override
        public void putUnknownObject(@NonNull String name, @NonNull Object args, @Nullable Params params) {
            super.putUnknownObject(name, args, params);
            this.put(name, args + "");
        }

    }

}
