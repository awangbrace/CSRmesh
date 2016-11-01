/**
 * File Name                   : �û�����ʵ����
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.presenter.controller.impl;

import java.util.HashMap;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.axalent.application.MyRequestQueue;
import com.axalent.application.XmlRequest;
import com.axalent.model.User;
import com.axalent.presenter.controller.UserManager;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;

public class UserManagerImpl implements UserManager {

	@Override
	public void userLogin(final User user, Listener<XmlPullParser> listener, ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "userLogin", listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("name", user.getUsername());
				map.put("password", user.getPassword());
				map.put("appId", user.getAppId());
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

	@Override
	public void registerUser(final User user, Listener<XmlPullParser> listener, ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "registerUser", listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("applicationId", user.getAppId());
				map.put("email", user.getEmail());
				map.put("username", user.getUsername());
				map.put("password", user.getPassword());
				map.put("appId", user.getAppId());
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

	@Override
	public void activateUser(final String activateCode, Listener<XmlPullParser> listener, ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "activateUser", listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("activationCode", activateCode);
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

	@Override
	public void updatePassword(final User user, final String newPassword, Listener<XmlPullParser> listener,
			ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "updatePassword", listener,
				errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("secToken", user.getSecurityToken());
				map.put("username", user.getUsername());
				map.put("oldPassword", user.getPassword());
				map.put("newPassword", newPassword);
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

	@Override
	public void requestPasswordRecoveryCode(final String emailAddress, Listener<XmlPullParser> listener,
			ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "requestPasswordRecoveryCode", listener,
				errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("email", emailAddress);
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

	@Override
	public void updateUserPassword(final User user, Listener<XmlPullParser> listener, ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "updateUserPassword", listener,
				errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("recoveryCode", user.getCode());
				map.put("username", user.getUsername());
				map.put("password", user.getPassword());
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

	@Override
	public void syncLocalData(final User user, Listener<XmlPullParser> listener, ErrorListener errorListener) {

		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "syncData", listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("name", user.getUsername());
				map.put("password", user.getPassword());
				map.put("appId", user.getAppId());
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

	@Override
	public void setUserAttribute(final String name, final String value, Listener<XmlPullParser> listener,
			ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "setUserAttribute", listener,
				errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", CacheUtils.getUser().getUserId());
				map.put("secToken", CacheUtils.getUser().getSecurityToken());
				map.put("name", name);
				map.put("value", value);
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);

	}

	@Override
	public void getUserValueList(Listener<XmlPullParser> listener, ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "getUserValueList", listener,
				errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", CacheUtils.getUser().getUserId());
				map.put("userName", CacheUtils.getUser().getUsername());
				map.put("secToken", CacheUtils.getUser().getSecurityToken());
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}
	
//	public void addEmailTemplate(Listener<XmlPullParser> listener, ErrorListener errorListener) {
//		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "addEmailTemplate", listener,
//				errorListener) {
//			@Override
//			protected Map<String, String> getParams() throws AuthFailureError {
//				Map<String, String> map = new HashMap<String, String>();
//				map.put("secToken", "100002-1897844264");
//				map.put("applicationId", "1101");
//				map.put("notificationStage", "RESET_PASSWORD");
//				map.put("locale", "en");
//				map.put("fromEmail", "support@domeHA.com");
//				map.put("fromName", "DomeHA");
//				map.put("subject", "DOME Password Reset To Reset your password");
//				map.put("content", "1.Copy provided Confirmation Code&#10 2.Return to the DOME app&#10 3.Enter Confirmation Code and new password&#10 CONFIRMATION CODE: *|RECOVERY_CODE|*");
//				return map;
//			}
//		};
//		MyRequestQueue.addToRequestQueue(req);
//	}

}
