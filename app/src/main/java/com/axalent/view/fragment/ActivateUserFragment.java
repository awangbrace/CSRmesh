/**
 * File Name                   : �����û�����
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
import com.axalent.model.User;
import com.axalent.R;
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
import android.widget.TextView;

public class ActivateUserFragment extends Fragment implements UserDispose {
	
	private View view;
	private EditText codeEdit;
	private User user;

	public ActivateUserFragment(User user) {
		this.user = user;
	}
	
	@Override
	public void onAttach(Activity activity) {
		UserActivity userActivity = (UserActivity) activity;
		userActivity.setTitle(R.string.activate_user);
		userActivity.setRightTxtStatus(View.GONE);
		userActivity.setDisposeBtnTxt(R.string.activate);
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_activate_user, null);
			initView();
		}
		return view;
	}
	
	private void initView() {
		codeEdit = (EditText) view.findViewById(R.id.fragActivateCodeEdit);
		codeEdit.setTypeface(Typeface.SANS_SERIF);
		if (user == null) {
			TextView successMsgTxt = (TextView) view.findViewById(R.id.fragActivateSuccessMsgTxt);
			successMsgTxt.setVisibility(View.GONE);
		}
	}

	private void activateUser() {
		String activateCode = codeEdit.getText().toString().trim();
		if (TextUtils.isEmpty(activateCode)) {
			ToastUtils.show(R.string.activate_code_not_null);
			return;
		}
		
		if (activateCode.length() < 3) {
			ToastUtils.show(R.string.activate_code_length_error);
			return;
		}
		
		if (!activateCode.matches("[a-zA-Z0-9]+")) {
			ToastUtils.show(R.string.activate_code_format_error);
			return;
		}
		
		final UserActivity userActivity = (UserActivity) getActivity();
		userActivity.getLoadingDialog().show(R.string.is_the_activate);
		userActivity.getUserManager().activateUser(activateCode, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				String reqCode = XmlUtils.converRequestCode(response);
				userActivity.getLoadingDialog().close();
				if (AxalentUtils.REQUEST_OK.equals(reqCode)) {
					if (user != null) {
						showLoginDialog();
					} else {
						ToastUtils.show(R.string.activate_success);
					}
				} else {
					ToastUtils.show(R.string.activate_failure);
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				userActivity.getLoadingDialog().close();
				ToastUtils.show(XmlUtils.converErrorMsg(error));
			}
		});
	}
	
	
	public void showLoginDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setIcon(R.drawable.app_logo_1);
		builder.setTitle(R.string.activate_success);
		builder.setMessage(R.string.activate_success_is_login);
		builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent();	
				intent.putExtra(AxalentUtils.KEY_USER, user);
				getActivity().setResult(AxalentUtils.REGISTER_SUCCESS, intent);
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

	@Override
	public void onDispose() {
		activateUser();
	}


}
