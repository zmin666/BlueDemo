package com.example.bluedemo;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;

import java.util.UUID;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;

/**
 * @author: ZhangMin
 * @date: 2020/4/17 10:49
 * @version: 1.0
 * @desc:
 */
public class DeviceActivity extends AppCompatActivity {
    private TextView tvtitle;
    private TextView tv;
    private RecyclerView rv;
    private BluetoothDevice mDevice;
    private BluetoothClient bluetoothClient;
    private boolean mConnected;
    private RVDetailAdapter rvDetailAdapter;


    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            BluetoothLog.v(String.format("DeviceDetailActivity onConnectStatusChanged %d in %s", status, Thread.currentThread().getName()));
            mConnected = (status == STATUS_CONNECTED);
            connectDeviceIfNeeded();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        tvtitle = (TextView) findViewById(R.id.tvtitle);
        rv = (RecyclerView) findViewById(R.id.rv);
        tv = (TextView) findViewById(R.id.tv);

        Intent intent = getIntent();
        String mac = intent.getStringExtra("mac");
        mDevice = BluetoothUtils.getRemoteDevice(mac);
        tvtitle.setText(mDevice.getAddress());
        bluetoothClient = BluetoothClientManager.getClient();

        rv.setLayoutManager(new LinearLayoutManager(this));
        rvDetailAdapter = new RVDetailAdapter(DeviceActivity.this,mDevice);
        rvDetailAdapter.setOnItemclickListener(new RVDetailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DetailItem detailItem1,DetailItem detailItem2) {
                if (!mConnected) {
                    return;
                }
                if (detailItem1.type == DetailItem.TYPE_CHARACTER && detailItem2.type == DetailItem.TYPE_CHARACTER) {
                    BluetoothLog.v(String.format("click service = %s, character = %s", detailItem1.service, detailItem1.uuid));
                    startCharacterActivity(detailItem1.service, detailItem1.uuid,detailItem2.service, detailItem2.uuid);
                }else{
                    Toast.makeText(DeviceActivity.this, "请重新点击蓝牙服务", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rv.setAdapter(rvDetailAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bluetoothClient.registerConnectStatusListener(mDevice.getAddress(), mConnectStatusListener);
        connectDeviceIfNeeded();
    }

    private void startCharacterActivity(UUID service, UUID character,UUID notifyService, UUID notifyCharacter) {
        Intent intent = new Intent(this, CharacterActivity.class);
        intent.putExtra("mac", mDevice.getAddress());
        intent.putExtra("service", service);
        intent.putExtra("character", character);
        intent.putExtra("notify_service", notifyService);
        intent.putExtra("notify_character", notifyCharacter);
        startActivity(intent);
    }


    private void connectDeviceIfNeeded() {
        if (!mConnected) {
            connectDevice();
        }
    }

    private void connectDevice() {
        tvtitle.setText(String.format("%s%s", "正在连接", mDevice.getAddress()));

        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)
                .setConnectTimeout(20000)
                .setServiceDiscoverRetry(3)
                .setServiceDiscoverTimeout(10000)
                .build();

        bluetoothClient.connect(mDevice.getAddress(), options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile profile) {
                BluetoothLog.v(String.format("profile:\n%s", profile));
                tvtitle.setText(String.format("%s", mDevice.getAddress()));
                if (code == REQUEST_SUCCESS) {
                    rvDetailAdapter.setGattProfile(profile);
                }
            }
        });
    }
}
