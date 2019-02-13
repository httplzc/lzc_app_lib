package pers.lizechao.android_lib.storage.db;

import android.content.Context;

import com.annimon.stream.Stream;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.schedulers.Schedulers;
import pers.lizechao.android_lib.common.SerializerFactory;
import pers.lizechao.android_lib.function.Serializer;

/**
 * Created by Lzc on 2018/5/24 0024.
 */
public class DBStorage implements IStorage, IStoreDataChange {

    private static DBStorage mDBStorage = null;
    //数据库存储
    final StoreDao<String> dao;
    //缓存存储
    final StoreDao<String> cacheDao;
    private final Serializer serializer;

    protected DBStorage(Context context) {
        cacheDao = new CacheJsonDao();
        serializer = SerializerFactory.newInstance().createJsonSerializer();
        dao = new DbJsonDao(new DbHelper(context));
    }

    private class ThreadCompletable extends Completable implements Runnable {
        private CompletableObserver completableObserver;
        Runnable runnable;

        ThreadCompletable(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        protected void subscribeActual(CompletableObserver s) {
            completableObserver = s;
        }

        @Override
        public void run() {
            try {
                runnable.run();
                if (completableObserver != null)
                    completableObserver.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
                completableObserver.onError(e);
            }
        }
    }

    private ThreadCompletable operateDbAsync(final Runnable runnable) {
        ThreadCompletable threadCompletable = new ThreadCompletable(runnable);
        Schedulers.io().scheduleDirect(threadCompletable);
        return threadCompletable;
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
        return operateDbAsync(() -> {
            DataWrap<String> dataWrap = new DataWrap<>(id
                    , serializer.serial(element)
                    , duration == 0 ? 0 : System.currentTimeMillis() + timeUnit.toMillis(duration)
                    , StoreUtils.getClassTypeStr(element));
            cacheDao.updateOrInsert(dataWrap);
            dao.updateOrInsert(dataWrap);
            dataUpdate(id, element.getClass());
        });
    }

    @Override
    public <T> Completable store(List<T> list, List<String> ids, long duration, TimeUnit timeUnit) {
        if (list == null || ids == null || list.size() == 0 || ids.size() == 0)
            return null;
        return operateDbAsync(() -> {
            long expiresTime = duration == 0 ? 0 : System.currentTimeMillis() + timeUnit.toMillis(duration);
            String classTypeStr = StoreUtils.getClassTypeStr(list.get(0));
            List<DataWrap<String>> listStr = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                listStr.add(new DataWrap<>(ids.get(i)
                        , serializer.serial(list.get(i))
                        , expiresTime
                        , classTypeStr));
            }
            cacheDao.updateOrInsert(listStr);
            dao.updateOrInsert(listStr);
            dataUpdate(ids, list.get(0).getClass());
        });
    }

    @Override
    public <T> T load(Type type, String id) {
        if (type == null || id == null)
            return null;
        DataWrap<String> dataWrap = cacheDao.select(id);
        if (dataWrap == null) {
            dataWrap = dao.select(id);
            if (dataWrap != null)
                cacheDao.updateOrInsert(dataWrap);
        }
        if (dataWrap != null) {
            return serializer.unSerial(dataWrap.getData(), type);
        }
        return null;
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
        List<DataWrap<String>> dataWrapList = cacheDao.select(ids);
        if (dataWrapList.size() != ids.size()) {
            dataWrapList = dao.select(ids);
            cacheDao.updateOrInsert(dataWrapList);
        }
        return Stream.of(dataWrapList).map(data -> (T) serializer.unSerial(data.getData(), type))
                .toList();
    }

    @Override
    public <T> List<T> load(Type type) {
        if (type == null)
            return null;
        String typeStr = StoreUtils.getClassTypeStr(type);
        List<DataWrap<String>> dataWrapList = cacheDao.selectByTypeStr(typeStr);
        if (dataWrapList.size() == 0) {
            dataWrapList = dao.selectByTypeStr(typeStr);
            if (dataWrapList.size() != 0)
                cacheDao.updateOrInsert(dataWrapList);
        }
        return Stream.of(dataWrapList).map(data -> (T) serializer.unSerial(data.getData(), type))
                .toList();
    }


    @Override
    public void delete(Type type, String id) {
        if (type == null || id == null)
            return;
        cacheDao.delete(id);
        operateDbAsync(() -> {
            dao.delete(id);
            dataDelete(id, type);
        });

    }

    @Override
    public void deleteLike(String idLike) {
        if (idLike == null)
            return;
        cacheDao.deleteLike(idLike);
        dao.deleteLike(idLike);
    }

    @Override
    public void delete(Type type, List<String> ids) {
        if (type == null || ids == null || ids.size() == 0)
            return;
        cacheDao.delete(ids);
        operateDbAsync(() -> {
            dao.delete(ids);
            dataDelete(ids, type);
        });
    }


    @Override
    public boolean contains(String id) {
        if (id == null)
            return false;
        return cacheDao.contains(id) || dao.contains(id);
    }

    @Override
    public void clear() {
        cacheDao.clear();
        dao.clear();
    }

     static void init(Context context) {
        if (mDBStorage == null) {
            synchronized (DBStorage.class) {
                if (mDBStorage == null) {
                    mDBStorage = new DBStorage(context);
                }
            }
        }
    }


    protected static IStorage getInstance() {
        return mDBStorage;
    }



    @Override
    public void dataUpdate(String key, Type type) {

    }

    @Override
    public void dataUpdate(List<String> key, Type type) {

    }

    @Override
    public void dataDelete(String key, Type type) {

    }

    @Override
    public void dataDelete(List<String> key, Type type) {

    }
}
