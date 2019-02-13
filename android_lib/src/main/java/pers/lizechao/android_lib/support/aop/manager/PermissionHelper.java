package pers.lizechao.android_lib.support.aop.manager;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import pers.lizechao.android_lib.common.DestroyListener;

/**
 * Created by Lzc on 2018/5/22 0022.
 */
public class PermissionHelper {
    private static final PermissionHelper helper = new PermissionHelper();
    private final SparseArray<CallBack> sparseArray = new SparseArray<>();

    private PermissionHelper() {
    }

    public interface CallBack {
        void succeed();

        void fail(String[] permissions);
    }

    public static PermissionHelper getInstance() {
        return helper;
    }

    public  void request(FragmentActivity context, int requestCode, String[] permissions, CallBack callBack) {
        context.getLifecycle().addObserver(new DestroyListener(() -> sparseArray.remove(requestCode)));
        sparseArray.append(requestCode, callBack);
        List<String> requestStr = new ArrayList<>();
        for (String s : permissions) {
            if (ContextCompat.checkSelfPermission(context, s)
                    != PackageManager.PERMISSION_GRANTED) {
                requestStr.add(s);
            }
        }
        if (requestStr.size() == 0) {
            sparseArray.remove(requestCode);
            callBack.succeed();
            return;
        }
        String realPermission[] = new String[requestStr.size()];
        requestStr.toArray(realPermission);
        ActivityCompat.requestPermissions(context, realPermission, requestCode);
    }

    public  void onPermissionBackDo(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        CallBack callBack = sparseArray.get(requestCode);
        sparseArray.remove(requestCode);
        if (callBack == null)
            return;
        if (grantResults.length != 0) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                int grantResult = grantResults[i];
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    list.add(permissions[i]);
                }
            }
            if (list.size() == 0) {
                callBack.succeed();
            } else {
                String array[] = new String[list.size()];
                list.toArray(array);
                callBack.fail(array);
            }

        }
    }
}
