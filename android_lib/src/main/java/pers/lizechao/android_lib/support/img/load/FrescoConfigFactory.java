package pers.lizechao.android_lib.support.img.load;

import android.content.Context;

import com.facebook.imagepipeline.core.ImagePipelineConfig;

import pers.lizechao.android_lib.ProjectConfig;

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
 * Time: 17:11
 */
public abstract class FrescoConfigFactory {
    public abstract ImagePipelineConfig createConfig(Context context);

    public static FrescoConfigFactory newInstance() {
        try {
            return ProjectConfig.getInstance().getFrescoConfigFactoryClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
