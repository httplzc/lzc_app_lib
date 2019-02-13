package pers.lizechao.android_lib.storage.db;

import java.util.List;

/**
 * Created by Lzc on 2018/5/30 0030.
 */
public interface StoreDao<T> {
    void updateOrInsert(DataWrap<T> dataWrap);

    void updateOrInsert(List<? extends DataWrap<T>> data);

    void delete(String key);

    void deleteLike(String keyLike);

    void delete(List<String> key);

    DataWrap<T> select(String key);

    List<DataWrap<T>> select(List<String> key);

    List<DataWrap<T>> selectByTypeStr(String typeStr);

    boolean contains(String key);

    void clear();
}
