/**
 * File Name                   : WindowCover ����
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.fragment;

import org.xmlpull.v1.XmlPullParser;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.axalent.R;
import com.axalent.view.activity.ShowDeviceActivity;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;
import com.axalent.util.ToastUtils;

public class WindowCoverFragment extends Fragment {
	
	private View view;
	private SeekBar bar;
	private ShowDeviceActivity aty;
	
	@Override
	public void onAttach(Activity activity) {
		aty = (ShowDeviceActivity) activity;
		super.onAttach(activity);
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_window_cover, null);
			initView();
			setDatas();
		}
		return view;
	}
	
	private void initView() {
		bar = (SeekBar) view.findViewById(R.id.fragWindowCoverSeekbar);
		bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				setDeviceAttribute(AxalentUtils.ATTRIBUTE_COVER, String.valueOf(arg0.getProgress()));
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				
			}
		});
	}
	
	private void setDatas() {
		bar.setProgress(getProgress());
	}
	
	private int getProgress() {
		String cover = AxalentUtils.getDeviceAttributeValue(aty.getCurrentDevice(), AxalentUtils.ATTRIBUTE_COVER);
		if (!TextUtils.isEmpty(cover)) {
			return Integer.parseInt(cover);
		}
		return 0;
	}
	
	private void setDeviceAttribute(final String key, final String val) {
		final String updateId = aty.getCurrentDevice().getDevId();
		aty.getDeviceManager().setDeviceAttribute(updateId, key, val, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				CacheUtils.updateDeviceAttibuteByDevId(updateId, key, val);
				aty.getCurrentDevice().setToggle(val);
				aty.sendUpdateCommand(key, val);
				ToastUtils.show(R.string.action_success);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				ToastUtils.show(R.string.action_failure);
			}
		});
	}
	
	
	
}
