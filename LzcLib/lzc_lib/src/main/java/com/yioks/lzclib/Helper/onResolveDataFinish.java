package com.yioks.lzclib.Helper;

/**
 * Created by Administrator on 2016/8/3 0003.
 */
public interface onResolveDataFinish {

    /**
     * @exception NullPointerException
     */
    void resolveFinish(Object data);

    /**
     * @exception NullPointerException
     * @param code
     */
    void onFail(String code);
}
