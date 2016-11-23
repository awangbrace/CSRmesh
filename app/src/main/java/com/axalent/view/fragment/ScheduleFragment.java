/**
 * File Name                   : Schedule ����
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
import org.xmlpull.v1.XmlPullParser;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.axalent.R;
import com.axalent.view.activity.AddActivity;
import com.axalent.view.activity.HomeActivity;
import com.axalent.view.activity.ShowGroupActivity;
import com.axalent.adapter.GroupAdapter;
import com.axalent.presenter.AddDevice;
import com.axalent.presenter.DeleteDevice;
import com.axalent.presenter.AddDevice.OnAddDeviceListener;
import com.axalent.presenter.controller.Manager;
import com.axalent.presenter.controller.MyOnSwipeListener;
import com.axalent.model.Device;
import com.axalent.model.HintError;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;
import com.axalent.util.LogUtils;
import com.axalent.util.ToastUtils;
import com.axalent.util.XmlUtils;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

public class ScheduleFragment extends Fragment implements Manager, OnMenuItemClickListener, OnItemLongClickListener, OnRefreshListener{
	
	private HomeActivity aty;
	private GroupAdapter adapter;
	private SwipeMenuListView listView;
	private SwipeRefreshLayout refreshLayout;
	private List<Device> schedules = new ArrayList<Device>();
	
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
		return inflater.inflate(R.layout.fragment_group, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragGroupRefreshLayout);
		refreshLayout.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light, android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		refreshLayout.setOnRefreshListener(this);
		listView = (SwipeMenuListView) view.findViewById(R.id.fragGroupListView);
		listView.setOnMenuItemClickListener(this);
		listView.setOnItemLongClickListener(this);
		listView.setMenuCreator(getMenuCreator());
		listView.setOnSwipeListener(new MyOnSwipeListener(refreshLayout));
		loadDatas();
	}
	
	@Override
	public void notifyPageRefresh() {
		loadDatas();
		refreshLayout.setRefreshing(false);
	}
	
	private SwipeMenuCreator getMenuCreator() {
		return new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
				deleteItem.setWidth(AxalentUtils.dp2px(getActivity(), 90));
				deleteItem.setIcon(R.drawable.delete);
				menu.addMenuItem(deleteItem);
			}
		};
	}
	
	private void loadDatas() {
		schedules.clear();
		List<Device> devices = CacheUtils.getDevices();
		for (Device device : devices) {
			if (AxalentUtils.TYPE_GATEWAY_SCHEDULE.equalsIgnoreCase(device.getTypeName())) {
				schedules.add(device);
			}
		}
		setAdapter();
	}
	
	private void setAdapter() {
		if (adapter == null) {
			adapter = new GroupAdapter(aty, schedules, aty.getDeviceManager());
			listView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		skipToShowSchedule(schedules.get(position));
		return true;
	}
	
	private void skipToShowSchedule(Device schedule) {
		Intent intent = new Intent(aty, ShowGroupActivity.class);
		intent.putExtra(AxalentUtils.KEY_DEVICE, schedule);
		startActivityForResult(intent, 0);
	}
	
	@Override
	public void onMenuItemClick(final int arg0, SwipeMenu arg1, int arg2) {
		if (AxalentUtils.getLoginMode() != R.id.atyLoginCloudBtn) {
			ToastUtils.show(R.string.please_change_to_cloud_mode);
			return;
		}
		showDeleteScheduleDialog(schedules.get(arg0));
	}
	 
	private void showDeleteScheduleDialog(final Device schedule) {
		AlertDialog.Builder builder = new AlertDialog.Builder(aty);
		builder.setIcon(R.drawable.app_logo_1);
		builder.setTitle(R.string.delete_schedule);
		builder.setMessage(R.string.confirm_delete_schedule_msg);
		builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DeleteDevice deleteDevice = new DeleteDevice(aty.getDeviceManager(), schedule);
				deleteDevice.setOnDeleteDeviceListener(new DeleteDevice.OnDeleteDeviceListener() {
					@Override
					public void onDeleteDevice(boolean isSuccess, HintError hintError) {
						if (isSuccess) {
							schedules.remove(schedule);
							adapter.notifyDataSetChanged();
							ToastUtils.showByTask(R.string.delete_success);
						}
					}
				});
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// ȡ��
			}
		});
		builder.create().show();
	}

	@Override
	public void addDevice(Device schedule) {
		new AddSchedule();
	}
	
	
	class AddSchedule implements OnAddDeviceListener {
		
		int count = 0;
		List<Device> gateways;
		
		public AddSchedule() {
			start();
		}
		
		private void start() {
			aty.loadingDialog.show(R.string.is_the_add);
			gateways = CacheUtils.getDeviceByTypeName(AxalentUtils.TYPE_GATEWAY);
			if (gateways != null && gateways.size() != 0) {
				for (Device gateway : gateways) {
					getAttribute(gateway);
				}
			} else {
				aty.loadingDialog.close();
				ToastUtils.show(R.string.not_gateway_not_add_schudule);
			}
		}
		
		private void getAttribute(final Device gateway) {
			aty.getDeviceManager().getDeviceAttributesWithValues(gateway, new Listener<XmlPullParser>() {
				@Override
				public void onResponse(XmlPullParser response) {
					Device tempDevice = XmlUtils.convertDeviceAttributesWithValues(response);
					gateway.setState(tempDevice.getState());
					gateway.setTypeId(tempDevice.getTypeId());
					gateway.setTypeName(tempDevice.getTypeName());
					gateway.setAttributes(tempDevice.getAttributes());
					isAccomplish();
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					isAccomplish();
				}
			});
		}
		
		private void isAccomplish() {
			count ++;
			if (count == gateways.size()) {
				checkGatewayStateIsOK(); 
			}
		}
		
		private void checkGatewayStateIsOK() {
			// Gateway �Ƿ�ͨ���3�μ��
			boolean isTransitSuccess = false;
			for (Device gateway : gateways) {
				// ������
				if (AxalentUtils.OFFLINE.equals(gateway.getState())) {
					continue;
				}
				String childInfo = AxalentUtils.getDeviceAttributeValue(gateway, AxalentUtils.ATTRIBUTE_CHILDINFO);
				// ChildInfo �ǿ�
				if (TextUtils.isEmpty(childInfo)) {
					continue;
				}
				// ���ChildInfo���豸����
				if (!childInfo.contains("00")) {
					continue;
				}
				isTransitSuccess = true;
				addSchedule(gateway);
				break;
			}
			if (!isTransitSuccess) {
				// û��ͨ��
				aty.loadingDialog.close();
				ToastUtils.show(R.string.add_failure);
			}
		}
		
		private void addSchedule(Device gateway) {
			AddDevice addDevice = new AddDevice(aty.getDeviceManager());
			addDevice.setTimeOut(30000);
			addDevice.setOnAddDeviceListener(this);
			addDevice.addGatewayDevice(gateway, AxalentUtils.ATTRIBUTE_ADD_SCHEDULE, "1");
		}

		@Override
		public void onAddDevice(Device schedule, HintError hintError) {
			aty.loadingDialog.close();
			if (schedule != null) {
				skipToAddPage(schedule);
				schedules.add(schedule);
				adapter.notifyDataSetChanged();
			} else {
				if (AxalentUtils.TIME_OUT.equals(hintError.getErrorCode())) {
					ToastUtils.showByTask(R.string.add_time_out);
				} else {
					ToastUtils.showByTask(R.string.add_failure);
				}
			}
		}
	}
	
	private void skipToAddPage(Device schedule) {
		Intent intent = new Intent(aty, AddActivity.class);
		intent.putExtra(AxalentUtils.KEY_SKIP, AxalentUtils.ADD_SCHEDULE);
		intent.putExtra(AxalentUtils.KEY_DEVICE, schedule);
		startActivity(intent);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtils.i("onActivityResult: schedule");
		notifyPageRefresh();
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onRefresh() {
		aty.loadData();		
	}

}
