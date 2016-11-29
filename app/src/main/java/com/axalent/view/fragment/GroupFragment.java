package com.axalent.view.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.axalent.R;
import com.axalent.adapter.GroupAdapter;
import com.axalent.model.Device;
import com.axalent.model.DeviceAttribute;
import com.axalent.presenter.controller.GroupInterface;
import com.axalent.presenter.controller.Manager;
import com.axalent.presenter.controller.MyOnSwipeListener;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;
import com.axalent.util.LogUtils;
import com.axalent.util.ToastUtils;
import com.axalent.util.XmlUtils;
import com.axalent.view.activity.HomeActivity;
import com.axalent.view.activity.ShowDeviceActivity;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.facebook.stetho.common.LogUtil;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;


import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;


/**
 * File Name                   : GroupFragment
 * Author                      : franky
 * Version                     : 1.0.0
 * Date                        : 2016/11/18
 * Revision History            : 10:31
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd.
 * All rights reserved.
 */

public class GroupFragment extends Fragment implements Manager, OnItemClickListener, OnItemLongClickListener, OnRefreshListener, GroupInterface {

    private HomeActivity aty;
    private SwipeMenuListView listView;
    private SwipeRefreshLayout refreshLayout;
    private GroupAdapter adapter;
    private List<Device> groups = new ArrayList<Device>();

    @Override
    public void onAttach(Activity activity) {
        aty = (HomeActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onPause() {
        refreshLayout.setRefreshing(false);
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtil.i("onCreateView");
        return inflater.inflate(R.layout.fragment_group, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        LogUtil.i("onViewCreated");
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragGroupRefreshLayout);
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        refreshLayout.setOnRefreshListener(this);
        listView = (SwipeMenuListView) view.findViewById(R.id.fragGroupListView);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        listView.setOnSwipeListener(new MyOnSwipeListener(refreshLayout));
        loadDatas();
    }

    public void loadDatas() {
        groups.clear();
        List<Device> devices = CacheUtils.getDevices();
        for (Device device : devices) {

            LogUtils.i("device_type:" + device.getTypeName());

            if (AxalentUtils.TYPE_GATEWAY_GROUP.equalsIgnoreCase(device.getTypeName())) {
                groups.add(device);
            }
        }
        setAdapter();
    }

    private void setAdapter() {
        if (adapter == null) {
            adapter = new GroupAdapter(aty, groups, aty.getDeviceManager(), true, this);
            listView.setAdapter(adapter);
        } else {
            aty.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onRefresh() {
        aty.loadData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        controllerGroup(groups.get(position));
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        controllerGroupAttr(groups.get(position));
        return true;
    }

    private void controllerGroup(final Device device) {
        aty.getDeviceManager().getDeviceAttribute(device.getDevId(), AxalentUtils.ATTRIBUTE_LIGHT, new Response.Listener<XmlPullParser>() {
            @Override
            public void onResponse(XmlPullParser xmlPullParser) {
                DeviceAttribute deviceAttribute = XmlUtils.converDeviceAttribute(xmlPullParser);
                boolean value = !AxalentUtils.ON.equals(deviceAttribute.getValue());
                controllPower(device, value);
//                aty.getDeviceManager().setDeviceAttribute(device.getDevId(), AxalentUtils.ATTRIBUTE_LIGHT, value, new Response.Listener<XmlPullParser>() {
//                    @Override
//                    public void onResponse(XmlPullParser response) {
//                        ToastUtils.show(R.string.action_success);
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        ToastUtils.show(XmlUtils.converErrorMsg(error));
//                    }
//                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtils.show(XmlUtils.converErrorMsg(volleyError));
            }
        });
    }

    private void controllerGroupAttr(Device group) {
        Intent intent = new Intent(aty, ShowDeviceActivity.class);
        intent.putExtra(AxalentUtils.KEY_DEVICE, group);
        startActivity(intent);
    }

    @Override
    public void notifyPageRefresh() {
        loadDatas();
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void addDevice(Device device) {
    }

    @Override
    public void controllPower(Device device, boolean isCheck) {
        String value = !isCheck ? AxalentUtils.OFF : AxalentUtils.ON;
        aty.getDeviceManager().setDeviceAttribute(device.getDevId(), AxalentUtils.ATTRIBUTE_LIGHT, value, new Response.Listener<XmlPullParser>() {
            @Override
            public void onResponse(XmlPullParser response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.show(XmlUtils.converErrorMsg(error));
            }
        });
    }
}
