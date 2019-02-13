package pers.lizechao.android_lib.storage.db;

import java.util.List;

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
 * Date: 2018/11/17 0017
 * Time: 9:49
 * 存储对象的json在内存中
 */
public class CacheJsonDao implements StoreDao<String> {
    private CacheBeanDao<String> cacheBeanDao;

    CacheJsonDao() {
        cacheBeanDao = new CacheBeanDao<>();
    }


    @Override
    public void updateOrInsert(DataWrap<String> dataWrap) {
        cacheBeanDao.updateOrInsert(dataWrap);
    }

    @Override
    public void updateOrInsert(List<? extends DataWrap<String>> data) {
        cacheBeanDao.updateOrInsert(data);
    }

    @Override
    public void delete(String key) {
        cacheBeanDao.delete(key);
    }

    @Override
    public void deleteLike(String keyLike) {
        cacheBeanDao.deleteLike(keyLike);
    }

    @Override
    public void delete(List<String> key) {
        cacheBeanDao.delete(key);
    }

    @Override
    public DataWrap<String> select(String key) {
        return cacheBeanDao.select(key);
    }

    @Override
    public List<DataWrap<String>> select(List<String> keys) {
        return cacheBeanDao.select(keys);
    }


    @Override
    public List<DataWrap<String>> selectByTypeStr(String typeStr) {
        return cacheBeanDao.selectByTypeStr(typeStr);
    }


    @Override
    public boolean contains(String key) {
        return cacheBeanDao.contains(key);
    }

    @Override
    public void clear() {
        cacheBeanDao.clear();
    }
}
