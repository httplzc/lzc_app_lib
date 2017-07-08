package com.yioks.lzclib.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.yioks.lzclib.R;

/**
 * Created by ${User} on 2017/4/18 0018.
 */

public class HeadScrollView extends ScrollView {
    private ArcImgView arcImgView;
    private int layoutId;
    private View headView;

    public HeadScrollView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.setOnScrollListener(new onScrollListener() {
            @Override
            public void onScroll() {
              //  Log.i("lzc", "onScroll" + getScrollY());
                if (arcImgView == null)
                    return;
                if (getScrollY() < (arcImgView.getMaxScrollHeight() - arcImgView.getMinScrollHeight())*2) {
                    arcImgView.setArcRatio(getScrollY() / ((arcImgView.getMaxScrollHeight() - arcImgView.getMinScrollHeight())*2));
                }
                else
                {
                    arcImgView.setImgRatio(1);
                }
            }
        });
        setOverScrollMode(android.widget.ScrollView.OVER_SCROLL_NEVER);
    }

    public HeadScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attributeSet) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.HeadScrollView);
        layoutId = typedArray.getResourceId(R.styleable.HeadScrollView_head_layout, -1);
        typedArray.recycle();
    }

    public HeadScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initAttrs(attrs);

    }

    public View getHeadView() {
        return headView;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (layoutId == -1)
            return;
        LinearLayout linearLayout = (LinearLayout) getChildAt(0);
        headView = LayoutInflater.from(getContext()).inflate(layoutId, linearLayout, false);
        linearLayout.addView(headView, 0);
        arcImgView = (ArcImgView) headView.findViewById(R.id.arcImgView);
    }
}
