/**
 * File Name                   : �û�����
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.presenter.controller;

import org.xmlpull.v1.XmlPullParser;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.axalent.model.User;

public interface UserManager {

	public void userLogin(User user, Listener<XmlPullParser> listener,
						  ErrorListener errorListener);
	
	public void registerUser(User user, Listener<XmlPullParser> listener,
							 ErrorListener errorListener);
	
	public void activateUser(String activateCode, Listener<XmlPullParser> listener,
							 ErrorListener errorListener);
	
	public void updatePassword(User user, String newPassword, Listener<XmlPullParser> listener,
							   ErrorListener errorListener);
	
	public void requestPasswordRecoveryCode(String emailAddress, Listener<XmlPullParser> listener,
											ErrorListener errorListener);
	
	public void updateUserPassword(User user, Listener<XmlPullParser> listener,
								   ErrorListener errorListener);
	
	public void syncLocalData(User user, Listener<XmlPullParser> listener,
							  ErrorListener errorListener);
	
	public void setUserAttribute(String name, String value, Listener<XmlPullParser> listener,
								 ErrorListener errorListener);
	
	public void getUserValueList(Listener<XmlPullParser> listener,
								 ErrorListener errorListener);

}
