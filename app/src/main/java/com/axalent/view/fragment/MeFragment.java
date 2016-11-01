/**
 * File Name                   : me 界面
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.fragment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import com.axalent.R;
import com.axalent.view.activity.DiscoveryGatewayActivity;
import com.axalent.view.activity.HomeActivity;
import com.axalent.view.activity.LoginActivity;
import com.axalent.adapter.MeAdapter;
import com.axalent.presenter.controller.Manager;
import com.axalent.model.Device;
import com.axalent.util.AxalentUtils;
import com.axalent.util.InstallationUtils;
import com.axalent.view.widget.CircleImageView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MeFragment extends Fragment implements OnClickListener, OnItemClickListener, Manager {
	
	private HomeActivity aty;
	private CircleImageView circleImageView;
	private int checkedLanguageItem = 0;
	
	@Override
	public void onAttach(Activity activity) {
		aty = (HomeActivity) activity;
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_me, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		circleImageView = (CircleImageView) view.findViewById(R.id.fragMeAvatar);
		circleImageView.setOnClickListener(this);
		ListView listView = (ListView) view.findViewById(R.id.fragMeListView);
		listView.setAdapter(new MeAdapter(aty));
		listView.setOnItemClickListener(this);
		
		Bitmap bitmap = getUserAvatar();
		if (bitmap != null) {
			circleImageView.setImageBitmap(bitmap);
		}
		checkedLanguageItem = aty.getSharedPreferences().getInt("languageItem", 0);
	}
	
	
	private void exetAccount() {
		Intent intent = new Intent(getActivity(), LoginActivity.class);
		intent.putExtra("exit", AxalentUtils.EXIT_ACCOUNT);
		startActivity(intent);
		getActivity().finish();
	}
	
	private void gotoGatewayConfig() {
		if (AxalentUtils.isHaveGateway()) {
			startActivity(new Intent(getActivity(), DiscoveryGatewayActivity.class));
		} else {
			showNoGatewayDialog();
		}
	}
	
	private void showNoGatewayDialog() {
		new AlertDialog.Builder(getActivity())
		.setIcon(R.drawable.app_logo_1)
		.setTitle(R.string.gateway_config_title)
		.setMessage(R.string.hint_add_gateway)
		.setPositiveButton(R.string.confirm, null)
		.create()
		.show();
	}
	
	private void showAboutAxalentDialog() {
		new AlertDialog.Builder(getActivity())
		.setIcon(R.drawable.app_logo_1)
		.setTitle(R.string.about_axalent)
		.setMessage(R.string.about_axalent_msg)
		.setPositiveButton(R.string.confirm, null)
		.create()
		.show();
	}
	
    private void showImeiDialog() {
		new AlertDialog.Builder(aty)
		.setIcon(R.drawable.app_logo_1)
		.setTitle(R.string.imei)
		.setMessage(InstallationUtils.getImei().replaceAll("-", ""))
		.setPositiveButton(R.string.confirm, null)
		.create().show();
    }
	
	
    private void showSwitchoverLanguageDialog() {
        new AlertDialog.Builder(aty)
        		.setIcon(R.drawable.app_logo_1)
                .setTitle(R.string.switchover_language)
                .setSingleChoiceItems(R.array.arrays_languages, checkedLanguageItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 获得选中的item
                        checkedLanguageItem = which;
                    }
                })
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 按确定
						aty.getSharedPreferences().edit().putInt("languageItem", checkedLanguageItem).commit();
						Intent intent;
						if (aty.isBluetoothMode()) {
							intent = new Intent(aty, LoginActivity.class);
						} else {
							intent = new Intent(aty, HomeActivity.class);
						}
						startActivity(intent);
						aty.finish();
					}
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();

    }
	
	public void setAvatar(Intent data) {
        try {  
        	if (data != null) {
        		Uri uri = data.getData();  
        		ContentResolver contentResolver = aty.getContentResolver();  
        		Bitmap avatarBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri));  
        		if (avatarBitmap != null) {
        			circleImageView.setImageBitmap(avatarBitmap);
        			saveUserAvatar(getCompressBitmap(avatarBitmap));
        		}
        	}
        } catch (FileNotFoundException e) {  
        	e.printStackTrace();
        }  
	}
	
	private boolean saveUserAvatar(String compressBitmap) {
		SharedPreferences sp = aty.getSharedPreferences(AxalentUtils.USER_FILE_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("avatar", compressBitmap);
		return editor.commit();
	}
	
	private Bitmap getUserAvatar() {
		SharedPreferences sp = aty.getSharedPreferences(AxalentUtils.USER_FILE_NAME, Context.MODE_PRIVATE);
        String imageString = sp.getString("avatar", "");  
        byte[] byteArray = Base64.decode(imageString, Base64.DEFAULT);  
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);  
        return BitmapFactory.decodeStream(byteArrayInputStream);
	}
	
	private String getCompressBitmap(Bitmap bitmap) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 50, byteArrayOutputStream); 
		byte[] byteArray = byteArrayOutputStream.toByteArray();
		return new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragMeAvatar:
			 Intent intent = new Intent();  
             intent.setType("image/*");  
             intent.setAction(Intent.ACTION_GET_CONTENT);   
             aty.startActivityForResult(intent, AxalentUtils.SET_AVATAR);  
			break;
		default:
			break;
		}
	}

	@Override
	public void notifyPageRefresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addDevice(Device device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
		case 2:
			// 唯一标识
			showImeiDialog();
			break;
		case 3:
			// 退出帐号
			exetAccount();
			break;
		case 4:
			// 关于朔联
			showAboutAxalentDialog();
			break;
		case 5:
			// 网关配置
			gotoGatewayConfig();
			break;
		case 7:
			// 切换语言
			showSwitchoverLanguageDialog();
			break;
		}
	
	}
	
}
