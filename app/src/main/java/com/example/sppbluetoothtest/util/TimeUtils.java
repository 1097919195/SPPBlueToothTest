package com.example.sppbluetoothtest.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/12/20.
 */

public class TimeUtils {
    /*垃圾袋二维码*/
    public static String getTimeStr() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        String strTime = simpleDateFormat.format(date);
        //Log.e("time",strTime);
        return strTime;
    }


    /*年月日 格式：yyyy-MM-dd HH:mm:ss*/
    public static String getYMDTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        String strTime = simpleDateFormat.format(date);
        //  Log.e("time",strTime);
        return strTime;
    }

    public static String getTimestampToString(Timestamp timestamp) {
        String time = timestamp.toString();
        if (time.indexOf(".") > -1) {
            time = time.split("\\.")[0];
        }
        return time;
    }


    public static String getTimestampToSubmitString(Timestamp timestamp) {
        String time = "";
        if (timestamp != null) {
            time = timestamp.toString();
            int index = time.indexOf(".");
            if (index > -1) {
                time = time.substring(0, index);
            }
        }
        return time;
    }
}
