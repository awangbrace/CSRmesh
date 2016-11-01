/**
 * File Name                   : ��������ˢ������ɾ���ͻ
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.presenter.controller;

import com.baoyz.swipemenulistview.SwipeMenuListView.OnSwipeListener;

import android.support.v4.widget.SwipeRefreshLayout;

public class MyOnSwipeListener implements OnSwipeListener {
	
	SwipeRefreshLayout refreshLayout;
	
	public MyOnSwipeListener(SwipeRefreshLayout refreshLayout) {
		this.refreshLayout = refreshLayout;
	}

	@Override
	public void onSwipeEnd(int arg0) {
		setViewState(arg0);
	}

	@Override
	public void onSwipeStart(int arg0) {
		setViewState(arg0);
	}
	
	private void setViewState(int state) {
		refreshLayout.setEnabled(state == -1 ? true : false);
	}

}
