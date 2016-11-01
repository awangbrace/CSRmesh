/**
 * File Name                   : ����������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TriggerAttribute implements Parcelable {
	
	private String deviceTriggerID;
	private String deviceID;
	private String attributeName ;
	private String value;
	
	public TriggerAttribute() {}
	
	public TriggerAttribute(Parcel in) {
		this.deviceTriggerID = in.readString();
		this.deviceID = in.readString();
		this.attributeName = in.readString();
		this.value = in.readString();
	}
	
	public TriggerAttribute(String deviceTriggerID, String deviceID,
			String attributeName, String value) {
		this.deviceTriggerID = deviceTriggerID;
		this.deviceID = deviceID;
		this.attributeName = attributeName;
		this.value = value;
	}

	public String getDeviceTriggerID() {
		return deviceTriggerID;
	}
	public void setDeviceTriggerID(String deviceTriggerID) {
		this.deviceTriggerID = deviceTriggerID;
	}
	public String getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public static final Creator<TriggerAttribute> CREATOR = new Creator<TriggerAttribute>() {
		@Override
		public TriggerAttribute createFromParcel(Parcel in) {
			return new TriggerAttribute(in);
		}

		@Override
		public TriggerAttribute[] newArray(int size) {
			 return new TriggerAttribute[size];
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeString(deviceTriggerID);
		arg0.writeString(deviceID);
		arg0.writeString(attributeName);
		arg0.writeString(value);
	}
	
	
	
}
