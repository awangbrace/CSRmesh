package com.axalent.view.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.axalent.R;
import com.axalent.application.BaseActivity;
import com.axalent.application.MyCacheData;
import com.axalent.model.User;
import com.axalent.presenter.controller.UserManager;
import com.axalent.presenter.controller.impl.UserManagerImpl;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;
import com.axalent.util.LogUtils;
import com.axalent.util.ToastUtils;
import com.axalent.util.XmlUtils;
import com.axalent.view.widget.LoadingDialog;

import org.xmlpull.v1.XmlPullParser;

/**
 * File Name                   : AddAccountActivity
 * Author                      : franky
 * Version                     : 1.0.0
 * Date                        : 2016/10/18
 * Revision History            : 14:17
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd.
 * All rights reserved.
 */

public class AddAccountActivity extends BaseActivity implements View.OnClickListener {

    private UserManager userManager = new UserManagerImpl();
    private EditText usernameEdit;
    private EditText passwordEdit;
    private Button loginButton;
    private LoadingDialog loadingDialog;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        initActionBar();
        initView();
    }

    private void initActionBar() {
        View content = findViewById(R.id.barAccountContent);
        TextView addAccount = (TextView) content.findViewById(R.id.barAccountTitleTxt);
        TextView cancelGroup = (TextView) content.findViewById(R.id.barCancel);

        addAccount.setText(getString(R.string.add_account));

        cancelGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        sp = getSharedPreferences(AxalentUtils.USER_FILE_NAME, MODE_PRIVATE);
        String username = sp.getString("username", "");
        String password = sp.getString("password", "");
        usernameEdit = (EditText) findViewById(R.id.atyLoginUsernameEdit);
        passwordEdit = (EditText) findViewById(R.id.atyLoginPasswordEdit);
        usernameEdit.setText(username);
        passwordEdit.setText(password);
        loginButton = (Button) findViewById(R.id.atyLoginLoginBtn);
        loginButton.setOnClickListener(this);
        loadingDialog = new LoadingDialog(this);
    }

    @Override
    public void onClick(View v) {
        userLogin();
    }

    private void userLogin() {
        final String username = usernameEdit.getText().toString().trim();
        final String password = passwordEdit.getText().toString().trim();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            ToastUtils.show(R.string.username_or_password_is_null);
            return;
        }
        loadingDialog.show(R.string.is_the_login);
        final User user = new User(username, password);
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
                    saveUserInfo(user);
                    loadingDialog.close();
                    setResult(AxalentUtils.ADD_ACCOUNT);
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
                ToastUtils.show(XmlUtils.converErrorMsg(error));
            }
        });
    }

    private void skipToHome() {
        finish();
    }

    private void saveUserInfo(User user) {
        LogUtils.e("saveUserInfo");
        SharedPreferences.Editor editor = sp.edit();

        String username = sp.getString("username", "");
        if (!username.equals(user.getUsername())) {
            editor.putString("username", user.getUsername());
        }

        String password = sp.getString("password", "");
        if (!password.equals(user.getPassword())) {
            editor.putString("password", user.getPassword());
        }
    }
}
