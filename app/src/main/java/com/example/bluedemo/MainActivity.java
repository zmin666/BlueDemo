package com.example.bluedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;

import java.util.ArrayList;

import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_DISCONNECTED;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_BT = 1012;
    private RecyclerView rvMain;
    private BluetoothClient bluetoothClient;
    private ArrayList<SearchResult> mData = new ArrayList<>();
    private RVAdapter rvAdapter;

    //蓝牙开启关闭 监听
    private final BluetoothStateListener mBluetoothStateListener = new BluetoothStateListener() {
        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {
            if (openOrClosed) {
                scan();
            }
        }
    };

    //蓝牙连接 监听
    private final BleConnectStatusListener mBleConnectStatusListener = new BleConnectStatusListener() {

        @Override
        public void onConnectStatusChanged(String mac, int status) {
            if (status == STATUS_CONNECTED) {
                Toast.makeText(MainActivity.this, "连接了", Toast.LENGTH_SHORT).show();
            } else if (status == STATUS_DISCONNECTED) {
                Toast.makeText(MainActivity.this, "断开了", Toast.LENGTH_SHORT).show();
            }
        }
    };

    //蓝牙搜索
    SearchResponse response = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            setTitle("寻找蓝牙设备中...");
            mData.clear();
            rvAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDeviceFounded(SearchResult device) {
            //5D:BC:99:BA:15:CE
//            if (!mData.contains(device) && device.getAddress().contains("CB:43")) {
            if (!mData.contains(device) && device.getAddress().contains("5D:BC:99:")) {
                mData.add(device);
                rvAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onSearchStopped() {
            setTitle("设备列表");
        }

        @Override
        public void onSearchCanceled() {
            setTitle("设备列表");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvMain = (RecyclerView) findViewById(R.id.rv_main);
        rvMain.setLayoutManager(new LinearLayoutManager(this));
        rvAdapter = new RVAdapter(this, mData);
        rvMain.setAdapter(rvAdapter);
        //判断是否有访问位置的权限，没有权限，直接申请位置权限
        checkBluetoothAndLocationPermission();
    }

    private void checkBluetoothAndLocationPermission() {
        if ((checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_BT);
        } else {
            bluetoothClient = BluetoothClientManager.getClient();
            openBlueAndScan();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_BT:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    bluetoothClient = BluetoothClientManager.getClient();
                    openBlueAndScan();
                } else {
                    Toast.makeText(this, "没有获取到权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void openBlueAndScan() {
        if (bluetoothClient.isBluetoothOpened()) {
            scan();
        } else {
            bluetoothClient.registerBluetoothStateListener(mBluetoothStateListener);
            bluetoothClient.openBluetooth();
        }
    }

    private void scan() {
        SearchRequest request = new SearchRequest.Builder().searchBluetoothLeDevice(5000, 1).build();
        bluetoothClient.search(request, response);
    }

    @Override
    protected void onDestroy() {
        bluetoothClient.unregisterBluetoothStateListener(mBluetoothStateListener);
        super.onDestroy();
    }




    public void click_refresh(View view) {
        openBlueAndScan();
    }

    @Override
    protected void onStop() {
        super.onStop();
        bluetoothClient.stopSearch();
    }
}
