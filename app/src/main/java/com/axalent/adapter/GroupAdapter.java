/**
 * File Name                   : scene��schedule��������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.adapter;

import java.util.List;
import org.xmlpull.v1.XmlPullParser;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.axalent.R;
import com.axalent.model.DeviceAttribute;
import com.axalent.model.data.model.Area;
import com.axalent.presenter.controller.GroupInterface;
import com.axalent.presenter.controller.SceneInterface;
import com.axalent.presenter.controller.DeviceManager;
import com.axalent.model.Device;
import com.axalent.util.AxalentUtils;
import com.axalent.util.LogUtils;
import com.axalent.util.ToastUtils;
import com.axalent.util.XmlUtils;
import com.axalent.view.activity.HomeActivity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class GroupAdapter extends BaseAdapter {

	private Context ctx;
	private List<Device> devices;
	private DeviceManager deviceManager;

	// bluetooth mode
	private List<Area> areas;
	private boolean isBluetooth = false;
	private SceneInterface mSceneInterface;

	// cloud group
	private boolean isCheck = false;
	private GroupInterface mGroupInterface;
	
	public GroupAdapter(Context ctx, List<Device> devices, DeviceManager deviceManager) {
		this.ctx = ctx;
		this.devices = devices;
		this.deviceManager = deviceManager;
	}

	public GroupAdapter(Context ctx, List<Device> devices, DeviceManager deviceManager, boolean isCheck, GroupInterface groupInterface) {
		this.ctx = ctx;
		this.devices = devices;
		this.deviceManager = deviceManager;
		this.isCheck = isCheck;
		mGroupInterface = groupInterface;
	}

	public GroupAdapter(Context ctx, List<Area> areas, boolean isBluetooth, SceneInterface sceneInterface) {
		this.ctx = ctx;
		this.areas = areas;
		this.isBluetooth = isBluetooth;
		mSceneInterface = sceneInterface;
	}
	
	@Override
	public int getCount() {
		if (isBluetooth) {
			return areas.size();
		}
		return devices.size();
	}

	@Override
	public Object getItem(int arg0) {
		if (isBluetooth) {
			areas.get(arg0);
		}
		return devices.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(ctx, R.layout.adapter_group, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		setResource(holder, position);
		return convertView;
	}
	
	private class ViewHolder {
		
		TextView txt;
		ImageView img;
		ImageButton ib;
		Switch sw;
		
		public ViewHolder(View view) {
			this.img = (ImageView) view.findViewById(R.id.adapterGroupImg);
			this.txt = (TextView) view.findViewById(R.id.adapterGroupTxt);
			this.ib = (ImageButton) view.findViewById(R.id.group_setting);
			this.ib.setVisibility(isBluetooth ? View.VISIBLE : View.GONE);
			this.sw = (Switch) view.findViewById(R.id.powerSwitch);
			this.sw.setVisibility(isCheck ? View.VISIBLE : View.GONE);
		}
	}
	
	private void setResource(ViewHolder holder, int position) {
		if (isBluetooth) {
			Area area = areas.get(position);
			setNameAndBackground(holder, area);
		} else {
			Device device = devices.get(position);
			if (device.getAttributes() == null) {
				getDeviceAttribute(holder, device);
			} else {
				setNameAndBackground(holder, device);
			}
		}
	}
	
	private void setNameAndBackground(final ViewHolder holder, final Device device) {
		holder.txt.setText(device.getCustomName());
		int resources = 0;
		if (AxalentUtils.TYPE_AXALENT_SCENE.equals(device.getTypeName())) {
			resources = R.drawable.scene_user_define;
		} else if (AxalentUtils.TYPE_GATEWAY_GROUP.equals(device.getTypeName())) {
			resources = R.drawable.scene_user_define;
		} else if (AxalentUtils.TYPE_GATEWAY_SCHEDULE.equals(device.getTypeName())) {
			resources = R.drawable.schedule;
		}
		holder.img.setBackgroundResource(resources);

		if (isCheck) {
			((HomeActivity)ctx).getDeviceManager().getDeviceAttribute(device.getDevId(), AxalentUtils.ATTRIBUTE_LIGHT, new Response.Listener<XmlPullParser>() {
				@Override
				public void onResponse(XmlPullParser xmlPullParser) {
					DeviceAttribute deviceAttribute = XmlUtils.converDeviceAttribute(xmlPullParser);
					boolean value = AxalentUtils.ON.equals(deviceAttribute.getValue());
					holder.sw.setChecked(value);
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError volleyError) {
					ToastUtils.show(XmlUtils.converErrorMsg(volleyError));
				}
			});
		}

		holder.sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mGroupInterface.controllPower(device, isChecked);
			}
		});
	}

	private void setNameAndBackground(ViewHolder holder, final Area area) {
		holder.txt.setText(area.getName());
		holder.img.setBackgroundResource(R.drawable.scene_user_define);
		holder.ib.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSceneInterface.goToAddGroup(area.getId());
			}
		});
	}
	
	private void getDeviceAttribute(final ViewHolder holder, final Device device) {
		deviceManager.getDeviceAttributesWithValues(device, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				Device tempDevice = XmlUtils.convertDeviceAttributesWithValues(response);
				device.setState(tempDevice.getState());
				device.setTypeId(tempDevice.getTypeId());
				device.setTypeName(tempDevice.getTypeName());
				device.setAttributes(tempDevice.getAttributes());
				setNameAndBackground(holder, device);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				LogUtils.i("");
			}
		});
	}
	
	

}
