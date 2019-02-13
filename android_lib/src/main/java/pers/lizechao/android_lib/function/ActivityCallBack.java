package pers.lizechao.android_lib.function;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by Lzc on 2018/6/22 0022.
 */
public interface ActivityCallBack {
    void onNewIntent(Intent intent);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onCreate(Activity activity);

    void onDestroy(Activity activity);
}
