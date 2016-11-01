/**
 * File Name                   : ��ʾ Scene or Schedule ҳ��
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/08/20
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.activity;

import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.axalent.R;
import com.axalent.application.BaseActivity;
import com.axalent.presenter.controller.DeviceManager;
import com.axalent.presenter.controller.impl.DeviceManagerImpl;
import com.axalent.model.Device;
import com.axalent.util.LogUtils;
import com.axalent.view.fragment.ShowSceneFragment;
import com.axalent.view.fragment.ShowScheduleFragment;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;
import com.axalent.util.ToastUtils;
import com.axalent.view.widget.LoadingDialog;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShowGroupActivity extends BaseActivity implements OnClickListener {
	
	private Device currentDevice;
	private Fragment currentFragment;
	private LoadingDialog loadingDialog;
	private DeviceManager deviceManager = new DeviceManagerImpl();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_group);
		init();
	}
	
	private void init() {
		currentDevice = getIntent().getParcelableExtra(AxalentUtils.KEY_DEVICE);
		String typeName = currentDevice.getTypeName();
		if (AxalentUtils.TYPE_AXALENT_SCENE.equalsIgnoreCase(typeName)) {
			currentFragment = new ShowSceneFragment();
		} else if (AxalentUtils.TYPE_GATEWAY_SCHEDULE.equalsIgnoreCase(typeName)) {
			currentFragment = new ShowScheduleFragment();
		}
		initActionBar();
		loadingDialog = new LoadingDialog(this);
		setCurrentFragment();
	}
	
	private void initActionBar() {
		View customView = findViewById(R.id.barShowGroupContent);
		TextView titleTxt = (TextView) customView.findViewById(R.id.barShowGroupTitleTxt);
		titleTxt.setText(getMyTitle());
		RelativeLayout back = (RelativeLayout) customView.findViewById(R.id.barShowGroupBack);
		RelativeLayout add = (RelativeLayout) customView.findViewById(R.id.barShowGroupAdd);
		back.setOnClickListener(this);
		add.setOnClickListener(this);
	}
	
	public LoadingDialog getLoadingDialog() {
		return loadingDialog;
	}
	
	private void setCurrentFragment() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(R.id.atyShowGroupFrameLayout, currentFragment);
		ft.commit();
	}
	
	private int getMyTitle() {
		String typeName = currentDevice.getTypeName();
		if (AxalentUtils.TYPE_AXALENT_SCENE.equalsIgnoreCase(typeName)) {
			return R.string.scene;
		} else if (AxalentUtils.TYPE_GATEWAY_SCHEDULE.equalsIgnoreCase(typeName)) {
			return R.string.schedule;
		}
		return R.string.scene;
	}
	
	
	public Device getCurrentDevice() {
		return currentDevice;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.barShowGroupBack:
			finish();
			break;
		case R.id.barShowGroupAdd:
			skipToAddPage();
			break;
		}
	}
	
	private void skipToAddPage() {
		if (AxalentUtils.getLoginMode() != R.id.atyLoginCloudBtn) {
			ToastUtils.show(R.string.please_change_to_cloud_mode);
			return;
		}
		Intent intent = new Intent(this, AddActivity.class);
		intent.putExtra(AxalentUtils.KEY_SKIP, getSkipType());
		intent.putExtra(AxalentUtils.KEY_DEVICE, currentDevice);
		intent.putParcelableArrayListExtra(AxalentUtils.KEY_DEVICES, getChildDevices());
		startActivityForResult(intent, 0);
	}
	
	private ArrayList<Device> getChildDevices() {
		return ((GroupManage) currentFragment).getChildDevices();
	}
	
	public interface GroupManage {
		ArrayList<Device> getChildDevices();
		void refresh();
		void alterGroupName(String newName);
	}
	
	private int getSkipType() {
		String typeName = currentDevice.getTypeName();
		if (AxalentUtils.TYPE_AXALENT_SCENE.equalsIgnoreCase(typeName)) {
			return AxalentUtils.UPDATE_SCENE;
		} else if (AxalentUtils.TYPE_GATEWAY_SCHEDULE.equalsIgnoreCase(typeName)) {
			return AxalentUtils.UPDATE_SCHEDULE;
		}
		return 0;
	}
	
	public DeviceManager getDeviceManager() {
		return deviceManager;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		((GroupManage) currentFragment).refresh();
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void showAlterNameDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.app_logo_1);
		builder.setTitle(R.string.alter_name);
		View view = View.inflate(this, R.layout.dialog_input, null);
		builder.setView(view);
		final EditText nameEidt = (EditText) view.findViewById(R.id.dialogInputEdit);
		nameEidt.setHint(R.string.please_input_device_name);
		builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String newName = nameEidt.getText().toString().trim();
				if (TextUtils.isEmpty(newName)) {
					ToastUtils.show(R.string.device_name_not_null);
				} else {
					loadingDialog.show(R.string.is_the_alter);
					setDeviceName(newName);
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
	
	private void setDeviceName(final String newName) {
		deviceManager.setDeviceAttribute(currentDevice.getDevId(), AxalentUtils.ATTRIBUTE_CUSTOM_NAME, newName, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				Device bufferDevice = CacheUtils.getDeviceByName(currentDevice.getDevName());
				if (bufferDevice != null) {
					bufferDevice.setCustomName(newName);
					CacheUtils.updateDeviceAttibuteByName(currentDevice.getDevName(), AxalentUtils.ATTRIBUTE_CUSTOM_NAME, newName);
				}
				((GroupManage) currentFragment).alterGroupName(newName);
				setResult(0);
				loadingDialog.close();
				ToastUtils.show(R.string.alter_success);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				loadingDialog.close();
				ToastUtils.show(R.string.alter_failure);
			}
		});
	}
	
	
	
}
