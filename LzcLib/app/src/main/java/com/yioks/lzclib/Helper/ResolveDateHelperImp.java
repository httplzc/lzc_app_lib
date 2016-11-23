package com.yioks.lzclib.Helper;

import android.content.Context;

import com.loopj.android.http.RequestParams;

/**
 * Created by ${User} on 2016/11/23 0023.
 */
public class ResolveDateHelperImp extends ResolveDataHelper {
    public ResolveDateHelperImp(Context context, RequestDataBase requestData) {
        super(context, requestData);
    }

    public ResolveDateHelperImp(Context context, RequestDataBase requestData, RequestParams requestParams) {
        super(context, requestData, requestParams);
    }

    @Override
    protected void Token_error() {

    }
}
