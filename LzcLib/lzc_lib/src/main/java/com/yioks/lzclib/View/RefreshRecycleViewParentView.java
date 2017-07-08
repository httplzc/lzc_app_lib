package com.yioks.lzclib.View;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yioks.lzclib.Data.ScreenData;
import com.yioks.lzclib.R;

/**
 * Created by ${User} on 2017/4/7 0007.
 */

public class RefreshRecycleViewParentView extends RefreshScrollParentViewBase<RecycleView> {
    private TextView load_more_text;
    private View load_more_progress;
    private RefreshRecycleViewParentView.LoaddingMoreListener loaddingMoreListener;
    private int footcount = 1;
    private boolean isLoaddingMoreFailure = false;

    public RefreshRecycleViewParentView(Context context) {
        super(context);
    }

    public RefreshRecycleViewParentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshRecycleViewParentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void checkOnScroll() {

        ViewGroup viewGroup = (ViewGroup) scrollView;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View content = viewGroup.getChildAt(i);
            if (groupViews.containsKey(content)) {
                View cloneView = groupViews.get(content);
                dealReplace(content, cloneView);
            }
        }
    }

    @Override
    protected void addExternView() {
        reFreshMoreView = LayoutInflater.from(context).inflate(R.layout.refresh_more_view, null);
        reFreshMoreView.setBackgroundColor(footColor);
        reFreshMoreFailureView = LayoutInflater.from(context).inflate(R.layout.refresh_more_failure_view, null);
        reFreshMoreFailureView.setBackgroundColor(footColor);
        reFreshMoreFailureView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.removeFootView(reFreshMoreFailureView);
                scrollView.addFootView(reFreshMoreView, null);
                isLoaddingMore = true;
                isLoaddingMoreFailure = false;
                reFreshMoreFailureView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (loaddingMoreListener != null)
                            loaddingMoreListener.loadMore();
                    }
                }, 500);
            }
        });
        load_more_text = (TextView) reFreshMoreView.findViewById(R.id.load_more_text);
        load_more_progress = (View) reFreshMoreView.findViewById(R.id.load_more_progress);
        reFreshView = LayoutInflater.from(context).inflate(R.layout.refresh_view, null);
        reFreshView.setBackgroundColor(refreshColor);
        reFreshView.setClickable(false);

        LinearLayout linearLayout = new LinearLayout(context);
        RecycleView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(lp);
        linearLayout.addView(reFreshView);


        scrollView.addHeadView(linearLayout, 0, null);
        refreshText = (TextView) reFreshView.findViewById(R.id.refresh_text);
        reFreshImg = (ImageView) reFreshView.findViewById(R.id.refresh_img);
        pull = (LinearLayout) reFreshView.findViewById(R.id.pull);
        loadding = (FrameLayout) reFreshView.findViewById(R.id.loadding);
        refresh_succeed = (LinearLayout) reFreshView.findViewById(R.id.refresh_succeed);
        loadding_effect = reFreshView.findViewById(R.id.loadding_effect);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) reFreshView.getLayoutParams();
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        //layoutParams.topMargin= (int) (-60*ScreenData.density);
        reFreshView.setLayoutParams(layoutParams);
        linearLayout.setPadding(linearLayout.getPaddingLeft(), (int) (-60 * ScreenData.density), linearLayout.getPaddingRight(), linearLayout.getPaddingBottom());
        scrollView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int scrollState;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                this.scrollState = newState;
                if (scrollState == RecyclerView.SCROLL_STATE_IDLE && isInEnd()) {
                    scrollView.scrollToPosition(scrollView.getWrapperAdapter().getItemCount() - 1);
                }
                Log.i("lzc", "scrollView getChildCount" + scrollView.getChildCount());
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                checkOnScroll();
                if (isReadyForPullEnd() && scrollState != RecyclerView.SCROLL_STATE_IDLE) {
                    if (isLoaddingMore || isHaveFinishLoadMore() || reFreshSatus != ReFreshSatus.NORMAL || isLoaddingMoreFailure) {
                        if (isHaveFinishLoadMore() && scrollView.getFootCount() != footcount) {
                            scrollView.addFootView(reFreshMoreView, null);
                        }
                        return;
                    }
                    isLoaddingMore = true;
                    isLoaddingMoreFailure = false;
                    load_more_text.setText("正在加载中……");
                    load_more_progress.setVisibility(VISIBLE);
                    scrollView.addFootView(reFreshMoreView, null);
                    loaddingMoreListener.loadMore();
                }
            }
        });

    }

    public void loaddingMoreComplete(boolean isFinish, boolean succeed) {
        if (succeed) {

            scrollView.removeFootView(reFreshMoreFailureView);
            isLoaddingMoreFailure = false;
            if (!isFinish) {
                scrollView.removeFootView(reFreshMoreView);
                haveFinishLoadMore = false;
            } else {
                setHaveFinishLoadMore(true);
            }
            isLoaddingMore = false;
        } else {
            isLoaddingMore = false;
            if (isLoaddingMoreFailure) {
                return;
            }
            isLoaddingMoreFailure = true;
            scrollView.removeFootView(reFreshMoreView);
            scrollView.addFootView(reFreshMoreFailureView, null);
            haveFinishLoadMore = false;
        }

    }

    @Override
    protected boolean isReadyForPullStart() {
        if (scrollView.getChildCount() < 0 || scrollView.getLayoutManager() == null || scrollView.getChildAt(0) == null) {
            return false;
        }
        int pos = scrollView.getLayoutManager().getPosition(scrollView.getChildAt(0));
        return pos == 0;
    }


    protected boolean isInEnd() {
        return scrollView.computeVerticalScrollExtent() + scrollView.computeVerticalScrollOffset()
                >= scrollView.computeVerticalScrollRange() && !isLessData();
    }

    @Override
    protected boolean isReadyForPullEnd() {
        return scrollView.computeVerticalScrollExtent() + scrollView.computeVerticalScrollOffset()
                >= scrollView.computeVerticalScrollRange() - 20 * ScreenData.density && !isLessData();
    }

    private boolean isLessData() {
        boolean isLessData;
        if (scrollView.getChildCount() != 0) {
            isLessData = scrollView.computeVerticalScrollRange() <= scrollView.getHeight();
        } else {
            isLessData = true;
        }
        if (isLessData) {
//            scrollView.removeFootView(reFreshMoreFailureView);
//            scrollView.removeFootView(reFreshMoreView);
        }
        return isLessData;
    }

    public interface LoaddingMoreListener {
        void loadMore();
    }

    public LoaddingMoreListener getLoaddingMoreListener() {
        return loaddingMoreListener;
    }

    public void setLoaddingMoreListener(RefreshRecycleViewParentView.LoaddingMoreListener loaddingMoreListener) {
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
                scrollView.removeFootView(reFreshMoreView);
                scrollView.removeFootView(reFreshMoreFailureView);
                isLoaddingMoreFailure = false;

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
