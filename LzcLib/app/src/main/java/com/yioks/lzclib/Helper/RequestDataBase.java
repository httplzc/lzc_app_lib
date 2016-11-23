package com.yioks.lzclib.Helper;

import com.loopj.android.http.RequestParams;

/**
 * Created by ${User} on 2016/8/30 0030.
 */
public interface RequestDataBase {
    RequestParams SetParams(RequestParams requestParams,int type,String... strings) throws Exception;
}
