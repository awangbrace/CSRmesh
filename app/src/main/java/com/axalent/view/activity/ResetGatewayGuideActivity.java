package com.axalent.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.axalent.R;
import com.axalent.application.BaseActivity;
import com.axalent.application.MyRequestQueue;
import com.axalent.model.Gateway;
import com.axalent.presenter.ssdp.SSDPSearchMsg;
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
import java.util.HashMap;

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
    private HashMap<String, String> filterMap = new HashMap<String, String>();

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
        new Thread(searchRunnable).start();
    }

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
        final String[] LOCATIONS = result.split("LOCATION: ");
        if (LOCATIONS.length < 2) {
            return;
        }
        String content = LOCATIONS[1];
        int start = 0;
        int end = content.indexOf("\r\n");
        String url = content.substring(start, end);
        if (filterMap.get(url) == null) {
            filterMap.put(url, url);
            findGateway(url);
        }
    }

    private void findGateway(final String url) {
        LogUtils.i("url:"+url);
        StringRequest req = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String str) {
                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser pull = factory.newPullParser();
                    pull.setInput(new StringReader(str));
                    Gateway gateway = XmlUtils.getGateway(pull);
                    String manufacturer = gateway.getManufacturer();

                    if (manufacturer != null && manufacturer.contains("Axalent")) {
                        LogUtils.i("find gateway ok!");
                        int start = 0;
                        int end = url.lastIndexOf(":");
                        String newUrl = url.substring(start, end);
                        gateway.setUrl(newUrl);
                        String localUrl = gateway.getUrl() + ":8087/zdk/services/zamapi/";
                        sharedPreferences.edit().putString("localUrl", localUrl).commit();
                        LogUtils.i("localUrl:"+localUrl);
                        configSuccess();
                    }

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
            }
        });

        MyRequestQueue.addToRequestQueue(req);

    }

    private void configSuccess() {
        ToastUtils.show(getString(R.string.config_success));
        setResult(AxalentUtils.SWITCH_GATEWAY_WIFI_SUCCESS);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.barShowGroupBack:
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
