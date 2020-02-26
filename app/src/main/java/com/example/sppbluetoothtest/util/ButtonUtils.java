package com.example.sppbluetoothtest.util;

import android.util.Log;

/**
 * Created by Administrator on 2018/5/16.
 */

public class ButtonUtils {

    private static long lastClickTime = 0;
    private static long DIFF = 1000;
    private static int lastButtonId = -1;

    /**
     * 判断两次点击的间隔，如果小于1000，则认为是多次无效点击
     *
     * @return
     */
    public static boolean isFastDoubleClick(int buttonId) {
        return isFastDoubleClick(buttonId, DIFF);
    }

    /**
     * 判断两次点击的间隔，如果小于diff，则认为是多次无效点击
     *
     * @param diff
     * @return
     */
    public static boolean isFastDoubleClick(int buttonId, long diff) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (lastButtonId == buttonId && lastClickTime > 0 && timeD < diff) {
            Log.e("isFastDoubleClick", "短时间内按钮多次触发");
            return true;
        }
        lastClickTime = time;
        lastButtonId = buttonId;
        return false;
    }


    final static long lDefaultTime=5*60*1000;
    public static boolean isFastDoubleClicks(long diff) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if(diff==0)
            diff=lDefaultTime;
        if (lastClickTime > 0 && timeD < diff) {
          //  Log.e("isFastDoubleClick", "当前时间："+String.valueOf(time)+"最后一次时间："+String.valueOf(timeD));
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /*private static long tmpDIFF = 2000;
    private static long lastClickTimes = 0;
    public static boolean isFastDoubleClick(){
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTimes;
        if(lastClickTimes > 0 && timeD < tmpDIFF)
            return true;
        return false;
    }*/
}
