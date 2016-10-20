package com.yioks.lzclib.Helper;

import android.content.Context;

import com.loopj.android.http.RequestParams;
import com.yioks.lzclib.Data.GlobalVariable;
import com.yioks.lzclib.Untils.DeviceUtil;

/**
 * 网络请求参数
 * Created by Yioks-ZhangMengzhen on 2016/5/21.
 */
public class HttpParameter {

    private String typeId = "";
    private String version = "1";
    private String data_md5 = "";
    private String key = "";
    private String flag = "";

    /**
     * 接口必备参数
     *
     * @param context 上下文
     * @param params  参数
     * @param method  接口方法名
     * @return
     */
    public static RequestParams setHttpParam(Context context, RequestParams params, String method) {

        String time = System.currentTimeMillis() + "";//时间戳
        params.put("a", method.trim());//操作
        params.put("t", "");//类型ID
        params.put("v", "1");//版本
        params.put("mpKey", DeviceUtil.getDeviceUUID(context));//手机序列号
        params.put("_t", time);//时间戳
        params.put("dataKey", "123456");//数据的MD5标识
        params.put("codeKey", "");//指纹校验KEY
        params.put("flag", GlobalVariable.flag);
        return params;
    }

    public RequestParams setHttpParamData(Context context, RequestParams params, String method) {

        String time = System.currentTimeMillis() + "";//时间戳
        params.put("a", method.trim());//操作
        params.put("t", typeId);//类型ID
        params.put("v", version);//版本
        params.put("mpKey", DeviceUtil.getDeviceUUID(context));//手机序列号
        params.put("_t", time);//时间戳
        params.put("dataKey", data_md5);//数据的MD5标识
        params.put("codeKey", key);//指纹校验KEY
        params.put("flag", flag);
        return params;
    }

}
