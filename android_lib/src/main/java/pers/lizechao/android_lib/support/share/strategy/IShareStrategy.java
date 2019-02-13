package pers.lizechao.android_lib.support.share.strategy;

import android.app.Activity;

import io.reactivex.Single;
import pers.lizechao.android_lib.function.ActivityCallBack;
import pers.lizechao.android_lib.support.share.data.ShareContent;
import pers.lizechao.android_lib.support.share.data.ShareDateType;

/**
 * Created by Lzc on 2018/6/22 0022.
 */
public interface IShareStrategy extends ActivityCallBack {
    Single<Boolean> share(Activity context, ShareContent shareContent, ShareDateType shareDateType);
}
