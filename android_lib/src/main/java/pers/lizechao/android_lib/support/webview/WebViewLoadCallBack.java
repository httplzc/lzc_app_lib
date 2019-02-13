package pers.lizechao.android_lib.support.webview;

import android.support.annotation.Nullable;
import android.webkit.WebView;

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
 * Date: 2018-08-27
 * Time: 21:00
 */
public interface WebViewLoadCallBack {
    void onStartLoad(String path);

    void onCompleteLoad(String path);

    void onLoadError(@Nullable String path, @Nullable Throwable throwable);

    void onHttpError(@Nullable String path, @Nullable Throwable throwable);

    void onSetting(WebView webView);

    void onTitle(String title);
}
