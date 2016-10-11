package com.yioks.lzclib.Untils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yioks.lzclib.Data.ScreenData;
import com.yioks.lzclib.R;

import java.util.Timer;
import java.util.TimerTask;


/**
 * 对话框各种样式显示
 * Created by Yioks-ZhangMengzhen on 2016/4/22.
 */
public class DialogUtil {

    public static ProgressDialog progressDialog;
    public static Context context;
    private static String ToastString = "";
    private static Toast mToast;
    private static Timer timer;

    /**
     * 加载提示框
     *
     * @param context
     * @param content
     */
    public static void showDialog(Context context, String content) {
        progressDialog = new ProgressDialog(context);
        DialogUtil.context = context;
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                progressDialog = null;
                HttpUtil.cancelAllClient(DialogUtil.context);
                DialogUtil.context = null;
            }
        });
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(content);
        progressDialog.show();

    }

    /**
     * 取消对话框显示
     */
    public static void dismissDialog() {
        try {
            if (progressDialog != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 自定义tosat显示
     *
     * @param context
     * @param str
     */
    public static void ShowToast(Context context, String str) {
        if (context == null) {
            return;
        }
        context = context.getApplicationContext();
        try {
            if (mToast == null) {
                mToast = Toast.makeText(context, str, Toast.LENGTH_SHORT);

                mToast.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout toastView = (LinearLayout) mToast.getView();
                ImageView imageCodeProject = new ImageView(context);
                imageCodeProject.setImageResource(R.drawable.warning_bai);
                imageCodeProject.setPadding(0, (int) (3 * ScreenData.density), 0, (int) (3 * ScreenData.density));
                toastView.addView(imageCodeProject, 0);

                ToastString = str;
                mToast.show();
                timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        DialogUtil.mToast = null;
                        DialogUtil.ToastString = "";
                        timer.cancel();
                        timer = null;
                    }
                };
                timer.schedule(timerTask, 3000);
            } else {
                if (!ToastString.equals(str)) {
                    mToast.setText(str);
                    mToast.setDuration(Toast.LENGTH_SHORT);
                    ToastString = str;
                    mToast.show();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            DialogUtil.mToast = null;
                            DialogUtil.ToastString = "";
                            timer.cancel();
                            timer = null;
                        }
                    };
                    timer.schedule(timerTask, 3000);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private static void PlayVideo(Context context, String url, String name) {
//        JCFullScreenActivity.startActivity(context, url, JCVideoPlayerStandard.class, name);
//        //  JCVideoPlayerStandard.startFullscreen(context,JCVideoPlayerStandard.class, url, name);
//    }


//    public static void playVideo(Context context, String path, String name) {
//        if (path == null || path.trim().equals("")) {
//            DialogUtil.showDialog(context, "内容错误,无法播放");
//            return;
//        }
//        if (name == null) {
//            name = "";
//        }
//        DialogUtil.PlayVideo(context, path, name);
//    }


}
