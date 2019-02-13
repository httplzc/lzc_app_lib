package pers.lizechao.android_lib.ui.manager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import pers.lizechao.android_lib.BuildConfig;
import pers.lizechao.android_lib.R;

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
 * Date: 2018-08-02
 * Time: 9:10
 */
public class StatusBarManager {
    private final Activity activity;
    //状态栏颜色
    private final int statusBarColor;

    //用于api 19以下 浸染式状态栏
    private View statusBarView;
    //状态栏颜色是否为深色
    private boolean statusBarTextDeep;
    //当前状态
    private BarState barState;

    public enum BarState {Normal, Full, Transparency, TransparencyAll}

    public StatusBarManager(Activity activity) {
        this.activity = activity;
        statusBarColor = R.color.starBarColor;
    }


    public void setWindowState(BarState state) {
        if (barState == state)
            return;
        //去除老状态
        if (barState != null) {
            switch (barState) {
                case Normal:
                    setSolidColorState(false);
                    break;
                case Full:
                    setWindowFull(false);
                    break;
                case Transparency:
                case TransparencyAll:
                    setWindowTrans(false, false);
                    break;
            }
        }
        //应用新状态
        switch (state) {
            case Normal:
                setSolidColorState(true);
                break;
            case Full:
                setWindowFull(true);
                break;
            case Transparency:
                setWindowTrans(true, false);
                break;
            case TransparencyAll:
                setWindowTrans(true, true);
                break;
        }
        this.barState = state;
    }


    /**
     * 获得状态栏高度
     */
    public int getStatusBarHeight() {
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return activity.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 设置为浸染模式
     *
     * @param enable 是否启用
     */
    private void setSolidColorState(boolean enable) {
        if (enable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setStatusBarColor(ContextCompat.getColor(activity, statusBarColor));
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //透明化头部
                setWindowTrans19(true, false);
                if (statusBarView == null) {
                    //创建一个View 放置到所有View的头部
                    statusBarView = new View(activity);
                    statusBarView.setBackgroundResource(statusBarColor);
                }
                ViewGroup contentLayout = activity.findViewById(android.R.id.content);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                  ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight());
                contentLayout.addView(statusBarView, lp);
                View contentChild = contentLayout.getChildAt(0);
                contentChild.setFitsSystemWindows(true);
            }
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (statusBarView != null) {
                        //移除头部View
                        ViewGroup contentLayout = activity.findViewById(android.R.id.content);
                        contentLayout.removeView(statusBarView);
                        View contentChild = contentLayout.getChildAt(0);
                        contentChild.setFitsSystemWindows(false);
                    }
                }
            }  //不处理

        }

    }


    /**
     * 设置为全屏
     *
     * @param full 是否全屏
     */
    private void setWindowFull(boolean full) {
        if (full) {
            removeSystemUI(View.SYSTEM_UI_FLAG_VISIBLE);
            addSystemUI(View.SYSTEM_UI_FLAG_FULLSCREEN, View.SYSTEM_UI_FLAG_HIDE_NAVIGATION, View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            removeWindowFlag(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            addWindowFlag(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            addSystemUI(View.SYSTEM_UI_FLAG_VISIBLE);
            removeSystemUI(View.SYSTEM_UI_FLAG_FULLSCREEN, View.SYSTEM_UI_FLAG_HIDE_NAVIGATION, View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            addWindowFlag(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            removeWindowFlag(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void setWindowTrans(boolean transHead, boolean tranBottom) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setWindowTrans21(transHead, tranBottom);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWindowTrans19(transHead, tranBottom);
        }
    }

    /**
     * 透明化 完全透明用于 api21以上
     *
     * @param transHead  是否透明状态栏
     * @param tranBottom 是否透明虚拟键
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setWindowTrans21(boolean transHead, boolean tranBottom) {
        if (transHead) {
            addSystemUI(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            addWindowFlag(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            setStatusBarColor(Color.TRANSPARENT);
        } else {
            removeWindowFlag(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            removeSystemUI(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (tranBottom) {
            addSystemUI(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        } else {
            removeSystemUI(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
    }


    /**
     * 透明化 api19 显示效果为半透明
     *
     * @param transHead  是否透明状态栏
     * @param tranBottom 是否透明虚拟键
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setWindowTrans19(boolean transHead, boolean tranBottom) {
        if (transHead)
            addWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        else
            removeWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        if (tranBottom)
            addWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        else
            removeWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(int color) {
        addWindowFlag(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        removeWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().setStatusBarColor(color);
    }

    //添加windowFlag
    private void addWindowFlag(int... flags) {
        int result = 0;
        for (int flag : flags) {
            result |= flag;
        }
        activity.getWindow().addFlags(result);
    }

    //移除windowFlag
    private void removeWindowFlag(int... flags) {
        int result = 0;
        for (int flag : flags) {
            result |= flag;
        }
        activity.getWindow().clearFlags(result);
    }

    //添加SystemUI flag
    private void addSystemUI(int... flags) {
        int originFlag = activity.getWindow().getDecorView().getSystemUiVisibility();
        for (int flag : flags) {
            originFlag |= flag;
        }
        activity.getWindow().getDecorView().setSystemUiVisibility(originFlag);
    }

    //移除SystemUI flag
    private void removeSystemUI(int... flags) {
        int originFlag = activity.getWindow().getDecorView().getSystemUiVisibility();
        for (int flag : flags) {
            originFlag &= ~flag;
        }
        activity.getWindow().getDecorView().setSystemUiVisibility(originFlag);
    }


    public void setStatusBarTextDeep(boolean statusBarTextDeep) {
        this.statusBarTextDeep = statusBarTextDeep;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (statusBarTextDeep) {
                addSystemUI(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                removeSystemUI(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }

        } else {
            Log.i(BuildConfig.LibTAG, "暂不支持此系统深色状态栏！");
        }
    }
}
