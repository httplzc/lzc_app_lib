package pers.lizechao.android_lib.support.img.load;

import android.graphics.Bitmap;
import android.net.Uri;

import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;

import io.reactivex.Single;
import io.reactivex.SingleObserver;

/**
 * Created by Lzc on 2018/6/29 0029.
 */
public class FrescoUtils {
    private static final String FrescoScheme = "res";

    public static Uri pathToUri(String path) {
        try {
            return Uri.parse(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Uri resToUriFresco(int res) {
        return new Uri.Builder()
                .scheme(FrescoScheme)
                .path(String.valueOf(res))
                .build();
    }

    public static Uri fileToUri(File file) {
        return Uri.fromFile(file);
    }

    private static Bitmap getBitmapFromCloseableRef(CloseableReference<CloseableImage> ref) {
        if (ref == null || !ref.isValid())
            return null;
        CloseableImage result = ref.get();
        if (result instanceof CloseableBitmap) {
            Bitmap bitmap = ((CloseableBitmap) result).getUnderlyingBitmap();
            return (bitmap != null && !bitmap.isRecycled()) ? bitmap : null;
        }
        return null;
    }

    public static Single<Bitmap> fetchBitmap(Uri uri) {
        return new Single<Bitmap>() {
            @Override
            protected void subscribeActual(SingleObserver<? super Bitmap> observer) {
                ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
                ImageRequest imageRequest = requestBuilder.build();
                DataSource<CloseableReference<CloseableImage>> dataSource = ImagePipelineFactory.getInstance().getImagePipeline()
                        .fetchDecodedImage(imageRequest, null);
                dataSource.subscribe(new BaseDataSubscriber<CloseableReference<CloseableImage>>() {
                    @Override
                    protected void onNewResultImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                        CloseableReference<CloseableImage> ref = dataSource.getResult();
                        Bitmap bitmap = getBitmapFromCloseableRef(ref);
                        CloseableReference.closeSafely(ref);
                        if (bitmap == null) {
                            observer.onError(dataSource.getFailureCause());
                        } else {
                            observer.onSuccess(bitmap);
                        }

                    }

                    @Override
                    protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                        observer.onError(dataSource.getFailureCause());
                    }
                }, UiThreadImmediateExecutorService.getInstance());
            }
        };
    }

}
