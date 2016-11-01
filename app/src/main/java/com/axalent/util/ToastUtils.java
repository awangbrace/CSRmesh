/**
 * File Name                   : Toast ������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.util;

import com.axalent.application.MyApplication;
import com.axalent.model.HintError;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtils {
	
	public static void show(int id) {
		Toast.makeText(MyApplication.getInstance(), id, Toast.LENGTH_SHORT).show();
	}
	
	public static void show(String text) {
		Toast.makeText(MyApplication.getInstance(), text, Toast.LENGTH_SHORT).show();
	}
	
	public static void show(HintError error) {
		show(error.getErrorMsg());
	}
	
	/**
	 * ���� Toast �ڹ����߳��б���ķ���
	 * @param context
	 * @param obj
	 */
	public static void showByTask(final Object obj) {
		if (obj != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Looper.prepare();
					if (obj instanceof Exception) {
						show((HintError) obj);
					} else if (obj instanceof CharSequence) {
						show((String) obj);
					} else {
						show((Integer) obj);
					}
					Looper.loop();
				}
			}).start();
		}
	}

}
