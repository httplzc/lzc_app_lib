package pers.lizechao.android_lib.support.img.load;

import android.net.Uri;
import android.view.View;

import java.io.File;

/**
 * Created by Lzc on 2018/6/20 0020.
 */
public interface IImgLoader<T extends View> {
    void loadImg(String url, T imageView, ImgLoadOption loadOption);

    void loadImg(String url, T imageView);

    void loadImg(Uri uri, T imageView, ImgLoadOption loadOption);

    void loadImg(Uri uri, T imageView);

    void loadImg(File file, T imageView, ImgLoadOption loadOption);

    void loadImg(File file, T imageView);

    void loadImg(int res, T imageView, ImgLoadOption loadOption);

    void loadImg(int res, T imageView);

    void clearCache();

    void clearCache(Uri uri);

    void clearCache(String path);

    void clearCache(File file);

    void setBaseUrl(String urlBase);

    String getBaseUrl();
}
