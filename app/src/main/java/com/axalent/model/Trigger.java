/**
 * File Name                   : ������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.model;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;


public class Trigger implements Parcelable {
	
	private String triggerId;
	private String deviceId;
	private String action;
	private String attrName;
	private String operation;
	private String threshold;
	private String stringThreshold;
	private String address;
	private String message;
	private String disarmed;
	private String autoDisarm;
	private String autoDelete;
	private String autoDisable;
	private String enable;
	private List<TriggerAttribute> triggerAttributes;
	
	public Trigger() {
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings("unchecked")
	public Trigger(Parcel in) {
		this.triggerId = in.readString();
		this.deviceId = in.readString();
		this.action = in.readString();
		this.attrName = in.readString();
		this.operation = in.readString();
		this.threshold = in.readString();
		this.stringThreshold = in.readString();
		this.address = in.readString();
		this.message = in.readString();
		this.disarmed = in.readString();
		this.autoDisarm = in.readString();
		this.autoDelete = in.readString();
		this.autoDisable = in.readString();
		this.enable = in.readString();
		this.triggerAttributes = in.readArrayList(DeviceAttribute.class.getClassLoader());
	}
	

	public String getTriggerId() {
		return triggerId;
	}
	public void setTriggerId(String triggerId) {
		this.triggerId = triggerId;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getAttrName() {
		return attrName;
	}
	
	public String getDisarmed() {
		return disarmed;
	}
	public void setDisarmed(String disarmed) {
		this.disarmed = disarmed;
	}
	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getThreshold() {
		return threshold;
	}
	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}
	
	public String getStringThreshold() {
		return stringThreshold;
	}
	public void setStringThreshold(String stringThreshold) {
		this.stringThreshold = stringThreshold;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getAutoDisarm() {
		return autoDisarm;
	}
	public void setAutoDisarm(String autoDisarm) {
		this.autoDisarm = autoDisarm;
	}
	public String getAutoDelete() {
		return autoDelete;
	}
	public void setAutoDelete(String autoDelete) {
		this.autoDelete = autoDelete;
	}
	public String getAutoDisable() {
		return autoDisable;
	}
	public void setAutoDisable(String autoDisable) {
		this.autoDisable = autoDisable;
	}
	public String getEnable() {
		return enable;
	}
	public void setEnable(String enable) {
		this.enable = enable;
	}
	public List<TriggerAttribute> getTriggerAttributes() {
		return triggerAttributes;
	}
	public void setTriggerAttributes(List<TriggerAttribute> triggerAttributes) {
		this.triggerAttributes = triggerAttributes;
	}
	
	
	public static final Creator<Trigger> CREATOR = new Creator<Trigger>() {

		@Override
		public Trigger createFromParcel(Parcel in) {
			return new Trigger(in);
		}

		@Override
		public Trigger[] newArray(int size) {
			 return new Trigger[size];
		}
		
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeString(triggerId);
		arg0.writeString(deviceId);
		arg0.writeString(action);
		arg0.writeString(attrName);
		arg0.writeString(operation);
		arg0.writeString(threshold);
		arg0.writeString(stringThreshold);
		arg0.writeString(address);
		arg0.writeString(message);
		arg0.writeString(disarmed);
		arg0.writeString(autoDisarm);
		arg0.writeString(autoDelete);
		arg0.writeString(autoDisable);
		arg0.writeString(enable);
		arg0.writeList(triggerAttributes);
	}
	
	
	
}
