package pers.lizechao.android_lib.support.log;

import android.content.Context;

/**
 * Created by Lzc on 2018/4/26 0026.
 */

public interface LogMsgHandle {
    LogData getFileHeadMsg(Context context, LogData logData);

    LogData getCommentStr(Context context, LogData logData);
}
