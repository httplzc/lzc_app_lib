package pers.lizechao.android_lib.storage.file;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import io.reactivex.schedulers.Schedulers;
import pers.lizechao.android_lib.storage.db.Storage;

/**
 * Created by Lzc on 2018/6/1 0001.
 */
public class FileStore implements IFileStore {
    private final Path rootPath;
    private final static String FileStoreTag = "FileStoreTag";

    private static IFileStore sdFileStore;
    private static IFileStore externalFileStore;
    private static IFileStore innerFileStore;

    @NonNull
    public static IFileStore getFileStore(StoreMedium storeMedium) {
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

    public static void init(Context context) {
        sdFileStore = new FileStore(context, StoreMedium.SDCard);
        externalFileStore = new FileStore(context, StoreMedium.External);
        innerFileStore = new FileStore(context, StoreMedium.Private);
    }


    private FileStore(Context context, StoreMedium storeMedium) {
        rootPath = Path.parse(getRootStore(context, storeMedium).getPath());
    }

    private File getRootStore(Context context, StoreMedium storeMedium) {
        File rootDir = null;
        switch (storeMedium) {
            case SDCard:
                rootDir = Environment.getExternalStorageDirectory();
                break;
            case External:
                rootDir = context.getExternalFilesDir(null);
                break;
            case Private:
                rootDir = context.getFilesDir();
                break;
        }
        return rootDir;
    }


    private void saveFileDb(String key, File file) {
        Storage.getDBInstance().store(file.getPath(), key + FileStoreTag);
    }

    private File getFileDb(String key) {
        String path = Storage.getDBInstance().load(String.class, key + FileStoreTag);
        File file = new File(path);
        if (!file.exists())
            return null;
        return file;
    }

    private File mkDir(Path path) {
        if (path == null)
            return null;
        File dir = path.toFile();
        if (!dir.exists()) {
            synchronized (FileStore.class) {
                if (!dir.exists())
                    dir.mkdirs();
            }
        }
        return dir;
    }

    @Override
    public File getFile(String key) {
        return getFileDb(key);
    }

    /**
     *
     * @param key
     * @param path 若path 不是
     * @param openOption
     * @return
     */
    @Override
    public File createFile(String key, Path path, FileStoreOption... openOption) {
        List<FileStoreOption> storeOptions = Arrays.asList(openOption);
        try {
            File file = null;
            path = rootPath.add(path);
            if (path.isDirectory()) {
                file = new File(mkDir(path).getPath() + File.separator + UUID.randomUUID().toString() + key);
            } else {
                if (mkDir(path.getParent()) != null)
                    file = path.toFile();
            }
            if (file == null)
                return null;
            if (file.exists() && !storeOptions.contains(FileStoreOption.CreateNew))
                return file;
            file.delete();
            file.createNewFile();
            saveFileDb(key, file);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public File createFile(Path path, FileStoreOption... fileStoreOptions) {
        List<FileStoreOption> storeOptions = Arrays.asList(fileStoreOptions);
        try {
            File file = null;
            path = rootPath.add(path);
            if (path.isDirectory()) {
                file = new File(mkDir(path).getPath() + File.separator + UUID.randomUUID().toString());
            } else {
                if (mkDir(path.getParent()) != null)
                    file = path.toFile();
            }
            if (file == null)
                return null;
            if (file.exists() && !storeOptions.contains(FileStoreOption.CreateNew))
                return file;
            file.delete();
            file.createNewFile();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deleteFile(String key) {
        File file = getFileDb(key);
        if (file != null)
            Schedulers.io().scheduleDirect(file::delete);
    }
}
