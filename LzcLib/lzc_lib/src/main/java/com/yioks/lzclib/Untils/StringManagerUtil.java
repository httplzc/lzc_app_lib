package com.yioks.lzclib.Untils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理类
 * Created by Yioks-ZhangMengzhen on 2016/5/21.
 */
public class StringManagerUtil {


    public static String[] xingqi = new String[]{"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    public static String[] xingqi_byzhouyi = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    /**
     * MD5字符加密(小写)
     *
     * @param string 需要加密的字符串
     * @return
     */
    public static String md5(String string) {
        byte[] hash;
        if (string == null)
            string = "";
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();// toUpperCase() 把小写字符串改为大写
    }

    public static String getStringFromRaw(Context context, int id) {
        InputStream inputStream = context.getResources().openRawResource(id);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return stringBuilder.toString();
    }

    public static String trimEmpty(String string) {
        if (string == null)
            return null;
        Pattern pattern = Pattern.compile(" ");
        Matcher matcher = pattern.matcher(string);
        return matcher.replaceAll("");
    }


    /**
     * 对字符串数据进行SHA1运算
     *
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String sha1(String data) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA1");

        md.update(data.getBytes());

        StringBuffer buf = new StringBuffer();

        byte[] bits = md.digest();

        for (int i = 0; i < bits.length; i++) {

            int a = bits[i];

            if (a < 0)
                a += 256;

            if (a < 16)
                buf.append("0");

            buf.append(Integer.toHexString(a));

        }

        return buf.toString();

    }

    /**
     * 判断图片路径格式
     *
     * @param str
     * @return
     */
    public static boolean isHttp(String str) {
        if (str.indexOf("http") != -1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 字符串分割
     *
     * @param str   要分隔的字符
     * @param split 分割标识
     * @return
     */
    public static String[] stringSplit(String str, String split) {
        if (isNullAndEmpty(str)) {
            return null;
        } else {
            String[] mapList = str.split(split);
            return mapList;
        }
    }

    /**
     * 判断某个字符串是否存在于数组中
     *
     * @param StrArray 原数组
     * @param source   查找的字符串
     * @return 是否找到
     */
    public static boolean contains(String[] StrArray, String source) {
        List<String> tempList = Arrays.asList(StrArray);
        if (tempList.contains(source)) {
            return true;
        } else {
            return false;
        }
    }


    public static boolean isNullAndEmpty(String str) {
        if (str == null)
            return true;
        else if (str.length() <= 0) {
            return true;
        } else
            return false;
    }

    public static String getCurrentTime() {
        String string = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        string = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        return string;
    }


    public static String DateToWeek(int year, int mouth, int day) throws Exception {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(year, mouth, day);

        int dayIndex = gregorianCalendar.get(Calendar.DAY_OF_WEEK);
        boolean isFirstSunday = (gregorianCalendar.getFirstDayOfWeek() == Calendar.SUNDAY);
        if (isFirstSunday) {
            return xingqi[dayIndex - 1];
        } else {
            return xingqi_byzhouyi[dayIndex - 1];
        }

    }

    public static int DateToWeekIndex(int year, int mouth, int day) throws Exception {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(year, mouth, day);

        int dayIndex = gregorianCalendar.get(Calendar.DAY_OF_WEEK);
        return dayIndex;
    }

    public static boolean checkImgEmpty(Context context, String url, ImageView imageView) {
        if (url == null || url.trim().equals("")) {

            //   Picasso.with(context).load(R.drawable.holder).centerCrop().fit().into(imageView);
            return false;
        }
        return true;
    }

    public static String CheckEmpty(String string) {
        if (string == null || string.trim().equals("")) {
            return "";
        }
        return string;
    }

    public static Uri resToUri(int res, Resources r) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + r.getResourcePackageName(res) + "/"
                + r.getResourceTypeName(res) + "/"
                + r.getResourceEntryName(res));
    }

    public static Uri resToUriFresco(int res, Resources r) {
        return Uri.parse("res://"
                + r.getResourcePackageName(res) + "/" + res);
    }

    public static Uri fileToUri(File file) {
        return Uri.fromFile(file);
    }

    public static boolean CheckNull(String string) {
        return string == null || string.trim().equals("");
    }

    public static boolean VerifyPhone(String phone) {
        if (phone == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("^1(3[0-9]|4[57]|5[0-35-9]|7[01678]|8[0-9])\\d{8}$");
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }


    public static boolean VerifyEmail(String email) {
        if (email == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$");
        Matcher matcher = pattern.matcher(email.trim());
        return matcher.matches();
    }

    public static boolean VerifyNumber(String number) {
        if (number == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(number.trim());
        return matcher.matches();
    }

    public static String getTimeFromDate(String date) {
        Pattern pattern = Pattern.compile("[0-9]{2}:[0-9]{2}:[0-9]{2}$");
        Matcher matcher = pattern.matcher(date);
        if (matcher.find()) {
            return matcher.group(0);
        } else {
            return "";
        }
    }

    public static String getXingqiFromDate(String date) throws Exception {

        String s[] = GetYearTime(date).split("-");
        return StringManagerUtil.DateToWeek(Integer.valueOf(s[0]), Integer.valueOf(s[1]) - 1, Integer.valueOf(s[2]));

    }


    public static String GetYearTime(String date) {
        Pattern pattern = Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}");
        Matcher matcher = pattern.matcher(date);
        if (matcher.find()) {
            String NYR = matcher.group(0);
            return NYR;
        } else {
            return "";
        }
    }

    private static String ChinaNumbers[] = {"一", "二", "三", "四", "五", "六", "七", "八", "九"};
    private static String ChinaNumbersWei[] = {"十", "百", "千", "万", "亿", "兆"};

    public static String formatNumberToChina(int number) {
        if (number <= 0)
            return "";
        else if (number < 10) {
            return ChinaNumbers[number - 1];
        } else if (number < 20) {
            if (number == 10)
                return ChinaNumbersWei[0];
            else {
                return ChinaNumbersWei[0] + ChinaNumbers[number % 10 - 1];
            }

        } else if (number < 100) {
            return ChinaNumbers[number / 10 - 1] + ChinaNumbersWei[0] + (number % 10 == 0 ? "" : ChinaNumbers[number % 10 - 1]);
        } else if (number < 100000) {
            int length = String.valueOf(number).length();
            String key = ChinaNumbersWei[length - 2];
            int a = number / (int) (Math.pow(10, length - 1));
            int b = number % (int) (Math.pow(10, length - 1));
            return ChinaNumbers[a - 1] + key +
                    (b < 10 ? "零" : b < 20 ? "一" : "") + formatNumberToChina(b);
        } else {
            int length = String.valueOf(number).length();
            int i = 5 + (length - 5) / 4;
            String key = ChinaNumbersWei[i - 2];
            int c = 5 + (length - 5) / 4 * 4;
            int a = number / (int) (Math.pow(10, c - 1));
            int b = number % (int) (Math.pow(10, c - 1));
            return formatNumberToChina(a) + key + (b < 10 ? "零" : "") + formatNumberToChina(b);
        }
    }


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


}
