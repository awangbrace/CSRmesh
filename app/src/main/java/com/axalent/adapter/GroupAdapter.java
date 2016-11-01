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
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.axalent.R;
import com.axalent.model.data.model.Area;
import com.axalent.presenter.controller.DeviceManager;
import com.axalent.model.Device;
import com.axalent.util.AxalentUtils;
import com.axalent.util.LogUtils;
import com.axalent.util.XmlUtils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupAdapter extends BaseAdapter {

	private Context ctx;
	private List<Device> devices;
	private DeviceManager deviceManager;

	// bluetooth mode
	private List<Area> areas;
	private boolean isBluetooth = false;
	
	public GroupAdapter(Context ctx, List<Device> devices, DeviceManager deviceManager) {
		this.ctx = ctx;
		this.devices = devices;
		this.deviceManager = deviceManager;
	}

	public GroupAdapter(Context ctx, List<Area> areas, boolean isBluetooth) {
		this.ctx = ctx;
		this.areas = areas;
		this.isBluetooth = isBluetooth;
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
		
		public ViewHolder(View view) {
			this.img = (ImageView) view.findViewById(R.id.adapterGroupImg);
			this.txt = (TextView) view.findViewById(R.id.adapterGroupTxt);
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
	
	private void setNameAndBackground(ViewHolder holder, Device device) {
		holder.txt.setText(device.getCustomName());
		holder.img.setBackgroundResource(AxalentUtils.TYPE_AXALENT_SCENE.equals(device.getTypeName()) ? R.drawable.scene_user_define : R.drawable.schedule);
	}

	private void setNameAndBackground(ViewHolder holder, Area area) {
		holder.txt.setText(area.getName());
		holder.img.setBackgroundResource(R.drawable.scene_user_define);
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
