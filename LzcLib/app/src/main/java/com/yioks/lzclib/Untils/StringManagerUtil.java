package com.yioks.lzclib.Untils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.yioks.lzclib.R;

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

            Picasso.with(context).load(R.drawable.holder).centerCrop().fit().into(imageView);
            return false;
        }
        return true;
    }

    public static String CheckEmpty(String string) {
        if (string == null || string.trim().equals("")) {
            return "未知";
        }
        return string;
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
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean VerifyNumber(String number)
    {
        if (number == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }

}
