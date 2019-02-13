package pers.lizechao.android_lib.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import pers.lizechao.android_lib.R;

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
 * Date: 2018/9/28 0028
 * Time: 15:56
 */
public class TopPopupView extends FrameLayout {
    private int contentViewId;
    private View alphaView;
    private int alphaColor = Color.parseColor("#50000000");
    private ViewDataBinding binding;
    private PopupWindow.OnDismissListener onDismissListener;


    private int enterAnim = R.anim.top_dialog_show;
    private int exitAnim = R.anim.top_dialog_hide;
    private int enterAlphaAnim = R.anim.alpha_dialog_show;
    private int exitAlphaAnim = R.anim.alpha_dialog_hide;

    private boolean isShow = false;

    public TopPopupView(Context context) {
        super(context);
        init();
    }

    public TopPopupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        init();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TopPopupView);
        contentViewId = typedArray.getResourceId(R.styleable.TopPopupView_top_pop_view_content_id, 0);
        typedArray.recycle();
        if (contentViewId == 0)
            throw new IllegalArgumentException("top_pop_view_content_id 不能为空");
    }

    private void init() {
        alphaView = new View(getContext());
        alphaView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        alphaView.setBackgroundColor(alphaColor);
        this.addView(alphaView);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), contentViewId, this, true);
        ((FrameLayout.LayoutParams) binding.getRoot().getLayoutParams()).gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        this.setVisibility(GONE);
        this.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss();
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            dismiss();
        }
        return true;
    }

    public boolean isShow() {
        return isShow;
    }

    public void show() {
        if (isShow)
            return;
        isShow = true;
        this.setVisibility(VISIBLE);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        this.requestFocus();
        binding.getRoot().clearAnimation();
        if (enterAnim != -1) {
            binding.getRoot().setAnimation(AnimationUtils.loadAnimation(getContext(), enterAnim));
            binding.getRoot().getAnimation().start();
        }
        if (enterAlphaAnim != -1) {
            alphaView.setAnimation(AnimationUtils.loadAnimation(getContext(), enterAlphaAnim));
            alphaView.getAnimation().start();
        }

    }

    public void dismiss() {
        if (!isShow)
            return;
        isShow = false;
        if (onDismissListener != null)
            onDismissListener.onDismiss();
        binding.getRoot().clearAnimation();
        if (exitAnim != -1) {
            binding.getRoot().setAnimation(AnimationUtils.loadAnimation(getContext(), exitAnim));
            binding.getRoot().getAnimation().start();
            binding.getRoot().getAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    TopPopupView.this.setVisibility(GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        if (exitAlphaAnim != -1) {
            alphaView.setAnimation(AnimationUtils.loadAnimation(getContext(), exitAlphaAnim));
            alphaView.getAnimation().start();
        }
    }

    public ViewDataBinding getBinding() {
        return binding;
    }

    public void setAlphaColor(int alphaColor) {
        this.alphaColor = alphaColor;
        alphaView.setBackgroundColor(alphaColor);
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }
}
