package pers.lizechao.android_lib.net.okhttp;

import java.util.Map;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import pers.lizechao.android_lib.net.base.Call;
import pers.lizechao.android_lib.net.base.CallFactory;
import pers.lizechao.android_lib.net.base.HttpMethod;
import pers.lizechao.android_lib.net.base.RequestData;
import pers.lizechao.android_lib.net.params.BaseParams;
import pers.lizechao.android_lib.net.params.BinaryData;
import pers.lizechao.android_lib.net.params.FormParams;
import pers.lizechao.android_lib.net.params.MultipartFormParams;
import pers.lizechao.android_lib.net.params.MultipleData;
import pers.lizechao.android_lib.net.params.RawParams;

/**
 * Created by Lzc on 2018/6/14 0014.
 */
public class OkHttpCallFactory extends CallFactory {

    private final OkHttpClient client;

    public OkHttpCallFactory(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public Call getCall(RequestData requestData) {
        okhttp3.Call newCall = null;
        try {
            newCall = client.newCall(transToRequest(requestData));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new OkHttpCall(newCall, requestData);
    }

    private Request transToRequest(RequestData requestData) {
        String url = requestData.url;
        BaseParams params = requestData.params;
        HttpMethod method = requestData.method;
        HttpUrl httpUrl = HttpUrl.parse(url);
        Objects.requireNonNull(httpUrl);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.headers(Headers.of(params.getHeads()));
        RequestBody body = null;
        if (method == HttpMethod.GET || method == HttpMethod.HEAD) {
            if (!(params instanceof FormParams))
                throw new IllegalArgumentException(method.name() + "只能为表单数据");
            httpUrl = paramsToHttpUrl(httpUrl, (FormParams) params);
        } else {
            if (params instanceof RawParams) {
                body = transToBody(((RawParams) params).getBinaryData());
            } else if (params instanceof MultipartFormParams) {
                body = transToBody((MultipartFormParams) params);
            } else if (params instanceof FormParams) {
                body = transToBody((FormParams) params);
            }
        }
        if (body != null)
            body = new ProxyRequestBody(body, requestData.uploadProgress);
        return requestBuilder.url(httpUrl).method(method.name(), body).build();

    }


    private HttpUrl paramsToHttpUrl(HttpUrl httpUrl, FormParams params) {
        HttpUrl.Builder builder = httpUrl.newBuilder();
        for (Map.Entry<String, String> entry : params.getUrlParams().entrySet()) {
            builder.addQueryParameter(entry.getKey(), entry.getValue()).build();
        }
        return builder.build();
    }


    private RequestBody transToBody(MultipartFormParams params) {
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();
        multipartBuilder.setType(MultipartBody.FORM);
        for (Map.Entry<String, String> entry : params.getUrlParams().entrySet()) {
            multipartBuilder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, MultipleData[]> entry : params.getMultipartDatas().entrySet()) {
            if (entry.getValue() != null) {
                for (MultipleData multipleData : entry.getValue()) {
                    RequestBody requestBody = transToBody(multipleData.binaryData);
                    if (requestBody != null)
                        multipartBuilder.addFormDataPart(entry.getKey(), multipleData.name, requestBody);
                }

            }

        }
        return multipartBuilder.build();
    }

    private RequestBody transToBody(FormParams params) {
        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.getUrlParams().entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue());
        }
        return formBuilder.build();
    }


    private RequestBody transToBody(BinaryData binaryData) {
        if (binaryData.isFile()) {
            return RequestBody.create(MediaType.parse(binaryData.mediaType), binaryData.file);
        } else if (binaryData.isBytes()) {
            return RequestBody.create(MediaType.parse(binaryData.mediaType), binaryData.bytes);
        } else if (binaryData.isString()) {
            return RequestBody.create(MediaType.parse(binaryData.mediaType), binaryData.data);
        } else {
            return null;
        }
    }
}
