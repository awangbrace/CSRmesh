/**
 * File Name                   : scene����
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
import com.axalent.model.data.database.DBManager;
import com.axalent.model.data.model.Area;
import com.axalent.model.data.model.devices.CSRDevice;
import com.axalent.view.activity.AddActivity;
import com.axalent.view.activity.AddGroupActivity;
import com.axalent.view.activity.HomeActivity;
import com.axalent.view.activity.ShowDeviceActivity;
import com.axalent.view.activity.ShowGroupActivity;
import com.axalent.adapter.GroupAdapter;
import com.axalent.presenter.AddDevice;
import com.axalent.presenter.AddDevice.OnAddDeviceListener;
import com.axalent.presenter.DeleteDevice;
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
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class SceneFragment extends Fragment implements Manager, OnItemClickListener, OnMenuItemClickListener, OnItemLongClickListener, OnRefreshListener{

	private HomeActivity aty;
	private GroupAdapter adapter;
	private SwipeMenuListView listView;
	private SwipeRefreshLayout refreshLayout;
	private List<Device> scenes = new ArrayList<Device>();

	// bluetooth mode
	private DBManager dbManager;
	private List<Area> areas = new ArrayList<>();
	
	@Override
	public void onAttach(Activity activity) {
		aty = (HomeActivity) activity;
		dbManager = DBManager.getDBManagerInstance(aty.getApplicationContext());
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_group, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragGroupRefreshLayout);
		refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
		refreshLayout.setOnRefreshListener(this);
		if (aty.isBluetoothMode()) {
			refreshLayout.setEnabled(false);
		}
		listView = (SwipeMenuListView) view.findViewById(R.id.fragGroupListView);
		listView.setOnItemClickListener(this);
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
	
	private void setDeviceAttribute(final Device device) {
		aty.getDeviceManager().getDeviceAttributesWithValues(device, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				Device tempDevice = XmlUtils.convertDeviceAttributesWithValues(response);
				device.setState(tempDevice.getState());
				device.setTypeId(tempDevice.getTypeId());
				device.setTypeName(tempDevice.getTypeName());
				device.setAttributes(tempDevice.getAttributes());
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
			}
		});
	}
	
	public void loadDatas() {
		if (aty.isBluetoothMode()) {
			areas.clear();
			areas.addAll(dbManager.getAllAreaList());
		} else {
			scenes.clear();
			List<Device> devices = CacheUtils.getDevices();
			for (Device device : devices) {
				if (AxalentUtils.TYPE_AXALENT_SCENE.equalsIgnoreCase(device.getTypeName())) {
					scenes.add(device);
				} else if (AxalentUtils.TYPE_GATEWAY_SCENE.equalsIgnoreCase(device.getTypeName())) {
					setDeviceAttribute(device);
				}
			}
		}
		setAdapter();
	}
	
	private void setAdapter() {
		if (adapter == null) {
			if (aty.isBluetoothMode()) {
				adapter = new GroupAdapter(aty, areas, true);
			} else {
				adapter = new GroupAdapter(aty, scenes, aty.getDeviceManager());
			}
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
		if (!aty.isBluetoothMode()) {
			aty.loadData();
		} else {
			loadDatas();
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (aty.isBluetoothMode()) {
			goToAddGroup(areas.get(position).getId());
		} else {
			skipToShowScene(scenes.get(position));
		}
		return true;
	}
	
	private void skipToShowScene(Device scene) {
		LogUtils.i("skipToShowScene");
		Intent intent = new Intent(aty, ShowGroupActivity.class);
		intent.putExtra(AxalentUtils.KEY_DEVICE, scene);
		startActivityForResult(intent, 0);
	}

	@Override
	public void onMenuItemClick(final int arg0, SwipeMenu arg1, int arg2) {
		if (!aty.isBluetoothMode()) {
			if (AxalentUtils.getLoginMode() != R.id.atyLoginCloudBtn) {
				ToastUtils.show(R.string.please_change_to_cloud_mode);
				return;
			}
			showDeleteSceneDialog(scenes.get(arg0));
		} else {
			showDeleteGroupDialog(areas.get(arg0));
		}
	}

	private void showDeleteGroupDialog(final Area area) {
		AlertDialog.Builder builder = new AlertDialog.Builder(aty);
		builder.setIcon(R.drawable.app_logo_1);
		builder.setTitle(R.string.delete_group_title);
		builder.setMessage(R.string.confirm_delete_group);
		builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				boolean isSuccess = true;
				aty.loadingDialog.show(R.string.is_the_delete);
				for (CSRDevice d : dbManager.getDevicesInArea(area.getId())) {
					for (int i = 0; i < d.getGroups().length; i++) {
						if (d.getGroups()[i] == area.getId()) {
							d.getGroups()[i] = 0;
						}
					}
					CSRDevice result = dbManager.createOrUpdateDevice(d);
					if (result == null) {
						isSuccess = false;
					}
				}
				if (isSuccess && dbManager.removeArea(area.getId())) {
					areas.remove(area);
					adapter.notifyDataSetChanged();
					aty.loadingDialog.close();
					ToastUtils.showByTask(R.string.delete_success);
				} else {
					aty.loadingDialog.close();
					ToastUtils.showByTask(R.string.delete_failure);
				}
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.create().show();
	}
	
	private void showDeleteSceneDialog(final Device scene) {
		AlertDialog.Builder builder = new AlertDialog.Builder(aty);
		builder.setIcon(R.drawable.app_logo_1);
		builder.setTitle(R.string.delete_scene_title);
		builder.setMessage(R.string.confirm_delete_scene);
		builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				aty.loadingDialog.show(R.string.is_the_delete);
				DeleteDevice deleteDevice = new DeleteDevice(aty.getDeviceManager(), scene);
				deleteDevice.setOnDeleteDeviceListener(new DeleteDevice.OnDeleteDeviceListener() {
					@Override
					public void onDeleteDevice(boolean isSuccess, HintError hintError) {
						aty.loadingDialog.close();
						if (isSuccess) {
							scenes.remove(scene);
							adapter.notifyDataSetChanged();
							ToastUtils.showByTask(R.string.delete_success);
						} else {
							ToastUtils.showByTask(R.string.delete_failure);
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (aty.isBluetoothMode()) {
			goToController(areas.get(position).getId());
		} else {
			controllerScene(scenes.get(position));
		}
	}

	private void goToAddGroup(int areaId) {
		Intent intent = new Intent(aty, AddGroupActivity.class);
		intent.putExtra(AddGroupActivity.KEY_AREA_ID, areaId);
		startActivityForResult(intent, AxalentUtils.ADD_GROUP);
	}

	private void goToController (int areaId) {
		Intent intent = new Intent(aty, ShowDeviceActivity.class);
		intent.putExtra(AddGroupActivity.KEY_AREA_ID, areaId);
		intent.putExtra(AxalentUtils.GROUP_OR_SING, AxalentUtils.GROUP);
		startActivityForResult(intent, AxalentUtils.ADD_GROUP);
	}
	
	private void controllerScene(Device scene) {
		aty.getDeviceManager().setDeviceAttribute(scene.getDevId(), AxalentUtils.ATTRIBUTE_ACTIVATE, AxalentUtils.ON, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				ToastUtils.show(R.string.action_success);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				ToastUtils.show(XmlUtils.converErrorMsg(error));
			}
		});
	}

	@Override
	public void addDevice(Device device) {
		addScene();
	}
	
	private void addScene() {
		aty.loadingDialog.show(R.string.is_the_add);
		AddDevice addDevice = new AddDevice(aty.getDeviceManager());
		addDevice.setTimeOut(30000);
		addDevice.setOnAddDeviceListener(new OnAddDeviceListener() {
			@Override
			public void onAddDevice(Device scene, HintError error) {
				aty.loadingDialog.close();
				if (scene != null) {
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
					int index = sp.getInt("indexs", 32);
					sp.edit()
							.putInt("indexs", index - 1)
							.apply();

					scenes.add(scene);
					skipToAddPage(scene);
					adapter.notifyDataSetChanged();
				} else {
					if (AxalentUtils.TIME_OUT.equals(error.getErrorCode())) {
						ToastUtils.showByTask(R.string.add_time_out);
					} else {
						ToastUtils.showByTask(R.string.add_failure);
					}
				}
			}
		});
		addDevice.addScene();

	}
	
	private void skipToAddPage(Device scene) {
		Intent intent = new Intent(aty, AddActivity.class);
		intent.putExtra(AxalentUtils.KEY_SKIP, AxalentUtils.ADD_SCENE);
		intent.putExtra(AxalentUtils.KEY_DEVICE, scene);
		startActivity(intent);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtils.i("onActivityResult: scene");
		notifyPageRefresh();
		super.onActivityResult(requestCode, resultCode, data);
	}

}
