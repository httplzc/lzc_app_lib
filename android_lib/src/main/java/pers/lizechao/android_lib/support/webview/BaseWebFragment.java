package pers.lizechao.android_lib.support.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.util.Objects;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import pers.lizechao.android_lib.BuildConfig;
import pers.lizechao.android_lib.R;
import pers.lizechao.android_lib.net.base.HttpMethod;
import pers.lizechao.android_lib.net.base.NetClient;
import pers.lizechao.android_lib.net.base.NetResult;
import pers.lizechao.android_lib.net.base.RequestData;
import pers.lizechao.android_lib.net.data.HttpCodeError;
import pers.lizechao.android_lib.net.data.NetError;
import pers.lizechao.android_lib.storage.file.FileStoreUtil;
import pers.lizechao.android_lib.support.webview.jsBridge.JsBridgeHelper;
import pers.lizechao.android_lib.support.webview.jsBridge.WebViewUntil;
import pers.lizechao.android_lib.ui.common.BaseFragment;
import pers.lizechao.android_lib.ui.layout.NormalDialog;

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
 * Time: 14:19
 */
public abstract class BaseWebFragment<T extends ViewDataBinding> extends BaseFragment<T> implements WebViewLoadCallBack {
    protected WebView webView;
    public JsBridgeHelper jsBridgeHelper;
    protected String title;
    private final static String TAG = "lzc_webView";


    @Override
    protected void initExtraView(View root) {
        super.initExtraView(root);
        webView = getWebView();
        jsBridgeHelper = new JsBridgeHelper(webView, getContext());
        initWebViewSetting();
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void initWebViewSetting() {
        //浏览器设置
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        //允许弹窗
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // 禁用 file 协议；
        webView.getSettings().setAllowFileAccess(false);
        webView.getSettings().setAllowFileAccessFromFileURLs(false);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(false);
        webView.getSettings().setUserAgentString(webView.getSettings().getUserAgentString() + "lzc");
        webView.setWebChromeClient(new DefaultWebViewChromeClient());
        webView.setWebViewClient(new DefaultWebViewClient());
        if (Build.VERSION.SDK_INT >= 19) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        onSetting(webView);
    }

    protected abstract WebView getWebView();

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webView.destroy();
    }

    public void stopLoadUrl() {
        if (webView != null)
            webView.stopLoading();
    }

    public void refreshUrl() {
        if (webView != null)
            webView.reload();
    }

    public boolean goBackLast() {
        if (webView != null) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return false;
    }

    public boolean goForward() {
        if (webView != null) {
            if (webView.canGoForward()) {
                webView.goForward();
                return true;
            }
        }
        return false;
    }

    private void loadLocalData(@Nullable String baseUrl, String html) {
        if (webView != null)
            webView.loadDataWithBaseURL(baseUrl, html, "text/html", "utf-8", null);
    }

    private void loadData(String url) {
        if (webView != null) {
            Log.i(BuildConfig.LibTAG, "webView load: " + url);
            webView.loadUrl(url);
        }
    }


    public void onStartLoad(String path) {
    }

    public void onCompleteLoad(String path) {

    }

    public void onLoadError(@Nullable String path, @Nullable Throwable throwable) {
        Log.i(TAG, "receivedError url=" + path + "-----" + throwable);
    }

    public void onHttpError(@Nullable String path, @Nullable Throwable throwable) {
        Log.i(TAG, "httpError url=" + path + "----" + throwable);
    }

    @Override
    public void onSetting(WebView webView) {

    }

    protected void onProgress(int newProgress) {

    }

    protected boolean onThrowNewUri(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        if (getActivity() == null || intent.resolveActivity(getActivity().getPackageManager()) == null)
            return false;
        getActivity().startActivity(intent);
        return true;
    }

    protected boolean onShouldOverrideUrl(Uri uri) {
        if (Objects.equals(uri.getScheme(), "http") || Objects.equals(uri.getScheme(), "https")) {
            webView.loadUrl(uri.toString());
            return true;
        }
        return false;
    }

