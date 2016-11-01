/**
 * File Name                   : ��ʾscene��schedule��������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.adapter;

import java.util.List;
import com.axalent.R;
import com.axalent.model.Device;
import com.axalent.model.Group;
import com.axalent.model.Trigger;
import com.axalent.util.AxalentUtils;
import com.axalent.util.LogUtils;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class ShowGroupAdapter extends BaseAdapter {

	private Context ctx;
	private String triTest;
	private List<Device> devices;
	private OnChangeStateListener onChangeStateListener;
	
	public void setOnChangeStateListener(OnChangeStateListener onChangeStateListener) {
		this.onChangeStateListener = onChangeStateListener;
	}
	
	public ShowGroupAdapter(Context ctx, List<Device> devices) {
		this.ctx = ctx;
		this.devices = devices;
	}
	
	public ShowGroupAdapter(Context ctx, List<Device> devices, String triTest) {
		this.ctx = ctx;
		this.devices = devices;
		this.triTest = triTest;
	}
	
	@Override
	public int getCount() {
		return devices.size();
	}

	@Override
	public Object getItem(int arg0) {
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
			convertView = View.inflate(ctx, R.layout.adapter_show_group, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		setResource(holder, position);
		return convertView;
	}
	
	private class ViewHolder implements OnClickListener {
		
		int position;
		TextView txt;
		ImageView img;
		Switch mSwitch;
		TextView stateTxt;
		
		public ViewHolder(View view) {
			this.img = (ImageView) view.findViewById(R.id.adapterShowGroupImg);
			this.txt = (TextView) view.findViewById(R.id.adapterShowGroupNameTxt);
			this.mSwitch = (Switch) view.findViewById(R.id.adapterShowGroupMswitch);
			this.stateTxt = (TextView) view.findViewById(R.id.adapterShowGroupStateTxt);
			this.mSwitch.setOnClickListener(this);
		}
		
		@Override
		public void onClick(View v) {
			if (onChangeStateListener != null) {
				onChangeStateListener.onChangeState(position, getValue(devices.get(position).getTypeName(), mSwitch.isChecked()));
			}
		}
	}
	
	private void setResource(ViewHolder holder, int position) {
		Device device = devices.get(position);
		holder.position = position;
		holder.txt.setText(device.getCustomName());
		holder.mSwitch.setChecked(isChecked(device));

		LogUtils.i("name:" + device.getCustomName());

		holder.img.setImageResource(AxalentUtils.getDeviceImageByTypeName(device.getTypeName()));
		if (triTest != null) {
			Group g = (Group) device;
			Trigger t = g.getTrigger();
			if (t != null) {
				float val = Float.parseFloat(t.getThreshold() == null ? "0" : t.getThreshold());
				holder.stateTxt.setText(t.getOperation()+" "+val / 100);
			}
		}
	}
	
	private boolean isChecked(Device device) {
		if (AxalentUtils.TYPE_WINDOW_COVER.equalsIgnoreCase(device.getTypeName())) {
			return Integer.parseInt(device.getToggle()) > 50;
		} 
		return AxalentUtils.ON.equals(device.getToggle());
	}
	
	private String getValue(String typeName, boolean isChecked) {
		if (AxalentUtils.TYPE_WINDOW_COVER.equalsIgnoreCase(typeName)) {
			return isChecked ? "99" : "0";
		}
		return isChecked ? "1" : "0";
	}
	
	public interface OnChangeStateListener {
		void onChangeState(int position, String value);
	}

}
