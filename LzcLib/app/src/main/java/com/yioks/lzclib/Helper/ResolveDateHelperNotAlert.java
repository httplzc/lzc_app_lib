package com.yioks.lzclib.Helper;

/**
 * Created by ${User} on 2016/9/19 0019.
 */
public class ResolveDateHelperNotAlert extends ResolveDataHelper {
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
