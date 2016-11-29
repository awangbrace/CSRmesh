/**
 * File Name                   : ��¼����
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.activity;

import org.xmlpull.v1.XmlPullParser;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.axalent.R;
import com.axalent.application.BaseActivity;
import com.axalent.application.MyCacheData;
import com.axalent.model.User;
import com.axalent.presenter.DiscoveryGateway;
import com.axalent.presenter.SyncLocalData;
import com.axalent.presenter.controller.UserManager;
import com.axalent.presenter.controller.impl.UserManagerImpl;
import com.axalent.presenter.csrapi.LogLevel;
import com.axalent.presenter.csrapi.MeshLibraryManager;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;
import com.axalent.util.LogUtils;
import com.axalent.util.ToastUtils;
import com.axalent.util.XmlUtils;
import com.axalent.view.widget.LoadingDialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class LoginActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener {

	private UserManager userManager = new UserManagerImpl();
	private EditText usernameEdit;
	private EditText passwordEdit;
	private TextView changePasswordTxt;
	private TextView createAccountTxt;
	private TextView gatewayConfigTxt;
	private CheckBox remPasswordBox;
	private RadioGroup changeLoginGroup;
	private LoadingDialog loadingDialog;
	private SharedPreferences sp;
	private int exit;
	private DiscoveryGateway discoveryGateway;
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initView();
		loadUserInfo();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.atyLoginLoginBtn:
			userLogin();
			break;
		case R.id.atyLoginCreateAccountTxt:
			skipToRegisterUser();
			break;
		case R.id.atyLoginChangePasswordTxt:
			skipAlterPassword();
			break;
		case R.id.atyLoginGatewayTxt:
			skipGatewayConfig();
			break;
		}
	}

	private void initView() {
		usernameEdit = (EditText) findViewById(R.id.atyLoginUsernameEdit);
		passwordEdit = (EditText) findViewById(R.id.atyLoginPasswordEdit);
		remPasswordBox = (CheckBox) findViewById(R.id.atyLoginRememberMeBox);
		usernameEdit.setTypeface(Typeface.SANS_SERIF);
		passwordEdit.setTypeface(Typeface.SANS_SERIF);
		passwordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
		Button loginBtn = (Button) findViewById(R.id.atyLoginLoginBtn);
		changePasswordTxt = (TextView) findViewById(R.id.atyLoginChangePasswordTxt);
		createAccountTxt = (TextView) findViewById(R.id.atyLoginCreateAccountTxt);
		gatewayConfigTxt = (TextView) findViewById(R.id.atyLoginGatewayTxt);
		changeLoginGroup = (RadioGroup) findViewById(R.id.atyLoginChangeGroup);

		loginBtn.setOnClickListener(this);
		changePasswordTxt.setOnClickListener(this);
		createAccountTxt.setOnClickListener(this);
		gatewayConfigTxt.setOnClickListener(this);
		loadingDialog = new LoadingDialog(this);
	}

	private void loadUserInfo() {
		sp = getSharedPreferences(AxalentUtils.USER_FILE_NAME, MODE_PRIVATE);
		exit = getIntent().getIntExtra("exit", -1);
		boolean flag = sp.getBoolean("isRemember", false);
		if (flag) {
			usernameEdit.setText(sp.getString("username", ""));
			passwordEdit.setText(sp.getString("password", ""));
		}
		remPasswordBox.setChecked(flag);
		boolean isRemote = sharedPreferences.getBoolean("isRemote", true);
		int loginMode = sp.getInt("loginMode", R.id.atyLoginCloudBtn);
		if (loginMode == R.id.atyLoginBluetoothBtn) {
			loginMode = R.id.atyLoginCloudBtn;
		}
		if (!isRemote && exit == -1) {
			searchGateway();
		}
		changeLoginGroup.check(loginMode);
		changeLoginGroup.setOnCheckedChangeListener(this);
		setModeView(loginMode, false);
	}

	private void saveUserInfo(User user) {
		LogUtils.e("saveUserInfo");
		boolean isRemember = remPasswordBox.isChecked();
		boolean isChange = false;
		SharedPreferences.Editor editor = sp.edit();

		if (isRemember) {
			String username = sp.getString("username", "");
			if (!username.equals(user.getUsername())) {
				editor.putString("username", user.getUsername());
				isChange = true;
			}

			String password = sp.getString("password", "");
			if (!password.equals(user.getPassword())) {
				editor.putString("password", user.getPassword());
				isChange = true;
			}
		}

		boolean remember = sp.getBoolean("isRemember", false);

		if (remember != isRemember) {
			editor.putBoolean("isRemember", isRemember);
			isChange = true;
		}
		LogUtils.e("isChange: " + isChange);
		if (isChange) {
			boolean result = editor.commit();
			LogUtils.e("result:" + result);
		}
	}

	private void userLogin() {
		final String username = usernameEdit.getText().toString().trim();
		final String password = passwordEdit.getText().toString().trim();

		switch (changeLoginGroup.getCheckedRadioButtonId()) {
			case R.id.atyLoginCloudBtn:

				if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
					ToastUtils.show(R.string.username_or_password_is_null);
					return;
				}
				login(new User(username, password));
				break;
//			case R.id.atyLoginGatewayBtn:
//
//
//				if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
//					ToastUtils.show(R.string.username_or_password_is_null);
//					return;
//				}
//				final String localUrl = sp.getString("localUrl", "");
//				if (TextUtils.isEmpty(localUrl)) {
//					showConfigLocalIPDialog();
//				} else {
//					login(new User(username, password));
//				}
//				break;
			case R.id.atyLoginBluetoothBtn:
				if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
					ToastUtils.show(R.string.username_or_password_is_null);
					return;
				}
				login(new User(username, password));
				break;

		}
	}
	
	private void login(final User user) {
		loadingDialog.show(R.string.is_the_login);
		userManager.userLogin(user, new Response.Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				User resultUser = XmlUtils.convertUserLogin(response);
				if (!TextUtils.isEmpty(resultUser.getSecurityToken())) {
					resultUser.setUsername(user.getUsername());
					resultUser.setPassword(user.getPassword());
					CacheUtils.saveUser(resultUser);
					MyCacheData.getInstance().setCacheUser(resultUser);
					skipToHome();
					loadingDialog.close();
					saveUserInfo(user);
					finish();
				} else {
					loadingDialog.close();
					ToastUtils.show(R.string.login_token_is_null);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				loadingDialog.close();
//				if (changeLoginGroup.getCheckedRadioButtonId() == R.id.atyLoginGatewayBtn) {
//					showSyncDialog(user);
//				} else {
					ToastUtils.show(XmlUtils.converErrorMsg(error));
//				}
			}
		});
	}

	private void skipBluetoothControl() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	private void skipToHome() {
		startActivity(new Intent(LoginActivity.this, HomeActivity.class));
	}

	private void skipToRegisterUser() {
		Intent intent = new Intent(this, UserActivity.class);
		intent.putExtra(AxalentUtils.KEY_SKIP, AxalentUtils.REGISTER_USER);
		startActivityForResult(intent, 0);
	}

	private void skipAlterPassword() {
		Intent intent = new Intent(this, UserActivity.class);
		intent.putExtra(AxalentUtils.KEY_SKIP, AxalentUtils.ALTER_PASSWORD);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case AxalentUtils.REGISTER_SUCCESS:
		case AxalentUtils.RESTORE_SUCCESS:
			if (!remPasswordBox.isChecked()){
				remPasswordBox.setChecked(true);
			}
			User user = data.getParcelableExtra(AxalentUtils.KEY_USER);
			usernameEdit.setText(user.getUsername());
			passwordEdit.setText(user.getPassword());
			userLogin();
			break;
		case AxalentUtils.SWITCH_GATEWAY_WIFI_SUCCESS:
			ToastUtils.show(getString(R.string.config_success));
			break;
		}


		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		setModeView(checkedId, true);
		sp.edit().putInt("loginMode", checkedId).commit();
	}

	private void setModeView(int id, boolean toast) {
		switch (id) {
			case R.id.atyLoginCloudBtn:
				changePasswordTxt.setVisibility(View.VISIBLE);
				changePasswordTxt.setText(R.string.change_password);
				usernameEdit.setVisibility(View.VISIBLE);
				passwordEdit.setVisibility(View.VISIBLE);
				remPasswordBox.setVisibility(View.VISIBLE);
				sharedPreferences.edit().putBoolean("isRemote", true).commit();
				break;
//			case R.id.atyLoginGatewayBtn:
//				changePasswordTxt.setVisibility(View.VISIBLE);
//				changePasswordTxt.setText(R.string.gateway_config);
//				usernameEdit.setVisibility(View.VISIBLE);
//				passwordEdit.setVisibility(View.VISIBLE);
//				remPasswordBox.setVisibility(View.VISIBLE);
//				sharedPreferences.edit().putBoolean("isRemote", false).commit();
//				if (exit == -1) {
//					searchGateway();
//				}
//				break;
			case R.id.atyLoginBluetoothBtn:
				sharedPreferences.edit().putBoolean("isRemote", true).commit();
				skipBluetoothControl();
				break;
		}
	}

	private void searchGateway() {
		if (discoveryGateway == null) {
			loadingDialog.show(R.string.is_the_search);
			discoveryGateway = new DiscoveryGateway(this) {
				@Override
				public void onResp(String url) {
					handler.removeCallbacks(searchTimeout);
					discoveryGateway.stop();
					sharedPreferences.edit().putString("localUrl", url).commit();
					loadingDialog.close();
				}
			};
			discoveryGateway.start();
			handler.postDelayed(searchTimeout, 6000);
		}
	}

	private Runnable searchTimeout = new Runnable() {
		@Override
		public void run() {
			discoveryGateway.stop();
			showNoDiscoveryGateway();
			loadingDialog.close();
		}
	};

	private void showNoDiscoveryGateway() {
		new AlertDialog.Builder(this).setIcon(R.drawable.app_logo_1).setTitle(R.string.no_discovery_gateway)
				.setMessage(R.string.no_discovery_gateway_find)
				.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						skipGatewayConfig();
					}
				}).setNegativeButton(R.string.cancel, null).create().show();
	}

	private void skipGatewayConfig() {
		Intent intent = new Intent(this, SwitchWifiGuideActivity.class);
		startActivity(intent);
	}

	private void showSyncDialog(final User user) {
		new AlertDialog.Builder(this)
		.setIcon(R.drawable.app_logo_1)
		.setTitle(R.string.sync_account)
		.setMessage(R.string.whether_need_sync)
		.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				syncLocalData(user);
			}
		})
		.setNegativeButton(R.string.cancel, null)
		.create()
		.show();
	}
	
	private void showConfigLocalIPDialog() {
		new AlertDialog.Builder(this)
		.setIcon(R.drawable.app_logo_1)
		.setTitle(R.string.config_ip)
		.setMessage(R.string.hint_config_local_ip)
		.setPositiveButton(R.string.confirm, null)
		.create()
		.show();
	}

	private void syncLocalData(final User user) {
		loadingDialog.show(R.string.is_the_sync);
		SyncLocalData syncLocalData = new SyncLocalData();
		syncLocalData.execute(AxalentUtils.getUrl() + "syncData", user);
		syncLocalData.setSyncDataListener(new SyncLocalData.SyncDataListener() {
			@Override
			public void syncDataListener(XmlPullParser result) {
				LogUtils.i("sync data:"+result);
				loadingDialog.close();
				String reqCode = XmlUtils.converRequestCode(result);
				if (AxalentUtils.REQUEST_OK.equals(reqCode)) {
					login(user);
				} else {
					ToastUtils.show(R.string.sync_failure);
				}
			}
		});
	}

}
