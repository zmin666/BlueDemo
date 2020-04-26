package com.example.bluedemo;

import com.inuker.bluetooth.library.BluetoothClient;

/**
 * @author: ZhangMin
 * @date: 2020/4/16 16:56
 * @version: 1.0
 * @desc:
 */
public class BluetoothClientManager {
    private static BluetoothClient mClient;

    private BluetoothClientManager() {
    }

    public static BluetoothClient getClient() {
        if (mClient == null) {
            mClient = new BluetoothClient(BaseApp.getInstance());
        }
        return mClient;
    }
}
