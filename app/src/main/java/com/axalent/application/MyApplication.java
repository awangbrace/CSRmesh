/**
 * File Name                   : Ӧ�ó�����
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/08/20
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

import com.axalent.model.data.database.DBManager;
import com.axalent.presenter.csrapi.MeshLibraryManager;

import cn.jpush.android.api.JPushInterface;

public class MyApplication extends Application {

	private static MyApplication instance;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		JPushInterface.setDebugMode(false);
		JPushInterface.init(this);

		// connect bluetooth channel
		MeshLibraryManager.initInstance(this, MeshLibraryManager.MeshChannel.BLUETOOTH);
	}

	public static synchronized MyApplication getInstance() {
		return instance;
	}

	@Override
	public void onLowMemory() {
		MyCacheData.cacheDatas();
		super.onLowMemory();
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this) ;
	}




}
