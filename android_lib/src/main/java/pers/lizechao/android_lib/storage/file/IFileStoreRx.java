package pers.lizechao.android_lib.storage.file;

import android.graphics.Bitmap;

import java.io.File;

import io.reactivex.Single;

/**
 * Created by Lzc on 2018/6/4 0004.
 */
public interface IFileStoreRx {

    Single<File> store(String key, byte[] data, Path path, FileStoreOption... openOption);

    Single<File> store(String key, String text, Path path, FileStoreOption... openOption);

    Single<File> store(String key, Bitmap bitmap, Path path, Bitmap.CompressFormat compressFormat, FileStoreOption... openOption);

    Single<Bitmap> loadBitmap(String key);

    Single<byte[]> loadBytes(String key);

    Single<String> loadStr(String key);
}
