/**
 * File Name                   : ����������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class HorizontalText extends TextView {
	
	public HorizontalText(Context context) {  
		 super(context);  
	}

	public HorizontalText(Context context, AttributeSet attrs) {  
		 super(context, attrs);  
	}

	public HorizontalText(Context context, AttributeSet attrs, int defStyle) {  
		 super(context, attrs, defStyle);  
	}

	@Override
	public boolean isFocused() {
		return true;
	}
}
