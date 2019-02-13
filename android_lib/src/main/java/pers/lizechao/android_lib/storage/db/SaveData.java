package pers.lizechao.android_lib.storage.db;

import java.lang.reflect.Type;

import pers.lizechao.android_lib.data.ApplicationData;

/**
 * Created by Lzc on 2018/3/5 0005.
 */
public class SaveData<T> {
    private T data;
    private final String tag;
    private Type type;


    //泛型使用
    public SaveData(String tag, Type type) {
        this.tag = "SaveData_" + tag;
        this.type = type;
    }

    //一般使用
    public SaveData(String tag) {
        this.tag = "SaveData_" + tag;
        type = null;
    }

    public void set(T data) {
        synchronized (this) {
            this.data = data;
            if (type == null)
                type = data.getClass();
            DBStorage.getInstance().store(data, tag);
        }
    }


    public T get() {
        synchronized (this) {
            if (data == null && ApplicationData.applicationContext != null) {
                data = DBStorage.getInstance().load(type, tag);
            }
            return data;
        }
    }

    public void reload() {
        synchronized (this) {
            if (ApplicationData.applicationContext != null) {
                data = DBStorage.getInstance().load(type, tag);
            }
        }
    }

    public synchronized void clear() {
        DBStorage.getInstance().clear();
    }


}
