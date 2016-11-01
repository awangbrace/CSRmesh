/**
 * File Name                   : ���������ListView��������
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
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.axalent.R;
import com.axalent.application.MyCacheData;
import com.axalent.model.data.model.devices.CSRDevice;
import com.axalent.presenter.controller.DeviceManager;
import com.axalent.model.Device;
import com.axalent.model.DeviceAttribute;
import com.axalent.util.AxalentUtils;
import com.axalent.util.LogUtils;
import com.axalent.util.XmlUtils;
import com.axalent.view.activity.HomeActivity;
import com.csr.csrmesh2.PowerState;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class MainHoriListAdapter extends BaseAdapter {
	
	private Context ctx;
	private DeviceManager dm;
	private List<Device> devices;
	private List<CSRDevice> csrDevices;

	public MainHoriListAdapter(Context ctx, DeviceManager dm, List<Device> devices) {
		this.ctx = ctx;
		this.dm = dm;
		this.devices = devices;
	}

	public MainHoriListAdapter(Context ctx,List<CSRDevice> devices) {
		this.ctx = ctx;
		this.csrDevices = devices;
	}
	
	@Override
	public int getCount() {
		if (((HomeActivity)ctx).isBluetoothMode()) {
			return csrDevices.size();
		}
		return devices.size();
	}

	@Override
	public Object getItem(int arg0) {
		if (((HomeActivity)ctx).isBluetoothMode()) {
			return csrDevices.get(arg0);
		}
		return devices.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ViewHolder vh = null;
		if (arg1 == null) {
			arg1 = View.inflate(ctx, R.layout.adapter_horizontal_list, null);
			vh = new ViewHolder(arg1);
			arg1.setTag(vh);
		} else {
			vh = (ViewHolder) arg1.getTag();
		}
		setResource(vh, arg0);
		return arg1;
	}
	
	private class ViewHolder {
		
		TextView deviceNameTxt;
		ImageView deviceBgImg;
		ImageView notConnImg;
		ImageView cloudImg;
		
		TextView val1, val2;
		
		public ViewHolder(View view) {
			this.deviceNameTxt = (TextView) view.findViewById(R.id.adapterMainDevNameTxt);
			this.deviceBgImg = (ImageView) view.findViewById(R.id.adapterMainDeviceBgImg);
			this.notConnImg = (ImageView) view.findViewById(R.id.adapterMainNotConnImg);
			this.cloudImg = (ImageView) view.findViewById(R.id.adapterMainCloudImg);
			
			this.val1 = (TextView) view.findViewById(R.id.val1);
			this.val2 = (TextView) view.findViewById(R.id.val2);
		}
		
	}
	
	private void setResource(ViewHolder vh, int position) {
		if (((HomeActivity)ctx).isBluetoothMode()) {
			CSRDevice device = csrDevices.get(position);
			setDeviceResource(vh, device);
		} else {
			Device device = devices.get(position);
			List<DeviceAttribute> attributes = device.getAttributes();
			if (attributes == null) {
				vh.deviceNameTxt.setVisibility(View.GONE);
				getDeviceAttribute(vh, device);
			} else {
				setDeviceResource(vh, device);
			}
		}
	}

	private void setCSRDeviceImageState(ViewHolder vh,int off) {
		vh.deviceBgImg.setBackgroundResource(off);
		vh.notConnImg.setVisibility(View.GONE);
		vh.cloudImg.setVisibility(View.GONE);

	}
	
	private void setDeviceImageState(Device device, ViewHolder vh, String key, int on, int off) {
		
		if (AxalentUtils.ONLINE.equals(device.getState())) {
			String toggle = AxalentUtils.getDeviceAttributeValue(device, key);
			if (device.getTypeName().equals(AxalentUtils.TYPE_SL)) {
				LogUtils.i("adapter �����豸����:"+device.getDevName()+" toggle:"+toggle);
			}
			device.setToggle(toggle == null || toggle.equals("") ? AxalentUtils.OFF : toggle);
			vh.deviceBgImg.setBackgroundResource(AxalentUtils.ON.equals(device.getToggle()) ? on : off);
			vh.notConnImg.setVisibility(View.GONE);
			vh.cloudImg.setVisibility(View.GONE);
		} else if (AxalentUtils.OFFLINE.equals(device.getState())){
			vh.deviceBgImg.setBackgroundResource(off);
			vh.notConnImg.setVisibility(View.VISIBLE);
			vh.cloudImg.setVisibility(View.VISIBLE);
		}
	}
	
	private void setDeviceImageState(Device device, ViewHolder vh, int imgId) {
		vh.deviceBgImg.setBackgroundResource(imgId);
		if (AxalentUtils.ONLINE.equals(device.getState())) {
			vh.notConnImg.setVisibility(View.GONE);
			vh.cloudImg.setVisibility(View.GONE);
		} else {
			vh.notConnImg.setVisibility(View.VISIBLE);
			vh.cloudImg.setVisibility(View.VISIBLE);
		}
	}
	
	private void setDeviceResource(ViewHolder vh, CSRDevice csrDevice) {
		int type = csrDevice.getType();
		vh.deviceNameTxt.setVisibility(View.VISIBLE);
		vh.deviceNameTxt.setText(csrDevice.getName());
		switch (type) {
			case CSRDevice.TYPE_LIGHT:
				PowerState power = MyCacheData.getInstance().getState(csrDevice.getDeviceID());
				Log.i("TYPE_LIGHT_state", "setDeviceResource: " + power);
				setCSRDeviceImageState(vh, power == PowerState.ON ? R.drawable.light_on : R.drawable.light_off);
				break;
			case CSRDevice.TYPE_TEMPERATURE:
				setCSRDeviceImageState(vh, R.drawable.hts);
				Log.i("csr device", ((HomeActivity)ctx).csrDeviceId+"");
				for (Integer i : csrDevice.getGroupsList()) {
					if (((HomeActivity)ctx).csrDeviceId == i) {
						double temperature = ((HomeActivity)ctx).getTemperature();
						vh.val1.setText(temperature != 0d ? temperature + "°C" : "0.0°C");
						vh.val2.setText("0%");
					}
				}
				break;
			case CSRDevice.TYPE_GATEWAY:
				setCSRDeviceImageState(vh, R.drawable.gateway);
				break;
		}
	}
	
	private void setDeviceResource(ViewHolder vh, Device device) {
		String typeName = device.getTypeName();
		vh.deviceNameTxt.setVisibility(View.VISIBLE);
		vh.deviceNameTxt.setText(device.getCustomName());
		vh.val1.setText("");
		vh.val2.setText("");
		if (AxalentUtils.TYPE_SL.equalsIgnoreCase(typeName)) {
			// Light Bulb
			setDeviceImageState(device, vh, AxalentUtils.ATTRIBUTE_LIGHT, R.drawable.light_on, R.drawable.light_off);
		} else if (AxalentUtils.TYPE_GUNI_LAMP.equalsIgnoreCase(typeName)) {
			// GUNI LAMP
			setDeviceImageState(device, vh, AxalentUtils.ATTRIBUTE_GU_LIGHT, R.drawable.light_on, R.drawable.light_off);
		} else if (AxalentUtils.TYPE_SM.equalsIgnoreCase(typeName)) {
			// Motion Sensor
			setDeviceImageState(device, vh, AxalentUtils.ATTRIBUTE_MOTION, R.drawable.motion_on, R.drawable.motion_off);
		} else if (AxalentUtils.TYPE_FLOOD_SENSOR.equalsIgnoreCase(typeName)) {
			// Flood Sensor
			setDeviceImageState(device, vh, AxalentUtils.ATTRIBUTE_FLOOD, R.drawable.flood_detected, R.drawable.flood_dry);
		} else if (AxalentUtils.TYPE_SHCK.equalsIgnoreCase(typeName)) {
			// SHCK
			setDeviceImageState(device, vh, AxalentUtils.ATTRIBUTE_SHOCK, R.drawable.shock_sensor_on, R.drawable.shock_sensor_off);
		} else if (AxalentUtils.TYPE_SSMOKE.equalsIgnoreCase(typeName)) {
			// Smoke Sensor
			setDeviceImageState(device, vh, AxalentUtils.ATTRIBUTE_SMOKE, R.drawable.smoke_on, R.drawable.smoke_off);
		} else if (AxalentUtils.TYPE_GATEWAY.equalsIgnoreCase(typeName)) {
			// Gateway
			setDeviceImageState(device, vh, R.drawable.gateway);
		} else if (AxalentUtils.TYPE_POWER_PLUG.equalsIgnoreCase(typeName)) {
			// Power Plug
			setDeviceImageState(device, vh, AxalentUtils.ATTRIBUTE_MYSWITCH, R.drawable.plug_on, R.drawable.plug_off);
		} else if (AxalentUtils.TYPE_GUNI_PLUG.equalsIgnoreCase(typeName)) {
			// GUNI PLUG
			setDeviceImageState(device, vh, AxalentUtils.ATTRIBUTE_GU_SWITCH, R.drawable.plug_on, R.drawable.plug_off);
		} else if (AxalentUtils.TYPE_SC.equalsIgnoreCase(typeName)) {
			// Contact Sensor
			setDeviceImageState(device, vh, AxalentUtils.ATTRIBUTE_CONTACT, R.drawable.door_open, R.drawable.door_closed);
		} else if (AxalentUtils.TYPE_HTM.equalsIgnoreCase(typeName)) {
			// Health Thermometer
			setDeviceImageState(device, vh, R.drawable.htm);
		} else if (AxalentUtils.TYPE_HTS.equalsIgnoreCase(typeName)) {
			// Humidity and Temperature Sensor
			
			setDeviceImageState(device, vh, R.drawable.hts);
			String temperatureVal = null;
			String humidityVal = null;
			
			for (DeviceAttribute deviceAttribute : device.getAttributes()) {
				String name = deviceAttribute.getName();
				String value = deviceAttribute.getValue();
				if (name.equals(AxalentUtils.ATTRIBUTE_TEMPERATURE)) {
					temperatureVal = value == null ? "" : value;
				}
				if (name.equals(AxalentUtils.ATTRIBUTE_HUMIDITY)) {
					humidityVal = value == null ? "" : value;
				}
			}
			
			if (temperatureVal.equals("")) temperatureVal = "0";
			if (humidityVal.equals("")) humidityVal = "0";
			
			vh.val1.setText(Float.parseFloat(temperatureVal) / 100 + "°C");
			vh.val2.setText(Float.parseFloat(humidityVal) / 100 + "%");
			
			
		} else if (AxalentUtils.TYPE_BEACON.equalsIgnoreCase(typeName)) {
			// Beacon
			setDeviceImageState(device, vh, R.drawable.beacon);
		} else if (AxalentUtils.TYPE_LIGHTSENSOR.equalsIgnoreCase(typeName)) {
			// light sensor
			setDeviceImageState(device, vh, R.drawable.light_sensor);
		} else if (AxalentUtils.TYPE_WINDOW_COVER.equalsIgnoreCase(typeName)) {
			// Window Cover
			if (AxalentUtils.ONLINE.equals(device.getState())) {
				String toggle = AxalentUtils.getDeviceAttributeValue(device, AxalentUtils.ATTRIBUTE_COVER);
				device.setToggle(toggle == null || toggle.equals("") ? AxalentUtils.OFF : toggle);
				int deviceBg = Integer.parseInt(toggle) >= 50 ? R.drawable.shade_open : R.drawable.shade_close;
				vh.deviceBgImg.setBackgroundResource(deviceBg);
				vh.notConnImg.setVisibility(View.GONE);
				vh.cloudImg.setVisibility(View.GONE);
			} else if (AxalentUtils.OFFLINE.equals(device.getState())){
				vh.deviceBgImg.setBackgroundResource(R.drawable.shade_close);
				vh.notConnImg.setVisibility(View.VISIBLE);
				vh.cloudImg.setVisibility(View.VISIBLE);
			}
		
		
		} else if (AxalentUtils.TYPE_SWITCH_TWO.equalsIgnoreCase(typeName)) {
			// Two-way Switch
			if (AxalentUtils.ONLINE.equals(device.getState())) {
				
				String mSwitch0 = "";
				String mSwitch1 = "";
				
				List<DeviceAttribute> deviceAttributes = device.getAttributes();
				for (DeviceAttribute deviceAttribute : deviceAttributes) {
					String name = deviceAttribute.getName();
					String value = deviceAttribute.getValue();
					if (TextUtils.isEmpty(value)) {
						value = AxalentUtils.OFF;
					}
					if (AxalentUtils.ATTRIBUTE_SWITCH_0.equals(name)) {
						mSwitch0 = value;
					} else if (AxalentUtils.ATTRIBUTE_SWITCH_1.equals(name)) {
						mSwitch1 = value;
					}
				}
				
				if (mSwitch0.equals(AxalentUtils.ON) && mSwitch1.equals(AxalentUtils.ON)) {
					vh.deviceBgImg.setBackgroundResource(R.drawable.switch_two_on);
				} else if (mSwitch0.equals(AxalentUtils.OFF) && mSwitch1.equals(AxalentUtils.OFF)) {
					vh.deviceBgImg.setBackgroundResource(R.drawable.switch_two_off);
				} else if (mSwitch0.equals(AxalentUtils.ON) || mSwitch1.equals(AxalentUtils.OFF)) {
					vh.deviceBgImg.setBackgroundResource(R.drawable.switch_two_on_off);
				} else if (mSwitch0.equals(AxalentUtils.OFF) || mSwitch1.equals(AxalentUtils.ON)) {
					vh.deviceBgImg.setBackgroundResource(R.drawable.switch_two_off_on);
				}
				
				vh.notConnImg.setVisibility(View.GONE);
				vh.cloudImg.setVisibility(View.GONE);
			} else if (AxalentUtils.OFFLINE.equals(device.getState())){
				vh.deviceBgImg.setBackgroundResource(R.drawable.switch_two_off);
				vh.notConnImg.setVisibility(View.VISIBLE);
				vh.cloudImg.setVisibility(View.VISIBLE);
			}
		
		} else if (AxalentUtils.TYPE_BPM.equalsIgnoreCase(typeName)) {
			// BPM
			setDeviceImageState(device, vh, R.drawable.bloodmonitor);
		} else if (AxalentUtils.TYPE_REMOTECONTROL.equalsIgnoreCase(typeName)) {
			// RemoteControl
			
		} else if (AxalentUtils.TYPE_CAMERA.equalsIgnoreCase(typeName)) {
			vh.deviceBgImg.setBackgroundResource(R.drawable.camera);
			int visibility = TextUtils.isEmpty(device.getPassword()) ? View.VISIBLE : View.GONE;
			vh.notConnImg.setVisibility(visibility);
			vh.cloudImg.setVisibility(visibility);
		} else if (AxalentUtils.TYPE_FLOOD_SENSOR.equalsIgnoreCase(typeName)){
			setDeviceImageState(device, vh, AxalentUtils.ATTRIBUTE_FLOOD, R.drawable.motion_on, R.drawable.motion_off);
		} else if (AxalentUtils.TYPE_GAS_SENSOR.equalsIgnoreCase(typeName)) {
			setDeviceImageState(device, vh, R.drawable.gas_sensor);
			String CO2 = null;
			String VOC = null;
			
			for (DeviceAttribute deviceAttribute : device.getAttributes()) {
				String name = deviceAttribute.getName();
				String value = deviceAttribute.getValue();
				if (name.equals(AxalentUtils.ATTRIBUTE_CO2)) {
					CO2 = value == null ? "" : value;
				}
				if (name.equals(AxalentUtils.ATTRIBUTE_VOC)) {
					VOC = value == null ? "" : value;
				}
			}
			if (CO2.equals("")) CO2 = "0";
			if (VOC.equals("")) VOC = "0";
			
			vh.val1.setText(CO2);
			vh.val2.setText(VOC);
			
			
		} else if (AxalentUtils.TYPE_EBIO_EPAD.equalsIgnoreCase(typeName)) {
			setDeviceImageState(device, vh, AxalentUtils.ATTRIBUTE_STATUS, R.drawable.pad_on, R.drawable.pad_off);
		} else if (AxalentUtils.TYPE_MTM_SM.equalsIgnoreCase(typeName)) {
			if (AxalentUtils.ONLINE.equals(device.getState())) {
				String roll = AxalentUtils.getDeviceAttributeValue(device, AxalentUtils.ATTRIBUTE_ROLL);
				if (roll.equals("")) roll = "1";
				int[] imgs = {R.drawable.dice1, R.drawable.dice2, R.drawable.dice3, 
						R.drawable.dice4, R.drawable.dice5, R.drawable.dice6};
	
				vh.deviceBgImg.setBackgroundResource(imgs[Integer.parseInt(roll)]);
				vh.notConnImg.setVisibility(View.GONE);
				vh.cloudImg.setVisibility(View.GONE);
			} else if (AxalentUtils.OFFLINE.equals(device.getState())){
				vh.deviceBgImg.setBackgroundResource(R.drawable.dice1);
				vh.notConnImg.setVisibility(View.VISIBLE);
				vh.cloudImg.setVisibility(View.VISIBLE);
			}
			
		} else if (AxalentUtils.TYPE_MOUSE_TRAP.equalsIgnoreCase(typeName)) {
			setDeviceImageState(device, vh, AxalentUtils.ATTRIBUTE_TRAPPED, R.drawable.mouser_got_mouse, R.drawable.mouser_no_mouse);
		} else if (AxalentUtils.TYPE_SIREN.equalsIgnoreCase(typeName)) {
			setDeviceImageState(device, vh, AxalentUtils.ATTRIBUTE_ALARM, R.drawable.siren_on, R.drawable.siren_off);
		} else if (AxalentUtils.TYPE_GUNI_SWITCH.equalsIgnoreCase(typeName)) {
//			setDeviceImageState(device, vh, AxalentUtils.ATTRIBUTE_LIGHT, R.drawable.light_on, R.drawable.light_off);
			setDeviceImageState(device, vh, R.drawable.light_sensor);
		} else if (AxalentUtils.TYPE_VALVE.equalsIgnoreCase(typeName)) {
			setDeviceImageState(device, vh, AxalentUtils.ATTRIBUTE_VALVE, R.drawable.valve_open, R.drawable.valve_closed);
		} else {
			setDeviceImageState(device, vh, R.drawable.light_sensor);
		}
	}
	
	private void getDeviceAttribute(final ViewHolder vh, final Device device) {
		dm.getDeviceAttributesWithValues(device, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				LogUtils.i("���ڲ�ѯ�豸����:"+device.getDevName());
				Device tempDevice = XmlUtils.convertDeviceAttributesWithValues(response);
				device.setState(tempDevice.getState());
				device.setTypeId(tempDevice.getTypeId());
				device.setTypeName(tempDevice.getTypeName());
				device.setAttributes(tempDevice.getAttributes());
				setDeviceResource(vh, device);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
//				LogUtils.i("");
			}
		});
	}
	
	@Override
	public void notifyDataSetChanged() {
		
		super.notifyDataSetChanged();
	}

}
