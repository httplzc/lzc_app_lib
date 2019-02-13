package pers.lizechao.android_lib.support.share.strategy;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

/**
 * Created by Lzc on 2018/6/22 0022.
 */
public class WXFriendShareStrategy extends WXShareStrategyBase {
    @Override
    int getScreenByType() {
        return SendMessageToWX.Req.WXSceneSession;
    }
}
