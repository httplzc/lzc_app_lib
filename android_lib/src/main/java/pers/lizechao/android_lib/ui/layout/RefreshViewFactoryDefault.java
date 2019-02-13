package pers.lizechao.android_lib.ui.layout;

import android.content.Context;
import android.view.ViewGroup;

import pers.lizechao.android_lib.ui.widget.RefreshParent;

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
 * Date: 2018-07-21
 * Time: 18:04
 */
public class RefreshViewFactoryDefault extends RefreshParent.RefreshViewFactory {

    @Override
    protected RefreshParent.PullView createHeadPullView(Context context, ViewGroup viewGroup) {
        return new RefreshPullNormalHeadDefault(context, true);
    }

    @Override
    protected RefreshParent.PullView createFootPullView(Context context, ViewGroup viewGroup) {
        //return new RefreshPullNormalFootDefault(context, false);
        return null;
    }

    @Override
    protected RefreshParent.RefreshMoreView createFootView(Context context, ViewGroup viewGroup) {
        return new RefreshMoreNormalDefault(context);
    }
}
