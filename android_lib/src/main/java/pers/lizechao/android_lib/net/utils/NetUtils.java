package pers.lizechao.android_lib.net.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pers.lizechao.android_lib.net.data.Progress;

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
 * Date: 2018-08-10
 * Time: 10:24
 */
public class NetUtils {
    public static Progress calcInitProgress(String range) {
        Pattern pattern = Pattern.compile("^bytes (\\d*)-(\\d*)/(\\d+)$");
        Matcher matcher = pattern.matcher(range);
        if (matcher.find()) {
            String start = matcher.group(1);
            String end = matcher.group(2);
            String size = matcher.group(3);
            long totalLength = Long.valueOf(size);
            long current = Long.valueOf(start);
            return new Progress(totalLength, current);
        }
        return new Progress(0, 0);
    }
}
