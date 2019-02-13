package pers.lizechao.android_lib.net.base;


import java.io.File;

import okhttp3.OkHttpClient;
import pers.lizechao.android_lib.net.okhttp.OkHttpCallFactory;
import pers.lizechao.android_lib.net.params.FormParams;

/**
 * Created by Lzc on 2018/6/14 0014.
 */
public class NetClient {
    private final CallFactory callFactory;
    private final String baseUrl;

    NetClient(CallFactory callFactory, String baseUrl) {
        this.callFactory = callFactory;
        this.baseUrl = baseUrl;
    }


    public Call newCall(RequestData requestData) {
        return callFactory.getCall(requestData.setUrlBase(baseUrl));
    }

    public Call newDownloadCall(String url, File file) {
        return newCall(new RequestData.Builder()
          .url(url)
          .params(new FormParams.Builder().setHeadRange(file.length()).build())
          .method(HttpMethod.GET)
          .build());
    }

    public static final class Builder {
        String baseUrl;
        CallFactory callFactory;
        OkHttpClient okHttpClient;


        public Builder() {
        }

        public Builder callFactory(CallFactory callFactory) {
            this.callFactory = callFactory;
            return this;
        }

        public Builder okHttpClient(OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
            return this;
        }


        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }


        public NetClient build() {
            if (callFactory == null) {
                if (okHttpClient == null)
                    okHttpClient = new OkHttpClient();
                callFactory = new OkHttpCallFactory(okHttpClient);
            }
            return new NetClient(callFactory, baseUrl);
        }
    }


}
