package com.yioks.lzclib.Data;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 手机屏幕信息数据获取类
 * <p/>
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
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 0.4f;
        activity.getWindow().setAttributes(lp);
    }


    public static void UpScreenColor(Activity activity) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 1f;
        activity.getWindow().setAttributes(lp);
    }

    public static int PxToDp(int px)
    {
        return (int) (px/ScreenData.density);
    }

    public static int DpToPx(int dp)
    {
        return (int) (dp*ScreenData.density);
    }

}
