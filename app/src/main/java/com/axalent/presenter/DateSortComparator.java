/**
 * File Name                   : ��ʱ������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.presenter;

import java.util.Comparator;

import com.axalent.model.Date;

public class DateSortComparator implements Comparator<Date> {
	
	@Override
	public int compare(Date lhs, Date rhs) {
		int result = lhs.getTime().compareTo(rhs.getTime());
		if (result < 0) {
			return 1;
		} else if (result > 0) {
			return -1;
		} else {
			return 0;
		}
	}

}
