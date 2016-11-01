/**
 * File Name                   : ������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/29
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.model;

public class HintError extends Exception {
	
	private static final long serialVersionUID = 1L;
	private String errorCode;
	private String errorMsg;
	
	public HintError() {
	}
	
	public HintError(String errorCode, String errorMsg) {
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}
	public String getErrorCode() {
		if (errorCode == null) {
			errorCode = "";
		}
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	

}
