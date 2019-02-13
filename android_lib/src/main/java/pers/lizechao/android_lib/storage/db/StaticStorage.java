package pers.lizechao.android_lib.storage.db;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;

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
 * Time: 10:47
 */
public class StaticStorage implements IStorage {
    private final static StaticStorage STORAGE = new StaticStorage();
    private CacheBeanDao<Object> cacheBeanDao;

    public static StaticStorage getInstance() {
        return STORAGE;
    }

    private StaticStorage() {
        this.cacheBeanDao = new CacheBeanDao<>();
    }

    @Override
    public <T> Completable store(T element, String id) {
        return store(element, id, 0, TimeUnit.MILLISECONDS);
    }

    @Override
    public <T> Completable store(List<T> list, List<String> ids) {
        return store(list, ids, 0, TimeUnit.MILLISECONDS);
    }

    @Override
    public <T> Completable store(T element, String id, long duration, TimeUnit timeUnit) {
        if (element == null || id == null)
            return Completable.create(emitter -> emitter.onError(new IllegalArgumentException("element == null || id == null")));
        DataWrap<Object> dataWrap = new DataWrap<>(id
                , element
                , duration == 0 ? 0 : System.currentTimeMillis() + timeUnit.toMillis(duration)
                , StoreUtils.getClassTypeStr(element));
        cacheBeanDao.updateOrInsert(dataWrap);
        return DBStorage.getInstance().store(element, id, duration, timeUnit);
    }

    @Override
    public <T> Completable store(List<T> list, List<String> ids, long duration, TimeUnit timeUnit) {
        long expiresTime = duration == 0 ? 0 : System.currentTimeMillis() + timeUnit.toMillis(duration);
        String classTypeStr = StoreUtils.getClassTypeStr(list.get(0));
        List<DataWrap<Object>> listStr = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            listStr.add(new DataWrap<>(ids.get(i)
                    , list.get(i)
                    , expiresTime
                    , classTypeStr));
        }
        cacheBeanDao.updateOrInsert(listStr);
        return DBStorage.getInstance().store(list, ids, duration, timeUnit);
    }

    @Override
    public <T> T load(Type type, String id) {
        T data = (T) Optional.ofNullable(cacheBeanDao.select(id)).map(DataWrap::getData).orElse(null);
        if (data == null) {
            data = DBStorage.getInstance().load(type, id);
        }
        return data;
    }

    @Override
    public <T> T load(Type type, String id, T defaultData) {
        T data = load(type, id);
        return data == null ? defaultData : data;
    }

    @Override
    public <T> List<T> load(Type type, List<String> ids) {
        if (type == null || ids == null || ids.size() == 0)
            return null;
        List<DataWrap<Object>> dataWrapList = cacheBeanDao.select(ids);
        if (dataWrapList.size() != ids.size()) {
            dataWrapList = DBStorage.getInstance().load(type, ids);
            cacheBeanDao.updateOrInsert(dataWrapList);
        }
        return Stream.of(dataWrapList).map(data -> (T) data.getData())
                .toList();
    }

    @Override
    public <T> List<T> load(Type type) {
        if (type == null)
            return null;
        String typeStr = StoreUtils.getClassTypeStr(type);
        List<DataWrap<Object>> dataWrapList = cacheBeanDao.selectByTypeStr(typeStr);
        if (dataWrapList.size() == 0) {
            dataWrapList = DBStorage.getInstance().load(type);
            if (dataWrapList.size() != 0)
                cacheBeanDao.updateOrInsert(dataWrapList);
        }
        return Stream.of(dataWrapList).map(data -> (T) data.getData())
                .toList();
    }

    @Override
    public void delete(Type type, String id) {
        cacheBeanDao.delete(id);
        DBStorage.getInstance().delete(type, id);
    }

    @Override
    public void deleteLike(String idLike) {
        cacheBeanDao.deleteLike(idLike);
        DBStorage.getInstance().deleteLike(idLike);
    }

    @Override
    public void delete(Type type, List<String> ids) {
        cacheBeanDao.delete(ids);
        DBStorage.getInstance().delete(type, ids);
    }

    @Override
    public boolean contains(String id) {
        return cacheBeanDao.contains(id) || DBStorage.getInstance().contains(id);
    }

    @Override
    public void clear() {
        cacheBeanDao.clear();
    }
}
