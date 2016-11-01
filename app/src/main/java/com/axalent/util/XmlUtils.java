/**
 * File Name                   : XML������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/29
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.util;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.NetworkResponse;
import com.axalent.R;
import com.axalent.application.MyApplication;
import com.axalent.model.APWifi;
import com.axalent.model.Date;
import com.axalent.model.Device;
import com.axalent.model.DeviceAttribute;
import com.axalent.model.DeviceRecord;
import com.axalent.model.DeviceType;
import com.axalent.model.Gateway;
import com.axalent.model.HintError;
import com.axalent.model.Trigger;
import com.axalent.model.TriggerAttribute;
import com.axalent.model.UpdataInfo;
import com.axalent.model.User;
import com.axalent.model.UserAttribute;

import android.text.TextUtils;
import android.util.Xml;

public class XmlUtils {
	
	public static User convertUserLogin(XmlPullParser pull) {
		User user = new User(); 
		try {
			String name = "";
			int eventType = pull.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					name = pull.getName();
					if ("ns1:userLoginResponse".equals(name)) {
						
					} else if ("userId".equals(name)) {
						user.setUserId(pull.nextText());
					} else if ("securityToken".equals(name)) {
						user.setSecurityToken(pull.nextText());
					}
					break;
				}
				eventType = pull.next();
			}
		} catch (Exception e) {
		}
		return user;
	}
	
	public static List<UserAttribute> converUserValueList(XmlPullParser pull) {
		List<UserAttribute> userAttributes = new ArrayList<UserAttribute>();
		try {
			String name = "";
			UserAttribute userAttribute = null;
			int eventType = pull.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					name = pull.getName();
					if ("valist".equals(name)) {
						userAttribute = new UserAttribute();
					} else if ("name".equals(name)) {
						userAttribute.setName(pull.nextText());
					} else if ("value".equals(name)) {
						userAttribute.setValue(pull.nextText());
					} else if ("updTime".equals(name)) {
						userAttribute.setUpdTime(pull.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					name = pull.getName();
					if ("valist".equals(name)) {
						userAttributes.add(userAttribute);
					}
					break;
				}
				eventType = pull.next();
			}
		} catch (Exception e) {
			
		}
		return userAttributes;
	}
	
	public static ArrayList<Device> converDeviceList(XmlPullParser pull) {
		ArrayList<Device> devices = new ArrayList<Device>();
		try {
			String name = "";
			Device device = null;
			int eventType = pull.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					name = pull.getName();
					if ("devList".equals(name)) {
						device = new Device();
					} else if ("devId".equals(name)) {
						device.setDevId(pull.nextText());
					} else if ("devName".equals(name)) {
						device.setDevName(pull.nextText());
					} else if ("typeId".equals(name)) {
						device.setTypeId(pull.nextText());
					} else if ("sleepMode".equals(name)) {
						
					} else if ("appID".equals(name)) {
						
					} else if ("userID".equals(name)) {
						device.setUserId(pull.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					name = pull.getName();
					if ("devList".equals(name)) {
						devices.add(device);
					}
					break;
				}
				eventType = pull.next();
			}
		} catch (Exception e) {
		}
		return devices;
	}
	
	public static HintError converErrorMsg(VolleyError volleyError) {
		try {
			if (volleyError != null) {
				NetworkResponse response = volleyError.networkResponse;
				if (response != null) {
					HintError errorUS = converErrorUsMsg(new StringReader(new String(response.data, HttpHeaderParser.parseCharset(response.headers))));
					String language = AxalentUtils.getLanguage();
					if ("en".equalsIgnoreCase(language)) {
						return errorUS;
					} else {
						// "ch".equalsIgnoreCase(language)
						return converErrorChMsg(MyApplication.getInstance().getApplicationContext().getAssets().open(AxalentUtils.ERROR_FILE_NAME), errorUS.getErrorCode());
					}
				}
			}
		} catch (Exception e) {
		}
		return new HintError("", MyApplication.getInstance().getApplicationContext().getResources().getString(R.string.network_disconnect));
	}
	
	
	public static HintError converErrorChMsg(InputStream is, String code) {
		try {
			if (is != null && !TextUtils.isEmpty(code)) {
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				XmlPullParser pull = factory.newPullParser();
				pull.setInput(is, "UTF-8");
				int eventType = pull.getEventType();
				String name = "";
				boolean find = false;
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_TAG:
						name = pull.getName();
						if ("ns1:requestFault".equals(name)) {
							
						} else if ("errorCode".equals(name)) {
							if (code.equals(pull.nextText())) {
								find = true;
							}
						} else if ("errorMsg".equals(name)) {
							if (find) {
								return new HintError(code, pull.nextText());
							}
						}
						break;
					}
					eventType = pull.next();
				}
			}
		} catch (Exception e) {
		}
		return new HintError(code, MyApplication.getInstance().getApplicationContext().getResources().getString(R.string.unknown_error));
	}
	
	
	public static HintError converErrorUsMsg(StringReader reader) {
		try {
			if (reader != null) {
				HintError hintError = new HintError();
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				XmlPullParser pull = factory.newPullParser();
				pull.setInput(reader);
				int eventType = pull.getEventType();
				String name = "";
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_TAG:
						name = pull.getName();
						if ("ns1:requestFault".equals(name)) {
							
						} else if ("errorCode".equals(name)) {
							hintError.setErrorCode(pull.nextText());
						} else if ("errorMsg".equals(name)) {
							hintError.setErrorMsg(pull.nextText());
						}
						break;
					}
					eventType = pull.next();
				}
				return hintError;
			}
		} catch (Exception e) {
		}
		return new HintError("", MyApplication.getInstance().getApplicationContext().getResources().getString(R.string.conver_data_error));
	}
	
	public static ArrayList<Trigger> convertTriggerByUser(XmlPullParser pull) {
		ArrayList<Trigger> triggers = new ArrayList<Trigger>();
		try {
			String name = "";
			Trigger trigger = null;
			TriggerAttribute triggerAttribute = null;
			List<TriggerAttribute> triggerAttributes = null;
			int eventType = pull.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
					case XmlPullParser.START_TAG:
						name = pull.getName();
						if ("ns1:getTriggerDetailListByUserResponse".equals(name)) {
							
						} else if ("trigList".equals(name)) {
							trigger = new Trigger(); 
						} else if ("triggerId".equals(name)) {
							trigger.setTriggerId(pull.nextText());
						} else if ("devId".equals(name)) {
							trigger.setDeviceId(pull.nextText());
						} else if ("action".equals(name)) {
							trigger.setAction(pull.nextText());
						} else if ("attrName".equals(name)) {
							trigger.setAttrName(pull.nextText());
						} else if ("operation".equals(name)) {
							trigger.setOperation(pull.nextText());
						} else if ("threshold".equals(name)) {
							trigger.setThreshold(pull.nextText());
						} else if ("stringThreshold".equals(name)) {
							trigger.setStringThreshold(pull.nextText());
						} else if ("address".equals(name)) {
							trigger.setAddress(pull.nextText());
						} else if ("msg".equals(name)) {
							trigger.setMessage(pull.nextText());
						} else if ("autoDisarm".equals(name)) {
							trigger.setAutoDisarm(pull.nextText());
						} else if ("disarmed".equals(name)) {
							trigger.setDisarmed(pull.nextText());
						} else if ("autoDelete".equals(name)) {
							trigger.setAutoDelete(pull.nextText());
						} else if ("autoDisable".equals(name)) {
							trigger.setAutoDisable(pull.nextText());
						} else if ("enable".equals(name)) {
							trigger.setEnable(pull.nextText());
						} else if ("targetAttributesList".equals(name)) {
							triggerAttributes = new ArrayList<TriggerAttribute>();
							triggerAttribute = new TriggerAttribute();	
						} else if ("deviceTriggerID".equals(name)) {
							triggerAttribute.setDeviceTriggerID(pull.nextText());
						} else if ("deviceID".equals(name)) {
							triggerAttribute.setDeviceID(pull.nextText());
						} else if ("attributeName".equals(name)) {
							triggerAttribute.setAttributeName(pull.nextText());
						} else if ("value".equals(name)) {
							triggerAttribute.setValue(pull.nextText());
						}
						break;
					case XmlPullParser.END_TAG:
						name = pull.getName();
						if ("trigList".equals(name)) {
							trigger.setTriggerAttributes(triggerAttributes);
							triggers.add(trigger);
						} else if ("targetAttributesList".equals(name)) {
							triggerAttributes.add(triggerAttribute);
						}
						break;
				}
				eventType = pull.next();
			}
		} catch (Exception e) {
		}
		return triggers;
	}
	
	public static List<DeviceType> convertDeviceTypeList(XmlPullParser pull) {
		List<DeviceType> deviceTypes = new ArrayList<DeviceType>();
		try {
			String name = "";
			DeviceType deviceType = null;
			int eventType = pull.getEventType();
			while ((eventType != XmlPullParser.END_DOCUMENT)) {
				switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						name = pull.getName();
						if ("typeNameList".equals(name)) {
							deviceType = new DeviceType();
						} else if ("id".equals(name)) {
							deviceType.setId(pull.nextText());
						} else if ("name".equals(name)) {
							deviceType.setName(pull.nextText());
						} else if ("displayName".equals(name)) {
							deviceType.setDisplayName(pull.nextText());
						}
						break;
					case XmlPullParser.END_TAG:
						if ("typeNameList".equals(pull.getName())) {
							deviceTypes.add(deviceType);
						} 
						break;
				}
				eventType = pull.next();
			}
		} catch (Exception e) {
		}
		return deviceTypes;
	}
	
	
	public static List<DeviceType> convertDeviceTypeList(StringReader reader) {
		List<DeviceType> deviceTypes = new ArrayList<DeviceType>();
		try {
			if (reader != null) {
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				XmlPullParser pull = factory.newPullParser();
				pull.setInput(reader);
				int eventType = pull.getEventType();
				DeviceType deviceType = null;
				String name = "";
				while ((eventType != XmlPullParser.END_DOCUMENT)) {
					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						name = pull.getName();
						if ("typeNameList".equals(name)) {
							deviceType = new DeviceType();
						} else if ("id".equals(name)) {
							deviceType.setId(pull.nextText());
						} else if ("name".equals(name)) {
							deviceType.setName(pull.nextText());
						} else if ("displayName".equals(name)) {
							deviceType.setDisplayName(pull.nextText());
						}
						break;
					case XmlPullParser.END_TAG:
						if ("typeNameList".equals(pull.getName())) {
							deviceTypes.add(deviceType);
						} 
						break;
					}
					eventType = pull.next();
				}
			}
		} catch (Exception e) {
		}
		return deviceTypes;
	}
	
	
	/**
	 * �����豸��������Ϣ
	 * @param XmlPullParser
	 * @return
	 */
	public static String convertPresenceInfo(XmlPullParser pull) {
		try {
			int eventType = pull.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("state".equals(pull.getName())) {
						return pull.nextText();
					}
					break;
				}
				eventType = pull.next();
			}
		} catch (Exception e) {
		}
		return "";
	}
	
	
	/**
	 * �����豸������
	 * @return
	 */
	public static Device convertDeviceAttributesWithValues(XmlPullParser pull) {
		Device device = new Device();
		List<DeviceAttribute> deviceAttributes = new ArrayList<DeviceAttribute>();
		try {
			String name = "";
			boolean flag = true;
			DeviceAttribute deviceAttribute = null;
			int eventType = pull.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					name = pull.getName();
					if ("ns1:getDeviceAttributesWithValuesResponse".equals(name)) {
						
					} else if ("typeId".equals(name)) {
						device.setTypeId(pull.nextText());
					} else if ("typeName".equals(name)) {
						device.setTypeName(pull.nextText());
					} else if ("presenceInfo".equals(name)) {
						device.setState(pull.nextText());
					} else if ("configurations".equals(name)) {
						
					} else if ("property".equals(name)) {
						
					} else if ("value".equals(name)) {
						if (!flag) {
							deviceAttribute.setValue(pull.nextText());
						}
					} else if ("attrList".equals(name)) {
						deviceAttribute = new DeviceAttribute();
						flag = false;
					} else if ("id".equals(name)) {
						deviceAttribute.setId(pull.nextText());
					} else if ("name".equals(name)) {
						deviceAttribute.setName(pull.nextText());
					} else if ("value".equals(name)) {
						
					} else if ("updTime".equals(name)) {
						deviceAttribute.setUpdTime(pull.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					name = pull.getName(); 
					if ("attrList".equals(name)) {
						deviceAttributes.add(deviceAttribute);
					} else if ("ns1:getDeviceAttributesWithValuesResponse".equals(name)) {
						device.setAttributes(deviceAttributes);
					}
					break;
				}
				eventType = pull.next();
			}
		} catch (Exception e) {
		}
		return device;
	}
	
	/**
	 * �����豸����ʷ��¼
	 * @param XmlPullParser
	 * @return
	 */
	public static DeviceRecord convertDeviceTS2(XmlPullParser pull) {
		
		DeviceRecord deviceRecord = new DeviceRecord();
		deviceRecord.setDates(new ArrayList<Date>());
		Date date = null;
		
		try {
			int eventType = pull.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String name = pull.getName();
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if ("devId".equals(name)) {
						deviceRecord.setDevId(pull.nextText());
					} else if ("propName".equals(name)) {
						deviceRecord.setPropName(pull.nextText());
					} else if ("tsData".equals(name)) {
						date = new Date();
					} else if ("time".equals(name)) {
						date.setTime(pull.nextText());
					} else if ("strValue".equals(name)) {
						date.setValue(pull.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("tsData".equals(name)) {
						deviceRecord.getDates().add(date);
					} 
					break;
				}
				eventType = pull.next();
			}
		} catch (Exception e) {
			
		}
		return deviceRecord;
	}
	
	/**
	 * �����豸 deviceId
	 * @param pull
	 * @return
	 */
	public static final String converDeviceAuth(XmlPullParser pull) {
		try {
			int eventType = pull.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("devId".equals(pull.getName())) {
						return pull.nextText();
					}
					break;
				}
				eventType = pull.next();
			}
		} catch (Exception e) {
		}
		return "";
	}
	
	/**
	 * ���� scene �� deviceId
	 * @param pull
	 * @return
	 */
	public static Device converSceneCode(XmlPullParser pull) {
		Device scene = new Device();
		try {
			int eventType = pull.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					String name = pull.getName();
					if ("ns1:requestCodeResponse".equals(name)) {
						
					} else if ("code".equals(name)) {
						scene.setDevName(pull.nextText());
					} else if ("password".equals(name)) {
						scene.setPassword(pull.nextText());
					}
					break;
				}
				eventType = pull.next();
			}
		} catch (Exception e) {
		}
		return scene;
	}
	
