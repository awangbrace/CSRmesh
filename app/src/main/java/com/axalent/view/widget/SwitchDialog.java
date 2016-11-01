/**
 * File Name                   : ���ش�����
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

import android.app.Dialog;
import android.content.Context;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
 
public class SwitchDialog extends Dialog implements OnCheckedChangeListener {
	
	private OnCheckedChangeListener onCheckedChangeListener;
	
	public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
		this.onCheckedChangeListener = onCheckedChangeListener;
	}
	
	public SwitchDialog(Context context, int titleId) {
		super(context);
		setTitle(titleId);
		setCancelable(true);
		
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setGravity(Gravity.CENTER);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		
		Switch mSwitch = new Switch(context);
		mSwitch.setPadding(50, 50, 50, 50);
		mSwitch.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mSwitch.setOnCheckedChangeListener(this);
		
		linearLayout.addView(mSwitch);
		
		addContentView(linearLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (onCheckedChangeListener != null) {
			onCheckedChangeListener.onCheckedChanged(buttonView, isChecked);
		}
	}
	
	public interface OnCheckedChangeListener {
		void onCheckedChanged(CompoundButton buttonView, boolean isChecked);
	}
	

}
