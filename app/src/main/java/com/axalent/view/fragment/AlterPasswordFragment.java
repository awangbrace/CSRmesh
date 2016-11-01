/**
 * File Name                   : �޸��������
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

public class AlterPasswordFragment extends Fragment implements UserDispose {
	
	private View view;
	private UserActivity aty;
	private EditText emailEdit, oldPassEdit, oneNewPassEdit, twoNewPassEdit;
	
	@Override
	public void onAttach(Activity activity) {
		aty = (UserActivity) activity;
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_alter_password, null);
			initView();
		}
		return view;
	}
	
	private void initView() {
		emailEdit = (EditText) view.findViewById(R.id.fragAlterPasswordEmailEdit);
		oldPassEdit = (EditText) view.findViewById(R.id.fragAlterOldPasswordEdit);
		oneNewPassEdit = (EditText) view.findViewById(R.id.fragAlterOneNewPasswordEdit);
		twoNewPassEdit = (EditText) view.findViewById(R.id.fragAlterTwoNewPasswordEdit);
		emailEdit.setTypeface(Typeface.SANS_SERIF);
		oldPassEdit.setTypeface(Typeface.SANS_SERIF);
		oneNewPassEdit.setTypeface(Typeface.SANS_SERIF);
		twoNewPassEdit.setTypeface(Typeface.SANS_SERIF);
	}
	

	private void alterPassword() {
		
		final String emailStr = emailEdit.getText().toString().trim();
		final String oldPasswordStr = oldPassEdit.getText().toString().trim();
		final String oneNewPasswordStr = oneNewPassEdit.getText().toString().trim();
		final String twoNewPasswordStr = twoNewPassEdit.getText().toString().trim();
		
		if (TextUtils.isEmpty(emailStr)) {
			ToastUtils.show(R.string.username_not_null);
			return;
		}
		
		if (!emailStr.matches(AxalentUtils.MATCHES_EMAIL)) {
			ToastUtils.show(R.string.username_format_error);
			return;
		}
		
		if (TextUtils.isEmpty(oldPasswordStr)) {
			ToastUtils.show(R.string.old_password_not_null);
			return;
		}
		
		if (oldPasswordStr.length() < 5) {
			ToastUtils.show(R.string.old_password_length_error);
			return;
		}
		
		if (!oldPasswordStr.matches(AxalentUtils.MATCHES_PASSWORD)) {
			ToastUtils.show(R.string.old_password_format_error);
			return;
		}
		
		if (TextUtils.isEmpty(oneNewPasswordStr)) {
			ToastUtils.show(R.string.new_password_not_null);
			return;
		}
		
		if (oneNewPasswordStr.length() < 5) {
			ToastUtils.show(R.string.new_password_length_error);
			return;
		}
		
		if (!oneNewPasswordStr.matches(AxalentUtils.MATCHES_PASSWORD)) {
			ToastUtils.show(R.string.new_password_format_error);
			return;
		}
		
		if (!oneNewPasswordStr.equals(twoNewPasswordStr)) {
			ToastUtils.show(R.string.two_assword_not_fit);
			return;
		}
		
		aty.getLoadingDialog().show(R.string.is_the_alter);
		login(new User(emailStr, oldPasswordStr), oneNewPasswordStr);
	}
	
	private void login(final User user, final String newPasswrod) {
		aty.getUserManager().userLogin(user, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				User tempUser = XmlUtils.convertUserLogin(response);
				if (!TextUtils.isEmpty(tempUser.getSecurityToken())) {
					tempUser.setUsername(user.getUsername());
					tempUser.setPassword(user.getPassword());
					alterPassword(tempUser, newPasswrod);
				} else {
					aty.getLoadingDialog().close();
					ToastUtils.show(R.string.alter_failure);
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

	private void alterPassword(User user, String newPassword) {
		aty.getUserManager().updatePassword(user, newPassword, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				String reqCode = XmlUtils.converRequestCode(response);
				aty.getLoadingDialog().close();
				if (AxalentUtils.REQUEST_OK.equals(reqCode)) {
					ToastUtils.show(R.string.alter_success);
				} else {
					ToastUtils.show(R.string.alter_failure);
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				ToastUtils.show(XmlUtils.converErrorMsg(error));
			}
		});
	
		
		
	}

	@Override
	public void onDispose() {
		alterPassword();
	}
}
