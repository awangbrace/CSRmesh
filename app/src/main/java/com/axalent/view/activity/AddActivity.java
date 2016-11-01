/**
 * File Name                   : ����豸����
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Message;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.axalent.R;
import com.axalent.adapter.AddAdapter;
import com.axalent.adapter.AddAdapter.OnCheckedDeviceListener;
import com.axalent.adapter.AddAdapter.OnItemClickListener;
import com.axalent.application.BaseActivity;
import com.axalent.presenter.AddDevice;
import com.axalent.presenter.DetectionChildInfo;
import com.axalent.presenter.DetectionChildInfo.OnDetectionDeviceListener;
import com.axalent.presenter.AddDevice.OnAddDeviceListener;
import com.axalent.presenter.controller.DeviceManager;
import com.axalent.presenter.controller.impl.DeviceManagerImpl;
import com.axalent.model.Device;
import com.axalent.model.DeviceAttribute;
import com.axalent.model.Group;
import com.axalent.model.HintError;
import com.axalent.model.Trigger;
import com.axalent.model.TriggerAttribute;
import com.axalent.model.ViewDevice;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;
import com.axalent.util.LogUtils;
import com.axalent.util.ToastUtils;
import com.axalent.util.XmlUtils;
import com.axalent.view.widget.AttributeWindow;
import com.axalent.view.widget.LoadingDialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AddActivity extends BaseActivity implements OnClickListener, OnCheckedDeviceListener, OnItemClickListener{

	private List<ViewDevice> viewDevices = new ArrayList<ViewDevice>();
	private DeviceManager deviceManager = new DeviceManagerImpl();
	private List<Device> successList = new ArrayList<Device>();
	private Map<Integer, ViewDevice> checkedDevices;
	private LoadingDialog loadingDialog;
//	private PopupWindow attributeWindow;
	private List<Device> filterDevices;
	private Button saveOrUpdateBtn;
	private Device currentDevice;
	private AddAdapter adapter;
	private ListView listView;
	private int action;
	// ͳ�Ƴɹ���ʧ��
	private int successCount = 0;
	private int errorCount = 0;
	private int sum = 0;
	// ��������� Handler
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AxalentUtils.DISPOSE_RESULT:
				disposeResult();
				break;
			case AxalentUtils.CLOSE_DIALOG:
				loadingDialog.close();
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		initExtraDatas();
		initActionBar();
		initView();
		initDatas();
	}
	
	private void initExtraDatas() {
		action = getIntent().getIntExtra(AxalentUtils.KEY_SKIP, 0);
		currentDevice = getIntent().getParcelableExtra(AxalentUtils.KEY_DEVICE);
		filterDevices = getIntent().getParcelableArrayListExtra(AxalentUtils.KEY_DEVICES);
	}
	
	private void initActionBar() {
		View customView = findViewById(R.id.barAddContent);
		TextView titleTxt = (TextView) customView.findViewById(R.id.barAddTitleTxt);
		titleTxt.setText(getActionBarTitle());
		RelativeLayout back = (RelativeLayout) customView.findViewById(R.id.barAddBack);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	private void disposeResult() {
		if (successCount == sum) {
			// ȫ���ɹ�
			ToastUtils.show(getSuccessTextId());
		} else if (errorCount == sum) {
			// ȫ��ʧ��
			ToastUtils.show(getErrorTextId());
		} else {
			// �ɹ���ʧ�ܶ���
			showItemErrorDialog();
		}
		clearAllDatas();
	}
	
	private int getSuccessTextId() {
		switch (action) {
		case AxalentUtils.ADD_SCENE:
		case AxalentUtils.ADD_TRIGGER:
		case AxalentUtils.ADD_SCHEDULE:
		case AxalentUtils.ADD_GATEWAY_DEVICE:
		case AxalentUtils.ADD_GUNI_TRIGGER:
			return R.string.add_success;
		case AxalentUtils.UPDATE_SCENE:
		case AxalentUtils.UPDATE_SCHEDULE:
			return R.string.update_success;
		default:
			return R.string.add_success;
		}
	} 
	
	private int getErrorTextId() {
		switch (action) {
		case AxalentUtils.ADD_SCENE:
		case AxalentUtils.ADD_TRIGGER:
		case AxalentUtils.ADD_SCHEDULE:
		case AxalentUtils.ADD_GATEWAY_DEVICE:
		case AxalentUtils.ADD_GUNI_TRIGGER:
			return R.string.add_failure;
		case AxalentUtils.UPDATE_SCENE:
		case AxalentUtils.UPDATE_SCHEDULE:
			return R.string.update_failure;
		default:
			return R.string.add_failure;
		}
	} 
	
	
	private void initView() {
		listView = (ListView) findViewById(R.id.atyAddListView);
		saveOrUpdateBtn = (Button) findViewById(R.id.atyAddSaveOrUpdateBtn);
		saveOrUpdateBtn.setText(getButtonText());
		saveOrUpdateBtn.setOnClickListener(this);
		loadingDialog = new LoadingDialog(this);
	}
	
	private void initDatas() {
		List<Device> currentDevices = CacheUtils.getDevices();
		if (currentDevices != null && currentDevices.size() != 0) {
			switch (action) {
			case AxalentUtils.ADD_SCENE:
			case AxalentUtils.ADD_TRIGGER:
			case AxalentUtils.ADD_SCHEDULE:
			case AxalentUtils.UPDATE_SCENE:
			case AxalentUtils.UPDATE_SCHEDULE:
				initSceneOrScheduleOrTriggerDatas(currentDevices);
				break;
			case AxalentUtils.ADD_GATEWAY_DEVICE:
				initAddGatewayDeviceDatas(currentDevices);
				break;
			case AxalentUtils.ADD_GUNI_TRIGGER:
				initAddGuniTriggerDatas(currentDevices);
				break;
			}
			setAdapter();
		} else {
			
		}
	}
	
	private void initAddGuniTriggerDatas(List<Device> currentDevices) {
		for (Device device : currentDevices) {
			String typeName = device.getTypeName();
			if (typeName.equalsIgnoreCase(AxalentUtils.TYPE_GUNI_LAMP)
					|| typeName.equalsIgnoreCase(AxalentUtils.TYPE_GUNI_PLUG)
//					|| typeName.equalsIgnoreCase(AxalentUtils.TYPE_SENSOR_PIR)
//					|| typeName.equalsIgnoreCase(AxalentUtils.TYPE_SENSOR_LIGHT)
					|| typeName.equalsIgnoreCase(AxalentUtils.TYPE_GUNI_SWITCH)) {
				saveCopyDeviceToList(device);
			}
		}
		
	}
	
	private void closeDialog() {
		handler.sendEmptyMessage(AxalentUtils.CLOSE_DIALOG);
	}
	
	
	private void initAddGatewayDeviceDatas(List<Device> currentDevices) {
		for (Device device : currentDevices) {
			String typeName = device.getTypeName();
			if (typeName != null && AxalentUtils.TYPE_GATEWAY.equalsIgnoreCase(typeName)) {
				saveCopyDeviceToList(device);
			}
		}
	}
	
	private void initSceneOrScheduleOrTriggerDatas(List<Device> currentDevices) {
		for (Device device : currentDevices) {
			String typeName = device.getTypeName();
			
			if(isExist(device.getDevName())) {
				//������豸�Ѿ����ھͲ����
				continue;
			}
			
			if (AxalentUtils.TYPE_SL.equalsIgnoreCase(typeName)
					|| AxalentUtils.TYPE_POWER_PLUG.equalsIgnoreCase(typeName)
					|| AxalentUtils.TYPE_SWITCH_TWO.equalsIgnoreCase(typeName)
					|| AxalentUtils.TYPE_WINDOW_COVER.equalsIgnoreCase(typeName)
					|| AxalentUtils.TYPE_GUNI_LAMP.equalsIgnoreCase(typeName)
					|| AxalentUtils.TYPE_GUNI_PLUG.equalsIgnoreCase(typeName)) {
				saveCopyDeviceToList(device);
			}
			
			if (AxalentUtils.TYPE_AXALENT_SCENE.equalsIgnoreCase(typeName)) {
				switch (action) {
				case AxalentUtils.ADD_SCHEDULE:
				case AxalentUtils.UPDATE_SCHEDULE:
					saveCopyDeviceToList(device);
					break;
				}
			}
			
		}
	}
	
	private void saveCopyDeviceToList(Device device) {
		ViewDevice viewDevice = (ViewDevice) AxalentUtils.copyDevice(device, new ViewDevice());
		viewDevice.setToggle(AxalentUtils.OFF);
		viewDevices.add(viewDevice);
	}

	/**
	 * �ж��Ƿ����ظ����豸
	 * @param devName
	 * @return
	 */
	private boolean isExist(String devName) {
		if (filterDevices != null && devName != null) {
			for (Device filterDevice : filterDevices) {
				String mDevName = filterDevice.getDevName();
				if (mDevName.equals(devName)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void setAdapter() {
		if (adapter == null) {
			adapter = new AddAdapter(this, action, viewDevices);
			adapter.setOnCheckedDeviceListener(this);
			adapter.setOnItemClickListener(this);
			listView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}
	
	private int getActionBarTitle() {
		switch (action) {
		case AxalentUtils.ADD_SCENE:
			return R.string.add_scene;
		case AxalentUtils.ADD_SCHEDULE:
			return R.string.add_schedule;
		case AxalentUtils.ADD_GATEWAY_DEVICE:
			return R.string.add_device;
		case AxalentUtils.ADD_TRIGGER:
		case AxalentUtils.ADD_GUNI_TRIGGER:
			return R.string.add_trigger;
		case AxalentUtils.UPDATE_SCENE:
			return R.string.update_scene;
		case AxalentUtils.UPDATE_SCHEDULE:
			return R.string.update_schedule;
		default:
			return R.string.add_scene;
		}
	}
	
	private int getButtonText() {
		switch (action) {
		case AxalentUtils.ADD_SCENE:
		case AxalentUtils.ADD_SCHEDULE:
		case AxalentUtils.ADD_GATEWAY_DEVICE:
		case AxalentUtils.ADD_TRIGGER:
		case AxalentUtils.ADD_GUNI_TRIGGER:
			return R.string.add;
		case AxalentUtils.UPDATE_SCENE:
		case AxalentUtils.UPDATE_SCHEDULE:
			return R.string.update;
		default:
			return R.string.add;
		}
	}

	@Override
	public void onChecked(int position, boolean isChecked) {
		if (checkedDevices == null) {
			checkedDevices = new HashMap<Integer, ViewDevice>();
		}
		
		if (isChecked) {
			checkedDevices.put(position, viewDevices.get(position));
		} else {
			if (checkedDevices.get(position) != null) {
				checkedDevices.remove(position);
			}
		}
	}
	

	@Override
	public void onClick(View v) {
		if (checkedDevices != null && checkedDevices.size() != 0) {
			switch (action) {
			case AxalentUtils.ADD_SCENE:
			case AxalentUtils.UPDATE_SCENE:
				new AddDeviceOrVirtualSceneTrigger();
				break;
			case AxalentUtils.ADD_SCHEDULE:
			case AxalentUtils.UPDATE_SCHEDULE:
				new AddScheduleTrigger();
				break;
			case AxalentUtils.ADD_GATEWAY_DEVICE:
				addGatewayDevice();
				break;
			case AxalentUtils.ADD_TRIGGER:
				new AddDeviceTrigger();
				break;
			case AxalentUtils.ADD_GUNI_TRIGGER:
				addGuniTrigger();
				break;
			}
		} else {
			ToastUtils.show(R.string.you_not_checked_device);
		}
	}
	
	private void addGuniTrigger() {
		loadingDialog.show(R.string.is_the_add);
		StringBuilder sb = new StringBuilder();
		for (Entry<Integer, ViewDevice> entry : checkedDevices.entrySet()) {
			String guid = AxalentUtils.getDeviceAttributeValue(entry.getValue(), AxalentUtils.ATTRIBUTE_GU_ID);
			if (guid.equals("")) {
				continue;
			}
			sb.append(guid);
		}
		
		final String name = AxalentUtils.ATTRIBUTE_GU_BIND;
		final String value = sb.toString();
		
		deviceManager.setDeviceAttribute(
				currentDevice.getDevId(), 
				name,
				value, 
				new Listener<XmlPullParser>() {
					@Override
					public void onResponse(XmlPullParser arg0) {
						CacheUtils.updateDeviceAttibuteByDevId(currentDevice.getDevId(), name, value);
						clearAllDatas();
						loadingDialog.close();
						ToastUtils.show(R.string.add_success);
					}
				}, 
				new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						clearAllDatas();
						loadingDialog.close();
						ToastUtils.show(R.string.add_failure);
					}
				});
	}
	
	private void addGatewayDevice() {
		loadingDialog.show(R.string.is_the_add);
		for (Entry<Integer, ViewDevice> entry : checkedDevices.entrySet()) {
			final Device gateway = entry.getValue();
			deviceManager.getPresenceInfo(gateway.getDevId(), new Listener<XmlPullParser>() {
				@Override
				public void onResponse(XmlPullParser response) {
					String state = XmlUtils.convertPresenceInfo(response);
					
					if (!TextUtils.isEmpty(state) && AxalentUtils.ONLINE.equals(state)) {
						AddDevice add = new AddDevice(deviceManager);
						add.setTimeOut(180000);
						add.setOnAddDeviceListener(new OnAddDeviceListener() {
							@Override
							public void onAddDevice(Device device, HintError error) {
								closeDialog();
								if (device != null) {
									sendAddPageDeviceToMain(device);
									ToastUtils.showByTask(R.string.add_success);
								} else {
									if (AxalentUtils.TIME_OUT.equals(error.getErrorCode())) {
										ToastUtils.showByTask(R.string.add_time_out);
									} else {
										ToastUtils.showByTask(R.string.add_failure);
									}
								}
							}
						});
						add.addGatewayDevice(gateway, AxalentUtils.ATTRIBUTE_PERMITJOIN, AxalentUtils.ADD_DEVICE_VALUE);
					} else {
						closeDialog();
						ToastUtils.show(R.string.device_offline);
					}
				
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					closeDialog();
					ToastUtils.show(XmlUtils.converErrorMsg(error));
				}
			});
			break;
		}
	}
	
	private void sendAddPageDeviceToMain(Device device) {
		Intent intent = new Intent("com.axalent.HomeActivity");
		intent.putExtra(AxalentUtils.KEY_DEVICE, device);
		sendBroadcast(intent);
	}
	
	class AddScheduleTrigger {
		
		public AddScheduleTrigger() {
			start();
		}
		
		private void start() {
			loadingDialog.show(AxalentUtils.ADD_SCHEDULE == action ? R.string.is_the_add : R.string.is_the_update);
			sum = checkedDevices.size();
			addScheduleTrigger1();
		}
		
		
		private void addScheduleTrigger1() {
			for (Entry<Integer, ViewDevice> entry : checkedDevices.entrySet()) {
				ViewDevice device = entry.getValue();
				Trigger trig = new Trigger();
				trig.setAction(AxalentUtils.ATTRIBUTE);
				trig.setAddress("");
				trig.setAttrName(AxalentUtils.ATTRIBUTE_ACTIVATE);
				trig.setAutoDelete("false");
				trig.setAutoDisable("false");
				trig.setAutoDisarm("false");
				trig.setDeviceId(currentDevice.getDevId());
				trig.setDisarmed("");
				trig.setEnable("true");
				trig.setMessage("");
				trig.setOperation("==");
				trig.setStringThreshold("");
				trig.setThreshold(AxalentUtils.ON);
				trig.setTriggerAttributes(getTriggerAttributes(device));
				addScheduleTrigger2(device, trig);
			}
		}
		
		private void addScheduleTrigger2(final ViewDevice device, final Trigger trig) {
			deviceManager.addTrigger(trig, new Listener<XmlPullParser>() {
				@Override
				public void onResponse(XmlPullParser response) {
					String triggerId = XmlUtils.converTriggerId(response);
					if (!TextUtils.isEmpty(triggerId)) {
						trig.setTriggerId(triggerId);
						CacheUtils.saveTrigger(trig);
						successList.add(device);
						successCount ++;
					} else {
						errorCount ++;
					}
					isAccomplish();
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					errorCount ++;
					isAccomplish();
				}
			});
		}
		
		private void isAccomplish() {
			if ((successCount + errorCount) == sum) {
				if (AxalentUtils.UPDATE_SCHEDULE == action) {
					setResult(0);
				}
				sendDisposeResult();
			}
		}
		
	}
	
	class AddDeviceOrVirtualSceneTrigger {
		
		public AddDeviceOrVirtualSceneTrigger() {
			start();
		}
			
		private void start() {
			loadingDialog.show(AxalentUtils.ADD_SCENE == action ? R.string.is_the_add : R.string.is_the_update);
			initSceneDatas();
		}
		
		private void initSceneDatas() {
			Map<String, Group> gatewayDevices = new HashMap<String, Group>();
			List<ViewDevice> wifiDevices = new ArrayList<ViewDevice>();
			for (Entry<Integer, ViewDevice> entry : checkedDevices.entrySet()) {
				ViewDevice device = entry.getValue();
				String gatewayName = AxalentUtils.getGatewayName(device.getDevName());
				Device gateway = findGateway(gatewayName);
				if (gateway != null) {
					// gateway ���б���ã���ѭ��������������scene
					Group group = gatewayDevices.get(gatewayName);
					if (group == null) {
						group = (Group) AxalentUtils.copyDevice(gateway, new Group());
						group.setChilds(new ArrayList<Device>());
						gatewayDevices.put(gatewayName, group);
					}
					group.getChilds().add(device);
				} else {
					// wifi 
					wifiDevices.add(device);
				}
			}
			disposeWiFiAndGatewayDevice(gatewayDevices, wifiDevices);
		}
		
		private Device findVirtualScene(Device gateway) {
			List<Trigger> triggers = CacheUtils.getTriggersByDevId(currentDevice.getDevId());
			for (Trigger trigger : triggers) {
				String devId = trigger.getTriggerAttributes().get(0).getDeviceID();
				Device device = CacheUtils.getDeviceByDevId(devId);
				if (device == null) {
					continue;
				}
				if (!AxalentUtils.TYPE_GATEWAY_SCENE.equalsIgnoreCase(device.getTypeName())) {
					continue;
				}
				String gatewayName = AxalentUtils.getGatewayName(device.getDevName());
				if (gatewayName.equals(gateway.getDevName())) {
					return device;
				}
			}
			return null;
		}
		
		private void disposeWiFiAndGatewayDevice(Map<String, Group> gatewayDevices, List<ViewDevice> wifiDevices) {
			// �����Ҫ��������
			sum = gatewayDevices.size() + wifiDevices.size();
			if (!gatewayDevices.isEmpty()) {
				for (Entry<String, Group> entry : gatewayDevices.entrySet()) {
					Group gateway = entry.getValue();
					Device virtualScene = findVirtualScene(gateway);
					if (virtualScene == null) {
						detectionGateway(gateway);
					} else {
						gateway.setDevice(virtualScene);
						setAttribute(gateway);
					}
				}
			}
			if (!wifiDevices.isEmpty()) {
				for (ViewDevice viewDevice : wifiDevices) {
					addDeviceTrigger1(viewDevice);
				}
			}
		}
		
		private void addDeviceTrigger1(ViewDevice device) {
			Trigger trig = new Trigger();
			trig.setAction(AxalentUtils.ATTRIBUTE);
			trig.setAddress("");
			trig.setAttrName(getTriggerKey(device.getTypeName()));
			trig.setAutoDelete("false");
			trig.setAutoDisable("false");
			trig.setAutoDisarm("false");
			trig.setDeviceId(currentDevice.getDevId());
			trig.setDisarmed("");
			trig.setEnable("true");
			trig.setMessage("");
			trig.setOperation("==");
			trig.setStringThreshold("");
			trig.setThreshold(AxalentUtils.ON);
			trig.setTriggerAttributes(getTriggerAttributes(device));
			addDeviceTrigger2(device, trig);
		}
		
		private void addDeviceTrigger2(final ViewDevice device, final Trigger trig) {
			deviceManager.addTrigger(trig, new Listener<XmlPullParser>() {
				@Override
				public void onResponse(XmlPullParser response) {
					String triggerId = XmlUtils.converTriggerId(response);
					if (!TextUtils.isEmpty(triggerId)) {
						trig.setTriggerId(triggerId);
						CacheUtils.saveTrigger(trig);
						successList.add(device);
						successCount ++;
					} else {
						errorCount ++;
					}
					isAccomplish();
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					errorCount ++;
					isAccomplish();
				}
			});
		}
		
		
		private void detectionGateway(final Group gateway) {
			DetectionChildInfo detection = new DetectionChildInfo(deviceManager, gateway, "C1");
			detection.setOnDetectionDeviceListener(new OnDetectionDeviceListener() {
				@Override
				public void onDetectionDevice(Device device, int detectionResult) {
					switch (detectionResult) {
					case AxalentUtils.DETECTION_NOT_ATTRIBUTE:
					case AxalentUtils.DETECTION_NOT_TRIGGER:
						gateway.setDevice(device);
						addVirtualSceneTrigger1(gateway);  
						break;
					default:
						addVirtualScene2(gateway, device, detectionResult);
						break;
					}
				}
			});
		}
		
		private void addVirtualScene2(final Group gateway, Device detectionDevice, int detectionResult) {
			AddDevice add = new AddDevice(deviceManager);
			add.setTimeOut(30000);
			add.setOnAddDeviceListener(new OnAddDeviceListener() {
				@Override
				public void onAddDevice(Device device, HintError hintError) {
					if (device != null) {
						gateway.setDevice(device);
						addVirtualSceneTrigger1(gateway);
					} else {
						errorCount ++;
						isAccomplish();
					}
				}
			});
			if (AxalentUtils.DETECTION_NOT_ADD == detectionResult) {
				add.addDevice(detectionDevice);
			} else {
				add.addGatewayDevice(gateway, AxalentUtils.ATTRIBUTE_ADD_SCENE, AxalentUtils.ADD_SCENE_VALUE);
			}
		}
		
		
		private void setAttribute(final Group gateway) {
			final Device device = gateway.getDevice();
			final List<Device> childs = gateway.getChilds();
			final String attributeValue = getDevInfoValue(childs);
			deviceManager.setDeviceAttribute(device.getDevId(), AxalentUtils.ATTRIBUTE_ADD_DEVICE, attributeValue, new Listener<XmlPullParser>() {
				@Override
				public void onResponse(XmlPullParser response) {
					LogUtils.i("scene setAttribute success");
					getAttribute(device, attributeValue);
					successList.addAll(childs);
					successCount ++;
					isAccomplish();
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					HintError hintError= XmlUtils.converErrorMsg(error);
					String errorCode =hintError.getErrorCode();
					LogUtils.e("scene setAttribute error:" + hintError.getErrorMsg());

					if ("6".equals(errorCode)) {
						// �豸����
						device.setState(AxalentUtils.OFF);
					}
					errorCount ++;
					isAccomplish();
				}
			});
		}
		
		private void getAttribute(Device device, String attributeValue) {
			List<DeviceAttribute> deviceAttributes = device.getAttributes();
			if (deviceAttributes == null) {
				deviceAttributes = new ArrayList<DeviceAttribute>();
				deviceAttributes.add(new DeviceAttribute("", AxalentUtils.ATTRIBUTE_ADD_DEVICE, attributeValue, ""));
				deviceAttributes.add(new DeviceAttribute("", AxalentUtils.ATTRIBUTE_DEVINFO, attributeValue, ""));
				device.setAttributes(deviceAttributes);
			} else {
				for (DeviceAttribute deviceAttribute : deviceAttributes) {
					if (AxalentUtils.ATTRIBUTE_DEVINFO.equals(deviceAttribute.getName())) {
						String value = deviceAttribute.getValue();
						if (TextUtils.isEmpty(value)) {
							deviceAttribute.setValue(attributeValue);
						} else {
							deviceAttribute.setValue(getAnewSplicingDevInfo(value, attributeValue));
						}
					}
				}
			}
		}
		
		/**
		 * �� VirtualScene �� DevInfo ��������ƴ��
		 */
		private String getAnewSplicingDevInfo(String value1, String value2) {
			try {
				JSONArray array1 = new JSONArray(value1);
				JSONArray array2 = new JSONArray(value2);
				int length = array2.length();
				for (int i = 0; i < length; i++) {
					array1.put(array2.getJSONObject(i));
				}
				return array1.toString();
			} catch (JSONException e) {
				return value1;
			}
		}
		
		
		private String getDevInfoValue(List<Device> devices) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			final int size = devices.size();
			for (int i = 0; i < size; i++) {
				ViewDevice viewDevice = (ViewDevice) devices.get(i);
				String typeName = viewDevice.getTypeName();
				
				sb.append("{");
				
				if (AxalentUtils.TYPE_SL.equalsIgnoreCase(typeName)) {
					appendParameters(viewDevice, sb, new String[]{AxalentUtils.ATTRIBUTE_DIMMER, AxalentUtils.ATTRIBUTE_HSV});
					sb.append("\"" + AxalentUtils.ATTRIBUTE_LIGHT + "\":");
					sb.append("\"" + String.valueOf(viewDevice.isSwitchIsChecked() ? AxalentUtils.ON : AxalentUtils.OFF) + "\",");
				} else if (AxalentUtils.TYPE_WINDOW_COVER.equalsIgnoreCase(typeName)) {
					if (!appendParameters(viewDevice, sb, new String[]{AxalentUtils.ATTRIBUTE_COVER})) {
						sb.append("\"" + AxalentUtils.ATTRIBUTE_COVER + "\":");
						sb.append("\"" + String.valueOf(viewDevice.isSwitchIsChecked() ? "99" : "0") + "\",");
					}
				} else if (AxalentUtils.TYPE_POWER_PLUG.equalsIgnoreCase(typeName)) {
					sb.append("\"" + AxalentUtils.ATTRIBUTE_MYSWITCH + "\":");
					sb.append("\"" + String.valueOf(viewDevice.isSwitchIsChecked() ? AxalentUtils.ON : AxalentUtils.OFF) + "\",");
				} else if (AxalentUtils.TYPE_SWITCH_TWO.equalsIgnoreCase(typeName)) {
					if (!appendParameters(viewDevice, sb, new String[]{AxalentUtils.ATTRIBUTE_SWITCH_0, AxalentUtils.ATTRIBUTE_SWITCH_1})) {
						String mSwitch = String.valueOf(viewDevice.isSwitchIsChecked() ? AxalentUtils.ON : AxalentUtils.OFF);
						sb.append("\"" + AxalentUtils.ATTRIBUTE_SWITCH_0 + "\":");
						sb.append("\"" + mSwitch + "\",");
						sb.append("\"" + AxalentUtils.ATTRIBUTE_SWITCH_1 + "\":");
						sb.append("\"" + mSwitch + "\",");
					}
				} else if (AxalentUtils.TYPE_GUNI_LAMP.equalsIgnoreCase(typeName)) {
					sb.append("\"" + AxalentUtils.ATTRIBUTE_GU_LIGHT + "\":");
					sb.append("\"" + String.valueOf(viewDevice.isSwitchIsChecked() ? AxalentUtils.ON : AxalentUtils.OFF) + "\",");
				} else if (AxalentUtils.TYPE_GUNI_PLUG.equalsIgnoreCase(typeName)) {
					sb.append("\"" + AxalentUtils.ATTRIBUTE_GU_SWITCH + "\":");
					sb.append("\"" + String.valueOf(viewDevice.isSwitchIsChecked() ? AxalentUtils.ON : AxalentUtils.OFF) + "\",");
				}
				
				sb.append("\"" + AxalentUtils.ATTRIBUTE_CODE + "\":");
				sb.append("\"" + viewDevice.getDevName() + "\"");
				sb.append("}");
				
				if ((i + 1) != size) {
					sb.append(",");
				}
				
			}
			sb.append("]");
			return sb.toString();
		}
		
		
		private boolean appendParameters(ViewDevice viewDevice, StringBuilder sb, String[] keys) {
			Map<String, String> attributesMap = viewDevice.getAttributesMap();
			if (attributesMap != null && attributesMap.size() != 0) {
				for (Entry<String, String> entry : attributesMap.entrySet()) {
					sb.append("\"" + entry.getKey() + "\":");
					sb.append("\"" + entry.getValue() + "\",");
				}
				viewDevice.setAttributesMap(null);
				return true;
			}
			return false;
		}

		
		private void addVirtualSceneTrigger1(Group gateway) {
			Device device = gateway.getDevice();
			device.setToggle(AxalentUtils.ON);
			Trigger trig = new Trigger();
			trig.setAction(AxalentUtils.ATTRIBUTE);
			trig.setAddress("");
			trig.setAttrName(AxalentUtils.ATTRIBUTE_ACTIVATE);
			trig.setAutoDelete("false");
			trig.setAutoDisable("false");
			trig.setAutoDisarm("false");
			trig.setDeviceId(currentDevice.getDevId());
			trig.setDisarmed("");
			trig.setEnable("true");
			trig.setMessage("");
			trig.setOperation("==");
			trig.setStringThreshold("");
			trig.setThreshold(AxalentUtils.ON);
			trig.setTriggerAttributes(getTriggerAttributes(device));
			addVirtualSceneTrigger2(trig, gateway);
		}
		
		private void addVirtualSceneTrigger2(final Trigger trig, final Group gateway) {
			deviceManager.addTrigger(trig, new Listener<XmlPullParser>() {
				@Override
				public void onResponse(XmlPullParser response) {
					String triggerId = XmlUtils.converTriggerId(response);
					if (!TextUtils.isEmpty(triggerId)) {
						trig.setTriggerId(triggerId);
						CacheUtils.saveTrigger(trig);
						getPresenceInfo(gateway);
					} else {
						errorCount ++;
						isAccomplish();
					}
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					errorCount ++;
					isAccomplish();
				}
			});
		}
		
		private void getPresenceInfo(final Group gateway) {
			final Device device = gateway.getDevice();
			deviceManager.getPresenceInfo(device.getDevId(), new Listener<XmlPullParser>() {
				@Override
				public void onResponse(XmlPullParser response) {
					String state = XmlUtils.convertPresenceInfo(response);
					if (!TextUtils.isEmpty(state) && state.equals(AxalentUtils.ONLINE)) {
						device.setState(state);
						setAttribute(gateway);
					} else {
						errorCount ++;
						isAccomplish();
					}
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					errorCount ++;
					isAccomplish();
				}
			});
		}
		
		private void isAccomplish() {
			if ((successCount + errorCount) == sum) {
				if (AxalentUtils.UPDATE_SCENE == action) {
					setResult(0);
				}
				sendDisposeResult();
			}
		}
		
	}
	
	
	class AddDeviceTrigger {
		
		String threshold = TextUtils.isEmpty(currentDevice.getToggle()) ? AxalentUtils.OFF : currentDevice.getToggle();
		
		public AddDeviceTrigger() {
			start();
		}
		
		private void start() {
			loadingDialog.show(R.string.is_the_add);
			sum = checkedDevices.size();
			if (currentDevice.getTypeName().equals(AxalentUtils.TYPE_HTS)) {
				addDeviceTrigger2();
			} else {
				addDeviceTrigger1();
			}
		}
		
		private void addDeviceTrigger1() {
			for (Entry<Integer, ViewDevice> entry : checkedDevices.entrySet()) {
				ViewDevice device = entry.getValue();
				Trigger trig = new Trigger();
				trig.setAction(AxalentUtils.ATTRIBUTE);
				trig.setAddress("");
				trig.setAttrName(getTriggerKey(currentDevice.getTypeName()));
				trig.setAutoDelete("false");
				trig.setAutoDisable("false");
				trig.setAutoDisarm("false");
				trig.setDeviceId(currentDevice.getDevId());
				trig.setDisarmed("");
				trig.setEnable("true");
				trig.setMessage("");
				trig.setOperation("==");
				trig.setStringThreshold("");
				trig.setThreshold(threshold + ".0");
				trig.setTriggerAttributes(getTriggerAttributes(device));
				addDeviceTrigger2(device, trig);
			}
		}
		
		private void addDeviceTrigger2() {
			
			String info = currentDevice.getToggle();
			
			LogUtils.i("info:"+info);
			
			String attrName = null;
			String operation = null;
			String threshold = null;
			
			try {
				JSONObject obj = new JSONObject(info);
				 attrName = (String) obj.opt("attrName");
				 operation = (String) obj.opt("operation");
				 threshold = (String) obj.opt("threshold");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			for (Entry<Integer, ViewDevice> entry : checkedDevices.entrySet()) {
				ViewDevice device = entry.getValue();
				Trigger trig = new Trigger();
				trig.setAction(AxalentUtils.ATTRIBUTE);
				trig.setAddress("");
				trig.setAttrName(attrName);
				trig.setAutoDelete("false");
				trig.setAutoDisable("false");
				trig.setAutoDisarm("false");
				trig.setDeviceId(currentDevice.getDevId());
				trig.setDisarmed("");
				trig.setEnable("true");
				trig.setMessage("");
				trig.setOperation(operation);
				trig.setStringThreshold("");
				trig.setThreshold(threshold);
				trig.setTriggerAttributes(getTriggerAttributes(device));
				addDeviceTrigger2(device, trig);
			}
		}
		
		private void addDeviceTrigger2(final ViewDevice device, final Trigger trig) {
			deviceManager.addTrigger(trig, new Listener<XmlPullParser>() {
				@Override
				public void onResponse(XmlPullParser response) {
					String triggerId = XmlUtils.converTriggerId(response);
					if (!TextUtils.isEmpty(triggerId)) {
						trig.setTriggerId(triggerId);
						CacheUtils.saveTrigger(trig);
						successList.add(device);
						successCount ++;
					} else {
						errorCount ++;
					}
					isAccomplish();
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					errorCount ++;
					isAccomplish();
				}
			});
		}
		
		private void isAccomplish() {
			if ((successCount + errorCount) == sum) {
				setResult(0, new Intent());
				sendDisposeResult();
			}
		}
	}
	
	
	private String getTriggerKey(String typeName) {
		if (AxalentUtils.TYPE_SL.equalsIgnoreCase(typeName)) {
			return AxalentUtils.ATTRIBUTE_LIGHT;
		} else if (AxalentUtils.TYPE_WINDOW_COVER.equalsIgnoreCase(typeName)) {
			return AxalentUtils.ATTRIBUTE_COVER;
		} else if (AxalentUtils.TYPE_POWER_PLUG.equalsIgnoreCase(typeName)) {
			return AxalentUtils.ATTRIBUTE_MYSWITCH;
		} else if (AxalentUtils.TYPE_SM.equalsIgnoreCase(typeName)) {
			return AxalentUtils.ATTRIBUTE_MOTION;
		} else if (AxalentUtils.TYPE_AXALENT_SCENE.equalsIgnoreCase(typeName)) {
			return AxalentUtils.ATTRIBUTE_ACTIVATE;
		} else if (AxalentUtils.TYPE_SC.equalsIgnoreCase(typeName)) {
			return AxalentUtils.ATTRIBUTE_CONTACT;
		}
		return "";
	}
	
	private List<TriggerAttribute> getTriggerAttributes(Device device) {
		List<TriggerAttribute> triggerAttributes = new ArrayList<TriggerAttribute>();
		String typeName = device.getTypeName();
		if (AxalentUtils.TYPE_SL.equalsIgnoreCase(typeName)) {
			triggerAttributes.add(new TriggerAttribute("", device.getDevId(), AxalentUtils.ATTRIBUTE_LIGHT, device.getToggle()));
		} else if (AxalentUtils.TYPE_WINDOW_COVER.equalsIgnoreCase(typeName)) {
			triggerAttributes.add(new TriggerAttribute("", device.getDevId(), AxalentUtils.ATTRIBUTE_COVER, device.getToggle()));
		} else if (AxalentUtils.TYPE_POWER_PLUG.equalsIgnoreCase(typeName)) {
			triggerAttributes.add(new TriggerAttribute("", device.getDevId(), AxalentUtils.ATTRIBUTE_MYSWITCH, device.getToggle()));
		} else if (AxalentUtils.TYPE_AXALENT_SCENE.equalsIgnoreCase(typeName) || AxalentUtils.TYPE_GATEWAY_SCENE.equalsIgnoreCase(typeName)) {
			triggerAttributes.add(new TriggerAttribute("", device.getDevId(), AxalentUtils.ATTRIBUTE_ACTIVATE, AxalentUtils.ON));
		} else if (AxalentUtils.TYPE_SWITCH_TWO.equalsIgnoreCase(typeName)) {
			triggerAttributes.add((new TriggerAttribute("", device.getDevId(), AxalentUtils.ATTRIBUTE_SWITCH_0, device.getToggle())));
			triggerAttributes.add((new TriggerAttribute("", device.getDevId(), AxalentUtils.ATTRIBUTE_SWITCH_1, device.getToggle())));
		} else if (AxalentUtils.TYPE_GUNI_LAMP.equalsIgnoreCase(typeName)) {
			triggerAttributes.add(new TriggerAttribute("", device.getDevId(),
					AxalentUtils.ATTRIBUTE_GU_LIGHT, device.getToggle()));
		} else if (AxalentUtils.TYPE_GUNI_PLUG.equalsIgnoreCase(typeName)) {
				triggerAttributes.add(new TriggerAttribute("", device.getDevId(),
						AxalentUtils.ATTRIBUTE_GU_SWITCH, device.getToggle()));
		}
		return triggerAttributes;
	}
	
	
	private void sendDisposeResult() {
		handler.sendEmptyMessage(AxalentUtils.DISPOSE_RESULT);
	}
	
	private Device findGateway(String devName) {
		return CacheUtils.getDeviceByName(devName);
	}
	
	private void clearAllDatas() {
		sum = 0;
		errorCount = 0;
		successCount = 0;
		deleteAdapterItem();
		successList.clear();
		resetCheckBoxState();
		loadingDialog.close();
		checkedDevices.clear();
	}
	
	private void deleteAdapterItem() {
		for (Device device : successList) {
			deleteAdapterDeviceById(device.getDevId());
		}
		adapter.notifyDataSetChanged();
	}
	
	private void deleteAdapterDeviceById(String devId) {
		for (Device device : viewDevices) {
			String mDevId = device.getDevId();
			if (mDevId.equals(devId)) {
				viewDevices.remove(device);
				break;
			}
		}
	}
	
	private void resetCheckBoxState() {
		for (ViewDevice viewDevice : viewDevices) {
			viewDevice.setBoxIsChecked(false);
			viewDevice.setEnabled(true);
		}
		adapter.notifyDataSetChanged();
	}
	

	/**
	 * ����״�����õ��������Ӻܶ��豸��Trigger��һ���ļ���ʧ�ܣ����dialog������ʾ�û�
	 */
	private void showItemErrorDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.app_logo_1);
		builder.setTitle(R.string.bind_device_failure);
		builder.setMessage(R.string.network_no_good_bind_devices_error);
		builder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		builder.create().show();
	}

	@Override
	public void onItemClick(View view, int position) {
		
		final ViewDevice viewDevice = viewDevices.get(position);		
		switch (action) {
		case AxalentUtils.ADD_TRIGGER:
		case AxalentUtils.ADD_SCHEDULE:
		case AxalentUtils.UPDATE_SCHEDULE:
		case AxalentUtils.ADD_GATEWAY_DEVICE:
			ToastUtils.show(R.string.device_not_support_hereon_edit);
			return;
		}
		
		final String typeName = viewDevice.getTypeName();
		if (AxalentUtils.TYPE_SL.equalsIgnoreCase(typeName) 
				|| AxalentUtils.TYPE_SWITCH_TWO.equalsIgnoreCase(typeName)
				|| AxalentUtils.TYPE_WINDOW_COVER.equalsIgnoreCase(typeName)) {
			AttributeWindow attributeWindow = new AttributeWindow(this, typeName);
			attributeWindow.setPitchonAttributeListener(new AttributeWindow.PitchonAttributeListener() {
				@Override
				public void onPitchonAttribute(Map<String, String> attributesMap) {
					viewDevice.setAttributesMap(attributesMap);
					if (AxalentUtils.TYPE_SL.equalsIgnoreCase(typeName)) {
						refreshDeviceState(viewDevice, true);
					} else if (AxalentUtils.TYPE_WINDOW_COVER.equalsIgnoreCase(typeName)) {
						refreshDeviceState(viewDevice, Integer.parseInt(attributesMap.get(AxalentUtils.ATTRIBUTE_COVER)) > 50 ? true : false);
					} else if (AxalentUtils.TYPE_SWITCH_TWO.equalsIgnoreCase(typeName)) {
						String switch0 = attributesMap.get(AxalentUtils.ATTRIBUTE_SWITCH_0);
						String switch1 = attributesMap.get(AxalentUtils.ATTRIBUTE_SWITCH_1);
						if (AxalentUtils.ON.equals(switch0) && AxalentUtils.ON.equals(switch1)) {
							refreshDeviceState(viewDevice, true);
						}
					}
				}
			});
			attributeWindow.show(saveOrUpdateBtn);
		} else {
			ToastUtils.show(R.string.device_not_support_hereon_edit);
		}	
		
//			Intent intent = new Intent(this, ShowDeviceActivity.class);
//			intent.putExtra(AxalentUtils.KEY_SKIP, AxalentUtils.SET_ATTRIBUTE);
//			intent.putExtra(AxalentUtils.KEY_DEVICE, device);
//			startActivityForResult(intent, 0);
	}
	
	private void refreshDeviceState(ViewDevice viewDevice, boolean switchIsChecked) {
		viewDevice.setSwitchIsChecked(switchIsChecked);
		adapter.notifyDataSetChanged();
	}
	
//	
//	
//	private boolean getSwitchIsChecked(ViewDevice viewDevice) {
//		String typeName = viewDevice.getTypeName();
//		if (AxalentUtils.TYPE_WINDOW_COVER.equalsIgnoreCase(typeName)) {
//			return Integer.parseInt(viewDevice.getAttributesMap().get(AxalentUtils.ATTRIBUTE_COVER)) > 50 ? true : false;
//		} else {
//			return true
//		}
//		return false;
//	}
//	


}
