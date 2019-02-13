package pers.lizechao.android_lib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 数据SharedPreferences存储工具类
 *
 */
public class SharedPreferencesUtil {
    /**
     * 保存在手机里面的文件名
     */
    public static final String FILE_NAME = "share_data";

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param key
     * @param object
     */
    public static void put(Context context, String key, Object object) {
        put(context, key, object, FILE_NAME);
    }


    public static void put(Context context, String key, Object object, String name) {
        if (object == null)
            return;
        SharedPreferences sp = context.getSharedPreferences(name,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer ) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean ) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float ) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    public static <T> T get(Context context, String key, T defaultObject) {
        return (T) get(context, key, defaultObject, FILE_NAME);
    }

    public static <T> T get(Context context, String key, Object defaultObject, String name) {
        return (T) getValue(context, key, defaultObject, name);
    }

    public static Object getValue(Context context, String key, Class dataClass, Object defaultObject, String name) {
        if (defaultObject == null)
            return null;
        SharedPreferences sp = context.getSharedPreferences(name,
                Context.MODE_PRIVATE);
        if (dataClass == String.class) {
            return sp.getString(key, (String) defaultObject);
        } else if (dataClass == Integer.class || dataClass == int.class) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (dataClass == Boolean.class || dataClass == boolean.class) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (dataClass == Float.class || dataClass == float.class) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (dataClass == Long.class || dataClass == long.class) {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    public static Object getValue(Context context, String key, Class dataClass, String name) {
        Object defaultValue = getDefaultValue(dataClass);
        return getValue(context, key, dataClass, defaultValue, name);
    }

    private static Object getDefaultValue(Class dataClass) {
        if (dataClass == String.class) {
            return "";
        } else if (dataClass == Integer.class || dataClass == int.class) {
            return 0;
        } else if (dataClass == Boolean.class || dataClass == boolean.class) {
            return false;
        } else if (dataClass == Float.class || dataClass == float.class) {
            return 0F;
        } else if (dataClass == Long.class || dataClass == long.class) {
            return 0L;
        }
        return null;
    }


    public static Object getValue(Context context, String key, @NonNull Object defaultObject, String name) {
        return getValue(context, key, defaultObject.getClass(), defaultObject, name);
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param context
     * @param key
     */
    public static void remove(Context context, String key) {
        remove(context, key, FILE_NAME);
    }

    public static void remove(Context context, String key, String name) {
        SharedPreferences sp = context.getSharedPreferences(name,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }


    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @param context
     * @return
     */
    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
            editor.commit();
        }
    }
}
