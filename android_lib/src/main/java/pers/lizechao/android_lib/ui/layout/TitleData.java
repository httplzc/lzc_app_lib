package pers.lizechao.android_lib.ui.layout;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import pers.lizechao.android_lib.BR;

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
 * Date: 2018-08-03
 * Time: 10:13
 */
public class TitleData extends BaseObservable {
    private String title;
    private String left_img_text;
    private String right_text;
    private int right_img_res_id;
    private boolean left_text_show = false;
    private boolean right_text_show = false;
    private boolean right_img_show = false;
    private boolean back_layout_show = false;


    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public String getLeft_img_text() {
        return left_img_text;
    }

    public void setLeft_img_text(String left_img_text) {
        this.left_img_text = left_img_text;
        notifyPropertyChanged(BR.left_img_text);
    }

    @Bindable
    public String getRight_text() {
        return right_text;
    }

    public void setRight_text(String right_text) {
        this.right_text = right_text;
        notifyPropertyChanged(BR.right_text);
    }

    @Bindable
    public int getRight_img_res_id() {
        return right_img_res_id;
    }

    public void setRight_img_res_id(int right_img_res_id) {
        this.right_img_res_id = right_img_res_id;
        notifyPropertyChanged(BR.right_img_res_id);
    }

    @Bindable
    public boolean getLeft_text_show() {
        return left_text_show;
    }

    public void setLeft_text_show(boolean left_text_show) {
        this.left_text_show = left_text_show;
        notifyPropertyChanged(BR.left_text_show);
    }

    @Bindable
    public boolean getRight_text_show() {
        return right_text_show;
    }

    public void setRight_text_show(boolean right_text_show) {
        this.right_text_show = right_text_show;
        notifyPropertyChanged(BR.right_text_show);
    }

    @Bindable
    public boolean getRight_img_show() {
        return right_img_show;
    }

    public void setRight_img_show(boolean right_img_show) {
        this.right_img_show = right_img_show;
        notifyPropertyChanged(BR.right_img_show);
    }

    @Bindable
    public boolean getBack_layout_show() {
        return back_layout_show;
    }

    public void setBack_layout_show(boolean back_layout_show) {
        this.back_layout_show = back_layout_show;
        notifyPropertyChanged(BR.back_layout_show);
    }
}
