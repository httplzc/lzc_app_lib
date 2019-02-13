package pers.lizechao.android_lib.support.protocol.broadcast;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Lzc on 2018/6/27 0027.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BroadcastMsg {
    String action();

    String permission();
}
