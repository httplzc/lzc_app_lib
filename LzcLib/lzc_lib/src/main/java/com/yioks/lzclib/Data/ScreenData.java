package com.yioks.lzclib.Data;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 手机屏幕信息数据获取类
 *
 * Created by asus-pc on 2016/3/26.
 */
public class ScreenData {
    public static float density; //像素密度
    public static int widthPX;   //宽度像素
    public static int heightPX;  //高度像素


    public static void init_srceen_data(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        //像素密度
        ScreenData.density = metric.density;
        //宽像素点
        ScreenData.widthPX = metric.widthPixels;
        // 高像素点
        ScreenData.heightPX = metric.heightPixels;
    }

    public static void DownScreenColor(Activity activity) {
        DownScreenColor(activity, 0.4f);
    }


    public static void UpScreenColor(Activity activity) {
        DownScreenColor(activity, 1f);
    }

    public static void DownScreenColor(Activity activity, float alpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = alpha;
        if (alpha == 1) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        activity.getWindow().setAttributes(lp);
    }


    public static int PxToDp(int px) {
        return (int) (px / ScreenData.density);
    }

    public static int DpToPx(int dp) {
        return (int) (dp * ScreenData.density);
    }

}
