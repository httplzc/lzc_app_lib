package pers.lizechao.android_lib.ui.layout;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import pers.lizechao.android_lib.R;
import pers.lizechao.android_lib.databinding.TitleViewLayoutBinding;

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
 * Date: 2018-08-02
 * Time: 18:20
 * 标题栏View
 */
public class TitleBarView extends FrameLayout {
    private final TitleData titleData = new TitleData();
    private TitleViewLayoutBinding binding;

    public TitleBarView(@NonNull Context context) {
        super(context);
        initView();

    }

    protected void initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.title_view_layout, this, true);
        binding.setTitleData(titleData);
    }

    public TitleBarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        initAttr(attrs);
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TitleBarView);
        boolean left = typedArray.getBoolean(R.styleable.TitleBarView_title_bar_show_back, true);
        String title = typedArray.getString(R.styleable.TitleBarView_title_bar_title);
        setTitleData(left, title);
        typedArray.recycle();
    }

    public TitleData getTitleData() {
        return titleData;
    }

    public void setTitleData(boolean left, String title) {
        titleData.setTitle(title);
        titleData.setBack_layout_show(left);
        if (left) {
            binding.backLayout.setOnClickListener(v -> {
                if (getContext() instanceof Activity) {
                    ((Activity) getContext()).finish();
                }
            });
        }
    }

    public void setTitleData(boolean left, String title, int rightRes) {
        setTitleData(left, title);
        titleData.setRight_img_show(true);
        titleData.setRight_img_res_id(rightRes);
    }

    public void setTitleData(boolean left, String title, String rightText) {
        setTitleData(left, title);
        titleData.setRight_text_show(true);
        titleData.setRight_text(rightText);
    }

    public void setLeftClickListener(OnClickListener clickListener) {
        binding.leftImg.setOnClickListener(clickListener);
    }

    public void setTitleClickListener(OnClickListener clickListener) {
        binding.title.setOnClickListener(clickListener);
    }


    public void setRightClickListener(OnClickListener clickListener) {
        binding.rightImg.setOnClickListener(clickListener);
        binding.rightText.setOnClickListener(clickListener);
    }

    public TitleViewLayoutBinding getBinding() {
        return binding;
    }
}
