package com.example.sppbluetoothtest.util;

/**
 * Created by Administrator on 2018/12/28.
 */

public class StrUtils {
    public static String removePoint(String str){
        String strs="";
        if(str!=null){
            if(!str.isEmpty()){
                if(str.indexOf(".")>-1){
                    strs=str.split("\\.")[0];
                }
            }
        }
        return strs;
    }
}
