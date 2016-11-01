/**
 * File Name                   : Trigger ����
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
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.axalent.R;
import com.axalent.view.activity.ShowTriggerActivity;
import com.axalent.adapter.ShowGroupAdapter;
import com.axalent.adapter.ShowGroupAdapter.OnChangeStateListener;
import com.axalent.model.Device;
import com.axalent.model.Group;
import com.axalent.model.Trigger;
import com.axalent.model.TriggerAttribute;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TriggerFragment extends Fragment implements OnChangeStateListener, OnMenuItemClickListener {

	private View view;
	private String tag;
	private SwipeMenuListView listView;
	private ShowTriggerActivity aty;
	private ShowGroupAdapter adapter;
	private ArrayList<Device> devices = new ArrayList<Device>();

	@Override
	public void onAttach(Activity activity) {
		aty = (ShowTriggerActivity) activity;
		super.onAttach(activity);
	}

	public TriggerFragment(String tag) {
		this.tag = tag + ".0";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_trigger, null);
			initView();
			initDatas();
		}
		return view;
	}

	private void initView() {
		listView = (SwipeMenuListView) view.findViewById(R.id.fragAddTriggerListView);
		listView.setOnMenuItemClickListener(this);
		listView.setMenuCreator(getMenuCreator());
	}

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

		List<Trigger> triggers = CacheUtils.getTriggersByDevId(aty.getCurrentDevice().getDevId());
		if (triggers != null && triggers.size() != 0) {
			if (aty.getCurrentDevice().getTypeName().equals(AxalentUtils.TYPE_HTS)) {
				for (Trigger trigger : triggers) {
					String attrName = trigger.getAttrName() + ".0";
					if (attrName.equals(tag)) {
						converAttributeDatas(trigger);
					}
				}
				
			} else {
				for (Trigger trigger : triggers) {
					String threshold = trigger.getThreshold();
					if (threshold.equals(tag)) {
						converAttributeDatas(trigger);
					}
				}
			}
			setAdapter();
		} 
	}

	public void refresh() {
		devices.clear();
		initDatas();
	}

	private void converAttributeDatas(Trigger trigger) {
		List<TriggerAttribute> triggerAttributes = trigger.getTriggerAttributes();
		if (triggerAttributes == null || triggerAttributes.size() == 0)
			return;
		Device device = CacheUtils.getDeviceByDevId(triggerAttributes.get(0).getDeviceID());
		if (device == null)
			return;
		Group group = (Group) AxalentUtils.copyDevice(device, new Group());
		group.setToggle(triggerAttributes.get(0).getValue());
		group.setTrigger(trigger);
		devices.add(group);
	}

	private void setAdapter() {
		if (adapter == null) {
			adapter = new ShowGroupAdapter(aty, devices, "com/axalent/test");
			adapter.setOnChangeStateListener(this);
			listView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onChangeState(final int position, final String value) {
		final Group group = (Group) devices.get(position);
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

	public ArrayList<Device> getDevices() {
		return devices;
	}

	@Override
	public void onMenuItemClick(int arg0, SwipeMenu arg1, int arg2) {
		deleteItemDevice(devices.get(arg0));
	}

	private void deleteItemDevice(final Device device) {
		Group group = (Group) device;
		final String triggerId = group.getTrigger().getTriggerId();
		aty.getDeviceManager().removeTrigger(triggerId, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				CacheUtils.deleteTriggerByTriggerId(triggerId);
				devices.remove(device);
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

}
