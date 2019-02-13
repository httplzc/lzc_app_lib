package pers.lizechao.android_lib.ui.common;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import pers.lizechao.android_lib.support.aop.manager.PermissionHelper;

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
public abstract class BaseFragment<T extends ViewDataBinding> extends Fragment {
    protected T viewBind;
    protected final List<Disposable> disposables = new ArrayList<>();
    protected boolean requestDataInit = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBind = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        initArgumentsParams();
        initExtraView(viewBind.getRoot());
        if (requestDataInit)
            requestData(true);
        return viewBind.getRoot();
    }

    protected void requestData(boolean useCache) {

    }

    protected void initArgumentsParams() {

    }

    protected void initExtraView(View root) {

    }

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
    public void onDestroy() {
        super.onDestroy();
        if (viewBind != null)
            viewBind.unbind();
        for (Disposable disposable : disposables) {
            if (disposable != null && !disposable.isDisposed())
                disposable.dispose();
        }
    }
}
