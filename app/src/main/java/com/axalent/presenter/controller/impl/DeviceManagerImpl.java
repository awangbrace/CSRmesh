/**
 * File Name                   : Device ����ʵ����
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/29
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.presenter.controller.impl;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.axalent.application.MyRequestQueue;
import com.axalent.application.XmlRequest;
import com.axalent.presenter.controller.DeviceManager;
import com.axalent.model.Device;
import com.axalent.model.DeviceAttribute;
import com.axalent.model.Trigger;
import com.axalent.model.TriggerAttribute;
import com.axalent.model.User;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;

public class DeviceManagerImpl implements DeviceManager {

	@Override
	public void getDeviceList(Listener<XmlPullParser> listener, ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "getDeviceList", listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", CacheUtils.getUser().getUserId());
				map.put("secToken", CacheUtils.getUser().getSecurityToken());
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

	@Override
	public void getDeviceAttributesWithValues(final Device device, Listener<XmlPullParser> listener,
			ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "getDeviceAttributesWithValues", listener,
				errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("devId", device.getDevId());
				map.put("deviceTypeId", device.getTypeId());
				map.put("secToken", CacheUtils.getUser().getSecurityToken());
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

	public void getDeviceTypeList(Listener<XmlPullParser> listener, ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "getDeviceTypeList", listener,
				errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("secToken", CacheUtils.getUser().getSecurityToken());
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

	@Override
	public void getPresenceInfo(final String devId, Listener<XmlPullParser> listener, ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "getDevicePresenceInfo", listener,
				errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("devId", devId);
				map.put("secToken", CacheUtils.getUser().getSecurityToken());
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

	public void setDeviceAttribute(final String devId, final String attributeName, final String attributeValue,
			Listener<XmlPullParser> listener, ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "setDeviceAttribute", listener,
				errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("secToken", CacheUtils.getUser().getSecurityToken());
				map.put("devId", devId);
				map.put("name", attributeName);
				map.put("value", attributeValue);
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

	@Override
	public void getDeviceTS2(final String devId, final String propName, final String startTime, final String endTime,
			Listener<XmlPullParser> listener, ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "getDeviceTS2", listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("secToken", CacheUtils.getUser().getSecurityToken());
				map.put("devId", devId);
				map.put("propName", propName);
				map.put("start", startTime);
				map.put("end", endTime);
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

	@Override
	public void removeDeviceFromUser(final String devId, Listener<XmlPullParser> listener,
			ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "removeDeviceFromUser", listener,
				errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("secToken", CacheUtils.getUser().getSecurityToken());
				map.put("userId", CacheUtils.getUser().getUserId());
				map.put("deviceId", devId);
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

	@Override
	public void addDeviceToUser(final Device device, Listener<XmlPullParser> listener, ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "addDeviceToUser", listener,
				errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("secToken", CacheUtils.getUser().getSecurityToken());
				map.put("userId", CacheUtils.getUser().getUserId());
				map.put("deviceId", device.getDevId());
				map.put("deviceName", device.getDevName());
				map.put("typeName", device.getTypeName());
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

	@Override
	public void getDeviceAuth(final Device device, Listener<XmlPullParser> listener, ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "deviceAuth", listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("secToken", CacheUtils.getUser().getSecurityToken());
				map.put("name", device.getDevName());
				map.put("password", device.getPassword());
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);

	}

	@Override
	public void addTrigger(final Trigger trig, Listener<XmlPullParser> listener, ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "addTrigger", listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new IdentityHashMap<String, String>();
				map.put("secToken", CacheUtils.getUser().getSecurityToken());
				map.put("devId", trig.getDeviceId());
				map.put("action", trig.getAction());
				map.put("attrName", trig.getAttrName());
				map.put("operation", trig.getOperation());
				map.put("threshold", trig.getThreshold());
				map.put("address", trig.getAddress());
				map.put("msg", trig.getMessage());
				map.put("autoDisarm", trig.getAutoDisarm());
				map.put("autoDelete", trig.getAutoDelete());
				map.put("autoDisable", trig.getAutoDisable());
				map.put("enable", trig.getEnable());
				List<TriggerAttribute> triggerAttributes = trig.getTriggerAttributes();
				for (TriggerAttribute triggerAttribute : triggerAttributes) {
					map.put(new String("targetDeviceAttributes"), triggerAttribute.getDeviceID() + ","
							+ triggerAttribute.getAttributeName() + ":" + triggerAttribute.getValue());
				}
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

	@Override
	public void updateTrigger(final Trigger trig, Listener<XmlPullParser> listener, ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "updTrigger", listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("secToken", CacheUtils.getUser().getSecurityToken());
				map.put("triggerId", trig.getTriggerId());
				map.put("devId", trig.getDeviceId());
				map.put("action", trig.getAction());
				map.put("attrName", trig.getAttrName());
				map.put("operation", trig.getOperation());
				map.put("threshold", trig.getThreshold());
				map.put("address", trig.getAddress());
				map.put("msg", trig.getMessage());
				map.put("autoDisarm", trig.getAutoDisarm());
				map.put("autoDelete", trig.getAutoDelete());
				map.put("autoDisable", trig.getAutoDisable());
				map.put("enable", trig.getEnable());
				List<TriggerAttribute> triggerAttributes = trig.getTriggerAttributes();
				for (TriggerAttribute triggerAttribute : triggerAttributes) {
					map.put("targetDeviceAttributes", triggerAttribute.getDeviceID() + ","
							+ triggerAttribute.getAttributeName() + ":" + triggerAttribute.getValue());
				}
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

	@Override
	public void getTriggerByUser(Listener<XmlPullParser> listener, ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "getTriggerDetailListByUser", listener,
				errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("secToken", CacheUtils.getUser().getSecurityToken());
				map.put("userId", CacheUtils.getUser().getUserId());
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

	@Override
	public void getSceneCode(final User user, final String type, Listener<XmlPullParser> listener,
			ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.SERVER_ADDRESS_API + "requestCode", listener,
				errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("name", user.getUsername());
				map.put("password", user.getPassword());
				map.put("type", type);
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

	@Override
	public void removeTrigger(final String triggerId, Listener<XmlPullParser> listener, ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "removeTrigger", listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("secToken", CacheUtils.getUser().getSecurityToken());
				map.put("triggerId", triggerId);
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);

	}

	@Override
	public void recycleCode(final String code, Listener<XmlPullParser> listener, ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.SERVER_ADDRESS_API + "recyclingCode", listener,
				errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("code", code);
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

	@Override
	public void getDeviceAttribute(final String devId, final String attributeName, Listener<XmlPullParser> listener,
			ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "getDeviceAttribute", listener,
				errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("devId", devId);
				map.put("name", attributeName);
				map.put("secToken", CacheUtils.getUser().getSecurityToken());
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

	@Override
	public void getTriggerDetailListByDevice(final String devId, Listener<XmlPullParser> listener,
			ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "getTriggerDetailListByDevice", listener,
				errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("secToken", CacheUtils.getUser().getSecurityToken());
				map.put("devId", devId);
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

	@Override
	public void setMultiDeviceAttributes2(final String devId, final List<DeviceAttribute> deviceAttributes,
			Listener<XmlPullParser> listener, ErrorListener errorListener) {
		XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.getUrl() + "setMultiDeviceAttributes2", listener,
				errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("secToken", CacheUtils.getUser().getSecurityToken());
				map.put("devId", devId);
				int size = deviceAttributes.size();
				for (int i = 0; i < size; i++) {
					DeviceAttribute attribute = deviceAttributes.get(i);
					map.put("name" + (i + 1), attribute.getName());
					map.put("value" + (i + 1), attribute.getValue());
				}
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

}
