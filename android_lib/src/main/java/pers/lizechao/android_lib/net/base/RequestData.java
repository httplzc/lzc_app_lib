package pers.lizechao.android_lib.net.base;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.annimon.stream.function.BiConsumer;

import pers.lizechao.android_lib.net.params.BaseParams;
import pers.lizechao.android_lib.net.params.FormParams;

/**
 * Created by Lzc on 2018/6/15 0015.
 */
public class RequestData {
    public final HttpMethod method;
    private final String urlPath;
    public final BaseParams params;
    public final BiConsumer<Long, Long> uploadProgress;
    @Nullable
    private String urlBase;
    public String url;

    RequestData setUrlBase(@Nullable String base) {
        if (base == null)
            return this;
        this.urlBase = base;
        if (urlPath != null) {
            String urlPath = this.urlPath;
            if (base.endsWith("/"))
                base = base.substring(0, base.length() - 1);
            if (!urlPath.startsWith("/"))
                urlPath = "/" + urlPath;
            url = base + urlPath;
        }

        return this;
    }

    RequestData(HttpMethod method, String urlPath, BaseParams params, BiConsumer<Long, Long> uploadProgress, String url) {
        this.method = method;
        this.urlPath = urlPath;
        this.params = params;
        this.uploadProgress = uploadProgress;
        this.url = url;
    }

    public static final class Builder {
        HttpMethod method;
        String urlPath;
        BaseParams params;
        String url;
        BiConsumer<Long, Long> uploadProgress;

        public Builder() {
        }


        public RequestData.Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }


        public RequestData.Builder urlPath(String urlPath) {
            if (urlPath != null && urlPath.contains("http")) {
                return url(urlPath);
            }
            this.urlPath = urlPath;
            return this;
        }

        public RequestData.Builder url(String url) {
            if (url != null && !url.contains("http")) {
                return urlPath(url);
            }
            this.url = url;
            return this;
        }

        public RequestData.Builder params(BaseParams params) {
            this.params = params;
            return this;
        }

        public RequestData.Builder uploadProgress(BiConsumer<Long, Long> uploadProgress) {
            this.uploadProgress = uploadProgress;
            return this;
        }

        public RequestData build() {
            if (method == null)
                method = HttpMethod.POST;
            if (params == null)
                params = new FormParams.Builder().build();
            if (TextUtils.isEmpty(urlPath) && TextUtils.isEmpty(url))
                throw new IllegalArgumentException("urlPath 和 url不能同时为空");
            return new RequestData(method, urlPath, params, uploadProgress, url);
        }
    }

    @Override
    public String toString() {
        return "\nmethod:" + method + "\n" + "urlPath:" + url + "\n" + "params:" + params;
    }
}
