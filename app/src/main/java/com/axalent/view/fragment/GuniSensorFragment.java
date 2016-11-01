package com.axalent.view.fragment;

import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.axalent.R;
import com.axalent.view.activity.ShowDeviceActivity;
import com.axalent.application.MyCacheData;
import com.axalent.model.Device;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class GuniSensorFragment extends Fragment implements View.OnClickListener, OnMenuItemClickListener{
	
	private final String regularExpression = "^[0-9]*[1-9][0-9]*$";
	private ShowDeviceActivity parent = null;
	private EditText timeEdit = null;
	private Button confirmBtn = null;
	private List<Device> devices = new ArrayList<Device>();
	private SwipeMenuListView deviceList = null;
	private DeviceAdapter adapter = null;
	private String guBind = null;
	
	@Override
	public void onAttach(Activity activity) {
		parent = (ShowDeviceActivity) activity;
		super.onAttach(activity);
	}
	
	@Override
	public void onResume() {
		String devId = parent.getCurrentDevice().getDevId();
		Device dev = CacheUtils.getDeviceByDevId(devId);
		if (dev != null) {
			loadTriggerDevices(dev);
		} else {
			loadTriggerDevices(parent.getCurrentDevice());
		}
		super.onResume();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_guni_sensor, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		timeEdit = (EditText) view.findViewById(R.id.edit_time);
		confirmBtn = (Button) view.findViewById(R.id.btn_confirm);
		deviceList = (SwipeMenuListView) view.findViewById(R.id.list_devices);
		deviceList.setOnMenuItemClickListener(this);
		deviceList.setMenuCreator(getMenuCreator());
		adapter = new DeviceAdapter();
		deviceList.setAdapter(adapter);
		confirmBtn.setOnClickListener(this);
		timeEdit.setText(AxalentUtils.getDeviceAttributeValue(parent.getCurrentDevice(), AxalentUtils.ATTRIBUTE_GU_ONTIME));
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
	
	
	private void setupTime() {
		String time = timeEdit.getText().toString().trim();
		if (time == null || "".equals(time)) {
			ToastUtils.show(R.string.please_enter_time);
			return;
		}
		if (!time.matches(regularExpression)) {
			ToastUtils.show(R.string.format_error);
			return;
		}
		parent.getLoadingDialog().show(R.string.please_wait);
		parent.getDeviceManager().setDeviceAttribute(
				parent.getCurrentDevice().getDevId(), 
				AxalentUtils.ATTRIBUTE_GU_ONTIME, 
				time, 
				new Listener<XmlPullParser>() {
					@Override
					public void onResponse(XmlPullParser arg0) {
						parent.getLoadingDialog().close();
						ToastUtils.show(R.string.success);
					}
				}, 
				new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						parent.getLoadingDialog().close();
						ToastUtils.show(R.string.failure);
					}
				});
	}
	
	private void loadTriggerDevices(Device dev) {
		guBind = AxalentUtils.getDeviceAttributeValue(dev, AxalentUtils.ATTRIBUTE_GU_BIND);
		if (guBind.equals("")) {
			return;
		}
		LogUtils.i("loadTriggerDevices");
		List<String> guBinds = new ArrayList<String>();
		StringBuilder guIds = new StringBuilder();
		char[] guBindArray = guBind.toCharArray();
		for (int i = 0; i < guBindArray.length; i++) {
			guIds.append(guBindArray[i]);
			if (guIds.length() == 4) {
				guBinds.add(guIds.toString());
				guIds.setLength(0);
			}
		}
		
		devices.clear();
		
		List<Device> cacheDevices = MyCacheData.getDevices();
		for (Device device : cacheDevices) {
			String typeName = device.getTypeName();
			if (typeName.equalsIgnoreCase(AxalentUtils.TYPE_GUNI_LAMP)
					|| typeName.equalsIgnoreCase(AxalentUtils.TYPE_GUNI_PLUG)
//					|| typeName.equalsIgnoreCase(AxalentUtils.TYPE_SENSOR_PIR)
//					|| typeName.equalsIgnoreCase(AxalentUtils.TYPE_SENSOR_LIGHT)
					|| typeName.equalsIgnoreCase(AxalentUtils.TYPE_GUNI_SWITCH)) {
				String guId1 = AxalentUtils.getDeviceAttributeValue(device, AxalentUtils.ATTRIBUTE_GU_ID);
				if (guId1.equals("")) continue;
				for (String guId2 : guBinds) {
					if (guId2.equals(guId1)) {
						devices.add(device);
					}
				}
			}
		}
		
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			// setup on time
			setupTime();
			break;

		default:
			break;
		}
		
	}
	
	private class DeviceAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			return devices.size();
		}

		@Override
		public Object getItem(int position) {
			return devices.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		
		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			ViewHolder holder = null;
			if(convertView == null) {
				convertView = LayoutInflater.from(parent).inflate(R.layout.adapter_gateway_config, null);
				holder = new  ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Device device = devices.get(position);
			
			holder.bgImg.setImageResource(AxalentUtils.getDeviceImageByTypeName(device.getTypeName()));
			holder.nameTxt.setText(device.getCustomName());
			
			return convertView;
		}
		
		private class ViewHolder {
			
			ImageView bgImg;
			TextView nameTxt;
			
			public ViewHolder(View view) {
				bgImg = (ImageView) view.findViewById(R.id.adapterGatewaySetupBgImg);
				nameTxt = (TextView) view.findViewById(R.id.adapterGatewaySetupNameTxt);
			}
		}
	}

	@Override
	public void onMenuItemClick(final int arg0, SwipeMenu arg1, int arg2) {
		Device device = devices.get(arg0);
		String guId1 = AxalentUtils.getDeviceAttributeValue(device, AxalentUtils.ATTRIBUTE_GU_ID);
		LogUtils.i("guId1:"+guId1);
		String result = guBind.replace(guId1, "");
		LogUtils.i("new guBind:"+result);
		
		parent.getLoadingDialog().show(R.string.please_wait);
		parent.getDeviceManager().setDeviceAttribute(
				parent.getCurrentDevice().getDevId(), 
				AxalentUtils.ATTRIBUTE_GU_BIND, 
				result, 
				new Listener<XmlPullParser>() {
					@Override
					public void onResponse(XmlPullParser pull) {
						devices.remove(arg0);
						adapter.notifyDataSetChanged();
						parent.getLoadingDialog().close();
						ToastUtils.show(R.string.success);
					}
				}, 
				new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						parent.getLoadingDialog().close();
						ToastUtils.show(R.string.failure);
					}
				});
	}
	

}
