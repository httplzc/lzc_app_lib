package com.yioks.lzclib.Helper;

import android.content.Context;

import com.loopj.android.http.RequestParams;

/**
 * Created by ${User} on 2016/9/19 0019.
 */
public  abstract class ResolveDateHelperNotAlert extends ResolveDataHelper {
    public ResolveDateHelperNotAlert(Context context, RequestDataBase requestData) {
        super(context, requestData);
    }

    public ResolveDateHelperNotAlert(Context context, RequestDataBase requestData, RequestParams requestParams) {
        super(context, requestData, requestParams);
    }

    @Override
    protected void requestDataFail() {
        if(onResolveDataFinish!=null)
        {
            onResolveDataFinish.onFail(null);
        }
    }

    @Override
    protected void requestDataFail(String string) {
        onResolveDataFinish.onFail(null);
    }

    @Override
    protected void requestDataFail(String string, String code) {
        onResolveDataFinish.onFail(null);
    }
}
