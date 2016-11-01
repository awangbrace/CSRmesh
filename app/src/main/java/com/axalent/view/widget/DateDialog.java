/**
 * File Name                   : ʱ�䴰����
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.widget;

import java.util.Calendar;
import com.axalent.util.AxalentUtils;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class DateDialog {
	
	private Context ctx;
	private int currentSelectId;
	private DatePickerDialog dateDialog;
	private TimePickerDialog timeDialog;
	private Calendar c = Calendar.getInstance();
	private StringBuilder sb = new StringBuilder();
	private OnSelectedDateListener onSelectedDateListener;
	
	public DateDialog(Context ctx) {
		this.ctx = ctx;
	}
	
	public void setCurrentSelectId(int currentSelectId) {
		this.currentSelectId = currentSelectId;
	}
	
	public void show() {
		showDateDialog();
	}
	
	public void setOnSelectedDateListener(OnSelectedDateListener onSelectedDateListener) {
		this.onSelectedDateListener = onSelectedDateListener;
	}

	private void showDateDialog() {
		sb.setLength(0);
		if (dateDialog == null) {
			dateDialog = new DatePickerDialog(ctx, new OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker dp, int year, int month, int dayofMonth) {
					if (sb.length() == 0) {
						sb.append(year+"-"+AxalentUtils.formatDate(true, month)+"-"+AxalentUtils.formatDate(false, dayofMonth)+" ");
						showTimeDialog(sb.length());
					}
				}
			}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
		}
		dateDialog.show();
	}
	
	private void showTimeDialog(final int length) {
		if (timeDialog == null) { 
			timeDialog = new TimePickerDialog(ctx, new OnTimeSetListener() {
				@Override
				public void onTimeSet(TimePicker tp, int hourOfDay, int minute) {
					if (sb.length() == length) {
						sb.append(AxalentUtils.formatDate(false, hourOfDay)+":"+AxalentUtils.formatDate(false, minute));
						onSelectedDateListener.onSelectedDate(currentSelectId, sb.toString());
					}
				}
			}, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
		}
		timeDialog.show();
	}
	
	public interface OnSelectedDateListener {
		void onSelectedDate(int currentSelectId, String date);
	}


}
