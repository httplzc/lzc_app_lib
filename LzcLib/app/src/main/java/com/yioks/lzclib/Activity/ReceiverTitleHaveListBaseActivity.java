package com.yioks.lzclib.Activity;

import android.util.Log;
import android.widget.ScrollView;

import com.yioks.lzclib.View.ParentView;
import com.yioks.lzclib.View.RefreshScrollParentView;


/**
 * Created by ${User} on 2016/9/21 0021.
 * scrollView 嵌套listView 时用
 */
public abstract class ReceiverTitleHaveListBaseActivity extends ReceiverTitleBaseActivity {
    protected boolean haveAskSucceed = false;
    protected boolean isNeoRun;
    protected int ScrollY;
    protected RefreshScrollParentView parentView;

    @Override
    protected void onResume() {
        ScrollView ScrollView = (ScrollView) parentView.getChildAt(0);
        Log.i("lzc","ScrollY"+ScrollY);
        ScrollView.smoothScrollTo(0, ScrollY);
        super.onResume();
    }

    @Override
    protected void onPause() {
        ScrollView ScrollView = (ScrollView) parentView.getChildAt(0);
        ScrollY = ScrollView.getScrollY();
        super.onPause();
    }

    protected void GetdataFinish()
    {
        haveAskSucceed = true;
        parentView.setstaus(ParentView.Staus.Normal);
        parentView.completeLoad(true);
        if (!isNeoRun) {
            final ScrollView ScrollView = (ScrollView) parentView.getChildAt(0);
            isNeoRun = true;
            ScrollView.post(new Runnable() {
                @Override
                public void run() {
                    ScrollView.smoothScrollTo(0, 0);
                }
            });
        }
    }

    protected void DealFail()
    {
        if (!haveAskSucceed) {
            parentView.setstaus(ParentView.Staus.Error);
            parentView.completeLoad(false);
        }
    }
}
