package com.yioks.lzclib.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yioks.lzclib.Data.BigImgShowData;
import com.yioks.lzclib.Data.WebViewJavascriptInterface;
import com.yioks.lzclib.R;
import com.yioks.lzclib.Untils.StringManagerUtil;
import com.yioks.lzclib.View.ParentView;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by ${User} on 2016/8/18 0018.
 */
public class WebActivity extends TitleBaseActivity {
    private WebView webView;
    private ParentView parentView;
    private Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        setTitleState();
        initIntentData();
        initView();
        loadData();
    }

    private void initIntentData() {
        Object o = getIntent().getSerializableExtra("data");
        if (o != null && o instanceof Data) {
            data = (Data) o;
        } else {
            data = new Data();
        }

    }

    public static class Data implements Serializable {
        public String url = "";
        public String title = "";
        private HashMap<String, WebViewJavascriptInterface> interfaceMap = new HashMap<>();
        public String html;
        public HashMap<String, Integer> cssMap = new HashMap<>();

        public Data(String url, String title) {
            this.url = url;
            this.title = title;
        }


        public Data() {
        }

        public void put(String name, WebViewJavascriptInterface interFace) {
            interfaceMap.put(name, interFace);
        }

        public HashMap<String, Integer> getCssMap() {
            return cssMap;
        }

        public void setCssMap(HashMap<String, Integer> cssMap) {
            this.cssMap = cssMap;
        }
    }


    public static void showWeb(Context context, Data data) {
        Intent intent = new Intent();
        intent.setClass(context, WebActivity.class);
        intent.putExtra("data", data);
        context.startActivity(intent);
    }

    private void initView() {
        bindTitle(true, data.title, -1);
        webView = (WebView) findViewById(R.id.web_view);
        parentView = (ParentView) findViewById(R.id.parent_view);

        parentView.setReFreshDataListener(new ParentView.ReFreshDataListener() {
            @Override
            public void refreshData() {
                parentView.setstaus(ParentView.Staus.Loading);
                loadData();
            }
        });

        //浏览器设置
        webView.getSettings().setJavaScriptEnabled(true);
        addInterface(webView);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                parentView.setProgressNoAnim(newProgress);
                Log.i("lzc", "newProgress" + newProgress);
                super.onProgressChanged(view, newProgress);
            }
        });
        webView.setDownloadListener(new DownLoadListener());
    }

    private void addInterface(WebView webView) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (data.interfaceMap != null) {
                for (Object entry : data.interfaceMap.entrySet()) {
                    Map.Entry<String, WebViewJavascriptInterface> objectEntry = (Map.Entry<String, WebViewJavascriptInterface>) entry;
                    WebViewJavascriptInterface anInterface = objectEntry.getValue();
                    anInterface.setContext(context);
                    webView.addJavascriptInterface(anInterface, objectEntry.getKey());
                }
            }
            webView.addJavascriptInterface(new OnClickImgListener(), "ImageClickListener");
        }
    }


    /**
     * 注入js函数监听
     */
    private void addImageClickListener() {
        webView.loadUrl("javascript:" + StringManagerUtil.getStringFromRaw(context, R.raw.show_bigimg));
    }


    private class OnClickImgListener {
        @JavascriptInterface
        public void onImgClick(String path) {
//            BigImgShowData bigImgShowData = new BigImgShowData();
//            bigImgShowData.setData(path);
//            ShowBigImgActivity.showBigImg(context, bigImgShowData);
        }
    }


    private class DownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }


    public void loadData() {
        Log.i("lzc", "loadUrl" + data.url);
        if (data.html != null) {
            //   String realHtmlData = "<link rel=\"stylesheet\" href=\"file:///android_asset/style.css\" type=\"text/css\" " + data.html;
            webView.loadDataWithBaseURL(null, data.html, "text/html", "utf-8", null);
        } else {
            if (data.url != null && !data.url.equals("")) {
                webView.loadUrl(data.url);
            } else {
                parentView.setstaus(ParentView.Staus.Error);
            }
        }

    }

    @Override
    protected void onDestroy() {
        webView.destroy();
        super.onDestroy();
    }

    public class MyWebViewClient extends WebViewClient {

        private boolean error = false;

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            Log.i("lzc", "onPageCommitVisible");
            super.onPageCommitVisible(view, url);

        }


        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                String last = url.substring(url.lastIndexOf("/"));
                if (last.contains("doc") || last.contains("txt") || last.contains("pdf") || last.contains("xlsx") || last.contains("docx") || last.contains("TXT")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            view.loadUrl(url);
            return true;
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.i("lzc", "onPageStarted");
            error = false;
            parentView.setstaus(ParentView.Staus.Loading);
            super.onPageStarted(view, url, favicon);
        }

        public void onPageFinished(WebView view, String url) {
            //  Log.i("lzc", "-MyWebViewClient->onPageFinished()--");
            Log.i("lzc", "onPageFinished " + url);
            addImageClickListener();
            if (data.cssMap.get(url) != null)
                addCss(data.cssMap.get(url));
            if (!error) {
                parentView.setstaus(ParentView.Staus.Normal);
            }
            super.onPageFinished(view, url);


        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.i("lzc", "onReceivedErrorOld " + errorCode + "    " + description + " --- " + failingUrl);
            //    this.error = true;
            //   parentView.setstaus(ParentView.Staus.Error);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            Log.i("lzc", "onReceivedError ");
            super.onReceivedError(view, request, error);

        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                Log.i("lzc", "onReceivedHttpError " + error + "---" + request.getUrl() + "---" + errorResponse.getStatusCode() + "---"
                        + errorResponse.getMimeType() + "---"+errorResponse.getReasonPhrase()+request.getUrl());
//            this.error = true;
//            parentView.setstaus(ParentView.Staus.Error);
            super.onReceivedHttpError(view, request, errorResponse);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            return super.shouldInterceptRequest(view, url);
        }
    }

    private void addCss(int cssId) {
        webView.loadUrl("JavaScript:(function() {" + "var parent = document.getElementsByTagName('head').item(0);"
                + "var style = document.createElement('style');"
                + "style.type = 'text/css';"
                + "style.innerHTML = window.atob('" + StringManagerUtil.getStringFromRaw(context, cssId) + "');"
                + "parent.appendChild(style)" + "})();");
    }

    @Override
    protected void onPause() {
        if (webView != null)
            webView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (webView != null)
            webView.onResume();
        super.onResume();
    }
}
