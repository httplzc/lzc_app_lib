package pers.lizechao.android_lib.ui.widget.refresh;

import android.content.Context;
import android.support.annotation.NonNull;

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
 * Date: 2018-07-30
 * Time: 16:15
 */
public abstract class RefreshMoreNormalView extends RefreshParent.RefreshMoreView {


    protected enum ViewStatus {
        Normal, ON_REFRESH, ON_FAIL, NoMoreData
    }

    protected ViewStatus footStatus = ViewStatus.Normal;

    protected RefreshMoreNormalView(@NonNull Context context) {
        super(context);
    }


    @Override
    protected void onRefresh(boolean anim) {
        stateChange(ViewStatus.ON_REFRESH);
        callGetData();
    }

    @Override
    protected void onFail() {
        stateChange(ViewStatus.ON_FAIL);
    }

    @Override
    protected void onSucceed() {
        stateChange(ViewStatus.Normal);
    }

    @Override
    protected void onRemove() {

    }

    @Override
    protected void onFinish(boolean finish) {
        if (finish)
            stateChange(ViewStatus.NoMoreData);
        else
            stateChange(ViewStatus.Normal);
    }

    private void stateChange(ViewStatus newStatus) {
        if (footStatus == newStatus)
            return;
        this.footStatus = newStatus;
        onStateChange(newStatus);
    }

    abstract protected void onStateChange(ViewStatus newStatus);
}
