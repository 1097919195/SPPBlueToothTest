package com.example.sppbluetoothtest.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sppbluetoothtest.R;
import com.example.sppbluetoothtest.app.MyApplication;
import com.example.sppbluetoothtest.library.BluetoothSPP;
import com.example.sppbluetoothtest.library.BluetoothState;
import com.example.sppbluetoothtest.util.SerializeUtil;
import com.example.sppbluetoothtest.util.ToastUtil;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;

public class BlueActivity extends AppCompatActivity {

    MyApplication myApplication = null;
    BluetoothAdapter mBluetoothAdapter = null;
    BluetoothSPP bt = null;
    String bindMac = "";
    String tipText = "";
    private Object readLockObject = new Object();
    private Object sendLockObject = new Object();
    boolean stopAuto;//退出后连接断开的标志
    boolean stopRealInfoGet;//连接时暂停数据获取
    TextView text, state;
    boolean print_mode = false;
    Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.text);
        state = findViewById(R.id.state);
        myApplication = (MyApplication) getApplication();
        InitScannerCanNFC();

        //判断蓝牙是否可用
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            ToastUtil.showShort("蓝牙不可用！");
            finish();
        } else if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

        //蓝牙可用的话在进行后续操作
        bt = new BluetoothSPP(MyApplication.getInstance());
        myApplication.setBluetoothSPP(bt);
        InitBluetooth();

        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            }
        }

    }

    private void InitScannerCanNFC() {
        IntentFilter filter = new IntentFilter("android.intent.ACTION_DECODE_DATA");
        registerReceiver(mCanNFCReceiver, filter);
    }

    private BroadcastReceiver mCanNFCReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String scanResult = intent.getStringExtra("barcode_string");
            if (stringIsMac(scanResult)) {
                bindMac = scanResult;
                if (!bindMac.equals("")) {
                    state.setText("扫描成功，通讯发起中...");
                    stopRealInfoGet = true;
                    if (bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                        bt.disconnect();
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                                ToastUtil.showLong("请等待连接断开后再试...");
                            }else {
                                bt.connect(bindMac);
                            }
                        }
                    }, 1000);
                }
            } else {
                ToastUtil.showShort("MAC地址格式不正确！！！");
            }

        }
    };

    Timer sendrealtimemessagetimer = null;
    TimerTask sendrealtimemessagetimertimerTask = null;

    private void startTimer() {
        if (sendrealtimemessagetimer == null) {
            sendrealtimemessagetimer = new Timer();
        }
        if (sendrealtimemessagetimertimerTask == null) {
            sendrealtimemessagetimertimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (bt != null && bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                        boolean ret;
                        if (print_mode) {
                            ret = BluetoothSendData(SerializeUtil.hexStringToByteArray("024103"));
                        } else {
                            ret = BluetoothSendData(SerializeUtil.hexStringToByteArray("AA550403BB66"));
                        }
                        if (ret == false) {
                            Log.e("LoginAct", "发送失败");
//                            lister.onBlueReadTimeCode(new byte[]{(byte) 0xaa, 0x55, 0x04, 0x00, 0x00, 0x00});
                        } else {
//                            Log.e("LoginAct", "发送成功" + String.valueOf(suc++));
                        }
                    } else {
//                        lister.onBlueReadTimeCode(new byte[]{(byte) 0xaa, 0x55, 0x04, 0x00, 0x00, 0x00});
                        Log.e("LoginAct", "断开了");
                    }
                }
            };
            sendrealtimemessagetimer.schedule(sendrealtimemessagetimertimerTask, 500, 250);
        }
    }

    private void stopTimer() {
        if (sendrealtimemessagetimer != null) {
            sendrealtimemessagetimer.purge();
            sendrealtimemessagetimer.cancel();
            sendrealtimemessagetimer = null;
        }
        if (sendrealtimemessagetimertimerTask != null) {
            sendrealtimemessagetimertimerTask.cancel();
            sendrealtimemessagetimertimerTask = null;
        }
    }

    public boolean BluetoothSendData(byte[] data) {
        if (bt != null && bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
            synchronized (sendLockObject) {
                bt.send(data, false);
                if (data.length > 100) {//保证cpu的工作
                    try {
                        sleep(data.length);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        }
        return false;
    }


    //初始化蓝牙监听，自动连接，数据监听
    private void InitBluetooth() {
        if (!bt.isBluetoothAvailable()) {
            ToastUtil.showShort("蓝牙尚未打开，启动失败");
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                synchronized (readLockObject) {
                    Log.e("LoginAct", "onDataReceived" + SerializeUtil.byteArrayToHexString(data));
                    if (!stopRealInfoGet){
                        state.setText("收集信息:\n" + SerializeUtil.byteArrayToHexString(data));
                    }
//                    lister.onBlueReadTimeCode(data);//蓝牙连接后的实时信息发送
                }
            }
        });

        //该回调处理断开 失败等监听
        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            @Override
            public void onDeviceDisconnected() {
                ToastUtil.showShort("蓝牙设备连接断开");
                Log.i("LoginAct", "onDeviceDisconnected断开的回调");
                if (!bindMac.equals("") && !stopAuto && !stopRealInfoGet) {
                    bt.connect(bindMac);
                }
            }

            @Override
            public void onDeviceConnectionFailed() {
                ToastUtil.showShort("蓝牙设备连接失败");
                Log.i("LoginAct", "onDeviceConnectionFailed失败的回调");
                if (!bindMac.equals("") && !stopAuto) {
                    bt.connect(bindMac);
                }
            }

            @Override
            public void onDeviceConnected(String name, String address) {
                stopRealInfoGet = false;
                state.setText("连接成功，重量计算中...");
                startTimer();
                ToastUtil.showShort("蓝牙设备已连接");
                Log.i("LoginAct", "onDeviceConnected");
                text.setText(name + "\n" + address);
            }
        });

        //自动连接的监听
        bt.setAutoConnectionListener(new BluetoothSPP.AutoConnectionListener() {
            public void onNewConnection(String name, String address) {//断开后重新连接可在这里操作的
                Log.i("LoginAct", "New Connection - " + name + " - " + address);
            }

            public void onAutoConnectionStarted() {
                Log.i("LoginAct", "Auto onAutoConnectionStarted started");
            }
        });

        //状态监听
        bt.setBluetoothStateListener(new BluetoothSPP.BluetoothStateListener() {
            public void onServiceStateChanged(int state) {
                if (state == BluetoothState.STATE_CONNECTED) {
//                    ToastUtil.showShort("蓝牙设备已连接");
//                    ParamsConstant.SCAN_STATUS = "连接成功";
                } else if (state == BluetoothState.STATE_CONNECTING) {
                    ToastUtil.showShort("正在连接蓝牙...");
                }
            }
        });
    }

    /**
     * 校验当前的Mac地址是否是真的Mac地址
     *
     * @param strMac 需要校验的Mac地址
     * @return
     */
    public boolean stringIsMac(String strMac) {
        // 这是真正的Mac地址，正则表达式
//        String trueMacAddress = "([A-Fa-f0-9]{2}-){5}[A-Fa-f0-9]{2}";
        //不行的话可以试试下面这个
        String trueMacAddress = "^[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}$";
        if (strMac.matches(trueMacAddress)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mCanNFCReceiver);
        stopTimer();
        stopAuto = true;
        bt.stopService();
        bt.disconnect();
    }
}
