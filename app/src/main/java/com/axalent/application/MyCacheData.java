package com.axalent.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.axalent.model.Device;
import com.axalent.model.DeviceType;
import com.axalent.model.Trigger;
import com.axalent.model.User;
import com.axalent.util.LogUtils;
import com.csr.csrmesh2.PowerState;

public class MyCacheData {

	private static MyCacheData instance = null;
	private static User cacheUser = null;
	private static List<Device> devices;
	private static List<Trigger> triggers;
	private static List<DeviceType> deviceTypes;
	private static Map<Integer, PowerState> stateMap;
	private static Map<Integer, Integer> lightStateMap;

	private MyCacheData() {};

	public static MyCacheData getInstance() {
		if (instance == null) {
			instance = new MyCacheData();
		}
		if (stateMap == null) {
			stateMap = new HashMap<>();
		}
		if (lightStateMap == null) {
			lightStateMap = new HashMap<>();
		}
		return instance;
	}

	public User getCacheUser() {
		if (cacheUser == null) {
			cacheUser = new User();
		}
		return cacheUser;
	}

	public void setCacheUser(User cacheUser) {
		this.cacheUser = cacheUser;
	}

	public PowerState getState(Integer key) {
		return this.stateMap.get(key);
	}

	public void setState(Integer key, PowerState state) {
		this.stateMap.put(key, state);
	}

	public Integer getLightState(Integer key) {
		return this.lightStateMap.get(key);
	}

	public void setLightState(Integer key, Integer state) {
		this.lightStateMap.put(key, state);
	}

	public static List<Device> getDevices() {
		if (devices == null) {
			devices = new ArrayList<Device>();
		}
		return devices;
	}

	public static void setDevices(List<Device> devices) {
		MyCacheData.devices = devices;
	}

	public static List<Trigger> getTriggers() {
		if (triggers == null) {
			triggers = new ArrayList<Trigger>();
		}
		return triggers;
	}

	public static void setTriggers(List<Trigger> triggers) {
		MyCacheData.triggers = triggers;
	}

	public static List<DeviceType> getDeviceTypes() {
		if (deviceTypes == null) {
			deviceTypes = new ArrayList<DeviceType>();
		}
		return deviceTypes;
	}

	public static void setDeviceTypes(List<DeviceType> deviceTypes) {
		MyCacheData.deviceTypes = deviceTypes;
	}

	public static void cacheDatas() {
		LogUtils.i("��������");
		
		if (cacheUser != null) {
			cacheUser.setUserId(null);
			cacheUser.setUsername(null);
			cacheUser.setSecurityToken(null);
			cacheUser.getUserAttributes().clear();
			cacheUser = null;
		}

		if (devices != null) {
			devices.clear();
			devices = null;
		}

		if (triggers != null) {
			triggers.clear();
			triggers = null;
		}

		if (deviceTypes != null) {
			deviceTypes.clear();
			deviceTypes = null;
		}

		MyApplication.getInstance().onLowMemory();
	}

}
