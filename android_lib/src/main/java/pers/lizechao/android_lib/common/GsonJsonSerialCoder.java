package pers.lizechao.android_lib.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import pers.lizechao.android_lib.function.Serializer;

/**
 * Created by Lzc on 2018/5/30 0030.
 */
public class GsonJsonSerialCoder implements Serializer {
    private static final GsonJsonSerialCoder GSON_JSON_SERIAL_CODER = new GsonJsonSerialCoder();
    private final ThreadLocal<Gson> gsonThreadLocal = new ThreadLocal<Gson>() {
        @Override
        protected Gson initialValue() {
            return new GsonBuilder().disableHtmlEscaping().create();

        }
    };

    private Gson getGson() {
        return gsonThreadLocal.get();
    }

    public static GsonJsonSerialCoder getInstance() {
        return GSON_JSON_SERIAL_CODER;
    }

    private GsonJsonSerialCoder() {
    }

    @Override
    public <T> String serial(T object) {
        if (object == null)
            return null;
        return getGson().toJson(object);
    }

    @Override
    public <T> T unSerial(String serialStr, Type type) {
        if (serialStr == null)
            return null;
        return getGson().fromJson(serialStr, type);
    }

    @Override
    public <T> T unSerial(String serialStr, Class<T> tClass) {
        if (serialStr == null)
            return null;
        return getGson().fromJson(serialStr, tClass);
    }

    @Override
    public <T> T unSerial(String serialStr, Class<T> main, Type... type) {
        if (serialStr == null)
            return null;
        return getGson().fromJson(serialStr, TypeToken.getParameterized(main, type).getType());
    }

}
