package pers.lizechao.android_lib.storage.file;

import android.support.annotation.Nullable;

import java.io.File;

/**
 * Created by Lzc on 2018/6/5 0005.
 */
public interface IFileStore {

    //获取文件
    @Nullable
    File getFile(String key);

    //获取文件，并创建
    File createFile(String key, Path path, FileStoreOption... fileStoreOptions);
    //获取文件，并创建
    File createFile(Path path, FileStoreOption... fileStoreOptions);

    //删除文件
    void deleteFile(String key);
}
