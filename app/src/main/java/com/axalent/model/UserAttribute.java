/**
 * File Name                   : �û�������
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

public class UserAttribute implements Parcelable {
	
	private String name;
	private String value;
	private String updTime;
	
	public UserAttribute() {
		// TODO Auto-generated constructor stub
	}
	

	public UserAttribute(String name, String value, String updTime) {
		this.name = name;
		this.value = value;
		this.updTime = updTime;
	}
	
	public UserAttribute(Parcel in) {
		this.name = in.readString();
		this.value = in.readString();
		this.updTime = in.readString();
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



	/**
	 * ʵ�������л�
	 */
	
	public static final Creator<UserAttribute> CREATOR = new Creator<UserAttribute>() {

		@Override
		public UserAttribute createFromParcel(Parcel in) {
			return new UserAttribute(in);
		}

		@Override
		public UserAttribute[] newArray(int size) {
			 return new UserAttribute[size];
		}
		
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeString(name);
		arg0.writeString(value);
		arg0.writeString(updTime);
	}
	
	
	
	

}
