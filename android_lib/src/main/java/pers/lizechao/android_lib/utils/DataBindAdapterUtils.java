package pers.lizechao.android_lib.utils;

import android.databinding.BindingAdapter;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Objects;

import pers.lizechao.android_lib.support.img.load.ImgLoadOption;
import pers.lizechao.android_lib.support.img.load.LoadImgManager;
import pers.lizechao.android_lib.ui.widget.WrapImageView;

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
 * Date: 2018-08-03
 * Time: 15:09
 */
public class DataBindAdapterUtils {
    @BindingAdapter({"android:src"})
    public static void loadImage(ImageView imageView, int src) {
        imageView.setImageResource(src);
    }


    @BindingAdapter(value = {"imageUrl","noCache"}, requireAll = false)
    public static void loadImage(WrapImageView wrapImageView, String url, boolean noCache) {
        if (wrapImageView == null)
            return;
        int width = wrapImageView.getWidth();
        int height = wrapImageView.getHeight();
        if (width > 0 && height > 0) {
            LoadImgManager.getImgLoad().loadImg(url, wrapImageView, new ImgLoadOption.Builder()
              .size(width, height).noCache(noCache).build());
        } else
            LoadImgManager.getImgLoad().loadImg(url, wrapImageView, new ImgLoadOption.Builder().noCache(noCache).build());
    }


    @BindingAdapter(value = {"imageUri"}, requireAll = false)
    public static void loadImage(WrapImageView wrapImageView, Uri uri) {
        long start = System.currentTimeMillis();
        if (wrapImageView == null)
            return;
        int width = wrapImageView.getWidth();
        int height = wrapImageView.getHeight();
        if (width > 0 && height > 0)
            LoadImgManager.getImgLoad().loadImg(uri, wrapImageView, new ImgLoadOption(width, height));
        else
            LoadImgManager.getImgLoad().loadImg(uri, wrapImageView);
        Log.i("lzc", "load_image_time    " + (System.currentTimeMillis() - start));
    }


    @BindingAdapter(value = {"imageResource"}, requireAll = false)
    public static void loadImage(WrapImageView wrapImageView, int resId) {
        if (wrapImageView == null)
            return;
        int width = wrapImageView.getWidth();
        int height = wrapImageView.getHeight();
        if (width > 0 && height > 0)
            LoadImgManager.getImgLoad().loadImg(resId, wrapImageView, new ImgLoadOption(width, height));
        else
            LoadImgManager.getImgLoad().loadImg(resId, wrapImageView);
    }

    @BindingAdapter(value = {"android:text"})
    public static void setText(TextView textView, String text) {
        if (!Objects.equals(text, textView.getText().toString()))
            textView.setText(StrUtils.CheckEmpty(text));
    }


}
