package pers.lizechao.android_lib.ui.manager.paging;

import android.graphics.Rect;
import android.view.View;

import java.util.List;

import pers.lizechao.android_lib.ui.widget.HeadFootRecycleView;
import pers.lizechao.android_lib.ui.widget.PageStateView;
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
 * Date: 2018-09-07
 * Time: 12:01
 */
public class HeadRecyclerViewPageCoverObserver<T> implements ListPageObserver<T> {
    //是否覆盖RecycleView的头部
    protected boolean stateShowCoverHead = true;
    protected final HeadFootRecycleView headFootRecycleView;
    protected final RefreshParent refreshParent;
    protected final PageStateView pageStateView;

    public HeadRecyclerViewPageCoverObserver(boolean stateShowCoverHead, HeadFootRecycleView headFootRecycleView, RefreshParent refreshParent, PageStateView pageStateView) {
        this.stateShowCoverHead = stateShowCoverHead;
        this.headFootRecycleView = headFootRecycleView;
        this.refreshParent = refreshParent;
        this.pageStateView = pageStateView;
    }

    @Override
    public void onRefreshSucceed(List<T> list, boolean finish) {
        changePageStateView();
    }

    @Override
    public void onLoadMoreSucceed(List<T> list, boolean finish) {

    }

    @Override
    public void onRefreshError(Throwable e) {
        changePageStateView();
    }

    @Override
    public void onLoadMoreError(Throwable e) {

    }

    private void changePageStateView() {
        if (headFootRecycleView == null || refreshParent == null || stateShowCoverHead)
            return;
        if (headFootRecycleView.getHeadCount() > 1 && pageStateView.getState() != PageStateView.State.Normal) {
            headFootRecycleView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                int headTotal = 0;
                int width = headFootRecycleView.getWidth();
                for (View view : headFootRecycleView.getHeadViewList()) {
                    headTotal += view.getHeight();
                    width = Math.max(width, view.getWidth());
                }
                Rect rect = pageStateView.getCoverRect();
                if (rect == null)
                    rect = new Rect(0, headTotal, width, headFootRecycleView.getHeight());
                rect.top = headTotal;
                pageStateView.setCoverRect(rect);
            });
        } else {
            pageStateView.setContentView(headFootRecycleView);
            refreshParent.setOnHeadPullListener(null);
        }
    }
}
