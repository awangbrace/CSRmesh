/**
 * File Name                   : Health Thermometer 界面
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.fragment;

import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.axalent.R;
import com.axalent.view.activity.ShowDeviceActivity;
import com.axalent.adapter.HistoryAdapter;
import com.axalent.presenter.DateSortComparator;
import com.axalent.model.DeviceAttribute;
import com.axalent.model.DeviceRecord;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;
import com.axalent.util.ListUtils;
import com.axalent.util.ToastUtils;
import com.axalent.util.XmlUtils;
import com.axalent.view.widget.DateDialog;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class HtmFragment extends Fragment implements OnClickListener, OnSeekBarChangeListener{

	private DeviceRecord deviceRecord;
	private ShowDeviceActivity aty;
	private HistoryAdapter adapter;
	private TextView endDateTxt;
	private TextView startDateTxt;
	private DateDialog dateDialog;
	private ListView listView; 
	private TextView lowTxt;
	private TextView highTxt;
	private SeekBar highBar;
	private SeekBar lowBar;
	private View view;
	
	@Override
	public void onAttach(Activity activity) {
		aty = (ShowDeviceActivity) activity;
		super.onAttach(activity);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_htm, null);
			initView();
			setDatas();
		}
		return view;
	}
	
	private void initView() {
		highTxt = (TextView) view.findViewById(R.id.fragHtmHighTxt);
		lowTxt = (TextView) view.findViewById(R.id.fragHtmLowTxt);
		highBar = (SeekBar) view.findViewById(R.id.fragHtmHighBar);
		lowBar = (SeekBar) view.findViewById(R.id.fragHtmLowBar);
		Button historyBtn = (Button) view.findViewById(R.id.includeHistoryBtn);
	    listView = (ListView) view.findViewById(R.id.includeHistoryListView);
		startDateTxt = (TextView) view.findViewById(R.id.includeStartDate);
		endDateTxt = (TextView) view.findViewById(R.id.includeEndDate);
		highBar.setOnSeekBarChangeListener(this);
		lowBar.setOnSeekBarChangeListener(this);
		startDateTxt.setOnClickListener(this);
		endDateTxt.setOnClickListener(this);
		historyBtn.setOnClickListener(this);
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
	
	
	private void setDatas() {
		final String systemTime = AxalentUtils.getSystemTime();
		startDateTxt.setText(systemTime);
		endDateTxt.setText(systemTime);
		List<DeviceAttribute> deviceAttributes = aty.getCurrentDevice().getAttributes();
		if (deviceAttributes != null && deviceAttributes.size() != 0) {
			for (DeviceAttribute attribute : deviceAttributes) {
				String attributeName = attribute.getName();
				String attributeValue = attribute.getValue();
				if (TextUtils.isEmpty(attributeValue)) continue;
				if (AxalentUtils.ATTRIBUTE_TEMPERATURE.equals(attributeName)) {
				} else if (AxalentUtils.ATTRIBUTE_HIGHTHRESHOLD.equals(attributeName)) {
					highBar.setProgress(Integer.parseInt(attributeValue));
					highTxt.setText(String.valueOf(Float.parseFloat(attributeValue)));
				} else if (AxalentUtils.ATTRIBUTE_LOWTHRESHOLD.equals(attributeName)) {
					lowBar.setProgress(Integer.parseInt(attributeValue));
					lowTxt.setText(String.valueOf(Float.parseFloat(attributeValue)));
				}
			}
		}
	}
	
	
	private void selectHistory() {
		final String startDate = startDateTxt.getText().toString();
		final String endDate = endDateTxt.getText().toString();
		if (!TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate)) {
			aty.getDeviceManager().getDeviceTS2(aty.getCurrentDevice().getDevId(), AxalentUtils.ATTRIBUTE_TEMPERATURE, AxalentUtils.formatTimeToUtc(startDate) + "", AxalentUtils.formatTimeToUtc(endDate) + "", new Listener<XmlPullParser>() {
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
					ToastUtils.show(R.string.select_failure);
				}
			});
		}
	}
	

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		switch (seekBar.getId()) {
		case R.id.fragHtmHighBar:
			highTxt.setText(String.valueOf("hig : "+ (float) progress));
			break;
		case R.id.fragHtmLowBar:
			lowTxt.setText(String.valueOf("low : "+ (float) progress));
			break;
		}
	}


	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		setDeviceAttribute((seekBar.getId() == R.id.fragHtmHighBar) ? "hig" : "low", String.valueOf((float) seekBar.getProgress()));
	}
	
	private void setAdapter() {
		if (adapter == null) {
			adapter = new HistoryAdapter(aty, deviceRecord, aty.getCurrentDevice().getTypeName());
			listView.setAdapter(adapter);
		} else {
			adapter.setData(deviceRecord);
		}
	}
	
	private void setDeviceAttribute(final String key, final String value) {
		final String updateId = aty.getCurrentDevice().getDevId();
		aty.getDeviceManager().setDeviceAttribute(updateId, key, value, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				CacheUtils.updateDeviceAttibuteByDevId(updateId, key, value);
				ToastUtils.show(R.string.alter_success);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				ToastUtils.show(R.string.alter_failure);
			}
		});
	}

	
}
