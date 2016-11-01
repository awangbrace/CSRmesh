/**
 * File Name                   : me����������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.axalent.R;
import com.axalent.presenter.RxBus;
import com.axalent.presenter.events.MeshResponseEvent;
import com.axalent.util.AxalentUtils;
import com.axalent.util.ToastUtils;

public class MeAdapter extends BaseAdapter {

	private Context ctx;
	private String[] dates;
	private SharedPreferences sharedPreferences;
	
	public MeAdapter(Context ctx) {
		this.ctx = ctx;
		this.dates = ctx.getResources().getStringArray(R.array.array_frag_me);
		this.sharedPreferences = ctx.getSharedPreferences(AxalentUtils.USER_FILE_NAME, Context.MODE_PRIVATE);
	}

	@Override
	public int getCount() {
		return dates.length;
	}

	@Override
	public Object getItem(int position) {
		return dates[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			convertView = View.inflate(ctx, R.layout.adapter_me, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		setDatas(vh, position);
		return convertView;
	}
	
	private class ViewHolder {
		
		TextView tv;
		CheckBox config;
		
		public ViewHolder(View view) {
			tv = (TextView) view.findViewById(R.id.adapterMeTxt);
			config = (CheckBox) view.findViewById(R.id.config_box);
			config.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					sharedPreferences.edit().putBoolean("showGateway", config.isChecked()).commit();
					Intent intent = new Intent("com.axalent.HomeActivity");
					intent.putExtra(AxalentUtils.KEY_SKIP, AxalentUtils.REFRESH_DATA);
					ctx.sendBroadcast(intent);
					ToastUtils.show(R.string.success);
					RxBus.getDefaultInstance().post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.REFRESH_PAGE));
				}
			});
		}
		
	}
	
	private void setDatas(ViewHolder vh, int position) {
		if (position == 6) {
			vh.config.setVisibility(View.VISIBLE);
			vh.config.setChecked(sharedPreferences.getBoolean("showGateway", false));
		} else {
			vh.config.setVisibility(View.GONE);
		}
		
		vh.tv.setTextColor(position == 3 ? Color.RED : Color.BLACK);
		vh.tv.setText(position == 7 ? dates[position] + AxalentUtils.getCurrentLanguageValue() : dates[position]);
	}

}
