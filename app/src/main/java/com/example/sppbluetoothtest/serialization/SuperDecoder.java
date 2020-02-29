package com.example.sppbluetoothtest.serialization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuperDecoder {

    public static byte[] bytes2List(byte[] testbuffer) {
        byte[] dest=new byte[1];
        ArrayList<Byte> arr_byte = new ArrayList<Byte>();
        boolean flg = false;
        for (int i = 0; i < testbuffer.length; i++) {
            if (testbuffer[i] == (byte)0xAA) {
                flg = true;
                arr_byte = new ArrayList<Byte>();
            }
            if (flg) {
                if (testbuffer[i] == (byte)0x66) {
                    arr_byte.add(testbuffer[i]);
                    dest = new byte[arr_byte.size()];
                    for (int j = 0; j < arr_byte.size(); j++) {
                        dest[j] = arr_byte.get(j);
                    }
                    return dest;
                } else {
                    arr_byte.add(testbuffer[i]);
                }
            }
        }
        return dest;
    }
    public static byte[] last;
    public static ArrayList<Byte[]> bytes2ListPerfect(Byte[] testbuffer) {
        Byte[] dest=new Byte[1];
        ArrayList<Byte> arr_byte;
        ArrayList<Byte[]> list_byte = new ArrayList<Byte[]>();;
        boolean flg=false;
        //AA开始
        if (testbuffer[0] == (byte)0xAA) {
            arr_byte = new ArrayList<Byte>();
            flg=false;
            for (int i = 0; i < testbuffer.length; i++) {
                arr_byte.add(testbuffer[i]);
                if(!flg) {
                    if (testbuffer[i] == (byte) 0x66) {
                        dest = new Byte[arr_byte.size()];
                        for (int j = 0; j < arr_byte.size(); j++) {
                            dest[j] = arr_byte.get(j);
                        }
                        list_byte.add(dest);
                        flg = true;
                    }
                }else{

                }
            }

        }
        return list_byte;
    }

    public static String prev=null;
    public static List<String> str2list(String str){
        List<String> list;
        List<String> dstlist = new ArrayList<>();
        if(prev==null){
            list= Arrays.asList(str.split("BB66"));
        }else {
            list= Arrays.asList((prev+str).split("BB66"));
            prev=null;
        }
        for(int i=0;i<list.size();i++) {
            if (isOK(list.get(i))) {
                dstlist.add(list.get(i));
            }else{
                prev = list.get(i);
            }
        }
        return dstlist;
    }
    public static  boolean isOK(String ele){
        if(10==ele.length() || 82==ele.length()){
            return true;
        }
        return false;
    }

    public static  boolean getLength(String ele){
        if(10==ele.length() || 82==ele.length()){
            return true;
        }
        return false;
    }
}
