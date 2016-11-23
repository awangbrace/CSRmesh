package com.axalent.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.axalent.R;
import com.axalent.application.BaseActivity;
import com.axalent.presenter.SmartConfigThread;
import com.axalent.util.AxalentUtils;
import com.axalent.util.ToastUtils;


/**
 * File Name                   : SendPacketActivity
 * Author                      : franky
 * Version                     : 1.0.0
 * Date                        : 2016/11/16
 * Revision History            : 12:18
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd.
 * All rights reserved.
 */

public class SendPacketActivity extends BaseActivity implements View.OnClickListener {

    private EditText wifiSsidEdit;
    private EditText wifiPassEdit;
    private TextView determine;

    private SmartConfigThread smartConfigThread;
    private static final int SEND_PERIOD_MS = 60 * 1000;
    private Handler mHandler = new Handler();
    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_packet);
        initActionBar();
        initView();
    }

    private void initActionBar() {
        View customView = findViewById(R.id.actionBarConfig);
        customView.findViewById(R.id.barShowGroupBack).setOnClickListener(this);
        customView.findViewById(R.id.barShowGroupAdd).setVisibility(View.GONE);
    }

    private void initView() {
        wifiSsidEdit = (EditText) findViewById(R.id.atyWifiSsidEdit);
        wifiPassEdit = (EditText) findViewById(R.id.atyWifiPasswordEdit);
        determine = (TextView) findViewById(R.id.atyOkBtn);
        determine.setOnClickListener(this);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    protected void onResume() {
        String ssid = wifiManager.getConnectionInfo().getSSID();
        wifiSsidEdit.setText(ssid.substring(1, ssid.length() - 1));
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        destroyConfigThread();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.barShowGroupBack:
                finish();
                break;
            case R.id.atyOkBtn:
                sendPacket();
                break;
        }
    }

    private void sendPacket() {
        String ssid = wifiSsidEdit.getText().toString().trim();
        String password = wifiPassEdit.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.show(getString(R.string.config_enter_pass));
        } else if (password.length() < 8) {
            ToastUtils.show(getString(R.string.config_pass_less));
        } else {
            startSend(ssid, password);
        }
    }

    private Runnable sendTimeOutCallback = new Runnable() {
        @Override
        public void run() {
            stopSend();
        }
    };

    private void destroyConfigThread() {
        mHandler.removeCallbacks(sendTimeOutCallback);
        if (smartConfigThread != null) {
            smartConfigThread.stopSend();
            smartConfigThread = null;
        }
    }

    private void startSend(String ssid, String password) {
        mHandler.postDelayed(sendTimeOutCallback, SEND_PERIOD_MS);
        if (smartConfigThread == null) {
            smartConfigThread = new SmartConfigThread(ssid, password);
            smartConfigThread.start();
        }
        Intent intent = new Intent(SendPacketActivity.this, ResetGatewayGuideActivity.class);
        startActivityForResult(intent, AxalentUtils.SWITCH_GATEWAY_WIFI_SUCCESS);
    }

    private void stopSend() {
        if (smartConfigThread != null) {
            smartConfigThread.stopSend();
            smartConfigThread = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AxalentUtils.SWITCH_GATEWAY_WIFI_SUCCESS) {
            setResult(AxalentUtils.SWITCH_GATEWAY_WIFI_SUCCESS);
            finish();
        } else if (resultCode == AxalentUtils.SWITCH_GATEWAY_STOP_SEND_PACKET) {
            destroyConfigThread();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
