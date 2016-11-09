/**
 * File Name                   : Axalent ������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.axalent.R;
import com.axalent.application.MyApplication;
import com.axalent.application.MyCacheData;
import com.axalent.model.Device;
import com.axalent.model.DeviceAttribute;
import com.axalent.model.DeviceType;
import com.axalent.model.Trigger;
import com.axalent.model.TriggerAttribute;
import com.axalent.model.data.model.Time;
import com.axalent.model.data.model.devices.CSRDevice;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class AxalentUtils {

//	 public static final String SERVER_ADDRESS_REMOTELY ="https://devkit-api.arrayent.com:8081/zdk/services/zamapi/";
	public static final String SERVER_ADDRESS_REMOTELY = "https://apac-axlprod01-api.arrayent.com:8081/zdk/services/zamapi/";
	public static final String SERVER_ADDRESS_LOCAL = "http://192.168.11.100:8087/zdk/services/zamapi/";
	public static final String SERVER_ADDRESS_API = "http://www.axalent.com:8081/api/";
	public static final String ERROR_FILE_NAME = "axalent_error_msg_chinese.xml";
	public static final String DEVICE_TYPE_FILE_NAME = "axalent_device_types.xml";
	public static final String DEVICE_TYPE_FILE_NAME2 = "axalent_device_types_devkit.xml";
	public static final String USER_FILE_NAME = "axalent_user_info";
	public static final String DEVICE_FILE_NAME = "axalent_device_info";
	public static final String LOGIN_RESET_CODE = "106";
	public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm";
	public static final String DEFAULT_FORMAT_SS = "yyyy-MM-dd HH:mm:ss";

	// ����
	public static final String MATCHES_EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
	// ����
	public static final String MATCHES_PASSWORD = "^[A-Za-z0-9]+$";
	//
	public static final String MATCHES_NUMBER = "^(-?[1-9]\\d*\\.?\\d*)|(-?0\\.\\d*[1-9])|(-?[0])|(-?[0]\\.\\d*)$";
	// IP ��ַ
	public static final String IP_ITEM = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";
	public static final String MATCHES_IP = "^" + IP_ITEM + "\\." + IP_ITEM + "\\." + IP_ITEM + "\\." + IP_ITEM + "$";

	// public static final String PASS_CHECK = NAME_CHECK;
	public static final int REGISTER_SUCCESS = 0x954540;
	public static final int RESTORE_SUCCESS = 0x954541;

	public static final int CONFIGURATION_IP_ADDRESS_SUCCESS = 0x432423;
	public static final int EXIT = 0x90;
	public static final String TIME_OUT = "9088434";
	public static final String REQUEST_OK = "0";
	public static final int DISPOSE_RESULT = 0x67;
	// public static final int CLEAR_ALL_DATAS = 0x68;
	public static final int SHOW_MESSAGE = 0x69;
	public static final int CLOSE_DIALOG = 0x70;
	/**
	 * û������
	 */
	public static final int DETECTION_NOT_ATTRIBUTE = 0x90;
	/**
	 * û��Trigger
	 */
	public static final int DETECTION_NOT_TRIGGER = 0x91;
	/**
	 * û��C1��C2����û�����
	 */
	public static final int DETECTION_NOT_ADD = 0x92;

	// 管理员账号
	public static final String SYSTEMACCOUNT = "elexaconsumerproductsAdmin";
	public static final String SYSTEMPASSWORD = "r1bgmhE0Ep";

	// compare date
	public static final int SAME_TIME = 0;
	public static final int GREATER_TIME = 1;
	public static final int LESS_TIME = -1;

	/**
	 * ������״̬
	 */
	public static final String ON = "1";
	public static final String OFF = "0";
	public static final String ONLINE = "1";
	public static final String OFFLINE = "0";

	public static final int ADAPTER_SHOW_SWITCH = 0x10;
	public static final int ADAPTER_SHOW_SEECKBAR = 0x11;

	public static final int STOP = 9999999;
	public static final int LOAD_DATA_TIME_OUT = 0x98454;
	public static final int LOAD_DATA = 0x9843454;
	public static final String ADD_SCENE_VALUE = "1";
	public static final String ADD_DEVICE_VALUE = "1";
	public static final String ADD_SCHEDULE_VALUE = "1";

	public static final String TYPE_SCALE = "scale";
	public static final String TYPE_KEYFOB = "keyfob";
	public static final String TYPE_POWER_PLUG = "computime_powerplug";
	public static final String TYPE_SL = "SL";
	public static final String TYPE_SWITCH_TWO = "axa_switch_binary_2";
	public static final String TYPE_GATEWAY_SCHEDULE = "axa_gateway_schedule";
	public static final String TYPE_AXALENT_SCENE = "axa_scene";
	public static final String TYPE_GATEWAY_SCENE = "axa_gateway_scene";
	public static final String TYPE_SWITCH_THREE = "axa_switch_binary_3";
	public static final String TYPE_WINDOW_COVER = "axa_window_cover";
	public static final String TYPE_GATEWAY = "gateway";
	public static final String TYPE_BPM = "BPM";
	public static final String TYPE_SM = "SM";
	public static final String TYPE_HTM = "HTM";
	public static final String TYPE_HTS = "HTS";
	public static final String TYPE_BEACON = "beacon";
	public static final String TYPE_SSMOKE = "SSmoke";
	public static final String TYPE_LIGHTSENSOR = "LightSensor";
	public static final String TYPE_SC = "SC";
	public static final String TYPE_REMOTECONTROL = "RemoteControl";
	public static final String TYPE_FLOOD_SENSOR = "axa_flood_sensor";
	public static final String TYPE_MOUSE_TRAP = "axa_mouse_trap";
	public static final String TYPE_VALVE = "axa_valve";
	public static final String TYPE_CAMERA = "axa_camera";
	public static final String TYPE_GAS_SENSOR = "axa_gas_sensor";
	public static final String TYPE_MTM_EPAD = "mtm_epad";
	public static final String TYPE_EBIO_EPAD = "eBio_epad";
	public static final String TYPE_MTM_SM = "mtm_motion_sensor";
	public static final String TYPE_SIREN = "axa_siren";
	public static final String TYPE_GUNI_LAMP = "guni_lamp";
	public static final String TYPE_GUNI_PLUG = "guni_plug";
	public static final String TYPE_SENSOR_PIR = "guni_sensor_PIR";
	public static final String TYPE_SENSOR_LIGHT = "guni_sensor_light";
	public static final String TYPE_GUNI_SWITCH = "guni_switch";
	public static final String TYPE_SHCK = "SHCK";

	public static final String ATTRIBUTE_DATABASE = "DataBase";

	public static final int SWITCH_GATEWAY_WIFI = 0x31;
	public static final int SAVE_GATEWAY_IP = 0x32;
	
	
	/**
	 * �������
	 */
	public static final String ATTRIBUTE_ALARM = "alarm";
	public static final String ATTRIBUTE_TRAPPED = "trapped";
	public static final String ATTRIBUTE_ROLL = "roll";
	public static final String ATTRIBUTE_REFRESH = "refresh";
	public static final String ATTRIBUTE_MYSWITCH = "switch";
	public static final String ATTRIBUTE_STATUS = "status";
	public static final String ATTRIBUTE_ENERGY = "energy";// ����
	public static final String ATTRIBUTE_CURRENT = "current";// ����
	public static final String ATTRIBUTE_VOLTAGE = "voltage";// ��ѹ
	public static final String ATTRIBUTE_FLOOD = "flood";
	public static final String ATTRIBUTE_SWITCH_0 = "switch_0";
	public static final String ATTRIBUTE_SWITCH_1 = "switch_1";
	public static final String ATTRIBUTE_COVER = "cover";
	public static final String ATTRIBUTE_CYCLE = "cycle";
	public static final String ATTRIBUTE_ENABLE = "enable";
	public static final String ATTRIBUTE_CODE = "code";
	public static final String ATTRIBUTE_DATETIME = "datetime";
	public static final String ATTRIBUTE_HSV = "hsv";
	public static final String ATTRIBUTE_LUX = "lux";
	public static final String ATTRIBUTE = "attribute";
	public static final String ATTRIBUTE_LIGHT = "light";
	public static final String ATTRIBUTE_POWER = "power";// ����
	public static final String ATTRIBUTE_SMOKE = "smoke";
	public static final String ATTRIBUTE_DIMMER = "dimmer";
	public static final String ATTRIBUTE_MOTION = "motion";
	public static final String ATTRIBUTE_CUSTOM_NAME = "custom_name";
	public static final String ATTRIBUTE_NETWORKKEY = "networkKey";
	public static final String ATTRIBUTE_CHILDINFO = "childInfo";
	public static final String ATTRIBUTE_ACTIVATE = "activate";
	public static final String ATTRIBUTE_DEVINFO = "devInfo";
	public static final String ATTRIBUTE_ADD_SCENE = "addScene";
	public static final String ATTRIBUTE_PERMITJOIN = "permitjoin";
	public static final String ATTRIBUTE_ADD_SCHEDULE = "addschedule";
	public static final String ATTRIBUTE_DELETEDEVICE = "deletedevice";
	public static final String ATTRIBUTE_UUID = "uuid";
	public static final String ATTRIBUTE_SCENE = "scene";
	public static final String ATTRIBUTE_CONTACT = "contact";
	public static final String ATTRIBUTE_HUMIDITY = "humidity";
	public static final String ATTRIBUTE_DELDEVICE = "delDevice";
	public static final String ATTRIBUTE_ADD_DEVICE = "addDevice";
	public static final String ATTRIBUTE_SCAN_DEVICE = "scandevice";
	public static final String ATTRIBUTE_TEMPERATURE = "temperature";
	public static final String ATTRIBUTE_LOWTHRESHOLD = "lowThreshold";
	public static final String ATTRIBUTE_HIGHTHRESHOLD = "highThreshold";
	public static final String ATTRIBUTE_CO2 = "CO2";
	public static final String ATTRIBUTE_VOC = "VOC";
	public static final String ATTRIBUTE_CAMERA_INFO = "cameraInfo";
	public static final String ATTRIBUTE_GU_WARM = "gu_warm";
	public static final String ATTRIBUTE_GU_RGB = "gu_rgb";
	public static final String ATTRIBUTE_GU_SWITCH = "gu_switch";
	public static final String ATTRIBUTE_GU_LIGHT = "gu_light";
	public static final String ATTRIBUTE_GU_DIMMER = "gu_dimmer";
	public static final String ATTRIBUTE_GU_POWER = "gu_power";
	public static final String ATTRIBUTE_GU_ONTIME = "gu_ontime";
	public static final String ATTRIBUTE_GU_ID = "gu_id";
	public static final String ATTRIBUTE_GU_BIND = "gu_bind";
	public static final String ATTRIBUTE_VALVE = "valve";
	public static final String ATTRIBUTE_SHOCK = "shock";

	public static final String ATTRIBUTE_SYNCDB = "syncdb";
	
	/**
	 * �豸�� ChildiInfo ��ֵ
	 */
	public static final String CHILDINFO_SL = "82";
	public static final String CHILDINFO_SM = "84";
	public static final String CHILDINFO_SC = "86";
	public static final String CHILDINFO_HTM = "87";
	public static final String CHILDINFO_SSMOKE = "85";
	public static final String CHILDINFO_POWERPLUG = "83";
	public static final String CHILDINFO_GAS_SENSOR= "96";
	public static final String CHILDINFO_SWITCH_BINARY_3 = "8D";
	public static final String CHILDINFO_SWITCH_BINARY_2 = "91";
	public static final String CHILDINFO_FLOOD_SENSOR = "92";
	public static final String CHILDINFO_WINDOW_COVER = "90";
	public static final String CHILDINFO_VIRTUAL_SCENE = "C1";
	public static final String CHILDINFO_VIRTUAL_SCHEDULE = "C2";
	public static final String CHILDINFO_SCALE = "8F";
	public static final String CHILDINFO_REMOTECONTROL = "8E";
	public static final String CHILDINFO_LIGHTSENSOR = "8C";
	public static final String CHILDINFO_KEYFOB = "8B";
	public static final String CHILDINFO_BEACON = "8A";
	public static final String CHILDINFO_BPM = "89";
	public static final String CHILDINFO_HTS = "88";
	public static final String CHILDINFO_MOUSE_TRAP = "93";
	public static final String CHILDINFO_VALUE = "94";
	public static final String CHILDINFO_EBIO_EPAD= "97";
	public static final String CHILDINFO_MTM_SM = "98";
	public static final String CHILDINFO_SIREN = "95";
	public static final String CHILDINFO_GUNI_LAMP = "99";
	public static final String CHILDINFO_GUNI_PLUG = "9A";
	public static final String CHILDINFO_GUNI_SWITCH = "9B";
	public static final String CHILDINFO_SENSOR_PIR = "9C";
	public static final String CHILDINFO_SENSOR_LIGHT = "9D";
	public static final String CHILDINFO_SHCK = "9E";

	public static final String KEY_SKIP = "skip";
	public static final String KEY_USER = "user";
	public static final String KEY_DEVICE = "device";
	public static final String KEY_ID_DATABASE_DEVICE = "id";
	public static final String KEY_ID_DATABASE_AREA = "areaId";
	public static final String KEY_DEVICES = "devices";
	public static final String KEY_TRIGGER = "trigger";
	public static final String KEY_TRIGGERS = "triggers";

	public static final int ADD_SCENE = 0x1;
	public static final int UPDATE_SCENE = 0x2;
	public static final int UPDATE_DEVICE = 0x3;
	public static final int DELETE_DEVICE = 0x4;
	public static final int ADD_DEVICE = 0x5;
	public static final int ADD_DETECTION_DEVICE = 0x7;
	public static final int ADD_GATEWAY_DEVICE = 0x6;
	public static final int SHOW_SCENE_DEVICE = 0x8;
	public static final int ADD_SCAN_DEVICE = 0x9;
	public static final int ADD_SCHEDULE = 0x10;
	public static final int SHOW_SCHEDULE_DEVICE = 0x11;
	public static final int UPDATE_SCHEDULE = 0x12;
	public static final int SET_ATTRIBUTE = 0x13;
	public static final int ADD_DEVICE_TRIGGER = 0x14;
	public static final int SHOW_DEVICE_TRIGGER = 0x15;
	public static final int ADD_TRIGGER = 0x16;
	public static final int UPDATE_TRIGGER = 0x17;
	public static final int REGISTER_USER = 0x18;
	public static final int ALTER_PASSWORD = 0x19;
	public static final int ACTIVATE_USER = 0x20;
	public static final int FORGET_PASSWORD = 0x21;
	public static final int SEND_RECOVERY_CODE_TO_USER_EMAIL = 0x22;
	public static final int SHOW_DEVICE = 0x23;
	public static final int SET_AVATAR = 0x24;
	public static final int REFRESH_DATA = 0x29;
	public static final int ADD_GUNI_TRIGGER = 0x30;
	public static final int ADD_GROUP = 0x31;
	public static final int ADD_ACCOUNT = 0x32;
	public static final int EXIT_ACCOUNT = 0x35;
	public static final int ACCOUNT_FINISH = 0x9001;

	public static final String GROUP_OR_SING = "group_or_sing";
	public static final int GROUP = 0x32;
	public static final int SING = 0x33;

	// brodcase
	public static final String CAST_FIFTER = "com.axalent.csr";
	public static final int UPDATE_PAGE = 1;


	public static Device copyDevice(Device obj, Device device) {
		if (obj != null) {
			device.setUid(obj.getUid());
			device.setState(obj.getState());
			device.setDevId(obj.getDevId());
			device.setTypeId(obj.getTypeId());
			device.setUserId(obj.getUserId());
			device.setToggle(obj.getToggle());
			device.setDevName(obj.getDevName());
			device.setTypeName(obj.getTypeName());
			device.setPassword(obj.getPassword());
			device.setAttributes(obj.getAttributes());
			device.setCustomName(obj.getCustomName());
			device.setDisplayName(obj.getDisplayName());
		}
		return device;
	}

	public static Trigger copyTrigger(Trigger obj, Trigger trigger) {
		if (obj != null) {
			trigger.setAction(obj.getAction());
			trigger.setAddress(obj.getAddress());
			trigger.setAttrName(obj.getAttrName());
			trigger.setAutoDelete(obj.getAutoDelete());
			trigger.setAutoDisable(obj.getAutoDisable());
			trigger.setAutoDisarm(obj.getAutoDisarm());
			trigger.setDeviceId(obj.getDeviceId());
			trigger.setDisarmed(obj.getDisarmed());
			trigger.setEnable(obj.getEnable());
			trigger.setMessage(obj.getMessage());
			trigger.setOperation(obj.getOperation());
			trigger.setStringThreshold(obj.getStringThreshold());
			trigger.setThreshold(obj.getThreshold());
			trigger.setTriggerAttributes(obj.getTriggerAttributes());
			trigger.setTriggerId(obj.getTriggerId());
		}
		return trigger;
	}

	public static List<TriggerAttribute> copyTriggerAttribute(Trigger obj) {
		List<TriggerAttribute> copyList = new ArrayList<TriggerAttribute>();
		if (obj != null) {
			List<TriggerAttribute> triggerAttributes = obj.getTriggerAttributes();
			if (triggerAttributes != null) {
				for (TriggerAttribute triggerAttribute : triggerAttributes) {
					copyList.add(
							new TriggerAttribute(triggerAttribute.getDeviceTriggerID(), triggerAttribute.getDeviceID(),
									triggerAttribute.getAttributeName(), triggerAttribute.getValue()));
				}
			}
		}
		return copyList;
	}

	public static String getDeviceAttributeValue(List<DeviceAttribute> deviceAttributes, String key) {
		if (deviceAttributes != null) {
			for (DeviceAttribute deviceAttribute : deviceAttributes) {
				if (key.equals(deviceAttribute.getName())) {
					String value = deviceAttribute.getValue();
					return value == null ? "" : value;
				}
			}
		}
		return "";
	}

	public static String getDeviceAttributeValue(Device device, String key) {
		if (device != null) {
			List<DeviceAttribute> deviceAttributes = device.getAttributes();
			if (deviceAttributes != null) {
				for (DeviceAttribute deviceAttribute : deviceAttributes) {
					if (key.equals(deviceAttribute.getName())) {
						String value = deviceAttribute.getValue();
						return value == null ? "" : value;
					}
				}
			}
		}
		return "";
	}

	public static boolean setDeviceAttributeValue(Device device, String key, String val) {
		if (device != null) {
			List<DeviceAttribute> deviceAttributes = device.getAttributes();
			if (deviceAttributes != null) {
				for (DeviceAttribute deviceAttribute : deviceAttributes) {
					if (deviceAttribute.getName().equals(key)) {
						deviceAttribute.setValue(val);
						return true;
					}
				}
			} else {
				deviceAttributes = new ArrayList<DeviceAttribute>();
				deviceAttributes.add(new DeviceAttribute(key, val));
				device.setAttributes(deviceAttributes);
				return true;
			}
		}
		return false;
	}

	public static boolean setDeviceAttributeValues(Device device, String[] keys, String[] vals) {
		if (device != null) {
			List<DeviceAttribute> deviceAttributes = device.getAttributes();
			if (deviceAttributes != null) {
				for (DeviceAttribute deviceAttribute : deviceAttributes) {
					for (int i = 0; i < keys.length; i++) {
						if (deviceAttribute.getName().equals(keys[i])) {
							deviceAttribute.setValue(vals[i]);
							return true;
						}
					}
				}
			} else {
				deviceAttributes = new ArrayList<DeviceAttribute>();
				for (int i = 0; i < keys.length; i++) {
					deviceAttributes.add(new DeviceAttribute(keys[i], vals[i]));
				}
				device.setAttributes(deviceAttributes);
				return true;
			}
		}
		return false;
	}

	public static Map<String, String> getDeviceAttributeValues(Device device, String[] keys) {
		Map<String, String> map = new HashMap<String, String>();
		if (device != null) {
			List<DeviceAttribute> deviceAttributes = device.getAttributes();
			if (deviceAttributes != null) {
				for (DeviceAttribute deviceAttribute : deviceAttributes) {
					String name = deviceAttribute.getName();
					for (int i = 0; i < keys.length; i++) {
						if (name.equals(keys[i])) {
							map.put(name, deviceAttribute.getValue());
						}
					}
				}
			}
		}
		return map;
	}
	
	public static String[] getDeviceAttributeValues2(Device device, String[] keys) {
		String[] values = new String[keys.length];
		if (device != null) {
			int count = 0;
			List<DeviceAttribute> deviceAttributes = device.getAttributes();
			if (deviceAttributes != null) {
				for (DeviceAttribute deviceAttribute : deviceAttributes) {
					String name = deviceAttribute.getName();
					for (int i = 0; i < keys.length; i++) {
						if (name.equals(keys[i])) {
							count++;
							String value = deviceAttribute.getValue();
							values[i] = value == null ? "" : value;
							if (count == keys.length) return values;
						}
					}
				}
			}
		}
		return values;
	}
	
	

	public static boolean updateDeviceAttibuteByDevId(Device device, String name, String value) {
		if (device != null) {
			List<DeviceAttribute> deviceAttributes = device.getAttributes();
			if (deviceAttributes != null) {
				for (DeviceAttribute deviceAttribute : deviceAttributes) {
					if (deviceAttribute.getName().equals(name)) {
						deviceAttribute.setValue(value);
						return true;
					}
				}
			}
		}
		return false;
	}

	public static int getDeviceImageByTypeName(String typeName) {
		if (TYPE_AXALENT_SCENE.equalsIgnoreCase(typeName)) {
			return R.drawable.scene_user_define;
		} else if (TYPE_GATEWAY_SCHEDULE.equalsIgnoreCase(typeName)) {
			return R.drawable.schedule;
		} else if (TYPE_SL.equalsIgnoreCase(typeName) || TYPE_GUNI_LAMP.equalsIgnoreCase(typeName)) {
			return R.drawable.light_off;
		} else if (TYPE_POWER_PLUG.equalsIgnoreCase(typeName) || TYPE_GUNI_PLUG.equalsIgnoreCase(typeName)) {
			return R.drawable.plug_off;
		} else if (TYPE_SWITCH_TWO.equalsIgnoreCase(typeName)) {
			return R.drawable.switch_two_off;
		} else if (TYPE_WINDOW_COVER.equalsIgnoreCase(typeName)) {
			return R.drawable.shade_open;
		} else if (TYPE_GATEWAY.equalsIgnoreCase(typeName)) {
			return R.drawable.gateway;
		} else if (TYPE_BPM.equalsIgnoreCase(typeName)) {
			return R.drawable.bloodmonitor;
		} else if (TYPE_SM.equalsIgnoreCase(typeName)) {
			return R.drawable.motion_off;
		} else if (TYPE_HTM.equalsIgnoreCase(typeName)) {
			return R.drawable.htm;
		} else if (TYPE_HTS.equalsIgnoreCase(typeName)) {
			return R.drawable.hts;
		} else if (TYPE_BEACON.equalsIgnoreCase(typeName)) {
			return R.drawable.beacon;
		} else if (TYPE_SSMOKE.equalsIgnoreCase(typeName)) {
			return R.drawable.smoke_off;
		} else if (TYPE_LIGHTSENSOR.equalsIgnoreCase(typeName)) {
			return R.drawable.light_sensor;
		} else if (TYPE_SC.equalsIgnoreCase(typeName)) {
			return R.drawable.door_closed;
		} else if (TYPE_CAMERA.equalsIgnoreCase(typeName)) {
			return R.drawable.camera;
		} else if (TYPE_GAS_SENSOR.equalsIgnoreCase(typeName)) {
			return R.drawable.gas_sensor;
		} else if (TYPE_EBIO_EPAD.equalsIgnoreCase(typeName)) {
			return R.drawable.pad_off;
		} else if (TYPE_MTM_SM.equalsIgnoreCase(typeName)) {
			return R.drawable.dice1;
		} else if (TYPE_SENSOR_PIR.equalsIgnoreCase(typeName)) {
			return R.drawable.light_sensor;
		} else if (TYPE_SENSOR_LIGHT.equalsIgnoreCase(typeName)) {
			return R.drawable.light_sensor;
		} else if (TYPE_GUNI_SWITCH.equalsIgnoreCase(typeName)) {
			return R.drawable.switch_one_off;
		} else if (TYPE_SHCK.equalsIgnoreCase(typeName)) {
			return R.drawable.shock_sensor_off;
		} else if (TYPE_FLOOD_SENSOR.equalsIgnoreCase(typeName)) {
			return R.drawable.flood_dry;
		} else if (TYPE_VALVE.equalsIgnoreCase(typeName)) {
			return R.drawable.valve_closed;
		} else if (TYPE_MOUSE_TRAP.equalsIgnoreCase(typeName)) {
			return R.drawable.mouser_no_mouse;
		} else if (TYPE_SIREN.equalsIgnoreCase(typeName)) {
			return R.drawable.siren_off;
		}
		return R.drawable.light_sensor;
	}

	public static int getCSRDeviceImageByTypeName(int typeName) {
		switch (typeName) {
			case CSRDevice.TYPE_LIGHT:
				return R.drawable.light_off;
			case CSRDevice.TYPE_TEMPERATURE:
				return R.drawable.scene_user_define;
			case CSRDevice.TYPE_GATEWAY:
				return R.drawable.gateway;
			default:
				return 0;
		}
	}

	public static String getGatewayName(String devName) {
		if (!TextUtils.isEmpty(devName) && devName.length() > 3) {
			return devName.substring(0, devName.length() - 2) + "00";
		}
		return "";
	}

	public static String getChildInfoIndexByDevName(String devName) {
		if (devName != null && !devName.equals("")) {
			int length = devName.length();
			if (length > 3) {
				return devName.substring(length - 2, length);
			}
		}
		return "";
	}

	public static Map<String, Object> getChangeValueAndIndex(String tempChildInfo, String childInfo) {
		Map<String, Object> maps = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tempChildInfo.length(); i++) {
			char temp = tempChildInfo.charAt(i);
			char info = childInfo.charAt(i);
			if (temp != info) {
				sb.append(info);
				if (i <= 1) {
					maps.put("index", 1);
				} else {
					maps.put("index", (i / 2) + 1);
				}
				if (sb.length() == 2) {
					maps.put("changeVal", sb.toString());
					return maps;
				}
			}
		}
		return maps;
	}

	public static String getDevName(int index, String gatewayName) {
		if (index < 10) {
			return gatewayName.substring(0, gatewayName.length() - 1) + index;
		} else {
			return gatewayName.substring(0, gatewayName.length() - 2) + index;
		}
	}

	/**
	 * ����û���ӵ��Ǻ����豸����
	 * @return
	 */
	public static String getTypeName(String childInfoValue) {
		if (!TextUtils.isEmpty(childInfoValue)) {
			if (CHILDINFO_POWERPLUG.equalsIgnoreCase(childInfoValue)) {
				return TYPE_POWER_PLUG;
			} else if (CHILDINFO_SM.equalsIgnoreCase(childInfoValue)) {
//				return TYPE_MTM_SM;
				return TYPE_SM;
			} else if (CHILDINFO_SL.equalsIgnoreCase(childInfoValue)) {
				return TYPE_SL;
			} else if (CHILDINFO_SSMOKE.equalsIgnoreCase(childInfoValue)) {
				return TYPE_SSMOKE;
			} else if (CHILDINFO_SC.equalsIgnoreCase(childInfoValue)) {
				return TYPE_SC;
			} else if (CHILDINFO_HTM.equalsIgnoreCase(childInfoValue)) {
				return TYPE_HTM;
			} else if (CHILDINFO_HTS.equalsIgnoreCase(childInfoValue)) {
				return TYPE_HTS;
			} else if (CHILDINFO_BPM.equalsIgnoreCase(childInfoValue)) {
				return TYPE_BPM;
			} else if (CHILDINFO_BEACON.equalsIgnoreCase(childInfoValue)) {
				return TYPE_BEACON;
			} else if (CHILDINFO_KEYFOB.equalsIgnoreCase(childInfoValue)) {
				return TYPE_KEYFOB;
			} else if (CHILDINFO_LIGHTSENSOR.equalsIgnoreCase(childInfoValue)) {
				return TYPE_LIGHTSENSOR;
			} else if (CHILDINFO_REMOTECONTROL.equalsIgnoreCase(childInfoValue)) {
				return TYPE_REMOTECONTROL;
			} else if (CHILDINFO_SCALE.equalsIgnoreCase(childInfoValue)) {
				return TYPE_SCALE;
			} else if (CHILDINFO_SWITCH_BINARY_3.equalsIgnoreCase(childInfoValue)) {
				return "SW";
			} else if (CHILDINFO_VIRTUAL_SCENE.equalsIgnoreCase(childInfoValue)) {
				return TYPE_GATEWAY_SCENE;
			} else if (CHILDINFO_VIRTUAL_SCHEDULE.equalsIgnoreCase(childInfoValue)) {
				return TYPE_GATEWAY_SCHEDULE;
			} else if (CHILDINFO_WINDOW_COVER.equalsIgnoreCase(childInfoValue)) {
				return TYPE_WINDOW_COVER;
			} else if (CHILDINFO_SWITCH_BINARY_2.equalsIgnoreCase(childInfoValue)) {
				return TYPE_SWITCH_TWO;
			} else if (CHILDINFO_FLOOD_SENSOR.equalsIgnoreCase(childInfoValue)) {
				return TYPE_FLOOD_SENSOR;
			} else if (CHILDINFO_MOUSE_TRAP.equalsIgnoreCase(childInfoValue)) {
				return TYPE_MOUSE_TRAP;
			} else if (CHILDINFO_VALUE.equalsIgnoreCase(childInfoValue)) {
				return TYPE_VALVE;
			} else if (CHILDINFO_GAS_SENSOR.equalsIgnoreCase(childInfoValue)) {
				return TYPE_GAS_SENSOR;
			} else if (CHILDINFO_MTM_SM.equalsIgnoreCase(childInfoValue)) {
				return TYPE_MTM_SM;
			} else if (CHILDINFO_EBIO_EPAD.equalsIgnoreCase(childInfoValue)) {
				return TYPE_EBIO_EPAD;
			} else if (CHILDINFO_SIREN.equalsIgnoreCase(childInfoValue)) {
				return TYPE_SIREN;
			} else if (CHILDINFO_GUNI_LAMP.equalsIgnoreCase(childInfoValue)) {
				return TYPE_GUNI_LAMP;
			} else if (CHILDINFO_GUNI_PLUG.equalsIgnoreCase(childInfoValue)) {
				return TYPE_GUNI_PLUG;
			}  else if (CHILDINFO_GUNI_SWITCH.equalsIgnoreCase(childInfoValue)) {
				return TYPE_GUNI_SWITCH;
			} else if (CHILDINFO_SENSOR_PIR.equalsIgnoreCase(childInfoValue)) {
				return TYPE_SENSOR_PIR;
			} else if (CHILDINFO_SENSOR_LIGHT.equalsIgnoreCase(childInfoValue)) {
				return TYPE_SENSOR_LIGHT;
			} else if (CHILDINFO_SHCK.equalsIgnoreCase(childInfoValue)) {
				return TYPE_SHCK;
			}
		}
		return "";
	}

	public static String getDisplayName(String typeName) {
		List<DeviceType> deviceTypes = CacheUtils.getDeviceTypes();
		for (DeviceType deviceType : deviceTypes) {
			if (typeName.equals(deviceType.getName())) {
				return deviceType.getDisplayName();
			}
		}
		return "null";
	}

	public static String getDeviceInGatewayIndex(String devName) {
		if (!TextUtils.isEmpty(devName)) {
			int length = devName.length();
			if (length > 3) {
				return devName.substring(length - 2, length);
			}
		}
		return "";
	}

	public static Map<String, String> getGatewayNameAndIndex(String deviceName) {
		Map<String, String> maps = new HashMap<String, String>();
		if (!TextUtils.isEmpty(deviceName)) {
			final int length = deviceName.length();
			String index = deviceName.substring(length - 2, length);
			String gatewayName = deviceName.substring(0, length - 2) + "00";
			maps.put("index", index);
			maps.put("gatewayName", gatewayName);
		}
		return maps;
	}

	public static String getLanguage() {
		return MyApplication.getInstance().getApplicationContext().getResources().getConfiguration().locale
				.getLanguage();
	}

	/**
	 * ��õ�ǰ�ֻ������
	 * 
	 * @return
	 */
	public static String getCurrentLanguageValue() {
		Resources resources = MyApplication.getInstance().getResources();
		String languageStr = resources.getConfiguration().locale.getLanguage();
		if (languageStr.endsWith("zh")) {
			return resources.getString(R.string.chinese);
		} else if (languageStr.endsWith("en")) {
			return resources.getString(R.string.english);
		} else {
			return resources.getString(R.string.unknown);
		}
	}

	public static int dp2px(Context ctx, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, ctx.getResources().getDisplayMetrics());
	}

	public static void showFragment(Activity aty, Fragment fragment) {
		if (aty != null && fragment != null) {
			fragment.onResume();
			aty.getFragmentManager().beginTransaction().show(fragment).commit();
		}
	}

	public static void hideFragment(Activity aty, Fragment fragment) {
		if (aty != null && fragment != null) {
			fragment.onPause();
			aty.getFragmentManager().beginTransaction().hide(fragment).commit();
		}
	}

	public static void replaceFragment(Activity aty, Fragment fragment, int contentId) {
		if (aty != null && fragment != null) {
			aty.getFragmentManager().beginTransaction().replace(contentId, fragment).commit();
		}
	}

	public static void commitFragment(Activity aty, Fragment fragment, int contentId) {
		if (aty != null && fragment != null) {
			aty.getFragmentManager().beginTransaction().add(contentId, fragment).commit();
		}
	}

	public static void saveSceneInfo(Context ctx, Device scene) {
		SharedPreferences sp = ctx.getSharedPreferences(DEVICE_FILE_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("code", scene.getDevName());
		editor.putString("passwrod", scene.getPassword());
		editor.commit();
	}

	public static void removeSceneInfo(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(DEVICE_FILE_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.remove("code");
		editor.remove("password");
		editor.commit();
	}

	public static Device getSceneInfo(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(DEVICE_FILE_NAME, Context.MODE_PRIVATE);
		String code = sp.getString("code", "");
		String password = sp.getString("password", "");
		Device scene = new Device();
		scene.setDevName(code);
		scene.setPassword(password);
		return scene;
	}

	/**
	 * Loading
	 * 
	 * @return
	 */
	public static Animation getRotateAnimation() {
		RotateAnimation anim = new RotateAnimation(0, 359f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		anim.setDuration(900);
		anim.setRepeatCount(-1);
		anim.setRepeatMode(RotateAnimation.RESTART);
		anim.setInterpolator(new LinearInterpolator());
		return anim;
	}

	/**
	 * ��ʽ��ʱ��
	 * 
	 * @param date
	 */
	public static String formatDate(String date) {
		int index = date.indexOf("T") + 6;
		return date.substring(0, index).replace("T", " ");
	}

	/**
	 * ת���¶�ֵ
	 * 
	 * @param value
	 * @return
	 */
	public static String valueFormatDate(String value) {
		return "�¶�:" + value.replace("_", " ʱ��: ").substring(0, value.length() - 3);
	}

	/**
	 * ת�� UTC ʱ��ĺ�����
	 * 
	 * @param time
	 * @return
	 */

	public static String formatUtcToTime(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAT);
		sdf.setTimeZone(TimeZone.getDefault());
		return sdf.format(new Date(time));
	}

	public static String formatUtcToTim2(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getDefault());
		return sdf.format(new Date(time));
	}

	public static long formatTimeToUtc(String time) {
		try {
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAT);
			sdf.setTimeZone(TimeZone.getDefault());
			calendar.setTime(sdf.parse(time));
			return calendar.getTimeInMillis();
		} catch (ParseException e) {
			return 0l;
		}
	}

	/**
	 * ��ʽ��ʱ��
	 * 
	 * @param isSpecial
	 * @param data
	 * @return
	 */
	public static String formatDate(boolean isSpecial, int data) {
		StringBuilder sb = new StringBuilder();
		if (isSpecial)
			data += 1;
		if (data < 10) {
			sb.append("0" + data);
		} else {
			sb.append(data);
		}
		return sb.toString();
	}

	/**
	 * ��ʱ�������ȥ���ķ���
	 * 
	 * @param time
	 * @return
	 */
	public static String removeSeconds(String time) {
		return time.substring(0, time.lastIndexOf(":"));
	}

	/**
	 * ����ת��������ʱ��
	 * 
	 * @return
	 */
	public static String localToGmt(String localTime) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAT);
			Date date = sdf.parse(localTime);
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			return sdf.format(date);
		} catch (Exception e) {
			return AxalentUtils.getSystemTime();
		}
	}

	/**
	 * ��������ʱ��ת����
	 * @return
	 */
	public static String gmtToLoLocal(String gmtTime) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAT);
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			Date date = sdf.parse(gmtTime);
			sdf.setTimeZone(TimeZone.getDefault());
			return sdf.format(date.getTime());
		} catch (Exception e) {
			LogUtils.e(e.getMessage());
			return AxalentUtils.getSystemTime();
		}
	}

	/**
	 *
	 * @return
	 */
	public static String gmtToLoLocal2(String gmtTime) {
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAT_SS);
		Date date = new Date(Long.parseLong(gmtTime));
		sdf.setTimeZone(TimeZone.getDefault());
		return sdf.format(date.getTime());
	}

	/**
	 *
	 */
	public static int compareDate(String serverTime, String localTime) {
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAT);
		try {
			Date serDate = sdf.parse(serverTime);
			Date locDate = sdf.parse(localTime);
			return serDate.compareTo(locDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * ��õ�ǰ���ֻ�ʱ��
	 * 
	 * @return
	 */
	public static String getSystemTime() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.YEAR) + "-" + formatDate(true, c.get(Calendar.MONTH)) + "-"
				+ formatDate(false, c.get(Calendar.DAY_OF_MONTH)) + " " + formatDate(false, c.get(Calendar.HOUR_OF_DAY))
				+ ":" + formatDate(false, c.get(Calendar.MINUTE));
	}

	public static String getSystemTime(int day) {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.YEAR) + "-" + formatDate(true, c.get(Calendar.MONTH)) + "-"
				+ formatDate(false, c.get(Calendar.DAY_OF_MONTH) - day) + " " + formatDate(false, c.get(Calendar.HOUR_OF_DAY))
				+ ":" + formatDate(false, c.get(Calendar.MINUTE));
	}

	public static String getCurrentTime() {
		Calendar c = Calendar.getInstance();
		return formatDate(false, c.get(Calendar.HOUR_OF_DAY)) + ":" + formatDate(false, c.get(Calendar.MINUTE));
	}

	public static String getCurrentDate() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.YEAR) + "-" + formatDate(true, c.get(Calendar.MONTH)) + "-"
				+ formatDate(false, c.get(Calendar.DAY_OF_MONTH));
	}

	public static boolean isHaveSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	public static String getUrl() {
		SharedPreferences sp = MyApplication.getInstance()
				.getSharedPreferences(USER_FILE_NAME, Context.MODE_PRIVATE);
		if (getLoginMode(sp) == R.id.atyLoginCloudBtn || getLoginMode(sp) == R.id.atyLoginBluetoothBtn) {
			return SERVER_ADDRESS_REMOTELY;
		} else {
			return sp.getString("localUrl", SERVER_ADDRESS_LOCAL);
		}
	}

	public static int getLoginMode() {
		return getLoginMode(MyApplication.getInstance()
				.getSharedPreferences(USER_FILE_NAME, Context.MODE_PRIVATE));
	}

	public static int getLoginMode(SharedPreferences sp) {
		return sp.getInt("loginMode", R.id.atyLoginCloudBtn);
	}

	public static long getAvailaleSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return (availableBlocks * blockSize) / 1024 / 1024;
	}

	public static String getStartTime(Device device, String propName) {
		if (device != null) {
			List<DeviceAttribute> deviceAttributes = device.getAttributes();
			if (deviceAttributes != null) {
				for (DeviceAttribute deviceAttribute : deviceAttributes) {
					String attributeName = deviceAttribute.getName();
					if (attributeName.equals(propName)) {
						String attributeTime = deviceAttribute.getUpdTime();
						LogUtils.i("attributeTime:" + attributeTime);
						if (!TextUtils.isEmpty(attributeTime)) {
							return formatUtcToTime(Long.parseLong(attributeTime));
						}
					}
				}
			}
		}
		return getSystemTime();
	}

	public static void setWindowState(Window window, float alpha) {
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.alpha = alpha;
		window.setAttributes(lp);
	}

	public static boolean isValidTagAndAlias(String s) {
        Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_-]{0,}$");
        Matcher m = p.matcher(s);
        return m.matches();
    }
	
	public static boolean isHaveGateway() {
		List<Device> devices = MyCacheData.getDevices();
		for (Device device : devices) {
			String typeName = device.getTypeName();
			if (typeName != null && typeName.equalsIgnoreCase(AxalentUtils.TYPE_GATEWAY)) {
				return true;
			}
		}
		return false;
	}
	
	
	public static Device getGateway() {
		List<Device> devices = MyCacheData.getDevices();
		for (Device device : devices) {
			String typeName = device.getTypeName();
			if (typeName != null && typeName.equalsIgnoreCase(AxalentUtils.TYPE_GATEWAY)) {
				return device;
			}
		}
		return null;
	}

	public static Time getCurrentTimeMillis() {
		Time time = new Time();
		time.setId(1);
		time.setTime(getSystemTimeMillis());
		return time;
	}

	public static String getSystemTimeMillis() {
		return String.valueOf(System.currentTimeMillis());
	}


	public static String getCSRDeviceType(int type) {
		switch (type) {
			case CSRDevice.TYPE_LIGHT:
				return "light";
			case CSRDevice.TYPE_TEMPERATURE:
				return "temperature";
			case CSRDevice.TYPE_GATEWAY:
				return "gateway";
			case CSRDevice.TYPE_UNKNOWN:
				return "unknown";
		}
		return "";
	}

	/**
	 * Convert celsius value to kelvin.
	 *
	 * @param celsius Temperature in celsius.
	 * @return Temperature in kelvin.
	 */
	static public double convertCelsiusToKelvin(double celsius) {
		return (273.15 + celsius);
	}

	/**
	 * Retrieve Drawable from drawableID
	 * @param context
	 * @param drawableID
	 * @return Drawable selected
	 */
	static public Drawable getDrawable(Context context, int drawableID) {
		try {
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
				return ContextCompat.getDrawable(context, drawableID);
			}
			else {
				return context.getResources().getDrawable(drawableID);
			}
		}
		catch (Exception e) {
			return null;
		}
	}
}
