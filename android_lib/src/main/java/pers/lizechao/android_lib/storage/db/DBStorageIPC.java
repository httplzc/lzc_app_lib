package pers.lizechao.android_lib.storage.db;

import android.content.Context;

import java.lang.reflect.Type;
import java.util.List;

import pers.lizechao.android_lib.support.protocol.broadcast.BroadcastStubReceiver;
import pers.lizechao.android_lib.support.protocol.broadcast.BroadcastStubSender;

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
 * Date: 2018-09-11
 * Time: 14:39
 */
 class DBStorageIPC extends DBStorage {
    private static DBStorageIPC dbStorageIPC = null;
    private IStoreDataChange iStoreDataChange;


    private DBStorageIPC(Context context) {
        super(context);
        initProcessDataChange(context);
    }

    //跨进程数据监听
    private void initProcessDataChange(Context context) {
        new BroadcastStubReceiver<>(context, IStoreDataChange.class,
          new IStoreDataChange() {
              @Override
              public void dataUpdate(String key, Type type) {
                  DataWrap<String>dataWrap = dao.select(key);
                  cacheDao.updateOrInsert(dataWrap);
              }

              @Override
              public void dataUpdate(List<String> key, Type type) {
                  List<DataWrap<String>>dataWrapList = dao.select(key);
                  cacheDao.updateOrInsert(dataWrapList);
              }

              @Override
              public void dataDelete(String key, Type type) {
                  cacheDao.delete(key);
              }

              @Override
              public void dataDelete(List<String> key, Type type) {
                  cacheDao.delete(key);
              }
          });
        iStoreDataChange = new BroadcastStubSender<>(context, IStoreDataChange.class).asInterface();
    }

    public static void init(Context context) {
        if (dbStorageIPC == null) {
            synchronized (DBStorage.class) {
                if (dbStorageIPC == null) {
                    dbStorageIPC = new DBStorageIPC(context);
                }
            }
        }
    }


    public static IStorage getInstance() {
        return dbStorageIPC;

    }

    @Override
    public void dataUpdate(String key, Type type) {
        iStoreDataChange.dataUpdate(key, type);
    }

    @Override
    public void dataUpdate(List<String> key, Type type) {
        iStoreDataChange.dataUpdate(key, type);
    }

    @Override
    public void dataDelete(String key, Type type) {
        iStoreDataChange.dataDelete(key, type);
    }

    @Override
    public void dataDelete(List<String> key, Type type) {
        iStoreDataChange.dataDelete(key, type);
    }
}
