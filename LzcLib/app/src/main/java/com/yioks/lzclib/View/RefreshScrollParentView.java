package com.yioks.lzclib.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yioks.lzclib.Data.ScreenData;
import com.yioks.lzclib.R;


/**
 * Created by ${User} on 2016/9/1 0001.
 */
public class RefreshScrollParentView extends RefreshScrollParentViewBase<ScrollView> {
    protected ViewGroup scrollChildView;
    protected LinearLayout reFreshBottom;
    public RefreshScrollParentView(Context context) {
        super(context);
    }

    public RefreshScrollParentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshScrollParentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void addExternView() {
        scrollChildView = (ViewGroup) scrollView.getChildAt(0);
        reFreshView = LayoutInflater.from(context).inflate(R.layout.refresh_view, null, false);
        reFreshBottom = (LinearLayout) scrollChildView.getChildAt(refresh_position);
        reFreshBottom.addView(reFreshView, 0);
        refreshText = (TextView) reFreshView.findViewById(R.id.refresh_text);
        reFreshImg = (ImageView) reFreshView.findViewById(R.id.refresh_img);
        pull = (LinearLayout) reFreshView.findViewById(R.id.pull);
        loadding = (FrameLayout) reFreshView.findViewById(R.id.loadding);
        refresh_succeed= (LinearLayout) reFreshView.findViewById(R.id.refresh_succeed);
        loadding_effect=reFreshView.findViewById(R.id.loadding_effect);
        reFreshBottom.setPadding(reFreshBottom.getPaddingLeft(), (int) (-60 * ScreenData.density), reFreshBottom.getPaddingRight(), reFreshBottom.getPaddingBottom());
    }


    /**
     * 是否到达顶部
     * @return
     */
    public boolean isReadyForPullStart() {
        return scrollView.getScrollY() == 0;
    }


    /**
     * 是否到达底部
     * @return
     */
    public boolean isReadyForPullEnd() {
        View scrollViewChild = scrollView.getChildAt(0);
        if (null != scrollViewChild) {
            return scrollView.getScrollY() >= (scrollViewChild.getHeight() - getHeight());
        }
        return false;
    }

}
