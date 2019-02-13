package pers.lizechao.android_lib.support.img.load;

import android.support.annotation.NonNull;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by Lzc on 2018/6/20 0020.
 */
public class LoadImgManager {
    private static final LoadImgManager instance = new LoadImgManager();
    private final FrescoImageLoader frescoImageLoader;

    private LoadImgManager() {
        frescoImageLoader = new FrescoImageLoader();
    }

    public static IImgLoader<SimpleDraweeView> getImgLoad() {
        return instance.frescoImageLoader;
    }

    @NonNull
    public static <T extends View> IImgLoader<T> getImgLoad(Class<T> imageView) {
        if (imageView.isAssignableFrom(SimpleDraweeView.class))
            return (IImgLoader<T>) instance.frescoImageLoader;
        else
            return null;
    }

}
