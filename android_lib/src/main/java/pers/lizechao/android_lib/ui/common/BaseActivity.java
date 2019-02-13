package pers.lizechao.android_lib.ui.common;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import pers.lizechao.android_lib.support.aop.manager.ActivityResultHelper;
import pers.lizechao.android_lib.support.aop.manager.PermissionHelper;
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
 * Date: 2018-08-12
 * Time: 10:58
 */
public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity {
    protected StatusBarManager statusBarManager;
    protected T viewBind;
    protected final List<Disposable> disposables = new ArrayList<>();
    protected AppCompatActivity activity;
    protected boolean requestDataInit = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        onSetContentBefore();
        initIntentParams();
        viewBind = DataBindingUtil.setContentView(this, getLayoutId());
        statusBarManager = createStatusBarManager();
        statusBarManager.setWindowState(getBarState());
        initExtraView();
        if (requestDataInit)
            requestData(true);
    }

    /**
     * 请求数据
     */
    protected void requestData(boolean useCache) {

    }

    /**
     * 初始化传入参数
     */
    protected void initIntentParams() {

    }

    /**
     * 初始化额外View
     */
    protected void initExtraView() {

    }

    protected StatusBarManager createStatusBarManager() {
        return new StatusBarManager(this);
    }

    protected void onSetContentBefore() {

    }

    protected abstract StatusBarManager.BarState getBarState();

    protected abstract int getLayoutId();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.getInstance().onPermissionBackDo(requestCode, permissions, grantResults);
    }

    public void addDisposable(Disposable disposable) {
        disposables.add(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewBind != null)
            viewBind.unbind();
        disposableAll();
    }

    protected void disposableAll() {
        for (Disposable disposable : disposables) {
            if (disposable != null && !disposable.isDisposed())
                disposable.dispose();
        }
    }

    public AppCompatActivity getActivity() {
        return activity;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResultHelper.getInstance().onActivityResult(requestCode, resultCode, data);
    }
}
