package com.yioks.lzclib.Untils;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


/**
 * 网络访问工具类
 * Created by Yioks-ZhangMengzhen on 2016/4/13.
 */
public class HttpUtil {
    private static AsyncHttpClient client = new AsyncHttpClient();//实例化对象

    static {
        //设置链接超时，如果不设置，默认为30s
        client.setTimeout(30000);
    }

    /**
     * 用一个完整的url获取一个string对象
     *
     * @param urlString
     * @param responseHandler
     */
    public static void get(Context context, String urlString, AsyncHttpResponseHandler responseHandler) {
        client.get(context, urlString, responseHandler);

    }

    /**
     * url里面带参数获取一个string对象
     *
     * @param urlString
     * @param params
     * @param responseHandler
     */
    public static void get(Context context, String urlString, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(context, urlString, params, responseHandler);
    }



    /**
     * 不带参数，获取json对象或者数组
     *
     * @param urlString
     * @param responseHandler
     */
    public static void get(Context context, String urlString, JsonHttpResponseHandler responseHandler) {
        client.get(context, urlString, responseHandler);
    }

    /**
     * 带参数，获取json对象或者数组
     *
     * @param urlString
     * @param params
     * @param responseHandler
     */
    public static void get(Context context, String urlString, RequestParams params, JsonHttpResponseHandler responseHandler) {
        client.get(context, urlString, params, responseHandler);
    }


    /**
     * 下载数据使用，会返回byte数据
     *
     * @param urlString
     * @param responseHandler
     */
    public static void get(Context context, String urlString, BinaryHttpResponseHandler responseHandler) {
        client.get(context, urlString, responseHandler);
    }

    /**
     * 用一个完整的url获取一个string对象
     *
     * @param url
     * @param responseHandler
     */
    public static void post(Context context,String url, AsyncHttpResponseHandler responseHandler) {
        client.post(url, responseHandler);
    }

    /**
     * url里面带参数获取一个string对象
     *
     * @param url
     * @param params
     * @param responseHandler
     */
    public static void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(context, url, params, responseHandler);

    }


    public static void postAndHead(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler,HashMap<String,String> headParams) {
        for(Map.Entry<String,String>entry:headParams.entrySet())
        {
            client.addHeader(entry.getKey(),entry.getValue());
        }
        client.post(context, url, params, responseHandler);

    }



    /**
     * 不带参数，获取json对象或者数组
     *
     * @param urlString
     * @param responseHandler
     */
    public static void post(String urlString, JsonHttpResponseHandler responseHandler) {
        client.post(urlString, responseHandler);
    }

    /**
     * 带参数，获取json对象或者数组
     *
     * @param urlString
     * @param params
     * @param responseHandler
     */
    public static void post(Context context, String urlString, RequestParams params, JsonHttpResponseHandler responseHandler) {
        client.post(context, urlString, params, responseHandler);
    }

    /**
     * 下载文件
     *
     * @param url
     * @param params
     * @param fileAsyncHttpResponseHandler
     */
    public static void download(String url, RequestParams params, FileAsyncHttpResponseHandler fileAsyncHttpResponseHandler) {
        client.get(url, params, fileAsyncHttpResponseHandler);
    }

    public static void download(String url,FileAsyncHttpResponseHandler fileAsyncHttpResponseHandler)
    {
        client.get(url,fileAsyncHttpResponseHandler);
    }

    public static void Head(String url,AsyncHttpResponseHandler asyncHttpResponseHandler)
    {
        client.head(url,asyncHttpResponseHandler);
    }

    public static void cancelAllClient()
    {

        client.cancelAllRequests(true);
    }


    public static void cancelAllClient(Context context)
    {

        client.cancelRequests(context, true);
    }

    public static void cancelAllClient(Object tag)
    {

        client.cancelRequestsByTAG(tag,true);
    }


    public static AsyncHttpClient getClient() {
        return client;
    }



    public static String getMD5(byte[] src) {
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(src);
            for (byte b : md.digest()) {
                sb.append(Integer.toString(b >>> 4 & 0xF, 16)).append(Integer.toString(b & 0xF, 16));
            }
        } catch (NoSuchAlgorithmException e) {
        }
        return sb.toString();
    }

    public static void setChaoshiTime(int time)
    {
        client.setTimeout(time);
    }

    public static void setChaoshiTime()
    {
        client.setTimeout(30000);
    }
}
