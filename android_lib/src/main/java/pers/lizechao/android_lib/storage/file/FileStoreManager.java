package pers.lizechao.android_lib.storage.file;

import android.content.Context;

/**
 * Created by Lzc on 2018/6/19 0019.
 */
public class FileStoreManager {

    public static IFileStore getFileStore(StoreMedium storeMedium) {
        return FileStore.getFileStore(storeMedium);
    }

    public static IFileStoreRx getRxFileStore(StoreMedium storeMedium) {
        return FileStoreRx.getFileStoreRx(storeMedium);
    }

    public static IFileStoreSync getSyncFileStore(StoreMedium storeMedium) {
        return FileStoreSync.getFileStoreSync(storeMedium);
    }


    public static void init(Context context) {
        FileStore.init(context);
        FileStoreSync.init();
        FileStoreRx.init();
    }

}
