/**
 * File Name                   : ����ͷ��ʷ��¼������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.adapter;

import java.util.List;
import com.axalent.R;
import com.axalent.view.activity.EventActivity.IncidentInfo;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class IncidentAdapter extends BaseAdapter {
	
	private Context context = null;
	private List<IncidentInfo> incidentInfos = null;
	
	public IncidentAdapter(Context context, List<IncidentInfo> incidentInfos) {
		this.context = context;
		this.incidentInfos = incidentInfos;
	}

	@Override
	public int getCount() {
		return incidentInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return incidentInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;
		
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.adapter_incident, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.date.setText(incidentInfos.get(position).date);
		
		return convertView;
	}
	
	private class ViewHolder {
		
		TextView date;
		
		public ViewHolder(View view) {
			this.date = (TextView) view.findViewById(R.id.adapterIncidentDateTxt);
		}
		
	}
	
	public void setData(List<IncidentInfo> incidentInfos) {
		this.incidentInfos = incidentInfos;
		this.notifyDataSetChanged();
	}

}
