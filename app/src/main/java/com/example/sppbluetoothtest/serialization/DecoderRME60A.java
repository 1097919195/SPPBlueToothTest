package com.example.sppbluetoothtest.serialization;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2019/8/22.
 */

public class DecoderRME60A {

    public static DecoderRME60ABeen decoder(String decoderdata) {
        DecoderRME60ABeen decoder = new DecoderRME60ABeen();
        //清理重量
        DecimalFormat df = new DecimalFormat("0.00");
//        df.setRoundingMode(RoundingMode.DOWN);
        try {
            int weight = (int) Long.parseLong(decoderdata.substring(6, 14), 16);
            String tmpWeight = df.format((float) (weight) / 1000);
            decoder.setWeight(tmpWeight);
            decoder.setWeightUnitStr(tmpWeight + "KG");
        } catch (Exception e) {
            decoder.setWeight("0");
            e.printStackTrace();
        }
        //实时重量
        try {
            int weight = (int) Long.parseLong(decoderdata.substring(14, 22), 16);
            String tmpWeight = df.format((float) (weight) / 1000);
            decoder.setWeightReal(tmpWeight);
            decoder.setWeightRealUnitStr(tmpWeight + "KG");
        } catch (Exception e) {
            decoder.setWeight("0");
            e.printStackTrace();
        }

        //重量系数
        try {
            int ratio = (int) Long.parseLong(decoderdata.substring(22, 26), 16);
            decoder.setWeightRatio(String.valueOf(ratio));
        } catch (Exception e) {
            decoder.setWeightRatio("-1");
            e.printStackTrace();
        }

        //电压（除以10）
        try {
            df = new DecimalFormat("0.0");
            int voltage = (int) Long.parseLong(decoderdata.substring(26, 30), 16) / 10;
            decoder.setVoltage(df.format(voltage));
        } catch (Exception e) {
            decoder.setVoltage("-1");
            e.printStackTrace();
        }

        //温度(要除以10)
        try {
            df = new DecimalFormat("0.0");
            int temp = (int) Long.parseLong(decoderdata.substring(30, 34), 16) / 10;
            decoder.setTemperature(df.format(temp));
        } catch (Exception e) {
            decoder.setTemperature("0");
            e.printStackTrace();
        }

//        //经度(一般都是高字节在前，低字节在后，这个经纬度刚好相反,浮点类型得使用intBitsToFloat)
//        try {
//            String longitudeStr = decoderdata.substring(66, 68) + decoderdata.substring(64, 66) + decoderdata.substring(62, 64) + decoderdata.substring(60, 62);
////            Float longitude = Float.intBitsToFloat(Integer.valueOf(decoderdata.substring(60, 68), 16));
//            Float longitude = Float.intBitsToFloat(Integer.valueOf(longitudeStr, 16));
//            decoder.setLongitude(String.valueOf(longitude));
//        } catch (Exception e) {
//            decoder.setLongitude("-1");
//            e.printStackTrace();
//        }
//
//        //纬度
//        try {
//            String latitudeStr = decoderdata.substring(74, 76) + decoderdata.substring(72, 74) + decoderdata.substring(70, 72) + decoderdata.substring(68, 70);
////            Float latitude = Float.intBitsToFloat(Integer.valueOf(decoderdata.substring(68, 76), 16));
//            Float latitude = Float.intBitsToFloat(Integer.valueOf(latitudeStr, 16));
//            decoder.setLatitude(String.valueOf(latitude));
//        } catch (Exception e) {
//            decoder.setLatitude("-1");
//            e.printStackTrace();
//        }

        return decoder;
    }
}