    protected WebResourceResponse interceptRequest(String url) {
        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requireActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);
        WebViewModel webViewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory()).get("webModel", WebViewModel.class);
        webViewModel.getHtmlLiveData().observe(this, localHtml -> {
            if (localHtml != null)
                loadLocalData(localHtml.baseUrl, localHtml.html);
        });
        webViewModel.getUrl().observe(this, s -> {
            if (s != null)
                loadData(s);
        });
    }

    @Override
    public void onTitle(String title) {
//        Log.i(TAG, "title:" + title);
//        Log.i(TAG, "title:" + title);
//        Log.i(TAG, "title:" + title);
    }


    //内部类与接口分界线
    //--------------------------------------------------------------------------------------------------------------------------------

    /**
     * 用于处理基本浏览器交互
     */
    protected class DefaultWebViewChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            onProgress(newProgress);
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }


        @Override
        public void onReceivedTitle(WebView view, String title) {
            BaseWebFragment.this.title = title;
            onTitle(title);
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.i(TAG, consoleMessage.message());
            return super.onConsoleMessage(consoleMessage);
        }


        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            new NormalDialog.Builder(getActivity())
                    .message(message)
                    .canBackCancel(true)
                    .canTouchCancel(true)
                    .isConfirm(true)
                    .build().showDialog();
            result.confirm();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            new NormalDialog.Builder(getActivity())
                    .message(message)
                    .canBackCancel(true)
                    .canTouchCancel(true)
                    .isConfirm(true)
                    .build().showDialog();
            result.confirm();
            return true;
        }


        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
    }


    /**
     * 用于处理页面状态回调事件
     */
    protected class DefaultWebViewClient extends WebViewClient {
        protected final NetClient netClient;
        private String webBridgeJs;

        protected DefaultWebViewClient() {
            netClient = new NetClient.Builder().build();
            try {
                webBridgeJs = FileStoreUtil.loadStr(Objects.requireNonNull(getActivity()).getResources().openRawResource(R.raw.webview_javascript_bridge));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 处理ssl
         */
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            netClient
                    .newCall(new RequestData.Builder().url(error.getUrl()).method(HttpMethod.HEAD).build())
                    .execute().subscribe(new SingleObserver<NetResult>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onSuccess(NetResult netResult) {
                    if (netResult.isSuccessful())
                        handler.proceed();
                    else
                        handler.cancel();
                }

                @Override
                public void onError(Throwable e) {
                    handler.cancel();
                }
            });
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = null;
            try {
                uri = Uri.parse(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return onShouldOverrideUrlLoading(uri);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Uri uri = request.getUrl();
            return onShouldOverrideUrlLoading(uri);
        }


        private boolean onShouldOverrideUrlLoading(@Nullable Uri uri) {
            if (uri == null)
                return false;
            //js和交互
            if (jsBridgeHelper.onShouldOverrideUrlLoading(uri.toString()))
                return true;
            if (onShouldOverrideUrl(uri))
                return true;
            if (onThrowNewUri(uri))
                return true;
            return false;
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                WebResourceResponse response = interceptRequest(request.getUrl().toString());
                if (response != null)
                    return response;
            }
            return super.shouldInterceptRequest(view, request);
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                WebResourceResponse response = interceptRequest(url);
                if (response != null)
                    return response;
            }
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            jsBridgeHelper.onPageStart();
            onStartLoad(url);
            super.onPageStarted(view, url, favicon);
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            WebViewUntil.loadJavaScript(webView, webBridgeJs);
            onCompleteLoad(url);
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Throwable throwable = errorCode < 0 ? new NetError(new IllegalStateException(description))
                        : new HttpCodeError(errorCode);
                onLoadError(failingUrl, throwable);
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest webResourceRequest, WebResourceError error) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && webResourceRequest.isForMainFrame()) {
                Throwable throwable = error.getErrorCode() < 0 ? new NetError(new IllegalStateException(error.getDescription().toString()))
                        : new HttpCodeError(error.getErrorCode());
                onLoadError(webResourceRequest.getUrl().toString(), throwable);
            }
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest webResourceRequest, WebResourceResponse errorResponse) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                onHttpError(webResourceRequest.getUrl().toString(), new HttpCodeError(errorResponse.getStatusCode()));
            }
        }


    }

}
