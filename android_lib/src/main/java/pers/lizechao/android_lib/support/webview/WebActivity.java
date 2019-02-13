package pers.lizechao.android_lib.support.webview;

import android.content.Context;

import pers.lizechao.android_lib.R;
import pers.lizechao.android_lib.ui.manager.StatusBarManager;

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
 * Date: 2018-09-10
 * Time: 16:14
 */
public class WebActivity extends NormalWebActivity<pers.lizechao.android_lib.databinding.ActivityNormalWebViewBinding> {

    @Override
    protected StatusBarManager.BarState getBarState() {
        return StatusBarManager.BarState.Normal;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_normal_web_view;
    }


    public static void showWebView(Context context, String title, String url) {
        NormalWebActivity.showWebView(context, title, url, WebActivity.class);
    }

    public static void showWebViewByRaw(Context context, String title, int rawId, String baseUrl) {
        NormalWebActivity.showWebViewByRaw(context, title, rawId, baseUrl, WebActivity.class);
    }

    public static void showWebViewByAsset(Context context, String title, String assetPath, String baseUrl) {
        NormalWebActivity.showWebViewByAsset(context, title, assetPath, baseUrl, WebActivity.class);
    }


}
