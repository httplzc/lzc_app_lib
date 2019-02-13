package pers.lizechao.android_lib.storage.db;

import java.lang.reflect.Type;

import pers.lizechao.android_lib.utils.JavaUtils;

/**
 * Created by Lzc on 2018/5/30 0030.
 */
public class StoreUtils {

    public static <T> String getClassTypeStr(T element) {
        return element.getClass().toString();
    }

    public static String getClassTypeStr(Type type) {
        if(JavaUtils.getRawType(type).isInterface())
            throw new IllegalArgumentException("请传入实际类型，而不是接口！");
        return JavaUtils.getRawType(type).toString();
    }



}
