package com.example.bluedemo;

import android.net.UrlQuerySanitizer;
import android.text.TextUtils;

import com.inuker.bluetooth.library.utils.ByteUtils;

import java.util.Calendar;

/**
 * @author: ZhangMin
 * @date: 2020/9/8 15:46
 * @version: 1.0
 * @desc:
 */
class DataUtils {

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String getTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int yy1 = year / 100;
        int yy2 = year - yy1 * 100;
        StringBuffer sbu = new StringBuffer();
        sbu.append("01");
        sbu.append(yy1);
        sbu.append(yy2);
        if (month < 10) {
            sbu.append("0");
        }
        sbu.append(month);
        if (day < 10) {
            sbu.append("0");
        }
        sbu.append(day);
        if (hour < 10) {
            sbu.append("0");
        }
        sbu.append(hour);
        if (minute < 10) {
            sbu.append("0");
        }
        sbu.append(minute);
        if (second < 10) {
            sbu.append("0");
        }
        sbu.append(second);
        sbu.append("07");
        sbu.append("19");
        return sbu.toString();
    }


    /**
     * 写入字母之类的信息
     * @param prefix 服务类型
     * @param value  服务数据
     * @return
     */
    public static byte[] stringToAscii(String prefix, String value) {
        StringBuffer sbu = new StringBuffer();
        sbu.append(prefix);
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            sbu.append(Integer.toHexString((int) chars[i]));
        }
        return ByteUtils.stringToBytes(sbu.toString());
    }


    public static byte[] stringToAscii2(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            sbu.append(Integer.toHexString((int) chars[i]));
        }
        return ByteUtils.stringToBytes(sbu.toString());
    }


    /**
     * 写入纯数字信息
     * @param text
     * @return
     */
    public static byte[] stringToBytes(String text) {
        int len = text.length();
        byte[] bytes = new byte[(len + 1) / 2];
        for (int i = 0; i < len; i += 2) {
            int size = Math.min(2, len - i);
            String sub = text.substring(i, i + size);
            bytes[i / 2] = (byte) Integer.parseInt(sub);
        }
        return bytes;
    }

    public static byte[] getByteTime() {
        return stringToBytes(getTime());
    }


    public static String asciiToString(String value) {
        if (TextUtils.isEmpty(value)) {
            return "无效数据";
        }

        StringBuffer sbu1 = new StringBuffer();
        int length = value.length();
        for (int i = 0; i < length; i += 2) {
            sbu1.append(value.substring(i, i + 2));
            sbu1.append(",");
        }
        String s = sbu1.toString();


        StringBuffer sbu = new StringBuffer();
        String[] chars = s.split(",");
        for (int i = 0; i < chars.length; i++) {
            int j = Integer.parseInt(chars[i]);
            sbu.append(j == 0 ? 0 : j - 30);
        }
        return sbu.toString();
    }

}