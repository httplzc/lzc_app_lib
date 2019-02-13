package pers.lizechao.android_lib.ui.widget;

import android.databinding.ViewDataBinding;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

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
 * Date: 2018-07-12
 * Time: 17:55
 */
public abstract class LabelSelectorBindingAdapter<T extends ViewDataBinding> extends LabelSelectorAdapter {
    private Map<View, T> bindMap = new HashMap<>();

    @Override
    protected void onChoiceStateChange(View view, int position, boolean choice) {
        onChoiceStateChange(bindMap.get(view), position, choice);
    }

    protected abstract void onChoiceStateChange(T viewBind, int position, boolean choice);

    protected abstract T bindDataBind(int position, ViewGroup viewGroup);

    @Override
    public View bindData(int position, ViewGroup viewGroup) {
        T bind = bindDataBind(position, viewGroup);
        bindMap.put(bind.getRoot(), bind);
        return bind.getRoot();
    }
}
