package com.axalent.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.axalent.R;
import com.axalent.application.BaseActivity;
import com.axalent.util.AxalentUtils;

/**
 * File Name                   : SwitchWifiGuideActivity
 * Author                      : franky
 * Version                     : 1.0.0
 * Date                        : 2016/11/16
 * Revision History            : 10:57
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd.
 * All rights reserved.
 */

public class SwitchWifiGuideActivity extends BaseActivity implements View.OnClickListener {

    private TextView wifiSSID;
    private TextView wifiSetting;
    private TextView nextSetting;

    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_wifi_guide);
        initActionBar();
        initView();
    }

    private void initActionBar() {
        View customView = findViewById(R.id.actionBarConfig);
        customView.findViewById(R.id.barShowGroupBack).setOnClickListener(this);
        customView.findViewById(R.id.barShowGroupAdd).setVisibility(View.GONE);
    }

    private void initView() {
        wifiSSID = (TextView) findViewById(R.id.wifi_ssid);
        wifiSetting = (TextView) findViewById(R.id.wifi_setting);
        nextSetting = (TextView) findViewById(R.id.next_setting);
        wifiSetting.setOnClickListener(this);
        nextSetting.setOnClickListener(this);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

    }

    @Override
    protected void onResume() {
        String ssid = wifiManager.getConnectionInfo().getSSID();
        wifiSSID.setText(ssid.substring(1, ssid.length() - 1));
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.barShowGroupBack:
                finish();
                break;
            case R.id.wifi_setting:
                Intent intent =  new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
                break;
            case R.id.next_setting:
                Intent nextIntent = new Intent(SwitchWifiGuideActivity.this, SendPacketActivity.class);
                startActivityForResult(nextIntent, AxalentUtils.SWITCH_GATEWAY_WIFI_SUCCESS);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AxalentUtils.SWITCH_GATEWAY_WIFI_SUCCESS) {
            setResult(AxalentUtils.SWITCH_GATEWAY_WIFI_SUCCESS);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
