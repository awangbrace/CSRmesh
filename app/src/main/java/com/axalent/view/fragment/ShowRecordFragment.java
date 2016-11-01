/**
 * File Name                   : ��ʾ��Ƭ��¼�����
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.axalent.R;
import com.axalent.view.activity.PlayVideoActivity;
import com.axalent.view.activity.ShowRecordActivity;
import com.axalent.view.activity.ShowRecordActivity.RecordManage;
import com.axalent.adapter.PhotoGalleryAdapter;
import com.axalent.adapter.ShowRecordAdapter;
import com.axalent.adapter.ShowRecordAdapter.ViewHolder;
import com.axalent.util.ToastUtils;
import com.axalent.view.widget.PhotoGallery;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ShowRecordFragment extends Fragment implements OnItemClickListener, OnItemLongClickListener, RecordManage {
	
	private View view;
	private String typeName;
	private int screenWidth;
	private int screenHeight;
	private GridView snapshotGrid;
	private List<RecordMode> records;
	private PhotoGallery photoGallery;
	private PopupWindow showRecordWindow;
	private Map<Integer, RecordMode> checkedDeleteItems;
	
	public ShowRecordFragment(String typeName) {
		this.typeName = typeName;
		this.records = getRecords(typeName);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		getScreenWidthAndHeight(activity.getWindowManager());
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_show_record, null);
			snapshotGrid = (GridView) view.findViewById(R.id.fragShowSnapshotGrid);
			snapshotGrid.setOnItemClickListener(this);
			snapshotGrid.setOnItemLongClickListener(this);
			snapshotGrid.setAdapter(new ShowRecordAdapter(getActivity(), records, typeName));
		}
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (checkedIsEmpty()) {
			showSnapshotWindow(position);
		} else {
			setCheckedViweState(view, position);
			setBarDeleteViewState();
		}
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (checkedIsEmpty()) {
			setCheckedViweState(view, position);
			setBarDeleteViewState();
		}
		return true;
	}
	
	private boolean checkedIsEmpty() {
		return checkedDeleteItems == null || checkedDeleteItems.size() == 0;
	}
	
	
	private void setCheckedViweState(View view, int position) {
		ViewHolder holder = (ViewHolder) view.getTag();
		int visibility = holder.checkedImg.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;
		holder.checkedImg.setVisibility(visibility);
		
		if (checkedDeleteItems == null) {
			checkedDeleteItems = new HashMap<Integer, RecordMode>();
		}
		
		RecordMode recordMode = records.get(position);
		recordMode.checkedImgState = visibility;
		
		if (View.VISIBLE == visibility) {
			checkedDeleteItems.put(position, recordMode);
		} else {
			checkedDeleteItems.remove(position);
		}
	}
	
	
	private void setBarDeleteViewState() {
		ShowRecordActivity aty = (ShowRecordActivity) getActivity();
		aty.getDeleteView().setVisibility(checkedDeleteItems.isEmpty() ? View.GONE : View.VISIBLE);
	}
	
	private void showSnapshotWindow(int position) {
		if ("photo".equals(typeName)) {
			if (showRecordWindow == null) {
				initSnapshoot();
			}
			if (photoGallery != null) {
				photoGallery.setSelection(position);
			}
			showRecordWindow.showAtLocation(snapshotGrid, Gravity.CENTER, 0, 0);
		} else if ("video".equals(typeName)) {
			Intent intent = new Intent(getActivity(), PlayVideoActivity.class);
			intent.putExtra("path", records.get(position).path);
			getActivity().startActivity(intent);
		}
	}
	
	
	private void initSnapshoot() {
		View contentView = View.inflate(getActivity(), R.layout.window_show_snapshot, null);
		showRecordWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		showRecordWindow.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
		photoGallery = (PhotoGallery) contentView.findViewById(R.id.windowSnapshotGallery);
		final TextView photeCountTxt = (TextView) contentView.findViewById(R.id.windowSnapshotCountTxt);
		photoGallery.setScreenWidth(screenWidth);
		photoGallery.setScreenHeight(screenHeight);
		photoGallery.setVerticalFadingEdgeEnabled(false);
		photoGallery.setHorizontalFadingEdgeEnabled(false);
		photoGallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				photeCountTxt.setText((position + 1) + "/" + records.size());
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		photoGallery.setAdapter(new PhotoGalleryAdapter(getActivity(), records, screenWidth, screenHeight));
	}
	
	
//	 void closeSnapshotWindow() {
//		if (showSnapshotWindow.isShowing()) {
//			showSnapshotWindow.dismiss();
//		}
//	}
	
	private void getScreenWidthAndHeight(WindowManager windowManager) {
		Point size = new Point(); 
		windowManager.getDefaultDisplay().getSize(size); 
		screenWidth = size.x; 
		screenHeight = size.y;
	}
	


	@Override
	public void delete() {
		int count = 0;
		for (Entry<Integer, RecordMode> e : checkedDeleteItems.entrySet()) {
			RecordMode record = e.getValue();
			File file = new File(record.path);
			boolean isSuccess = file.delete();
			if (isSuccess) {
				records.remove(record);
				count ++;
			}
		}
		if (count == checkedDeleteItems.size()) {
			ToastUtils.show(R.string.delete_success);
		}
		((BaseAdapter) snapshotGrid.getAdapter()).notifyDataSetChanged();
		checkedDeleteItems.clear();
		setBarDeleteViewState();
	}
	
	@Override
	public void pageScroll() {
		View deleteView = ((ShowRecordActivity) getActivity()).getDeleteView();
		if (View.VISIBLE == deleteView.getVisibility()) {
			deleteView.setVisibility(View.GONE);
			for (Entry<Integer, RecordMode> entry : checkedDeleteItems.entrySet()) {
				records.get(entry.getKey()).checkedImgState = View.GONE;
			}
			((BaseAdapter) snapshotGrid.getAdapter()).notifyDataSetChanged();
			checkedDeleteItems.clear();
		}
	}
	
	public List<RecordMode> getRecords(String typeName) {
		ArrayList<RecordMode> records = new ArrayList<RecordMode>();
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/axalent/" + typeName;
    	File folder = new File(path) ;
    	String[] files = folder.list();
    	if (files != null && files.length != 0) {
//	    	Arrays.sort(files);
	    	for(String fileName : files) {
	    		records.add(new RecordMode(path + "/" + fileName));
	    	}
//	    	Collections.reverse(records);
    	}
    	return records;
	}
	
	public class RecordMode {
		
		public String path;
		public String date;
		public Bitmap bitmap;
		public int checkedImgState = View.GONE;
		
		public RecordMode() {
		}
		
		public RecordMode(String path) {
			this.path = path;
		}
	}

	

}
