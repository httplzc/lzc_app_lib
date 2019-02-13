package pers.lizechao.android_lib.ui.manager;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.WindowManager;

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
 * Date: 2018-07-31
 * Time: 9:38
 */
public class ScreenManager {
    private float density; //像素密度
    public int widthPX;   //宽度像素
    public int heightPX;  //高度像素

    public ScreenManager(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metric);
        initData(metric);
    }

    public ScreenManager(Context context) {
        this(context.getResources());
    }

    public ScreenManager(Resources resources) {
        DisplayMetrics metric = resources.getDisplayMetrics();
        initData(metric);
    }

    private void initData(DisplayMetrics metric) {
        density = metric.density;
        widthPX = metric.widthPixels;
        heightPX = metric.heightPixels;
    }

    public int PxToDp(float px) {
        return (int) (px / density);
    }

    public int DpToPx(float dp) {
        return (int) (dp * density);
    }

    public static void upLightScreen(Activity activity)
    {
        changeScreenAlpha(activity,1f);
    }
    public static void downLightScreen(Activity activity)
    {
        changeScreenAlpha(activity,0.4f);
    }

    public static void changeScreenAlpha(Activity activity, float alpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = alpha;
        if (alpha == 1) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        activity.getWindow().setAttributes(lp);
    }
}
