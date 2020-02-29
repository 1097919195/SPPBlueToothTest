package com.example.sppbluetoothtest.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sppbluetoothtest.R;
import com.example.sppbluetoothtest.app.MyApplication;
import com.example.sppbluetoothtest.library.BluetoothSPP;
import com.example.sppbluetoothtest.library.BluetoothState;
import com.example.sppbluetoothtest.serialization.DecoderRME60A;
import com.example.sppbluetoothtest.serialization.DecoderRME60ABeen;
import com.example.sppbluetoothtest.util.SerializeUtil;
import com.example.sppbluetoothtest.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;

public class BlueActivity extends AppCompatActivity {

    MyApplication myApplication = null;
    BluetoothAdapter mBluetoothAdapter = null;
    BluetoothSPP bt = null;
    String bindMac = "";
    private Object readLockObject = new Object();
    private Object sendLockObject = new Object();
    boolean stopAuto;//退出后连接断开的标志
    boolean stopRealInfoGet;//连接时暂停数据获取
    TextView text, state, infoCountdown, infoError, maxTime, successTotal;
    Button startTest;
    boolean print_mode = false;
    Handler handler = new Handler();
    int nFail = 1;
    int maxConsume = 1;
    int allTime = 10;
    int countdown = allTime;
    List<String> macList = new ArrayList<>();
    int index = 0;
    int successWith1 = 0;
    int successWith2 = 0;
    int successWith2_4 = 0;
    int successWith4_6 = 0;
    int successWith6_10 = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.text);
        state = findViewById(R.id.state);
        infoCountdown = findViewById(R.id.infoCountdown);
        infoError = findViewById(R.id.infoError);
        maxTime = findViewById(R.id.maxTime);
        successTotal = findViewById(R.id.successTotal);
        startTest = findViewById(R.id.startTest);
        myApplication = (MyApplication) getApplication();
        InitScannerNewLoad();
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

        macList.add("20:19:08:20:17:57");
        macList.add("20:19:08:20:29:87");
//        macList.add("A0:14:F2:99:01:4E");//1
//        macList.add("A0:14:F2:99:01:BD");//2
//        macList.add("A0:14:F2:99:01:48");//3
//        macList.add("A0:14:F2:99:01:4B");//4
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            }
        }

        initLinstener();

    }

    private void initLinstener() {
        startTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer == null) {
                    startTimer();
                    ToastUtil.showLong("开启测试");
                } else {
                    ToastUtil.showLong("已经处于测试状态");
                }
            }
        });
    }

    private void InitScannerNewLoad() {
        IntentFilter filter = new IntentFilter("nlscan.action.SCANNER_RESULT");
        registerReceiver(mNewLoadReceiver, filter);
    }

    private BroadcastReceiver mNewLoadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String scanResult = intent.getStringExtra("SCAN_BARCODE1");
            final String scanStatus = intent.getStringExtra("SCAN_STATE");
            if ("ok".equals(scanStatus)) {
                if (stringIsMac(scanResult)) {
                    bindMac = scanResult;
                    connectWithScan();
                } else {
                    ToastUtil.showShort("MAC地址格式不正确！！！");
                }
            } else {
                ToastUtil.showShort("扫描失败了");
            }

        }
    };

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
                connectWithScan();
            } else {
                ToastUtil.showShort("MAC地址格式不正确！！！");
            }

        }
    };

    private void connectWithScan() {
        if (!bindMac.equals("")) {
            state.setText("扫描成功，通讯发起中...");
            stopRealInfoGet = true;
            if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                bt.disconnect();
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                        ToastUtil.showLong("请等待连接断开后再试...");
                    } else {
                        bt.connect(bindMac);
                        if (countDownTimer != null) {
                            countdown = allTime;
                            countDownTimer.start();
                        }
                    }
                }
            }, 1000);
        }
    }

    Timer timer = null;
    TimerTask timerTask = null;
    Timer sendrealtimemessagetimer = null;
    TimerTask sendrealtimemessagetimertimerTask = null;

    private void startTimer() {
        if (timer == null) {
            timer = new Timer();
        }
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
//                            if (index == 0) {
//                                bindMac = macList.get(0);
//                                index = 1;
//                            } else {
//                                bindMac = macList.get(1);
//                                index = 0;
//                            }
//                            connectWithScan();
                        }
                    });
                }
            };
            timer.schedule(timerTask, allTime * 1000, allTime * 1000);
        }

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
//                            ret = BluetoothSendData(SerializeUtil.hexStringToByteArray("AA550403BB66"));
                            ret = BluetoothSendData(SerializeUtil.hexStringToByteArray("A5A50AB6B6"));
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
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
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
                    if (!stopRealInfoGet) {
                        state.setText("收集信息:\n" + SerializeUtil.byteArrayToHexString(data));

//                        decoderBlueToothData(SerializeUtil.byteArrayToHexString(data));
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
                //连接成功后记录最大的消耗时间
                int thisConsume = allTime - countdown;
                if (maxConsume < thisConsume) {
                    maxConsume = thisConsume;
                    maxTime.setText("当前连接成功后的最大耗时:" + maxConsume);
                } else {
                    maxTime.setText("当前连接成功后的最大耗时:" + maxConsume);
                }

                if (thisConsume <= 1) {
                    successWith1 = successWith1 + 1;
                } else if (thisConsume <= 2) {
                    successWith2 = successWith2 + 1;
                } else if (thisConsume <= 4) {
                    successWith2_4 = successWith2_4 + 1;
                } else if (thisConsume <= 6) {
                    successWith4_6 = successWith4_6 + 1;
                } else {
                    successWith6_10 = successWith6_10 + 1;
                }
                successTotal.setText("1秒内成功的次数:" + successWith1 + "\n2秒内成功的次数:" + successWith2
                        + "\n2-4秒内成功的次数:" + successWith2_4 + "\n4-6秒内成功的次数:" + successWith4_6 + "\n6-10秒内成功的次数:" + successWith6_10);

                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
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

    private void decoderBlueToothData(String data) {
        DecoderRME60ABeen decoder = DecoderRME60A.decoder(data);
        state.setText("清理重量：" + decoder.getWeightUnitStr()
                + "实时重量：" + decoder.getWeightRealUnitStr()
                + "重量系数：" + decoder.getWeightRatio()
                + "电压：" + decoder.getVoltage()
                + "温度：" + decoder.getTemperature());
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
        unregisterReceiver(mNewLoadReceiver);
        unregisterReceiver(mCanNFCReceiver);
        stopTimer();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        stopAuto = true;
        bt.stopService();
        bt.disconnect();
    }


    private CountDownTimer countDownTimer = new CountDownTimer(allTime * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            countdown = (int) (millisUntilFinished / 1000);
            infoCountdown.setText("当前倒计时间:" + countdown);
        }

        @Override
        public void onFinish() {
            infoError.setText("失败次数:" + nFail++);
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
        }
    };
}
