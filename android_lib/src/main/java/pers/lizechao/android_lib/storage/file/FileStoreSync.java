package pers.lizechao.android_lib.storage.file;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Lzc on 2018/6/19 0019.
 */
public class FileStoreSync implements IFileStoreSync {
    @NonNull
    private final IFileStore fileStore;

    private static IFileStoreSync sdFileStore;
    private static IFileStoreSync externalFileStore;
    private static IFileStoreSync innerFileStore;


    @NonNull
    public static IFileStoreSync getFileStoreSync(StoreMedium storeMedium) {
        switch (storeMedium) {
            case SDCard:
                return sdFileStore;
            case External:
                return externalFileStore;
            case Private:
                return innerFileStore;
        }
        return externalFileStore;
    }

    public static void init() {
        sdFileStore = new FileStoreSync(FileStore.getFileStore(StoreMedium.SDCard));
        externalFileStore = new FileStoreSync(FileStore.getFileStore(StoreMedium.External));
        innerFileStore = new FileStoreSync(FileStore.getFileStore(StoreMedium.Private));
    }

    private FileStoreSync(@NonNull IFileStore fileStore) {
        this.fileStore = fileStore;
    }

    @Override
    public File store(String key, byte[] data, Path path, FileStoreOption... openOption) {
        List<FileStoreOption> storeOptions = Arrays.asList(openOption);
        try {
            File file = fileStore.createFile(key, path, openOption);
            if (file != null) {
                FileStoreUtil.saveByteData(file, data, storeOptions.contains(FileStoreOption.Append));
                return file;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public File store(String key, String text, Path path, FileStoreOption... openOption) {
        List<FileStoreOption> storeOptions = Arrays.asList(openOption);
        try {
            File file = fileStore.createFile(key, path, openOption);
            if (file != null) {
                FileStoreUtil.saveTextData(file, text, storeOptions.contains(FileStoreOption.Append));
                return file;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public File store(String key, Bitmap bitmap, Path path, Bitmap.CompressFormat compressFormat, FileStoreOption... openOption) {
        List<FileStoreOption> storeOptions = Arrays.asList(openOption);
        try {
            File file = fileStore.createFile(key, path, openOption);
            if (file != null) {
                FileStoreUtil.saveBitmap(file, bitmap, compressFormat);
                return file;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Bitmap loadBitmap(String key) {
        return FileStoreUtil.loadBitmap(fileStore.getFile(key));
    }

    @Override
    public byte[] loadBytes(String key) {
        try {
            return FileStoreUtil.loadBytes(fileStore.getFile(key));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String loadStr(String key) {
        try {
            return FileStoreUtil.loadStr(fileStore.getFile(key));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
