package pers.lizechao.android_lib.support.share.data;

import pers.lizechao.android_lib.support.aop.annotation.AutoSave;

/**
 * Created by Lzc on 2018/6/22 0022.
 */
public class ShareExtraData {
    @AutoSave(key = "QQId")
    public static String QQId;
    @AutoSave(key = "WXId")
    public static String WXId;
    @AutoSave(key = "WBId")
    public static String WBId;
}
