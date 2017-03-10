package com.yioks.lzclib.Helper;

import android.app.Activity;
import android.content.Context;

import com.yioks.lzclib.Untils.DeviceUtil;

/**
 * Created by ${User} on 2016/10/20 0020.
 */
public class ParamsBuilder {
    private String typeId = "";
    private String version = "1";
    private String data_md5 = "";
    private String key = "";
    private String flag = "";
    private String method = "";
    private RequestParams params;
    private Context context;

    public ParamsBuilder(Context context) {
        this.context = context;
    }

    public ParamsBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    public ParamsBuilder setData_md5(String data_md5) {
        this.data_md5 = data_md5;
        return this;
    }

    public ParamsBuilder setKey(String key) {
        this.key = key;
        return this;
    }

    public ParamsBuilder setFlag(String flag) {
        this.flag = flag;
        return this;
    }

    public ParamsBuilder setMethod(String method) {
        this.method = method;
        return this;
    }

    public RequestParams build() {
        params = new RequestParams();
        String time = System.currentTimeMillis() + "";//时间戳
        params.put("a", method.trim());//操作
        params.put("t", typeId);//类型ID
        params.put("v", version);//版本
        if(context instanceof Activity)
        {
            params.put("mpKey", DeviceUtil.getDeviceUUID((Activity) context));//手机序列号
        }
        else
        {
            params.put("mpKey","");
        }

        params.put("_t", time);//时间戳
        params.put("dataKey", data_md5);//数据的MD5标识
        params.put("codeKey", key);//指纹校验KEY
        params.put("flag", flag);
        return params;
    }

}

