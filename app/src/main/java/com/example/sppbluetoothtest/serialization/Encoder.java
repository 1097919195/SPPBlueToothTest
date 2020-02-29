package com.example.sppbluetoothtest.serialization;

public class Encoder {

    //命令码03
    //1.打印走空纸
    public static final String BLANKPRINT = "AA550300051A0C001B6999BB66";

    //去皮
    public static final String ZEROREP = "AA550201000000000100000000000004BB66";

    //投放门开关（01是主设备地址）
    public static volatile String OPEN_DOOR_THROW = "AA550201000000000081000000000004BB66";
    public static volatile String CLOSE_DOOR_THROW = "AA550201000000000080000000000004BB66";

    //开关灯
    public static volatile String OPEN_LAMP = "AA550201000000000000000000008104BB66";
    public static volatile String CLOSE_LAMP = "AA550201000000000000000000008004BB66";

    //清理门（只有开，关闭是手动的）
    public static volatile String OPEN_DOOR_CLEAR = "AA550201000000000000008100000004BB66";

    //读取实时信息（主机）
    public static volatile String REALINFO = "AA55010102BB66";

    public static String WEIGHT_RATIO = "AA550201000000000000000000000000BB66";

}
