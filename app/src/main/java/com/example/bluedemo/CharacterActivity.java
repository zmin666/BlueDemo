package com.example.bluedemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.Calendar;
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
    private Button tv;
    private TextView tvMeg;
    private Button tvWrite;
    private EditText etText;
    private EditText etWriteMsg;
    private boolean isOpenNotify = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);
        tv = (Button) findViewById(R.id.tv);
        tvMeg = (TextView) findViewById(R.id.tv_meg);
        tvWrite = (Button) findViewById(R.id.tv_write);
        etText = (EditText) findViewById(R.id.et_write);
        etWriteMsg = (EditText) findViewById(R.id.et_write_msg);

        Intent intent = getIntent();
        mMac = intent.getStringExtra("mac");
        mService = (UUID) intent.getSerializableExtra("service");
        mCharacter = (UUID) intent.getSerializableExtra("character");
        mNotifyService = (UUID) intent.getSerializableExtra("notify_service");
        mNotifyCharacter = (UUID) intent.getSerializableExtra("notify_character");
        openNotify();
        tv.setOnClickListener(new View.OnClickListener() {
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
                tvMeg.setText(ByteUtils.byteToString(value));
            }

            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    isOpenNotify = true;
                    tv.setText("当前已经打开, 点击关闭通知");
                }
            }
        });
    }


    /**
     * 写入信息
     * @param view
     */
    public void click_white(View view) {
        String et = etText.getText().toString();
        BluetoothClientManager.getClient().write(mMac, mService, mCharacter, stringToAscii("A3", et), mWriteRsp);
    }

    public void click_white_time(View view) {
        BluetoothClientManager.getClient().write(mMac, mService, mCharacter, stringToBytes(getTime()), mWriteRsp);
}

    public void click_query_time(View view) {
        BluetoothClientManager.getClient().write(mMac, mService, mCharacter, stringToBytes("05"), mWriteRsp);
    }

    public void click_reset_time(View view) {
        String s = "01202001010100020108";
        BluetoothClientManager.getClient().write(mMac, mService, mCharacter, stringToBytes(s), mWriteRsp);
    }


    /**
     * 写入字母之类的信息
     * @param prefix 服务类型
     * @param value  服务数据
     * @return
     */
    public static byte[] stringToAscii(String prefix, String value) {
        StringBuffer sbu = new StringBuffer();
        sbu.append(prefix);
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            sbu.append(Integer.toHexString((int) chars[i]));
        }
        return ByteUtils.stringToBytes(sbu.toString());
    }


    /**
     * 写入纯数字信息
     * @param text
     * @return
     */
    public static byte[] stringToBytes(String text) {
        int len = text.length();
        byte[] bytes = new byte[(len + 1) / 2];
        for (int i = 0; i < len; i += 2) {
            int size = Math.min(2, len - i);
            String sub = text.substring(i, i + size);
            bytes[i / 2] = (byte) Integer.parseInt(sub);
        }
        return bytes;
    }


    public String getTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int yy1 = year / 100;
        int yy2 = year - yy1 * 100;
        StringBuffer sbu = new StringBuffer();
        sbu.append("01");
        sbu.append(yy1);
        sbu.append(yy2);
        if (month < 10) {
            sbu.append("0");
        }
        sbu.append(month);
        if (day < 10) {
            sbu.append("0");
        }
        sbu.append(day);
        if (hour < 10) {
            sbu.append("0");
        }
        sbu.append(hour);
        if (minute < 10) {
            sbu.append("0");
        }
        sbu.append(minute);
        if (second < 10) {
            sbu.append("0");
        }
        sbu.append(second);
        sbu.append("07");
        sbu.append("19");
        return sbu.toString();
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
        String msg = etWriteMsg.getText().toString();
        BluetoothClientManager.getClient().write(mMac, mService, mCharacter, stringToBytes(msg), mWriteRsp);
    }
}
