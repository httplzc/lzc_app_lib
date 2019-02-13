package pers.lizechao.android_lib.net.data;

import android.support.annotation.Nullable;

/**
 * Created by Lzc on 2018/6/27 0027.
 * http错误码错误
 */
public class HttpCodeError extends RuntimeException{

    private int code;
    @Nullable
    private String msg;
    public HttpCodeError(int code) {
        super("http code:  "+code);
    }

    public HttpCodeError(int code, @Nullable String msg) {
        super("http code:  "+code+"   msg:"+msg);
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    @Nullable
    public String getMsg() {
        return msg;
    }
}
