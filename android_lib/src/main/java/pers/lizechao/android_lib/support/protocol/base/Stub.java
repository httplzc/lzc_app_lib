package pers.lizechao.android_lib.support.protocol.base;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Lzc on 2018/6/26 0026.
 * 交互协议基类
 * 由交互接口class初始化
 */
public abstract class Stub<T> {
    //协议接口
    protected final Class<T> interfaceClass;
    //协议方法列表
    protected final List<Method> methodList;

    private static final String MethodIndexKey = "StubData_Method_Index";
    private static final String ParamsCountKey = "Params_Count_Index";
    private static final String paramsHead = "Stub_params";


    public Stub(Class<T> interfaceClass) {
        requireInterface(interfaceClass);
        this.interfaceClass = interfaceClass;
        methodList = StubUtil.getMethodList(interfaceClass);
    }

    private void requireInterface(Class<T> tClass) {
        if (tClass == null || !tClass.isInterface()) {
            throw new IllegalArgumentException("class 必须为接口");
        }
    }


    protected  static class StubData {
        final int indexMethod;
        @Nullable
        final
        Object[] args;

        StubData(int indexMethod, @Nullable Object[] args) {
            this.indexMethod = indexMethod;
            this.args = args;
            if (indexMethod < 0) {
                throw new IllegalArgumentException("协议目标方法不存在");
            }
        }

        public StubData(Bundle bundle) {
            args = getArgsFromBundle(bundle);
            indexMethod = bundle.getInt(MethodIndexKey);
            if (indexMethod < 0) {
                throw new IllegalArgumentException("协议目标方法不存在  "+bundle.toString());
            }

        }

        public StubData(Object obj) {
            Object[] datas = (Object[]) obj;
            args = (Object[]) datas[0];
            indexMethod = (int) datas[1];
        }

        public Bundle getBundle() {
            Bundle bundle = createBundle(args);
            bundle.putInt(MethodIndexKey, indexMethod);
            bundle.putInt(ParamsCountKey, args!=null?args.length:0);
            return bundle;
        }

        public Object getObject() {
            return new Object[]{args, indexMethod};
        }


        private Bundle createBundle(Object[] args) {
            Bundle bundle = new Bundle();
            if (args == null)
                return bundle;
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                String key = paramsHead + i;
                if (arg == null) {
                    bundle.putSerializable(key, null);
                } else if (Parcelable.class.isInstance(arg)) {
                    bundle.putParcelable(key, (Parcelable) arg);
                } else if (Serializable.class.isInstance(arg))
                    bundle.putSerializable(key, (Serializable) arg);
                else if (Parcelable[].class.isInstance(arg)) {
                    bundle.putParcelableArray(key, (Parcelable[]) arg);
                } else {
                    bundle.putSerializable(key, null);
                }
            }
            return bundle;
        }

        private Object[] getArgsFromBundle(Bundle bundle) {
            bundle.setClassLoader(StubUtil.class.getClassLoader());
            int paramsCount = bundle.getInt(ParamsCountKey);
            if (paramsCount == 0)
                return null;
            Object data[] = new Object[paramsCount];
            for (int i = 0; i < paramsCount; i++) {
                data[i] = bundle.get(paramsHead + i);
            }
            return data;
        }
    }


}
