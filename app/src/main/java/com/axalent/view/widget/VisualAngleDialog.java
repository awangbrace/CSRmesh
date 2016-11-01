/**
 * File Name                   : �ӽǴ�����
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.widget;

import com.axalent.R;
import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.view.View;
 
public class VisualAngleDialog extends Dialog implements View.OnClickListener, View.OnLongClickListener{
	
	private Button one, two, three, four;
	private OnClickListener onClickListener;
	private OnLongClickListener onLongClickListener;
	
	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}
	
	public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
		this.onLongClickListener = onLongClickListener;
	}
	
	public VisualAngleDialog(Context context) {
		super(context);
		
		setCancelable(true);
		setTitle(R.string.visual_angle);
		setContentView(R.layout.dialog_visualangle);
		
		one = (Button) findViewById(R.id.dialogVisualAngleOne);
		two = (Button) findViewById(R.id.dialogVisualAngleTwo);
		three = (Button) findViewById(R.id.dialogVisualAngleThree);
		four = (Button) findViewById(R.id.dialogVisualAngleFour);
		
		one.setOnClickListener(this);
		two.setOnClickListener(this);
		three.setOnClickListener(this);
		four.setOnClickListener(this);
		
		one.setOnLongClickListener(this);
		two.setOnLongClickListener(this);
		three.setOnLongClickListener(this);
		four.setOnLongClickListener(this);
		
	}
	

	@Override
	public boolean onLongClick(View v) {
		if (onLongClickListener != null) {
			return onLongClickListener.onLongClick(v);
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		if (onClickListener != null) {
			onClickListener.onClick(v);
		}
	}
	
	public interface OnClickListener {
		void onClick(View v);
	}
	
	public interface OnLongClickListener {
		boolean onLongClick(View v);
	}
	

}
