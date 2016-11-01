/**
 * File Name                   : ��ʾscene ����
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.axalent.R;
import com.axalent.view.activity.ShowGroupActivity;
import com.axalent.view.activity.ShowGroupActivity.GroupManage;
import com.axalent.adapter.ShowGroupAdapter;
import com.axalent.adapter.ShowGroupAdapter.OnChangeStateListener;
import com.axalent.model.Device;
import com.axalent.model.Group;
import com.axalent.model.Trigger;
import com.axalent.model.TriggerAttribute;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;
import com.axalent.util.LogUtils;
import com.axalent.util.ToastUtils;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.TextView;

public class ShowSceneFragment extends Fragment implements GroupManage, OnMenuItemClickListener, OnChangeStateListener{

	private View view;
	private ShowGroupActivity aty;
	private TextView headerViewTxt;
	private ShowGroupAdapter adapter;
	private SwipeMenuListView listView;
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
			view = inflater.inflate(R.layout.fragment_show_scene, null);
			initView();
			initDatas();
		}
		return view;
	} 
	
	private void initView() {
		listView = (SwipeMenuListView) view.findViewById(R.id.fragShowSceneListView);
		listView.setOnMenuItemClickListener(this);
		listView.addHeaderView(getHeaderView());
		listView.setMenuCreator(getMenuCreator());
	}
	
	private View getHeaderView() {
		headerViewTxt = new TextView(getActivity());
		headerViewTxt.setGravity(Gravity.CENTER);
		headerViewTxt.setTextColor(Color.WHITE);
		headerViewTxt.setBackgroundColor(Color.BLACK);
		headerViewTxt.setOnClickListener(onClickListener);
		headerViewTxt.setText(aty.getCurrentDevice().getCustomName());
		headerViewTxt.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.list_header_width)));
		return headerViewTxt;
	}
	
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			aty.showAlterNameDialog();
		}
		
	};
	
	private SwipeMenuCreator getMenuCreator() {
		return new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
				deleteItem.setWidth(AxalentUtils.dp2px(getActivity(), 90));
				deleteItem.setIcon(R.drawable.delete);
				menu.addMenuItem(deleteItem);
			}
		};
	}
	
	private void initDatas() {
		final String sceneId = aty.getCurrentDevice().getDevId();
		List<Trigger> sceneTriggers = CacheUtils.getTriggersByDevId(sceneId);
		if (sceneTriggers != null && sceneTriggers.size() != 0) {
			for (Trigger trigger : sceneTriggers) {
				List<TriggerAttribute> triggerAttributes = trigger.getTriggerAttributes();
				if (triggerAttributes != null && triggerAttributes.size() != 0) {
					Device device = CacheUtils.getDeviceByDevId(triggerAttributes.get(0).getDeviceID());
					if (device != null) {
						if (AxalentUtils.TYPE_GATEWAY_SCENE.equalsIgnoreCase(device.getTypeName())) {
							parserVirtualScene(device);
						} else {
							copyDeviceToList(device, null, trigger, triggerAttributes.get(0).getValue());
						}	
					}
				}
			}
		}
		setAdapter();
	}
	
	
	private void parserVirtualScene(Device virtualScene) {
		try {
			String devInfo = AxalentUtils.getDeviceAttributeValue(virtualScene, AxalentUtils.ATTRIBUTE_DEVINFO);
			LogUtils.i("devInfo:"+devInfo);
			if (!TextUtils.isEmpty(devInfo)) {
				JSONArray arrays = new JSONArray(devInfo);
				for (int i = 0; i < arrays.length(); i++) {
					JSONObject obj = arrays.getJSONObject(i);
					String code = obj.optString(AxalentUtils.ATTRIBUTE_CODE);
					if (!TextUtils.isEmpty(code)) {
						Device childDevice = CacheUtils.getDeviceByName(code);
						if (childDevice != null) {
							String key = getToggleKeys(childDevice.getTypeName())[0];
							if (!TextUtils.isEmpty(key)) {
								copyDeviceToList(childDevice, virtualScene, null, obj.optString(key));
							}
						}
					}
				}
			}
		} catch (JSONException e) {
			
		}
	}
	
	private void copyDeviceToList(Device device, Device virtualScene, Trigger trigger, String toggle) {
		Group copy = (Group) AxalentUtils.copyDevice(device, new Group());
		copy.setToggle(TextUtils.isEmpty(toggle) ? AxalentUtils.OFF : toggle);
		copy.setDevice(virtualScene);
		copy.setTrigger(trigger);
		childDevices.add(copy);
	}
	
	
	private void setAdapter() {
		if (childDevices != null) {
			LogUtils.i("size:" + childDevices.size());
		}
		if (adapter == null) {
			adapter = new ShowGroupAdapter(aty, childDevices);
			adapter.setOnChangeStateListener(this);
			listView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onMenuItemClick(final int arg0, SwipeMenu arg1, int arg2) {
		if (AxalentUtils.getLoginMode() != R.id.atyLoginCloudBtn) {
			ToastUtils.show(R.string.please_change_to_cloud_mode);
			return;
		}
		deleteItemDevice((Group) childDevices.get(arg0));
	}
	
	private void deleteItemDevice(Group group) {
		if (group.getDevice() != null) {
			deleteDataToDevInfo(group);
		} else {
			deleteTrigger(group, group.getTrigger().getTriggerId());
		}
	}
	
	private void updateDataToDevInfo(Group group, String value) {
		String newDevInfo = "";
		try {
			String devInfo = AxalentUtils.getDeviceAttributeValue(group.getDevice(), AxalentUtils.ATTRIBUTE_DEVINFO);
			JSONArray arrays = new JSONArray(devInfo);
			final int length = arrays.length();
			for (int i = 0; i < length; i++) {
				JSONObject object = arrays.getJSONObject(i);
				String code = object.optString(AxalentUtils.ATTRIBUTE_CODE);
				if (TextUtils.isEmpty(code) || !group.getDevName().equals(code)) {
					continue;
				}
				String[] keys = getToggleKeys(group.getTypeName());
				for (int j = 0; j < keys.length; j++) {
					object.put(keys[j], value);
				}
				newDevInfo = arrays.toString();
				break;
			}
			setDeviceAttribute(group, AxalentUtils.ATTRIBUTE_ADD_DEVICE, newDevInfo, false);
		} catch (JSONException e) {
		}
	}
	
	private void deleteDataToDevInfo(Group group) {
		String newDevInfo = "";
		try {
			String devInfo = AxalentUtils.getDeviceAttributeValue(group.getDevice(), AxalentUtils.ATTRIBUTE_DEVINFO);
			JSONArray arrays = new JSONArray(devInfo);
			final int length = arrays.length();
			for (int i = 0; i < length; i++) {
				JSONObject object = arrays.getJSONObject(i);
				String code = object.optString(AxalentUtils.ATTRIBUTE_CODE);
				if (TextUtils.isEmpty(code)) {
					continue;
				}
				if (group.getDevName().equals(code)) {
					arrays.remove(i);
					newDevInfo = arrays.toString();
					setDeviceAttribute(group, AxalentUtils.ATTRIBUTE_DEVINFO, newDevInfo, true);
					break;
				}
			}
		} catch (Exception e) {
			ToastUtils.show(R.string.delete_failure);
		}
	}
	
	
	private void setDeviceAttribute(final Group group, final String key, final String val, final boolean isDelete) {
		final String devId = group.getDevice().getDevId();
		aty.getDeviceManager().setDeviceAttribute(devId, key, val, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				CacheUtils.updateDeviceAttibuteByDevId(devId, key, val);
				if (isDelete) {
					childDevices.remove(group);
					adapter.notifyDataSetChanged();
					ToastUtils.show(R.string.delete_success);
				} else {
					ToastUtils.show(R.string.update_success);
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (isDelete) {
					ToastUtils.show(R.string.delete_failure);
				} else {
					ToastUtils.show(R.string.update_failure);
				}
			}
		});
	}
	
	
	private void deleteTrigger(final Device device, final String triggerId) {
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
	
	private void updateItemDevice(Group group, String value) {
		if (group.getDevice() != null) {
			updateDataToDevInfo(group, value);
		} else {
			updateTrigger(group, value);
		}
	}
	
	private void updateTrigger(final Group group, final String value) {
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
	
	private String[] getToggleKeys(String typeName) {
		if (typeName != null) {
			if (AxalentUtils.TYPE_SL.equalsIgnoreCase(typeName)) {
				return new String[] {AxalentUtils.ATTRIBUTE_LIGHT};
			} else if (AxalentUtils.TYPE_WINDOW_COVER.equalsIgnoreCase(typeName)) {
				return new String[] {AxalentUtils.ATTRIBUTE_COVER};
			} else if (AxalentUtils.TYPE_POWER_PLUG.equalsIgnoreCase(typeName)) {
				return new String[] {AxalentUtils.ATTRIBUTE_MYSWITCH};
			} else if (AxalentUtils.TYPE_SWITCH_TWO.equalsIgnoreCase(typeName)) {
				return new String[] {AxalentUtils.ATTRIBUTE_SWITCH_0, AxalentUtils.ATTRIBUTE_SWITCH_1};
			} else if (AxalentUtils.TYPE_GUNI_LAMP.equalsIgnoreCase(typeName)) {
				return new String[] {AxalentUtils.ATTRIBUTE_GU_LIGHT};
			} else if (AxalentUtils.TYPE_GUNI_PLUG.equalsIgnoreCase(typeName)) {
				return new String[] {AxalentUtils.ATTRIBUTE_GU_SWITCH};
			}
		}
		return new String[] {""};
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
	public void refresh() {
		childDevices.clear();
		initDatas();
	}

	@Override
	public void alterGroupName(String newName) {
		headerViewTxt.setText(newName);
	}
	
}
