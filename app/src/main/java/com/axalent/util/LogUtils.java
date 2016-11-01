/**
 * File Name                   : ��־������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.util;

import com.axalent.BuildConfig;

import android.util.Log;

public class LogUtils {
	
	public static final String TAG = "com/axalent/test";
	public static final boolean DBUG = BuildConfig.DEBUG; 
	
	public static void i(String msg) {
		Log.i(TAG, msg);
	}

	public static void e(String msg) {
		Log.e(TAG, msg);
	}
	
	public static void w(String msg) {
		Log.w(TAG, msg);
	}
	
	public static void d(String msg) {
		Log.d(TAG, msg);
	}

	public static void v(String msg) {
		Log.v(TAG, msg);
	}
	
	
	
	
	
}
