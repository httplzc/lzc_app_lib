package pers.lizechao.android_lib.function;

import java.lang.reflect.Type;

/**
 * Created by Lzc on 2018/5/30 0030.
 */
public interface Serializer {
    <T> String serial(T object);

    <T> T unSerial(String serialStr, Type type);

    <T> T unSerial(String serialStr, Class<T> tClass);

    <T> T unSerial(String serialStr, Class<T> main, Type... type);

}
