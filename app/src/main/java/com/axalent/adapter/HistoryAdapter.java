/**
 * File Name                   : �豸��ʷ��¼������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.adapter;

import com.axalent.R;
import com.axalent.model.Date;
import com.axalent.model.DeviceRecord;
import com.axalent.util.AxalentUtils;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HistoryAdapter extends BaseAdapter {
	
	private Context ctx;
	private String typeName;
	private DeviceRecord deviceRecord;
	
	public HistoryAdapter(Context ctx, DeviceRecord deviceRecord, String typeName) {
		this.ctx = ctx;
		this.deviceRecord = deviceRecord;
		this.typeName = typeName;
	}

	@Override
	public int getCount() {
		return deviceRecord.getDates().size();
	}

	@Override
	public Object getItem(int position) {
		return deviceRecord.getDates().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			convertView = View.inflate(ctx, R.layout.adapter_history,  null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		setDatas(vh, position);
		return convertView;
	}
	
	private class ViewHolder {
		TextView time;
		TextView val;
		
		public ViewHolder(View view) {
			this.time = (TextView) view.findViewById(R.id.adapterHistoryTime);
			this.val = (TextView) view.findViewById(R.id.adapterhistoryNumberVal);
		}
	}
	
	private void setDatas(ViewHolder vh, int position) {
		Date date = deviceRecord.getDates().get(position);
		vh.time.setText(AxalentUtils.formatUtcToTim2(Long.parseLong(date.getTime())));
		vh.val.setText(getValue(date.getValue()));
		
	}
	
	private String getValue(String value) {
		if (!TextUtils.isEmpty(value)) {
			if (AxalentUtils.TYPE_SM.equalsIgnoreCase(typeName) 
					|| AxalentUtils.TYPE_WINDOW_COVER.equalsIgnoreCase(typeName)) {
				return AxalentUtils.OFF.equals(value) ? ctx.getResources().getString(R.string.off) : ctx.getResources().getString(R.string.on);
			} else if (AxalentUtils.TYPE_HTM.equalsIgnoreCase(typeName)) {
				return AxalentUtils.valueFormatDate(value);
			} else if (AxalentUtils.TYPE_HTS.equalsIgnoreCase(typeName)) {
				return Float.parseFloat(value) / 100 + "";
			} else if (AxalentUtils.TYPE_POWER_PLUG.equalsIgnoreCase(typeName) || AxalentUtils.TYPE_GUNI_PLUG.equalsIgnoreCase(typeName)) {
				String propName = deviceRecord.getPropName();
				if (AxalentUtils.ATTRIBUTE_MYSWITCH.equals(propName)) {
					return "true".equals(value) ? ctx.getResources().getString(R.string.on) : ctx.getResources().getString(R.string.off);
				} else if (AxalentUtils.ATTRIBUTE_CURRENT.equals(propName)) {
					// ����
					float newValue = Float.parseFloat(value);
					if (newValue > 0f) {
						return value + " (mA)";
					}
					return "0.00 (mA)";
				} else if (AxalentUtils.ATTRIBUTE_ENERGY.equals(propName)) {
					// ����
				    return value + " (Wh)";
				} else if (AxalentUtils.ATTRIBUTE_POWER.equals(propName)) {
					// ����
					float newValue = Float.parseFloat(value);
					if (newValue > 0f) {
						return value + " (mW)";
					}
					return "0.00 (mW)";
				} else if (AxalentUtils.ATTRIBUTE_VOLTAGE.equals(propName)) {
					// ��ѹ
					return value + " (mV)";
				}
			    return value;
			} else if (AxalentUtils.TYPE_SC.equalsIgnoreCase(typeName) || AxalentUtils.TYPE_EBIO_EPAD.equalsIgnoreCase(typeName)) {
				return value.equals("false") ? ctx.getResources().getString(R.string.off) : ctx.getResources().getString(R.string.on);
			} else {
				return value;
			}
		}
		return "null";
	}
	
	public void setData(DeviceRecord deviceRecord) {
		this.deviceRecord = deviceRecord;
		this.notifyDataSetChanged();
	}

}
 