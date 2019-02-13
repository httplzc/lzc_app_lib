package pers.lizechao.android_lib.storage.db;

import android.text.TextUtils;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with
 * ********************************************************************************
 * #         ___                     ________                ________             *
 * #       |\  \                   |\_____  \              |\   ____\             *
 * #       \ \  \                   \|___/  /|             \ \  \___|             *
 * #        \ \  \                      /  / /              \ \  \                *
 * #         \ \  \____                /  /_/__              \ \  \____           *
 * #          \ \_______\             |\________\             \ \_______\         *
 * #           \|_______|              \|_______|              \|_______|         *
 * #                                                                              *
 * ********************************************************************************
 * Date: 2018/11/19 0019
 * Time: 10:50
 * 存储静态对象 ，线程不安全，并且共享同一个对象
 */
class CacheBeanDao<T> implements StoreDao<T> {
    private ConcurrentHashMap<String, SoftReference<DataWrap<T>>> mCache;

    CacheBeanDao() {
        mCache = new ConcurrentHashMap<>();
    }

    //是否有效
    private boolean checkNotExpires(long expires) {
        return expires == 0 || expires >= System.currentTimeMillis();
    }


    @Override
    public void updateOrInsert(DataWrap<T> dataWrap) {
        SoftReference<DataWrap<T>> softReference = new SoftReference<>(dataWrap);
        mCache.put(dataWrap.getKey(), softReference);
    }

    @Override
    public void updateOrInsert(List<? extends DataWrap<T>> data) {
        for (int i = 0; i < data.size(); i++) {
            SoftReference<DataWrap<T>> softReference = new SoftReference<>(data.get(i));
            mCache.put(data.get(i).getKey(), softReference);
        }
    }

    @Override
    public void delete(String key) {
        mCache.remove(key);
    }

    @Override
    public void deleteLike(String keyLike) {
        Stream.of(mCache.keySet())
                .filter(value -> value.contains(keyLike))
                .forEach(s -> mCache.remove(s));
    }

    @Override
    public void delete(List<String> key) {
        for (String s : key) {
            mCache.remove(s);
        }
    }

    @Override
    public DataWrap<T> select(String key) {
        return Optional.ofNullable(mCache.get(key)).map(SoftReference::get)
                .filter(value -> checkNotExpires(value.getExpires()))
                .orElse(null);
    }

    @Override
    public List<DataWrap<T>> select(List<String> keys) {
        return Stream.of(keys)
                .map(key -> mCache.get(key))
                .filter(data -> Optional.ofNullable(data).map(SoftReference::get) != null)
                .map(SoftReference::get)
                .filter(value -> checkNotExpires(value.getExpires()))
                .collect(Collectors.toList());
    }


    @Override
    public List<DataWrap<T>> selectByTypeStr(String typeStr) {
        return Stream.ofNullable(mCache).filter(data -> TextUtils.equals(
                Optional.ofNullable(data).map(Map.Entry::getValue).map(SoftReference::get).map(DataWrap::getTypeStr).orElse(null),
                typeStr
        ))
                .filter(value -> checkNotExpires(value.getValue().get().getExpires()))
                .map(data -> data.getValue().get()).collect(Collectors.toList());
    }


    @Override
    public boolean contains(String key) {
        return mCache.containsKey(key);
    }

    @Override
    public void clear() {
        mCache.clear();
    }
}
