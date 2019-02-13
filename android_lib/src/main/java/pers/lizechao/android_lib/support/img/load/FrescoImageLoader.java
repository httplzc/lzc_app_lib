package pers.lizechao.android_lib.support.img.load;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;

/**
 * Created by Lzc on 2018/6/20 0020.
 */
public class FrescoImageLoader implements IImgLoader<SimpleDraweeView> {
    private String baseUrl;

    private String formatUrl(String url) {
        if (url != null && url.contains("http"))
            return url;
        else
            return baseUrl + url;
    }

    @Override
    public void loadImg(String url, SimpleDraweeView imageView, @Nullable ImgLoadOption loadOption) {
        loadImg(FrescoUtils.pathToUri(formatUrl(url)), imageView, loadOption);
    }

    @Override
    public void loadImg(String url, SimpleDraweeView imageView) {
        loadImg(FrescoUtils.pathToUri(formatUrl(url)), imageView, null);
    }

    @Override
    public void loadImg(Uri uri, SimpleDraweeView imageView, @Nullable ImgLoadOption loadOption) {
        AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
          .setOldController(imageView.getController())
          .setImageRequest(getRequest(uri, loadOption))
          .build();
        imageView.setHierarchy(getHierarchy(imageView, loadOption));
        imageView.setController(controller);
    }

    @Override
    public void loadImg(Uri uri, SimpleDraweeView imageView) {
        loadImg(uri, imageView, null);
    }

    @Override
    public void loadImg(File file, SimpleDraweeView imageView, @Nullable ImgLoadOption loadOption) {
        loadImg(FrescoUtils.fileToUri(file), imageView, loadOption);
    }

    @Override
    public void loadImg(File file, SimpleDraweeView imageView) {
        loadImg(FrescoUtils.fileToUri(file), imageView, null);
    }

    @Override
    public void loadImg(int res, SimpleDraweeView imageView, ImgLoadOption loadOption) {
        loadImg(FrescoUtils.resToUriFresco(res), imageView, loadOption);
    }

    @Override
    public void loadImg(int res, SimpleDraweeView imageView) {
        loadImg(FrescoUtils.resToUriFresco(res), imageView, null);
    }

    @Override
    public void clearCache() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearCaches();
        imagePipeline.clearDiskCaches();
        imagePipeline.clearMemoryCaches();
    }

    @Override
    public void clearCache(Uri uri) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromCache(uri);
        imagePipeline.evictFromDiskCache(uri);
        imagePipeline.evictFromMemoryCache(uri);
    }

    @Override
    public void clearCache(String path) {
        clearCache(FrescoUtils.pathToUri(path));
    }

    @Override
    public void clearCache(File file) {
        clearCache(FrescoUtils.fileToUri(file));
    }

    @Override
    public void setBaseUrl(String urlBase) {
        this.baseUrl = urlBase;
    }

    private ImageRequest getRequest(Uri uri, @Nullable ImgLoadOption loadOption) {
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);
        builder.setImageDecodeOptions(ImageDecodeOptions.newBuilder().setBitmapConfig(Bitmap.Config.RGB_565).build());
        if (loadOption != null) {
            if (loadOption.getWidth() != -1 && loadOption.getHeight() != -1) {
                builder.setResizeOptions(new ResizeOptions(loadOption.getWidth(), loadOption.getHeight()));
            }
            if (loadOption.getRotate() != -1) {
                builder.setRotationOptions(RotationOptions.forceRotation(loadOption.getRotate()));
            }
            if (loadOption.isNoCache()) {
                clearCache(uri);
            }
        }
        return builder.build();
    }


    private GenericDraweeHierarchy getHierarchy(SimpleDraweeView imageView, @Nullable ImgLoadOption loadOption) {
        GenericDraweeHierarchy hierarchyOld = imageView.getHierarchy();
        if (loadOption != null) {
            if (loadOption.getFailResId() != -1) {
                hierarchyOld.setFailureImage(loadOption.getFailResId());
            }
            if (loadOption.getPlaceHolderResId() != -1) {
                hierarchyOld.setPlaceholderImage(loadOption.getPlaceHolderResId());
            }
        }
        return hierarchyOld;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
