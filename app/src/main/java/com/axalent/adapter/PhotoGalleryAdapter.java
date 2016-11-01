/**
 * File Name                   : ��Ƭ������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.adapter;

import java.io.File;
import java.util.List;

import com.axalent.view.fragment.ShowRecordFragment.RecordMode;
import com.axalent.view.widget.PhotoImage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;

public class PhotoGalleryAdapter extends BaseAdapter {

	private Context context;
	private int screenWidth;
	private int screenHeight;
	private List<RecordMode> snapshots;

	public PhotoGalleryAdapter(Context context, List<RecordMode> snapshots, int screenWidth, int screenHeight) {
		this.context = context;
		this.snapshots = snapshots;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}

	@Override
	public int getCount() {
		return snapshots.size();
	}

	@Override
	public Object getItem(int position) {
		return snapshots.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Bitmap bmp = BitmapFactory.decodeFile(snapshots.get(position).path);
		PhotoImage photoImage = new PhotoImage(context);
		photoImage.setScreenWidth(screenWidth);
		photoImage.setScreenHeight(screenHeight);
		photoImage.setLayoutParams(new Gallery.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		photoImage.setImageBitmap(bmp);
		return photoImage;
	}
	
	public boolean removeChildViewAtPosition(int position){
		try {
			File file = new File(snapshots.get(position).path);
			boolean deleted = file.delete();
			snapshots.remove(position);
			this.notifyDataSetChanged();
			return deleted;
		} catch (Exception e){
			return false;
		}
	}

}
