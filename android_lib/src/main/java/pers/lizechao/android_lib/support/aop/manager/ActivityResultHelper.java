package pers.lizechao.android_lib.support.aop.manager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;

import pers.lizechao.android_lib.common.DestroyListener;

/**
 * Created by Lzc on 2018/5/22 0022.
 */
public class ActivityResultHelper {
    private static final ActivityResultHelper helper = new ActivityResultHelper();
    private final SparseArray<CallBack> sparseArray = new SparseArray<>();

    private ActivityResultHelper() {
    }

    public interface CallBack {
        void onActivityResult(int requestCode, int resultCode, Intent data);

    }

    public static ActivityResultHelper getInstance() {
        return helper;
    }

    public void startActivityForResult(AppCompatActivity context, int requestCode, Intent intent, CallBack callBack) {
        context.getLifecycle().addObserver(new DestroyListener(() -> sparseArray.remove(requestCode)));
        sparseArray.append(requestCode, callBack);
        context.startActivityForResult(intent, requestCode);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CallBack callBack = sparseArray.get(requestCode);
        sparseArray.remove(requestCode);
        if (callBack == null)
            return;
        callBack.onActivityResult(requestCode, resultCode, data);
    }
}
