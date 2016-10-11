package com.yioks.lzclib.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yioks.lzclib.R;
import com.yioks.lzclib.View.ParentView;


/**
 * Created by ${User} on 2016/8/18 0018.
 */
public class WebActivity extends TitleBaseActivity {
    private WebView webView;
    private ParentView parentView;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        setTitleState();
        initView();
        url = getIntent().getStringExtra("url");
        loadData();
    }

    private void initView() {
        String title = getIntent().getStringExtra("title");
        if (title == null) {
            title = "";
        }
        bindTitle(true, title, -1);
        webView = (WebView) findViewById(R.id.web_view);
        parentView = (ParentView) findViewById(R.id.parent_view);

        parentView.setReFreshDataListener(new ParentView.ReFreshDataListener() {
            @Override
            public void refreshData() {
                parentView.setstaus(ParentView.Staus.Loading);
                loadData();
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                parentView.setProgressNoAnim(newProgress);
                super.onProgressChanged(view, newProgress);
            }
        });
      //  webView.addJavascriptInterface(this, "call_android");
    }


    public void loadData() {
        if (url != null && !url.equals("")) {
            webView.loadUrl(url);
        }
    }

    public class MyWebViewClient extends WebViewClient {

        private boolean error = false;

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
            //   Log.i("lzc", "-MyWebViewClient->onPageStarted()--");
            error = false;
            super.onPageStarted(view, url, favicon);
            parentView.setstaus(ParentView.Staus.Loading);
        }

        public void onPageFinished(WebView view, String url) {
            //  Log.i("lzc", "-MyWebViewClient->onPageFinished()--");
            super.onPageFinished(view, url);
            if (!error) {
                parentView.setstaus(ParentView.Staus.Normal);
            }

        }


        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            error = true;
            parentView.setstaus(ParentView.Staus.Error);
            //    Log.i("lzc", "-MyWebViewClient->onReceivedError()--\n errorCode=" + errorCode + " \ndescription=" + description + " \nfailingUrl=" + failingUrl);

        }

    }
}
