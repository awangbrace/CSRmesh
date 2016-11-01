/**
 * File Name                   : Power plug ����
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.axalent.R;
import com.axalent.view.activity.ChartActivity;
import com.axalent.view.activity.ShowDeviceActivity;
import com.axalent.adapter.HistoryAdapter;
import com.axalent.presenter.DateSortComparator;
import com.axalent.model.Date;
import com.axalent.model.DeviceRecord;
import com.axalent.util.AxalentUtils;
import com.axalent.util.ListUtils;
import com.axalent.util.ToastUtils;
import com.axalent.util.XmlUtils;
import com.axalent.view.widget.DateDialog;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

public class PowerplugFragment extends Fragment implements OnClickListener, OnItemSelectedListener {
	
	private TextView endDateTxt;
	private TextView startDateTxt;
	private DateDialog dateDialog;
	private Spinner selectSpinner;
	private ShowDeviceActivity aty;
	private HistoryAdapter historyAdapter;
	private ListView historyListView;
	private int currentOptionPosition;
	private DeviceRecord deviceRecord;
	private List<AllRecordMode> allRecordModes; 
	private AllAdapter allAdapter;
	private int count = 0;
	private static final int SHOW_CHART_BTN = 0x4349;
	
	@Override
	public void onAttach(Activity activity) {
		aty = (ShowDeviceActivity) activity;
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_powerplug, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		historyListView = (ListView) view.findViewById(R.id.includeHistoryListView);
		
	    selectSpinner = (Spinner) view.findViewById(R.id.includeSelectSpinner);
		Button historyBtn = (Button) view.findViewById(R.id.includeHistoryBtn);
		startDateTxt = (TextView) view.findViewById(R.id.includeStartDate);
		endDateTxt = (TextView) view.findViewById(R.id.includeEndDate);
	
		RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.includeRecordLayout1);
		Button showChartBtn = new Button(getActivity());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, 
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		lp.leftMargin = (int) getResources().getDimension(R.dimen.frag_camera_padding_top);
		
		showChartBtn.setId(SHOW_CHART_BTN);
		showChartBtn.setText(R.string.chart);
		showChartBtn.setTextColor(Color.WHITE);
		showChartBtn.setOnClickListener(this);
		showChartBtn.setBackgroundResource(R.drawable.selector_base_btn);
		
		lp.addRule(RelativeLayout.RIGHT_OF, R.id.includeHistoryBtn);
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
		showChartBtn.setLayoutParams(lp);
		rl.addView(showChartBtn);
		
		selectSpinner.setOnItemSelectedListener(this);
		startDateTxt.setOnClickListener(this);
		endDateTxt.setOnClickListener(this);
		historyBtn.setOnClickListener(this);
		
		startDateTxt.setText(AxalentUtils.getSystemTime(1));
		endDateTxt.setText(AxalentUtils.getSystemTime());
		selectSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getSelectSpinnerData()));
	}
	
	
	private String[] getSelectSpinnerData() {
		String typeName = aty.getCurrentDevice().getTypeName();
		if (typeName.equalsIgnoreCase(AxalentUtils.TYPE_GUNI_PLUG)) {
			return getResources().getStringArray(R.array.array_frag_plug2);
		} else {
			return getResources().getStringArray(R.array.array_frag_plug);
		}
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
		case SHOW_CHART_BTN:
			if (AxalentUtils.getLoginMode() != R.id.atyLoginCloudBtn) {
				ToastUtils.show(R.string.please_change_to_cloud_mode);
				return;
			}
			// ��ʾͼ��
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
	
	private void selectHistory() {
		
		final String devId = aty.getCurrentDevice().getDevId();
		final String startDate = startDateTxt.getText().toString().trim();
		final String endDate = endDateTxt.getText().toString().trim();

		if ("all".equals(getPlugPropName(currentOptionPosition))) {
			
			final int defaultSize = 5;
			
			if (allRecordModes != null) {
				allRecordModes.clear();
			}
			
			for (int i = 0; i < defaultSize; i++) {
				currentOptionPosition = i;
				aty.getDeviceManager().getDeviceTS2(devId, getPlugPropName(currentOptionPosition), AxalentUtils.formatTimeToUtc(startDate) + "", AxalentUtils.formatTimeToUtc(endDate) + "", new Listener<XmlPullParser>() {
					@Override
					public void onResponse(XmlPullParser response) {
						count ++;
						deviceRecord = XmlUtils.convertDeviceTS2(response);
						
						if (!deviceRecord.getDates().isEmpty()) {
							
							if (allRecordModes == null) {
								allRecordModes = new ArrayList<AllRecordMode>();
							} 
							
							String on = getResources().getString(R.string.on);
							String off = getResources().getString(R.string.off);
							DecimalFormat decimalFormat = new DecimalFormat("##0.00");
							
							for (Date date : deviceRecord.getDates()) {
								AllRecordMode mode = new AllRecordMode();
								mode.name = deviceRecord.getPropName();
								mode.time = AxalentUtils.formatUtcToTime(Long.parseLong(date.getTime()));
								
								if (AxalentUtils.ATTRIBUTE_MYSWITCH.equals(mode.name)) {
									mode.val = "true".equals(date.getValue()) ? on : off;
								} else {
									mode.val = decimalFormat.format(Float.parseFloat(date.getValue()));
								}
								allRecordModes.add(mode);
							}
						} 

						if (count == defaultSize) {
							count = 0;
							currentOptionPosition = defaultSize;
							ListUtils.sort(allRecordModes, new DateSortComparatorByAll());
							if (allAdapter == null) {
								allAdapter = new AllAdapter();
								historyListView.setAdapter(allAdapter);
							} else {
								historyListView.setAdapter(allAdapter);
								allAdapter.notifyDataSetChanged();
							}
						}
						
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						count ++;
						if (count == defaultSize) {
							count = 0;
							currentOptionPosition = defaultSize;
							ToastUtils.show(R.string.select_failure);
						}
					}
				});
			}
		} else {
			
			aty.getDeviceManager().getDeviceTS2(devId, getPlugPropName(currentOptionPosition), AxalentUtils.formatTimeToUtc(startDate) + "", AxalentUtils.formatTimeToUtc(endDate) + "", new Listener<XmlPullParser>() {
				@Override
				public void onResponse(XmlPullParser response) {
					deviceRecord = XmlUtils.convertDeviceTS2(response);
					if (!deviceRecord.getDates().isEmpty()) {
						ListUtils.sort(deviceRecord.getDates(), new DateSortComparator());
						if (historyAdapter == null) {
							historyAdapter = new HistoryAdapter(aty, deviceRecord, aty.getCurrentDevice().getTypeName());
							historyListView.setAdapter(historyAdapter);
						} else {
							historyListView.setAdapter(historyAdapter);
							historyAdapter.setData(deviceRecord);
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
	
	private String getPlugPropName(int which) {
		String typeName = aty.getCurrentDevice().getTypeName();
		if (typeName.equalsIgnoreCase(AxalentUtils.TYPE_GUNI_PLUG)) {
			// ����
			return AxalentUtils.ATTRIBUTE_GU_POWER;
		} 
		switch (which) {
		case 0:
			// ����
			return AxalentUtils.ATTRIBUTE_MYSWITCH;
		case 1:
			// ����
			return AxalentUtils.ATTRIBUTE_CURRENT;
		case 2:
			// ����
			return AxalentUtils.ATTRIBUTE_ENERGY;
		case 3:
			// ����
			return AxalentUtils.ATTRIBUTE_POWER;
		case 4:
			// ��ѹ
			return AxalentUtils.ATTRIBUTE_VOLTAGE;
		default:
			// ����
			return "all";
		}
	}
	
	
	class DateSortComparatorByAll implements Comparator<AllRecordMode> {

		@Override
		public int compare(AllRecordMode lhs, AllRecordMode rhs) {
			int result = lhs.time.compareTo(rhs.time);
			if (result < 0) {
				return 1;
			} else if (result > 0) {
				return -1;
			} else {
				return 0;
			}
		}
	}
	
	class AllAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			return allRecordModes.size();
		}

		@Override
		public Object getItem(int position) {
			return allRecordModes.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
			if (convertView == null) {
				convertView = View.inflate(getActivity(), R.layout.adapter_history,  null);
				vh = new ViewHolder(convertView);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			
			AllRecordMode mode = allRecordModes.get(position);
			vh.time.setText(mode.time);
			vh.val.setText(mode.name + " : " + mode.val);
			
			return convertView;
		}
		
		
		private class ViewHolder {
			
			TextView time;
			TextView val;
			
			public ViewHolder(View view) {
				this.time = (TextView) view.findViewById(R.id.adapterHistoryTime);
				this.val = (TextView) view.findViewById(R.id.adapterhistoryNumberVal);
				this.val.setPadding(20, 0, 0, 0);
				this.val.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			}
		}
		
	}
	

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		currentOptionPosition = position;
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
	
	class AllRecordMode {
		String name;
		String time;
		String val;
	}
	
	
}
