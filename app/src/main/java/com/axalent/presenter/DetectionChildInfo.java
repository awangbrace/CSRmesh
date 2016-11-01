/**
 * File Name                   : 检查设备的childinfo
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.presenter;

import org.xmlpull.v1.XmlPullParser;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.axalent.presenter.controller.DeviceManager;
import com.axalent.model.Device;
import com.axalent.model.DeviceAttribute;
import com.axalent.model.Trigger;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;
import com.axalent.util.XmlUtils;
import android.text.TextUtils;

public class DetectionChildInfo {
	
	private int count = 0; 
	private Device gateway;
	private String detectionType;
	private DeviceManager deviceManager;
	private StringBuilder sb = new StringBuilder();
	private OnDetectionDeviceListener onDetectionDeviceListener = null;
	
	public DetectionChildInfo(DeviceManager deviceManager, Device gateway, String detectionType) {
		this.deviceManager = deviceManager;
		this.gateway = gateway;
		this.detectionType = detectionType;
		getPresenceInfo();
	}
	
	public DetectionChildInfo(Device gateway, String detectionType) {
		this.gateway = gateway;
		this.detectionType = detectionType;
		detection(AxalentUtils.getDeviceAttributeValue(gateway, AxalentUtils.ATTRIBUTE_CHILDINFO));
	}
	
	private void getPresenceInfo() {
		deviceManager.getPresenceInfo(gateway.getDevId(), new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				String state = XmlUtils.convertPresenceInfo(response);
				if (AxalentUtils.ONLINE.equals(state)) {
					getChildInfo();
				} else {
					onDetectionDeviceListener.onDetectionDevice(null, 0);
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				onDetectionDeviceListener.onDetectionDevice(null, 0);
			}
		});
	}
	
	private void getChildInfo() {
		deviceManager.getDeviceAttribute(gateway.getDevId(), AxalentUtils.ATTRIBUTE_CHILDINFO, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				DeviceAttribute deviceAttribute = XmlUtils.converDeviceAttribute(response);
				String value = deviceAttribute.getValue();
				if (!TextUtils.isEmpty(value) && value.contains("00")) {
					detection(value);
				} else {
					onDetectionDeviceListener.onDetectionDevice(null, 0);
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				onDetectionDeviceListener.onDetectionDevice(null, 0);
			}
		});
	}

	public void detection(String childInfo) {
		if (!TextUtils.isEmpty(childInfo)) {
			char[] c = childInfo.toCharArray();
			for (int i = 0; i < c.length; i++) {
				sb.append(c[i]);
				if (sb.length() == 2) {
					count ++;
					String str = sb.toString();
					if (detectionType.equals(str)) {
						String devName = AxalentUtils.getDevName(count, gateway.getDevName());
						Device device = CacheUtils.getDeviceByName(devName);
						if (device != null) {
							if ("C2".equals(detectionType)) {
								sb.setLength(0);
								continue;
							}
							String state = device.getState();
							if (AxalentUtils.OFFLINE.equals(state)) {
								sb.setLength(0);
								continue;
							}
							Trigger trigger = CacheUtils.getTriggerByAttributeDevId(device.getDevId());
							if (trigger == null) {
								onDetectionDeviceListener.onDetectionDevice(device, AxalentUtils.DETECTION_NOT_ATTRIBUTE);
								return;
							}
	//						String devInfo = AxalentUtils.getDeviceAttributeValue(device, AxalentUtils.ATTRIBUTE_DEVINFO);
	//						if (TextUtils.isEmpty(devInfo)) {
	//							onDetectionDeviceListener.onDetectionDevice(device, AxalentUtils.DETECTION_NOT_ATTRIBUTE);
	//							return;
	//						}
	//						if (!devInfo.contains(AxalentUtils.ATTRIBUTE_CODE)){
	//							onDetectionDeviceListener.onDetectionDevice(device, AxalentUtils.DETECTION_NOT_ATTRIBUTE);
	//							return;
	//						}
						} else {
							device = new Device();
							device.setTypeId("");
							device.setPassword("XX");
							device.setDevName(devName);
							device.setTypeName(AxalentUtils.getTypeName(str));
							device.setDisplayName(device.getTypeName());
							onDetectionDeviceListener.onDetectionDevice(device, AxalentUtils.DETECTION_NOT_ADD);
							return;
						}
					}
					sb.setLength(0);
				}
			}
		}
		onDetectionDeviceListener.onDetectionDevice(null, 0);
	}
	
	
	public void setOnDetectionDeviceListener(OnDetectionDeviceListener onDetectionDeviceListener) {
		this.onDetectionDeviceListener = onDetectionDeviceListener;
	}
	
	public interface OnDetectionDeviceListener {
		void onDetectionDevice(Device device, int detectionResult);
	}
	

}
