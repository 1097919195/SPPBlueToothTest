package com.example.sppbluetoothtest.serialization;

public class Decoder04 {
    //称重
    private String weight;
    //称重带kg单位
    private String weightUnitStr;
    //秤状态
    private String scales;
    //电池电压
    private String voltage;
    //温度
    private String temperature;
    //GPS经度
    private String longitude;
    //GPS维度
    private String latitude;


    public void setWeightUnitStr(String weightUnitStr) {
        this.weightUnitStr = weightUnitStr;
    }

    public String getWeightUnitStr() {
        return weightUnitStr;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getScales() {
        return scales;
    }

    public void setScales(String scales) {
        this.scales = scales;
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

    @Override
    public String toString() {
        return "Decoder04{" +
                "weight='" + weightUnitStr + '\'' +
                ", scales='" + scales + '\'' +
                ", voltage='" + voltage + '\'' +
                ", temperature='" + temperature + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                '}';
    }
}
