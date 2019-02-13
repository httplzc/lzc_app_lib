package pers.lizechao.android_lib.support.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Lzc on 2018/5/22 0022.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NeedPermission {
    String[] permissions() default {};

    String failFunction() default "";

    int requestCode() default -1;
}
