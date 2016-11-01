/**
 * File Name                   : ������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.model;

import java.util.List;

public class Group extends Device {
	
	private Device device;
	private Trigger trigger;
	private List<Device> childs;
	
	public Group() {
		// TODO Auto-generated constructor stub
	}
	
	public Group(Device device,  List<Device> childs) {
		this.device = device;
		this.childs = childs;
	}
	
	public Group(Device device, Trigger trigger, List<Device> childs) {
		this.device = device;
		this.trigger = trigger;
		this.childs = childs;
	}
	
	public Device getDevice() {
		return device;
	}
	public void setDevice(Device device) {
		this.device = device;
	}
	public Trigger getTrigger() {
		return trigger;
	}
	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}
	public List<Device> getChilds() {
		return childs;
	}
	public void setChilds(List<Device> childs) {
		this.childs = childs;
	}
	
	
	

}
