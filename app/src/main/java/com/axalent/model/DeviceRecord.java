/**
 * File Name                   : ��ʷ��¼
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.model;

import java.util.ArrayList;
import java.util.List;

public class DeviceRecord {
	
	private String devId;
	private String propName;
	private List<Date> dates;
	
	public String getDevId() {
		return devId;
	}
	public void setDevId(String devId) {
		this.devId = devId;
	}
	public String getPropName() {
		return propName;
	}
	public void setPropName(String propName) {
		this.propName = propName;
	}
	public List<Date> getDates() {
		if (dates == null) {
			dates = new ArrayList<Date>();
		}
		return dates;
	}
	public void setDates(List<Date> dates) {
		this.dates = dates;
	}
	
	

}
