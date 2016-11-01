/**
 * File Name                   : Delete Device������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/08/20
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.presenter;

import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.axalent.presenter.controller.DeviceManager;
import com.axalent.model.Device;
import com.axalent.model.HintError;
import com.axalent.model.Trigger;
import com.axalent.model.TriggerAttribute;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;
import com.axalent.util.LogUtils;
import com.axalent.util.XmlUtils;

import android.text.TextUtils;

public class DeleteDevice {
	
	private DeviceManager deviceManager;
	private Device device;
	private Device gateway;
	private OnDeleteDeviceListener onDeleteDeviceListener;
	
	public DeleteDevice(DeviceManager deviceManager, Device device) {
		this.deviceManager = deviceManager;
		this.device = device;
		start();
	}
	
	public void setOnDeleteDeviceListener(OnDeleteDeviceListener onDeleteDeviceListener) {
		this.onDeleteDeviceListener = onDeleteDeviceListener;
	}
	
	private void start() {
		removeDeviceFromUser();
	}
	
	private void removeDeviceFromUser() {
		final String deleteId = device.getDevId();
		gateway = CacheUtils.getDeviceByName(AxalentUtils.getGatewayName(device.getDevName()));
		deviceManager.removeDeviceFromUser(deleteId, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				String reqCode = XmlUtils.converRequestCode(response);
				if (AxalentUtils.REQUEST_OK.equals(reqCode)) {
					// success
					CacheUtils.deleteDeviceById(deleteId);
					if (gateway == null) {
						if (AxalentUtils.TYPE_AXALENT_SCENE.equalsIgnoreCase(device.getTypeName())) {
							recycleCode();
							deleteVirtualScenes();
						} else {
							disposeResult(true, "delete_success");
						}
					} else {
						CacheUtils.deleteTriggersByDevId(deleteId);
						disposeResult(true, "delete_success");
						deleteChildInfoIndex(device.getDevName());
					}
				} else {
					disposeResult(false, "delete_error");
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				disposeResult(false, "delete_error");
			}
		});
	}
	
	private void recycleCode() {
		deviceManager.recycleCode(device.getDevName(), new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				disposeResult(true, "delete_success");
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				disposeResult(true, "delete_success");
			}
		});
	}
	
	private void deleteVirtualScenes() {
		List<Trigger> triggers = CacheUtils.getTriggersByDevId(device.getDevId());
		if (triggers.isEmpty()) return;
		for (Trigger trigger : triggers) {
			List<TriggerAttribute> triggerAttributes = trigger.getTriggerAttributes();
			if (triggerAttributes != null && triggerAttributes.size() != 0) {
				String devId = triggerAttributes.get(0).getDeviceID();
				if (TextUtils.isEmpty(devId)) continue;
				Device virtualScene = CacheUtils.getDeviceByDevId(devId);
				if (virtualScene == null ) continue;
				deleteVirtualScene(virtualScene);
			} 
		}
		CacheUtils.deleteTriggersByDevId(device.getDevId());
	}
	
	private void deleteVirtualScene(final Device virtualScene) {
		deviceManager.removeDeviceFromUser(virtualScene.getDevId(), new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				String reqCode = XmlUtils.converRequestCode(response);
				if (AxalentUtils.REQUEST_OK.equals(reqCode)) {
					String gatewayName = AxalentUtils.getGatewayName(virtualScene.getDevName());
					if (!TextUtils.isEmpty(gatewayName)) {
						gateway = CacheUtils.getDeviceByName(gatewayName);
						if (gateway != null) {
							deleteChildInfoIndex(virtualScene.getDevName());
						}
					}
					CacheUtils.deleteDeviceById(virtualScene.getDevId());
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
			}
		});
	}
	
	private void disposeResult(boolean isSuccess, String message) {
		if (onDeleteDeviceListener != null) {
			onDeleteDeviceListener.onDeleteDevice(isSuccess, new HintError("", message));
		}
	}
	
	private void deleteChildInfoIndex(String devName) {
		final String index = AxalentUtils.getDeviceInGatewayIndex(devName);
		if (TextUtils.isEmpty(index)) return;
		deviceManager.setDeviceAttribute(gateway.getDevId(), AxalentUtils.ATTRIBUTE_DELETEDEVICE, index, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				LogUtils.i("ɾ��� ChildInfo Index:"+index);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
			}
		});
			
	}
	
	public interface OnDeleteDeviceListener {
		void onDeleteDevice(boolean isSuccess, HintError hintError);
	}
	
}
