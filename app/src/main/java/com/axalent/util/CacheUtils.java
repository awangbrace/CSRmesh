/**
 * File Name                   : ���������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.util;

import java.util.ArrayList;
import java.util.List;

import com.axalent.application.MyCacheData;
import com.axalent.model.Device;
import com.axalent.model.DeviceAttribute;
import com.axalent.model.DeviceType;
import com.axalent.model.Trigger;
import com.axalent.model.TriggerAttribute;
import com.axalent.model.User;
import com.axalent.model.UserAttribute;

import android.text.TextUtils;

public class CacheUtils {

	public static User getUser() {
		return MyCacheData.getInstance().getCacheUser();
	}
	
	public static List<Trigger> getTriggers() {
		return MyCacheData.getTriggers();
	}
	
	public static List<Device> getDevices() {
		return MyCacheData.getDevices();
	}
	
	public static List<DeviceType> getDeviceTypes() {
		return MyCacheData.getDeviceTypes();
	}
	
	public static void saveUser(User user) {
		MyCacheData.getInstance().setCacheUser(user);
	}
	
	public static void saveDevice(Device device) {
		MyCacheData.getDevices().add(device);
	}
	
	public static void saveDevices(List<Device> devices) {
		MyCacheData.setDevices(devices);
	}
	
	public static void saveDeviceTypes(List<DeviceType> deviceTypes) {
		MyCacheData.setDeviceTypes(deviceTypes);
	}
	
	public static void saveTrigger(Trigger trigger) {
		MyCacheData.getTriggers().add(trigger);
	}
	
	public static void saveTriggers(List<Trigger> triggers) {
		MyCacheData.setTriggers(triggers);
	}
	
	public static Trigger getTriggerByAttributeDevId(String devId) {
		List<Trigger> triggers = getTriggers();
		for (Trigger trigger : triggers) {
			List<TriggerAttribute> triggerAttributes = trigger.getTriggerAttributes();
			if (triggerAttributes != null) {
				for (TriggerAttribute triggerAttribute : triggerAttributes) {
					String triggerDevId = triggerAttribute.getDeviceID();
					if (triggerDevId.equals(devId)) {
						return trigger;
					}
				}
			}
		}
		return null;
	}
	
	public static Trigger getTriggerByTriggerId(String triggerId) {
		List<Trigger> triggers = getTriggers();
		for (Trigger trigger : triggers) {
			if (trigger.getTriggerId().equals(triggerId)) {
				return trigger;
			}
		}
		return null;
	}
	
	public static Trigger getTriggerByDevId(String devId) {
		List<Trigger> triggers = getTriggers();
		for (Trigger trigger : triggers) {
			if (trigger.getDeviceId().equals(devId)) {
				return trigger;
			}
		}
		return null;
	}
	
	public static List<Trigger> getTriggersByDevId(String devId) {
		List<Trigger> newList = new ArrayList<Trigger>();
		List<Trigger> triggers = getTriggers();
		for (Trigger trigger : triggers) {
			if (trigger.getDeviceId().equals(devId)) {
				newList.add(trigger);
			}
		}
		return newList;
	}
	
	public static void deleteTriggersByDevId(String devId) {
		List<Trigger> triggers = getTriggers();
		if (triggers.isEmpty()) return;
		List<Trigger> deleteList = new ArrayList<Trigger>();
		for (Trigger trigger : triggers) {
			if (devId.equals(trigger.getDeviceId())) {
				deleteList.add(trigger);
				continue;
			}
			List<TriggerAttribute> triggerAttributes = trigger.getTriggerAttributes();
			if (triggerAttributes != null) {
				for (TriggerAttribute triggerAttribute : triggerAttributes) {
					if (devId.equals(triggerAttribute.getDeviceID())) {
						deleteList.add(trigger);
						break;
					}
				}
			}
		}
		if (deleteList.isEmpty()) return;
		for (Trigger trigger : deleteList) {
//			LogUtils.i("ɾ��� Trigger ID��"+trigger.getTriggerId());
			deleteTriggerByTriggerId(trigger.getTriggerId());
		}
	}
	
	public static boolean deleteTriggerByTriggerId(String triggerId) {
		List<Trigger> triggers = getTriggers();
		for (Trigger trigger : triggers) {
			if (trigger.getTriggerId().equals(triggerId)) {
				triggers.remove(trigger);
				return true;
			}
		}
		return false;
	}
	
	public static boolean updateTriggerAttributeByTriggerId(String triggerId, List<TriggerAttribute> triggerAttributes) {
		List<Trigger> triggers = getTriggers();
		for (Trigger trigger : triggers) {
			if (trigger.getTriggerId().equals(triggerId)) {
				trigger.setTriggerAttributes(triggerAttributes);
				return true;
			}
		}
		return false;
	}
	
	public static List<Device> getDevicesByTypeNames(String[] typeNames) {
		List<Device> typeNamesDevices = new ArrayList<Device>();
		List<Device> devices = getDevices();
		for (Device device : devices) {
			for (int i = 0; i < typeNames.length; i++) {
				if (device.getTypeName().equals(typeNames[i])) {
					typeNamesDevices.add(device);
				}
			}
		}
		return typeNamesDevices;
	}
	
	public static List<Device> getDeviceByTypeName(String typeName) {
		List<Device> typeDevices = new ArrayList<Device>();
		if (!TextUtils.isEmpty(typeName)) {
			List<Device> devices = getDevices();
			// 12-21�����ָ��,ԭ���� DeviceType.xml ��������豸����
			for (Device device : devices) {
				String name = device.getTypeName();
				if (name != null && name.equals(typeName)) {
					typeDevices.add(device);
				}
			}
		}
		return typeDevices;
	}
	
	public static Device getDeviceByDevId(String devId) {
		List<Device> devices = getDevices();
		for (Device device : devices) {
			if (device.getDevId().equals(devId)) {
				return device;
			}
		}
		return null;
	}
	
	public static Device getDeviceByName(String devName) {
		List<Device> devices = getDevices();
		for (Device device : devices) {
			if (device.getDevName().equals(devName)) {
				return device;
			}
		}
		return null;
	}
	
	public static boolean deleteDeviceById(String devId) {
		List<Device> devices = getDevices();
		for (Device device : devices) {
			if (device.getDevId().equals(devId)) {
				devices.remove(device);
				return true;
			}
		}
		return false;
	}
	
	public static boolean deleteDeviceByName(String devName) {
//		LogUtils.i("ɾ����豸��:"+devName);
		List<Device> devices = getDevices();
		for (Device device : devices) {
			if (device.getDevName().equals(devName)) {
				devices.remove(device);
				return true;
			}
		}
		return false;
	}
	
	public static boolean updateDeviceAttibuteByName(String devName, String name, String value) {
		Device device = getDeviceByName(devName);
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
	
	public static boolean updateDeviceAttibuteByDevId(String devId, String name, String value) {
		Device device = getDeviceByDevId(devId);
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
	
	
	public static boolean replaceDevice(Device device) {
		if (deleteDeviceById(device.getDevId())) {
			saveDevice(device);
			return true;
		}
		return false;
	}
	
	public static int getDeviceTypeCount(String typeName) {
		List<Device> devices = getDevices();
		int count = 0;
		for (Device device : devices) {
			if (typeName.equalsIgnoreCase(device.getTypeName())) {
				count ++;
			}
		}
		return count;
	}
	
	public static String getUserAttribute(String name) {
		List<UserAttribute> userAttributes = getUser().getUserAttributes();
		for (UserAttribute userAttribute : userAttributes) {
			if (name.equals(userAttribute.getName())) {
				String value = userAttribute.getValue();
				return value == null ? "" : value;
			}
		}
		return "";
	}
	
	public static boolean setUserAttribute(String name, String value) {
		List<UserAttribute> userAttributes = getUser().getUserAttributes();
		if (userAttributes != null && value != null) {
			for (UserAttribute userAttribute : userAttributes) {
				if (name.equals(userAttribute.getName())) {
					userAttribute.setValue(value);
					return true;
				}
			}
		}
		return false;
	}
	
	
}
