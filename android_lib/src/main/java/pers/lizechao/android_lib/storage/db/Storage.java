package pers.lizechao.android_lib.storage.db;

import android.content.Context;

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
 * Time: 10:42
 */
public class Storage {
    public static void init(Context context) {
        DBStorage.init(context);
    }

    public static IStorage getDBInstance() {
        return DBStorage.getInstance();
    }

    public static IStorage getStaticInstance() {
        return StaticStorage.getInstance();
    }

    public static IStorage getIPCDbInstance() {
        return DBStorageIPC.getInstance();
    }

}
