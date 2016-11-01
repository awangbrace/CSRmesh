/**
 * File Name                   : Switch 界面
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.fragment;

import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.axalent.R;
import com.axalent.view.activity.ShowDeviceActivity;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;
import com.axalent.util.ToastUtils;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SwitchFragment extends Fragment implements CompoundButton.OnCheckedChangeListener{

	private View view;
	private Switch switchOne;
	private Switch switchTwo;
	private ShowDeviceActivity aty;
	
	@Override
	public void onAttach(Activity activity) {
		aty = (ShowDeviceActivity) activity;
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_switch, null);
			initView();
			setDatas();
		}
		return view;
	}
	
	private void initView() {
		switchOne = (Switch) view.findViewById(R.id.fragSwitchOneSwitch);
		switchTwo = (Switch) view.findViewById(R.id.fragSwitchTwoSwitch);
		switchOne.setChecked(getState(AxalentUtils.ATTRIBUTE_SWITCH_0));
		switchTwo.setChecked(getState(AxalentUtils.ATTRIBUTE_SWITCH_1));
	}
	
	private void setDatas() {
		Map<String, String> map = AxalentUtils.getDeviceAttributeValues(aty.getCurrentDevice(), new String[]{AxalentUtils.ATTRIBUTE_SWITCH_0, AxalentUtils.ATTRIBUTE_SWITCH_1});
		String switchOneValue = map.get(AxalentUtils.ATTRIBUTE_SWITCH_0);
		String switchTwoValue = map.get(AxalentUtils.ATTRIBUTE_SWITCH_1);
		switchOne.setChecked(getState(switchOneValue));
		switchTwo.setChecked(getState(switchTwoValue));
		switchOne.setOnCheckedChangeListener(this);
		switchTwo.setOnCheckedChangeListener(this);
	}
	
	private boolean getState(String value) {
		if (value != null) {
			return AxalentUtils.ON.equals(value) ? true : false;
		}
		return false;
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		setDeviceAttribute(getKey(arg0.getId()), getVal(arg1));
	}
	
	
	private String getKey(int id) {
		switch (id) {
		case R.id.fragSwitchOneSwitch:
			return AxalentUtils.ATTRIBUTE_SWITCH_0;
		case R.id.fragSwitchTwoSwitch:
			return AxalentUtils.ATTRIBUTE_SWITCH_1;
		default:
			return AxalentUtils.ATTRIBUTE_SWITCH_0;
		}
	}
	
	private String getVal(boolean flag) {
		return flag ? AxalentUtils.ON : AxalentUtils.OFF;
	}
	
	
	private void setDeviceAttribute(final String key, final String val) {
		final String updateId = aty.getCurrentDevice().getDevId();
		aty.getDeviceManager().setDeviceAttribute(updateId, key, val, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				CacheUtils.updateDeviceAttibuteByDevId(updateId, key, val);
				aty.sendUpdateCommand(key, val);
				ToastUtils.show(R.string.action_success);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				ToastUtils.show(R.string.action_failure);
			}
		});
	}
	
	
}
