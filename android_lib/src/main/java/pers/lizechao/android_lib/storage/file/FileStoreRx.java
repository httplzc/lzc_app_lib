package pers.lizechao.android_lib.storage.file;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.io.File;

import io.reactivex.Single;
import io.reactivex.SingleObserver;

/**
 * Created with
 * #         ___                     ________                ________
 * #       |\  \                   |\_____  \              |\   ____\
 * #       \ \  \                   \|___/  /|             \ \  \___|
 * #        \ \  \                      /  / /              \ \  \
 * #         \ \  \____                /  /_/__              \ \  \____
 * #          \ \_______\             |\________\             \ \_______\
 * #           \|_______|              \|_______|              \|_______|
 * # 2018/6/19 0019.
 */
public class FileStoreRx implements IFileStoreRx {
    private final IFileStoreSync fileStoreSync;

    private FileStoreRx(IFileStoreSync fileStoreSync) {
        this.fileStoreSync = fileStoreSync;
    }


    private static IFileStoreRx sdFileStore;
    private static IFileStoreRx externalFileStore;
    private static IFileStoreRx innerFileStore;


    @NonNull
    public static IFileStoreRx getFileStoreRx(StoreMedium storeMedium) {
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
        sdFileStore = new FileStoreRx(FileStoreSync.getFileStoreSync(StoreMedium.SDCard));
        externalFileStore = new FileStoreRx(FileStoreSync.getFileStoreSync(StoreMedium.External));
        innerFileStore = new FileStoreRx(FileStoreSync.getFileStoreSync(StoreMedium.Private));
    }

    @Override
    public Single<File> store(String key, byte[] data, Path path, FileStoreOption... openOption) {
        return new Single<File>() {
            @Override
            protected void subscribeActual(SingleObserver<? super File> observer) {
                observer.onSuccess(fileStoreSync.store(key, data, path, openOption));
            }
        };
    }

    @Override
    public Single<File> store(String key, String text, Path path, FileStoreOption... openOption) {
        return new Single<File>() {
            @Override
            protected void subscribeActual(SingleObserver<? super File> observer) {
                observer.onSuccess(fileStoreSync.store(key, text, path, openOption));
            }
        };
    }

    @Override
    public Single<File> store(String key, Bitmap bitmap, Path path, Bitmap.CompressFormat compressFormat, FileStoreOption... openOption) {
        return new Single<File>() {
            @Override
            protected void subscribeActual(SingleObserver<? super File> observer) {
                observer.onSuccess(fileStoreSync.store(key, bitmap, path, compressFormat, openOption));
            }
        };
    }

    @Override
    public Single<Bitmap> loadBitmap(String key) {
        return new Single<Bitmap>() {
            @Override
            protected void subscribeActual(SingleObserver<? super Bitmap> observer) {
                observer.onSuccess(fileStoreSync.loadBitmap(key));
            }
        };
    }

    @Override
    public Single<byte[]> loadBytes(String key) {
        return new Single<byte[]>() {
            @Override
            protected void subscribeActual(SingleObserver<? super byte[]> observer) {
                observer.onSuccess(fileStoreSync.loadBytes(key));
            }
        };
    }

    @Override
    public Single<String> loadStr(String key) {
        return new Single<String>() {
            @Override
            protected void subscribeActual(SingleObserver<? super String> observer) {
                observer.onSuccess(fileStoreSync.loadStr(key));
            }
        };
    }
}
