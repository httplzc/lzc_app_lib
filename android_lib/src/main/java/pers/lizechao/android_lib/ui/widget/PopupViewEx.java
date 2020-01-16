package pers.lizechao.android_lib.ui.widget;

import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import pers.lizechao.android_lib.R;
import pers.lizechao.android_lib.common.DestroyListener;
import pers.lizechao.android_lib.ui.manager.ScreenManager;

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
 * Date: 2018/9/28 0028
 * Time: 11:23
 */
public class PopupViewEx {
    protected PopupWindow origin;
    private OnShowListener onShowListener;
    protected FragmentActivity activity;
    private final boolean enableScreenDown;

    public interface OnShowListener
    {
        void onShow();
        void onDismiss();
    }

    protected PopupViewEx(PopupWindow popupWindow, FragmentActivity activity, boolean enableScreenDown,OnShowListener onShowListener) {
        this.origin = popupWindow;
        this.activity = activity;
        this.enableScreenDown = enableScreenDown;
        this.onShowListener=onShowListener;
        initContentView(popupWindow.getContentView());
        origin.setOnDismissListener(() -> {
            onDismissBefore();
            if (onShowListener != null)
                onShowListener.onDismiss();
        });
        activity.getLifecycle().addObserver(new DestroyListener(this::onParentDestroy));
    }

    private void onParentDestroy() {
        activity = null;
    }

    protected void initContentView(View contentView) {

    }

    private void onShowBefore() {
        if(onShowListener!=null)
            onShowListener.onShow();
        if (enableScreenDown)
            ScreenManager.downLightScreen(activity);
    }


    private void onDismissBefore() {
        if (enableScreenDown)
            ScreenManager.upLightScreen(activity);
    }

    /**
     * 显示底部弹窗
     */
    public void showInBottom() {
        onShowBefore();
        origin.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    /**
     * 显示底部弹窗
     */
    public void showInTop() {
        onShowBefore();
        origin.showAtLocation(activity.getWindow().getDecorView(), Gravity.TOP, 0, 0);
    }

    public void dismiss() {
        if (origin != null) {
            origin.dismiss();
        }
    }

    /**
     * 显示在之下
     */
    public void showAsDrop(View root) {
        onShowBefore();
        if (Build.VERSION.SDK_INT < 24) {
            origin.showAsDropDown(root);
        } else {
            int[] location = new int[2];
            root.getLocationOnScreen(location);
            origin.showAtLocation(root, Gravity.NO_GRAVITY, location[0], location[1] + root.getHeight());
        }
    }

    public void setOnShowListener(OnShowListener onShowListener) {
        this.onShowListener = onShowListener;
    }

    public static class Builder {
        private int animStyle = R.style.pop_window_anim_style_alpha;
        private boolean mOutsideTouchable = true;
        private boolean mFocusable = true;
        private View view;
        private int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        private int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        private boolean enableScreenDown = true;
        private OnShowListener onShowListener;

        public Builder setOnShowListener(OnShowListener onShowListener)
        {
            this.onShowListener=onShowListener;
            return this;
        }
        public Builder setAnimStyle(int animStyle) {
            this.animStyle = animStyle;
            return this;
        }

        public Builder setEnableScreenDown(boolean enableScreenDown) {
            this.enableScreenDown = enableScreenDown;
            return this;
        }

        public Builder setAnimStyleBottom() {
            this.animStyle = R.style.pop_window_anim_style_bottom;
            return this;
        }

        public Builder setAnimStyleTop() {
            this.animStyle = R.style.pop_window_anim_style_top;
            return this;
        }

        public Builder setOutsideTouchable(boolean mOutsideTouchable) {
            this.mOutsideTouchable = mOutsideTouchable;
            return this;
        }

        public Builder setFocusable(boolean mFocusable) {
            this.mFocusable = mFocusable;
            return this;
        }

        public Builder setView(View view) {
            this.view = view;
            return this;
        }

        public Builder setSize(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public PopupViewEx build(FragmentActivity activity) {
            PopupWindow popupWindow = new PopupWindow(view, width, height);
            popupWindow.setAnimationStyle(animStyle);
            popupWindow.setOutsideTouchable(mOutsideTouchable);
            popupWindow.setFocusable(mFocusable);
            view.setFocusable(mFocusable);
            view.setFocusableInTouchMode(mFocusable);
            return new PopupViewEx(popupWindow, activity, enableScreenDown,onShowListener);
        }
    }


}
