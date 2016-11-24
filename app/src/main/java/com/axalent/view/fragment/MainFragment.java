/**
 * File Name                   : �豸����
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.fragment;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.axalent.R;
import com.axalent.model.UserAttribute;
import com.axalent.model.data.model.Time;
import com.axalent.presenter.axaapi.UserAPI;
import com.axalent.util.AxalentUtils;
import com.axalent.util.LogUtils;
import com.axalent.util.ToastUtils;
import com.axalent.util.XmlUtils;
import com.axalent.view.activity.HomeActivity;
import com.axalent.adapter.MainListAdapter;
import com.axalent.adapter.MainListAdapter.MyScrollListener;
import com.axalent.presenter.controller.Manager;
import com.axalent.model.Device;
import com.axalent.model.HorizontalData;
import com.axalent.util.CacheUtils;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;

public class MainFragment extends Fragment implements Manager, OnRefreshListener, MyScrollListener {
	
	private SwipeRefreshLayout refreshLayout;
	private MainListAdapter adapter;
	private ListView listView;
	private HomeActivity aty;

	@Override
	public void onAttach(Activity activity) {
		aty = (HomeActivity) activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main, null);
	}
	
	@Override
	public void
	onViewCreated(View view, Bundle savedInstanceState) {
		refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.frgMainRefreshLayout);
		refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
		listView = (ListView) view.findViewById(R.id.frgMainListView);
		refreshLayout.setOnRefreshListener(this);
		if (aty.isBluetoothMode()) {
			setAdapter();
//			aty.syncServerData();
		}
	}
	

	@Override
	public void onRefresh() {
		aty.loadData();
		if (aty.isBluetoothMode()) {
			Log.i("test", "onRefresh");
			aty.setupCSRDatas();
			notifyPageRefresh();
		} else {
			getServerTime();
		}
		refreshLayout.setRefreshing(false);
	}

	private void getServerTime() {
		UserAPI.getUserValueList(new Response.Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser xmlPullParser) {
				List<UserAttribute> userAttributes = XmlUtils.converUserValueList(xmlPullParser);
				for (int i = 0; i < userAttributes.size(); i++) {
					if (AxalentUtils.ATTRIBUTE_DATABASE.equals(userAttributes.get(i).getName())) {
						String updateTime = userAttributes.get(i).getUpdTime();
						LogUtils.i("update time:" + updateTime);
						aty.sendMsgToGateway(updateTime);
					}
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				ToastUtils.show(getString(R.string.get_server_data_error));
			}
		});
	}

	public void autoRefresh() {
		refreshLayout.post(new Runnable() {
			@Override
			public void run() {
				refreshLayout.setRefreshing(true);
			}
		});
	}

	public void closeRefresh() {
		refreshLayout.setRefreshing(false);
	}
	
	@Override
	public void notifyPageRefresh() {
		setAdapter();
		refreshLayout.setRefreshing(false);
	}
	
	private void setAdapter() {
		if (adapter == null) {
			if (aty.isBluetoothMode()) {
				adapter = new MainListAdapter(aty, aty.getHorizontalCSRDeviceDatas());
			} else {
				adapter = new MainListAdapter(aty.getDeviceManager(), aty, aty.getHorizontalDatas());
			}
			adapter.setMyScrollListener(this);
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
	public void onScroll(int state) {
		refreshLayout.setEnabled(AbsListView.OnScrollListener.SCROLL_STATE_IDLE == state ? true : false);
	}
	
	public boolean updatePageDevice(Device updateDevice) {
	    List<HorizontalData> horizontalDatas = aty.getHorizontalDatas();
		for (HorizontalData horizontalData : horizontalDatas) {
			List<Device> tempDevices = horizontalData.getDevices();
			String mTypeName = tempDevices.get(0).getTypeName();
			if (mTypeName.equals(updateDevice.getTypeName())) {
				for (Device device : tempDevices) {
					String mDevId = device.getDevId();
					if (mDevId.equals(updateDevice.getDevId())) {
						tempDevices.remove(device);
						tempDevices.add(updateDevice);
						horizontalData.getAdapter().notifyDataSetChanged();
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean deletePageDevice(Device deleteDevice) {
		List<HorizontalData> horizontalDatas = aty.getHorizontalDatas();
		for (HorizontalData horizontalData : horizontalDatas) {
			List<Device> tempDevices = horizontalData.getDevices();
			String mTypeName = tempDevices.get(0).getTypeName();
			if (mTypeName.equals(deleteDevice.getTypeName())) {
				for (Device device : tempDevices) {
					String mDevId = device.getDevId();
					if (mDevId.equals(deleteDevice.getDevId())) {
						tempDevices.remove(device);
						if (tempDevices.isEmpty()) {
							horizontalDatas.remove(horizontalData);
							adapter.notifyDataSetChanged();
						} else {
							BaseAdapter adapter = horizontalData.getAdapter();
							if (adapter != null) {
								adapter.notifyDataSetChanged();
							}
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void addDevice(Device device) {
		Device cacheDevice = CacheUtils.getDeviceByDevId(device.getDevId());
		if (cacheDevice == null) {
			CacheUtils.saveDevice(device);
		} 
		List<HorizontalData> horizontalDatas = aty.getHorizontalDatas();
		for (HorizontalData horizontalData : horizontalDatas) {
			List<Device> tempDevices = horizontalData.getDevices();
			String mTypeName = tempDevices.get(0).getTypeName();
			if (mTypeName.equals(cacheDevice.getTypeName())) {
				tempDevices.add(cacheDevice);
				BaseAdapter adapter = horizontalData.getAdapter();
				if (adapter != null) {
					adapter.notifyDataSetChanged();
				}
				return;
			}
		}
		List<Device> tempDevices = new ArrayList<Device>();
		tempDevices.add(cacheDevice);
		horizontalDatas.add(new HorizontalData(tempDevices, 0, 0, null));
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

}
