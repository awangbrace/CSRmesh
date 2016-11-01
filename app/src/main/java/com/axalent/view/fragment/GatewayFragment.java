/**
 * File Name                   : ��ؽ���
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.fragment;

import org.xmlpull.v1.XmlPullParser;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.axalent.R;
import com.axalent.view.activity.ShowDeviceActivity;
import com.axalent.model.Device;
import com.axalent.model.DeviceAttribute;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;
import com.axalent.util.ToastUtils;
import com.axalent.util.XmlUtils;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class GatewayFragment extends Fragment {

	private View view;
	private EditText passwordEdit;
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
			view = inflater.inflate(R.layout.fragment_gateway, null);
			initView();
		}
		return view;
	}
	
	private void initView() {
		passwordEdit = (EditText) view.findViewById(R.id.fragGatewayPasswordEdit);
		Button passwrodBtn = (Button) view.findViewById(R.id.fragGatewayPasswrodBtn);
		passwrodBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setGatewayPassword();
			}
		});
		queryGatewayPassword();
	}
	
	private void queryGatewayPassword() {
		passwordEdit.setText(AxalentUtils.getDeviceAttributeValue(aty.getCurrentDevice(), AxalentUtils.ATTRIBUTE_NETWORKKEY));
		passwordEdit.setTypeface(Typeface.SANS_SERIF);
	}
	
	
	private void setGatewayPassword() {
		final String passwrod = passwordEdit.getText().toString().trim();
		if (TextUtils.isEmpty(passwrod)) {
			ToastUtils.show(R.string.password_not_null);
		} else {
			final Device gateway = aty.getCurrentDevice();
			aty.getDeviceManager().getDeviceAttribute(gateway.getDevId(), AxalentUtils.ATTRIBUTE_NETWORKKEY, new Listener<XmlPullParser>() {
				@Override
				public void onResponse(XmlPullParser response) {
					DeviceAttribute deviceAttribute = XmlUtils.converDeviceAttribute(response);
					String value = deviceAttribute.getValue();
					if (value != null && value.equals(passwrod)) {
						ToastUtils.show(R.string.you_do_not_make_any_change);
					} else {
						setDeviceAttribute(gateway, passwrod);
					}
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					ToastUtils.show(R.string.alter_failure);
				}
			});
		}
	}
	
	private void setDeviceAttribute(final Device gateway, final String password) {
		aty.getDeviceManager().setDeviceAttribute(gateway.getDevId(), AxalentUtils.ATTRIBUTE_NETWORKKEY, password, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				CacheUtils.updateDeviceAttibuteByDevId(gateway.getDevId(), AxalentUtils.ATTRIBUTE_NETWORKKEY, password);
				ToastUtils.show(R.string.alter_success);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				ToastUtils.show(R.string.alter_failure);
			}
		});
	}
	
}
