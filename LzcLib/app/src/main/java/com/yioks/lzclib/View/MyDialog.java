package com.yioks.lzclib.View;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yioks.lzclib.R;


/**
 * Created by ${User} on 2016/8/19 0019.
 */
public class MyDialog {
    private Context context;
    private Dialog dialog;
    private View.OnClickListener ok_button_click_listener;
    private View.OnClickListener cancel_button_click_listener;
    private TextView txtOK;
    private TextView confirm;
    private TextView txtcancle;
    private ImageView dialog_waring_img;
    private TextView title_head;
    private LinearLayout confirm_cancel_lin;

    private MyDialog(Context context, String message) {
        this.context = context;
        this.dialog = new Dialog(context, R.style.MydialogStyle);
        Window window = dialog.getWindow();
        window.setContentView(R.layout.mydialog_exit);
        txtcancle = (TextView) window.findViewById(R.id.mydialog_cancle);
        txtOK = (TextView) window.findViewById(R.id.mydialog_ok);
        dialog_waring_img = (ImageView) window.findViewById(R.id.dialog_waring_img);
        title_head = (TextView) window.findViewById(R.id.title_head);
        confirm_cancel_lin= (LinearLayout) window.findViewById(R.id.confirm_cancel_lin);
        TextView mydialog_remind = (TextView) window.findViewById(R.id.mydialog_remind);
        mydialog_remind.setText(message);
        confirm = (TextView) window.findViewById(R.id.mydialog_confirm);
        txtOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ok_button_click_listener != null)
                    ok_button_click_listener.onClick(v);
                dialog.dismiss();
            }
        });
        txtcancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancel_button_click_listener != null)
                    cancel_button_click_listener.onClick(v);
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ok_button_click_listener != null)
                    ok_button_click_listener.onClick(v);
                dialog.dismiss();
            }
        });
    }


    /**
     * @param context
     * @param message        消息内容
     * @param canTouchCancel 触摸外部取消
     * @param canBackCancel  按返回键取消
     * @param isConfirm
     * @param title          传空则为图片
     */
    /** 参数依次为 上下文  dialog内容  标题（可为空） 能否触摸屏幕外取消 能否点返回键取消
    是否只有确定**/
    public MyDialog(Context context, String message, @Nullable String title, boolean canTouchCancel, boolean canBackCancel, boolean isConfirm) {
        this(context, message);
        if (canTouchCancel)
            dialog.setCanceledOnTouchOutside(true);
        else
            dialog.setCanceledOnTouchOutside(false);

        if (!canBackCancel) {
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
            });
        }
        if (isConfirm) {
            confirm.setVisibility(View.VISIBLE);
            confirm_cancel_lin.setVisibility(View.GONE);
        } else {
            confirm.setVisibility(View.GONE);
            confirm_cancel_lin.setVisibility(View.VISIBLE);
        }

        if (title == null || title.equals("")) {
            title_head.setVisibility(View.GONE);
            dialog_waring_img.setVisibility(View.VISIBLE);
        } else {
            title_head.setVisibility(View.VISIBLE);
            dialog_waring_img.setVisibility(View.GONE);
        }

    }


    public void showDialog() {
        if (dialog.isShowing()) {
            return;
        }
        this.dialog.show();
    }

    public void dismissDialog() {
        this.dialog.dismiss();
    }

    public View.OnClickListener getOk_button_click_listener() {
        return ok_button_click_listener;
    }

    public void setOk_button_click_listener(View.OnClickListener ok_button_click_listener) {
        this.ok_button_click_listener = ok_button_click_listener;
    }

    public View.OnClickListener getCancel_button_click_listener() {
        return cancel_button_click_listener;
    }

    public void setCancel_button_click_listener(View.OnClickListener cancel_button_click_listener) {
        this.cancel_button_click_listener = cancel_button_click_listener;
    }
}
