package com.yioks.lzclib.View;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.yioks.lzclib.R;


/**
 * Created by ${User} on 2016/8/19 0019.
 */
public class MyDialog {
    private Context context;
    private Dialog dialog;
    private View.OnClickListener ok_button_click_listener;
    private TextView txtOK;


    public MyDialog(Context context, String message, View.OnClickListener ok_button_click_listener) {
        this(context,message);
        this.ok_button_click_listener = ok_button_click_listener;
        this.setOk_button_click_listener(ok_button_click_listener);


    }

    public MyDialog(Context context,String message)
    {
        this.context = context;
        this.dialog = new Dialog(context, R.style.MydialogStyle);
        Window window = dialog.getWindow();
        window.setContentView(R.layout.mydialog_exit);
        TextView txtCancle = (TextView) window.findViewById(R.id.mydialog_cancle);
        txtOK = (TextView) window.findViewById(R.id.mydialog_ok);
        TextView mydialog_remind = (TextView) window.findViewById(R.id.mydialog_remind);
        mydialog_remind.setText(message);
        txtCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }
    public MyDialog(Context context,String message,int flag)
    {
        this(context,message);
        dialog.setCanceledOnTouchOutside(false);
    }

    public MyDialog(Context context,String message,boolean can_cancel)
    {
        this.context = context;
        this.dialog = new Dialog(context, R.style.MydialogStyle);
        Window window = dialog.getWindow();
        window.setContentView(R.layout.mydialog_exit);
        TextView txtCancle = (TextView) window.findViewById(R.id.mydialog_cancle);
        txtCancle.setEnabled(false);
        txtOK = (TextView) window.findViewById(R.id.mydialog_ok);
        TextView mydialog_remind = (TextView) window.findViewById(R.id.mydialog_remind);
        mydialog_remind.setText(message);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_BACK)
                {
                    return true;
                }
                return false;
            }
        });
    }

    public void showDialog() {
        if(dialog.isShowing())
        {
            return;
        }
        this.dialog.show();
    }
    public void dissMissDialog()
    {
        this.dialog.dismiss();
    }

    public View.OnClickListener getOk_button_click_listener() {
        return ok_button_click_listener;
    }

    public void setOk_button_click_listener(View.OnClickListener ok_button_click_listener) {
        this.ok_button_click_listener = ok_button_click_listener;
        txtOK.setOnClickListener(ok_button_click_listener);
    }
}
