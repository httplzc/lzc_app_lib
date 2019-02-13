package pers.lizechao.android_lib.utils;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Lzc on 2017/7/24 0024.
 */

public class JavaUtils {


    public static <T> T defaultValue(T data, T defaultValue) {
        if (data == null)
            return defaultValue;
        else
            return data;
    }

    public static <T> List<T> newList(int count, T defaultValue) {
        List<T> data = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            data.add(defaultValue);
        }
        return data;
    }


    public static Class<?> getRawType(Type type) {
        //普通class
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        //泛型类型
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            if (!(rawType instanceof Class)) throw new IllegalArgumentException();
            return (Class<?>) rawType;
        }
        //泛型数组
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return Array.newInstance(getRawType(componentType), 0).getClass();
        }
        //
        if (type instanceof TypeVariable) {
            return Object.class;
        }
        if (type instanceof WildcardType) {
            return getRawType(((WildcardType) type).getUpperBounds()[0]);
        }
        return null;
    }

    public static Type createType(Type rawType, Type... typeArguments) {
        return TypeToken.getParameterized(rawType, typeArguments).getType();
    }

    public static Type createArrayType(Type rawType) {
        return TypeToken.getArray(rawType).getType();
    }

    public static String typeToString(Type type) {
        return type instanceof Class ? ((Class<?>) type).getName() : type.toString();
    }

    public static float random(float start, float end) {
        return random(new Random(), start, end);
    }

    public static float random(Random random, float start, float end) {
        return start + (end - start) * random.nextFloat();
    }
}
