package pers.lizechao.android_lib.support.share.strategy;

import android.support.annotation.NonNull;

import pers.lizechao.android_lib.support.share.data.ShareTarget;

/**
 * Created by Lzc on 2018/6/25 0025.
 */
public class StrategyFactory {

    @NonNull
    public static IShareStrategy selectStrategyByType(ShareTarget shareTarget) {
        IShareStrategy iShareStrategy = null;
        switch (shareTarget) {
            case TYPE_QQ:
                iShareStrategy = new QQFriendShareStrategy();
                break;
            case TYPE_QQ_ZONE:
                iShareStrategy = new QQZoneShareStrategy();
                break;
            case TYPE_WX:
                iShareStrategy = new WXFriendShareStrategy();
                break;
            case TYPE_WB:
                iShareStrategy = new WBShareStrategy();
                break;
            case TYPE_WX_Circle:
                iShareStrategy = new WXZoneShareStrategy();
                break;
        }
        return iShareStrategy;
    }
}
