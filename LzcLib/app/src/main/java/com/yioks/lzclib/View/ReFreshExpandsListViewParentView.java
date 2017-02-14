package com.yioks.lzclib.View;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by ${User} on 2017/1/17 0017.
 */
public class ReFreshExpandsListViewParentView extends ReFreshListViewParentView {
    public ReFreshExpandsListViewParentView(Context context) {
        super(context);
    }

    public ReFreshExpandsListViewParentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReFreshExpandsListViewParentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean isReadyForPullStart() {
        return super.isReadyForPullStart();
    }

    @Override
    protected boolean isReadyForPullEnd() {
        return false;
    }
}
