package com.example.sppbluetoothtest;

import org.junit.Test;

public class ExampleTest {
    int data = 0xFF6a;
    String code = "0087";

    @Test
    public void test() {
        int i = (short)data;
        float temp = (short) Integer.parseInt(code, 16);
        System.err.println(Integer.toString(data, 16));
        System.err.println(i);
        System.err.println(temp/10);
    }
}
