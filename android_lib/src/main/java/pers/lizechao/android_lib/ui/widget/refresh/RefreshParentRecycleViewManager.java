package pers.lizechao.android_lib.ui.widget.refresh;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import pers.lizechao.android_lib.ui.widget.HeadFootRecycleView;
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
 * Time: 17:45
 */
public class RefreshParentRecycleViewManager extends RefreshParent.RefreshParentManager<HeadFootRecycleView> {


    @Override
    protected void addToHead(@NonNull View view) {
        contentView.addHeadView(view);
    }

    @Override
    protected void addToFoot(@NonNull View view) {
        contentView.addFootView(view, contentView.getFootCount());
    }

    @Override
    protected void removeFromFoot(@NonNull View view) {
        contentView.removeFootView(view);
    }

    @Override
    protected boolean isOnTop() {
        if (contentView.getChildCount() < 0 || contentView.getLayoutManager() == null || contentView.getChildAt(0) == null) {
            return false;
        }
        int pos = contentView.getLayoutManager().getPosition(contentView.getChildAt(0));
        return pos == 0 && contentView.getChildAt(0).getTop() == 0;
    }

    @Override
    protected boolean isOnBottom() {
        if (contentView.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager lm = (LinearLayoutManager) contentView.getLayoutManager();
            return lm.findLastCompletelyVisibleItemPosition() == contentView.getTotalCount() - 1 && !isLessData();
        }
        return false;
    }

    @Override
    protected boolean isReadyLoadMore() {
        if ((contentView.getLayoutManager() instanceof LinearLayoutManager)&&contentView.getChildCount()!=0) {
            LinearLayoutManager lm = (LinearLayoutManager) contentView.getLayoutManager();
            int fix=(lm instanceof GridLayoutManager)?((GridLayoutManager) lm).getSpanCount()+2:2;
            return lm.findLastCompletelyVisibleItemPosition()>= contentView.getTotalCount() - fix && !isLessData();
        }
        return false;
    }

    //不满一屏
    private boolean isLessData() {
        return contentView.getChildCount() == 0 || contentView.computeVerticalScrollRange() <= contentView.getHeight();
    }

    @Override
    protected void addScrollListener(OnScrollListener<HeadFootRecycleView> scrollListener) {
        contentView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int scrollState;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                this.scrollState = newState;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                scrollListener.onScroll(contentView);
            }
        });
    }

    @Override
    public void callScrollBy(int x, int y) {
        contentView.scrollBy(x, y);
    }

}
