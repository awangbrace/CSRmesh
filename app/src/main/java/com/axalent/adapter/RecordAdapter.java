/**
 * File Name                   : ¼����¼��Ƶ��������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.adapter;

import java.util.List;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class RecordAdapter extends FragmentPagerAdapter {
	
	private List<Fragment> fragmenrs;
	
	public RecordAdapter(FragmentManager fm, List<Fragment> fragmenrs) {
		super(fm);
		this.fragmenrs = fragmenrs;
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragmenrs.get(arg0);
	}

	@Override
	public int getCount() {
		return fragmenrs.size();
	}
	
	

}
