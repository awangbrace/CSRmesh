/**
 * File Name                   : ��ʾ��������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.activity;

import java.util.ArrayList;
import com.axalent.R;
import com.axalent.application.BaseActivity;
import com.axalent.presenter.controller.DeviceManager;
import com.axalent.presenter.controller.impl.DeviceManagerImpl;
import com.axalent.model.Device;
import com.axalent.view.fragment.TriggerFragment;
import com.axalent.util.AxalentUtils;
import com.axalent.util.ToastUtils;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class ShowTriggerActivity extends BaseActivity implements OnClickListener {
	
	private int currentIndex = R.id.atyShowTriggerOnBtn;
	private DeviceManager dm = new DeviceManagerImpl();
	private Device currentDevice;
	private Fragment onFragment;
	private Fragment offFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_trigger);
		initActionBar();
		initView();
		init();
	}
	
	private void initActionBar() {
		View customView = findViewById(R.id.barShowGroupContent);
		TextView titleTxt = (TextView) customView.findViewById(R.id.barShowGroupTitleTxt);
		titleTxt.setText(R.string.add_trigger);
		RelativeLayout back = (RelativeLayout) customView.findViewById(R.id.barShowGroupBack);
		RelativeLayout add = (RelativeLayout) customView.findViewById(R.id.barShowGroupAdd);
		back.setOnClickListener(this);
		add.setOnClickListener(this);
	}
	
	private void initView() {
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.atyShowTriggerGroup);
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				changeFragment(checkedId);
			}
		});
	}
	
	private void init() {
		currentDevice = getIntent().getParcelableExtra(AxalentUtils.KEY_DEVICE);
		if (currentDevice.getTypeName().equals(AxalentUtils.TYPE_HTS)) {
			RadioButton onBtn = (RadioButton) findViewById(R.id.atyShowTriggerOnBtn);
			onBtn.setText(R.string.humidity);
			RadioButton offBtn = (RadioButton) findViewById(R.id.atyShowTriggerOffBtn);
			offBtn.setText(R.string.temperature);
			
			onFragment = new TriggerFragment("humidity");
		} else {
			onFragment = new TriggerFragment(AxalentUtils.ON);
		}
		
		// Ĭ����ʾ ON ����
		AxalentUtils.commitFragment(this, onFragment, R.id.atyShowTriggerFrameLayout);
	}
	
	private void changeFragment(int checkedId) {
		currentIndex = checkedId;
		switch (currentIndex) {
		case R.id.atyShowTriggerOnBtn:
			AxalentUtils.hideFragment(this, offFragment);
			AxalentUtils.showFragment(this, onFragment);
			break;
		case R.id.atyShowTriggerOffBtn:
			AxalentUtils.hideFragment(this, onFragment);
			if (offFragment == null) {
				offFragment = new TriggerFragment(currentDevice.getTypeName().equals(AxalentUtils.TYPE_HTS) ? "temperature" : AxalentUtils.OFF);
				AxalentUtils.commitFragment(this, offFragment, R.id.atyShowTriggerFrameLayout);
			} else {
				AxalentUtils.showFragment(this, offFragment);
			}
			break;
		}
	}
	
	private String getTag() {
		return currentIndex == R.id.atyShowTriggerOnBtn ? AxalentUtils.ON : AxalentUtils.OFF;
	}
	
	public DeviceManager getDeviceManager() {
		return dm;
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
		if (currentDevice.getTypeName().equals(AxalentUtils.TYPE_HTS)) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.app_logo_1);
			builder.setTitle(R.string.trigger_condition);
			View view = View.inflate(this, R.layout.dialog_hts, null);
			builder.setView(view);
			
			final Spinner operationSpinner = (Spinner) view.findViewById(R.id.dialogHtsOperationSpinner);
			operationSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
					new String[]{"==", "<=", ">=", "<", ">"}));
			final EditText valueEdit = (EditText) view.findViewById(R.id.dialogHtsValueEdit);
			
			builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					String threshold = valueEdit.getText().toString();
					if (TextUtils.isEmpty(threshold)) {
						ToastUtils.show(R.string.please_input_trigger_value);
						return;
					}
					
					if (!threshold.matches(AxalentUtils.MATCHES_NUMBER)) {
						ToastUtils.show(R.string.format_error);
						return;
					}
					
					float newThreshold = Float.parseFloat(threshold) * 100;
					
					String attrName = currentIndex == R.id.atyShowTriggerOnBtn ? "humidity" : "temperature";
					String operation = operationSpinner.getSelectedItem().toString();
					
					StringBuffer sb = new StringBuffer();
					sb.append("{")
					.append("\"attrName\":")
					.append("\""+attrName+"\",")
					.append("\"operation\":")
					.append("\""+operation+"\",")
					.append("\"threshold\":")
					.append("\""+newThreshold+"\"")
					.append("}");
					
					currentDevice.setToggle(sb.toString());
					
					
					Intent intent = new Intent(ShowTriggerActivity.this, AddActivity.class);
					intent.putExtra(AxalentUtils.KEY_SKIP, AxalentUtils.ADD_TRIGGER);
					intent.putExtra(AxalentUtils.KEY_DEVICE, currentDevice);
//					intent.putParcelableArrayListExtra(AxalentUtils.KEY_DEVICES, getChildDevices());
					startActivityForResult(intent, 0);
				}
			});
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			builder.create().show();
		} else {
			Intent intent = new Intent(this, AddActivity.class);
			intent.putExtra(AxalentUtils.KEY_SKIP, AxalentUtils.ADD_TRIGGER);
			intent.putExtra(AxalentUtils.KEY_DEVICE, getCurrentDevice());
			intent.putParcelableArrayListExtra(AxalentUtils.KEY_DEVICES, getChildDevices());
			startActivityForResult(intent, 0);
		}
	}
	
	public Device getCurrentDevice() {
		currentDevice.setToggle(getTag());
		return currentDevice;
	}
	
	private ArrayList<Device> getChildDevices() {
		switch (currentIndex) {
		case R.id.atyShowTriggerOnBtn:
			return ((TriggerFragment) onFragment).getDevices();
		case R.id.atyShowTriggerOffBtn:
			return ((TriggerFragment) offFragment).getDevices();
		default:
			return ((TriggerFragment) onFragment).getDevices();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			
			if (onFragment != null) {
				((TriggerFragment) onFragment).refresh();
			}
			
			if (offFragment != null) {
				((TriggerFragment) offFragment).refresh();
			}
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
