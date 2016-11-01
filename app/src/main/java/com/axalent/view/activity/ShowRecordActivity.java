/**
 * File Name                   : ��ʾ����ͷ�������Ƭ����Ƶ����
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.activity;

import java.util.ArrayList;
import java.util.List;
import com.axalent.R;
import com.axalent.adapter.RecordAdapter;
import com.axalent.application.BaseActivity;
import com.axalent.view.fragment.ShowRecordFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ShowRecordActivity extends BaseActivity implements OnClickListener, OnPageChangeListener{

	private View deleteView;
	private TextView titleTxt;
	private ViewPager viewPager;
	private int currentFragmentIndex;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_record);
		initActionBar();
		initView();
	}
	
	private void initActionBar() {
		View customView = findViewById(R.id.barShowRecordContent);
		titleTxt = (TextView) customView.findViewById(R.id.barShowRecordTitleTxt);
		titleTxt.setText(R.string.snapshot_list);
		View backView = customView.findViewById(R.id.barShowRecordBack);
		deleteView = customView.findViewById(R.id.barShowRecordDelete);
		backView.setOnClickListener(this);
		deleteView.setOnClickListener(this);
	}
	
	private void initView() {
		viewPager = (ViewPager) findViewById(R.id.atyShowRecordPager);
		viewPager.setOnPageChangeListener(this);
		viewPager.setAdapter(new RecordAdapter(getSupportFragmentManager(), getFragments()));
		Button lookSnapshotBtn = (Button) findViewById(R.id.atyShowRecordLookSnapshotBtn);
		Button lookRecordingBtn = (Button) findViewById(R.id.atyShowRecordLookRecordingBtn);
		lookSnapshotBtn.setOnClickListener(this);
		lookRecordingBtn.setOnClickListener(this);
	}
	
	private List<Fragment> getFragments() {
		List<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(new ShowRecordFragment("photo"));
		fragments.add(new ShowRecordFragment("video"));
		return fragments;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.barShowRecordBack:
			finish();
			break;
		case R.id.barShowRecordDelete:
			getRecordManage().delete();
			break;
		case R.id.atyShowRecordLookSnapshotBtn:
			// �鿴����
			if (viewPager.getCurrentItem() != 0) {
				viewPager.setCurrentItem(0);
			}
			break;
		case R.id.atyShowRecordLookRecordingBtn:
			// �鿴¼��
			if (viewPager.getCurrentItem() != 1) {
				viewPager.setCurrentItem(1);
			}
			break;
		}
	}
	
	private RecordManage getRecordManage() {
		RecordAdapter adapter = (RecordAdapter) viewPager.getAdapter();
		return (RecordManage) adapter.getItem(currentFragmentIndex);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int arg0) {
		// ���ñ���
		getRecordManage().pageScroll();
		currentFragmentIndex = arg0;
		titleTxt.setText(arg0 == 0 ? R.string.snapshot_list : R.string.recording_list);
	}
	
	
	public interface RecordManage {
		void delete();
		void pageScroll();
	}
	
	public View getDeleteView() {
		return deleteView;
	}
	


}
