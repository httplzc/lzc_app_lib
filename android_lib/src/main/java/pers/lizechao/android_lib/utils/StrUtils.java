package pers.lizechao.android_lib.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lzc on 2018/4/18 0018.
 */

public class StrUtils {

    /**
     * 清空所有空格
     *
     * @param string 字符
     *
     */
    public static String trimEmpty(String string) {
        if (string == null)
            return null;
        Pattern pattern = Pattern.compile(" ");
        Matcher matcher = pattern.matcher(string);
        return matcher.replaceAll("");
    }


    /**
     * 获取当前堆栈信息
     *
     * @return 堆栈信息
     */
    public static String getCurrentStackMsg() {
        StringBuilder stringBuilder = new StringBuilder();
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        if (stackElements != null) {
            for (StackTraceElement stackElement : stackElements) {
                stringBuilder.append(stackElement.getClassName());
                stringBuilder.append("----");
                stringBuilder.append(stackElement.getFileName());
                stringBuilder.append("----");
                stringBuilder.append(stackElement.getLineNumber());
                stringBuilder.append("----");
                stringBuilder.append(stackElement.getMethodName());
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

    private static final String[] weekFirstSunday = new String[]{"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    private static final String[] weekFirstMondy = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    //获取当前星期中文
    public static String getDataWeekStr(int year, int mouth, int day) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(year, mouth, day);

        int dayIndex = gregorianCalendar.get(Calendar.DAY_OF_WEEK);
        boolean isFirstSunday = (gregorianCalendar.getFirstDayOfWeek() == Calendar.SUNDAY);
        if (isFirstSunday) {
            return weekFirstSunday[dayIndex - 1];
        } else {
            return weekFirstMondy[dayIndex - 1];
        }

    }


    //如果是空返回空字符串
    public static String CheckEmpty(String string) {
        if (string == null) {
            return "";
        }
        return string;
    }


    //是否为空字符
    public static boolean CheckNull(String string) {
        return string == null || string.trim().equals("");
    }


    //验证是否为手机号码
    public static boolean VerifyPhone(String phone) {
        if (phone == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("^1\\d{10}$");
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    //验证是否为邮件
    public static boolean VerifyEmail(String email) {
        if (email == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$");
        Matcher matcher = pattern.matcher(email.trim());
        return matcher.matches();
    }

    //验证书否为数字
    public static boolean VerifyNumber(String number) {
        if (number == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("[-+]?[0-9]+");
        Matcher matcher = pattern.matcher(number.trim());
        return matcher.matches();
    }


    private static final String[] ChinaNumbers = {"一", "二", "三", "四", "五", "六", "七", "八", "九"};
    private static final String[] ChinaNumbersWei = {"十", "百", "千", "万", "亿", "兆"};


    /**
     * 数字转中文汉字
     *
     * @param number 数字
     */
    public static String formatNumberToChinaLong(long number) {
        if (number <= 0)
            return "";
        else if (number < 10) {
            return ChinaNumbers[(int) (number - 1)];
        } else if (number < 20) {
            if (number == 10)
                return ChinaNumbersWei[0];
            else {
                return ChinaNumbersWei[0] + ChinaNumbers[(int) (number % 10 - 1)];
            }

        } else if (number < 100) {
            return ChinaNumbers[(int) (number / 10 - 1)] + ChinaNumbersWei[0] + (number % 10 == 0 ? "" : ChinaNumbers[(int) (number % 10 - 1)]);
        } else if (number < 100000) {
            long length = String.valueOf(number).length();
            String key = ChinaNumbersWei[(int) (length - 2)];
            long a = number / (int) (Math.pow(10, length - 1));
            long b = number % (int) (Math.pow(10, length - 1));
            return ChinaNumbers[(int) (a - 1)] + key +
              (b < 10 ? "零" : b < 20 ? "一" : "") + formatNumberToChinaLong(b);
        } else {
            long length = String.valueOf(number).length();
            long i = 5 + (length - 5) / 4;
            String key = ChinaNumbersWei[(int) (i - 2)];
            long c = 5 + (length - 5) / 4 * 4;
            long a = number / (long) (Math.pow(10, c - 1));
            long b = number % (long) (Math.pow(10, c - 1));
            return formatNumberToChinaLong(a) + key + (b < 10 ? "零" : "") + formatNumberToChinaLong(b);
        }
    }


    /**
     * byte数组转16进制字符
     *
     * @param hash byte数组
     * @return 字符
     */
    public static String byteToHexString(byte[] hash) {
        if (hash == null)
            return null;
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }


}
