package pers.lizechao.android_lib.net.params;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.annimon.stream.Optional;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lzc on 2018/6/27 0027.
 */
public abstract class BaseParams<B extends BaseParams.Builder> {
    protected final Map<String, String> heads;

    BaseParams(Map<String, String> heads) {
        this.heads = heads;
    }

    public void removeHead(String key) {
        heads.remove(key);
    }

    public Map<String, String> getHeads() {
        return heads;
    }

    abstract public B newBuilder();


    public abstract static class Builder<T extends Builder, P extends BaseParams> {
        protected final Map<String, String> heads = new HashMap<>();

        protected abstract T self();


        public T putParams(@Nullable BaseParams<?> baseParams) {
            if (baseParams != null)
                putHeadAll(baseParams.heads);
            return self();
        }

        public T putHeadAll(Map<String, String> heads) {
            this.heads.putAll(heads);
            return self();
        }

        //请求头
        public T putHead(String key, String value) {
            heads.put(key, value);
            return self();
        }

        public T setHeadRange(long length) {
            return putHead("RANGE", " bytes=" + length + "-");
        }

        abstract public P build();


        /**
         * @param objects 参数数组
         * @param params  注解数组
         * @param names   备用名字数组
         */
        public T putBeanByData(Object objects[], Params params[], String names[]) {
            if (objects.length != params.length)
                throw new IllegalArgumentException("参数长度不相等");
            for (int i = 0; i < objects.length; i++) {
                Object arg = objects[i];
                Params param = params[i];
                if(arg==null)
                    continue;
                if (param != null && param.ignore())
                    continue;
                if (param != null && param.isBean()) {
                    this.putParams(putBean(arg).build());
                    continue;
                }
                if((param==null|| TextUtils.isEmpty(param.name()))&&TextUtils.isEmpty(names[i]))
                    continue;
                String name = Optional.ofNullable(param).map(Params::name).orElse(names[i]);
                putUnknownObject(name, arg, param);
            }
            return self();
        }

        public void putUnknownObject(@NonNull String name, @NonNull Object args, @Nullable Params params) {
            if (params != null && params.isHead()) {
                if (args instanceof String) {
                    putHead(name, (String) args);
                }
              else if(args instanceof Map)
                {
                    Map<String,String>map= (Map<String, String>) args;
                    putHeadAll(map);
                }
            }
        }

        public T putBean(Object data) {
            Class thisClass = data.getClass();
            Field fields[] = thisClass.getDeclaredFields();
            List<Object> objectList = new ArrayList<>();
            List<String> nameList = new ArrayList<>();
            List<Params> paramsList = new ArrayList<>();
            for (Field field : fields) {
                field.setAccessible(true);
                String valueName = field.getName();
                Class valueType = field.getType();
                Params params = field.getAnnotation(Params.class);
                int fieldValue = field.getModifiers();
                try {
                    if (Modifier.isStatic(fieldValue) || Modifier.isFinal(fieldValue) || valueType == null || field.get(data) == null) {
                        field.setAccessible(false);
                        continue;
                    }
                    objectList.add(field.get(data));
                    nameList.add(valueName);
                    paramsList.add(params);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                field.setAccessible(false);
            }
            String names[] = new String[nameList.size()];
            Params params[] = new Params[paramsList.size()];
            nameList.toArray(names);
            paramsList.toArray(params);
            return putBeanByData(objectList.toArray(), params, names);
        }

    }
}
