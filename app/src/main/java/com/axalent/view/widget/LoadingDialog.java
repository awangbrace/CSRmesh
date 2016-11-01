/**
 * File Name                   : ���ؿ�
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.widget;

import com.axalent.R;
import com.axalent.util.AxalentUtils;
import com.axalent.util.LogUtils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

public class LoadingDialog extends Dialog implements DialogInterface.OnCancelListener {
	
	private TextView dialogTxt;
	private ImageView dialogImg;
	private Animation animation = AxalentUtils.getRotateAnimation();

	public LoadingDialog(Context context) {
		super(context, R.style.style_loading_dialog);
		setContentView(R.layout.dialog_loading);
		setCancelable(false);
//		setCanceledOnTouchOutside(false);
//		setOnCancelListener(this);
		AxalentUtils.setWindowState(getWindow(), 0.5f);
		dialogTxt = (TextView) findViewById(R.id.dialogLoadingTxt);
		dialogImg = (ImageView) findViewById(R.id.dialogLoadingImg);
	}
	
	
	public void show(int id) {
		super.show();
		dialogTxt.setText(id);
		dialogImg.startAnimation(animation);
	}
	
	public void show(String id) {
		super.show();
		dialogTxt.setText(id);
		dialogImg.startAnimation(animation);
	}
	
	public void close() {
		super.dismiss();
	}


	@Override
	public void onCancel(DialogInterface dialog) {
		LogUtils.i("cancel request!");
//		ApplicationController.getInstance().cancelPendingRequests();
	}
	
}
