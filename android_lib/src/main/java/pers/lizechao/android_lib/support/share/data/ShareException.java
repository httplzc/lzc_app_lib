package pers.lizechao.android_lib.support.share.data;

/**
 * Created by Lzc on 2018/6/23 0023.
 */
public class ShareException extends IllegalStateException {
    public ShareException(String s) {
        super(s);
    }

    public ShareException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShareException(Throwable cause) {
        super(cause);
    }
}
