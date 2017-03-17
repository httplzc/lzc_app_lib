package com.yioks.lzclib.Untils;

import android.content.Context;
import android.util.Log;

import com.yioks.lzclib.Data.OkHttpInstance;
import com.yioks.lzclib.Helper.FileDownloadCallBack;
import com.yioks.lzclib.Helper.RequestParams;

import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

import static android.R.attr.tag;


/**
 * 网络访问工具类
 */
public class HttpUtil {

    /**
     * get 无参数
     *
     * @param urlString
     * @param responseHandler
     */
    public static void get(String urlString, Callback responseHandler) {
        get(urlString, responseHandler, tag);

    }

    /**
     * get 无参数
     *
     * @param urlString
     * @param responseHandler
     */
    public static void get(String urlString, Callback responseHandler, Object tag) {
        Request request = new Request.Builder().url(urlString).tag(tag).build();
        OkHttpInstance.getClient().newCall(request).enqueue(responseHandler);

    }


    /**
     * post
     *
     * @param urlString
     * @param params
     * @param responseHandler
     */
    public static void post(String urlString, RequestParams params, Callback responseHandler) {
        post(urlString, params, responseHandler, tag);
    }


    /**
     * post
     *
     * @param urlString
     * @param params
     * @param responseHandler
     */
    public static void post(String urlString, RequestParams params, Callback responseHandler, Object tag) {
        RequestBody requestBody = paramsToRequest(params);
        Request request = new Request.Builder().url(urlString).post(requestBody).tag(tag).build();
        OkHttpInstance.getClient().newCall(request).enqueue(responseHandler);
    }


    /**
     * get 有参数
     *
     * @param urlString
     * @param params
     * @param responseHandler
     */
    public static void get(String urlString, RequestParams params, Callback responseHandler) {
        get(urlString, params, responseHandler, null);
    }


    /**
     * get 有参数
     *
     * @param urlString
     * @param params
     * @param responseHandler
     */
    public static void get(String urlString, RequestParams params, Callback responseHandler, Object tag) {
        Request request = new Request.Builder().url(formatUrl(params, urlString)).tag(tag).build();
        OkHttpInstance.getClient().newCall(request).enqueue(responseHandler);
    }


    //head 有参数
    public static void Head(String urlString, RequestParams params, Callback responseHandler) {
        Request request = new Request.Builder().url(formatUrl(params, urlString)).head().build();
        OkHttpInstance.getClient().newCall(request).enqueue(responseHandler);
    }


    /**
     * 格式化url
     *
     * @param params
     * @param urlString
     * @return
     */
    private static HttpUrl formatUrl(RequestParams params, String urlString) {
        String scheme;
        String host;
        urlString=urlString.replaceAll("\\?","");
        scheme = urlString.contains("https://") ? "https" : "http";
        String content = urlString.substring(urlString.indexOf(scheme) + scheme.length()+"://".length());
        String temp[] = content.split("/");
        host = temp[0];
        HttpUrl.Builder builder = new HttpUrl.Builder();
        builder.scheme(scheme);
        builder.host(host);
        for (int i = 1; i < temp.length; i++) {
            builder.addPathSegment(temp[i]);
        }
        for (Map.Entry<String, String> entry : params.getUrlParamsEntry()) {
            builder.addEncodedQueryParameter(entry.getKey(), entry.getValue());
        }
        Log.i("lzc","builder+url"+builder.toString());
        return builder.build();
    }

    public static void cancelAllClient() {
        OkHttpInstance.getClient().dispatcher().cancelAll();
//        for (Call call : OkHttpInstance.getClient().dispatcher().queuedCalls()) {
//            if (call instanceof HandlerCallBack)
//                ((HandlerCallBack) call).cancelAllRequest();
//        }
//        for (Call call : OkHttpInstance.getClient().dispatcher().runningCalls()) {
//            if (call instanceof HandlerCallBack)
//                ((HandlerCallBack) call).cancelAllRequest();
//        }
    }


    public static void cancelAllClient(Context context) {
        for (Call call : OkHttpInstance.getClient().dispatcher().queuedCalls()) {
            if (call.request().tag() instanceof String && call.request().tag().equals(context.getPackageName() + context.getClass().getName())) {
                call.cancel();
//                if (call instanceof HandlerCallBack)
//                    ((HandlerCallBack) call).cancelAllRequest();
            }

        }

        for (Call call : OkHttpInstance.getClient().dispatcher().runningCalls()) {
            if (call.request().tag() instanceof String && call.request().tag().equals(context.getPackageName() + context.getClass().getName())) {
                call.cancel();
//                if (call instanceof HandlerCallBack)
//                    ((HandlerCallBack) call).cancelAllRequest();
            }


        }
    }

    public static void cancelAllClient(Object tag) {
        for (Call call : OkHttpInstance.getClient().dispatcher().queuedCalls()) {
            if (call.request().tag() == tag)
                call.cancel();
        }

        for (Call call : OkHttpInstance.getClient().dispatcher().runningCalls()) {
            if (call.request().tag() == tag)
                call.cancel();
        }
    }


    /**
     * 参数转为请求
     *
     * @param params
     * @return
     */
    private static RequestBody paramsToRequest(RequestParams params) {
        RequestBody requestBody = null;
        if (!params.fileIsEmpty()) {
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

            for (Map.Entry<String, String> entry : params.getUrlParamsEntry()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String, RequestParams.FileWrapper> entry : params.getFileParams()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue().customFileName, RequestBody.create(entry.getValue().contentType, entry.getValue().file));
            }
            for (Map.Entry<String, List<RequestParams.FileWrapper>> entry : params.getFileListParams()) {
                for (RequestParams.FileWrapper fileWrapper : entry.getValue()) {
                    builder.addFormDataPart(entry.getKey(), fileWrapper.customFileName, RequestBody.create(fileWrapper.contentType, fileWrapper.customFileName));
                }

            }
            requestBody = builder.build();
        } else {
            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : params.getUrlParamsEntry()) {
                builder.add(entry.getKey(), entry.getValue());
            }
            requestBody = builder.build();
        }
        return requestBody;
    }


    //下载文件
    public static void download(String url, RequestParams params, FileDownloadCallBack fileDownloadCallBack) {
        Request request = new Request.Builder().url(formatUrl(params, url)).tag(tag).build();
        OkHttpInstance.getClient().newCall(request).enqueue(fileDownloadCallBack);
    }

    //下载文件
    public static void download(String url, FileDownloadCallBack fileDownloadCallBack) {
        Request request = new Request.Builder().url(url).tag(tag).build();
        OkHttpInstance.getClient().newCall(request).enqueue(fileDownloadCallBack);
    }

}
