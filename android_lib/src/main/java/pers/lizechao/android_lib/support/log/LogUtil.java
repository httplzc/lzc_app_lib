package pers.lizechao.android_lib.support.log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pers.lizechao.android_lib.storage.file.FileStoreManager;
import pers.lizechao.android_lib.storage.file.Path;
import pers.lizechao.android_lib.storage.file.StoreMedium;

/**
 * Created by Lzc on 2018/5/23 0023.
 */
public class LogUtil {
    private static final String TOP_LEFT_CORNER = "╔";
    private static final String BOTTOM_LEFT_CORNER = "╚";
    private static final String MIDDLE_CORNER = "╟";
    public static final String HORIZONTAL_DOUBLE_LINE = "║ ";
    public static final String BR = "\n";   // 换行符
    public static final String EMPTY = "  ";   // 换行符
    private static final String DOUBLE_DIVIDER = "═════════════════════════════════════════════════";
    private static final String SINGLE_DIVIDER = "─────────────────────────────────────────────────";
    public static final String TOP_BORDER = DOUBLE_DIVIDER + DOUBLE_DIVIDER + BR;
    public static final String BOTTOM_BORDER = DOUBLE_DIVIDER + DOUBLE_DIVIDER + BR;
    public static final String MIDDLE_BORDER = SINGLE_DIVIDER + SINGLE_DIVIDER + BR;
    private static final String TAG = "log_lib";
    private static final ThreadLocal<SimpleDateFormat> simpleDateFormatLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }
    };

    public static File getOriginLogFile(LogType logType) {
        return FileStoreManager.getFileStore(StoreMedium.External)
                .createFile("logType_" + logType.name(), Path.from(TAG, logType.name() + ".txt"));
    }

    public static String formatData(Date date) {
        SimpleDateFormat simpleDateFormat = simpleDateFormatLocal.get();
        return simpleDateFormat.format(date);
    }


}
