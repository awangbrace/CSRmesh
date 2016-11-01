/**
 * File Name                   : Device��
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/29
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.model;

import java.util.List;

import com.axalent.util.AxalentUtils;

import android.os.Parcel;
import android.os.Parcelable;

public class Device implements Parcelable{

	private String uid;
	private String devId;
	private String state;
	private String userId;
	private String typeId;
	private String toggle;
	private String devName;
	private String typeName;
	private String password;
	private String customName;
	private String displayName;
	private List<DeviceAttribute> attributes;
	
	public Device() {
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings("unchecked")
	public Device(Parcel in) {
		this.uid = in.readString();
		this.devId = in.readString();
		this.state = in.readString();
		this.userId = in.readString();
		this.typeId = in.readString();
		this.toggle = in.readString();
		this.devName = in.readString();
		this.typeName = in.readString();
		this.password = in.readString();
		this.customName = in.readString();
		this.displayName = in.readString();
		this.attributes = in.readArrayList(DeviceAttribute.class.getClassLoader());
	}
	
	
	public Device(String uid, String devId, String state, String userId,
			String typeId, String toggle, String devName, String typeName,
			String password, String customName, String displayName,
			List<DeviceAttribute> attributes) {
		this.uid = uid;
		this.devId = devId;
		this.state = state;
		this.userId = userId;
		this.typeId = typeId;
		this.toggle = toggle;
		this.devName = devName;
		this.typeName = typeName;
		this.password = password;
		this.customName = customName;
		this.displayName = displayName;
		this.attributes = attributes;
	}



	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getDevId() {
		return devId;
	}
	public void setDevId(String devId) {
		this.devId = devId;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	public String getDevName() {
		return devName;
	}
	public void setDevName(String devName) {
		this.devName = devName;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCustomName() {
		String cn = AxalentUtils.getDeviceAttributeValue(attributes, AxalentUtils.ATTRIBUTE_CUSTOM_NAME);
		setCustomName(cn == null || cn.equals("") ? devName : cn);
		return customName;
	}
	public void setCustomName(String customName) {
		this.customName = customName;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public List<DeviceAttribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<DeviceAttribute> attributes) {
		this.attributes = attributes;
	}
	
	public String getToggle() {
		return toggle;
	}

	public void setToggle(String toggle) {
		this.toggle = toggle;
	}

	/**
	 * ʵ�������л�
	 */
	
	public static final Creator<Device> CREATOR = new Creator<Device>() {

		@Override
		public Device createFromParcel(Parcel in) {
			return new Device(in);
		}

		@Override
		public Device[] newArray(int size) {
			 return new Device[size];
		}
		
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeString(uid);
		arg0.writeString(devId);
		arg0.writeString(state);
		arg0.writeString(userId);
		arg0.writeString(typeId);
		arg0.writeString(toggle);
		arg0.writeString(devName);
		arg0.writeString(typeName);
		arg0.writeString(password);
		arg0.writeString(customName);
		arg0.writeString(displayName);
		arg0.writeList(attributes);
	}
	
	
	
}
