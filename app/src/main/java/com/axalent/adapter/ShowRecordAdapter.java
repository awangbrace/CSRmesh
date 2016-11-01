/**
 * File Name                   : ��ʾ¼������Ƶ������
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
import com.axalent.view.fragment.ShowRecordFragment.RecordMode;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowRecordAdapter extends BaseAdapter {
	
	private Context context;
	private String typeName;
	private List<RecordMode> recordModes;
//	private MediaMetadataRetriever mediaMetadataRetriever;
	
	public ShowRecordAdapter(Context context, List<RecordMode> recordModes, String typeName) {
		this.context = context;
		this.recordModes = recordModes;
		this.typeName = typeName;
	}

	@Override
	public int getCount() {
		return recordModes.size();
	}

	@Override
	public Object getItem(int position) {
		return recordModes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.adapter_show_record, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		setResource(holder, position);
		return convertView;
	}
	
	
	public class ViewHolder {
		
		public TextView dateTxt;
		public ImageView itemImg;
		public ImageView checkedImg;
		
		
		public ViewHolder(View view) {
			this.dateTxt = (TextView) view.findViewById(R.id.adapterShowRecordDateTxt);
			this.itemImg = (ImageView) view.findViewById(R.id.adapterShowRecordItemImg);
			this.checkedImg = (ImageView) view.findViewById(R.id.adapterShowRecordCheckedImg);
		}
	}
	
	private void setResource(ViewHolder holder, int position) {
		RecordMode recordMode = recordModes.get(position);
		if ("photo".equals(typeName)) {
			if (recordMode.bitmap == null) {
				recordMode.bitmap = getSnapshotBitmap(recordMode.path);
			}
			holder.itemImg.setImageBitmap(recordMode.bitmap); 
		} else if ("video".equals(typeName)) {
//			if (recordMode.bitmap == null) {
//				recordMode.bitmap = getRecordingBitmap(recordMode.path);
//			}
//			holder.itemImg.setImageBitmap(recordMode.bitmap);
			if (recordMode.date == null) {
				recordMode.date = getDate(recordMode.path);
			}
			holder.dateTxt.setText(recordMode.date);
			holder.itemImg.setBackgroundResource(R.drawable.play);
		}
		holder.checkedImg.setVisibility(recordMode.checkedImgState);
	}
	
	private String getDate(String path) {
		int start = path.indexOf("_") + 1;
		int end = path.indexOf(".");
		
		String data = path.substring(start, end);
		
		String[] datas = data.split("_");
		
		StringBuilder date = new StringBuilder(datas[0]);
		date.insert(4, "-");
		date.insert(7, "-");
		
		int length = datas[1].length();
		String tempTime = datas[1].substring(0, length - 2);
		StringBuilder time = new StringBuilder(tempTime);
		time.insert(2, ":");
		
		return date.toString() + " " + time.toString();
	}
	
	private Options getOptions() {
		Options options = new Options();
		options.inSampleSize = 4;
		return options;
	}
	
	private Bitmap getSnapshotBitmap(String path) {
		Bitmap bitmap = BitmapFactory.decodeFile(path, getOptions()) ;
		if (bitmap == null) {
			for (int i = recordModes.size() - 1; i >= 0; i--) {
				bitmap = BitmapFactory.decodeFile(path, getOptions()) ;
				if (bitmap != null) {
					break;
				}
			}
		}
		return bitmap;
	}
	
	
//	private Bitmap getRecordingBitmap(String path) {
//		if (mediaMetadataRetriever == null) {
//			mediaMetadataRetriever = new MediaMetadataRetriever();
//		}
//		mediaMetadataRetriever.setDataSource(path);
//		mediaMetadataRetriever.release();
//		return mediaMetadataRetriever.getFrameAtTime();
//	}

}
