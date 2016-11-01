/**
 * File Name                   : �˵�������������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.axalent.R;
import com.axalent.util.AxalentUtils;
import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter {
	
	private int type;
	private Context ctx;
	private String[] items;
	private int loginMode = AxalentUtils.getLoginMode();
	private Map<Integer, String[]> itemMap = new HashMap<Integer, String[]>();
	
	public MenuAdapter(Context ctx, int type) {
		this.ctx = ctx;
		this.type = type;
		this.items = getItems();
	}

	@Override
	public int getCount() {
		return items.length;
	}

	@Override
	public Object getItem(int position) {
		return items[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			convertView = View.inflate(ctx, R.layout.adapter_menu, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.tv.setText(items[position]);
		if (loginMode != R.id.atyLoginBluetoothBtn) {
			if (position != 3 && loginMode != R.id.atyLoginCloudBtn) {
				vh.tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			}
		}
		return convertView;
	}
	
	
	@Override
	public void notifyDataSetChanged() {
		this.items = getItems();
		super.notifyDataSetChanged();
	}
	
	class ViewHolder {
		
		TextView tv;
		
		public ViewHolder(View view) {
			this.tv = (TextView) view.findViewById(R.id.menuListItemTxt);
		}
	}
	
	public void setPageType(int type) {
		if (this.type != type) {
			this.type = type;
			notifyDataSetChanged();
		}
	}
	
	private String[] getItems() {
		String[] items = itemMap.get(type);
		if (items == null || items.length == 0) {
			items = getResourcesArray();
			if (items == null) {
				items = new String[]{""};
			}
			itemMap.put(type, items);
		}
		return items;
	}
	
	
	private String[] getResourcesArray() {
		switch (type) {
		case R.id.atyHomeMainBtn:
			String[] items = ctx.getResources().getStringArray(R.array.array_menu_main);
			if (loginMode == R.id.atyLoginCloudBtn) {
				List<String> listItems = new ArrayList<String>();
				int length = items.length - 1;
				for (int i = 0; i < items.length; i++) {
					if (i != length) {
						listItems.add(items[i]);
					}
				}
				return listItems.toArray(new String[length]);
			}

			String[] blueModeItems = ctx.getResources().getStringArray(R.array.array_menu_main_blue_tooth);
			if (loginMode == R.id.atyLoginBluetoothBtn) {
				return blueModeItems;
			}

			return items;
		case R.id.atyHomeSceneBtn:
			if (loginMode == R.id.atyLoginBluetoothBtn) {
				return ctx.getResources().getStringArray(R.array.array_menu_group);
			}
			return ctx.getResources().getStringArray(R.array.array_menu_scene);
		case R.id.atyHomeScheduleBtn:
			return ctx.getResources().getStringArray(R.array.array_menu_schedule);
		case AxalentUtils.SHOW_DEVICE:
			if (loginMode == R.id.atyLoginBluetoothBtn) {
				return ctx.getResources().getStringArray(R.array.array_menu_show_device_bluetooth);
			}
			return ctx.getResources().getStringArray(R.array.array_menu_show_device);
		default:
			return new String[]{};
		}
	}
}
