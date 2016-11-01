/**
 * File Name                   : ������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.presenter.controller;

import com.axalent.model.Device;

public interface Manager {
	
	public void notifyPageRefresh();
	
	public void addDevice(Device device);
	
}
