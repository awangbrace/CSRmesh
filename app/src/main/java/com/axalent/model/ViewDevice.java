/**
 * File Name                   : ��ͼ�豸��
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.model;

import java.util.Map;
import android.widget.CheckBox;
import android.widget.ImageView;

public class ViewDevice extends Device {
	
//	private int position;
	private boolean boxIsChecked;
	private boolean switchIsChecked;
	private boolean enabled = true;
	private ImageView animImg;
	private CheckBox checkedBox;
	private Map<String, String> attributesMap;
//	private int animViewState = View.GONE;
//	private int boxViewState = View.VISIBLE;
//	private int animViewBgId = R.drawable.loading_2;
	
//	public int getPosition() {
//		return position;
//	}
//	public void setPosition(int position) {
//		this.position = position;
//	}
	public boolean isBoxIsChecked() {
		return boxIsChecked;
	}
	public void setBoxIsChecked(boolean boxIsChecked) {
		this.boxIsChecked = boxIsChecked;
	}
	public boolean isSwitchIsChecked() {
		return switchIsChecked;
	}
	public void setSwitchIsChecked(boolean switchIsChecked) {
		this.switchIsChecked = switchIsChecked;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public ImageView getAnimImg() {
		return animImg;
	}
	public void setAnimImg(ImageView animImg) {
		this.animImg = animImg;
	}
	public CheckBox getCheckedBox() {
		return checkedBox;
	}
	public void setCheckedBox(CheckBox checkedBox) {
		this.checkedBox = checkedBox;
	}
	public Map<String, String> getAttributesMap() {
		return attributesMap;
	}
	public void setAttributesMap(Map<String, String> attributesMap) {
		this.attributesMap = attributesMap;
	}
	
	
//	public int getAnimViewState() {
//		return animViewState;
//	}
//	public void setAnimViewState(int animViewState) {
//		this.animViewState = animViewState;
//	}
//	public int getBoxViewState() {
//		return boxViewState;
//	}
//	public void setBoxViewState(int boxViewState) {
//		this.boxViewState = boxViewState;
//	}
//	public int getAnimViewBgId() {
//		return animViewBgId;
//	}
//	public void setAnimViewBgId(int animViewBgId) {
//		this.animViewBgId = animViewBgId;
//	}
	
	
	
}
