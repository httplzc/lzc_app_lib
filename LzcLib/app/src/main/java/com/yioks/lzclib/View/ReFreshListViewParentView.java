package com.yioks.lzclib.View;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yioks.lzclib.Data.ScreenData;
import com.yioks.lzclib.R;


/**
 * Created by 李泽超 on 2016/9/5 0005.
 */
public class ReFreshListViewParentView extends RefreshScrollParentViewBase<ListView> {
    private TextView load_more_text;
    private View load_more_progress;
    private LoaddingMoreListener loaddingMoreListener;
    private int footcount = 1;


    public ReFreshListViewParentView(Context context) {
        super(context);
    }

    public ReFreshListViewParentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReFreshListViewParentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void addExternView() {
        reFreshMoreView = LayoutInflater.from(context).inflate(R.layout.refresh_more_view, scrollView, false);
        load_more_text = (TextView) reFreshMoreView.findViewById(R.id.load_more_text);
        load_more_progress = (View) reFreshMoreView.findViewById(R.id.load_more_progress);
        reFreshView = LayoutInflater.from(context).inflate(R.layout.refresh_view, scrollView, false);
        reFreshView.setClickable(false);
        scrollView.addHeaderView(reFreshView, null, false);
        refreshText = (TextView) reFreshView.findViewById(R.id.refresh_text);
        reFreshImg = (ImageView) reFreshView.findViewById(R.id.refresh_img);
        pull = (LinearLayout) reFreshView.findViewById(R.id.pull);
        loadding = (FrameLayout) reFreshView.findViewById(R.id.loadding);
        refresh_succeed = (LinearLayout) reFreshView.findViewById(R.id.refresh_succeed);
        loadding_effect = reFreshView.findViewById(R.id.loadding_effect);
        scrollView.setPadding(scrollView.getPaddingLeft(), (int) (-60 * ScreenData.density), scrollView.getPaddingRight(), scrollView.getPaddingBottom());
        scrollView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int scrollState;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.scrollState = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    return;
                }
                if (isReadyForPullEnd()) {
                    if (isLoaddingMore || isHaveFinishLoadMore() || reFreshSatus != ReFreshSatus.NORMAL) {
                        if (isHaveFinishLoadMore() && scrollView.getFooterViewsCount() == footcount) {
                            scrollView.addFooterView(reFreshMoreView, null, false);
                        }
                        return;
                    }
                    isLoaddingMore = true;
                    load_more_text.setText("正在加载中……");
                    load_more_progress.setVisibility(VISIBLE);
                    scrollView.addFooterView(reFreshMoreView, null, false);
                    loaddingMoreListener.loadMore();
                }
            }
        });
    }


    @Override
    protected boolean isReadyForPullStart() {
        final Adapter adapter = scrollView.getAdapter();
        if (null == adapter || adapter.isEmpty()) {
            return true;
        } else {
            if (scrollView.getFirstVisiblePosition() <= 0) {
                final View firstVisibleChild = scrollView.getChildAt(0);
                if (firstVisibleChild != null) {
                    Log.i("lzc", "firstVisibleChild.getTop()" + firstVisibleChild.getTop());
                    return firstVisibleChild.getTop() <= 0;
                }
            }
        }

        return false;
    }


    public void loaddingMoreComplete(boolean isFinish) {
        if (!isFinish) {
            scrollView.removeFooterView(reFreshMoreView);
            haveFinishLoadMore=false;
        } else {
            setHaveFinishLoadMore(true);
        }
        isLoaddingMore = false;
    }


    @Override
    protected boolean isReadyForPullEnd() {
        final Adapter adapter = scrollView.getAdapter();
        if (null == adapter || adapter.isEmpty()) {
            return false;
        } else {
            if (isLessData()) {
                return false;
            }
            final int lastItemPosition = scrollView.getCount() - scrollView.getFooterViewsCount() - 1;
            final int lastVisiblePosition = scrollView.getLastVisiblePosition();
            if (lastVisiblePosition >= lastItemPosition) {
                return true;
            }
        }
        return false;
    }

    private boolean isLessData() {
        if (scrollView.getChildCount() != 0) {
            if (scrollView.getLastVisiblePosition() == scrollView.getAdapter().getCount() - 1) {
                return true;
            }
        }
        return false;
    }

    public interface LoaddingMoreListener {
        void loadMore();
    }

    public LoaddingMoreListener getLoaddingMoreListener() {
        return loaddingMoreListener;
    }

    public void setLoaddingMoreListener(LoaddingMoreListener loaddingMoreListener) {
        this.loaddingMoreListener = loaddingMoreListener;
    }

    public boolean isHaveFinishLoadMore() {
        return haveFinishLoadMore;
    }

    public void setHaveFinishLoadMore(boolean haveFinishLoadMore) {

        this.haveFinishLoadMore = haveFinishLoadMore;
        if (haveFinishLoadMore) {
            load_more_text.setText("没有更多了");
            load_more_progress.setVisibility(GONE);
        }
    }

    @Override
    public void completeLoad(boolean succeed) {
        super.completeLoad(succeed);
        if (succeed) {
            if (isLoaddingMore) {
                return;
            } else {
                scrollView.removeFooterView(reFreshMoreView);
            }

        }
    }

    public int getFootcount() {
        return footcount;
    }

    public void setFootcount(int footcount) {
        this.footcount = footcount;
    }
}
