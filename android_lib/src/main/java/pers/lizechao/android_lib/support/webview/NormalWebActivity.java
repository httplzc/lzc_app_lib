package pers.lizechao.android_lib.support.webview;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.webkit.WebView;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pers.lizechao.android_lib.R;
import pers.lizechao.android_lib.storage.file.FileStoreUtil;
import pers.lizechao.android_lib.ui.common.BaseActivity;
import pers.lizechao.android_lib.ui.layout.TitleBarView;
import pers.lizechao.android_lib.utils.StrUtils;

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
 * Time: 18:58
 */
public abstract class NormalWebActivity<T extends ViewDataBinding> extends BaseActivity<T> implements WebViewLoadCallBack {
    protected String url;
    protected int rawId;
    protected String assetPath;
    protected String baseUrl;
    protected String title;
    protected BaseWebFragment fragment;
    protected WebViewModel webViewModel;


    @Override
    protected void initExtraView() {
        super.initExtraView();
        initView();
        webViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory())
          .get("webModel", WebViewModel.class);
    }

    @Override
    protected void requestData(boolean needCache) {
        if (url != null) {
            loadUrl(url);
        } else if (rawId != 0) {
            Disposable d = Single.create((SingleOnSubscribe<String>) emitter -> FileStoreUtil.loadStr(getResources().openRawResource(rawId)))
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(s -> loadUrl(baseUrl, s));
        } else if (assetPath != null) {
            Disposable d = Single.create((SingleOnSubscribe<String>) emitter -> FileStoreUtil.loadStr(getResources().getAssets().open(assetPath)))
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(s -> loadUrl(baseUrl, s));
        }
    }

    protected BaseWebFragment<?> getBaseWebFragment() {
        return new NormalWebFragment();
    }


    protected void loadUrl(String url) {
        webViewModel.getUrl().setValue(url);
    }

    protected void loadUrl(String baseUrl, String html) {
        webViewModel.getHtmlLiveData().setValue(new WebViewModel.LocalHtml(baseUrl, html));
    }



    private void initView() {
        TitleBarView titleBarView = findViewById(R.id.titleBarView);
        if (titleBarView != null) {
            titleBarView.setTitleData(true, StrUtils.CheckEmpty(title));
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragment = getBaseWebFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        fragmentTransaction.add(R.id.content, fragment);
        fragmentTransaction.commit();
    }

    public static void showWebView(Context context, String title, String url, Class tClass) {
        Intent intent = new Intent(context, tClass);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    public static void showWebViewByRaw(Context context, String title, int rawId, String baseUrl, Class tClass) {
        Intent intent = new Intent(context, tClass);
        intent.putExtra("rawId", rawId);
        intent.putExtra("baseUrl", baseUrl);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    public static void showWebViewByAsset(Context context, String title, String assetPath, String baseUrl, Class tClass) {
        Intent intent = new Intent(context, tClass);
        intent.putExtra("assetPath", assetPath);
        intent.putExtra("baseUrl", baseUrl);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }


    public void initIntentParams() {
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        url = intent.getStringExtra("url");
        rawId = intent.getIntExtra("rawId", 0);
        assetPath = intent.getStringExtra("assetPath");
        baseUrl = intent.getStringExtra("baseUrl");
    }


    @Override
    public void onStartLoad(String path) {

    }

    @Override
    public void onCompleteLoad(String path) {

    }

    @Override
    public void onLoadError(@Nullable String path, @Nullable Throwable throwable) {

    }

    @Override
    public void onHttpError(@Nullable String path, @Nullable Throwable throwable) {

    }

    @Override
    public void onSetting(WebView webView) {

    }

    @Override
    public void onTitle(String title) {

    }
}
