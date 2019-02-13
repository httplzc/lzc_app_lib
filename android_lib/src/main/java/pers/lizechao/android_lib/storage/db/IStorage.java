package pers.lizechao.android_lib.storage.db;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;

/**
 * Created by Lzc on 2018/5/24 0024.
 */
public interface IStorage {
    <T> Completable store(T element, String id);

    <T> Completable store(List<T> list, List<String> ids);

    <T> Completable store(T element, String id, long duration, TimeUnit timeUnit);

    <T> Completable store(List<T> list, List<String> ids, long duration, TimeUnit timeUnit);

    <T> T load(Type type, String id);

    <T> T load(Type type, String id, T defaultData);

    <T> List<T> load(Type type, List<String> ids);

    <T> List<T> load(Type type);

    void delete(Type type, String id);

    void deleteLike(String idLike);

    void delete(Type type, List<String> ids);

    boolean contains(String id);

    void clear();

}
