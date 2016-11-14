/**
 * File Name                   : ������������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.adapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.axalent.R;
import com.axalent.application.MyCacheData;
import com.axalent.model.HorizontalCSRDeviceData;
import com.axalent.model.data.model.devices.CSRDevice;
import com.axalent.presenter.csrapi.MeshLibraryManager;
import com.axalent.view.activity.HomeActivity;
import com.axalent.view.activity.ShowDeviceActivity;
import com.axalent.presenter.controller.DeviceManager;
import com.axalent.model.Device;
import com.axalent.model.HorizontalData;
import com.axalent.util.AxalentUtils;
import com.axalent.util.ToastUtils;
import com.axalent.view.widget.HorizontalList;
import com.axalent.view.widget.HorizontalList.OnScrollListener;
import com.csr.csrmesh2.LightModelApi;
import com.csr.csrmesh2.PowerModelApi;
import com.csr.csrmesh2.PowerState;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainListAdapter extends BaseAdapter implements OnScrollListener, OnItemClickListener, OnItemLongClickListener {

	private List<HorizontalData> horizontalDatas;
	private List<HorizontalCSRDeviceData> horizontalCSRDeviceDatas;
	private MyScrollListener myScrollListener;
	private DeviceManager dm;
	private Context ctx;

	public void setMyScrollListener(MyScrollListener myScrollListener) {
		this.myScrollListener = myScrollListener;
	}
	
	public MainListAdapter(DeviceManager dm, Context ctx, List<HorizontalData> horizontalDatas) {
		this.dm = dm;
		this.ctx = ctx;
		this.horizontalDatas = horizontalDatas;
	}

	public MainListAdapter(Context ctx, List<HorizontalCSRDeviceData> horizontalDatas) {
		this.ctx = ctx;
		this.horizontalCSRDeviceDatas = horizontalDatas;
	}
	
	@Override
	public int getCount() {
		if (((HomeActivity)ctx).isBluetoothMode()) {
			if (horizontalCSRDeviceDatas != null) {
				return horizontalCSRDeviceDatas.size();
			}
		} else {
			if (horizontalDatas != null) {
				return horizontalDatas.size();
			}
		}
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		if (((HomeActivity)ctx).isBluetoothMode()) {
			return horizontalCSRDeviceDatas.get(arg0);
		}
		return horizontalDatas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ViewHolder vh = null;
		if (arg1 == null) {
			arg1 = View.inflate(ctx, R.layout.adapter_main_list, null);
			vh = new ViewHolder(arg1);
			arg1.setTag(vh);
		} else {
			vh = (ViewHolder) arg1.getTag();
		}
		setResource(vh, arg0);
		return arg1;
	}
	
	private class ViewHolder {
		
		HorizontalList horiListView;
		TextView txt;
		
		public ViewHolder(View view) {
			this.horiListView = (HorizontalList) view.findViewById(R.id.adapterMainListView);
			this.txt = (TextView) view.findViewById(R.id.adapterMainNameTxt);
			this.horiListView.setOnScrollListener(MainListAdapter.this);
			this.horiListView.setOnItemClickListener(MainListAdapter.this);
			this.horiListView.setOnItemLongClickListener(MainListAdapter.this);
		}
		
	}
	
	private void setResource(ViewHolder vh, final int position) {
		if (((HomeActivity)ctx).isBluetoothMode()) {
			HorizontalCSRDeviceData csrData = horizontalCSRDeviceDatas.get(position);
			HorizontalList horizontalList = vh.horiListView;
			horizontalList.setTag(position);
			horizontalList.setSelectionFromOffset(csrData.getIndex(), csrData.getOffSet());
			List<CSRDevice> devices = csrData.getDevices();
			MainHoriListAdapter adapter = (MainHoriListAdapter) csrData.getAdapter();
			if (adapter == null) {
				adapter = new MainHoriListAdapter(ctx,devices);
				horizontalList.setAdapter(adapter);
				csrData.setAdapter(adapter);
			} else {
				horizontalList.setAdapter(adapter);
			}
			vh.txt.setText(devices.size() != 0 ? AxalentUtils.getCSRDeviceType(devices.get(0).getType()) : "");
		} else {
			HorizontalData data = horizontalDatas.get(position);
			HorizontalList horizontalList = vh.horiListView;
			horizontalList.setTag(position);
			horizontalList.setSelectionFromOffset(data.getIndex(), data.getOffSet());
			List<Device> devices = data.getDevices();
			MainHoriListAdapter adapter = (MainHoriListAdapter) data.getAdapter();
			if (adapter == null) {
				adapter = new MainHoriListAdapter(ctx, dm, devices);
				horizontalList.setAdapter(adapter);
				data.setAdapter(adapter);
			} else {
				horizontalList.setAdapter(adapter);
			}
			vh.txt.setText(devices.get(0).getDisplayName());
		}
	}
	

	@Override
	public void onScrollStateChanged(HorizontalList view, int scrollState) {
		
		if (myScrollListener != null) {
			myScrollListener.onScroll(scrollState);
		}
		
		switch (scrollState) {
		case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
			// stop
			break;
		case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			// scroll
			int index = view.getFirstVisiblePosition();
			View v = view.getChildAt(0);
			int left = (v == null) ? 0 : v.getLeft();

			if (((HomeActivity)ctx).isBluetoothMode()) {
				HorizontalCSRDeviceData CSRData = horizontalCSRDeviceDatas.get((Integer) view.getTag());
				CSRData.setIndex(index);
				CSRData.setOffSet(left);
			} else {
				HorizontalData data = horizontalDatas.get((Integer) view.getTag());
				data.setIndex(index);
				data.setOffSet(left);
			}

			break;
		case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
			// 
			break;
		}
	}

	@Override
	public void onScroll(HorizontalList view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (((HomeActivity)ctx).isBluetoothMode()) {
			CSRDevice csrDevice = horizontalCSRDeviceDatas.get((Integer) arg0.getTag()).getDevices().get(arg2);
			controlCSRDevice(csrDevice, arg1);
		} else {
			Device device = horizontalDatas.get((Integer) arg0.getTag()).getDevices().get(arg2);
			String state = device.getState();
			if (AxalentUtils.ONLINE.equals(state)) {
				new ControlDevice(arg1, device);
			} else if (AxalentUtils.OFFLINE.equals(state)) {
				Toast.makeText(ctx, R.string.device_offline, Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (((HomeActivity)ctx).isBluetoothMode()) {
			CSRDevice csrDevice = horizontalCSRDeviceDatas.get((Integer) arg0.getTag()).getDevices().get(arg2);
			Log.i("type", "onItemLongClick: " + csrDevice.getType());
			if (csrDevice.getType() == CSRDevice.TYPE_LIGHT ||
					csrDevice.getType() == CSRDevice.TYPE_GATEWAY) {

				skipToShowDevice(csrDevice.getId());

			} else if (csrDevice.getType() == CSRDevice.TYPE_TEMPERATURE) {

				sensorDetail(csrDevice);

			}
		} else {
			Device device = horizontalDatas.get((Integer) arg0.getTag()).getDevices().get(arg2);
			skipToShowDevice(device);
		}
		return true;
	}

	private void skipToShowDevice(int id) {
		HomeActivity activity = (HomeActivity) ctx;
		Intent intent = new Intent(ctx, ShowDeviceActivity.class);
		intent.putExtra(AxalentUtils.KEY_ID_DATABASE_DEVICE, id);
		intent.putExtra(AxalentUtils.GROUP_OR_SING, AxalentUtils.SING);
		activity.startActivityForResult(intent,AxalentUtils.REFRESH_DATA);
	}

	private void skipToShowDevice(int id, int areaId) {
		HomeActivity activity = (HomeActivity) ctx;
		Intent intent = new Intent(ctx, ShowDeviceActivity.class);
		intent.putExtra(AxalentUtils.KEY_ID_DATABASE_DEVICE, id);
		intent.putExtra(AxalentUtils.KEY_ID_DATABASE_AREA, areaId);
		intent.putExtra("temperature",activity.getTemperature());
		intent.putExtra(AxalentUtils.GROUP_OR_SING, AxalentUtils.SING);
		activity.startActivityForResult(intent,AxalentUtils.REFRESH_DATA);
	}
	
	private void skipToShowDevice(Device device) {
		HomeActivity activity = (HomeActivity) ctx;
		Intent intent = new Intent(ctx, ShowDeviceActivity.class);
		intent.putExtra(AxalentUtils.KEY_DEVICE, device);
//		activity.startActivityForResult(intent, AxalentUtils.REFRESH_DATA);
		activity.startActivity(intent);
	}

	private void controlCSRDevice(CSRDevice csrDevice, View view) {
		if (MeshLibraryManager.isBluetoothBridgeReady()) {
			if (csrDevice.getType() == CSRDevice.TYPE_LIGHT) {
				int state = MyCacheData.getInstance().getLightState(csrDevice.getDeviceID()) != null ? MyCacheData.getInstance().getLightState(csrDevice.getDeviceID()) : 1;
				ImageView deviceImage = (ImageView) view.findViewById(R.id.adapterMainDeviceBgImg);
				PowerState powerState = state % 2 == 0 ? PowerState.OFF : PowerState.ON;
				int iconId = state % 2 == 0 ? R.drawable.light_off : R.drawable.light_on;
				int tempState = state % 2 == 0 ? 1 : 0;
				PowerModelApi.setState(csrDevice.getDeviceID(), powerState, true);
				MyCacheData.getInstance().setState(csrDevice.getDeviceID(), powerState);
				deviceImage.setImageResource(iconId);
				MyCacheData.getInstance().setLightState(csrDevice.getDeviceID(), tempState);
			} else if (csrDevice.getType() == CSRDevice.TYPE_TEMPERATURE) {
				sensorControl(csrDevice);
			}
		} else {
			Toast.makeText(ctx, ctx.getString(R.string.bluetooth_needed_to_control_devices), Toast.LENGTH_SHORT).show();
			MeshLibraryManager.getInstance().setBluetoothChannelEnabled();
		}
	}

	private void sensorControl(CSRDevice csrDevice) {
		List<Integer> groups = csrDevice.getGroupsList();
		Set<Integer> hs = new HashSet<>();
		hs.addAll(groups);
		hs.remove(0);
		if (hs.isEmpty()) {
			Toast.makeText(ctx, "The device must to belong to a group before being able to be controlled.", Toast.LENGTH_SHORT).show();
			return;
		}
		else {
			skipToShowDevice(csrDevice.getId(), hs.iterator().next());
		}
	}

	private void sensorDetail(CSRDevice csrDevice) {
		skipToShowDevice(csrDevice.getId(), csrDevice.getId());
	}

	private class ControlDevice {

		View view;
		Device device;
		String typeName;
		
		public ControlDevice(View view, Device device) {
			this.view = view;
			this.device = device;
			start();
		}
		
		private void start() {
			typeName = device.getTypeName();
			if (AxalentUtils.TYPE_SL.equalsIgnoreCase(typeName)) {
				// Light Bulb
				setDeviceAttribute(AxalentUtils.ATTRIBUTE_LIGHT, getValue(), new int[]{R.drawable.light_off, R.drawable.light_on});
			} else if (AxalentUtils.TYPE_GUNI_LAMP.equalsIgnoreCase(typeName)) {
				//  GU LIGHT
				setDeviceAttribute(AxalentUtils.ATTRIBUTE_GU_LIGHT, getValue(), new int[]{R.drawable.light_off, R.drawable.light_on});
			} else if (AxalentUtils.TYPE_SSMOKE.equalsIgnoreCase(typeName)) {
				// Smoke Sensor
//				setDeviceAttribute(AxalentUtils.ATTRIBUTE_SMOKE, getValue(), new int[]{R.drawable.smoke_off, R.drawable.smoke_on});
			} else if (AxalentUtils.TYPE_POWER_PLUG.equalsIgnoreCase(typeName)) {
				// Power Plug
				setDeviceAttribute(AxalentUtils.ATTRIBUTE_MYSWITCH, getValue(), new int[]{R.drawable.plug_off, R.drawable.plug_on});
			} else if (AxalentUtils.TYPE_GUNI_PLUG.equalsIgnoreCase(typeName)) {
				// GUNI PLUG
				setDeviceAttribute(AxalentUtils.ATTRIBUTE_GU_SWITCH, getValue(), new int[]{R.drawable.plug_off, R.drawable.plug_on});
			} else if (AxalentUtils.TYPE_WINDOW_COVER.equalsIgnoreCase(typeName)) {
				// Window Cover
				setDeviceAttribute(AxalentUtils.ATTRIBUTE_COVER, getValue(), new int[]{R.drawable.shade_close, R.drawable.shade_open});
			} else if (AxalentUtils.TYPE_SWITCH_TWO.equalsIgnoreCase(typeName)) {
				// Two-way Switch
				for (int i = 0; i < 2; i++) {
					setDeviceAttribute((i == 0) ? AxalentUtils.ATTRIBUTE_SWITCH_0 : AxalentUtils.ATTRIBUTE_SWITCH_1, getValue(), new int[]{	R.drawable.switch_two_off, R.drawable.switch_two_on});
				}
			} else if (AxalentUtils.TYPE_VALVE.equalsIgnoreCase(typeName)) {
				// axa_valve
				setDeviceAttribute(AxalentUtils.ATTRIBUTE_VALVE, getValue(), new int[]{R.drawable.valve_closed, R.drawable.valve_open});
			}
		}
		
		private void setDeviceAttribute(final String name, final String value, final int[] imgs) {
			dm.setDeviceAttribute(device.getDevId(), name, value, new Listener<XmlPullParser>() {
				@Override
				public void onResponse(XmlPullParser response) {
					ImageView deviceImage = (ImageView) view.findViewById(R.id.adapterMainDeviceBgImg);
					deviceImage.setImageResource(getDeviceImageId(value, imgs));
					device.setToggle(value);
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					ToastUtils.show(R.string.action_failure);
				}
			});
		}

		private String getValue() {
			String value = device.getToggle();
			if (AxalentUtils.TYPE_WINDOW_COVER.equalsIgnoreCase(typeName)) {
				return "99".contains(value) ? "0" : "99";
			}
			return AxalentUtils.ON.equals(value) ? AxalentUtils.OFF : AxalentUtils.ON;
		}
		
		private int getDeviceImageId(String value, int[] imgs) {
			if (AxalentUtils.ON.equals(value) || "99".contains(value)) {
				return imgs[1]; 
			}
			return imgs[0];
		}
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		if (((HomeActivity)ctx).isBluetoothMode()) {
			if (horizontalCSRDeviceDatas != null) {
				for (HorizontalCSRDeviceData horizontalCSRDeviceData : horizontalCSRDeviceDatas) {
					BaseAdapter baseAdapter = horizontalCSRDeviceData.getAdapter();
					if (baseAdapter != null) {
						baseAdapter.notifyDataSetChanged();
					}
				}
			}
		} else {
			if (horizontalDatas != null) {
				for (HorizontalData horizontalData : horizontalDatas) {
					BaseAdapter baseAdapter = horizontalData.getAdapter();
					if (baseAdapter != null) {
						baseAdapter.notifyDataSetChanged();
					}
				}
			}
		}
	}
	
	public interface MyScrollListener {
		void onScroll(int state);
	}

}
