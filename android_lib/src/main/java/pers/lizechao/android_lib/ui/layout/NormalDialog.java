package pers.lizechao.android_lib.ui.layout;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Objects;

import java.text.DecimalFormat;

import pers.lizechao.android_lib.R;
import pers.lizechao.android_lib.ui.widget.CustomProgressBar;
import pers.lizechao.android_lib.utils.StrUtils;

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
 * Date: 2018-07-13
 * Time: 9:42
 * 一个通用Dialog 用于显示
 * -确定取消弹窗
 * -确认弹窗
 * -进度提醒弹窗
 */
public class NormalDialog {
    private final Context context;
    //消息
    private final String message;
    //标题
    private final String title;
    //点击屏幕外取消
    private final boolean canTouchCancel;
    //返回键取消
    private final boolean canBackCancel;
    //是否为确认模式
    private final boolean isConfirm;
    //是否为进度提示模式
    private final boolean isProgressMode;
    //是否显示动画
    private final boolean showStartAnim;
    //真实弹窗
    private Dialog dialog;
    //回调
    private DialogInterface.OnDismissListener onDismissListener;
    private View.OnClickListener confirmListener;
    private View.OnClickListener cancelListener;


    //布局中的View
    private TextView activeBtn;
    private TextView cancelBtn;
    private ImageView dialog_waring_img;
    private TextView titleView;

    private TextView remindView;
    private CustomProgressBar progressBar;
    private TextView progressText;

    private View confirm_cancel_state;
    private View confirm_state;
    private View progress_state;

    private NormalDialog(Builder builder) {
        context = builder.context;
        message = builder.message;
        title = builder.title;
        canTouchCancel = builder.canTouchCancel;
        canBackCancel = builder.canBackCancel;
        isConfirm = builder.isConfirm;
        isProgressMode = builder.isProgressMode;
        showStartAnim = builder.showStartAnim;
        onDismissListener = builder.onDismissListener;
        confirmListener = builder.confirmListener;
        cancelListener = builder.cancelListener;
        initDialog();

    }

    //初始化Dialog属性
    private void initDialog() {
        this.dialog = new Dialog(context, R.style.NormalDialogStyle);
        Window window = dialog.getWindow();
        if (window == null)
            return;
        window.setContentView(R.layout.normal_dialog_layout);
        if (showStartAnim)
            window.setWindowAnimations(R.style.my_dialog_default_anim);
        dialog.setCanceledOnTouchOutside(canTouchCancel);
        if (!canBackCancel) {
            dialog.setOnKeyListener((dialog, keyCode, event) -> keyCode == KeyEvent.KEYCODE_BACK);
        }
        initDialogView(window);
        initDialogState();
        initDialogValue();
        setCancelListener(cancelListener);
        setConfirmListener(confirmListener);
        setOnDismissListener(onDismissListener);


    }

    //为view赋值
    private void initDialogValue() {
        if (StrUtils.CheckNull(message))
            remindView.setVisibility(View.GONE);
        else
            remindView.setText(message);
        if (StrUtils.CheckNull(title)) {
            titleView.setVisibility(View.GONE);
            dialog_waring_img.setVisibility(View.VISIBLE);
        } else {
            titleView.setVisibility(View.VISIBLE);
            dialog_waring_img.setVisibility(View.GONE);
            titleView.setText(title);
        }
    }

    //根据类型隐藏布局
    private void initDialogState() {
        if (isProgressMode) {
            confirm_cancel_state.setVisibility(View.GONE);
            confirm_state.setVisibility(View.GONE);
            progress_state.setVisibility(View.VISIBLE);
        }
        else if(isConfirm)
        {
            confirm_cancel_state.setVisibility(View.GONE);
            confirm_state.setVisibility(View.VISIBLE);
            progress_state.setVisibility(View.GONE);
        }
        else
        {
            confirm_cancel_state.setVisibility(View.VISIBLE);
            confirm_state.setVisibility(View.GONE);
            progress_state.setVisibility(View.GONE);
        }
    }

    private void initDialogView(Window window) {
        cancelBtn = window.findViewById(R.id.cancel_btn);
        activeBtn = window.findViewById(R.id.active_btn);
        dialog_waring_img = window.findViewById(R.id.dialog_waring_img);
        titleView = window.findViewById(R.id.title);

        remindView = window.findViewById(R.id.remind);
        remindView.setMovementMethod(new ScrollingMovementMethod());
        if (isConfirm)
            activeBtn = window.findViewById(R.id.confirm_btn);
        progressBar = window.findViewById(R.id.progress);
        progressText = window.findViewById(R.id.progress_text);

        confirm_cancel_state = window.findViewById(R.id.confirm_cancel_state);
        confirm_state=window.findViewById(R.id.confirm_state);
        progress_state = window.findViewById(R.id.progress_state);
    }


    public static final class Builder {
        private Context context;
        private String message = "";
        private String title = "";
        private boolean canTouchCancel = true;
        private boolean canBackCancel = true;
        private boolean isConfirm = false;
        private boolean isProgressMode = false;
        private boolean showStartAnim = true;

        private DialogInterface.OnDismissListener onDismissListener;
        private View.OnClickListener confirmListener;
        private View.OnClickListener cancelListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder onDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            this.onDismissListener = onDismissListener;
            return this;
        }

        public Builder confirmListener(View.OnClickListener confirmListener) {
            this.confirmListener = confirmListener;
            return this;
        }

        public Builder cancelListener(View.OnClickListener cancelListener) {
            this.cancelListener = cancelListener;
            return this;
        }

        public Builder message(String val) {
            message = val;
            return this;
        }

        public Builder title(String val) {
            title = val;
            return this;
        }

        public Builder canTouchCancel(boolean val) {
            canTouchCancel = val;
            return this;
        }

        public Builder canBackCancel(boolean val) {
            canBackCancel = val;
            return this;
        }

        public Builder isConfirm(boolean val) {
            isConfirm = val;
            return this;
        }

        public Builder isProgressMode(boolean val) {
            isProgressMode = val;
            return this;
        }

        public Builder showStartAnim(boolean val) {
            showStartAnim = val;
            return this;
        }


        public NormalDialog build() {
            context = Objects.requireNonNull(context);
            return new NormalDialog(this);
        }
    }


    public void setConfirmListener(View.OnClickListener confirmListenerP) {
        this.confirmListener = confirmListenerP;
        if (activeBtn != null) {
            activeBtn.setOnClickListener(v -> {
                if (confirmListener != null)
                    confirmListener.onClick(v);
                dialog.dismiss();
            });
        }

    }

    public void setCancelListener(View.OnClickListener cancelListenerP) {
        this.cancelListener = cancelListenerP;
        if (cancelBtn != null) {
            cancelBtn.setOnClickListener(v -> {
                if (cancelListener != null)
                    cancelListener.onClick(v);
                dialog.dismiss();
            });
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        if (dialog != null)
            dialog.setOnDismissListener(onDismissListener);
    }

    public void setProgress(float progress) {
        progressBar.setProgress(progress);
        DecimalFormat decimalFormat = new DecimalFormat("0%");
        progressText.setText(decimalFormat.format((progress / progressBar.getMaxProgress())));
    }

    public void setMaxProgress(int maxProgress) {
        progressBar.setMaxProgress(maxProgress);
        setProgress(progressBar.getProgress());

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

}
