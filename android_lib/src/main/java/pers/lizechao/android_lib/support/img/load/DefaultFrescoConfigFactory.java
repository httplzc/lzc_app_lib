package pers.lizechao.android_lib.support.img.load;

import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import pers.lizechao.android_lib.net.okhttp.OkHttpInstance;

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
 * Date: 2018-08-06
 * Time: 17:18
 */
public class DefaultFrescoConfigFactory extends FrescoConfigFactory {
    @Override
    public ImagePipelineConfig createConfig(Context context) {
        return OkHttpImagePipelineConfigFactory.newBuilder(context, OkHttpInstance.getClient())
          .setDownsampleEnabled(true)
          .setResizeAndRotateEnabledForNetwork(true)
          .setBitmapsConfig(Bitmap.Config.RGB_565)
          .build();
    }
}
