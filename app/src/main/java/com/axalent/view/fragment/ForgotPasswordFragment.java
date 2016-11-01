/**
 * File Name                   : ����������
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
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotPasswordFragment extends Fragment implements UserDispose {
	
	private View view;
	private EditText codeEdit, emailEdit, psssEdit;
	
	@Override
	public void onAttach(Activity activity) {
		UserActivity userActivity = (UserActivity) activity;
		userActivity.setDisposeBtnTxt(R.string.reset);
		userActivity.setTitle(R.string.reset_password);
		userActivity.setRightTxtStatus(View.GONE);
		super.onAttach(activity);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_forgot_password, null);
			codeEdit = (EditText) view.findViewById(R.id.fragForgotPasswordCodeEdit);
			emailEdit = (EditText) view.findViewById(R.id.fragForgotPasswordEmailEdit);
			psssEdit = (EditText) view.findViewById(R.id.fragForgotPasswordPassEdit);
			codeEdit.setTypeface(Typeface.SANS_SERIF);
			emailEdit.setTypeface(Typeface.SANS_SERIF);
			psssEdit.setTypeface(Typeface.SANS_SERIF);
		}
		return view;
	}

	@Override
	public void onDispose() {
		forgotPassword();
	}
	
	private void forgotPassword() {
		
		String code = codeEdit.getText().toString().trim();
		String email = emailEdit.getText().toString().trim();
		String pass = psssEdit.getText().toString().trim();
		
		if (TextUtils.isEmpty(code)) {
			Toast.makeText(getActivity(), R.string.forgot_code_not_null, Toast.LENGTH_SHORT).show();
			return;
		} 
		
		if (TextUtils.isEmpty(email)) {
			Toast.makeText(getActivity(), R.string.username_not_null, Toast.LENGTH_SHORT).show();
			return;
		} 
		
		if (TextUtils.isEmpty(pass)) {
			Toast.makeText(getActivity(), R.string.password_not_null, Toast.LENGTH_SHORT).show();
			return;
		} 
		
		if (pass.length() < 5) {
			Toast.makeText(getActivity(), R.string.password_length_error, Toast.LENGTH_SHORT).show();
			return;
		}
		
		final User user = new User();
		user.setCode(code);
		user.setUsername(email);
		user.setPassword(pass);
		
		final UserActivity userActivity = (UserActivity) getActivity();
		userActivity.getLoadingDialog().show(R.string.is_the_reset_password);
		userActivity.getUserManager().updateUserPassword(user, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				String reqCode = XmlUtils.converRequestCode(response);
				userActivity.getLoadingDialog().close();
				if (AxalentUtils.REQUEST_OK.equals(reqCode)) {
					showLoginDialog(user);
				} else {
					Toast.makeText(userActivity, R.string.reset_failure, Toast.LENGTH_LONG).show();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				userActivity.getLoadingDialog().close();
				ToastUtils.show(R.string.reset_failure);
			}
		});
		
	}
	
	public void showLoginDialog(final User user) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setIcon(R.drawable.app_logo_1);
		builder.setTitle(R.string.reset_success);
		builder.setMessage(R.string.reset_password_success_is_login);
		builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent data = new Intent();	
				data.putExtra(AxalentUtils.KEY_USER, user);
				getActivity().setResult(AxalentUtils.RESTORE_SUCCESS, data);
				getActivity().finish();
				onLowMemory();
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// ȡ��
			}
		});
		builder.create().show();
	}

}
