package pers.lizechao.android_lib.storage.file;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by Lzc on 2018/6/4 0004.
 */
public interface IFileStoreSync{
    File store(String key, byte[] data, Path path, FileStoreOption... openOption);

    File store(String key, String text, Path path, FileStoreOption... openOption);

    File store(String key, Bitmap bitmap, Path path, Bitmap.CompressFormat compressFormat, FileStoreOption... openOption);

    Bitmap loadBitmap(String key);

    byte[] loadBytes(String key);

    String loadStr(String key);
}
