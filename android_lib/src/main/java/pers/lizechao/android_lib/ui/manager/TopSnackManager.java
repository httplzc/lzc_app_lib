package pers.lizechao.android_lib.ui.manager;

import android.app.Activity;

import com.annimon.stream.Optional;
import pers.lizechao.android_lib.ui.widget.TopSnackView;

import java.lang.ref.WeakReference;

/**
 * Created by Lzc on 2018/4/23 0023.
 */

public class TopSnackManager {
    private static final TopSnackManager ourInstance = new TopSnackManager();
    private WeakReference<TopSnackView> topSnackViewRef;
    private String msgString;

    public static TopSnackManager getInstance() {
        return ourInstance;
    }

    private TopSnackManager() {

    }

    public void cancelSnack() {
        TopSnackView topSnackView = Optional.ofNullable(topSnackViewRef).map(WeakReference::get).orElse(null);
        if (topSnackView != null)
            topSnackView.removeView();
    }


    public void showSnack(Activity context, String msg) {
        if (context == null || msg == null)
            return;
        TopSnackView topSnackView = Optional.ofNullable(topSnackViewRef).map(WeakReference::get).orElse(null);
        if (topSnackView == null || !msg.equals(msgString)) {
            if (topSnackView != null)
                topSnackView.removeView();
            topSnackView = TopSnackView.make(context, msg);
            topSnackViewRef = new WeakReference<>(topSnackView);
            msgString = msg;
            topSnackView.show();
        } else {
            topSnackView.setMessage(msg);
            topSnackView.show();
        }

    }
}
