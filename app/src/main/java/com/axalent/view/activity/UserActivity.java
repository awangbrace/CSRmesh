/**
 * File Name                   : �û��������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.axalent.R;
import com.axalent.application.BaseActivity;
import com.axalent.presenter.controller.UserManager;
import com.axalent.presenter.controller.impl.UserManagerImpl;
import com.axalent.view.fragment.ActivateUserFragment;
import com.axalent.view.fragment.RegisterUserFragment;
import com.axalent.view.fragment.AlterPasswordFragment;
import com.axalent.view.fragment.ForgotPasswordFragment;
import com.axalent.view.fragment.SendRecoveryCodeToEmailFragment;
import com.axalent.util.AxalentUtils;
import com.axalent.view.widget.LoadingDialog;

public class UserActivity extends BaseActivity implements OnClickListener {
	
	private int action;
	private Button disposeBtn;
	private Fragment currentFragment;
	public TextView titleTxt, rightTxt;
	private LoadingDialog loadingDialog;
	private UserManager userManager = new UserManagerImpl();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
		action = getIntent().getIntExtra(AxalentUtils.KEY_SKIP, 0);
		initActionBar();
		initView();
		initFragment();
	}
	
	private void initView() {
		loadingDialog = new LoadingDialog(this);
		disposeBtn = (Button) findViewById(R.id.atyUserDisposeBtn);
		disposeBtn.setText(getButtonId());
		disposeBtn.setOnClickListener(this);
	}
	
	
	private void initFragment() {
		currentFragment = getFragment();
		AxalentUtils.commitFragment(this, currentFragment, R.id.atySetUserFrameLayout);
	}

	private Fragment getFragment() {
		switch (action) {
		case AxalentUtils.REGISTER_USER:
			return new RegisterUserFragment();
		case AxalentUtils.ALTER_PASSWORD:
			return new AlterPasswordFragment();
		case AxalentUtils.FORGET_PASSWORD:
			return new ForgotPasswordFragment();
		case AxalentUtils.ACTIVATE_USER:
			return new ActivateUserFragment(null);
		case AxalentUtils.SEND_RECOVERY_CODE_TO_USER_EMAIL:
			return new SendRecoveryCodeToEmailFragment();
		default:
			return null;
		}
	}
	
	private void initActionBar() {
		View customView = findViewById(R.id.barUserContent);
		titleTxt = (TextView) customView.findViewById(R.id.actionBarSetUserTitleTxt);
		titleTxt.setText(getTitleId());
		RelativeLayout back = (RelativeLayout) customView.findViewById(R.id.barUserBack);
		back.setOnClickListener(this);
		if (AxalentUtils.REGISTER_USER == action || AxalentUtils.ALTER_PASSWORD == action || AxalentUtils.SEND_RECOVERY_CODE_TO_USER_EMAIL == action) {
			rightTxt = (TextView) customView.findViewById(R.id.barUserRightTxt);
			rightTxt.setText(getRightTextId());
			rightTxt.setVisibility(View.VISIBLE);
			rightTxt.setOnClickListener(this);
		}
	}
	
	public int getRightTextId() {
		switch (action) {
		case AxalentUtils.REGISTER_USER:
			return R.string.activate;
		case AxalentUtils.ALTER_PASSWORD:
			return R.string.forget_password;
		default:
			return R.string.already_have_forgot_code;
		}
	}
	
	
	public void setTitle(int resid) {
		titleTxt.setText(resid);
	}
	
	public void setRightTxtStatus(int visibility) {
		rightTxt.setVisibility(visibility);
	}
	
	public void setDisposeBtnTxt(int resid) {
		disposeBtn.setText(resid);
	}
	
	private int getTitleId() {
		switch (action) {
		case AxalentUtils.REGISTER_USER:
			return R.string.register_user;
		case AxalentUtils.ALTER_PASSWORD:
			return R.string.change_password_title;
		case AxalentUtils.ACTIVATE_USER:
			return R.string.activate_user;
		case AxalentUtils.FORGET_PASSWORD:
			return R.string.reset_password;
		case AxalentUtils.SEND_RECOVERY_CODE_TO_USER_EMAIL:
			return R.string.forget_password;
		default:
			return R.string.register_user;
		}
	}
	
	
	private int getButtonId() {
		switch (action) {
		case AxalentUtils.REGISTER_USER:
			return R.string.register;
		case AxalentUtils.ALTER_PASSWORD:
			return R.string.alter;
		case AxalentUtils.ACTIVATE_USER:
			return R.string.activate;
		case AxalentUtils.SEND_RECOVERY_CODE_TO_USER_EMAIL:
			return R.string.send;
		case AxalentUtils.FORGET_PASSWORD:
			return R.string.reset;
		default:
			return R.string.register;
		}
	}
	
	
	public LoadingDialog getLoadingDialog() {
		return loadingDialog;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.barUserBack:
			finish();
			break;
		case R.id.barUserRightTxt:
			action = getRightSkipType();
			setCurrentFragment(getFragment());
			AxalentUtils.replaceFragment(this, getCurrentFragment(), R.id.atySetUserFrameLayout);
			break;
		case R.id.atyUserDisposeBtn:
			((UserDispose) currentFragment).onDispose();
			break;
		}
	}
	
	private int getRightSkipType() {
		switch (action) {
		case AxalentUtils.REGISTER_USER:
			return AxalentUtils.ACTIVATE_USER;
		case AxalentUtils.ALTER_PASSWORD:
			return AxalentUtils.SEND_RECOVERY_CODE_TO_USER_EMAIL;
		default:
			return AxalentUtils.FORGET_PASSWORD;
		}
	}

	public UserManager getUserManager() {
		return userManager;
	}
	
	public void setCurrentFragment(Fragment currentFragment) {
		this.currentFragment = currentFragment;
	}
	
	public Fragment getCurrentFragment() {
		return currentFragment;
	}

	public interface UserDispose {
		void onDispose();
	}
	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (AxalentUtils.REGISTER_SUCCESS == resultCode || AxalentUtils.RESTORE_SUCCESS == resultCode) {
//			setResult(resultCode, data);
//		}
//		super.onActivityResult(requestCode, resultCode, data);
//	}
	
	
}
