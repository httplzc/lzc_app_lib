package pers.lizechao.android_lib.ui.widget.refresh;

import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
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
 * Date: 2018-08-01
 * Time: 15:37
 */
public class RefreshParentNestScrollViewManager extends RefreshParent.RefreshParentManager<NestedScrollView>{


    @Override
    protected void addToHead(@NonNull View view) {
        ((ViewGroup)(contentView.getChildAt(0))).addView(view,0);
    }

    @Override
    protected void addToFoot(@NonNull View view) {
        ((ViewGroup)(contentView.getChildAt(0))).addView(view,contentView.getChildCount());
    }

    @Override
    protected void removeFromFoot(@NonNull View view) {
        ((ViewGroup)(contentView.getChildAt(0))).removeView(view);
    }

    @Override
    protected boolean isOnTop() {
        return contentView.getScrollY() == 0;
    }

    @Override
    protected boolean isOnBottom() {
        View scrollViewChild = contentView.getChildAt(0);
        return null != scrollViewChild && contentView.getScrollY() >= (scrollViewChild.getHeight() - contentView.getHeight());
    }

    @Override
    protected boolean isReadyLoadMore() {
        return false;
    }

    @Override
    protected void addScrollListener(OnScrollListener<NestedScrollView> scrollListener) {

    }

    @Override
    public void callScrollBy(int x, int y) {
        contentView.scrollBy(x,y);
    }
}