//	public static String converSceneError(VolleyError volleyError) {
//		try {
//			if (volleyError != null) {
//				NetworkResponse response = volleyError.networkResponse;
//				if (response != null) {
//					XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//					XmlPullParser pull = factory.newPullParser();
//					pull.setInput(new StringReader(new String(response.data, HttpHeaderParser.parseCharset(response.headers))));
//					int eventType = pull.getEventType();
//					while (eventType != XmlPullParser.END_DOCUMENT) {
//						switch (eventType) {
//						case XmlPullParser.START_DOCUMENT:
//							break;
//						case XmlPullParser.START_TAG:
//							if ("errorInfo".equals(pull.getName())) {
//								return pull.nextText();
//							}
//							break;
//						}
//						eventType = pull.next();
//					}
//				}
//			}
//		} catch (Exception e) {
//		}
//		return "";
//	}
	
	public static String converTriggerId(XmlPullParser pull) {
		try {
			int eventType = pull.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("triggerId".equals(pull.getName())) {
						return pull.nextText();
					} 
					break;
				}
				eventType = pull.next();
			}
		} catch (Exception e) {
		}
		return "";
	}
	
	
	public static String converRequestCode(XmlPullParser pull) {
		String retCode = "";
		try {
			int eventType = pull.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("retCode".equals(pull.getName())) {
						retCode = pull.nextText();
					} 
					break;
				}
				eventType = pull.next();
			}
		} catch (Exception e) {
		}
		return retCode == null ? "" : retCode;
	}
	
	public static DeviceAttribute converDeviceAttribute(XmlPullParser pull) {
		DeviceAttribute deviceAttribute = new DeviceAttribute();
		try {
			String name = "";
			int eventType = pull.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					name = pull.getName();
					if ("value".equals(name)) {
						deviceAttribute.setValue(pull.nextText());
					} else if ("updTime".equals(name)) {
						deviceAttribute.setUpdTime(pull.nextText());
					}
					break;
				}
				eventType = pull.next();
			}
		} catch (Exception e) {
		}
		return deviceAttribute;
	}
	
	// updating version version.xml parser
	public static UpdataInfo getUpdataInfo(InputStream is) throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "utf-8");
		int type = parser.getEventType();
		UpdataInfo info = new UpdataInfo();
		while(type != XmlPullParser.END_DOCUMENT){
			switch (type) {
			case XmlPullParser.START_TAG:
				if("version".equals(parser.getName())) {
					info.setVersion(parser.nextText());
				} else if("url".equals(parser.getName())) {
					info.setUrl(parser.nextText());
				} else if("description".equals(parser.getName())) {
					info.setDescription(parser.nextText());
				}
				break;
			}
			type = parser.next();
		}
		return info;
	}

	public static Gateway getGateway(XmlPullParser pull) {
		Gateway gateway = new Gateway();
		try {
			int eventType = pull.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String name = pull.getName();
				switch (eventType) {
					case XmlPullParser.START_TAG:
						if ("friendlyName".equals(name)) {
							gateway.setFriendlyName(pull.nextText());
						} else if ("manufacturer".equals(name)) {
							gateway.setManufacturer(pull.nextText());
						} else if ("modelName".equals(name)) {
							gateway.setModelName(pull.nextText());
						} else if ("ID".equals(name)) {
							gateway.setId(pull.nextText());
						}
						break;
					case XmlPullParser.END_TAG:
						if ("serviceList".equals(name) || "device".equals(name)) {
							return gateway;
						}
						break;
				}
				eventType = pull.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gateway;
	}
	
	
	public static void parserAPWifiList(XmlPullParser pull, List<APWifi> wifis) {
		APWifi wifi = null;
		try {
			int eventType = pull.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String name = pull.getName();
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("ApList".equals(name)) {
						wifi = new APWifi();
					} else if ("SSID".equals(name)) {
						wifi.setSsid(pull.nextText());
					} else if ("signal".equals(name)) {
						wifi.setSignal(pull.nextText());
					} else if ("encryption".equals(name)) {
						wifi.setEncryption(pull.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("ApList".equals(name)) {
						wifis.add(wifi);
					}
					break;
				}
				eventType = pull.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}
