/**
 * File Name                   : Device ����ӿ���
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/29
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.presenter.controller;

import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.axalent.model.Device;
import com.axalent.model.DeviceAttribute;
import com.axalent.model.Trigger;
import com.axalent.model.User;

public interface DeviceManager {

	public void getDeviceList(Listener<XmlPullParser> listener, ErrorListener errorListener);

	public void getDeviceTypeList(Listener<XmlPullParser> listener, ErrorListener errorListener);
	
	public void getTriggerByUser(Listener<XmlPullParser> listener, ErrorListener errorListener);
	
	public void getDeviceAttributesWithValues(Device device, Listener<XmlPullParser> listener, ErrorListener errorListener);
	
	public void getDeviceAttribute(String devId, String attributeName, Listener<XmlPullParser> listener, ErrorListener errorListener);

	public void getPresenceInfo(String devId, Listener<XmlPullParser> listener, ErrorListener errorListener);

	public void setDeviceAttribute(String devId, String attributeName, String attributeValue, Listener<XmlPullParser> listener, ErrorListener errorListener);
	
	public void getDeviceTS2(String devId, String propName, String startTime, String endTime, Listener<XmlPullParser> listener, ErrorListener errorListener);
	
	public void removeDeviceFromUser(String devId, Listener<XmlPullParser> listener, ErrorListener errorListener);

	public void addDeviceToUser(Device device, Listener<XmlPullParser> listener, ErrorListener errorListener);
	
	public void getDeviceAuth(Device device, Listener<XmlPullParser> listener, ErrorListener errorListener);
	
	public void addTrigger(Trigger trig, Listener<XmlPullParser> listener, ErrorListener errorListener);
	
	public void updateTrigger(Trigger trig, Listener<XmlPullParser> listener, ErrorListener errorListener);
	
	public void removeTrigger(String triggerId, Listener<XmlPullParser> listener, ErrorListener errorListener);
	
	public void getSceneCode(User user, String type, Listener<XmlPullParser> listener, ErrorListener errorListener);
	
	public void recycleCode(String code, Listener<XmlPullParser> listener, ErrorListener errorListener);
	
	public void getTriggerDetailListByDevice(String devId, Listener<XmlPullParser> listener, ErrorListener errorListener);

	public void setMultiDeviceAttributes2(String devId, List<DeviceAttribute> deviceAttributes, Listener<XmlPullParser> listener, ErrorListener errorListener);
	
}
