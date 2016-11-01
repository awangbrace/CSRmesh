/**
 * File Name                   : Device������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/29
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.model;

import android.os.Parcel;
import android.os.Parcelable;


public class DeviceAttribute implements Parcelable{

	private String id;
	private String name;
	private String value;
	private String updTime;
	
	public DeviceAttribute() {
		// TODO Auto-generated constructor stub
	}
	
	public DeviceAttribute(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public DeviceAttribute(String id, String name, String value, String updTime) {
		this.id = id;
		this.name = name;
		this.value = value;
		this.updTime = updTime;
	}

	public DeviceAttribute(Parcel in) {
		this.id = in.readString();
		this.name = in.readString();
		this.value = in.readString();
		this.updTime = in.readString();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUpdTime() {
		return updTime;
	}

	public void setUpdTime(String updTime) {
		this.updTime = updTime;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeString(id);
		arg0.writeString(name);
		arg0.writeString(value);
		arg0.writeString(updTime);
	}
	
	public static final Creator<DeviceAttribute> CREATOR = new Creator<DeviceAttribute>() {
		
		@Override
		public DeviceAttribute[] newArray(int arg0) {
			return new DeviceAttribute[arg0];
		}
		
		@Override
		public DeviceAttribute createFromParcel(Parcel arg0) {
			return new DeviceAttribute(arg0);
		}
	};
	

}
