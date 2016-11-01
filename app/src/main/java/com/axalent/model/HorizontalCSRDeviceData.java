/**
 * File Name                   : ����ListView���ģ��
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.model;

import android.widget.BaseAdapter;

import com.axalent.model.data.model.devices.CSRDevice;

import java.util.List;

public class HorizontalCSRDeviceData extends Model {

	private int index;
	private int offSet;
	private List<CSRDevice> devices;
	private BaseAdapter adapter;


	public HorizontalCSRDeviceData(List<CSRDevice> devices, int index, int offSet, BaseAdapter adapter) {
		this.devices = devices;
		this.index = index;
		this.offSet = offSet;
		this.adapter = adapter;
	}
	
	public List<CSRDevice> getDevices() {
		return devices;
	}

	public void setDevices(List<CSRDevice> devices) {
		this.devices = devices;
	}

	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getOffSet() {
		return offSet;
	}
	
	public void setOffSet(int offSet) {
		this.offSet = offSet;
	}

	public BaseAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
	}

	
}
