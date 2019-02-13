package pers.lizechao.android_lib.utils;

import android.app.Activity;

import pers.lizechao.android_lib.ui.manager.ToastManager;
import pers.lizechao.android_lib.ui.manager.TopSnackManager;
import pers.lizechao.android_lib.ui.manager.WaitViewManager;


/**
 * 对话框各种样式显示
 */
public class DialogUtil {
    /**
     * 加载提示框
     */
    public static void showDialog(Activity context, String content) {
        WaitViewManager.getInstance().showDialog(context, content, null);
    }

    /**
     * 加载提示框
     */
    public static void showDialog(Activity context, String content, Runnable cancelRun) {
        WaitViewManager.getInstance().showDialog(context, content, cancelRun);
    }

    /**
     * 加载提示框
     */
    public static void dismissDialog() {
        WaitViewManager.getInstance().dismissDialog();
    }


    public static void showTopSnack(Activity context, String str) {
        TopSnackManager.getInstance().showSnack(context, str);
    }

    public static void cancelTopSnack() {
        TopSnackManager.getInstance().cancelSnack();
    }


    /**
     * 自定义tosat显示
     *
     * @param str 显示的字符
     */
    public static void ShowToast(String str) {
        ToastManager.getInstance().showToast(str);
    }

    public static void ShowToastLong(String str) {
        ToastManager.getInstance().showToastLong(str);
    }


    public static void cancelToast() {
        ToastManager.getInstance().cancelToast();

    }


}
