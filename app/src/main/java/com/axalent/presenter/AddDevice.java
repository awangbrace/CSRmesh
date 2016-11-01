/**
 * File Name                   : Add Device������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/08/20
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.presenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import android.content.Context;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.axalent.application.MyApplication;
import com.axalent.presenter.controller.DeviceManager;
import com.axalent.presenter.controller.impl.UserManagerImpl;
import com.axalent.model.Device;
import com.axalent.model.DeviceAttribute;
import com.axalent.model.HintError;
import com.axalent.model.User;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;
import com.axalent.util.LogUtils;
import com.axalent.util.XmlUtils;

public class AddDevice {
	
	private int max = 10;
	private int count = 0;
	private Device gateway;
	private Context context;
	private Device currentDevice;
	private String tempChildInfo = "";
	private DeviceManager deviceManager;
	private OnAddDeviceListener onAddDeviceListener = null;
	
	public AddDevice(DeviceManager deviceManager) {
		this.deviceManager = deviceManager;
		this.context = MyApplication.getInstance().getApplicationContext();
	}
	
	public void setOnAddDeviceListener(OnAddDeviceListener onAddDeviceListener) {
		this.onAddDeviceListener = onAddDeviceListener;
	}
	
	public void addGatewayDevice(Device gateway, String key, String val) {
		this.gateway = gateway;
		examineChildInfoIsOK(key, val);
	}
	
	public void addDevice(Device device) {
		currentDevice = device;
		getDeviceAuth();
	}
	
	public void addSearchDevice(Device gateway, String uuid) {
		addGatewayDevice(gateway, AxalentUtils.ATTRIBUTE_PERMITJOIN, uuid);
	}
	
	public void addScanDevice(String scanResult) {
		// �����JSON��ʽ
		if (scanResult.contains("[") || scanResult.contains("{")) {
			try {
				JSONObject object = new JSONObject(scanResult);
//				int operation = object.optInt("operation");
				String code = object.optString("code");
				String pwd = object.optString("pwd");
				String type = object.optString("type");
				
				currentDevice = new Device();
				currentDevice.setTypeId("");
				currentDevice.setPassword(pwd);
				currentDevice.setDevName(code);
				currentDevice.setTypeName(type);
				currentDevice.setState(AxalentUtils.OFF);
				currentDevice.setDisplayName(AxalentUtils.getDisplayName(type));
				
			} catch (Exception e) {
				onAddDeviceListener.onAddDevice(null, new HintError("", "init scan device error"));
			}
			getDeviceAuth();
		} else if (scanResult.length() == 20) {
			// �����������ͷ
			
//			int cameraId = getCameraId();
			
			currentDevice = new Device();
			currentDevice.setUid(scanResult);
			currentDevice.setPassword("");
			currentDevice.setTypeName(AxalentUtils.TYPE_CAMERA);
			currentDevice.setDisplayName("Camera");
			currentDevice.setTypeId("");
			currentDevice.setState(AxalentUtils.ON);
			currentDevice.setAttributes(new ArrayList<DeviceAttribute>());
			
			StringBuilder sb = new StringBuilder();
			
			String cameraInfo = CacheUtils.getUserAttribute(AxalentUtils.ATTRIBUTE_CAMERA_INFO);
			
			if ("".equals(cameraInfo)) {
				// ����鵽û�� camera ��Ϣ��ֱ���ϴ���
				currentDevice.setDevName("camera" + 0);
				currentDevice.setDevId(currentDevice.getDevName());
				sb.append("[{\"cameraId\":\"" + 0 + "\",\"uid\":\"" + currentDevice.getUid()+ "\",\"password\":\"\"}]");
				cameraInfo = sb.toString();
			} else {
				// ������Ҫ������ݲ��뵽���������
				try {
					boolean isAdd = false; 
					JSONArray array = new JSONArray(cameraInfo);
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = array.getJSONObject(i);
						String uid = object.optString("uid");
						if (currentDevice.getUid().equals(uid)) {
							isAdd = true;
							break;
						}
					}
					
					if (isAdd) {
						onAddDeviceListener.onAddDevice(null, new HintError("809", "camera_by_add"));
						return;
					}
					currentDevice.setDevName("camera"+array.length());
					currentDevice.setDevId(currentDevice.getDevName());
					sb.append("{\"cameraId\":\"" + array.length() + "\",\"uid\":\"" + currentDevice.getUid()+ "\",\"password\":\"\"}");
					
					JSONObject object = new JSONObject(sb.toString());
					array.put(object);
					cameraInfo = array.toString();
				} catch (JSONException e) {
					cameraInfo = "";
				}
			}
			
			if (!TextUtils.isEmpty(cameraInfo)) {
				setUserAttribute(AxalentUtils.ATTRIBUTE_CAMERA_INFO, cameraInfo);
			} else {
				onAddDeviceListener.onAddDevice(null, new HintError("", "add camera error"));
			}
		} else {
			onAddDeviceListener.onAddDevice(null, new HintError("992", "result format error"));
		}
	}
	
	private void setUserAttribute(final String name, final String value) {
		new UserManagerImpl().setUserAttribute(name, value, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				String reqCode = XmlUtils.converRequestCode(response);
				if (AxalentUtils.REQUEST_OK.equals(reqCode)) {
					CacheUtils.setUserAttribute(name, value);
					CacheUtils.saveDevice(currentDevice);
					onAddDeviceListener.onAddDevice(currentDevice, new HintError("", "add camera ok"));
				} else {
					onAddDeviceListener.onAddDevice(null, new HintError("", "add camera error"));
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				onAddDeviceListener.onAddDevice(null, new HintError("", "add camera error"));
			}
		});
	}
	
//	private int getCameraId() {
//		return CacheUtils.getDeviceTypeCount(AxalentUtils.TYPE_CAMERA);
//	}
	
	
	public void addScene() {
		getSceneCode();
	}
	
	private void getSceneCode() {
		if (CacheUtils.getUser().getAppId().equals("2186")) {
			Device gateway = AxalentUtils.getGateway();
			if (gateway != null) {

				String name = gateway.getDevName().substring(0,  gateway.getDevName().length() - 2);
				int index = PreferenceManager.getDefaultSharedPreferences(context)
						.getInt("indexs", 32);

				currentDevice = new Device();
				currentDevice.setDevName(name + index);

				LogUtils.i("scene name:" + currentDevice.getDevName());

				currentDevice.setTypeName(AxalentUtils.TYPE_AXALENT_SCENE);
				currentDevice.setPassword("XX");
				currentDevice.setTypeId("393");
			} else {
				onAddDeviceListener.onAddDevice(null, new HintError("", "code_null"));
			}
		} else {
			currentDevice = AxalentUtils.getSceneInfo(context);
		}
		if (!TextUtils.isEmpty(currentDevice.getDevName()) && !TextUtils.isEmpty(currentDevice.getPassword())) {
			getDeviceAuth();
		} else {
			deviceManager.getSceneCode(new User("jason", "1234"), "scene", new Listener<XmlPullParser>() {
				@Override
				public void onResponse(XmlPullParser response) {
					currentDevice = XmlUtils.converSceneCode(response);
					if (!TextUtils.isEmpty(currentDevice.getDevName())) {
						// ��ֹ������code
						AxalentUtils.saveSceneInfo(context, currentDevice);
						initScene();
						getDeviceAuth();
					} else {
						onAddDeviceListener.onAddDevice(null, new HintError("", "code_null"));
					}
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					onAddDeviceListener.onAddDevice(null, new HintError("", "request_error"));
				}
			});
		}
	}
	
	private void initScene() {
		currentDevice.setTypeId("");
		currentDevice.setPassword(currentDevice.getPassword());
		currentDevice.setTypeName(AxalentUtils.TYPE_AXALENT_SCENE);
		currentDevice.setDisplayName(AxalentUtils.getDisplayName(currentDevice.getTypeName()));
	}
	
	private void examineChildInfoIsOK(final String key, final String val) {
		deviceManager.getDeviceAttribute(gateway.getDevId(), AxalentUtils.ATTRIBUTE_CHILDINFO, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				DeviceAttribute deviceAttribute = XmlUtils.converDeviceAttribute(response);
				String value = deviceAttribute.getValue();
				if (!TextUtils.isEmpty(value) && value.contains("00")) {
					tempChildInfo = value;
					sendAddInfoToGateway(key, val);
				} else {
					onAddDeviceListener.onAddDevice(null, new HintError("", "chidInfo_error"));
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				onAddDeviceListener.onAddDevice(null, new HintError("", "request_error"));
			}
		});
	}
	
	private void sendAddInfoToGateway(final String key, final String value) {
		deviceManager.setDeviceAttribute(gateway.getDevId(), key, value, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				LogUtils.i("sendPermitjoinToGateway:"+gateway.getDevName());
				new Thread() {
					public void run() {
						while (count < max) {
							count ++;
							getChildInfo();
							SystemClock.sleep(1000);
						}
						if (AxalentUtils.STOP != count) {
							onAddDeviceListener.onAddDevice(null, new HintError(AxalentUtils.TIME_OUT, "add_time_out"));
						}
					};
				}.start();
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				onAddDeviceListener.onAddDevice(null, new HintError("", "request_error"));
			}
		});
	}
	
	private void getChildInfo() {
		deviceManager.getDeviceAttribute(gateway.getDevId(), AxalentUtils.ATTRIBUTE_CHILDINFO, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				DeviceAttribute deviceAttribute = XmlUtils.converDeviceAttribute(response);
				String childInfo = deviceAttribute.getValue();
				if (!tempChildInfo.equals(childInfo)) {
					count = AxalentUtils.STOP;
					getDeviceName(childInfo);
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				count = AxalentUtils.STOP;
				onAddDeviceListener.onAddDevice(null, new HintError("", "request_error"));
			}
		});
	}
	
	private void getDeviceName(String childInfo) {
		Map<String, Object> maps = AxalentUtils.getChangeValueAndIndex(tempChildInfo, childInfo);
		Integer index = (Integer) maps.get("index");
		String changeVal = (String) maps.get("changeVal");
		String gatewayName = gateway.getDevName();
		if (index != null && gatewayName != null) {
			String devName = AxalentUtils.getDevName(index, gatewayName);
			currentDevice = new Device();
			currentDevice.setTypeId("");
			currentDevice.setPassword("XX");
			currentDevice.setDevName(devName);
			currentDevice.setState(AxalentUtils.OFF);
			currentDevice.setTypeName(AxalentUtils.getTypeName(changeVal));
			currentDevice.setDisplayName(AxalentUtils.getDisplayName(currentDevice.getTypeName()));
			getDeviceAuth();
		} else {
			onAddDeviceListener.onAddDevice(null, new HintError("", "getDevName_error"));
		}
	}
	
	private void getDeviceAuth() {
		deviceManager.getDeviceAuth(currentDevice, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				LogUtils.i("getDeviceAuth success");
				String deviceId = XmlUtils.converDeviceAuth(response);
				if (!TextUtils.isEmpty(deviceId)) {
					currentDevice.setDevId(deviceId);
					addToUser();
				} else {
					onAddDeviceListener.onAddDevice(null, new HintError("", "getDevIde_error"));
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
//				HintError hintError = XmlUtils.converErrorMsg(error);
				onAddDeviceListener.onAddDevice(null, new HintError("", "getDevIde_error"));
			}
		});
	}
	
	private void addToUser() {
		deviceManager.addDeviceToUser(currentDevice, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				LogUtils.i("addToUser success");
				String reqCode = XmlUtils.converRequestCode(response);
				if (AxalentUtils.REQUEST_OK.equals(reqCode)) {
					CacheUtils.saveDevice(currentDevice);
					String typeName = currentDevice.getTypeName();
					if (AxalentUtils.TYPE_AXALENT_SCENE.equalsIgnoreCase(typeName)) {
						AxalentUtils.removeSceneInfo(context);
					} else if (AxalentUtils.TYPE_GATEWAY.equalsIgnoreCase(typeName)) {
						// ����� gateway ��ӳɹ�֮����Ҫˢ�� childInfo
						refreshChildInfo();
					}
					onAddDeviceListener.onAddDevice(currentDevice, new HintError("", "add_ok"));
				} else {
					onAddDeviceListener.onAddDevice(null, new HintError("", "add_error"));
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				HintError hintError = XmlUtils.converErrorMsg(error);
				if (AxalentUtils.TYPE_AXALENT_SCENE.equalsIgnoreCase(currentDevice.getTypeName())) {
					String errorCode = hintError.getErrorCode();
					if ("346".equals(errorCode) || "110".equals(errorCode)) {
						// �Ѿ�����ӹ��ˣ���������豸�Ѿ��������û���
						AxalentUtils.removeSceneInfo(context);
					}
				}
				onAddDeviceListener.onAddDevice(null, hintError);
			}
		});
	}

	private void refreshChildInfo() {
		deviceManager.setDeviceAttribute(currentDevice.getDevId(), AxalentUtils.ATTRIBUTE_REFRESH, "1", new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
			}
		});
	}
	
	public interface OnAddDeviceListener {
		void onAddDevice(Device device, HintError error);
	}
	
	/**
	 * ���ó�ʱʱ��
	 * @param time
	 */
	public void setTimeOut(long time) {
		max = (int) (time / 1000);
	}
	
	 

}
