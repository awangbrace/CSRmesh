/**
 * File Name                   : ��ʾ Schedule ����
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.fragment;

import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Switch;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.axalent.R;
import com.axalent.view.activity.ShowGroupActivity;
import com.axalent.view.activity.ShowGroupActivity.GroupManage;
import com.axalent.adapter.ShowGroupAdapter;
import com.axalent.adapter.ShowGroupAdapter.OnChangeStateListener;
import com.axalent.model.Device;
import com.axalent.model.DeviceAttribute;
import com.axalent.model.Group;
import com.axalent.model.Trigger;
import com.axalent.model.TriggerAttribute;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;
import com.axalent.util.ToastUtils;
import com.axalent.util.XmlUtils;
import com.axalent.view.widget.DateDialog;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class ShowScheduleFragment extends Fragment implements GroupManage, OnMenuItemClickListener, OnChangeStateListener, OnCheckedChangeListener, OnItemSelectedListener, OnClickListener {

	private View view;
	private TextView dateTxt;
	private Switch enableSwitch;
	private Spinner optionSpinner;
	private ShowGroupActivity aty;
	private ShowGroupAdapter adapter;
	private SwipeMenuListView listView;
	private String cycle = "Never";
	private DateDialog dateDialog;
	private TextView nameTxt;
	private ArrayList<Device> childDevices = new ArrayList<Device>();
	
	@Override
	public void onAttach(Activity activity) {
		aty = (ShowGroupActivity) activity;
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_show_schedule, null);
			initView();
			initDatas();
		}
		return view;
	}
	
	private void initView() {
		listView = (SwipeMenuListView) view.findViewById(R.id.fragShowScheduleListView);
		optionSpinner = (Spinner) view.findViewById(R.id.fragShowScheduleOptionSpinner);
		dateTxt = (TextView) view.findViewById(R.id.fragShowScheduleDateTxt);
		enableSwitch = (Switch) view.findViewById(R.id.fragShowScheduleSwitch);
		Button updateBtn = (Button) view.findViewById(R.id.fragShowScheduleUpdateBtn);
		nameTxt = (TextView) view.findViewById(R.id.fragShowScheduleDevNameTxt);
		listView.setOnMenuItemClickListener(this);
		listView.setMenuCreator(getMenuCreator());
		enableSwitch.setOnCheckedChangeListener(this);
		optionSpinner.setOnItemSelectedListener(this);
		nameTxt.setOnClickListener(this);
		dateTxt.setOnClickListener(this);
		updateBtn.setOnClickListener(this);
	}
	
	private SwipeMenuCreator getMenuCreator() {
		return new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem deleteItem = new SwipeMenuItem(aty);
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
				deleteItem.setWidth(AxalentUtils.dp2px(getActivity(), 90));
				deleteItem.setIcon(R.drawable.delete);
				menu.addMenuItem(deleteItem);
			}
		};
	}
	
	private void initDatas() {
		aty.getLoadingDialog().show(R.string.load_data);
		aty.getDeviceManager().getDeviceAttributesWithValues(aty.getCurrentDevice(), new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				Device tempDevice = XmlUtils.convertDeviceAttributesWithValues(response);
				setPageDatas(tempDevice);
				initChildDatas();
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				aty.getLoadingDialog().close();
				ToastUtils.show(R.string.load_failure);
			}
		});
	}
	
	private void setPageDatas(Device tempDevice) {
		List<DeviceAttribute> deviceAttributes = tempDevice.getAttributes();
		if (deviceAttributes != null && deviceAttributes.size() != 0) {
			for (DeviceAttribute deviceAttribute : deviceAttributes) {
				String name = deviceAttribute.getName();
				String value = deviceAttribute.getValue();
				if (AxalentUtils.ATTRIBUTE_CYCLE.equals(name)) {
					optionSpinner.setSelection(getPosition(value));
				} else if (AxalentUtils.ATTRIBUTE_DATETIME.equals(name)) {
					dateTxt.setText(TextUtils.isEmpty(value) ? AxalentUtils.getSystemTime() : AxalentUtils.gmtToLoLocal(value));
				} else if (AxalentUtils.ATTRIBUTE_ENABLE.equals(name)) {
					if (TextUtils.isEmpty(value)){  
						value = AxalentUtils.OFF;
					}
					enableSwitch.setChecked(AxalentUtils.ON.equals(value) ? true : false);
				} else if (AxalentUtils.ATTRIBUTE_CUSTOM_NAME.equals(name)) {
					nameTxt.setText(TextUtils.isEmpty(value) ? aty.getCurrentDevice().getDevName() : value);
				}
			}
		} else {
			dateTxt.setText(AxalentUtils.getSystemTime());
			nameTxt.setText(aty.getCurrentDevice().getDevName());
		}
	}
	
	private void updateSchedule() {
		String datetime = dateTxt.getText().toString().trim();
		String enable = enableSwitch.isEnabled() ? AxalentUtils.ON : AxalentUtils.OFF;
		List<DeviceAttribute> deviceAttributes = new ArrayList<DeviceAttribute>();
		deviceAttributes.add(new DeviceAttribute("", AxalentUtils.ATTRIBUTE_CYCLE, cycle, ""));
		deviceAttributes.add(new DeviceAttribute("", AxalentUtils.ATTRIBUTE_DATETIME, AxalentUtils.localToGmt(datetime), ""));
		deviceAttributes.add(new DeviceAttribute("", AxalentUtils.ATTRIBUTE_ENABLE, enable, ""));
		aty.getDeviceManager().setMultiDeviceAttributes2(aty.getCurrentDevice().getDevId(), deviceAttributes, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				ToastUtils.show(R.string.update_success);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				ToastUtils.show(R.string.update_failure);
			}
		});
	}
	
	private int getPosition(String value) {
		if (!TextUtils.isEmpty(value)) {
			if ("Never".equals(value)) {
				return 0;
			} else if ("Day".equals(value)) {
				return 1;
			} else if ("Week".equals(value)) {
				return 2;
			} else if ("Month".equals(value)) {
				return 3;
			} else if ("Year".equals(value)) {
				return 4;
			}
		}
		return 0;
	}
	
	private void initChildDatas() {
		final String scheduleId = aty.getCurrentDevice().getDevId();
		List<Trigger> scheduleTriggers = CacheUtils.getTriggersByDevId(scheduleId);
		if (scheduleTriggers != null && scheduleTriggers.size() != 0) {
			for (Trigger trigger : scheduleTriggers) {
				Group group = getChildDevice(trigger);
				if (group != null) {
					childDevices.add(group);
				}
			}
			setAdapter();
		}
		aty.getLoadingDialog().close();
	}
	
	private Group getChildDevice(Trigger trigger) {
		List<TriggerAttribute> triggerAttributes = trigger.getTriggerAttributes();
		if (triggerAttributes != null && triggerAttributes.size() != 0) {
			final String devId = triggerAttributes.get(0).getDeviceID();
			Device device = CacheUtils.getDeviceByDevId(devId);
			if (device != null) {
				Group group = (Group) AxalentUtils.copyDevice(device, new Group());
				String value = triggerAttributes.get(0).getValue();
				group.setToggle((value == null) ? AxalentUtils.OFF : value);
				group.setTrigger(trigger);
				return group;
			}
		}
		return null;
	}
	

	@Override
	public void onMenuItemClick(int arg0, SwipeMenu arg1, int arg2) {
		if (AxalentUtils.getLoginMode() != R.id.atyLoginCloudBtn) {
			ToastUtils.show(R.string.please_change_to_cloud_mode);
			return;
		}
		deleteItemDevice((Group) childDevices.get(arg0));
	}
	
	private void deleteItemDevice(final Device device) {
		final Group group = (Group) device;
		final String triggerId = group.getTrigger().getTriggerId();
		aty.getDeviceManager().removeTrigger(triggerId, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				CacheUtils.deleteTriggerByTriggerId(triggerId);
				childDevices.remove(device);
				adapter.notifyDataSetChanged();
				ToastUtils.show(R.string.delete_success);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				ToastUtils.show(R.string.delete_failure);
			}
		});
	}
	
	private void updateItemDevice(final Group group, final String value) {
		if (AxalentUtils.TYPE_AXALENT_SCENE.equalsIgnoreCase(group.getTypeName())) {
			ToastUtils.show(R.string.temporary_not_support_update_scene);
			return;
		}
		final Trigger trigger = AxalentUtils.copyTrigger(group.getTrigger(), new Trigger());
		final List<TriggerAttribute> triggerAttributes = trigger.getTriggerAttributes();
		for (TriggerAttribute triggerAttribute : triggerAttributes) {
			triggerAttribute.setValue(value);
		}
		trigger.setTriggerAttributes(triggerAttributes);
		aty.getDeviceManager().updateTrigger(trigger, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				CacheUtils.updateTriggerAttributeByTriggerId(trigger.getTriggerId(), triggerAttributes);
				group.getTrigger().setTriggerAttributes(triggerAttributes);
				group.setToggle(value);
				ToastUtils.show(R.string.update_success);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				ToastUtils.show(R.string.update_failure);
			}
		});
	}
	
	private void setAdapter() {
		if (adapter == null) {
			adapter = new ShowGroupAdapter(aty, childDevices);
			adapter.setOnChangeStateListener(this);
			listView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public ArrayList<Device> getChildDevices() {
		return childDevices;
	}

	@Override
	public void onChangeState(int position, String value) {
		if (AxalentUtils.getLoginMode() != R.id.atyLoginCloudBtn) {
			ToastUtils.show(R.string.please_change_to_cloud_mode);
			return;
		}
		updateItemDevice((Group) childDevices.get(position), value);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		cycle = parent.getItemAtPosition(position).toString();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refresh() {
		childDevices.clear();
		initChildDatas();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.fragShowScheduleUpdateBtn:
			updateSchedule();
			break;
		case R.id.fragShowScheduleDateTxt:
			showDateDialog(id);
			break;
		case R.id.fragShowScheduleDevNameTxt:
			aty.showAlterNameDialog();
			break;
		}
	}
	
	private void showDateDialog(int id) {
		if (dateDialog == null) {
			dateDialog = new DateDialog(aty);
			dateDialog.setOnSelectedDateListener(new DateDialog.OnSelectedDateListener() {
				@Override
				public void onSelectedDate(int currentSelectId, String date) {
					dateTxt.setText(date);
				}
			});
		}
		dateDialog.setCurrentSelectId(id);
		dateDialog.show();
	}
	
	@Override
	public void alterGroupName(String newName) {
		nameTxt.setText(newName);
	}
	
}
