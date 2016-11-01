/**
 * File Name                   : ���ͻ��������
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
import com.axalent.view.activity.UserActivity;
import com.axalent.view.activity.UserActivity.UserDispose;
import com.axalent.util.AxalentUtils;
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
import android.widget.EditText;
import android.widget.Toast;

public class SendRecoveryCodeToEmailFragment extends Fragment implements UserDispose {
	
	private View view;
	private EditText emailEdit;
	
	@Override
	public void onAttach(Activity activity) {
		UserActivity userActivity = (UserActivity) activity;
		userActivity.setDisposeBtnTxt(R.string.send);;
		userActivity.setTitle(R.string.forget_password);
		userActivity.rightTxt.setText(userActivity.getRightTextId());
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_send_recovery_code_to_email, null);
			emailEdit = (EditText) view.findViewById(R.id.fragSendRecoveryEmailEdit);
			emailEdit.setTypeface(Typeface.SANS_SERIF);
		}
		return view;
	}
	
	
	private void sendRecoveryCodeToEmail(String emailAddress) {
		if (TextUtils.isEmpty(emailAddress)) {
			Toast.makeText(getActivity(), R.string.email_not_null, Toast.LENGTH_SHORT).show();
			return;
		} 
		
		if (!emailAddress.matches(AxalentUtils.MATCHES_EMAIL)) {
			Toast.makeText(getActivity(), R.string.email_format_error, Toast.LENGTH_SHORT).show();
			return;
		}
		
		final UserActivity userActivity = (UserActivity) getActivity();
		userActivity.getLoadingDialog().show(R.string.is_the_send);
		userActivity.getUserManager().requestPasswordRecoveryCode(emailAddress, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				String reqCode = XmlUtils.converRequestCode(response);
				userActivity.getLoadingDialog().close();
				if (AxalentUtils.REQUEST_OK.equals(reqCode)) {
					userActivity.setCurrentFragment(new ForgotPasswordFragment());
					AxalentUtils.replaceFragment(userActivity, userActivity.getCurrentFragment(), R.id.atySetUserFrameLayout);
				} else {
					ToastUtils.show(R.string.send_failure);
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				userActivity.getLoadingDialog().close();
				ToastUtils.show(R.string.send_failure);
			}
		});
	}

	@Override
	public void onDispose() {
		sendRecoveryCodeToEmail(emailEdit.getText().toString().trim());
	}
	

}
