/**
 * File Name                   : User��
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/29
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
	
	private String code;
	private String appId = "1101";
	private String email;
	private String userId;
	private String username;
	private String password;
	private String securityToken;
	private List<UserAttribute> userAttributes;
	
	public User() {}
	
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	@SuppressWarnings("unchecked")
	public User(Parcel in) {
		this.code = in.readString();
		this.appId = in.readString();
		this.email = in.readString();
		this.userId = in.readString();
		this.username = in.readString();
		this.password = in.readString();
		this.securityToken = in.readString();
		this.userAttributes = in.readArrayList(DeviceAttribute.class.getClassLoader());
	}
	
	public User(String appId, String email, String userId, String username, String password, String securityToken) {
		this.appId = appId;
		this.email = email;
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.securityToken = securityToken;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSecurityToken() {
		return securityToken;
	}
	public void setSecurityToken(String securityToken) {
		this.securityToken = securityToken;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public List<UserAttribute> getUserAttributes() {
		if (userAttributes == null) {
			userAttributes = new ArrayList<UserAttribute>();
		}
		return userAttributes;
	}

	public void setUserAttributes(List<UserAttribute> userAttributes) {
		this.userAttributes = userAttributes;
	}



	/**
	 * ʵ�������л�
	 */
	
	public static final Creator<User> CREATOR = new Creator<User>() {

		@Override
		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		@Override
		public User[] newArray(int size) {
			 return new User[size];
		}
		
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeString(code);
		arg0.writeString(appId);
		arg0.writeString(email);
		arg0.writeString(userId);
		arg0.writeString(username);
		arg0.writeString(password);
		arg0.writeString(securityToken);
		arg0.writeList(userAttributes);
	}
	
	
	
	
	
}
