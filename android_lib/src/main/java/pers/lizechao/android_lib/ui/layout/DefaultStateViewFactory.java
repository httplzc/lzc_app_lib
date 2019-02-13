package pers.lizechao.android_lib.ui.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pers.lizechao.android_lib.R;
import pers.lizechao.android_lib.ui.widget.PageStateView;

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
 * Date: 2018-08-04
 * Time: 16:48
 */
public class DefaultStateViewFactory extends PageStateView.StateViewFactory {

    @Override
    public View createLoadingView(Context context, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.state_view_load_layout, viewGroup, false);
    }

    @Override
    public View createNullView(Context context, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.state_view_null_layout, viewGroup, false);
    }

    @Override
    public View createErrorView(Context context, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.state_view_error_layout, viewGroup, false);
    }
}
