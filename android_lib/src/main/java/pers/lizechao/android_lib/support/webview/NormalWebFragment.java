package pers.lizechao.android_lib.support.webview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import pers.lizechao.android_lib.R;
import pers.lizechao.android_lib.ui.widget.PageStateView;

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
 * Time: 19:45
 */
public class NormalWebFragment extends BaseWebFragment<pers.lizechao.android_lib.databinding.FragmentNormalWebBinding> {
    @Nullable
    private WebViewLoadCallBack webViewLoadCallBack;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WebViewLoadCallBack)
            webViewLoadCallBack = (WebViewLoadCallBack) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        webViewLoadCallBack = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initView();
        return view;
    }

    private void initView() {
        viewBind.parentView.setRefreshNotify(() -> {
            viewBind.parentView.setState(PageStateView.State.Loading);
            refreshUrl();
        });
    }

    @Override
    protected WebView getWebView() {
        return viewBind.webView;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_normal_web;
    }

    @Override
    public void onStartLoad(String path) {
        viewBind.parentView.setState(PageStateView.State.Loading);
        if (webViewLoadCallBack != null)
            webViewLoadCallBack.onStartLoad(path);
    }

    @Override
    public void onCompleteLoad(String path) {
        viewBind.parentView.setState(PageStateView.State.Normal);
        if (webViewLoadCallBack != null)
            webViewLoadCallBack.onCompleteLoad(path);
    }

    @Override
    public void onLoadError(@Nullable String path, @Nullable Throwable throwable) {
        viewBind.parentView.setState(PageStateView.State.Error);
        if (webViewLoadCallBack != null)
            webViewLoadCallBack.onLoadError(path, throwable);
    }

    @Override
    public void onHttpError(@Nullable String path, @Nullable Throwable throwable) {
        if (webViewLoadCallBack != null)
            webViewLoadCallBack.onHttpError(path, throwable);
    }

    @Override
    public void onSetting(WebView webView) {
        if (webViewLoadCallBack != null)
            webViewLoadCallBack.onSetting(webView);
    }

    @Override
    public void onTitle(String title) {
        super.onTitle(title);
        if (webViewLoadCallBack != null)
            webViewLoadCallBack.onTitle(title);
    }





}
