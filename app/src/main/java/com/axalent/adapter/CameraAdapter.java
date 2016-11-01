/**
 * File Name                   : ����ͷ������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.adapter;

import com.axalent.R;
import com.axalent.util.LogUtils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CameraAdapter extends BaseAdapter {
	
	private Context context;
	private String[] items;
	private OnItemClickListener onItemClickListener = null;
	
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}
	
	public CameraAdapter(Context context, String[] items) {
		LogUtils.i("���ȣ�"+items.length);
		this.context = context;
		this.items = items;
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
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.adapter_camera, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.position = position;
		viewHolder.txt.setText(items[position]);
		viewHolder.txt.setCompoundDrawables(null, getTopDrawable(viewHolder, position), null, null);
		
		return convertView;
	}
	
	private class ViewHolder implements OnClickListener {
		
		public TextView txt;
		public int position;

		public ViewHolder(View View) {
			txt = (TextView) View.findViewById(R.id.adapterCameraItemTxt);
			txt.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (onItemClickListener != null) {
				onItemClickListener.onItemClick(v, position);
			}
		}

		
	}
	
	private Drawable getTopDrawable(ViewHolder viewHolder, int position) {
		Drawable topDrawable = viewHolder.txt.getCompoundDrawables()[2];
		if (topDrawable == null) {
			topDrawable = getDrawable(position);
			topDrawable.setBounds(0, 0, 30, 30);
		}
		return topDrawable;
	}
	
	private Drawable getDrawable(int arg1) {
		switch (arg1) {
		case 0:
			return context.getResources().getDrawable(R.drawable.listening);
		case 1:
			return context.getResources().getDrawable(R.drawable.recording);
		case 2:
			return context.getResources().getDrawable(R.drawable.snapshoot);
		case 3:
			return context.getResources().getDrawable(R.drawable.photograph);
		case 4:
			return context.getResources().getDrawable(R.drawable.alarm);
		case 5:
			return context.getResources().getDrawable(R.drawable.angle);
		case 6:
			return context.getResources().getDrawable(R.drawable.infrared);
		case 7:
			return context.getResources().getDrawable(R.drawable.change_up_down);
		case 8:
			return context.getResources().getDrawable(R.drawable.change_left_right);
		case 9:
			return context.getResources().getDrawable(R.drawable.quality);
		case 10:
			return context.getResources().getDrawable(R.drawable.pattern);
		default:
			return context.getResources().getDrawable(R.drawable.listening);
		}
	}

	public interface OnItemClickListener {
		void onItemClick(View v, int position);
	}


}
