/**
 * File Name                   : ����豸������
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
import com.axalent.model.ViewDevice;
import com.axalent.util.AxalentUtils;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class AddAdapter extends BaseAdapter {
	
	private int action;
	private Context ctx;
//	private int restore = 0;
	private List<ViewDevice> devices;
	private OnItemClickListener onItemClickListener;
	private OnCheckedDeviceListener onCheckedDeviceListener;
	
	public void setOnCheckedDeviceListener(OnCheckedDeviceListener onCheckedDeviceListener) {
		this.onCheckedDeviceListener = onCheckedDeviceListener;
	}
	
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}
	
	public AddAdapter(Context ctx, int action, List<ViewDevice> devices) {
		this.ctx = ctx;
		this.action = action;
		this.devices = devices;
	}

	@Override
	public int getCount() {
		return devices.size();
	}

	@Override
	public Object getItem(int position) {
		return devices.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			convertView = View.inflate(ctx, R.layout.adapter_add, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		setDatas(vh, position);
		return convertView;
	}
	
	public class ViewHolder implements OnClickListener {
		
		int position;
		
		ImageView animImg;
		CheckBox deviceBox;
		Switch deviceSwitch;
		TextView deviceName;
		ImageView deviceImg;
		
		public ViewHolder(View view) {
			this.deviceImg = (ImageView) view.findViewById(R.id.adapterAddImg);
			this.deviceName = (TextView) view.findViewById(R.id.adapterAddNameTxt);
			this.deviceBox = (CheckBox) view.findViewById(R.id.adapterAddBox);
			this.deviceSwitch = (Switch) view.findViewById(R.id.adapterAddMswitch);
			this.animImg = (ImageView) view.findViewById(R.id.adapterAddAnimImg);
			this.deviceBox.setOnClickListener(this);
			this.deviceImg.setOnClickListener(this);
			this.deviceSwitch.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			
			ViewDevice viewDevice = devices.get(position);
			boolean isChecked = false;
			
			switch (v.getId()) {
			case R.id.adapterAddBox:
				isChecked = deviceBox.isChecked();
				viewDevice.setBoxIsChecked(isChecked);
				if (onCheckedDeviceListener != null) {
					onCheckedDeviceListener.onChecked(position, isChecked);
				}
				setItemViewState(isChecked, position);
				break;
			case R.id.adapterAddMswitch:
				isChecked = deviceSwitch.isChecked();
				viewDevice.setSwitchIsChecked(isChecked);
				viewDevice.setToggle(getValue(viewDevice));
				break;
			case R.id.adapterAddImg:
				if (onItemClickListener != null) {
					onItemClickListener.onItemClick(v, position);
				}
				break;
//			case R.id.adapterAddAnimImg:
//				viewDevice.setBoxViewState(View.VISIBLE);
//				viewDevice.setBoxIsChecked(false);
//				viewDevice.setAnimViewState(View.GONE);
//				viewDevice.setAnimViewBgId(R.drawable.loading_2);
//				deviceBox.setVisibility(viewDevice.getBoxViewState());
//				deviceBox.setChecked(viewDevice.isBoxIsChecked());
//				animImg.setVisibility(viewDevice.getAnimViewState());
//				animImg.setBackgroundResource(viewDevice.getAnimViewBgId());
//				if (viewDevice.getAnimViewBgId() == R.drawable.error) {
//					
//				} else {
//					
//				}
//				break;
				
			}
		}

	}
	
	private void setDatas(ViewHolder vh, int position) {
		ViewDevice device = devices.get(position);
		vh.position = position;
//		device.setPosition(position);
//		device.setAnimImg(vh.animImg);
		device.setCheckedBox(vh.deviceBox);
		setViewState(vh, device);
		vh.deviceImg.setBackgroundResource(AxalentUtils.getDeviceImageByTypeName(device.getTypeName()));
//		vh.animImg.setBackgroundResource(device.getAnimViewBgId());
		vh.deviceSwitch.setChecked(device.isSwitchIsChecked());
//		vh.deviceBox.setVisibility(device.getBoxViewState());
//		vh.animImg.setVisibility(device.getAnimViewState());
		vh.deviceBox.setChecked(device.isBoxIsChecked());
		vh.deviceName.setText(device.getCustomName());
		vh.deviceBox.setEnabled(device.isEnabled());
		
	}
	
	private void setViewState(ViewHolder vh, ViewDevice device) {
		String typeName = device.getTypeName();
		if (AxalentUtils.TYPE_GATEWAY.equalsIgnoreCase(typeName)){
			vh.deviceSwitch.setVisibility(View.GONE);
		} else {
			vh.deviceSwitch.setVisibility(View.VISIBLE);
		}
	}
	
	public interface OnCheckedDeviceListener {
		void onChecked(int position, boolean isChecked);
	}
	
	public interface OnItemClickListener {
		void onItemClick(View view, int position);
	}
	
	private void setItemViewState(boolean enabled, int position) {
		switch (action) {
		case AxalentUtils.ADD_GATEWAY_DEVICE:
			setCheckBoxState(enabled, position);
			break;
//		case IntentUtils.ADD_SCHEDULE:
//		case IntentUtils.UPDATE_SCHEDULE:
//			String typeName = devices.get(position).getTypeName();
//			if (AxalentUtils.TYPE_AXALENT_SCENE.equalsIgnoreCase(typeName)) {
//				setCheckBoxState(enabled, position);
//			} else {
//				checkIsResetSceneItem(enabled);
//			}
//			break;
		}
		
	}
	
	private void setCheckBoxState(boolean enabled, int position) {
		boolean mEnabled = enabled ? false : true;
		final int size = devices.size();
		for (int i = 0; i < size; i++) {
			if (i != position) {
				devices.get(i).setEnabled(mEnabled);
			}
		}
		notifyDataSetChanged();
	}
//	
//	private void checkIsResetSceneItem(boolean enabled) {
//		if (enabled) {
//			restore ++;
//		} else {
//			restore --;
//			if (restore != 0) {
//				return;
//			}
//		}
//		setCheckBoxState(enabled ? false : true);
//	}
	
//	private void setCheckBoxState(boolean enabled) {
//		final int size = devices.size();
//		for (int i = 0; i < size; i++) {
//			ViewDevice viewDevice = devices.get(i);
//			if (AxalentUtils.TYPE_AXALENT_SCENE.equalsIgnoreCase(viewDevice.getTypeName())) {
//				viewDevice.setEnabled(enabled);
//			}
//		}
//		notifyDataSetChanged();
//	}
	
//	private void setDeviceAttributeValue(ViewDevice viewDevice) {
//		String typeName = viewDevice.getTypeName();
//		if (AxalentUtils.TYPE_SL.equalsIgnoreCase(typeName)) {
//			AxalentUtils.setDeviceAttributeValue(viewDevice, AxalentUtils.ATTRIBUTE_LIGHT, viewDevice.isSwitchIsChecked() ? AxalentUtils.ON : AxalentUtils.OFF);
//		} else if (AxalentUtils.TYPE_WINDOW_COVER.equalsIgnoreCase(typeName)) {
//			AxalentUtils.setDeviceAttributeValue(viewDevice, AxalentUtils.ATTRIBUTE_COVER, viewDevice.isSwitchIsChecked() ? "99" : "0");
//		} else if (AxalentUtils.TYPE_SWITCH_TWO.equalsIgnoreCase(typeName)) {
//			String mSwitch = viewDevice.isSwitchIsChecked() ? AxalentUtils.ON : AxalentUtils.OFF;
//			AxalentUtils.setDeviceAttributeValues(viewDevice, new String[]{AxalentUtils.ATTRIBUTE_SWITCH_0, AxalentUtils.ATTRIBUTE_SWITCH_1}, new String[]{mSwitch, mSwitch});
//		} else if (AxalentUtils.TYPE_POWER_PLUG.equalsIgnoreCase(typeName)) {
//			AxalentUtils.setDeviceAttributeValue(viewDevice, AxalentUtils.ATTRIBUTE_MYSWITCH, viewDevice.isSwitchIsChecked() ? AxalentUtils.ON : AxalentUtils.OFF);
//		}
//	}

	private String getValue(ViewDevice viewDevice) {
		if (AxalentUtils.TYPE_WINDOW_COVER.equalsIgnoreCase(viewDevice.getTypeName())) {
			return viewDevice.isSwitchIsChecked() ? "99" : "0";
		}
		return viewDevice.isSwitchIsChecked() ? "1" : "0";
	}
}
