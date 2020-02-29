package com.example.sppbluetoothtest.serialization;


/**
 * Created by Administrator on 2019/8/22.
 */


public class DecoderRME60ABeen {
    //清理重量
    private String weight;
    //清理重量带kg单位
    private String weightUnitStr;
    //实时重量
    private String weightReal;
    //实时重量带kg单位
    private String weightRealUnitStr;

    //重量系数
    private String weightRatio;
    //电池电压
    private String voltage;
    //温度
    private String temperature;

    //GPS经度
    private String longitude;
    //GPS维度
    private String latitude;


    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWeightUnitStr() {
        return weightUnitStr;
    }

    public void setWeightUnitStr(String weightUnitStr) {
        this.weightUnitStr = weightUnitStr;
    }

    public String getWeightReal() {
        return weightReal;
    }

    public void setWeightReal(String weightReal) {
        this.weightReal = weightReal;
    }

    public String getWeightRealUnitStr() {
        return weightRealUnitStr;
    }

    public void setWeightRealUnitStr(String weightRealUnitStr) {
        this.weightRealUnitStr = weightRealUnitStr;
    }

    public String getWeightRatio() {
        return weightRatio;
    }

    public void setWeightRatio(String weightRatio) {
        this.weightRatio = weightRatio;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
