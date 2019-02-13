package pers.lizechao.android_lib.ui.manager;

import android.support.v4.util.ObjectsCompat;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.annimon.stream.Optional;

import java.lang.ref.WeakReference;

import pers.lizechao.android_lib.R;
import pers.lizechao.android_lib.data.ApplicationData;

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
 * Date: 2018-07-02
 * Time: 14:21
 */
public class ToastManager {
    private static final ToastManager instance = new ToastManager();
    private String toastString;
    private long lastTime;
    private long lastDurationTime;
    private WeakReference<Toast> mToastRef;

    private static final int LONG_DELAY = 3500; // 3.5 seconds

    private static final int SHORT_DELAY = 2000; // 2 seconds


    public static ToastManager getInstance() {
        return instance;
    }

    public void showToast(String str) {
        showToast(str, Toast.LENGTH_SHORT);
    }

    public void showToastLong(String str) {
        showToast(str, Toast.LENGTH_LONG);
    }


    private void showToast8(String str, int time) {
        if (str == null)
            return;
        Toast mToast = Toast.makeText(ApplicationData.applicationContext, str, time);
        mToastRef = new WeakReference<>(mToast);
        toastString = str;
        lastTime = System.currentTimeMillis();
        lastDurationTime = (time == Toast.LENGTH_SHORT ? SHORT_DELAY : LONG_DELAY);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastView = (LinearLayout) mToast.getView();
        ImageView imageCodeProject = new ImageView(ApplicationData.applicationContext);
        imageCodeProject.setImageResource(R.drawable.warning_bai);
        toastView.addView(imageCodeProject, 0);
        mToast.show();
    }

    private void showToast(String str, int time) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            showToast8(str, time);
//            return;
//        }
        if (str == null)
            return;
        Toast mToast = Optional.ofNullable(mToastRef).map(WeakReference::get).orElse(null);
        if (mToast == null || System.currentTimeMillis() - lastTime > lastDurationTime || !ObjectsCompat.equals(toastString, str)) {
            mToast = Toast.makeText(ApplicationData.applicationContext, str, time);
            mToastRef = new WeakReference<>(mToast);
            toastString = str;
            lastTime = System.currentTimeMillis();
            lastDurationTime = (time == Toast.LENGTH_SHORT ? SHORT_DELAY : LONG_DELAY);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            LinearLayout toastView = (LinearLayout) mToast.getView();
            ImageView imageCodeProject = new ImageView(ApplicationData.applicationContext);
            imageCodeProject.setImageResource(R.drawable.warning_bai);
            toastView.addView(imageCodeProject, 0);
            mToast.show();
        } else {
            mToast.show();
        }
    }

    public void cancelToast() {
        Toast mToast = Optional.ofNullable(mToastRef).map(WeakReference::get).orElse(null);
        if (mToast != null) {
            mToast.cancel();
            mToastRef = null;
        }

    }

}
