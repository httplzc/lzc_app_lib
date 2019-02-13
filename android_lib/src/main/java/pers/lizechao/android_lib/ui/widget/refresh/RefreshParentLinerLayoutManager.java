package pers.lizechao.android_lib.ui.widget.refresh;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;

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
 * Date: 2018-08-01
 * Time: 15:37
 */
public class RefreshParentLinerLayoutManager extends RefreshParent.RefreshParentManager<LinearLayout>{


    @Override
    protected void addToHead(@NonNull View view) {
        contentView.addView(view,0);
    }

    @Override
    protected void addToFoot(@NonNull View view) {
        contentView.addView(view,contentView.getChildCount());
    }

    @Override
    protected void removeFromFoot(@NonNull View view) {
        contentView.removeView(view);
    }

    @Override
    protected boolean isOnTop() {
        return true;
    }

    @Override
    protected boolean isOnBottom() {
      return true;
    }

    @Override
    protected boolean isReadyLoadMore() {
        return false;
    }

    @Override
    protected void addScrollListener(OnScrollListener<LinearLayout> scrollListener) {

    }

    @Override
    public void callScrollBy(int x, int y) {

    }
}
