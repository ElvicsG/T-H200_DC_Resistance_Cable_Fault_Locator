package com.kehui.t_h200.app;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.res.Configuration;
import android.util.Log;

import java.util.Locale;

import com.kehui.t_h200.utils.DES3Utils;
import com.kehui.t_h200.utils.MultiLanguageUtil;
import com.kehui.t_h200.utils.PrefUtils;

/**
 * Created by jwj on 2018/4/14.
 */
public class App extends Application {

    public static final String key = DES3Utils.MD5Encode("KH_Key_*", "").substring(3, 27).toUpperCase();//秘钥
    public static final byte[] keyBytes = DES3Utils.hexToBytes(DES3Utils.byte2hex(key.getBytes()));//24位密钥
    public final Locale Locale_Spanisch = new Locale("Es", "es", "");
    public static App instances;

    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    public void onCreate() {
        super.onCreate();

        MultiLanguageUtil.init(getApplicationContext());
        instances = this;
        MultiLanguageUtil.getInstance().updateLanguage(PrefUtils.getString(App.getInstances(), AppConfig.CURRENT_LANGUAGE, "follow_sys"));
//        switchLanguage(PrefUtils.getString(App.getInstances(), AppConfig.CURRENT_LANGUAGE, "ch"));

        //蓝牙通信socket
        bluetoothSocket = null;
        //远程蓝牙设备
        bluetoothDevice = null;
        //本地蓝牙设备    获得本设备的蓝牙适配器实例
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("aaaaaaaa","走我了");
        MultiLanguageUtil.getInstance().updateLanguage(PrefUtils.getString(App.getInstances(), AppConfig.CURRENT_LANGUAGE, "follow_sys"));
//        switchLanguage(PrefUtils.getString(App.getInstances(), AppConfig.CURRENT_LANGUAGE, "ch"));

    }

    public static App getInstances() {
        return instances;
    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

}
