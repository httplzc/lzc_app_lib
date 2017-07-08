package com.yioks.lzclib.Helper;

import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.yioks.lzclib.Untils.FileUntil;

/**
 * Created by ${User} on 2017/3/1 0001.
 */

public class LzcLibInit {
    public static void initApp(Context context)
    {
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(context)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(context,config);
        FileUntil.initFileUntil(context);
    }
}
