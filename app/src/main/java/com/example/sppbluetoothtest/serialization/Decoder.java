package com.example.sppbluetoothtest.serialization;

import java.text.DecimalFormat;

public class Decoder {
    public static boolean decoder03(String decoderdata) {
        // String decoderdata=SerializeUtil.byteArrayToHexString(data);
        if ("01".equals(decoderdata.substring(6, 8))) {
            return true;
        }
        return false;
    }

    public static boolean decoder81(String decoderdata) {
        // String decoderdata=SerializeUtil.byteArrayToHexString(data);
        if ("01".equals(decoderdata.substring(6, 8))) {
            return true;
        }
        return false;
    }

    public static Decoder04 decoder04(String decoderdata) {
        Decoder04 decoder04 = new Decoder04();

        //  String decoderdata = SerializeUtil.byteArrayToHexString(data);
        //称重
        DecimalFormat df = new DecimalFormat("0.00");
//        df.setRoundingMode(RoundingMode.DOWN);
        try {
            int weight = (int) Long.parseLong(decoderdata.substring(6, 14), 16);
            String tmpWeight = df.format((float) (weight) / 1000);
            decoder04.setWeight(tmpWeight);
            decoder04.setWeightUnitStr(tmpWeight + "KG");
            // decoder04.setWeight(df.format((float) Integer.valueOf(decoderdata.substring(6, 14), 16) / 1000) + "KG");
        } catch (Exception e) {
            decoder04.setWeight("0");
            e.printStackTrace();
        }
        try {
            //秤状态
            if ("01".equals(decoderdata.substring(14, 16))) {
                decoder04.setScales("出秤");
            } else if ("00".equals(decoderdata.substring(14, 16))) {
                decoder04.setScales("入秤");
            } else {
                decoder04.setScales("秤异常");
            }
        } catch (Exception e) {
            decoder04.setScales("0");
            e.printStackTrace();
        }

        try {
            //电池电压
            df = new DecimalFormat("0.0");
            decoder04.setVoltage(df.format((float) Integer.valueOf(decoderdata.substring(16, 20), 16) / 10));
        } catch (Exception e) {
            decoder04.setVoltage("0");
            e.printStackTrace();
        }
        try { //温度
            decoder04.setTemperature(df.format((float) Integer.valueOf(decoderdata.substring(20, 24), 16) / 10));
        } catch (Exception e) {
            decoder04.setTemperature("0");
            e.printStackTrace();
        }
//        try {
//            //GPS经度
//            String longitude = decoderdata.substring(30, 32) + decoderdata.substring(28, 30) + decoderdata.substring(26, 28) + decoderdata.substring(24, 26);
//            Float flongitude = Float.intBitsToFloat(Integer.valueOf(longitude, 16));
//            decoder04.setLongitude(flongitude.toString());
//        } catch (Exception e) {
//            decoder04.setLongitude("0");
//            e.printStackTrace();
//        }
//        try {
//            //GPS维度
//            String latitude = decoderdata.substring(38, 40) + decoderdata.substring(36, 38) + decoderdata.substring(34, 36) + decoderdata.substring(32, 34);
//            Float flatitude = Float.intBitsToFloat(Integer.valueOf(latitude, 16));
//            decoder04.setLatitude(flatitude.toString());
//        } catch (Exception e) {
//            decoder04.setLatitude("0");
//            e.printStackTrace();
//        }

        //因为这个便携式是没有经纬度的
//        if (MyApplication.getMyApplication().getLocation() != null) {
//            Log.e("dodo--经度测试", String.valueOf(MyApplication.getMyApplication().getLocation().getLongitude()));
//            decoder04.setLongitude(String.valueOf(MyApplication.getMyApplication().getLocation().getLongitude()));
//            decoder04.setLatitude(String.valueOf(MyApplication.getMyApplication().getLocation().getLatitude()));
////            historyinfoBeen.setLatitude(String.valueOf(MyApplication.getMyApplication().getLocation().getLatitude()));//纬度
////            historyinfoBeen.setLongitude(String.valueOf(MyApplication.getMyApplication().getLocation().getLongitude()));//经度
//        }else {
//            decoder04.setLongitude("0.0");
//            decoder04.setLatitude("0.0");
////            historyinfoBeen.setLatitude("0.0");//纬度
////            historyinfoBeen.setLongitude("0.0");//经度
//        }


        return decoder04;
    }

}
