package com.example.sppbluetoothtest.messagequeue;

import android.os.Handler;
import android.util.Log;

import com.example.sppbluetoothtest.library.BluetoothSPP;
import com.example.sppbluetoothtest.library.BluetoothState;
import com.example.sppbluetoothtest.util.SerializeUtil;

import static java.lang.Thread.sleep;

/**
 * 队列消息处理实现
 *
 * @author hp
 */
public class PushBlockQueueHandler implements Runnable {

    private Object obj;
    BluetoothSPP serialPort;
    Handler handler;
    private Object sendLockObject = new Object();

    public PushBlockQueueHandler(Object obj, BluetoothSPP serialPort, Handler handler) {
        this.obj = obj;
        this.serialPort = serialPort;
        this.handler = handler;
    }

    @Override
    public void run() {
        doBusiness();
    }

    /**
     * 业务处理时限
     */
    public void doBusiness() {
        Log.e("处理请求:", obj.toString());
        try {
            //串口的
//            serialPort.sendData(obj.toString(), "HEX");
//            Thread.sleep(200);//发完等待一下，直接读取可能会存在内存中
//            int size = serialPort.receiveData(readData);
//            if (size > 0) {
//                int tmpSize = size * 2;
//                serialData = SerializeUtil.byteArrayToHexString(readData).substring(0, tmpSize);
//                Log.e("test", "接收到串口数据:" + serialData);
//                msg = new Message();
//                msg.what = AppConstant.READ_TIME_CODE;
//                msg.obj = serialData;
//                handlers.sendMessage(msg);
//            }
//            obj = null;

            //蓝牙的
            BluetoothSendData(SerializeUtil.hexStringToByteArray(obj.toString()));
            obj = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean BluetoothSendData(byte[] data) {
        if (serialPort != null && serialPort.getServiceState() == BluetoothState.STATE_CONNECTED) {
            synchronized (sendLockObject) {
                serialPort.send(data, false);
                try {
                    sleep(100);//保证cpu的工作 发完等待一下
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }


}
