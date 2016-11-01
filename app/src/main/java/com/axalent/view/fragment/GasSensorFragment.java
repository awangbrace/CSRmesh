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
import com.axalent.model.Device;
import com.axalent.model.DeviceAttribute;
import com.axalent.model.DeviceRecord;
import com.axalent.util.AxalentUtils;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class GasSensorFragment extends Fragment implements View.OnClickListener {
	
	private ShowDeviceActivity parent = null;
	private TextView co2TxtVal, vocTxtVal; 
	private ListView co2List, vocList; 
	private HistoryAdapter co2Adapter, vocAdapter;
	private TextView endDateTxt, startDateTxt;
	private DateDialog dateDialog;
	private final String[] historyNames = {AxalentUtils.ATTRIBUTE_CO2, AxalentUtils.ATTRIBUTE_VOC};
	
	@Override
	public void onAttach(Activity activity) {
		parent = (ShowDeviceActivity) activity;
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_gas_sensor, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		co2TxtVal = parent.val1;
		vocTxtVal = parent.val2;
		startDateTxt = (TextView) view.findViewById(R.id.includeStartDate);
		endDateTxt = (TextView) view.findViewById(R.id.includeEndDate);
		co2List = (ListView) view.findViewById(R.id.list_history_1);
		vocList = (ListView) view.findViewById(R.id.list_history_2);
		Button selectBtn = (Button) view.findViewById(R.id.btn_select);
		selectBtn.setOnClickListener(this);
		startDateTxt.setOnClickListener(this);
		endDateTxt.setOnClickListener(this);
		setData();
	}
	
	private void setData() {
		startDateTxt.setText(AxalentUtils.getSystemTime(1));
		endDateTxt.setText(AxalentUtils.getSystemTime());
		
		List<DeviceAttribute> deviceAttributes = parent.getCurrentDevice().getAttributes();
		if (deviceAttributes != null && deviceAttributes.size() > 0) {
			int count = 0;
			for (DeviceAttribute deviceAttribute : deviceAttributes) {
				String name = deviceAttribute.getName();
				String value = deviceAttribute.getValue();
				if (name.equals(AxalentUtils.ATTRIBUTE_CO2)) {
					co2TxtVal.setText(TextUtils.isEmpty(value) ? "0" : value);
					count++;
				}
				if (name.equals(AxalentUtils.ATTRIBUTE_VOC)) {
					vocTxtVal.setText(TextUtils.isEmpty(value) ? "0" : value);
					count++;
				}
				if (count == 2) break;
			}
		} else {
			co2TxtVal.setText("0");
			vocTxtVal.setText("0");
		}
	}
	
	private void loadData() {
		parent.getLoadingDialog().show(R.string.load_data);
		parent.getDeviceManager().getDeviceAttributesWithValues(parent.getCurrentDevice(), new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser arg0) {
				Device result = XmlUtils.convertDeviceAttributesWithValues(arg0);
				parent.getCurrentDevice().setAttributes(result.getAttributes());
				setData();
				parent.getLoadingDialog().close();
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				parent.getLoadingDialog().close();
				ToastUtils.show(R.string.load_failure);
			}
		});
	}
	
	private void loadHistory() {
		final String startDate = startDateTxt.getText().toString().trim();
		final String endDate = endDateTxt.getText().toString().trim();
		for (int i = 0; i < historyNames.length; i++) {
			parent.getDeviceManager().getDeviceTS2(parent.getCurrentDevice().getDevId(), historyNames[i], 
					AxalentUtils.formatTimeToUtc(startDate) + "", AxalentUtils.formatTimeToUtc(endDate) + "", new Listener<XmlPullParser>() {
				@Override
				public void onResponse(XmlPullParser response) {
					DeviceRecord deviceRecord = XmlUtils.convertDeviceTS2(response);
					if (!deviceRecord.getDates().isEmpty()) {
						ListUtils.sort(deviceRecord.getDates(), new DateSortComparator());
						if (AxalentUtils.ATTRIBUTE_CO2.equals(deviceRecord.getPropName())) {
							if (co2Adapter == null) {
								co2Adapter = new HistoryAdapter(parent, deviceRecord, parent.getCurrentDevice().getTypeName());
								co2List.setAdapter(co2Adapter);
							} else {
								co2Adapter.setData(deviceRecord);
							}
						} else {
							if (vocAdapter == null) {
								vocAdapter = new HistoryAdapter(parent, deviceRecord, parent.getCurrentDevice().getTypeName());
								vocList.setAdapter(vocAdapter);
							} else {
								vocAdapter.setData(deviceRecord);
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

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_select:
			if (AxalentUtils.getLoginMode() != R.id.atyLoginCloudBtn) {
				ToastUtils.show(R.string.please_change_to_cloud_mode);
				return;
			}
			loadHistory();
			break;
		case R.id.includeStartDate:
		case R.id.includeEndDate:
			showDataDialog(id);
			break;
		}
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

}
