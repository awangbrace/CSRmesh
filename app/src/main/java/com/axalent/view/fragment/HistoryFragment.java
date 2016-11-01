/**
 * File Name                   : 历史记录界面
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.fragment;

import org.xmlpull.v1.XmlPullParser;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.axalent.R;
import com.axalent.view.activity.ShowDeviceActivity;
import com.axalent.adapter.HistoryAdapter;
import com.axalent.presenter.DateSortComparator;
import com.axalent.model.DeviceRecord;
import com.axalent.util.AxalentUtils;
import com.axalent.util.ListUtils;
import com.axalent.util.LogUtils;
import com.axalent.util.ToastUtils;
import com.axalent.util.XmlUtils;
import com.axalent.view.widget.DateDialog;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryFragment extends Fragment implements View.OnClickListener {

	private ListView listView;
	private String historyName;
	private TextView endDateTxt;
	private TextView startDateTxt;
	private DateDialog dateDialog;
	private ShowDeviceActivity aty;
	private HistoryAdapter adapter;
	private DeviceRecord deviceRecord;
	
	@Override
	public void onAttach(Activity activity) {
		aty = (ShowDeviceActivity) activity;
		super.onAttach(activity);
	}
	
	public HistoryFragment(String historyName) {
		this.historyName = historyName;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_history, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		listView = (ListView) view.findViewById(R.id.includeHistoryListView);
		Button historyBtn = (Button) view.findViewById(R.id.includeHistoryBtn);
		startDateTxt = (TextView) view.findViewById(R.id.includeStartDate);
		endDateTxt = (TextView) view.findViewById(R.id.includeEndDate);
		startDateTxt.setOnClickListener(this);
		endDateTxt.setOnClickListener(this);
		historyBtn.setOnClickListener(this);
		startDateTxt.setText(AxalentUtils.getSystemTime(1));
		endDateTxt.setText(AxalentUtils.getSystemTime());
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.includeHistoryBtn:
			if (AxalentUtils.getLoginMode() != R.id.atyLoginCloudBtn) {
				ToastUtils.show(R.string.please_change_to_cloud_mode);
				return;
			}
			selectHistory();
			break;
		case R.id.includeStartDate:
			showDataDialog(id);
			break;
		case R.id.includeEndDate:
			showDataDialog(id);
			break;
		}
	}
	
	private void showDataDialog(int id) {
		if (dateDialog == null) {
			dateDialog = new DateDialog(aty);
			dateDialog.setOnSelectedDateListener(new DateDialog.OnSelectedDateListener() {
				@Override
				public void onSelectedDate(int currentSelectId, String date) {
					switch (currentSelectId) {
					case R.id.includeStartDate:
						startDateTxt.setText(date);
						break;
					case R.id.includeEndDate:
						endDateTxt.setText(date);
						break;
					}
				}
			});
		}
		dateDialog.setCurrentSelectId(id);
		dateDialog.show();
	}
	
	private void selectHistory() {
		final String startDate = startDateTxt.getText().toString();
		final String endDate = endDateTxt.getText().toString();
		LogUtils.i("selectHistory:"+historyName+","+startDate+",endDate");
		if (!TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate)) {
			aty.getDeviceManager().getDeviceTS2(aty.getCurrentDevice().getDevId(), historyName, AxalentUtils.formatTimeToUtc(startDate) + "", AxalentUtils.formatTimeToUtc(endDate) + "", new Listener<XmlPullParser>() {
				@Override
				public void onResponse(XmlPullParser response) {
					deviceRecord = XmlUtils.convertDeviceTS2(response);
					if (!deviceRecord.getDates().isEmpty()) {
						ListUtils.sort(deviceRecord.getDates(), new DateSortComparator());
						setAdapter();
					} else {
						ToastUtils.show(R.string.not_historical_record);
					}
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					Toast.makeText(aty, R.string.select_failure, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	private void setAdapter() {
		if (adapter == null) {
			adapter = new HistoryAdapter(aty, deviceRecord, aty.getCurrentDevice().getTypeName());
			listView.setAdapter(adapter);
		} else {
			adapter.setData(deviceRecord);
		}
	}
	
}
