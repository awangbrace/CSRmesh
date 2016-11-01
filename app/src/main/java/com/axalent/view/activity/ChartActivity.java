/**
 * File Name                   : ͼ�����
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
import org.xmlpull.v1.XmlPullParser;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.axalent.R;
import com.axalent.application.BaseActivity;
import com.axalent.presenter.controller.DeviceManager;
import com.axalent.presenter.controller.impl.DeviceManagerImpl;
import com.axalent.model.Date;
import com.axalent.model.Device;
import com.axalent.model.DeviceRecord;
import com.axalent.util.AxalentUtils;
import com.axalent.util.LogUtils;
import com.axalent.util.ToastUtils;
import com.axalent.util.XmlUtils;
import com.axalent.view.widget.DateDialog;
import com.axalent.view.widget.LoadingDialog;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class ChartActivity extends BaseActivity implements OnClickListener, 
    LineChartOnValueSelectListener, OnItemSelectedListener, ViewportChangeListener {
	
	private static final String TAG = "ChartActivity";
	// �豸
	private Device device;
	// ��ʾ���Ե�ͼ��
	private LineChartView lineChartView;
	// ���
	private LineChartData chartData = new LineChartData();
	// ѡ���Item
	private int selectItem = 0;
	// ����Dialog
	private LoadingDialog loadingDialog;
	// �豸����
	private DeviceManager deviceManager = new DeviceManagerImpl();
	// ��ѯ��Item
	private String[] items;
	// ��ʽ���ַ�ĸ��������
//	private DecimalFormat floatFormat = new DecimalFormat("##0.00");
	// ��ʼ����ʱ��
	private TextView startDateTxt, endDateTxt;
	// ʱ�䴰��
	private DateDialog dateDialog;
	// ѡ�����ԵĴ���
	private Spinner attributeSpinner;
	// ��ѯ��ť
	private Button chartShowBtn;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		initActionBar();
		
		setContentView(R.layout.activity_chart);
		
		device = getIntent().getParcelableExtra(AxalentUtils.KEY_DEVICE);
		String typeName = device.getTypeName();
		
		if (typeName.equalsIgnoreCase(AxalentUtils.TYPE_POWER_PLUG)) {
			items = new String[]{getString(R.string.energy), getString(R.string.power)};
		} else if (typeName.equalsIgnoreCase(AxalentUtils.TYPE_HTS)) {
			items = new String[]{getString(R.string.temperature), getString(R.string.humidity)};
		} else {
			items = new String[]{getString(R.string.power)};
		}
		
		lineChartView = (LineChartView) findViewById(R.id.chartView);
		lineChartView.setViewportCalculationEnabled(true);
        lineChartView.setValueSelectionEnabled(true);
        lineChartView.setViewportChangeListener(this);
		lineChartView.setOnValueTouchListener(this);
		lineChartView.setScrollEnabled(true);
		lineChartView.setMaxZoom(50f);
		lineChartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
		lineChartView.setLineChartData(chartData);
		
		startDateTxt = (TextView) findViewById(R.id.includeStartDate);
		endDateTxt = (TextView) findViewById(R.id.includeEndDate);
		startDateTxt.setOnClickListener(this);
		endDateTxt.setOnClickListener(this);
		
		attributeSpinner = (Spinner) findViewById(R.id.includeAttributeSpinner);
		attributeSpinner.setAdapter(new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_1, items));
		attributeSpinner.setOnItemSelectedListener(this);
		
		chartShowBtn = (Button) findViewById(R.id.includeChartShowBtn);
		chartShowBtn.setOnClickListener(this);
		
		loadingDialog = new LoadingDialog(this);
		
		setCurrentDate();
		loadData();
		
	}
	
	private void setCurrentDate() {
		if (device != null) {
			startDateTxt.setText(AxalentUtils.getSystemTime(1));
			endDateTxt.setText(AxalentUtils.getSystemTime());
		}
	}
	
	private void initActionBar() {
		View content = findViewById(R.id.barChartContent);
		View back = content.findViewById(R.id.barChartBack);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.barChartBack:
			finish();
			break;
		case R.id.includeStartDate:
		case R.id.includeEndDate:
			showDataDialog(id);
			break;
		case R.id.includeChartShowBtn:
			loadData();
			break;
		}
	}
	
	private void showDataDialog(int id) {
		if (dateDialog == null) {
			dateDialog = new DateDialog(this);
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
	
	
	/**
	 * �������
	 */
	private void loadData() {
		if (device != null) {
			loadingDialog.show(R.string.load_data);
			deviceManager.getDeviceTS2(device.getDevId(), 
					selectItem == 0? getAttribute()[0] : getAttribute()[1], 
							AxalentUtils.formatTimeToUtc(startDateTxt.getText().toString().trim()) + "", 
							AxalentUtils.formatTimeToUtc(endDateTxt.getText().toString().trim()) + "",
							loadSuccess, 
							loadError);
		} else {
			ToastUtils.show(R.string.load_failure);
		}
	}
	
	private String[] getAttribute() {
		String[] attributes = null;
		String typeName = device.getTypeName();
		if (typeName.equals(AxalentUtils.TYPE_POWER_PLUG)) {
			attributes = new String[]{AxalentUtils.ATTRIBUTE_ENERGY, AxalentUtils.ATTRIBUTE_POWER};
		} else if (typeName.equalsIgnoreCase(AxalentUtils.TYPE_HTS)) {
			attributes = new String[]{AxalentUtils.ATTRIBUTE_TEMPERATURE, AxalentUtils.ATTRIBUTE_HUMIDITY};
		} else {
			attributes = new String[]{AxalentUtils.ATTRIBUTE_GU_POWER};
		}
		return attributes;
	}
	
	private Listener<XmlPullParser> loadSuccess = new Listener<XmlPullParser>() {

		@Override
		public void onResponse(XmlPullParser response) {
			DeviceRecord deviceRecord = XmlUtils.convertDeviceTS2(response);
			List<Date> dates = deviceRecord.getDates();
			int size = dates.size();
			
			if (size > 0) {
//				if (size > 10) size = 10;
				// ����ʷ��¼ 
				float top = 10;
				
				List<AxisValue> axisValues = new ArrayList<AxisValue>();
				List<PointValue> pointValues = new ArrayList<PointValue>();
				
				LogUtils.i("size:"+size);
				
				for (int i = 0; i < size; i++) {
					
					String date = AxalentUtils.formatUtcToTime(
							Long.parseLong(dates.get(i).getTime()));
					
					int start = date.indexOf("-") + 1;
					int end = date.length();
					String month = date.substring(start, end);
					
					LogUtils.i("month:"+month);
					
					axisValues.add(new AxisValue(i)
							.setLabel(month));
					
					float fValue = Float.parseFloat(dates.get(i).getValue());
					LogUtils.i("����ǰvalue:"+fValue);
					
					if (device.getTypeName().equals(AxalentUtils.TYPE_HTS)) {
						fValue /= 100;
					}
					
					LogUtils.i("�����value:"+fValue);
					
					pointValues.add(new PointValue(i, fValue));
					
					if (fValue > top) {
						top = fValue;
					}
				}
				
				
				List<Line> lines = new ArrayList<Line>();
				
				Line line = new Line(pointValues);
	            line.setColor(ChartUtils.COLOR_BLUE);
	            line.setShape(ValueShape.CIRCLE);
	            line.setCubic(true);
	            line.setFilled(true);
	            line.setHasLabels(true);
	            line.setHasLines(true);
	            line.setHasPoints(true);
	            line.setHasLabelsOnlyForSelected(false);
	            
//	            line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
	            lines.add(line);
	            
		        chartData.setLines(lines);
		        chartData.setBaseValue(Float.NEGATIVE_INFINITY);
		        
				chartData.setAxisXBottom(new Axis(axisValues)
						.setHasLines(true)
						.setName(getString(R.string.time))
						.setTypeface(Typeface.DEFAULT_BOLD)
						.setTextColor(Color.BLACK)
						.setMaxLabelChars(10));
				
				chartData.setAxisYLeft(new Axis()
						.setHasLines(true)
						.setName(getString(R.string.number_value) + getUnit())
						.setTextColor(Color.BLACK)
						.setTypeface(Typeface.DEFAULT_BOLD)
						.setMaxLabelChars(5));
						
				
				LogUtils.i("top:"+top);
				
		        Viewport v = new Viewport(0, top, size, 0);
		        lineChartView.setMaximumViewport(v);
	            lineChartView.setCurrentViewport(v);
		        lineChartView.setLineChartData(chartData);
		        lineChartView.setZoomLevel(v.centerX(), v.centerY(), 1f);
//		        lineChartView.moveTo(335f, 335f);
		        
		        loadingDialog.close();
			} else {
				// ����ʷ��¼
				loadingDialog.close();
				ToastUtils.show(R.string.not_historical_record);
			}
			
		}
	};
	
	private ErrorListener loadError = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			loadingDialog.close();
			ToastUtils.show(R.string.load_failure);
		}
		
	};
	
	private String getUnit() {
		if (device.getTypeName().equals(AxalentUtils.TYPE_HTS)) {
			return selectItem == 0 ? " (temperature)" : " (humidity)";
		} else {
			return selectItem == 0 ? " (Wh)" : " (mW)";
		}
	}
	

	@Override
	public void onValueDeselected() {
		
	}

	@Override
	public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
		
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		selectItem = position;
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}

	@Override
	public void onViewportChanged(Viewport viewport) {
//		LogUtils.i("left:"+viewport.left);
//		LogUtils.i("right:"+viewport.right);
	}
    

}
