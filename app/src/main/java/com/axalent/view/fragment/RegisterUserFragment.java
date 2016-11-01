/**
 * File Name                   : ע�����
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.fragment;

import org.xmlpull.v1.XmlPullParser;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.axalent.R;
import com.axalent.model.User;
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

public class RegisterUserFragment extends Fragment implements UserDispose {
	
	private View view;
	private UserActivity aty;
	private EditText emailEdit, onePassEdit, twoPassEdit;
	
	@Override
	public void onAttach(Activity activity) {
		aty = (UserActivity) activity;
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_register_user, null);
			initViews();
		}
		return view;
	}
	
	private void initViews() {
		emailEdit = (EditText) view.findViewById(R.id.fragRegisterUserEmailEdit);
		onePassEdit = (EditText) view.findViewById(R.id.fragRegisterUserOnePassEdit);
		twoPassEdit = (EditText) view.findViewById(R.id.fragRegisterUserTwoPassEdit);
		onePassEdit.setTypeface(Typeface.SANS_SERIF);
		twoPassEdit.setTypeface(Typeface.SANS_SERIF);
		emailEdit.setTypeface(Typeface.SANS_SERIF);
	}
	
	private void registerUser() {
		String emailStr = emailEdit.getText().toString().trim();
		String passwordOneStr = onePassEdit.getText().toString().trim();
		String passwordTwoStr = twoPassEdit.getText().toString().trim();
		
		if (TextUtils.isEmpty(emailStr)) {
			Toast.makeText(getActivity(), R.string.email_not_null, Toast.LENGTH_SHORT).show();
			return;
		} 
		
		if (!emailStr.matches(AxalentUtils.MATCHES_EMAIL)) {
			Toast.makeText(getActivity(), R.string.email_format_error, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (TextUtils.isEmpty(passwordOneStr)) {
			ToastUtils.show(R.string.password_not_null);
			return;
		}
		
		if (passwordOneStr.length() < 5) {
			ToastUtils.show(R.string.password_length_error);
			return;
		}
		
		if (!passwordOneStr.matches(AxalentUtils.MATCHES_PASSWORD)) {
			ToastUtils.show(R.string.password_format_error);
			return;
		}
		
		if (!passwordOneStr.equals(passwordTwoStr)) {
			ToastUtils.show(R.string.two_assword_not_fit);
			return;
		}
		
		aty.getLoadingDialog().show(R.string.is_the_register);
		final User user = new User();
		user.setUsername(emailStr);
		user.setPassword(passwordOneStr);
		user.setAppId("1101");
		user.setEmail(emailStr);
		aty.getUserManager().registerUser(user, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				String reqCode = XmlUtils.converRequestCode(response);
				if (AxalentUtils.REQUEST_OK.equals(reqCode)) {
					aty.getLoadingDialog().close();
					skipToActivateUser(user);
				} else {
					aty.getLoadingDialog().close();
					ToastUtils.show(R.string.register_failure);
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				aty.getLoadingDialog().close();
				ToastUtils.show(XmlUtils.converErrorMsg(error));
			}
		});
	}

	private void skipToActivateUser(User user) {
		aty.setCurrentFragment(new ActivateUserFragment(user));
		AxalentUtils.replaceFragment(aty, aty.getCurrentFragment(), R.id.atySetUserFrameLayout);
	}

	@Override
	public void onDispose() {
		registerUser();
	}

}
