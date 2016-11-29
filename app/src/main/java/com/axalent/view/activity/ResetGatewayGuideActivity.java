package com.axalent.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.axalent.R;
import com.axalent.application.BaseActivity;
import com.axalent.application.MyRequestQueue;
import com.axalent.model.Gateway;
import com.axalent.presenter.ssdp.SSDPSocket;
import com.axalent.util.AxalentUtils;
import com.axalent.util.LogUtils;
import com.axalent.util.ToastUtils;
import com.axalent.util.XmlUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.net.DatagramPacket;

/**
 * File Name                   : ResetGatewayGuideActivity
 * Author                      : franky
 * Version                     : 1.0.0
 * Date                        : 2016/11/16
 * Revision History            : 14:41
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd.
 * All rights reserved.
 */

public class ResetGatewayGuideActivity extends BaseActivity implements View.OnClickListener {

    private boolean stop = false;
    private static final int SEND_PERIOD_MS = 2 * 60 * 1000;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_gateway);
        initActionBar();
        initMulticastLock();
        sendSearchMessage();
    }

    @Override
    protected void onResume() {
        stop = false;
        super.onResume();
    }

    @Override
    protected void onPause() {
        stop = true;
        super.onPause();
    }

    private void initActionBar() {
        View customView = findViewById(R.id.actionBarConfig);
        customView.findViewById(R.id.barShowGroupBack).setOnClickListener(this);
        customView.findViewById(R.id.barShowGroupAdd).setVisibility(View.GONE);
    }

    private void initMulticastLock() {
        WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiManager.MulticastLock multicastLock = wm.createMulticastLock("multicastLock");
        multicastLock.setReferenceCounted(true);
        multicastLock.acquire();
    }

    private void sendSearchMessage() {
        mHandler.postDelayed(searchTimeout, SEND_PERIOD_MS);
        new Thread(searchRunnable).start();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(searchTimeout);
        super.onDestroy();
    }

    private Runnable searchTimeout = new Runnable() {
        @Override
        public void run() {
            ToastUtils.show(R.string.time_out);
            finish();
        }
    };

    private Runnable searchRunnable = new Runnable() {
        public void run() {
            SSDPSocket sock = null;
            try {
                sock = new SSDPSocket("");
                while(!stop) {
                    DatagramPacket dp = sock.receiveAck();

                    LogUtils.i("result:" + new String(dp.getData()));

                    parserResult(new String(dp.getData()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private void parserResult(String result) {
        if (result.contains(AxalentUtils.SWITCH_WIFI_SUCCESS_FEEDBACK)) {
            configSuccess();
        }
    }

    private void configSuccess() {
        setResult(AxalentUtils.SWITCH_GATEWAY_WIFI_SUCCESS);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.barShowGroupBack:
                setResult(AxalentUtils.SWITCH_GATEWAY_STOP_SEND_PACKET);
                finish();
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
