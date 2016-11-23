package com.axalent.view.activity;

import com.axalent.R;
import com.axalent.application.BaseActivity;
import com.axalent.util.AxalentUtils;
import com.axalent.util.ToastUtils;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GatewayConfigActivity extends BaseActivity implements View.OnClickListener {
	
	private RelativeLayout gatewayIpLayout;
	private RelativeLayout wifiNameLayout;
	private TextView gatewayIpValueTxt;
	private TextView wifiNameValueTxt;
	private String localUrl;
	private String wifiName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gateway_config);
		initActionBar();
		initView();
		
		localUrl = sharedPreferences.getString("localUrl", "");
		wifiName = sharedPreferences.getString("wifiName", "");

		setupData();
	}

	private void initActionBar() {
		View customView = findViewById(R.id.actionBarConfig);
		customView.findViewById(R.id.barShowGroupBack).setOnClickListener(this);
		customView.findViewById(R.id.barShowGroupAdd).setOnClickListener(this);
	}
	
	private void initView() {
		gatewayIpLayout = (RelativeLayout) findViewById(R.id.gateway_ip_layout);
		wifiNameLayout = (RelativeLayout) findViewById(R.id.wifi_name_layout);
		gatewayIpValueTxt = (TextView) findViewById(R.id.gateway_ip_value_txt);
		wifiNameValueTxt = (TextView) findViewById(R.id.wifi_name_value_txt);
		gatewayIpLayout.setOnClickListener(this);
		wifiNameLayout.setOnClickListener(this);
	}
	
	private void setupData() {
		if (localUrl.equals("")) {
			gatewayIpValueTxt.setVisibility(View.GONE);
		} else {
			gatewayIpValueTxt.setText(getIp());
		}
		
		if (wifiName.equals("")) {
			wifiNameValueTxt.setVisibility(View.GONE);
		} else {
			wifiNameValueTxt.setText(wifiName);
		}
	}
	
	private String getIp() {
		int start = localUrl.indexOf("//") + 2;
		int end = localUrl.lastIndexOf(":");
		String ip = localUrl.substring(start, end);
		return ip;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.barShowGroupBack:
				//exit
				finish();
				break;
			case R.id.barShowGroupAdd:
				showInputDialog();
				break;
			case R.id.gateway_ip_layout:
				//find gateway
				gotoDiscoveryGateway();
				break;
			case R.id.wifi_name_layout:
				//switch WiFi
//				String localUrl = sharedPreferences.getString("localUrl", "");
//				if (localUrl.equals("")) {
//					new AlertDialog.Builder(this)
//					.setIcon(R.drawable.app_logo_1)
//					.setTitle(R.string.config_ip)
//					.setMessage(R.string.hint_config_local_ip)
//					.setPositiveButton(R.string.confirm, new OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							gotoDiscoveryGateway();
//						}
//					})
//					.setNegativeButton(R.string.cancel, null)
//					.create()
//					.show();
//				} else {
//					gotoSwitchGatewatWiFi();
//				}

				Intent intent = new Intent(GatewayConfigActivity.this, SwitchWifiGuideActivity.class);
				startActivityForResult(intent, AxalentUtils.SWITCH_GATEWAY_WIFI_SUCCESS);

				break;
			default:
				break;
		}
	}
	
	public void showInputDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.app_logo_1);
		builder.setTitle(R.string.config_ip);
		View view = View.inflate(this, R.layout.dialog_input, null);
		builder.setView(view);
		final EditText codeEdit = (EditText) view.findViewById(R.id.dialogInputEdit);
		codeEdit.setHint(R.string.please_input_address);
		builder.setPositiveButton(R.string.confirm, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String ip = codeEdit.getText().toString().trim();
				if (ip.matches(AxalentUtils.MATCHES_IP)) {
					// http://192.168.51.1:37215/upnpdev.xml
					localUrl = "http://"+ ip + ":8087/zdk/services/zamapi/";
					sharedPreferences.edit().putString("localUrl", localUrl).commit();
					gatewayIpValueTxt.setText(ip);
					ToastUtils.show(R.string.success);
				} else {
					ToastUtils.show(R.string.format_error);
				}
			}
		});
		builder.setNegativeButton(R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		builder.create().show();
	}
	
	
	private void gotoDiscoveryGateway() {
		Intent intent = new Intent(this, DiscoveryGatewayActivity.class);
		startActivityForResult(intent, 0);
	}
	
	private void gotoSwitchGatewatWiFi() {
		Intent intent = new Intent(this, SwitchGatewayWifiActivity.class);
		intent.putExtra("localUrl", localUrl);
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == AxalentUtils.SWITCH_GATEWAY_WIFI) {
			String ssid = data.getStringExtra("ssid");
			String url = data.getStringExtra("url");
			
			if (!TextUtils.isEmpty(ssid)) {
				wifiName = ssid;
				wifiNameValueTxt.setVisibility(View.VISIBLE);
				wifiNameValueTxt.setText(wifiName);
			}
		
			if (!TextUtils.isEmpty(url)) {
				localUrl = url;
				gatewayIpValueTxt.setVisibility(View.VISIBLE);
				gatewayIpValueTxt.setText(getIp());
			}
		} else if (resultCode == AxalentUtils.SAVE_GATEWAY_IP) {
			String url = data.getStringExtra("url");
			if (!TextUtils.isEmpty(url)) {
				localUrl = url;
				gatewayIpValueTxt.setVisibility(View.VISIBLE);
				gatewayIpValueTxt.setText(getIp());
			}
		} else if (resultCode == AxalentUtils.SWITCH_GATEWAY_WIFI_SUCCESS) {
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
