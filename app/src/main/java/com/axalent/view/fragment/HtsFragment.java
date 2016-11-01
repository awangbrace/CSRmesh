/**
 * File Name                   : Humidity and Temperature Sensor界面
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.fragment;

import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.axalent.R;
import com.axalent.view.activity.ChartActivity;
import com.axalent.view.activity.ShowDeviceActivity;
import com.axalent.adapter.HistoryAdapter;
import com.axalent.presenter.DateSortComparator;
import com.axalent.model.DeviceRecord;
import com.axalent.util.AxalentUtils;
import com.axalent.util.ListUtils;
import com.axalent.util.ToastUtils;
import com.axalent.util.XmlUtils;
import com.axalent.view.widget.DateDialog;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class HtsFragment extends Fragment implements View.OnClickListener {
	
	private View view;
	private TextView endDateTxt;
	private TextView startDateTxt;
	private DateDialog dateDialog ;
	private ListView firstListView; 
	private ListView lastListView; 
	private ShowDeviceActivity aty;
	private HistoryAdapter firstAdapter;
	private HistoryAdapter lastAdapter;
	private TextView temperatureValueTxt, humidityValueTxt;
	
	@Override
	public void onAttach(Activity activity) {
		aty = (ShowDeviceActivity) activity;
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_hts, null);
			initView();
			setDatas();
		}
		return view;
	}
	
	private void initView() {
		Button historyBtn = (Button) view.findViewById(R.id.fragHtsHistoryBtn);
		Button chartBtn = (Button) view.findViewById(R.id.fragHtsChartBtn);
		
	    firstListView = (ListView) view.findViewById(R.id.fragHtsListViewOne);
		lastListView = (ListView) view.findViewById(R.id.fragHtsListViewTwo);
		startDateTxt = (TextView) view.findViewById(R.id.includeStartDate);
		endDateTxt = (TextView) view.findViewById(R.id.includeEndDate);
		
		temperatureValueTxt = aty.val1;
		humidityValueTxt = aty.val2;
		
		startDateTxt.setOnClickListener(this);
		endDateTxt.setOnClickListener(this);
		historyBtn.setOnClickListener(this);
		chartBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.fragHtsHistoryBtn:
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
		case R.id.fragHtsChartBtn:
			if (AxalentUtils.getLoginMode() != R.id.atyLoginCloudBtn) {
				ToastUtils.show(R.string.please_change_to_cloud_mode);
				return;
			}
			gotoChart();
			break;
		}
	}
	
	private void gotoChart() {
		Intent intent = new Intent(getActivity(), ChartActivity.class);
		intent.putExtra(AxalentUtils.KEY_DEVICE, aty.getCurrentDevice());
		getActivity().startActivity(intent);
	}
	
	private void showDataDialog(int id) {
		if (dateDialog == null) {
			dateDialog = new DateDialog(getActivity());
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
	
	private void setDatas() {
		startDateTxt.setText(AxalentUtils.getSystemTime(1));
		endDateTxt.setText(AxalentUtils.getSystemTime());
		String[] names = {AxalentUtils.ATTRIBUTE_HUMIDITY, AxalentUtils.ATTRIBUTE_TEMPERATURE};
		Map<String, String> maps = AxalentUtils.getDeviceAttributeValues(aty.getCurrentDevice(), names);
		String humidity = maps.get(names[0]);
		String temperature = maps.get(names[1]);
		if (TextUtils.isEmpty(humidity)) humidity = "0";
		if (TextUtils.isEmpty(temperature)) temperature = "0";
		humidityValueTxt.setText(Float.parseFloat(humidity) / 100 + "°c");
		temperatureValueTxt.setText(Float.parseFloat(temperature) / 100 + "%");
	}
	
	private void selectHistory() {
		final String startDate = startDateTxt.getText().toString().trim();
		final String endDate = endDateTxt.getText().toString().trim();
		for (int i = 0; i < 2; i++) {
			final int index = i;
			String historyName = (index == 0 ? AxalentUtils.ATTRIBUTE_HUMIDITY : AxalentUtils.ATTRIBUTE_TEMPERATURE);
			aty.getDeviceManager().getDeviceTS2(aty.getCurrentDevice().getDevId(), historyName, AxalentUtils.formatTimeToUtc(startDate) + "", AxalentUtils.formatTimeToUtc(endDate) + "", new Listener<XmlPullParser>() {
				@Override
				public void onResponse(XmlPullParser response) {
					DeviceRecord deviceRecord = XmlUtils.convertDeviceTS2(response);
					if (!deviceRecord.getDates().isEmpty()) {
						ListUtils.sort(deviceRecord.getDates(), new DateSortComparator());
						if (AxalentUtils.ATTRIBUTE_HUMIDITY.equals(deviceRecord.getPropName())) {
							if (firstAdapter == null) {
								firstAdapter = new HistoryAdapter(aty, deviceRecord, aty.getCurrentDevice().getTypeName());
								firstListView.setAdapter(firstAdapter);
							} else {
								firstAdapter.setData(deviceRecord);
							}
						} else {
							if (lastAdapter == null) {
								lastAdapter = new HistoryAdapter(aty, deviceRecord, aty.getCurrentDevice().getTypeName());
								lastListView.setAdapter(lastAdapter);
							} else {
								lastAdapter.setData(deviceRecord);
							}
						}
					} else {
						ToastUtils.show(R.string.not_historical_record);
					}
					
					
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					ToastUtils.show(R.string.select_failure);
				}
			});
		}
	}
	
	
	
}
