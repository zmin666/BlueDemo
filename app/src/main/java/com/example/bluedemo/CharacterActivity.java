package com.example.bluedemo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.utils.ByteUtils;

import java.util.UUID;

import static com.inuker.bluetooth.library.Code.REQUEST_SUCCESS;

/**
 * @author: ZhangMin
 * @date: 2020/4/17 13:53
 * @version: 1.0
 * @desc:
 */
public class CharacterActivity extends AppCompatActivity {
    private String mMac;
    private UUID mService;
    private UUID mCharacter;
    private UUID mNotifyService;
    private UUID mNotifyCharacter;
    private TextView tvVersion;
    private TextView tvPhone;
    private TextView tvSport;
    private TextView tv;
    private TextView tvMeg;
    private EditText etWrite;
    private boolean isOpenNotify = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character1);
        tvVersion = (TextView) findViewById(R.id.tv_vertion);
        tvPhone = (TextView) findViewById(R.id.tv_phone);
        tvSport = (TextView) findViewById(R.id.tv_sport);
        tv = (TextView) findViewById(R.id.tv);
        tvMeg = (TextView) findViewById(R.id.tv_meg);
        etWrite = (EditText) findViewById(R.id.et_write);
        tvMeg.setMovementMethod(ScrollingMovementMethod.getInstance());

        Intent intent = getIntent();
        mMac = intent.getStringExtra("mac");
        mService = (UUID) intent.getSerializableExtra("service");
        mCharacter = (UUID) intent.getSerializableExtra("character");
        mNotifyService = (UUID) intent.getSerializableExtra("notify_service");
        mNotifyCharacter = (UUID) intent.getSerializableExtra("notify_character");
        openNotify();
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpenNotify) {
                    closeNotity();
                } else {
                    openNotify();
                }
            }
        });
    }

    private void closeNotity() {
        BluetoothClientManager.getClient().unnotify(mMac, mNotifyService, mNotifyCharacter, new BleUnnotifyResponse() {
            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    isOpenNotify = false;
                    tv.setText("当前已经关闭, 点击打开通知");
                }
            }
        });
    }

    private void openNotify() {
        BluetoothClientManager.getClient().notify(mMac, mNotifyService, mNotifyCharacter, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {
                Log.i(".....收到信息..", "///////");
                String s = ByteUtils.byteToString(value);
                String s1 = tvMeg.getText().toString();
                tvMeg.setText(s1 + "\n" + s);
                parseNotifyNew(s);
            }

            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    isOpenNotify = true;
                    isOpenNotify = true;
                    tv.setText("当前已经打开, 点击关闭通知");
                }
            }
        });
    }


    /**
     * 解析通知过来的数据
     * @param s
     */
    private void parseNotifyNew(String s) {
        if (s.startsWith("AA81")) {
            String validLength = s.substring(4, 6);
            int length = Integer.parseInt(validLength, 16);
            String version = s.substring(6, 6 + length * 2);
            String versionName = version.replace("0", ".");
            if (versionName.startsWith(".")) {
                versionName = versionName.substring(1, versionName.length());
            }
            tvVersion.setText("版本号为  v" + versionName);
        } else if (s.startsWith("AA85")) {
            String validLength = s.substring(4, 6);
            int length = Integer.parseInt(validLength, 16);
            int end = 6 + 22;
            String phoneNumber = s.substring(6, end);
            String phone = "手机号码:  " + DataUtils.asciiToString(phoneNumber);
            tvPhone.setText(phone);
            String paceStr = s.substring(end, end + 4);
            String pace = "步数:  " + Integer.parseInt(paceStr,16)+"步";
            String caloriesStr = s.substring(end + 4, end + 8);
            String calories = "卡路里:  " + Integer.parseInt(caloriesStr,16)+"卡";
            String distanceStr = s.substring(end + 8, end + 12);
            String distance = "距离:  " + Integer.parseInt(distanceStr,16)+"米";
            String temperatureStr = s.substring(end + 12, end + 14);
            String temperature = "体温:  " + Integer.parseInt(temperatureStr,16)+" 摄氏度";
            String powerStr = s.substring(end + 14, end + 16);
            String power = "电量:  " + Integer.parseInt(powerStr,16) + "%";

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(pace);
            stringBuilder.append("\n");
            stringBuilder.append(calories);
            stringBuilder.append("\n");
            stringBuilder.append(distance);
            stringBuilder.append("\n");
            stringBuilder.append(temperature);
            stringBuilder.append("\n");
            stringBuilder.append(power);
            tvSport.setText(stringBuilder.toString());
        }
    }


    /**
     * 写入信息
     * @param view
     */
    public void click_white(View view) {
//        String et = etText.getText().toString();
//        BluetoothClientManager.getClient().write(mMac, mService, mCharacter, DataUtils.stringToAscii("A3", et), mWriteRsp);
    }

    public void click_white_time(View view) {
        BluetoothClientManager.getClient().write(mMac, mService, mCharacter, DataUtils.getByteTime(), mWriteRsp);
    }

    public void click_query_time(View view) {
        BluetoothClientManager.getClient().write(mMac, mService, mCharacter, DataUtils.stringToBytes("05"), mWriteRsp);
    }

    public void click_reset_time(View view) {
        String s = "01202001010100020108";
        BluetoothClientManager.getClient().write(mMac, mService, mCharacter, DataUtils.stringToBytes(s), mWriteRsp);
    }


    private final BleWriteResponse mWriteRsp = new BleWriteResponse() {
        @Override
        public void onResponse(int code) {
            if (code == Constants.REQUEST_SUCCESS) {
                Toast.makeText(CharacterActivity.this, "success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CharacterActivity.this, "failed", Toast.LENGTH_SHORT).show();
            }
        }
    };


    public void sendMsg(View view) {
        sendMsgForVersion("AA0500AF");
    }


    public void getVersion(View view) {
        sendMsgForVersion("AA0100AB");
    }

    public void sendMsgForVersion(String s) {
        BluetoothClientManager.getClient().write(mMac, mService, mCharacter, DataUtils.hexStringToByteArray(s), mWriteRsp);
    }

    public void clear(View view) {
        tvMeg.setText("通知信息log");
    }

    public void sendCommand(View view) {
        String trim = etWrite.getText().toString().trim();
        if (TextUtils.isEmpty(trim)) {
            Toast.makeText(this, "请输入指令", Toast.LENGTH_SHORT).show();
        }else{
            sendMsgForVersion(trim);
        }

    }
}
